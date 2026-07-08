/**
 * chatbot.service.js
 * Business logic for the Kanila chatbot module.
 *
 * Phase 1:  general_chat — Gemini conversation
 * Phase 2A: product_recommendation — real MongoDB products
 * Phase 3A: order_tracking — secure per-user order lookup
 *           support_ticket — ticket creation
 * Phase 4A: personalized product recommendation using customer profile
 *           progressive profile questioning
 *           preference saving from conversation
 *
 * Response always includes:
 *   session_id, reply_type, bot_message, products[], order|null, ticket|null,
 *   quick_replies[], handoff_required, customer_context_used
 */

const crypto = require("crypto");
const mongoose = require("mongoose");
const ChatbotSession = require("../models/chatbotSession.model");
const ChatbotMessage = require("../models/chatbotMessage.model");
const Customer = require("../models/customer.model");
const {
  generateChatReply,
  generatePersonalizedProductExplanation,
  generateProductExplanation,
  generateMissingInfoQuestion,
  generateOrderExplanation,
  generateTicketConfirmation,
} = require("./gemini.provider");
const { findRecommendedProducts } = require("./chatbotProduct.tool");
const { findOrderForUser } = require("./chatbotOrder.tool");
const { createSupportTicket } = require("./chatbotSupport.tool");
const { getCustomerContext } = require("./chatbotCustomerContext.service");
const { updateCustomerPreference } = require("./chatbotPreference.service");
const { rankProducts } = require("./chatbotRecommendation.scorer");
const { parseProductConstraints } = require("./chatbotProductQuery.parser");

const MAX_HISTORY_MESSAGES = 10;

// ─────────────────────────────────────────────────────────────────────────────
// Quick replies
// ─────────────────────────────────────────────────────────────────────────────

const DEFAULT_QUICK_REPLIES = [
  "Tư vấn sản phẩm",
  "Kiểm tra đơn hàng",
  "Tạo routine",
  "Gặp nhân viên",
];

const PRODUCT_QUICK_REPLIES = [
  "So sánh các sản phẩm này",
  "Tạo routine với sản phẩm phù hợp",
  "Tìm sản phẩm rẻ hơn",
  "Tư vấn sản phẩm khác",
];

const NO_PRODUCT_QUICK_REPLIES = [
  "Chọn loại da khác",
  "Tăng ngân sách",
  "Xem tất cả sản phẩm",
  "Gặp nhân viên tư vấn",
];

const MISSING_INFO_QUICK_REPLIES = {
  skin_type: ["Da dầu", "Da khô", "Da hỗn hợp", "Da nhạy cảm", "Da thường"],
  skin_concerns: ["Mụn", "Thâm", "Xỉn màu", "Lão hóa", "Khô ráp"],
  budget_range: ["Dưới 200k", "200k - 500k", "500k - 1 triệu", "Trên 1 triệu"],
};

const ORDER_LOGIN_QUICK_REPLIES = ["Đăng nhập tài khoản", "Nhập mã đơn hàng", "Gặp nhân viên hỗ trợ"];
const ORDER_FOUND_QUICK_REPLIES = ["Xem chi tiết đơn hàng", "Tạo yêu cầu đổi trả", "Gặp nhân viên hỗ trợ", "Mua thêm sản phẩm"];
const ORDER_NOT_FOUND_QUICK_REPLIES = ["Nhập mã đơn hàng khác", "Gặp nhân viên hỗ trợ", "Quay lại mua sắm"];
const SUPPORT_LOGIN_QUICK_REPLIES = ["Đăng nhập tài khoản", "Gặp nhân viên hỗ trợ", "Quay lại"];
const SUPPORT_CREATED_QUICK_REPLIES = ["Kiểm tra đơn hàng", "Tư vấn sản phẩm", "Quay lại trang chủ"];

