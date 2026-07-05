/**
 * dataproduct.js
 * Kanila Beauty Commerce - real product catalog seed data.
 *
 * Scope seeded with mapped ObjectId references:
 * - brands: 50
 * - categories: 50
 * - products: 50
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
 * Image URLs are placeholders as requested. Replace IMAGE_BASE_URL or edit mediaUrl/imageUrl/logoUrl later.
 *
 * Usage from backend root:
 *   npm install dotenv mongoose
 *   MONGODB_URI="mongodb+srv://..." node dataproduct.js
 *
 * Optional:
 *   node dataproduct.js --reset
 *     Deletes records with the deterministic seed _id values before inserting again.
 *
 * If your models folder is not ./models from this file, set:
 *   KANILA_MODEL_BASE_PATH=../models node scripts/dataproduct.js
 */

require('dotenv').config();
const path = require('path');
const mongoose = require('mongoose');

const IMAGE_BASE_URL = process.env.KANILA_IMAGE_BASE_URL || 'https://example.com/kanila/products';
const LOGO_BASE_URL = process.env.KANILA_LOGO_BASE_URL || 'https://example.com/kanila/brands';
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
    "brandCode": "CERAVE",
    "brandName": "CeraVe",
    "description": "Thương hiệu chăm sóc da nổi tiếng với công thức ceramide và sản phẩm làm sạch, dưỡng ẩm dịu nhẹ."
  },
  {
    "brandCode": "LAROCHEPOSAY",
    "brandName": "La Roche-Posay",
    "description": "Dược mỹ phẩm Pháp tập trung vào chăm sóc da nhạy cảm, kem chống nắng và sản phẩm hỗ trợ da mụn."
  },
  {
    "brandCode": "ANESSA",
    "brandName": "Anessa",
    "description": "Thương hiệu chống nắng Nhật Bản nổi bật với các dòng sunscreen chống nước, phù hợp dùng hằng ngày."
  },
  {
    "brandCode": "SKIN1004",
    "brandName": "SKIN1004",
    "description": "Thương hiệu Hàn Quốc nổi tiếng với dòng Madagascar Centella cho da cần phục hồi và làm dịu."
  },
  {
    "brandCode": "COSRX",
    "brandName": "COSRX",
    "description": "Thương hiệu K-beauty phổ biến với các sản phẩm treatment, essence và chăm sóc da tối giản."
  },
  {
    "brandCode": "BEAUTYOFJOSEON",
    "brandName": "Beauty of Joseon",
    "description": "Thương hiệu Hàn Quốc lấy cảm hứng từ làm đẹp truyền thống, nổi bật với kem chống nắng và serum."
  },
  {
    "brandCode": "SOMEBYMI",
    "brandName": "Some By Mi",
    "description": "Thương hiệu K-beauty phổ biến với các dòng AHA, BHA, PHA và chăm sóc da dầu mụn."
  },
  {
    "brandCode": "THEORDINARY",
    "brandName": "The Ordinary",
    "description": "Thương hiệu nổi bật với công thức hoạt chất đơn giản, minh bạch và giá dễ tiếp cận."
  },
  {
    "brandCode": "PAULASCHOICE",
    "brandName": "Paula's Choice",
    "description": "Thương hiệu skincare nổi tiếng với BHA, retinol và các sản phẩm treatment dựa trên thành phần."
  },
  {
    "brandCode": "BIODERMA",
    "brandName": "Bioderma",
    "description": "Dược mỹ phẩm Pháp nổi bật với nước tẩy trang Sensibio H2O và các dòng cho da nhạy cảm."
  },
  {
    "brandCode": "GARNIER",
    "brandName": "Garnier",
    "description": "Thương hiệu chăm sóc cá nhân phổ biến với nước tẩy trang, chăm sóc tóc và skincare dễ tiếp cận."
  },
  {
    "brandCode": "HADALABO",
    "brandName": "Hada Labo",
    "description": "Thương hiệu Nhật Bản nổi bật với các dòng lotion cấp ẩm chứa hyaluronic acid."
  },
  {
    "brandCode": "KLAIRS",
    "brandName": "Klairs",
    "description": "Thương hiệu Hàn Quốc hướng đến công thức dịu nhẹ, phù hợp nhiều tình trạng da."
  },
  {
    "brandCode": "LANEIGE",
    "brandName": "Laneige",
    "description": "Thương hiệu Hàn Quốc nổi tiếng với cấp ẩm, cushion và các sản phẩm trang điểm nền."
  },
  {
    "brandCode": "INNISFREE",
    "brandName": "Innisfree",
    "description": "Thương hiệu Hàn Quốc nổi bật với trà xanh, đất sét và các sản phẩm skincare/makeup dễ dùng."
  },
  {
    "brandCode": "KIEHLS",
    "brandName": "Kiehl's",
    "description": "Thương hiệu chăm sóc da lâu đời với các dòng dưỡng ẩm, serum và sản phẩm phục hồi."
  },
  {
    "brandCode": "VICHY",
    "brandName": "Vichy",
    "description": "Dược mỹ phẩm Pháp nổi bật với nước khoáng núi lửa, serum cấp ẩm và chăm sóc da nhạy cảm."
  },
  {
    "brandCode": "EUCERIN",
    "brandName": "Eucerin",
    "description": "Dược mỹ phẩm tập trung vào chăm sóc da nhạy cảm, da khô và các vấn đề về hàng rào da."
  },
  {
    "brandCode": "SVR",
    "brandName": "SVR",
    "description": "Dược mỹ phẩm Pháp với nhiều dòng làm sạch, chống nắng và chăm sóc da dầu mụn."
  },
  {
    "brandCode": "AVENE",
    "brandName": "Avène",
    "description": "Dược mỹ phẩm Pháp nổi tiếng với nước khoáng Avène và sản phẩm phục hồi da."
  },
  {
    "brandCode": "CETAPHIL",
    "brandName": "Cetaphil",
    "description": "Thương hiệu chăm sóc da dịu nhẹ với sữa rửa mặt và dưỡng ẩm phổ biến."
  },
  {
    "brandCode": "NEUTROGENA",
    "brandName": "Neutrogena",
    "description": "Thương hiệu chăm sóc da phổ biến với dòng Hydro Boost và sản phẩm làm sạch."
  },
  {
    "brandCode": "MAYBELLINE",
    "brandName": "Maybelline",
    "description": "Thương hiệu makeup đại chúng nổi bật với foundation, mascara và son môi."
  },
  {
    "brandCode": "LOREALPARIS",
    "brandName": "L'Oréal Paris",
    "description": "Thương hiệu làm đẹp quốc tế với skincare, makeup và chăm sóc tóc đa dạng."
  },
  {
    "brandCode": "ROMAND",
    "brandName": "Rom&nd",
    "description": "Thương hiệu K-beauty nổi tiếng với son tint, bảng màu trẻ trung và makeup Hàn Quốc."
  },
  {
    "brandCode": "PERIPERA",
    "brandName": "Peripera",
    "description": "Thương hiệu makeup Hàn Quốc nổi bật với son tint, màu sắc trẻ trung và bao bì đáng yêu."
  },
  {
    "brandCode": "THREECE",
    "brandName": "3CE",
    "description": "Thương hiệu makeup Hàn Quốc nổi bật với son, phấn mắt và phong cách thời trang."
  },
  {
    "brandCode": "CLIO",
    "brandName": "Clio",
    "description": "Thương hiệu makeup Hàn Quốc nổi tiếng với cushion, eyeliner và sản phẩm nền."
  },
  {
    "brandCode": "ETUDE",
    "brandName": "Etude",
    "description": "Thương hiệu K-beauty trẻ trung với son tint, makeup mắt và sản phẩm dễ dùng."
  },
  {
    "brandCode": "MAC",
    "brandName": "MAC",
    "description": "Thương hiệu makeup chuyên nghiệp nổi tiếng với son môi và sản phẩm nền."
  },
  {
    "brandCode": "NARS",
    "brandName": "NARS",
    "description": "Thương hiệu makeup cao cấp nổi bật với concealer, blush và sản phẩm nền."
  },
  {
    "brandCode": "FENTYBEAUTY",
    "brandName": "Fenty Beauty",
    "description": "Thương hiệu makeup nổi bật với dải màu nền đa dạng và phong cách hiện đại."
  },
  {
    "brandCode": "RAREBEAUTY",
    "brandName": "Rare Beauty",
    "description": "Thương hiệu makeup nổi tiếng với liquid blush, highlighter và tinh thần tự nhiên."
  },
  {
    "brandCode": "DIOR",
    "brandName": "Dior",
    "description": "Thương hiệu làm đẹp cao cấp với son dưỡng màu, nước hoa và makeup sang trọng."
  },
  {
    "brandCode": "YSLBEAUTY",
    "brandName": "YSL Beauty",
    "description": "Thương hiệu beauty cao cấp nổi bật với son môi, nền và hương thơm."
  },
  {
    "brandCode": "SHUUEMURA",
    "brandName": "Shu Uemura",
    "description": "Thương hiệu Nhật Bản nổi tiếng với makeup chuyên nghiệp, chì mày và dầu tẩy trang."
  },
  {
    "brandCode": "KISSME",
    "brandName": "Kiss Me Heroine Make",
    "description": "Thương hiệu Nhật Bản nổi bật với mascara và eyeliner chống lem."
  },
  {
    "brandCode": "SHISEIDO",
    "brandName": "Shiseido",
    "description": "Thương hiệu Nhật Bản lâu đời với skincare và mỹ phẩm cao cấp."
  },
  {
    "brandCode": "SULWHASOO",
    "brandName": "Sulwhasoo",
    "description": "Thương hiệu Hàn Quốc cao cấp nổi bật với skincare lấy cảm hứng từ thảo mộc Á Đông."
  },
  {
    "brandCode": "MEDIHEAL",
    "brandName": "Mediheal",
    "description": "Thương hiệu mặt nạ Hàn Quốc phổ biến với nhiều dòng sheet mask."
  },
  {
    "brandCode": "DRJART",
    "brandName": "Dr.Jart+",
    "description": "Thương hiệu Hàn Quốc nổi bật với Cicapair, Ceramidin và chăm sóc da nhạy cảm."
  },
  {
    "brandCode": "TORRIDEN",
    "brandName": "Torriden",
    "description": "Thương hiệu K-beauty nổi tiếng với serum cấp ẩm hyaluronic acid."
  },
  {
    "brandCode": "ROUNDLAB",
    "brandName": "Round Lab",
    "description": "Thương hiệu Hàn Quốc nổi bật với dòng Dokdo, Birch Juice và skincare dịu nhẹ."
  },
  {
    "brandCode": "NIVEA",
    "brandName": "Nivea",
    "description": "Thương hiệu chăm sóc da đại chúng với kem dưỡng, chống nắng và chăm sóc cơ thể."
  },
  {
    "brandCode": "SENKA",
    "brandName": "Senka",
    "description": "Thương hiệu Nhật Bản nổi tiếng với sữa rửa mặt Perfect Whip."
  },
  {
    "brandCode": "BIORE",
    "brandName": "Bioré",
    "description": "Thương hiệu Nhật Bản phổ biến với kem chống nắng, tẩy trang và sản phẩm làm sạch."
  },
  {
    "brandCode": "SIMPLE",
    "brandName": "Simple",
    "description": "Thương hiệu skincare hướng đến công thức đơn giản, dịu nhẹ và không gây nặng da."
  },
  {
    "brandCode": "THESAEM",
    "brandName": "The Saem",
    "description": "Thương hiệu Hàn Quốc phổ biến với concealer và makeup tiện dụng."
  },
  {
    "brandCode": "CANMAKE",
    "brandName": "Canmake",
    "description": "Thương hiệu makeup Nhật Bản nổi bật với phấn phủ, má hồng và bao bì nhỏ gọn."
  },
  {
    "brandCode": "ELF",
    "brandName": "e.l.f. Cosmetics",
    "description": "Thương hiệu makeup dễ tiếp cận với primer, cọ trang điểm và sản phẩm nền."
  }
];

