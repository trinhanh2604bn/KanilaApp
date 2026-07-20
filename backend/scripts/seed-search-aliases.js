#!/usr/bin/env node
"use strict";

/**
 * scripts/seed-search-aliases.js
 *
 * Seeds the search_aliases collection with Vietnamese abbreviations,
 * common makeup terms, category shortcuts, and brand aliases.
 * Idempotent — safe to re-run.
 *
 * Usage:
 *   node scripts/seed-search-aliases.js
 */

require("dotenv").config();
const mongoose = require("mongoose");
const SearchAliasService = require("../services/search/searchAlias.service");
const SearchQueryNormalizer = require("../utils/searchQueryNormalizer");

// All makeup-only aliases. NO skincare.
const ALIASES = [
  // ─── Lip products ──────────────────────────────────────────────────────────
  { alias: "son", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son lì", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son bóng", canonical_term: "Lip Gloss", category_context: "lip" },
  { alias: "son tint", canonical_term: "Lip Stain", category_context: "lip" },
  { alias: "son kem", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son gloss", canonical_term: "Lip Gloss", category_context: "lip" },
  { alias: "son nhũ", canonical_term: "Lip Gloss", category_context: "lip" },
  { alias: "son dưỡng", canonical_term: "Lip Balm", category_context: "lip" },
  { alias: "lip tint", canonical_term: "Lip Stain", category_context: "lip" },
  { alias: "lip gloss", canonical_term: "Lip Gloss", category_context: "lip" },
  { alias: "lip liner", canonical_term: "Lip Liner", category_context: "lip" },
  { alias: "son matte", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son bullet", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son thỏi", canonical_term: "Lipstick", category_context: "lip" },
  { alias: "son nước", canonical_term: "Lip Stain", category_context: "lip" },

  // ─── Foundation / base makeup ──────────────────────────────────────────────
  { alias: "kem nền", canonical_term: "Foundation", category_context: "face" },
  { alias: "foundation", canonical_term: "Foundation", category_context: "face" },
  { alias: "cushion", canonical_term: "Foundation", category_context: "face" },
  { alias: "phấn nước", canonical_term: "Foundation", category_context: "face" },
  { alias: "bb", canonical_term: "BB & CC Cream", category_context: "face" },
  { alias: "cc", canonical_term: "BB & CC Cream", category_context: "face" },
  { alias: "bb cream", canonical_term: "BB & CC Cream", category_context: "face" },
  { alias: "cc cream", canonical_term: "BB & CC Cream", category_context: "face" },
  { alias: "primer", canonical_term: "Primer", category_context: "face" },
  { alias: "kem lót", canonical_term: "Primer", category_context: "face" },
  { alias: "concealer", canonical_term: "Concealer", category_context: "face" },
  { alias: "che khuyết điểm", canonical_term: "Concealer", category_context: "face" },
  { alias: "setting powder", canonical_term: "Powder", category_context: "face" },
  { alias: "phấn phủ", canonical_term: "Powder", category_context: "face" },
  { alias: "compact", canonical_term: "Powder", category_context: "face" },
  { alias: "phấn nén", canonical_term: "Powder", category_context: "face" },
  { alias: "setting spray", canonical_term: "Setting Spray", category_context: "face" },
  { alias: "xịt khóa", canonical_term: "Setting Spray", category_context: "face" },
  { alias: "xịt khóa nền", canonical_term: "Setting Spray", category_context: "face" },

  // ─── Contour / blush / highlight ───────────────────────────────────────────
  { alias: "phấn má", canonical_term: "Blush", category_context: "face" },
  { alias: "má hồng", canonical_term: "Blush", category_context: "face" },
  { alias: "blush", canonical_term: "Blush", category_context: "face" },
  { alias: "contour", canonical_term: "Contour", category_context: "face" },
  { alias: "tạo khối", canonical_term: "Contour", category_context: "face" },
  { alias: "highlight", canonical_term: "Highlighter", category_context: "face" },
  { alias: "highlighter", canonical_term: "Highlighter", category_context: "face" },
  { alias: "bronzer", canonical_term: "Bronzer", category_context: "face" },

  // ─── Eye makeup ────────────────────────────────────────────────────────────
  { alias: "phấn mắt", canonical_term: "Eyeshadow", category_context: "eye" },
  { alias: "eyeshadow", canonical_term: "Eyeshadow", category_context: "eye" },
  { alias: "palette", canonical_term: "Eyeshadow Palette", category_context: "eye" },
  { alias: "bảng mắt", canonical_term: "Eyeshadow Palette", category_context: "eye" },
  { alias: "eyeliner", canonical_term: "Eyeliner", category_context: "eye" },
  { alias: "kẻ mắt", canonical_term: "Eyeliner", category_context: "eye" },
  { alias: "mascara", canonical_term: "Mascara", category_context: "eye" },
  { alias: "chì mắt", canonical_term: "Eyeliner", category_context: "eye" },
  { alias: "eye primer", canonical_term: "Primer", category_context: "eye" },
  { alias: "glitter", canonical_term: "Eyeshadow", category_context: "eye" },
  { alias: "winged", canonical_term: "Eyeliner", category_context: "eye" },

  // ─── Eyebrow ───────────────────────────────────────────────────────────────
  { alias: "kẻ chân mày", canonical_term: "Eyebrow", category_context: "eye" },
  { alias: "chân mày", canonical_term: "Eyebrow", category_context: "eye" },
  { alias: "eyebrow", canonical_term: "Eyebrow", category_context: "eye" },
  { alias: "brow pencil", canonical_term: "Eyebrow", category_context: "eye" },
  { alias: "brow mascara", canonical_term: "Eyebrow", category_context: "eye" },
  { alias: "brow gel", canonical_term: "Eyebrow", category_context: "eye" },

  // ─── Makeup tools ──────────────────────────────────────────────────────────
  { alias: "cọ trang điểm", canonical_term: "cọ trang điểm", category_context: null },
  { alias: "brush", canonical_term: "cọ trang điểm", category_context: null },
  { alias: "sponge", canonical_term: "mút trang điểm", category_context: null },
  { alias: "mút", canonical_term: "mút trang điểm", category_context: null },
  { alias: "beauty blender", canonical_term: "mút trang điểm", category_context: null },
  { alias: "bông phấn", canonical_term: "bông phấn", category_context: null },

  // ─── Attribute abbreviations ───────────────────────────────────────────────
  { alias: "lau bền", canonical_term: "lâu trôi", category_context: null },
  { alias: "lâu bền", canonical_term: "lâu trôi", category_context: null },
  { alias: "long wear", canonical_term: "lâu trôi", category_context: null },
  { alias: "chống nước", canonical_term: "không thấm nước", category_context: null },
  { alias: "waterproof", canonical_term: "không thấm nước", category_context: null },
  { alias: "không lem", canonical_term: "chống lem", category_context: null },
  { alias: "matte", canonical_term: "lì matte", category_context: null },
  { alias: "dewy", canonical_term: "căng bóng", category_context: null },
  { alias: "natural", canonical_term: "tự nhiên", category_context: null },
  { alias: "full coverage", canonical_term: "che phủ cao", category_context: null },
  { alias: "light coverage", canonical_term: "che phủ nhẹ", category_context: null },
  { alias: "buildable", canonical_term: "che phủ linh hoạt", category_context: null },

  // ─── Shade abbreviations ───────────────────────────────────────────────────
  { alias: "màu đỏ", canonical_term: "đỏ", category_context: "lip" },
  { alias: "đỏ đô", canonical_term: "đỏ đô", category_context: "lip" },
  { alias: "đỏ cam", canonical_term: "đỏ cam", category_context: "lip" },
  { alias: "đỏ hồng", canonical_term: "hồng đỏ", category_context: "lip" },
  { alias: "hồng", canonical_term: "hồng", category_context: "lip" },
  { alias: "cam", canonical_term: "cam", category_context: "lip" },
  { alias: "nude", canonical_term: "nude", category_context: "lip" },
  { alias: "hồng nude", canonical_term: "hồng nude", category_context: "lip" },
  { alias: "berry", canonical_term: "berry", category_context: "lip" },
  { alias: "plum", canonical_term: "tím mận", category_context: "lip" },
  { alias: "tím", canonical_term: "tím", category_context: "lip" },
  { alias: "đất", canonical_term: "nâu đất", category_context: "lip" },
  { alias: "coral", canonical_term: "san hô", category_context: "lip" },
  { alias: "đỏ cherry", canonical_term: "đỏ cherry", category_context: "lip" },

  // ─── Brand common abbreviations / misspellings ────────────────────────────
  { alias: "3ce", canonical_term: "3CE", entity_type: "brand" },
  { alias: "romand", canonical_term: "rom&nd", entity_type: "brand" },
  { alias: "mac", canonical_term: "M.A.C", entity_type: "brand" },
  { alias: "nyx", canonical_term: "NYX", entity_type: "brand" },
  { alias: "the face shop", canonical_term: "The Face Shop", entity_type: "brand" },
  { alias: "tfs", canonical_term: "The Face Shop", entity_type: "brand" },
  { alias: "innisfree", canonical_term: "Innisfree", entity_type: "brand" },
  { alias: "etude", canonical_term: "Etude House", entity_type: "brand" },
  { alias: "missha", canonical_term: "MISSHA", entity_type: "brand" },
  { alias: "laneige", canonical_term: "LANEIGE", entity_type: "brand" },
  { alias: "maybelline", canonical_term: "Maybelline", entity_type: "brand" },
  { alias: "loreal", canonical_term: "L'Oréal", entity_type: "brand" },
  { alias: "l'oreal", canonical_term: "L'Oréal", entity_type: "brand" },
  { alias: "revlon", canonical_term: "Revlon", entity_type: "brand" },
  { alias: "covergirl", canonical_term: "CoverGirl", entity_type: "brand" },
  { alias: "urban decay", canonical_term: "Urban Decay", entity_type: "brand" },
  { alias: "tarte", canonical_term: "Tarte", entity_type: "brand" },
  { alias: "fenty", canonical_term: "Fenty Beauty", entity_type: "brand" },
  { alias: "nars", canonical_term: "NARS", entity_type: "brand" },
  { alias: "charlotte tilbury", canonical_term: "Charlotte Tilbury", entity_type: "brand" },
  { alias: "ct", canonical_term: "Charlotte Tilbury", entity_type: "brand" },
  { alias: "clinique", canonical_term: "Clinique", entity_type: "brand" },
  { alias: "too faced", canonical_term: "Too Faced", entity_type: "brand" },
  { alias: "benefit", canonical_term: "Benefit Cosmetics", entity_type: "brand" },
  { alias: "milani", canonical_term: "Milani", entity_type: "brand" },
  { alias: "elf", canonical_term: "e.l.f.", entity_type: "brand" },
  { alias: "clio", canonical_term: "CLIO", entity_type: "brand" },
  { alias: "black rouge", canonical_term: "Black Rouge", entity_type: "brand" },
  { alias: "merzy", canonical_term: "Merzy", entity_type: "brand" },
  { alias: "peripera", canonical_term: "Peripera", entity_type: "brand" },

  // ─── Generic category shortcuts ────────────────────────────────────────────
  { alias: "trang diem", canonical_term: "Makeup", category_context: null },
  { alias: "trang điểm", canonical_term: "Makeup", category_context: null },
  { alias: "makeup", canonical_term: "Makeup", category_context: null },
  { alias: "cosmetics", canonical_term: "Makeup", category_context: null },
  { alias: "my pham", canonical_term: "Makeup", category_context: null },
  { alias: "mỹ phẩm", canonical_term: "Makeup", category_context: null },
];

function buildEntry(raw) {
  const canonicalNormalized = SearchQueryNormalizer.normalize(raw.canonical_term || "").folded;
  const aliasNormalized     = SearchQueryNormalizer.normalize(raw.alias || "").folded;

  return {
    alias:               raw.alias,
    alias_normalized:    aliasNormalized,
    canonical_term:      raw.canonical_term || raw.alias,
    canonical_normalized: canonicalNormalized,
    entity_type:         raw.entity_type || "generic",
    entity_id:           raw.entity_id || null,
    category_context:    raw.category_context || null,
    priority:            raw.priority || 0,
    is_active:           true,
  };
}

async function run() {
  console.log("[seed-aliases] Connecting to MongoDB…");
  await mongoose.connect(process.env.MONGO_URI, {
    serverSelectionTimeoutMS: 30000,
  });

  const entries = ALIASES.map(buildEntry);
  const result = await SearchAliasService.bulkUpsert(entries);

  console.log(`[seed-aliases] ✅ Done.`);
  console.log(`  Inserted: ${result.inserted}`);
  console.log(`  Updated:  ${result.updated}`);
  console.log(`  Total:    ${entries.length}`);

  await mongoose.disconnect();
}

run().catch((e) => {
  console.error("[seed-aliases] Fatal error:", e);
  process.exit(1);
});
