"use strict";

/**
 * tests/unit/search/fallbackSearchProvider.test.js
 *
 * Unit tests for FallbackSearchProvider — specifically _buildBaseFilter()
 * and _buildSort() since DB operations require a live connection.
 */

const FallbackSearchProvider = require("../../../services/search/providers/fallbackSearch.provider");

describe("FallbackSearchProvider", () => {
  // ─── _buildSort ─────────────────────────────────────────────────────────
  describe("_buildSort()", () => {
    test("returns price ascending sort", () => {
      const sort = FallbackSearchProvider._buildSort("price_asc");
      expect(sort.price).toBe(1);
    });

    test("returns price descending sort", () => {
      const sort = FallbackSearchProvider._buildSort("price_desc");
      expect(sort.price).toBe(-1);
    });

    test("returns rating sort", () => {
      const sort = FallbackSearchProvider._buildSort("rating");
      expect(sort.average_rating).toBe(-1);
    });

    test("returns best_selling sort", () => {
      const sort = FallbackSearchProvider._buildSort("best_selling");
      expect(sort.sales_count).toBe(-1);
      expect(sort.is_best_seller).toBe(-1);
    });

    test("returns newest sort", () => {
      const sort = FallbackSearchProvider._buildSort("newest");
      expect(sort.indexed_at).toBe(-1);
    });

    test("defaults to relevance sort for unknown sort", () => {
      const sort = FallbackSearchProvider._buildSort("unknown");
      // Relevance sort uses sales_count and is_best_seller as tie-breakers
      expect(sort.is_best_seller).toBe(-1);
    });

    test("defaults to relevance sort for empty string", () => {
      const sort = FallbackSearchProvider._buildSort("");
      expect(sort.is_best_seller).toBe(-1);
    });

    test("CRITICAL: relevance sort NEVER primary-ranks by popularity alone", () => {
      const sort = FallbackSearchProvider._buildSort("relevance");
      // The primary key is is_best_seller, which is secondary to the actual $or query matches
      // This test confirms popularity is only a tie-breaker, not the primary sort key
      const keys = Object.keys(sort);
      expect(keys[0]).not.toBe("sales_count"); // sales_count must not be the primary key alone
    });
  });

  // ─── _buildBaseFilter ───────────────────────────────────────────────────
  describe("_buildBaseFilter()", () => {
    test("always includes is_active and product_status", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({});
      expect(filter.is_active).toBe(true);
      expect(filter.product_status).toBe("active");
    });

    test("adds in_stock filter when requested", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ in_stock: true });
      expect(filter.in_stock).toBe(true);
    });

    test("adds on_sale filter when requested", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ on_sale: true });
      expect(filter.is_on_sale).toBe(true);
    });

    test("adds ar_supported filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ ar_supported: true });
      expect(filter.ar_supported).toBe(true);
    });

    test("adds waterproof filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ waterproof: true });
      expect(filter.waterproof).toBe(true);
    });

    test("adds transfer_proof filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ transfer_proof: true });
      expect(filter.transfer_proof).toBe(true);
    });

    test("adds price range filter with min and max", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ min_price: 100, max_price: 500 });
      expect(filter.price.$gte).toBe(100);
      expect(filter.price.$lte).toBe(500);
    });

    test("adds price range filter with min only", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ min_price: 100 });
      expect(filter.price.$gte).toBe(100);
      expect(filter.price.$lte).toBeUndefined();
    });

    test("adds min_rating filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ min_rating: 4 });
      expect(filter.average_rating.$gte).toBe(4);
    });

    test("adds finish_types filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ finish_types: ["matte", "glossy"] });
      expect(filter.finish_types.$in).toEqual(["matte", "glossy"]);
    });

    test("adds coverage_levels filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ coverage_levels: ["full"] });
      expect(filter.coverage_levels.$in).toEqual(["full"]);
    });

    test("adds color_families filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ color_families: ["đỏ", "hồng"] });
      expect(filter.color_families.$in).toEqual(["đỏ", "hồng"]);
    });

    test("adds shade_codes filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ shade_codes: ["NC30"] });
      expect(filter.shade_codes.$in).toEqual(["NC30"]);
    });

    test("adds makeup_styles filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ makeup_styles: ["natural"] });
      expect(filter.makeup_styles.$in).toEqual(["natural"]);
    });

    test("adds skin_tones filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ skin_tones: ["medium"] });
      expect(filter.skin_tones.$in).toEqual(["medium"]);
    });

    test("adds undertones filter", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ undertones: ["warm"] });
      expect(filter.undertones.$in).toEqual(["warm"]);
    });

    test("converts category_ids to ObjectIds", () => {
      const mongoose = require("mongoose");
      const filter = FallbackSearchProvider._buildBaseFilter({
        category_ids: ["507f1f77bcf86cd799439011"],
      });
      expect(filter.category_ids.$in[0]).toBeInstanceOf(mongoose.Types.ObjectId);
    });

    test("does not add in_stock if not explicitly true", () => {
      const filter = FallbackSearchProvider._buildBaseFilter({ in_stock: false });
      expect(filter.in_stock).toBeUndefined();
    });

    test("handles empty params gracefully", () => {
      const filter = FallbackSearchProvider._buildBaseFilter(undefined);
      expect(filter.is_active).toBe(true);
    });
  });
});
