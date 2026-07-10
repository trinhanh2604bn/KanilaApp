/**
 * priceParser.js
 * Standalone utility for parsing Vietnamese price expressions.
 * No database calls. Used by chatbot product retrieval services.
 */

/**
 * Parse a raw VND amount from a numeric string with optional suffix.
 * @param {string} numStr - e.g. "200", "1.5", "300"
 * @param {string} unit   - e.g. "k", "tr", "triệu", "nghìn"
 * @returns {number}
 */
function parseAmount(numStr, unit) {
  const n = parseFloat(numStr.replace(/[,\.]/g, (m, offset, str) => {
    // Keep decimal point if it's a decimal separator (preceded by 1-2 digits), else strip thousands sep
    const after = str.slice(offset + 1);
    return /^\d{3}($|[^0-9])/.test(after) ? "" : ".";
  }));
  const u = (unit || "").toLowerCase();
  if (u === "k" || u === "k đ" || u === "kđ") return Math.round(n * 1000);
  if (u === "tr" || u === "triệu" || u === "triệu đồng") return Math.round(n * 1_000_000);
  if (u === "nghìn" || u === "ngàn") return Math.round(n * 1000);
  return Math.round(n); // assume plain VND
}

/**
 * Parse price range from a Vietnamese message.
 * @param {string} message
 * @returns {{ minPrice: number|null, maxPrice: number|null, sortHint: string|null }}
 *   sortHint: "price_asc" | "price_desc" | "rating_desc" | null
 */
function parsePriceRange(message) {
  const text = message.toLowerCase();
  let minPrice = null;
  let maxPrice = null;
  let sortHint = null;

  // ── Sort hints ────────────────────────────────────────────────────────────────
  if (/rẻ nhất|rẻ hơn|giá rẻ|giá học sinh|bình dân|tiết kiệm|affordable/.test(text)) {
    sortHint = "price_asc";
  } else if (/bán chạy|đánh giá cao|được yêu thích|best seller|phổ biến|hot nhất/.test(text)) {
    sortHint = "rating_desc";
  } else if (/đắt nhất|cao cấp|premium|luxury/.test(text)) {
    sortHint = "price_desc";
  }

  // ── Range: "từ X đến Y" / "X-Y" ──────────────────────────────────────────────
  const rangePattern = /(?:từ\s*)?([\d]+(?:[.,]\d+)?)\s*(k|tr|triệu|nghìn|ngàn)?\s*(?:đến|tới|-)\s*([\d]+(?:[.,]\d+)?)\s*(k|tr|triệu|nghìn|ngàn)?/i;
  const rangeMatch = text.match(rangePattern);
  if (rangeMatch) {
    minPrice = parseAmount(rangeMatch[1], rangeMatch[2] || rangeMatch[4]);
    maxPrice = parseAmount(rangeMatch[3], rangeMatch[4] || rangeMatch[2]);
    return { minPrice, maxPrice, sortHint };
  }

  // ── Max: "dưới / không quá / tối đa / max" ───────────────────────────────────
  const maxPattern = /(?:dưới|không quá|tối đa|max|dưới khoảng)\s*([\d]+(?:[.,]\d+)?)\s*(k|tr|triệu|nghìn|ngàn)?/i;
  const maxMatch = text.match(maxPattern);
  if (maxMatch) {
    maxPrice = parseAmount(maxMatch[1], maxMatch[2]);
  }

  // ── Min: "trên / từ / tối thiểu" ─────────────────────────────────────────────
  const minPattern = /(?:trên|từ|tối thiểu|ít nhất)\s*([\d]+(?:[.,]\d+)?)\s*(k|tr|triệu|nghìn|ngàn)?/i;
  const minMatch = text.match(minPattern);
  if (minMatch) {
    // Only treat as a minPrice if there is no max already (avoid "từ X đến Y" double parse)
    if (!rangeMatch) minPrice = parseAmount(minMatch[1], minMatch[2]);
  }

  // ── Approximate: "khoảng / tầm / tầm khoảng" → ±30% band ───────────────────
  const approxPattern = /(?:khoảng|tầm|tầm khoảng)\s*([\d]+(?:[.,]\d+)?)\s*(k|tr|triệu|nghìn|ngàn)?/i;
  const approxMatch = text.match(approxPattern);
  if (approxMatch && !maxMatch && !minMatch) {
    const center = parseAmount(approxMatch[1], approxMatch[2]);
    minPrice = Math.round(center * 0.7);
    maxPrice = Math.round(center * 1.3);
  }

  return { minPrice, maxPrice, sortHint };
}

module.exports = { parsePriceRange, parseAmount };
