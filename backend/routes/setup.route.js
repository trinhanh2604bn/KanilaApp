const express = require("express");
const router = express.Router();
const Account = require("../models/account.model");

/**
 * GET /api/setup/admin
 * Creates the default admin account. Idempotent.
 * Visit http://localhost:5000/api/setup/admin in your browser.
 *
 * NOTE: Passwordless system — no password is created or stored.
 * Admins log in via email OTP (POST /api/auth/login → POST /api/auth/verify-otp).
 */
router.get("/admin", async (req, res) => {
  try {
    const email = "admin@kanila.com";

    const existing = await Account.findOne({ email });
    if (existing) {
      // Ensure it's admin + active
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
      if (updated) await existing.save();

      return res.json({
        success: true,
        message: "Admin account already exists (verified admin + active)",
        data: {
          email,
          account_type: "admin",
          login: "Use POST /api/auth/login with this email, then verify OTP",
        },
      });
    }

    const account = await Account.create({
      email,
      account_type: "admin",
      account_status: "active",
      username: "Kanila Admin",
      email_verified_at: new Date(),
    });

    res.json({
      success: true,
      message: "Admin account created! Login via email OTP.",
      data: {
        _id: account._id,
        email,
        account_type: "admin",
        loginStep1: "POST /api/auth/login { email }",
        loginStep2: "POST /api/auth/verify-otp { email, otp, purpose: 'login' }",
      },
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: error.message,
    });
  }
});

module.exports = router;
