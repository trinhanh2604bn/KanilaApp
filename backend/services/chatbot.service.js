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
 * Phase 5A: AI Cart Assistant
 *           cart_recommendation — build product combos
 *           add_to_cart         — add combo to real cart
 *           cart_summary        — read current cart
 *
 * Response always includes:
 *   session_id, reply_type, bot_message, products[], order|null, ticket|null,
 *   quick_replies[], handoff_required, customer_context_used,
 *   cart_summary|null, cart_action|null, upsell_products[]
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
  generateCartExplanation,
  generateCartActionConfirmation,
  generateCartSummaryReply,
  generateBeautyConsultationReply,
  generateComboExplanation,
} = require("./gemini.provider");
const { findRecommendedProducts } = require("./chatbotProduct.tool");
const { extractShoppingContext, CANONICAL_CATEGORY_MAP } = require("./chatbotShoppingContext");
const { findOrderForUser } = require("./chatbotOrder.tool");
const { createSupportTicket } = require("./chatbotSupport.tool");
const { getCustomerContext } = require("./chatbotCustomerContext.service");
const { updateCustomerPreference } = require("./chatbotPreference.service");
const { rankProducts } = require("./chatbotRecommendation.scorer");
const { parseProductConstraints } = require("./chatbotProductQuery.parser");
const { generateCartRecommendation, addProductsToCart, calculateCartSummary } = require("./chatbotCart.tool");
const { findComplementaryProducts } = require("./chatbotUpsell.tool");
const { parseCartIntent } = require("./chatbotCart.parser");
const { handleProductComparison } = require("./chatbotComparison.service");
// Phase 5 shopping assistant
const {
  getRecommendationContext,
  buildComboRecommendation,
} = require("./chatbotRecommendation.service");
const { classifyIntent, resolveRoutingIntent } = require("./chatbotIntent.classifier");
const { findMakeupProductsPipeline } = require("./makeupRecommendation.service");
const { buildMakeupProductContextMessage, buildMakeupAnalysisPrompt } = require("./chatbot.prompt");
const { generateMakeupReply, generateMakeupReplyWithAnalysis, handleVoucherQuery } = require("./gemini.provider");
const { loadConversationContext } = require("./chatbotConversationContext");
const { buildMakeupBundle } = require("./chatbotMakeupBundle.service");

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
  budget: ["Dưới 200k", "200k - 500k", "500k - 1 triệu", "Trên 1 triệu"],
};

const ORDER_LOGIN_QUICK_REPLIES = ["Đăng nhập tài khoản", "Nhập mã đơn hàng", "Gặp nhân viên hỗ trợ"];
const ORDER_FOUND_QUICK_REPLIES = ["Xem chi tiết đơn hàng", "Tạo yêu cầu đổi trả", "Gặp nhân viên hỗ trợ", "Mua thêm sản phẩm"];
const ORDER_NOT_FOUND_QUICK_REPLIES = ["Nhập mã đơn hàng khác", "Gặp nhân viên hỗ trợ", "Quay lại mua sắm"];
const SUPPORT_LOGIN_QUICK_REPLIES = ["Đăng nhập tài khoản", "Gặp nhân viên hỗ trợ", "Quay lại"];
const SUPPORT_CREATED_QUICK_REPLIES = ["Kiểm tra đơn hàng", "Tư vấn sản phẩm", "Quay lại trang chủ"];

// Phase 5A cart quick replies
const CART_RECOMMEND_QUICK_REPLIES = ["Thêm combo này vào giỏ", "Thêm sản phẩm bổ sung", "Xem giỏ hàng của mình", "Tư vấn sản phẩm khác"];
const CART_LOGIN_REQUIRED_QUICK_REPLIES = ["Đăng nhập tài khoản", "Xem combo không đăng nhập", "Tư vấn sản phẩm"];
const CART_ADDED_QUICK_REPLIES = ["Xem giỏ hàng của mình", "Thanh toán ngay", "Tiếp tục mua sắm", "Tư vấn thêm sản phẩm"];
const CART_SUMMARY_QUICK_REPLIES = ["Thanh toán ngay", "Xóa sản phẩm khỏi giỏ", "Tiếp tục mua sắm", "Tư vấn thêm sản phẩm"];
const CART_EMPTY_QUICK_REPLIES = ["Tạo combo skincare", "Tư vấn sản phẩm", "Xem ưu đãi hôm nay"];

