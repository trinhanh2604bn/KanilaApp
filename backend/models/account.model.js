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
    /**
     * Optional if phone registration is used.
     * Trimmed, lowercased, and unique sparse.
     */
    email: {
      type: String,
      trim: true,
      lowercase: true,
    },
    /**
     * Optional if email registration is used.
     * Trimmed, unique sparse.
     */
    phone: {
      type: String,
      trim: true,
      set: function(v) {
        if (!v) return v;
        // Strip everything except digits and +
        let digits = String(v).replace(/[^\d+]/g, "");
        // Ensure VN E.164 format
        if (digits.startsWith("0")) {
          digits = "+84" + digits.substring(1);
        } else if (digits.length >= 9 && !digits.startsWith("+")) {
          digits = "+84" + digits;
        }
        return digits;
      }
    },
    registration_channel: {
      type: String,
      enum: ["email", "phone", "social"],
      required: true,
      default: "email",
    },
    username: {
      type: String,
      trim: true,
      sparse: true,
    },
    account_status: {
      type: String,
      enum: ["active", "inactive", "locked", "pending"],
      default: "active",
    },
    password_hash: {
      type: String,
      select: false,
      default: null,
    },
    /** Set to a Date when email ownership is confirmed via OTP/magic-link. */
    email_verified_at: {
      type: Date,
      default: null,
    },
    /** Set to a Date when phone ownership is confirmed via SMS OTP. */
    phone_verified_at: {
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

// Validation: account must have at least one of email or phone.
accountSchema.pre("validate", function (next) {
  if (!this.email && !this.phone) {
    next(new Error("Account must have at least one of email or phone."));
  } else {
    next();
  }
});

// Unique indexes with partial filter to exclude null/empty values
accountSchema.index(
  { email: 1 },
  {
    unique: true,
    partialFilterExpression: { email: { $type: "string" } },
  }
);
accountSchema.index(
  { phone: 1 },
  {
    unique: true,
    partialFilterExpression: { phone: { $type: "string" } },
  }
);
accountSchema.index(
  { username: 1 },
  {
    unique: true,
    partialFilterExpression: { username: { $type: "string" } },
  }
);

/** Expose account_id alongside _id for API symmetry with target schema */
accountSchema.virtual("account_id").get(function () {
  return this._id;
});
accountSchema.set("toJSON", { virtuals: true });
accountSchema.set("toObject", { virtuals: true });

module.exports = mongoose.model("Account", accountSchema);
