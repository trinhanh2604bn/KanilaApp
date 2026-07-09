/**
 * chatbotLookBuilder.js
 * Stage 6 of the Advanced Recommendation Pipeline.
 *
 * Builds bundled makeup look recommendations for event/set intents.
 * Fetches one best-scored product per "slot" (primer, foundation, blush, etc.)
 * and assembles them into a complete look with total price.
 *
 * No Gemini calls here — Gemini explains the look, but selection is deterministic.
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

const Product = require("../models/product.model");
const ProductMedia = require("../models/productMedia.model");
const ReviewSummary = require("../models/reviewSummary.model");
const PromotionTarget = require("../models/promotionTarget.model");
const Category = require("../models/category.model");
const { scoreAndRankProducts } = require("./chatbotProductScorer");

// ─────────────────────────────────────────────────────────────────────────────
// Look definitions per occasion
// ─────────────────────────────────────────────────────────────────────────────

const LOOK_DEFINITIONS = {
  party: {
    name: "Party Glam Look",
    nameVi: "Look Đi Tiệc Rạng Rỡ",
    slots: ["primer", "cushion_foundation", "concealer", "blush", "lipstick", "setting_spray"],
    optionalSlots: ["highlighter", "mascara"],
    style: "glam",
  },
  wedding: {
    name: "Wedding Soft Glam",
    nameVi: "Look Đám Cưới Tinh Tế",
    slots: ["primer", "foundation", "concealer", "blush", "lipstick", "setting_spray"],
    optionalSlots: ["highlighter", "mascara", "eyeshadow"],
    style: "soft_glam",
  },
  school: {
    name: "Natural School Look",
    nameVi: "Look Đi Học Tự Nhiên",
    slots: ["cushion_foundation", "lip_tint"],
    optionalSlots: ["blush", "mascara"],
    style: "natural",
  },
  office: {
    name: "Office Polished Look",
    nameVi: "Look Công Sở Chuyên Nghiệp",
    slots: ["foundation", "concealer", "lipstick"],
    optionalSlots: ["blush", "setting_spray"],
    style: "polished",
  },
  date: {
    name: "Romantic Date Look",
    nameVi: "Look Hẹn Hò Lãng Mạn",
    slots: ["cushion_foundation", "blush", "lip_tint"],
    optionalSlots: ["mascara", "highlighter"],
    style: "soft_glam",
  },
  daily: {
    name: "Everyday Natural Look",
    nameVi: "Look Hàng Ngày Nhẹ Nhàng",
    slots: ["cushion_foundation", "lip_tint"],
    optionalSlots: ["blush"],
    style: "natural",
  },
  default: {
    name: "Complete Makeup Set",
    nameVi: "Bộ Trang Điểm Đầy Đủ",
    slots: ["primer", "cushion_foundation", "concealer", "blush", "lipstick", "setting_spray"],
    optionalSlots: [],
    style: "natural",
  },
};

// ─────────────────────────────────────────────────────────────────────────────
// Slot → category name mapping for DB query
// ─────────────────────────────────────────────────────────────────────────────

const SLOT_CATEGORY_NAMES = {
  primer:              ["Primer", "Kem lót"],
  foundation:          ["Kem nền", "Foundation", "Trang điểm nền"],
  cushion_foundation:  ["Cushion", "Phấn nước"],
  concealer:           ["Concealer", "Che khuyết điểm"],
  blush:               ["Má hồng", "Blush", "Phấn má hồng"],
  lipstick:            ["Son thỏi", "Lipstick", "Son môi"],
  lip_tint:            ["Son tint", "Lip tint", "Tint môi"],
  mascara:             ["Mascara", "Chuốt mi"],
  eyeliner:            ["Kẻ mắt", "Eyeliner", "Bút kẻ mắt"],
  eyeshadow:           ["Phấn mắt", "Eyeshadow", "Bảng phấn"],
  setting_spray:       ["Setting spray", "Xịt khóa nền", "Xịt cố định makeup"],
  highlighter:         ["Highlighter", "Tạo khối sáng", "Phấn highlight"],
  powder:              ["Phấn phủ", "Loose powder", "Phấn hoàn thiện"],
  contour:             ["Contour", "Tạo khối tối"],
};

const SLOT_LABEL_VI = {
  primer: "Kem lót",
  foundation: "Kem nền",
  cushion_foundation: "Cushion",
  concealer: "Che khuyết điểm",
  blush: "Má hồng",
  lipstick: "Son môi",
  lip_tint: "Son tint",
  mascara: "Mascara",
  eyeliner: "Kẻ mắt",
  eyeshadow: "Phấn mắt",
  setting_spray: "Xịt khóa nền",
  highlighter: "Highlight",
  powder: "Phấn phủ",
  contour: "Tạo khối",
};

// ─────────────────────────────────────────────────────────────────────────────
// DB helpers
// ─────────────────────────────────────────────────────────────────────────────

async function resolveCategoryIdsForSlot(slot) {
  const names = SLOT_CATEGORY_NAMES[slot] || [];
  if (names.length === 0) return [];
  const regexes = names.map((n) => new RegExp(n.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "i"));
  const categories = await Category.find({
    $or: [{ categoryName: { $in: regexes } }],
    isActive: true,
  }).select("_id").lean();
  return categories.map((c) => c._id);
}

async function fetchCandidatesForSlot(slot, context, limit = 10) {
  let categoryIds;
  try {
    categoryIds = await resolveCategoryIdsForSlot(slot);
  } catch (_) {
    categoryIds = [];
  }

  const filter = { isActive: true, productStatus: "active", stock: { $gt: 0 } };

  if (categoryIds.length > 0) {
    filter.categoryId = { $in: categoryIds };
  }

  if (context.budget?.max) {
    // Per slot: allow up to 60% of total budget for any single item
    const slotBudgetCap = Math.max(context.budget.max * 0.6, 150000);
    filter.price = { $lte: slotBudgetCap };
  }

  let products = [];
  try {
    products = await Product.find(filter)
      .sort({ averageRating: -1, sales_count: -1 })
      .limit(limit)
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName categoryCode")
      .lean();
  } catch (_) {
    return [];
  }

  if (products.length === 0 && categoryIds.length > 0) {
    // Relax: no price filter fallback
    try {
      const relaxed = { isActive: true, productStatus: "active", categoryId: { $in: categoryIds } };
      products = await Product.find(relaxed)
        .sort({ averageRating: -1 })
        .limit(limit)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName categoryCode")
        .lean();
    } catch (_) {}
  }

  // Normalize to chatbot format
  return products.map((p) => ({
    product_id: p._id.toString(),
    _id: p._id,
    productName: p.productName,
    name: p.productName,
    brand_name: p.brandId?.brandName || "",
    brandName: p.brandId?.brandName || "",
    category_name: p.categoryId?.categoryName || "",
    categoryName: p.categoryId?.categoryName || "",
    price: p.price || 0,
    compareAtPrice: p.compareAtPrice || null,
    compare_at_price: p.compareAtPrice || null,
    imageUrl: p.imageUrl || "",
    image_url: p.imageUrl || "",
    image: p.imageUrl || "",
    averageRating: p.averageRating || 0,
    rating: p.averageRating || 0,
    stock: p.stock,
    stock_status: p.stock > 10 ? "in_stock" : p.stock > 0 ? "low_stock" : "out_of_stock",
    finish_type: p.finish_type || null,
    shades: p.shades || [],
    skin_types_supported: p.skin_types_supported || [],
    is_sensitive_friendly: p.is_sensitive_friendly || false,
    isActive: p.isActive,
    productStatus: p.productStatus,
  }));
}

async function enrichWithMedia(products) {
  if (products.length === 0) return products;
  const ids = products.map((p) => p._id || p.product_id).filter(Boolean);

  let mediaList = [];
  try {
    mediaList = await ProductMedia.find({ productId: { $in: ids } })
      .sort({ isPrimary: -1, sortOrder: 1 })
      .select("productId mediaUrl")
      .lean();
  } catch (_) {}

  const mediaMap = {};
  for (const m of mediaList) {
    const pid = m.productId.toString();
    if (!mediaMap[pid]) mediaMap[pid] = m.mediaUrl;
  }

  return products.map((p) => {
    const pid = (p._id || p.product_id || "").toString();
    if (!p.image_url && mediaMap[pid]) {
      return { ...p, image_url: mediaMap[pid], image: mediaMap[pid] };
    }
    return p;
  });
}

async function fetchPromotedIds() {
  try {
    const promoTargets = await PromotionTarget.find({ targetType: "product" })
      .select("targetRefId")
      .lean();
    return new Set(promoTargets.map((t) => t.targetRefId?.toString()).filter(Boolean));
  } catch (_) {
    return new Set();
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Look Builder
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a complete makeup look for the given occasion/context.
 *
 * @param {object} context — ShoppingContext from extractShoppingContext()
 * @returns {Promise<{
 *   lookName: string,
 *   lookNameVi: string,
 *   occasion: string,
 *   style: string,
 *   totalPrice: number,
 *   slots: Array<{ slot, slotLabel, product }>,
 *   missingSlots: string[],
 * }>}
 */
