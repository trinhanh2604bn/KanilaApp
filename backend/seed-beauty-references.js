/**
 * KANILA — Beauty References Seed Script
 *
 * Seeds the `beauty_references` collection with standardized reference data.
 * All reference_codes are UPPER_SNAKE_CASE.
 * Vietnamese display names are included for localization.
 *
 * Run: node seed-beauty-references.js
 *
 * Safe to re-run: uses upsert on (reference_group, reference_code) unique index.
 */
require("dotenv").config();
const mongoose = require("mongoose");
const BeautyReference = require("./models/beautyReference.model");

const MONGO_URI = process.env.MONGO_URI;

// ─── SEED DATA ────────────────────────────────────────────────────────────────

const beautyReferencesData = [

  // ═══════════════════════════════════════════════════════
  // GROUP: skin_type
  // Single-select — customer's primary skin type
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "skin_type",
    reference_code: "OILY_SKIN",
    display_name_vi: "Da dầu",
    display_name_en: "Oily Skin",
    description: "Da tiết nhiều bã nhờn, lỗ chân lông to, dễ nổi mụn và bóng nhờn.",
    icon_url: "https://cdn.kanila.vn/icons/skin-type/oily.svg",
    sort_order: 1,
    is_multi_select: false,
    recommendation_weight: 1.2,
    boost_tags: ["oil_control", "mattifying", "pore_minimizing"],
    avoid_tags: ["heavy_cream", "coconut_oil", "shea_butter"],
  },
  {
    reference_group: "skin_type",
    reference_code: "DRY_SKIN",
    display_name_vi: "Da khô",
    display_name_en: "Dry Skin",
    description: "Da thiếu độ ẩm, dễ bong tróc, cảm giác căng và thô ráp.",
    icon_url: "https://cdn.kanila.vn/icons/skin-type/dry.svg",
    sort_order: 2,
    is_multi_select: false,
    recommendation_weight: 1.2,
    boost_tags: ["hydrating", "moisturizing", "nourishing", "barrier_repair"],
    avoid_tags: ["alcohol", "clay", "harsh_exfoliant"],
  },
  {
    reference_group: "skin_type",
    reference_code: "COMBINATION_SKIN",
    display_name_vi: "Da hỗn hợp",
    display_name_en: "Combination Skin",
    description: "Vùng chữ T dầu (trán, mũi, cằm) trong khi má thường khô hoặc bình thường.",
    icon_url: "https://cdn.kanila.vn/icons/skin-type/combination.svg",
    sort_order: 3,
    is_multi_select: false,
    recommendation_weight: 1.1,
    boost_tags: ["balancing", "lightweight", "oil_control"],
    avoid_tags: ["heavy_cream"],
  },
  {
    reference_group: "skin_type",
    reference_code: "NORMAL_SKIN",
    display_name_vi: "Da thường",
    display_name_en: "Normal Skin",
    description: "Da cân bằng, không quá dầu cũng không quá khô, ít gặp vấn đề về da.",
    icon_url: "https://cdn.kanila.vn/icons/skin-type/normal.svg",
    sort_order: 4,
    is_multi_select: false,
    recommendation_weight: 1.0,
    boost_tags: ["maintenance", "balanced"],
    avoid_tags: [],
  },
  {
    reference_group: "skin_type",
    reference_code: "SENSITIVE_SKIN",
    display_name_vi: "Da nhạy cảm",
    display_name_en: "Sensitive Skin",
    description: "Da dễ bị kích ứng, đỏ, ngứa hoặc bỏng rát khi tiếp xúc với các thành phần mạnh.",
    icon_url: "https://cdn.kanila.vn/icons/skin-type/sensitive.svg",
    sort_order: 5,
    is_multi_select: false,
    recommendation_weight: 1.3,
    boost_tags: ["gentle", "hypoallergenic", "fragrance_free", "calming"],
    avoid_tags: ["alcohol", "fragrance", "essential_oil", "retinol_high"],
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: skin_concern
  // Multi-select — specific skin concerns the customer has
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "skin_concern",
    reference_code: "ACNE",
    display_name_vi: "Mụn",
    display_name_en: "Acne",
    description: "Da dễ nổi mụn viêm, mụn đầu đen hoặc mụn đầu trắng.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/acne.svg",
    sort_order: 1,
    is_multi_select: true,
    recommendation_weight: 1.3,
    boost_tags: ["anti_acne", "salicylic_acid", "niacinamide", "tea_tree"],
    avoid_tags: ["comedogenic", "heavy_oil"],
  },
  {
    reference_group: "skin_concern",
    reference_code: "DARK_SPOT",
    display_name_vi: "Thâm nám",
    display_name_en: "Dark Spots",
    description: "Các vết thâm, nám, tàn nhang hoặc tăng sắc tố trên da.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/dark-spot.svg",
    sort_order: 2,
    is_multi_select: true,
    recommendation_weight: 1.2,
    boost_tags: ["brightening", "vitamin_c", "niacinamide", "kojic_acid", "spf"],
    avoid_tags: [],
  },
  {
    reference_group: "skin_concern",
    reference_code: "DULLNESS",
    display_name_vi: "Da xỉn màu",
    display_name_en: "Dullness",
    description: "Da thiếu sức sống, không sáng bóng, trông mệt mỏi và xỉn màu.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/dullness.svg",
    sort_order: 3,
    is_multi_select: true,
    recommendation_weight: 1.1,
    boost_tags: ["brightening", "radiance", "exfoliating", "vitamin_c", "aha"],
    avoid_tags: [],
  },
  {
    reference_group: "skin_concern",
    reference_code: "LARGE_PORES",
    display_name_vi: "Lỗ chân lông to",
    display_name_en: "Large Pores",
    description: "Lỗ chân lông trông rõ, đặc biệt ở vùng mũi và má.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/large-pores.svg",
    sort_order: 4,
    is_multi_select: true,
    recommendation_weight: 1.1,
    boost_tags: ["pore_minimizing", "niacinamide", "salicylic_acid", "astringent"],
    avoid_tags: ["heavy_cream", "comedogenic"],
  },
  {
    reference_group: "skin_concern",
    reference_code: "BLACKHEADS",
    display_name_vi: "Mụn đầu đen",
    display_name_en: "Blackheads",
    description: "Các nốt mụn đầu đen ở mũi và vùng chữ T do bã nhờn và tế bào chết tích tụ.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/blackheads.svg",
    sort_order: 5,
    is_multi_select: true,
    recommendation_weight: 1.1,
    boost_tags: ["pore_cleansing", "bha", "salicylic_acid", "charcoal"],
    avoid_tags: ["comedogenic"],
  },
  {
    reference_group: "skin_concern",
    reference_code: "WRINKLES",
    display_name_vi: "Nếp nhăn",
    display_name_en: "Wrinkles",
    description: "Nếp nhăn và đường biểu hiện trên da, đặc biệt quanh mắt và miệng.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/wrinkles.svg",
    sort_order: 6,
    is_multi_select: true,
    recommendation_weight: 1.2,
    boost_tags: ["anti_aging", "retinol", "peptide", "hyaluronic_acid", "collagen"],
    avoid_tags: [],
  },
  {
    reference_group: "skin_concern",
    reference_code: "DEHYDRATION",
    display_name_vi: "Da mất nước",
    display_name_en: "Dehydration",
    description: "Da thiếu nước (khác với da khô), căng tức và sần sùi dù da dầu.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/dehydration.svg",
    sort_order: 7,
    is_multi_select: true,
    recommendation_weight: 1.2,
    boost_tags: ["hydrating", "hyaluronic_acid", "glycerin", "ceramide"],
    avoid_tags: ["alcohol", "harsh_exfoliant"],
  },
  {
    reference_group: "skin_concern",
    reference_code: "REDNESS",
    display_name_vi: "Da đỏ ửng",
    display_name_en: "Redness",
    description: "Da thường xuyên đỏ ửng, kích ứng hoặc có biểu hiện giãn mao mạch.",
    icon_url: "https://cdn.kanila.vn/icons/skin-concern/redness.svg",
    sort_order: 8,
    is_multi_select: true,
    recommendation_weight: 1.2,
    boost_tags: ["calming", "anti_redness", "centella", "allantoin", "green_tea"],
    avoid_tags: ["alcohol", "fragrance", "menthol"],
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: sensitivity_level
  // Single-select — how reactive the customer's skin is
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "sensitivity_level",
    reference_code: "LOW",
    display_name_vi: "Ít nhạy cảm",
    display_name_en: "Low Sensitivity",
    description: "Da ít phản ứng với sản phẩm mới, chịu được hầu hết các thành phần.",
    icon_url: "https://cdn.kanila.vn/icons/sensitivity/low.svg",
    sort_order: 1,
    is_multi_select: false,
    recommendation_weight: 1.0,
  },
  {
    reference_group: "sensitivity_level",
    reference_code: "MEDIUM",
    display_name_vi: "Nhạy cảm vừa",
    display_name_en: "Medium Sensitivity",
    description: "Da đôi khi phản ứng với một số thành phần nhất định, cần thử trước khi dùng.",
    icon_url: "https://cdn.kanila.vn/icons/sensitivity/medium.svg",
    sort_order: 2,
    is_multi_select: false,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "sensitivity_level",
    reference_code: "HIGH",
    display_name_vi: "Khá nhạy cảm",
    display_name_en: "High Sensitivity",
    description: "Da thường phản ứng với nhiều thành phần, cần sản phẩm lành tính.",
    icon_url: "https://cdn.kanila.vn/icons/sensitivity/high.svg",
    sort_order: 3,
    is_multi_select: false,
    recommendation_weight: 1.2,
    boost_tags: ["gentle", "hypoallergenic", "fragrance_free"],
    avoid_tags: ["alcohol", "fragrance", "essential_oil"],
  },
  {
    reference_group: "sensitivity_level",
    reference_code: "REACTIVE",
    display_name_vi: "Rất nhạy cảm / Phản ứng mạnh",
    display_name_en: "Reactive Skin",
    description: "Da cực kỳ nhạy cảm, dễ bị dị ứng, kích ứng, hoặc có bệnh da như rosacea.",
    icon_url: "https://cdn.kanila.vn/icons/sensitivity/reactive.svg",
    sort_order: 4,
    is_multi_select: false,
    recommendation_weight: 1.4,
    boost_tags: ["hypoallergenic", "dermatologist_tested", "minimal_ingredients"],
    avoid_tags: ["alcohol", "fragrance", "essential_oil", "retinol", "aha", "bha"],
    warning_text: "Nên tham khảo ý kiến bác sĩ da liễu trước khi dùng sản phẩm mới.",
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: skin_color
  // Single-select — customer's natural skin tone shade
  // Renamed from: skin_tone
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "skin_color",
    reference_code: "FAIR",
    display_name_vi: "Trắng hồng",
    display_name_en: "Fair",
    description: "Làn da rất sáng, dễ bị đỏ khi ra nắng.",
    icon_url: "https://cdn.kanila.vn/icons/skin-color/fair.svg",
    sort_order: 1,
    is_multi_select: false,
  },
  {
    reference_group: "skin_color",
    reference_code: "LIGHT",
    display_name_vi: "Trắng sáng",
    display_name_en: "Light",
    description: "Làn da sáng, tông nhẹ, phổ biến ở người Đông Nam Á.",
    icon_url: "https://cdn.kanila.vn/icons/skin-color/light.svg",
    sort_order: 2,
    is_multi_select: false,
  },
  {
    reference_group: "skin_color",
    reference_code: "MEDIUM",
    display_name_vi: "Tông trung",
    display_name_en: "Medium",
    description: "Làn da trung tính, không quá sáng cũng không quá sậm.",
    icon_url: "https://cdn.kanila.vn/icons/skin-color/medium.svg",
    sort_order: 3,
    is_multi_select: false,
  },
  {
    reference_group: "skin_color",
    reference_code: "TAN",
    display_name_vi: "Ngăm",
    display_name_en: "Tan",
    description: "Làn da ngăm vàng hoặc nâu nhẹ, phổ biến ở khu vực nhiệt đới.",
    icon_url: "https://cdn.kanila.vn/icons/skin-color/tan.svg",
    sort_order: 4,
    is_multi_select: false,
  },
  {
    reference_group: "skin_color",
    reference_code: "DEEP",
    display_name_vi: "Sậm màu",
    display_name_en: "Deep",
    description: "Làn da đậm màu, sắc tố da cao.",
    icon_url: "https://cdn.kanila.vn/icons/skin-color/deep.svg",
    sort_order: 5,
    is_multi_select: false,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: skin_undertone
  // Single-select — the underlying tone beneath the skin
  // Renamed from: undertone
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "skin_undertone",
    reference_code: "COOL",
    display_name_vi: "Tông lạnh",
    display_name_en: "Cool",
    description: "Tông da có ánh hồng, đỏ hoặc xanh. Hợp màu bạc, hồng nhạt, berry.",
    icon_url: "https://cdn.kanila.vn/icons/undertone/cool.svg",
    sort_order: 1,
    is_multi_select: false,
  },
  {
    reference_group: "skin_undertone",
    reference_code: "WARM",
    display_name_vi: "Tông ấm",
    display_name_en: "Warm",
    description: "Tông da có ánh vàng, đào hoặc cam. Hợp màu vàng, cam, nude đào.",
    icon_url: "https://cdn.kanila.vn/icons/undertone/warm.svg",
    sort_order: 2,
    is_multi_select: false,
  },
  {
    reference_group: "skin_undertone",
    reference_code: "NEUTRAL",
    display_name_vi: "Tông trung tính",
    display_name_en: "Neutral",
    description: "Tông da cân bằng giữa ấm và lạnh. Hợp hầu hết các màu sắc.",
    icon_url: "https://cdn.kanila.vn/icons/undertone/neutral.svg",
    sort_order: 3,
    is_multi_select: false,
  },
  {
    reference_group: "skin_undertone",
    reference_code: "OLIVE",
    display_name_vi: "Tông olive",
    display_name_en: "Olive",
    description: "Tông da có ánh xanh lá nhẹ, phổ biến ở người Địa Trung Hải và Đông Nam Á.",
    icon_url: "https://cdn.kanila.vn/icons/undertone/olive.svg",
    sort_order: 4,
    is_multi_select: false,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: foundation_finish
  // Single-select — preferred makeup finish for foundation
  // Renamed from: finish_preference
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "foundation_finish",
    reference_code: "NATURAL",
    display_name_vi: "Tự nhiên",
    display_name_en: "Natural",
    description: "Finish tự nhiên, cân bằng giữa matte và dewy. Phù hợp hầu hết loại da.",
    icon_url: "https://cdn.kanila.vn/icons/finish/natural.svg",
    sort_order: 1,
    is_multi_select: false,
  },
  {
    reference_group: "foundation_finish",
    reference_code: "DEWY",
    display_name_vi: "Căng bóng",
    display_name_en: "Dewy",
    description: "Finish bóng mịn, tạo cảm giác da căng mượt, tươi tắn. Phù hợp da khô.",
    icon_url: "https://cdn.kanila.vn/icons/finish/dewy.svg",
    sort_order: 2,
    is_multi_select: false,
  },
  {
    reference_group: "foundation_finish",
    reference_code: "MATTE",
    display_name_vi: "Lì mờ",
    display_name_en: "Matte",
    description: "Finish lì, kiềm dầu tốt, không bóng nhờn. Phù hợp da dầu, hỗn hợp.",
    icon_url: "https://cdn.kanila.vn/icons/finish/matte.svg",
    sort_order: 3,
    is_multi_select: false,
  },
  {
    reference_group: "foundation_finish",
    reference_code: "SATIN",
    display_name_vi: "Lụa mịn",
    display_name_en: "Satin",
    description: "Finish satin mịn, giữa matte và dewy. Tạo cảm giác da mịn màng thanh lịch.",
    icon_url: "https://cdn.kanila.vn/icons/finish/satin.svg",
    sort_order: 4,
    is_multi_select: false,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: lipstick_color
  // Multi-select — preferred lipstick/lip color shades
  // Renamed from: lip_color_preference
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "lipstick_color",
    reference_code: "NUDE",
    display_name_vi: "Nude",
    display_name_en: "Nude",
    description: "Màu son gần với tông môi tự nhiên, thanh lịch và dễ phối đồ.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/nude.svg",
    sort_order: 1,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "PINK",
    display_name_vi: "Hồng",
    display_name_en: "Pink",
    description: "Các tone hồng từ hồng baby đến hồng đậm, dễ thương và nữ tính.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/pink.svg",
    sort_order: 2,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "CORAL",
    display_name_vi: "Cam san hô",
    display_name_en: "Coral",
    description: "Màu cam pha hồng, tươi tắn, phù hợp da ngăm hoặc tông ấm.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/coral.svg",
    sort_order: 3,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "RED",
    display_name_vi: "Đỏ",
    display_name_en: "Red",
    description: "Son đỏ cổ điển, nổi bật và quyến rũ. Phù hợp với nhiều dịp.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/red.svg",
    sort_order: 4,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "BROWN",
    display_name_vi: "Nâu đất",
    display_name_en: "Brown",
    description: "Các tông nâu từ nâu nhạt đến nâu đậm, phong cách thu đông sang trọng.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/brown.svg",
    sort_order: 5,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "BERRY",
    display_name_vi: "Berry / Tím mận",
    display_name_en: "Berry",
    description: "Tông tím mận, đỏ tím, cá tính và sang trọng.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/berry.svg",
    sort_order: 6,
    is_multi_select: true,
  },
  {
    reference_group: "lipstick_color",
    reference_code: "ORANGE",
    display_name_vi: "Cam",
    display_name_en: "Orange",
    description: "Son cam rực rỡ, năng động và hiện đại.",
    icon_url: "https://cdn.kanila.vn/icons/lip-color/orange.svg",
    sort_order: 7,
    is_multi_select: true,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: makeup_style
  // Multi-select — preferred overall makeup aesthetic
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "makeup_style",
    reference_code: "NATURAL",
    display_name_vi: "Tự nhiên",
    display_name_en: "Natural",
    description: "Trang điểm nhẹ nhàng, tôn vẻ tự nhiên, da căng bóng.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/natural.svg",
    sort_order: 1,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "KOREAN",
    display_name_vi: "Phong cách Hàn",
    display_name_en: "Korean (K-Beauty)",
    description: "Da căng mướt, má hồng, môi biting lip, theo xu hướng Hàn Quốc.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/korean.svg",
    sort_order: 2,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "GLAM",
    display_name_vi: "Sang trọng / Glam",
    display_name_en: "Glam",
    description: "Trang điểm đậm, nổi bật, phù hợp sự kiện đặc biệt và tiệc tùng.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/glam.svg",
    sort_order: 3,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "OFFICE",
    display_name_vi: "Công sở",
    display_name_en: "Office",
    description: "Trang điểm chuyên nghiệp, gọn gàng, phù hợp môi trường làm việc.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/office.svg",
    sort_order: 4,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "PARTY",
    display_name_vi: "Tiệc / Dạ hội",
    display_name_en: "Party",
    description: "Trang điểm rực rỡ, lấp lánh, nổi bật trong buổi tiệc hay sự kiện.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/party.svg",
    sort_order: 5,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "MINIMAL",
    display_name_vi: "Tối giản",
    display_name_en: "Minimal",
    description: "Trang điểm tối giản, ít bước, chú trọng skincare thay vì makeup.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/minimal.svg",
    sort_order: 6,
    is_multi_select: true,
  },
  {
    reference_group: "makeup_style",
    reference_code: "EDITORIAL",
    display_name_vi: "Nghệ thuật / Editorial",
    display_name_en: "Editorial",
    description: "Phong cách trang điểm sáng tạo, nghệ thuật, cá tính mạnh.",
    icon_url: "https://cdn.kanila.vn/icons/makeup-style/editorial.svg",
    sort_order: 7,
    is_multi_select: true,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: budget
  // Single-select — customer's price range preference
  // Renamed from: budget_range
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "budget",
    reference_code: "UNDER_300",
    display_name_vi: "Dưới 300.000₫",
    display_name_en: "Under 300K VND",
    description: "Ưu tiên sản phẩm bình dân, tiết kiệm, dưới 300.000 đồng.",
    icon_url: "https://cdn.kanila.vn/icons/budget/under-300.svg",
    sort_order: 1,
    is_multi_select: false,
    recommendation_weight: 1.0,
  },
  {
    reference_group: "budget",
    reference_code: "300_500",
    display_name_vi: "300.000₫ – 500.000₫",
    display_name_en: "300K – 500K VND",
    description: "Phân khúc trung cấp, cân bằng giữa chất lượng và giá cả.",
    icon_url: "https://cdn.kanila.vn/icons/budget/300-500.svg",
    sort_order: 2,
    is_multi_select: false,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "budget",
    reference_code: "500_1000",
    display_name_vi: "500.000₫ – 1.000.000₫",
    display_name_en: "500K – 1M VND",
    description: "Phân khúc cao cấp nhẹ, sản phẩm chất lượng tốt.",
    icon_url: "https://cdn.kanila.vn/icons/budget/500-1000.svg",
    sort_order: 3,
    is_multi_select: false,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "budget",
    reference_code: "OVER_1000",
    display_name_vi: "Trên 1.000.000₫",
    display_name_en: "Over 1M VND",
    description: "Sản phẩm cao cấp và luxury, không giới hạn ngân sách.",
    icon_url: "https://cdn.kanila.vn/icons/budget/over-1000.svg",
    sort_order: 4,
    is_multi_select: false,
    recommendation_weight: 1.2,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: avoid_ingredient
  // Multi-select — ingredients the customer wants to avoid
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "avoid_ingredient",
    reference_code: "ALCOHOL",
    display_name_vi: "Cồn (Alcohol)",
    display_name_en: "Alcohol",
    description: "Cồn biến tính (denatured alcohol) có thể gây khô và kích ứng da nhạy cảm.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/alcohol.svg",
    sort_order: 1,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "FRAGRANCE",
    display_name_vi: "Hương liệu / Nước hoa",
    display_name_en: "Fragrance",
    description: "Hương liệu tổng hợp hoặc thiên nhiên, nguyên nhân phổ biến gây dị ứng da.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/fragrance.svg",
    sort_order: 2,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "PARABEN",
    display_name_vi: "Paraben",
    display_name_en: "Paraben",
    description: "Chất bảo quản paraben, một số người lựa chọn tránh vì lo ngại nội tiết tố.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/paraben.svg",
    sort_order: 3,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "SULFATE",
    display_name_vi: "Sulfate (SLS/SLES)",
    display_name_en: "Sulfate",
    description: "Chất tạo bọt mạnh, có thể làm khô và kích ứng da và tóc.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/sulfate.svg",
    sort_order: 4,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "ESSENTIAL_OIL",
    display_name_vi: "Tinh dầu",
    display_name_en: "Essential Oil",
    description: "Tinh dầu thiên nhiên (tea tree, lavender...) có thể gây kích ứng da nhạy cảm.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/essential-oil.svg",
    sort_order: 5,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "RETINOL",
    display_name_vi: "Retinol / Retinoid",
    display_name_en: "Retinol",
    description: "Vitamin A dẫn xuất, mạnh nhưng có thể gây kích ứng cho da nhạy cảm.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/retinol.svg",
    sort_order: 6,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "MINERAL_OIL",
    display_name_vi: "Dầu khoáng",
    display_name_en: "Mineral Oil",
    description: "Dầu khoáng có thể gây bít tắc lỗ chân lông ở một số loại da.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/mineral-oil.svg",
    sort_order: 7,
    is_multi_select: true,
  },
  {
    reference_group: "avoid_ingredient",
    reference_code: "SILICONE",
    display_name_vi: "Silicone",
    display_name_en: "Silicone",
    description: "Silicone tạo cảm giác mượt mà nhưng một số người lo ngại tích tụ trên da.",
    icon_url: "https://cdn.kanila.vn/icons/avoid/silicone.svg",
    sort_order: 8,
    is_multi_select: true,
  },

  // ═══════════════════════════════════════════════════════
  // GROUP: beauty_goal
  // Multi-select — what the customer wants to achieve
  // ═══════════════════════════════════════════════════════
  {
    reference_group: "beauty_goal",
    reference_code: "EVEN_SKIN_TONE",
    display_name_vi: "Đều màu da",
    display_name_en: "Even Skin Tone",
    description: "Giảm thâm nám, tàn nhang, làm đều và sáng tông da tổng thể.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/even-tone.svg",
    sort_order: 1,
    is_multi_select: true,
    recommendation_weight: 1.2,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "REDUCE_ACNE",
    display_name_vi: "Giảm mụn",
    display_name_en: "Reduce Acne",
    description: "Kiểm soát và giảm thiểu mụn viêm, mụn đầu đen và ngừa tái phát.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/reduce-acne.svg",
    sort_order: 2,
    is_multi_select: true,
    recommendation_weight: 1.3,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "HYDRATION",
    display_name_vi: "Cấp ẩm",
    display_name_en: "Deep Hydration",
    description: "Bổ sung và giữ ẩm cho da, tránh tình trạng khô, căng và mất nước.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/hydration.svg",
    sort_order: 3,
    is_multi_select: true,
    recommendation_weight: 1.2,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "ANTI_AGING",
    display_name_vi: "Chống lão hóa",
    display_name_en: "Anti-Aging",
    description: "Giảm nếp nhăn, làm chắc da và ngăn ngừa dấu hiệu lão hóa.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/anti-aging.svg",
    sort_order: 4,
    is_multi_select: true,
    recommendation_weight: 1.2,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "BRIGHTENING",
    display_name_vi: "Sáng da",
    display_name_en: "Brightening",
    description: "Làm sáng da, tăng độ rạng rỡ và mang lại vẻ tươi tắn cho làn da.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/brightening.svg",
    sort_order: 5,
    is_multi_select: true,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "OIL_CONTROL",
    display_name_vi: "Kiểm soát dầu",
    display_name_en: "Oil Control",
    description: "Giảm tiết bã nhờn, giữ da khô thoáng và mờ bóng nhờn suốt ngày.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/oil-control.svg",
    sort_order: 6,
    is_multi_select: true,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "PORE_MINIMIZING",
    display_name_vi: "Se khít lỗ chân lông",
    display_name_en: "Pore Minimizing",
    description: "Làm nhỏ lỗ chân lông và cho da vẻ mịn màng hơn.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/pore-minimizing.svg",
    sort_order: 7,
    is_multi_select: true,
    recommendation_weight: 1.0,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "CALMING",
    display_name_vi: "Làm dịu da",
    display_name_en: "Calming",
    description: "Giảm đỏ ửng, kích ứng và làm dịu da nhạy cảm.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/calming.svg",
    sort_order: 8,
    is_multi_select: true,
    recommendation_weight: 1.1,
  },
  {
    reference_group: "beauty_goal",
    reference_code: "SUN_PROTECTION",
    display_name_vi: "Bảo vệ da khỏi nắng",
    display_name_en: "Sun Protection",
    description: "Bảo vệ da khỏi tia UV, ngăn ngừa cháy nắng và lão hóa do ánh sáng.",
    icon_url: "https://cdn.kanila.vn/icons/beauty-goal/sun-protection.svg",
    sort_order: 9,
    is_multi_select: true,
    recommendation_weight: 1.3,
  },
];

