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
 * - All writes go through customerBeautyProfile.service.js (which triggers model hooks,
 *   BeautyReference validation, and hash recalculation).
 * - runValidators is NEVER bypassed.
 * - All stored codes MUST be UPPER_SNAKE_CASE matching seed-beauty-references.js values.
 * - Budget values MUST map to an actual seeded reference code (UNDER_300, 300_500, 500_1000, OVER_1000).
 */

const mongoose = require("mongoose");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const CustomerPreference = require("../models/customerPreference.model");
const customerBeautyProfileService = require("./customerBeautyProfile.service");

// ─────────────────────────────────────────────────────────────────────────────
// Canonical code maps — all codes match seed-beauty-references.js reference_codes
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_TYPE_MAP = [
  { keywords: ["da dầu", "da nhờn", "oily", "dầu và", "dầu mụn", "mình dầu", "da của mình dầu"], value: "OILY_SKIN" },
  { keywords: ["da khô", "dry skin", "khô da"], value: "DRY_SKIN" },
  { keywords: ["da nhạy cảm", "da nhạy", "sensitive", "nhạy cảm"], value: "SENSITIVE_SKIN" },
  { keywords: ["da hỗn hợp", "combination", "hỗn hợp"], value: "COMBINATION_SKIN" },
  { keywords: ["da thường", "da bình thường", "normal skin", "bình thường"], value: "NORMAL_SKIN" },
];

// Canonical skin_concern reference_codes from seed-beauty-references.js
const SKIN_CONCERN_MAP = [
  { keywords: ["mụn", "acne", "nổi mụn", "dễ bị mụn", "hay bị mụn"], value: "ACNE" },
  { keywords: ["thâm", "dark spot", "nám", "tàn nhang", "thâm nám"], value: "DARK_SPOT" },
  { keywords: ["xỉn màu", "xỉn", "dullness", "sạm", "da xỉn"], value: "DULLNESS" },
  { keywords: ["lỗ chân lông to", "pore", "lỗ chân lông"], value: "LARGE_PORES" },
  { keywords: ["mụn đầu đen", "blackhead", "đầu đen"], value: "BLACKHEADS" },
  { keywords: ["nếp nhăn", "wrinkle", "lão hóa", "anti aging", "chống lão"], value: "WRINKLES" },
  { keywords: ["thiếu ẩm", "mất nước", "dehydration", "mất ẩm", "khô ráp"], value: "DEHYDRATION" },
  { keywords: ["da đỏ", "redness", "đỏ ửng", "kích ứng"], value: "REDNESS" },
];

// Canonical preferred_ingredient reference_codes from seed-beauty-references.js
const PREFERRED_INGREDIENT_MAP = [
  { keywords: ["niacinamide", "niacin"], value: "NIACINAMIDE" },
  { keywords: ["hyaluronic acid", "ha", "axit hyaluronic", "hyaluronic"], value: "HYALURONIC_ACID" },
  { keywords: ["ceramide"], value: "CERAMIDE" },
  { keywords: ["centella", "rau má", "cica"], value: "CENTELLA" },
  { keywords: ["vitamin c", "vit c", "ascorbic"], value: "VITAMIN_C" },
  { keywords: ["retinol", "vitamin a"], value: "RETINOL" },
  { keywords: ["aha", "glycolic acid", "lactic acid"], value: "AHA" },
  { keywords: ["bha", "salicylic acid", "acid salicylic"], value: "BHA" },
  { keywords: ["peptide"], value: "PEPTIDE" },
  { keywords: ["panthenol", "provitamin b5", "b5"], value: "PANTHENOL" },
];

// Canonical budget reference_codes from seed-beauty-references.js:
//   UNDER_300  → under 300,000 VND
//   300_500    → 300,000 – 500,000 VND
//   500_1000   → 500,000 – 1,000,000 VND
//   OVER_1000  → over 1,000,000 VND
const BUDGET_PATTERNS = [
  // Explicit "dưới 300k" or "under 300k" → UNDER_300
  { re: /(?:dưới|under|below)\s*3\d{2}[kK]/i,       value: "UNDER_300" },
  { re: /(?:dưới|under|below)\s*3\d{2}\s*nghìn/i,    value: "UNDER_300" },
  // "khoảng 300–500k" or "300 đến 500k" → 300_500
  { re: /3\d{2}[–\-]\s*5\d{2}[kK]/i,                value: "300_500" },
  { re: /khoảng 4\d{2}[kK]/i,                       value: "300_500" },
  // "500 đến 1 triệu" → 500_1000
  { re: /5\d{2}[–\-]\s*[1-9]\d{2,3}[kK]/i,          value: "500_1000" },
  { re: /khoảng 7\d{2}[kK]/i,                       value: "500_1000" },
  // "trên 1 triệu" → OVER_1000
  { re: /(?:trên|over|above)\s*1\s*(?:tr|triệu|m)/i, value: "OVER_1000" },
  // Exact numeric fallback — bucket by amount
  { re: /(\d+[.,]?\d*)\s*(?:nghìn|ngàn|[kK])(?:\s*(?:đ|d|đồng))?/i, numeric: true },
];

