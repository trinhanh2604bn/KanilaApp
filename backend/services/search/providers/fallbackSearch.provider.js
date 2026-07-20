"use strict";

const mongoose = require("mongoose");
const SearchQueryNormalizer = require("../../../utils/searchQueryNormalizer");
const ProductSearchDocument = require("../../../models/productSearchDocument.model");
const searchConfig = require("../../../config/search.config");

/**
 * FallbackSearchProvider
 *
 * Safe, anchored-prefix fallback search against product_search_documents.
 * Never does unanchored regex on the products collection.
 * Implements the deterministic ranking priority from the spec.
 */
class FallbackSearchProvider {
  /**
   * Full-text-like product search using product_search_documents.
   *
   * @param {{
   *   normalized: object,       // from SearchQueryNormalizer.normalize()
   *   searchTarget: string,     // final (possibly alias-resolved) folded query
   *   filters: object,          // sanitized filter params
   *   sort: string,
   *   skip: number,
   *   limit: number,
   * }} opts
   */
  static async search({ normalized, searchTarget, filters, sort, skip, limit }) {
    const baseFilter = this._buildBaseFilter(filters);
    const mongoSort = this._buildSort(sort);

    // When query is empty, just list with filters
    if (normalized.isEmpty || !searchTarget) {
      const [total, docs] = await Promise.all([
        ProductSearchDocument.countDocuments(baseFilter),
        ProductSearchDocument.find(baseFilter).sort(mongoSort).skip(skip).limit(limit).lean(),
      ]);
      return {
        items: docs,
        total,
        facets: await this._buildFacets(baseFilter),
      };
    }

    const escaped = SearchQueryNormalizer.escapeRegex(searchTarget);

    // ─── Priority buckets ────────────────────────────────────────────────────
    // We run a single query and rely on MongoDB sort + scored fields.
    // For fallback, we use a weighted $or array ordered by priority.

    const orConditions = [];

    // P1: Exact barcode
    if (normalized.looksLikeBarcode) {
      orConditions.push({ barcodes: normalized.alphanum });
    }
    // P1-P4: Exact identifiers
    if (normalized.looksLikeSku) {
      orConditions.push({ variant_skus: normalized.alphanum.toUpperCase() });
    }
    if (normalized.looksLikeProductCode) {
      orConditions.push({ product_code: { $regex: `^${SearchQueryNormalizer.escapeRegex(normalized.alphanum)}$`, $options: "i" } });
    }
    if (normalized.looksLikeShadeCode) {
      orConditions.push({ shade_codes: { $regex: `^${SearchQueryNormalizer.escapeRegex(normalized.alphanum)}$`, $options: "i" } });
    }

    // Always try identifier exact lookup on barcode/sku/code fields regardless of "looksLike" heuristic
    orConditions.push({ barcodes: searchTarget });
    orConditions.push({ variant_skus: { $regex: `^${escaped}$`, $options: "i" } });
    orConditions.push({ product_code: { $regex: `^${escaped}$`, $options: "i" } });
    orConditions.push({ shade_codes: { $regex: `^${escaped}$`, $options: "i" } });

    // P5: Exact product name
    orConditions.push({
      product_name_normalized: { $regex: `^${escaped}$`, $options: "i" },
    });

    // P6: Exact variant/shade name
    orConditions.push({
      shade_names_normalized: { $regex: `^${escaped}$`, $options: "i" },
    });
    orConditions.push({
      variant_names_normalized: { $regex: `^${escaped}$`, $options: "i" },
    });

    // P7: Product name prefix (anchored)
    orConditions.push({
      product_name_normalized: { $regex: `^${escaped}`, $options: "i" },
    });

    // P8: Shade name prefix (anchored)
    orConditions.push({
      shade_names_normalized: { $regex: `^${escaped}`, $options: "i" },
    });

    // P9: Category name prefix (anchored)
    orConditions.push({
      category_names_normalized: { $elemMatch: { $regex: `^${escaped}`, $options: "i" } },
    });

    // P10: Brand name prefix (anchored)
    orConditions.push({
      brand_name_normalized: { $regex: `^${escaped}`, $options: "i" },
    });

    // P11: Color family
    orConditions.push({
      color_families: { $elemMatch: { $regex: `^${escaped}`, $options: "i" } },
    });

    // P12-P14: Makeup property arrays
    for (const field of ["finish_types", "coverage_levels", "formula_types", "texture_types"]) {
      orConditions.push({
        [field]: { $elemMatch: { $regex: `^${escaped}`, $options: "i" } },
      });
    }

    // P15: Benefits / claims / tags — contains match (safe since restricted to search_documents)
    for (const field of ["product_benefits", "product_claims", "product_tags", "attribute_terms", "makeup_styles"]) {
      orConditions.push({
        [field]: { $elemMatch: { $regex: escaped, $options: "i" } },
      });
    }

    // P17-P18: Fuzzy — product name contains (safe on indexed field)
    orConditions.push({
      product_name_normalized: { $regex: escaped, $options: "i" },
    });

    // Multi-word handling: split and require all tokens to be present in product name
    const tokens = searchTarget.split(/\s+/).filter((t) => t.length >= 2);
    if (tokens.length > 1) {
      const tokenFilters = tokens.map((t) => ({
        product_name_normalized: { $regex: SearchQueryNormalizer.escapeRegex(t), $options: "i" },
      }));
      orConditions.push({ $and: tokenFilters });
    }

    const combinedFilter = { ...baseFilter, $or: orConditions };

    const [total, docs] = await Promise.all([
      ProductSearchDocument.countDocuments(combinedFilter),
      ProductSearchDocument.find(combinedFilter)
        .sort(mongoSort)
        .skip(skip)
        .limit(limit)
        .lean(),
    ]);

    return {
      items: docs,
      total,
      facets: await this._buildFacets(baseFilter),
    };
  }

