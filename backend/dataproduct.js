/**
 * dataproduct.js
 * Kanila Makeup Commerce - real makeup product catalog seed data.
 *
 * Updated scope:
 * - Kanila is treated as a makeup-commerce app, not a skincare-first app.
 * - 50 real makeup products mapped into the requested taxonomy:
 *   Face, Eyes, Lips, Cheeks, Gift, Mini & Travel.
 * - Category tree: 6 parent groups + 27 leaf categories exactly matching the requested list.
 * - Image URLs are placeholders only. Replace IMAGE_BASE_URL / LOGO_BASE_URL later.
 *
 * Seeded/mapped collections:
 * - brands: 50 real makeup brands
 * - categories: 33 taxonomy rows (6 parent groups + 27 leaf categories)
 * - products: 50 real makeup products
 * - product_beauty_profiles: 50
 * - product_categories: 50
 * - product_media: 100
 * - product_attributes: 250
 * - product_options: 50
 * - product_option_values: 100
 * - product_variants: 100
 * - variant_option_values: 100
 * - variant_medias: 100
 *
 * Usage from backend root:
 *   npm install dotenv mongoose
 *   MONGODB_URI="mongodb+srv://..." node dataproduct.js
 *
 * Optional:
 *   node dataproduct.js --reset
 *     Deletes rows with deterministic seed _id values before inserting again.
 *
 * If your models folder is not ./models from this file, set:
 *   KANILA_MODEL_BASE_PATH=../models node scripts/dataproduct.js
 */

require('dotenv').config();
const path = require('path');
const mongoose = require('mongoose');

const IMAGE_BASE_URL = process.env.KANILA_IMAGE_BASE_URL || 'https://example.com/kanila/makeup-products';
const LOGO_BASE_URL = process.env.KANILA_LOGO_BASE_URL || 'https://example.com/kanila/makeup-brands';
const SHOULD_RESET = process.argv.includes('--reset');

function objectIdFromNumber(n) {
  return new mongoose.Types.ObjectId(Number(n).toString(16).padStart(24, '0'));
}

function slugify(value) {
  return String(value)
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .replace(/&/g, ' and ')
    .replace(/\+/g, ' plus ')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .replace(/-{2,}/g, '-');
}

function safeCode(value) {
  return String(value)
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'D')
    .replace(/Đ/g, 'D')
    .toUpperCase()
    .replace(/[^A-Z0-9]+/g, '')
    .slice(0, 32);
}

function loadModel(fileName, modelName) {
  const explicitBase = process.env.KANILA_MODEL_BASE_PATH;
  const candidates = [
    explicitBase ? path.resolve(process.cwd(), explicitBase, fileName) : null,
    path.join(__dirname, 'models', fileName),
    path.join(__dirname, '..', 'models', fileName),
    path.join(process.cwd(), 'models', fileName),
    path.join(process.cwd(), 'src', 'models', fileName),
  ].filter(Boolean);

  let lastError;
  for (const candidate of candidates) {
    try {
      const exported = require(candidate);
      if (exported && exported.modelName === modelName) return exported;
      if (exported && exported[modelName]) return exported[modelName];
      if (mongoose.models[modelName]) return mongoose.models[modelName];
    } catch (error) {
      lastError = error;
    }
  }

  throw new Error(`Cannot load model ${modelName} from ${fileName}. Last error: ${lastError ? lastError.message : 'unknown'}`);
}

const Brand = loadModel('brand.model.js', 'Brand');
const Category = loadModel('category.model.js', 'Category');
const Product = loadModel('product.model.js', 'Product');
const ProductBeautyProfile = loadModel('productBeautyProfile.model.js', 'ProductBeautyProfile');
const ProductCategory = loadModel('productCategory.model.js', 'ProductCategory');
const ProductMedia = loadModel('productMedia.model.js', 'ProductMedia');
const ProductAttribute = loadModel('productAttribute.model.js', 'ProductAttribute');
const ProductOption = loadModel('productOption.model.js', 'ProductOption');
const ProductOptionValue = loadModel('productOptionValue.model.js', 'ProductOptionValue');
const ProductVariant = loadModel('productVariant.model.js', 'ProductVariant');
const VariantOptionValue = loadModel('variantOptionValue.model.js', 'VariantOptionValue');
const VariantMedia = loadModel('variantMedia.model.js', 'VariantMedia');

const brandCatalog = [
  {
    "brandCode": "MAYBELLINE",
    "brandName": "Maybelline",
    "description": "Thương hiệu makeup đại chúng nổi tiếng với foundation, mascara và son môi dễ tiếp cận."
  },
  {
    "brandCode": "LOREALPARIS",
    "brandName": "L'Oréal Paris",
    "description": "Thương hiệu làm đẹp toàn cầu với các dòng makeup nền, mắt và môi phổ biến."
  },
  {
    "brandCode": "NYX",
    "brandName": "NYX Professional Makeup",
    "description": "Thương hiệu makeup chuyên nghiệp giá dễ tiếp cận, nổi bật với eyeliner, lip liner và contour."
  },
  {
    "brandCode": "ELF",
    "brandName": "e.l.f. Cosmetics",
    "description": "Thương hiệu makeup cruelty-free nổi bật với primer, base và sản phẩm xu hướng."
  },
  {
    "brandCode": "NARS",
    "brandName": "NARS",
    "description": "Thương hiệu makeup cao cấp nổi tiếng với foundation, concealer, blush và tinted moisturizer."
  },
  {
    "brandCode": "MAC",
    "brandName": "MAC Cosmetics",
    "description": "Thương hiệu trang điểm chuyên nghiệp nổi tiếng với lipstick, lip pencil và eyeshadow."
  },
  {
    "brandCode": "FENTYBEAUTY",
    "brandName": "Fenty Beauty",
    "description": "Thương hiệu makeup nổi bật với dải màu đa dạng, gloss, contour và highlighter."
  },
  {
    "brandCode": "RAREBEAUTY",
    "brandName": "Rare Beauty",
    "description": "Thương hiệu makeup hiện đại nổi bật với blush, highlighter và sản phẩm dễ tán."
  },
  {
    "brandCode": "CHARLOTTETILBURY",
    "brandName": "Charlotte Tilbury",
    "description": "Thương hiệu makeup cao cấp nổi tiếng với lipstick, setting spray và lip liner."
  },
  {
    "brandCode": "URBANDECAY",
    "brandName": "Urban Decay",
    "description": "Thương hiệu makeup nổi tiếng với setting spray và bảng mắt Naked."
  },
  {
    "brandCode": "BENEFIT",
    "brandName": "Benefit Cosmetics",
    "description": "Thương hiệu nổi bật với brow, bronzer, primer và tint."
  },
  {
    "brandCode": "ANASTASIABEVERLYHILLS",
    "brandName": "Anastasia Beverly Hills",
    "description": "Thương hiệu chuyên về lông mày, mắt và các sản phẩm makeup chuyên nghiệp."
  },
  {
    "brandCode": "LAURAMERCIER",
    "brandName": "Laura Mercier",
    "description": "Thương hiệu makeup cao cấp nổi tiếng với setting powder và tinted moisturizer."
  },
  {
    "brandCode": "DIOR",
    "brandName": "Dior Beauty",
    "description": "Thương hiệu luxury beauty với lip gloss, lip maximizer và makeup cao cấp."
  },
  {
    "brandCode": "LANCOME",
    "brandName": "Lancôme",
    "description": "Thương hiệu beauty cao cấp với foundation, mascara và lip product."
  },
  {
    "brandCode": "ESTEELAUDER",
    "brandName": "Estée Lauder",
    "description": "Thương hiệu cao cấp nổi tiếng với Double Wear foundation."
  },
  {
    "brandCode": "ITCOSMETICS",
    "brandName": "IT Cosmetics",
    "description": "Thương hiệu base makeup nổi bật với CC cream và sản phẩm nền có skincare benefit."
  },
  {
    "brandCode": "ERBORIAN",
    "brandName": "Erborian",
    "description": "Thương hiệu K-beauty/Pháp nổi bật với BB cream, CC cream và base hybrid."
  },
  {
    "brandCode": "COLOURPOP",
    "brandName": "ColourPop",
    "description": "Thương hiệu makeup nổi tiếng với eyeshadow, palette và sản phẩm trendy."
  },
  {
    "brandCode": "KVDBEAUTY",
    "brandName": "KVD Beauty",
    "description": "Thương hiệu makeup nổi bật với eyeliner và sản phẩm sắc nét, lâu trôi."
  },
  {
    "brandCode": "ARDELL",
    "brandName": "Ardell",
    "description": "Thương hiệu mi giả phổ biến với nhiều kiểu mi tự nhiên và dramatic."
  },
  {
    "brandCode": "KISS",
    "brandName": "KISS",
    "description": "Thương hiệu mi giả, móng và beauty tool phổ biến."
  },
  {
    "brandCode": "LANEIGE",
    "brandName": "Laneige",
    "description": "Thương hiệu K-beauty nổi bật với lip balm và sản phẩm dưỡng môi."
  },
  {
    "brandCode": "BURTSBEES",
    "brandName": "Burt's Bees",
    "description": "Thương hiệu chăm sóc môi nổi tiếng với lip balm từ sáp ong."
  },
  {
    "brandCode": "ROMAND",
    "brandName": "rom&nd",
    "description": "Thương hiệu K-beauty nổi tiếng với tint, lip product và màu makeup trẻ trung."
  },
  {
    "brandCode": "PHYSICIANSFORMULA",
    "brandName": "Physicians Formula",
    "description": "Thương hiệu makeup nổi tiếng với Butter Bronzer và sản phẩm dễ dùng."
  },
  {
    "brandCode": "HOURGLASS",
    "brandName": "Hourglass",
    "description": "Thương hiệu makeup cao cấp nổi bật với ambient lighting powder và face palette."
  },
  {
    "brandCode": "NATASHADENONA",
    "brandName": "Natasha Denona",
    "description": "Thương hiệu makeup cao cấp nổi tiếng với eyeshadow palette chất lượng cao."
  },
  {
    "brandCode": "SEPHORACOLLECTION",
    "brandName": "Sephora Collection",
    "description": "Thương hiệu makeup và beauty set thuộc Sephora, đa dạng sản phẩm."
  },
  {
    "brandCode": "TARTE",
    "brandName": "Tarte Cosmetics",
    "description": "Thương hiệu makeup nổi tiếng với Shape Tape concealer và sản phẩm base."
  },
  {
    "brandCode": "MORPHE",
    "brandName": "Morphe",
    "description": "Thương hiệu makeup nổi tiếng với palette, brush và sản phẩm cho makeup look."
  },
  {
    "brandCode": "HUDABEAUTY",
    "brandName": "Huda Beauty",
    "description": "Thương hiệu makeup nổi bật với eyeshadow palette, complexion và lip product."
  },
  {
    "brandCode": "TOOFACED",
    "brandName": "Too Faced",
    "description": "Thương hiệu makeup nổi tiếng với mascara, bronzer và palette."
  },
  {
    "brandCode": "BOBBIBROWN",
    "brandName": "Bobbi Brown",
    "description": "Thương hiệu makeup cao cấp tập trung vào nền tự nhiên và màu trung tính."
  },
  {
    "brandCode": "SHISEIDO",
    "brandName": "Shiseido",
    "description": "Thương hiệu Nhật Bản cao cấp với makeup và skincare."
  },
  {
    "brandCode": "CLIO",
    "brandName": "CLIO",
    "description": "Thương hiệu K-beauty nổi tiếng với cushion, eyeliner và mascara."
  },
  {
    "brandCode": "PERIPERA",
    "brandName": "Peripera",
    "description": "Thương hiệu K-beauty nổi tiếng với ink tint và màu môi trẻ trung."
  },
  {
    "brandCode": "ETUDE",
    "brandName": "ETUDE",
    "description": "Thương hiệu K-beauty nổi bật với makeup dễ thương, mascara và tint."
  },
  {
    "brandCode": "THREECE",
    "brandName": "3CE",
    "description": "Thương hiệu makeup Hàn Quốc nổi tiếng với lipstick, palette và màu trendy."
  },
  {
    "brandCode": "MAKEUPFOREVER",
    "brandName": "MAKE UP FOR EVER",
    "description": "Thương hiệu makeup chuyên nghiệp nổi tiếng với foundation, powder và setting product."
  },
  {
    "brandCode": "PATMCGRATHLABS",
    "brandName": "Pat McGrath Labs",
    "description": "Thương hiệu makeup luxury nổi bật với eyeshadow, lipstick và highlighter."
  },
  {
    "brandCode": "MILKMAKEUP",
    "brandName": "Milk Makeup",
    "description": "Thương hiệu makeup hiện đại với stick product, primer và cream makeup."
  },
  {
    "brandCode": "TOWER28",
    "brandName": "Tower 28",
    "description": "Thương hiệu clean makeup nổi bật với gloss, blush và sản phẩm cho da nhạy cảm."
  },
  {
    "brandCode": "SAIE",
    "brandName": "Saie",
    "description": "Thương hiệu clean makeup nổi bật với tint, glow base và complexion."
  },
  {
    "brandCode": "GLOSSIER",
    "brandName": "Glossier",
    "description": "Thương hiệu makeup tối giản với skin tint, balm và cloud paint."
  },
  {
    "brandCode": "CATRICE",
    "brandName": "Catrice",
    "description": "Thương hiệu makeup drugstore châu Âu với base, concealer và powder."
  },
  {
    "brandCode": "ESSENCE",
    "brandName": "essence",
    "description": "Thương hiệu makeup drugstore nổi tiếng với mascara và sản phẩm giá tốt."
  },
  {
    "brandCode": "RIMMEL",
    "brandName": "Rimmel London",
    "description": "Thương hiệu makeup Anh Quốc nổi bật với mascara, eyeliner và complexion."
  },
  {
    "brandCode": "REVLON",
    "brandName": "Revlon",
    "description": "Thương hiệu makeup lâu đời nổi tiếng với lipstick, foundation và nail."
  },
  {
    "brandCode": "INNISFREE",
    "brandName": "Innisfree",
    "description": "Thương hiệu K-beauty Hàn Quốc nổi tiếng với các sản phẩm kiểm soát dầu và khoáng chất từ đảo Jeju."
  }
];

