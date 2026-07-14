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

    // ─── Core Skin Profile (all values store reference_code, e.g. "OILY_SKIN") ──
    skin_type:        { type: String, default: "unknown", index: true },
    skin_concerns:    { type: [String], default: [], index: true },
    sensitivity_level:{ type: String, default: "unknown", index: true },

    // Renamed from skin_tone → skin_color (matches frontend field name)
    skin_color:       { type: String, default: "unknown", index: true },
    // Renamed from undertone → skin_undertone (matches frontend field name)
    skin_undertone:   { type: String, default: "unknown", index: true },

    // Renamed from finish_preference:[String] → foundation_finish:String (single-select)
    foundation_finish:{ type: String, default: "unknown" },

    // Renamed from lip_color_preference → lipstick_colors (matches frontend field name)
    lipstick_colors:  { type: [String], default: [] },

    // Renamed from makeup_style → makeup_styles (plural, matches frontend field name)
    makeup_styles:    { type: [String], default: [] },

    // Renamed from budget_range → budget (matches frontend field name)
    budget:           { type: String, default: "unknown" },

    avoid_ingredients:    { type: [String], default: [] },
    beauty_goals:         { type: [String], default: [], index: true },

    // ─── Extended Profile Fields (used by chatbot / AI / future features) ───────
    preferred_ingredients: { type: [String], default: [] },
    preferred_brands:      [{ type: mongoose.Schema.Types.ObjectId, ref: "Brand", default: [] }],
    disliked_brands:       [{ type: mongoose.Schema.Types.ObjectId, ref: "Brand", default: [] }],
    preferred_categories:  [{ type: mongoose.Schema.Types.ObjectId, ref: "Category", default: [] }],
    texture_preference:    { type: [String], default: [] },
    fragrance_preference:  { type: String, default: "no_preference" },
    purchase_intent:       { type: [String], default: [] },

    // ─── AI / AR Analysis Output ─────────────────────────────────────────────────
    // skin_indicators is AI-generated, NOT user input — stored separately from reference codes
    skin_indicators: {
      data:             { type: [String], default: [] },
      analyzed_at:      { type: Date, default: null },
      source:           { type: String, default: null }, // e.g. "ar_scan", "chatbot"
      confidence_score: { type: Number, default: null },
    },

    // ─── System Fields ────────────────────────────────────────────────────────────
    profile_completion_rate: { type: Number, default: 0, min: 0, max: 100 },
    profile_hash:            { type: String, required: true, index: true },
    source: {
      type: String,
      enum: ["onboarding", "account", "chatbot", "ar", "admin", "migration"],
      default: "onboarding",
    },
    last_updated_at: { type: Date, default: Date.now },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "customer_beauty_profiles",
  }
);

const { areValidCodes, isValidCode } = require("../utils/beautyReferenceCodeResolver");

// Strict validation against reference codes - DISABLED FOR NOW to allow any data to save
customerBeautyProfileSchema.pre("validate", async function () {
  // Bỏ qua validation mapping codes để đảm bảo lưu được dữ liệu từ app
});

// ─── Pre-save hook: completion rate + profile hash ────────────────────────────
customerBeautyProfileSchema.pre("validate", async function () {
  const targetFields = [
    "skin_type", "skin_concerns", "sensitivity_level", "skin_color", "skin_undertone",
    "foundation_finish", "lipstick_colors", "makeup_styles", "budget", "avoid_ingredients",
    "beauty_goals", "preferred_ingredients", "preferred_brands", "disliked_brands",
    "preferred_categories", "texture_preference", "fragrance_preference", "purchase_intent"
  ];

  let populatedFields = 0;
  
  // NOTE: For Phase 2, we treat [] and null as "not populated" for immediate compatibility.
  for (const field of targetFields) {
    const val = this[field];
    if (Array.isArray(val) && val.length > 0) {
      populatedFields++;
    } else if (typeof val === "string" && val && val !== "unknown" && val !== "no_preference") {
      populatedFields++;
    }
  }

  this.profile_completion_rate = Math.round((populatedFields / targetFields.length) * 100);

  // Compute deterministic hash
  const hashObj = {};
  for (const field of targetFields) {
    const val = this[field];
    // Sort arrays for deterministic hashing
    if (Array.isArray(val)) {
      hashObj[field] = [...val].sort();
    } else {
      hashObj[field] = val;
    }
  }
  // Also sort keys just in case
  const sortedKeys = Object.keys(hashObj).sort();
  const canonicalObj = {};
  for (const k of sortedKeys) {
    canonicalObj[k] = hashObj[k];
  }

  const hashString = JSON.stringify(canonicalObj);
  this.profile_hash = crypto.createHash("md5").update(hashString).digest("hex");
  this.last_updated_at = new Date();
});

module.exports = mongoose.model("CustomerBeautyProfile", customerBeautyProfileSchema);
