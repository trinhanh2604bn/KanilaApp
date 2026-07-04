const mongoose = require("mongoose");

const productBeautyProfileSchema = new mongoose.Schema(
  {
    product_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: true,
      unique: true,
      index: true,
    },
    suitable_skin_types: { type: [String], default: [], index: true },
    suitable_skin_concerns: { type: [String], default: [], index: true },
    suitable_sensitivity_levels: { type: [String], default: [] },
    suitable_skin_tones: { type: [String], default: [] },
    suitable_undertones: { type: [String], default: [] },
    supported_beauty_goals: { type: [String], default: [], index: true },
    key_ingredients: { type: [String], default: [], index: true },
    avoid_for_ingredients: { type: [String], default: [] },
    texture: { type: String, default: "" },
    finish: { type: String, default: "" },
    fragrance_type: { type: String, default: "no_preference" },
    product_tags: { type: [String], default: [], index: true },
    recommendation_boost_score: { type: Number, default: 0 },
    recommendation_penalty_score: { type: Number, default: 0 },
    is_active: { type: Boolean, default: true, index: true },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "product_beauty_profiles",
  }
);

module.exports = mongoose.model("ProductBeautyProfile", productBeautyProfileSchema);