const categoryCatalog = [
  {
    "categoryCode": "CAT_FACE",
    "categoryName": "Face",
    "description": "Nhóm sản phẩm trang điểm nền và hoàn thiện lớp makeup mặt.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_EYES",
    "categoryName": "Eyes",
    "description": "Nhóm sản phẩm trang điểm mắt, lông mày và mi giả.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_LIPS",
    "categoryName": "Lips",
    "description": "Nhóm sản phẩm trang điểm và chăm sóc môi.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_CHEEKS",
    "categoryName": "Cheeks",
    "description": "Nhóm sản phẩm tạo màu, tạo khối và bắt sáng gò má.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_GIFT",
    "categoryName": "Gift",
    "description": "Nhóm sản phẩm dạng set, palette hoặc quà tặng makeup.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_MINI_TRAVEL",
    "categoryName": "Mini & Travel",
    "description": "Nhóm sản phẩm mini size, travel size và trial kits.",
    "parentCode": null
  },
  {
    "categoryCode": "CAT_FOUNDATION",
    "categoryName": "Foundation",
    "description": "Kem nền/lớp nền chính giúp làm đều màu da và che phủ khuyết điểm.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_CONCEALER",
    "categoryName": "Concealer",
    "description": "Sản phẩm che khuyết điểm, quầng thâm và vùng da cần hiệu chỉnh.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_PRIMER",
    "categoryName": "Primer",
    "description": "Kem lót giúp chuẩn bị bề mặt da, tăng độ bám và độ mịn lớp nền.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_POWDER",
    "categoryName": "Powder",
    "description": "Phấn phủ hoặc bột phủ giúp cố định nền và kiểm soát bóng dầu.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_SETTING_SPRAY",
    "categoryName": "Setting Spray",
    "description": "Xịt khóa nền giúp lớp makeup bền hơn và giảm mốc nền.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_BB_CC_CREAM",
    "categoryName": "BB & CC Cream",
    "description": "Sản phẩm nền lai skincare, mỏng nhẹ và dễ dùng hằng ngày.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_TINTED_MOISTURIZER",
    "categoryName": "Tinted Moisturizer",
    "description": "Kem dưỡng có màu/tinted base tạo lớp nền tự nhiên.",
    "parentCode": "CAT_FACE"
  },
  {
    "categoryCode": "CAT_MASCARA",
    "categoryName": "Mascara",
    "description": "Sản phẩm làm dài, cong hoặc dày mi.",
    "parentCode": "CAT_EYES"
  },
  {
    "categoryCode": "CAT_EYELINER",
    "categoryName": "Eyeliner",
    "description": "Kẻ mắt dạng bút, gel hoặc nước giúp định hình đường viền mắt.",
    "parentCode": "CAT_EYES"
  },
  {
    "categoryCode": "CAT_EYESHADOW",
    "categoryName": "Eyeshadow",
    "description": "Phấn mắt đơn hoặc dạng kem tạo màu cho bầu mắt.",
    "parentCode": "CAT_EYES"
  },
  {
    "categoryCode": "CAT_EYEBROW",
    "categoryName": "Eyebrow",
    "description": "Sản phẩm tạo dáng, tô màu và cố định lông mày.",
    "parentCode": "CAT_EYES"
  },
  {
    "categoryCode": "CAT_FALSE_LASHES",
    "categoryName": "False Lashes",
    "description": "Mi giả giúp tăng độ dày, dài hoặc hiệu ứng cho đôi mắt.",
    "parentCode": "CAT_EYES"
  },
  {
    "categoryCode": "CAT_LIPSTICK",
    "categoryName": "Lipstick",
    "description": "Son thỏi, son kem hoặc son lì tạo màu môi rõ nét.",
    "parentCode": "CAT_LIPS"
  },
  {
    "categoryCode": "CAT_LIP_GLOSS",
    "categoryName": "Lip Gloss",
    "description": "Son bóng giúp tạo hiệu ứng căng mọng, bóng hoặc shimmer.",
    "parentCode": "CAT_LIPS"
  },
  {
    "categoryCode": "CAT_LIP_BALM",
    "categoryName": "Lip Balm",
    "description": "Son dưỡng môi có màu hoặc không màu.",
    "parentCode": "CAT_LIPS"
  },
  {
    "categoryCode": "CAT_LIP_LINER",
    "categoryName": "Lip Liner",
    "description": "Chì viền môi giúp định hình viền môi và giữ màu son.",
    "parentCode": "CAT_LIPS"
  },
  {
    "categoryCode": "CAT_LIP_STAIN",
    "categoryName": "Lip Stain",
    "description": "Son tint/stain tạo màu bám lâu và tự nhiên.",
    "parentCode": "CAT_LIPS"
  },
  {
    "categoryCode": "CAT_BLUSH",
    "categoryName": "Blush",
    "description": "Má hồng dạng phấn, kem hoặc liquid tạo sắc hồng tự nhiên.",
    "parentCode": "CAT_CHEEKS"
  },
  {
    "categoryCode": "CAT_BRONZER",
    "categoryName": "Bronzer",
    "description": "Sản phẩm làm ấm gương mặt và tạo hiệu ứng sunkissed.",
    "parentCode": "CAT_CHEEKS"
  },
  {
    "categoryCode": "CAT_HIGHLIGHTER",
    "categoryName": "Highlighter",
    "description": "Sản phẩm bắt sáng vùng gò má, sống mũi và điểm nhấn mặt.",
    "parentCode": "CAT_CHEEKS"
  },
  {
    "categoryCode": "CAT_CONTOUR",
    "categoryName": "Contour",
    "description": "Tạo khối giúp tăng chiều sâu và định hình khuôn mặt.",
    "parentCode": "CAT_CHEEKS"
  },
  {
    "categoryCode": "CAT_EYESHADOW_PALETTE",
    "categoryName": "Eyeshadow Palette",
    "description": "Bảng phấn mắt nhiều màu, phù hợp tạo nhiều makeup look.",
    "parentCode": "CAT_GIFT"
  },
  {
    "categoryCode": "CAT_FACE_PALETTE",
    "categoryName": "Face Palette",
    "description": "Bảng mặt gồm blush/bronzer/highlighter/setting shade.",
    "parentCode": "CAT_GIFT"
  },
  {
    "categoryCode": "CAT_MAKEUP_KIT",
    "categoryName": "Makeup Kit",
    "description": "Bộ makeup set dùng làm quà hoặc tạo look hoàn chỉnh.",
    "parentCode": "CAT_GIFT"
  },
  {
    "categoryCode": "CAT_MINI_FOUNDATION",
    "categoryName": "Mini Foundation",
    "description": "Kem nền size nhỏ/travel size để thử màu hoặc mang theo.",
    "parentCode": "CAT_MINI_TRAVEL"
  },
  {
    "categoryCode": "CAT_MINI_LIPSTICK",
    "categoryName": "Mini Lipstick",
    "description": "Son mini size tiện thử màu hoặc bỏ túi.",
    "parentCode": "CAT_MINI_TRAVEL"
  },
  {
    "categoryCode": "CAT_TRIAL_KITS",
    "categoryName": "Trial Kits",
    "description": "Bộ sản phẩm dùng thử/travel kit cho makeup routine.",
    "parentCode": "CAT_MINI_TRAVEL"
  }
];

