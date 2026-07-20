/**
 * skinMatch.config.js
 * Config for deterministic Skin Match algorithm.
 */

module.exports = {
  SKIN_MATCH_ALGORITHM_VERSION: "skin_match_rule_v2",

  WEIGHTS: {
    SKINCARE: {
      skin_type: 25,
      skin_concerns: 25,
      sensitivity_compatibility: 15,
      beauty_goals: 15,
      preferred_ingredients: 10,
      texture_preference: 10,
    },
    MAKEUP: {
      skin_color_tone_compatibility: 20,
      undertone_compatibility: 25,
      finish_preference: 15,
      skin_type_compatibility: 15,
      shade_preference: 15,
      sensitivity_compatibility: 10,
    }
  },

  MATCH_LEVEL_THRESHOLDS: {
    EXCELLENT_MATCH: 85,
    GOOD_MATCH: 70,
    MODERATE_MATCH: 50,
    CAUTION: 0
  },

  // These attributes trigger hard conflicts for users with specific profiles.
  CONFLICT_RULES: {
    SENSITIVE_SKIN: {
      check: (profile) => profile.sensitivity_level === "high",
      flags_to_avoid: ["contains_alcohol", "contains_fragrance", "not_for_sensitive_skin"]
    }
  }
};
