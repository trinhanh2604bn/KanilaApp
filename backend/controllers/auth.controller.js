/**
 * auth.controller.js
 *
 * Passwordless email-OTP authentication for Kanila.
 *
 * Flows:
 *   1. Register  → POST /api/auth/register    (email + name, no password)
 *   2. Login     → POST /api/auth/login        (email only → sends OTP)
 *   3. Verify    → POST /api/auth/verify-otp   (email + otp + purpose → returns JWT)
 *   4. Me        → GET  /api/auth/me            (protected)
 *
 * Deprecated (returns 410 Gone):
 *   POST /api/auth/check-email
 *   POST /api/auth/verify-reset-otp
 *   POST /api/auth/reset-password
 */

const jwt = require("jsonwebtoken");
const nodemailer = require("nodemailer");
const Account = require("../models/account.model");
const Customer = require("../models/customer.model");
const EmailOtp = require("../models/emailOtp.model");
const { generateSixDigitOtp, sha256Hex } = require("../utils/otp");

// ─── Constants ────────────────────────────────────────────────────────────────

/** Dev-only shortcut OTP. Set AUTH_DEBUG_OTP in .env for local testing. */
const AUTH_DEBUG_OTP = process.env.AUTH_DEBUG_OTP || process.env.PASSWORD_RESET_DEBUG_OTP;

const OTP_EXPIRY_LOGIN_MS = 10 * 60 * 1000;          // 10 minutes
const OTP_EXPIRY_VERIFY_MS = 24 * 60 * 60 * 1000;    // 24 hours
const OTP_MAX_ATTEMPTS = 5;
const OTP_RESEND_COOLDOWN_MS = 60 * 1000;             // 60 seconds between new OTPs

// ─── Helpers ──────────────────────────────────────────────────────────────────

/** Generate a simple customer code like CUS0001 */
const generateCustomerCode = async () => {
  const count = await Customer.countDocuments();
  const nextNum = count + 1;
  return `CUS${String(nextNum).padStart(4, "0")}`;
};

/**
 * Send an email OTP via SMTP.
 * Falls back to console.warn in dev mode when SMTP is not configured.
 */
async function sendOtpEmail(toEmail, otp, purpose) {
  const smtpHost = process.env.SMTP_HOST;
  const smtpPort = Number(process.env.SMTP_PORT || 587);
  const smtpUser = process.env.SMTP_USER;
  const smtpPass = process.env.SMTP_PASS;
  const smtpFrom = process.env.SMTP_FROM || "no-reply@kanila.vn";

  const subjects = {
    email_verification: "Kanila — Xác thực địa chỉ email của bạn",
    login: "Kanila — Mã đăng nhập của bạn",
  };

  const bodies = {
    email_verification:
      `Mã xác thực email của bạn là: ${otp}\n` +
      `Mã có hiệu lực trong 24 giờ.\n\n` +
      `Nếu bạn không yêu cầu xác thực này, hãy bỏ qua email này.`,
    login:
      `Mã đăng nhập của bạn là: ${otp}\n` +
      `Mã có hiệu lực trong 10 phút.\n\n` +
      `Nếu bạn không yêu cầu đăng nhập, hãy bỏ qua email này.`,
  };

  const hasSmtp = !!smtpHost && !!smtpUser && !!smtpPass;

  if (hasSmtp) {
    const transporter = nodemailer.createTransport({
      host: smtpHost,
      port: smtpPort,
      secure: smtpPort === 465,
      auth: { user: smtpUser, pass: smtpPass },
    });
    try {
      await transporter.sendMail({
        from: smtpFrom,
        to: toEmail,
        subject: subjects[purpose] || "Kanila — Mã xác thực",
        text: bodies[purpose] || `Mã của bạn là: ${otp}`,
      });
    } catch (mailErr) {
      // SMTP failure is non-fatal — log and continue so the flow still works
      // eslint-disable-next-line no-console
      console.warn(`[WARN] Failed to send OTP email to ${toEmail}:`, mailErr?.message || mailErr);
    }
  } else {
    // Dev fallback — never log OTPs in production
    if (process.env.NODE_ENV !== "production") {
      // eslint-disable-next-line no-console
      console.warn(`[DEV] OTP for ${toEmail} (${purpose}): ${otp}`);
    }
  }
}