const productCatalog = [
  {
    "brandCode": "ESTEELAUDER",
    "productName": "Estée Lauder Double Wear Stay-in-Place Makeup",
    "categoryCode": "CAT_FOUNDATION",
    "price": 1350000,
    "compareAtPrice": 1550000,
    "optionName": "Shade",
    "options": [
      {
        "value": "1N2 Ecru",
        "hex": "#D9B894",
        "undertone": "neutral",
        "volumeMl": 30,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "2W1 Dawn",
        "hex": "#C9976B",
        "undertone": "warm",
        "volumeMl": 30,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Iron Oxides",
      "Titanium Dioxide",
      "Silica"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "long_wear",
      "oil_control",
      "full_coverage"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "United States",
    "type": "kem nền lâu trôi",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Light Reflecting Foundation",
    "categoryCode": "CAT_FOUNDATION",
    "price": 1280000,
    "compareAtPrice": 1450000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Mont Blanc",
        "hex": "#E6C5A1",
        "undertone": "neutral",
        "volumeMl": 30,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Punjab",
        "hex": "#C9976B",
        "undertone": "warm",
        "volumeMl": 30,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Biomimetic Oat",
      "Japanese Lilyturf",
      "Cacao Peptides"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "natural_base",
      "radiance",
      "tone_evening"
    ],
    "finish": "natural radiant",
    "coverage": "medium",
    "country": "France",
    "type": "kem nền phản chiếu ánh sáng",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Radiant Creamy Concealer",
    "categoryCode": "CAT_CONCEALER",
    "price": 850000,
    "compareAtPrice": 980000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Vanilla",
        "hex": "#E8C7A5",
        "undertone": "neutral",
        "volumeMl": 6,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Custard",
        "hex": "#D7A46E",
        "undertone": "yellow",
        "volumeMl": 6,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Glycerin",
      "Mineral Powder",
      "Magnolia Bark Extract"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "dark_circle",
      "spot_conceal",
      "radiance"
    ],
    "finish": "radiant",
    "coverage": "medium_to_full",
    "country": "United States",
    "type": "kem che khuyết điểm dạng kem",
    "texture": "creamy liquid",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "TARTE",
    "productName": "Tarte Shape Tape Full-Coverage Concealer",
    "categoryCode": "CAT_CONCEALER",
    "price": 790000,
    "compareAtPrice": 920000,
    "optionName": "Shade",
    "options": [
      {
        "value": "22N Light Neutral",
        "hex": "#E1B98D",
        "undertone": "neutral",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "35N Medium",
        "hex": "#BA7E53",
        "undertone": "neutral",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Shea Butter",
      "Mango Seed Butter",
      "Licorice Root"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "full_coverage",
      "dark_circle",
      "long_wear"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "United States",
    "type": "concealer che phủ cao",
    "texture": "cream liquid",
    "sensitive": false,
    "fragrance": "fragranced",
    "arSupported": true
  },
  {
    "brandCode": "ELF",
    "productName": "e.l.f. Power Grip Primer",
    "categoryCode": "CAT_PRIMER",
    "price": 280000,
    "compareAtPrice": 340000,
    "optionName": "Size",
    "options": [
      {
        "value": "24ml",
        "hex": "",
        "undertone": "",
        "volumeMl": 24,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "15ml",
        "hex": "",
        "undertone": "",
        "volumeMl": 15,
        "weightGrams": 0,
        "priceFactor": 0.7
      }
    ],
    "ingredients": [
      "Niacinamide",
      "Glycerin",
      "Hyaluronic Acid"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "makeup_grip",
      "hydration",
      "long_wear"
    ],
    "finish": "dewy",
    "coverage": "none",
    "country": "United States",
    "type": "kem lót bám nền",
    "texture": "gel primer",
    "sensitive": true,
    "fragrance": "fragrance_free",
    "arSupported": false
  },
  {
    "brandCode": "BENEFIT",
    "productName": "Benefit The POREfessional Face Primer",
    "categoryCode": "CAT_PRIMER",
    "price": 890000,
    "compareAtPrice": 1020000,
    "optionName": "Size",
    "options": [
      {
        "value": "22ml",
        "hex": "",
        "undertone": "",
        "volumeMl": 22,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "7.5ml Mini",
        "hex": "",
        "undertone": "",
        "volumeMl": 7.5,
        "weightGrams": 0,
        "priceFactor": 0.55
      }
    ],
    "ingredients": [
      "Vitamin E",
      "Silica",
      "Dimethicone"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "pore_blur",
      "oil_control",
      "smooth_base"
    ],
    "finish": "soft matte",
    "coverage": "none",
    "country": "United States",
    "type": "kem lót che mờ lỗ chân lông",
    "texture": "balm primer",
    "sensitive": false,
    "fragrance": "fragranced",
    "arSupported": false
  },
  {
    "brandCode": "LAURAMERCIER",
    "productName": "Laura Mercier Translucent Loose Setting Powder",
    "categoryCode": "CAT_POWDER",
    "price": 1150000,
    "compareAtPrice": 1320000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Translucent",
        "hex": "#E5CFB2",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 29,
        "priceFactor": 1.0
      },
      {
        "value": "Honey",
        "hex": "#D6A66A",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 29,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Talc",
      "Silica",
      "Iron Oxides"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "set_makeup",
      "oil_control",
      "soft_focus"
    ],
    "finish": "matte",
    "coverage": "sheer",
    "country": "United States",
    "type": "phấn phủ dạng bột",
    "texture": "loose powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "INNISFREE",
    "productName": "Innisfree No-Sebum Mineral Powder",
    "categoryCode": "CAT_POWDER",
    "price": 180000,
    "compareAtPrice": 230000,
    "optionName": "Size",
    "options": [
      {
        "value": "5g",
        "hex": "",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 5,
        "priceFactor": 1.0
      },
      {
        "value": "10g Duo",
        "hex": "",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 10,
        "priceFactor": 1.8
      }
    ],
    "ingredients": [
      "Jeju Minerals",
      "Mint Extract",
      "Silica"
    ],
    "skinTypes": [
      "combination",
      "oily"
    ],
    "concerns": [
      "oil_control",
      "set_makeup",
      "blur"
    ],
    "finish": "matte",
    "coverage": "sheer",
    "country": "Korea",
    "type": "phấn phủ kiềm dầu",
    "texture": "loose powder",
    "sensitive": true,
    "fragrance": "light_fragrance",
    "arSupported": false
  },
  {
    "brandCode": "URBANDECAY",
    "productName": "Urban Decay All Nighter Setting Spray",
    "categoryCode": "CAT_SETTING_SPRAY",
    "price": 920000,
    "compareAtPrice": 1050000,
    "optionName": "Size",
    "options": [
      {
        "value": "118ml",
        "hex": "",
        "undertone": "",
        "volumeMl": 118,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "30ml Travel",
        "hex": "",
        "undertone": "",
        "volumeMl": 30,
        "weightGrams": 0,
        "priceFactor": 0.45
      }
    ],
    "ingredients": [
      "Temperature Control Technology",
      "Aloe Vera",
      "Polymer Film Former"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "long_wear",
      "transfer_resistant",
      "set_makeup"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "United States",
    "type": "xịt khóa nền lâu trôi",
    "texture": "fine mist",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": false
  },
  {
    "brandCode": "CHARLOTTETILBURY",
    "productName": "Charlotte Tilbury Airbrush Flawless Setting Spray",
    "categoryCode": "CAT_SETTING_SPRAY",
    "price": 1050000,
    "compareAtPrice": 1190000,
    "optionName": "Size",
    "options": [
      {
        "value": "100ml",
        "hex": "",
        "undertone": "",
        "volumeMl": 100,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "34ml Travel",
        "hex": "",
        "undertone": "",
        "volumeMl": 34,
        "weightGrams": 0,
        "priceFactor": 0.55
      }
    ],
    "ingredients": [
      "Aloe Vera",
      "Japanese Green Tea",
      "Aromatic Resin"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "long_wear",
      "smooth_finish",
      "radiance"
    ],
    "finish": "natural glow",
    "coverage": "none",
    "country": "United Kingdom",
    "type": "xịt cố định makeup",
    "texture": "fine mist",
    "sensitive": false,
    "fragrance": "fragranced",
    "arSupported": false
  },
  {
    "brandCode": "ITCOSMETICS",
    "productName": "IT Cosmetics CC+ Cream SPF 50+",
    "categoryCode": "CAT_BB_CC_CREAM",
    "price": 1180000,
    "compareAtPrice": 1340000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Light",
        "hex": "#DAB38B",
        "undertone": "neutral",
        "volumeMl": 32,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Medium",
        "hex": "#C4895F",
        "undertone": "warm",
        "volumeMl": 32,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Niacinamide",
      "Peptides"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "tone_evening",
      "hydration",
      "spf_base"
    ],
    "finish": "natural",
    "coverage": "full",
    "country": "United States",
    "type": "CC cream nền lai skincare",
    "texture": "cream",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "ERBORIAN",
    "productName": "Erborian CC Crème",
    "categoryCode": "CAT_BB_CC_CREAM",
    "price": 890000,
    "compareAtPrice": 1020000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Clair",
        "hex": "#E4C5A5",
        "undertone": "neutral",
        "volumeMl": 45,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Doré",
        "hex": "#C99263",
        "undertone": "warm",
        "volumeMl": 45,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Centella Asiatica",
      "Vitamin E",
      "Honey Extract"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "tone_adjust",
      "natural_base",
      "radiance"
    ],
    "finish": "natural radiant",
    "coverage": "light",
    "country": "Korea/France",
    "type": "CC cream hiệu chỉnh sắc da",
    "texture": "cream",
    "sensitive": true,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Pure Radiant Tinted Moisturizer",
    "categoryCode": "CAT_TINTED_MOISTURIZER",
    "price": 1120000,
    "compareAtPrice": 1280000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Alaska",
        "hex": "#D6A36C",
        "undertone": "neutral",
        "volumeMl": 50,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "St. Moritz",
        "hex": "#B8794A",
        "undertone": "warm",
        "volumeMl": 50,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Kopara",
      "Vitamin C",
      "Mineral Rich Seawater"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "light_coverage",
      "hydration",
      "daily_base"
    ],
    "finish": "radiant",
    "coverage": "light",
    "country": "United States",
    "type": "kem dưỡng có màu",
    "texture": "tinted cream",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "LAURAMERCIER",
    "productName": "Laura Mercier Tinted Moisturizer Natural Skin Perfector",
    "categoryCode": "CAT_TINTED_MOISTURIZER",
    "price": 1150000,
    "compareAtPrice": 1290000,
    "optionName": "Shade",
    "options": [
      {
        "value": "1N2 Vanille",
        "hex": "#E5B98D",
        "undertone": "neutral",
        "volumeMl": 50,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "2W1 Natural",
        "hex": "#C68E5F",
        "undertone": "warm",
        "volumeMl": 50,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Macadamia Oil",
      "Tamarind Seed Extract",
      "Vitamin C"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "daily_base",
      "hydration",
      "natural_skin"
    ],
    "finish": "natural",
    "coverage": "light",
    "country": "United States",
    "type": "tinted moisturizer nền tự nhiên",
    "texture": "cream",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "MAYBELLINE",
    "productName": "Maybelline Lash Sensational Sky High Mascara",
    "categoryCode": "CAT_MASCARA",
    "price": 285000,
    "compareAtPrice": 340000,
    "optionName": "Color",
    "options": [
      {
        "value": "Very Black",
        "hex": "#111111",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 7,
        "priceFactor": 1.0
      },
      {
        "value": "Brownish Black",
        "hex": "#2B1A12",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 7,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Bamboo Extract",
      "Fibers",
      "Film Former"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lengthening",
      "curling",
      "definition"
    ],
    "finish": "natural black",
    "coverage": "buildable",
    "country": "United States",
    "type": "mascara làm dài mi",
    "texture": "mascara cream",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "LOREALPARIS",
    "productName": "L'Oréal Paris Voluminous Lash Paradise Mascara",
    "categoryCode": "CAT_MASCARA",
    "price": 295000,
    "compareAtPrice": 350000,
    "optionName": "Color",
    "options": [
      {
        "value": "Blackest Black",
        "hex": "#050505",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 7.6,
        "priceFactor": 1.0
      },
      {
        "value": "Black Brown",
        "hex": "#2A1911",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 7.6,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Castor Oil",
      "Rose Oil",
      "Film Former"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "volume",
      "lengthening",
      "dramatic_lash"
    ],
    "finish": "voluminous black",
    "coverage": "buildable",
    "country": "France/United States",
    "type": "mascara làm dày mi",
    "texture": "mascara cream",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "KVDBEAUTY",
    "productName": "KVD Beauty Tattoo Liner",
    "categoryCode": "CAT_EYELINER",
    "price": 580000,
    "compareAtPrice": 660000,
    "optionName": "Color",
    "options": [
      {
        "value": "Trooper Black",
        "hex": "#050505",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 0.55,
        "priceFactor": 1.0
      },
      {
        "value": "Mad Max Brown",
        "hex": "#3B2417",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 0.55,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Waterproof Pigment",
      "Film Former",
      "Carbon Black"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "precise_line",
      "waterproof",
      "long_wear"
    ],
    "finish": "satin",
    "coverage": "opaque",
    "country": "United States",
    "type": "bút kẻ mắt nước",
    "texture": "liquid liner",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "NYX",
    "productName": "NYX Professional Makeup Epic Ink Liner",
    "categoryCode": "CAT_EYELINER",
    "price": 260000,
    "compareAtPrice": 310000,
    "optionName": "Color",
    "options": [
      {
        "value": "Black",
        "hex": "#000000",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 1,
        "priceFactor": 1.0
      },
      {
        "value": "Brown",
        "hex": "#3A2115",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 1,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Waterproof Polymer",
      "Iron Oxides",
      "Film Former"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "precise_line",
      "winged_liner",
      "long_wear"
    ],
    "finish": "satin",
    "coverage": "opaque",
    "country": "United States",
    "type": "kẻ mắt đầu cọ",
    "texture": "liquid liner",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "COLOURPOP",
    "productName": "ColourPop Super Shock Shadow",
    "categoryCode": "CAT_EYESHADOW",
    "price": 190000,
    "compareAtPrice": 240000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Amaze",
        "hex": "#C98F5A",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 2.1,
        "priceFactor": 1.0
      },
      {
        "value": "Frog",
        "hex": "#D7C1BA",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 2.1,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Pearl Pigment",
      "Dimethicone",
      "Mica"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eye_color",
      "shimmer",
      "quick_makeup"
    ],
    "finish": "metallic shimmer",
    "coverage": "buildable",
    "country": "United States",
    "type": "phấn mắt dạng kem bouncy",
    "texture": "cream powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "MAC",
    "productName": "MAC Eye Shadow",
    "categoryCode": "CAT_EYESHADOW",
    "price": 520000,
    "compareAtPrice": 610000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Satin Taupe",
        "hex": "#806A63",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 1.5,
        "priceFactor": 1.0
      },
      {
        "value": "Soft Brown",
        "hex": "#B17452",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 1.5,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Mica",
      "Talc",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eye_depth",
      "blendable",
      "daily_eye"
    ],
    "finish": "satin",
    "coverage": "buildable",
    "country": "Canada/United States",
    "type": "phấn mắt đơn",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "ANASTASIABEVERLYHILLS",
    "productName": "Anastasia Beverly Hills Brow Wiz",
    "categoryCode": "CAT_EYEBROW",
    "price": 620000,
    "compareAtPrice": 710000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Medium Brown",
        "hex": "#5B3A2E",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 0.085,
        "priceFactor": 1.0
      },
      {
        "value": "Dark Brown",
        "hex": "#3D291F",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 0.085,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Waxes",
      "Pigments",
      "Vitamin E"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "brow_shape",
      "natural_brow",
      "precision"
    ],
    "finish": "natural matte",
    "coverage": "buildable",
    "country": "United States",
    "type": "chì kẻ mày đầu siêu mảnh",
    "texture": "pencil",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "BENEFIT",
    "productName": "Benefit Precisely, My Brow Pencil",
    "categoryCode": "CAT_EYEBROW",
    "price": 780000,
    "compareAtPrice": 890000,
    "optionName": "Shade",
    "options": [
      {
        "value": "3 Warm Light Brown",
        "hex": "#7B513B",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 0.08,
        "priceFactor": 1.0
      },
      {
        "value": "4 Warm Deep Brown",
        "hex": "#4C3021",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 0.08,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Synthetic Wax",
      "Pigment",
      "Vitamin E"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "brow_shape",
      "hair_like_strokes",
      "long_wear"
    ],
    "finish": "natural matte",
    "coverage": "buildable",
    "country": "United States",
    "type": "chì kẻ mày chính xác",
    "texture": "pencil",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "ARDELL",
    "productName": "Ardell Demi Wispies",
    "categoryCode": "CAT_FALSE_LASHES",
    "price": 135000,
    "compareAtPrice": 165000,
    "optionName": "Style",
    "options": [
      {
        "value": "Black Pair",
        "hex": "#111111",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 2,
        "priceFactor": 1.0
      },
      {
        "value": "Multipack 4 Pairs",
        "hex": "#111111",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 8,
        "priceFactor": 2.9
      }
    ],
    "ingredients": [
      "Synthetic Hair",
      "Cotton Band"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "natural_lash",
      "eye_definition",
      "event_makeup"
    ],
    "finish": "natural flutter",
    "coverage": "medium",
    "country": "United States",
    "type": "mi giả tự nhiên",
    "texture": "synthetic lash",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "KISS",
    "productName": "KISS Lash Couture Naked Drama",
    "categoryCode": "CAT_FALSE_LASHES",
    "price": 185000,
    "compareAtPrice": 225000,
    "optionName": "Style",
    "options": [
      {
        "value": "Tulle",
        "hex": "#111111",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 2,
        "priceFactor": 1.0
      },
      {
        "value": "Organza",
        "hex": "#111111",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 2,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Synthetic Fiber",
      "Flexible Band"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "dramatic_lash",
      "volume",
      "event_makeup"
    ],
    "finish": "soft dramatic",
    "coverage": "full",
    "country": "United States",
    "type": "mi giả hiệu ứng dày mềm",
    "texture": "synthetic lash",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "MAC",
    "productName": "MAC Matte Lipstick",
    "categoryCode": "CAT_LIPSTICK",
    "price": 620000,
    "compareAtPrice": 710000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Ruby Woo",
        "hex": "#B9002A",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 3,
        "priceFactor": 1.0
      },
      {
        "value": "Velvet Teddy",
        "hex": "#A66A57",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 3,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Castor Seed Oil",
      "Wax Blend",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "long_wear",
      "classic_lip"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "Canada/United States",
    "type": "son thỏi lì",
    "texture": "bullet lipstick",
    "sensitive": false,
    "fragrance": "vanilla_scent",
    "arSupported": true
  },
  {
    "brandCode": "CHARLOTTETILBURY",
    "productName": "Charlotte Tilbury Matte Revolution Lipstick",
    "categoryCode": "CAT_LIPSTICK",
    "price": 890000,
    "compareAtPrice": 1020000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Pillow Talk",
        "hex": "#B9796E",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 3.5,
        "priceFactor": 1.0
      },
      {
        "value": "Walk of No Shame",
        "hex": "#8E2F38",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 3.5,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Orchid Extract",
      "3D Glow Pigments",
      "Wax Blend"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "soft_matte",
      "everyday_lip"
    ],
    "finish": "soft matte",
    "coverage": "full",
    "country": "United Kingdom",
    "type": "son thỏi lì mềm môi",
    "texture": "bullet lipstick",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "MAYBELLINE",
    "productName": "Maybelline SuperStay Matte Ink Liquid Lipstick",
    "categoryCode": "CAT_LIPSTICK",
    "price": 245000,
    "compareAtPrice": 295000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Amazonian",
        "hex": "#9A5B47",
        "undertone": "warm",
        "volumeMl": 5,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Pioneer",
        "hex": "#B01134",
        "undertone": "cool",
        "volumeMl": 5,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Film Former",
      "Pigment",
      "Dimethicone"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "transfer_resistant",
      "long_wear"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "United States",
    "type": "son kem lì lâu trôi",
    "texture": "liquid lipstick",
    "sensitive": false,
    "fragrance": "fragranced",
    "arSupported": true
  },
  {
    "brandCode": "FENTYBEAUTY",
    "productName": "Fenty Beauty Gloss Bomb Universal Lip Luminizer",
    "categoryCode": "CAT_LIP_GLOSS",
    "price": 620000,
    "compareAtPrice": 720000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Fenty Glow",
        "hex": "#C47A69",
        "undertone": "neutral",
        "volumeMl": 9,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Fu$$y",
        "hex": "#D9979D",
        "undertone": "cool",
        "volumeMl": 9,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Shea Butter",
      "Vitamin E",
      "Shimmer Pigment"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_glow",
      "plump_look",
      "shine"
    ],
    "finish": "glossy",
    "coverage": "sheer",
    "country": "United States",
    "type": "son bóng đa dụng",
    "texture": "lip gloss",
    "sensitive": false,
    "fragrance": "sweet_scent",
    "arSupported": true
  },
  {
    "brandCode": "DIOR",
    "productName": "Dior Addict Lip Maximizer",
    "categoryCode": "CAT_LIP_GLOSS",
    "price": 1050000,
    "compareAtPrice": 1190000,
    "optionName": "Shade",
    "options": [
      {
        "value": "001 Pink",
        "hex": "#F4B4C1",
        "undertone": "cool",
        "volumeMl": 6,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "012 Rosewood",
        "hex": "#B76B6B",
        "undertone": "neutral",
        "volumeMl": 6,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Cherry Oil",
      "Hyaluronic Acid",
      "Menthol Derivative"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_plump",
      "shine",
      "hydration"
    ],
    "finish": "high shine",
    "coverage": "sheer",
    "country": "France",
    "type": "son bóng làm đầy môi",
    "texture": "lip gloss",
    "sensitive": false,
    "fragrance": "mint_scent",
    "arSupported": true
  },
  {
    "brandCode": "LANEIGE",
    "productName": "Laneige Lip Glowy Balm",
    "categoryCode": "CAT_LIP_BALM",
    "price": 420000,
    "compareAtPrice": 490000,
    "optionName": "Flavor/Shade",
    "options": [
      {
        "value": "Berry",
        "hex": "#E893A3",
        "undertone": "cool",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Gummy Bear",
        "hex": "#A77BD3",
        "undertone": "cool",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Shea Butter",
      "Murunga Seed Butter",
      "Vitamin C"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_hydration",
      "dry_lips",
      "daily_lip"
    ],
    "finish": "glossy balm",
    "coverage": "sheer",
    "country": "Korea",
    "type": "son dưỡng bóng có màu nhẹ",
    "texture": "balm gloss",
    "sensitive": true,
    "fragrance": "fragranced",
    "arSupported": true
  },
  {
    "brandCode": "BURTSBEES",
    "productName": "Burt's Bees Beeswax Lip Balm",
    "categoryCode": "CAT_LIP_BALM",
    "price": 120000,
    "compareAtPrice": 150000,
    "optionName": "Flavor",
    "options": [
      {
        "value": "Original Beeswax",
        "hex": "#F2D3A7",
        "undertone": "",
        "volumeMl": 0,
        "weightGrams": 4.25,
        "priceFactor": 1.0
      },
      {
        "value": "Pomegranate",
        "hex": "#C8586A",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 4.25,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Beeswax",
      "Vitamin E",
      "Peppermint Oil"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_hydration",
      "chapped_lips",
      "daily_care"
    ],
    "finish": "natural balm",
    "coverage": "sheer",
    "country": "United States",
    "type": "son dưỡng môi sáp ong",
    "texture": "stick balm",
    "sensitive": true,
    "fragrance": "mint_scent",
    "arSupported": false
  },
  {
    "brandCode": "CHARLOTTETILBURY",
    "productName": "Charlotte Tilbury Lip Cheat",
    "categoryCode": "CAT_LIP_LINER",
    "price": 690000,
    "compareAtPrice": 790000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Pillow Talk",
        "hex": "#B9796E",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 1.2,
        "priceFactor": 1.0
      },
      {
        "value": "Iconic Nude",
        "hex": "#A9785F",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 1.2,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Wax Blend",
      "Pigment",
      "Vitamin E"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_shape",
      "long_wear",
      "lip_definition"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "United Kingdom",
    "type": "chì viền môi",
    "texture": "pencil",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "MAC",
    "productName": "MAC Lip Pencil",
    "categoryCode": "CAT_LIP_LINER",
    "price": 540000,
    "compareAtPrice": 620000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Whirl",
        "hex": "#8B5A4B",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 1.45,
        "priceFactor": 1.0
      },
      {
        "value": "Soar",
        "hex": "#A35D6D",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 1.45,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Wax Blend",
      "Pigment",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_shape",
      "classic_lip",
      "long_wear"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "Canada/United States",
    "type": "chì kẻ viền môi",
    "texture": "pencil",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "BENEFIT",
    "productName": "Benefit Benetint",
    "categoryCode": "CAT_LIP_STAIN",
    "price": 720000,
    "compareAtPrice": 830000,
    "optionName": "Size",
    "options": [
      {
        "value": "6ml",
        "hex": "#C42A3A",
        "undertone": "cool",
        "volumeMl": 6,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Mini 3ml",
        "hex": "#C42A3A",
        "undertone": "cool",
        "volumeMl": 3,
        "weightGrams": 0,
        "priceFactor": 0.6
      }
    ],
    "ingredients": [
      "Rose-Tinted Dye",
      "Water",
      "Glycerin"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_stain",
      "cheek_tint",
      "natural_flush"
    ],
    "finish": "natural stain",
    "coverage": "sheer buildable",
    "country": "United States",
    "type": "tint môi và má",
    "texture": "watery tint",
    "sensitive": false,
    "fragrance": "rose_scent",
    "arSupported": true
  },
  {
    "brandCode": "ROMAND",
    "productName": "rom&nd Juicy Lasting Tint",
    "categoryCode": "CAT_LIP_STAIN",
    "price": 210000,
    "compareAtPrice": 260000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Figfig",
        "hex": "#A54F58",
        "undertone": "cool",
        "volumeMl": 5.5,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Nucadamia",
        "hex": "#B5684D",
        "undertone": "warm",
        "volumeMl": 5.5,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Fruit Extract",
      "Gloss Polymer",
      "Pigment"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_stain",
      "glossy_tint",
      "kbeauty_lip"
    ],
    "finish": "juicy glossy",
    "coverage": "medium",
    "country": "Korea",
    "type": "son tint bóng bám màu",
    "texture": "gloss tint",
    "sensitive": false,
    "fragrance": "fragranced",
    "arSupported": true
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Blush",
    "categoryCode": "CAT_BLUSH",
    "price": 920000,
    "compareAtPrice": 1050000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Orgasm",
        "hex": "#D98270",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 4.8,
        "priceFactor": 1.0
      },
      {
        "value": "Deep Throat",
        "hex": "#C77770",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 4.8,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Mica",
      "Pigment",
      "Silica"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "cheek_color",
      "radiance",
      "daily_flush"
    ],
    "finish": "satin shimmer",
    "coverage": "buildable",
    "country": "United States",
    "type": "phấn má hồng",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "RAREBEAUTY",
    "productName": "Rare Beauty Soft Pinch Liquid Blush",
    "categoryCode": "CAT_BLUSH",
    "price": 690000,
    "compareAtPrice": 790000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Hope",
        "hex": "#C46A6C",
        "undertone": "neutral",
        "volumeMl": 7.5,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Joy",
        "hex": "#D96F52",
        "undertone": "warm",
        "volumeMl": 7.5,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Lotus Extract",
      "Gardenia Extract",
      "Pigment"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "cheek_color",
      "liquid_blush",
      "fresh_look"
    ],
    "finish": "natural matte",
    "coverage": "high pigment",
    "country": "United States",
    "type": "má hồng dạng liquid",
    "texture": "liquid blush",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "BENEFIT",
    "productName": "Benefit Hoola Matte Bronzer",
    "categoryCode": "CAT_BRONZER",
    "price": 820000,
    "compareAtPrice": 930000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Hoola",
        "hex": "#A46D42",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 8,
        "priceFactor": 1.0
      },
      {
        "value": "Hoola Lite",
        "hex": "#C28A5D",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 8,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Talc",
      "Mica",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "warmth",
      "face_dimension",
      "matte_bronze"
    ],
    "finish": "matte",
    "coverage": "buildable",
    "country": "United States",
    "type": "phấn bronzer lì",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "PHYSICIANSFORMULA",
    "productName": "Physicians Formula Butter Bronzer",
    "categoryCode": "CAT_BRONZER",
    "price": 420000,
    "compareAtPrice": 490000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Bronzer",
        "hex": "#B27543",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 11,
        "priceFactor": 1.0
      },
      {
        "value": "Deep Bronzer",
        "hex": "#885532",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 11,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Murumuru Butter",
      "Cupuaçu Butter",
      "Tucuma Butter"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "warmth",
      "soft_blend",
      "summer_glow"
    ],
    "finish": "satin",
    "coverage": "buildable",
    "country": "United States",
    "type": "phấn bronzer bơ mềm",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "tropical_scent",
    "arSupported": true
  },
  {
    "brandCode": "RAREBEAUTY",
    "productName": "Rare Beauty Positive Light Silky Touch Highlighter",
    "categoryCode": "CAT_HIGHLIGHTER",
    "price": 760000,
    "compareAtPrice": 870000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Enlighten",
        "hex": "#F1D3A2",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 2.8,
        "priceFactor": 1.0
      },
      {
        "value": "Flaunt",
        "hex": "#D39B6A",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 2.8,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Pearl Pigment",
      "Mica",
      "Squalane"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "highlight",
      "glow",
      "face_dimension"
    ],
    "finish": "luminous",
    "coverage": "buildable",
    "country": "United States",
    "type": "phấn bắt sáng",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "FENTYBEAUTY",
    "productName": "Fenty Beauty Killawatt Freestyle Highlighter",
    "categoryCode": "CAT_HIGHLIGHTER",
    "price": 920000,
    "compareAtPrice": 1050000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Mean Money/Hu$tla Baby",
        "hex": "#E3B56E",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 7,
        "priceFactor": 1.0
      },
      {
        "value": "Lightning Dust/Fire Crystal",
        "hex": "#F1D2B0",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 7,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Mica",
      "Pearl Pigment",
      "Dimethicone"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "highlight",
      "high_impact_glow",
      "event_makeup"
    ],
    "finish": "shimmer",
    "coverage": "buildable",
    "country": "United States",
    "type": "highlighter duo",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "FENTYBEAUTY",
    "productName": "Fenty Beauty Match Stix Matte Contour Skinstick",
    "categoryCode": "CAT_CONTOUR",
    "price": 780000,
    "compareAtPrice": 890000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Amber",
        "hex": "#8A5A45",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 7.1,
        "priceFactor": 1.0
      },
      {
        "value": "Mocha",
        "hex": "#6B4636",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 7.1,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Wax Blend",
      "Pigment",
      "Silica"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "contour",
      "face_dimension",
      "cream_makeup"
    ],
    "finish": "matte",
    "coverage": "buildable",
    "country": "United States",
    "type": "thỏi tạo khối dạng kem",
    "texture": "cream stick",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "NYX",
    "productName": "NYX Professional Makeup Wonder Stick Contour and Highlight Stick",
    "categoryCode": "CAT_CONTOUR",
    "price": 330000,
    "compareAtPrice": 390000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Light Medium",
        "hex": "#B07856",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 8,
        "priceFactor": 1.0
      },
      {
        "value": "Medium Tan",
        "hex": "#85563A",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 8,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Cream Pigment",
      "Wax Blend",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "contour",
      "highlight",
      "quick_sculpt"
    ],
    "finish": "natural matte",
    "coverage": "buildable",
    "country": "United States",
    "type": "thỏi contour và highlight 2 đầu",
    "texture": "cream stick",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "URBANDECAY",
    "productName": "Urban Decay Naked3 Eyeshadow Palette",
    "categoryCode": "CAT_EYESHADOW_PALETTE",
    "price": 1450000,
    "compareAtPrice": 1650000,
    "optionName": "Palette Version",
    "options": [
      {
        "value": "Naked3 12 Shades",
        "hex": "#C79B8C",
        "undertone": "rose",
        "volumeMl": 0,
        "weightGrams": 15.6,
        "priceFactor": 1.0
      },
      {
        "value": "Naked3 Mini",
        "hex": "#B98A7D",
        "undertone": "rose",
        "volumeMl": 0,
        "weightGrams": 6,
        "priceFactor": 0.55
      }
    ],
    "ingredients": [
      "Mica",
      "Synthetic Fluorphlogopite",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eye_palette",
      "rose_neutral",
      "giftable"
    ],
    "finish": "matte shimmer metallic",
    "coverage": "buildable",
    "country": "United States",
    "type": "bảng phấn mắt tông hồng nude",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "NATASHADENONA",
    "productName": "Natasha Denona Mini Nude Palette",
    "categoryCode": "CAT_EYESHADOW_PALETTE",
    "price": 750000,
    "compareAtPrice": 860000,
    "optionName": "Palette",
    "options": [
      {
        "value": "Mini Nude 5 Shades",
        "hex": "#B67C58",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 4,
        "priceFactor": 1.0
      },
      {
        "value": "Mini Glam 5 Shades",
        "hex": "#9C8274",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 4,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Mica",
      "Silica",
      "Pearl Pigment"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eye_palette",
      "travel_palette",
      "neutral_look"
    ],
    "finish": "matte metallic",
    "coverage": "buildable",
    "country": "Italy/Israel",
    "type": "bảng phấn mắt mini",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "HOURGLASS",
    "productName": "Hourglass Ambient Lighting Edit Palette",
    "categoryCode": "CAT_FACE_PALETTE",
    "price": 2200000,
    "compareAtPrice": 2480000,
    "optionName": "Palette",
    "options": [
      {
        "value": "Ambient Lighting Edit",
        "hex": "#D3A06E",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 9.6,
        "priceFactor": 1.0
      },
      {
        "value": "Ambient Lighting Edit Unlocked",
        "hex": "#C18358",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 9.6,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Photoluminescent Technology",
      "Mica",
      "Pearl Pigment"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "face_palette",
      "soft_glow",
      "giftable"
    ],
    "finish": "luminous",
    "coverage": "sheer buildable",
    "country": "United States",
    "type": "bảng mặt đa năng",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "arSupported": true
  },
  {
    "brandCode": "SEPHORACOLLECTION",
    "productName": "Sephora Collection Makeup Must Haves Set",
    "categoryCode": "CAT_MAKEUP_KIT",
    "price": 890000,
    "compareAtPrice": 1150000,
    "optionName": "Set",
    "options": [
      {
        "value": "Mini Makeup Set",
        "hex": "#C7778A",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 120,
        "priceFactor": 1.0
      },
      {
        "value": "Full Makeup Set",
        "hex": "#C7778A",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 280,
        "priceFactor": 1.6
      }
    ],
    "ingredients": [
      "Mixed Makeup Items",
      "Pigments",
      "Wax Blend"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "makeup_kit",
      "giftable",
      "complete_look"
    ],
    "finish": "mixed finish",
    "coverage": "mixed",
    "country": "France/United States",
    "type": "bộ makeup cơ bản",
    "texture": "mixed",
    "sensitive": false,
    "fragrance": "mixed",
    "arSupported": true
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Light Reflecting Foundation Mini",
    "categoryCode": "CAT_MINI_FOUNDATION",
    "price": 520000,
    "compareAtPrice": 620000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Mont Blanc Mini",
        "hex": "#E6C5A1",
        "undertone": "neutral",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      },
      {
        "value": "Punjab Mini",
        "hex": "#C9976B",
        "undertone": "warm",
        "volumeMl": 10,
        "weightGrams": 0,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Biomimetic Oat",
      "Japanese Lilyturf",
      "Cacao Peptides"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "mini_foundation",
      "shade_trial",
      "travel_base"
    ],
    "finish": "natural radiant",
    "coverage": "medium",
    "country": "France",
    "type": "kem nền mini size",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "arSupported": true
  },
  {
    "brandCode": "MAC",
    "productName": "MAC Mini Lipstick",
    "categoryCode": "CAT_MINI_LIPSTICK",
    "price": 320000,
    "compareAtPrice": 390000,
    "optionName": "Shade",
    "options": [
      {
        "value": "Mini Ruby Woo",
        "hex": "#B9002A",
        "undertone": "cool",
        "volumeMl": 0,
        "weightGrams": 1.8,
        "priceFactor": 1.0
      },
      {
        "value": "Mini Velvet Teddy",
        "hex": "#A66A57",
        "undertone": "warm",
        "volumeMl": 0,
        "weightGrams": 1.8,
        "priceFactor": 1.0
      }
    ],
    "ingredients": [
      "Castor Seed Oil",
      "Wax Blend",
      "Iron Oxides"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "mini_lipstick",
      "shade_trial",
      "travel_lip"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "Canada/United States",
    "type": "son thỏi mini",
    "texture": "bullet lipstick",
    "sensitive": false,
    "fragrance": "vanilla_scent",
    "arSupported": true
  },
  {
    "brandCode": "BENEFIT",
    "productName": "Benefit Cosmetics Trial Size Bestsellers Set",
    "categoryCode": "CAT_TRIAL_KITS",
    "price": 650000,
    "compareAtPrice": 820000,
    "optionName": "Set",
    "options": [
      {
        "value": "Trial Size Set",
        "hex": "#D46B80",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 90,
        "priceFactor": 1.0
      },
      {
        "value": "Deluxe Trial Set",
        "hex": "#D46B80",
        "undertone": "neutral",
        "volumeMl": 0,
        "weightGrams": 150,
        "priceFactor": 1.35
      }
    ],
    "ingredients": [
      "Mixed Makeup Items",
      "Pigments",
      "Waxes"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "trial_kit",
      "giftable",
      "travel_makeup"
    ],
    "finish": "mixed finish",
    "coverage": "mixed",
    "country": "United States",
    "type": "bộ dùng thử makeup bestseller",
    "texture": "mixed",
    "sensitive": false,
    "fragrance": "mixed",
    "arSupported": true
  }
];