// ─────────────────────────────────────────────────────────────────────────────
// Intent detection
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_RULES = [
  {
    intent: "order_tracking",
    keywords: [
      "đơn hàng", "trạng thái đơn", "giao hàng", "đã giao chưa",
      "vận chuyển", "mã đơn", "tracking", "kiểm tra đơn", "tra cứu đơn",
      "đơn của mình", "bao giờ giao", "ship", "shipper", "tới đâu rồi",
    ],
  },
  {
    intent: "support_ticket",
    keywords: [
      "gặp nhân viên", "hỗ trợ", "khiếu nại", "đổi trả", "hoàn tiền",
      "sai hàng", "thiếu hàng", "lỗi sản phẩm", "cần tư vấn thêm",
      "gặp người", "tư vấn viên", "tạo yêu cầu", "yêu cầu hỗ trợ",
      "giao nhầm", "không nhận được",
    ],
  },
  {
    intent: "product_recommendation",
    keywords: [
      "tư vấn", "sản phẩm", "da dầu", "da khô", "mụn",
      "kem chống nắng", "serum", "toner", "dưỡng ẩm", "làm sáng",
      "gợi ý", "recommend", "chọn", "tìm", "da nhạy cảm",
      "da hỗn hợp", "cleanser", "sữa rửa mặt", "mặt nạ", "moisturizer",
      "tìm sản phẩm", "tìm cho mình",
    ],
  },
  {
    intent: "routine_builder",
    keywords: ["routine", "chu trình", "skincare", "chăm sóc da"],
  },
  {
    intent: "ingredient_check",
    keywords: [
      "thành phần", "retinol", "bha", "aha", "niacinamide",
      "vitamin c", "spf", "hyaluronic", "peptide", "ceramide",
    ],
  },
];

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
    } catch (_) {}
  }
  const guestId = accountId ? null : `guest_${crypto.randomUUID()}`;
  return ChatbotSession.create({
    account_id: accountId,
    customer_id: customerId,
    guest_session_id: guestId,
    source_screen: sourceScreen || "unknown",
  });
}

