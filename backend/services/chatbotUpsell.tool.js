/**
 * chatbotUpsell.tool.js
 * Cross-sell / upsell complementary product recommendations for the Kanila chatbot.
 *
 * Strategy:
 *   1. Find products in complementary categories based on what's already in the combo/cart.
 *   2. Filter by the same skin type and concerns as the customer profile.
 *   3. Exclude products that are already in the current combo.
 *   4. Rank by averageRating DESC, then bought DESC.
 *
 * Rules:
 *   - Do NOT randomly suggest products.
 *   - Only use verified MongoDB data.
 *   - Gemini only writes the explanation — not the products list.
 */

const Product  = require("../models/product.model");
const Category = require("../models/category.model");

// ─────────────────────────────────────────────────────────────────────────────
// Complementary category map
// ─────────────────────────────────────────────────────────────────────────────

// When user has X category in their combo, suggest Y
const COMPLEMENTARY_MAP = [
  {
    trigger:     ["cleanser", "sữa rửa mặt", "gel rửa mặt"],
    suggestions: ["toner", "nước hoa hồng", "nước cân bằng"],
    reason:      "Dùng sau bước rửa mặt để cân bằng da",
  },
  {
    trigger:     ["toner", "nước hoa hồng"],
    suggestions: ["serum", "tinh chất"],
    reason:      "Tăng cường dưỡng chất sau toner",
  },
  {
    trigger:     ["serum", "tinh chất"],
    suggestions: ["kem dưỡng ẩm", "moisturizer"],
    reason:      "Khóa ẩm sau bước serum",
  },
  {
    trigger:     ["kem dưỡng ẩm", "moisturizer"],
    suggestions: ["kem chống nắng", "sunscreen", "chống nắng"],
    reason:      "Bảo vệ da khỏi tia UV mỗi ngày",
  },
  {
    trigger:     ["kem chống nắng", "sunscreen"],
    suggestions: ["sữa rửa mặt", "cleanser"],
    reason:      "Làm sạch chống nắng hiệu quả cuối ngày",
  },
  {
    trigger:     ["serum", "tinh chất"],
    suggestions: ["mặt nạ", "mask", "sheet mask"],
    reason:      "Bổ sung dưỡng chất đậm đặc 2-3 lần/tuần",
  },
  {
    trigger:     ["bb cream", "cushion", "kem nền"],
    suggestions: ["tẩy trang", "micellar", "cleansing"],
    reason:      "Làm sạch make-up cuối ngày",
  },
];

/**
 * Resolve category IDs for a list of name keywords.
 */
async function resolveCategoryIds(names) {
  if (!names.length) return [];
  const regexes = names.map((n) => new RegExp(n, "i"));
  const cats = await Category.find({
    $or: [{ categoryName: { $in: regexes } }],
    isActive: true,
  })
    .select("_id categoryName")
    .lean();
  return cats;
}

// ─────────────────────────────────────────────────────────────────────────────
// Main function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find complementary products for an existing product combo.
 *
 * @param {object} params
 * @param {string[]} params.existingProductIds  — product_ids already in the combo
 * @param {string[]} params.existingCategories  — category names in the combo
 * @param {object|null} params.customerProfile  — from getCustomerContext().customer_profile
 * @param {number} [params.limit=2]
 * @returns {Promise<{
 *   upsell_products: object[],
 *   upsell_reason: string
 * }>}
 */
async function findComplementaryProducts({
  existingProductIds = [],
  existingCategories = [],
  customerProfile = null,
  limit = 2,
}) {
  const lower = existingCategories.map((c) => c.toLowerCase());

  // Find which complementary rule applies
  let bestRule = null;
  for (const rule of COMPLEMENTARY_MAP) {
    if (rule.trigger.some((t) => lower.some((cat) => cat.includes(t)))) {
      bestRule = rule;
      break;
    }
  }

  if (!bestRule) {
    return { upsell_products: [], upsell_reason: "" };
  }

  try {
    const suggestedCats = await resolveCategoryIds(bestRule.suggestions);
    if (!suggestedCats.length) {
      return { upsell_products: [], upsell_reason: "" };
    }

    const catIds = suggestedCats.map((c) => c._id);
    const filter = {
      isActive: true,
      productStatus: "active",
      stock: { $gt: 0 },
      categoryId: { $in: catIds },
    };

    // Exclude already-in-combo products
    if (existingProductIds.length) {
      filter._id = { $nin: existingProductIds };
    }

    // Filter by skin type if known
    if (customerProfile?.skin_type) {
      filter.skin_types_supported = customerProfile.skin_type;
    }

    let products = await Product.find(filter)
      .sort({ averageRating: -1, bought: -1 })
      .limit(limit)
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName")
      .lean();

    // Relax skin_type if no results
    if (!products.length && customerProfile?.skin_type) {
      delete filter.skin_types_supported;
      products = await Product.find(filter)
        .sort({ averageRating: -1, bought: -1 })
        .limit(limit)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName")
        .lean();
    }

    if (!products.length) {
      return { upsell_products: [], upsell_reason: "" };
    }

    const upsell_products = products.map((p) => ({
      product_id:   p._id.toString(),
      name:         p.productName,
      brand_name:   p.brandId?.brandName || "",
      category_name: p.categoryId?.categoryName || "",
      price:        p.price,
      image_url:    p.imageUrl || "",
      stock_status: Number(p.stock || 0) > 0 ? "in_stock" : "out_of_stock",
      reason:       bestRule.reason,
    }));

    return { upsell_products, upsell_reason: bestRule.reason };
  } catch (err) {
    console.error("[ChatbotUpsell] findComplementaryProducts failed:", err.message);
    return { upsell_products: [], upsell_reason: "" };
  }
}

module.exports = { findComplementaryProducts };
