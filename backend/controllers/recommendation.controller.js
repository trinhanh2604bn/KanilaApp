const validateObjectId = require("../utils/validateObjectId");
const RecommendationLog = require("../models/recommendation-log.model");
const {
  ALGORITHM_VERSION,
  getCustomerAndSkinProfile,
  getBehaviorSignals,
  recommendForProfile,
} = require("../services/recommendation.service");
const {
  getHomepageSnapshotProducts,
  getSkinProfileByCustomerId,
  SNAPSHOT_RECOMMENDATION_TYPE,
} = require("../services/recommendationSnapshot.service");

function resolveContext(req) {
  const c = String(req.query?.context || req.body?.context || "").trim().toLowerCase();
  if (["homepage", "profile_page", "category_page", "preview", "chatbot"].includes(c)) return c;
  return "unknown";
}

async function logFinalRecommendations({ customerId, items, context, categoryContext, req }) {
  if (!Array.isArray(items) || !items.length) return;
  const logs = items.map((it, idx) => ({
    customer_id: customerId || null,
    product_id: it.productId,
    context,
    category_context: categoryContext || "",
    score: Number(it.score || 0),
    reason_codes: Array.isArray(it.reason_codes) ? it.reason_codes : [],
    reasons: Array.isArray(it.reasons) ? it.reasons : [],
    badges: Array.isArray(it.badges) ? it.badges : [],
    score_breakdown: it.score_breakdown || {},
    rank_position: idx + 1,
    generated_at: new Date(),
    algorithm_version: it.algorithm_version || ALGORITHM_VERSION,
    session_id: String(req.headers["x-session-id"] || ""),
    request_source: String(req.headers["user-agent"] || ""),
  }));
  await RecommendationLog.insertMany(logs, { ordered: false });
}

