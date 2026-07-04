const Product = require("../models/product.model");
const Customer = require("../models/customer.model");
const CustomerPreference = require("../models/customerPreference.model");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const ProductBeautyProfile = require("../models/productBeautyProfile.model");
const Wishlist = require("../models/wishlist.model");
const WishlistItem = require("../models/wishlistItem.model");
const Order = require("../models/order.model");
const OrderItem = require("../models/orderItem.model");
const ALGORITHM_VERSION = "rule_v2";

const WEIGHTS = {
  exactSkinType: 25,
  acceptableSkinType: 15,
  concernMatch: 12,
  ingredientPrefMatch: 8,
  goalMatch: 10,
  textureMatch: 5,
  finishMatch: 5,
  favoriteBrand: 10,
  ratingReview: 8,
  bestSeller: 6,
  priceMatch: 6,
  behaviorAffinity: 7,
  sensitiveMismatch: -25,
  hardSkinMismatch: -30,
  avoidIngredientConflict: -40,
  dislikedBrandPenalty: -30,
};

const toArray = (v) => {
  if (v == null) return [];
  if (Array.isArray(v)) return v.map((x) => String(x || "").trim()).filter(Boolean);
  const raw = String(v).trim();
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    if (Array.isArray(parsed)) return parsed.map((x) => String(x || "").trim()).filter(Boolean);
  } catch {}
  return raw.split(",").map((x) => x.trim()).filter(Boolean);
};

const toLowerSet = (arr) => new Set(toArray(arr).map((x) => x.toLowerCase()));

async function getCustomerAndSkinProfile(accountId) {
  const customer = await Customer.findOne({ account_id: accountId }).select("_id");
  if (!customer) return { customer: null, profile: null };

  // Try fetching the new CustomerBeautyProfile
  const beautyProfile = await CustomerBeautyProfile.findOne({ customer_id: customer._id }).lean();
  if (beautyProfile) {
    return {
      customer,
      profile: {
        is_new_profile: true,
        skin_types: beautyProfile.skin_type && beautyProfile.skin_type !== "unknown" ? [beautyProfile.skin_type] : [],
        skin_tone: beautyProfile.skin_tone,
        concerns: beautyProfile.skin_concerns || [],
        sensitivity_level: beautyProfile.sensitivity_level,
        beauty_goals: beautyProfile.beauty_goals || [],
        preferred_ingredients: beautyProfile.preferred_ingredients || [],
        avoid_ingredients: beautyProfile.avoid_ingredients || [],
        preferred_brands: beautyProfile.preferred_brands || [],
        disliked_brands: beautyProfile.disliked_brands || [],
        texture_preference: beautyProfile.texture_preference || [],
        finish_preference: beautyProfile.finish_preference || [],
        budget_range: beautyProfile.budget_range,
      },
    };
  }

  // Fallback to legacy CustomerPreference if new one doesn't exist
  const prefs = await CustomerPreference.find({
    customer_id: customer._id,
    preference_key: {
      $in: [
        "skin_type",
        "skin_tone",
        "eye_color",
        "concerns",
        "ingredient_preferences",
        "favorite_brands",
        "price_range_preference",
        "routine_goal",
      ],
    },
  }).lean();
  const map = new Map(prefs.map((p) => [p.preference_key, p.preference_value]));
  const profile = {
    is_new_profile: false,
    skin_types: toArray(map.get("skin_type")),
    skin_tone: String((toArray(map.get("skin_tone"))[0] || "")).trim(),
    concerns: toArray(map.get("concerns")),
    preferred_ingredients: toArray(map.get("ingredient_preferences")),
    avoid_ingredients: [],
    favorite_brands: toArray(map.get("favorite_brands")),
    budget_range: String((toArray(map.get("price_range_preference"))[0] || "")).trim(),
    beauty_goals: toArray(map.get("routine_goal")),
    texture_preference: [],
    finish_preference: [],
  };
  return { customer, profile };
}

