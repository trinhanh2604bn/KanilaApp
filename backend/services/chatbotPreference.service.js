/**
 * chatbotPreference.service.js
 * Extracts customer preferences from conversation text and saves them.
 *
 * STORAGE STRATEGY (reuses existing models — no new model created):
 *   PRIMARY:  CustomerBeautyProfile (source: "chatbot") — full structured profile
 *   MIRROR:   CustomerPreference key-value rows — fast lookup fallback
 *             key-value rows use preference_key + preference_value (String)
 *
 * SAFETY RULES:
 * - Only save CONFIRMED, explicit information from the user message.
 * - Do NOT infer or guess sensitive attributes.
 * - Never save payment info, private health data, or internal IDs.
 * - All writes are upserts — never destructive to existing data.
 */

const mongoose = require("mongoose");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerPreference = require("../models/customerPreference.model");
const crypto = require("crypto");

// ─────────────────────────────────────────────────────────────────────────────
// Preference extraction from Vietnamese user messages
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_TYPE_MAP = [
  { keywords: ["da dầu", "da nhờn", "oily", "dầu và", "dầu mụn", "mình dầu", "da của mình dầu"], value: "oily" },
  { keywords: ["da khô", "dry skin"], value: "dry" },
  { keywords: ["da nhạy cảm", "da nhạy", "sensitive"], value: "sensitive" },
  { keywords: ["da hỗn hợp", "combination"], value: "combination" },
  { keywords: ["da thường", "da bình thường", "normal skin"], value: "normal" },
];

const SKIN_CONCERN_MAP = [
  { keywords: ["mụn", "acne", "nổi mụn", "dễ bị mụn"], value: "acne" },
  { keywords: ["thâm", "dark spot", "nám", "tàn nhang"], value: "dark_spot" },
  { keywords: ["xỉn màu", "xỉn", "dullness", "sạm"], value: "dullness" },
  { keywords: ["thiếu ẩm", "khô ráp", "dryness", "mất ẩm"], value: "dryness" },
  { keywords: ["kiểm soát dầu", "kiểm soát nhờn", "oil_control"], value: "oil_control" },
  { keywords: ["lão hóa", "nếp nhăn", "anti aging"], value: "anti_aging" },
  { keywords: ["lỗ chân lông", "pore"], value: "pore" },
];

const BUDGET_PATTERNS = [
  // "khoảng 500 nghìn", "500 nghìn", "500 ngàn"
  { re: /(\d+[\.,]?\d*)\s*(?:nghìn|ngàn)(?:đ|d|đồng)?/i, multiplier: 1000 },
  // "dưới 300k", "khoảng 300k"
  { re: /(\d+[\.,]?\d*)\s*k(?:đ|d)?/i,     multiplier: 1000 },
  // "300.000" or "300,000"
  { re: /(\d{2,3}[\.,]\d{3})\s*(?:đ|vnd)?/i, multiplier: 1, normalize: true },
  // "300000đ"
  { re: /(\d{4,7})\s*(?:đ|vnd|đồng)/i,       multiplier: 1 },
];

/**
 * Parse max budget from a message string.
 * @param {string} message
 * @returns {number|null}
 */
function parseBudgetMax(message) {
  const text = message.toLowerCase();
  for (const { re, multiplier, normalize } of BUDGET_PATTERNS) {
    const m = text.match(re);
    if (m) {
      const raw = normalize
        ? parseInt(m[1].replace(/[\.,]/g, ""))
        : parseFloat(m[1].replace(",", ".")) * multiplier;
      if (!isNaN(raw) && raw > 0) return Math.round(raw);
    }
  }
  return null;
}

/**
 * Extract confirmed preference facts from a single user message.
 * Returns only fields that are explicitly mentioned.
 *
 * @param {string} message
 * @returns {{
 *   skin_type: string|null,
 *   skin_concerns: string[],
 *   budget_max: number|null
 * }}
 */
function extractPreferenceFromMessage(message) {
  const lower = message.toLowerCase();
  let skin_type = null;
  const skin_concerns = [];

  for (const entry of SKIN_TYPE_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      skin_type = entry.value;
      break;
    }
  }

  for (const entry of SKIN_CONCERN_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      skin_concerns.push(entry.value);
    }
  }

  const budget_max = parseBudgetMax(message);

  return { skin_type, skin_concerns, budget_max };
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers for beauty profile upsert
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Compute a stable MD5 hash for the profile fields used in CustomerBeautyProfile.
 * Mirrors the pre-validate hook logic so the hash is always consistent.
 */
function computeProfileHash(fields) {
  return crypto.createHash("md5").update(JSON.stringify(fields)).digest("hex");
}

