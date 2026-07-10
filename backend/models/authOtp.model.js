const mongoose = require("mongoose");

const authOtpSchema = new mongoose.Schema(
  {
    account_id: { type: mongoose.Schema.Types.ObjectId, ref: "Account", default: null, index: true },
    target_type: { type: String, required: true, enum: ["email", "phone"], index: true },
    target_value: { type: String, required: true, index: true, lowercase: true, trim: true },
    purpose: {
      type: String,
      required: true,
      // "login" is intentionally removed — login now uses password authentication.
      // Only register and reset_password flows use OTP.
      enum: ["register", "reset_password", "change_email", "change_phone"],
      index: true
    },
    otp_hash: { type: String, required: true },
    magic_token_hash: { type: String, default: null },
    expires_at: { type: Date, required: true, index: true },
    consumed_at: { type: Date, default: null, index: true },
    attempt_count: { type: Number, default: 0 },
    max_attempts: { type: Number, default: 5 },
    request_ip: { type: String },
    user_agent: { type: String },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "auth_otps"
  }
);

// TTL index to automatically delete expired OTPs (optional, but requested implicitly by "OTP expires")
// We already have expires_at index, we can make it a TTL index.
authOtpSchema.index({ expires_at: 1 }, { expireAfterSeconds: 0 });

// Index for efficiently finding valid OTPs
authOtpSchema.index({ target_type: 1, target_value: 1, purpose: 1, consumed_at: 1, expires_at: 1 });

module.exports = mongoose.model("AuthOtp", authOtpSchema);