// ─────────────────────────────────────────────────────────────────────────────
// Intent detection
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_RULES = [
  // Phase 5A: cart intents must appear BEFORE product_recommendation to avoid overlap
  {
    intent: "cart_summary",
    keywords: [
      "giỏ hàng của mình", "xem giỏ hàng", "giỏ hàng có gì",
      "tổng tiền giỏ", "giỏ hiện tại", "trong giỏ", "xem giỏ",
      "kiểm tra giỏ", "bao nhiêu trong giỏ", "cart của mình",
    ],
  },
  {
    intent: "add_to_cart",
    keywords: [
      "thêm vào giỏ", "cho vào giỏ", "thêm combo", "mua combo",
      "thêm bộ này", "add to cart", "mua giúp mình", "thêm hết vào giỏ",
      "mua ngay", "thêm cái này", "cho thêm vào", "đặt hàng ngay",
      "cho mình mua",
    ],
  },
  {
    intent: "cart_recommendation",
    keywords: [
      "bộ skincare", "combo skincare", "bộ chăm sóc da", "tạo combo",
      "gợi ý bộ sản phẩm", "routine đầy đủ", "cần bộ sản phẩm",
      "mua bộ", "set sản phẩm", "bộ dưỡng da", "combo cho mình",
      "bộ làm đẹp", "bộ dưỡng", "gợi ý bộ",
    ],
  },
  // Phase 5: beauty_consultation — general advisory (checked before product_recommendation)
  {
    intent: "beauty_consultation",
    keywords: [
      "nên dùng gì", "dùng loại nào", "có nên dùng", "nên chọn",
      "phù hợp cho da", "tìm hiểu về da", "chăm sóc da như thế nào",
      "hỏi về da", "da mình nên", "tư vấn da", "càng dùng càng",
      "làm đẹp tự nhiên", "làm sáng da", "làn da khỏe",
    ],
  },
  // Phase 5: combo_recommendation — advisory combo without cart action
  {
    intent: "combo_recommendation",
    keywords: [
      "bộ này giá bao nhiêu", "combo dưới", "bộ sản phẩm dưới",
      "routine cơ bản", "gợi ý routine", "chu trình dưỡng da",
      "routine cho da", "bộ đầy đủ", "lập bộ sản phẩm",
      "nếu mua cả bộ", "cần mấy sản phẩm",
    ],
  },
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
        budget: "Bạn thường chi bao nhiêu cho một sản phẩm chăm sóc da?",
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
// Phase 5: Shopping Assistant handlers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Phase 5: General beauty consultation handler.
 *
 * Uses the full recommendation engine (recommendForProfile with ProductBeautyProfile
 * scoring) instead of keyword-only search. Intended for advisory questions like
 * "Da dầu mụn nên dùng gì?" where the user wants product guidance without a
 * specific cart action.
 *
 * Flow:
 *   1. Parse constraints (skin type, concern, budget, category) from message.
 *   2. Load customer context if authenticated.
 *   3. Fetch profile-scored products via getRecommendationContext().
 *   4. Gemini writes the explanation — Gemini does NOT select products.
 *
 * @returns {{ botText, products, quickReplies, replyType, customerContextUsed }}
 */
async function handleBeautyConsultation(message, user, history) {
  const constraints = parseProductConstraints(message);

  // Resolve customerId from JWT (security: never from client body)
  let customerId = null;
  if (user && user.account_id) {
    try {
      const customer = await Customer.findOne({ account_id: user.account_id }).select("_id").lean();
      if (customer) customerId = customer._id;
    } catch (_) {}
  }

  // Fetch profile-scored products
  const { products, customer_context_used } = await getRecommendationContext(
    customerId,
    constraints,
    6
  ).catch((err) => {
    console.error("[Chatbot] BeautyConsultation product error:", err.message);
    return { products: [], customer_context_used: false };
  });

  // Load customer profile for Gemini context (non-fatal)
  let customerProfile = null;
  if (customerId && customer_context_used) {
    try {
      const ctx = await getCustomerContext(customerId);
      customerProfile = ctx?.customer_profile || null;
    } catch (_) {}
  }

  if (products.length === 0) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText = "Mình chưa tìm được sản phẩm phù hợp. Bạn có thể cho mình biết loại da, vấn đề da đang gặp và ngân sách không?";
    }
    return {
      botText,
      products: [],
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
      customerContextUsed: false,
    };
  }

  // Gemini explains WHY these products suit the user — does NOT select them
  let botText;
  try {
    botText = await generateBeautyConsultationReply(products, customerProfile, message, history);
  } catch (_) {
    botText = `Mình tìm được ${products.length} sản phẩm phù hợp cho nhu cầu của bạn. Bạn có thể xem các gợi ý bên dưới nhé!`;
  }

  return {
    botText,
    products,
    quickReplies: PRODUCT_QUICK_REPLIES,
    replyType: "product_recommendation",
    customerContextUsed: customer_context_used,
  };
}

/**
 * Phase 5: Combo recommendation handler (advisory, no cart action).
 *
 * Builds a slot-based skincare combo (cleanser → serum → moisturizer…) using
 * the full recommendation engine. Price calculated by backend — Gemini only
 * explains the combo.
 *
 * Use case: "Tạo routine dưỡng da cơ bản dưới 500k"
 *
 * @returns {{ botText, products, quickReplies, replyType, customerContextUsed }}
 */
