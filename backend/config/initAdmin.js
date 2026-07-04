/**
 * Auto-initializer: ensures a default admin account exists.
 * Called once at server startup. Idempotent — skips if account already exists.
 *
 * NOTE: Passwordless system — no password_hash is created or stored.
 * Admins log in via email OTP (POST /api/auth/login → POST /api/auth/verify-otp).
 */
const Account = require("../models/account.model");

const ADMIN_EMAIL = "ngan@gmail.com";

const ensureAdminAccount = async () => {
  try {
    let existing = await Account.findOne({ email: ADMIN_EMAIL });
    if (existing) {
      let updated = false;
      if (existing.account_type !== "admin") {
        existing.account_type = "admin";
        updated = true;
      }
      if (existing.account_status !== "active") {
        existing.account_status = "active";
        updated = true;
      }
      if (!existing.email_verified_at) {
        existing.email_verified_at = new Date();
        updated = true;
      }
      if (updated) {
        await existing.save();
        console.log(`[init] Admin account updated: ${existing.email}`);
      } else {
        console.log(`[init] Admin account already exists: ${existing.email}`);
      }
      return;
    }

    await Account.create({
      email: ADMIN_EMAIL,
      account_type: "admin",
      account_status: "active",
      username: "Kanila Admin",
      email_verified_at: new Date(),
    });

    console.log(`[init] Admin account created: ${ADMIN_EMAIL}`);
  } catch (error) {
    console.error("[init] Failed to create admin account:", error.message);
  }
};

module.exports = ensureAdminAccount;