const categoryCatalog = [
  {
    "categoryCode": "CAT_CLEANSER",
    "categoryName": "Sữa rửa mặt",
    "description": "Làm sạch da mặt hằng ngày, loại bỏ dầu thừa và bụi bẩn."
  },
  {
    "categoryCode": "CAT_MICELLAR_WATER",
    "categoryName": "Nước tẩy trang",
    "description": "Tẩy trang, làm sạch lớp trang điểm nhẹ và kem chống nắng."
  },
  {
    "categoryCode": "CAT_TONER",
    "categoryName": "Toner",
    "description": "Cân bằng, cấp ẩm nhẹ hoặc hỗ trợ làm sạch sau rửa mặt."
  },
  {
    "categoryCode": "CAT_ESSENCE",
    "categoryName": "Essence",
    "description": "Tinh chất lỏng hỗ trợ cấp ẩm, phục hồi hoặc làm sáng."
  },
  {
    "categoryCode": "CAT_SERUM",
    "categoryName": "Serum",
    "description": "Sản phẩm hoạt chất tập trung theo mục tiêu da."
  },
  {
    "categoryCode": "CAT_AMPOULE",
    "categoryName": "Ampoule",
    "description": "Tinh chất cô đặc, thường dùng trong chu trình phục hồi hoặc cấp ẩm."
  },
  {
    "categoryCode": "CAT_MOISTURIZER",
    "categoryName": "Kem dưỡng",
    "description": "Khóa ẩm, hỗ trợ hàng rào bảo vệ da."
  },
  {
    "categoryCode": "CAT_SUNSCREEN",
    "categoryName": "Kem chống nắng",
    "description": "Bảo vệ da ban ngày, dùng trước khi ra ngoài."
  },
  {
    "categoryCode": "CAT_LIP_TINT",
    "categoryName": "Son tint",
    "description": "Son nước/son tint cho hiệu ứng màu trẻ trung, bám màu."
  },
  {
    "categoryCode": "CAT_LIPSTICK",
    "categoryName": "Son thỏi",
    "description": "Son môi dạng thỏi, nhiều finish và tông màu."
  },
  {
    "categoryCode": "CAT_FOUNDATION",
    "categoryName": "Kem nền",
    "description": "Trang điểm nền, hỗ trợ đều màu da."
  },
  {
    "categoryCode": "CAT_CUSHION",
    "categoryName": "Cushion",
    "description": "Phấn nước tiện lợi cho lớp nền nhanh gọn."
  },
  {
    "categoryCode": "CAT_CONCEALER",
    "categoryName": "Che khuyết điểm",
    "description": "Che quầng thâm, mụn và vùng da không đều màu."
  },
  {
    "categoryCode": "CAT_BLUSH",
    "categoryName": "Má hồng",
    "description": "Tạo sắc hồng tự nhiên hoặc nổi bật cho gò má."
  },
  {
    "categoryCode": "CAT_MASCARA",
    "categoryName": "Mascara",
    "description": "Làm dài, cong hoặc dày mi."
  },
  {
    "categoryCode": "CAT_EYEBROW",
    "categoryName": "Chì kẻ mày",
    "description": "Định hình lông mày và hoàn thiện makeup mắt."
  },
  {
    "categoryCode": "CAT_SHEET_MASK",
    "categoryName": "Mặt nạ giấy",
    "description": "Cấp ẩm, làm dịu hoặc chăm sóc da nhanh."
  },
  {
    "categoryCode": "CAT_TREATMENT",
    "categoryName": "Treatment",
    "description": "Sản phẩm chăm sóc chuyên sâu theo vấn đề da."
  },
  {
    "categoryCode": "CAT_EXFOLIANT",
    "categoryName": "Tẩy da chết hóa học",
    "description": "AHA/BHA/PHA hoặc hoạt chất hỗ trợ làm sạch bề mặt da."
  },
  {
    "categoryCode": "CAT_COLOR_CORRECTOR",
    "categoryName": "Hiệu chỉnh màu da",
    "description": "Sản phẩm trang điểm hiệu chỉnh sắc tố, redness hoặc xỉn màu."
  },
  {
    "categoryCode": "CAT_CLEANSING_OIL",
    "categoryName": "Dầu tẩy trang",
    "description": "Tẩy trang nền dầu, phù hợp makeup hoặc kem chống nắng bám tốt."
  },
  {
    "categoryCode": "CAT_CLEANSING_BALM",
    "categoryName": "Sáp tẩy trang",
    "description": "Tẩy trang dạng balm, tiện lợi khi massage làm sạch."
  },
  {
    "categoryCode": "CAT_FACE_MIST",
    "categoryName": "Xịt khoáng",
    "description": "Cấp ẩm tức thì hoặc làm dịu da trong ngày."
  },
  {
    "categoryCode": "CAT_EYE_CREAM",
    "categoryName": "Kem mắt",
    "description": "Chăm sóc vùng da quanh mắt."
  },
  {
    "categoryCode": "CAT_LIP_BALM",
    "categoryName": "Son dưỡng",
    "description": "Dưỡng môi, có thể có màu hoặc không màu."
  },
  {
    "categoryCode": "CAT_POWDER",
    "categoryName": "Phấn phủ",
    "description": "Cố định nền, giảm bóng dầu."
  },
  {
    "categoryCode": "CAT_SETTING_SPRAY",
    "categoryName": "Xịt khóa nền",
    "description": "Giúp lớp makeup bền hơn."
  },
  {
    "categoryCode": "CAT_PRIMER",
    "categoryName": "Kem lót",
    "description": "Chuẩn bị bề mặt da trước makeup."
  },
  {
    "categoryCode": "CAT_HIGHLIGHTER",
    "categoryName": "Bắt sáng",
    "description": "Tạo điểm sáng cho khuôn mặt."
  },
  {
    "categoryCode": "CAT_CONTOUR",
    "categoryName": "Tạo khối",
    "description": "Tạo chiều sâu cho gương mặt."
  },
  {
    "categoryCode": "CAT_EYESHADOW",
    "categoryName": "Phấn mắt",
    "description": "Trang điểm mắt với nhiều tông màu."
  },
  {
    "categoryCode": "CAT_EYELINER",
    "categoryName": "Kẻ mắt",
    "description": "Định hình đường viền mắt."
  },
  {
    "categoryCode": "CAT_BODY_LOTION",
    "categoryName": "Sữa dưỡng thể",
    "description": "Dưỡng ẩm da cơ thể."
  },
  {
    "categoryCode": "CAT_BODY_SUNSCREEN",
    "categoryName": "Chống nắng cơ thể",
    "description": "Bảo vệ vùng da body khi đi ngoài trời."
  },
  {
    "categoryCode": "CAT_SHAMPOO",
    "categoryName": "Dầu gội",
    "description": "Làm sạch tóc và da đầu."
  },
  {
    "categoryCode": "CAT_CONDITIONER",
    "categoryName": "Dầu xả",
    "description": "Làm mềm và gỡ rối tóc."
  },
  {
    "categoryCode": "CAT_HAIR_TREATMENT",
    "categoryName": "Ủ tóc/serum tóc",
    "description": "Chăm sóc tóc hư tổn hoặc khô xơ."
  },
  {
    "categoryCode": "CAT_FRAGRANCE",
    "categoryName": "Nước hoa",
    "description": "Sản phẩm tạo mùi hương cá nhân."
  },
  {
    "categoryCode": "CAT_MEN_SKINCARE",
    "categoryName": "Skincare nam",
    "description": "Sản phẩm chăm sóc da cho nam giới."
  },
  {
    "categoryCode": "CAT_HAND_CREAM",
    "categoryName": "Kem tay",
    "description": "Dưỡng ẩm da tay."
  },
  {
    "categoryCode": "CAT_MAKEUP_REMOVER",
    "categoryName": "Tẩy trang makeup",
    "description": "Làm sạch lớp makeup bền màu."
  },
  {
    "categoryCode": "CAT_ACNE_CARE",
    "categoryName": "Chăm sóc da mụn",
    "description": "Sản phẩm hỗ trợ routine cho da dầu mụn."
  },
  {
    "categoryCode": "CAT_BRIGHTENING",
    "categoryName": "Dưỡng sáng",
    "description": "Sản phẩm hướng đến làn da tươi sáng hơn."
  },
  {
    "categoryCode": "CAT_ANTI_AGING",
    "categoryName": "Chống lão hóa",
    "description": "Sản phẩm chăm sóc da có dấu hiệu tuổi tác."
  },
  {
    "categoryCode": "CAT_SENSITIVE_CARE",
    "categoryName": "Da nhạy cảm",
    "description": "Sản phẩm dịu nhẹ, ưu tiên tối giản."
  },
  {
    "categoryCode": "CAT_BARRIER_REPAIR",
    "categoryName": "Phục hồi hàng rào da",
    "description": "Sản phẩm hỗ trợ hàng rào bảo vệ da."
  },
  {
    "categoryCode": "CAT_HYDRATION",
    "categoryName": "Cấp ẩm",
    "description": "Sản phẩm tăng độ ẩm và giảm cảm giác khô căng."
  },
  {
    "categoryCode": "CAT_OIL_CONTROL",
    "categoryName": "Kiểm soát dầu",
    "description": "Sản phẩm dành cho da dầu hoặc vùng chữ T bóng dầu."
  },
  {
    "categoryCode": "CAT_PORE_CARE",
    "categoryName": "Chăm sóc lỗ chân lông",
    "description": "Sản phẩm hỗ trợ bề mặt da mịn hơn."
  },
  {
    "categoryCode": "CAT_TRAVEL_SIZE",
    "categoryName": "Mini/Travel size",
    "description": "Sản phẩm dung tích nhỏ, tiện mang theo."
  }
];