async function handleComboRecommendation(message, user, history) {
  const constraints = parseProductConstraints(message);
  // Also parse combo type from cart intent parser (it knows "skincare_basic" vs "skincare_full")
  const { comboType, budgetMax: parsedBudget } = parseCartIntent(message);
  if (parsedBudget && !constraints.budgetMax) constraints.budgetMax = parsedBudget;

  // Resolve customerId
  let customerId = null;
  if (user && user.account_id) {
    try {
      const customer = await Customer.findOne({ account_id: user.account_id }).select("_id").lean();
      if (customer) customerId = customer._id;
    } catch (_) {}
  }

  // Build slot-based combo (backend calculates price)
  let comboResult;
  try {
    comboResult = await buildComboRecommendation(customerId, {
      ...constraints,
      comboType: comboType && comboType !== "unknown" ? comboType : "skincare_basic",
    });
  } catch (err) {
    console.error("[Chatbot] ComboRecommendation error:", err.message);
    return {
      botText: "Mình chưa thể tạo combo phù hợp lúc này. Bạn có thể cho mình biết thêm ngân sách và loại da để mình tư vấn chính xác hơn không?",
      products: [],
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
      customerContextUsed: false,
    };
  }

  const { combo = [], total = 0, customer_context_used = false } = comboResult;

  if (combo.length === 0) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText = "Mình chưa tìm được bộ sản phẩm phù hợp. Bạn hãy cho mình biết ngân sách và loại da để mình tư vấn chính xác hơn nhé!";
    }
    return {
      botText,
      products: [],
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
      customerContextUsed: false,
    };
  }

  // Load customer profile for Gemini context (non-fatal)
  let customerProfile = null;
  if (customerId && customer_context_used) {
    try {
      const ctx = await getCustomerContext(customerId);
      customerProfile = ctx?.customer_profile || null;
    } catch (_) {}
  }

  // Gemini explains combo rationale — does NOT select or price products
  let botText;
  try {
    botText = await generateComboExplanation(combo, total, customerProfile, message, history);
  } catch (_) {
    const totalStr = total.toLocaleString("vi-VN");
    botText = `Mình gợi ý combo ${combo.length} sản phẩm tổng cộng ${totalStr}đ, phù hợp với nhu cầu của bạn. Bạn xem thử nhé!`;
  }

  return {
    botText,
    products: combo, // same field for backward compat
    quickReplies: CART_RECOMMEND_QUICK_REPLIES, // suggest adding to cart as next step
    replyType: "combo_recommendation",
    customerContextUsed: customer_context_used,
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 5A: Cart handlers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Resolve a verified customerId for cart operations.
 * SECURITY: Must come from JWT-verified user, never from client body.
 */
async function resolveCustomerIdFromUser(user) {
  if (!user || !user.account_id) return null;
  try {
    const customer = await Customer.findOne({ account_id: user.account_id }).select("_id").lean();
    return customer ? customer._id : null;
  } catch (_) {
    return null;
  }
}

/**
 * Phase 5A: Read and summarize the customer's current active cart.
 */
async function handleCartSummary(message, user, history) {
  const customerId = await resolveCustomerIdFromUser(user);

  if (!customerId) {
    const botText = "Để xem giỏ hàng, bạn cần đăng nhập tài khoản Kanila nhé. Sau khi đăng nhập, mình có thể hiển thị toàn bộ giỏ hàng và tổng tiền cho bạn!";
    return { botText, cartSummary: null, quickReplies: CART_LOGIN_REQUIRED_QUICK_REPLIES, replyType: "text" };
  }

  let summary;
  try {
    summary = await calculateCartSummary(customerId);
  } catch (err) {
    console.error("[Chatbot] CartSummary error:", err.message);
    return {
      botText: "Mình chưa thể lấy thông tin giỏ hàng lúc này. Bạn vui lòng thử lại sau nhé.",
      cartSummary: null,
      quickReplies: DEFAULT_QUICK_REPLIES,
      replyType: "text",
    };
  }

  if (!summary.found || summary.items_count === 0) {
    let botText;
    try {
      botText = await generateCartSummaryReply(summary, message, history);
    } catch (_) {
      botText = "Giỏ hàng của bạn hiện đang trống. Bạn có muốn mình gợi ý một bộ skincare phù hợp không?";
    }
    return { botText, cartSummary: summary, quickReplies: CART_EMPTY_QUICK_REPLIES, replyType: "cart_summary" };
  }

  let botText;
  try {
    botText = await generateCartSummaryReply(summary, message, history);
  } catch (_) {
    botText = `Giỏ hàng của bạn có ${summary.items_count} sản phẩm, tổng cộng ${summary.total.toLocaleString("vi-VN")}đ. Bạn có muốn thanh toán ngay không?`;
  }

  return {
    botText,
    cartSummary: {
      items_count: summary.items_count,
      subtotal:    summary.subtotal,
      discount:    summary.discount,
      total:       summary.total,
    },
    quickReplies: CART_SUMMARY_QUICK_REPLIES,
    replyType: "cart_summary",
  };
}

/**
 * Phase 5A: Build a product combo recommendation and find upsell products.
 */
async function handleCartRecommendation(message, user, history) {
  // Parse combo type and budget from message
  const { comboType, budgetMax: messageBudget } = parseCartIntent(message);

  // Load customer profile for personalization
  let customerProfile = null;
  if (user && user.account_id) {
    try {
      const customerId = await resolveCustomerIdFromUser(user);
      if (customerId) {
        const ctx = await getCustomerContext(customerId);
        customerProfile = ctx?.customer_profile || null;
      }
    } catch (_) { /* non-fatal */ }
  }

  // Use message budget or profile budget
  const budgetMax = messageBudget || customerProfile?.budget_max || null;

  // Build combo from real MongoDB products
  let comboResult;
  try {
    comboResult = await generateCartRecommendation({
      comboType: comboType === "unknown" ? "skincare_basic" : comboType,
      customerProfile,
      budgetMax,
    });
  } catch (err) {
    console.error("[Chatbot] CartRecommendation error:", err.message);
    return {
      botText: "Mình chưa tìm được bộ sản phẩm phù hợp lúc này. Bạn có thể cho mình biết ngân sách và loại da của bạn để mình tư vấn chính xác hơn không?",
      products: [],
      upsellProducts: [],
      cartSummary: {
        items_count: comboResult?.products?.length || 0,
        subtotal:    comboResult?.totalPrice || 0,
        discount:    0,
        total:       comboResult?.totalPrice || 0,
      },
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
    };
  }

  const products = comboResult.products || [];

  if (products.length === 0) {
    let botText;
    try {
      botText = await generateChatReply(message, history);
    } catch (_) {
      botText = "Mình chưa tìm được bộ sản phẩm phù hợp. Bạn hãy cho mình biết ngân sách và loại da để mình tư vấn chính xác hơn nhé!";
    }
    return {
      botText,
      products: [],
      upsellProducts: [],
      cartSummary: null,
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "text",
    };
  }

  // Find complementary upsell products
  let upsellProducts = [];
  try {
    const existingIds = products.map((p) => p.product_id);
    const existingCats = products.map((p) => p.category_name).filter(Boolean);
    const upsellResult = await findComplementaryProducts({
      existingProductIds: existingIds,
      existingCategories: existingCats,
      customerProfile,
      limit: 2,
    });
    upsellProducts = upsellResult.upsell_products || [];
  } catch (_) { /* non-fatal */ }

  // Gemini explanation
  let botText;
  try {
    botText = await generateCartExplanation(products, upsellProducts, customerProfile, message, history);
  } catch (_) {
    botText = `Mình gợi ý bộ ${comboResult.comboType.replace(/_/g, " ")} gồm ${products.length} sản phẩm, tổng ${comboResult.totalPrice.toLocaleString("vi-VN")}đ. Bạn có muốn thêm vào giỏ không?`;
  }

  return {
    botText,
    products,
    upsellProducts,
    cartSummary: {
      items_count: products.length,
      subtotal:    comboResult.totalPrice,
      discount:    0,
      total:       comboResult.totalPrice,
    },
    quickReplies: CART_RECOMMEND_QUICK_REPLIES,
    replyType: "cart_recommendation",
  };
}

/**
 * Phase 5A/5B: Add recommended products to the authenticated customer's cart.
 *
 * SECURITY:
 *  - Requires authentication (customerId from JWT).
 *  - product_ids re-validated from DB by addProductsToCart() — never trusted from client.
 *  - Never modifies another customer's cart.
 *
 * @param {string}   message
 * @param {object}   user
 * @param {string[]} productIds  — legacy: plain product_id array
 * @param {Array}    history
 * @param {Array}    [cartItems] — Phase 5B: [{product_id, variant_id?, quantity?}]
 */
async function handleAddToCart(message, user, productIds, history, cartItems = null) {
  const customerId = await resolveCustomerIdFromUser(user);

  if (!customerId) {
    return {
      botText: "Để thêm sản phẩm vào giỏ, bạn cần đăng nhập tài khoản Kanila trước nhé!",
      cartAction: { success: false, reason: "login_required" },
      quickReplies: CART_LOGIN_REQUIRED_QUICK_REPLIES,
      replyType: "text",
    };
  }

  const hasCartItems  = Array.isArray(cartItems) && cartItems.length > 0;
  const hasProductIds = Array.isArray(productIds) && productIds.length > 0;

  if (!hasCartItems && !hasProductIds) {
    return {
      botText: "Mình chưa có sản phẩm nào để thêm vào giỏ. Bạn có muốn mình tạo combo sản phẩm phù hợp không?",
      cartAction: { success: false, reason: "no_products" },
      quickReplies: CART_RECOMMEND_QUICK_REPLIES,
      replyType: "text",
    };
  }

  let addResult;
  try {
    addResult = await addProductsToCart({
      customerId,
      productIds: hasCartItems ? undefined : productIds,
      cartItems:  hasCartItems ? cartItems : undefined,
    });
  } catch (err) {
    console.error("[Chatbot] AddToCart error:", err.message);
    return {
      botText: "Xin lỗi, mình không thể thêm sản phẩm vào giỏ lúc này. Bạn vui lòng thử lại sau nhé.",
      cartAction: { success: false, reason: "internal_error" },
      quickReplies: DEFAULT_QUICK_REPLIES,
      replyType: "text",
    };
  }

  // ── Variant selection required ────────────────────────────────────────────────────
  if (addResult.reason === "variant_selection_required") {
    const firstProduct = addResult.needs_variant_selection[0];
    const variantList  = (firstProduct?.variants || [])
      .map((v) => v.volume_ml ? `${v.name} (${v.volume_ml}ml)` : v.name)
      .join(" / ");
    const botText = `Sản phẩm "${firstProduct?.product_name || "này"}" có nhiều phiên bản. Bạn muốn chọn loại nào?${
      variantList ? ` (${variantList})` : ""
    }`;
    return {
      botText,
      cartAction: addResult,
      quickReplies: (firstProduct?.variants || []).map((v) => v.name).slice(0, 5),
      replyType: "cart_action",
    };
  }

  // ── No products / all unavailable ─────────────────────────────────────────────────
  if (addResult.reason === "all_products_unavailable" || addResult.reason === "no_products") {
    return {
      botText: "Một số sản phẩm trong combo hiện không còn phù hợp hoặc đã hết hàng. Bạn có muốn mình tìm sản phẩm thay thế không?",
      cartAction: addResult,
      quickReplies: NO_PRODUCT_QUICK_REPLIES,
      replyType: "cart_action",
    };
  }

  let botText;
  try {
    botText = await generateCartActionConfirmation(addResult, message, history);
  } catch (_) {
    botText = addResult.success
      ? `Mình đã thêm ${addResult.items_added} sản phẩm vào giỏ hàng thành công! Giỏ hàng hiện có ${addResult.cart_count} sản phẩm, tổng ${(addResult.cart_total || 0).toLocaleString("vi-VN")}đ.`
      : "Mình không thể thêm tất cả sản phẩm vào giỏ. Một số sản phẩm có thể đã hết hàng.";
  }

  return {
    botText,
    cartAction: addResult,
    quickReplies: addResult.success ? CART_ADDED_QUICK_REPLIES : NO_PRODUCT_QUICK_REPLIES,
    replyType: "cart_action",
  };
}

// ─────────────────────────────────────────────────────────────────────────────
// ─────────────────────────────────────────────────────────────────────────────
// Phase 8: Makeup Commerce Handlers
// ─────────────────────────────────────────────────────────────────────────────

const INTENT_TO_CATEGORY_HINT = {
  lipstick_recommendation:           ["CAT_LIP_TINT", "CAT_LIPSTICK"],
  cushion_foundation_recommendation: ["CAT_CUSHION", "CAT_FOUNDATION"],
  concealer_recommendation:          ["CAT_CONCEALER"],
  blush_recommendation:              ["CAT_BLUSH"],
  eye_makeup_recommendation:         ["CAT_MASCARA", "CAT_EYELINER", "CAT_EYESHADOW"],
  base_makeup_recommendation:        ["CAT_PRIMER", "CAT_POWDER", "CAT_SETTING_SPRAY"],
  makeup_set_builder:                null,
  event_makeup_look:                 null,
  daily_makeup_look:                 null,
  shade_tone_advice:                 null,
  product_availability:              null,
};

const MAKEUP_RECOMMEND_QUICK_REPLIES = [
  "Thêm vào giỏ hàng",
  "Xem chi tiết sản phẩm",
  "Tìm sản phẩm tương tự",
  "Có voucher makeup không?",
];

const MAKEUP_SET_QUICK_REPLIES = [
  "Thêm toàn bộ vào giỏ",
  "Đổi sản phẩm khác",
  "Tư vấn kỹ hơn về makeup look này"
];

const SHADE_QUICK_REPLIES = [
  "Màu này có hợp da ngăm không?",
  "Có màu nào sáng hơn không?",
  "Thêm vào giỏ hàng"
];

const VOUCHER_QUICK_REPLIES = [
  "Xem tất cả ưu đãi",
  "Mình muốn tìm son đang giảm giá",
  "Cushion bán chạy nhất"
];

async function handleMakeupRecommendation(intent, message, user, history, followUpContext = null) {
  let customerProfile = null;
  try {
    if (user && user.account_id) {
      const ctx = await getCustomerContext({ accountId: user.account_id });
      customerProfile = ctx?.customer_profile || null;
    }
  } catch (err) {}

  let shoppingContext = extractShoppingContext(message, intent);

  // PART 3: Fallback — if categoryNames empty, inject from INTENT_TO_CATEGORY_HINT
  if ((!shoppingContext.categoryNames || shoppingContext.categoryNames.length === 0) && INTENT_TO_CATEGORY_HINT[intent]) {
    const hintCodes = INTENT_TO_CATEGORY_HINT[intent];
    if (hintCodes && hintCodes.length > 0) {
      const fallbackNames = [];
      for (const code of hintCodes) {
        if (CANONICAL_CATEGORY_MAP && CANONICAL_CATEGORY_MAP[code]) {
          fallbackNames.push(...CANONICAL_CATEGORY_MAP[code].names);
        }
      }
      if (fallbackNames.length > 0) {
        shoppingContext.categoryNames = fallbackNames;
        shoppingContext.categoryCode = shoppingContext.categoryCode || hintCodes[0];
      }
    }
  }

  // PART 8: For event_makeup_look — auto-inject occasion if not already set
  if (intent === "event_makeup_look" && !shoppingContext.occasion) {
    const lower = message.toLowerCase();
    // Try to infer occasion from common keywords not caught by parseOccasion
    if (lower.includes("tiệc") || lower.includes("party") || lower.includes("gọp mặt")) {
      shoppingContext.occasion = "party";
    } else if (lower.includes("đám cưới") || lower.includes("wedding")) {
      shoppingContext.occasion = "wedding";
    } else if (lower.includes("đi học") || lower.includes("trường")) {
      shoppingContext.occasion = "school";
    } else if (lower.includes("đi làm") || lower.includes("văn phòng") || lower.includes("công sở")) {
      shoppingContext.occasion = "office";
    } else {
      shoppingContext.occasion = "daily"; // safe default
    }
  }

  // PART 8: For makeup_set_builder — set bundle=true and inject multi-category names
  if (intent === "makeup_set_builder") {
    shoppingContext.bundle = true;
    // If no specific categories, pull all base makeup categories
    if (!shoppingContext.categoryNames || shoppingContext.categoryNames.length === 0) {
      shoppingContext.categoryNames = [
        "Cushion", "Phấn nước", "Kem nền", "Foundation",
        "Son tint", "Son thỏi", "Má hồng", "Phấn phủ",
      ];
    }
  }

  let products = [];
  let filters = null;
  let candidateCount = 0;
  let dbFilter = {};

  if (followUpContext) {
    products = followUpContext.previousProducts || [];
    filters = followUpContext.previousFilters || {};
    shoppingContext.occasion = followUpContext.previousOccasion || shoppingContext.occasion;
    shoppingContext.makeupStyle = followUpContext.previousMakeupStyle || shoppingContext.makeupStyle;
    shoppingContext.isFollowUp = true;
  } else if (intent === "event_makeup_look" || intent === "makeup_set_builder") {
    try {
      const bundleResult = await buildMakeupBundle(shoppingContext.occasion, shoppingContext);
      if (bundleResult && bundleResult.slots.length > 0) {
        products = bundleResult.slots;
        filters = {}; // simplified for bundle
        candidateCount = products.length;
      } else {
        const pipelineResult = await findMakeupProductsPipeline(shoppingContext, 5);
        products = pipelineResult.products || [];
        filters = pipelineResult.filters;
        candidateCount = pipelineResult.candidateCount;
        dbFilter = pipelineResult.dbFilter;
      }
    } catch (err) {
      console.error("[MakeupBundle] error:", err.message);
    }
  } else {
    try {
      const pipelineResult = await findMakeupProductsPipeline(shoppingContext, 5);
      products = pipelineResult.products || [];
      filters = pipelineResult.filters;
      candidateCount = pipelineResult.candidateCount;
      dbFilter = pipelineResult.dbFilter;
    } catch (err) {
      console.error("[MakeupHandler] findMakeupProducts error:", err.message);
    }
  }

  try {
    if (process.env.NODE_ENV === "development" || process.env.CHATBOT_DEBUG === "true") {
      const debugLog = {
        USER: message,
        INTENT: intent,
        FOLLOW_UP: !!followUpContext,
        SHOPPING_CONTEXT: {
          categoryCode: shoppingContext.categoryCode,
          categoryNames: shoppingContext.categoryNames,
          budget: shoppingContext.budget,
          skinType: shoppingContext.skinType,
          occasion: shoppingContext.occasion,
          bundle: shoppingContext.bundle,
        },
        DATABASE_QUERY: dbFilter || {},
        PRODUCT_FOUND: candidateCount || products.length,
        SELECTED_PRODUCTS: products.map(p => ({ name: p.name || p.productName, score: p._score || p.score })),
        GEMINI_CONTEXT_LENGTH: JSON.stringify(products).length,
      };
      console.log("\n[CHATBOT_DEBUG]\n" + JSON.stringify(debugLog, null, 2) + "\n");
    }
  } catch (err) {
    console.error("[MakeupHandler] log error:", err.message);
  }

  let quickReplies = MAKEUP_RECOMMEND_QUICK_REPLIES;
  if (intent === "makeup_set_builder" || intent === "event_makeup_look") {
    quickReplies = MAKEUP_SET_QUICK_REPLIES;
  }

  const supportActions = ["view_product_detail", "add_to_cart"];
  if (filters?.maxPrice) supportActions.push("filter_by_price");

  let botText;
  try {
    const promptMsg = buildMakeupAnalysisPrompt(products, message, filters, customerProfile, shoppingContext.isFollowUp);
    const analysisResult = await generateMakeupReplyWithAnalysis(promptMsg, history);
    
    botText = analysisResult.overview;
    if (analysisResult.followUp) {
      botText += "\n\n" + analysisResult.followUp;
    }

    // Merge analysis into product cards
    if (analysisResult.productAnalysis && products.length > 0) {
      for (let i = 0; i < products.length; i++) {
        // Find corresponding analysis (1-indexed in JSON)
        const pa = analysisResult.productAnalysis.find(a => a.product_index === i + 1);
        if (pa && products[i].recommendation) {
          products[i].recommendation.whyRecommended = pa.why_recommended || products[i].recommendation.whyRecommended;
          products[i].recommendation.strengths = pa.strengths || "";
          products[i].recommendation.bestFor = pa.best_for || "";
          products[i].recommendation.tip = pa.tip || "";
        } else if (pa) {
          products[i].recommendation = {
            whyRecommended: pa.why_recommended || "",
            strengths: pa.strengths || "",
            bestFor: pa.best_for || "",
            tip: pa.tip || ""
          };
        }

        // IMPORTANT: Update flat-level `reason` field with AI analysis
        // so clients that only read the flat "reason" get real AI analysis
        // instead of the scorer's static dataset reason.
        if (pa) {
          const aiReasonParts = [];
          if (pa.why_recommended) aiReasonParts.push(pa.why_recommended);
          if (pa.strengths) aiReasonParts.push(`💪 ${pa.strengths}`);
          if (pa.best_for) aiReasonParts.push(`🎯 ${pa.best_for}`);
          if (pa.tip) aiReasonParts.push(`💡 ${pa.tip}`);
          if (aiReasonParts.length > 0) {
            products[i].reason = aiReasonParts.join("\n\n");
          }
        }
      }
    }
  } catch (err) {
    console.error("[MakeupHandler] analysis error:", err.message);
    if (products.length > 0) {
      const topProduct = products[0];
      botText = `Mình tìm được ${products.length} sản phẩm phù hợp cho bạn! Sản phẩm nổi bật: ${topProduct.name || topProduct.productName} (${topProduct.brand || topProduct.brandName}) - ${(topProduct.price || 0).toLocaleString("vi-VN")}đ.`;
    } else {
      botText = "Mình không tìm thấy sản phẩm phù hợp trong danh mục hiện tại. Bạn có thể thử điều chỉnh bộ lọc nhé!";
    }
  }

  return { botText, products, filters, quickReplies, replyType: "makeup_recommendation", supportActions };
}

// ─────────────────────────────────────────────────────────────────────────────
// Main service function
// ─────────────────────────────────────────────────────────────────────────────

async function handleUserMessage({ sessionId, message, sourceScreen, user, productIds, cartItems }) {
  // 1. Resolve or create session
  const session = await resolveOrCreateSession(sessionId, user, sourceScreen);

  // 1.5 Handle Quick Reply JSON payload
  let parsedQuickReply = null;
  try {
    if (message && message.trim().startsWith("{") && message.trim().endsWith("}")) {
      const payload = JSON.parse(message);
      if (payload.action && payload.action.type) {
        parsedQuickReply = payload;
        message = payload.text || message; // Use text for logging and history
      }
    }
  } catch (e) {
    // Ignore, it's just a regular message containing braces
  }

  // 1.8 Load previous conversation context (if it's a follow-up)
  const { isFollowUp, resolvedContext } = await loadConversationContext(session._id, message.trim());

  // 2. Intent classification
  let classification = { intent: "find_product", needsClarification: false };
  let fallbackIntent = "find_product";
  
  if (parsedQuickReply && parsedQuickReply.action.type === "PRODUCT_SEARCH") {
    // Bypass re-classification
    classification.intent = parsedQuickReply.action.category || "find_product";
    fallbackIntent = classification.intent;
    classification.needsClarification = false;
  } else {
    classification = classifyIntent(message.trim());
    fallbackIntent = detectIntent(message);

    if (isFollowUp) {
      if (classification.intent === "compare_products" || message.toLowerCase().includes("so sánh")) {
        classification.intent = "compare_products";
        fallbackIntent = "product_comparison";
      } else {
        classification.intent = resolvedContext.previousIntent;
        fallbackIntent = resolvedContext.previousIntent;
      }
      classification.needsClarification = false;
    }
  }

  const intent = resolveRoutingIntent(classification.intent, fallbackIntent);

  const shoppingContext = extractShoppingContext(message, intent);

  // PART 6: High-level debug log — visible at start of every request
  if (process.env.NODE_ENV === "development" || process.env.CHATBOT_DEBUG === "true") {
    console.log("\n[CHATBOT_DEBUG]");
    console.log("USER:", JSON.stringify(message));
    console.log("INTENT:", intent);
    console.log("SHOPPING_CONTEXT:", JSON.stringify({
      categoryCode: shoppingContext.categoryCode || "",
      categoryNames: shoppingContext.categoryNames || [],
      budget: shoppingContext.budget || "",
      skinType: shoppingContext.skinType || "",
      occasion: shoppingContext.occasion || "",
    }, null, 2));
  }
  const DIRECT_SEARCH_INTENTS = [
    "cushion_foundation_recommendation",
    "lipstick_recommendation",
    "mascara_recommendation",
    "eye_makeup_recommendation",
    "blush_recommendation",
    "concealer_recommendation",
    "base_makeup_recommendation",
    "makeup_set_builder",
    "event_makeup_look",
    "daily_makeup_look",
    "shade_tone_advice",
    // Classifier-specific intents (from chatbotIntent.classifier.js)
    "find_sale_product",
  ];
  
  let needsClarification = classification.needsClarification;
  if (shoppingContext.categoryNames && shoppingContext.categoryNames.length > 0) needsClarification = false;
  if (shoppingContext.occasion) needsClarification = false;
  if (DIRECT_SEARCH_INTENTS.includes(intent)) needsClarification = false;

  if (needsClarification && classification.clarificationPrompt && !["find_product", "order_tracking", "support_ticket"].includes(intent)) {
    const history = await buildGeminiHistory(session._id);
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "user",
      message_text: message.trim(),
      intent,
      response_type: "text",
    });
    const botText = classification.clarificationPrompt.text;
    const quickReplies = classification.clarificationPrompt.quickReplies;
    await ChatbotMessage.create({
      session_id: session._id,
      sender_type: "bot",
      message_text: botText,
      intent,
      response_type: "text",
      metadata: { reply_type: "text", quick_replies: quickReplies }
    });
    return {
      session_id: session._id,
      bot_message: botText,
      products: [], quick_replies: quickReplies,
      reply_type: "text", handoff_required: false, customer_context_used: false,
    };
  }

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
  if (user && user.account_id &&
    (intent === "product_recommendation" || intent === "beauty_consultation" || intent === "combo_recommendation")
  ) {
    Customer.findOne({ account_id: user.account_id })
      .select("_id")
      .lean()
      .then((customer) => {
        if (customer) return updateCustomerPreference({ customerId: customer._id, message: message.trim() });
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
  // Phase 5A additions
  let cartSummary = null;
  let cartAction = null;
  let upsellProducts = [];
  let needsVariantSelection = false;
  let makeupFilters = null;
  let supportActions = [];
  let comparison = null;

  // Phase 5B: resolve product_ids / cart_items for add_to_cart
  // Android passes product_ids (simple) or cart_items (with variant+qty) when user confirms
  const pendingCartItems  = Array.isArray(cartItems) && cartItems.length > 0 ? cartItems : null;
  const pendingProductIds = pendingCartItems ? null : (Array.isArray(productIds) && productIds.length > 0 ? productIds : []);

  try {
    if (intent === "cart_summary") {
      // Phase 5A
      const r = await handleCartSummary(message.trim(), user, history);
      botText = r.botText;
      cartSummary = r.cartSummary || null;
      quickReplies = r.quickReplies;
      replyType = r.replyType;

    } else if (intent === "cart_recommendation") {
      // Phase 5A
      const r = await handleCartRecommendation(message.trim(), user, history);
      botText = r.botText;
      products = r.products || [];
      upsellProducts = r.upsellProducts || [];
      cartSummary = r.cartSummary || null;
      quickReplies = r.quickReplies;
      replyType = r.replyType;

    } else if (intent === "add_to_cart") {
      // Phase 5B — Android sends product_ids[] or cart_items[] when user confirms
      const r = await handleAddToCart(
        message.trim(),
        user,
        pendingProductIds,
        history,
        pendingCartItems
      );
      botText = r.botText;
      cartAction = r.cartAction || null;
      quickReplies = r.quickReplies;
      replyType = r.replyType;

    } else if (DIRECT_SEARCH_INTENTS.includes(intent) || intent === "product_availability") {
      const r = await handleMakeupRecommendation(intent, message.trim(), user, history, isFollowUp ? resolvedContext : null);
      botText = r.botText;
      products = r.products || [];
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      makeupFilters = r.filters || null;
      supportActions = r.supportActions || [];

    } else if (intent === "voucher_promotion_question") {
      const r = await handleVoucherQuery(message.trim(), history);
      botText = r.botText;
      quickReplies = VOUCHER_QUICK_REPLIES;
      replyType = "text";
      supportActions = ["open_voucher_wallet"];

    } else if (intent === "product_recommendation") {
      // Phase 4A: personalized recommendation
      const r = await handlePersonalizedProductRecommendation(message.trim(), user, history);
      botText = r.botText;
      products = r.products;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      customerContextUsed = r.customerContextUsed;

    } else if (intent === "beauty_consultation") {
      // Phase 5: general beauty advisory using full recommendation engine
      const r = await handleBeautyConsultation(message.trim(), user, history);
      botText = r.botText;
      products = r.products;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      customerContextUsed = r.customerContextUsed;

    } else if (intent === "combo_recommendation") {
      // Phase 5: advisory combo without cart action
      const r = await handleComboRecommendation(message.trim(), user, history);
      botText = r.botText;
      products = r.products;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      customerContextUsed = r.customerContextUsed;

    } else if (intent === "product_comparison") {
      let compProductIds = pendingProductIds || [];
      if (compProductIds.length === 0 && isFollowUp && resolvedContext && resolvedContext.previousProducts) {
        const indices = [];
        const regex = /(?:sản phẩm|cái|số|thứ)\s*(\d+)/gi;
        let match;
        while ((match = regex.exec(message)) !== null) {
          const idx = parseInt(match[1], 10) - 1; // 1-based to 0-indexed
          if (idx >= 0 && idx < resolvedContext.previousProducts.length && !indices.includes(idx)) {
            indices.push(idx);
          }
        }
        
        if (indices.length > 0) {
          compProductIds = indices.map(i => resolvedContext.previousProducts[i].id || resolvedContext.previousProducts[i].product_id || resolvedContext.previousProducts[i]._id);
        } else {
          // Default to first 2 products if no specific numbers mentioned
          compProductIds = resolvedContext.previousProducts.slice(0, 2).map(p => p.id || p.product_id || p._id);
        }
      }
      const r = await handleProductComparison(message.trim(), user, compProductIds, history);
      botText = r.botText;
      quickReplies = r.quickReplies;
      replyType = r.replyType;
      if (r.comparison) {
        comparison = r.comparison;
      }

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
      // Phase 5A
      cart_summary: cartSummary,
      cart_action: cartAction ? { success: cartAction.success, items_added: cartAction.items_added, cart_count: cartAction.cart_count, reason: cartAction.reason } : null,
      // Phase 5B
      needs_variant_selection: cartAction?.needs_variant_selection || null,
      filters: makeupFilters,
      support_actions: supportActions,
    },
  });

  // 8. Update session last intent
  await ChatbotSession.findByIdAndUpdate(session._id, { last_intent: intent });

  // 9. Return full structured result (backward compatible + Phase 5A additions)
  if (process.env.NODE_ENV === "development" || process.env.CHATBOT_DEBUG === "true") {
    console.log("[CHATBOT_DEBUG] FINAL_RESPONSE:", JSON.stringify({
      message: (botText || "").slice(0, 120),
      productsCount: products.length,
      replyType,
    }, null, 2));
  }
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
    // Phase 5A
    cart_summary: cartSummary,
    cart_action: cartAction,
    upsell_products: upsellProducts,
    // Phase 5B: variant selection prompt
    needs_variant_selection: cartAction?.needs_variant_selection || null,
    filters: makeupFilters,
    support_actions: supportActions,
    comparison: comparison,
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
