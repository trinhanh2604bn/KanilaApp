"use strict";

/**
 * tests/unit/search/searchQueryNormalizer.test.js
 *
 * Unit tests for SearchQueryNormalizer.
 * Tests Vietnamese folding, barcode/SKU/shade code detection,
 * regex escaping, and security boundaries.
 */

const SearchQueryNormalizer = require("../../../utils/searchQueryNormalizer");

describe("SearchQueryNormalizer", () => {
  // ─── normalize() ─────────────────────────────────────────────────────────
  describe("normalize()", () => {
    test("returns emptyResult for null input", () => {
      const result = SearchQueryNormalizer.normalize(null);
      expect(result.isEmpty).toBe(true);
      expect(result.original).toBe("");
    });

    test("returns emptyResult for empty string", () => {
      const result = SearchQueryNormalizer.normalize("");
      expect(result.isEmpty).toBe(true);
    });

    test("returns emptyResult for whitespace-only string", () => {
      const result = SearchQueryNormalizer.normalize("   ");
      expect(result.isEmpty).toBe(true);
    });

    test("trims and collapses whitespace", () => {
      const result = SearchQueryNormalizer.normalize("  son   tint  ");
      expect(result.original).toBe("son tint");
    });

    test("lowercases ASCII text", () => {
      const result = SearchQueryNormalizer.normalize("Son Môi HỒNG");
      expect(result.lowercase).toBe("son môi hồng");
    });

    test("folds basic Vietnamese lowercase", () => {
      const result = SearchQueryNormalizer.normalize("son môi");
      expect(result.folded).toBe("son moi");
    });

    test("folds uppercase Vietnamese", () => {
      const result = SearchQueryNormalizer.normalize("SON MÔI");
      // After lowercase then fold
      expect(result.folded).toBe("son moi");
    });

    test("folds đ/Đ correctly", () => {
      const result = SearchQueryNormalizer.normalize("Đẹp đẽ");
      expect(result.folded).toBe("dep de");
    });

    test("folds all tonal Vietnamese letters - ơ family", () => {
      const r = SearchQueryNormalizer.normalize("ờ ớ ợ ở ỡ ơ");
      expect(r.folded.replace(/\s/g, "")).toBe("oooooo");
    });

    test("folds all tonal Vietnamese letters - ư family", () => {
      const r = SearchQueryNormalizer.normalize("ừ ứ ự ử ữ ư");
      expect(r.folded.replace(/\s/g, "")).toBe("uuuuuu");
    });

    test("folds ổ from ô family", () => {
      const r = SearchQueryNormalizer.normalize("tổng");
      expect(r.folded).toBe("tong");
    });

    test("normalizes NFKC", () => {
      // Fullwidth characters should be normalized
      const r = SearchQueryNormalizer.normalize("ｓｏｎ");
      expect(r.lowercase).toBe("son");
    });

    test("caps at 120 characters", () => {
      const long = "a".repeat(200);
      const r = SearchQueryNormalizer.normalize(long);
      expect(r.original.length).toBe(120);
    });

    test("strips control characters", () => {
      const r = SearchQueryNormalizer.normalize("son\x00\x01\x1Fmoi");
      expect(r.original).toBe("sonmoi");
    });

    test("isEmpty is false for valid input", () => {
      const r = SearchQueryNormalizer.normalize("cushion");
      expect(r.isEmpty).toBe(false);
    });
  });

  // ─── Barcode detection ─────────────────────────────────────────────────────
  describe("barcode detection", () => {
    test("detects 8-digit barcode", () => {
      const r = SearchQueryNormalizer.normalize("12345678");
      expect(r.looksLikeBarcode).toBe(true);
    });

    test("detects 13-digit EAN barcode", () => {
      const r = SearchQueryNormalizer.normalize("4987241134457");
      expect(r.looksLikeBarcode).toBe(true);
    });

    test("does not flag 7-digit as barcode", () => {
      const r = SearchQueryNormalizer.normalize("1234567");
      expect(r.looksLikeBarcode).toBe(false);
    });

    test("does not flag 15-digit as barcode", () => {
      const r = SearchQueryNormalizer.normalize("123456789012345");
      expect(r.looksLikeBarcode).toBe(false);
    });

    test("does not flag mixed alphanumeric as barcode", () => {
      const r = SearchQueryNormalizer.normalize("ABC12345678");
      expect(r.looksLikeBarcode).toBe(false);
    });
  });

  // ─── SKU detection ─────────────────────────────────────────────────────────
  describe("SKU / product code detection", () => {
    test("detects typical SKU format", () => {
      const r = SearchQueryNormalizer.normalize("SON-LI-01");
      expect(r.looksLikeSku).toBe(true);
    });

    test("does not flag common Vietnamese word as SKU", () => {
      const r = SearchQueryNormalizer.normalize("son");
      // "son" is 3 chars — 3-char single word should NOT look like sku (need 4+ chars)
      expect(r.looksLikeSku).toBe(false);
    });
  });

  // ─── Shade code detection ─────────────────────────────────────────────────
  describe("shade code detection", () => {
    test("detects NC30 shade code", () => {
      const r = SearchQueryNormalizer.normalize("NC30");
      expect(r.looksLikeShadeCode).toBe(true);
    });

    test("detects N10 shade code", () => {
      const r = SearchQueryNormalizer.normalize("N10");
      expect(r.looksLikeShadeCode).toBe(true);
    });

    test("detects NW25 shade code", () => {
      const r = SearchQueryNormalizer.normalize("NW25");
      expect(r.looksLikeShadeCode).toBe(true);
    });

    test("detects 21N shade code", () => {
      const r = SearchQueryNormalizer.normalize("21N");
      expect(r.looksLikeShadeCode).toBe(true);
    });

    test("does not flag regular word as shade code", () => {
      const r = SearchQueryNormalizer.normalize("nude");
      expect(r.looksLikeShadeCode).toBe(false);
    });
  });

  // ─── escapeRegex() ─────────────────────────────────────────────────────────
  describe("escapeRegex()", () => {
    test("escapes dot", () => {
      expect(SearchQueryNormalizer.escapeRegex("a.b")).toBe("a\\.b");
    });

    test("escapes asterisk", () => {
      expect(SearchQueryNormalizer.escapeRegex("a*b")).toBe("a\\*b");
    });

    test("escapes parentheses", () => {
      expect(SearchQueryNormalizer.escapeRegex("a(b)")).toBe("a\\(b\\)");
    });

    test("escapes square brackets and hyphen", () => {
      const escaped = SearchQueryNormalizer.escapeRegex("[0-9]");
      // Square brackets and hyphen should all be escaped
      expect(escaped).toContain("\\[");
      expect(escaped).toContain("\\]");
      // Confirm it works safely as a regex literal match
      const re = new RegExp(escaped);
      expect(re.test("[0-9]")).toBe(true);
      expect(re.test("09")).toBe(false);
    });

    test("does not escape normal alphanumeric", () => {
      expect(SearchQueryNormalizer.escapeRegex("abc123")).toBe("abc123");
    });

    test("handles user injection attempt", () => {
      const evil = ".*evil.*";
      const escaped = SearchQueryNormalizer.escapeRegex(evil);
      expect(escaped).toBe("\\.\\*evil\\.\\*");
      // Confirm the escaped string works safely in a RegExp constructor
      expect(() => new RegExp(escaped)).not.toThrow();
    });

    test("escapes full regex injection pattern", () => {
      const injection = "^(a|b)+$";
      const escaped = SearchQueryNormalizer.escapeRegex(injection);
      const re = new RegExp(escaped);
      expect(re.test("^(a|b)+$")).toBe(true);
      expect(re.test("aab")).toBe(false);
    });
  });

  // ─── Multi-character normalization ─────────────────────────────────────────
  describe("specific makeup query normalization", () => {
    const cases = [
      ["son môi", "son moi"],
      ["phấn phủ", "phan phu"],
      ["kem che khuyết điểm", "kem che khuyet diem"],
      ["kẻ mắt dạng bút", "ke mat dang but"],
      ["trang điểm", "trang diem"],
      ["phấn má hồng", "phan ma hong"],
      ["mascara chống thấm", "mascara chong tham"],
    ];

    test.each(cases)("normalize('%s') → '%s'", (input, expected) => {
      const r = SearchQueryNormalizer.normalize(input);
      expect(r.folded).toBe(expected);
    });
  });
});
