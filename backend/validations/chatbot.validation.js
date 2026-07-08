const mongoose = require("mongoose");

/**
 * Validate POST /api/chatbot/message request body.
 * @param {object} data — req.body
 * @returns {string[]} — array of error strings; empty array means valid
 */
function validateSendMessage(data) {
  const errors = [];

  // message: required, non-empty after trim, max 2000 chars
  if (!data.message || typeof data.message !== "string") {
    errors.push("message is required and must be a string.");
  } else if (data.message.trim().length === 0) {
    errors.push("message must not be empty.");
  } else if (data.message.trim().length > 2000) {
    errors.push("message must not exceed 2000 characters.");
  }

  // session_id: optional, but must be a valid ObjectId if provided
  if (data.session_id !== undefined && data.session_id !== null && data.session_id !== "") {
    if (!mongoose.Types.ObjectId.isValid(data.session_id)) {
      errors.push("session_id must be a valid MongoDB ObjectId when provided.");
    }
  }

  // source_screen: optional string
  if (data.source_screen !== undefined && data.source_screen !== null) {
    if (typeof data.source_screen !== "string") {
      errors.push("source_screen must be a string when provided.");
    }
  }

  // context: optional object (not array)
  if (data.context !== undefined && data.context !== null) {
    if (typeof data.context !== "object" || Array.isArray(data.context)) {
      errors.push("context must be a plain object when provided.");
    }
  }

  return errors;
}

module.exports = { validateSendMessage };
