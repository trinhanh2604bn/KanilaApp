"use strict";

/**
 * Search configuration.
 * Values are read from environment variables with safe defaults.
 */
module.exports = {
  /** "atlas" or "fallback" */
  provider: process.env.SEARCH_PROVIDER || "fallback",

  /** Allow fallback to product_search_documents query when Atlas fails */
  fallbackEnabled: process.env.SEARCH_FALLBACK_ENABLED !== "false",

  /** Atlas Search index name */
  atlasIndexName: process.env.SEARCH_ATLAS_INDEX_NAME || "product_search_v1",

  /** Minimum normalized query length for autocomplete */
  autocompletMinChars: parseInt(process.env.SEARCH_AUTOCOMPLETE_MIN_CHARS, 10) || 2,

  /** Default page size */
  defaultLimit: parseInt(process.env.SEARCH_DEFAULT_LIMIT, 10) || 20,

  /** Maximum page size */
  maxLimit: parseInt(process.env.SEARCH_MAX_LIMIT, 10) || 40,

  /** Whether image search is enabled */
  imageEnabled: process.env.SEARCH_IMAGE_ENABLED === "true",

  /**
   * Makeup category codes used to restrict search results.
   * Products whose primary or associated category code matches one of these
   * are included in makeup search.
   * Extend this list when new makeup categories are added.
   */
  makeupCategoryCodes: [
    "LIP_TINT",
    "LIP_LIQUID",
    "LIP_BULLET",
    "LIP_GLOSS",
    "LIP_BALM",
    "LIP_LINER",
    "LIP_PENCIL",
    "FOUNDATION",
    "CUSHION",
    "CONCEALER",
    "PRIMER",
    "SETTING_POWDER",
    "COMPACT_POWDER",
    "BLUSH",
    "CONTOUR",
    "BRONZER",
    "HIGHLIGHTER",
    "EYESHADOW",
    "EYESHADOW_PALETTE",
    "EYELINER",
    "MASCARA",
    "EYEBROW_PENCIL",
    "EYEBROW_MASCARA",
    "SETTING_SPRAY",
    "MAKEUP_TOOLS",
    // Generic makeup parent
    "MAKEUP",
    "TRANG_DIEM",
    "SON_MOI",
    "MAT",
    "KEM_NEN",
  ],

  /** Allowed sort options */
  sortOptions: ["relevance", "price_asc", "price_desc", "rating", "best_selling", "newest"],

  /** Allowed filter keys for public API */
  allowedFilterKeys: [
    "category_ids", "brand_ids",
    "min_price", "max_price",
    "min_rating",
    "in_stock", "on_sale", "ar_supported",
    "finish_types", "coverage_levels",
    "formula_types", "texture_types",
    "makeup_styles",
    "color_families",
    "shade_codes", "shade_names",
    "skin_tones", "undertones",
    "waterproof", "transfer_proof",
    "long_wear", "smudge_proof",
    "oil_control", "sensitive_friendly",
  ],

  /** Maximum length for filter array values */
  maxFilterArrayLength: 20,
};
