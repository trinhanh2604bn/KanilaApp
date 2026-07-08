/**
 * chatbotProduct.tool.js
 * MongoDB product query tool for the Kanila chatbot.
 *
 * Uses ONLY verified field names from actual schema inspection:
 *   Product: productName, slug, price, compareAtPrice, imageUrl, stock,
 *            averageRating, isActive, productStatus, brandId, categoryId,
 *            skin_types_supported[], concerns_targeted[], is_sensitive_friendly
 *   Brand:   brandName
 *   Category: categoryName, categoryCode
 *   ProductMedia: mediaUrl, isPrimary, sortOrder
 *   ReviewSummary: reviewCount, averageRating (keyed by productId)
 *   ProductBeautyProfile: suitable_skin_types[], suitable_skin_concerns[]
 *
 * Does NOT expose: costAmount, internal inventory details, margin, audit fields.
 */

const mongoose = require("mongoose");
const Product = require("../models/product.model");
const Brand = require("../models/brand.model");
const Category = require("../models/category.model");
const ProductMedia = require("../models/productMedia.model");
const ReviewSummary = require("../models/reviewSummary.model");
const ProductBeautyProfile = require("../models/productBeautyProfile.model");
const { parseProductConstraints } = require("./chatbotProductQuery.parser");

const MAX_RESULTS = 5;

// ─────────────────────────────────────────────────────────────────────────────
// Category resolution
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find category IDs matching any of the given name keywords (case-insensitive).
 * @param {string[]} names — e.g. ["kem chống nắng", "sunscreen"]
 * @returns {Promise<mongoose.Types.ObjectId[]>}
 */
async function resolveCategoryIds(names) {
  if (!names || names.length === 0) return [];
  const regexes = names.map((n) => new RegExp(n, "i"));
  const categories = await Category.find({
    $or: [
      { categoryName: { $in: regexes } },
      { categoryCode: { $in: names.map((n) => n.toUpperCase()) } },
    ],
    isActive: true,
  })
    .select("_id")
    .lean();
  return categories.map((c) => c._id);
}

// ─────────────────────────────────────────────────────────────────────────────
// Reason builder
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Generate a Vietnamese reason string for a recommendation based on matched constraints.
 * Backend builds this before Gemini polish — ensures no hallucination.
 * @param {object} product — normalized product object
 * @param {object} constraints — parsed constraints
 * @returns {string}
 */
