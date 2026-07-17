/**
 * gemini.provider.js
 * Handles all communication with the Gemini API via @google/genai SDK.
 *
 * SECURITY REVIEW REQUIRED before commit/deploy:
 * - Remove TEMP_DEV_GEMINI_API_KEY and rely solely on process.env.GEMINI_API_KEY.
 */
const { getVertexClient } = require("../config/vertex.config");
const {
  KANILA_SYSTEM_PROMPT,
  buildProductContextMessage,
  buildMissingInfoMessage,
  buildOrderContextMessage,
  buildTicketContextMessage,
  buildCartRecommendationMessage,
  buildAddToCartMessage,
  buildCartSummaryMessage,
  buildBeautyConsultationMessage,
  buildComboRecommendationMessage,
  buildProductComparisonMessage,
  buildIngredientMessage,
  buildMakeupProductContextMessage,
  buildSkinAnalysisPrompt,
} = require("./chatbot.prompt");

const GEMINI_TIMEOUT_MS = 60000;
/**
 * Internal: run a Gemini chat call with timeout protection.
 */
async function _geminiChatWithTimeout(message, history = [], customConfig = {}) {
  const geminiPromise = (async () => {
    const { client, model } = getVertexClient();
    const config = {
      systemInstruction: KANILA_SYSTEM_PROMPT,
      ...customConfig
    };
    const chat = client.chats.create({
      model: model,
      config,
      history,
    });
    const result = await chat.sendMessage({ message });
    return result.text;
  })();

  const timeoutPromise = new Promise((_, reject) => {
    setTimeout(() => {
      const err = new Error("Gemini API request timed out.");
      err.code = "CHATBOT_TIMEOUT";
      reject(err);
    }, GEMINI_TIMEOUT_MS);
  });

  try {
    return await Promise.race([geminiPromise, timeoutPromise]);
  } catch (err) {
    if (err.code === "CHATBOT_CONFIG_ERROR" || err.code === "CHATBOT_TIMEOUT") throw err;
    console.error("[Gemini] Provider error:", err.message);
    const wrapped = new Error("Gemini provider encountered an error.");
    wrapped.code = "CHATBOT_ERROR";
    throw wrapped;
  }
}

/**
 * Internal: run a standalone Gemini generation without Chatbot system prompt.
 */
