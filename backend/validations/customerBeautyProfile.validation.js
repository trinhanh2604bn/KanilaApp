/**
 * Customer Beauty Profile Validation
 *
 * Validates incoming PATCH/POST body for /api/customers/:id/beauty-profile
 * All values are expected to be reference_code strings (UPPER_SNAKE_CASE)
 * e.g. skin_type: "OILY_SKIN", skin_concerns: ["ACNE", "DARK_SPOT"]
 */

const validateCustomerBeautyProfile = async (data) => {
  const errors = [];

  // ─── Multi-select fields (arrays of reference_code strings) ──────────────────
  const arrayFields = [
    "skin_concerns",
    "lipstick_colors",    // renamed from: lip_color_preference
    "makeup_styles",      // renamed from: makeup_style
    "beauty_goals",
    "avoid_ingredients",
    "preferred_ingredients",
    "texture_preference",
    "purchase_intent",
  ];

  for (const field of arrayFields) {
    if (data[field] !== undefined) {
      if (!Array.isArray(data[field])) {
        errors.push(`${field} must be an array of strings`);
      } else if (data[field].some((item) => typeof item !== "string")) {
        errors.push(`All elements in ${field} must be strings`);
      }
    }
  }

  // ─── Single-select fields (single reference_code string) ─────────────────────
  const stringFields = [
    "skin_type",
    "sensitivity_level",
    "skin_color",         // renamed from: skin_tone
    "skin_undertone",     // renamed from: undertone
    "foundation_finish",  // renamed from: finish_preference (now single String)
    "budget",             // renamed from: budget_range
    "fragrance_preference",
    "source",
  ];

  for (const field of stringFields) {
    if (data[field] !== undefined && typeof data[field] !== "string") {
      errors.push(`${field} must be a string`);
    }
  }

  // ─── Reject legacy field names to prevent silent data loss ───────────────────
  const legacyFields = {
    skin_tone:           "skin_color",
    undertone:           "skin_undertone",
    finish_preference:   "foundation_finish",
    lip_color_preference:"lipstick_colors",
    makeup_style:        "makeup_styles",
    budget_range:        "budget",
  };

  for (const [oldName, newName] of Object.entries(legacyFields)) {
    if (data[oldName] !== undefined) {
      errors.push(
        `Field "${oldName}" is no longer accepted. Use "${newName}" instead.`
      );
    }
  }

  return errors;
};

module.exports = {
  validateCustomerBeautyProfile,
};
