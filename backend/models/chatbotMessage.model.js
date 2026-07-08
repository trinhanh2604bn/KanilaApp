const mongoose = require("mongoose");

/**
 * ChatbotMessage — stores individual messages within a chatbot session.
 * Records both user messages and bot/agent responses.
 */
const chatbotMessageSchema = new mongoose.Schema(
  {
    session_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "ChatbotSession",
      required: [true, "Session ID is required"],
    },
    // Who sent this message
    sender_type: {
      type: String,
      enum: ["user", "bot", "agent"],
      required: [true, "Sender type is required"],
    },
    message_text: {
      type: String,
      required: [true, "Message text is required"],
      trim: true,
    },
    // Detected or assigned intent for this message
    intent: {
      type: String,
      default: "general_chat",
      trim: true,
    },
    // Type of response (used by Android to render appropriate UI)
    response_type: {
      type: String,
      enum: ["text", "quick_reply", "error"],
      default: "text",
    },
    // Flexible metadata (e.g. quick_replies list, handoff flag)
    metadata: {
      type: mongoose.Schema.Types.Mixed,
      default: {},
    },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "chatbot_messages",
  }
);

// Index for efficient history lookup (ascending for conversation order)
chatbotMessageSchema.index({ session_id: 1, created_at: 1 });

module.exports = mongoose.model("ChatbotMessage", chatbotMessageSchema);
