const mongoose = require("mongoose");

const variantOptionValueSchema = new mongoose.Schema(
  {
    variantId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "ProductVariant",
      required: [true, "Variant ID is required"],
    },
    productOptionValueId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "ProductOptionValue",
      required: [true, "Product option value ID is required"],
    },
  },
  { timestamps: true, collection: "variant_option_values" }
);

variantOptionValueSchema.index({ variantId: 1, productOptionValueId: 1 }, { unique: true });

module.exports = mongoose.model("VariantOptionValue", variantOptionValueSchema);
