"use strict";

/**
 * SearchQueryNormalizer
 *
 * Rules:
 * - Unicode NFKC normalization.
 * - Remove unsafe control characters.
 * - Trim + collapse whitespace.
 * - Lowercase.
 * - Vietnamese diacritic folding (including đ/Đ).
 * - Preserve numbers and meaningful makeup codes.
 * - Handle apostrophes, hyphens, ampersands, plus signs safely.
 * - Cap at 120 characters.
 * - Detect product codes, SKUs, barcodes, shade codes.
 * - NEVER use raw user input as a regex pattern.
 */

// ─── Vietnamese diacritic removal ───────────────────────────────────────────
function foldVietnamese(str) {
  str = str.replace(/[àáạảãâầấậẩẫăằắặẳẵ]/g, "a");
  str = str.replace(/[èéẹẻẽêềếệểễ]/g, "e");
  str = str.replace(/[ìíịỉĩ]/g, "i");
  str = str.replace(/[òóọỏõôồốộổỗơờớợởỡ]/g, "o");
  str = str.replace(/[ùúụủũưừứựửữ]/g, "u");
  str = str.replace(/[ỳýỵỷỹ]/g, "y");
  str = str.replace(/đ/g, "d");
  str = str.replace(/[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]/g, "A");
  str = str.replace(/[ÈÉẸẺẼÊỀẾỆỂỄ]/g, "E"); // note: uppercase Ê variants
  str = str.replace(/[ÌÍỊỈĨ]/g, "I");
  str = str.replace(/[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]/g, "O");
  str = str.replace(/[ÙÚỤỦŨƯỪỨỰỬỮ]/g, "U");
  str = str.replace(/[ỲÝỴỶỸ]/g, "Y");
  str = str.replace(/Đ/g, "D");
  // NFD combining marks leftover after NFKC
  str = str.replace(/[\u0300-\u036f]/g, "");
  str = str.replace(/[\u02C6\u0306\u031B]/g, "");
  return str;
}

class SearchQueryNormalizer {
  /**
   * Main entry point. Returns a rich normalization result object.
   *
   * @param {string} query
   * @returns {{
   *   original: string,
   *   lowercase: string,
   *   folded: string,
   *   alphanum: string,
   *   looksLikeBarcode: boolean,
   *   looksLikeProductCode: boolean,
   *   looksLikeSku: boolean,
   *   looksLikeShadeCode: boolean,
   *   isEmpty: boolean
   * }}
   */
  static normalize(query) {
    if (!query || typeof query !== "string") {
      return this.emptyResult();
    }

    // 1. Cap length
    let raw = query.slice(0, 120);

    // 2. NFKC
    let nfkc = raw.normalize("NFKC");

    // 3. Remove unsafe control chars
    nfkc = nfkc.replace(/[\x00-\x1F\x7F]/g, "");

    // 4. Trim + collapse whitespace
    nfkc = nfkc.replace(/\s+/g, " ").trim();

    if (nfkc.length === 0) return this.emptyResult();

    const lowercase = nfkc.toLowerCase();

    // 5. Vietnamese diacritic folding
    const folded = foldVietnamese(lowercase);

    // 6. Alpha-numeric only (for identifier detection)
    // Preserve alphanumeric + spaces; strip punctuation for clean detection
    const alphanumOnly = folded.replace(/[^a-z0-9\s]/g, "").trim();
    const alphanumNoSpaces = alphanumOnly.replace(/\s+/g, "");

    // ─── Pattern detection (on folded, stripped) ────────────────────────────

    // Barcode: 8-14 digits
    const looksLikeBarcode = /^\d{8,14}$/.test(alphanumNoSpaces);

    // Product code: 3-40 uppercase/digit chars, no common word patterns
    const looksLikeProductCode =
      !looksLikeBarcode &&
      /^[a-z0-9][a-z0-9_\-]{2,39}$/i.test(nfkc.replace(/\s/g, "")) &&
      !/^(son|kem|phan|mascara|eyeliner|cushion|highlight|blush|foundation)$/i.test(nfkc.trim());

    // SKU: 4-20 uppercase/digit/dash
    const looksLikeSku =
      !looksLikeBarcode &&
      /^[a-z0-9][a-z0-9\-]{3,19}$/i.test(nfkc.replace(/\s/g, ""));

    // Shade code: starts with letter(s) then digits, optionally ends with letter
    // e.g. 21N, 23W, NC20, N10, 05, NW25
    const looksLikeShadeCode =
      /^(nc|nw|n|w|c|[0-9])[0-9]{1,3}[a-z]?$/i.test(alphanumNoSpaces) ||
      /^[0-9]{2}[a-z]$/i.test(alphanumNoSpaces);

    return {
      original:             nfkc,
      lowercase:            lowercase,
      folded:               folded,
      alphanum:             alphanumNoSpaces,
      looksLikeBarcode,
      looksLikeProductCode,
      looksLikeSku,
      looksLikeShadeCode,
      isEmpty: false,
    };
  }

  static emptyResult() {
    return {
      original: "",
      lowercase: "",
      folded: "",
      alphanum: "",
      looksLikeBarcode: false,
      looksLikeProductCode: false,
      looksLikeSku: false,
      looksLikeShadeCode: false,
      isEmpty: true,
    };
  }

  /**
   * Escapes all regex-special characters so user input is never
   * used as an unanchored regex pattern.
   */
  static escapeRegex(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  }
}

module.exports = SearchQueryNormalizer;
