const mongoose = require("mongoose");

const productVariantSchema = new mongoose.Schema(
  {
    productId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: [true, "Product ID is required"],
    },
    sku: {
      type: String,
      required: [true, "SKU is required"],
      unique: true,
      uppercase: true,
      trim: true,
    },
    barcode: {
      type: String,
      default: "",
    },
    variantName: {
      type: String,
      required: [true, "Variant name is required"],
      trim: true,
    },
    variantStatus: {
      type: String,
      enum: ["active", "inactive"],
      default: "active",
    },
    weightGrams: {
      type: Number,
      default: 0,
    },
    volumeMl: {
      type: Number,
      default: 0,
    },
    costAmount: {
      type: Number,
      default: 0,
    },
    ar_config: {
      enabled: { type: Boolean, default: false },
      type: { type: String, enum: ["LIP"], default: "LIP" },
      shade_hex: { type: String },
      finish_type: { type: String, enum: ["MATTE", "SATIN", "GLOSS", "TINT"] },
      opacity: { type: Number, min: 0, max: 1, default: 1 },
      renderer_version: { type: String, default: "lip_v1" },
      calibration_status: { type: String, enum: ["DRAFT", "VERIFIED", "DISABLED"], default: "DRAFT" },
    },
  },
  { timestamps: true, collection: "product_variants" }
);

productVariantSchema.index({ productId: 1, variantStatus: 1 });

module.exports = mongoose.model("ProductVariant", productVariantSchema);