// ─── MAIN SEED FUNCTION ───────────────────────────────────────────────────────

async function seedBeautyReferences() {
  try {
    await mongoose.connect(MONGO_URI);
    console.log("✅ Connected to MongoDB");

    let inserted = 0;
    let updated = 0;
    let skipped = 0;

    for (const ref of beautyReferencesData) {
      const filter = {
        reference_group: ref.reference_group,
        reference_code: ref.reference_code,
      };

      const result = await BeautyReference.findOneAndUpdate(
        filter,
        {
          $set: {
            display_name_vi: ref.display_name_vi,
            display_name_en: ref.display_name_en ?? "",
            description: ref.description ?? "",
            icon_url: ref.icon_url ?? "",
            sort_order: ref.sort_order ?? 0,
            is_active: true,
            is_multi_select: ref.is_multi_select ?? true,
            recommendation_weight: ref.recommendation_weight ?? 1,
            boost_tags: ref.boost_tags ?? [],
            avoid_tags: ref.avoid_tags ?? [],
            avoid_ingredients: ref.avoid_ingredients ?? [],
            preferred_ingredients: ref.preferred_ingredients ?? [],
            warning_text: ref.warning_text ?? "",
          },
        },
        { upsert: true, new: true, rawResult: true }
      );

      if (result.lastErrorObject?.updatedExisting) {
        updated++;
        console.log(`  ↻ Updated:  [${ref.reference_group}] ${ref.reference_code}`);
      } else {
        inserted++;
        console.log(`  ✚ Inserted: [${ref.reference_group}] ${ref.reference_code}`);
      }
    }

    console.log("\n─────────────────────────────────────────");
    console.log(`✅ Seed complete`);
    console.log(`   Inserted: ${inserted}`);
    console.log(`   Updated:  ${updated}`);
    console.log(`   Skipped:  ${skipped}`);
    console.log(`   Total:    ${beautyReferencesData.length}`);

    // Summary by group
    const groups = [...new Set(beautyReferencesData.map((r) => r.reference_group))];
    console.log("\n📊 References by group:");
    for (const group of groups) {
      const count = beautyReferencesData.filter((r) => r.reference_group === group).length;
      console.log(`   ${group.padEnd(20)} ${count} records`);
    }

  } catch (error) {
    console.error("❌ Seed failed:", error.message);
    process.exit(1);
  } finally {
    await mongoose.disconnect();
    console.log("\n🔌 Disconnected from MongoDB");
  }
}

seedBeautyReferences();