async function getBehaviorSignals(customerId) {
  if (!customerId) return { orderedProductIds: new Set(), wishlistProductIds: new Set(), wishlistBrandIds: new Set() };
  const wishlists = await Wishlist.find({ customer_id: customerId }).select("_id");
  const wishlistIds = wishlists.map((w) => w._id);
  const [wishlistItems, orders] = await Promise.all([
    wishlistIds.length ? WishlistItem.find({ wishlistId: { $in: wishlistIds } }).select("productId").lean() : [],
    Order.find({ customer_id: customerId }).select("_id").sort({ placed_at: -1 }).limit(20).lean(),
  ]);
  const orderIds = orders.map((o) => o._id);
  const orderItems = orderIds.length ? await OrderItem.find({ order_id: { $in: orderIds } }).select("product_id").lean() : [];

  return {
    orderedProductIds: new Set(orderItems.map((x) => String(x.product_id))),
    wishlistProductIds: new Set(wishlistItems.map((x) => String(x.productId))),
    wishlistBrandIds: new Set(),
  };
}

function buildReason(text, reasons) {
  if (reasons.length < 3 && !reasons.includes(text)) reasons.push(text);
}

function scoreProduct(product, productProfile, profile, behavior) {
  let score = 0;
  const reasons = [];
  const caution_reasons = [];
  const reason_codes = [];
  const matched_attributes = [];
  const score_breakdown = {
    skin_type: 0,
    concerns: 0,
    goals: 0,
    ingredient_preferences: 0,
    brand_affinity: 0,
    popularity: 0,
    behavior: 0,
    business_boost: 0,
    diversity_penalty: 0,
    texture: 0,
    finish: 0,
  };

  if (product.productStatus === "inactive" || product.isActive === false) return { eligible: false, score: -999, reasons, caution_reasons, reason_codes, matched_attributes, score_breakdown };
  
  // Use productBeautyProfile if available, otherwise fallback to existing attributes on product
  const pSkinTypes = productProfile ? productProfile.suitable_skin_types : product.skin_types_supported;
  const pConcerns = productProfile ? productProfile.suitable_skin_concerns : product.concerns_targeted;
  const pIngredients = productProfile ? productProfile.key_ingredients : product.ingredient_flags;
  const pAvoidIngredients = productProfile ? productProfile.avoid_for_ingredients : [];
  const pGoals = productProfile ? productProfile.supported_beauty_goals : [];
  const pTexture = productProfile ? productProfile.texture : "";
  const pFinish = productProfile ? productProfile.finish : "";
  const brandName = String(product.brandId?.brandName || "").toLowerCase();
  const brandId = String(product.brandId?._id || "");

  // Skin Type Match
  const userSkin = toLowerSet(profile.skin_types);
  const productSkin = toLowerSet(pSkinTypes);
  if (userSkin.size && productSkin.size) {
    const overlap = [...userSkin].filter((x) => productSkin.has(x));
    if (overlap.length) {
      score += WEIGHTS.exactSkinType;
      score_breakdown.skin_type += WEIGHTS.exactSkinType;
      reason_codes.push("SKIN_TYPE_MATCH");
      buildReason(`Phù hợp với tình trạng ${profile.skin_types[0] || "da"} của bạn`, reasons);
      matched_attributes.push("skin_type");
    } else {
      score += WEIGHTS.hardSkinMismatch;
      score_breakdown.skin_type += WEIGHTS.hardSkinMismatch;
      caution_reasons.push("Không chuyên biệt cho loại da của bạn");
    }
  }

  // Concern Match
  const concerns = toLowerSet(profile.concerns);
  const targeted = toLowerSet(pConcerns);
  let concernMatches = 0;
  for (const c of concerns) if (targeted.has(c)) concernMatches += 1;
  if (concernMatches > 0) {
    const concernScore = concernMatches * WEIGHTS.concernMatch;
    score += concernScore;
    score_breakdown.concerns += concernScore;
    reason_codes.push("CONCERN_MATCH");
    buildReason(`Hỗ trợ cải thiện vấn đề da của bạn`, reasons);
    matched_attributes.push("skin_concern");
  }

  // Beauty Goal Match
  const goals = toLowerSet(profile.beauty_goals);
  const pSupportedGoals = toLowerSet(pGoals);
  let goalMatches = 0;
  for (const g of goals) if (pSupportedGoals.has(g)) goalMatches += 1;
  if (goalMatches > 0) {
    const goalScore = goalMatches * WEIGHTS.goalMatch;
    score += goalScore;
    score_breakdown.goals += goalScore;
    reason_codes.push("GOAL_MATCH");
    buildReason("Hỗ trợ mục tiêu làm đẹp của bạn", reasons);
    matched_attributes.push("beauty_goal");
  }

  // Preferred Ingredients Match
  const prefIngredients = toLowerSet(profile.preferred_ingredients);
  const productKeyIngredients = toLowerSet(pIngredients);
  let prefMatches = 0;
  for (const p of prefIngredients) if (productKeyIngredients.has(p)) prefMatches += 1;
  if (prefMatches > 0) {
    const ingredientScore = prefMatches * WEIGHTS.ingredientPrefMatch;
    score += ingredientScore;
    score_breakdown.ingredient_preferences += ingredientScore;
    reason_codes.push("PREF_INGREDIENT_MATCH");
    buildReason("Có thành phần bạn ưa thích", reasons);
    matched_attributes.push("preferred_ingredient");
  }

  // Avoid Ingredients Conflict
  const avoidIngredients = toLowerSet(profile.avoid_ingredients);
  let avoidConflict = false;
  for (const a of avoidIngredients) {
    if (productKeyIngredients.has(a)) {
      avoidConflict = true;
      break;
    }
  }
  if (avoidConflict) {
    score += WEIGHTS.avoidIngredientConflict;
    score_breakdown.ingredient_preferences += WEIGHTS.avoidIngredientConflict;
    reason_codes.push("AVOID_INGREDIENT_CONFLICT");
    caution_reasons.push("Có thành phần bạn muốn tránh");
  }

  // Brand Preference
  const prefBrands = new Set((profile.preferred_brands || []).map(b => String(b)));
  const legacyPrefBrands = toLowerSet(profile.favorite_brands || []);
  if (prefBrands.has(brandId) || legacyPrefBrands.has(brandName)) {
    score += WEIGHTS.favoriteBrand;
    score_breakdown.brand_affinity += WEIGHTS.favoriteBrand;
    reason_codes.push("FAVORITE_BRAND_MATCH");
    buildReason("Thương hiệu yêu thích của bạn", reasons);
    matched_attributes.push("preferred_brand");
  }

  const dislikedBrands = new Set((profile.disliked_brands || []).map(b => String(b)));
  if (dislikedBrands.has(brandId)) {
    score += WEIGHTS.dislikedBrandPenalty;
    score_breakdown.brand_affinity += WEIGHTS.dislikedBrandPenalty;
    reason_codes.push("DISLIKED_BRAND_PENALTY");
    caution_reasons.push("Thuộc thương hiệu bạn không ưu tiên");
  }

  // Texture and Finish
  const texturePrefs = toLowerSet(profile.texture_preference);
  if (pTexture && texturePrefs.has(pTexture.toLowerCase())) {
    score += WEIGHTS.textureMatch;
    score_breakdown.texture += WEIGHTS.textureMatch;
    matched_attributes.push("texture_preference");
  }
  const finishPrefs = toLowerSet(profile.finish_preference);
  if (pFinish && finishPrefs.has(pFinish.toLowerCase())) {
    score += WEIGHTS.finishMatch;
    score_breakdown.finish += WEIGHTS.finishMatch;
    matched_attributes.push("finish_preference");
  }

  // Ratings & Popularity
  const rating = Number(product.averageRating || 0);
  if (rating >= 4.2) {
    score += WEIGHTS.ratingReview;
    score_breakdown.popularity += WEIGHTS.ratingReview;
    reason_codes.push("HIGH_RATING");
  }
  if (product.is_best_seller || Number(product.sales_count || product.bought || 0) > 200) {
    score += WEIGHTS.bestSeller;
    score_breakdown.business_boost += WEIGHTS.bestSeller;
    reason_codes.push("BEST_SELLER_BOOST");
    buildReason("Sản phẩm bán chạy", reasons);
  }

  // Behavior Matches
  if (behavior.wishlistProductIds.has(String(product._id))) {
    score += WEIGHTS.behaviorAffinity;
    score_breakdown.behavior += WEIGHTS.behaviorAffinity;
    reason_codes.push("BEHAVIOR_WISHLIST_MATCH");
  }
  if (behavior.orderedProductIds.has(String(product._id))) {
    score += WEIGHTS.behaviorAffinity;
    score_breakdown.behavior += WEIGHTS.behaviorAffinity;
    reason_codes.push("BEHAVIOR_ORDER_HISTORY_MATCH");
  }

  // Sensitivity Check
  const sensitivity = profile.sensitivity_level;
  if (sensitivity === "high" || sensitivity === "reactive") {
    const isSensitiveFriendly = productProfile ? (productProfile.suitable_sensitivity_levels || []).includes(sensitivity) : product.is_sensitive_friendly;
    if (!isSensitiveFriendly) {
      score += WEIGHTS.sensitiveMismatch;
      score_breakdown.skin_type += WEIGHTS.sensitiveMismatch;
      caution_reasons.push("Có thể không phù hợp với mức độ nhạy cảm của da bạn");
    }
  }

  const badges = [];
  if (score >= 25 && caution_reasons.length === 0) badges.push("Phù hợp với bạn");
  if (product.is_best_seller || Number(product.sales_count || product.bought || 0) > 200) badges.push("Best Seller");

  return { eligible: true, score, reasons, caution_reasons, reason_codes: [...new Set(reason_codes)], matched_attributes: [...new Set(matched_attributes)], badges, score_breakdown };
}

