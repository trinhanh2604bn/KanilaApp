/**
 * chatbotShoppingContext.js
 * Stage 2 of the Advanced Recommendation Pipeline.
 *
 * Converts a raw user message + classified intent into a structured
 * ShoppingContext object used by all downstream pipeline stages.
 *
 * Reuses: priceParser.js, makeupIntentParser.js (Phase 8 utilities)
 * No database calls — pure parsing.
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

const { parsePriceRange } = require("../utils/priceParser");
const {
  parseMakeupCategory,
  parseFinish,
  parseTone,
  parseOccasion,
  parseUseCaseHints,
} = require("../utils/makeupIntentParser");

// ─────────────────────────────────────────────────────────────────────────────
// Skin type parser (reuse logic from chatbotProductQuery.parser.js but scoped)
// ─────────────────────────────────────────────────────────────────────────────

const SKIN_TYPE_MAP = [
  { keywords: ["da dầu", "da nhờn", "oily", "nhờn"], value: "oily" },
  { keywords: ["da khô", "dry", "khô"], value: "dry" },
  { keywords: ["da nhạy cảm", "nhạy cảm", "sensitive", "kích ứng"], value: "sensitive" },
  { keywords: ["da hỗn hợp", "combination", "hỗn hợp"], value: "combination" },
  { keywords: ["da thường", "da bình thường", "normal"], value: "normal" },
];

const SKIN_CONCERN_MAP = [
  { keywords: ["mụn", "acne", "nổi mụn", "da mụn"], value: "acne" },
  { keywords: ["thâm", "dark spot", "nám", "tàn nhang"], value: "dark_spot" },
  { keywords: ["xỉn màu", "xỉn", "sạm", "không đều màu"], value: "dullness" },
  { keywords: ["kiểm soát dầu", "kiểm soát nhờn", "oil control"], value: "oil_control" },
  { keywords: ["anti aging", "lão hóa", "nếp nhăn", "chống lão hóa"], value: "anti_aging" },
  { keywords: ["lỗ chân lông", "pore", "thu nhỏ lỗ"], value: "pore" },
];

const EVENT_REQUIREMENTS = {
  party:   { requirements: ["long_wear", "bold_color"], style: "glam" },
  wedding: { requirements: ["long_wear", "soft_glam", "waterproof"], style: "soft_glam" },
  school:  { requirements: ["lightweight", "natural"], style: "natural" },
  office:  { requirements: ["natural", "long_wear"], style: "polished" },
  date:    { requirements: ["long_wear", "romantic"], style: "soft_glam" },
  daily:   { requirements: ["lightweight", "natural", "quick"], style: "natural" },
};

const STYLE_MAP = [
  { keywords: ["soft glam", "nhẹ nhàng nhưng sang", "tinh tế", "tự nhiên sang"], value: "soft_glam" },
  { keywords: ["no makeup", "không trang điểm", "tự nhiên nhất", "trong veo"], value: "no_makeup" },
  { keywords: ["bold", "đậm", "nổi bật", "sắc sảo", "smoky"], value: "bold" },
  { keywords: ["natural", "tự nhiên", "nhẹ nhàng", "look tươi"], value: "natural" },
  { keywords: ["glam", "glamour", "đi sự kiện", "nổi"], value: "glam" },
  { keywords: ["ulzzang", "kiểu hàn", "hàn quốc", "korean"], value: "korean" },
  { keywords: ["vintage", "retro", "cổ điển"], value: "vintage" },
];

// ─────────────────────────────────────────────────────────────────────────────
// Extract makeup style from message
// ─────────────────────────────────────────────────────────────────────────────

function parseMakeupStyle(message) {
  const lower = message.toLowerCase();
  for (const entry of STYLE_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) return entry.value;
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Shade preference extractor
// ─────────────────────────────────────────────────────────────────────────────

function parseShadePreferences(message) {
  const lower = message.toLowerCase();
  const shades = [];

  if (lower.includes("mlbb") || lower.includes("my lips but better")) shades.push("MLBB");
  if (lower.includes("nude") || lower.includes("hồng nude") || lower.includes("màu tự nhiên")) shades.push("nude");
  if (lower.includes("hồng đào") || lower.includes("peachy") || lower.includes("peach")) shades.push("peach");
  if (lower.includes("đỏ") || lower.includes("red")) shades.push("red");
  if (lower.includes("cam") || lower.includes("orange") || lower.includes("coral")) shades.push("coral");
  if (lower.includes("nâu") || lower.includes("brown") || lower.includes("terra")) shades.push("brown");
  if (lower.includes("hồng") || lower.includes("pink")) shades.push("pink");
  if (lower.includes("berry") || lower.includes("tím đỏ") || lower.includes("mận")) shades.push("berry");
  if (lower.includes("màu sáng") || lower.includes("light")) shades.push("light");

  return shades;
}

// ─────────────────────────────────────────────────────────────────────────────
// Best-seller / sale signals
// ─────────────────────────────────────────────────────────────────────────────

function parseBestSellerSignal(message) {
  const lower = message.toLowerCase();
  return (
    lower.includes("bán chạy") ||
    lower.includes("best seller") ||
    lower.includes("phổ biến") ||
    lower.includes("hot nhất") ||
    lower.includes("nhiều người dùng")
  );
}

function parseSaleSignal(message) {
  const lower = message.toLowerCase();
  return (
    lower.includes("đang sale") ||
    lower.includes("giảm giá") ||
    lower.includes("khuyến mãi") ||
    lower.includes("flash sale") ||
    lower.includes("đang giảm")
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Category → canonical key resolver
// ─────────────────────────────────────────────────────────────────────────────

const CANONICAL_CATEGORY_MAP = {
  CAT_LIP_TINT:       { key: "lip_tint",   label: "Son tint",    names: ["Son tint", "Tint môi"] },
  CAT_LIPSTICK:       { key: "lipstick",   label: "Son thỏi",    names: ["Son thỏi", "Son môi", "Lipstick"] },
  CAT_CUSHION:        { key: "cushion",    label: "Cushion",     names: ["Cushion", "Phấn nước"] },
  CAT_FOUNDATION:     { key: "foundation", label: "Kem nền",     names: ["Kem nền", "Foundation"] },
  CAT_CONCEALER:      { key: "concealer",  label: "Che khuyết điểm", names: ["Concealer", "Che khuyết điểm"] },
  CAT_PRIMER:         { key: "primer",     label: "Kem lót",     names: ["Primer", "Kem lót"] },
  CAT_BLUSH:          { key: "blush",      label: "Má hồng",     names: ["Má hồng", "Blush", "Phấn má"] },
  CAT_MASCARA:        { key: "mascara",    label: "Mascara",     names: ["Mascara", "Chuốt mi"] },
  CAT_EYELINER:       { key: "eyeliner",   label: "Kẻ mắt",     names: ["Eyeliner", "Kẻ mắt", "Bút kẻ mắt"] },
  CAT_EYESHADOW:      { key: "eyeshadow",  label: "Phấn mắt",   names: ["Eyeshadow", "Phấn mắt", "Bảng phấn mắt"] },
  CAT_SETTING_SPRAY:  { key: "setting_spray", label: "Xịt khóa nền", names: ["Setting spray", "Xịt khóa nền", "Xịt cố định"] },
  CAT_POWDER:         { key: "powder",     label: "Phấn phủ",   names: ["Phấn phủ", "Loose powder", "Powder"] },
  CAT_HIGHLIGHTER:    { key: "highlighter", label: "Highlight", names: ["Highlighter", "Tạo khối sáng"] },
  CAT_CONTOUR:        { key: "contour",    label: "Tạo khối tối", names: ["Contour", "Tạo khối"] },
  CAT_LIP_GLOSS:      { key: "lip_gloss",  label: "Son bóng",   names: ["Son bóng", "Lip gloss"] },
  CAT_LIP_LINER:      { key: "lip_liner",  label: "Chì môi",    names: ["Chì môi", "Lip liner"] },
};

function resolveCategory(makeupCategoryResult) {
  if (!makeupCategoryResult) return null;
  const { categoryCode } = makeupCategoryResult;
  const canonical = CANONICAL_CATEGORY_MAP[categoryCode];
  if (!canonical) return null;
  return {
    code: categoryCode,
    key: canonical.key,
    label: canonical.label,
    categoryNames: canonical.names,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Skin requirement resolver (need_match for scoring)
// ─────────────────────────────────────────────────────────────────────────────

function buildRequirementsFromContext(skinType, occasion) {
  const requirements = [];

  if (skinType === "oily") requirements.push("oil_control", "matte", "long_wear");
  if (skinType === "dry") requirements.push("moisturizing", "dewy", "hydrating");
  if (skinType === "sensitive") requirements.push("gentle", "fragrance_free", "non_comedogenic");
  if (skinType === "combination") requirements.push("oil_control", "balance");

  if (occasion) {
    const eventReqs = EVENT_REQUIREMENTS[occasion];
    if (eventReqs) requirements.push(...eventReqs.requirements);
  }

  return [...new Set(requirements)];
}

// ─────────────────────────────────────────────────────────────────────────────
// Main extractor
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Extract a structured ShoppingContext from a raw user message + classified intent.
 *
 * @param {string} message          — raw user message
 * @param {string} classifiedIntent — from classifyIntent().intent
 * @returns {ShoppingContext}
 */
