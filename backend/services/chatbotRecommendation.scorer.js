/**
 * chatbotRecommendation.scorer.js
 * Pure scoring function for personalized product ranking.
 * No database calls — receives products and customer context as arguments.
 *
 * Score components (total max: 100):
 *   Category match:               +30
 *   Skin type match:              +25
 *   Skin concern match:           +20
 *   Budget match:                 +15
 *   Brand preference:             +5
 *   Sensitive-friendly bonus:     +5 (when customer is sensitive)
 */

// ─────────────────────────────────────────────────────────────────────────────
// Score a single product against customer context
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Compute a personalization score for one product.
 *
 * @param {object} product — normalized product from chatbotProduct.tool.js
 * @param {object} customerProfile — from getCustomerContext().customer_profile
 * @param {object} [constraints] — from parseProductConstraints() for the current message
 * @returns {{ score: number, reasons: string[], scoreBreakdown: object }}
 */
function scoreProduct(product, customerProfile, constraints = {}) {
  let score = 0;
  const reasons = [];
  const breakdown = {};

  // ── Category match ────────────────────────────────────────────────────────
  // Check if product category_name matches customer's preferred_categories
  const preferredCats = (customerProfile.preferred_categories || []).map((c) =>
    c.toLowerCase()
  );
  const productCat = (product.category_name || "").toLowerCase();

  if (
    preferredCats.length &&
    preferredCats.some((c) => productCat.includes(c) || c.includes(productCat))
  ) {
    score += 30;
    breakdown.category_match = 30;
    reasons.push("Thuộc nhóm sản phẩm bạn quan tâm");
  } else if (constraints.categoryIntent) {
    // If the user asked for a specific category this turn and product matches
    const catQuery = constraints.categoryIntent.value || "";
    if (productCat.includes(catQuery) || catQuery.includes(productCat)) {
      score += 20; // Partial: matches current query but not saved preference
      breakdown.category_match = 20;
    }
  }

  // ── Skin type match ───────────────────────────────────────────────────────
  const skinType = customerProfile.skin_type || constraints.skinType;
  if (skinType && product.skin_types_supported && product.skin_types_supported.includes(skinType)) {
    score += 25;
    breakdown.skin_type_match = 25;
    const labels = {
      oily: "da dầu", dry: "da khô", sensitive: "da nhạy cảm",
      combination: "da hỗn hợp", normal: "da thường",
    };
    reasons.push(`Phù hợp ${labels[skinType] || skinType}`);
  }

  // ── Skin concern match ────────────────────────────────────────────────────
  const concerns = [
    ...(customerProfile.skin_concerns || []),
    ...(constraints.skinConcern ? [constraints.skinConcern] : []),
  ];
  const uniqueConcerns = [...new Set(concerns)];

  if (uniqueConcerns.length && product.concerns_targeted) {
    const matched = uniqueConcerns.filter((c) => product.concerns_targeted.includes(c));
    if (matched.length) {
      const concernScore = Math.min(20, matched.length * 10);
      score += concernScore;
      breakdown.concern_match = concernScore;
      const concernLabels = {
        acne: "kiểm soát mụn", dark_spot: "giảm thâm", dullness: "cải thiện xỉn màu",
        dryness: "cấp ẩm sâu", oil_control: "kiểm soát dầu nhờn",
        anti_aging: "chống lão hóa", pore: "thu nhỏ lỗ chân lông",
      };
      reasons.push(...matched.slice(0, 2).map((c) => concernLabels[c] || c));
    }
  }

  // ── Budget match ──────────────────────────────────────────────────────────
  const budgetMax = customerProfile.budget_max || constraints.budgetMax;
  if (budgetMax && product.price != null) {
    if (product.price <= budgetMax) {
      score += 15;
      breakdown.budget_match = 15;
      reasons.push("Trong ngân sách của bạn");
    }
  } else if (!budgetMax && !constraints.budgetMax) {
    // No budget info — give partial credit (neutral)
    score += 7;
    breakdown.budget_match = 7;
  }

  // ── Brand preference ──────────────────────────────────────────────────────
  const preferredBrands = (customerProfile.preferred_brands || []).map((b) =>
    b.toLowerCase()
  );
  const productBrand = (product.brand_name || "").toLowerCase();
  if (preferredBrands.length && preferredBrands.some((b) => productBrand.includes(b))) {
    score += 5;
    breakdown.brand_preference = 5;
    reasons.push("Thương hiệu bạn yêu thích");
  }

  // ── Sensitive-friendly bonus ──────────────────────────────────────────────
  if (
    (skinType === "sensitive" || customerProfile.skin_concerns?.includes("sensitive")) &&
    product.is_sensitive_friendly
  ) {
    score += 5;
    breakdown.sensitive_friendly = 5;
    reasons.push("An toàn cho da nhạy cảm");
  }

  // Ensure reasons is non-empty
  if (!reasons.length) {
    reasons.push("Sản phẩm phù hợp với nhu cầu của bạn");
  }

  return { score, reasons, scoreBreakdown: breakdown };
}

// ─────────────────────────────────────────────────────────────────────────────
// Rank a list of products
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Score and re-rank a list of products based on customer context.
 * Replaces the simple Phase 2 reason string with structured reasons[].
 *
 * @param {object[]} products       — normalized products from chatbotProduct.tool.js
 * @param {object} customerProfile  — from getCustomerContext().customer_profile
 * @param {object} [constraints]    — from parseProductConstraints()
 * @param {number} [limit=5]
 * @returns {object[]}              — products with updated reason, personalization_score, score_reasons
 */
function rankProducts(products, customerProfile, constraints = {}, limit = 5) {
  if (!products || !products.length) return [];

  const scored = products.map((p) => {
    const { score, reasons, scoreBreakdown } = scoreProduct(p, customerProfile, constraints);
    return {
      ...p,
      reason: reasons[0] || p.reason, // primary reason (for Phase 2 compat)
      personalization_score: score,
      score_reasons: reasons,
      score_breakdown: scoreBreakdown,
    };
  });

  // Sort by personalization_score DESC, then by existing rating DESC
  scored.sort((a, b) => {
    if (b.personalization_score !== a.personalization_score) {
      return b.personalization_score - a.personalization_score;
    }
    return (b.rating || 0) - (a.rating || 0);
  });

  return scored.slice(0, limit);
}

module.exports = { scoreProduct, rankProducts };
