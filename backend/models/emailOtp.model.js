const mongoose = require("mongoose");

const emailOtpSchema = new mongoose.Schema(
  {
    email: { type: String, required: true, index: true, lowercase: true, trim: true },
    purpose: { type: String, required: true, enum: ['login', 'email_verification'], index: true },
    otp_code_hash: { type: String, required: true },
    expires_at: { type: Date, required: true, index: true },
    attempt_count: { type: Number, default: 0 },
    account_id: { type: mongoose.Schema.Types.ObjectId, ref: "Account", default: null, index: true },
    consumed_at: { type: Date, default: null, index: true },
  },
  { 
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "email_otps"
  }
);

// Index for efficiently finding valid OTPs
emailOtpSchema.index({ email: 1, purpose: 1, consumed_at: 1, expires_at: 1 });
// Index for checking recent OTPs via created_at (useful for rate limiting logic)
emailOtpSchema.index({ email: 1, purpose: 1, created_at: -1 });

module.exports = mongoose.model("EmailOtp", emailOtpSchema);
