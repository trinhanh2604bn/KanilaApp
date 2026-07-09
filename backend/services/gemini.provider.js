/**
 * gemini.provider.js
 * Handles all communication with the Gemini API via @google/genai SDK.
 *
 * SECURITY REVIEW REQUIRED before commit/deploy:
 * - Remove TEMP_DEV_GEMINI_API_KEY and rely solely on process.env.GEMINI_API_KEY.
 * - Add gemini.provider.js to pre-commit checklist.
 */

const { GoogleGenAI } = require("@google/genai");
const {
  KANILA_SYSTEM_PROMPT,
  buildProductContextMessage,
  buildOrderContextMessage,
  buildTicketContextMessage,
} = require("./chatbot.prompt");

// ─────────────────────────────────────────────────────────────────────────────
// TEMP DEV ONLY - remove before commit/deploy
const TEMP_DEV_GEMINI_API_KEY = "PASTE_YOUR_GEMINI_KEY_HERE"; // TEMP DEV ONLY - remove before commit/deploy
// ─────────────────────────────────────────────────────────────────────────────

// Resolve API key: prefer environment variable, fallback to dev key
const geminiApiKey = process.env.GEMINI_API_KEY || TEMP_DEV_GEMINI_API_KEY;

const GEMINI_MODEL = process.env.GEMINI_MODEL || "gemini-2.5-flash";

// Timeout in milliseconds for Gemini API requests
const GEMINI_TIMEOUT_MS = 10000;

/**
 * Initialise the GoogleGenAI client lazily so a missing key does not crash
 * the server at startup — it only throws when a message is sent.
 */
let _genAI = null;
function getGenAI() {
  if (
    !geminiApiKey ||
    geminiApiKey === "PASTE_YOUR_GEMINI_KEY_HERE"
  ) {
    const err = new Error(
      "Gemini API key is missing or not configured. Set GEMINI_API_KEY in .env or add a temporary dev key in gemini.provider.js."
    );
    err.code = "CHATBOT_CONFIG_ERROR";
    throw err;
  }
  if (!_genAI) {
    _genAI = new GoogleGenAI({ apiKey: geminiApiKey });
  }
  return _genAI;
}

/**
 * Internal: run a Gemini chat call with timeout protection.
 * @param {string} message — user message text (may include injected context)
 * @param {Array} history — prior conversation turns in Gemini format
 * @returns {Promise<string>}
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
    if (err.code === "CHATBOT_CONFIG_ERROR" || err.code === "CHATBOT_TIMEOUT") {
      throw err;
    }
    console.error("[Gemini] Provider error:", err.message);
    const wrapped = new Error("Gemini provider encountered an error.");
    wrapped.code = "CHATBOT_ERROR";
    throw wrapped;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Public functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * General chat — used for non-product, non-order intents.
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateChatReply(userMessage, history = []) {
  return _geminiChatWithTimeout(userMessage, history);
}

/**
 * Product recommendation explanation.
 * Products are injected as KANILA_PRODUCT_CONTEXT — Gemini must NOT invent data.
 * @param {object[]} products
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateProductExplanation(products, userMessage, history = []) {
  const messageWithContext = buildProductContextMessage(products, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Order tracking explanation.
 * Order data is injected as KANILA_ORDER_CONTEXT — Gemini must NOT invent delivery/refund data.
 * @param {object} order — normalized order object
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateOrderExplanation(order, userMessage, history = []) {
  const messageWithContext = buildOrderContextMessage(order, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

/**
 * Support ticket confirmation.
 * Ticket data is injected as KANILA_TICKET_CONTEXT.
 * @param {object} ticket — normalized ticket object
 * @param {string} userMessage
 * @param {Array} history
 * @returns {Promise<string>}
 */
async function generateTicketConfirmation(ticket, userMessage, history = []) {
  const messageWithContext = buildTicketContextMessage(ticket, userMessage);
  return _geminiChatWithTimeout(messageWithContext, history);
}

module.exports = {
  generateChatReply,
  generateProductExplanation,
  generateOrderExplanation,
  generateTicketConfirmation,
};