// GET /api/recommendations/me
const getMyRecommendations = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId || !validateObjectId(accountId)) {
      return res.status(401).json({ success: false, message: "Invalid or missing account identity" });
    }
    const { customer, profile } = await getCustomerAndSkinProfile(accountId);
    if (!customer || !profile) {
      return res.status(404).json({ success: false, message: "Skin profile not found" });
    }
    const behavior = await getBehaviorSignals(customer._id);
    const category = req.query?.category || "";
    const limit = Number(req.query?.limit || 12);
    const context = resolveContext(req);
    const items = await recommendForProfile(profile, { category, limit, behavior });
    await logFinalRecommendations({
      customerId: customer._id,
      items,
      context,
      categoryContext: category,
      req,
    });

    const profileSource = profile._source || (profile.skin_type ? "CustomerBeautyProfile" : "CustomerPreference");

    const products = items.map((it) => ({
      product: it.product || { _id: it.productId },
      score: Number(it.score || 0),
      reasons: Array.isArray(it.reasons) ? it.reasons : [],
      reason_codes: Array.isArray(it.reason_codes) ? it.reason_codes : [],
      badges: Array.isArray(it.badges) ? it.badges : [],
      score_breakdown: it.score_breakdown || {},
    }));

    return res.status(200).json({
      success: true,
      message: "Get personalized recommendations successfully",
      data: {
        recommendation_type: category ? `category_${category}` : "skin_profile_homepage",
        profile_source: profileSource,
        from_snapshot: false,
        algorithm_version: ALGORITHM_VERSION,
        products,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

// POST /api/recommendations/preview
const previewRecommendations = async (req, res) => {
  try {
    const payload = req.body || {};
    const profile = {
      skin_types: Array.isArray(payload.skin_types) ? payload.skin_types : [],
      skin_tone: String(payload.skin_tone || ""),
      eye_color: String(payload.eye_color || ""),
      concerns: Array.isArray(payload.concerns) ? payload.concerns : [],
      ingredient_preferences: Array.isArray(payload.ingredient_preferences) ? payload.ingredient_preferences : [],
      favorite_brands: Array.isArray(payload.favorite_brands) ? payload.favorite_brands : [],
      routine_goal: String(payload.routine_goal || ""),
      price_range_preference: String(payload.price_range_preference || ""),
    };
    const category = req.query?.category || "";
    const limit = Number(req.query?.limit || 12);
    const context = "preview";
    const items = await recommendForProfile(profile, { category, limit });
    await logFinalRecommendations({
      customerId: null,
      items,
      context,
      categoryContext: category,
      req,
    });
    
    const products = items.map((it) => ({
      product: it.product || { _id: it.productId },
      score: Number(it.score || 0),
      reasons: Array.isArray(it.reasons) ? it.reasons : [],
      reason_codes: Array.isArray(it.reason_codes) ? it.reason_codes : [],
      badges: Array.isArray(it.badges) ? it.badges : [],
      score_breakdown: it.score_breakdown || {},
    }));

    return res.status(200).json({
      success: true,
      message: "Preview recommendations generated",
      data: {
        recommendation_type: "preview",
        profile_source: "manual",
        from_snapshot: false,
        algorithm_version: ALGORITHM_VERSION,
        products,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

// GET /api/recommendations/me/homepage
const getMyHomepageRecommendations = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId || !validateObjectId(accountId)) {
      return res.status(401).json({ success: false, message: "Invalid or missing account identity" });
    }

    const limit = Number(req.query?.limit || 20);
    const ttlHours = Number(req.query?.ttlHours || 48);
    const minValidCount = Number(req.query?.minValidCount || 8);

    const Customer = require("../models/customer.model");
    const CustomerRecommendationSnapshot = require("../models/customerRecommendationSnapshot.model");
    const cust = await Customer.findOne({ account_id: accountId }).select("_id").lean();

    let from_snapshot = false;
    let profile_source = "CustomerPreference";
    let snapshot_generated_at = null;

    if (cust) {
      const { computeProfileHash } = require("../services/recommendationSnapshot.service");
      const skinProfile = await getSkinProfileByCustomerId(cust._id);
      profile_source = skinProfile._source || "CustomerPreference";
      const profileHash = computeProfileHash(skinProfile);
      const now = new Date();
      const existingSnap = await CustomerRecommendationSnapshot.findOne({
        customer_id: cust._id,
        recommendation_type: SNAPSHOT_RECOMMENDATION_TYPE,
      }).lean();
      from_snapshot = !!(existingSnap &&
        existingSnap.profile_hash === profileHash &&
        !existingSnap.invalidated_at &&
        (!existingSnap.expires_at || existingSnap.expires_at > now));
      snapshot_generated_at = existingSnap?.generated_at || null;
    }

    const productsRaw = await getHomepageSnapshotProducts({
      accountId,
      limit,
      ttlHours,
      minValidCount,
      recommendationType: SNAPSHOT_RECOMMENDATION_TYPE,
    });

    const products = productsRaw.map(it => ({
      product: it.product || it,
      score: Number(it.score || 0),
      reasons: Array.isArray(it.reasons) ? it.reasons : [],
      reason_codes: Array.isArray(it.reason_codes) ? it.reason_codes : [],
      badges: Array.isArray(it.badges) ? it.badges : [],
      score_breakdown: it.score_breakdown || {},
    }));

    return res.status(200).json({
      success: true,
      message: "Get personalized homepage recommendations successfully",
      data: {
        recommendation_type: SNAPSHOT_RECOMMENDATION_TYPE,
        profile_source,
        from_snapshot,
        algorithm_version: ALGORITHM_VERSION,
        snapshot_generated_at,
        products,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

// GET /api/recommendations/me/all
const getMyAllRecommendations = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId || !validateObjectId(accountId)) {
      return res.status(401).json({ success: false, message: "Invalid or missing account identity" });
    }

    const limit = Number(req.query?.limit || 20);
    const productsRaw = await getHomepageSnapshotProducts({
      accountId,
      limit,
      ttlHours: Number(req.query?.ttlHours || 48),
      minValidCount: Number(req.query?.minValidCount || 8),
      recommendationType: SNAPSHOT_RECOMMENDATION_TYPE,
    });

    const products = productsRaw.map(it => ({
      product: it.product || it,
      score: Number(it.score || 0),
      reasons: Array.isArray(it.reasons) ? it.reasons : [],
      reason_codes: Array.isArray(it.reason_codes) ? it.reason_codes : [],
      badges: Array.isArray(it.badges) ? it.badges : [],
      score_breakdown: it.score_breakdown || {},
    }));

    return res.status(200).json({
      success: true,
      message: "Get personalized recommendations successfully",
      data: {
        recommendation_type: "all_recommendations",
        profile_source: "CustomerPreference",
        from_snapshot: true,
        algorithm_version: ALGORITHM_VERSION,
        products,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = {
  getMyRecommendations,
  previewRecommendations,
  getMyHomepageRecommendations,
  getMyAllRecommendations,
};
