const mongoose = require("mongoose");

/**
 * ChatbotSession — tracks each AI chat session per user or guest.
 * A session groups related messages together and stores context like
 * the entry source screen and the last detected intent.
 */
const chatbotSessionSchema = new mongoose.Schema(
  {
    // Authenticated user references (both nullable for guest sessions)
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      default: null,
    },
    account_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Account",
      default: null,
    },
    // Anonymous/guest identifier — populated when no auth token is present
    guest_session_id: {
      type: String,
      default: null,
      trim: true,
    },
    status: {
      type: String,
      enum: ["active", "closed"],
      default: "active",
    },
    // Which screen the user opened the chatbot from (e.g. "home", "product_detail")
    source_screen: {
      type: String,
      default: "unknown",
      trim: true,
    },
    // Last detected intent from rule-based detection
    last_intent: {
      type: String,
      default: "general_chat",
      trim: true,
    },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "chatbot_sessions",
  }
);

// Expose session_id alias alongside _id for API symmetry
chatbotSessionSchema.virtual("session_id").get(function () {
  return this._id;
});
chatbotSessionSchema.set("toJSON", { virtuals: true });
chatbotSessionSchema.set("toObject", { virtuals: true });

module.exports = mongoose.model("ChatbotSession", chatbotSessionSchema);
