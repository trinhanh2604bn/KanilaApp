/**
 * chatbotProductScorer.js
 * Stage 4 of the Advanced Recommendation Pipeline.
 *
 * 7-Dimension weighted scoring engine for product ranking.
 * No database calls — receives enriched products as input.
 *
 * Score formula (max 100):
 *   category_match  × 30%
 *   need_match      × 25%
 *   budget_match    × 20%
 *   rating_score    × 10%
 *   review_count    ×  5%
 *   stock_score     ×  5%
 *   promotion_score ×  5%
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

// ─────────────────────────────────────────────────────────────────────────────
// Score weights (must sum to 100)
// ─────────────────────────────────────────────────────────────────────────────

const WEIGHTS = {
  category:  30,
  need:      25,
  budget:    20,
  rating:    10,
  review:     5,
  stock:      5,
  promotion:  5,
};

// ─────────────────────────────────────────────────────────────────────────────
// Skin type → finish/attribute mappings for need_match
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_FINISH_AFFINITY = {
  oily:        ["matte", "semi-matte", "powder", "oil_control"],
  dry:         ["dewy", "glowy", "satin", "hydrating", "moisturizing"],
  combination: ["satin", "semi-matte", "natural", "balance"],
  sensitive:   ["gentle", "fragrance_free", "non_comedogenic"],
  normal:      ["natural", "satin", "glowy"],
};

const OCCASION_FINISH_AFFINITY = {
  party:   ["glowy", "glam", "bold", "shimmer", "long_wear"],
  wedding: ["satin", "soft_glam", "glowy", "long_wear", "waterproof"],
  school:  ["natural", "light", "tint", "lightweight", "quick"],
  office:  ["natural", "satin", "polished", "long_wear"],
  date:    ["glowy", "dewy", "romantic", "long_wear"],
  daily:   ["natural", "light", "quick", "tint"],
};

const REQUIREMENT_FIELD_MAP = {
  oil_control:      ["finish_type", "skin_types_supported", "concerns_targeted"],
  long_wear:        ["finish_type"],
  waterproof:       [],  // checked via product name/description
  lightweight:      [],  // heuristic: low-coverage products
  natural:          ["finish_type"],
  moisturizing:     ["skin_types_supported", "concerns_targeted"],
  gentle:           ["is_sensitive_friendly"],
  non_comedogenic:  [],
};

// ─────────────────────────────────────────────────────────────────────────────
// Vietnamese label maps for explanation building
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_TYPE_LABEL = {
  oily: "da dầu", dry: "da khô", sensitive: "da nhạy cảm",
  combination: "da hỗn hợp", normal: "da thường",
};

const OCCASION_LABEL = {
  party: "đi tiệc", wedding: "đám cưới", school: "đi học",
  office: "đi làm", date: "hẹn hò", daily: "dùng hàng ngày",
};

const FINISH_LABEL = {
  matte: "matte kiềm dầu", glowy: "glowy sáng da", satin: "satin mềm mịn",
  dewy: "dewy ẩm mướt", velvet: "velvet nhung mờ", tint: "bám màu lâu",
  natural: "look tự nhiên", "semi-matte": "semi-matte",
};

// ─────────────────────────────────────────────────────────────────────────────
// Dimension scorers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * D1: Category match score (0–30).
 * Exact match = 30, related category = 15, no match = 0.
 */
function scoreCategoryMatch(product, context) {
  const { categoryCode, categoryNames = [], productCategory } = context;

  // Exact category code match
  if (categoryCode && product._categoryCode === categoryCode) return 30;
  if (categoryCode && product.category_code === categoryCode) return 30;

  // Category name match
  const productCat = (product.category_name || "").toLowerCase();
  if (categoryNames.length) {
    for (const name of categoryNames) {
      if (productCat.includes(name.toLowerCase()) || name.toLowerCase().includes(productCat)) {
        return 30;
      }
    }
  }

  // Category key match
  if (productCategory && productCat.includes(productCategory.replace(/_/g, " "))) return 25;

  // No category specified → neutral score
  if (!categoryCode && !categoryNames.length && !productCategory) return 15;

  return 0;
}

/**
 * D2: Need match score (0–25).
 * Matches skin type, occasion, finish preference, requirements.
 */