/**
 * Map a raw numeric VND amount to a canonical budget reference code.
 * Thresholds match seed-beauty-references.js groups.
 */
function bucketBudget(vndAmount) {
  if (vndAmount < 300000) return "UNDER_300";
  if (vndAmount < 500000) return "300_500";
  if (vndAmount < 1000000) return "500_1000";
  return "OVER_1000";
}

/**
 * Parse a budget reference code from a Vietnamese message.
 * Returns a canonical reference_code (e.g. "UNDER_300") or null.
 */
function parseBudgetCode(message) {
  const text = message.toLowerCase();
  for (const pattern of BUDGET_PATTERNS) {
    if (pattern.numeric) {
      const m = text.match(pattern.re);
      if (m) {
        const raw = parseFloat(m[1].replace(",", ".")) * 1000;
        if (!isNaN(raw) && raw > 0) return bucketBudget(Math.round(raw));
      }
    } else {
      if (pattern.re.test(text)) return pattern.value;
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
 *   budget: string|null,
 *   preferred_ingredients: string[]
 * }}
 */
function extractPreferenceFromMessage(message) {
  const lower = message.toLowerCase();
  let skin_type = null;
  const skin_concerns = [];
  const preferred_ingredients = [];

  for (const entry of SKIN_TYPE_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      skin_type = entry.value; // Already UPPER_SNAKE_CASE
      break;
    }
  }

  for (const entry of SKIN_CONCERN_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      skin_concerns.push(entry.value); // Already UPPER_SNAKE_CASE
    }
  }

  for (const entry of PREFERRED_INGREDIENT_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      preferred_ingredients.push(entry.value); // Already UPPER_SNAKE_CASE
    }
  }

  const budget = parseBudgetCode(message);

  return { skin_type, skin_concerns, preferred_ingredients, budget };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main save function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Save confirmed preferences extracted from the user message.
 *
 * ALL writes go through customerBeautyProfile.service.js, which:
 *   - Runs the Mongoose pre("validate") hook (BeautyReference validation + hash)
 *   - Invalidates recommendation snapshots on hash change
 *   - Does NOT use runValidators: false
 *
 * @param {object} params
 * @param {ObjectId|string} params.customerId
 * @param {string} params.message — raw user message
 * @returns {Promise<{ saved: boolean, extracted: object }>}
 */
async function updateCustomerPreference({ customerId, message }) {
  if (!customerId) return { saved: false, extracted: {} };

  const { skin_type, skin_concerns, preferred_ingredients, budget } = extractPreferenceFromMessage(message);

  // Nothing extracted — nothing to save
  if (!skin_type && !skin_concerns.length && !preferred_ingredients.length && budget == null) {
    return { saved: false, extracted: {} };
  }

  const extracted = { skin_type, skin_concerns, preferred_ingredients, budget };

  try {
    // Load existing profile to perform additive merges on arrays
    const existingProfile = await CustomerBeautyProfile.findOne({ customer_id: customerId }).lean();

    const update = {};

    if (skin_type) {
      update.skin_type = skin_type;
    }

    if (skin_concerns.length) {
      const existing = existingProfile ? existingProfile.skin_concerns || [] : [];
      // Deduplicate, preserve canonical UPPER_SNAKE_CASE
      update.skin_concerns = [...new Set([...existing, ...skin_concerns])];
    }

    if (preferred_ingredients.length) {
      const existing = existingProfile ? existingProfile.preferred_ingredients || [] : [];
      update.preferred_ingredients = [...new Set([...existing, ...preferred_ingredients])];
    }

    if (budget != null) {
      update.budget = budget; // Canonical reference code (UNDER_300, 300_500, etc.)
    }

    if (Object.keys(update).length === 0) {
      return { saved: false, extracted };
    }

    // ── Route through the service (triggers model hooks: validation + hash + snapshot invalidation)
    const context = { source: "chatbot", trustedInternalCall: true };

    if (existingProfile) {
      await customerBeautyProfileService.updateProfile(String(customerId), update, context);
    } else {
      await customerBeautyProfileService.createProfile(String(customerId), update, context);
    }

    // ── Mirror to CustomerPreference key-value rows (fast lookup fallback)
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

    if (preferred_ingredients.length) {
      let existing = [];
      try {
        const row = await CustomerPreference.findOne({
          customer_id: customerId,
          preference_key: "preferred_ingredients",
        }).lean();
        if (row) existing = row.preference_value.split(",").filter(Boolean);
      } catch (_) {}
      const merged = [...new Set([...existing, ...preferred_ingredients])].join(",");
      kvUpdates.push({
        updateOne: {
          filter: { customer_id: customerId, preference_key: "preferred_ingredients" },
          update: { $set: { preference_value: merged, updated_at: new Date() } },
          upsert: true,
        },
      });
    }

    if (budget != null) {
      kvUpdates.push({
        updateOne: {
          filter: { customer_id: customerId, preference_key: "budget" },
          update: { $set: { preference_value: budget, updated_at: new Date() } },
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
