/**
 * chatbotCustomerContext.service.js
 * Builds a unified, read-only customer context object for the Kanila AI Assistant.
 *
 * Data sources (in priority order):
 *   1. CustomerBeautyProfile — primary skin/beauty profile (most complete)
 *   2. CustomerPreference    — key-value fallback / chatbot-saved preferences
 *   3. Order + OrderItem     — recent purchase history for brand/category signals
 *
 * SECURITY: this service returns only data needed by the AI.
 * Never expose: customer PII beyond first name, payment details, internal IDs.
 */

const mongoose = require("mongoose");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerPreference = require("../models/customerPreference.model");
const Customer = require("../models/customer.model");
const Order = require("../models/order.model");
const OrderItem = require("../models/orderItem.model");

// ─────────────────────────────────────────────────────────────────────────────
// Budget range parser
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parse CustomerBeautyProfile.budget_range string (e.g. "200000-500000", "under_300000", "unknown")
 * into numeric { min, max }.
 * @param {string} budgetRange
 * @returns {{ min: number|null, max: number|null }}
 */
function parseBudgetRange(budgetRange) {
  if (!budgetRange || budgetRange === "unknown") return { min: null, max: null };

  // Format: "200000-500000"
  const rangeMatch = budgetRange.match(/^(\d+)-(\d+)$/);
  if (rangeMatch) return { min: parseInt(rangeMatch[1]), max: parseInt(rangeMatch[2]) };

  // Format: "under_300000" or "below_300000"
  const underMatch = budgetRange.match(/(?:under|below|duoi)_?(\d+)/i);
  if (underMatch) return { min: null, max: parseInt(underMatch[1]) };

  // Format: "above_200000" or "over_200000"
  const overMatch = budgetRange.match(/(?:above|over|tren)_?(\d+)/i);
  if (overMatch) return { min: parseInt(overMatch[1]), max: null };

  // Single number
  const single = budgetRange.match(/^(\d+)$/);
  if (single) return { min: null, max: parseInt(single[1]) };

  return { min: null, max: null };
}

// ─────────────────────────────────────────────────────────────────────────────
// Preference confidence
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Estimate how confident we are in the customer profile.
 * "high"   — skin_type known + ≥1 concern + budget known
 * "medium" — skin_type known OR ≥1 concern
 * "low"    — nothing meaningful is known
 */
function calcPreferenceConfidence(profile) {
  const hasSkinType = profile.skin_type && profile.skin_type !== "unknown";
  const hasConcern  = Array.isArray(profile.skin_concerns) && profile.skin_concerns.length > 0;
  const hasBudget   = profile.budget_min != null || profile.budget_max != null;

  if (hasSkinType && (hasConcern || hasBudget)) return "high";
  if (hasSkinType || hasConcern) return "medium";
  return "low";
}

// ─────────────────────────────────────────────────────────────────────────────
// Purchase history
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Load the customer's last N completed orders and extract brand/category signals.
 * Only uses order_status: completed or fulfillment_status: delivered.
 * @param {ObjectId} customerId
 * @param {number} limit
 */
