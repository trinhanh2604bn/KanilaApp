/**
 * chatbot.service.js
 * Business logic for the Kanila chatbot module.
 * Handles session management, intent detection, message persistence,
 * and orchestrating calls to the Gemini provider.
 *
 * Phase 2A: product_recommendation intent now queries real MongoDB products
 *           and passes them to Gemini for explanation.
 */

const crypto = require("crypto");
const mongoose = require("mongoose");
const ChatbotSession = require("../models/chatbotSession.model");
const ChatbotMessage = require("../models/chatbotMessage.model");
const Customer = require("../models/customer.model");
const { generateChatReply, generateProductExplanation } = require("./gemini.provider");
const { findRecommendedProducts } = require("./chatbotProduct.tool");

// Maximum number of recent messages to send to Gemini as history context
const MAX_HISTORY_MESSAGES = 10;

// ─────────────────────────────────────────────────────────────────────────────
// Quick replies per intent
// ─────────────────────────────────────────────────────────────────────────────

const DEFAULT_QUICK_REPLIES = [
  "Tư vấn sản phẩm",
  "Tạo routine",
  "Kiểm tra thành phần",
  "Tra cứu đơn hàng",
  "Gặp nhân viên",
];

const PRODUCT_QUICK_REPLIES = [
  "So sánh các sản phẩm này",
  "Tạo routine với sản phẩm phù hợp",
  "Tìm sản phẩm rẻ hơn",
  "Tư vấn sản phẩm khác",
];

const NO_RESULT_QUICK_REPLIES = [
  "Chọn loại da khác",
  "Tăng ngân sách",
  "Xem tất cả sản phẩm",
  "Gặp nhân viên tư vấn",
];

// ─────────────────────────────────────────────────────────────────────────────
// Intent detection — simple rule-based keyword matching (Phase 1 + 2A)
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_RULES = [
  {
    intent: "product_recommendation",
    keywords: [
      "tư vấn", "sản phẩm", "da dầu", "da khô", "mụn",
      "kem chống nắng", "serum", "toner", "dưỡng ẩm", "làm sáng",
      "gợi ý", "recommend", "chọn", "tìm", "da nhạy cảm",
      "da hỗn hợp", "cleanser", "sữa rửa mặt", "mặt nạ", "moisturizer",
    ],
  },
  {
    intent: "routine_builder",
    keywords: ["routine", "chu trình", "sáng", "tối", "skincare", "chăm sóc da"],
  },
  {
    intent: "ingredient_check",
    keywords: [
      "thành phần", "retinol", "bha", "aha", "niacinamide",
      "vitamin c", "spf", "hyaluronic", "peptide", "ceramide",
    ],
  },
  {
    intent: "order_tracking",
    keywords: ["đơn hàng", "giao hàng", "vận chuyển", "tới đâu", "tracking", "shipper"],
  },
  {
    intent: "human_support",
    keywords: ["nhân viên", "hỗ trợ", "khiếu nại", "đổi trả", "hoàn tiền", "tư vấn viên"],
  },
];

/**
 * Detect user intent from message text using keyword matching.
 * Returns "general_chat" if no rule matches.
 * @param {string} text
 * @returns {string}
 */
function detectIntent(text) {
  const lower = text.toLowerCase();
  for (const rule of INTENT_RULES) {
    if (rule.keywords.some((kw) => lower.includes(kw))) {
      return rule.intent;
    }
  }
  return "general_chat";
}

// ─────────────────────────────────────────────────────────────────────────────
// Session helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Find an existing session by ID, or create a new one.
 * Attaches account/customer from the authenticated user when available.
 *
 * @param {string|null} sessionId
 * @param {object} user — decoded JWT payload (req.user), may be undefined
 * @param {string} sourceScreen
 * @returns {Promise<ChatbotSession>}
 */
async function resolveOrCreateSession(sessionId, user, sourceScreen) {
  if (sessionId && mongoose.Types.ObjectId.isValid(sessionId)) {
    const existing = await ChatbotSession.findById(sessionId);
    if (existing) return existing;
  }

  let accountId = null;
  let customerId = null;

  if (user && user.account_id) {
    accountId = user.account_id;
    try {
      const customer = await Customer.findOne({ account_id: user.account_id }).lean();
      if (customer) customerId = customer._id;
    } catch (_) {
      // Non-blocking — guest chat must still work if customer lookup fails
    }
  }

  const guestId = accountId ? null : `guest_${crypto.randomUUID()}`;

  const session = await ChatbotSession.create({
    account_id: accountId,
    customer_id: customerId,
    guest_session_id: guestId,
    source_screen: sourceScreen || "unknown",
  });

  return session;
}

// ─────────────────────────────────────────────────────────────────────────────
// Message helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Fetch the last N messages from a session and format them for Gemini history.
 * Called BEFORE saving the current user message.
 *
 * @param {string} sessionId
 * @returns {Promise<Array<{role: string, parts: Array<{text: string}>}>>}
 */
async function buildGeminiHistory(sessionId) {
  const messages = await ChatbotMessage.find({ session_id: sessionId })
    .sort({ created_at: -1 })
    .limit(MAX_HISTORY_MESSAGES)
    .lean();

  return messages
    .reverse()
    .map((msg) => ({
      role: msg.sender_type === "user" ? "user" : "model",
      parts: [{ text: msg.message_text }],
    }));
}