const brandIdByCode = new Map(brandCatalog.map((brand, index) => [brand.brandCode, objectIdFromNumber(1001 + index)]));
const categoryIdByCode = new Map(categoryCatalog.map((category, index) => [category.categoryCode, objectIdFromNumber(2001 + index)]));
const productIdByIndex = new Map(productCatalog.map((product, index) => [index, objectIdFromNumber(3001 + index)]));

function placeholderProductImage(productIndex, variant = 'main') {
  return `${IMAGE_BASE_URL}/${String(productIndex + 1).padStart(3, '0')}-${variant}.jpg`;
}

function placeholderBrandLogo(brandCode) {
  return `${LOGO_BASE_URL}/${brandCode.toLowerCase()}.png`;
}

function optionMetric(option) {
  return {
    volumeMl: Number(option.volumeMl || 0),
    weightGrams: Number(option.weightGrams || 0),
  };
}

function isShadeLikeOption(product) {
  return ['Shade', 'Color', 'Flavor/Shade'].includes(product.optionName);
}

const brandDocs = brandCatalog.map((brand) => ({
  _id: brandIdByCode.get(brand.brandCode),
  brandName: brand.brandName,
  brandCode: brand.brandCode,
  description: brand.description,
  logoUrl: placeholderBrandLogo(brand.brandCode),
  brandStatus: 'active',
  isActive: true,
}));

