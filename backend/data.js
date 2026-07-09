/**
 * KANILA — Comprehensive MongoDB Seed Data
 * File name: data.js
 *
 * Run from backend root:
 *   npm install
 *   cp .env.example .env
 *   # set MONGO_URI in .env
 *   node data.js
 *
 * Notes:
 * - Passwordless direction: this seed does NOT create password_hash or plain password.
 * - Email is treated as the verified identifier. Phone is optional contact data.
 * - Image URL fields are blank by default where optional. Required media URL fields use a placeholder
 *   unless SEED_BLANK_IMAGE_URLS=true is forced and your schema allows empty strings.
 * - The script dynamically loads all model JavaScript files and seeds all detected models where possible.
 */

require("dotenv").config();

const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const mongoose = require("mongoose");

const MONGO_URI = process.env.MONGO_URI;
const SEED_SIZE = Number(process.env.SEED_SIZE || 20);
const FORCE_BLANK_IMAGE_URLS = process.env.SEED_BLANK_IMAGE_URLS === "true";
const CLEAN_DATABASE = process.env.SEED_CLEAN_DATABASE !== "false";

if (!MONGO_URI) {
  console.error("❌ Missing MONGO_URI in .env");
  process.exit(1);
}

/* ──────────────────────────────────────────────────────────────
 * Dynamic model loader
 * ────────────────────────────────────────────────────────────── */
function walkJsFiles(dir) {
  if (!fs.existsSync(dir)) return [];
  const out = [];
  for (const entry of fs.readdirSync(dir)) {
    const full = path.join(dir, entry);
    const stat = fs.statSync(full);
    if (stat.isDirectory()) out.push(...walkJsFiles(full));
    else if (entry.endsWith(".js")) out.push(full);
  }
  return out;
}

function loadAllModels() {
  const candidates = [
    path.join(__dirname, "models"),
    path.join(__dirname, "src", "models"),
    path.join(__dirname, "src", "modules"),
  ];

  const files = [];
  for (const dir of candidates) files.push(...walkJsFiles(dir));

  for (const file of files) {
    try {
      require(file);
    } catch (err) {
      // Some module files may be controllers/services, or may depend on unavailable runtime config.
      // Keep seeding resilient.
      if (file.includes(".model") || file.includes("model")) {
        console.warn(`⚠️  Could not load model file: ${path.relative(__dirname, file)} — ${err.message}`);
      }
    }
  }

  return mongoose.models;
}

function M(name) {
  return mongoose.models[name] || null;
}

function hasPath(Model, field) {
  return Boolean(Model && Model.schema && Model.schema.path(field));
}

function collectionName(Model) {
  return Model?.collection?.name || Model?.modelName || "";
}

/* ──────────────────────────────────────────────────────────────
 * General helpers
 * ────────────────────────────────────────────────────────────── */
const now = () => new Date();
const daysAgo = (n) => new Date(Date.now() - n * 24 * 60 * 60 * 1000);
const daysFromNow = (n) => new Date(Date.now() + n * 24 * 60 * 60 * 1000);
const pick = (arr, i) => (arr && arr.length) ? arr[i % arr.length] : undefined;
const money = (base, i) => base + (i % 9) * 25000;
const phoneAt = (i) => `09${String(10000000 + i).slice(-8)}`;
const emailAt = (prefix, i) => `${prefix}${String(i + 1).padStart(2, "0")}@kanila-seed.vn`;
const objectId = () => new mongoose.Types.ObjectId();
const maybe = (arr) => arr.filter(Boolean);

function imageUrl(seed, required = false) {
  if (FORCE_BLANK_IMAGE_URLS && !required) return "";
  if (!required) return "";
  return `https://placehold.co/800x800/png?text=${encodeURIComponent(seed)}`;
}

function uniqueCode(prefix, i) {
  return `${prefix}_${String(i + 1).padStart(4, "0")}`;
}

function md5(value) {
  return crypto.createHash("md5").update(JSON.stringify(value)).digest("hex");
}

/* ──────────────────────────────────────────────────────────────
 * Context registry for relationship mapping
 * ────────────────────────────────────────────────────────────── */
const ctx = {
  inserted: {},
  byCode: {},
};

function remember(modelName, docs, codeFieldCandidates = []) {
  ctx.inserted[modelName] = docs || [];
  for (const field of codeFieldCandidates) {
    for (const doc of docs || []) {
      if (doc && doc[field]) {
        ctx.byCode[`${modelName}.${doc[field]}`] = doc;
      }
    }
  }
}

function ids(modelName) {
  return (ctx.inserted[modelName] || []).map((d) => d._id);
}

function idOf(modelName, i = 0) {
  const list = ids(modelName);
  return list.length ? list[i % list.length] : objectId();
}

/* ──────────────────────────────────────────────────────────────
 * Schema-aware filler
 * Fills missing top-level fields to satisfy the user's requirement that
 * non-image fields should have data where possible.
 * ────────────────────────────────────────────────────────────── */
function isTimestampLike(field) {
  return [
    "password",
    "passwordHash",
    "password_hash",
    "confirmPassword",
    "oldPassword",
    "newPassword",
    "phone_verified_at",
    "_id",
    "__v",
    "createdAt",
    "updatedAt",
    "created_at",
    "updated_at",
  ].includes(field);
}

function pathRequired(schemaType) {
  const opt = schemaType?.options || {};
  return opt.required === true || Array.isArray(opt.required);
}

function smartString(field, i, schemaType) {
  const lower = field.toLowerCase();
  const opt = schemaType?.options || {};

  if (schemaType?.enumValues && schemaType.enumValues.length) {
    return pick(schemaType.enumValues, i);
  }

  if (lower.includes("email")) return emailAt("user", i);
  if (lower.includes("phone") || lower.includes("hotline")) return phoneAt(i);
  if (lower.includes("url") || lower.includes("image") || lower.includes("media") || lower.includes("avatar") || lower.includes("logo")) {
    return imageUrl(`${field}-${i}`, pathRequired(schemaType));
  }
  if (lower.includes("code") || lower.endsWith("number") || lower.includes("tracking")) return uniqueCode(field.toUpperCase().replace(/[^A-Z0-9]/g, "_"), i);
  if (lower.includes("slug")) return `kanila-${field}-${i + 1}`;
  if (lower.includes("status")) return opt.default || "active";
  if (lower.includes("type")) return opt.default || "general";
  if (lower.includes("name") || lower.includes("title")) return `Kanila ${field.replace(/_/g, " ")} ${i + 1}`;
  if (lower.includes("description") || lower.includes("note") || lower.includes("content") || lower.includes("text")) {
    return `Dữ liệu mẫu ${field.replace(/_/g, " ")} số ${i + 1} phục vụ kiểm thử Kanila App.`;
  }
  if (lower.includes("currency")) return "VND";
  if (lower.includes("country")) return "VN";
  if (lower.includes("locale")) return "vi-VN";
  if (lower.includes("ip")) return `127.0.0.${(i % 200) + 1}`;
  if (lower.includes("agent")) return "Kanila Seed Script";
  return `${field}_${i + 1}`;
}

function smartArray(field, i, schemaType) {
  const lower = field.toLowerCase();
  const caster = schemaType?.caster;
  const casterOpt = caster?.options || {};
  if (casterOpt.ref) return [idOf(casterOpt.ref, i), idOf(casterOpt.ref, i + 1)].filter(Boolean);
  if (lower.includes("ingredient")) return [pick(["niacinamide", "hyaluronic_acid", "ceramide", "centella"], i)];
  if (lower.includes("tag")) return [pick(["hydration", "brightening", "oil_control", "daily_use"], i)];
  if (lower.includes("skin")) return [pick(["oily", "dry", "combination", "normal", "sensitive"], i)];
  if (lower.includes("concern")) return [pick(["acne", "dark_spots", "dullness", "large_pores"], i)];
  if (lower.includes("product")) return [idOf("Product", i), idOf("Product", i + 1)].filter(Boolean);
  if (lower.includes("category")) return [idOf("Category", i), idOf("Category", i + 1)].filter(Boolean);
  return [`${field}_${i + 1}`];
}

function smartValue(field, schemaType, i) {
  const instance = schemaType?.instance;
  const opt = schemaType?.options || {};

  if (opt.ref) return idOf(opt.ref, i);

  if (instance === "String") return smartString(field, i, schemaType);
  if (instance === "Number") {
    if (field.toLowerCase().includes("rating")) return 4 + ((i % 10) / 10);
    if (field.toLowerCase().includes("percent") || field.toLowerCase().includes("rate")) return Math.min(100, 10 + i);
    if (field.toLowerCase().includes("amount") || field.toLowerCase().includes("price") || field.toLowerCase().includes("fee")) return money(100000, i);
    if (field.toLowerCase().includes("qty") || field.toLowerCase().includes("quantity") || field.toLowerCase().includes("stock")) return 10 + i;
    return i + 1;
  }
  if (instance === "Boolean") return i % 2 === 0;
  if (instance === "Date") {
    if (field.toLowerCase().includes("expire") || field.toLowerCase().includes("validto") || field.toLowerCase().includes("end")) return daysFromNow(30 + i);
    return daysAgo(i);
  }
  if (instance === "ObjectID" || instance === "ObjectId") return objectId();
  if (instance === "Array") return smartArray(field, i, schemaType);
  if (instance === "Mixed") return { seed: true, value: i + 1 };

  return undefined;
}

function fillBySchema(Model, doc, i, options = {}) {
  const out = { ...doc };
  const fillOptional = options.fillOptional !== false;

  for (const [field, schemaType] of Object.entries(Model.schema.paths)) {
    if (field.includes(".")) continue;
    if (isTimestampLike(field)) continue;
    if (Object.prototype.hasOwnProperty.call(out, field)) continue;

    const required = pathRequired(schemaType);
    if (!fillOptional && !required) continue;

    const value = smartValue(field, schemaType, i);
    if (value !== undefined) out[field] = value;
  }
  return out;
}

async function insertDocs(Model, docs, label, options = {}) {
  if (!Model) {
    console.log(`⏭️  Skip ${label}: model not found`);
    return [];
  }

  const payload = docs.map((d, i) => fillBySchema(Model, d, i, options));
  if (!payload.length) return [];

  try {
    const inserted = await Model.insertMany(payload, { ordered: false });
    console.log(`✅ Inserted ${inserted.length} ${label}`);
    return inserted;
  } catch (err) {
    // For ordered:false, inserted docs may still be saved, but Mongoose may not return them consistently.
    console.warn(`⚠️  InsertMany issue for ${label}: ${err.message}`);
    const recovered = await Model.find({}).limit(Math.max(payload.length, SEED_SIZE));
    return recovered;
  }
}

async function createOne(Model, doc, i, label) {
  try {
    return await Model.create(fillBySchema(Model, doc, i));
  } catch (err) {
    console.warn(`⚠️  Failed ${label || Model.modelName} #${i + 1}: ${err.message}`);
    return null;
  }
}

async function seedGeneric(Model, count = SEED_SIZE) {
  if (!Model) return [];
  const docs = [];
  for (let i = 0; i < count; i++) docs.push(fillBySchema(Model, {}, i));
  const inserted = [];
  for (let i = 0; i < docs.length; i++) {
    const item = await createOne(Model, docs[i], i, Model.modelName);
    if (item) inserted.push(item);
  }
  if (inserted.length) console.log(`✅ Generic seeded ${inserted.length} ${Model.modelName}`);
  return inserted;
}

/* ──────────────────────────────────────────────────────────────
 * Fixed Kanila data
 * ────────────────────────────────────────────────────────────── */
const brandNames = [
  ["L'Oréal Paris", "LOREAL"],
  ["Maybelline New York", "MAYBELLINE"],
  ["The Ordinary", "ORDINARY"],
  ["Innisfree", "INNISFREE"],
  ["Laneige", "LANEIGE"],
  ["Bioderma", "BIODERMA"],
  ["Sulwhasoo", "SULWHASOO"],
  ["Klairs", "KLAIRS"],
  ["La Roche-Posay", "LAROCHE"],
  ["Cocoon Vietnam", "COCOON"],
  ["Shiseido", "SHISEIDO"],
  ["MAC Cosmetics", "MAC"],
  ["Rom&nd", "ROMAND"],
  ["3CE", "THREECE"],
  ["Etude", "ETUDE"],
  ["NARS", "NARS"],
  ["Fenty Beauty", "FENTY"],
  ["Dior Beauty", "DIOR"],
  ["Clinique", "CLINIQUE"],
  ["Paula's Choice", "PAULAS"],
  ["Anessa", "ANESSA"],
  ["Hada Labo", "HADALABO"],
  ["Kiehl's", "KIEHLS"],
  ["Eucerin", "EUCERIN"],
];