/**
 * Create and store a new hashed OTP, replacing any existing unconsumed OTP
 * for the same email+purpose pair.
 *
 * Returns the raw OTP (only time it exists in memory — never persisted).
 */
async function issueOtp(email, purpose, accountId = null) {
  const expiryMs = purpose === "login" ? OTP_EXPIRY_LOGIN_MS : OTP_EXPIRY_VERIFY_MS;

  const otp = generateSixDigitOtp();
  const otpHash = sha256Hex(`${email}:${otp}`);
  const expiresAt = new Date(Date.now() + expiryMs);

  // Overwrite any existing unconsumed OTP for this email+purpose
  await EmailOtp.findOneAndUpdate(
    { email, purpose, consumed_at: null },
    {
      $set: {
        otp_code_hash: otpHash,
        expires_at: expiresAt,
        attempt_count: 0,
        account_id: accountId || null,
      },
    },
    { upsert: true, new: true }
  );

  return otp;
}

/**
 * Check OTP rate limit: prevent issuing a new OTP within the cooldown window.
 * Returns true if rate-limited.
 */
async function isOtpRateLimited(email, purpose) {
  const recent = await EmailOtp.findOne({
    email,
    purpose,
    created_at: { $gt: new Date(Date.now() - OTP_RESEND_COOLDOWN_MS) },
  }).sort({ created_at: -1 }).lean();
  return !!recent;
}

// ─── Normalize legacy date fields ─────────────────────────────────────────────

const normalizeDateField = (value) => {
  if (value === null || value === undefined) return null;
  if (value instanceof Date) return value;
  if (typeof value === "string" || typeof value === "number") {
    const d = new Date(value);
    return Number.isNaN(d.getTime()) ? null : d;
  }
  return null;
};

const sanitizeAccountDatesForSave = (account) => {
  account.email_verified_at = normalizeDateField(account.email_verified_at);
  account.last_login_at = normalizeDateField(account.last_login_at);
  account.created_at = normalizeDateField(account.created_at);
  account.updated_at = normalizeDateField(account.updated_at);
};

// ─── Controllers ──────────────────────────────────────────────────────────────

/**
 * POST /api/auth/register
 *
 * Registration flow:
 *   1. Validate email + full_name (no password required or accepted).
 *   2. Create Account with email_verified_at = null.
 *   3. Create Customer profile.
 *   4. Issue email_verification OTP and send email.
 *   5. Return success — no JWT yet (account must verify email to log in).
 */
