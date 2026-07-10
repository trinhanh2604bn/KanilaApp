/**
 * chatbot.controller.js
 * Thin controller — handles HTTP request/response only.
 * All business logic lives in chatbot.service.js.
 */

const mongoose = require("mongoose");
const chatbotService = require("../services/chatbot.service");
const { validateSendMessage } = require("../validations/chatbot.validation");

// ─────────────────────────────────────────────────────────────────────────────
// Safe error helpers — never expose internal details to API clients
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Return a generic, client-safe error detail string for known error codes.
 * Raw err.message is intentionally NOT forwarded to prevent leaking internals.
 * @param {string} code
 * @returns {string}
 */
function safeErrorDetails(code) {
  switch (code) {
    case "CHATBOT_CONFIG_ERROR":
      return "The AI assistant is currently unavailable. Please try again later.";
    case "CHATBOT_TIMEOUT":
      return "The AI assistant took too long to respond. Please try again.";
    case "CHATBOT_ERROR":
      return "The AI assistant encountered an error. Please try again.";
    case "SESSION_NOT_FOUND":
      return "The requested chat session was not found.";
    case "SESSION_ACCESS_DENIED":
      return "You do not have permission to access this chat session.";
    default:
      return "An unexpected error occurred. Please try again.";
  }
}

/**
 * Map a service/provider error code to an HTTP status code.
 * @param {string|undefined} code
 * @returns {number}
 */
function errorStatusCode(code) {
  switch (code) {
    case "CHATBOT_CONFIG_ERROR":
      return 503; // Service unavailable — misconfiguration
    case "CHATBOT_TIMEOUT":
      return 504; // Gateway timeout
    case "SESSION_NOT_FOUND":
      return 404;
    case "SESSION_ACCESS_DENIED":
      return 403;
    default:
      return 500;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/chatbot/message
// ─────────────────────────────────────────────────────────────────────────────

const sendMessage = async (req, res) => {
  // 1. Validate request body
  const validationErrors = validateSendMessage(req.body);
  if (validationErrors.length > 0) {
    return res.status(400).json({
      success: false,
      message: "Validation error",
      data: null,
      error: {
        code: "VALIDATION_ERROR",
        details: validationErrors.join(" "),
      },
    });
  }

  const { session_id, message, source_screen, product_ids, cart_items } = req.body;

  console.log("\n[CHATBOT_DEBUG]");
  console.log("USER:");
  console.log(`"${message}"\n`);

  try {
    // 2. Delegate to service — passes req.user (may be undefined for guests)
    // Phase 5B: also forward product_ids (legacy) / cart_items (variant-explicit) for add_to_cart
    // Security: only pass safe fields from cart_items, never price/discount etc.
    const sanitizedCartItems = Array.isArray(cart_items)
      ? cart_items.map((ci) => ({
          product_id: String(ci.product_id || ""),
          variant_id: ci.variant_id ? String(ci.variant_id) : null,
          quantity:   Math.max(1, parseInt(ci.quantity, 10) || 1),
        }))
      : null;

    const result = await chatbotService.handleUserMessage({
      sessionId:    session_id || null,
      message,
      sourceScreen: source_screen,
      user:         req.user, // populated by optionalAuth if token was provided
      productIds:   Array.isArray(product_ids) ? product_ids.map(String) : undefined,
      cartItems:    sanitizedCartItems || undefined,
    });

    console.log("GEMINI RESPONSE:\n" + (result.bot_message || "") + "\n");
    console.log("FINAL RESPONSE:\n" + JSON.stringify(result, null, 2) + "\n");

    return res.status(200).json({
      success: true,
      message: "Chatbot replied successfully",
      data: result,
      error: null,
    });
  } catch (err) {
    // Map structured error codes to appropriate responses
    const code = err.code || "CHATBOT_ERROR";
    const status = errorStatusCode(code);

    // Log technical details server-side only — never forward to client
    console.error(`[Chatbot] sendMessage error [${code}]:`, err.message);

    return res.status(status).json({
      success: false,
      message: "Unable to process chatbot message",
      data: null,
      error: {
        code,
        details: safeErrorDetails(code),
      },
    });
  }
};

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/chatbot/sessions/:sessionId/messages
// ─────────────────────────────────────────────────────────────────────────────

const getSessionMessages = async (req, res) => {
  const { sessionId } = req.params;

  // 1. Validate sessionId format before hitting Mongoose
  if (!mongoose.Types.ObjectId.isValid(sessionId)) {
    return res.status(400).json({
      success: false,
      message: "Invalid session ID",
      data: null,
      error: {
        code: "INVALID_SESSION_ID",
        details: "session_id must be a valid MongoDB ObjectId.",
      },
    });
  }

  try {
    // 2. Fetch session — returns null if not found, or { session, messages }
    const result = await chatbotService.getSessionMessages(sessionId);

    if (!result) {
      return res.status(404).json({
        success: false,
        message: "Chatbot session not found",
        data: null,
        error: {
          code: "SESSION_NOT_FOUND",
          details: safeErrorDetails("SESSION_NOT_FOUND"),
        },
      });
    }

    // 3. Session ownership check
    const session = result.session;
    if (session.account_id) {
      // Session belongs to an authenticated account
      if (!req.user) {
        // No token provided — block guest access to an owned session
        return res.status(403).json({
          success: false,
          message: "Access denied",
          data: null,
          error: {
            code: "SESSION_ACCESS_DENIED",
            details: safeErrorDetails("SESSION_ACCESS_DENIED"),
          },
        });
      }
      // Token provided — verify it matches the session owner
      const sessionAccountId = session.account_id.toString();
      const requestAccountId = (req.user.account_id || "").toString();
      if (sessionAccountId !== requestAccountId) {
        return res.status(403).json({
          success: false,
          message: "Access denied",
          data: null,
          error: {
            code: "SESSION_ACCESS_DENIED",
            details: safeErrorDetails("SESSION_ACCESS_DENIED"),
          },
        });
      }
    }
    // Guest sessions (account_id is null) — allow access by session_id in Phase 1

    return res.status(200).json({
      success: true,
      message: "Fetched session messages successfully",
      data: {
        session,
        messages: result.messages,
      },
      error: null,
    });
  } catch (err) {
    console.error("[Chatbot] getSessionMessages error:", err.message);
    return res.status(500).json({
      success: false,
      message: "Unable to fetch session messages",
      data: null,
      error: {
        code: "CHATBOT_ERROR",
        details: safeErrorDetails("CHATBOT_ERROR"),
      },
    });
  }
};

module.exports = { sendMessage, getSessionMessages };