// ─────────────────────────────────────────────────────────────────────────────
// Product recommendation handler (Phase 2A)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Handle a product_recommendation intent:
 * 1. Query real products from MongoDB based on parsed constraints.
 * 2. If products found, ask Gemini to write a short Vietnamese explanation.
 * 3. If no products found, return a friendly message asking for more info.
 * 4. If Gemini fails, use a safe fallback message (products still returned).
 *
 * @param {string} message — user message
 * @param {Array} history — prior Gemini history
 * @returns {Promise<{ botText: string, products: object[], quickReplies: string[], replyType: string }>}
 */
async function handleProductRecommendation(message, history) {
  // 1. Query products (non-fatal — falls back gracefully)
  let products = [];
  try {
    const result = await findRecommendedProducts({ message });
    products = result.products || [];
  } catch (productErr) {
    console.error("[Chatbot] Product query failed:", productErr.message);
    // Fall through to general Gemini response
  }

  // 2. No products found — ask Gemini for a helpful follow-up question
  if (products.length === 0) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText =
        "Mình chưa tìm được sản phẩm phù hợp. Bạn có thể cho mình biết thêm về loại da, vấn đề da và ngân sách để mình tìm kiếm tốt hơn nhé?";
    }
    return {
      botText,
      products: [],
      quickReplies: NO_RESULT_QUICK_REPLIES,
      replyType: "text",
    };
  }

  // 3. Products found — ask Gemini to write a short explanation
  let botText;
  try {
    botText = await generateProductExplanation(products, message, history);
  } catch (_) {
    // Gemini failed but we still have products — use safe fallback message
    botText =
      "Mình tìm được một số sản phẩm phù hợp. Bạn có thể xem nhanh các gợi ý bên dưới nhé.";
  }

  return {
    botText,
    products,
    quickReplies: PRODUCT_QUICK_REPLIES,
    replyType: "product_recommendation",
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main service functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Handle an incoming user message.
 *
 * Flow:
 * 1. Resolve or create session.
 * 2. Detect intent.
 * 3. Build Gemini history from EXISTING messages (before saving current message).
 * 4. Save user message.
 * 5. Branch by intent:
 *    - product_recommendation → handleProductRecommendation()
 *    - all others → generateChatReply()
 * 6. Save bot reply (or error message if generation fails).
 * 7. Update session's last_intent.
 * 8. Return structured result (always includes products:[]).
 *
 * @param {object} params
 * @param {string|null} params.sessionId
 * @param {string} params.message
 * @param {string} [params.sourceScreen]
 * @param {object} [params.user] — decoded JWT payload
 * @returns {Promise<object>}
 */
async function handleUserMessage({ sessionId, message, sourceScreen, user }) {
  // 1. Resolve or create session
  const session = await resolveOrCreateSession(sessionId, user, sourceScreen);

  // 2. Detect intent
  const intent = detectIntent(message);

  // 3. Build Gemini history BEFORE saving current user message
  const history = await buildGeminiHistory(session._id);

  // 4. Save user message
  await ChatbotMessage.create({
    session_id: session._id,
    sender_type: "user",
    message_text: message.trim(),
    intent,
    response_type: "text",
  });

  // 5. Branch by intent
  let botText;
  let products = [];
  let quickReplies = DEFAULT_QUICK_REPLIES;
  let replyType = "text";

  if (intent === "product_recommendation") {
    // Phase 2A: real product recommendation
    try {
      const result = await handleProductRecommendation(message.trim(), history);
      botText = result.botText;
      products = result.products;
      quickReplies = result.quickReplies;
      replyType = result.replyType;
    } catch (err) {
      // Save a bot error message and rethrow structured error
      await ChatbotMessage.create({
        session_id: session._id,
        sender_type: "bot",
        message_text:
          "Xin lỗi, mình không thể xử lý yêu cầu của bạn lúc này. Vui lòng thử lại sau.",
        intent,
        response_type: "error",
        metadata: { error_code: err.code || "CHATBOT_ERROR" },
      });
      throw err;
    }
  } else {
    // Phase 1: general Gemini reply
    try {
      botText = await generateChatReply(message.trim(), history);
    } catch (err) {
      await ChatbotMessage.create({
        session_id: session._id,
        sender_type: "bot",
        message_text:
          "Xin lỗi, mình không thể xử lý yêu cầu của bạn lúc này. Vui lòng thử lại sau.",
        intent,
        response_type: "error",
        metadata: { error_code: err.code || "CHATBOT_ERROR" },
      });
      throw err;
    }
  }

  // 6. Save bot reply
  await ChatbotMessage.create({
    session_id: session._id,
    sender_type: "bot",
    message_text: botText,
    intent,
    response_type: "text",
    metadata: {
      reply_type: replyType,
      quick_replies: quickReplies,
      products,          // save product snapshot in metadata for history retrieval
    },
  });

  // 7. Update session's last intent
  await ChatbotSession.findByIdAndUpdate(session._id, { last_intent: intent });

  // 8. Return structured result — always include products:[] for Android safety
  return {
    session_id: session._id.toString(),
    reply_type: replyType,
    bot_message: botText,
    products,
    quick_replies: quickReplies,
    handoff_required: intent === "human_support",
  };
}

/**
 * Retrieve all messages for a session ordered by created_at ascending.
 *
 * @param {string} sessionId
 * @returns {Promise<{session: ChatbotSession, messages: ChatbotMessage[]}|null>}
 */
async function getSessionMessages(sessionId) {
  const session = await ChatbotSession.findById(sessionId).lean();
  if (!session) return null;

  const messages = await ChatbotMessage.find({ session_id: sessionId })
    .sort({ created_at: 1 })
    .lean();

  return { session, messages };
}

module.exports = {
  handleUserMessage,
  getSessionMessages,
};
