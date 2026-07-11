/**
 * makeupIntentParser.js
 * Standalone parsing utilities for makeup-specific attributes.
 * No database calls. Used by chatbot and makeupRecommendation.service.js.
 */

// ─────────────────────────────────────────────────────────────────────────────
// Makeup category map (matches DB categoryCode values)
// ─────────────────────────────────────────────────────────────────────────────

const MAKEUP_CATEGORY_MAP = [
  {
    keywords: ["son tint", "lip tint", "son nước", "son nươc", "tint"],
    categoryCode: "CAT_LIP_TINT",
    categoryNames: ["Son tint"],
  },
  {
    keywords: ["son thỏi", "son thoi", "lipstick", "son lì", "son li", "son nhung", "son mờ"],
    categoryCode: "CAT_LIPSTICK",
    categoryNames: ["Son thỏi"],
  },
  {
    keywords: ["kem nền", "kem nen", "foundation", "liquid foundation"],
    categoryCode: "CAT_FOUNDATION",
    categoryNames: ["Kem nền"],
  },
  {
    keywords: ["cushion", "phấn nước", "phan nuoc"],
    categoryCode: "CAT_CUSHION",
    categoryNames: ["Cushion"],
  },
  {
    keywords: ["che khuyết điểm", "che khuyet diem", "concealer", "che mụn", "ẩn vết thâm"],
    categoryCode: "CAT_CONCEALER",
    categoryNames: ["Che khuyết điểm"],
  },
  {
    keywords: ["phấn phủ", "phan phu", "powder", "loose powder", "pressed powder"],
    categoryCode: "CAT_POWDER",
    categoryNames: ["Phấn phủ"],
  },
  {
    keywords: ["má hồng", "ma hong", "blush", "phấn má"],
    categoryCode: "CAT_BLUSH",
    categoryNames: ["Má hồng"],
  },
  {
    keywords: ["mascara"],
    categoryCode: "CAT_MASCARA",
    categoryNames: ["Mascara"],
  },
  {
    keywords: ["kẻ mày", "ke may", "chì mày", "chi may", "pencil mày", "eyebrow"],
    categoryCode: "CAT_EYEBROW",
    categoryNames: ["Chì kẻ mày"],
  },
  {
    keywords: ["kẻ mắt", "ke mat", "eyeliner", "liner mắt"],
    categoryCode: "CAT_EYELINER",
    categoryNames: ["Kẻ mắt"],
  },
  {
    keywords: ["phấn mắt", "phan mat", "eyeshadow", "bảng màu mắt", "palette"],
    categoryCode: "CAT_EYESHADOW",
    categoryNames: ["Phấn mắt"],
  },
  {
    keywords: ["kem lót", "kem lot", "primer", "lót nền"],
    categoryCode: "CAT_PRIMER",
    categoryNames: ["Kem lót"],
  },
  {
    keywords: ["xịt khóa nền", "xit khoa nen", "setting spray", "xịt cố định", "xit co dinh"],
    categoryCode: "CAT_SETTING_SPRAY",
    categoryNames: ["Xịt khóa nền"],
  },
  {
    keywords: ["bắt sáng", "bat sang", "highlighter", "bắt highlight"],
    categoryCode: "CAT_HIGHLIGHTER",
    categoryNames: ["Bắt sáng"],
  },
  {
    keywords: ["tạo khối", "tao khoi", "contour", "bronzer"],
    categoryCode: "CAT_CONTOUR",
    categoryNames: ["Tạo khối"],
  },
  {
    keywords: ["tẩy trang makeup", "tay trang makeup", "tẩy makeup", "tay makeup"],
    categoryCode: "CAT_MAKEUP_REMOVER",
    categoryNames: ["Tẩy trang makeup"],
  },
  {
    keywords: ["son dưỡng", "son duong", "lip balm", "son môi dưỡng"],
    categoryCode: "CAT_LIP_BALM",
    categoryNames: ["Son dưỡng"],
  },
  // Catch-all "son" → lip tint (must come LAST among lip entries)
  {
    keywords: ["son môi", "son moi", "son"],
    categoryCode: "CAT_LIP_TINT",
    categoryNames: ["Son tint", "Son thỏi"],
  },
];

/**
 * Parse makeup category from a Vietnamese message.
 * @param {string} message
 * @returns {{ categoryCode: string, categoryNames: string[] }|null}
 */
