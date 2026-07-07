/**
 * KANILA_DATAPRODUCT_70_MAKEUP_RESET.js
 * Fresh makeup catalog seed for Kanila App.
 *
 * What this file does:
 * - Keeps existing brands; only inserts missing brand codes with $setOnInsert.
 * - Deletes and recreates categories with fixed taxonomy:
 *   6 parent categories + 27 subcategories exactly as requested.
 * - Deletes and recreates product catalog tables that reference categories/products.
 * - Seeds 70 real makeup products and tightly maps:
 *   Category -> Product -> ProductCategory -> ProductMedia -> ProductAttribute
 *   -> ProductOption -> ProductOptionValue -> ProductVariant
 *   -> VariantOptionValue -> VariantMedia -> ProductBeautyProfile.
 * - Adds ProductAttribute "Lượt đánh giá" for each product.
 * - Uses shade objects with required `name` to avoid Product validation error.
 *
 * Usage from backend root:
 *   node KANILA_DATAPRODUCT_70_MAKEUP_RESET.js
 *
 * Optional:
 *   node KANILA_DATAPRODUCT_70_MAKEUP_RESET.js --full-reset
 *   Also clears downstream data that may reference old products/variants.
 */

require('dotenv').config();
const path = require('path');
const mongoose = require('mongoose');

const IMAGE_BASE_URL = process.env.KANILA_IMAGE_BASE_URL || 'http://localhost:5000/kanila/products';
const LOGO_BASE_URL = process.env.KANILA_LOGO_BASE_URL || 'http://localhost:5000/kanila/brands';
const FULL_RESET = process.argv.includes('--full-reset');

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
    .slice(0, 36);
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
  ['MAYBELLINE', 'Maybelline New York'], ['LOREALPARIS', "L'Oréal Paris"], ['MAC', 'MAC Cosmetics'],
  ['NARS', 'NARS'], ['FENTYBEAUTY', 'Fenty Beauty'], ['CHARLOTTETILBURY', 'Charlotte Tilbury'],
  ['RAREBEAUTY', 'Rare Beauty'], ['ELF', 'e.l.f. Cosmetics'], ['NYX', 'NYX Professional Makeup'],
  ['TOOFACED', 'Too Faced'], ['BENEFIT', 'Benefit Cosmetics'], ['HUDABEAUTY', 'Huda Beauty'],
  ['URBANDECAY', 'Urban Decay'], ['DIOR', 'Dior Beauty'], ['CHANEL', 'CHANEL Beauty'],
  ['YSLBEAUTY', 'YSL Beauty'], ['ARMANIBEAUTY', 'Giorgio Armani Beauty'], ['ESTEELAUDER', 'Estée Lauder'],
  ['LANCOME', 'Lancôme'], ['SHISEIDO', 'Shiseido'], ['INNISFREE', 'Innisfree'], ['LANEIGE', 'Laneige'],
  ['ROMAND', 'rom&nd'], ['PERIPERA', 'Peripera'], ['ETUDE', 'Etude'], ['THREECE', '3CE'],
  ['CLIO', 'CLIO'], ['CANMAKE', 'Canmake'], ['KISSME', 'Kiss Me Heroine Make'], ['SHUUEMURA', 'Shu Uemura'],
  ['MAKEUPFOREVER', 'Make Up For Ever'], ['BOBBIBROWN', 'Bobbi Brown'], ['TARTE', 'Tarte'],
  ['MILKMAKEUP', 'Milk Makeup'], ['SAIE', 'Saie'], ['MERIT', 'MERIT'], ['GLOSSIER', 'Glossier'],
  ['TOWER28', 'Tower 28'], ['RHODE', 'rhode'], ['KYLIECOSMETICS', 'Kylie Cosmetics'],
  ['ANASTASIABEVERLYHILLS', 'Anastasia Beverly Hills'], ['MORPHE', 'Morphe'], ['HOURGLASS', 'Hourglass'],
  ['PATMCGRATHLABS', 'Pat McGrath Labs'], ['JUVIASPLACE', "Juvia's Place"], ['COLOURPOP', 'ColourPop'],
  ['KOSAS', 'Kosas'], ['ILIA', 'ILIA'], ['WESTMANATELIER', 'Westman Atelier'], ['LAURAMERCIER', 'Laura Mercier'],
];

