/**
 * chatbotRecommendation.service.js
 * Phase 5 – Kanila AI Shopping Assistant
 *
 * Bridges recommendation.service.js (full profile-based scoring engine) with
 * the chatbot layer.  Provides:
 *
 *   1. getRecommendationContext()  – profile-aware product list for Gemini context.
 *   2. buildComboRecommendation()  – slot-grouped combo (cleanser/serum/moisturizer…).
 *
 * IMPORTANT:
 * - Does NOT replace chatbotProduct.tool.js (keyword search, Phase 2A).
 * - Does NOT replace chatbotCart.tool.js    (cart operations,  Phase 5A).
 * - Reuses recommendation.service.js without duplicating its scoring logic.
 * - Gemini receives product list as context; Gemini NEVER selects or prices products.
 */

"use strict";

const { recommendForProfile, getBehaviorSignals } = require("./recommendation.service");
const { getCustomerContext }                       = require("./chatbotCustomerContext.service");

// ─────────────────────────────────────────────────────────────────────────────
// Slot / combo definitions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Available combo slot configurations.
 * Key = comboType string; value = ordered list of product slots.
 */
const COMBO_SLOTS = {
  skincare_basic: ["cleanser", "serum", "moisturizer"],
  skincare_full:  ["cleanser", "toner", "serum", "moisturizer", "sunscreen"],
  makeup:         ["foundation", "concealer", "blush", "setting_spray"],
};

/**
 * Vietnamese + English keywords used to detect a product slot from its category
 * name or product name (case-insensitive substring match).
 */
const SLOT_KEYWORDS = {
  cleanser:      ["cleanser", "sữa rửa mặt", "gel rửa mặt", "foam rửa mặt", "làm sạch mặt"],
  toner:         ["toner", "nước cân bằng", "nước hoa hồng", "essence toner"],
  serum:         ["serum", "tinh chất"],
  moisturizer:   ["moisturizer", "kem dưỡng ẩm", "dưỡng ẩm", "kem dưỡng", "lotion dưỡng"],
  sunscreen:     ["sunscreen", "chống nắng", "spf", "kem chống nắng", "kem nắng"],
  essence:       ["essence", "nước dưỡng bản chất"],
  eyecream:      ["eye cream", "kem mắt", "dưỡng mắt"],
  mask:          ["mặt nạ", "mask", "sheet mask"],
  foundation:    ["foundation", "kem nền", "nền trang điểm"],
  concealer:     ["concealer", "che khuyết điểm"],
  blush:         ["blush", "má hồng", "phấn hồng"],
  setting_spray: ["setting spray", "xịt khóa", "xịt cố định"],
};

// ─────────────────────────────────────────────────────────────────────────────
// Internal helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Detect which slot a product belongs to by matching its category + name text.
 * @param {object} product – raw or formatted product object
 * @returns {string} slot name or "other"
 */
function detectProductSlot(product) {
  const haystack = [
    product.categoryId?.categoryName || product.category || "",
    product.productName || product.name || "",
  ]
    .join(" ")
    .toLowerCase();

  for (const [slot, keywords] of Object.entries(SLOT_KEYWORDS)) {
    if (keywords.some((kw) => haystack.includes(kw))) return slot;
  }
  return "other";
}

/**
 * Convert chatbotCustomerContext.service.js output to the profile shape that
 * recommendation.service.js expects.
 *
 * @param {object} customerContext – result of getCustomerContext()
 * @returns {object} profile shape for recommendForProfile()
 */
function customerContextToProfile(customerContext) {
  const cp = customerContext.customer_profile;
  return {
    is_new_profile: true,
    skin_types: cp.skin_type && cp.skin_type !== "unknown" ? [cp.skin_type] : [],
    skin_tone: "",
    concerns: cp.skin_concerns || [],
    sensitivity_level: null,
    beauty_goals: [],
    preferred_ingredients: [],
    avoid_ingredients: cp.avoid_ingredients || [],
    preferred_brands: (cp.preferred_brands || []).map(String),
    disliked_brands: [],
    texture_preference: [],
    finish_preference: [],
    budget_range: cp.budget_max ? `under_${cp.budget_max}` : null,
    favorite_brands: [],
  };
}

