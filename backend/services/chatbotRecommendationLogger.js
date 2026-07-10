/**
 * chatbotRecommendationLogger.js
 * Stage 8 of the Advanced Recommendation Pipeline.
 *
 * Non-blocking async writes to the recommendation_logs collection.
 * Uses existing recommendation-log.model.js schema.
 *
 * Logs: question, detectedIntent, confidence, extractedContext,
 *       candidateCount, selectedProducts (id, name, score, breakdown), timestamp.
 *
 * Phase 9: Advanced Product Recommendation Engine
 */

"use strict";

const RecommendationLog = require("../models/recommendation-log.model");

const ALGORITHM_VERSION = "pipeline_v1";

/**
 * Log a chatbot recommendation session to recommendation_logs.
 * Fire-and-forget — never throws, never blocks the response.
 *
 * @param {object} params
 * @param {string}        params.sessionId          — chatbot session ID
 * @param {string|null}   params.customerId          — MongoDB ObjectId string or null
 * @param {string}        params.question            — raw user message
 * @param {string}        params.detectedIntent      — classified intent
 * @param {number}        params.confidence          — intent confidence score 0–100
 * @param {object}        params.extractedContext    — ShoppingContext object
 * @param {number}        params.candidateCount      — how many products were retrieved before scoring
 * @param {object[]}      params.selectedProducts    — final scored product cards (max 5)
 * @param {string}        params.source              — e.g. "product_recommendation", "makeup_recommendation"
 */
async function logRecommendation({
  sessionId,
  customerId,
  question,
  detectedIntent,
  confidence,
  extractedContext,
  candidateCount,
  selectedProducts = [],
  source = "chatbot_pipeline",
}) {
  try {
    if (!selectedProducts || selectedProducts.length === 0) return;

    // Write one log entry per returned product (matches existing schema structure)
    const docs = selectedProducts.map((product, index) => {
      const pid = product.id || product.product_id || product._id;
      if (!pid) return null;

      const score = product.recommendation?.score ?? product._score ?? 0;
      const breakdown = product.recommendation?.scoreBreakdown ?? product._scoreBreakdown ?? {};

      return {
        customer_id: customerId || null,
        product_id: pid,
        context: "chatbot",
        category_context: extractedContext?.categoryLabel || extractedContext?.productCategory || "",
        score,
        reason_codes: buildReasonCodes(extractedContext),
        reasons: [product.recommendation?.reason || product.reason || ""],
        badges: buildBadges(product, extractedContext),
        score_breakdown: {
          ...breakdown,
          // Extra chatbot-specific metadata
          intent: detectedIntent,
          confidence,
          occasion: extractedContext?.occasion || null,
          skin_type: extractedContext?.skinType || null,
          budget_max: extractedContext?.budget?.max || null,
        },
        rank_position: index + 1,
        session_id: sessionId || "",
        request_source: source,
        algorithm_version: ALGORITHM_VERSION,
        generated_at: new Date(),
      };
    }).filter(Boolean);

    if (docs.length === 0) return;

    // insertMany is non-atomic by default — ordered:false ignores individual failures
    await RecommendationLog.insertMany(docs, { ordered: false });
  } catch (err) {
    // Never propagate — logging failures must not affect the user response
    console.warn("[RecommendationLogger] Non-fatal logging error:", err.message);
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

function buildReasonCodes(context) {
  if (!context) return [];
  const codes = [];
  if (context.skinType) codes.push(`skin:${context.skinType}`);
  if (context.occasion) codes.push(`occasion:${context.occasion}`);
  if (context.budget?.max) codes.push("has_budget");
  if (context.finishPreference?.length) codes.push(`finish:${context.finishPreference[0]}`);
  if (context.wantsBestSeller) codes.push("wants_bestseller");
  if (context.wantsSale) codes.push("wants_sale");
  if (context.wantsLongWear) codes.push("wants_longwear");
  if (context.wantsWaterproof) codes.push("wants_waterproof");
  return codes;
}

function buildBadges(product, context) {
  const badges = [];
  const rating = product.rating || product.recommendation?.score;
  if (rating >= 4.5) badges.push("high_rated");
  if (product.pricing?.sale !== null && product.pricing?.compareAt) badges.push("on_sale");
  if (context?.wantsBestSeller) badges.push("bestseller");
  if (context?.wantsLongWear) badges.push("long_wear");
  return badges;
}

module.exports = { logRecommendation };