const categoryTree = [
  { code: 'FACE', name: 'Face', children: [
    ['FOUNDATION', 'Foundation'], ['CONCEALER', 'Concealer'], ['PRIMER', 'Primer'], ['POWDER', 'Powder'],
    ['SETTINGSPRAY', 'Setting Spray'], ['BBCCCREAM', 'BB & CC Cream'], ['TINTEDMOISTURIZER', 'Tinted Moisturizer'],
  ]},
  { code: 'EYES', name: 'Eyes', children: [
    ['MASCARA', 'Mascara'], ['EYELINER', 'Eyeliner'], ['EYESHADOW', 'Eyeshadow'], ['EYEBROW', 'Eyebrow'], ['FALSELASHES', 'False Lashes'],
  ]},
  { code: 'LIPS', name: 'Lips', children: [
    ['LIPSTICK', 'Lipstick'], ['LIPGLOSS', 'Lip Gloss'], ['LIPBALM', 'Lip Balm'], ['LIPLINER', 'Lip Liner'], ['LIPSTAIN', 'Lip Stain'],
  ]},
  { code: 'CHEEKS', name: 'Cheeks', children: [
    ['BLUSH', 'Blush'], ['BRONZER', 'Bronzer'], ['HIGHLIGHTER', 'Highlighter'], ['CONTOUR', 'Contour'],
  ]},
  { code: 'GIFT', name: 'Gift', children: [
    ['EYESHADOWPALETTE', 'Eyeshadow Palette'], ['FACEPALETTE', 'Face Palette'], ['MAKEUPKIT', 'Makeup Kit'],
  ]},
  { code: 'MINITRAVEL', name: 'Mini & Travel', children: [
    ['MINIFOUNDATION', 'Mini Foundation'], ['MINILIPSTICK', 'Mini Lipstick'], ['TRIALKITS', 'Trial Kits'],
  ]},
];

function buildCategories() {
  const categories = [];
  let idx = 1;
  for (const parent of categoryTree) {
    const parentId = objectIdFromNumber(3000 + idx++);
    categories.push({
      _id: parentId,
      categoryName: parent.name,
      name: parent.name,
      categoryCode: parent.code,
      slug: slugify(parent.name),
      description: `Nhóm sản phẩm makeup ${parent.name} trong Kanila.`,
      parentCategoryId: null,
      displayOrder: idx,
      categoryStatus: 'active',
      isActive: true,
    });
    for (const [childCode, childName] of parent.children) {
      categories.push({
        _id: objectIdFromNumber(3000 + idx++),
        categoryName: childName,
        name: childName,
        categoryCode: childCode,
        slug: slugify(childName),
        description: `${childName} thuộc nhóm ${parent.name}.`,
        parentCategoryId: parentId,
        displayOrder: idx,
        categoryStatus: 'active',
        isActive: true,
      });
    }
  }
  return categories;
}