  /**
   * Suggestions — anchored prefix only.
   */
  static async getSuggestions({ searchTarget, limit }) {
    if (!searchTarget || searchTarget.trim() === "") {
      return { query_suggestions: [], products: [], brands: [], categories: [], shades: [], color_families: [] };
    }

    const escaped = SearchQueryNormalizer.escapeRegex(searchTarget);
    const prefixRegex = new RegExp(`^${escaped}`, "i");

    const productCap = Math.max(2, Math.floor(limit * 0.6));
    const brandCap = Math.max(1, Math.floor(limit * 0.2));
    const catCap = Math.max(1, Math.floor(limit * 0.2));

    const [rawProducts, rawBrands, rawCategories, rawShades] = await Promise.all([
      // Products — anchored prefix on name
      ProductSearchDocument.find({ is_active: true, product_name_normalized: { $regex: prefixRegex } })
        .select("product_id product_name primary_image_url price slug shade_names shade_codes")
        .sort({ is_best_seller: -1, sales_count: -1 })
        .limit(productCap)
        .lean(),

      // Brands
      ProductSearchDocument.aggregate([
        { $match: { is_active: true, brand_name_normalized: { $regex: prefixRegex } } },
        { $group: { _id: "$brand_id", brand_name: { $first: "$brand_name" } } },
        { $limit: brandCap },
      ]),

      // Categories
      ProductSearchDocument.aggregate([
        { $match: { is_active: true } },
        { $unwind: "$category_names_normalized" },
        { $match: { category_names_normalized: { $regex: prefixRegex } } },
        { $group: { _id: "$category_names_normalized", name: { $first: { $arrayElemAt: ["$category_names", 0] } } } },
        { $limit: catCap },
      ]),

      // Shades (anchored prefix on shade name)
      ProductSearchDocument.aggregate([
        { $match: { is_active: true } },
        { $unwind: { path: "$shade_names_normalized", includeArrayIndex: "idx" } },
        { $match: { shade_names_normalized: { $regex: prefixRegex } } },
        {
          $project: {
            product_id: 1,
            product_name: 1,
            shade_name: { $arrayElemAt: ["$shade_names", "$idx"] },
            shade_names_normalized: 1,
          },
        },
        { $limit: 4 },
      ]),
    ]);

    return {
      query_suggestions: rawProducts.slice(0, 5).map((p) => p.product_name),
      products: rawProducts.map((p) => ({
        id: p.product_id,
        name: p.product_name,
        imageUrl: p.primary_image_url,
        price: p.price,
        slug: p.slug,
      })),
      brands: rawBrands.map((b) => ({ id: b._id, name: b.brand_name })),
      categories: rawCategories.map((c) => ({ name: c.name || c._id })),
      shades: rawShades.map((s) => ({
        product_id: s.product_id,
        product_name: s.product_name,
        shade_name: s.shade_name,
      })),
      color_families: [],
      search_all: searchTarget,
    };
  }