function parseMakeupCategory(message) {
  const lower = message.toLowerCase();
  for (const entry of MAKEUP_CATEGORY_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return { categoryCode: entry.categoryCode, categoryNames: entry.categoryNames };
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Finish type parser
// ─────────────────────────────────────────────────────────────────────────────

const FINISH_MAP = [
  { keywords: ["matte", "lì", "kiềm dầu", "oil control", "kiem dau", "không bóng"], value: "matte" },
  { keywords: ["glowy", "glow", "sáng bóng", "tươi sáng", "glass skin", "dewy glow"], value: "glowy" },
  { keywords: ["satin", "semi matte", "bán matte"], value: "satin" },
  { keywords: ["dewy", "ẩm mướt", "am muot", "căng mọng", "ướt"], value: "dewy" },
  { keywords: ["velvet", "nhung", "mềm mịn"], value: "velvet" },
  { keywords: ["tint", "stain", "bám màu", "lên màu"], value: "tint" },
  { keywords: ["tự nhiên", "tu nhien", "natural look", "no makeup"], value: "natural" },
];

/**
 * Parse product finish type from a Vietnamese message.
 * @param {string} message
 * @returns {string|null}
 */
function parseFinish(message) {
  const lower = message.toLowerCase();
  for (const entry of FINISH_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return entry.value;
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Tone / skin tone parser
// ─────────────────────────────────────────────────────────────────────────────

const TONE_MAP = [
  { keywords: ["da ngăm", "ngăm", "dark skin", "da tối", "da sậm", "tâm màu tối", "olive da"], value: "olive" },
  { keywords: ["da sáng", "da trắng", "fair skin", "light skin", "tông sáng"], value: "light" },
  { keywords: ["da trung bình", "medium skin", "da vừa", "neutral skin"], value: "neutral" },
  { keywords: ["warm tone", "tông ấm", "ấm", "undertone ấm"], value: "warm" },
  { keywords: ["cool tone", "tông lạnh", "lạnh", "undertone lạnh"], value: "cool" },
];

/**
 * Parse skin tone / undertone from a Vietnamese message.
 * @param {string} message
 * @returns {string|null}
 */
function parseTone(message) {
  const lower = message.toLowerCase();
  for (const entry of TONE_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return entry.value;
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Occasion parser
// ─────────────────────────────────────────────────────────────────────────────

const OCCASION_MAP = [
  { keywords: ["đi tiệc", "tiec", "tiệc", "party", "dạ tiệc", "da tiec", "prom", "gala"], value: "party" },
  { keywords: ["đi cưới", "đám cưới", "dam cuoi", "wedding", "lễ cưới", "le cuoi"], value: "wedding" },
  { keywords: ["đi học", "di hoc", "học sinh", "sinh viên", "trường"], value: "school" },
  { keywords: ["đi làm", "di lam", "văn phòng", "van phong", "công sở", "cong so", "office"], value: "office" },
  { keywords: ["hẹn hò", "hen ho", "date", "romantic", "hẹn"], value: "date" },
  { keywords: ["hàng ngày", "hang ngay", "everyday", "daily", "thường ngày", "bình thường"], value: "daily" },
];

/**
 * Parse occasion from a Vietnamese message.
 * @param {string} message
 * @returns {string|null}
 */
function parseOccasion(message) {
  const lower = message.toLowerCase();
  for (const entry of OCCASION_MAP) {
    if (entry.keywords.some((kw) => lower.includes(kw))) {
      return entry.value;
    }
  }
  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Use-case hints
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parse use-case attribute hints from a Vietnamese message.
 * @param {string} message
 * @returns {{ waterproof: boolean, longWear: boolean, nonComedogenic: boolean, fragranceFree: boolean }}
 */
function parseUseCaseHints(message) {
  const lower = message.toLowerCase();
  return {
    waterproof: /chống nước|waterproof|chong nuoc|chống lem|chong lem|lâu trôi không lem/.test(lower),
    longWear: /lâu trôi|bền màu|ben mau|long wear|long-wear|long lasting|bám bền|bam ben/.test(lower),
    nonComedogenic: /không gây mụn|khong gay mun|non-comedogenic|dưỡng ẩm không mụn/.test(lower),
    fragranceFree: /không mùi|khong mui|fragrance.free|không hương liệu/.test(lower),
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Composite makeup parse
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parse all makeup-specific search constraints from a user message.
 * @param {string} message
 * @returns {{ category, finish, tone, occasion, useCaseHints }}
 */
function parseMakeupConstraints(message) {
  return {
    category: parseMakeupCategory(message),
    finish: parseFinish(message),
    tone: parseTone(message),
    occasion: parseOccasion(message),
    useCaseHints: parseUseCaseHints(message),
  };
}

module.exports = {
  parseMakeupCategory,
  parseFinish,
  parseTone,
  parseOccasion,
  parseUseCaseHints,
  parseMakeupConstraints,
  MAKEUP_CATEGORY_MAP,
};