const productCatalog = [
  ['Maybelline Fit Me Matte + Poreless Foundation','MAYBELLINE','FOUNDATION',199000],
  ['L’Oréal Paris Infallible 24H Fresh Wear Foundation','LOREALPARIS','FOUNDATION',329000],
  ['Fenty Beauty Pro Filt’r Soft Matte Longwear Foundation','FENTYBEAUTY','FOUNDATION',950000],
  ['NARS Light Reflecting Foundation','NARS','FOUNDATION',1250000],
  ['Estée Lauder Double Wear Stay-in-Place Makeup','ESTEELAUDER','FOUNDATION',1350000],
  ['Armani Beauty Luminous Silk Foundation','ARMANIBEAUTY','FOUNDATION',1650000],
  ['Dior Forever Skin Glow Foundation','DIOR','FOUNDATION',1500000],
  ['NARS Radiant Creamy Concealer','NARS','CONCEALER',890000],
  ['Maybelline Instant Age Rewind Eraser Concealer','MAYBELLINE','CONCEALER',219000],
  ['Tarte Shape Tape Full Coverage Concealer','TARTE','CONCEALER',760000],
  ['e.l.f. Power Grip Primer','ELF','PRIMER',250000],
  ['Benefit The POREfessional Face Primer','BENEFIT','PRIMER',890000],
  ['Milk Makeup Hydro Grip Primer','MILKMAKEUP','PRIMER',980000],
  ['Laura Mercier Translucent Loose Setting Powder','LAURAMERCIER','POWDER',1050000],
  ['Charlotte Tilbury Airbrush Flawless Finish Powder','CHARLOTTETILBURY','POWDER',1250000],
  ['Urban Decay All Nighter Setting Spray','URBANDECAY','SETTINGSPRAY',890000],
  ['Charlotte Tilbury Airbrush Flawless Setting Spray','CHARLOTTETILBURY','SETTINGSPRAY',980000],
  ['IT Cosmetics CC+ Cream SPF 50+','LOREALPARIS','BBCCCREAM',1050000],
  ['Erborian CC Red Correct','LOREALPARIS','BBCCCREAM',820000],
  ['Rare Beauty Positive Light Tinted Moisturizer','RAREBEAUTY','TINTEDMOISTURIZER',820000],
  ['Saie Slip Tint Dewy Tinted Moisturizer SPF 35','SAIE','TINTEDMOISTURIZER',890000],

  ['Maybelline Lash Sensational Sky High Mascara','MAYBELLINE','MASCARA',249000],
  ['Kiss Me Heroine Make Long & Curl Mascara Advanced Film','KISSME','MASCARA',329000],
  ['Lancôme Lash Idôle Mascara','LANCOME','MASCARA',880000],
  ['NYX Epic Ink Waterproof Liquid Eyeliner','NYX','EYELINER',269000],
  ['Stila Stay All Day Waterproof Liquid Eye Liner','URBANDECAY','EYELINER',650000],
  ['Maybelline Hyper Easy Liquid Pen Eyeliner','MAYBELLINE','EYELINER',189000],
  ['Urban Decay Naked3 Eyeshadow Palette','URBANDECAY','EYESHADOW',1350000],
  ['ColourPop Super Shock Shadow','COLOURPOP','EYESHADOW',190000],
  ['3CE Multi Eye Color Palette Overtake','THREECE','EYESHADOW',850000],
  ['Anastasia Beverly Hills Brow Wiz','ANASTASIABEVERLYHILLS','EYEBROW',650000],
  ['Benefit Precisely, My Brow Pencil','BENEFIT','EYEBROW',720000],
  ['Ardell Demi Wispies False Lashes','MORPHE','FALSELASHES',160000],
  ['Huda Beauty Classic False Lashes Samantha #7','HUDABEAUTY','FALSELASHES',480000],

  ['MAC Matte Lipstick Ruby Woo','MAC','LIPSTICK',650000],
  ['Charlotte Tilbury Matte Revolution Pillow Talk','CHARLOTTETILBURY','LIPSTICK',950000],
  ['Dior Rouge Dior Lipstick 999','DIOR','LIPSTICK',1100000],
  ['YSL Rouge Pur Couture Lipstick','YSLBEAUTY','LIPSTICK',1050000],
  ['Fenty Beauty Gloss Bomb Universal Lip Luminizer','FENTYBEAUTY','LIPGLOSS',650000],
  ['NYX Butter Gloss','NYX','LIPGLOSS',180000],
  ['Dior Addict Lip Maximizer','DIOR','LIPGLOSS',980000],
  ['Laneige Lip Sleeping Mask Berry','LANEIGE','LIPBALM',520000],
  ['rhode Peptide Lip Treatment','RHODE','LIPBALM',620000],
  ['Kiehl’s Butterstick Lip Treatment','LANEIGE','LIPBALM',580000],
  ['MAC Lip Pencil Spice','MAC','LIPLINER',520000],
  ['Charlotte Tilbury Lip Cheat Pillow Talk','CHARLOTTETILBURY','LIPLINER',780000],
  ['rom&nd Juicy Lasting Tint','ROMAND','LIPSTAIN',250000],
  ['Peripera Ink Mood Glowy Tint','PERIPERA','LIPSTAIN',210000],
  ['Etude Dear Darling Water Gel Tint','ETUDE','LIPSTAIN',150000],

  ['Rare Beauty Soft Pinch Liquid Blush Happy','RAREBEAUTY','BLUSH',650000],
  ['NARS Blush Orgasm','NARS','BLUSH',890000],
  ['Canmake Cream Cheek','CANMAKE','BLUSH',220000],
  ['Benefit Hoola Matte Bronzer','BENEFIT','BRONZER',850000],
  ['Fenty Beauty Sun Stalk’r Instant Warmth Bronzer','FENTYBEAUTY','BRONZER',850000],
  ['Dior Backstage Glow Face Palette','DIOR','HIGHLIGHTER',1250000],
  ['Rare Beauty Positive Light Liquid Luminizer','RAREBEAUTY','HIGHLIGHTER',650000],
  ['Hourglass Ambient Lighting Powder','HOURGLASS','HIGHLIGHTER',1250000],
  ['Fenty Beauty Match Stix Matte Contour Skinstick','FENTYBEAUTY','CONTOUR',750000],
  ['Charlotte Tilbury Hollywood Contour Wand','CHARLOTTETILBURY','CONTOUR',980000],

  ['Huda Beauty Empowered Eyeshadow Palette','HUDABEAUTY','EYESHADOWPALETTE',1750000],
  ['Pat McGrath Labs Mothership Eyeshadow Palette','PATMCGRATHLABS','EYESHADOWPALETTE',3200000],
  ['Morphe 35O Supernatural Glow Artistry Palette','MORPHE','EYESHADOWPALETTE',750000],
  ['Hourglass Ambient Lighting Edit Unlocked Palette','HOURGLASS','FACEPALETTE',2100000],
  ['Make Up For Ever HD Skin Face Essentials Palette','MAKEUPFOREVER','FACEPALETTE',1800000],
  ['Sephora Favorites Makeup Must-Haves Kit','MAKEUPFOREVER','MAKEUPKIT',1200000],
  ['Charlotte Tilbury Pillow Talk Makeup Kit','CHARLOTTETILBURY','MAKEUPKIT',2200000],

  ['NARS Mini Light Reflecting Foundation','NARS','MINIFOUNDATION',520000],
  ['Fenty Beauty Mini Pro Filt’r Foundation','FENTYBEAUTY','MINIFOUNDATION',490000],
  ['MAC Mini Matte Lipstick Velvet Teddy','MAC','MINILIPSTICK',390000],
  ['Dior Rouge Dior Mini Lipstick 999','DIOR','MINILIPSTICK',520000],
  ['Rare Beauty Mini Soft Pinch Liquid Blush Trio','RAREBEAUTY','TRIALKITS',790000],
  ['Laneige Mini Lip Sleeping Mask Set','LANEIGE','TRIALKITS',620000],
  ['Benefit Mini Bestseller Trial Kit','BENEFIT','TRIALKITS',850000],
];

