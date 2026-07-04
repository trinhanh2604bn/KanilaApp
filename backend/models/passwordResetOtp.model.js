/**
 * @deprecated PasswordResetOtp is deprecated and removed from Kanila.
 *
 * The password reset flow no longer exists. Authentication is now passwordless
 * via email OTP using the `EmailOtp` model (models/emailOtp.model.js).
 *
 * This file is a backward-compatibility shim only. It re-exports `EmailOtp`
 * under the old name so that any legacy `require("./passwordResetOtp.model")`
 * will not crash immediately. Update callers to use `EmailOtp` directly.
 *
 * COLLECTION CHANGE: The active collection is now `email_otps`.
 * The old `password_reset_otps` collection is no longer written to.
 */

// eslint-disable-next-line no-console
console.warn(
  "[DEPRECATED] passwordResetOtp.model.js: This model is deprecated. " +
    "Import EmailOtp from models/emailOtp.model.js instead. " +
    "The password reset flow has been replaced by passwordless email OTP authentication."
);

const EmailOtp = require("./emailOtp.model");

module.exports = EmailOtp;
