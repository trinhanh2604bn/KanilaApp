/**
 * chatbotProductQuery.parser.js
 * Pure text parsing utilities for extracting product search constraints
 * from Vietnamese user messages. No database calls — outputs only.
 */

// ─────────────────────────────────────────────────────────────────────────────
// Budget parser
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Extract a maximum budget (VND) from user message.
 * Handles patterns: "dưới 300k", "dưới 300.000", "khoảng 500k", "500000đ", etc.
 * @param {string} message
 * @returns {number|null}
 */
function parseBudgetMax(message) {
  const text = message.toLowerCase();

  // Pattern: number followed by "k" (thousands)
  const kPattern = /(?:dưới|khoảng|tầm|trong|tối đa|max)?\s*(\d+[\.,]?\d*)\s*k(?:đ|d)?/i;
  // Pattern: number followed by "triệu" or "tr"
  const mPattern = /(?:dưới|khoảng|tầm|trong|tối đa|max)?\s*(\d+[\.,]?\d*)\s*(?:triệu|tr)(?:đ|d)?/i;
  // Pattern: number followed by "nghìn" or "ngàn"
  const nPattern = /(?:dưới|khoảng|tầm|trong|tối đa|max)?\s*(\d+[\.,]?\d*)\s*(?:nghìn|ngàn)(?:đ|d)?/i;
  // Pattern: full VND amount (e.g. 300000, 300.000, 300,000)
  const vndPattern = /(?:dưới|khoảng|tầm|trong|tối đa|max)?\s*(\d{2,3}[\.,]\d{3}(?:[\.,]\d{3})?)\s*(?:đ|vnd|vnđ|₫)?/i;
  // Pattern: plain number followed by "đồng" or "vnd"
  const plainPattern = /(?:dưới|khoảng|tầm|trong|tối đa|max)?\s*(\d{4,7})\s*(?:đ|vnd|vnđ|₫|đồng)/i;

  let match = text.match(kPattern);
  if (match) {
    const raw = parseFloat(match[1].replace(",", "."));
    return Math.round(raw * 1000);
  }

  match = text.match(mPattern);
  if (match) {
    const raw = parseFloat(match[1].replace(",", "."));
    return Math.round(raw * 1000000);
  }

  match = text.match(nPattern);
  if (match) {
    const raw = parseFloat(match[1].replace(",", "."));
    return Math.round(raw * 1000);
  }

  match = text.match(vndPattern);
  if (match) {
    const raw = match[1].replace(/[\.,]/g, "");
    return parseInt(raw, 10);
  }

  match = text.match(plainPattern);
  if (match) {
    return parseInt(match[1], 10);
  }

  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Skin type parser
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_TYPE_MAP = [
  { keywords: ["da dầu", "da nhờn", "oily"], value: "oily" },
  { keywords: ["da khô", "dry"], value: "dry" },
  { keywords: ["da nhạy cảm", "da nhạy", "sensitive", "nhạy cảm"], value: "sensitive" },
  { keywords: ["da hỗn hợp", "combination"], value: "combination" },
  { keywords: ["da thường", "da bình thường", "normal"], value: "normal" },
];

/**
 * Extract skin type from message.
 * @param {string} message
 * @returns {string|null}
 */
function parseSkinType(message) {
  const lower = message.toLowerCase();
  for (const entry of SKIN_TYPE_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return entry.value;
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Skin concern parser
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_CONCERN_MAP = [
  { keywords: ["mụn", "acne", "nổi mụn"], value: "acne" },
  { keywords: ["thâm", "dark spot", "nám", "tàn nhang"], value: "dark_spot" },
  { keywords: ["xỉn màu", "xỉn", "dullness", "sạm", "không đều màu"], value: "dullness" },
  { keywords: ["khô", "dryness", "thiếu ẩm", "mất ẩm"], value: "dryness" },
  { keywords: ["nhờn", "dầu", "oil_control", "kiểm soát dầu", "kiểm soát nhờn"], value: "oil_control" },
  { keywords: ["nhạy cảm", "kích ứng", "sensitive"], value: "sensitive" },
  { keywords: ["lão hóa", "nếp nhăn", "anti aging", "chống lão hóa"], value: "anti_aging" },
  { keywords: ["lỗ chân lông", "pore", "thu nhỏ lỗ chân lông"], value: "pore" },
];

/**
 * Extract primary skin concern from message.
 * @param {string} message
 * @returns {string|null}
 */
function parseSkinConcern(message) {
  const lower = message.toLowerCase();
  for (const entry of SKIN_CONCERN_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return entry.value;
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Category intent parser
// ─────────────────────────────────────────────────────────────────────────────

const CATEGORY_MAP = [
  {
    keywords: ["kem chống nắng", "chống nắng", "sunscreen", "kem chống nắng", "spf"],
    value: "sunscreen",
    names: ["kem chống nắng", "chống nắng", "sunscreen"],
  },
  {
    keywords: ["serum", "tinh chất"],
    value: "serum",
    names: ["serum", "tinh chất"],
  },
  {
    keywords: ["toner", "nước hoa hồng", "nước cân bằng"],
    value: "toner",
    names: ["toner", "nước hoa hồng", "nước cân bằng"],
  },
  {
    keywords: ["sữa rửa mặt", "cleanser", "rửa mặt", "gel rửa mặt", "foam rửa mặt"],
    value: "cleanser",
    names: ["sữa rửa mặt", "cleanser", "rửa mặt"],
  },
  {
    keywords: ["kem dưỡng", "moisturizer", "dưỡng ẩm", "kem dưỡng ẩm", "kem dưỡng da"],
    value: "moisturizer",
    names: ["kem dưỡng", "moisturizer", "dưỡng ẩm"],
  },
  {
    keywords: ["mặt nạ", "mask", "đắp mặt nạ"],
    value: "mask",
    names: ["mặt nạ", "mask"],
  },
  {
    keywords: ["son", "lipstick", "son môi", "son kem", "son bóng"],
    value: "lip",
    names: ["son", "lipstick", "lip"],
  },
  {
    keywords: ["kem nền", "foundation", "cushion"],
    value: "foundation",
    names: ["kem nền", "foundation", "cushion"],
  },
];

/**
 * Extract product category intent from message.
 * Returns the category slug value and keywords for DB search.
 * @param {string} message
 * @returns {{ value: string, names: string[] }|null}
 */
function parseCategoryIntent(message) {
  const lower = message.toLowerCase();
  for (const entry of CATEGORY_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return { value: entry.value, names: entry.names };
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Composite parser — parse all constraints at once
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parse all product search constraints from a user message.
 * @param {string} message
 * @returns {{
 *   budgetMax: number|null,
 *   skinType: string|null,
 *   skinConcern: string|null,
 *   categoryIntent: { value: string, names: string[] }|null,
 *   isSensitiveFriendly: boolean
 * }}
 */
function parseProductConstraints(message) {
  const lower = message.toLowerCase();
  return {
    budgetMax: parseBudgetMax(message),
    skinType: parseSkinType(message),
    skinConcern: parseSkinConcern(message),
    categoryIntent: parseCategoryIntent(message),
    isSensitiveFriendly:
      lower.includes("nhạy cảm") || lower.includes("sensitive"),
  };
}

module.exports = {
  parseBudgetMax,
  parseSkinType,
  parseSkinConcern,
  parseCategoryIntent,
  parseProductConstraints,
};
