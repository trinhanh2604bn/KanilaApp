"use strict";

const ProductSearchDocument = require("../../../models/productSearchDocument.model");
const searchConfig = require("../../../config/search.config");

/**
 * AtlasSearchProvider
 *
 * Uses the product_search_v1 Atlas Search index via MongoDB aggregation $search.
 * Falls back gracefully if the index is unavailable.
 */
class AtlasSearchProvider {
  /**
   * @param {{
   *   normalized: object,
   *   searchTarget: string,
   *   filters: object,
   *   sort: string,
   *   skip: number,
   *   limit: number,
   * }} opts
   */
  static async search({ normalized, searchTarget, filters, sort, skip, limit }) {
    const mustClauses = [];
    const filterClauses = this._buildAtlasFilters(filters);

    if (!normalized.isEmpty && searchTarget) {
      // Identifier exact matching — highest priority
      if (normalized.looksLikeBarcode) {
        mustClauses.push({
          text: { query: normalized.alphanum, path: "barcodes", score: { boost: { value: 30 } } },
        });
      } else if (normalized.looksLikeSku) {
        mustClauses.push({
          text: { query: normalized.alphanum.toUpperCase(), path: "variant_skus", score: { boost: { value: 25 } } },
        });
      } else if (normalized.looksLikeProductCode) {
        mustClauses.push({
          text: { query: normalized.alphanum, path: "product_code", score: { boost: { value: 20 } } },
        });
      } else if (normalized.looksLikeShadeCode) {
        mustClauses.push({
          text: { query: normalized.alphanum, path: "shade_codes", score: { boost: { value: 20 } } },
        });
      } else {
        // Full text search with boosted fields
        mustClauses.push({
          text: {
            query: searchTarget,
            path: [
              "product_name", "brand_name", "category_names",
              "variant_names", "shade_names",
              "product_benefits", "product_claims", "attribute_terms",
              "product_tags", "color_families",
              "finish_types", "coverage_levels",
              "formula_types", "texture_types", "makeup_styles",
            ],
            fuzzy: { maxEdits: 1, prefixLength: 2 },
            score: {
              function: {
                path: {
                  value: "product_name",
                  undefined: 0,
                },
              },
            },
          },
        });
      }
    }

    // Compound must + filter
    const compound = { filter: filterClauses };
    if (mustClauses.length > 0) {
      compound.must = mustClauses;
    }

    // Autocomplete boosting for prefix
    if (!normalized.isEmpty && searchTarget && !normalized.looksLikeBarcode && !normalized.looksLikeSku) {
      compound.should = [
        {
          autocomplete: {
            query: searchTarget,
            path: "product_name",
            score: { boost: { value: 5 } },
          },
        },
        {
          autocomplete: {
            query: searchTarget,
            path: "shade_names",
            score: { boost: { value: 4 } },
          },
        },
        {
          autocomplete: {
            query: searchTarget,
            path: "brand_name",
            score: { boost: { value: 2 } },
          },
        },
      ];
    }

    const pipeline = [
      {
        $search: {
          index: searchConfig.atlasIndexName,
          compound,
          ...(sort === "relevance" ? { sort: { score: { $meta: "searchScore" } } } : {}),
        },
      },
      {
        $addFields: {
          _searchScore: { $meta: "searchScore" },
        },
      },
      { $skip: skip },
      { $limit: limit },
    ];

    // Atlas doesn't give total with $search directly — we need $searchMeta
    const countPipeline = [
      {
        $searchMeta: {
          index: searchConfig.atlasIndexName,
          compound,
          count: { type: "total" },
        },
      },
    ];

    const [docs, countResult] = await Promise.all([
      ProductSearchDocument.aggregate(pipeline),
      ProductSearchDocument.aggregate(countPipeline),
    ]);

    const total = countResult?.[0]?.count?.total ?? docs.length;

    return {
      items: docs,
      total,
      facets: {}, // Atlas facets need separate $facet pipeline — simplified here
    };
  }

  /**
   * Atlas autocomplete suggestions.
   */
  static async getSuggestions({ searchTarget, limit }) {
    if (!searchTarget) return { query_suggestions: [], products: [], brands: [], categories: [] };

    const pipeline = [
      {
        $search: {
          index: searchConfig.atlasIndexName,
          compound: {
            should: [
              { autocomplete: { query: searchTarget, path: "product_name", score: { boost: { value: 3 } } } },
              { autocomplete: { query: searchTarget, path: "shade_names", score: { boost: { value: 2 } } } },
              { autocomplete: { query: searchTarget, path: "brand_name" } },
              { autocomplete: { query: searchTarget, path: "category_names" } },
            ],
            filter: [{ equals: { path: "is_active", value: true } }],
          },
        },
      },
      { $limit: limit },
      { $project: { product_id: 1, product_name: 1, brand_name: 1, category_names: 1, primary_image_url: 1, price: 1, slug: 1 } },
    ];

    const docs = await ProductSearchDocument.aggregate(pipeline);

    return {
      query_suggestions: [...new Set(docs.map((d) => d.product_name))].slice(0, 5),
      products: docs.slice(0, Math.ceil(limit * 0.6)).map((d) => ({
        id: d.product_id,
        name: d.product_name,
        imageUrl: d.primary_image_url,
        price: d.price,
        slug: d.slug,
      })),
      brands: [...new Map(docs.filter((d) => d.brand_name).map((d) => [d.brand_name, d])).values()]
        .slice(0, 3)
        .map((d) => ({ name: d.brand_name })),
      categories: [],
      search_all: searchTarget,
    };
  }

  static _buildAtlasFilters(filters = {}) {
    const clauses = [
      { equals: { path: "is_active", value: true } },
      { equals: { path: "product_status", value: "active" } },
    ];

    if (filters.in_stock === true) clauses.push({ equals: { path: "in_stock", value: true } });
    if (filters.on_sale === true) clauses.push({ equals: { path: "is_on_sale", value: true } });
    if (filters.ar_supported === true) clauses.push({ equals: { path: "ar_supported", value: true } });
    if (filters.waterproof === true) clauses.push({ equals: { path: "waterproof", value: true } });
    if (filters.transfer_proof === true) clauses.push({ equals: { path: "transfer_proof", value: true } });
    if (filters.long_wear === true) clauses.push({ equals: { path: "long_wear", value: true } });

    if (filters.min_price != null || filters.max_price != null) {
      const range = { path: "price", gte: filters.min_price ?? 0 };
      if (filters.max_price != null) range.lte = filters.max_price;
      clauses.push({ range });
    }

    if (filters.min_rating != null) {
      clauses.push({ range: { path: "average_rating", gte: filters.min_rating } });
    }

    return clauses;
  }
}

module.exports = AtlasSearchProvider;