function scoreNeedMatch(product, context) {
  let score = 0;
  const reasons = [];
  const { skinType, occasion, finishPreference, requirements = [], wantsLongWear, wantsWaterproof } = context;

  // Skin type affinity → finish/attribute match
  if (skinType) {
    const affinityFinishes = SKIN_FINISH_AFFINITY[skinType] || [];
    const productFinish = (product.finish_type || "").toLowerCase();

    // Check if product finish aligns with skin type needs
    if (affinityFinishes.some((f) => productFinish.includes(f))) {
      score += 10;
      reasons.push(`finish phù hợp ${SKIN_TYPE_LABEL[skinType]}`);
    }

    // Check skin_types_supported
    if (product.skin_types_supported && product.skin_types_supported.includes(skinType)) {
      score += 8;
      reasons.push(`hỗ trợ ${SKIN_TYPE_LABEL[skinType]}`);
    }

    // Sensitive skin bonus
    if (skinType === "sensitive" && product.is_sensitive_friendly) {
      score += 5;
      reasons.push("an toàn da nhạy cảm");
    }
  }

  // Occasion affinity
  if (occasion) {
    const affinityFinishes = OCCASION_FINISH_AFFINITY[occasion] || [];
    const productFinish = (product.finish_type || "").toLowerCase();
    const productName = (product.productName || product.name || "").toLowerCase();

    if (affinityFinishes.some((f) => productFinish.includes(f) || productName.includes(f))) {
      score += 5;
      reasons.push(`phù hợp ${OCCASION_LABEL[occasion]}`);
    }
  }

  // Explicit finish preference
  if (finishPreference && finishPreference.length > 0) {
    const productFinish = (product.finish_type || "").toLowerCase();
    if (finishPreference.some((f) => productFinish.includes(f))) {
      score += 5;
      reasons.push(`finish ${finishPreference[0]}`);
    }
  }

  // Long wear / waterproof signals
  const productName = (product.productName || product.name || "").toLowerCase();
  if (wantsLongWear && (productName.includes("lâu trôi") || productName.includes("long wear") || productName.includes("24h"))) {
    score += 3;
    reasons.push("lâu trôi bền màu");
  }
  if (wantsWaterproof && (productName.includes("waterproof") || productName.includes("chống nước"))) {
    score += 3;
    reasons.push("chống nước");
  }

  // Cap at 25
  return { score: Math.min(25, score), reasons };
}

/**
 * D3: Budget match score (0–20).
 */
function scoreBudgetMatch(product, context) {
  const { budget } = context;
  const max = budget?.max;
  const min = budget?.min;

  if (!max && !min) return 10; // No budget constraint → neutral

  const price = product.price || 0;

  if (max && price <= max) {
    // Within budget
    const ratio = price / max;
    if (ratio >= 0.5) return 20; // Good value in budget range
    return 15; // Very cheap, may seem low quality
  }

  if (max && price <= max * 1.1) {
    // Within 10% over budget
    return 8;
  }

  if (max && price > max * 1.1) {
    return 0; // Over budget
  }

  return 10; // Min only or no constraint
}

/**
 * D4: Rating score (0–10).
 */
function scoreRating(product) {
  const rating = product.rating || product.averageRating || 0;
  if (!rating) return 0;
  return Math.round((rating / 5) * 10);
}

/**
 * D5: Review count score (0–5). Log-scaled.
 */
function scoreReviewCount(product) {
  const count = product.review_count || product.reviewCount || 0;
  if (count >= 500) return 5;
  if (count >= 200) return 4;
  if (count >= 100) return 3;
  if (count >= 50) return 2;
  if (count >= 10) return 1;
  return 0;
}

/**
 * D6: Stock score (0–5).
 */
function scoreStock(product) {
  const status = product.stock_status || product.stockStatus;
  if (status === "in_stock") return 5;
  if (status === "low_stock") return 3;
  return 0;
}

/**
 * D7: Promotion score (0–5).
 * Uses promotedProductIds Set passed from retrieval stage.
 */
function scorePromotion(product, promotedProductIds = new Set()) {
  const pid = (product.product_id || product._id || "").toString();
  if (promotedProductIds.has(pid)) return 5;

  // Fallback: check compareAtPrice for implied sale
  if (product.compare_at_price && product.compare_at_price > product.price) return 3;
  if (product.compareAtPrice && product.compareAtPrice > product.price) return 3;

  return 0;
}

// ─────────────────────────────────────────────────────────────────────────────
// Explanation builder
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build the 3 explanation fields required by the response contract.
 *
 * @param {object} product
 * @param {object} context — ShoppingContext
 * @param {object} breakdown — score breakdown
 * @param {string[]} needReasons — from scoreNeedMatch
 * @returns {{ matchedReason, suggestedUse, whyRecommended }}
 */