function diversify(sorted, max = 12) {
  const out = [];
  const brandCap = new Map();
  const categoryCap = new Map();
  for (const item of sorted) {
    if (out.length >= max) break;
    const brand = String(item.product.brandId?._id || "");
    const category = String(item.product.categoryId?._id || "");
    const bCount = brandCap.get(brand) || 0;
    const cCount = categoryCap.get(category) || 0;
    if (bCount >= 3 || cCount >= 4) {
      item.score += -4;
      item.score_breakdown = item.score_breakdown || {};
      item.score_breakdown.diversity_penalty = (item.score_breakdown.diversity_penalty || 0) - 4;
      item.reason_codes = [...new Set([...(item.reason_codes || []), "DIVERSITY_PENALTY"])];
      continue;
    }
    out.push(item);
    brandCap.set(brand, bCount + 1);
    categoryCap.set(category, cCount + 1);
  }
  return out;
}

async function recommendForProfile(profile, opts = {}) {
  const category = String(opts.category || "").trim().toLowerCase();
  
  let products = await Product.find({
    productStatus: { $ne: "inactive" },
    isActive: { $ne: false },
  }).populate("brandId", "brandName").populate("categoryId", "categoryName").lean();
  
  if (category) {
    products = products.filter((p) =>
      String(p.categoryId?.categoryName || "").toLowerCase().includes(category) ||
      String(p.productName || "").toLowerCase().includes(category) ||
      String(p.shortDescription || "").toLowerCase().includes(category)
    );
  }

  // Load product beauty profiles
  const productIds = products.map(p => p._id);
  const pProfiles = await ProductBeautyProfile.find({ product_id: { $in: productIds }, is_active: true }).lean();
  const pProfileMap = new Map(pProfiles.map(p => [String(p.product_id), p]));

  const behavior = opts.behavior || { orderedProductIds: new Set(), wishlistProductIds: new Set(), wishlistBrandIds: new Set() };

  const scored = products
    .map((p) => {
      const pProfile = pProfileMap.get(String(p._id));
      const result = scoreProduct(p, pProfile, profile, behavior);
      return { product: p, ...result };
    })
    .filter((x) => x.eligible)
    .sort((a, b) => b.score - a.score);

  const diversified = diversify(scored, Number(opts.limit || 12));
  return diversified.map((x) => ({
    productId: String(x.product._id),
    score: x.score,
    reasons: x.reasons.slice(0, 3),
    caution_reasons: x.caution_reasons || [],
    matched_attributes: x.matched_attributes || [],
    reason_codes: x.reason_codes || [],
    badges: x.badges || [],
    score_breakdown: x.score_breakdown || {},
    algorithm_version: ALGORITHM_VERSION,
    product: {
      _id: String(x.product._id),
      productName: x.product.productName,
      name: x.product.productName,
      slug: x.product.slug || String(x.product._id),
      imageUrl: x.product.imageUrl || "",
      image: x.product.imageUrl || "",
      price: Number(x.product.price || 0),
      averageRating: Number(x.product.averageRating || 0),
      rating: Number(x.product.averageRating || 0),
      bought: Number(x.product.bought || 0),
      brandName: x.product.brandId?.brandName || "",
      brand: x.product.brandId?.brandName || "",
    },
  }));
}

module.exports = {
  WEIGHTS,
  ALGORITHM_VERSION,
  getCustomerAndSkinProfile,
  getBehaviorSignals,
  recommendForProfile,
};
