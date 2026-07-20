/**
 * chatbotIntent.classifier.js
 * Stage 1 of the Advanced Recommendation Pipeline.
 *
 * Classifies a Vietnamese user message into one of 12 structured shopping intents
 * plus a confidence score (0–100). No DB calls — pure rule-based.
 *
 * Output: { intent, confidence, signals, needsClarification }
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

// ─────────────────────────────────────────────────────────────────────────────
// Intent definitions — ordered by specificity (most specific first)
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_DEFINITIONS = [
  // ── Voucher / promotion ────────────────────────────────────────────────────
  {
    intent: "find_voucher",
    weight: 100,
    required: [],
    keywords: [
      "voucher", "mã giảm", "mã giảm giá", "coupon", "discount",
      "freeship", "có mã không", "flash sale", "deal",
    ],
    boosts: ["ưu đãi", "khuyến mãi", "giảm giá"],
  },

  // ── Sale products ──────────────────────────────────────────────────────────
  {
    intent: "find_sale_product",
    weight: 95,
    required: [],
    keywords: ["đang sale", "sale", "đang giảm", "đang khuyến mãi", "giảm giá"],
    boosts: ["hôm nay", "tuần này", "tháng này"],
  },

  // ── Best seller ───────────────────────────────────────────────────────────
  {
    intent: "find_best_seller",
    weight: 90,
    required: [],
    keywords: [
      "bán chạy", "best seller", "bestseller", "phổ biến nhất",
      "được yêu thích nhất", "hot nhất", "nhiều người mua",
    ],
    boosts: ["top", "xếp hạng"],
  },

  // ── Product comparison ─────────────────────────────────────────────────────
  {
    intent: "compare_products",
    weight: 90,
    required: ["so sánh"],
    keywords: ["so sánh", "hay", "tốt hơn", "nên chọn cái nào", "khác nhau"],
    boosts: ["giữa", "với", "và"],
  },

  // ── Product information ────────────────────────────────────────────────────
  {
    intent: "product_information",
    weight: 85,
    required: [],
    keywords: [
      "sản phẩm này", "cái này có", "có chứa", "thành phần",
      "công dụng", "cách dùng", "hướng dẫn sử dụng",
      "có tốt không", "review", "đánh giá sản phẩm",
    ],
    boosts: ["cho biết", "giải thích", "nói về"],
  },

  // ── Makeup set / bundle ────────────────────────────────────────────────────
  {
    intent: "find_makeup_set",
    weight: 88,
    required: [],
    keywords: [
      "set makeup", "bộ makeup", "combo makeup", "bộ trang điểm",
      "combo trang điểm", "mua cả bộ", "cần gì để trang điểm",
      "bắt đầu makeup", "makeup kit", "bộ đồ makeup",
      "routine trang điểm",
    ],
    boosts: ["đầy đủ", "hoàn chỉnh", "từ a-z"],
  },

  // ── Event-based ────────────────────────────────────────────────────────────
  {
    intent: "find_by_event",
    weight: 85,
    required: [],
    keywords: [
      "đi tiệc", "đám cưới", "dạ tiệc", "prom", "gala",
      "đi học", "đi làm", "công sở", "văn phòng",
      "hẹn hò", "hằng ngày", "everyday", "hàng ngày",
      "dịp đặc biệt", "lễ tết", "sinh nhật",
    ],
    boosts: ["makeup", "trang điểm", "look"],
  },

  // ── Skin tone / shade ──────────────────────────────────────────────────────
  {
    intent: "find_by_skin_tone",
    weight: 82,
    required: [],
    keywords: [
      "da ngăm", "da sáng", "da trắng", "warm tone", "cool tone",
      "skin_undertone", "tông da", "tìm màu", "shade phù hợp",
      "chọn màu", "màu nào phù hợp", "tông nào",
      "da ngăm đen", "da vàng", "da olive",
    ],
    boosts: ["son", "cushion", "nền", "kem"],
  },

  // ── Makeup style ───────────────────────────────────────────────────────────
  {
    intent: "find_by_makeup_style",
    weight: 80,
    required: [],
    keywords: [
      "matte", "glowy", "dewy", "natural look", "glass skin",
      "no makeup makeup", "soft glam", "clean girl",
      "tự nhiên", "look nhẹ", "kiểu hàn", "ulzzang",
      "bold lip", "smoky",
    ],
    boosts: ["muốn", "thích", "style", "phong cách"],
  },

  // ── Skin condition ─────────────────────────────────────────────────────────
  {
    intent: "find_by_skin_condition",
    weight: 78,
    required: [],
    keywords: [
      "da dầu", "da nhờn", "da khô", "da mụn", "da nhạy cảm",
      "da hỗn hợp", "da thường", "da bóng", "kiềm dầu",
      "mụn trứng cá", "da kích ứng",
    ],
    boosts: ["phù hợp", "dùng được không", "có dùng được"],
  },

  // ── Budget ─────────────────────────────────────────────────────────────────
  {
    intent: "find_by_budget",
    weight: 75,
    required: [],
    keywords: [
      "dưới", "khoảng", "tầm", "không quá", "giá rẻ",
      "bình dân", "học sinh", "tiết kiệm", "budget",
      "tầm giá", "giá khoảng", "rẻ nhất",
    ],
    boosts: ["k", "nghìn", "triệu", "đồng", "vnđ"],
  },

  // ── Specific makeup category: Cushion / Foundation ────────────────────────
  {
    intent: "cushion_foundation_recommendation",
    weight: 95,
    required: [],
    keywords: [
      "cushion", "phấn nước", "phấn nền",
      "kem nền", "foundation", "liquid foundation",
      "tìm nền", "muốn nền", "cần nền", "gợi ý nền",
    ],
    boosts: ["da dầu", "da khô", "che phủ", "tự nhiên", "matte", "glowy", "dewy"],
  },

  // ── Specific makeup category: Lipstick / Lip tint ─────────────────────────
  {
    intent: "lipstick_recommendation",
    weight: 95,
    required: [],
    keywords: [
      "son tint", "tint môi", "lip tint", "son nước",
      "son thỏi", "lipstick", "son lì", "son nhung", "son mờ",
      "son môi", "son bóng", "son kem",
    ],
    boosts: ["màu", "tone", "bền", "lâu trôi", "mlbb", "nude", "đỏ"],
  },

  // ── Specific makeup category: Eye makeup ───────────────────────────────────
  {
    intent: "eye_makeup_recommendation",
    weight: 93,
    required: [],
    keywords: [
      "mascara", "kẻ mắt", "eyeliner", "phấn mắt", "eyeshadow",
      "bảng màu mắt", "chuốt mi", "bút kẻ mắt", "liner mắt",
    ],
    boosts: ["dày mi", "dài mi", "smoky", "chống lem", "waterproof"],
  },

  // ── Specific makeup category: Blush ────────────────────────────────────────
  {
    intent: "blush_recommendation",
    weight: 93,
    required: [],
    keywords: [
      "má hồng", "blush", "phấn má", "má đào",
      "blush on", "tạo khối má",
    ],
    boosts: ["hồng", "cam", "san hô", "tự nhiên"],
  },

  // ── Specific makeup category: Concealer ────────────────────────────────────
  {
    intent: "concealer_recommendation",
    weight: 93,
    required: [],
    keywords: [
      "che khuyết điểm", "concealer", "che mụn", "ẩn vết thâm",
      "che thâm", "che quầng mắt",
    ],
    boosts: ["da mụn", "thâm", "quầng mắt", "che phủ cao"],
  },

  // ── Specific makeup category: Base makeup (primer/powder/setting) ──────────
  {
    intent: "base_makeup_recommendation",
    weight: 91,
    required: [],
    keywords: [
      "phấn phủ", "powder", "loose powder", "pressed powder",
      "kem lót", "primer", "lót nền",
      "xịt khóa nền", "setting spray", "xịt cố định",
    ],
    boosts: ["bền", "kiềm dầu", "matte", "lâu trôi", "khóa nền"],
  },

  // ── Generic product find ───────────────────────────────────────────────────
  {
    intent: "find_product",
    weight: 50, // lowest — catch-all
    required: [],
    keywords: [
      "tìm", "gợi ý", "recommend", "tư vấn", "muốn mua",
      "cần", "cho mình", "loại nào", "cái nào",
      "trang điểm", "makeup", "mỹ phẩm",
    ],
    boosts: ["tốt", "chất lượng", "đẹp"],
  },
];

// ─────────────────────────────────────────────────────────────────────────────
// Clarification prompts per weak-signal intent
// ─────────────────────────────────────────────────────────────────────────────

const CLARIFICATION_PROMPTS = {
  // Specific makeup category intents — only ask for clarifying detail, NOT category again
  cushion_foundation_recommendation: {
    text: "Mình sẽ gợi ý ngay! Bạn có thể cho mình biết thêm không?",
    quickReplies: ["Da dầu", "Da khô", "Dưới 300k", "300k-600k", "Trên 600k"],
  },
  lipstick_recommendation: {
    text: "Mình sẽ gợi ý ngay! Bạn thích loại son nào?",
    quickReplies: ["Son tint", "Son thỏi", "Son bóng", "Son lì", "Tint môi"],
  },
  eye_makeup_recommendation: {
    text: "Mình sẽ gợi ý sản phẩm mắt ngay! Bạn cần loại nào?",
    quickReplies: ["Mascara", "Kẻ mắt", "Phấn mắt"],
  },
  blush_recommendation: {
    text: "Mình sẽ gợi ý má hồng phù hợp ngay!",
    quickReplies: ["Màu hồng", "Màu cam san hô", "Màu đào", "Dưới 200k"],
  },
  concealer_recommendation: {
    text: "Mình sẽ gợi ý concealer phù hợp ngay!",
    quickReplies: ["Che mụn", "Che quầng mắt", "Che thâm", "Dưới 300k"],
  },
  base_makeup_recommendation: {
    text: "Mình sẽ gợi ý ngay! Bạn cần loại nào?",
    quickReplies: ["Phấn phủ", "Kem lót", "Xịt khóa nền"],
  },
  find_product: {
    text: "Mình có thể giúp bạn chọn makeup phù hợp. Cho mình biết thêm một chút nhé: 1. Bạn muốn makeup cho dịp nào? 2. Loại da của bạn? 3. Khoảng ngân sách?",
    quickReplies: [
      { text: "Son môi", action: { type: "PRODUCT_SEARCH", category: "lipstick_recommendation" } },
      { text: "Cushion / Kem nền", action: { type: "PRODUCT_SEARCH", category: "cushion_foundation_recommendation" } },
      { text: "Mascara / Kẻ mắt", action: { type: "PRODUCT_SEARCH", category: "eye_makeup_recommendation" } },
      { text: "Phấn má hồng", action: { type: "PRODUCT_SEARCH", category: "blush_recommendation" } },
      { text: "Set makeup đầy đủ", action: { type: "PRODUCT_SEARCH", category: "makeup_set_builder" } }
    ],
  },
  find_by_skin_condition: {
    text: "Bạn có thể cho mình biết loại da của bạn không?",
    quickReplies: ["Da dầu", "Da khô", "Da hỗn hợp", "Da nhạy cảm", "Da thường"],
  },
  find_by_event: {
    text: "Bạn cần trang điểm cho dịp nào?",
    quickReplies: ["Đi học hằng ngày", "Đi làm / Công sở", "Đi tiệc / Sự kiện", "Đám cưới", "Hẹn hò"],
  },
  find_by_skin_color: {
    text: "Bạn muốn tìm màu phù hợp với tông da nào?",
    quickReplies: ["Da sáng / Trắng", "Da trung bình", "Da ngăm / Olive", "Warm tone", "Cool tone"],
  },
  find_by_budget: {
    text: "Ngân sách của bạn khoảng bao nhiêu?",
    quickReplies: ["Dưới 200k", "200k – 400k", "400k – 700k", "Trên 700k"],
  },
  default: {
    text: "Mình có thể giúp bạn tìm sản phẩm makeup phù hợp! Bạn đang cần gì?",
    quickReplies: ["Tư vấn son tint", "Tìm cushion phù hợp", "Set makeup đi tiệc", "Tư vấn theo ngân sách"],
  },
};

// ─────────────────────────────────────────────────────────────────────────────
// Classifier
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Classify the shopping intent of a Vietnamese user message.
 *
 * @param {string} message — raw user message
 * @returns {{
 *   intent: string,
 *   confidence: number,      // 0–100
 *   signals: string[],       // matched keywords
 *   needsClarification: boolean,
 *   clarificationPrompt: { text: string, quickReplies: string[] } | null
 * }}
 */