/**
 * Format a raw recommendForProfile() item into a clean chatbot product object.
 * @param {object} item – item from recommendForProfile()
 * @returns {object}
 */
function formatRecommendationItem(item) {
  return {
    product_id: item.productId || String(item.product?._id || ""),
    name:       item.product?.productName || item.product?.name || "",
    brand:      item.product?.brandName   || item.product?.brand || "",
    category:   item.product?.categoryId?.categoryName || "",
    price:      Number(item.product?.price || 0),
    image:      item.product?.imageUrl    || item.product?.image || "",
    rating:     Number(item.product?.averageRating || 0),
    slug:       item.product?.slug        || "",
    reason:     Array.isArray(item.reasons) && item.reasons[0]
                  ? item.reasons[0]
                  : "Phù hợp với nhu cầu của bạn",
    reasons:    item.reasons     || [],
    badges:     item.badges      || [],
    score:      item.score       || 0,
    matched_attributes: item.matched_attributes || [],
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Public: getRecommendationContext
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Get a profile-scored product list for the AI chatbot context.
 *
 * Flow:
 *   1. Load customer context (authenticated only).
 *   2. Convert to recommendation profile.
 *   3. Run recommendForProfile() — full scoring engine with ProductBeautyProfile.
 *   4. Apply optional budget + category filters from parsed message constraints.
 *   5. Return formatted products.
 *
 * Falls back gracefully: if context fails or profile is empty, runs a generic
 * recommendForProfile() so guest users still get results.
 *
 * @param {string|ObjectId|null} customerId
 * @param {object} constraints – parsed from message:
 *   { skinType, concern, category, budgetMax }
 * @param {number} limit – max products to return (default 6)
 * @returns {Promise<{ products: object[], customer_context_used: boolean }>}
 */
async function getRecommendationContext(customerId, constraints = {}, limit = 6) {
  let customerContext = null;
  let profile = _buildGuestProfile(constraints);
  let behavior = _emptyBehavior();
  let customer_context_used = false;

  // ── Authenticated: load customer profile ────────────────────────────────
  if (customerId) {
    try {
      customerContext = await getCustomerContext(customerId);
      if (customerContext && customerContext.preference_confidence !== "low") {
        profile = customerContextToProfile(customerContext);
        // Override with message-extracted values if they provide more detail
        if (constraints.skinType && !profile.skin_types.length) {
          profile.skin_types = [constraints.skinType];
        }
        if (constraints.budgetMax && !profile.budget_range) {
          profile.budget_range = `under_${constraints.budgetMax}`;
        }
        customer_context_used = true;
      } else if (customerContext && (constraints.skinType || constraints.concern)) {
        // Low confidence profile — supplement with message constraints
        profile = customerContextToProfile(customerContext);
        if (constraints.skinType) profile.skin_types = [constraints.skinType];
        if (constraints.concern)  profile.concerns   = [constraints.concern];
      }
      behavior = await getBehaviorSignals(customerId).catch(() => _emptyBehavior());
    } catch (_) {
      // Non-fatal: fall back to guest profile built from constraints
    }
  }

  // ── Run full scoring engine ─────────────────────────────────────────────
  let rawItems = [];
  try {
    rawItems = await recommendForProfile(profile, {
      category: constraints.category || "",
      limit:    limit * 3, // over-fetch to allow budget filtering
      behavior,
    });
  } catch (err) {
    console.error("[chatbotRecommendation] recommendForProfile error:", err.message);
    return { products: [], customer_context_used: false };
  }

  // ── Budget filter ────────────────────────────────────────────────────────
  const budgetMax =
    constraints.budgetMax ||
    (customerContext?.customer_profile?.budget_max) ||
    null;

  const filtered = budgetMax
    ? rawItems.filter((item) => Number(item.product?.price || 0) <= budgetMax)
    : rawItems;

  // ── Format and return ────────────────────────────────────────────────────
  const products = filtered.slice(0, limit).map(formatRecommendationItem);
  return { products, customer_context_used };
}

// ─────────────────────────────────────────────────────────────────────────────
// Public: buildComboRecommendation
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build a skincare / makeup combo grouped by product slot.
 *
 * Algorithm:
 *   1. Fetch recommendation pool via getRecommendationContext() (profile-scored).
 *   2. Classify each product into a slot (cleanser / serum / moisturizer…).
 *   3. For each required slot, pick the top-scoring product that fits budget.
 *   4. Compute total price.
 *
 * Gemini NEVER selects or prices the combo — that is done deterministically here.
 *
 * @param {string|ObjectId|null} customerId
 * @param {object} constraints – { comboType, budgetMax, skinType, concern, category }
 * @returns {Promise<{
 *   combo:                object[],  // products with slot annotation
 *   total:                number,
 *   comboType:            string,
 *   customer_context_used: boolean,
 * }>}
 */
async function buildComboRecommendation(customerId, constraints = {}) {
  const comboType = constraints.comboType || "skincare_basic";
  const slots     = COMBO_SLOTS[comboType] || COMBO_SLOTS.skincare_basic;
  const budgetMax = constraints.budgetMax || null;

  // Fetch a larger pool (no budget filter yet — we'll distribute budget slot by slot)
  const { products, customer_context_used } = await getRecommendationContext(
    customerId,
    { ...constraints, budgetMax: null }, // no global filter; slot-level below
    40
  );

  if (!products.length) {
    return { combo: [], total: 0, comboType, customer_context_used };
  }

  // Group products by slot
  const bySlot = {};
  for (const slot of slots) bySlot[slot] = [];

  for (const product of products) {
    const slot = detectProductSlot({ categoryId: { categoryName: product.category }, productName: product.name });
    if (bySlot[slot]) bySlot[slot].push(product);
  }

  // Pick the best (highest-score, already sorted) affordable product per slot
  const combo = [];
  let total    = 0;
  let remaining = budgetMax;

  for (const slot of slots) {
    const candidates = bySlot[slot] || [];
    for (const candidate of candidates) {
      if (remaining !== null && candidate.price > remaining) continue;
      combo.push({ ...candidate, slot });
      total     += candidate.price;
      if (remaining !== null) remaining -= candidate.price;
      break; // one product per slot
    }
  }

  return { combo, total, comboType, customer_context_used };
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helpers
// ─────────────────────────────────────────────────────────────────────────────

/** Build a minimal recommendation profile from message constraints (guest path). */
function _buildGuestProfile(constraints) {
  return {
    is_new_profile: false,
    skin_types:         constraints.skinType ? [constraints.skinType] : [],
    skin_tone:          "",
    concerns:           constraints.concern  ? [constraints.concern]  : [],
    sensitivity_level:  null,
    beauty_goals:       [],
    preferred_ingredients: [],
    avoid_ingredients:  [],
    preferred_brands:   [],
    disliked_brands:    [],
    texture_preference: [],
    finish_preference:  [],
    budget_range:       constraints.budgetMax ? `under_${constraints.budgetMax}` : null,
    favorite_brands:    [],
  };
}

/** Empty behavior signals for guest or when loading fails. */
function _emptyBehavior() {
  return {
    orderedProductIds:  new Set(),
    wishlistProductIds: new Set(),
    wishlistBrandIds:   new Set(),
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Exports
// ─────────────────────────────────────────────────────────────────────────────

module.exports = {
  getRecommendationContext,
  buildComboRecommendation,
  detectProductSlot,
  COMBO_SLOTS,
};