const categoryDocs = categoryCatalog.map((category, index) => ({
  _id: categoryIdByCode.get(category.categoryCode),
  categoryName: category.categoryName,
  categoryCode: category.categoryCode,
  description: category.description,
  parentCategoryId: category.parentCode ? categoryIdByCode.get(category.parentCode) : null,
  displayOrder: index + 1,
  categoryStatus: 'active',
  isActive: true,
}));

const productDocs = productCatalog.map((product, index) => {
  const brandId = brandIdByCode.get(product.brandCode);
  const categoryId = categoryIdByCode.get(product.categoryCode);
  if (!brandId) throw new Error(`Missing brand for product ${product.productName}: ${product.brandCode}`);
  if (!categoryId) throw new Error(`Missing category for product ${product.productName}: ${product.categoryCode}`);

  const shadeDocs = product.options
    .filter((option) => option.hex)
    .map((option) => ({
      shade_name: option.value,
      shadeName: option.value,
      shade_code: safeCode(option.value),
      shadeCode: safeCode(option.value),
      hex: option.hex,
      undertone: option.undertone || '',
    }));

  const bought = 120 + (index * 23) % 680;
  const rating = Number((4.2 + ((index % 8) * 0.1)).toFixed(1));
  const category = categoryCatalog.find((item) => item.categoryCode === product.categoryCode);

  return {
    _id: productIdByIndex.get(index),
    productName: product.productName,
    productCode: `KNL-MKP-${String(index + 1).padStart(3, '0')}`,
    slug: `kanila-${slugify(product.productName)}`,
    brandId,
    categoryId,
    price: product.price,
    compareAtPrice: product.compareAtPrice,
    imageUrl: placeholderProductImage(index, 'main'),
    shortDescription: `${product.productName} - ${product.type}, thuộc nhóm ${category.categoryName}.`,
    longDescription: `Sản phẩm makeup thật được seed cho Kanila: ${product.productName}. Giá trong file là giá tham khảo để test catalog/API, không phải giá niêm yết chính thức. URL ảnh đang để placeholder để đội dự án thay bằng ảnh thật sau.`,
    stock: 80 + (index * 13) % 260,
    bought,
    averageRating: rating,
    isActive: true,
    productStatus: 'active',
    ingredientText: product.ingredients.join(', '),
    shades: shadeDocs,
    skin_types_supported: product.skinTypes,
    concerns_targeted: product.concerns,
    ingredient_flags: product.sensitive ? ['sensitive_friendly'] : [],
    key_ingredients: product.ingredients,
    is_sensitive_friendly: Boolean(product.sensitive),
    tone_match_supported: isShadeLikeOption(product) ? ['fair', 'light', 'medium', 'tan', 'deep'] : [],
    finish_type: product.finish || '',
    coverage_type: product.coverage || '',
    sales_count: bought,
    is_best_seller: index < 15,
    usageInstruction: product.arSupported
      ? 'Chọn màu/phiên bản phù hợp, xem swatch hoặc thử AR nếu app đã bật AR Try-On cho sản phẩm này.'
      : 'Chọn phiên bản phù hợp và xem review/swatch trước khi mua. Bảo quản nơi khô ráo, tránh nhiệt cao.',
  };
});