async function buildGeminiHistory(sessionId) {
  const messages = await ChatbotMessage.find({ session_id: sessionId })
    .sort({ created_at: -1 })
    .limit(MAX_HISTORY_MESSAGES)
    .lean();
  return messages.reverse().map((msg) => ({
    role: msg.sender_type === "user" ? "user" : "model",
    parts: [{ text: msg.message_text }],
  }));
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 4A: personalized product recommendation handler
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Handle product_recommendation intent with personalization:
 *
 * 1. If user is authenticated: load customer context.
 * 2. If critical info is missing (skin_type not in profile NOR in message):
 *    → Ask ONE progressive question. Do NOT query products yet.
 * 3. If enough context: query products + rank by personalization score + explain.
 * 4. If user is guest: Phase 2A behavior (no profile).
 * 5. Non-fatal: if context/ranking fails, fall back to Phase 2A.
 *
 * @returns {{ botText, products, quickReplies, replyType, customerContextUsed, askingQuestion }}
 */
async function handlePersonalizedProductRecommendation(message, user, history) {
  const constraints = parseProductConstraints(message);
  let customerContext = null;
  let customerId = null;
  let customerContextUsed = false;

  // ── Load customer context (authenticated only) ────────────────────────────
  if (user && user.account_id) {
    try {
      const customer = await Customer.findOne({ account_id: user.account_id })
        .select("_id")
        .lean();
      if (customer) {
        customerId = customer._id;
        customerContext = await getCustomerContext(customerId);
      }
    } catch (_) {
      // Non-fatal — fall back to Phase 2A
    }
  }

  const profile = customerContext ? customerContext.customer_profile : null;

  // ── Progressive questioning: ask if skin_type is unknown in BOTH profile AND message ──
  if (
    customerContext &&
    customerContext.preference_confidence === "low" &&
    !constraints.skinType &&
    customerContext.missing_info.length > 0
  ) {
    const missingField = customerContext.missing_info[0]; // highest priority first
    let botText;
    try {
      botText = await generateMissingInfoQuestion(missingField, message, history);
    } catch (_) {
      const fallbacks = {
        skin_type: "Để tư vấn chính xác hơn, bạn thuộc loại da nào? Da dầu / Da khô / Da hỗn hợp / Da nhạy cảm / Da thường",
        skin_concerns: "Bạn đang gặp vấn đề gì về da? Ví dụ: Mụn, Thâm, Xỉn màu, Lão hóa...",
        budget_range: "Bạn thường chi bao nhiêu cho một sản phẩm chăm sóc da?",
      };
      botText = fallbacks[missingField] || "Bạn có thể cho mình biết thêm về nhu cầu của bạn không?";
    }
    return {
      botText,
      products: [],
      quickReplies: MISSING_INFO_QUICK_REPLIES[missingField] || DEFAULT_QUICK_REPLIES,
      replyType: "text",
      customerContextUsed: true,
      askingQuestion: true,
    };
  }

  // ── Query products (Phase 2A query engine) ────────────────────────────────
  let rawProducts = [];
  try {
    const result = await findRecommendedProducts({ message, limit: 10 });
    rawProducts = result.products || [];
  } catch (err) {
    console.error("[Chatbot] Product query failed:", err.message);
  }

  // ── Personalized ranking (Phase 4A) ──────────────────────────────────────
  let products = rawProducts;
  if (profile && rawProducts.length > 0) {
    try {
      products = rankProducts(rawProducts, profile, constraints, 5);
      customerContextUsed = true;
    } catch (_) {
      // Fall back to Phase 2A ordering
      products = rawProducts.slice(0, 5);
    }
  }

  // ── No products found ─────────────────────────────────────────────────────
  if (products.length === 0) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText = "Mình chưa tìm được sản phẩm phù hợp. Bạn có thể cho mình biết thêm về loại da, vấn đề da và ngân sách không?";
    }
    return {
      botText,
      products: [],
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
      customerContextUsed,
      askingQuestion: false,
    };
  }

  // ── Gemini explanation with customer context ──────────────────────────────
  let botText;
  try {
    if (profile && customerContextUsed) {
      botText = await generatePersonalizedProductExplanation(products, message, profile, history);
    } else {
      botText = await generateProductExplanation(products, message, history);
    }
  } catch (_) {
    botText = "Mình tìm được một số sản phẩm phù hợp. Bạn có thể xem nhanh các gợi ý bên dưới nhé.";
  }

  return {
    botText,
    products,
    quickReplies: PRODUCT_QUICK_REPLIES,
    replyType: "product_recommendation",
    customerContextUsed,
    askingQuestion: false,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 3A handlers (unchanged)
// ─────────────────────────────────────────────────────────────────────────────

async function handleOrderTracking(message, user, history) {
  const accountId = user ? user.account_id : null;
  const result = await findOrderForUser({ accountId, message }).catch((err) => {
    console.error("[Chatbot] Order query error:", err.message);
    return { order: null, notFound: false, loginRequired: false };
  });

  if (result.loginRequired) {
    const botText =
      "Để xem thông tin đơn hàng, bạn cần đăng nhập tài khoản Kanila trước nhé. " +
      "Nếu bạn có mã đơn hàng, bạn cũng có thể cung cấp cho mình!";
    return { botText, order: null, quickReplies: ORDER_LOGIN_QUICK_REPLIES, replyType: "order_tracking", handoffRequired: false };
  }

  if (result.notFound && result.orderCode) {
    const botText =
      "Mình chưa tìm thấy đơn hàng này trong tài khoản của bạn. " +
      "Vui lòng kiểm tra lại mã đơn hàng hoặc liên hệ hỗ trợ để được trợ giúp.";
    return { botText, order: null, quickReplies: ORDER_NOT_FOUND_QUICK_REPLIES, replyType: "order_tracking", handoffRequired: false };
  }

  if (result.notFound || !result.order) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText = "Mình không tìm thấy đơn hàng nào trong tài khoản của bạn. Bạn cần hỗ trợ thêm không?";
    }
    return { botText, order: null, quickReplies: ORDER_NOT_FOUND_QUICK_REPLIES, replyType: "order_tracking", handoffRequired: false };
  }

  let botText;
  try {
    botText = await generateOrderExplanation(result.order, message, history);
  } catch (_) {
    const o = result.order;
    botText = `Đơn hàng ${o.order_code} của bạn đang ở trạng thái: ${o.fulfillment_status_label}. ${o.next_action}`;
  }

  return { botText, order: result.order, quickReplies: ORDER_FOUND_QUICK_REPLIES, replyType: "order_tracking", handoffRequired: false };
}