async function loadPurchaseHistory(customerId, limit = 5) {
  try {
    const recentOrders = await Order.find({
      customer_id: customerId,
      $or: [{ order_status: "completed" }, { fulfillment_status: "delivered" }],
    })
      .sort({ placed_at: -1 })
      .limit(limit)
      .select("_id order_number placed_at")
      .lean();

    if (!recentOrders.length) return [];

    const orderIds = recentOrders.map((o) => o._id);
    const items = await OrderItem.find({ order_id: { $in: orderIds } })
      .select("product_name_snapshot quantity")
      .lean();

    return items.slice(0, 10).map((i) => ({
      product_name: i.product_name_snapshot,
      quantity: i.quantity,
    }));
  } catch (_) {
    return [];
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Key-value preference fallback reader
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Read CustomerPreference rows and merge into a partial profile object.
 * Used as fallback when CustomerBeautyProfile is missing or incomplete.
 * @param {ObjectId} customerId
 * @returns {Promise<object>}
 */
async function readKeyValuePreferences(customerId) {
  const rows = await CustomerPreference.find({ customer_id: customerId }).lean();
  const kv = {};
  for (const row of rows) {
    kv[row.preference_key] = row.preference_value;
  }

  return {
    skin_type: kv["skin_type"] || null,
    skin_concerns: kv["skin_concerns"] ? kv["skin_concerns"].split(",").filter(Boolean) : [],
    budget_min: kv["budget_min"] ? parseInt(kv["budget_min"]) : null,
    budget_max: kv["budget_max"] ? parseInt(kv["budget_max"]) : null,
    preferred_categories: kv["preferred_categories"]
      ? kv["preferred_categories"].split(",").filter(Boolean)
      : [],
    preferred_brands: kv["preferred_brands"]
      ? kv["preferred_brands"].split(",").filter(Boolean)
      : [],
    avoid_ingredients: kv["avoid_ingredients"]
      ? kv["avoid_ingredients"].split(",").filter(Boolean)
      : [],
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main context builder
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Build unified customer context for the AI assistant.
 *
 * @param {ObjectId|string} customerId
 * @returns {Promise<{
 *   customer_profile: {
 *     skin_type: string|null,
 *     skin_concerns: string[],
 *     preferred_categories: string[],
 *     preferred_brands: string[],
 *     budget_min: number|null,
 *     budget_max: number|null,
 *     avoid_ingredients: string[],
 *     profile_completion_rate: number
 *   },
 *   purchase_history: object[],
 *   preference_confidence: "high"|"medium"|"low",
 *   missing_info: string[]
 * }>}
 */
async function getCustomerContext(customerId) {
  if (!customerId) {
    return _emptyContext();
  }

  let beautyProfile = null;
  try {
    beautyProfile = await CustomerBeautyProfile.findOne({ customer_id: customerId })
      .populate("preferred_brands", "brandName")
      .populate("preferred_categories", "categoryName")
      .lean();
  } catch (_) {
    // Non-fatal
  }

  // Key-value fallback (merges chatbot-saved preferences)
  let kvPrefs = {};
  try {
    kvPrefs = await readKeyValuePreferences(customerId);
  } catch (_) {}

  // Build merged profile — beauty profile takes priority over key-value
  let skin_type = null;
  let skin_concerns = [];
  let budget_min = null;
  let budget_max = null;
  let preferred_categories = [];
  let preferred_brands = [];
  let avoid_ingredients = [];
  let profile_completion_rate = 0;

  if (beautyProfile) {
    skin_type = beautyProfile.skin_type !== "unknown" ? beautyProfile.skin_type : null;
    skin_concerns = beautyProfile.skin_concerns || [];
    const parsed = parseBudgetRange(beautyProfile.budget_range);
    budget_min = parsed.min;
    budget_max = parsed.max;
    preferred_categories = (beautyProfile.preferred_categories || [])
      .map((c) => (typeof c === "object" ? c.categoryName : c))
      .filter(Boolean);
    preferred_brands = (beautyProfile.preferred_brands || [])
      .map((b) => (typeof b === "object" ? b.brandName : b))
      .filter(Boolean);
    avoid_ingredients = beautyProfile.avoid_ingredients || [];
    profile_completion_rate = beautyProfile.profile_completion_rate || 0;
  }

  // Merge missing info from key-value preferences (chatbot answers)
  if (!skin_type && kvPrefs.skin_type) skin_type = kvPrefs.skin_type;
  if (!skin_concerns.length && kvPrefs.skin_concerns.length) skin_concerns = kvPrefs.skin_concerns;
  if (budget_min == null && kvPrefs.budget_min != null) budget_min = kvPrefs.budget_min;
  if (budget_max == null && kvPrefs.budget_max != null) budget_max = kvPrefs.budget_max;
  if (!preferred_categories.length && kvPrefs.preferred_categories.length)
    preferred_categories = kvPrefs.preferred_categories;
  if (!preferred_brands.length && kvPrefs.preferred_brands.length)
    preferred_brands = kvPrefs.preferred_brands;
  if (!avoid_ingredients.length && kvPrefs.avoid_ingredients.length)
    avoid_ingredients = kvPrefs.avoid_ingredients;

  // Purchase history (for brand/category signals)
  const purchase_history = await loadPurchaseHistory(customerId);

  // Missing info checklist (for progressive questioning)
  const missing_info = [];
  if (!skin_type) missing_info.push("skin_type");
  if (!skin_concerns.length) missing_info.push("skin_concerns");
  if (budget_min == null && budget_max == null) missing_info.push("budget_range");
  if (!preferred_categories.length) missing_info.push("preferred_category");

  const customer_profile = {
    skin_type,
    skin_concerns,
    preferred_categories,
    preferred_brands,
    budget_min,
    budget_max,
    avoid_ingredients,
    profile_completion_rate,
  };

  return {
    customer_profile,
    purchase_history,
    preference_confidence: calcPreferenceConfidence(customer_profile),
    missing_info,
  };
}

/**
 * Empty context returned for guests or when context fails.
 */
function _emptyContext() {
  return {
    customer_profile: {
      skin_type: null,
      skin_concerns: [],
      preferred_categories: [],
      preferred_brands: [],
      budget_min: null,
      budget_max: null,
      avoid_ingredients: [],
      profile_completion_rate: 0,
    },
    purchase_history: [],
    preference_confidence: "low",
    missing_info: ["skin_type", "skin_concerns", "budget_range", "preferred_category"],
  };
}

module.exports = { getCustomerContext };
