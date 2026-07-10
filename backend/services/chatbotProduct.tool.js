/**
 * chatbotProduct.tool.js
 * Stage 3 + 5 of the Advanced Recommendation Pipeline.
 *
 * Priority-based product candidate retrieval (Stage 3) and
 * quality-gate + card formatting (Stage 5).
 *
 * Changes from Phase 2/8:
 * - findRecommendedProducts() now accepts ShoppingContext OR legacy message string
 * - Retrieves 15 candidates before scoring (was 5)
 * - Adds promotion flag lookup (PromotionTarget)
 * - Delegates scoring to chatbotProductScorer.js
 * - Returns Phase 9 product card format (with backward-compat aliases)
 *
 * Uses ONLY verified field names from actual schema:
 *   Product: productName, slug, price, compareAtPrice, imageUrl, stock,
 *            averageRating, isActive, productStatus, brandId, categoryId,
 *            skin_types_supported[], concerns_targeted[], is_sensitive_friendly,
 *            finish_type, shades[], tone_match_supported[], is_best_seller, sales_count
 *   Brand:   brandName
 *   Category: categoryName, categoryCode
 *   ProductMedia: mediaUrl, isPrimary, sortOrder
 *   ReviewSummary: reviewCount, averageRating
 *
 * Does NOT expose: costAmount, internal inventory details, margin, audit fields.
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

const Product = require("../models/product.model");
const Category = require("../models/category.model");
const ProductMedia = require("../models/productMedia.model");
const ReviewSummary = require("../models/reviewSummary.model");
const PromotionTarget = require("../models/promotionTarget.model");
const { parseProductConstraints } = require("./chatbotProductQuery.parser");
const { scoreAndRankProducts } = require("./chatbotProductScorer");

const CANDIDATE_POOL = 15; // Over-fetch before scoring
const MAX_RESULTS = 5;

// ─────────────────────────────────────────────────────────────────────────────
// Category resolution
// ─────────────────────────────────────────────────────────────────────────────

async function resolveCategoryIds(names) {
  if (!names || names.length === 0) return [];
  const regexes = names.map((n) => new RegExp(n.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "i"));
  const categories = await Category.find({
    $or: [
      { categoryName: { $in: regexes } },
      { categoryCode: { $in: names.map((n) => n.toUpperCase()) } },
    ],
    isActive: true,
  })
    .select("_id categoryCode")
    .lean();
  return { ids: categories.map((c) => c._id), codes: categories.map((c) => c.categoryCode) };
}

// ─────────────────────────────────────────────────────────────────────────────
// Stock status
// ─────────────────────────────────────────────────────────────────────────────

function resolveStockStatus(stock) {
  if (stock === null || stock === undefined) return "unknown";
  if (stock <= 0) return "out_of_stock";
  if (stock <= 10) return "low_stock";
  return "in_stock";
}

// ─────────────────────────────────────────────────────────────────────────────
// Promotion lookup
// ─────────────────────────────────────────────────────────────────────────────

async function fetchPromotedProductIds(productIds) {
  if (!productIds || productIds.length === 0) return new Set();
  try {
    const targets = await PromotionTarget.find({
      targetType: "product",
      targetRefId: { $in: productIds },
    })
      .select("targetRefId")
      .lean();
    return new Set(targets.map((t) => t.targetRefId.toString()));
  } catch (_) {
    return new Set();
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Context normalizer — accepts ShoppingContext OR legacy constraints object
// ─────────────────────────────────────────────────────────────────────────────

function normalizeContext(contextOrMessage) {
  // If it's a string → parse legacy style
  if (typeof contextOrMessage === "string") {
    const constraints = parseProductConstraints(contextOrMessage);
    return {
      productCategory: constraints.categoryIntent?.value || null,
      categoryCode: null,
      categoryNames: constraints.categoryIntent?.names || [],
      skinType: constraints.skinType,
      skinConcerns: constraints.skinConcern ? [constraints.skinConcern] : [],
      isSensitiveFriendly: constraints.isSensitiveFriendly,
      budget: { min: null, max: constraints.budgetMax },
      finishPreference: [],
      shadePreference: [],
      requirements: [],
      wantsBestSeller: false,
      wantsSale: false,
      wantsLongWear: false,
      wantsWaterproof: false,
      wantsOutOfStock: false,
      detectedIntent: "find_product",
      rawMessage: contextOrMessage,
      // Legacy compat
      _legacyConstraints: constraints,
    };
  }

  // ShoppingContext object — use directly
  return contextOrMessage;
}

// ─────────────────────────────────────────────────────────────────────────────
// Build MongoDB filter from ShoppingContext (priority-based)
// ─────────────────────────────────────────────────────────────────────────────

async function buildProductFilter(context) {
  const filter = {
    isActive: true,
    productStatus: "active",
  };

  // Priority 1: Category match
  const catNames = context.categoryNames || [];
  if (catNames.length > 0) {
    try {
      const { ids } = await resolveCategoryIds(catNames);
      if (ids.length > 0) {
        filter.categoryId = { $in: ids };
      }
    } catch (_) {
      // non-fatal
    }
  }

  // Priority 2: Need match (skin type)
  if (context.skinType) {
    filter.skin_types_supported = context.skinType;
  }

  // Priority 2: Skin concerns
  if (context.skinConcerns && context.skinConcerns.length > 0) {
    filter.concerns_targeted = { $in: context.skinConcerns };
  }

  // Priority 2: Sensitive friendly
  if (context.isSensitiveFriendly) {
    filter.is_sensitive_friendly = true;
  }

  // Priority 3: Budget
  if (context.budget?.max) {
    filter.price = { $lte: context.budget.max };
  }

  // Priority 4: Best sellers
  if (context.wantsBestSeller) {
    filter.is_best_seller = true;
  }

  return filter;
}

// ─────────────────────────────────────────────────────────────────────────────
// Normalize DB product to chatbot format
// ─────────────────────────────────────────────────────────────────────────────

function normalizeProduct(p, reviewMap, mediaMap) {
  const pid = p._id.toString();
  const review = reviewMap[pid];
  const imageUrl = p.imageUrl?.trim() ? p.imageUrl : (mediaMap[pid] || "");
  const rating = review
    ? parseFloat(review.averageRating.toFixed(1))
    : p.averageRating > 0 ? parseFloat(p.averageRating.toFixed(1)) : null;
  const reviewCount = review ? review.reviewCount : 0;
  const stockStatus = resolveStockStatus(p.stock);

  return {
    product_id: pid,
    _id: p._id,
    slug: p.slug || "",
    productName: p.productName,
    name: p.productName,
    brand_name: p.brandId?.brandName || "",
    brandName: p.brandId?.brandName || "",
    category_name: p.categoryId?.categoryName || "",
    categoryName: p.categoryId?.categoryName || "",
    _categoryCode: p.categoryId?.categoryCode || "",
    category_code: p.categoryId?.categoryCode || "",
    price: p.price || 0,
    compare_at_price: p.compareAtPrice || null,
    compareAtPrice: p.compareAtPrice || null,
    imageUrl: imageUrl,
    image_url: imageUrl,
    image: imageUrl,
    rating,
    averageRating: rating,
    review_count: reviewCount,
    reviewCount,
    stock_status: stockStatus,
    stockStatus,
    skin_types_supported: p.skin_types_supported || [],
    concerns_targeted: p.concerns_targeted || [],
    is_sensitive_friendly: p.is_sensitive_friendly || false,
    finish_type: p.finish_type || null,
    shades: p.shades || [],
    tone_match_supported: p.tone_match_supported || [],
    is_best_seller: p.is_best_seller || false,
    isActive: p.isActive,
    productStatus: p.productStatus,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main: findRecommendedProducts
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find and score recommended products based on a ShoppingContext or message string.
 *
 * @param {object} params
 * @param {object|string} params.context  — ShoppingContext (Phase 9) or raw message string (legacy)
 * @param {string} [params.message]       — legacy: raw message string (used if context not provided)
 * @param {number} [params.limit=5]
 * @returns {Promise<{ products: object[], context: object, candidateCount: number }>}
 */