function buildExplanations(product, context, breakdown, needReasons) {
  const parts = [];

  // Category
  if (product.category_name) parts.push(product.category_name);

  // Brand
  if (product.brand_name || product.brandName) {
    parts.push(`${product.brand_name || product.brandName}`);
  }

  // Budget
  if (breakdown.budget >= 15 && context.budget?.max) {
    const price = (product.price || 0).toLocaleString("vi-VN");
    parts.push(`giá ${price}đ trong ngân sách`);
  }

  // Need reasons
  parts.push(...needReasons.slice(0, 2));

  // Rating
  const rating = product.rating || product.averageRating;
  if (rating >= 4.5) parts.push(`đánh giá ${rating.toFixed(1)}★`);

  const matchedReason = parts.length > 0
    ? parts.join(", ") + "."
    : "Sản phẩm phù hợp với nhu cầu của bạn.";

  // suggestedUse — based on occasion/skinType
  let suggestedUse = "Thoa đều lên da đã dưỡng ẩm.";
  if (context.occasion === "school" || context.occasion === "daily") {
    suggestedUse = "Dùng 1 lớp mỏng cho look tự nhiên nhẹ nhàng. Tái tạo nếu cần vào buổi trưa.";
  } else if (context.occasion === "party" || context.occasion === "wedding") {
    suggestedUse = "Phủ đều 1–2 lớp cho độ che phủ cao, bền suốt cả buổi tiệc.";
  } else if (context.occasion === "office") {
    suggestedUse = "Thoa nhẹ 1 lớp cho look gọn gàng chuyên nghiệp suốt ngày làm việc.";
  } else if (context.skinType === "oily") {
    suggestedUse = "Kết hợp cùng bột phủ để tăng độ bền và kiểm soát dầu tốt hơn.";
  }

  // whyRecommended — personalized reason
  const whyParts = [];
  if (context.skinType) whyParts.push(`phù hợp ${SKIN_TYPE_LABEL[context.skinType] || context.skinType}`);
  if (context.budget?.max) {
    whyParts.push(`trong ngân sách ${context.budget.max.toLocaleString("vi-VN")}đ`);
  }
  if (context.occasion) whyParts.push(`cho ${OCCASION_LABEL[context.occasion] || context.occasion}`);
  if (context.finishPreference?.length) {
    whyParts.push(`finish ${FINISH_LABEL[context.finishPreference[0]] || context.finishPreference[0]}`);
  }
  if (needReasons.length) whyParts.push(...needReasons.slice(0, 1));

  const whyRecommended = whyParts.length > 0
    ? `Phù hợp với bạn vì: ${whyParts.join(", ")}.`
    : "Sản phẩm phù hợp với nhu cầu makeup của bạn.";

  return { matchedReason, suggestedUse, whyRecommended };
}

// ─────────────────────────────────────────────────────────────────────────────
// Quality gate
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Validate a product passes the quality gate before being included in results.
 *
 * @param {object} product
 * @param {object} context — ShoppingContext
 * @returns {boolean}
 */
function passesQualityGate(product, context) {
  // Must have an image
  const image = product.image_url || product.imageUrl || product.image || "";
  if (!image || image.trim() === "") return false;

  // Must be active (already filtered in DB query but double-check)
  if (product.isActive === false) return false;
  if (product.productStatus && product.productStatus !== "active") return false;

  // Out-of-stock filter (unless user explicitly wants it)
  const stockStatus = product.stock_status || product.stockStatus;
  if (!context.wantsOutOfStock && stockStatus === "out_of_stock") return false;

  // Must have a product ID
  if (!product.product_id && !product._id) return false;

  return true;
}

// ─────────────────────────────────────────────────────────────────────────────
// New response card format
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Map a scored product to the Phase 9 product card response format.
 * Preserves old keys as aliases for backward compatibility (Phase 1–8 clients).
 *
 * @param {object} product — scored product
 * @param {string} makeupStyle — from context
 * @returns {object}
 */
