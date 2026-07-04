const express = require("express");
const router = express.Router();
const {
  register,
  login,
  verifyOtp,
  getMe,
  checkEmail,
  verifyResetOtp,
  resetPassword,
} = require("../controllers/auth.controller");
const authMiddleware = require("../middlewares/auth.middleware");

// ─── Active endpoints ─────────────────────────────────────────────────────────

/** Register a new account (email + name, no password) */
router.post("/register", register);

/** Initiate passwordless login — sends an OTP to the given email */
router.post("/login", login);

/** Verify an OTP (purpose: "email_verification" | "login") → returns JWT */
router.post("/verify-otp", verifyOtp);

/** Get the currently authenticated user (requires Bearer JWT) */
router.get("/me", authMiddleware, getMe);

// ─── Deprecated endpoints (410 Gone) ─────────────────────────────────────────
// These were part of the old password-based auth flow and have been removed.
// Returning 410 instead of 404 signals to clients that the resource is gone intentionally.

router.post("/check-email", checkEmail);
router.post("/verify-reset-otp", verifyResetOtp);
router.post("/reset-password", resetPassword);

module.exports = router;
