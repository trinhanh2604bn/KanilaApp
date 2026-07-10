/**
 * makeupRecommendation.service.js
 * Makeup-specific product retrieval for Kanila AI Chatbot.
 *
 * Phase 8: Makeup Commerce Sales Assistant (message-string path)
 * Phase 9: Advanced Pipeline integration (ShoppingContext path)
 *
 * Exports:
 *   findMakeupProducts()         — Phase 8 legacy, accepts { message, ... }
 *   findMakeupProductsPipeline() — Phase 9, accepts ShoppingContext
 */

"use strict";

const Product = require("../models/product.model");
const Category = require("../models/category.model");
const ProductMedia = require("../models/productMedia.model");
const ReviewSummary = require("../models/reviewSummary.model");
const PromotionTarget = require("../models/promotionTarget.model");
const { parsePriceRange } = require("../utils/priceParser");
const {
  parseMakeupCategory,
  parseFinish,
  parseTone,
  parseOccasion,
  parseUseCaseHints,
} = require("../utils/makeupIntentParser");
const { scoreAndRankProducts } = require("./chatbotProductScorer");

const MAX_RESULTS = 5;
const CANDIDATE_POOL = 15;

// ─────────────────────────────────────────────────────────────────────────────
// Category resolution
// ─────────────────────────────────────────────────────────────────────────────

function normalizeText(text) {
  return text.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
}