function classifyIntent(message) {
  const lower = message.toLowerCase().trim();
  const words = lower.split(/\s+/);

  let bestMatch = null;
  let bestScore = 0;

  for (const def of INTENT_DEFINITIONS) {
    // Check required keywords (all must match)
    if (def.required.length > 0) {
      const allRequired = def.required.every((kw) => lower.includes(kw));
      if (!allRequired) continue;
    }

    // Count keyword matches
    const matchedKeywords = def.keywords.filter((kw) => lower.includes(kw));
    if (matchedKeywords.length === 0) continue;

    // Count boost matches
    const matchedBoosts = (def.boosts || []).filter((b) => lower.includes(b));

    // Confidence formula:
    // base = (matched / total_keywords) * weight
    // bonus = matched_boosts * 5
    // bonus for multiple signals
    const keywordRatio = matchedKeywords.length / def.keywords.length;
    const baseConfidence = keywordRatio * def.weight;
    const boostBonus = matchedBoosts.length * 5;
    const multiSignalBonus = matchedKeywords.length >= 2 ? 8 : 0;
    const messageLength = words.length >= 4 ? 5 : 0; // longer messages = more context = higher confidence

    const rawScore = baseConfidence + boostBonus + multiSignalBonus + messageLength;
    const confidence = Math.min(100, Math.round(rawScore));

    if (confidence > bestScore) {
      bestScore = confidence;
      bestMatch = {
        intent: def.intent,
        confidence,
        signals: matchedKeywords,
      };
    }
  }

  // No match at all → general fallback
  if (!bestMatch) {
    bestMatch = {
      intent: "find_product",
      confidence: 25,
      signals: [],
    };
  }

  // PART 5 FIX: Specific makeup category intents are always considered clear
  // when matched — force confidence ≥ 65 so needsClarification stays false.
  const SPECIFIC_MAKEUP_INTENTS = new Set([
    "cushion_foundation_recommendation",
    "lipstick_recommendation",
    "eye_makeup_recommendation",
    "blush_recommendation",
    "concealer_recommendation",
    "base_makeup_recommendation",
    "find_makeup_set",
    "find_by_event",   // events are always clear enough to proceed
  ]);
  if (SPECIFIC_MAKEUP_INTENTS.has(bestMatch.intent) && bestMatch.confidence < 65) {
    bestMatch = { ...bestMatch, confidence: 65 };
  }

  const needsClarification = bestMatch.confidence < 60;
  const clarificationPrompt = needsClarification
    ? (CLARIFICATION_PROMPTS[bestMatch.intent] || CLARIFICATION_PROMPTS.default)
    : null;

  return {
    ...bestMatch,
    needsClarification,
    clarificationPrompt,
  };
}