// ─────────────────────────────────────────────────────────────────────────────
// Main save function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Save confirmed preferences extracted from the user message.
 *
 * Writes to:
 *   1. CustomerBeautyProfile (upsert, source: "chatbot")
 *      — Only updates fields that were explicitly extracted (never overwrites with null)
 *   2. CustomerPreference key-value rows (upsert per field)
 *      — Mirror for fast chatbot context reads
 *
 * @param {object} params
 * @param {ObjectId|string} params.customerId
 * @param {string} params.message — raw user message
 * @returns {Promise<{ saved: boolean, extracted: object }>}
 */
async function updateCustomerPreference({ customerId, message }) {
  if (!customerId) return { saved: false, extracted: {} };

  const { skin_type, skin_concerns, budget_max } = extractPreferenceFromMessage(message);

  // Nothing extracted — nothing to save
  if (!skin_type && !skin_concerns.length && budget_max == null) {
    return { saved: false, extracted: {} };
  }

  const extracted = { skin_type, skin_concerns, budget_max };

  try {
    // ── 1. Upsert CustomerBeautyProfile ──────────────────────────────────────
    const existingProfile = await CustomerBeautyProfile.findOne({ customer_id: customerId }).lean();

    // Build merge — only overwrite unknown/empty values
    const update = { source: "chatbot", last_updated_at: new Date() };

    if (skin_type) {
      update.skin_type = skin_type;
    }

    if (skin_concerns.length) {
      // Merge new concerns with existing ones (deduplicate)
      const existing = existingProfile ? existingProfile.skin_concerns || [] : [];
      const merged = [...new Set([...existing, ...skin_concerns])];
      update.skin_concerns = merged;
    }

    if (budget_max != null) {
      // Store as "under_X" format to match the budget_range string convention
      update.budget_range = `under_${budget_max}`;
    }

    // Recompute profile_hash for the merged object
    const mergedForHash = {
      skin_type:          update.skin_type || existingProfile?.skin_type || "unknown",
      skin_concerns:      update.skin_concerns || existingProfile?.skin_concerns || [],
      sensitivity_level:  existingProfile?.sensitivity_level || "unknown",
      skin_tone:          existingProfile?.skin_tone || "unknown",
      undertone:          existingProfile?.undertone || "unknown",
      shade_preference:   existingProfile?.shade_preference || [],
      lip_color_preference: existingProfile?.lip_color_preference || [],
      makeup_style:       existingProfile?.makeup_style || [],
      beauty_goals:       existingProfile?.beauty_goals || [],
      avoid_ingredients:  existingProfile?.avoid_ingredients || [],
      preferred_ingredients: existingProfile?.preferred_ingredients || [],
      budget_range:       update.budget_range || existingProfile?.budget_range || "unknown",
      preferred_brands:   existingProfile?.preferred_brands || [],
      disliked_brands:    existingProfile?.disliked_brands || [],
      preferred_categories: existingProfile?.preferred_categories || [],
      texture_preference: existingProfile?.texture_preference || [],
      finish_preference:  existingProfile?.finish_preference || [],
      fragrance_preference: existingProfile?.fragrance_preference || "no_preference",
      purchase_intent:    existingProfile?.purchase_intent || [],
    };
    update.profile_hash = computeProfileHash(mergedForHash);

    await CustomerBeautyProfile.findOneAndUpdate(
      { customer_id: customerId },
      { $set: update },
      { upsert: true, new: true, runValidators: false }
    );

    // ── 2. Mirror to CustomerPreference key-value rows ────────────────────────
    const kvUpdates = [];

    if (skin_type) {
      kvUpdates.push({
        updateOne: {
          filter: { customer_id: customerId, preference_key: "skin_type" },
          update: { $set: { preference_value: skin_type, updated_at: new Date() } },
          upsert: true,
        },
      });
    }

    if (skin_concerns.length) {
      // Merge with existing KV concerns
      let existing = [];
      try {
        const row = await CustomerPreference.findOne({
          customer_id: customerId,
          preference_key: "skin_concerns",
        }).lean();
        if (row) existing = row.preference_value.split(",").filter(Boolean);
      } catch (_) {}
      const merged = [...new Set([...existing, ...skin_concerns])].join(",");
      kvUpdates.push({
        updateOne: {
          filter: { customer_id: customerId, preference_key: "skin_concerns" },
          update: { $set: { preference_value: merged, updated_at: new Date() } },
          upsert: true,
        },
      });
    }

    if (budget_max != null) {
      kvUpdates.push({
        updateOne: {
          filter: { customer_id: customerId, preference_key: "budget_max" },
          update: { $set: { preference_value: String(budget_max), updated_at: new Date() } },
          upsert: true,
        },
      });
    }

    if (kvUpdates.length) {
      await CustomerPreference.bulkWrite(kvUpdates, { ordered: false });
    }

    return { saved: true, extracted };
  } catch (err) {
    // Non-fatal — chatbot continues normally if preference save fails
    console.error("[ChatbotPreference] Save failed:", err.message);
    return { saved: false, extracted };
  }
}

module.exports = { updateCustomerPreference, extractPreferenceFromMessage };
