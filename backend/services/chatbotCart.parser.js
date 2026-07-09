/**
 * chatbotCart.parser.js
 * Detect cart-specific intent from user messages.
 * Returns a structured cart intent object consumed by chatbot.service.js.
 *
 * Intents:
 *   cart_recommendation — user wants a combo built for them
 *   add_to_cart         — user wants to add a specific product/combo to cart
 *   cart_summary        — user wants to see their current cart
 *   remove_from_cart    — user wants to remove something (Phase 5A read-only: info only)
 *
 * Sub-signals:
 *   combo_type          — "skincare_basic" | "skincare_full" | "makeup" | "unknown"
 *   budget_max          — number | null
 */

// ─────────────────────────────────────────────────────────────────────────────
// Intent keyword maps
// ─────────────────────────────────────────────────────────────────────────────

const CART_INTENT_RULES = [
  // cart_summary — must come before add_to_cart (overlap on "giỏ hàng")
  {
    intent: "cart_summary",
    keywords: [
      "giỏ hàng của mình", "xem giỏ hàng", "giỏ hàng có gì",
      "tổng tiền giỏ", "giỏ hiện tại", "trong giỏ", "cart của mình",
      "xem giỏ", "kiểm tra giỏ", "bao nhiêu trong giỏ",
    ],
  },
  // remove_from_cart
  {
    intent: "remove_from_cart",
    keywords: [
      "xóa khỏi giỏ", "bỏ khỏi giỏ", "xóa sản phẩm", "remove from cart",
      "bỏ sản phẩm", "xóa giỏ", "không mua nữa",
    ],
  },
  // add_to_cart — explicit add action
  {
    intent: "add_to_cart",
    keywords: [
      "thêm vào giỏ", "cho vào giỏ", "thêm combo", "thêm sản phẩm",
      "mua combo", "thêm bộ này", "add to cart", "cho mình mua",
      "mua giúp mình", "thêm hết vào giỏ", "mua ngay",
      "thêm cái này", "cho thêm vào", "đặt hàng ngay",
    ],
  },
  // cart_recommendation — build a combo
  {
    intent: "cart_recommendation",
    keywords: [
      "bộ skincare", "combo skincare", "bộ chăm sóc da", "tạo combo",
      "gợi ý bộ sản phẩm", "routine đầy đủ", "cần bộ sản phẩm",
      "mua bộ", "set sản phẩm", "bộ dưỡng da", "combo cho mình",
      "bộ làm đẹp", "bộ dưỡng", "gợi ý bộ",
    ],
  },
];

// ─────────────────────────────────────────────────────────────────────────────
// Combo type signals
// ─────────────────────────────────────────────────────────────────────────────

const COMBO_TYPE_RULES = [
  {
    type: "skincare_full",
    keywords: ["đầy đủ", "full routine", "hoàn chỉnh", "toàn bộ", "đầy đủ bước"],
  },
  {
    type: "makeup",
    keywords: ["makeup", "trang điểm", "make up", "son phấn", "trang điểm nhẹ"],
  },
  {
    type: "skincare_basic",
    keywords: [], // default fallback for skincare context
  },
];

// ─────────────────────────────────────────────────────────────────────────────
// Budget parser (reuses same patterns as chatbotPreference)
// ─────────────────────────────────────────────────────────────────────────────

function parseBudgetMax(message) {
  const text = message.toLowerCase();
  const patterns = [
    { re: /(\d+[.,]?\d*)\s*(?:nghìn|ngàn)(?:đ|d|đồng)?/i, multiplier: 1000 },
    { re: /(\d+[.,]?\d*)\s*tr(?:iệu)?(?:đ|d)?/i,           multiplier: 1000000 },
    { re: /(\d+[.,]?\d*)\s*k(?:đ|d)?/i,                     multiplier: 1000 },
    { re: /(\d{2,3}[.,]\d{3})\s*(?:đ|vnd)?/i,              multiplier: 1, normalize: true },
    { re: /(\d{4,7})\s*(?:đ|vnd|đồng)/i,                   multiplier: 1 },
  ];
  for (const { re, multiplier, normalize } of patterns) {
    const m = text.match(re);
    if (m) {
      const raw = normalize
        ? parseInt(m[1].replace(/[.,]/g, ""))
        : parseFloat(m[1].replace(",", ".")) * multiplier;
      if (!isNaN(raw) && raw > 0) return Math.round(raw);
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Main parser
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parse cart-specific intent from a user message.
 *
 * @param {string} message
 * @returns {{
 *   cartIntent: "cart_recommendation"|"add_to_cart"|"cart_summary"|"remove_from_cart"|null,
 *   comboType:  "skincare_basic"|"skincare_full"|"makeup"|"unknown",
 *   budgetMax:  number|null
 * }}
 */
function parseCartIntent(message) {
  const lower = message.toLowerCase();

  let cartIntent = null;
  for (const rule of CART_INTENT_RULES) {
    if (rule.keywords.some((kw) => lower.includes(kw))) {
      cartIntent = rule.intent;
      break;
    }
  }

  let comboType = "unknown";
  if (cartIntent === "cart_recommendation") {
    const matched = COMBO_TYPE_RULES.find((r) => r.keywords.some((kw) => lower.includes(kw)));
    comboType = matched ? matched.type : "skincare_basic";
  }

  const budgetMax = parseBudgetMax(message);

  return { cartIntent, comboType, budgetMax };
}

module.exports = { parseCartIntent };
