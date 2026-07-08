/**
 * chatbot.service.js
 * Business logic for the Kanila chatbot module.
 * Handles session management, intent detection, message persistence,
 * and orchestrating calls to the Gemini provider.
 */

const crypto = require("crypto");
const mongoose = require("mongoose");
const ChatbotSession = require("../models/chatbotSession.model");
const ChatbotMessage = require("../models/chatbotMessage.model");
const Customer = require("../models/customer.model");
const { generateChatReply } = require("./gemini.provider");

// Maximum number of recent messages to send to Gemini as history context
const MAX_HISTORY_MESSAGES = 10;

// Default quick replies shown after every bot response in Phase 1
const DEFAULT_QUICK_REPLIES = [
  "Tư vấn sản phẩm",
  "Tạo routine",
  "Kiểm tra thành phần",
  "Tra cứu đơn hàng",
  "Gặp nhân viên",
];

// ─────────────────────────────────────────────────────────────────────────────
// Intent detection — simple rule-based keyword matching (Phase 1)
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_RULES = [
  {
    intent: "product_recommendation",
    keywords: [
      "tư vấn", "sản phẩm", "da dầu", "da khô", "mụn",
      "kem chống nắng", "serum", "toner", "dưỡng ẩm", "làm sáng",
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
  // Try to find existing session
  if (sessionId && mongoose.Types.ObjectId.isValid(sessionId)) {
    const existing = await ChatbotSession.findById(sessionId);
    if (existing) return existing;
  }

  // Resolve customer_id from account if authenticated
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

  // Use crypto.randomUUID() for a safer, globally unique guest identifier
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
 * Fetch the last N messages from a session and format them for the Gemini
 * chat history API (role: "user" | "model").
 * Used to build context BEFORE the current user message is saved.
 *
 * @param {string} sessionId
 * @returns {Promise<Array<{role: string, parts: Array<{text: string}>}>>}
 */
async function buildGeminiHistory(sessionId) {
  const messages = await ChatbotMessage.find({ session_id: sessionId })
    .sort({ created_at: -1 })
    .limit(MAX_HISTORY_MESSAGES)
    .lean();

  // Reverse to chronological order (oldest first) and map to Gemini format
  return messages
    .reverse()
    .map((msg) => ({
      role: msg.sender_type === "user" ? "user" : "model",
      parts: [{ text: msg.message_text }],
    }));
}

// ─────────────────────────────────────────────────────────────────────────────
// Main service functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Handle an incoming user message:
 * 1. Resolve or create session.
 * 2. Detect intent.
 * 3. Build Gemini history from EXISTING messages (before saving current message).
 * 4. Save user message.
 * 5. Call Gemini with history + current user message.
 * 6. Save bot reply (or a bot error message if Gemini fails).
 * 7. Update session's last_intent.
 * 8. Return structured result.
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

  // 3. Build Gemini history from existing messages BEFORE saving the current one.
  //    This ensures the current user turn is passed as the active message,
  //    not as a history item — no pop() workaround needed.
  const history = await buildGeminiHistory(session._id);

  // 4. Save user message
  await ChatbotMessage.create({
    session_id: session._id,
    sender_type: "user",
    message_text: message.trim(),
    intent,
    response_type: "text",
  });

  // 5. Call Gemini with prior history and the current message as active turn
  let botText;
  try {
    botText = await generateChatReply(message.trim(), history);
  } catch (geminiErr) {
    // 6a. Gemini failed — persist a bot error message so the session record
    //     reflects the failure, then re-throw the structured error upstream.
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "bot",
      message_text: "Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này. Vui lòng thử lại sau.",
      intent,
      response_type: "error",
      metadata: { error_code: geminiErr.code || "CHATBOT_ERROR" },
    });
    throw geminiErr; // re-throw structured error (CHATBOT_CONFIG_ERROR, CHATBOT_TIMEOUT, CHATBOT_ERROR)
  }

  // 6b. Save successful bot reply
  await ChatbotMessage.create({
    session_id: session._id,
    sender_type: "bot",
    message_text: botText,
    intent,
    response_type: "text",
    metadata: { quick_replies: DEFAULT_QUICK_REPLIES },
  });

  // 7. Update session's last intent
  await ChatbotSession.findByIdAndUpdate(session._id, { last_intent: intent });

  // 8. Return structured result matching the API contract
  return {
    session_id: session._id.toString(),
    reply_type: "text",
    bot_message: botText,
    quick_replies: DEFAULT_QUICK_REPLIES,
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
