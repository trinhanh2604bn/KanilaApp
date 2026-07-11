/**
 * chatbotCart.tool.js
 * Cart operations service for the Kanila AI chatbot.
 *
 * Reuses existing Cart / CartItem / Product / ProductVariant models directly.
 * Mirrors the validation + insertion logic in cart.controller.js (addItemToMyCart).
 *
 * SECURITY:
 * - addProductsToCart() requires a verified customerId resolved from JWT — never from request body.
 * - Never trusts product_id from Android client at add-to-cart time; re-validates from DB.
 * - Never exposes DB error messages — all errors are wrapped with safe codes.
 */

const mongoose = require("mongoose");
const Cart     = require("../models/cart.model");
const CartItem = require("../models/cartItem.model");
const Product  = require("../models/product.model");
const ProductVariant = require("../models/productVariant.model");

// ─────────────────────────────────────────────────────────────────────────────
// Helpers (mirrored from cart.controller.js)
// ─────────────────────────────────────────────────────────────────────────────

const toMoney = (v) => Math.max(0, Math.round(Number(v || 0)));

const buildLineKey = (productId, variantId) =>
  `${String(productId)}::${String(variantId || "default")}`;

const getAvailableStock = (product) => Math.max(0, Number(product?.stock || 0));

const computeStockStatus = (product) => (getAvailableStock(product) > 0 ? "in_stock" : "out_of_stock");

/**
 * Get or create an active cart for a customer.
 * @param {ObjectId} customerId
 * @returns {Promise<Cart>}
 */
async function ensureActiveCart(customerId) {
  let cart = await Cart.findOne({ customer_id: customerId, cart_status: "active" })
    .sort({ updated_at: -1 });
  if (!cart) {
    cart = await Cart.create({
      customer_id: customerId,
      cart_status: "active",
      currency_code: "VND",
      item_count: 0,
      subtotal_amount: 0,
      discount_amount: 0,
      total_amount: 0,
    });
  }
  return cart;
}

/**
 * Resolve the best available variant for a product (mirrors controller resilience logic).
 * @param {Product} product
 * @param {string|null} preferredVariantId
 * @returns {Promise<ProductVariant|null>}
 */
async function resolveVariant(product, preferredVariantId = null) {
  const productId = product._id;
  if (preferredVariantId && mongoose.Types.ObjectId.isValid(preferredVariantId)) {
    const v = await ProductVariant.findOne({ _id: preferredVariantId, productId });
    if (v) return v;
  }
  let v = await ProductVariant.findOne({ productId, variantStatus: "active" }).sort({ createdAt: 1 });
  if (v) return v;
  v = await ProductVariant.findOne({ productId }).sort({ createdAt: 1 });
  if (v) return v;

  // Auto-create default variant (same resilience as cart.controller.js)
  const skuBase = String(product.productCode || productId).replace(/\s+/g, "-").toUpperCase();
  const suffix = String(productId).slice(-6).toUpperCase();
  const sku = `${skuBase}-DEFAULT-${suffix}`;
  try {
    return await ProductVariant.create({
      productId,
      sku,
      variantName: "Default",
      variantStatus: "active",
      barcode: "",
      weightGrams: 0,
      volumeMl: 0,
      costAmount: 0,
    });
  } catch {
    return ProductVariant.findOne({ productId }).sort({ createdAt: 1 });
  }
}

/**
 * Update cart totals after item changes.
 * @param {string|ObjectId} cartId
 */