async function resolveMakeupCategoryIds(names) {
  if (!names || names.length === 0) return [];

  // Expand aliases based on PART 4 requirements
  const expandedNames = [...names];
  const inputJoined = names.join(" ").toLowerCase();
  if (inputJoined.includes("cushion") || inputJoined.includes("phấn nước") || inputJoined.includes("phấn nền")) {
    expandedNames.push("Cushion", "Phấn nước", "Phấn nền");
  }
  if (inputJoined.includes("kem nền") || inputJoined.includes("foundation") || inputJoined.includes("nền")) {
    expandedNames.push("Kem nền", "Foundation", "Nền");
  }
  if (inputJoined.includes("son tint") || inputJoined.includes("tint môi") || inputJoined.includes("tint")) {
    expandedNames.push("Son tint", "Tint môi");
  }
  if (inputJoined.includes("mascara") || inputJoined.includes("chuốt mi")) {
    expandedNames.push("Mascara", "Chuốt mi");
  }

  const normalizedInput = expandedNames.map(normalizeText);

  // Fetch all active categories to do in-memory accent-insensitive matching
  const categories = await Category.find({ isActive: true }).select("_id categoryName").lean();
  
  const resolvedCategoryIds = [];
  for (const cat of categories) {
    if (cat.categoryName) {
      const normCat = normalizeText(cat.categoryName);
      if (normalizedInput.some(input => normCat === input || normCat.includes(input))) {
        resolvedCategoryIds.push(cat._id);
      }
    }
  }

  if (process.env.NODE_ENV === "development" || process.env.CHATBOT_DEBUG === "true") {
    console.log("[CATEGORY_RESOLUTION]", { inputNames: names, resolvedCategoryIds });
  }

  return resolvedCategoryIds;
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

function resolveStockStatus(stock) {
  if (stock === null || stock === undefined) return "unknown";
  if (stock <= 0) return "out_of_stock";
  if (stock <= 10) return "low_stock";
  return "in_stock";
}

const OCCASION_LABEL = {
  party: "đi tiệc", wedding: "đám cưới", school: "đi học",
  office: "đi làm", date: "hẹn hò", daily: "dùng hàng ngày",
};
const FINISH_LABEL = {
  matte: "matte kiềm dầu", glowy: "glowy tươi sáng", satin: "satin mềm mịn",
  dewy: "dewy ẩm mướt", velvet: "velvet nhung mờ", tint: "tint bám màu",
  natural: "look tự nhiên",
};
const TONE_LABEL = {
  olive: "da ngăm/olive", light: "da sáng/trắng", neutral: "da trung bình",
  warm: "tông ấm", cool: "tông lạnh",
};

function buildMatchedReason(product, filters) {
  const parts = [];
  const cat = product.category_name || product.categoryName || "";
  const brand = product.brand_name || product.brandName || "";
  if (cat) parts.push(cat);
  if (brand) parts.push(`thương hiệu ${brand}`);
  if (filters.maxPrice && product.price <= filters.maxPrice) {
    parts.push(`giá trong ngân sách (${product.price.toLocaleString("vi-VN")}đ)`);
  }
  if (filters.finish && product.finish_type) {
    if (product.finish_type.toLowerCase().includes(filters.finish)) {
      parts.push(FINISH_LABEL[filters.finish] || filters.finish);
    }
  }
  if (filters.tone && product.tone_match_supported?.length) {
    if (product.tone_match_supported.some((t) => t.toLowerCase().includes(filters.tone))) {
      parts.push(`phù hợp ${TONE_LABEL[filters.tone] || filters.tone}`);
    }
  }
  if (filters.occasion) parts.push(`phù hợp ${OCCASION_LABEL[filters.occasion] || filters.occasion}`);
  if (filters.useCaseHints?.waterproof) parts.push("chống nước / chống lem");
  if (filters.useCaseHints?.longWear) parts.push("lâu trôi bền màu");
  if (product.rating && product.rating >= 4.5) parts.push(`đánh giá cao ${product.rating.toFixed(1)}★`);
  return parts.length > 0 ? parts.join(", ") + "." : "Sản phẩm phù hợp với nhu cầu makeup của bạn.";
}

function buildSuggestedUse(product, occasion) {
  if (!occasion) return "Dùng theo hướng dẫn trên bao bì.";
  const cat = ((product.category_name || product.categoryName) || "").toLowerCase();
  if (occasion === "party" || occasion === "wedding") {
    if (cat.includes("son") || cat.includes("lip")) return "Chồng 2-3 lớp để màu rõ nét và bền suốt buổi tiệc.";
    if (cat.includes("cushion") || cat.includes("kem nền")) return "Dùng primer trước để nền bám chắc, tái tạo giữa buổi nếu cần.";
    return "Kết hợp với setting spray để makeup bền cả đêm.";
  }
  if (occasion === "school" || occasion === "daily") {
    if (cat.includes("son") || cat.includes("lip")) return "Dùng 1 lớp cho look tươi tắn tự nhiên.";
    if (cat.includes("cushion") || cat.includes("kem nền")) return "Thoa 1 lớp mỏng để nền nhẹ nhàng, thở da tốt cả ngày.";
    return "Phù hợp makeup nhanh 5-10 phút mỗi sáng.";
  }
  if (occasion === "office") return "Chọn màu neutral, không quá đậm, phù hợp môi trường chuyên nghiệp.";
  if (occasion === "date") return "Tông màu ấm nhẹ, finish tự nhiên hoặc dewy sẽ tạo cảm giác gần gũi, cuốn hút.";
  return "Điều chỉnh lượng dùng theo mục đích trang điểm.";
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared enrichment helpers
// ─────────────────────────────────────────────────────────────────────────────

async function enrichCandidates(products) {
  if (products.length === 0) return { reviewMap: {}, mediaMap: {} };
  const productIds = products.map((p) => p._id);
  const [reviewSummaries, mediaList] = await Promise.all([
    ReviewSummary.find({ productId: { $in: productIds } })
      .select("productId reviewCount averageRating").lean().catch(() => []),
    ProductMedia.find({ productId: { $in: productIds } })
      .sort({ isPrimary: -1, sortOrder: 1 })
      .select("productId mediaUrl isPrimary").lean().catch(() => []),
  ]);
  const reviewMap = {};
  for (const r of reviewSummaries) reviewMap[r.productId.toString()] = r;
  const mediaMap = {};
  for (const m of mediaList) {
    const pid = m.productId.toString();
    if (!mediaMap[pid]) mediaMap[pid] = m.mediaUrl;
  }
  return { reviewMap, mediaMap };
}

async function fetchPromotedProductIds(productIds) {
  try {
    const targets = await PromotionTarget.find({
      targetType: "product",
      targetRefId: { $in: productIds },
    }).select("targetRefId").lean();
    return new Set(targets.map((t) => t.targetRefId.toString()));
  } catch (_) {
    return new Set();
  }
}

function normalizeRaw(p, reviewMap, mediaMap) {
  const pid = p._id.toString();
  const review = reviewMap[pid];
  const imageUrl = p.imageUrl?.trim() ? p.imageUrl : (mediaMap[pid] || "");
  const rating = review
    ? parseFloat(review.averageRating.toFixed(1))
    : p.averageRating > 0 ? parseFloat(p.averageRating.toFixed(1)) : null;
  const reviewCount = review ? review.reviewCount : 0;
  const stockStatus = resolveStockStatus(p.stock);

  return {
    product_id: pid, productId: pid, _id: p._id,
    slug: p.slug || "",
    name: p.productName, productName: p.productName,
    brand_name: p.brandId?.brandName || "", brandName: p.brandId?.brandName || "",
    category_name: p.categoryId?.categoryName || "", categoryName: p.categoryId?.categoryName || "",
    _categoryCode: p.categoryId?.categoryCode || "",
    price: p.price || 0,
    compareAtPrice: p.compareAtPrice || null, compare_at_price: p.compareAtPrice || null,
    finalPrice: p.compareAtPrice && p.compareAtPrice < p.price ? p.compareAtPrice : (p.price || 0),
    imageUrl, image_url: imageUrl, image: imageUrl,
    rating, averageRating: rating,
    review_count: reviewCount, reviewCount,
    stock_status: stockStatus, stockStatus,
    finish_type: p.finish_type || null,
    shades: (p.shades || []).slice(0, 5),
    tone_match_supported: p.tone_match_supported || [],
    skin_types_supported: p.skin_types_supported || [],
    is_sensitive_friendly: p.is_sensitive_friendly || false,
    isActive: p.isActive,
    productStatus: p.productStatus,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 8 legacy: findMakeupProducts (message-string based)
// ─────────────────────────────────────────────────────────────────────────────

async function findMakeupProducts({ message, limit = MAX_RESULTS, customerProfile = null, intentHint = null }) {
  const priceRange = parsePriceRange(message);
  const categoryParsed = parseMakeupCategory(message);
  const finish = parseFinish(message);
  const tone = parseTone(message);
  const occasion = parseOccasion(message);
  const useCaseHints = parseUseCaseHints(message);

  const filters = {
    category: categoryParsed?.categoryCode || null,
    categoryNames: categoryParsed?.categoryNames || [],
    minPrice: priceRange.minPrice,
    maxPrice: priceRange.maxPrice,
    sortHint: priceRange.sortHint,
    finish, tone, occasion, useCaseHints,
  };

  const dbFilter = { isActive: true, productStatus: "active" };
  if (filters.maxPrice) dbFilter.price = { $lte: filters.maxPrice };
  if (filters.minPrice) dbFilter.price = { ...(dbFilter.price || {}), $gte: filters.minPrice };

  if (filters.categoryNames.length > 0) {
    try {
      const catIds = await resolveMakeupCategoryIds(filters.categoryNames);
      if (catIds.length > 0) dbFilter.categoryId = { $in: catIds };
    } catch (_) {}
  }
  if (filters.finish) dbFilter.finish_type = new RegExp(filters.finish, "i");
  if (filters.tone) dbFilter.tone_match_supported = new RegExp(filters.tone, "i");
  if (useCaseHints.waterproof) {
    dbFilter.$or = [
      { concerns_targeted: /waterproof|long_wear|chống lem/i },
      { ingredient_flags: /waterproof/i },
    ];
  }

  let sortObj;
  if (filters.sortHint === "price_asc") sortObj = { price: 1 };
  else if (filters.sortHint === "price_desc") sortObj = { price: -1 };
  else sortObj = { averageRating: -1, is_best_seller: -1, sales_count: -1, createdAt: -1 };

  let rawProducts = [];
  try {
    rawProducts = await Product.find(dbFilter).sort(sortObj).limit(CANDIDATE_POOL)
      .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
  } catch (err) {
    console.error("[MakeupRecommendation] DB query failed:", err.message);
    return { products: [], filters };
  }

  // Fallback 1: relax finish/tone/waterproof
  if (rawProducts.length === 0 && (filters.finish || filters.tone || useCaseHints.waterproof)) {
    const relaxed = {
      isActive: true, productStatus: "active",
      ...(dbFilter.categoryId ? { categoryId: dbFilter.categoryId } : {}),
      ...(dbFilter.price ? { price: dbFilter.price } : {}),
    };
    try {
      rawProducts = await Product.find(relaxed).sort(sortObj).limit(CANDIDATE_POOL)
        .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
    } catch (_) {}
  }

  // Fallback 2: price only
  if (rawProducts.length === 0) {
    const priceOnly = { isActive: true, productStatus: "active", ...(dbFilter.price ? { price: dbFilter.price } : {}) };
    try {
      rawProducts = await Product.find(priceOnly).sort(sortObj).limit(CANDIDATE_POOL)
        .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
    } catch (_) {}
  }

  if (rawProducts.length === 0) return { products: [], filters };

  const { reviewMap, mediaMap } = await enrichCandidates(rawProducts);
  const normalized = rawProducts.map((p) => normalizeRaw(p, reviewMap, mediaMap));

  // Phase 9: score and re-rank
  const shoppingContext = {
    productCategory: filters.category,
    categoryCode: filters.category,
    categoryNames: filters.categoryNames,
    budget: { min: filters.minPrice, max: filters.maxPrice },
    finishPreference: filters.finish ? [filters.finish] : [],
    occasion: filters.occasion,
    skinType: null, skinConcerns: [],
    wantsLongWear: filters.useCaseHints?.longWear || false,
    wantsWaterproof: filters.useCaseHints?.waterproof || false,
    wantsOutOfStock: false,
    makeupStyle: null,
  };
  const promotedIds = await fetchPromotedProductIds(rawProducts.map((p) => p._id));
  const scored = scoreAndRankProducts(normalized, shoppingContext, promotedIds, limit);

  return { products: scored, filters };
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 9 pipeline: findMakeupProductsPipeline (ShoppingContext based)
// ─────────────────────────────────────────────────────────────────────────────

async function findMakeupProductsPipeline(shoppingContext, limit = MAX_RESULTS) {
  const dbFilter = { isActive: true, productStatus: "active" };

  if (shoppingContext.categoryNames?.length > 0) {
    try {
      const catIds = await resolveMakeupCategoryIds(shoppingContext.categoryNames);
      if (catIds.length > 0) dbFilter.categoryId = { $in: catIds };
    } catch (_) {}
  }

  if (shoppingContext.budget?.max) dbFilter.price = { $lte: shoppingContext.budget.max };
  if (shoppingContext.budget?.min) dbFilter.price = { ...(dbFilter.price || {}), $gte: shoppingContext.budget.min };
  if (shoppingContext.finishPreference?.length) {
    dbFilter.finish_type = new RegExp(shoppingContext.finishPreference.join("|"), "i");
  }

  const sortObj = { averageRating: -1, is_best_seller: -1, sales_count: -1, createdAt: -1 };

  let rawProducts = [];
  try {
    rawProducts = await Product.find(dbFilter).sort(sortObj).limit(CANDIDATE_POOL)
      .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
  } catch (err) {
    console.error("[MakeupRecommendation Pipeline] DB query failed:", err.message);
    return { products: [], filters: {}, candidateCount: 0, dbFilter };
  }

  // Fallback 1: relax finish
  if (rawProducts.length === 0 && shoppingContext.finishPreference?.length) {
    const relaxed = {
      isActive: true, productStatus: "active",
      ...(dbFilter.categoryId ? { categoryId: dbFilter.categoryId } : {}),
      ...(dbFilter.price ? { price: dbFilter.price } : {}),
    };
    try {
      rawProducts = await Product.find(relaxed).sort(sortObj).limit(CANDIDATE_POOL)
        .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
    } catch (_) {}
  }

  // Fallback 2: price only
  if (rawProducts.length === 0) {
    const priceOnly = { isActive: true, productStatus: "active", ...(dbFilter.price ? { price: dbFilter.price } : {}) };
    try {
      rawProducts = await Product.find(priceOnly).sort(sortObj).limit(CANDIDATE_POOL)
        .populate("brandId", "brandName").populate("categoryId", "categoryName categoryCode").lean();
    } catch (_) {}
  }

  if (rawProducts.length === 0) return { products: [], filters: {}, candidateCount: 0, dbFilter };

  const candidateCount = rawProducts.length;
  const { reviewMap, mediaMap } = await enrichCandidates(rawProducts);
  const promotedIds = await fetchPromotedProductIds(rawProducts.map((p) => p._id));
  const normalized = rawProducts.map((p) => normalizeRaw(p, reviewMap, mediaMap));
  const scored = scoreAndRankProducts(normalized, shoppingContext, promotedIds, limit);

  const filters = {
    category: shoppingContext.categoryCode,
    categoryNames: shoppingContext.categoryNames,
    maxPrice: shoppingContext.budget?.max,
    minPrice: shoppingContext.budget?.min,
    finish: shoppingContext.finishPreference?.[0] || null,
    occasion: shoppingContext.occasion,
    useCaseHints: {
      waterproof: shoppingContext.wantsWaterproof,
      longWear: shoppingContext.wantsLongWear,
    },
  };

  return { products: scored, filters, candidateCount, dbFilter };
}

module.exports = {
  findMakeupProducts,
  findMakeupProductsPipeline,
  buildMatchedReason,
  buildSuggestedUse,
  resolveMakeupCategoryIds,
};