/**
 * Map the classifier intent to the chatbot.service.js routing intent.
 * Ensures the pipeline intent keys align with existing INTENT_RULES intents.
 *
 * @param {string} classifiedIntent
 * @param {string} existingChatbotIntent — from detectIntent() in chatbot.service.js
 * @returns {string} — final routing intent
 */
function resolveRoutingIntent(classifiedIntent, existingChatbotIntent) {
  const MAP = {
    // ── Specific makeup category intents (direct routing) ──────────────────
    cushion_foundation_recommendation: "cushion_foundation_recommendation",
    lipstick_recommendation:           "lipstick_recommendation",
    eye_makeup_recommendation:         "eye_makeup_recommendation",
    blush_recommendation:              "blush_recommendation",
    concealer_recommendation:          "concealer_recommendation",
    base_makeup_recommendation:        "base_makeup_recommendation",
    // ── Existing intents ───────────────────────────────────────────────────
    find_makeup_set:   "makeup_set_builder",
    find_by_event:     "event_makeup_look",
    find_by_skin_color: "shade_tone_advice",
    find_best_seller:  "product_recommendation",
    find_by_budget:    "product_recommendation",
    find_sale_product: "find_sale_product",
    find_voucher:      "voucher_promotion_question",
    compare_products:  "product_comparison",
    product_information: "product_recommendation",
    find_by_skin_condition: "product_recommendation",
    find_by_makeup_style:   "product_recommendation",
    find_product:      existingChatbotIntent, // delegate to existing intent routing
  };
  return MAP[classifiedIntent] || existingChatbotIntent;
}

module.exports = { classifyIntent, resolveRoutingIntent, CLARIFICATION_PROMPTS };
