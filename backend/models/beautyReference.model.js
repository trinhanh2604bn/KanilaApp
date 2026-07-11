const mongoose = require("mongoose");

const beautyReferenceSchema = new mongoose.Schema(
  {
    reference_group: {
      type: String,
      required: true,
      index: true,
      enum: [
        "skin_type",
        "skin_concern",
        "sensitivity_level",
        "skin_color",
        "skin_undertone",
        "foundation_finish",
        "lipstick_color",
        "makeup_style",
        "budget",
        "avoid_ingredient",
        "beauty_goal",
        // Extended groups (for future features / chatbot / AI)
        "preferred_ingredient",
        "shopping_preference",
        "texture_preference",
        "fragrance_preference",
        "purchase_intent",
      ],
    },
    // reference_code must be UPPER_SNAKE_CASE (e.g. OILY_SKIN, ACNE, DARK_SPOT)
    // Enforced by validations/beautyReference.validation.js
    reference_code: {
      type: String,
      required: true,
      index: true,
    },
    // Flat display name fields — easier to index and query than nested objects
    display_name_vi: {
      type: String,
      required: true,
    },
    display_name_en: {
      type: String,
      default: "",
    },
    description: {
      type: String,
      default: "",
    },
    helper_text: {
      type: String,
      default: "",
    },
    icon_url: {
      type: String,
      default: "",
    },
    parent_code: {
      type: String,
      default: null,
    },
    sort_order: {
      type: Number,
      default: 0,
      index: true,
    },
    is_active: {
      type: Boolean,
      default: true,
      index: true,
    },
    is_multi_select: {
      type: Boolean,
      default: true,
    },
    severity_enabled: {
      type: Boolean,
      default: false,
    },
    recommendation_weight: {
      type: Number,
      default: 1,
    },
    boost_tags: {
      type: [String],
      default: [],
    },
    avoid_tags: {
      type: [String],
      default: [],
    },
    preferred_ingredients: {
      type: [String],
      default: [],
    },
    avoid_ingredients: {
      type: [String],
      default: [],
    },
    recommended_categories: {
      type: [String],
      default: [],
    },
    warning_text: {
      type: String,
      default: "",
    },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "beauty_references",
  }
);

beautyReferenceSchema.index({ reference_group: 1, reference_code: 1 }, { unique: true });

module.exports = mongoose.model("BeautyReference", beautyReferenceSchema);
