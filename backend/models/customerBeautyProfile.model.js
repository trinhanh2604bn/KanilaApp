const mongoose = require("mongoose");
const crypto = require("crypto");

const customerBeautyProfileSchema = new mongoose.Schema(
  {
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      required: true,
      unique: true,
      index: true,
    },
    skin_type: { type: String, default: "unknown", index: true },
    skin_concerns: { type: [String], default: [], index: true },
    sensitivity_level: { type: String, default: "unknown", index: true },
    skin_tone: { type: String, default: "unknown", index: true },
    undertone: { type: String, default: "unknown", index: true },
    shade_preference: { type: [String], default: [] },
    lip_color_preference: { type: [String], default: [] },
    makeup_style: { type: [String], default: [] },
    beauty_goals: { type: [String], default: [], index: true },
    avoid_ingredients: { type: [String], default: [] },
    preferred_ingredients: { type: [String], default: [] },
    budget_range: { type: String, default: "unknown" },
    preferred_brands: [{ type: mongoose.Schema.Types.ObjectId, ref: "Brand", default: [] }],
    disliked_brands: [{ type: mongoose.Schema.Types.ObjectId, ref: "Brand", default: [] }],
    preferred_categories: [{ type: mongoose.Schema.Types.ObjectId, ref: "Category", default: [] }],
    texture_preference: { type: [String], default: [] },
    finish_preference: { type: [String], default: [] },
    fragrance_preference: { type: String, default: "no_preference" },
    purchase_intent: { type: [String], default: [] },
    profile_completion_rate: { type: Number, default: 0, min: 0, max: 100 },
    profile_hash: { type: String, required: true, index: true },
    source: {
      type: String,
      enum: ["onboarding", "account", "chatbot", "ar", "admin"],
      default: "onboarding",
    },
    last_updated_at: { type: Date, default: Date.now },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "customer_beauty_profiles",
  }
);

// Pre-save hook to calculate profile completion rate and profile hash
customerBeautyProfileSchema.pre("validate", function (next) {
  // Compute profile completion rate
  let populatedFields = 0;
  const targetFields = [
    "skin_type",
    "skin_concerns",
    "sensitivity_level",
    "skin_tone",
    "undertone",
    "shade_preference",
    "lip_color_preference",
    "makeup_style",
    "beauty_goals",
    "avoid_ingredients",
    "preferred_ingredients",
    "budget_range",
    "preferred_brands",
    "disliked_brands",
    "preferred_categories",
    "texture_preference",
    "finish_preference",
    "fragrance_preference",
    "purchase_intent",
  ];

  for (const field of targetFields) {
    const val = this[field];
    if (Array.isArray(val) && val.length > 0) {
      populatedFields++;
    } else if (typeof val === "string" && val && val !== "unknown" && val !== "no_preference") {
      populatedFields++;
    }
  }

  this.profile_completion_rate = Math.round((populatedFields / targetFields.length) * 100);

  // Compute profile hash
  const hashObj = {};
  for (const field of targetFields) {
    hashObj[field] = this[field];
  }
  const hashString = JSON.stringify(hashObj);
  this.profile_hash = crypto.createHash("md5").update(hashString).digest("hex");
  this.last_updated_at = new Date();

  next();
});

module.exports = mongoose.model("CustomerBeautyProfile", customerBeautyProfileSchema);
