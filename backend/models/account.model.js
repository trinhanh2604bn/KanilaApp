const mongoose = require("mongoose");

/**
 * Logical match to target `accounts` table.
 * Primary key: MongoDB `_id` (maps to account_id in relational terms).
 *
 * Authentication: Passwordless — email OTP / magic link only.
 * No password_hash is stored. No phone_verified_at.
 * Phone remains as an optional contact/shipping field only.
 */
const accountSchema = new mongoose.Schema(
  {
    account_type: {
      type: String,
      required: [true, "Account type is required"],
      enum: ["customer", "admin", "staff"],
      default: "customer",
    },
    email: {
      type: String,
      required: [true, "Email is required"],
      trim: true,
      lowercase: true,
    },
    /**
     * Optional contact phone. NOT used for authentication or verification.
     * Phone verification has been removed from the auth model.
     */
    phone: {
      type: String,
      default: null,
      trim: true,
    },
    username: {
      type: String,
      trim: true,
      sparse: true,
    },
    account_status: {
      type: String,
      enum: ["active", "inactive", "locked"],
      default: "active",
    },
    /** Set to a Date when email ownership is confirmed via OTP/magic-link. */
    email_verified_at: {
      type: Date,
      default: null,
    },
    last_login_at: {
      type: Date,
      default: null,
    },
    /**
     * Counts consecutive failed OTP verification attempts.
     * Used for rate-limiting and temporary account locking.
     */
    failed_login_count: {
      type: Number,
      default: 0,
    },
    /** When set (and in the future), the account is temporarily locked. */
    locked_until: {
      type: Date,
      default: null,
    },
  },
  {
    timestamps: { createdAt: "created_at", updatedAt: "updated_at" },
    collection: "accounts",
  }
);

// Unique indexes — declared once via schema.index() to avoid Mongoose duplicate-index warnings.
accountSchema.index({ email: 1 }, { unique: true });
accountSchema.index({ username: 1 }, { unique: true, sparse: true });
// Phone is unique only when present and non-empty (partial filter).
// Accounts with phone = null or "" are excluded — multiple no-phone accounts are allowed.
accountSchema.index(
  { phone: 1 },
  {
    unique: true,
    partialFilterExpression: { phone: { $exists: true, $gt: "" } },
  }
);

/** Expose account_id alongside _id for API symmetry with target schema */
accountSchema.virtual("account_id").get(function () {
  return this._id;
});
accountSchema.set("toJSON", { virtuals: true });
accountSchema.set("toObject", { virtuals: true });

module.exports = mongoose.model("Account", accountSchema);