function buildReason(product, constraints) {
  const parts = [];

  if (constraints.skinType && product.skin_types_supported &&
      product.skin_types_supported.includes(constraints.skinType)) {
    const skinTypeLabels = {
      oily: "da dầu", dry: "da khô", sensitive: "da nhạy cảm",
      combination: "da hỗn hợp", normal: "da thường",
    };
    parts.push(`Phù hợp ${skinTypeLabels[constraints.skinType] || constraints.skinType}`);
  }

  if (constraints.skinConcern && product.concerns_targeted &&
      product.concerns_targeted.includes(constraints.skinConcern)) {
    const concernLabels = {
      acne: "kiểm soát mụn", dark_spot: "giảm thâm", dullness: "cải thiện xỉn màu",
      dryness: "cấp ẩm", oil_control: "kiểm soát dầu nhờn", sensitive: "dịu nhẹ cho da nhạy",
      anti_aging: "chống lão hóa", pore: "thu nhỏ lỗ chân lông",
    };
    parts.push(concernLabels[constraints.skinConcern] || constraints.skinConcern);
  }

  if (constraints.isSensitiveFriendly && product.is_sensitive_friendly) {
    parts.push("an toàn cho da nhạy cảm");
  }

  if (constraints.budgetMax && product.price <= constraints.budgetMax) {
    parts.push(`giá trong ngân sách`);
  }

  if (product.stock_status === "in_stock") {
    parts.push("còn hàng");
  }

  if (product.rating && product.rating >= 4.5) {
    parts.push(`đánh giá cao (${product.rating.toFixed(1)}★)`);
  }

  return parts.length > 0
    ? parts.join(", ") + "."
    : "Sản phẩm phù hợp với nhu cầu của bạn.";
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
// Main query function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find recommended products based on the user's message constraints.
 *
 * @param {object} params
 * @param {string} params.message — raw user message for parsing
 * @param {number} [params.limit=5] — max number of products to return
 * @returns {Promise<{ products: object[], constraints: object }>}
 */
async function findRecommendedProducts({ message, limit = MAX_RESULTS }) {
  // 1. Parse all constraints from the message
  const constraints = parseProductConstraints(message);

  // 2. Build MongoDB filter
  const filter = {
    isActive: true,
    productStatus: "active",
  };

  // Budget filter (use product.price — direct field, no join needed)
  if (constraints.budgetMax) {
    filter.price = { $lte: constraints.budgetMax };
  }

  // Skin type filter — uses product.skin_types_supported[]
  if (constraints.skinType) {
    filter.skin_types_supported = constraints.skinType;
  }

  // Skin concern filter — uses product.concerns_targeted[]
  if (constraints.skinConcern) {
    filter.concerns_targeted = constraints.skinConcern;
  }

  // Sensitive skin filter
  if (constraints.isSensitiveFriendly) {
    filter.is_sensitive_friendly = true;
  }

  // Category filter — resolve category IDs from name keywords
  if (constraints.categoryIntent) {
    try {
      const categoryIds = await resolveCategoryIds(constraints.categoryIntent.names);
      if (categoryIds.length > 0) {
        filter.categoryId = { $in: categoryIds };
      }
      // If no category ID found, fall back to text search on productName
    } catch (_) {
      // Non-fatal — proceed without category filter
    }
  }

  // 3. Query products — sort by rating desc, then sales desc, then newest
  let products;
  try {
    products = await Product.find(filter)
      .sort({ averageRating: -1, sales_count: -1, createdAt: -1 })
      .limit(limit)
      .populate("brandId", "brandName")
      .populate("categoryId", "categoryName")
      .lean();
  } catch (err) {
    console.error("[ChatbotProduct] Primary query failed:", err.message);
    return { products: [], constraints };
  }

  // 4. Fallback: if strict filters return 0 results, relax to category + budget only
  if (products.length === 0 && (constraints.skinType || constraints.skinConcern)) {
    const relaxedFilter = {
      isActive: true,
      productStatus: "active",
      ...(filter.categoryId ? { categoryId: filter.categoryId } : {}),
      ...(filter.price ? { price: filter.price } : {}),
    };
    try {
      products = await Product.find(relaxedFilter)
        .sort({ averageRating: -1, sales_count: -1 })
        .limit(limit)
        .populate("brandId", "brandName")
        .populate("categoryId", "categoryName")
        .lean();
    } catch (_) {
      return { products: [], constraints };
    }
  }

  if (products.length === 0) {
    return { products: [], constraints };
  }

  const productIds = products.map((p) => p._id);

  // 5. Enrich: fetch review summaries and primary media in parallel
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

  // Index enrichment data by productId string for O(1) lookup
  const reviewMap = {};
  for (const r of reviewSummaries) {
    reviewMap[r.productId.toString()] = r;
  }

  const mediaMap = {};
  for (const m of mediaList) {
    const pid = m.productId.toString();
    // Keep only the first (highest priority: isPrimary=true, sortOrder ASC) image per product
    if (!mediaMap[pid]) mediaMap[pid] = m.mediaUrl;
  }

  // 6. Normalize to lightweight chatbot product objects
  const normalized = products.map((p) => {
    const pid = p._id.toString();
    const review = reviewMap[pid];

    // Image: product.imageUrl → ProductMedia → ""
    const imageUrl =
      p.imageUrl && p.imageUrl.trim() !== ""
        ? p.imageUrl
        : mediaMap[pid] || "";

    // Rating: ReviewSummary preferred, then product.averageRating
    const rating = review
      ? parseFloat(review.averageRating.toFixed(1))
      : p.averageRating > 0
      ? parseFloat(p.averageRating.toFixed(1))
      : null;

    const reviewCount = review ? review.reviewCount : 0;
    const stockStatus = resolveStockStatus(p.stock);

    const normalized = {
      product_id: pid,
      variant_id: null,           // No variant selected in Phase 2A
      slug: p.slug || "",
      name: p.productName,
      brand_name: p.brandId?.brandName || "",
      category_name: p.categoryId?.categoryName || "",
      price: p.price,
      compare_at_price: p.compareAtPrice || null,
      image_url: imageUrl,
      rating,
      review_count: reviewCount,
      stock_status: stockStatus,
      skin_types_supported: p.skin_types_supported || [],
      concerns_targeted: p.concerns_targeted || [],
      is_sensitive_friendly: p.is_sensitive_friendly || false,
      reason: "",                 // filled below
    };

    normalized.reason = buildReason(normalized, constraints);
    return normalized;
  });

  return { products: normalized, constraints };
}

module.exports = { findRecommendedProducts };