function toProductCard(product, makeupStyle) {
  const pid = (product.product_id || product._id || "").toString();
  const price = product.price || 0;
  const compareAt = product.compare_at_price || product.compareAtPrice;
  const hasSale = compareAt && compareAt > price;

  const shades = (product.shades || []).slice(0, 5).map((s) => s.name || s);

  // Determine available actions
  const actions = ["VIEW_DETAIL"];
  const stockStatus = product.stock_status || product.stockStatus;
  if (stockStatus !== "out_of_stock") actions.push("ADD_CART");
  if (shades.length > 0) actions.push("SELECT_SHADE");

  return {
    // ── New Phase 9 format ────────────────────────────────────────────────
    id: pid,
    name: product.name || product.productName,
    brand: product.brand_name || product.brandName || "",
    category: product.category_name || product.categoryName || "",
    image: product.image_url || product.imageUrl || product.image || "",

    pricing: {
      original: price,
      sale: hasSale ? price : null,
      compareAt: hasSale ? compareAt : null,
    },

    recommendation: {
      score: product._score || 0,
      reason: product.matchedReason || product.reason || "",
      usage: product.suggestedUse || "",
      makeupStyle: makeupStyle || null,
      whyRecommended: product.whyRecommended || "",
      scoreBreakdown: product._scoreBreakdown || {},
    },

    variant: {
      shade: shades.length > 0 ? shades[0] : null,
      availableShades: shades,
      finish: product.finish_type || null,
    },

    actions,
    stockStatus: stockStatus || "unknown",

    // ── Backward-compat aliases (Phase 1–8 Android clients) ───────────────
    product_id: pid,
    slug: product.slug || "",
    product_name: product.name || product.productName,
    brand_name: product.brand_name || product.brandName || "",
    category_name: product.category_name || product.categoryName || "",
    image_url: product.image_url || product.imageUrl || product.image || "",
    price,
    compare_at_price: compareAt || null,
    rating: product.rating || product.averageRating || null,
    review_count: product.review_count || product.reviewCount || 0,
    stock_status: stockStatus || "unknown",
    reason: product.matchedReason || product.reason || "",
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main: score + rank + quality-gate + format
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Score a list of product candidates against a ShoppingContext, apply quality
 * gate, and return the top N as formatted product cards.
 *
 * @param {object[]} candidates — raw/normalized products from DB retrieval
 * @param {object}   context    — ShoppingContext from extractShoppingContext()
 * @param {Set<string>} [promotedProductIds=new Set()] — product IDs with active promotions
 * @param {number}   [limit=5]
 * @returns {object[]} — sorted, quality-gated product cards
 */
function scoreAndRankProducts(candidates, context, promotedProductIds = new Set(), limit = 5) {
  if (!candidates || candidates.length === 0) return [];

  // 1. Score all candidates
  const scored = [];
  const seenIds = new Set();

  for (const product of candidates) {
    const pid = (product.product_id || product._id || "").toString();

    // Deduplicate
    if (seenIds.has(pid)) continue;
    seenIds.add(pid);

    // Quality gate
    if (!passesQualityGate(product, context)) continue;

    // Score each dimension
    const categoryScore = scoreCategoryMatch(product, context);
    const { score: needScore, reasons: needReasons } = scoreNeedMatch(product, context);
    const budgetScore = scoreBudgetMatch(product, context);
    const ratingScore = scoreRating(product);
    const reviewScore = scoreReviewCount(product);
    const stockScore = scoreStock(product);
    const promoScore = scorePromotion(product, promotedProductIds);

    let totalScore =
      categoryScore + needScore + budgetScore + ratingScore + reviewScore + stockScore + promoScore;

    if (context.isEventMakeup) {
      totalScore =
        (categoryScore * (40 / 30)) +
        (needScore * (35 / 25)) +
        (budgetScore * (15 / 20)) +
        (ratingScore * (5 / 10)) +
        (reviewScore * (2 / 5)) +
        (stockScore * (2 / 5)) +
        (promoScore * (1 / 5));
    }

    const breakdown = {
      category: categoryScore,
      need: needScore,
      budget: budgetScore,
      rating: ratingScore,
      review: reviewScore,
      stock: stockScore,
      promotion: promoScore,
    };

    // Build explanations
    const { matchedReason, suggestedUse, whyRecommended } = buildExplanations(
      product, context, breakdown, needReasons
    );

    scored.push({
      ...product,
      _score: totalScore,
      _scoreBreakdown: breakdown,
      matchedReason,
      suggestedUse,
      whyRecommended,
    });
  }

  // 2. Sort by score DESC, then by rating DESC
  scored.sort((a, b) => {
    if (b._score !== a._score) return b._score - a._score;
    const ra = a.rating || a.averageRating || 0;
    const rb = b.rating || b.averageRating || 0;
    return rb - ra;
  });

  // 3. Take top N and format as product cards
  return scored
    .slice(0, limit)
    .map((p) => toProductCard(p, context.makeupStyle));
}

module.exports = {
  scoreAndRankProducts,
  passesQualityGate,
  toProductCard,
  // Exported for unit testing individual dimensions
  _scorers: {
    scoreCategoryMatch,
    scoreNeedMatch,
    scoreBudgetMatch,
    scoreRating,
    scoreReviewCount,
    scoreStock,
    scorePromotion,
  },
  WEIGHTS,
};
