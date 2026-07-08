/**
 * gemini.provider.js
 * Handles all communication with the Gemini API via @google/genai SDK.
 *
 * SECURITY REVIEW REQUIRED before commit/deploy:
 * - Remove TEMP_DEV_GEMINI_API_KEY and rely solely on process.env.GEMINI_API_KEY.
 */

const { GoogleGenAI } = require("@google/genai");
const {
  KANILA_SYSTEM_PROMPT,
  buildProductContextMessage,
  buildMissingInfoMessage,
  buildOrderContextMessage,
  buildTicketContextMessage,
} = require("./chatbot.prompt");

// ─────────────────────────────────────────────────────────────────────────────
// TEMP DEV ONLY - remove before commit/deploy
const TEMP_DEV_GEMINI_API_KEY = "PASTE_YOUR_GEMINI_KEY_HERE"; // TEMP DEV ONLY
// ─────────────────────────────────────────────────────────────────────────────

const geminiApiKey = process.env.GEMINI_API_KEY || TEMP_DEV_GEMINI_API_KEY;
const GEMINI_MODEL = process.env.GEMINI_MODEL || "gemini-2.5-flash";
const GEMINI_TIMEOUT_MS = 10000;

let _genAI = null;
function getGenAI() {
  if (!geminiApiKey || geminiApiKey === "PASTE_YOUR_GEMINI_KEY_HERE") {
    const err = new Error(
      "Gemini API key is missing or not configured. Set GEMINI_API_KEY in .env."
    );
    err.code = "CHATBOT_CONFIG_ERROR";
    throw err;
  }
  if (!_genAI) _genAI = new GoogleGenAI({ apiKey: geminiApiKey });
  return _genAI;
}

/**
 * Internal: run a Gemini chat call with timeout protection.
 */
async function _geminiChatWithTimeout(message, history = []) {
  const genAI = getGenAI();

  const geminiPromise = (async () => {
    const chat = genAI.chats.create({
      model: GEMINI_MODEL,
      config: { systemInstruction: KANILA_SYSTEM_PROMPT },
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
 * @param {string} missingField — "skin_type" | "skin_concerns" | "budget_range"
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
};
