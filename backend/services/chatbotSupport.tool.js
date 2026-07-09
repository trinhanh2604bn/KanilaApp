/**
 * chatbotSupport.tool.js
 * Support ticket creation and category detection for the Kanila chatbot.
 *
 * Backend owns all ticket creation logic.
 * Gemini must NOT decide whether to create a ticket.
 */

const crypto = require("crypto");
const SupportTicket = require("../models/supportTicket.model");
const Customer = require("../models/customer.model");

// ─────────────────────────────────────────────────────────────────────────────
// Category detection
// ─────────────────────────────────────────────────────────────────────────────

const CATEGORY_RULES = [
  { keywords: ["đổi trả", "trả hàng", "return", "đổi hàng", "exchange"], category: "return_exchange" },
  { keywords: ["hoàn tiền", "refund", "hoàn lại tiền"], category: "refund" },
  { keywords: ["sai hàng", "thiếu hàng", "wrong", "missing", "giao nhầm", "không đủ"], category: "wrong_or_missing_item" },
  { keywords: ["lỗi sản phẩm", "hỏng", "vỡ", "defect", "damaged", "sản phẩm lỗi"], category: "product_issue" },
  { keywords: ["giao hàng", "vận chuyển", "shipper", "shipping", "chưa nhận được"], category: "shipping_issue" },
  { keywords: ["tư vấn", "hỏi thêm", "cần hỗ trợ thêm", "consultation"], category: "consultation" },
];

/**
 * Detect support ticket category from user message.
 * @param {string} message
 * @returns {string} — one of the category enum values
 */
function detectTicketCategory(message) {
  const lower = message.toLowerCase();
  for (const rule of CATEGORY_RULES) {
    if (rule.keywords.some((kw) => lower.includes(kw))) {
      return rule.category;
    }
  }
  return "general_support";
}

// ─────────────────────────────────────────────────────────────────────────────
// Category & status labels (Vietnamese)
// ─────────────────────────────────────────────────────────────────────────────

const CATEGORY_LABELS = {
  return_exchange:       "Đổi trả sản phẩm",
  refund:                "Hoàn tiền",
  wrong_or_missing_item: "Sai hàng / Thiếu hàng",
  product_issue:         "Lỗi sản phẩm",
  shipping_issue:        "Vấn đề giao hàng",
  consultation:          "Tư vấn thêm",
  general_support:       "Hỗ trợ chung",
};

const STATUS_LABELS = {
  open:        "Đã tiếp nhận",
  in_progress: "Đang xử lý",
  resolved:    "Đã giải quyết",
  closed:      "Đã đóng",
};

// ─────────────────────────────────────────────────────────────────────────────
// Ticket code generator
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Generate a unique ticket code: SPT + YYYYMMDD + 4-char random hex.
 * @returns {string}
 */
function generateTicketCode() {
  const now = new Date();
  const date = [
    now.getFullYear(),
    String(now.getMonth() + 1).padStart(2, "0"),
    String(now.getDate()).padStart(2, "0"),
  ].join("");
  const rand = crypto.randomBytes(2).toString("hex").toUpperCase();
  return `SPT${date}${rand}`;
}

// ─────────────────────────────────────────────────────────────────────────────
// Main support ticket function
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Create a support ticket for an authenticated user, or return handoff
 * instructions for guests (Phase 3A: guest tickets are deferred to a later phase).
 *
 * @param {object} params
 * @param {string|null} params.accountId  — from req.user.account_id
 * @param {string} params.message         — raw user message
 * @param {string} params.sessionId       — chatbot session _id
 * @returns {Promise<{
 *   ticket: object|null,
 *   loginRequired: boolean,
 *   error: string|null
 * }>}
 */
async function createSupportTicket({ accountId, message, sessionId }) {
  // Phase 3A: guest users must log in first
  if (!accountId) {
    return { ticket: null, loginRequired: true, error: null };
  }

  // Resolve customer_id (non-fatal)
  let customerId = null;
  try {
    const customer = await Customer.findOne({ account_id: accountId })
      .select("_id")
      .lean();
    if (customer) customerId = customer._id;
  } catch (_) {
    // Proceed without customerId; ticket is still created with accountId
  }

  const category = detectTicketCategory(message);
  const ticketCode = generateTicketCode();
  const subject = buildSubject(category, message);
  const description = message.trim().slice(0, 1000); // cap at 1000 chars

  // Determine priority from category
  const highPriority = ["wrong_or_missing_item", "product_issue", "refund"];
  const priority = highPriority.includes(category) ? "high" : "normal";

  let ticket;
  try {
    ticket = await SupportTicket.create({
      ticket_code:    ticketCode,
      account_id:     accountId,
      customer_id:    customerId,
      session_id:     sessionId,
      category,
      subject,
      description,
      status:         "open",
      priority,
      source:         "chatbot",
    });
  } catch (err) {
    console.error("[ChatbotSupport] Ticket creation failed:", err.message);
    return { ticket: null, loginRequired: false, error: "ticket_creation_failed" };
  }

  // Return safe normalized ticket object
  const normalized = {
    ticket_id:      ticket._id.toString(),
    ticket_code:    ticket.ticket_code,
    status:         ticket.status,
    status_label:   STATUS_LABELS[ticket.status] || "Đã tiếp nhận",
    category:       ticket.category,
    category_label: CATEGORY_LABELS[ticket.category] || ticket.category,
    priority:       ticket.priority,
    created_at:     ticket.created_at,
    message:        "Kanila đã ghi nhận yêu cầu hỗ trợ của bạn. Đội ngũ sẽ liên hệ trong thời gian sớm nhất.",
  };

  return { ticket: normalized, loginRequired: false, error: null };
}

/**
 * Build a short ticket subject from category and message.
 * @param {string} category
 * @param {string} message
 * @returns {string}
 */
function buildSubject(category, message) {
  const labelPart = CATEGORY_LABELS[category] || "Yêu cầu hỗ trợ";
  // Use first 80 chars of message as a description hint
  const hint = message.trim().slice(0, 80);
  return `[${labelPart}] ${hint}`;
}

module.exports = { createSupportTicket, detectTicketCategory, CATEGORY_LABELS, STATUS_LABELS };