const subcategoryMeta = {
  FOUNDATION: { option: 'Shade', values: ['Light Neutral', 'Medium Warm'], finish: 'natural matte', coverage: 'medium to full', skin: ['normal','combination','oily'] },
  CONCEALER: { option: 'Shade', values: ['Fair', 'Medium'], finish: 'natural', coverage: 'full', skin: ['normal','combination'] },
  PRIMER: { option: 'Type', values: ['Hydrating', 'Pore Blurring'], finish: 'smooth', coverage: 'prep', skin: ['normal','oily','combination'] },
  POWDER: { option: 'Shade', values: ['Translucent', 'Medium'], finish: 'soft matte', coverage: 'setting', skin: ['normal','oily','combination'] },
  SETTINGSPRAY: { option: 'Finish', values: ['Natural', 'Dewy'], finish: 'long-wear', coverage: 'setting', skin: ['normal','dry','combination'] },
  BBCCCREAM: { option: 'Shade', values: ['Light', 'Medium'], finish: 'dewy', coverage: 'light to medium', skin: ['dry','normal','combination'] },
  TINTEDMOISTURIZER: { option: 'Shade', values: ['Light', 'Medium'], finish: 'skin tint', coverage: 'light', skin: ['dry','normal'] },
  MASCARA: { option: 'Color', values: ['Black', 'Brown Black'], finish: 'lengthening', coverage: 'lash volume', skin: ['all'] },
  EYELINER: { option: 'Color', values: ['Black', 'Brown'], finish: 'waterproof', coverage: 'defined line', skin: ['all'] },
  EYESHADOW: { option: 'Shade', values: ['Warm Nude', 'Rose Brown'], finish: 'shimmer matte', coverage: 'buildable', skin: ['all'] },
  EYEBROW: { option: 'Shade', values: ['Soft Brown', 'Dark Brown'], finish: 'natural brow', coverage: 'buildable', skin: ['all'] },
  FALSELASHES: { option: 'Style', values: ['Natural', 'Dramatic'], finish: 'flutter', coverage: 'lash strip', skin: ['all'] },
  LIPSTICK: { option: 'Shade', values: ['Rose Nude', 'Classic Red'], finish: 'matte satin', coverage: 'full', skin: ['all'] },
  LIPGLOSS: { option: 'Shade', values: ['Clear Pink', 'Rose Shimmer'], finish: 'glossy', coverage: 'sheer', skin: ['all'] },
  LIPBALM: { option: 'Scent', values: ['Berry', 'Vanilla'], finish: 'hydrating', coverage: 'care', skin: ['all'] },
  LIPLINER: { option: 'Shade', values: ['Nude Pink', 'Warm Brown'], finish: 'matte', coverage: 'precise line', skin: ['all'] },
  LIPSTAIN: { option: 'Shade', values: ['Coral', 'Berry Red'], finish: 'stain glow', coverage: 'buildable', skin: ['all'] },
  BLUSH: { option: 'Shade', values: ['Soft Pink', 'Peach Coral'], finish: 'radiant', coverage: 'buildable', skin: ['all'] },
  BRONZER: { option: 'Shade', values: ['Light Bronze', 'Warm Tan'], finish: 'sun-kissed', coverage: 'buildable', skin: ['all'] },
  HIGHLIGHTER: { option: 'Shade', values: ['Champagne', 'Rose Gold'], finish: 'glow', coverage: 'luminous', skin: ['all'] },
  CONTOUR: { option: 'Shade', values: ['Light Medium', 'Medium Deep'], finish: 'sculpted', coverage: 'buildable', skin: ['all'] },
  EYESHADOWPALETTE: { option: 'Palette Tone', values: ['Warm Neutral', 'Rose Glam'], finish: 'mixed', coverage: 'buildable', skin: ['all'] },
  FACEPALETTE: { option: 'Palette Tone', values: ['Light Medium', 'Medium Deep'], finish: 'multi-finish', coverage: 'buildable', skin: ['all'] },
  MAKEUPKIT: { option: 'Kit Type', values: ['Daily Look', 'Full Glam'], finish: 'complete set', coverage: 'varied', skin: ['all'] },
  MINIFOUNDATION: { option: 'Shade', values: ['Light Neutral', 'Medium Warm'], finish: 'travel base', coverage: 'medium', skin: ['normal','combination'] },
  MINILIPSTICK: { option: 'Shade', values: ['Nude Rose', 'Red'], finish: 'mini lip', coverage: 'full', skin: ['all'] },
  TRIALKITS: { option: 'Set Type', values: ['Discovery', 'Travel'], finish: 'trial set', coverage: 'varied', skin: ['all'] },
};