async function handleSupportTicket(message, user, sessionId, history) {
  const accountId = user ? user.account_id : null;

  if (!accountId) {
    const botText =
      "Để tạo yêu cầu hỗ trợ, bạn cần đăng nhập tài khoản Kanila. " +
      "Sau khi đăng nhập, mình sẽ ghi nhận và chuyển yêu cầu đến đội ngũ hỗ trợ ngay nhé!";
    return { botText, ticket: null, quickReplies: SUPPORT_LOGIN_QUICK_REPLIES, replyType: "support_ticket", handoffRequired: true };
  }

  const result = await createSupportTicket({ accountId, message, sessionId: sessionId.toString() }).catch((err) => {
    console.error("[Chatbot] Ticket creation error:", err.message);
    return { ticket: null, loginRequired: false, error: "internal_error" };
  });

  if (!result.ticket || result.error) {
    const botText =
      "Xin lỗi, mình không thể tạo yêu cầu hỗ trợ lúc này. " +
      "Bạn có thể thử lại sau hoặc liên hệ trực tiếp với đội hỗ trợ Kanila nhé.";
    return { botText, ticket: null, quickReplies: DEFAULT_QUICK_REPLIES, replyType: "support_ticket", handoffRequired: true };
  }

  let botText;
  try {
    botText = await generateTicketConfirmation(result.ticket, message, history);
  } catch (_) {
    botText = result.ticket.message ||
      `Kanila đã ghi nhận yêu cầu hỗ trợ của bạn (mã: ${result.ticket.ticket_code}). Đội ngũ sẽ liên hệ sớm nhất có thể!`;
  }

  return { botText, ticket: result.ticket, quickReplies: SUPPORT_CREATED_QUICK_REPLIES, replyType: "support_ticket", handoffRequired: true };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main service function
// ─────────────────────────────────────────────────────────────────────────────

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

  // 5. Phase 4A: save preferences from user message (authenticated only, non-blocking)
  if (user && user.account_id && intent === "product_recommendation") {
    // Resolve customerId lazily — non-fatal
    Customer.findOne({ account_id: user.account_id })
      .select("_id")
      .lean()
      .then((customer) => {
        if (customer) {
          return updateCustomerPreference({ customerId: customer._id, message: message.trim() });
        }
      })
      .catch((err) => console.error("[ChatbotPreference] Non-fatal:", err.message));
  }

  // 6. Branch by intent
  let botText;
  let products = [];
  let order = null;
  let ticket = null;
  let quickReplies = DEFAULT_QUICK_REPLIES;
  let replyType = "text";
  let handoffRequired = false;
  let customerContextUsed = false;

  try {
    if (intent === "product_recommendation") {
      // Phase 4A: personalized recommendation
      const r = await handlePersonalizedProductRecommendation(message.trim(), user, history);
      botText = r.botText;
      products = r.products;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      customerContextUsed = r.customerContextUsed;

    } else if (intent === "order_tracking") {
      const r = await handleOrderTracking(message.trim(), user, history);
      botText = r.botText;
      order = r.order;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      handoffRequired = r.handoffRequired || false;

    } else if (intent === "support_ticket") {
      const r = await handleSupportTicket(message.trim(), user, session._id, history);
      botText = r.botText;
      ticket = r.ticket;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      handoffRequired = r.handoffRequired || false;

    } else {
      // Phase 1: general Gemini reply
      botText = await generateChatReply(message.trim(), history);
      handoffRequired = intent === "human_support";
    }
  } catch (err) {
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "bot",
      message_text: "Xin lỗi, mình không thể xử lý yêu cầu của bạn lúc này. Vui lòng thử lại sau.",
      intent,
      response_type: "error",
      metadata: { error_code: err.code || "CHATBOT_ERROR" },
    });
    throw err;
  }

  // 7. Save bot reply (safe metadata only)
  await ChatbotMessage.create({
    session_id: session._id,
    sender_type: "bot",
    message_text: botText,
    intent,
    response_type: "text",
    metadata: {
      reply_type: replyType,
      quick_replies: quickReplies,
      products,
      order: order ? { order_id: order.order_id, order_code: order.order_code, status: order.status, fulfillment_status: order.fulfillment_status } : null,
      ticket: ticket ? { ticket_id: ticket.ticket_id, ticket_code: ticket.ticket_code, status: ticket.status, category: ticket.category } : null,
      handoff_required: handoffRequired,
      customer_context_used: customerContextUsed,
    },
  });

  // 8. Update session last intent
  await ChatbotSession.findByIdAndUpdate(session._id, { last_intent: intent });

  // 9. Return full structured result (backward compatible + Phase 4A additions)
  return {
    session_id: session._id.toString(),
    reply_type: replyType,
    bot_message: botText,
    products,
    order,
    ticket,
    quick_replies: quickReplies,
    handoff_required: handoffRequired,
    customer_context_used: customerContextUsed,
  };
}

async function getSessionMessages(sessionId) {
  const session = await ChatbotSession.findById(sessionId).lean();
  if (!session) return null;
  const messages = await ChatbotMessage.find({ session_id: sessionId })
    .sort({ created_at: 1 })
    .lean();
  return { session, messages };
}

module.exports = { handleUserMessage, getSessionMessages };