const categoryTree = [
  {
    name: "Face",
    vi: "Trang điểm mặt",
    code: "FACE",
    children: ["Foundation", "Concealer", "Primer", "Powder", "Setting Spray", "BB & CC Cream", "Tinted Moisturizer"],
  },
  {
    name: "Eyes",
    vi: "Trang điểm mắt",
    code: "EYES",
    children: ["Mascara", "Eyeliner", "Eyeshadow", "Eyebrow", "False Lashes"],
  },
  {
    name: "Lips",
    vi: "Trang điểm môi",
    code: "LIPS",
    children: ["Lipstick", "Lip Gloss", "Lip Balm", "Lip Liner", "Lip Stain"],
  },
  {
    name: "Cheeks",
    vi: "Trang điểm má",
    code: "CHEEKS",
    children: ["Blush", "Bronzer", "Highlighter", "Contour"],
  },
  {
    name: "Gift",
    vi: "Quà tặng",
    code: "GIFT",
    children: ["Eyeshadow Palette", "Face Palette", "Makeup Kit"],
  },
  {
    name: "Mini & Travel",
    vi: "Mini & Du lịch",
    code: "MINI_TRAVEL",
    children: ["Mini Foundation", "Mini Lipstick", "Trial Kits"],
  },
  {
    name: "Skincare",
    vi: "Chăm sóc da",
    code: "SKINCARE",
    children: ["Cleanser", "Toner", "Serum", "Moisturizer", "Sunscreen", "Mask", "Exfoliator"],
  },
];