function shadeObjects(subCode) {
  const meta = subcategoryMeta[subCode] || subcategoryMeta.LIPSTICK;
  const palette = {
    'Light Neutral': ['Light Neutral', '#f2d6bf', 'neutral'], 'Medium Warm': ['Medium Warm', '#d7a072', 'warm'],
    Fair: ['Fair', '#f3d8c4', 'neutral'], Medium: ['Medium', '#c98d62', 'warm'], Translucent: ['Translucent', '#f4efe8', 'neutral'],
    Light: ['Light', '#efd1b8', 'neutral'], Black: ['Black', '#141414', 'neutral'], Brown: ['Brown', '#5a341f', 'warm'],
    'Brown Black': ['Brown Black', '#27201c', 'neutral'], 'Warm Nude': ['Warm Nude', '#b37758', 'warm'], 'Rose Brown': ['Rose Brown', '#9f5f62', 'neutral'],
    'Soft Brown': ['Soft Brown', '#7b4d32', 'warm'], 'Dark Brown': ['Dark Brown', '#3a2419', 'neutral'], Natural: ['Natural', '#f0d5c0', 'neutral'],
    Dramatic: ['Dramatic', '#1b1b1b', 'neutral'], 'Rose Nude': ['Rose Nude', '#bc6b72', 'neutral'], 'Classic Red': ['Classic Red', '#b20f2a', 'cool'],
    'Clear Pink': ['Clear Pink', '#f7cdd4', 'cool'], 'Rose Shimmer': ['Rose Shimmer', '#d98b9a', 'neutral'], Berry: ['Berry', '#a83b62', 'cool'],
    Vanilla: ['Vanilla', '#f1dfc8', 'warm'], 'Nude Pink': ['Nude Pink', '#c98686', 'neutral'], 'Warm Brown': ['Warm Brown', '#9c5f42', 'warm'],
    Coral: ['Coral', '#eb6d61', 'warm'], 'Berry Red': ['Berry Red', '#b51f45', 'cool'], 'Soft Pink': ['Soft Pink', '#eda0ad', 'cool'],
    'Peach Coral': ['Peach Coral', '#f08a70', 'warm'], 'Light Bronze': ['Light Bronze', '#bd8152', 'warm'], 'Warm Tan': ['Warm Tan', '#9d6639', 'warm'],
    Champagne: ['Champagne', '#ead1a3', 'warm'], 'Rose Gold': ['Rose Gold', '#d89a8f', 'neutral'], 'Light Medium': ['Light Medium', '#c78e68', 'neutral'],
    'Medium Deep': ['Medium Deep', '#8a5537', 'warm'], 'Warm Neutral': ['Warm Neutral', '#b87755', 'warm'], 'Rose Glam': ['Rose Glam', '#a85d71', 'cool'],
    'Daily Look': ['Daily Look', '#c78b7d', 'neutral'], 'Full Glam': ['Full Glam', '#8e3b3b', 'cool'], Discovery: ['Discovery', '#d8a48f', 'neutral'], Travel: ['Travel', '#b98972', 'warm'],
    Hydrating: ['Hydrating', '#f0dcca', 'neutral'], 'Pore Blurring': ['Pore Blurring', '#e7cab3', 'neutral'], Dewy: ['Dewy', '#eec6b6', 'neutral'],
  };
  return meta.values.map((v, idx) => {
    const p = palette[v] || [v, idx === 0 ? '#c98274' : '#8f4f42', 'neutral'];
    return {
      name: p[0],
      shadeName: p[0],
      shade_name: p[0],
      code: safeCode(p[0]),
      shadeCode: safeCode(p[0]),
      shade_code: safeCode(p[0]),
      hex: p[1],
      undertone: p[2],
    };
  });
}

