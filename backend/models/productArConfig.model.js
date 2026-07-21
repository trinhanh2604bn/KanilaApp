const mongoose = require("mongoose");

const arShadeSchema = new mongoose.Schema(
  {
    variant_id: { type: String },
    variant_name: { type: String },
    shade_hex: { type: String, default: "#FFFFFF" },
    finish_type: {
      type: String,
      enum: ["MATTE", "GLOSSY", "SATIN", "TINT"],
      default: "MATTE",
    },
    opacity: { type: Number, default: 0.7, min: 0.0, max: 1.0 },
    price: { type: Number, default: 0 },
    currency_code: { type: String, default: "VND" },
    in_stock: { type: Boolean, default: true },
    thumbnail_url: { type: String, default: "" },
    enabled: { type: Boolean, default: true },
    display_order: { type: Number, default: 0 },
  },
  { _id: false }
);

const productArConfigSchema = new mongoose.Schema(
  {
    // Store as Mixed to support both ObjectId (legacy) and String product IDs
    product_id: {
      type: mongoose.Schema.Types.Mixed,
      required: true,
      unique: true,
      index: true,
    },
    product_name: { type: String },
    status: {
      type: String,
      enum: ["active", "inactive", "draft"],
      default: "active",
    },
    ar_type: {
      type: String,
      enum: ["LIPS", "CHEEKS", "EYES"],
      default: "LIPS",
    },
    renderer_version: { type: String, default: "v2" },
    variants: { type: [arShadeSchema], default: [] },

    // Legacy fields kept for backward-compat
    makeup_type: { type: String },
    texture_url: { type: String, default: "" },
    hex_color: { type: String, default: "#FFFFFF" },
    intensity: { type: Number, default: 1.0, min: 0.0, max: 1.0 },
    is_active: { type: Boolean, default: true },

    mock_data: { type: Boolean, default: false },
    seed_batch: { type: String },
  },
  {
    timestamps: true,
    collection: "product_ar_configs",
  }
);

module.exports = mongoose.model("ProductArConfig", productArConfigSchema);
