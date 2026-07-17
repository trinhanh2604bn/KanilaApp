"use strict";

const SearchQueryNormalizer = require("../utils/searchQueryNormalizer");
const SearchAliasService   = require("./search/searchAlias.service");
const FallbackSearchProvider = require("./search/providers/fallbackSearch.provider");
const AtlasSearchProvider    = require("./search/providers/atlasSearch.provider");
const ProductSearchDocument  = require("../models/productSearchDocument.model");
const SearchEvent            = require("../models/searchEvent.model");
const searchConfig           = require("../config/search.config");
const { v4: uuidv4 }         = require("crypto").randomUUID
  ? { v4: () => require("crypto").randomBytes(16).toString("hex") }
  : { v4: () => Math.random().toString(36).slice(2) };

/**
 * SearchService
 *
 * Public surface for all search operations.
 * Delegates to Atlas or Fallback depending on configuration.
 * Converts ProductSearchDocument → Product-shaped response objects.
 */
class SearchService {
  /**
   * Primary product search.
   */
  static async searchProducts(rawQuery, sanitizedParams) {
    const queryId = uuidv4();
    const start   = Date.now();

    // ─── Normalize ───────────────────────────────────────────────────────────
    const normalized = SearchQueryNormalizer.normalize(rawQuery);

    // ─── Alias resolution ────────────────────────────────────────────────────
    let searchTarget = normalized.folded;
    let resolvedAlias = null;
    if (!normalized.isEmpty && !normalized.looksLikeBarcode && !normalized.looksLikeSku) {
      resolvedAlias = await SearchAliasService.resolveAlias(normalized.folded);
      if (resolvedAlias) {
        searchTarget = resolvedAlias.canonical_normalized;
      }
    }

    // ─── Pagination ──────────────────────────────────────────────────────────
    const page   = sanitizedParams.page   || 1;
    const limit  = sanitizedParams.limit  || searchConfig.defaultLimit;
    const sort   = sanitizedParams.sort   || "relevance";
    const skip   = (page - 1) * limit;

    // ─── Provider selection ──────────────────────────────────────────────────
    let result;
    const provider = searchConfig.provider;
    const useAtlas = provider === "atlas";

    if (useAtlas) {
      try {
        result = await AtlasSearchProvider.search({
          normalized,
          searchTarget,
          filters: sanitizedParams,
          sort,
          skip,
          limit,
        });
      } catch (atlasError) {
        if (!searchConfig.fallbackEnabled) throw atlasError;
        result = await FallbackSearchProvider.search({
          normalized,
          searchTarget,
          filters: sanitizedParams,
          sort,
          skip,
          limit,
        });
      }
    } else {
      result = await FallbackSearchProvider.search({
        normalized,
        searchTarget,
        filters: sanitizedParams,
        sort,
        skip,
        limit,
      });
    }

    // ─── Map documents → product-shaped response ─────────────────────────────
    const items = (result.items || []).map(this._mapDocToProduct);
    const total  = result.total || items.length;
    const totalPages = Math.ceil(total / limit);

    // ─── Log event (fire-and-forget) ─────────────────────────────────────────
    SearchEvent.create({
      query_id:         queryId,
      event_type:       "SEARCH_SUBMITTED",
      query:            normalized.original,
      normalized_query: normalized.folded,
      result_count:     total,
      latency_ms:       Date.now() - start,
    }).catch(() => {});

    return {
      query_id:         queryId,
      query:            normalized.original,
      normalized_query: normalized.folded,
      corrected_query:  resolvedAlias ? resolvedAlias.canonical_term : null,
      items,
      pagination: {
        page,
        limit,
        total,
        total_pages: totalPages,
        has_more:    page < totalPages,
      },
      facets: result.facets || {},
    };
  }