const productCatalog = [
  {
    "brandCode": "CERAVE",
    "productName": "CeraVe Hydrating Cleanser",
    "categoryCode": "CAT_CLEANSER",
    "price": 385000,
    "compareAtPrice": 449000,
    "sizeOptions": [
      "473ml",
      "236ml"
    ],
    "ingredients": [
      "Ceramides",
      "Hyaluronic Acid",
      "Glycerin"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "sensitive"
    ],
    "concerns": [
      "hydration",
      "barrier_repair",
      "sensitive_care"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "United States",
    "type": "sữa rửa mặt dưỡng ẩm",
    "texture": "cream gel",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "LAROCHEPOSAY",
    "productName": "La Roche-Posay Anthelios UVMune 400 Invisible Fluid SPF50+",
    "categoryCode": "CAT_SUNSCREEN",
    "price": 535000,
    "compareAtPrice": 610000,
    "sizeOptions": [
      "50ml",
      "30ml"
    ],
    "ingredients": [
      "Mexoryl 400",
      "Glycerin",
      "Vitamin E"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "sensitive"
    ],
    "concerns": [
      "sun_protection",
      "sensitive_care",
      "oil_control"
    ],
    "finish": "natural",
    "coverage": "sheer",
    "country": "France",
    "type": "kem chống nắng dạng fluid",
    "texture": "fluid",
    "sensitive": true,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "ANESSA",
    "productName": "Anessa Perfect UV Sunscreen Skincare Milk",
    "categoryCode": "CAT_SUNSCREEN",
    "price": 515000,
    "compareAtPrice": 585000,
    "sizeOptions": [
      "60ml",
      "20ml"
    ],
    "ingredients": [
      "Zinc Oxide",
      "Hyaluronic Acid",
      "Green Tea Extract"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "sun_protection",
      "oil_control",
      "long_wear"
    ],
    "finish": "semi_matte",
    "coverage": "sheer",
    "country": "Japan",
    "type": "sữa chống nắng",
    "texture": "milk",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "SKIN1004",
    "productName": "SKIN1004 Madagascar Centella Ampoule",
    "categoryCode": "CAT_AMPOULE",
    "price": 325000,
    "compareAtPrice": 389000,
    "sizeOptions": [
      "55ml",
      "100ml"
    ],
    "ingredients": [
      "Centella Asiatica Extract"
    ],
    "skinTypes": [
      "all",
      "sensitive",
      "combination"
    ],
    "concerns": [
      "soothing",
      "barrier_repair",
      "redness"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "Korea",
    "type": "ampoule centella",
    "texture": "watery ampoule",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "COSRX",
    "productName": "COSRX Advanced Snail 96 Mucin Power Essence",
    "categoryCode": "CAT_ESSENCE",
    "price": 359000,
    "compareAtPrice": 425000,
    "sizeOptions": [
      "100ml",
      "50ml"
    ],
    "ingredients": [
      "Snail Secretion Filtrate",
      "Betaine",
      "Allantoin"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "barrier_repair",
      "texture"
    ],
    "finish": "glow",
    "coverage": "none",
    "country": "Korea",
    "type": "essence phục hồi",
    "texture": "viscous essence",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "BEAUTYOFJOSEON",
    "productName": "Beauty of Joseon Relief Sun: Rice + Probiotics SPF50+ PA++++",
    "categoryCode": "CAT_SUNSCREEN",
    "price": 285000,
    "compareAtPrice": 340000,
    "sizeOptions": [
      "50ml",
      "10ml"
    ],
    "ingredients": [
      "Rice Extract",
      "Probiotics",
      "Niacinamide"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "sun_protection",
      "hydration",
      "brightening"
    ],
    "finish": "dewy",
    "coverage": "sheer",
    "country": "Korea",
    "type": "kem chống nắng lai dưỡng da",
    "texture": "cream",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "SOMEBYMI",
    "productName": "Some By Mi AHA-BHA-PHA 30 Days Miracle Toner",
    "categoryCode": "CAT_TONER",
    "price": 299000,
    "compareAtPrice": 360000,
    "sizeOptions": [
      "150ml",
      "30ml"
    ],
    "ingredients": [
      "AHA",
      "BHA",
      "PHA",
      "Tea Tree Water"
    ],
    "skinTypes": [
      "oily",
      "combination"
    ],
    "concerns": [
      "acne_care",
      "oil_control",
      "pore_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "Korea",
    "type": "toner treatment",
    "texture": "watery toner",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "THEORDINARY",
    "productName": "The Ordinary Niacinamide 10% + Zinc 1%",
    "categoryCode": "CAT_SERUM",
    "price": 245000,
    "compareAtPrice": 299000,
    "sizeOptions": [
      "30ml",
      "60ml"
    ],
    "ingredients": [
      "Niacinamide",
      "Zinc PCA"
    ],
    "skinTypes": [
      "oily",
      "combination",
      "normal"
    ],
    "concerns": [
      "oil_control",
      "pore_care",
      "brightening"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "Canada",
    "type": "serum niacinamide",
    "texture": "serum",
    "sensitive": false,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "PAULASCHOICE",
    "productName": "Paula's Choice Skin Perfecting 2% BHA Liquid Exfoliant",
    "categoryCode": "CAT_EXFOLIANT",
    "price": 760000,
    "compareAtPrice": 890000,
    "sizeOptions": [
      "118ml",
      "30ml"
    ],
    "ingredients": [
      "Salicylic Acid",
      "Green Tea Extract",
      "Methylpropanediol"
    ],
    "skinTypes": [
      "oily",
      "combination"
    ],
    "concerns": [
      "pore_care",
      "acne_care",
      "texture"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "United States",
    "type": "tẩy da chết BHA",
    "texture": "liquid",
    "sensitive": false,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "BIODERMA",
    "productName": "Bioderma Sensibio H2O Micellar Water",
    "categoryCode": "CAT_MICELLAR_WATER",
    "price": 385000,
    "compareAtPrice": 450000,
    "sizeOptions": [
      "500ml",
      "250ml"
    ],
    "ingredients": [
      "Micelles",
      "Cucumber Extract"
    ],
    "skinTypes": [
      "all",
      "sensitive"
    ],
    "concerns": [
      "cleansing",
      "sensitive_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "France",
    "type": "nước tẩy trang",
    "texture": "water",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "GARNIER",
    "productName": "Garnier SkinActive Micellar Cleansing Water",
    "categoryCode": "CAT_MICELLAR_WATER",
    "price": 169000,
    "compareAtPrice": 205000,
    "sizeOptions": [
      "400ml",
      "125ml"
    ],
    "ingredients": [
      "Micelles",
      "Glycerin"
    ],
    "skinTypes": [
      "all",
      "sensitive"
    ],
    "concerns": [
      "cleansing",
      "hydration"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "France",
    "type": "nước tẩy trang",
    "texture": "water",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "HADALABO",
    "productName": "Hada Labo Gokujyun Premium Hydrating Lotion",
    "categoryCode": "CAT_TONER",
    "price": 285000,
    "compareAtPrice": 335000,
    "sizeOptions": [
      "170ml",
      "100ml"
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Super Hyaluronic Acid",
      "Glycerin"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "barrier_repair"
    ],
    "finish": "dewy",
    "coverage": "none",
    "country": "Japan",
    "type": "lotion cấp ẩm",
    "texture": "rich lotion",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "KLAIRS",
    "productName": "Klairs Supple Preparation Unscented Toner",
    "categoryCode": "CAT_TONER",
    "price": 335000,
    "compareAtPrice": 399000,
    "sizeOptions": [
      "180ml",
      "30ml"
    ],
    "ingredients": [
      "Beta Glucan",
      "Centella Asiatica",
      "Hyaluronic Acid"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "sensitive"
    ],
    "concerns": [
      "hydration",
      "soothing",
      "sensitive_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "Korea",
    "type": "toner dịu nhẹ",
    "texture": "watery toner",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "LANEIGE",
    "productName": "Laneige Water Bank Blue Hyaluronic Cream",
    "categoryCode": "CAT_MOISTURIZER",
    "price": 760000,
    "compareAtPrice": 890000,
    "sizeOptions": [
      "50ml",
      "25ml"
    ],
    "ingredients": [
      "Blue Hyaluronic Acid",
      "Squalane",
      "Ceramide"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "barrier_repair"
    ],
    "finish": "dewy",
    "coverage": "none",
    "country": "Korea",
    "type": "kem dưỡng cấp ẩm",
    "texture": "cream",
    "sensitive": true,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "INNISFREE",
    "productName": "Innisfree Green Tea Seed Hyaluronic Serum",
    "categoryCode": "CAT_SERUM",
    "price": 580000,
    "compareAtPrice": 680000,
    "sizeOptions": [
      "80ml",
      "30ml"
    ],
    "ingredients": [
      "Green Tea Extract",
      "Hyaluronic Acid",
      "Panthenol"
    ],
    "skinTypes": [
      "all",
      "combination"
    ],
    "concerns": [
      "hydration",
      "freshness"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "Korea",
    "type": "serum trà xanh",
    "texture": "serum",
    "sensitive": true,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "KIEHLS",
    "productName": "Kiehl's Ultra Facial Cream",
    "categoryCode": "CAT_MOISTURIZER",
    "price": 1150000,
    "compareAtPrice": 1350000,
    "sizeOptions": [
      "50ml",
      "28ml"
    ],
    "ingredients": [
      "Squalane",
      "Glacial Glycoprotein",
      "Glycerin"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "barrier_repair"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "United States",
    "type": "kem dưỡng ẩm",
    "texture": "cream",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "VICHY",
    "productName": "Vichy Minéral 89 Hyaluronic Acid Booster",
    "categoryCode": "CAT_SERUM",
    "price": 780000,
    "compareAtPrice": 910000,
    "sizeOptions": [
      "50ml",
      "30ml"
    ],
    "ingredients": [
      "Vichy Volcanic Water",
      "Hyaluronic Acid"
    ],
    "skinTypes": [
      "all",
      "sensitive"
    ],
    "concerns": [
      "hydration",
      "barrier_repair",
      "sensitive_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "France",
    "type": "booster cấp ẩm",
    "texture": "gel serum",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "EUCERIN",
    "productName": "Eucerin ProACNE Solution A.I. Clearing Treatment",
    "categoryCode": "CAT_ACNE_CARE",
    "price": 510000,
    "compareAtPrice": 610000,
    "sizeOptions": [
      "40ml",
      "20ml"
    ],
    "ingredients": [
      "Thiamidol",
      "Licochalcone A",
      "Salicylic Acid"
    ],
    "skinTypes": [
      "oily",
      "combination"
    ],
    "concerns": [
      "acne_care",
      "dark_spot",
      "oil_control"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "Germany",
    "type": "treatment hỗ trợ da mụn",
    "texture": "cream gel",
    "sensitive": false,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "SVR",
    "productName": "SVR Sebiaclear Gel Moussant",
    "categoryCode": "CAT_CLEANSER",
    "price": 285000,
    "compareAtPrice": 335000,
    "sizeOptions": [
      "200ml",
      "400ml"
    ],
    "ingredients": [
      "Gluconolactone",
      "Niacinamide"
    ],
    "skinTypes": [
      "oily",
      "combination"
    ],
    "concerns": [
      "cleansing",
      "oil_control",
      "acne_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "France",
    "type": "gel rửa mặt",
    "texture": "gel",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "AVENE",
    "productName": "Avène Cicalfate+ Restorative Protective Cream",
    "categoryCode": "CAT_BARRIER_REPAIR",
    "price": 390000,
    "compareAtPrice": 455000,
    "sizeOptions": [
      "40ml",
      "100ml"
    ],
    "ingredients": [
      "Avène Thermal Spring Water",
      "Copper-Zinc Sulfate",
      "Sucralfate"
    ],
    "skinTypes": [
      "dry",
      "sensitive",
      "normal"
    ],
    "concerns": [
      "barrier_repair",
      "soothing",
      "sensitive_care"
    ],
    "finish": "rich",
    "coverage": "none",
    "country": "France",
    "type": "kem phục hồi",
    "texture": "rich cream",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "CETAPHIL",
    "productName": "Cetaphil Gentle Skin Cleanser",
    "categoryCode": "CAT_CLEANSER",
    "price": 295000,
    "compareAtPrice": 350000,
    "sizeOptions": [
      "500ml",
      "236ml"
    ],
    "ingredients": [
      "Glycerin",
      "Niacinamide",
      "Panthenol"
    ],
    "skinTypes": [
      "all",
      "sensitive"
    ],
    "concerns": [
      "cleansing",
      "hydration",
      "sensitive_care"
    ],
    "finish": "soft",
    "coverage": "none",
    "country": "Canada",
    "type": "sữa rửa mặt dịu nhẹ",
    "texture": "lotion cleanser",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "NEUTROGENA",
    "productName": "Neutrogena Hydro Boost Water Gel",
    "categoryCode": "CAT_MOISTURIZER",
    "price": 355000,
    "compareAtPrice": 425000,
    "sizeOptions": [
      "50g",
      "15g"
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Glycerin"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "hydration",
      "freshness"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "United States",
    "type": "gel dưỡng ẩm",
    "texture": "water gel",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "MAYBELLINE",
    "productName": "Maybelline Fit Me Matte + Poreless Foundation",
    "categoryCode": "CAT_FOUNDATION",
    "price": 235000,
    "compareAtPrice": 299000,
    "sizeOptions": [
      "110 Porcelain",
      "128 Warm Nude"
    ],
    "ingredients": [
      "Micro Powder",
      "Glycerin"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "oil_control",
      "pore_care",
      "makeup_base"
    ],
    "finish": "matte",
    "coverage": "medium",
    "country": "United States",
    "type": "kem nền lì",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "shades": [
      [
        "110 Porcelain",
        "#f1d2bd",
        "cool"
      ],
      [
        "128 Warm Nude",
        "#d9ad8b",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "LOREALPARIS",
    "productName": "L'Oréal Paris True Match Liquid Foundation",
    "categoryCode": "CAT_FOUNDATION",
    "price": 315000,
    "compareAtPrice": 380000,
    "sizeOptions": [
      "1N Ivory",
      "3W Golden Beige"
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Vitamin E"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "makeup_base",
      "tone_evening"
    ],
    "finish": "natural",
    "coverage": "medium",
    "country": "France",
    "type": "kem nền tự nhiên",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "shades": [
      [
        "1N Ivory",
        "#efd0ba",
        "neutral"
      ],
      [
        "3W Golden Beige",
        "#d7a276",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "ROMAND",
    "productName": "Rom&nd Juicy Lasting Tint",
    "categoryCode": "CAT_LIP_TINT",
    "price": 175000,
    "compareAtPrice": 220000,
    "sizeOptions": [
      "06 Figfig",
      "13 Eat Dotori"
    ],
    "ingredients": [
      "Fruit Extract",
      "Glycerin"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "long_wear"
    ],
    "finish": "glossy",
    "coverage": "buildable",
    "country": "Korea",
    "type": "son tint bóng",
    "texture": "gloss tint",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "06 Figfig",
        "#b15a61",
        "neutral"
      ],
      [
        "13 Eat Dotori",
        "#9b3f2f",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "PERIPERA",
    "productName": "Peripera Ink Velvet Lip Tint",
    "categoryCode": "CAT_LIP_TINT",
    "price": 165000,
    "compareAtPrice": 210000,
    "sizeOptions": [
      "17 Rosy Nude",
      "29 Cocoa Nude"
    ],
    "ingredients": [
      "Silicone Elastomer",
      "Moisturizing Oil"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "long_wear"
    ],
    "finish": "velvet",
    "coverage": "medium",
    "country": "Korea",
    "type": "son tint lì mịn",
    "texture": "velvet tint",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "17 Rosy Nude",
        "#c06b6f",
        "neutral"
      ],
      [
        "29 Cocoa Nude",
        "#8b4a3f",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "THREECE",
    "productName": "3CE Velvet Lip Tint",
    "categoryCode": "CAT_LIP_TINT",
    "price": 335000,
    "compareAtPrice": 390000,
    "sizeOptions": [
      "Daffodil",
      "Going Right"
    ],
    "ingredients": [
      "Dimethicone",
      "Color Pigments"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "long_wear"
    ],
    "finish": "velvet",
    "coverage": "medium",
    "country": "Korea",
    "type": "son tint velvet",
    "texture": "velvet tint",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "Daffodil",
        "#b64b3d",
        "warm"
      ],
      [
        "Going Right",
        "#c16a58",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "CLIO",
    "productName": "Clio Kill Cover Mesh Glow Cushion",
    "categoryCode": "CAT_CUSHION",
    "price": 520000,
    "compareAtPrice": 620000,
    "sizeOptions": [
      "02 Lingerie",
      "03 Linen"
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Pearl Extract"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "makeup_base",
      "hydration"
    ],
    "finish": "glow",
    "coverage": "medium",
    "country": "Korea",
    "type": "cushion glow",
    "texture": "cushion foundation",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "shades": [
      [
        "02 Lingerie",
        "#efd3c3",
        "cool"
      ],
      [
        "03 Linen",
        "#e5c0a5",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "ETUDE",
    "productName": "Etude Dear Darling Water Gel Tint",
    "categoryCode": "CAT_LIP_TINT",
    "price": 115000,
    "compareAtPrice": 150000,
    "sizeOptions": [
      "PK003 Sweet Potato Red",
      "RD306 Shark Red"
    ],
    "ingredients": [
      "Pomegranate Extract",
      "Soapberry Extract"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color"
    ],
    "finish": "glossy",
    "coverage": "sheer",
    "country": "Korea",
    "type": "son tint nước",
    "texture": "water gel tint",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "PK003 Sweet Potato Red",
        "#b34258",
        "cool"
      ],
      [
        "RD306 Shark Red",
        "#c2182b",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "MAC",
    "productName": "MAC Retro Matte Lipstick Ruby Woo",
    "categoryCode": "CAT_LIPSTICK",
    "price": 620000,
    "compareAtPrice": 720000,
    "sizeOptions": [
      "Ruby Woo 3g",
      "Mini Ruby Woo 1.8g"
    ],
    "ingredients": [
      "Castor Seed Oil",
      "Wax Blend"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "long_wear"
    ],
    "finish": "matte",
    "coverage": "full",
    "country": "Canada",
    "type": "son thỏi lì",
    "texture": "bullet lipstick",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "Ruby Woo",
        "#b10f2e",
        "cool"
      ],
      [
        "Ruby Woo Mini",
        "#b10f2e",
        "cool"
      ]
    ]
  },
  {
    "brandCode": "NARS",
    "productName": "NARS Radiant Creamy Concealer",
    "categoryCode": "CAT_CONCEALER",
    "price": 860000,
    "compareAtPrice": 990000,
    "sizeOptions": [
      "Vanilla",
      "Custard"
    ],
    "ingredients": [
      "Glycerin",
      "Vitamin E"
    ],
    "skinTypes": [
      "normal",
      "dry",
      "combination"
    ],
    "concerns": [
      "makeup_base",
      "dark_circle"
    ],
    "finish": "natural",
    "coverage": "medium_full",
    "country": "United States",
    "type": "kem che khuyết điểm",
    "texture": "creamy concealer",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "Vanilla",
        "#efcfb8",
        "neutral"
      ],
      [
        "Custard",
        "#d7aa82",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "FENTYBEAUTY",
    "productName": "Fenty Beauty Pro Filt'r Soft Matte Foundation",
    "categoryCode": "CAT_FOUNDATION",
    "price": 980000,
    "compareAtPrice": 1150000,
    "sizeOptions": [
      "150 Light Neutral",
      "230 Medium Neutral"
    ],
    "ingredients": [
      "Silica",
      "Glycerin"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "makeup_base",
      "oil_control",
      "long_wear"
    ],
    "finish": "soft_matte",
    "coverage": "medium_full",
    "country": "United States",
    "type": "kem nền lì mềm",
    "texture": "liquid foundation",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "150 Light Neutral",
        "#e7c6a8",
        "neutral"
      ],
      [
        "230 Medium Neutral",
        "#c88e68",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "RAREBEAUTY",
    "productName": "Rare Beauty Soft Pinch Liquid Blush",
    "categoryCode": "CAT_BLUSH",
    "price": 690000,
    "compareAtPrice": 790000,
    "sizeOptions": [
      "Hope",
      "Joy"
    ],
    "ingredients": [
      "Botanical Blend",
      "Color Pigments"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "makeup_color",
      "long_wear"
    ],
    "finish": "natural",
    "coverage": "buildable",
    "country": "United States",
    "type": "má hồng dạng kem lỏng",
    "texture": "liquid blush",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "Hope",
        "#c67575",
        "neutral"
      ],
      [
        "Joy",
        "#e57354",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "DIOR",
    "productName": "Dior Addict Lip Glow",
    "categoryCode": "CAT_LIP_BALM",
    "price": 980000,
    "compareAtPrice": 1150000,
    "sizeOptions": [
      "001 Pink",
      "004 Coral"
    ],
    "ingredients": [
      "Cherry Oil",
      "Shea Butter"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_care",
      "lip_color"
    ],
    "finish": "glow",
    "coverage": "sheer",
    "country": "France",
    "type": "son dưỡng có màu",
    "texture": "balm",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "001 Pink",
        "#f2a3b5",
        "cool"
      ],
      [
        "004 Coral",
        "#f08d70",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "YSLBEAUTY",
    "productName": "YSL Rouge Volupté Shine Lipstick Balm",
    "categoryCode": "CAT_LIPSTICK",
    "price": 950000,
    "compareAtPrice": 1100000,
    "sizeOptions": [
      "No.12 Corail Dolman",
      "No.45 Rouge Tuxedo"
    ],
    "ingredients": [
      "Pomegranate Extract",
      "Macadamia Butter"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "lip_color",
      "lip_care"
    ],
    "finish": "shine",
    "coverage": "medium",
    "country": "France",
    "type": "son thỏi bóng dưỡng",
    "texture": "balm lipstick",
    "sensitive": false,
    "fragrance": "fragranced",
    "shades": [
      [
        "No.12 Corail Dolman",
        "#d95b4a",
        "warm"
      ],
      [
        "No.45 Rouge Tuxedo",
        "#b51f2e",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "SHUUEMURA",
    "productName": "Shu Uemura Hard Formula Eyebrow Pencil",
    "categoryCode": "CAT_EYEBROW",
    "price": 650000,
    "compareAtPrice": 760000,
    "sizeOptions": [
      "Seal Brown",
      "Stone Gray"
    ],
    "ingredients": [
      "Wax Blend",
      "Pigments"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eyebrow_definition",
      "long_wear"
    ],
    "finish": "natural",
    "coverage": "buildable",
    "country": "Japan",
    "type": "chì kẻ mày",
    "texture": "pencil",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "Seal Brown",
        "#5b4636",
        "neutral"
      ],
      [
        "Stone Gray",
        "#565555",
        "cool"
      ]
    ]
  },
  {
    "brandCode": "KISSME",
    "productName": "Kiss Me Heroine Make Long & Curl Mascara",
    "categoryCode": "CAT_MASCARA",
    "price": 295000,
    "compareAtPrice": 350000,
    "sizeOptions": [
      "Black",
      "Brown"
    ],
    "ingredients": [
      "Curl Lock Polymer",
      "Camellia Oil"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "eye_makeup",
      "long_wear"
    ],
    "finish": "defined",
    "coverage": "full",
    "country": "Japan",
    "type": "mascara chống lem",
    "texture": "mascara",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "Black",
        "#111111",
        "neutral"
      ],
      [
        "Brown",
        "#4b3324",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "SHISEIDO",
    "productName": "Shiseido Ultimune Power Infusing Concentrate",
    "categoryCode": "CAT_SERUM",
    "price": 2350000,
    "compareAtPrice": 2650000,
    "sizeOptions": [
      "50ml",
      "30ml"
    ],
    "ingredients": [
      "ImuGenerationRED Technology",
      "Reishi Mushroom Extract",
      "Roselle Extract"
    ],
    "skinTypes": [
      "all",
      "normal",
      "dry"
    ],
    "concerns": [
      "anti_aging",
      "hydration",
      "barrier_repair"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "Japan",
    "type": "serum cao cấp",
    "texture": "serum",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "SULWHASOO",
    "productName": "Sulwhasoo First Care Activating Serum",
    "categoryCode": "CAT_SERUM",
    "price": 1850000,
    "compareAtPrice": 2150000,
    "sizeOptions": [
      "60ml",
      "30ml"
    ],
    "ingredients": [
      "JAUM Activator",
      "Peony Extract",
      "Lotus Extract"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "anti_aging",
      "glow"
    ],
    "finish": "glow",
    "coverage": "none",
    "country": "Korea",
    "type": "serum first care",
    "texture": "serum",
    "sensitive": false,
    "fragrance": "herbal"
  },
  {
    "brandCode": "MEDIHEAL",
    "productName": "Mediheal N.M.F Aquaring Ampoule Mask EX",
    "categoryCode": "CAT_SHEET_MASK",
    "price": 35000,
    "compareAtPrice": 45000,
    "sizeOptions": [
      "1 miếng",
      "10 miếng"
    ],
    "ingredients": [
      "N.M.F",
      "Hyaluronic Acid",
      "Witch Hazel"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "combination"
    ],
    "concerns": [
      "hydration",
      "soothing"
    ],
    "finish": "dewy",
    "coverage": "none",
    "country": "Korea",
    "type": "mặt nạ giấy cấp ẩm",
    "texture": "sheet mask",
    "sensitive": true,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "DRJART",
    "productName": "Dr.Jart+ Cicapair Tiger Grass Color Correcting Treatment",
    "categoryCode": "CAT_COLOR_CORRECTOR",
    "price": 980000,
    "compareAtPrice": 1150000,
    "sizeOptions": [
      "50ml",
      "15ml"
    ],
    "ingredients": [
      "Centella Asiatica",
      "Chlorophyll",
      "Zinc Oxide"
    ],
    "skinTypes": [
      "sensitive",
      "normal",
      "combination"
    ],
    "concerns": [
      "redness",
      "sensitive_care",
      "makeup_base"
    ],
    "finish": "natural",
    "coverage": "light",
    "country": "Korea",
    "type": "kem hiệu chỉnh màu da",
    "texture": "cream",
    "sensitive": true,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "TORRIDEN",
    "productName": "Torriden Dive-In Low Molecular Hyaluronic Acid Serum",
    "categoryCode": "CAT_SERUM",
    "price": 385000,
    "compareAtPrice": 450000,
    "sizeOptions": [
      "50ml",
      "30ml"
    ],
    "ingredients": [
      "Low Molecular Hyaluronic Acid",
      "Panthenol",
      "Allantoin"
    ],
    "skinTypes": [
      "dry",
      "normal",
      "sensitive"
    ],
    "concerns": [
      "hydration",
      "soothing",
      "barrier_repair"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "Korea",
    "type": "serum cấp ẩm",
    "texture": "watery serum",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "ROUNDLAB",
    "productName": "Round Lab 1025 Dokdo Toner",
    "categoryCode": "CAT_TONER",
    "price": 330000,
    "compareAtPrice": 390000,
    "sizeOptions": [
      "200ml",
      "100ml"
    ],
    "ingredients": [
      "Deep Sea Water",
      "Panthenol",
      "Allantoin"
    ],
    "skinTypes": [
      "all",
      "sensitive"
    ],
    "concerns": [
      "hydration",
      "soothing",
      "sensitive_care"
    ],
    "finish": "fresh",
    "coverage": "none",
    "country": "Korea",
    "type": "toner cấp ẩm dịu nhẹ",
    "texture": "watery toner",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "NIVEA",
    "productName": "Nivea Sun Protect & Moisture SPF50+",
    "categoryCode": "CAT_BODY_SUNSCREEN",
    "price": 189000,
    "compareAtPrice": 240000,
    "sizeOptions": [
      "125ml",
      "75ml"
    ],
    "ingredients": [
      "UVA/UVB Filters",
      "Vitamin E",
      "Glycerin"
    ],
    "skinTypes": [
      "all"
    ],
    "concerns": [
      "sun_protection",
      "body_care"
    ],
    "finish": "natural",
    "coverage": "sheer",
    "country": "Germany",
    "type": "chống nắng body",
    "texture": "lotion",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "SENKA",
    "productName": "Senka Perfect Whip Cleansing Foam",
    "categoryCode": "CAT_CLEANSER",
    "price": 99000,
    "compareAtPrice": 135000,
    "sizeOptions": [
      "120g",
      "50g"
    ],
    "ingredients": [
      "Silk Cocoon Essence",
      "Hyaluronic Acid"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "cleansing",
      "oil_control"
    ],
    "finish": "clean",
    "coverage": "none",
    "country": "Japan",
    "type": "sữa rửa mặt tạo bọt",
    "texture": "foam",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "BIORE",
    "productName": "Bioré UV Aqua Rich Watery Essence SPF50+ PA++++",
    "categoryCode": "CAT_SUNSCREEN",
    "price": 215000,
    "compareAtPrice": 260000,
    "sizeOptions": [
      "70g",
      "50g"
    ],
    "ingredients": [
      "Hyaluronic Acid",
      "Royal Jelly Extract",
      "UV Filters"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "sun_protection",
      "freshness"
    ],
    "finish": "fresh",
    "coverage": "sheer",
    "country": "Japan",
    "type": "kem chống nắng dạng essence",
    "texture": "watery essence",
    "sensitive": false,
    "fragrance": "light_fragrance"
  },
  {
    "brandCode": "SIMPLE",
    "productName": "Simple Kind to Skin Hydrating Light Moisturiser",
    "categoryCode": "CAT_MOISTURIZER",
    "price": 165000,
    "compareAtPrice": 210000,
    "sizeOptions": [
      "125ml",
      "50ml"
    ],
    "ingredients": [
      "Vitamin B5",
      "Vitamin E",
      "Glycerin"
    ],
    "skinTypes": [
      "normal",
      "sensitive",
      "combination"
    ],
    "concerns": [
      "hydration",
      "sensitive_care"
    ],
    "finish": "natural",
    "coverage": "none",
    "country": "United Kingdom",
    "type": "kem dưỡng nhẹ",
    "texture": "light lotion",
    "sensitive": true,
    "fragrance": "fragrance_free"
  },
  {
    "brandCode": "THESAEM",
    "productName": "The Saem Cover Perfection Tip Concealer",
    "categoryCode": "CAT_CONCEALER",
    "price": 105000,
    "compareAtPrice": 145000,
    "sizeOptions": [
      "01 Clear Beige",
      "1.5 Natural Beige"
    ],
    "ingredients": [
      "Aloe Vera Extract",
      "Oxygen Complex"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "makeup_base",
      "dark_circle"
    ],
    "finish": "semi_matte",
    "coverage": "high",
    "country": "Korea",
    "type": "kem che khuyết điểm tip",
    "texture": "liquid concealer",
    "sensitive": false,
    "fragrance": "light_fragrance",
    "shades": [
      [
        "01 Clear Beige",
        "#eed0b8",
        "cool"
      ],
      [
        "1.5 Natural Beige",
        "#e3b98f",
        "neutral"
      ]
    ]
  },
  {
    "brandCode": "CANMAKE",
    "productName": "Canmake Marshmallow Finish Powder",
    "categoryCode": "CAT_POWDER",
    "price": 285000,
    "compareAtPrice": 340000,
    "sizeOptions": [
      "MO Matte Ochre",
      "MB Matte Beige Ochre"
    ],
    "ingredients": [
      "Silica",
      "Mineral Powder"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "oily"
    ],
    "concerns": [
      "oil_control",
      "makeup_set"
    ],
    "finish": "matte",
    "coverage": "light",
    "country": "Japan",
    "type": "phấn phủ nén",
    "texture": "pressed powder",
    "sensitive": false,
    "fragrance": "fragrance_free",
    "shades": [
      [
        "MO Matte Ochre",
        "#e9c4a4",
        "neutral"
      ],
      [
        "MB Matte Beige Ochre",
        "#dcb28d",
        "warm"
      ]
    ]
  },
  {
    "brandCode": "ELF",
    "productName": "e.l.f. Power Grip Primer",
    "categoryCode": "CAT_PRIMER",
    "price": 330000,
    "compareAtPrice": 390000,
    "sizeOptions": [
      "24ml",
      "15ml"
    ],
    "ingredients": [
      "Niacinamide",
      "Hyaluronic Acid"
    ],
    "skinTypes": [
      "normal",
      "combination",
      "dry"
    ],
    "concerns": [
      "makeup_base",
      "hydration",
      "long_wear"
    ],
    "finish": "dewy",
    "coverage": "none",
    "country": "United States",
    "type": "kem lót giữ nền",
    "texture": "gel primer",
    "sensitive": false,
    "fragrance": "fragrance_free"
  }
]
const brandIdByCode = new Map(brandCatalog.map((brand, index) => [brand.brandCode, objectIdFromNumber(1001 + index)]));
const categoryIdByCode = new Map(categoryCatalog.map((category, index) => [category.categoryCode, objectIdFromNumber(2001 + index)]));
const productIdByIndex = new Map(productCatalog.map((product, index) => [index, objectIdFromNumber(3001 + index)]));

function placeholderProductImage(productIndex, variant = 'main') {
  return `${IMAGE_BASE_URL}/${String(productIndex + 1).padStart(3, '0')}-${variant}.jpg`;
}

function placeholderBrandLogo(brandCode) {
  return `${LOGO_BASE_URL}/${brandCode.toLowerCase()}.png`;
}

function parseMetric(optionValue) {
  const text = String(optionValue).toLowerCase();
  const mlMatch = text.match(/(\d+(?:\.\d+)?)\s*ml/);
  if (mlMatch) return { volumeMl: Number(mlMatch[1]), weightGrams: 0 };
  const gramMatch = text.match(/(\d+(?:\.\d+)?)\s*(g|gram)/);
  if (gramMatch) return { volumeMl: 0, weightGrams: Number(gramMatch[1]) };
  const pieceMatch = text.match(/(\d+)\s*(miếng|mieng|pcs|pieces)/);
  if (pieceMatch) return { volumeMl: 0, weightGrams: Number(pieceMatch[1]) * 25 };
  return { volumeMl: 0, weightGrams: 0 };
}

function isShadeOption(product) {
  return Array.isArray(product.shades) && product.shades.length > 0;
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
  parentCategoryId: null,
  displayOrder: index + 1,
  categoryStatus: 'active',
  isActive: true,
}));

const productDocs = productCatalog.map((product, index) => {
  const brandId = brandIdByCode.get(product.brandCode);
  const categoryId = categoryIdByCode.get(product.categoryCode);
  if (!brandId) throw new Error(`Missing brand for product ${product.productName}: ${product.brandCode}`);
  if (!categoryId) throw new Error(`Missing category for product ${product.productName}: ${product.categoryCode}`);

  const shadeDocs = (product.shades || []).map(([shadeName, hex, undertone]) => ({
    // Product schema requires shades[].name.
    // Keep the old aliases too because Android/API mapping may still read shadeName or shade_name.
    name: shadeName,
    shade_name: shadeName,
    shadeName,
    shade_code: safeCode(shadeName),
    shadeCode: safeCode(shadeName),
    hex,
    undertone,
  }));
  const bought = 95 + (index * 17) % 460;
  const rating = Number((4.1 + ((index % 9) * 0.1)).toFixed(1));
  const category = categoryCatalog.find((item) => item.categoryCode === product.categoryCode);

  return {
    _id: productIdByIndex.get(index),
    productName: product.productName,
    productCode: `KNL-PRD-${String(index + 1).padStart(3, '0')}`,
    slug: `kanila-${slugify(product.productName)}`,
    brandId,
    categoryId,
    price: product.price,
    compareAtPrice: product.compareAtPrice,
    imageUrl: placeholderProductImage(index, 'main'),
    shortDescription: `${product.productName} - ${product.type}, thuộc nhóm ${category.categoryName}.`,
    longDescription: `Dữ liệu seed catalog Kanila cho sản phẩm thật ${product.productName}. Thông tin phục vụ listing, product detail, beauty profile matching và kiểm thử API; hãy đối chiếu nhãn chính thức khi public thương mại.`,
    stock: 120 + (index * 11) % 180,
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
    tone_match_supported: isShadeOption(product) ? ['fair', 'light', 'medium', 'tan'] : [],
    finish_type: product.finish || '',
    coverage_type: product.coverage || '',
    sales_count: bought,
    is_best_seller: index < 12,
    usageInstruction: isShadeOption(product)
      ? 'Chọn màu phù hợp với undertone/tone da; có thể thử AR hoặc xem swatch/review trước khi mua.'
      : 'Dùng theo routine cá nhân; với sản phẩm ban ngày, hoàn tất bằng chống nắng khi cần.',
  };
});

const productBeautyProfileDocs = productCatalog.map((product, index) => ({
  _id: objectIdFromNumber(4001 + index),
  product_id: productIdByIndex.get(index),
  suitable_skin_types: product.skinTypes,
  suitable_skin_concerns: product.concerns,
  suitable_sensitivity_levels: product.sensitive ? ['low', 'medium', 'high'] : ['low', 'medium'],
  suitable_skin_tones: isShadeOption(product) ? ['fair', 'light', 'medium', 'tan'] : [],
  suitable_undertones: isShadeOption(product) ? ['cool', 'neutral', 'warm'] : [],
  supported_beauty_goals: product.concerns.includes('sun_protection')
    ? ['daily_protection', 'prevent_dark_spot']
    : product.concerns.includes('lip_color') || product.concerns.includes('makeup_base')
      ? ['makeup_look', 'tone_evening']
      : ['hydration', 'barrier_repair', 'healthy_glow'],
  key_ingredients: product.ingredients,
  avoid_for_ingredients: product.fragrance === 'fragranced' ? ['fragrance_sensitive'] : [],
  texture: product.texture || '',
  finish: product.finish || '',
  fragrance_type: product.fragrance || 'no_preference',
  product_tags: [product.type, product.categoryCode.toLowerCase().replace('cat_', ''), product.brandCode.toLowerCase()],
  recommendation_boost_score: 70 + (index % 20),
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
    mediaUrl: placeholderProductImage(index, 'gallery-1'),
    altText: `${product.productName} ảnh phụ`,
    sortOrder: 2,
    isPrimary: false,
  },
]));

const productAttributeDocs = productCatalog.flatMap((product, index) => {
  const productId = productIdByIndex.get(index);
  const category = categoryCatalog.find((item) => item.categoryCode === product.categoryCode);
  const attributes = [
    ['Dung tích/Khối lượng', product.sizeOptions.join(' / ')],
    ['Xuất xứ thương hiệu', product.country],
    ['Loại sản phẩm', category.categoryName],
    ['Thành phần nổi bật', product.ingredients.join(', ')],
    ['Công dụng chính', product.concerns.join(', ')],
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
  optionName: isShadeOption(product) ? 'Màu sắc' : 'Dung tích/Quy cách',
  displayOrder: 1,
}));

const productOptionValueDocs = productCatalog.flatMap((product, index) => product.sizeOptions.map((optionValue, optionIndex) => ({
  _id: objectIdFromNumber(9001 + index * 2 + optionIndex),
  productOptionId: objectIdFromNumber(8001 + index),
  optionValue,
  displayOrder: optionIndex + 1,
})));

const productVariantDocs = productCatalog.flatMap((product, index) => product.sizeOptions.map((optionValue, optionIndex) => {
  const metric = parseMetric(optionValue);
  const variantPrice = optionIndex === 0 ? product.price : Math.max(25000, Math.round(product.price * 0.68 / 1000) * 1000);
  return {
    _id: objectIdFromNumber(10001 + index * 2 + optionIndex),
    productId: productIdByIndex.get(index),
    sku: `KNL-${safeCode(product.brandCode).slice(0, 8)}-${String(index + 1).padStart(3, '0')}-${String(optionIndex + 1).padStart(2, '0')}`,
    barcode: `8938${String(100000000 + index * 10 + optionIndex).slice(0, 9)}`,
    variantName: `${product.productName} - ${optionValue}`,
    variantStatus: 'active',
    weightGrams: metric.weightGrams,
    volumeMl: metric.volumeMl,
    costAmount: Math.round(variantPrice * 0.58 / 1000) * 1000,
  };
}));

const variantOptionValueDocs = productCatalog.flatMap((product, index) => product.sizeOptions.map((optionValue, optionIndex) => ({
  _id: objectIdFromNumber(11001 + index * 2 + optionIndex),
  variantId: objectIdFromNumber(10001 + index * 2 + optionIndex),
  productOptionValueId: objectIdFromNumber(9001 + index * 2 + optionIndex),
})));

const variantMediaDocs = productCatalog.flatMap((product, index) => product.sizeOptions.map((optionValue, optionIndex) => ({
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
  for (const item of seedPlan) {
    if (item.docs.length < 50) {
      throw new Error(`${item.label} has only ${item.docs.length} rows. Every product table must have at least 50 rows.`);
    }
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
  return model.bulkWrite(operations, { ordered: false, throwOnValidationError: true });
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
    console.log('Reset mode enabled: deleting deterministic Kanila product seed rows by _id...');
    await resetSeedRows();
  }

  for (const item of seedPlan) {
    const result = await upsertById(item.model, item.docs);
    console.log(`${item.label.padEnd(24)} => ${String(item.docs.length).padStart(3)} rows | upserted: ${result.upsertedCount || 0}, modified: ${result.modifiedCount || 0}, matched: ${result.matchedCount || 0}`);
  }

  console.log('Kanila product seed completed successfully.');
}

if (require.main === module) {
  runSeed()
    .catch((error) => {
      console.error('Kanila product seed failed:', error);
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