const productBeautyProfileDocs = productCatalog.map((product, index) => ({
  _id: objectIdFromNumber(4001 + index),
  product_id: productIdByIndex.get(index),
  suitable_skin_types: product.skinTypes,
  suitable_skin_concerns: product.concerns,
  suitable_sensitivity_levels: product.sensitive ? ['low', 'medium', 'high'] : ['low', 'medium'],
  suitable_skin_tones: isShadeLikeOption(product) ? ['fair', 'light', 'medium', 'tan', 'deep'] : [],
  suitable_undertones: isShadeLikeOption(product) ? ['cool', 'neutral', 'warm'] : [],
  supported_beauty_goals: product.concerns.includes('long_wear')
    ? ['long_wear_makeup', 'event_makeup']
    : product.concerns.includes('lip_color') || product.categoryCode.includes('LIP')
      ? ['lip_makeup', 'daily_makeup']
      : product.concerns.includes('eye_palette') || product.categoryCode.includes('EYE')
        ? ['eye_makeup', 'creative_look']
        : ['base_makeup', 'natural_look'],
  key_ingredients: product.ingredients,
  avoid_for_ingredients: product.fragrance === 'fragranced' ? ['fragrance_sensitive'] : [],
  texture: product.texture || '',
  finish: product.finish || '',
  fragrance_type: product.fragrance || 'no_preference',
  product_tags: [product.type, product.categoryCode.toLowerCase().replace('cat_', ''), product.brandCode.toLowerCase(), product.optionName.toLowerCase()],
  recommendation_boost_score: 72 + (index % 20),
  recommendation_penalty_score: product.sensitive ? 0 : 5,
  is_active: true,
}));