function productText(productName, categoryName) {
  return `${productName} là sản phẩm ${categoryName.toLowerCase()} chính hãng, phù hợp cho routine makeup hằng ngày và các layout trang điểm chuyên nghiệp.`;
}

function buildSeedData(categoryByCode, brandByCode) {
  const products = [];
  const productBeautyProfiles = [];
  const productCategories = [];
  const productMedia = [];
  const productAttributes = [];
  const productOptions = [];
  const productOptionValues = [];
  const productVariants = [];
  const variantOptionValues = [];
  const variantMedia = [];

  productCatalog.forEach((row, i) => {
    const [productName, brandCode, subcategoryCode, price] = row;
    const idx = i + 1;
    const productId = objectIdFromNumber(4000 + idx);
    const category = categoryByCode[subcategoryCode];
    const brand = brandByCode[brandCode] || brandByCode.MAYBELLINE;
    if (!category) throw new Error(`Missing category ${subcategoryCode} for ${productName}`);
    if (!brand) throw new Error(`Missing brand ${brandCode} for ${productName}`);
    const meta = subcategoryMeta[subcategoryCode] || subcategoryMeta.LIPSTICK;
    const reviewCount = 86 + ((idx * 37) % 920);
    const avgRating = Number((4.1 + ((idx % 9) * 0.09)).toFixed(1));
    const bought = 120 + idx * 19;
    const sales = 80 + idx * 17;
    const slug = `kanila-${slugify(productName)}-${idx}`;
    const imageSlug = slugify(productName);
    const compareAtPrice = Math.round(price * 1.18 / 1000) * 1000;

    products.push({
      _id: productId,
      productName,
      productCode: `KNL-${safeCode(brandCode).slice(0, 10)}-${String(idx).padStart(3, '0')}`,
      slug,
      brandId: brand._id,
      categoryId: category._id,
      price,
      compareAtPrice,
      imageUrl: `${IMAGE_BASE_URL}/${imageSlug}/main.jpg`,
      shortDescription: productText(productName, category.categoryName),
      longDescription: `${productText(productName, category.categoryName)} Dữ liệu seed dùng cho Kanila Beauty Commerce App, có mapping chặt với category, variant, media, option và beauty profile.`,
      stock: 70 + idx * 4,
      bought,
      averageRating: avgRating,
      reviewCount,
      isActive: true,
      productStatus: 'active',
      ingredientText: 'Aqua, Dimethicone, Glycerin, Tocopherol, Iron Oxides, Mica, Silica.',
      shades: shadeObjects(subcategoryCode),
      skin_types_supported: meta.skin,
      concerns_targeted: ['long_wear', 'smooth_finish', 'photo_ready'],
      ingredient_flags: ['makeup', 'authentic', 'daily_use'],
      key_ingredients: ['Glycerin', 'Tocopherol', 'Mica'],
      is_sensitive_friendly: idx % 3 !== 0,
      tone_match_supported: ['fair', 'light', 'medium', 'tan'],
      finish_type: meta.finish,
      coverage_type: meta.coverage,
      sales_count: sales,
      is_best_seller: idx % 7 === 0 || idx <= 6,
      usageInstruction: 'Dùng sau bước dưỡng da. Tán đều bằng cọ, mút hoặc đầu ngón tay tùy loại sản phẩm.',
    });

    productBeautyProfiles.push({
      _id: objectIdFromNumber(5000 + idx),
      product_id: productId,
      suitable_skin_types: meta.skin,
      suitable_skin_concerns: ['tone_evening', 'long_wear', 'makeup_base'],
      suitable_sensitivity_levels: idx % 4 === 0 ? ['medium'] : ['low', 'medium'],
      suitable_skin_tones: ['fair', 'light', 'medium', 'tan'],
      suitable_undertones: ['cool', 'neutral', 'warm'],
      supported_beauty_goals: ['daily_makeup', 'natural_finish', 'photo_ready'],
      key_ingredients: ['Glycerin', 'Tocopherol', 'Mica'],
      avoid_for_ingredients: [],
      texture: meta.finish,
      finish: meta.finish,
      fragrance_type: idx % 5 === 0 ? 'light_fragrance' : 'no_preference',
      product_tags: [subcategoryCode.toLowerCase(), 'makeup', 'kanila_catalog'],
      recommendation_boost_score: 70 + (idx % 25),
      recommendation_penalty_score: idx % 6,
      is_active: true,
    });

    productCategories.push({
      _id: objectIdFromNumber(6000 + idx),
      productId,
      categoryId: category._id,
      isPrimary: true,
      sortOrder: idx,
    });

    for (let m = 0; m < 2; m++) {
      productMedia.push({
        _id: objectIdFromNumber(7000 + idx * 10 + m),
        productId,
        mediaType: 'image',
        mediaUrl: `${IMAGE_BASE_URL}/${imageSlug}/${m === 0 ? 'main' : 'swatch'}.jpg`,
        altText: `${productName} ${m === 0 ? 'main product image' : 'shade swatch image'}`,
        sortOrder: m + 1,
        isPrimary: m === 0,
      });
    }

    const attributes = [
      ['Thương hiệu', brand.brandName || brand.brandCode],
      ['Danh mục lớn', category.parentName || 'Makeup'],
      ['Phân loại', category.categoryName],
      ['Lượt đánh giá', String(reviewCount)],
      ['Đánh giá trung bình', String(avgRating)],
      ['Finish', meta.finish],
    ];
    attributes.forEach((attr, a) => {
      productAttributes.push({
        _id: objectIdFromNumber(8000 + idx * 10 + a),
        productId,
        attributeName: attr[0],
        attributeValue: attr[1],
        displayOrder: a + 1,
      });
    });

    const optionId = objectIdFromNumber(9000 + idx);
    productOptions.push({
      _id: optionId,
      productId,
      optionName: meta.option,
      displayOrder: 1,
    });

    const optionValueIds = [];
    meta.values.forEach((value, v) => {
      const valueId = objectIdFromNumber(10000 + idx * 10 + v);
      optionValueIds.push(valueId);
      productOptionValues.push({
        _id: valueId,
        productOptionId: optionId,
        optionValue: value,
        displayOrder: v + 1,
      });
    });

    meta.values.forEach((value, v) => {
      const variantId = objectIdFromNumber(11000 + idx * 10 + v);
      const sku = `KNL-${safeCode(brandCode).slice(0, 8)}-${safeCode(subcategoryCode).slice(0, 8)}-${String(idx).padStart(3, '0')}-${v + 1}`;
      productVariants.push({
        _id: variantId,
        productId,
        sku,
        barcode: `893${String(4000000000 + idx * 10 + v).padStart(10, '0')}`,
        variantName: `${productName} - ${value}`,
        variantStatus: 'active',
        weightGrams: subcategoryCode.includes('MINI') || subcategoryCode === 'TRIALKITS' ? 25 + v * 5 : 45 + v * 8,
        volumeMl: ['SETTINGSPRAY','BBCCCREAM','TINTEDMOISTURIZER','FOUNDATION','CONCEALER'].includes(subcategoryCode) ? 30 + v * 5 : 0,
        costAmount: Math.round(price * 0.55),
      });
      variantOptionValues.push({
        _id: objectIdFromNumber(12000 + idx * 10 + v),
        variantId,
        productOptionValueId: optionValueIds[v],
      });
      variantMedia.push({
        _id: objectIdFromNumber(13000 + idx * 10 + v),
        variantId,
        mediaUrl: `${IMAGE_BASE_URL}/${imageSlug}/variant-${v + 1}.jpg`,
        altText: `${productName} ${value}`,
        sortOrder: v + 1,
        isPrimary: v === 0,
      });
    });
  });

  return { products, productBeautyProfiles, productCategories, productMedia, productAttributes, productOptions, productOptionValues, productVariants, variantOptionValues, variantMedia };
}