async function _geminiGenerateTextWithTimeout(prompt) {
  const geminiPromise = (async () => {
    const { client, model } = getVertexClient();
    const response = await client.models.generateContent({
      model: model,
      contents: prompt
    });
    return response.text;
  })();

  const timeoutPromise = new Promise((_, reject) => {
    setTimeout(() => {
      const err = new Error("Gemini API request timed out.");
      err.code = "CHATBOT_TIMEOUT";
      reject(err);
    }, GEMINI_TIMEOUT_MS);
  });

  try {
    return await Promise.race([geminiPromise, timeoutPromise]);
  } catch (err) {
    throw err;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Public functions
// ─────────────────────────────────────────────────────────────────────────────

/** General chat reply — Phase 1. */
async function generateChatReply(userMessage, history = []) {
  return _geminiChatWithTimeout(userMessage, history);
}

/**
 * Phase 2A: product recommendation explanation (no customer context).
 * Kept for backward compatibility — Phase 4A uses generatePersonalizedProductExplanation().
 */
async function generateProductExplanation(products, userMessage, history = []) {
  const messageWithContext = buildProductContextMessage(products, userMessage, null);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Phase 4A: personalized product explanation — includes CUSTOMER_CONTEXT.
 * @param {object[]} products
 * @param {string} userMessage
 * @param {object|null} customerProfile — from getCustomerContext().customer_profile
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generatePersonalizedProductExplanation(products, userMessage, customerProfile, history = []) {
  const messageWithContext = buildProductContextMessage(products, userMessage, customerProfile);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Phase 4A: ask one progressive question when customer profile is incomplete.
 * @param {string} missingField — "skin_type" | "skin_concerns" | "budget"
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateMissingInfoQuestion(missingField, userMessage, history = []) {
  const messageWithContext = buildMissingInfoMessage(missingField, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/** Phase 3A: order tracking explanation. */
async function generateOrderExplanation(order, userMessage, history = []) {
  const messageWithContext = buildOrderContextMessage(order, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/** Phase 3A: support ticket confirmation. */
async function generateTicketConfirmation(ticket, userMessage, history = []) {
  const messageWithContext = buildTicketContextMessage(ticket, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

module.exports = {
  generateChatReply,
  generateProductExplanation,            // Phase 2A — kept for compat
  generatePersonalizedProductExplanation, // Phase 4A
  generateMissingInfoQuestion,            // Phase 4A
  generateOrderExplanation,              // Phase 3A
  generateTicketConfirmation,            // Phase 3A
  generateCartExplanation,               // Phase 5A
  generateCartActionConfirmation,        // Phase 5A
  generateCartSummaryReply,              // Phase 5A
  generateBeautyConsultationReply,       // Phase 5 shopping assistant
  generateComboExplanation,              // Phase 5 shopping assistant
};

// ─────────────────────────────────────────────────────────────────────────────
// Phase 5A cart functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 5A: Explain a cart combo recommendation to the user.
 * Gemini only writes the explanation — products and prices come from backend.
 */
async function generateCartExplanation(products, upsellProducts, customerProfile, userMessage, history = []) {
  const messageWithContext = buildCartRecommendationMessage(products, upsellProducts, customerProfile, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Phase 5A: Confirm that products were added to cart.
 */
async function generateCartActionConfirmation(addResult, userMessage, history = []) {
  const messageWithContext = buildAddToCartMessage(addResult, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Phase 5A: Reply about current cart contents.
 */
async function generateCartSummaryReply(summary, userMessage, history = []) {
  const messageWithContext = buildCartSummaryMessage(summary, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 5 Shopping Assistant functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 5: General beauty advisory reply.
 * Gemini explains why the backend-selected products suit the user's skin profile.
 * Gemini does NOT select products — backend already did via getRecommendationContext().
 *
 * @param {object[]} products       — formatted products
 * @param {object|null} customerProfile
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateBeautyConsultationReply(products, customerProfile, userMessage, history = []) {
  const messageWithContext = buildBeautyConsultationMessage(products, customerProfile, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Phase 5: Explain a slot-based combo recommendation.
 * Gemini explains the combo rationale; backend already selected and priced the combo.
 *
 * @param {object[]} combo       — slot-annotated products
 * @param {number} total         — backend-calculated total
 * @param {object|null} customerProfile
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateComboExplanation(combo, total, customerProfile, userMessage, history = []) {
  const messageWithContext = buildComboRecommendationMessage(combo, total, customerProfile, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 6A Product Comparison functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 6A: Generate a product comparison reply based on structured differences.
 */
async function generateProductComparisonReply(userMessage, comparison, customerProfile, history = []) {
  const messageWithContext = buildProductComparisonMessage(userMessage, comparison, customerProfile);
  return _geminiChatWithTimeout(messageWithContext, history);
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 7A Ingredient Intelligence functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 7A: Generate an ingredient intelligence reply.
 */
async function generateIngredientReply(userMessage, contextObj, history = []) {
  const messageWithContext = buildIngredientMessage(userMessage, contextObj);
  return _geminiChatWithTimeout(messageWithContext, history);
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 8: Makeup Commerce reply
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 8: Generate a makeup-focused sales assistant reply.
 * Context is pre-built by buildMakeupProductContextMessage in the handler.
 * Accepts the fully-formed context string as `contextMessage`.
 *
 * @param {string} contextMessage — output of buildMakeupProductContextMessage()
 * @param {Array}  history        — Gemini chat history
 * @returns {Promise<string>}
 */
async function generateMakeupReply(contextMessage, history = []) {
  return _geminiChatWithTimeout(contextMessage, history);
}

/**
 * Generate Skin Analysis for CustomerBeautyProfile
 */
async function generateSkinAnalysis(profile, products = []) {
  const messageWithContext = buildSkinAnalysisPrompt(profile, products);
  try {
    const jsonStr = await _geminiGenerateTextWithTimeout(messageWithContext);
    console.log("Raw Gemini Output:", jsonStr);
    const match = jsonStr.match(/\{[\s\S]*\}/);
    if (!match) throw new Error("No JSON object found in Gemini response");
    return JSON.parse(match[0]);
  } catch (error) {
    console.error("[Gemini] generateSkinAnalysis error:", error.message);
    return null;
  }
}

module.exports = {
  generateChatReply,
  generatePersonalizedProductExplanation,
  generateProductExplanation,
  generateMissingInfoQuestion,
  generateOrderExplanation,
  generateTicketConfirmation,
  generateCartExplanation,
  generateCartActionConfirmation,
  generateCartSummaryReply,
  generateBeautyConsultationReply,
  generateComboExplanation,
  generateProductComparisonReply,
  generateIngredientReply,
  // Phase 8: Makeup Commerce
  generateMakeupReply,
  generateSkinAnalysis,
};