const referenceData = [
  // skin_type
  ["skin_type", "oily", "Da dầu", "Oily skin", "Ưu tiên gel, lotion mỏng nhẹ, oil-control, non-comedogenic.", ["oil_control", "gel_texture"], [], ["niacinamide", "zinc_pca"], [], ["FACE", "SKINCARE"]],
  ["skin_type", "dry", "Da khô", "Dry skin", "Ưu tiên cream, dưỡng ẩm sâu, ceramide, HA.", ["hydration", "cream_texture"], [], ["hyaluronic_acid", "ceramide"], ["alcohol_denat"], ["SKINCARE"]],
  ["skin_type", "combination", "Da hỗn hợp", "Combination skin", "Ưu tiên sản phẩm cân bằng dầu nước.", ["balance", "lightweight"], [], ["niacinamide", "hyaluronic_acid"], [], ["SKINCARE"]],
  ["skin_type", "normal", "Da thường", "Normal skin", "Gợi ý rộng, ít giới hạn.", ["daily_use"], [], [], [], ["SKINCARE", "FACE"]],
  ["skin_type", "sensitive", "Da nhạy cảm", "Sensitive skin", "Ưu tiên soothing, fragrance-free, alcohol-free.", ["soothing", "fragrance_free"], ["strong_active"], ["centella", "panthenol"], ["fragrance", "alcohol_denat"], ["SKINCARE"]],
  ["skin_type", "unknown", "Chưa chắc chắn", "Not sure", "Cho phép dùng app khi người dùng chưa biết loại da.", [], [], [], [], []],

  // skin_concern
  ["skin_concern", "acne", "Mụn", "Acne", "Gợi ý sản phẩm hỗ trợ giảm mụn, BHA, tea tree, zinc, phục hồi.", ["acne_care"], ["heavy_oil"], ["bha", "zinc_pca", "centella"], [], ["SKINCARE"]],
  ["skin_concern", "dark_spots", "Thâm mụn", "Post-acne marks", "Ưu tiên Niacinamide, Vitamin C, Tranexamic Acid.", ["brightening"], [], ["niacinamide", "vitamin_c", "tranexamic_acid"], [], ["SKINCARE"]],
  ["skin_concern", "melasma", "Nám/sạm màu", "Melasma", "Ưu tiên làm sáng, chống nắng, đều màu.", ["brightening", "sun_protection"], [], ["tranexamic_acid", "vitamin_c"], [], ["SKINCARE"]],
  ["skin_concern", "dullness", "Da xỉn màu", "Dullness", "Ưu tiên Vitamin C, AHA nhẹ, brightening.", ["brightening", "glow"], [], ["vitamin_c", "aha"], [], ["SKINCARE"]],
  ["skin_concern", "large_pores", "Lỗ chân lông to", "Large pores", "Ưu tiên BHA, niacinamide, oil-control.", ["pore_care"], [], ["bha", "niacinamide"], [], ["SKINCARE"]],
  ["skin_concern", "blackheads", "Mụn đầu đen", "Blackheads", "Ưu tiên BHA, clay mask, cleansing oil.", ["pore_care"], [], ["bha"], [], ["SKINCARE"]],
  ["skin_concern", "redness", "Da dễ đỏ", "Redness", "Ưu tiên Centella, Panthenol, phục hồi, soothing.", ["soothing"], ["fragrance"], ["centella", "panthenol"], ["fragrance"], ["SKINCARE"]],
  ["skin_concern", "dehydrated", "Da thiếu nước", "Dehydrated skin", "Ưu tiên Hyaluronic Acid, toner cấp nước, serum cấp ẩm.", ["hydration"], [], ["hyaluronic_acid"], [], ["SKINCARE"]],
  ["skin_concern", "wrinkles", "Nếp nhăn/lão hóa", "Wrinkles", "Ưu tiên retinol, peptide, collagen support.", ["anti_aging"], [], ["retinol", "peptide"], [], ["SKINCARE"]],
  ["skin_concern", "uneven_texture", "Bề mặt da không mịn", "Uneven texture", "Ưu tiên AHA/BHA/PHA nhẹ.", ["smooth_texture"], [], ["aha", "bha"], [], ["SKINCARE"]],
  ["skin_concern", "damaged_barrier", "Hàng rào da yếu", "Damaged barrier", "Ưu tiên Ceramide, Panthenol, phục hồi.", ["barrier_repair"], ["strong_active"], ["ceramide", "panthenol"], ["retinoid", "aha_bha_high"], ["SKINCARE"]],
  ["skin_concern", "sun_damage", "Da chịu nắng nhiều", "Sun damage", "Ưu tiên chống nắng, phục hồi, làm sáng.", ["sun_protection"], [], ["vitamin_c"], [], ["SKINCARE"]],

  // sensitivity_level
  ["sensitivity_level", "low", "Ít nhạy cảm", "Low sensitivity", "Có thể gợi ý treatment thông thường.", ["treatment"], [], [], [], []],
  ["sensitivity_level", "medium", "Dễ kích ứng nhẹ", "Medium sensitivity", "Ưu tiên active nhẹ, hướng dẫn dùng từ từ.", ["gentle_active"], ["high_strength"], ["panthenol"], ["aha_bha_high"], []],
  ["sensitivity_level", "high", "Rất nhạy cảm", "High sensitivity", "Tránh hương liệu, cồn khô, acid/retinol nồng độ cao.", ["soothing"], ["strong_active"], ["centella", "panthenol"], ["fragrance", "alcohol_denat", "retinoid", "aha_bha_high"], []],
  ["sensitivity_level", "reactive", "Dễ đỏ/rát khi đổi sản phẩm", "Reactive skin", "Ưu tiên phục hồi, soothing, patch test.", ["patch_test", "barrier_repair"], ["strong_active"], ["ceramide", "centella"], ["fragrance", "essential_oil"], []],

  // tone / makeup references
  ["skin_tone", "fair", "Rất sáng", "Fair", "Tone da rất sáng.", ["shade_fair"], [], [], [], ["FACE"]],
  ["skin_tone", "light", "Sáng", "Light", "Tone da sáng.", ["shade_light"], [], [], [], ["FACE"]],
  ["skin_tone", "medium", "Trung bình", "Medium", "Tone da trung bình.", ["shade_medium"], [], [], [], ["FACE"]],
  ["skin_tone", "tan", "Ngăm", "Tan", "Tone da ngăm.", ["shade_tan"], [], [], [], ["FACE"]],
  ["skin_tone", "deep", "Nâu sâu", "Deep", "Tone da nâu sâu.", ["shade_deep"], [], [], [], ["FACE"]],
  ["undertone", "cool", "Lạnh", "Cool", "Thiên hồng/xanh.", ["cool_tone"], [], [], [], ["FACE", "LIPS"]],
  ["undertone", "warm", "Ấm", "Warm", "Thiên vàng/cam.", ["warm_tone"], [], [], [], ["FACE", "LIPS"]],
  ["undertone", "neutral", "Trung tính", "Neutral", "Cân bằng ấm/lạnh.", ["neutral_tone"], [], [], [], ["FACE", "LIPS"]],
  ["undertone", "olive", "Olive", "Olive", "Ánh olive.", ["olive_tone"], [], [], [], ["FACE"]],
  ["undertone", "unknown", "Chưa chắc chắn", "Not sure", "Chưa xác định undertone.", [], [], [], [], []],

  ["shade_preference", "natural", "Tự nhiên", "Natural", "Ưu tiên nền tự nhiên.", ["natural_finish"], [], [], [], ["FACE"]],
  ["shade_preference", "brightening", "Sáng da", "Brightening", "Ưu tiên hiệu ứng sáng da.", ["brightening"], [], [], [], ["FACE"]],
  ["shade_preference", "warm_glow", "Ấm khỏe", "Warm glow", "Ưu tiên nền ấm khỏe.", ["warm_glow"], [], [], [], ["FACE"]],
  ["shade_preference", "pinkish", "Ánh hồng", "Pinkish", "Ưu tiên nền ánh hồng.", ["pink_tone"], [], [], [], ["FACE"]],
  ["shade_preference", "matte", "Lì", "Matte", "Ưu tiên nền lì.", ["matte"], [], [], [], ["FACE"]],

  ["lip_color_preference", "nude", "Nude", "Nude", "Màu nude dễ dùng.", ["lip_nude"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "pink", "Hồng", "Pink", "Màu hồng tươi/trẻ.", ["lip_pink"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "coral", "Cam san hô", "Coral", "Màu coral.", ["lip_coral"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "red", "Đỏ", "Red", "Màu đỏ.", ["lip_red"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "brown", "Nâu", "Brown", "Màu nâu.", ["lip_brown"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "mlbb", "MLBB", "MLBB", "Màu môi tự nhiên.", ["lip_mlbb"], [], [], [], ["LIPS"]],
  ["lip_color_preference", "bold", "Nổi bật", "Bold", "Màu cá tính.", ["lip_bold"], [], [], [], ["LIPS"]],

  ["makeup_style", "natural", "Tự nhiên", "Natural", "Makeup nhẹ nhàng.", ["daily"], [], [], [], ["FACE", "LIPS"]],
  ["makeup_style", "korean", "Kiểu Hàn", "Korean", "Makeup Hàn Quốc.", ["k_beauty"], [], [], [], ["FACE", "LIPS"]],
  ["makeup_style", "glam", "Glam", "Glam", "Trang điểm nổi bật.", ["glam"], [], [], [], ["FACE", "EYES", "LIPS"]],
  ["makeup_style", "office", "Công sở", "Office", "Trang điểm đi làm.", ["office"], [], [], [], ["FACE", "LIPS"]],
  ["makeup_style", "party", "Dự tiệc", "Party", "Trang điểm dự tiệc.", ["party"], [], [], [], ["FACE", "EYES", "LIPS"]],
  ["makeup_style", "daily", "Hằng ngày", "Daily", "Dùng hằng ngày.", ["daily"], [], [], [], ["FACE", "LIPS"]],

  // goals
  ["beauty_goal", "hydration", "Cấp ẩm", "Hydration", "Cải thiện độ ẩm.", ["hydration"], [], ["hyaluronic_acid"], [], ["SKINCARE"]],
  ["beauty_goal", "brightening", "Làm sáng da", "Brightening", "Hỗ trợ da sáng và đều màu.", ["brightening"], [], ["vitamin_c", "niacinamide"], [], ["SKINCARE"]],
  ["beauty_goal", "acne_care", "Hỗ trợ giảm mụn", "Acne care", "Hỗ trợ chăm sóc da mụn.", ["acne_care"], [], ["bha", "zinc_pca"], [], ["SKINCARE"]],
  ["beauty_goal", "oil_control", "Kiểm soát dầu", "Oil control", "Giảm bóng dầu.", ["oil_control"], [], ["niacinamide", "zinc_pca"], [], ["SKINCARE"]],
  ["beauty_goal", "barrier_repair", "Phục hồi hàng rào da", "Barrier repair", "Tăng cường hàng rào bảo vệ da.", ["barrier_repair"], [], ["ceramide", "panthenol"], [], ["SKINCARE"]],
  ["beauty_goal", "anti_aging", "Chống lão hóa", "Anti-aging", "Hỗ trợ nếp nhăn và độ đàn hồi.", ["anti_aging"], [], ["retinol", "peptide"], [], ["SKINCARE"]],
  ["beauty_goal", "pore_care", "Chăm sóc lỗ chân lông", "Pore care", "Hỗ trợ lỗ chân lông.", ["pore_care"], [], ["bha", "niacinamide"], [], ["SKINCARE"]],
  ["beauty_goal", "soothing", "Làm dịu da", "Soothing", "Làm dịu da dễ đỏ/kích ứng.", ["soothing"], [], ["centella", "panthenol"], [], ["SKINCARE"]],
  ["beauty_goal", "sun_protection", "Chống nắng", "Sun protection", "Bảo vệ da khỏi UV.", ["sun_protection"], [], [], [], ["SKINCARE"]],
  ["beauty_goal", "even_tone", "Làm đều màu da", "Even tone", "Hỗ trợ da đều màu.", ["even_tone"], [], ["tranexamic_acid", "niacinamide"], [], ["SKINCARE"]],

  // ingredients
  ["avoid_ingredient", "fragrance", "Hương liệu", "Fragrance", "Người dùng muốn tránh hương liệu.", [], ["fragrance"], [], ["fragrance"], []],
  ["avoid_ingredient", "alcohol_denat", "Cồn khô", "Alcohol denat", "Người dùng muốn tránh cồn khô.", [], ["alcohol_denat"], [], ["alcohol_denat"], []],
  ["avoid_ingredient", "essential_oil", "Tinh dầu", "Essential oil", "Người dùng muốn tránh tinh dầu.", [], ["essential_oil"], [], ["essential_oil"], []],
  ["avoid_ingredient", "paraben", "Paraben", "Paraben", "Người dùng muốn tránh paraben.", [], ["paraben"], [], ["paraben"], []],
  ["avoid_ingredient", "mineral_oil", "Dầu khoáng", "Mineral oil", "Người dùng muốn tránh dầu khoáng.", [], ["mineral_oil"], [], ["mineral_oil"], []],
  ["avoid_ingredient", "silicone", "Silicone", "Silicone", "Người dùng muốn tránh silicone.", [], ["silicone"], [], ["silicone"], []],
  ["avoid_ingredient", "sulfate", "Sulfate", "Sulfate", "Người dùng muốn tránh sulfate.", [], ["sulfate"], [], ["sulfate"], []],
  ["avoid_ingredient", "lanolin", "Lanolin", "Lanolin", "Người dùng muốn tránh lanolin.", [], ["lanolin"], [], ["lanolin"], []],
  ["avoid_ingredient", "retinoid", "Retinoid", "Retinoid", "Người dùng muốn tránh retinoid.", [], ["retinoid"], [], ["retinoid"], []],
  ["avoid_ingredient", "aha_bha_high", "Acid nồng độ cao", "High-strength acid", "Người dùng muốn tránh acid nồng độ cao.", [], ["aha_bha_high"], [], ["aha_bha_high"], []],

  ["preferred_ingredient", "niacinamide", "Niacinamide", "Niacinamide", "Phù hợp dầu, lỗ chân lông, thâm.", ["oil_control", "brightening"], [], ["niacinamide"], [], ["SKINCARE"]],
  ["preferred_ingredient", "hyaluronic_acid", "Hyaluronic Acid", "Hyaluronic Acid", "Cấp nước.", ["hydration"], [], ["hyaluronic_acid"], [], ["SKINCARE"]],
  ["preferred_ingredient", "ceramide", "Ceramide", "Ceramide", "Phục hồi.", ["barrier_repair"], [], ["ceramide"], [], ["SKINCARE"]],
  ["preferred_ingredient", "centella", "Rau má/Centella", "Centella", "Làm dịu.", ["soothing"], [], ["centella"], [], ["SKINCARE"]],
  ["preferred_ingredient", "panthenol", "Panthenol", "Panthenol", "Phục hồi, nhạy cảm.", ["barrier_repair", "soothing"], [], ["panthenol"], [], ["SKINCARE"]],
  ["preferred_ingredient", "vitamin_c", "Vitamin C", "Vitamin C", "Sáng da.", ["brightening"], [], ["vitamin_c"], [], ["SKINCARE"]],
  ["preferred_ingredient", "bha", "BHA", "BHA", "Mụn đầu đen, dầu.", ["pore_care", "acne_care"], [], ["bha"], [], ["SKINCARE"]],
  ["preferred_ingredient", "aha", "AHA", "AHA", "Bề mặt da, xỉn màu.", ["smooth_texture", "brightening"], [], ["aha"], [], ["SKINCARE"]],
  ["preferred_ingredient", "retinol", "Retinol", "Retinol", "Lão hóa, texture.", ["anti_aging"], [], ["retinol"], [], ["SKINCARE"]],
  ["preferred_ingredient", "peptide", "Peptide", "Peptide", "Chống lão hóa.", ["anti_aging"], [], ["peptide"], [], ["SKINCARE"]],
  ["preferred_ingredient", "zinc_pca", "Zinc PCA", "Zinc PCA", "Dầu, mụn.", ["oil_control", "acne_care"], [], ["zinc_pca"], [], ["SKINCARE"]],
  ["preferred_ingredient", "tranexamic_acid", "Tranexamic Acid", "Tranexamic Acid", "Thâm/nám.", ["brightening", "even_tone"], [], ["tranexamic_acid"], [], ["SKINCARE"]],

  // commerce preferences
  ["budget_range", "under_200k", "Dưới 200K", "Under 200K", "Ngân sách tiết kiệm.", ["budget"], [], [], [], []],
  ["budget_range", "200_500k", "200K - 500K", "200K - 500K", "Ngân sách phổ biến.", ["budget"], [], [], [], []],
  ["budget_range", "500_1000k", "500K - 1 triệu", "500K - 1M", "Ngân sách trung cao.", ["budget"], [], [], [], []],
  ["budget_range", "premium", "Cao cấp", "Premium", "Ưu tiên sản phẩm cao cấp.", ["premium"], [], [], [], []],
  ["texture_preference", "gel", "Gel", "Gel", "Kết cấu gel.", ["gel_texture"], [], [], [], ["SKINCARE"]],
  ["texture_preference", "cream", "Kem", "Cream", "Kết cấu kem.", ["cream_texture"], [], [], [], ["SKINCARE"]],
  ["texture_preference", "lotion", "Lotion", "Lotion", "Kết cấu lotion.", ["lotion_texture"], [], [], [], ["SKINCARE"]],
  ["texture_preference", "serum", "Serum", "Serum", "Kết cấu serum.", ["serum_texture"], [], [], [], ["SKINCARE"]],
  ["texture_preference", "oil", "Dầu", "Oil", "Kết cấu dầu.", ["oil_texture"], [], [], [], ["SKINCARE"]],
  ["texture_preference", "balm", "Balm", "Balm", "Kết cấu balm.", ["balm_texture"], [], [], [], ["SKINCARE"]],
  ["finish_preference", "matte", "Lì", "Matte", "Hiệu ứng lì.", ["matte"], [], [], [], ["FACE"]],
  ["finish_preference", "dewy", "Căng bóng", "Dewy", "Hiệu ứng căng bóng.", ["dewy"], [], [], [], ["FACE"]],
  ["finish_preference", "natural", "Tự nhiên", "Natural", "Hiệu ứng tự nhiên.", ["natural"], [], [], [], ["FACE"]],
  ["finish_preference", "glowy", "Bắt sáng", "Glowy", "Hiệu ứng glowy.", ["glowy"], [], [], [], ["FACE"]],
  ["fragrance_preference", "fragrance_free", "Không hương liệu", "Fragrance-free", "Ưu tiên không hương liệu.", ["fragrance_free"], ["fragrance"], [], ["fragrance"], []],
  ["fragrance_preference", "light_fragrance", "Hương nhẹ", "Light fragrance", "Chấp nhận hương nhẹ.", ["light_fragrance"], [], [], [], []],
  ["fragrance_preference", "no_preference", "Không ưu tiên", "No preference", "Không có ưu tiên về mùi.", [], [], [], [], []],
  ["purchase_intent", "daily_use", "Dùng hằng ngày", "Daily use", "Sản phẩm sử dụng hằng ngày.", ["daily_use"], [], [], [], []],
  ["purchase_intent", "treatment", "Đặc trị", "Treatment", "Sản phẩm chăm sóc chuyên sâu.", ["treatment"], [], [], [], []],
  ["purchase_intent", "gift", "Làm quà", "Gift", "Mua để tặng.", ["gift"], [], [], [], ["GIFT"]],
  ["purchase_intent", "try_new", "Thử sản phẩm mới", "Try new", "Khám phá sản phẩm mới.", ["try_new"], [], [], [], []],
  ["purchase_intent", "repurchase", "Mua lại", "Repurchase", "Mua lại sản phẩm yêu thích.", ["repurchase"], [], [], [], []],
];

const vietnameseCustomers = [
  ["Lê", "Thị Hương", "female"], ["Nguyễn", "Văn Tùng", "male"], ["Trần", "Thị Mỹ", "female"],
  ["Phạm", "Thị Lan", "female"], ["Hoàng", "Thị Thảo", "female"], ["Đặng", "Văn Đức", "male"],
  ["Bùi", "Thị Kim", "female"], ["Ngô", "Thanh Sơn", "male"], ["Vũ", "Thị Yến", "female"],
  ["Đỗ", "Thị Bích", "female"], ["Lý", "Nguyên", "male"], ["Trần", "Minh Anh", "female"],
  ["Lê", "Hoài Phương", "female"], ["Nguyễn", "Thị Thuỷ", "female"], ["Phạm", "Quốc Việt", "male"],
  ["Nguyễn", "Văn Khôi", "male"], ["Trần", "Thị Ngọc", "female"], ["Lê", "Minh Tuấn", "male"],
  ["Phạm", "Thị Hải", "female"], ["Hoàng", "Đức Anh", "male"], ["Vũ", "Thị Linh", "female"],
  ["Đỗ", "Quang Huy", "male"], ["Bùi", "Thị Mai", "female"], ["Ngô", "Văn Tâm", "male"],
  ["Võ", "Thị Hạnh", "female"],
];

const addressLocations = [
  ["Thành phố Hồ Chí Minh", "Quận 1", "Phường Bến Nghé", "123 Lê Lợi"],
  ["Thành phố Hồ Chí Minh", "Quận 3", "Phường Võ Thị Sáu", "45 Nguyễn Thị Minh Khai"],
  ["Hà Nội", "Quận Ba Đình", "Phường Điện Biên", "78 Hoàng Diệu"],
  ["Đà Nẵng", "Quận Hải Châu", "Phường Thạch Thang", "12 Trần Phú"],
  ["Cần Thơ", "Quận Ninh Kiều", "Phường Tân An", "56 Nguyễn Văn Cừ"],
  ["Hải Phòng", "Quận Ngô Quyền", "Phường Máy Chai", "9 Lạch Tray"],
];

const productBaseNames = [
  "Hydrating Serum", "Matte Foundation", "Vitamin C Brightening Serum", "Green Tea Cream",
  "Water Sleeping Mask", "Micellar Cleansing Water", "Ginseng Renewal Cream", "Soothing Toner",
  "Anthelios Sunscreen SPF50+", "Coffee Body Scrub", "Power Infusing Concentrate", "Matte Lipstick",
  "Lengthening Mascara", "AHA BHA Peeling Solution", "Volcanic Clay Mask", "Lip Sleeping Mask",
  "Effaclar Duo Acne Care", "Coconut Multi Oil", "Intensive Body Balm", "Fix Setting Spray",
  "Calming Cream", "Brightening Gel Cream", "UV Defender SPF50+", "Superstay Matte Ink",
  "Brow Definer", "Eyeshadow Palette", "Creamy Concealer", "Radiant Primer", "Loose Powder",
  "Liquid Blush", "Highlighter Stick", "Contour Palette", "Lip Gloss", "Lip Liner", "Tinted Moisturizer",
];

/* ──────────────────────────────────────────────────────────────
 * Domain seeders
 * ────────────────────────────────────────────────────────────── */
async function seedBrands() {
  const Brand = M("Brand");
  if (!Brand) return [];
  const docs = brandNames.map(([brandName, brandCode], i) => ({
    brandName,
    brandCode,
    description: `${brandName} là thương hiệu mỹ phẩm mẫu dùng cho dữ liệu Kanila App.`,
    logoUrl: "",
    brandStatus: "active",
    isActive: true,
  }));
  const brands = await insertDocs(Brand, docs, "brands");
  remember("Brand", brands, ["brandCode"]);
  return brands;
}

async function seedCategories() {
  const Category = M("Category");
  if (!Category) return [];

  const all = [];
  const parentMap = new Map();

  for (let i = 0; i < categoryTree.length; i++) {
    const p = categoryTree[i];
    const doc = await Category.create(fillBySchema(Category, {
      categoryName: p.name,
      categoryCode: p.code,
      description: p.vi,
      parentCategoryId: null,
      displayOrder: i + 1,
      categoryStatus: "active",
      isActive: true,
    }, i));
    all.push(doc);
    parentMap.set(p.code, doc);
  }

  let sort = 100;
  for (const p of categoryTree) {
    for (const child of p.children) {
      const code = `${p.code}_${child.toUpperCase().replace(/&/g, "AND").replace(/[^A-Z0-9]+/g, "_").replace(/^_|_$/g, "")}`;
      const doc = await Category.create(fillBySchema(Category, {
        categoryName: child,
        categoryCode: code,
        description: `${child} thuộc nhóm ${p.name}.`,
        parentCategoryId: parentMap.get(p.code)._id,
        displayOrder: sort++,
        categoryStatus: "active",
        isActive: true,
      }, sort));
      all.push(doc);
    }
  }

  console.log(`✅ Inserted ${all.length} categories`);
  remember("Category", all, ["categoryCode"]);
  return all;
}

async function seedRolesPermissionsAccounts() {
  const Role = M("Role");
  const Permission = M("Permission");
  const RolePermission = M("RolePermission");
  const Account = M("Account");
  const AccountRole = M("AccountRole");
  const AdminProfile = M("AdminProfile");
  const AccountAuthProvider = M("AccountAuthProvider");

  const permissions = Permission ? await insertDocs(Permission, Array.from({ length: 24 }, (_, i) => ({
    permission_code: uniqueCode("PERM", i),
    permission_name: `Quyền ${i + 1}`,
    permission_group: pick(["auth", "catalog", "order", "payment", "support", "community"], i),
    description: `Quyền thao tác ${i + 1} trong Kanila Admin.`,
  })), "permissions") : [];
  remember("Permission", permissions, ["permission_code"]);

  const roles = Role ? await insertDocs(Role, [
    { role_code: "SUPER_ADMIN", role_name: "Super Admin", role_description: "Toàn quyền hệ thống", role_status: "active" },
    { role_code: "ADMIN", role_name: "Admin", role_description: "Quản trị vận hành", role_status: "active" },
    { role_code: "CS_STAFF", role_name: "Customer Support", role_description: "Chăm sóc khách hàng", role_status: "active" },
    { role_code: "CONTENT_STAFF", role_name: "Content Staff", role_description: "Quản lý nội dung", role_status: "active" },
    { role_code: "WAREHOUSE_STAFF", role_name: "Warehouse Staff", role_description: "Quản lý kho", role_status: "active" },
  ], "roles") : [];
  remember("Role", roles, ["role_code"]);

  if (RolePermission && roles.length && permissions.length) {
    const rp = [];
    for (let i = 0; i < Math.max(SEED_SIZE, permissions.length); i++) {
      rp.push({
        role_id: idOf("Role", i % roles.length),
        permission_id: idOf("Permission", i),
        granted_at: daysAgo(i),
      });
    }
    remember("RolePermission", await insertDocs(RolePermission, rp, "role_permissions"), []);
  }

  if (!Account) return [];
  const staffDocs = Array.from({ length: 8 }, (_, i) => ({
    account_type: i === 0 ? "admin" : "staff",
    email: i === 0 ? "admin@kanila.vn" : `staff${i}@kanila.vn`,
    phone: phoneAt(700 + i),
    username: i === 0 ? "admin" : `staff${i}`,
    account_status: "active",
    email_verified_at: now(),
    phone_verified_at: undefined,
    last_login_at: daysAgo(i),
    failed_login_count: 0,
    locked_until: null,
  }));

  const customerAccountDocs = Array.from({ length: 25 }, (_, i) => ({
    account_type: "customer",
    email: emailAt("customer", i),
    phone: i >= 22 ? null : phoneAt(i),
    username: `customer${String(i + 1).padStart(2, "0")}`,
    account_status: "active",
    email_verified_at: now(),
    phone_verified_at: undefined,
    last_login_at: daysAgo(i % 30),
    failed_login_count: 0,
    locked_until: null,
  }));

  const accounts = await insertDocs(Account, [...staffDocs, ...customerAccountDocs], "accounts", { fillOptional: true });
  remember("Account", accounts, ["email", "username"]);

  const staffAccounts = accounts.filter(a => a.account_type === "admin" || a.account_type === "staff");
  const customerAccounts = accounts.filter(a => a.account_type === "customer");
  ctx.inserted.StaffAccount = staffAccounts;
  ctx.inserted.CustomerAccount = customerAccounts;

  if (AccountRole && accounts.length && roles.length) {
    const ar = accounts.slice(0, Math.max(SEED_SIZE, accounts.length)).map((a, i) => ({
      account_id: a._id,
      role_id: idOf("Role", a.account_type === "admin" ? 0 : (i % roles.length)),
      assigned_by_account_id: accounts[0]._id,
      assigned_at: daysAgo(i),
    }));
    remember("AccountRole", await insertDocs(AccountRole, ar, "account_roles"), []);
  }

  if (AdminProfile && staffAccounts.length) {
    const ap = staffAccounts.map((a, i) => ({
      account_id: a._id,
      employee_code: uniqueCode("EMP", i),
      full_name: i === 0 ? "Quản trị viên Kanila" : `Nhân sự Kanila ${i}`,
      department: pick(["Admin", "CSKH", "Kho", "Marketing", "Nội dung"], i),
      job_title: i === 0 ? "System Admin" : pick(["Staff", "Support Executive", "Warehouse Executive"], i),
      manager_account_id: staffAccounts[0]._id,
      employment_status: "active",
    }));
    remember("AdminProfile", await insertDocs(AdminProfile, ap, "admin_profiles"), []);
  }

  if (AccountAuthProvider && accounts.length) {
    const providers = accounts.slice(0, SEED_SIZE).map((a, i) => ({
      account_id: a._id,
      provider_code: pick(["google", "facebook", "apple"], i),
      provider_subject: `provider-subject-${a._id}`,
      provider_email: a.email,
      linked_at: daysAgo(i),
      last_used_at: daysAgo(i % 5),
    }));
    remember("AccountAuthProvider", await insertDocs(AccountAuthProvider, providers, "account_auth_providers"), []);
  }

  return accounts;
}

async function seedCustomersAndProfiles() {
  const Customer = M("Customer");
  const Address = M("Address");
  const CustomerConsent = M("CustomerConsent");
  const CustomerPreference = M("CustomerPreference");
  const CustomerBeautyProfile = M("CustomerBeautyProfile");

  if (!Customer) return [];
  const accounts = ctx.inserted.CustomerAccount || ctx.inserted.Account || [];
  const docs = Array.from({ length: Math.min(25, accounts.length) }, (_, i) => {
    const [first, last, gender] = pick(vietnameseCustomers, i);
    return {
      account_id: accounts[i]._id,
      customer_code: uniqueCode("KNL_C", i),
      first_name: first,
      last_name: last,
      full_name: `${first} ${last}`,
      date_of_birth: new Date(1988 + (i % 14), i % 12, (i % 27) + 1),
      gender,
      avatar_url: "",
      customer_status: "active",
      registered_at: daysAgo(120 - i),
    };
  });

  const customers = await insertDocs(Customer, docs, "customer_profiles");
  remember("Customer", customers, ["customer_code"]);

  if (Address && customers.length) {
    const addressDocs = [];
    for (let i = 0; i < customers.length; i++) {
      const [city, district, ward, line1] = pick(addressLocations, i);
      addressDocs.push({
        customer_id: customers[i]._id,
        address_label: "Nhà riêng",
        recipient_name: customers[i].full_name,
        phone: accounts[i]?.phone || phoneAt(900 + i),
        address_line_1: line1,
        address_line_2: i % 2 === 0 ? "Căn hộ/Khu dân cư" : "",
        ward, district, city,
        country_code: "VN",
        postal_code: pick(["700000", "100000", "550000", "940000"], i),
        address_type: "home",
        address_note: "Gọi trước khi giao hàng.",
        is_default_shipping: true,
        is_default_billing: true,
      });
      addressDocs.push({
        customer_id: customers[i]._id,
        address_label: "Văn phòng",
        recipient_name: customers[i].full_name,
        phone: phoneAt(1200 + i), // recipient phone can duplicate/ differ from Account.phone
        address_line_1: `Tầng ${2 + (i % 8)} - ${line1}`,
        address_line_2: "Lễ tân nhận hàng",
        ward, district, city,
        country_code: "VN",
        postal_code: pick(["700000", "100000", "550000", "940000"], i),
        address_type: "office",
        address_note: "Giao giờ hành chính.",
        is_default_shipping: false,
        is_default_billing: false,
      });
    }
    remember("Address", await insertDocs(Address, addressDocs, "customer_addresses"), []);
  }

  if (CustomerConsent && customers.length) {
    const consents = [];
    const types = ["terms_of_service", "privacy_policy", "marketing_email"];
    for (let i = 0; i < customers.length; i++) {
      for (let j = 0; j < types.length; j++) {
        consents.push({
          customer_id: customers[i]._id,
          consent_type: types[j],
          consent_status: j === 2 && i % 5 === 0 ? "withdrawn" : "granted",
          consent_version: j === 1 ? "2.0" : "1.0",
          consented_at: daysAgo(60 - i),
          source_channel: pick(["mobile_app", "web", "chatbot"], i + j),
          created_at: daysAgo(60 - i),
        });
      }
    }
    remember("CustomerConsent", await insertDocs(CustomerConsent, consents, "customer_consents"), []);
  }

  if (CustomerPreference && customers.length) {
    const prefs = [];
    const keys = ["locale", "currency", "newsletter", "push_notification", "theme"];
    for (let i = 0; i < customers.length; i++) {
      for (const key of keys) {
        prefs.push({
          customer_id: customers[i]._id,
          preference_key: key,
          preference_value:
            key === "locale" ? "vi-VN" :
              key === "currency" ? "VND" :
                key === "theme" ? "light" :
                  i % 4 === 0 ? "false" : "true",
          updated_at: daysAgo(i),
        });
      }
    }
    remember("CustomerPreference", await insertDocs(CustomerPreference, prefs, "customer_preferences"), []);
  }

  if (CustomerBeautyProfile && customers.length) {
    const skinTypes = ["oily", "dry", "combination", "normal", "sensitive"];
    const concerns = ["acne", "dark_spots", "dullness", "large_pores", "dehydrated", "redness"];
    const profiles = customers.map((c, i) => {
      const data = {
        customer_id: c._id,
        skin_type: pick(skinTypes, i),
        skin_concerns: [pick(concerns, i), pick(concerns, i + 2)],
        sensitivity_level: pick(["low", "medium", "high", "reactive"], i),
        skin_tone: pick(["fair", "light", "medium", "tan", "deep"], i),
        undertone: pick(["cool", "warm", "neutral", "olive", "unknown"], i),
        shade_preference: [pick(["natural", "brightening", "warm_glow", "pinkish", "matte"], i)],
        lip_color_preference: [pick(["nude", "pink", "coral", "red", "brown", "mlbb", "bold"], i)],
        makeup_style: [pick(["natural", "korean", "glam", "office", "party", "daily"], i)],
        beauty_goals: [pick(["hydration", "brightening", "acne_care", "oil_control", "barrier_repair", "anti_aging"], i)],
        avoid_ingredients: i % 3 === 0 ? ["fragrance", "alcohol_denat"] : [],
        preferred_ingredients: [pick(["niacinamide", "hyaluronic_acid", "ceramide", "centella", "vitamin_c"], i)],
        budget_range: pick(["under_200k", "200_500k", "500_1000k", "premium"], i),
        preferred_brands: [idOf("Brand", i), idOf("Brand", i + 1)],
        disliked_brands: [idOf("Brand", i + 5)],
        preferred_categories: [idOf("Category", i), idOf("Category", i + 2)],
        texture_preference: [pick(["gel", "cream", "lotion", "serum", "oil", "balm"], i)],
        finish_preference: [pick(["matte", "dewy", "natural", "glowy"], i)],
        fragrance_preference: pick(["fragrance_free", "light_fragrance", "no_preference"], i),
        purchase_intent: [pick(["daily_use", "treatment", "gift", "try_new", "repurchase"], i)],
        source: pick(["onboarding", "account", "chatbot", "ar"], i),
      };
      data.profile_hash = md5(data);
      data.profile_completion_rate = 95;
      data.last_updated_at = now();
      return data;
    });
    remember("CustomerBeautyProfile", await insertDocs(CustomerBeautyProfile, profiles, "customer_beauty_profiles"), []);
  }

  return customers;
}

async function seedBeautyReferences() {
  const BeautyReference = M("BeautyReference") || M("ProfileReference");
  if (!BeautyReference) {
    console.log("⏭️  Skip beauty_references: BeautyReference/ProfileReference model not found");
    return [];
  }

  const docs = referenceData.map((r, i) => ({
    reference_group: r[0],
    reference_code: r[1],
    display_name_vi: r[2],
    display_name_en: r[3],
    description: r[4],
    helper_text: r[4],
    parent_code: null,
    sort_order: i + 1,
    is_active: true,
    is_multi_select: !["skin_type", "sensitivity_level", "skin_tone", "undertone", "budget_range", "fragrance_preference"].includes(r[0]),
    severity_enabled: ["skin_concern", "avoid_ingredient", "sensitivity_level"].includes(r[0]),
    recommendation_weight: ["skin_type", "skin_concern", "beauty_goal"].includes(r[0]) ? 2 : 1,
    boost_tags: r[5],
    avoid_tags: r[6],
    preferred_ingredients: r[7],
    avoid_ingredients: r[8],
    recommended_categories: r[9],
    warning_text: r[0] === "avoid_ingredient" ? "Chỉ là gợi ý làm đẹp, không phải chẩn đoán y khoa." : "",
  }));

  const refs = await insertDocs(BeautyReference, docs, "beauty_references");
  remember("BeautyReference", refs, ["reference_code"]);
  return refs;
}

async function seedProductsCatalog() {
  const Product = M("Product");
  const ProductMedia = M("ProductMedia");
  const ProductAttribute = M("ProductAttribute");
  const ProductCategory = M("ProductCategory");
  const ProductOption = M("ProductOption");
  const ProductOptionValue = M("ProductOptionValue");
  const ProductVariant = M("ProductVariant");
  const VariantOptionValue = M("VariantOptionValue");
  const VariantMedia = M("VariantMedia");
  const ProductBeautyProfile = M("ProductBeautyProfile");

  if (!Product) return [];
  const products = [];
  const docs = Array.from({ length: 60 }, (_, i) => {
    const brand = pick(ctx.inserted.Brand, i);
    const cat = pick(ctx.inserted.Category, i + 7);
    const baseName = `${brand.brandName} ${pick(productBaseNames, i)}`;
    return {
      productName: baseName,
      productCode: uniqueCode("PRD", i),
      slug: `kanila-${uniqueCode("product", i).toLowerCase()}`,
      brandId: brand._id,
      categoryId: cat._id,
      price: money(120000, i),
      compareAtPrice: money(160000, i),
      imageUrl: "",
      shortDescription: `${baseName} phù hợp nhu cầu làm đẹp hằng ngày.`,
      longDescription: `${baseName} là sản phẩm mẫu của Kanila, có mô tả đầy đủ, công dụng rõ ràng và dùng cho kiểm thử listing, chi tiết sản phẩm, giỏ hàng, checkout và recommendation.`,
      stock: 30 + (i % 150),
      bought: 100 + i * 17,
      averageRating: 4 + ((i % 10) / 10),
      isActive: true,
      productStatus: "active",
      ingredientText: pick([
        "Aqua, Glycerin, Niacinamide, Panthenol, Phenoxyethanol",
        "Water, Hyaluronic Acid, Ceramide NP, Centella Asiatica Extract",
        "Aqua, Dimethicone, Titanium Dioxide, Iron Oxides, Tocopherol",
        "Aqua, Vitamin C derivative, Tranexamic Acid, Butylene Glycol",
      ], i),
      shades: [pick(["01 Ivory", "02 Natural", "03 Beige", "04 Sand", "05 Tan"], i)],
      skin_types_supported: [pick(["oily", "dry", "combination", "normal", "sensitive"], i), "normal"],
      concerns_targeted: [pick(["acne", "dark_spots", "dullness", "large_pores", "dehydrated"], i)],
      ingredient_flags: i % 5 === 0 ? ["fragrance"] : [],
      key_ingredients: [pick(["niacinamide", "hyaluronic_acid", "ceramide", "centella", "vitamin_c"], i)],
      is_sensitive_friendly: i % 3 !== 0,
      tone_match_supported: [pick(["fair", "light", "medium", "tan", "deep"], i)],
      finish_type: pick(["matte", "dewy", "natural", "glowy"], i),
      coverage_type: pick(["light", "medium", "full", ""], i),
      sales_count: 20 + i * 5,
      is_best_seller: i % 7 === 0,
      usageInstruction: "Sử dụng theo hướng dẫn trên bao bì. Patch test trước khi dùng treatment mạnh.",
      createdByAccountId: idOf("Account", 0),
      updatedByAccountId: idOf("Account", 1),
    };
  });

  const inserted = await insertDocs(Product, docs, "products");
  remember("Product", inserted, ["productCode", "slug"]);

  if (ProductMedia && inserted.length) {
    const mediaDocs = inserted.map((p, i) => ({
      productId: p._id,
      mediaType: "image",
      mediaUrl: imageUrl(`product-${i}`, true),
      altText: p.productName,
      sortOrder: 0,
      isPrimary: true,
    }));
    remember("ProductMedia", await insertDocs(ProductMedia, mediaDocs, "productmedias"), []);
  }

  if (ProductAttribute && inserted.length) {
    const attrDocs = [];
    for (let i = 0; i < inserted.length; i++) {
      attrDocs.push(
        { productId: inserted[i]._id, attributeName: "skin_type", attributeValue: pick(["oily", "dry", "combination", "normal", "sensitive"], i), displayOrder: 1 },
        { productId: inserted[i]._id, attributeName: "finish", attributeValue: pick(["matte", "dewy", "natural", "glowy"], i), displayOrder: 2 }
      );
    }
    remember("ProductAttribute", await insertDocs(ProductAttribute, attrDocs, "productattributes"), []);
  }

  if (ProductCategory && inserted.length) {
    const pcDocs = inserted.map((p, i) => ({
      productId: p._id,
      categoryId: idOf("Category", i + 7),
      isPrimary: true,
    }));
    remember("ProductCategory", await insertDocs(ProductCategory, pcDocs, "productcategories"), []);
  }

  let options = [];
  let values = [];
  if (ProductOption && inserted.length) {
    const optionDocs = inserted.slice(0, 30).map((p, i) => ({
      productId: p._id,
      optionName: pick(["Shade", "Size", "Finish"], i),
      displayOrder: 1,
    }));
    options = await insertDocs(ProductOption, optionDocs, "productoptions");
    remember("ProductOption", options, []);
  }

  if (ProductOptionValue && options.length) {
    const valDocs = [];
    options.forEach((op, i) => {
      const vals = op.optionName === "Shade" ? ["01 Ivory", "02 Natural", "03 Beige"] :
        op.optionName === "Size" ? ["Mini", "Full Size", "Travel"] : ["Matte", "Dewy", "Natural"];
      vals.forEach((v, j) => valDocs.push({ productOptionId: op._id, optionValue: v, displayOrder: j + 1 }));
    });
    values = await insertDocs(ProductOptionValue, valDocs, "productoptionvalues");
    remember("ProductOptionValue", values, []);
  }

  let variants = [];
  if (ProductVariant && inserted.length) {
    const varDocs = [];
    inserted.forEach((p, i) => {
      for (let j = 0; j < 2; j++) {
        varDocs.push({
          productId: p._id,
          sku: `${p.productCode}-SKU${j + 1}`,
          barcode: `893${String(100000000 + i * 10 + j).slice(-9)}`,
          variantName: j === 0 ? "Mặc định" : pick(["Mini", "Full Size", "02 Natural", "Matte"], i + j),
          variantStatus: "active",
          weightGrams: 80 + i,
          volumeMl: 30 + j * 20,
          costAmount: Math.round((p.price || 100000) * 0.55),
        });
      }
    });
    variants = await insertDocs(ProductVariant, varDocs, "productvariants");
    remember("ProductVariant", variants, ["sku"]);
  }

  if (VariantOptionValue && variants.length && values.length) {
    const vovDocs = variants.slice(0, Math.max(SEED_SIZE, 40)).map((v, i) => ({
      variantId: v._id,
      optionValueId: values[i % values.length]._id,
    }));
    remember("VariantOptionValue", await insertDocs(VariantOptionValue, vovDocs, "variantoptionvalues"), []);
  }

  if (VariantMedia && variants.length) {
    const vmDocs = variants.slice(0, Math.max(SEED_SIZE, 40)).map((v, i) => ({
      variantId: v._id,
      mediaType: "image",
      mediaUrl: imageUrl(`variant-${i}`, true),
      sortOrder: 0,
      isPrimary: i % 2 === 0,
    }));
    remember("VariantMedia", await insertDocs(VariantMedia, vmDocs, "variantmedias"), []);
  }

  if (ProductBeautyProfile && inserted.length) {
    const pbpDocs = inserted.map((p, i) => ({
      product_id: p._id,
      suitable_skin_types: [pick(["oily", "dry", "combination", "normal", "sensitive"], i), "normal"],
      suitable_skin_concerns: [pick(["acne", "dark_spots", "dullness", "large_pores", "dehydrated", "redness"], i)],
      suitable_sensitivity_levels: [pick(["low", "medium", "high", "reactive"], i)],
      suitable_skin_tones: [pick(["fair", "light", "medium", "tan", "deep"], i)],
      suitable_undertones: [pick(["cool", "warm", "neutral", "olive"], i)],
      supported_beauty_goals: [pick(["hydration", "brightening", "acne_care", "oil_control", "barrier_repair", "anti_aging"], i)],
      key_ingredients: [pick(["niacinamide", "hyaluronic_acid", "ceramide", "centella", "vitamin_c", "bha"], i)],
      avoid_for_ingredients: i % 4 === 0 ? ["fragrance"] : [],
      texture: pick(["gel", "cream", "lotion", "serum", "balm"], i),
      finish: pick(["matte", "dewy", "natural", "glowy"], i),
      fragrance_type: pick(["fragrance_free", "light_fragrance", "no_preference"], i),
      product_tags: [pick(["hydration", "brightening", "daily_use", "treatment", "makeup"], i)],
      recommendation_boost_score: 10 + (i % 20),
      recommendation_penalty_score: i % 4,
      is_active: true,
    }));
    remember("ProductBeautyProfile", await insertDocs(ProductBeautyProfile, pbpDocs, "product_beauty_profiles"), []);
  }

  return inserted;
}

async function seedPricingPromotion() {
  const PriceBook = M("PriceBook");
  const PriceBookEntry = M("PriceBookEntry");
  const PriceHistory = M("PriceHistory");
  const Promotion = M("Promotion");
  const PromotionRule = M("PromotionRule");
  const PromotionTarget = M("PromotionTarget");
  const Coupon = M("Coupon");
  const CustomerCoupon = M("CustomerCoupon");
  const CouponRedemption = M("CouponRedemption");

  if (PriceBook) {
    const books = await insertDocs(PriceBook, [
      { priceBookCode: "RETAIL", priceBookName: "Giá bán lẻ", currencyCode: "VND", isActive: true },
      { priceBookCode: "VIP", priceBookName: "Giá khách VIP", currencyCode: "VND", isActive: true },
      { priceBookCode: "CAMPAIGN", priceBookName: "Giá chiến dịch", currencyCode: "VND", isActive: true },
    ], "pricebooks");
    remember("PriceBook", books, ["priceBookCode"]);
  }

  if (PriceBookEntry) {
    const entries = (ctx.inserted.Product || []).slice(0, 40).map((p, i) => ({
      priceBookId: idOf("PriceBook", i),
      productId: p._id,
      variantId: idOf("ProductVariant", i),
      listPriceAmount: p.compareAtPrice || p.price,
      salePriceAmount: p.price,
      currencyCode: "VND",
      validFrom: daysAgo(10),
      validTo: daysFromNow(120),
      isActive: true,
    }));
    remember("PriceBookEntry", await insertDocs(PriceBookEntry, entries, "pricebookentries"), []);
  }

  if (PriceHistory) {
    const histories = (ctx.inserted.Product || []).slice(0, 40).map((p, i) => ({
      productId: p._id,
      variantId: idOf("ProductVariant", i),
      oldPriceAmount: (p.price || 100000) + 50000,
      newPriceAmount: p.price || 100000,
      currencyCode: "VND",
      changedByAccountId: idOf("Account", 0),
      changedAt: daysAgo(i),
      reason: "Cập nhật giá seed data",
    }));
    remember("PriceHistory", await insertDocs(PriceHistory, histories, "pricehistories"), []);
  }

  let promotions = [];
  if (Promotion) {
    promotions = await insertDocs(Promotion, Array.from({ length: 20 }, (_, i) => ({
      promotionCode: uniqueCode("PROMO", i),
      promotionName: `Chiến dịch ưu đãi Kanila ${i + 1}`,
      description: `Khuyến mãi mẫu số ${i + 1}.`,
      promotionType: pick(["seasonal", "welcome", "flash_sale", "shipping", "birthday", "bundle"], i),
      discountType: pick(["percentage", "fixed"], i),
      discountValue: i % 2 === 0 ? 10 + (i % 30) : 30000 + i * 1000,
      maxDiscountAmount: 200000 + i * 10000,
      startAt: daysAgo(10),
      endAt: daysFromNow(60 + i),
      usageLimitTotal: 1000 + i,
      usageLimitPerCustomer: 1 + (i % 5),
      isAutoApply: i % 4 === 0,
      priority: i,
      promotionStatus: "active",
      createdByAccountId: idOf("Account", 0),
    })), "promotions");
    remember("Promotion", promotions, ["promotionCode"]);
  }

  if (PromotionRule && promotions.length) {
    const rules = promotions.map((p, i) => ({
      promotionId: p._id,
      ruleType: pick(["min_order_amount", "category", "brand", "customer_segment"], i),
      operator: pick(["gte", "in", "eq"], i),
      ruleValue: i % 2 === 0 ? { amount: 300000 } : { ids: [String(idOf("Category", i))] },
      isActive: true,
    }));
    remember("PromotionRule", await insertDocs(PromotionRule, rules, "promotionrules"), []);
  }

  if (PromotionTarget && promotions.length) {
    const targets = promotions.map((p, i) => ({
      promotionId: p._id,
      targetType: pick(["product", "category", "brand", "cart"], i),
      productId: idOf("Product", i),
      categoryId: idOf("Category", i),
      brandId: idOf("Brand", i),
      discountValue: 10 + (i % 20),
    }));
    remember("PromotionTarget", await insertDocs(PromotionTarget, targets, "promotiontargets"), []);
  }

  let coupons = [];
  if (Coupon && promotions.length) {
    coupons = await insertDocs(Coupon, Array.from({ length: 25 }, (_, i) => ({
      promotionId: promotions[i % promotions.length]._id,
      couponCode: uniqueCode("KANI", i),
      validFrom: daysAgo(5),
      validTo: daysFromNow(90),
      usageLimitTotal: 500 + i,
      usageLimitPerCustomer: 1 + (i % 3),
      minOrderAmount: 100000 + i * 10000,
      couponStatus: "active",
    })), "coupons");
    remember("Coupon", coupons, ["couponCode"]);
  }

  if (CustomerCoupon && coupons.length) {
    const cc = (ctx.inserted.Customer || []).map((c, i) => ({
      couponId: coupons[i % coupons.length]._id,
      customer_id: c._id,
      savedAt: daysAgo(i),
      status: pick(["saved", "used", "expired"], i),
      usedAt: i % 3 === 1 ? daysAgo(i - 1) : null,
    }));
    remember("CustomerCoupon", await insertDocs(CustomerCoupon, cc, "customer_coupons"), []);
  }

  if (CouponRedemption && coupons.length) {
    const cr = Array.from({ length: 20 }, (_, i) => ({
      couponId: coupons[i % coupons.length]._id,
      customer_id: idOf("Customer", i),
      order_id: idOf("Order", i),
      discountAmount: 20000 + i * 1000,
      redeemedAt: daysAgo(i),
      redemptionStatus: pick(["used", "cancelled"], i),
    }));
    remember("CouponRedemption", await insertDocs(CouponRedemption, cr, "couponredemptions"), []);
  }
}

async function seedInventory() {
  const Warehouse = M("Warehouse");
  const InventoryBalance = M("InventoryBalance");
  const InventoryTransaction = M("InventoryTransaction");
  const StockReservation = M("StockReservation");

  let warehouses = [];
  if (Warehouse) {
    warehouses = await insertDocs(Warehouse, [
      { warehouseCode: "HCM_MAIN", warehouseName: "Kho Hồ Chí Minh", warehouseStatus: "active", addressLine: "KCN Tân Bình", city: "Hồ Chí Minh", countryCode: "VN" },
      { warehouseCode: "HN_MAIN", warehouseName: "Kho Hà Nội", warehouseStatus: "active", addressLine: "Long Biên", city: "Hà Nội", countryCode: "VN" },
      { warehouseCode: "DN_MAIN", warehouseName: "Kho Đà Nẵng", warehouseStatus: "active", addressLine: "Hải Châu", city: "Đà Nẵng", countryCode: "VN" },
    ], "warehouses");
    remember("Warehouse", warehouses, ["warehouseCode"]);
  }

  if (InventoryBalance) {
    const balances = (ctx.inserted.ProductVariant || []).slice(0, 60).map((v, i) => ({
      warehouseId: idOf("Warehouse", i),
      variantId: v._id,
      onHandQty: 100 + i,
      reservedQty: i % 10,
      blockedQty: i % 3,
      availableQty: 90 + i,
      reorderPointQty: 20,
      safetyStockQty: 10,
      lastCountedAt: daysAgo(i % 20),
    }));
    remember("InventoryBalance", await insertDocs(InventoryBalance, balances, "inventorybalances"), []);
  }

  if (InventoryTransaction) {
    const tx = Array.from({ length: 40 }, (_, i) => ({
      warehouseId: idOf("Warehouse", i),
      variantId: idOf("ProductVariant", i),
      transactionType: pick(["receipt", "sale", "adjustment", "return"], i),
      quantityChange: pick([50, -1, -2, 5], i),
      referenceType: pick(["purchase_order", "order", "stocktake", "return"], i),
      referenceId: uniqueCode("REF", i),
      reasonCode: pick(["new_stock", "customer_order", "cycle_count", "customer_return"], i),
      note: "Giao dịch tồn kho mẫu.",
      performedByAccountId: idOf("Account", 0),
    }));
    remember("InventoryTransaction", await insertDocs(InventoryTransaction, tx, "inventorytransactions"), []);
  }

  if (StockReservation) {
    const reservations = Array.from({ length: 20 }, (_, i) => ({
      checkoutSessionId: idOf("CheckoutSession", i),
      variantId: idOf("ProductVariant", i),
      warehouseId: idOf("Warehouse", i),
      reservedQty: 1 + (i % 3),
      reservationStatus: pick(["active", "released", "converted", "expired"], i),
      expiresAt: daysFromNow(1 + (i % 5)),
    }));
    remember("StockReservation", await insertDocs(StockReservation, reservations, "stockreservations"), []);
  }
}

async function seedCartCheckoutOrdersPayments() {
  const GuestSession = M("GuestSession");
  const Cart = M("Cart");
  const CartItem = M("CartItem");
  const ShippingMethod = M("ShippingMethod");
  const CheckoutSession = M("CheckoutSession");
  const CheckoutAddress = M("CheckoutAddress");
  const CheckoutShippingMethod = M("CheckoutShippingMethod");
  const Order = M("Order");
  const OrderItem = M("OrderItem");
  const OrderAddress = M("OrderAddress");
  const OrderStatusHistory = M("OrderStatusHistory");
  const OrderTotal = M("OrderTotal");
  const PaymentMethod = M("PaymentMethod");
  const PaymentIntent = M("PaymentIntent");
  const PaymentTransaction = M("PaymentTransaction");

  if (GuestSession) {
    const guests = await insertDocs(GuestSession, Array.from({ length: 20 }, (_, i) => ({
      guest_session_id: uniqueCode("GUEST", i),
      status: pick(["active", "expired"], i),
      last_seen_at: daysAgo(i),
      user_agent: "Kanila Mobile Guest",
    })), "guest_sessions");
    remember("GuestSession", guests, ["guest_session_id"]);
  }

  let carts = [];
  if (Cart) {
    const cartDocs = Array.from({ length: 30 }, (_, i) => ({
      owner_type: i < 20 ? "customer" : "guest",
      customer_id: i < 20 ? idOf("Customer", i) : undefined,
      guest_session_id: i >= 20 ? `GUEST_${String(i - 19).padStart(4, "0")}` : null,
      cart_status: pick(["active", "converted", "expired", "merged"], i),
      currency_code: "VND",
      item_count: 2 + (i % 3),
      subtotal_amount: money(250000, i),
      discount_amount: i % 2 === 0 ? 20000 : 0,
      total_amount: money(230000, i),
      expires_at: daysFromNow(3 + i),
    }));
    carts = await insertDocs(Cart, cartDocs, "carts");
    remember("Cart", carts, []);
  }

  if (CartItem && carts.length) {
    const items = [];
    carts.forEach((cart, i) => {
      for (let j = 0; j < 2; j++) {
        const product = pick(ctx.inserted.Product, i + j);
        const variant = pick(ctx.inserted.ProductVariant, i + j);
        const qty = 1 + ((i + j) % 3);
        const unit = product?.price || 100000;
        items.push({
          line_key: `${cart._id}_${variant?._id || j}`,
          product_id: product?._id,
          cart_id: cart._id,
          variant_id: variant?._id,
          sku_snapshot: variant?.sku || product?.productCode || uniqueCode("SKU", i + j),
          product_name_snapshot: product?.productName || `Sản phẩm ${i + j}`,
          variant_name_snapshot: variant?.variantName || "Mặc định",
          brand_name_snapshot: pick(ctx.inserted.Brand, i)?.brandName || "Kanila",
          image_url_snapshot: "",
          compare_at_price_amount: unit + 30000,
          stock_status: "in_stock",
          quantity: qty,
          selected: true,
          unit_price_amount: unit,
          discount_amount: 0,
          final_unit_price_amount: unit,
          line_total_amount: unit * qty,
          added_at: daysAgo(i),
        });
      }
    });
    remember("CartItem", await insertDocs(CartItem, items, "cart_items"), []);
  }

  if (ShippingMethod) {
    const methods = await insertDocs(ShippingMethod, [
      { shipping_method_code: "STANDARD", carrier_code: "GHTK", service_name: "Giao tiêu chuẩn", base_fee_amount: 30000, estimated_days_min: 2, estimated_days_max: 4, is_active: true },
      { shipping_method_code: "EXPRESS", carrier_code: "GHN", service_name: "Giao nhanh", base_fee_amount: 45000, estimated_days_min: 1, estimated_days_max: 2, is_active: true },
      { shipping_method_code: "SAME_DAY", carrier_code: "AHA", service_name: "Giao trong ngày", base_fee_amount: 60000, estimated_days_min: 0, estimated_days_max: 1, is_active: true },
    ], "shipping_methods");
    remember("ShippingMethod", methods, ["shipping_method_code"]);
  }

  let checkouts = [];
  if (CheckoutSession && carts.length) {
    const docs = carts.slice(0, 25).map((cart, i) => ({
      owner_type: cart.owner_type,
      guest_session_id: cart.guest_session_id,
      cart_id: cart._id,
      customer_id: cart.customer_id || idOf("Customer", i),
      guest_email: i >= 20 ? emailAt("guest", i) : "",
      guest_phone: i >= 20 ? phoneAt(2000 + i) : "",
      guest_full_name: i >= 20 ? `Khách vãng lai ${i}` : "",
      checkout_status: pick(["in_progress", "completed", "expired"], i),
      currency_code: "VND",
      selected_shipping_address_id: null,
      selected_billing_address_id: null,
      selected_shipping_method_id: null,
      selected_payment_method_id: null,
      subtotal_amount: cart.subtotal_amount || money(250000, i),
      shipping_fee_amount: i % 3 === 0 ? 0 : 30000,
      discount_amount: i % 2 === 0 ? 20000 : 0,
      applied_coupon_id: idOf("Coupon", i),
      applied_coupon_code: `KANI_${String((i % 20) + 1).padStart(4, "0")}`,
      coupon_discount_amount: i % 2 === 0 ? 20000 : 0,
      tax_amount: 0,
      total_amount: cart.total_amount || money(270000, i),
      expires_at: daysFromNow(2 + i),
    }));
    checkouts = await insertDocs(CheckoutSession, docs, "checkout_sessions");
    remember("CheckoutSession", checkouts, []);
  }

  if (CheckoutAddress && checkouts.length) {
    const ads = [];
    checkouts.forEach((cs, i) => {
      const [city, district, ward, line1] = pick(addressLocations, i);
      ads.push({
        checkout_session_id: cs._id,
        address_type: "shipping",
        recipient_name: `Người nhận ${i + 1}`,
        phone: phoneAt(3000 + i),
        address_line_1: line1,
        address_line_2: "Giao hàng tại quầy lễ tân nếu không nghe máy.",
        ward, district, city,
        country_code: "VN",
        postal_code: "700000",
        is_selected: true,
      });
    });
    const inserted = await insertDocs(CheckoutAddress, ads, "checkout_addresses");
    remember("CheckoutAddress", inserted, []);
  }

  if (CheckoutShippingMethod && checkouts.length) {
    const csm = checkouts.map((cs, i) => ({
      checkout_session_id: cs._id,
      shipping_method_id: idOf("ShippingMethod", i),
      shipping_method_code: pick(["STANDARD", "EXPRESS", "SAME_DAY"], i),
      carrier_code: pick(["GHTK", "GHN", "AHA"], i),
      service_name: pick(["Giao tiêu chuẩn", "Giao nhanh", "Giao trong ngày"], i),
      estimated_days_min: i % 2,
      estimated_days_max: 2 + (i % 4),
      shipping_fee_amount: i % 3 === 0 ? 0 : 30000,
      currency_code: "VND",
      is_selected: true,
      created_at: now(),
    }));
    remember("CheckoutShippingMethod", await insertDocs(CheckoutShippingMethod, csm, "checkout_shipping_methods"), []);
  }

  let paymentMethods = [];
  if (PaymentMethod) {
    paymentMethods = await insertDocs(PaymentMethod, Array.from({ length: 20 }, (_, i) => ({
      customer_id: idOf("Customer", i),
      payment_method_type: pick(["cod", "card", "ewallet", "bank_transfer"], i),
      provider_code: pick(["COD", "VNPAY", "MOMO", "ZALOPAY"], i),
      display_name: pick(["Thanh toán khi nhận hàng", "Thẻ nội địa", "Ví MoMo", "ZaloPay"], i),
      token_reference: uniqueCode("PMTOKEN", i),
      is_default: i % 5 === 0,
      payment_method_status: "active",
    })), "payment_methods");
    remember("PaymentMethod", paymentMethods, []);
  }

  let orders = [];
  if (Order) {
    const statuses = ["pending", "confirmed", "processing", "completed", "cancelled"];
    const docs = Array.from({ length: 30 }, (_, i) => ({
      order_number: uniqueCode("KNL_ORDER", i),
      customer_id: idOf("Customer", i),
      order_status: pick(statuses, i),
      payment_status: pick(["unpaid", "paid", "paid", "paid", "refunded"], i),
      fulfillment_status: pick(["unfulfilled", "processing", "fulfilled", "fulfilled", "cancelled"], i),
      placed_at: daysAgo((i * 3) % 90),
      confirmed_at: i % 5 !== 0 ? daysAgo((i * 3) % 90) : null,
      currency_code: "VND",
      customer_note: i % 3 === 0 ? "Giao giờ hành chính." : "Đóng gói cẩn thận giúp tôi.",
    }));
    orders = await insertDocs(Order, docs, "orders");
    remember("Order", orders, ["order_number"]);
  }

  if (OrderItem && orders.length) {
    const oi = [];
    orders.forEach((o, i) => {
      for (let j = 0; j < 2; j++) {
        const p = pick(ctx.inserted.Product, i + j);
        const v = pick(ctx.inserted.ProductVariant, i + j);
        const qty = 1 + ((i + j) % 3);
        const unit = p?.price || 100000;
        oi.push({
          order_id: o._id,
          product_id: p?._id,
          variant_id: v?._id,
          sku_snapshot: v?.sku || p?.productCode || uniqueCode("SKU", i),
          product_name_snapshot: p?.productName || `Sản phẩm ${i}`,
          variant_name_snapshot: v?.variantName || "Mặc định",
          brand_name_snapshot: pick(ctx.inserted.Brand, i)?.brandName || "Kanila",
          image_url_snapshot: "",
          quantity: qty,
          unit_list_price_amount: unit + 20000,
          unit_sale_price_amount: unit,
          unit_final_price_amount: unit,
          line_subtotal_amount: unit * qty,
          line_discount_amount: 0,
          line_total_amount: unit * qty,
          currency_code: "VND",
        });
      }
    });
    remember("OrderItem", await insertDocs(OrderItem, oi, "order_items"), []);
  }

  if (OrderAddress && orders.length) {
    const oa = [];
    orders.forEach((o, i) => {
      const [city, district, ward, line1] = pick(addressLocations, i);
      oa.push({
        order_id: o._id,
        address_type: "shipping",
        recipient_name: `Người nhận đơn ${i + 1}`,
        phone: phoneAt(4000 + i),
        address_line_1: line1,
        address_line_2: "",
        ward, district, city,
        country_code: "VN",
        postal_code: "700000",
      });
    });
    remember("OrderAddress", await insertDocs(OrderAddress, oa, "order_addresses"), []);
  }

  if (OrderStatusHistory && orders.length) {
    const oh = [];
    orders.forEach((o, i) => {
      oh.push(
        { order_id: o._id, from_status: "", to_status: "pending", changed_by_account_id: idOf("Account", 0), changed_at: o.placed_at || daysAgo(i), note: "Đơn hàng được tạo." },
        { order_id: o._id, from_status: "pending", to_status: o.order_status || "confirmed", changed_by_account_id: idOf("Account", 0), changed_at: daysAgo(i), note: "Cập nhật trạng thái đơn hàng." }
      );
    });
    remember("OrderStatusHistory", await insertDocs(OrderStatusHistory, oh, "order_status_history"), []);
  }

  if (OrderTotal && orders.length) {
    const totals = orders.map((o, i) => {
      const subtotal = money(250000, i);
      const ship = subtotal >= 500000 ? 0 : 30000;
      return {
        order_id: o._id,
        subtotal_amount: subtotal,
        item_discount_amount: i % 2 === 0 ? 20000 : 0,
        order_discount_amount: i % 3 === 0 ? 30000 : 0,
        shipping_fee_amount: ship,
        tax_amount: 0,
        grand_total_amount: subtotal + ship - (i % 2 === 0 ? 20000 : 0) - (i % 3 === 0 ? 30000 : 0),
        currency_code: "VND",
      };
    });
    remember("OrderTotal", await insertDocs(OrderTotal, totals, "order_totals"), []);
  }

  if (PaymentIntent && orders.length) {
    const intents = orders.map((o, i) => ({
      order_id: o._id,
      customer_id: idOf("Customer", i),
      provider_code: pick(["VNPAY", "MOMO", "ZALOPAY", "COD"], i),
      provider_intent_id: uniqueCode("INTENT", i),
      amount: money(250000, i),
      currency_code: "VND",
      payment_intent_status: pick(["created", "requires_payment", "succeeded", "cancelled"], i),
      expires_at: daysFromNow(1),
    }));
    remember("PaymentIntent", await insertDocs(PaymentIntent, intents, "paymentintents"), []);
  }

  if (PaymentTransaction && orders.length) {
    const tx = orders.map((o, i) => ({
      order_id: o._id,
      payment_intent_id: idOf("PaymentIntent", i),
      provider_code: pick(["VNPAY", "MOMO", "ZALOPAY", "COD"], i),
      transaction_code: uniqueCode("PAYTX", i),
      transaction_type: pick(["payment", "refund"], i),
      transaction_status: pick(["success", "pending", "failed"], i),
      amount: money(250000, i),
      currency_code: "VND",
      paid_at: i % 3 === 2 ? null : daysAgo(i),
      raw_response_json: { seed: true, gateway: pick(["VNPAY", "MOMO", "ZALOPAY"], i) },
    }));
    remember("PaymentTransaction", await insertDocs(PaymentTransaction, tx, "paymenttransactions"), []);
  }
}

async function seedFulfillmentReturnsReviewsLoyalty() {
  const Shipment = M("Shipment");
  const ShipmentEvent = M("ShipmentEvent");
  const ShipmentItem = M("ShipmentItem");
  const Return = M("Return");
  const ReturnItem = M("ReturnItem");
  const Refund = M("Refund");
  const Review = M("Review");
  const ReviewMedia = M("ReviewMedia");
  const ReviewSummary = M("ReviewSummary");
  const ReviewVote = M("ReviewVote");
  const Wishlist = M("Wishlist");
  const WishlistItem = M("WishlistItem");
  const LoyaltyTier = M("LoyaltyTier");
  const LoyaltyAccount = M("LoyaltyAccount");
  const LoyaltyPointLedger = M("LoyaltyPointLedger");

  let shipments = [];
  if (Shipment) {
    shipments = await insertDocs(Shipment, (ctx.inserted.Order || []).slice(0, 25).map((o, i) => ({
      order_id: o._id,
      shipmentNumber: uniqueCode("SHP", i),
      carrierCode: pick(["GHTK", "GHN", "VNPOST", "JNT"], i),
      serviceName: pick(["Tiêu chuẩn", "Nhanh", "Hỏa tốc"], i),
      trackingNumber: uniqueCode("TRACK", i),
      shipmentStatus: pick(["created", "picked_up", "in_transit", "delivered"], i),
      shippedAt: daysAgo(i),
      deliveredAt: i % 4 === 3 ? daysAgo(Math.max(0, i - 2)) : null,
      shippingFeeAmount: 30000,
    })), "shipments");
    remember("Shipment", shipments, ["shipmentNumber", "trackingNumber"]);
  }

  if (ShipmentEvent && shipments.length) {
    const ev = [];
    shipments.forEach((s, i) => {
      ev.push(
        { shipment_id: s._id, event_code: "CREATED", event_name: "Đã tạo vận đơn", event_description: "Đơn vị vận chuyển đã nhận thông tin.", event_time: daysAgo(i + 2), location: "Kho Kanila" },
        { shipment_id: s._id, event_code: "IN_TRANSIT", event_name: "Đang vận chuyển", event_description: "Đơn hàng đang trên đường giao.", event_time: daysAgo(i + 1), location: "Trung tâm phân loại" }
      );
    });
    remember("ShipmentEvent", await insertDocs(ShipmentEvent, ev, "shipmentevents"), []);
  }

  if (ShipmentItem && shipments.length) {
    const si = shipments.map((s, i) => ({
      shipment_id: s._id,
      order_item_id: idOf("OrderItem", i),
      quantity: 1 + (i % 2),
    }));
    remember("ShipmentItem", await insertDocs(ShipmentItem, si, "shipmentitems"), []);
  }

  let returns = [];
  if (Return) {
    returns = await insertDocs(Return, (ctx.inserted.Order || []).slice(0, 20).map((o, i) => ({
      return_number: uniqueCode("RTN", i),
      order_id: o._id,
      customer_id: idOf("Customer", i),
      return_reason_code: pick(["wrong_item", "damaged", "missing_item", "allergy", "changed_mind"], i),
      return_status: pick(["requested", "approved", "rejected", "received", "completed"], i),
      requested_at: daysAgo(i),
      approved_at: i % 5 !== 2 ? daysAgo(Math.max(0, i - 1)) : null,
      customer_note: "Yêu cầu đổi/trả mẫu để kiểm thử.",
    })), "returns");
    remember("Return", returns, ["return_number"]);
  }

  if (ReturnItem && returns.length) {
    const ri = returns.map((r, i) => ({
      return_id: r._id,
      order_item_id: idOf("OrderItem", i),
      product_id: idOf("Product", i),
      variant_id: idOf("ProductVariant", i),
      quantity: 1,
      item_condition: pick(["sealed", "opened", "damaged"], i),
      resolution_type: pick(["refund", "exchange", "reject"], i),
      note: "Dòng sản phẩm đổi/trả mẫu.",
    }));
    remember("ReturnItem", await insertDocs(ReturnItem, ri, "returnitems"), []);
  }

  if (Refund && returns.length) {
    const rf = returns.map((r, i) => ({
      refund_number: uniqueCode("RFD", i),
      return_id: r._id,
      order_id: r.order_id,
      customer_id: r.customer_id,
      refund_status: pick(["pending", "processing", "completed", "rejected"], i),
      refund_method: pick(["original_payment", "bank_transfer", "wallet"], i),
      refund_amount: 100000 + i * 5000,
      currency_code: "VND",
      requested_at: daysAgo(i),
      refunded_at: i % 4 === 2 ? daysAgo(Math.max(0, i - 1)) : null,
      note: "Hoàn tiền mẫu.",
    }));
    remember("Refund", await insertDocs(Refund, rf, "refunds"), []);
  }

  let reviews = [];
  if (Review) {
    reviews = await insertDocs(Review, Array.from({ length: 40 }, (_, i) => ({
      customer_id: idOf("Customer", i),
      productId: idOf("Product", i),
      product_id: idOf("Product", i),
      order_id: idOf("Order", i),
      rating: 3 + (i % 3),
      reviewTitle: pick(["Rất thích", "Sản phẩm ổn", "Giao hàng nhanh", "Đáng mua"], i),
      reviewContent: `Đánh giá mẫu số ${i + 1}. Sản phẩm dùng ổn, đóng gói cẩn thận và đúng mô tả.`,
      reviewStatus: pick(["approved", "approved", "pending"], i),
      verifiedPurchaseFlag: i % 4 !== 0,
      helpfulCount: i % 30,
      approvedByAccountId: idOf("Account", 0),
      approvedAt: daysAgo(i),
    })), "reviews");
    remember("Review", reviews, []);
  }

  if (ReviewMedia && reviews.length) {
    const rm = reviews.slice(0, 20).map((r, i) => ({
      reviewId: r._id,
      mediaType: "image",
      mediaUrl: imageUrl(`review-${i}`, true),
      sortOrder: 0,
    }));
    remember("ReviewMedia", await insertDocs(ReviewMedia, rm, "reviewmedias"), []);
  }

  if (ReviewSummary) {
    const summaries = (ctx.inserted.Product || []).slice(0, 30).map((p, i) => ({
      product_id: p._id,
      productId: p._id,
      averageRating: 4 + ((i % 8) / 10),
      reviewCount: 10 + i,
      rating1Count: i % 2,
      rating2Count: i % 3,
      rating3Count: 2 + (i % 4),
      rating4Count: 5 + (i % 5),
      rating5Count: 10 + (i % 10),
      updatedAt: now(),
    }));
    remember("ReviewSummary", await insertDocs(ReviewSummary, summaries, "reviewsummaries"), []);
  }

  if (ReviewVote && reviews.length) {
    const votes = Array.from({ length: 40 }, (_, i) => ({
      reviewId: idOf("Review", i),
      customer_id: idOf("Customer", i + 1),
      voteType: pick(["helpful", "not_helpful"], i),
      createdAt: daysAgo(i),
    }));
    remember("ReviewVote", await insertDocs(ReviewVote, votes, "reviewvotes"), []);
  }

  if (Wishlist) {
    const wishlists = (ctx.inserted.Customer || []).slice(0, 25).map((c, i) => ({
      customer_id: c._id,
      wishlist_name: "Yêu thích",
      wishlist_status: "active",
      item_count: 2 + (i % 4),
    }));
    remember("Wishlist", await insertDocs(Wishlist, wishlists, "wishlists"), []);
  }

  if (WishlistItem && ctx.inserted.Wishlist?.length) {
    const wi = [];
    ctx.inserted.Wishlist.forEach((w, i) => {
      for (let j = 0; j < 2; j++) {
        wi.push({
          wishlist_id: w._id,
          product_id: idOf("Product", i + j),
          variant_id: idOf("ProductVariant", i + j),
          added_at: daysAgo(i + j),
        });
      }
    });
    remember("WishlistItem", await insertDocs(WishlistItem, wi, "wishlistitems"), []);
  }

  if (LoyaltyTier) {
    const tiers = await insertDocs(LoyaltyTier, [
      { tierCode: "BRONZE", tierName: "Bronze", minPoints: 0, maxPoints: 999, rewardMultiplier: 1, tierStatus: "active" },
      { tierCode: "SILVER", tierName: "Silver", minPoints: 1000, maxPoints: 2999, rewardMultiplier: 1.2, tierStatus: "active" },
      { tierCode: "GOLD", tierName: "Gold", minPoints: 3000, maxPoints: 7999, rewardMultiplier: 1.5, tierStatus: "active" },
      { tierCode: "DIAMOND", tierName: "Diamond", minPoints: 8000, maxPoints: 999999, rewardMultiplier: 2, tierStatus: "active" },
    ], "loyaltytiers");
    remember("LoyaltyTier", tiers, ["tierCode"]);
  }

  if (LoyaltyAccount) {
    const la = (ctx.inserted.Customer || []).slice(0, 25).map((c, i) => ({
      customer_id: c._id,
      tierId: idOf("LoyaltyTier", i),
      pointsBalance: 100 + i * 50,
      lifetimePoints: 300 + i * 80,
      loyaltyStatus: "active",
      joinedAt: daysAgo(100 - i),
    }));
    remember("LoyaltyAccount", await insertDocs(LoyaltyAccount, la, "loyaltyaccounts"), []);
  }

  if (LoyaltyPointLedger) {
    const ledger = Array.from({ length: 40 }, (_, i) => ({
      loyaltyAccountId: idOf("LoyaltyAccount", i),
      customer_id: idOf("Customer", i),
      order_id: idOf("Order", i),
      transactionType: pick(["earn", "redeem", "adjust"], i),
      pointsChange: pick([50, 100, -30, 20], i),
      balanceAfter: 500 + i * 10,
      reasonCode: pick(["order_completed", "voucher_redeem", "admin_adjust"], i),
      createdAt: daysAgo(i),
    }));
    remember("LoyaltyPointLedger", await insertDocs(LoyaltyPointLedger, ledger, "loyaltypointledgers"), []);
  }
}

async function seedRecommendationLogsSnapshotsAudit() {
  const CustomerRecommendationSnapshot = M("CustomerRecommendationSnapshot");
  const RecommendationLog = M("RecommendationLog");
  const AuditLog = M("AuditLog");

  if (CustomerRecommendationSnapshot) {
    const snapshots = (ctx.inserted.Customer || []).slice(0, 25).map((c, i) => {
      const products = [idOf("Product", i), idOf("Product", i + 1), idOf("Product", i + 2)];
      const profile = pick(ctx.inserted.CustomerBeautyProfile || [], i);
      return {
        customer_id: c._id,
        recommendation_type: "skin_profile_homepage",
        profile_hash: profile?.profile_hash || md5({ customer_id: c._id, i }),
        product_ids: products,
        items: products.map((pid, j) => ({
          product_id: pid,
          reasons: [pick(["Phù hợp loại da", "Phù hợp ngân sách", "Có thành phần yêu thích"], j)],
          caution_reasons: j === 2 ? ["Nên patch test trước khi dùng"] : [],
          matched_attributes: [pick(["skin_type", "beauty_goal", "preferred_ingredient"], j)],
          reason_codes: [pick(["MATCH_SKIN", "MATCH_GOAL", "MATCH_INGREDIENT"], j)],
          badges: [pick(["Phù hợp với bạn", "Best seller", "Da nhạy cảm"], j)],
          score_breakdown: { skin: 40 + j, budget: 20, ingredient: 25 },
        })),
        algorithm_version: "rule_v1",
        generated_at: now(),
        expires_at: daysFromNow(7),
        invalidated_at: null,
      };
    });
    remember("CustomerRecommendationSnapshot", await insertDocs(CustomerRecommendationSnapshot, snapshots, "customer_recommendation_snapshots"), []);
  }

  if (RecommendationLog) {
    const logs = Array.from({ length: 30 }, (_, i) => ({
      customer_id: idOf("Customer", i),
      profile_hash: pick(ctx.inserted.CustomerBeautyProfile || [], i)?.profile_hash || md5({ i }),
      query_text: "Gợi ý sản phẩm phù hợp với da của tôi",
      recommendation_type: pick(["skin_profile_homepage", "product_detail", "chatbot"], i),
      result_product_ids: [idOf("Product", i), idOf("Product", i + 1)],
      algorithm_version: "rule_v1",
      response_json: { seed: true, matched: true },
      created_at: daysAgo(i),
    }));
    remember("RecommendationLog", await insertDocs(RecommendationLog, logs, "recommendation_logs"), []);
  }

  if (AuditLog) {
    const audits = Array.from({ length: 40 }, (_, i) => ({
      actor_account_id: idOf("Account", i),
      action_code: pick(["CREATE", "UPDATE", "LOGIN", "APPROVE", "EXPORT"], i),
      entity_name: pick(["Product", "Order", "Customer", "Promotion", "Review"], i),
      entity_id: String(idOf("Product", i)),
      old_values_json: { before: i },
      new_values_json: { after: i + 1 },
      ip_address: `127.0.0.${(i % 200) + 1}`,
      user_agent: "Kanila Seed Script",
      created_at: daysAgo(i),
    }));
    remember("AuditLog", await insertDocs(AuditLog, audits, "audit_logs"), []);
  }
}

/* ──────────────────────────────────────────────────────────────
 * Fallback seeder for any model not already seeded
 * ────────────────────────────────────────────────────────────── */
async function seedRemainingModels() {
  const skip = new Set([
    "PasswordResetOtp", // Deprecated by passwordless email authentication. Do not seed password reset OTP.
  ]);

  const already = new Set(Object.keys(ctx.inserted));
  for (const [name, Model] of Object.entries(mongoose.models)) {
    if (skip.has(name)) {
      console.log(`⏭️  Skip ${name}: deprecated password reset/auth-by-password table`);
      continue;
    }

    const existingCount = await Model.countDocuments().catch(() => 0);
    if (existingCount >= Math.min(SEED_SIZE, 20)) {
      if (!already.has(name)) {
        const sample = await Model.find({}).limit(Math.min(existingCount, 30));
        remember(name, sample, []);
      }
      continue;
    }

    const need = Math.max(0, Math.min(SEED_SIZE, 20) - existingCount);
    if (need > 0 && !already.has(name)) {
      const inserted = await seedGeneric(Model, need);
      remember(name, inserted, []);
    }
  }
}

/* ──────────────────────────────────────────────────────────────
 * Cleaning
 * ────────────────────────────────────────────────────────────── */
async function cleanCollections() {
  if (!CLEAN_DATABASE) {
    console.log("ℹ️  SEED_CLEAN_DATABASE=false, existing data will be kept.");
    return;
  }

  const protectedCollections = new Set([
    // Add a collection name here if you want to keep it.
  ]);

  const models = Object.entries(mongoose.models);
  for (const [name, Model] of models) {
    if (name === "PasswordResetOtp") continue;
    const col = collectionName(Model);
    if (!col || protectedCollections.has(col)) continue;
    try {
      await Model.deleteMany({});
      console.log(`🗑️  Cleared ${col}`);
    } catch (err) {
      console.warn(`⚠️  Could not clear ${col}: ${err.message}`);
    }
  }
}

/* ──────────────────────────────────────────────────────────────
 * Main
 * ────────────────────────────────────────────────────────────── */
async function seed() {
  try {
    loadAllModels();

    const AccountModelForAudit = M("Account");
    if (AccountModelForAudit && hasPath(AccountModelForAudit, "password_hash")) {
      throw new Error("Account schema still contains password_hash. Please remove password_hash from account.model.js before running this passwordless seed file.");
    }

    console.log("═══════════════════════════════════════");
    console.log("  KANILA COMPREHENSIVE SEED DATA");
    console.log("═══════════════════════════════════════");
    console.log(`Loaded models: ${Object.keys(mongoose.models).sort().join(", ")}`);

    await mongoose.connect(MONGO_URI);
    console.log("✅ Connected to MongoDB");

    await cleanCollections();

    // Core dependency order
    await seedBrands();
    await seedCategories();
    await seedBeautyReferences();
    await seedRolesPermissionsAccounts();
    await seedCustomersAndProfiles();
    await seedProductsCatalog();
    await seedPricingPromotion();

    // Cart/order/payment flow depends on products, customers, coupons, variants.
    await seedCartCheckoutOrdersPayments();

    // Inventory can use variants and checkout sessions.
    await seedInventory();

    // Fulfillment, returns, reviews, loyalty depend on orders/products/customers.
    await seedFulfillmentReturnsReviewsLoyalty();

    // AI/recommendation/audit logs last.
    await seedRecommendationLogsSnapshotsAudit();

    // Make sure all remaining detected models get at least 20 records where feasible.
    await seedRemainingModels();

    console.log("\n═══════════════════════════════════════");
    console.log("  🎉 KANILA SEED DATA COMPLETE");
    console.log("═══════════════════════════════════════");

    for (const [name, Model] of Object.entries(mongoose.models).sort()) {
      try {
        const count = await Model.countDocuments();
        console.log(`${name.padEnd(36)} ${String(collectionName(Model)).padEnd(36)} ${count}`);
      } catch {
        // ignore
      }
    }

    console.log("═══════════════════════════════════════\n");
  } catch (err) {
    console.error("❌ Seed failed:", err);
    process.exitCode = 1;
  } finally {
    await mongoose.disconnect();
    console.log("Disconnected from MongoDB.");
  }
}

seed();