async function deleteCollectionDocs(collectionName) {
  const exists = await mongoose.connection.db.listCollections({ name: collectionName }).hasNext();
  if (!exists) return 0;
  const result = await mongoose.connection.db.collection(collectionName).deleteMany({});
  return result.deletedCount || 0;
}

async function resetCatalogCollections() {
  const productCatalogCollections = [
    'variant_medias',
    'variant_option_values',
    'product_variants',
    'product_option_values',
    'product_options',
    'product_attributes',
    'product_media',
    'product_categories',
    'product_beauty_profiles',
    'products',
    'categories',
  ];
  const downstreamCollections = [
    'cart_items', 'carts', 'wishlist_items', 'wishlists', 'reviews', 'review_medias', 'review_summary', 'review_votes',
    'price_book_entries', 'price_histories', 'inventory_balances', 'inventory_transactions', 'stock_reservations',
    'order_items', 'orders', 'order_totals', 'order_addresses', 'order_status_history', 'shipments', 'shipment_items', 'shipment_events',
    'payment_intents', 'payment_transactions', 'returns', 'returnitems', 'refunds', 'checkout_sessions', 'checkout_addresses', 'checkout_shipping_methods',
    'customer_recommendation_snapshots', 'recommendation_logs',
  ];
  const collections = FULL_RESET ? downstreamCollections.concat(productCatalogCollections) : productCatalogCollections;
  console.log(`Reset mode: deleting ${FULL_RESET ? 'product catalog + downstream mapped business data' : 'product catalog/category data only'}...`);
  for (const collectionName of collections) {
    const deleted = await deleteCollectionDocs(collectionName);
    if (deleted > 0) console.log(`Deleted ${String(deleted).padStart(4, ' ')} docs from ${collectionName}`);
  }
}