async function refreshCartTotals(cartId) {
  const items = await CartItem.find({ cart_id: cartId }).lean();
  const subtotal = items.reduce((s, i) => s + toMoney(i.unit_price_amount) * Number(i.quantity), 0);
  const discount = items.reduce((s, i) => s + toMoney(i.discount_amount) * Number(i.quantity), 0);
  const total    = items.reduce((s, i) => s + toMoney(i.final_unit_price_amount) * Number(i.quantity), 0);
  const count    = items.reduce((s, i) => s + Number(i.quantity), 0);
  await Cart.findByIdAndUpdate(cartId, {
    item_count: count,
    subtotal_amount: subtotal,
    discount_amount: discount,
    total_amount: total,
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Validate a list of products (chatbot-facing)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Validate product IDs from chatbot context.
 * Re-loads each product from DB to get current stock and price — never trusts chatbot state.
 *
 * @param {string[]} productIds
 * @returns {Promise<{
 *   valid: { product: object, variant: object }[],
 *   unavailable: string[]
 * }>}
 */
async function validateCartProducts(productIds) {
  const valid = [];
  const unavailable = [];

  for (const pid of productIds) {
    if (!mongoose.Types.ObjectId.isValid(pid)) {
      unavailable.push(pid);
      continue;
    }
    let product;
    try {
      product = await Product.findById(pid)
        .populate("brandId", "brandName")
        .lean();
    } catch {
      unavailable.push(pid);
      continue;
    }
    if (!product || product.isActive === false || product.productStatus !== "active") {
      unavailable.push(pid);
      continue;
    }
    if (getAvailableStock(product) <= 0) {
      unavailable.push(pid);
      continue;
    }
    const variant = await resolveVariant(product);
    if (!variant) {
      unavailable.push(pid);
      continue;
    }
    valid.push({ product, variant });
  }

  return { valid, unavailable };
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 5B: validateCartItems — supports explicit variant_id + quantity
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Validate an array of cart item objects: { product_id, variant_id?, quantity? }.
 * Re-fetches each product + variant from DB — never trusts chatbot state.
 *
 * Variant selection logic:
 *   - If product has multiple active variants AND no variant_id is specified:
 *     → returns requires_variant_selection: true with variant options.
 *   - If a variant_id is specified, validates it against the product.
 *   - If only one variant exists, auto-selects it.
 *
 * @param {Array<{product_id: string, variant_id?: string, quantity?: number}>} cartItems
 * @returns {Promise<{
 *   valid: Array<{product, variant, quantity, stock_status}>,
 *   unavailable: Array<{product_id, reason}>,
 *   needs_variant_selection: Array<{product_id, product_name, variants: Array<{variant_id, name, volume_ml, weight_grams}>}>
 * }>}
 */
async function validateCartItems(cartItems) {
  const valid = [];
  const unavailable = [];
  const needs_variant_selection = [];

  for (const item of cartItems) {
    const { product_id, variant_id = null, quantity = 1 } = item;

    if (!mongoose.Types.ObjectId.isValid(product_id)) {
      unavailable.push({ product_id, reason: "invalid_id" });
      continue;
    }

    let product;
    try {
      product = await Product.findById(product_id)
        .populate("brandId", "brandName")
        .lean();
    } catch {
      unavailable.push({ product_id, reason: "not_found" });
      continue;
    }

    if (!product || product.isActive === false || product.productStatus !== "active") {
      unavailable.push({ product_id, reason: "inactive" });
      continue;
    }

    const availableStock = getAvailableStock(product);
    if (availableStock <= 0) {
      unavailable.push({ product_id, reason: "out_of_stock" });
      continue;
    }

    const qty = Math.max(1, Number(quantity) || 1);
    if (qty > availableStock) {
      unavailable.push({ product_id, reason: `insufficient_stock:${availableStock}` });
      continue;
    }

    // ── Variant resolution ──────────────────────────────────────────────────
    // Count active variants for this product
    const activeVariants = await ProductVariant.find({
      productId: product._id,
      variantStatus: "active",
    }).lean();

    if (activeVariants.length > 1 && !variant_id) {
      // Multiple variants and user hasn't chosen — ask for selection
      needs_variant_selection.push({
        product_id: String(product._id),
        product_name: product.productName,
        price: product.price,
        image_url: product.imageUrl || "",
        variants: activeVariants.map((v) => ({
          variant_id:   String(v._id),
          name:         v.variantName,
          volume_ml:    v.volumeMl || null,
          weight_grams: v.weightGrams || null,
          sku:          v.sku || "",
        })),
      });
      continue;
    }

    // Resolve the specific variant
    let resolvedVariant = null;
    if (variant_id && mongoose.Types.ObjectId.isValid(variant_id)) {
      resolvedVariant = activeVariants.find((v) => String(v._id) === String(variant_id)) || null;
    }
    if (!resolvedVariant) {
      // Auto-select: prefer explicit resolveVariant() logic
      resolvedVariant = await resolveVariant(product, variant_id);
    }
    if (!resolvedVariant) {
      unavailable.push({ product_id, reason: "no_variant" });
      continue;
    }

    valid.push({
      product,
      variant:      resolvedVariant,
      quantity:     qty,
      stock_status: availableStock > 0 ? "in_stock" : "out_of_stock",
    });
  }

  return { valid, unavailable, needs_variant_selection };
}

/**
 * Get all active variants for a product (for variant selection UI).
 * Returns clean, safe variant data.
 *
 * @param {string} productId
 * @returns {Promise<Array<{variant_id, name, volume_ml, sku}>>}
 */
async function getProductVariants(productId) {
  if (!mongoose.Types.ObjectId.isValid(productId)) return [];
  const variants = await ProductVariant.find({
    productId,
    variantStatus: "active",
  }).lean();
  return variants.map((v) => ({
    variant_id:   String(v._id),
    name:         v.variantName,
    volume_ml:    v.volumeMl || null,
    weight_grams: v.weightGrams || null,
    sku:          v.sku || "",
  }));
}

// ─────────────────────────────────────────────────────────────────────────────
// Generate a skincare combo recommendation
// ─────────────────────────────────────────────────────────────────────────────

// Combo slots — maps combo_type to desired category keywords (in priority order)
const COMBO_SLOTS = {
  skincare_basic: [
    { slot: "cleanser",    names: ["sữa rửa mặt", "cleanser", "gel rửa mặt", "foam cleanser"] },
    { slot: "serum",       names: ["serum", "tinh chất"] },
    { slot: "moisturizer", names: ["kem dưỡng ẩm", "moisturizer", "dưỡng ẩm"] },
  ],
  skincare_full: [
    { slot: "cleanser",    names: ["sữa rửa mặt", "cleanser"] },
    { slot: "toner",       names: ["toner", "nước hoa hồng", "nước cân bằng"] },
    { slot: "serum",       names: ["serum", "tinh chất"] },
    { slot: "moisturizer", names: ["kem dưỡng ẩm", "moisturizer"] },
    { slot: "sunscreen",   names: ["kem chống nắng", "sunscreen", "chống nắng"] },
  ],
  makeup: [
    { slot: "bb_cream",    names: ["bb cream", "cushion", "kem nền"] },
    { slot: "mascara",     names: ["mascara"] },
    { slot: "lipstick",    names: ["son môi", "lipstick", "son", "lip"] },
  ],
};

const Category = require("../models/category.model");

async function resolveCategoryIds(names) {
  if (!names.length) return [];
  const regexes = names.map((n) => new RegExp(n, "i"));
  const cats = await Category.find({
    $or: [{ categoryName: { $in: regexes } }, { categoryCode: { $in: names.map((n) => n.toUpperCase()) } }],
    isActive: true,
  })
    .select("_id")
    .lean();
  return cats.map((c) => c._id);
}

/**
 * Build a product combo based on combo type and customer context.
 *
 * @param {object} params
 * @param {"skincare_basic"|"skincare_full"|"makeup"} params.comboType
 * @param {object|null} params.customerProfile  — from getCustomerContext()
 * @param {number|null} params.budgetMax        — total budget (split evenly per slot)
 * @returns {Promise<{ products: object[], comboType: string, totalPrice: number }>}
 */
async function generateCartRecommendation({ comboType, customerProfile, budgetMax }) {
  const slots = COMBO_SLOTS[comboType] || COMBO_SLOTS["skincare_basic"];
  const perSlotBudget = budgetMax && slots.length > 0 ? Math.floor(budgetMax / slots.length) : null;

  const skinType    = customerProfile?.skin_type;
  const skinConcerns = customerProfile?.skin_concerns || [];

  const slotProducts = [];

  for (const slot of slots) {
    try {
      const categoryIds = await resolveCategoryIds(slot.names);

      const filter = {
        isActive: true,
        productStatus: "active",
        stock: { $gt: 0 },
      };
      if (categoryIds.length) filter.categoryId = { $in: categoryIds };
      if (perSlotBudget)       filter.price = { $lte: perSlotBudget };
      if (skinType)            filter.skin_types_supported = skinType;

      let products = await Product.find(filter)
        .sort({ averageRating: -1, bought: -1 })
        .limit(3)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName")
        .lean();

      // Fallback: relax skin_type filter
      if (!products.length && skinType) {
        const relaxed = { ...filter };
        delete relaxed.skin_types_supported;
        products = await Product.find(relaxed)
          .sort({ averageRating: -1, bought: -1 })
          .limit(3)
          .populate("brandId", "brandName")
          .populate("categoryId", "categoryName")
          .lean();
      }

      if (products.length) {
        const p = products[0];
        slotProducts.push({
          product_id:     p._id.toString(),
          slot:           slot.slot,
          name:           p.productName,
          brand_name:     p.brandId?.brandName || "",
          category_name:  p.categoryId?.categoryName || "",
          price:          p.price,
          compare_at_price: p.compareAtPrice || null,
          image_url:      p.imageUrl || "",
          stock_status:   getAvailableStock(p) > 0 ? "in_stock" : "out_of_stock",
          skin_types_supported: p.skin_types_supported || [],
          concerns_targeted:    p.concerns_targeted || [],
          reason: `Bước ${slot.slot.replace(/_/g, " ")} phù hợp cho routine của bạn`,
        });
      }
    } catch (err) {
      console.error(`[ChatbotCart] Slot ${slot.slot} query failed:`, err.message);
    }
  }

  const totalPrice = slotProducts.reduce((sum, p) => sum + p.price, 0);
  return { products: slotProducts, comboType, totalPrice };
}

// ─────────────────────────────────────────────────────────────────────────────
// Add products to cart
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Add a list of products to a customer's active cart.
 *
 * SECURITY: customerId must come from verified JWT, not client payload.
 *
 * Supports two input modes (backward compatible):
 *   - Legacy:   { customerId, productIds: string[] }
 *   - Phase 5B: { customerId, cartItems: [{product_id, variant_id?, quantity?}] }
 *
 * When a product has multiple active variants and no variant_id is given,
 * returns reason: "variant_selection_required" with variant options — no cart modification.
 *
 * @param {object} params
 * @param {ObjectId|string} params.customerId
 * @param {string[]}        [params.productIds]  — legacy: simple ID array
 * @param {Array}           [params.cartItems]   — Phase 5B explicit format
 */
async function addProductsToCart({ customerId, productIds, cartItems }) {
  if (!customerId) {
    return { success: false, reason: "login_required" };
  }

  // ── Normalize input to validateCartItems format ───────────────────────────
  let itemsToValidate;
  if (Array.isArray(cartItems) && cartItems.length > 0) {
    itemsToValidate = cartItems.map((ci) => ({
      product_id: String(ci.product_id || ci.productId || ""),
      variant_id: ci.variant_id || ci.variantId || null,
      quantity:   Number(ci.quantity || 1),
    }));
  } else {
    const ids = Array.isArray(productIds) ? productIds : [];
    itemsToValidate = ids.map((pid) => ({ product_id: String(pid), variant_id: null, quantity: 1 }));
  }

  if (!itemsToValidate.length) {
    return { success: false, reason: "no_products" };
  }

  const { valid, unavailable, needs_variant_selection } = await validateCartItems(itemsToValidate);

  // Surface variant selection requirement — no cart modification
  if (needs_variant_selection.length > 0) {
    return {
      success: false,
      reason: "variant_selection_required",
      needs_variant_selection,
      items_ready:       valid.length,
      items_unavailable: unavailable.length,
    };
  }
  if (!valid.length) {
    return {
      success: false,
      reason: "all_products_unavailable",
      items_added: 0,
      items_skipped: unavailable.length,
      skipped_reasons: unavailable.map((u) => (typeof u === "string" ? u : `${u.product_id}: ${u.reason}`)),
      cart_count: 0,
      cart_subtotal: 0,
      cart_total: 0,
    };
  }

  const cart = await ensureActiveCart(customerId);
  let itemsAdded = 0;
  const skippedReasons = [];

  for (const { product, variant, quantity } of valid) {
    try {
      const lineKey    = buildLineKey(product._id, variant._id);
      const unitPrice  = toMoney(product.price);
      const finalPrice = unitPrice;
      const compareAt  = toMoney(product.compareAtPrice || 0);
      const stock      = getAvailableStock(product);
      const qty        = Math.max(1, Number(quantity) || 1);

      const existing = await CartItem.findOne({
        cart_id: cart._id,
        $or: [{ line_key: lineKey }, { product_id: product._id, variant_id: variant._id }],
      });

      if (existing) {
        const nextQty = Number(existing.quantity || 1) + qty;
        if (nextQty > stock) {
          skippedReasons.push(`${product.productName}: số lượng vượt tồn kho`);
          continue;
        }
        existing.quantity               = nextQty;
        existing.unit_price_amount      = unitPrice;
        existing.final_unit_price_amount = finalPrice;
        existing.line_total_amount      = finalPrice * nextQty;
        existing.stock_status           = computeStockStatus(product);
        await existing.save();
      } else {
        if (qty > stock) {
          skippedReasons.push(`${product.productName}: hết hàng`);
          continue;
        }
        await CartItem.create({
          cart_id:                 cart._id,
          product_id:              product._id,
          variant_id:              variant._id,
          line_key:                lineKey,
          sku_snapshot:            variant.sku || product.productCode || String(product._id),
          product_name_snapshot:   product.productName || "Product",
          variant_name_snapshot:   variant.variantName || "Default",
          brand_name_snapshot:     product.brandId?.brandName || "",
          image_url_snapshot:      product.imageUrl || "",
          compare_at_price_amount: compareAt > finalPrice ? compareAt : 0,
          stock_status:            computeStockStatus(product),
          unit_price_amount:       unitPrice,
          discount_amount:         0,
          final_unit_price_amount: finalPrice,
          quantity:                qty,
          line_total_amount:       finalPrice * qty,
          selected:                true,
        });
      }
      itemsAdded++;
    } catch (err) {
      console.error("[ChatbotCart] Add item failed:", err.message);
      skippedReasons.push(`${product.productName}: lỗi thêm vào giỏ`);
    }
  }

  await refreshCartTotals(cart._id);
  const updatedCart = await Cart.findById(cart._id).lean();

  return {
    success:       itemsAdded > 0,
    items_added:   itemsAdded,
    items_skipped: unavailable.length + (valid.length - itemsAdded),
    skipped_reasons: skippedReasons,
    cart_count:    updatedCart?.item_count || 0,
    cart_subtotal: updatedCart?.subtotal_amount || 0,
    cart_total:    updatedCart?.total_amount || 0,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Calculate cart summary (read-only)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Load and summarize the current active cart for a customer.
 *
 * @param {ObjectId|string} customerId
 * @returns {Promise<{
 *   found: boolean,
 *   items_count: number,
 *   items: object[],
 *   subtotal: number,
 *   discount: number,
 *   total: number
 * }>}
 */
async function calculateCartSummary(customerId) {
  if (!customerId) return { found: false, items_count: 0, items: [], subtotal: 0, discount: 0, total: 0 };

  const cart = await Cart.findOne({ customer_id: customerId, cart_status: "active" })
    .sort({ updated_at: -1 })
    .lean();

  if (!cart) return { found: false, items_count: 0, items: [], subtotal: 0, discount: 0, total: 0 };

  const items = await CartItem.find({ cart_id: cart._id }).sort({ added_at: -1 }).lean();

  const safeItems = items.map((i) => ({
    product_name: i.product_name_snapshot,
    brand_name:   i.brand_name_snapshot,
    variant_name: i.variant_name_snapshot,
    price:        i.unit_price_amount,
    quantity:     i.quantity,
    line_total:   i.final_unit_price_amount * i.quantity,
    stock_status: i.stock_status,
  }));

  return {
    found:       true,
    items_count: items.reduce((s, i) => s + Number(i.quantity), 0),
    items:       safeItems,
    subtotal:    cart.subtotal_amount || 0,
    discount:    cart.discount_amount || 0,
    total:       cart.total_amount || 0,
  };
}

module.exports = {
  validateCartProducts,
  validateCartItems,        // Phase 5B: explicit variant+quantity validation
  getProductVariants,       // Phase 5B: list variants for selection UI
  generateCartRecommendation,
  addProductsToCart,
  calculateCartSummary,
};