async function findRecommendedProducts({ context, message, limit = MAX_RESULTS }) {
  // Normalize context (supports legacy message-string callers)
  const shoppingContext = normalizeContext(context || message || "");

  // 1. Build priority-based filter
  const filter = await buildProductFilter(shoppingContext);

  // 2. Query candidates (over-fetch for scorer)
  let rawProducts = [];
  try {
    rawProducts = await Product.find(filter)
      .sort({ averageRating: -1, sales_count: -1, createdAt: -1 })
      .limit(CANDIDATE_POOL)
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName categoryCode")
      .lean();
  } catch (err) {
    console.error("[ChatbotProduct] Primary query failed:", err.message);
    return { products: [], context: shoppingContext, candidateCount: 0 };
  }

  // 3. Fallback 1: relax skin/concern filters, keep category + budget
  if (rawProducts.length === 0 && (shoppingContext.skinType || shoppingContext.skinConcerns?.length)) {
    const relaxed = {
      isActive: true,
      productStatus: "active",
      ...(filter.categoryId ? { categoryId: filter.categoryId } : {}),
      ...(filter.price ? { price: filter.price } : {}),
    };
    try {
      rawProducts = await Product.find(relaxed)
        .sort({ averageRating: -1, sales_count: -1 })
        .limit(CANDIDATE_POOL)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName categoryCode")
        .lean();
    } catch (_) {}
  }

  // 4. Fallback 2: price-only (broadest)
  if (rawProducts.length === 0) {
    const priceOnly = {
      isActive: true,
      productStatus: "active",
      ...(filter.price ? { price: filter.price } : {}),
    };
    try {
      rawProducts = await Product.find(priceOnly)
        .sort({ averageRating: -1 })
        .limit(CANDIDATE_POOL)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName categoryCode")
        .lean();
    } catch (_) {}
  }

  if (rawProducts.length === 0) {
    return { products: [], context: shoppingContext, candidateCount: 0 };
  }

  const candidateCount = rawProducts.length;
  const productIds = rawProducts.map((p) => p._id);

  // 5. Enrich: reviews + media in parallel
  const [reviewSummaries, mediaList] = await Promise.all([
    ReviewSummary.find({ productId: { $in: productIds } })
      .select("productId reviewCount averageRating")
      .lean()
      .catch(() => []),
    ProductMedia.find({ productId: { $in: productIds } })
      .sort({ isPrimary: -1, sortOrder: 1 })
      .select("productId mediaUrl isPrimary")
      .lean()
      .catch(() => []),
  ]);

  const reviewMap = {};
  for (const r of reviewSummaries) reviewMap[r.productId.toString()] = r;

  const mediaMap = {};
  for (const m of mediaList) {
    const pid = m.productId.toString();
    if (!mediaMap[pid]) mediaMap[pid] = m.mediaUrl;
  }

  // 6. Normalize
  const candidates = rawProducts.map((p) => normalizeProduct(p, reviewMap, mediaMap));

  // 7. Fetch promotion flags
  const promotedIds = await fetchPromotedProductIds(productIds);

  // 8. Score + rank + quality gate + format
  const products = scoreAndRankProducts(candidates, shoppingContext, promotedIds, limit);

  return { products, context: shoppingContext, candidateCount };
}

module.exports = { findRecommendedProducts };
