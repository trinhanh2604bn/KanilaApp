const express = require("express");
const router = express.Router();

const optionalAuth = require("../middlewares/optionalAuth.middleware");
const { sendMessage, getSessionMessages } = require("../controllers/chatbot.controller");

/**
 * POST /message
 * Final path: POST /api/chatbot/message
 *
 * Accepts both guest and authenticated requests.
 * optionalAuth populates req.user if a valid Bearer token is provided;
 * unauthenticated requests proceed normally as guests.
 */
router.post("/message", optionalAuth, sendMessage);

/**
 * GET /sessions/:sessionId/messages
 * Final path: GET /api/chatbot/sessions/:sessionId/messages
 *
 * Returns all messages for a session ordered by created_at ascending.
 */
router.get("/sessions/:sessionId/messages", optionalAuth, getSessionMessages);

module.exports = router;