function extractShoppingContext(message, classifiedIntent) {
  // ── Makeup-specific parsing ──────────────────────────────────────────────
  const makeupCategoryResult = parseMakeupCategory(message);
  const category = resolveCategory(makeupCategoryResult);
  const finishType = parseFinish(message);
  const tone = parseTone(message);
  const occasion = parseOccasion(message);
  const useCaseHints = parseUseCaseHints(message);

  // ── Price ────────────────────────────────────────────────────────────────
  const { minPrice, maxPrice, sortHint } = parsePriceRange(message);

  // ── Skin ────────────────────────────────────────────────────────────────
  let skinType = null;
  for (const entry of SKIN_TYPE_MAP) {
    if (entry.keywords.some((kw) => message.toLowerCase().includes(kw))) {
      skinType = entry.value;
      break;
    }
  }

  let skinConcerns = [];
  for (const entry of SKIN_CONCERN_MAP) {
    if (entry.keywords.some((kw) => message.toLowerCase().includes(kw))) {
      skinConcerns.push(entry.value);
    }
  }
  skinConcerns = [...new Set(skinConcerns)];

  // ── Makeup style / shade ─────────────────────────────────────────────────
  const makeupStyle = parseMakeupStyle(message);
  const shadePreference = parseShadePreferences(message);
  const finishPreference = finishType ? [finishType] : [];

  // ── Requirements (for need_match scoring) ───────────────────────────────
  const requirements = buildRequirementsFromContext(skinType, occasion);

  // ── Event requirements addition ──────────────────────────────────────────
  let eventStyle = null;
  if (occasion && EVENT_REQUIREMENTS[occasion]) {
    eventStyle = EVENT_REQUIREMENTS[occasion].style;
    if (!makeupStyle) {
      // Infer style from event
    }
  }

  // ── Signals ─────────────────────────────────────────────────────────────
  const wantsBestSeller = parseBestSellerSignal(message);
  const wantsSale = parseSaleSignal(message);
  const wantsLongWear = useCaseHints?.longWear || requirements.includes("long_wear");
  const wantsWaterproof = useCaseHints?.waterproof || false;
  
  const lowerMsg = message.toLowerCase();
  const isBundle = lowerMsg.includes("set") || lowerMsg.includes("combo") || lowerMsg.includes("bộ");

  return {
    // Category
    productCategory: category?.key || null,
    categoryCode: category?.code || null,
    categoryLabel: category?.label || null,
    categoryNames: category?.categoryNames || [],

    // Skin
    skinType,
    skinConcerns,
    isSensitiveFriendly: skinType === "sensitive" || skinConcerns.includes("sensitive"),

    // Requirements (used for need_match scoring)
    requirements,

    // Makeup style attributes
    finishPreference,
    shadePreference,
    occasion,
    makeupStyle: makeupStyle || eventStyle,

    // Budget
    budget: {
      min: minPrice,
      max: maxPrice,
      sortHint, // "asc" = cheapest first, "desc" = priciest first
    },

    // Intent signals
    wantsLongWear,
    wantsWaterproof,
    wantsBestSeller,
    wantsSale,
    wantsOutOfStock: false, // user must explicitly ask
    bundle: isBundle,

    // Metadata
    detectedIntent: classifiedIntent,
    rawMessage: message,
  };
}

module.exports = { extractShoppingContext, CANONICAL_CATEGORY_MAP, EVENT_REQUIREMENTS };
