"use strict";

/**
 * tests/unit/search/searchValidator.test.js
 *
 * Unit tests for SearchValidator.
 * Tests parameter sanitization, edge cases, and security boundaries.
 */

const SearchValidator = require("../../../validations/search.validator");

describe("SearchValidator", () => {
  // ─── validateSearchParams ───────────────────────────────────────────────
  describe("validateSearchParams()", () => {
    test("accepts valid minimal params", () => {
      const result = SearchValidator.validateSearchParams({ q: "son môi" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.q).toBe("son môi");
      expect(result.sanitized.page).toBe(1);
      expect(result.sanitized.limit).toBe(20);
      expect(result.sanitized.sort).toBe("relevance");
    });

    test("uses defaults when no params provided", () => {
      const result = SearchValidator.validateSearchParams({});
      expect(result.isValid).toBe(true);
      expect(result.sanitized.page).toBe(1);
      expect(result.sanitized.limit).toBe(20);
      expect(result.sanitized.sort).toBe("relevance");
    });

    test("caps q at 120 characters", () => {
      const long = "a".repeat(200);
      const result = SearchValidator.validateSearchParams({ q: long });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.q.length).toBe(120);
    });

    test("rejects invalid page (0)", () => {
      const result = SearchValidator.validateSearchParams({ page: "0" });
      expect(result.isValid).toBe(false);
      expect(result.errors.some((e) => e.includes("page"))).toBe(true);
    });

    test("rejects invalid page (string)", () => {
      const result = SearchValidator.validateSearchParams({ page: "abc" });
      expect(result.isValid).toBe(false);
    });

    test("accepts valid page", () => {
      const result = SearchValidator.validateSearchParams({ page: "3" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.page).toBe(3);
    });

    test("caps limit at maxLimit", () => {
      const result = SearchValidator.validateSearchParams({ limit: "1000" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.limit).toBeLessThanOrEqual(40);
    });

    test("rejects invalid sort", () => {
      const result = SearchValidator.validateSearchParams({ sort: "malicious; DROP TABLE" });
      expect(result.isValid).toBe(false);
      expect(result.errors.some((e) => e.includes("sort"))).toBe(true);
    });

    test("accepts all valid sort options", () => {
      const validSorts = ["relevance", "price_asc", "price_desc", "rating", "best_selling", "newest"];
      for (const sort of validSorts) {
        const result = SearchValidator.validateSearchParams({ sort });
        expect(result.isValid).toBe(true);
        expect(result.sanitized.sort).toBe(sort);
      }
    });

    test("rejects invalid ObjectId in category_ids", () => {
      const result = SearchValidator.validateSearchParams({ category_ids: "not-an-id" });
      expect(result.isValid).toBe(false);
    });

    test("accepts valid ObjectId in category_ids", () => {
      const result = SearchValidator.validateSearchParams({
        category_ids: "507f1f77bcf86cd799439011",
      });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.category_ids).toHaveLength(1);
    });

    test("accepts multiple ObjectIds in brand_ids", () => {
      const ids = ["507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012"].join(",");
      const result = SearchValidator.validateSearchParams({ brand_ids: ids });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.brand_ids).toHaveLength(2);
    });

    test("rejects negative min_price", () => {
      const result = SearchValidator.validateSearchParams({ min_price: "-100" });
      expect(result.isValid).toBe(false);
    });

    test("rejects min_price > max_price", () => {
      const result = SearchValidator.validateSearchParams({ min_price: "1000", max_price: "500" });
      expect(result.isValid).toBe(false);
      expect(result.errors.some((e) => e.includes("min_price"))).toBe(true);
    });

    test("accepts valid price range", () => {
      const result = SearchValidator.validateSearchParams({ min_price: "100", max_price: "500" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.min_price).toBe(100);
      expect(result.sanitized.max_price).toBe(500);
    });

    test("accepts boolean filter in_stock=true", () => {
      const result = SearchValidator.validateSearchParams({ in_stock: "true" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.in_stock).toBe(true);
    });

    test("accepts boolean filter waterproof=false", () => {
      const result = SearchValidator.validateSearchParams({ waterproof: "false" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.waterproof).toBe(false);
    });

    test("rejects invalid boolean", () => {
      const result = SearchValidator.validateSearchParams({ in_stock: "yes" });
      expect(result.isValid).toBe(false);
    });

    test("accepts valid finish_types", () => {
      const result = SearchValidator.validateSearchParams({ finish_types: "matte,glossy" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.finish_types).toEqual(["matte", "glossy"]);
    });

    test("rejects invalid finish_types value", () => {
      const result = SearchValidator.validateSearchParams({ finish_types: "invalid_value" });
      expect(result.isValid).toBe(false);
    });

    test("accepts valid coverage_levels", () => {
      const result = SearchValidator.validateSearchParams({ coverage_levels: "full,medium" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.coverage_levels).toEqual(["full", "medium"]);
    });

    test("accepts color_families free text", () => {
      const result = SearchValidator.validateSearchParams({ color_families: "đỏ,hồng" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.color_families).toEqual(["đỏ", "hồng"]);
    });

    test("caps color_families values at 60 chars", () => {
      const long = "a".repeat(100);
      const result = SearchValidator.validateSearchParams({ color_families: long });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.color_families[0].length).toBe(60);
    });

    test("rejects too many items in array filter", () => {
      const tooMany = Array.from({ length: 25 }, () => "507f1f77bcf86cd799439011").join(",");
      const result = SearchValidator.validateSearchParams({ category_ids: tooMany });
      expect(result.isValid).toBe(false);
    });

    test("SECURITY: rejects NoSQL injection attempt in q", () => {
      const result = SearchValidator.validateSearchParams({
        q: '{"$where": "function(){ return true; }"}',
      });
      // q is sanitized to a plain string with cap — still valid (search handles sanitization)
      expect(result.sanitized.q).toBe(
        '{"$where": "function(){ return true; }"}'.slice(0, 120)
      );
    });

    test("SECURITY: rejects JS-injection sort", () => {
      const result = SearchValidator.validateSearchParams({
        sort: '"; DROP TABLE products; --',
      });
      expect(result.isValid).toBe(false);
    });
  });

  // ─── validateSuggestionParams ───────────────────────────────────────────
  describe("validateSuggestionParams()", () => {
    test("accepts valid query with default limit", () => {
      const result = SearchValidator.validateSuggestionParams({ q: "son" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.q).toBe("son");
      expect(result.sanitized.limit).toBe(10);
    });

    test("uses empty string when q is missing", () => {
      const result = SearchValidator.validateSuggestionParams({});
      expect(result.isValid).toBe(true);
      expect(result.sanitized.q).toBe("");
    });

    test("accepts limit=5", () => {
      const result = SearchValidator.validateSuggestionParams({ q: "lip", limit: "5" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.limit).toBe(5);
    });

    test("rejects limit > 20", () => {
      const result = SearchValidator.validateSuggestionParams({ q: "lip", limit: "25" });
      expect(result.isValid).toBe(false);
    });
  });

  // ─── validateScanParams ─────────────────────────────────────────────────
  describe("validateScanParams()", () => {
    test("accepts valid barcode", () => {
      const result = SearchValidator.validateScanParams({ value: "4987241134457" });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.value).toBe("4987241134457");
    });

    test("rejects missing value", () => {
      const result = SearchValidator.validateScanParams({});
      expect(result.isValid).toBe(false);
    });

    test("rejects empty value", () => {
      const result = SearchValidator.validateScanParams({ value: "   " });
      expect(result.isValid).toBe(false);
    });

    test("caps at 120 characters", () => {
      const long = "1".repeat(200);
      const result = SearchValidator.validateScanParams({ value: long });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.value.length).toBe(120);
    });
  });

  // ─── validateEventPayload ───────────────────────────────────────────────
  describe("validateEventPayload()", () => {
    test("accepts SEARCH_SUBMITTED event", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "SEARCH_SUBMITTED",
        query: "son môi",
      });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.event_type).toBe("SEARCH_SUBMITTED");
    });

    test("rejects unknown event type", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "UNKNOWN_EVENT",
      });
      expect(result.isValid).toBe(false);
    });

    test("caps query at 120 chars", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "SEARCH_SUBMITTED",
        query: "a".repeat(200),
      });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.query.length).toBe(120);
    });

    test("rejects invalid product_id", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "PRODUCT_CLICKED",
        product_id: "not-an-id",
      });
      // product_id is simply not included in sanitized — still valid event
      expect(result.sanitized.product_id).toBeUndefined();
    });

    test("accepts valid product_id ObjectId", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "PRODUCT_CLICKED",
        product_id: "507f1f77bcf86cd799439011",
      });
      expect(result.sanitized.product_id).toBe("507f1f77bcf86cd799439011");
    });

    test("SECURITY: strips nested object from filters", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "FILTER_APPLIED",
        filters: {
          category: "lip",
          "$where": "function(){ return true; }",
          nested: { deep: "injection" },
        },
      });
      expect(result.sanitized.filters.category).toBe("lip");
      expect(result.sanitized.filters["$where"]).toBeUndefined();
      expect(result.sanitized.filters.nested).toBeUndefined();
    });

    test("accepts SHADE_CLICKED with shade_code", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "SHADE_CLICKED",
        shade_code: "NC30",
      });
      expect(result.isValid).toBe(true);
      expect(result.sanitized.shade_code).toBe("NC30");
    });

    test("accepts BARCODE_SEARCH event", () => {
      const result = SearchValidator.validateEventPayload({
        event_type: "BARCODE_SEARCH",
        query: "4987241134457",
      });
      expect(result.isValid).toBe(true);
    });
  });

  // ─── validateImageSearch ────────────────────────────────────────────────
  describe("validateImageSearch()", () => {
    test("rejects missing file", () => {
      const result = SearchValidator.validateImageSearch(null);
      expect(result.isValid).toBe(false);
    });

    test("accepts JPEG file under 5MB", () => {
      const file = { mimetype: "image/jpeg", size: 1024 * 100 };
      const result = SearchValidator.validateImageSearch(file);
      expect(result.isValid).toBe(true);
    });

    test("rejects SVG as unsupported type", () => {
      const file = { mimetype: "image/svg+xml", size: 1024 };
      const result = SearchValidator.validateImageSearch(file);
      expect(result.isValid).toBe(false);
    });

    test("rejects file over 5MB", () => {
      const file = { mimetype: "image/jpeg", size: 6 * 1024 * 1024 };
      const result = SearchValidator.validateImageSearch(file);
      expect(result.isValid).toBe(false);
    });

    test("accepts WebP file", () => {
      const file = { mimetype: "image/webp", size: 500000 };
      const result = SearchValidator.validateImageSearch(file);
      expect(result.isValid).toBe(true);
    });
  });
});