async function upsertBrandsWithoutChangingExisting() {
  const ops = brandCatalog.map(([brandCode, brandName]) => ({
    updateOne: {
      filter: { brandCode },
      update: {
        $setOnInsert: {
          brandCode,
          brandName,
          description: `${brandName} makeup brand trong catalog Kanila.`,
          logoUrl: `${LOGO_BASE_URL}/${slugify(brandName)}.png`,
          brandStatus: 'active',
          isActive: true,
        },
      },
      upsert: true,
    },
  }));
  await Brand.bulkWrite(ops, { ordered: false });
  const docs = await Brand.find({ brandCode: { $in: brandCatalog.map(b => b[0]) } }).lean();
  return Object.fromEntries(docs.map(d => [d.brandCode, d]));
}

async function seedCategories(categories) {
  const parentById = Object.fromEntries(categories.map(c => [String(c._id), c]));
  const docs = categories.map(c => ({
    ...c,
    parentName: c.parentCategoryId ? parentById[String(c.parentCategoryId)]?.categoryName || '' : '',
  }));
  const ops = docs.map(doc => ({
    updateOne: {
      filter: { categoryCode: doc.categoryCode },
      update: { $set: doc },
      upsert: true,
    },
  }));
  await Category.collection.bulkWrite(ops, { ordered: false });
  return docs;
}

function upsertById(Model, docs) {
  if (!docs.length) return Promise.resolve({ upsertedCount: 0, modifiedCount: 0, matchedCount: 0 });
  return Model.bulkWrite(docs.map(doc => ({
    updateOne: {
      filter: { _id: doc._id },
      update: { $set: doc },
      upsert: true,
    },
  })), { ordered: false, throwOnValidationError: true });
}

async function runSeed() {
  const mongoUri = process.env.MONGODB_URI || process.env.MONGO_URI || process.env.MONGODB_URL || process.env.DATABASE_URL;
  if (!mongoUri) throw new Error('Missing MongoDB URI. Please set MONGO_URI or MONGODB_URI in .env');
  await mongoose.connect(mongoUri, process.env.MONGODB_DB_NAME ? { dbName: process.env.MONGODB_DB_NAME } : undefined);
  console.log(`Connected to MongoDB database: ${mongoose.connection.name}`);

  await resetCatalogCollections();
  const brandByCode = await upsertBrandsWithoutChangingExisting();
  const categories = buildCategories();
  const savedCategories = await seedCategories(categories);
  const categoryByCode = Object.fromEntries(savedCategories.map(c => [c.categoryCode, c]));
  const data = buildSeedData(categoryByCode, brandByCode);

  const steps = [
    ['Product', Product, data.products],
    ['ProductBeautyProfile', ProductBeautyProfile, data.productBeautyProfiles],
    ['ProductCategory', ProductCategory, data.productCategories],
    ['ProductMedia', ProductMedia, data.productMedia],
    ['ProductAttribute', ProductAttribute, data.productAttributes],
    ['ProductOption', ProductOption, data.productOptions],
    ['ProductOptionValue', ProductOptionValue, data.productOptionValues],
    ['ProductVariant', ProductVariant, data.productVariants],
    ['VariantOptionValue', VariantOptionValue, data.variantOptionValues],
    ['VariantMedia', VariantMedia, data.variantMedia],
  ];

  console.log(`Brand                    => preserved existing, ensured ${brandCatalog.length} brand codes`);
  console.log(`Category                 => ${String(savedCategories.length).padStart(3, ' ')} rows | fixed 6 parent + 27 subcategories`);
  for (const [name, Model, docs] of steps) {
    const result = await upsertById(Model, docs);
    console.log(`${name.padEnd(24, ' ')} => ${String(docs.length).padStart(3, ' ')} rows | upserted: ${result.upsertedCount || 0}, modified: ${result.modifiedCount || 0}, matched: ${result.matchedCount || 0}`);
  }

  console.log('Kanila 70-product makeup catalog seed completed successfully.');
  console.log('Next step: run dataremaining.js --reset after this, if you need business data mapped to this fresh catalog.');
}

runSeed()
  .catch(error => {
    console.error('Kanila 70-product makeup catalog seed failed:', error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await mongoose.disconnect().catch(() => {});
  });