  /**
   * Autocomplete suggestions.
   */
  static async getSuggestions(rawQuery, sanitizedParams) {
    const normalized = SearchQueryNormalizer.normalize(rawQuery);

    if (normalized.isEmpty || normalized.folded.length < searchConfig.autocompletMinChars) {
      return {
        query_suggestions: [],
        products:  [],
        brands:    [],
        categories: [],
        shades:    [],
        color_families: [],
      };
    }

    // Alias resolve for suggestions too
    let searchTarget = normalized.folded;
    const alias = await SearchAliasService.resolveAlias(normalized.folded);
    if (alias) searchTarget = alias.canonical_normalized;

    const limit = sanitizedParams.limit || 10;
    const provider = searchConfig.provider;

    if (provider === "atlas") {
      try {
        return await AtlasSearchProvider.getSuggestions({ searchTarget, limit });
      } catch {
        return await FallbackSearchProvider.getSuggestions({ searchTarget, limit });
      }
    }

    return await FallbackSearchProvider.getSuggestions({ searchTarget, limit });
  }

  /**
   * Barcode / QR / SKU scan search.
   * Exact lookup only — no fuzzy.
   */
  static async scanSearch(rawValue) {
    const value = (rawValue || "").trim();
    if (!value) return { items: [], total: 0 };

    // Try barcode exact match first
    let doc = await ProductSearchDocument.findOne({
      is_active: true,
      $or: [
        { barcodes: value },
        { variant_skus: value.toUpperCase() },
        { product_code: value.toUpperCase() },
      ],
    }).lean();

    // Try shade code
    if (!doc) {
      doc = await ProductSearchDocument.findOne({
        is_active: true,
        shade_codes: { $regex: `^${SearchQueryNormalizer.escapeRegex(value)}$`, $options: "i" },
      }).lean();
    }

    if (!doc) return { items: [], total: 0, scan_value: value };

    return {
      items: [this._mapDocToProduct(doc)],
      total: 1,
      scan_value: value,
    };
  }

  /**
   * Discovery — trending / featured products shown on empty search screen.
   * Only returns makeup products. No skincare.
   */
  static async getDiscovery() {
    const docs = await ProductSearchDocument.find({
      is_active: true,
      product_status: "active",
    })
      .sort({ sales_count: -1, average_rating: -1, is_best_seller: -1 })
      .limit(12)
      .lean();

    return docs.map(this._mapDocToProduct);
  }

  /**
   * Record a client-side search event (clicks, adds, etc.).
   */
  static async recordEvent(sanitized, identifiers) {
    const normalized = SearchQueryNormalizer.normalize(sanitized.query || "");

    await SearchEvent.create({
      ...sanitized,
      normalized_query: normalized.folded,
      customer_id:      identifiers.customerId || null,
      guest_session_id: identifiers.guestSessionId || null,
    });
  }

  /**
   * Map a ProductSearchDocument lean object to a Product-shaped response.
   */
  static _mapDocToProduct(doc) {
    return {
      _id:             doc.product_id,
      productName:     doc.product_name,
      productCode:     doc.product_code,
      slug:            doc.slug,
      brandName:       doc.brand_name,
      brandId:         doc.brand_id,
      categoryId:      doc.category_ids?.[0] || null,
      price:           doc.price,
      compareAtPrice:  doc.compare_at_price,
      imageUrl:        doc.primary_image_url,
      averageRating:   doc.average_rating,
      reviewCount:     String(doc.review_count),
      bought:          doc.sales_count,
      stock:           doc.stock,
      isActive:        doc.is_active,
      productStatus:   doc.product_status,
      is_best_seller:  doc.is_best_seller,
      is_new:          doc.is_new,
      is_on_sale:      doc.is_on_sale,
      hasAr:           doc.ar_supported,
      shades: (doc.shade_names || []).map((name, i) => ({
        name,
        hex: doc.color_hex_values?.[i] || null,
        code: doc.shade_codes?.[i] || null,
      })),
      _searchScore:    doc._searchScore || null,
    };
  }
}

module.exports = SearchService;