const productCategoryDocs = productCatalog.map((product, index) => ({
  _id: objectIdFromNumber(5001 + index),
  productId: productIdByIndex.get(index),
  categoryId: categoryIdByCode.get(product.categoryCode),
  isPrimary: true,
  sortOrder: index + 1,
}));

const productMediaDocs = productCatalog.flatMap((product, index) => ([
  {
    _id: objectIdFromNumber(6001 + index * 2),
    productId: productIdByIndex.get(index),
    mediaType: 'image',
    mediaUrl: placeholderProductImage(index, 'main'),
    altText: `${product.productName} ảnh chính`,
    sortOrder: 1,
    isPrimary: true,
  },
  {
    _id: objectIdFromNumber(6002 + index * 2),
    productId: productIdByIndex.get(index),
    mediaType: 'image',
    mediaUrl: placeholderProductImage(index, 'swatch-or-gallery'),
    altText: `${product.productName} ảnh swatch hoặc gallery`,
    sortOrder: 2,
    isPrimary: false,
  },
]));

const productAttributeDocs = productCatalog.flatMap((product, index) => {
  const productId = productIdByIndex.get(index);
  const category = categoryCatalog.find((item) => item.categoryCode === product.categoryCode);
  const attributes = [
    ['Phân loại', category.categoryName],
    ['Xuất xứ thương hiệu', product.country],
    ['Finish', product.finish],
    ['Coverage', product.coverage],
    ['Thành phần nổi bật', product.ingredients.join(', ')],
  ];
  return attributes.map(([attributeName, attributeValue], attrIndex) => ({
    _id: objectIdFromNumber(7001 + index * 5 + attrIndex),
    productId,
    attributeName,
    attributeValue,
    displayOrder: attrIndex + 1,
  }));
});

