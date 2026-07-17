"use strict";
const mongoose = require("mongoose");

/**
 * product_search_documents
 *
 * Derived collection — one document per makeup product.
 * Built from Product, Brand, Category, ProductCategory, ProductVariant,
 * ProductAttribute, ReviewSummary, InventoryBalance, Promotion, and AR metadata.
 *
 * NEVER replace Product as the source of truth.
 * schema_version must be bumped when the shape changes.
 */
const productSearchDocumentSchema = new mongoose.Schema(
  {
    // ─── Identity ────────────────────────────────────────────────────────────
    product_id:               { type: mongoose.Schema.Types.ObjectId, required: true, unique: true },
    product_name:             { type: String, default: "" },
    product_name_normalized:  { type: String, default: "" },
    product_code:             { type: String, default: "" },
    slug:                     { type: String, default: "" },

    // ─── Brand ───────────────────────────────────────────────────────────────
    brand_id:                 { type: mongoose.Schema.Types.ObjectId, default: null },
    brand_name:               { type: String, default: "" },
    brand_name_normalized:    { type: String, default: "" },
    brand_code:               { type: String, default: "" },

    // ─── Category ────────────────────────────────────────────────────────────
    category_ids:             { type: [mongoose.Schema.Types.ObjectId], default: [] },
    category_names:           { type: [String], default: [] },
    category_names_normalized:{ type: [String], default: [] },
    category_codes:           { type: [String], default: [] },

    // ─── Variants ────────────────────────────────────────────────────────────
    variant_ids:              { type: [mongoose.Schema.Types.ObjectId], default: [] },
    variant_names:            { type: [String], default: [] },
    variant_names_normalized: { type: [String], default: [] },
    variant_skus:             { type: [String], default: [] },
    barcodes:                 { type: [String], default: [] },

    // ─── Shade / color data ───────────────────────────────────────────────────
    shade_names:              { type: [String], default: [] },
    shade_names_normalized:   { type: [String], default: [] },
    shade_codes:              { type: [String], default: [] },
    color_families:           { type: [String], default: [] },
    color_hex_values:         { type: [String], default: [] },

    // ─── Makeup attributes ────────────────────────────────────────────────────
    finish_types:             { type: [String], default: [] },   // e.g. matte, glossy, dewy, satin
    coverage_levels:          { type: [String], default: [] },   // e.g. full, medium, light
    formula_types:            { type: [String], default: [] },   // e.g. liquid, powder, stick, cream
    texture_types:            { type: [String], default: [] },   // e.g. velvet, mousse, balm
    makeup_styles:            { type: [String], default: [] },   // e.g. natural, glam, everyday
    product_benefits:         { type: [String], default: [] },
    product_claims:           { type: [String], default: [] },
    attribute_terms:          { type: [String], default: [] },
    product_tags:             { type: [String], default: [] },

    // Skin tone / undertone (for foundation / concealer)
    skin_tones:               { type: [String], default: [] },
    undertones:               { type: [String], default: [] },

    // ─── Commercial claims (only when source data exists) ────────────────────
    waterproof:               { type: Boolean, default: null },
    transfer_proof:           { type: Boolean, default: null },
    long_wear:                { type: Boolean, default: null },
    smudge_proof:             { type: Boolean, default: null },
    oil_control:              { type: Boolean, default: null },
    sensitive_friendly:       { type: Boolean, default: null },
    fragrance_free:           { type: Boolean, default: null },
    cruelty_free:             { type: Boolean, default: null },

    // ─── Commerce ────────────────────────────────────────────────────────────
    price:                    { type: Number, default: 0 },
    compare_at_price:         { type: Number, default: null },
    discount_percentage:      { type: Number, default: null },
    average_rating:           { type: Number, default: 0 },
    review_count:             { type: Number, default: 0 },
    sales_count:              { type: Number, default: 0 },
    stock:                    { type: Number, default: 0 },
    in_stock:                 { type: Boolean, default: false },
    is_best_seller:           { type: Boolean, default: false },
    is_new:                   { type: Boolean, default: false },
    is_on_sale:               { type: Boolean, default: false },
    ar_supported:             { type: Boolean, default: false },

    // ─── Presentation ────────────────────────────────────────────────────────
    primary_image_url:        { type: String, default: "" },

    // ─── Lifecycle ───────────────────────────────────────────────────────────
    product_status:           { type: String, default: "active" },
    is_active:                { type: Boolean, default: true },
    source_updated_at:        { type: Date, default: null },
    indexed_at:               { type: Date, default: Date.now },
    schema_version:           { type: Number, default: 2 },
  },
  { timestamps: true, collection: "product_search_documents" }
);

// ─── Indexes (fallback search + filters) ─────────────────────────────────────
// Note: product_id already has unique:true index from schema definition above
productSearchDocumentSchema.index({ is_active: 1, product_status: 1 });
productSearchDocumentSchema.index({ product_name_normalized: 1 });
productSearchDocumentSchema.index({ brand_name_normalized: 1 });
productSearchDocumentSchema.index({ category_names_normalized: 1 });
productSearchDocumentSchema.index({ product_code: 1 });
productSearchDocumentSchema.index({ variant_skus: 1 });
productSearchDocumentSchema.index({ barcodes: 1 });
productSearchDocumentSchema.index({ shade_codes: 1 });
productSearchDocumentSchema.index({ shade_names_normalized: 1 });
productSearchDocumentSchema.index({ color_families: 1 });
productSearchDocumentSchema.index({ finish_types: 1 });
productSearchDocumentSchema.index({ coverage_levels: 1 });
productSearchDocumentSchema.index({ brand_id: 1 });
productSearchDocumentSchema.index({ category_ids: 1 });
productSearchDocumentSchema.index({ price: 1 });
productSearchDocumentSchema.index({ average_rating: -1 });
productSearchDocumentSchema.index({ sales_count: -1 });
productSearchDocumentSchema.index({ is_best_seller: 1, sales_count: -1 });
productSearchDocumentSchema.index({ in_stock: 1 });
productSearchDocumentSchema.index({ is_on_sale: 1 });
productSearchDocumentSchema.index({ ar_supported: 1 });
productSearchDocumentSchema.index({ indexed_at: -1 });

module.exports = mongoose.model("ProductSearchDocument", productSearchDocumentSchema);
