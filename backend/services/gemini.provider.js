/**
 * gemini.provider.js
 * Handles all communication with the Gemini API via @google/genai SDK.
 *
 * SECURITY REVIEW REQUIRED before commit/deploy:
 * - Remove TEMP_DEV_GEMINI_API_KEY and rely solely on process.env.GEMINI_API_KEY.
 * - Add gemini.provider.js to pre-commit checklist.
 */

const { GoogleGenAI } = require("@google/genai");
const { KANILA_SYSTEM_PROMPT } = require("./chatbot.prompt");

// Resolve API key: prefer environment variable
const geminiApiKey = process.env.GEMINI_API_KEY;

const GEMINI_MODEL = process.env.GEMINI_MODEL || "gemini-2.5-flash";

// Timeout in milliseconds for Gemini API requests
const GEMINI_TIMEOUT_MS = 10000;

/**
 * Initialise the GoogleGenAI client lazily so a missing key does not crash
 * the server at startup — it only throws when a message is sent.
 */
let _genAI = null;
function getGenAI() {
  if (!geminiApiKey) {
    const err = new Error(
      "Gemini API key is missing or not configured. Set GEMINI_API_KEY in .env."
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
 * Call Gemini with conversation history and the user's latest message.
 *
 * @param {string} userMessage — The latest user message text.
 * @param {Array<{role: string, parts: Array<{text: string}>}>} history
 *   — The last N messages formatted as Gemini Content objects (role: "user"|"model").
 * @returns {Promise<string>} — The assistant's text reply.
 */
async function generateChatReply(userMessage, history = []) {
  const genAI = getGenAI(); // throws CHATBOT_CONFIG_ERROR if key missing

  // Wrap the Gemini call with a timeout race
  const geminiPromise = (async () => {
    const chat = genAI.chats.create({
      model: GEMINI_MODEL,
      config: {
        systemInstruction: KANILA_SYSTEM_PROMPT,
      },
      history,
    });

    const result = await chat.sendMessage({ message: userMessage });
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
    const replyText = await Promise.race([geminiPromise, timeoutPromise]);
    return replyText;
  } catch (err) {
    // Re-throw structured errors (CONFIG or TIMEOUT) as-is
    if (err.code === "CHATBOT_CONFIG_ERROR" || err.code === "CHATBOT_TIMEOUT") {
      throw err;
    }
    // Wrap unexpected Gemini SDK errors without leaking internals
    console.error("[Gemini] Provider error:", err.message);
    const wrapped = new Error("Gemini provider encountered an error.");
    wrapped.code = "CHATBOT_ERROR";
    throw wrapped;
  }
}

module.exports = { generateChatReply };
