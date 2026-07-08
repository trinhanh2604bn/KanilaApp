/**
 * supportTicket.model.js
 * Support ticket created by customers via chatbot or support portal.
 * Collection: support_tickets
 */

const mongoose = require("mongoose");

const supportTicketSchema = new mongoose.Schema(
  {
    /** Auto-generated human-readable code, e.g. SPT20260708001 */
    ticket_code: {
      type: String,
      required: true,
      unique: true,
      uppercase: true,
      trim: true,
    },
    /** Authenticated account, null for potential future guest tickets */
    account_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Account",
      default: null,
      index: true,
    },
    /** Resolved customer profile _id (if available) */
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      default: null,
      index: true,
    },
    /** Chatbot session that spawned this ticket */
    session_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "ChatbotSession",
      default: null,
    },
    /** Related order (optional) */
    related_order_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Order",
      default: null,
    },
    category: {
      type: String,
      enum: [
        "return_exchange",
        "refund",
        "wrong_or_missing_item",
        "product_issue",
        "shipping_issue",
        "consultation",
        "general_support",
      ],
      default: "general_support",
    },
    subject: {
      type: String,
      default: "",
      trim: true,
    },
    description: {
      type: String,
      default: "",
      trim: true,
    },
    status: {
      type: String,
      enum: ["open", "in_progress", "resolved", "closed"],
      default: "open",
    },
    priority: {
      type: String,
      enum: ["low", "normal", "high"],
      default: "normal",
    },
    /** How the ticket was created */
    source: {
      type: String,
      enum: ["chatbot", "web", "app", "phone", "email"],
      default: "chatbot",
    },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "support_tickets",
  }
);

supportTicketSchema.virtual("ticket_id").get(function () {
  return this._id;
});
supportTicketSchema.set("toJSON", { virtuals: true });
supportTicketSchema.set("toObject", { virtuals: true });

supportTicketSchema.index({ account_id: 1, created_at: -1 });
supportTicketSchema.index({ status: 1, created_at: -1 });

module.exports = mongoose.model("SupportTicket", supportTicketSchema);