const productOptionDocs = productCatalog.map((product, index) => ({
  _id: objectIdFromNumber(8001 + index),
  productId: productIdByIndex.get(index),
  optionName: product.optionName,
  displayOrder: 1,
}));

const productOptionValueDocs = productCatalog.flatMap((product, index) => product.options.map((option, optionIndex) => ({
  _id: objectIdFromNumber(9001 + index * 2 + optionIndex),
  productOptionId: objectIdFromNumber(8001 + index),
  optionValue: option.value,
  displayOrder: optionIndex + 1,
})));

const productVariantDocs = productCatalog.flatMap((product, index) => product.options.map((option, optionIndex) => {
  const metric = optionMetric(option);
  const variantPrice = Math.round(product.price * Number(option.priceFactor || 1) / 1000) * 1000;
  return {
    _id: objectIdFromNumber(10001 + index * 2 + optionIndex),
    productId: productIdByIndex.get(index),
    sku: `KNL-${safeCode(product.brandCode).slice(0, 8)}-${String(index + 1).padStart(3, '0')}-${String(optionIndex + 1).padStart(2, '0')}`,
    barcode: `8938${String(100000000 + index * 10 + optionIndex).slice(0, 9)}`,
    variantName: `${product.productName} - ${option.value}`,
    variantStatus: 'active',
    weightGrams: metric.weightGrams,
    volumeMl: metric.volumeMl,
    costAmount: Math.round(variantPrice * 0.58 / 1000) * 1000,
  };
}));

const variantOptionValueDocs = productCatalog.flatMap((product, index) => product.options.map((option, optionIndex) => ({
  _id: objectIdFromNumber(11001 + index * 2 + optionIndex),
  variantId: objectIdFromNumber(10001 + index * 2 + optionIndex),
  productOptionValueId: objectIdFromNumber(9001 + index * 2 + optionIndex),
})));

const variantMediaDocs = productCatalog.flatMap((product, index) => product.options.map((option, optionIndex) => ({
  _id: objectIdFromNumber(12001 + index * 2 + optionIndex),
  variantId: objectIdFromNumber(10001 + index * 2 + optionIndex),
  mediaType: 'image',
  mediaUrl: placeholderProductImage(index, `variant-${optionIndex + 1}`),
  sortOrder: optionIndex + 1,
  isPrimary: optionIndex === 0,
})));

const seedPlan = [
  { label: 'Brand', model: Brand, docs: brandDocs },
  { label: 'Category', model: Category, docs: categoryDocs },
  { label: 'Product', model: Product, docs: productDocs },
  { label: 'ProductBeautyProfile', model: ProductBeautyProfile, docs: productBeautyProfileDocs },
  { label: 'ProductCategory', model: ProductCategory, docs: productCategoryDocs },
  { label: 'ProductMedia', model: ProductMedia, docs: productMediaDocs },
  { label: 'ProductAttribute', model: ProductAttribute, docs: productAttributeDocs },
  { label: 'ProductOption', model: ProductOption, docs: productOptionDocs },
  { label: 'ProductOptionValue', model: ProductOptionValue, docs: productOptionValueDocs },
  { label: 'ProductVariant', model: ProductVariant, docs: productVariantDocs },
  { label: 'VariantOptionValue', model: VariantOptionValue, docs: variantOptionValueDocs },
  { label: 'VariantMedia', model: VariantMedia, docs: variantMediaDocs },
];

function assertSeedQuality() {
  if (brandDocs.length !== 50) throw new Error(`Brand must have 50 rows, found ${brandDocs.length}.`);
  if (categoryDocs.length !== 33) throw new Error(`Category must match taxonomy: 33 rows, found ${categoryDocs.length}.`);
  if (productDocs.length !== 50) throw new Error(`Product must have exactly 50 makeup rows, found ${productDocs.length}.`);

  const min50Labels = [
    'ProductBeautyProfile',
    'ProductCategory',
    'ProductMedia',
    'ProductAttribute',
    'ProductOption',
    'ProductOptionValue',
    'ProductVariant',
    'VariantOptionValue',
    'VariantMedia',
  ];
  for (const item of seedPlan) {
    if (min50Labels.includes(item.label) && item.docs.length < 50) {
      throw new Error(`${item.label} has only ${item.docs.length} rows. Product mapping tables must have at least 50 rows.`);
    }
  }

  const leafCodes = categoryCatalog.filter((category) => category.parentCode).map((category) => category.categoryCode);
  const missingLeafCodes = leafCodes.filter((categoryCode) => !productCatalog.some((product) => product.categoryCode === categoryCode));
  if (missingLeafCodes.length) {
    throw new Error(`Missing product coverage for leaf categories: ${missingLeafCodes.join(', ')}`);
  }
}

async function resetSeedRows() {
  for (const item of [...seedPlan].reverse()) {
    await item.model.deleteMany({ _id: { $in: item.docs.map((doc) => doc._id) } });
  }
}

async function upsertById(model, docs) {
  if (!docs.length) return { upsertedCount: 0, modifiedCount: 0, matchedCount: 0 };
  const operations = docs.map((doc) => ({
    replaceOne: {
      filter: { _id: doc._id },
      replacement: doc,
      upsert: true,
    },
  }));
  return model.bulkWrite(operations, {
    ordered: false,
    throwOnValidationError: true
  });
}

async function runSeed() {
  assertSeedQuality();
  const mongoUri = process.env.MONGODB_URI || process.env.MONGO_URI || process.env.MONGODB_URL || process.env.DATABASE_URL;
  if (!mongoUri) {
    throw new Error('Missing MongoDB connection string. Set MONGODB_URI, MONGO_URI, MONGODB_URL, or DATABASE_URL.');
  }

  await mongoose.connect(mongoUri, process.env.MONGODB_DB_NAME ? { dbName: process.env.MONGODB_DB_NAME } : undefined);
  console.log(`Connected to MongoDB: ${mongoose.connection.name}`);

  if (SHOULD_RESET) {
    console.log('Reset mode enabled: deleting deterministic Kanila makeup product seed rows by _id...');
    await resetSeedRows();
  }

  for (const item of seedPlan) {
    const result = await upsertById(item.model, item.docs);
    console.log(`${item.label.padEnd(24)} => ${String(item.docs.length).padStart(3)} rows | upserted: ${result.upsertedCount || 0}, modified: ${result.modifiedCount || 0}, matched: ${result.matchedCount || 0}`);
  }

  console.log('Kanila makeup product seed completed successfully.');
}

if (require.main === module) {
  runSeed()
    .catch((error) => {
      console.error('Kanila makeup product seed failed:', error);
      process.exitCode = 1;
    })
    .finally(async () => {
      await mongoose.disconnect();
    });
}

module.exports = {
  brandDocs,
  categoryDocs,
  productDocs,
  productBeautyProfileDocs,
  productCategoryDocs,
  productMediaDocs,
  productAttributeDocs,
  productOptionDocs,
  productOptionValueDocs,
  productVariantDocs,
  variantOptionValueDocs,
  variantMediaDocs,
};