const register = async (req, res) => {
  try {
    const emailRaw = String(req.body?.email || "").trim().toLowerCase();
    const full_name = req.body.full_name ?? req.body.fullName ?? "";
    const first_name = req.body.first_name ?? req.body.firstName ?? "";
    const last_name = req.body.last_name ?? req.body.lastName ?? "";
    const phone = req.body.phone ?? "";

    if (!emailRaw || !full_name) {
      return res.status(400).json({
        success: false,
        message: "email and full_name (or fullName) are required",
      });
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailRaw)) {
      return res.status(400).json({ success: false, message: "Invalid email format" });
    }

    // Check duplicate
    const existingAccount = await Account.findOne({ email: emailRaw });
    if (existingAccount) {
      return res.status(400).json({
        success: false,
        message: "Email already registered",
      });
    }

    // Normalize phone — store null for empty/missing so partial unique index is not triggered
    const normalizedPhone = phone && String(phone).trim() ? String(phone).trim() : null;

    // Check phone uniqueness only when a non-empty phone is provided
    if (normalizedPhone) {
      const phoneTaken = await Account.findOne({ phone: normalizedPhone });
      if (phoneTaken) {
        return res.status(409).json({
          success: false,
          message:
            "This phone number is already linked to another account. " +
            "Please use another number or contact support if this number belongs to you.",
        });
      }
    }

    // Create account (unverified)
    const account = await Account.create({
      email: emailRaw,
      phone: normalizedPhone,
      account_type: "customer",
      email_verified_at: null,
    });

    // Create customer profile
    const customer_code = await generateCustomerCode();
    const customer = await Customer.create({
      account_id: account._id,
      customer_code,
      full_name: String(full_name).trim(),
      first_name: String(first_name).trim(),
      last_name: String(last_name).trim(),
    });

    // Issue and send email verification OTP
    const otp = await issueOtp(emailRaw, "email_verification", account._id);
    await sendOtpEmail(emailRaw, otp, "email_verification");

    return res.status(201).json({
      success: true,
      message: "Registration successful. A verification code has been sent to your email.",
      data: {
        account_id: account._id,
        email: account.email,
        account_type: account.account_type,
        email_verified: false,
        customer: {
          _id: customer._id,
          customer_code: customer.customer_code,
          full_name: customer.full_name,
        },
      },
    });
  } catch (error) {
    if (error.code === 11000) {
      const duplicateField = Object.keys(error?.keyPattern || error?.keyValue || {})[0] || "";
      if (duplicateField === "email") {
        return res.status(400).json({ success: false, message: "Email already registered" });
      }
      if (duplicateField === "phone") {
        return res.status(409).json({
          success: false,
          message:
            "This phone number is already linked to another account. " +
            "Please use another number or contact support if this number belongs to you.",
        });
      }
      return res.status(400).json({
        success: false,
        message: `Duplicate value for field: ${duplicateField || "unknown"}`,
      });
    }
    return res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * POST /api/auth/login
 *
 * Passwordless login initiation:
 *   1. Accept email only.
 *   2. Silently look up the account.
 *   3. If eligible, issue a login OTP and send it to the email.
 *   4. Always return a generic response to avoid email enumeration.
 */
const login = async (req, res) => {
  try {
    const emailRaw = String(req.body?.email || "").trim().toLowerCase();

    if (!emailRaw) {
      return res.status(400).json({ success: false, message: "Email is required" });
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailRaw)) {
      return res.status(400).json({ success: false, message: "Invalid email format" });
    }

    const account = await Account.findOne({ email: emailRaw }).lean();

    // Rate limit check (applied regardless of account existence to prevent timing attacks)
    if (account) {
      const rateLimited = await isOtpRateLimited(emailRaw, "login");
      if (rateLimited) {
        // Return generic message — do not reveal rate limit detail
        return res.status(200).json({
          success: true,
          message: "If this email is eligible, a login code has been sent.",
        });
      }

      // Check account eligibility
      const isEligible =
        account.account_status === "active" ||
        (account.account_status === "locked" &&
          (!account.locked_until || account.locked_until <= new Date()));

      if (isEligible) {
        const otp = await issueOtp(emailRaw, "login", account._id);
        await sendOtpEmail(emailRaw, otp, "login");
      }
    }

    // Generic response regardless of whether the email exists
    return res.status(200).json({
      success: true,
      message: "If this email is eligible, a login code has been sent.",
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * POST /api/auth/verify-otp
 *
 * OTP verification for both login and email_verification purposes.
 *
 * On success:
 *   - purpose "email_verification": sets email_verified_at, returns JWT.
 *   - purpose "login": updates last_login_at, returns JWT.
 *
 * Security:
 *   - Checks expiry, consumed_at, and attempt_count.
 *   - Increments attempt_count on failure; consumes OTP after OTP_MAX_ATTEMPTS.
 *   - AUTH_DEBUG_OTP env var bypasses hash check in non-production environments only.
 */
const verifyOtp = async (req, res) => {
  try {
    const emailRaw = String(req.body?.email || "").trim().toLowerCase();
    const rawOtp = String(req.body?.otp || "").trim();
    const purpose = String(req.body?.purpose || "").trim();

    if (!emailRaw || !rawOtp || !purpose) {
      return res.status(400).json({ success: false, message: "email, otp, and purpose are required" });
    }

    if (!/^\d{6}$/.test(rawOtp)) {
      return res.status(400).json({ success: false, message: "OTP must be 6 digits" });
    }

    if (!["email_verification", "login"].includes(purpose)) {
      return res.status(400).json({
        success: false,
        message: 'purpose must be "email_verification" or "login"',
      });
    }

    // Dev-only debug OTP bypass (never in production)
    const isDebugOtp =
      AUTH_DEBUG_OTP &&
      process.env.NODE_ENV !== "production" &&
      rawOtp === AUTH_DEBUG_OTP;

    let account = await Account.findOne({ email: emailRaw });
    if (!account) {
      return res.status(400).json({ success: false, message: "Mã xác thực sai hoặc đã hết hạn." });
    }

    if (!isDebugOtp) {
      // Find the most recent valid OTP record
      const otpDoc = await EmailOtp.findOne({
        email: emailRaw,
        purpose,
        consumed_at: null,
        expires_at: { $gt: new Date() },
      }).sort({ created_at: -1 });

      if (!otpDoc) {
        return res.status(400).json({ success: false, message: "Mã xác thực sai hoặc đã hết hạn." });
      }

      const expectedHash = sha256Hex(`${emailRaw}:${rawOtp}`);
      if (otpDoc.otp_code_hash !== expectedHash) {
        // Increment attempt count; burn OTP if max attempts reached
        otpDoc.attempt_count += 1;
        if (otpDoc.attempt_count >= OTP_MAX_ATTEMPTS) {
          otpDoc.consumed_at = new Date();
        }
        await otpDoc.save();
        return res.status(400).json({ success: false, message: "Mã xác thực sai hoặc đã hết hạn." });
      }

      // Valid — consume OTP
      otpDoc.consumed_at = new Date();
      await otpDoc.save();
    }

    // Update account post-verification
    if (purpose === "email_verification" && !account.email_verified_at) {
      account.email_verified_at = new Date();
    }
    if (purpose === "login") {
      account.last_login_at = new Date();
      account.failed_login_count = 0;

      // Auto-unlock if lock has expired
      if (account.account_status === "locked" && account.locked_until && account.locked_until <= new Date()) {
        account.account_status = "active";
        account.locked_until = null;
      }
    }

    sanitizeAccountDatesForSave(account);
    await account.save();

    // Check account is usable (after status updates above)
    if (account.account_status === "inactive") {
      return res.status(403).json({ success: false, message: "Account is inactive" });
    }
    if (account.account_status === "locked") {
      return res.status(403).json({ success: false, message: "Account is locked. Please try again later." });
    }

    // Issue JWT
    const token = jwt.sign(
      { account_id: account._id, email: account.email, account_type: account.account_type },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN || "7d" }
    );

    const customer = await Customer.findOne({ account_id: account._id }).lean();

    return res.status(200).json({
      success: true,
      message: purpose === "email_verification"
        ? "Email verified successfully."
        : "Login successful.",
      data: {
        token,
        account: {
          _id: account._id,
          email: account.email,
          account_type: account.account_type,
          email_verified_at: account.email_verified_at,
          last_login_at: account.last_login_at,
        },
        customer: customer
          ? {
              _id: customer._id,
              customer_code: customer.customer_code,
              full_name: customer.full_name,
            }
          : null,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

/**
 * GET /api/auth/me  (protected)
 *
 * Returns the authenticated account and linked customer profile.
 */
const getMe = async (req, res) => {
  try {
    const id = req.user.account_id || req.user.accountId;
    const account = await Account.findById(id);
    if (!account) {
      return res.status(404).json({ success: false, message: "Account not found" });
    }

    const customer = await Customer.findOne({ account_id: account._id });

    return res.status(200).json({
      success: true,
      message: "Get current user successfully",
      data: {
        account,
        customer: customer || null,
      },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

// ─── Deprecated endpoints (410 Gone) ─────────────────────────────────────────

/**
 * These endpoints were part of the password-based authentication flow.
 * They have been removed. Clients must migrate to the email-OTP flow.
 *
 * POST /api/auth/check-email      → use POST /api/auth/login
 * POST /api/auth/verify-reset-otp → use POST /api/auth/verify-otp (purpose: "login")
 * POST /api/auth/reset-password   → endpoint removed; system is now passwordless
 */
function deprecated410(req, res) {
  return res.status(410).json({
    success: false,
    message:
      "This endpoint has been removed. Kanila now uses passwordless email-OTP authentication. " +
      "Use POST /api/auth/login to request a login code and POST /api/auth/verify-otp to authenticate.",
  });
}

// ─── Exports ──────────────────────────────────────────────────────────────────

module.exports = {
  register,
  login,
  verifyOtp,
  getMe,
  // Deprecated stubs — kept so route file does not break during transition
  checkEmail: deprecated410,
  verifyResetOtp: deprecated410,
  resetPassword: deprecated410,
};