async function buildMakeupLook(context) {
  const occasion = context.occasion || "default";
  const lookDef = LOOK_DEFINITIONS[occasion] || LOOK_DEFINITIONS.default;

  // Fetch promoted product IDs (for scoring)
  const promotedIds = await fetchPromotedIds();

  // Fetch and score candidates for each required slot in parallel
  const slotResults = await Promise.allSettled(
    lookDef.slots.map(async (slot) => {
      const candidates = await fetchCandidatesForSlot(slot, context, 10);
      const enriched = await enrichWithMedia(candidates);
      const scored = scoreAndRankProducts(enriched, context, promotedIds, 1);
      return { slot, product: scored[0] || null };
    })
  );

  const slots = [];
  const missingSlots = [];
  let totalPrice = 0;

  for (const result of slotResults) {
    if (result.status === "fulfilled") {
      const { slot, product } = result.value;
      if (product) {
        slots.push({
          slot,
          slotLabel: SLOT_LABEL_VI[slot] || slot,
          product,
        });
        totalPrice += product.pricing?.original || product.price || 0;
      } else {
        missingSlots.push(SLOT_LABEL_VI[slot] || slot);
      }
    }
  }

  return {
    lookName: lookDef.name,
    lookNameVi: lookDef.nameVi,
    occasion,
    style: lookDef.style,
    totalPrice,
    slots,
    missingSlots,
    productCount: slots.length,
  };
}

module.exports = { buildMakeupLook, LOOK_DEFINITIONS, SLOT_CATEGORY_NAMES };
