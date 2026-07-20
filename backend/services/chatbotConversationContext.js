"use strict";

const ChatbotMessage = require("../models/chatbotMessage.model");

const FOLLOW_UP_KEYWORDS = [
  "cái này",
  "sản phẩm này",
  "makeup look này",
  "combo này",
  "tư vấn thêm",
  "chi tiết hơn",
  "kỹ hơn",
  "tư vấn kỹ",
  "về cái đó",
  "giải thích thêm",
  "sản phẩm số",
  "sản phẩm thứ",
  "sản phẩm 1",
  "sản phẩm 2",
  "sản phẩm 3",
  "sản phẩm 4",
  "sản phẩm 5",
  "cái số",
  "cái thứ",
  "so sánh",
];

/**
 * Checks if the user message is a follow-up referring to the previous context.
 * If so, extracts the context from the last bot message.
 *
 * @param {string} sessionId
 * @param {string} message
 * @returns {Promise<{isFollowUp: boolean, resolvedContext: object|null}>}
 */
async function loadConversationContext(sessionId, message) {
  const lowerMsg = message.toLowerCase();
  
  const isFollowUp = FOLLOW_UP_KEYWORDS.some((kw) => lowerMsg.includes(kw));

  if (!isFollowUp) {
    return { isFollowUp: false, resolvedContext: null };
  }

  // Fetch the last bot message for this session
  const lastBotMessage = await ChatbotMessage.findOne({
    session_id: sessionId,
    sender_type: "bot",
  })
    .sort({ created_at: -1 })
    .lean();

  if (!lastBotMessage) {
    return { isFollowUp: false, resolvedContext: null };
  }

  const metadata = lastBotMessage.metadata || {};
  const makeupContext = metadata.makeup_context || {};

  const resolvedContext = {
    previousIntent: lastBotMessage.intent,
    previousProducts: metadata.products || [],
    previousFilters: metadata.filters || null,
    previousOccasion: makeupContext.occasion || null,
    previousMakeupStyle: makeupContext.makeup_style || null,
  };

  // Ensure we have some valid context to actually follow up on
  if (!resolvedContext.previousIntent || resolvedContext.previousIntent === "general_chat") {
    return { isFollowUp: false, resolvedContext: null };
  }

  return { isFollowUp: true, resolvedContext };
}

module.exports = {
  loadConversationContext,
};