  /**
   * Build a verified MongoDB filter from sanitized params.
   * Only runs on product_search_documents.
   */
  static _buildBaseFilter(params = {}) {
    const filter = { is_active: true, product_status: "active" };

    if (params.category_ids && params.category_ids.length > 0) {
      filter.category_ids = {
        $in: params.category_ids.map((id) => new mongoose.Types.ObjectId(id)),
      };
    }

    if (params.brand_ids && params.brand_ids.length > 0) {
      filter.brand_id = {
        $in: params.brand_ids.map((id) => new mongoose.Types.ObjectId(id)),
      };
    }

    if (params.min_price != null || params.max_price != null) {
      filter.price = {};
      if (params.min_price != null) filter.price.$gte = params.min_price;
      if (params.max_price != null) filter.price.$lte = params.max_price;
    }

    if (params.min_rating != null) {
      filter.average_rating = { $gte: params.min_rating };
    }

    if (params.in_stock === true) filter.in_stock = true;
    if (params.on_sale === true) filter.is_on_sale = true;
    if (params.ar_supported === true) filter.ar_supported = true;
    if (params.waterproof === true) filter.waterproof = true;
    if (params.transfer_proof === true) filter.transfer_proof = true;
    if (params.long_wear === true) filter.long_wear = true;
    if (params.smudge_proof === true) filter.smudge_proof = true;
    if (params.oil_control === true) filter.oil_control = true;
    if (params.sensitive_friendly === true) filter.sensitive_friendly = true;

    if (params.finish_types && params.finish_types.length > 0) {
      filter.finish_types = { $in: params.finish_types };
    }
    if (params.coverage_levels && params.coverage_levels.length > 0) {
      filter.coverage_levels = { $in: params.coverage_levels };
    }
    if (params.formula_types && params.formula_types.length > 0) {
      filter.formula_types = { $in: params.formula_types };
    }
    if (params.texture_types && params.texture_types.length > 0) {
      filter.texture_types = { $in: params.texture_types };
    }
    if (params.makeup_styles && params.makeup_styles.length > 0) {
      filter.makeup_styles = { $in: params.makeup_styles };
    }
    if (params.color_families && params.color_families.length > 0) {
      filter.color_families = { $in: params.color_families };
    }
    if (params.shade_codes && params.shade_codes.length > 0) {
      filter.shade_codes = { $in: params.shade_codes };
    }
    if (params.skin_tones && params.skin_tones.length > 0) {
      filter.skin_tones = { $in: params.skin_tones };
    }
    if (params.undertones && params.undertones.length > 0) {
      filter.undertones = { $in: params.undertones };
    }

    return filter;
  }

  static _buildSort(sortParam) {
    switch (sortParam) {
      case "price_asc":    return { price: 1, _id: 1 };
      case "price_desc":   return { price: -1, _id: 1 };
      case "rating":       return { average_rating: -1, _id: 1 };
      case "best_selling": return { sales_count: -1, is_best_seller: -1, _id: 1 };
      case "newest":       return { indexed_at: -1, _id: 1 };
      case "relevance":
      default:
        // In fallback mode true text scoring is unavailable.
        // We use is_best_seller + sales_count as tie-breakers, NEVER as primary rank.
        return { is_best_seller: -1, sales_count: -1, _id: 1 };
    }
  }

  /**
   * Build facet counts from existing base filter (without text query).
   * Only builds facets for fields that have data.
   */
  static async _buildFacets(baseFilter) {
    try {
      const facetPipeline = [
        { $match: baseFilter },
        {
          $facet: {
            finish_types: [
              { $unwind: "$finish_types" },
              { $group: { _id: "$finish_types", count: { $sum: 1 } } },
              { $sort: { count: -1 } },
              { $limit: 20 },
            ],
            coverage_levels: [
              { $unwind: "$coverage_levels" },
              { $group: { _id: "$coverage_levels", count: { $sum: 1 } } },
              { $sort: { count: -1 } },
            ],
            color_families: [
              { $unwind: "$color_families" },
              { $group: { _id: "$color_families", count: { $sum: 1 } } },
              { $sort: { count: -1 } },
              { $limit: 30 },
            ],
            formula_types: [
              { $unwind: "$formula_types" },
              { $group: { _id: "$formula_types", count: { $sum: 1 } } },
              { $sort: { count: -1 } },
            ],
            in_stock: [
              { $group: { _id: "$in_stock", count: { $sum: 1 } } },
            ],
            ar_supported: [
              { $match: { ar_supported: true } },
              { $count: "count" },
            ],
            is_on_sale: [
              { $match: { is_on_sale: true } },
              { $count: "count" },
            ],
          },
        },
      ];

      const [result] = await ProductSearchDocument.aggregate(facetPipeline);
      if (!result) return {};

      const toMap = (arr) => Object.fromEntries((arr || []).map((r) => [r._id, r.count]));

      return {
        finish_types:     toMap(result.finish_types),
        coverage_levels:  toMap(result.coverage_levels),
        color_families:   toMap(result.color_families),
        formula_types:    toMap(result.formula_types),
        in_stock:         toMap(result.in_stock),
        ar_supported_count: result.ar_supported?.[0]?.count || 0,
        on_sale_count:    result.is_on_sale?.[0]?.count || 0,
      };
    } catch (e) {
      // Facets are best-effort
      return {};
    }
  }
}

module.exports = FallbackSearchProvider;
