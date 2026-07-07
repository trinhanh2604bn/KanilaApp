/**
 * auth.controller.js
 *
 * Authentication for Kanila supporting both Email and Phone.
 *
 * Flows:
 *   1. Register  → POST /api/auth/register
 *   2. Login     → POST /api/auth/login
 *   3. Verify    → POST /api/auth/verify-otp
 *   4. Me        → GET  /api/auth/me (protected)
 */

const jwt = require("jsonwebtoken");
const nodemailer = require("nodemailer");
const bcrypt = require("bcryptjs");
const Account = require("../models/account.model");
const Customer = require("../models/customer.model");
const AuthOtp = require("../models/authOtp.model");
const { generateSixDigitOtp, sha256Hex } = require("../utils/otp");

// ─── Constants ────────────────────────────────────────────────────────────────

const AUTH_DEBUG_OTP = process.env.AUTH_DEBUG_OTP;
const OTP_EXPIRES_MINUTES = parseInt(process.env.OTP_EXPIRES_MINUTES || "5");
const OTP_MAX_ATTEMPTS = parseInt(process.env.OTP_MAX_ATTEMPTS || "5");

// ─── Helpers ──────────────────────────────────────────────────────────────────

const generateCustomerCode = async () => {
  const base = await Customer.countDocuments();
  let attempt = 0;
  while (attempt < 5) {
    const next = base + attempt + 1;
    const code = `CUS${String(next).padStart(4, "0")}`;
    // eslint-disable-next-line no-await-in-loop
    const exists = await Customer.findOne({ customer_code: code }).select("_id").lean();
    if (!exists) return code;
    attempt += 1;
  }
  return `CUS${Date.now()}`;
};

const maskIdentifier = (identifier, type) => {
  if (type === "email") {
    const [user, domain] = identifier.split("@");
    return `${user.substring(0, 2)}***@${domain}`;
  } else if (type === "phone") {
    return identifier.replace(/(\d{3})\d+(\d{3})/, "$1****$2");
  }
  return identifier;
};

const normalizeIdentifier = (identifier, type) => {
  if (!identifier) return null;
  const trimmed = String(identifier).trim();
  if (type === "email") return trimmed.toLowerCase();
  if (type === "phone") {
    // Strip everything except digits and +
    let digits = trimmed.replace(/[^\d+]/g, "");
    // Ensure VN E.164 format if it looks like a local number
    if (digits.startsWith("0")) {
      digits = "+84" + digits.substring(1);
    } else if (digits.length >= 9 && !digits.startsWith("+")) {
      digits = "+84" + digits;
    }
    return digits;
  }
  return trimmed;
};

async function sendOtp(target_type, target_value, otp, purpose) {
  if (target_type === "email") {
    await sendOtpEmail(target_value, otp, purpose);
  } else {
    await sendSmsOtp(target_value, otp, purpose);
  }
}

async function sendOtpEmail(toEmail, otp, purpose) {
  // Use SMTP for now as in original code, but could be extended to use EMAIL_PROVIDER
  const smtpHost = process.env.SMTP_HOST;
  const smtpPort = Number(process.env.SMTP_PORT || 587);
  const smtpUser = process.env.SMTP_USER;
  const smtpPass = process.env.SMTP_PASS;
  const smtpFrom = process.env.SMTP_FROM || "no-reply@kanila.vn";

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
        subject: `Kanila — Mã xác thực (${purpose})`,
        text: `Mã xác thực của bạn là: ${otp}. Mã có hiệu lực trong ${OTP_EXPIRES_MINUTES} phút.`,
      });
    } catch (err) {
      console.warn(`[WARN] Failed to send OTP email to ${toEmail}:`, err.message);
    }
  } else if (process.env.NODE_ENV !== "production") {
    console.warn(`[DEV] OTP for ${toEmail} (${purpose}): ${otp}`);
  }
}

async function sendSmsOtp(phone, otp, purpose) {
  // Placeholder for SMS provider
  if (process.env.NODE_ENV !== "production") {
    console.warn(`[DEV] SMS OTP for ${phone} (${purpose}): ${otp}`);
  } else {
    console.log(`[SMS] Sending OTP ${otp} to ${phone} for ${purpose} via ${process.env.SMS_PROVIDER || 'unknown'}`);
  }
}

async function issueOtp(target_type, target_value, purpose, accountId = null, req = null) {
  const otp = generateSixDigitOtp();
  const otpHash = sha256Hex(`${target_value}:${otp}`);
  const expiresAt = new Date(Date.now() + OTP_EXPIRES_MINUTES * 60 * 1000);

  await AuthOtp.findOneAndUpdate(
    { target_type, target_value, purpose, consumed_at: null },
    {
      $set: {
        otp_hash: otpHash,
        expires_at: expiresAt,
        attempt_count: 0,
        account_id: accountId,
        max_attempts: OTP_MAX_ATTEMPTS,
        request_ip: req?.ip,
        user_agent: req?.get("user-agent"),
      },
    },
    { upsert: true, new: true }
  );

  return otp;
}

const generateTokens = (account) => {
  const payload = {
    account_id: account._id,
    email: account.email,
    phone: account.phone,
    account_type: account.account_type
  };

  const accessToken = jwt.sign(
    payload,
    process.env.JWT_ACCESS_SECRET || process.env.JWT_SECRET || "access_secret",
    { expiresIn: process.env.JWT_ACCESS_EXPIRES_IN || "15m" }
  );

  const refreshToken = jwt.sign(
    { account_id: account._id },
    process.env.JWT_REFRESH_SECRET || "refresh_secret",
    { expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || "30d" }
  );

  return { accessToken, refreshToken };
};

// ─── Controllers ──────────────────────────────────────────────────────────────

const register = async (req, res) => {
  try {
    const { registration_channel, full_name, email, phone, username, password } = req.body;
    console.log(`[AUTH] Register request: channel=${registration_channel}, name=${full_name}, email=${email}, phone=${phone}, username=${username}`);

    if (!registration_channel || !full_name || !username || !password) {
      return res.status(400).json({ success: false, message: "registration_channel, full_name, username and password are required" });
    }

    const target_type = registration_channel;
    const target_value = normalizeIdentifier(target_type === "email" ? email : phone, target_type);
    console.log(`[AUTH] Normalized target: type=${target_type}, value=${target_value}`);

    if (!target_value) {
      return res.status(400).json({ success: false, message: `${target_type} is required for ${target_type} registration` });
    }

    // Check duplicate
    const query = target_type === "email" ? { email: target_value } : { phone: target_value };
    let account = await Account.findOne({ $or: [query, { username }] });

    if (account) {
      console.log(`[AUTH] Account or username exists: id=${account._id}, status=${account.account_status}`);
      if (account.username === username) {
         return res.status(400).json({ success: false, message: "Tên đăng nhập đã được sử dụng." });
      }
      if (account.account_status !== "pending") {
        return res.status(400).json({ success: false, message: `${target_type === 'email' ? 'Email' : 'Số điện thoại'} đã được đăng ký.` });
      }
      // If pending, continue to issue new OTP and update name/password/username
      const salt = await bcrypt.genSalt(10);
      account.password_hash = await bcrypt.hash(password, salt);
      account.username = username;
      await account.save();
      await Customer.findOneAndUpdate({ account_id: account._id }, { full_name: String(full_name).trim() });
    } else {
      console.log(`[AUTH] Creating new pending account for ${target_value}`);

      const salt = await bcrypt.genSalt(10);
      const password_hash = await bcrypt.hash(password, salt);

      // Create pending account
      const accountData = {
        registration_channel,
        username,
        password_hash,
        account_status: "pending",
        account_type: "customer",
      };
      if (target_type === "email") accountData.email = target_value;
      else accountData.phone = target_value;

      account = await Account.create(accountData);

      // Create customer profile
      const customer_code = await generateCustomerCode();
      console.log(`[AUTH] Creating customer profile: code=${customer_code}`);
      await Customer.create({
        account_id: account._id,
        customer_code,
        full_name: String(full_name).trim(),
      });
    }

    // Issue OTP
    const otp = await issueOtp(target_type, target_value, "register", account._id, req);
    await sendOtp(target_type, target_value, otp, "register");

    return res.status(201).json({
      success: true,
      data: {
        verification_required: true,
        target_type,
        masked_target: maskIdentifier(target_value, target_type),
      }
    });
  } catch (error) {
    console.error(`[AUTH] Register error:`, error);
    return res.status(500).json({ success: false, message: error.message });
  }
};

const login = async (req, res) => {
  try {
    const { login_channel, identifier, password } = req.body;
    console.log(`[AUTH] Login request: channel=${login_channel}, identifier=${identifier}`);

    if (!login_channel || !identifier) {
      return res.status(400).json({ success: false, message: "login_channel and identifier are required" });
    }

    const target_value = normalizeIdentifier(identifier, login_channel);
    console.log(`[AUTH] Normalized target: ${target_value}`);
    const query = login_channel === "email" ? { email: target_value } : { phone: target_value };

    const account = await Account.findOne(query).select("+password_hash");

    if (!account) {
      return res.status(404).json({ success: false, message: "Tài khoản không tồn tại." });
    }

    if (account.account_status === "locked") {
      return res.status(403).json({ success: false, message: "Tài khoản đang bị khóa." });
    }

    // If password is provided, check it. If not, it's likely a resend OTP request for a pending/unverified account
    if (password) {
      const isMatch = await bcrypt.compare(password, account.password_hash);
      if (!isMatch) {
        account.failed_login_count += 1;
        if (account.failed_login_count >= 5) {
          account.account_status = "locked";
          account.locked_until = new Date(Date.now() + 30 * 60 * 1000); // 30 mins
        }
        await account.save();
        return res.status(400).json({ success: false, message: "Mật khẩu không chính xác." });
      }
    } else if (account.account_status !== "pending") {
      // Password is required for active accounts
      return res.status(400).json({ success: false, message: "Mật khẩu là bắt buộc." });
    }

    // For pending accounts or successful password match, issue OTP if needed
    // In this refactored flow, we always go through OTP verification for demo purposes or unverified state
    console.log(`[AUTH] Issuing OTP for id=${account._id}`);
    const otp = await issueOtp(login_channel, target_value, "login", account._id, req);
    await sendOtp(login_channel, target_value, otp, "login");

    return res.status(200).json({
      success: true,
      message: "Mã xác minh đã được gửi.",
      data: {
        verification_required: true,
      }
    });
  } catch (error) {
    console.error(`[AUTH] Login error:`, error);
    return res.status(500).json({ success: false, message: error.message });
  }
};


const forgotPassword = async (req, res) => {
  try {
    const { login_channel, identifier } = req.body;
    console.log(`[AUTH] Forgot password request: channel=${login_channel}, identifier=${identifier}`);

    if (!login_channel || !identifier) {
      return res.status(400).json({ success: false, message: "login_channel and identifier are required" });
    }

    const target_value = normalizeIdentifier(identifier, login_channel);
    console.log(`[AUTH] Normalized target: ${target_value}`);
    const query = login_channel === "email" ? { email: target_value } : { phone: target_value };

    const account = await Account.findOne(query);

    if (account && account.account_status !== "locked") {
      console.log(`[AUTH] Account found: id=${account._id}, issuing reset OTP`);
      const otp = await issueOtp(login_channel, target_value, "reset_password", account._id, req);
      await sendOtp(login_channel, target_value, otp, "reset_password");
    } else {
      console.log(`[AUTH] Account not found or locked for reset: ${target_value}`);
    }

    return res.status(200).json({
      success: true,
      message: "Mã xác minh đã được gửi.",
      data: {
        verification_required: true,
      }
    });
  } catch (error) {
    console.error(`[AUTH] Forgot password error:`, error);
    return res.status(500).json({ success: false, message: error.message });
  }
};

const verifyOtp = async (req, res) => {
  try {
    const { target_type, target_value: rawValue, otp, purpose } = req.body;
    console.log(`[AUTH] Verify OTP request: type=${target_type}, value=${rawValue}, otp=${otp}, purpose=${purpose}`);

    if (!target_type || !rawValue || !otp || !purpose) {
      return res.status(400).json({ success: false, message: "Missing required fields" });
    }

    const target_value = normalizeIdentifier(rawValue, target_type);
    console.log(`[AUTH] Normalized value: ${target_value}`);

    const otpDoc = await AuthOtp.findOne({
      target_type,
      target_value,
      purpose,
      consumed_at: null,
      expires_at: { $gt: new Date() },
    }).sort({ created_at: -1 });

    if (!otpDoc) {
      console.log(`[AUTH] Valid OTP not found for ${target_value} (${purpose})`);
      return res.status(400).json({ success: false, message: "Mã xác thực không hợp lệ hoặc đã hết hạn." });
    }

    const isDemoOtp = String(otp) === "666666"; // DEMO ONLY: fixed OTP 666666
    const expectedHash = sha256Hex(`${target_value}:${otp}`);

    if (!isDemoOtp && otpDoc.otp_hash !== expectedHash) {
      console.log(`[AUTH] OTP mismatch for ${target_value}. Provided: ${otp}`);
      otpDoc.attempt_count += 1;
      if (otpDoc.attempt_count >= otpDoc.max_attempts) {
        otpDoc.consumed_at = new Date();
      }
      await otpDoc.save();
      return res.status(400).json({ success: false, message: "Mã xác thực không đúng." });
    }

    // Success
    console.log(`[AUTH] OTP verified successfully for ${target_value}`);
    otpDoc.consumed_at = new Date();
    await otpDoc.save();

    let account = await Account.findById(otpDoc.account_id);
    if (!account) {
      // Fallback search if account_id was not in OTP doc
      const query = target_type === "email" ? { email: target_value } : { phone: target_value };
      account = await Account.findOne(query);
    }

    if (!account) {
      console.log(`[AUTH] Account not found after OTP verification for ${target_value}`);
      return res.status(404).json({ success: false, message: "Account not found" });
    }

    if (purpose === "reset_password") {
      const resetToken = jwt.sign(
        { account_id: account._id, purpose: "reset_password" },
        process.env.JWT_ACCESS_SECRET || "reset_secret",
        { expiresIn: "15m" }
      );
      return res.status(200).json({
        success: true,
        data: { reset_token: resetToken }
      });
    }

    // Update account
    if (target_type === "email") account.email_verified_at = new Date();
    else if (target_type === "phone") account.phone_verified_at = new Date();

    if (account.account_status === "pending") account.account_status = "active";
    account.last_login_at = new Date();
    account.failed_login_count = 0;
    // Activate account
    account.account_status = "active";
    await account.save();

    const { accessToken, refreshToken } = generateTokens(account);
    const customer = await Customer.findOne({ account_id: account._id }).lean();

    console.log(`[AUTH] Issued tokens for account: ${account._id}`);
    return res.status(200).json({
      success: true,
      data: {
        access_token: accessToken,
        refresh_token: refreshToken,
        account: {
          _id: account._id,
          email: account.email,
          phone: account.phone,
          account_type: account.account_type,
        },
        customer: customer ? {
          _id: customer._id,
          customer_code: customer.customer_code,
          full_name: customer.full_name,
        } : null,
      },
    });
  } catch (error) {
    console.error(`[AUTH] Verify OTP error:`, error);
    return res.status(500).json({ success: false, message: error.message });
  }
};

const getMe = async (req, res) => {
  try {
    const account = await Account.findById(req.user.account_id);
    if (!account) return res.status(404).json({ success: false, message: "Account not found" });
    const customer = await Customer.findOne({ account_id: account._id });
    return res.status(200).json({
      success: true,
      data: { account, customer },
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const resetPassword = async (req, res) => {
  try {
    const { reset_token, new_password, confirm_password } = req.body;

    if (!reset_token || !new_password || !confirm_password) {
      return res.status(400).json({ success: false, message: "Missing required fields" });
    }

    if (new_password !== confirm_password) {
      return res.status(400).json({ success: false, message: "Mật khẩu xác nhận không khớp." });
    }

    if (new_password.length < 8) {
      return res.status(400).json({ success: false, message: "Mật khẩu phải có ít nhất 8 ký tự." });
    }

    let decoded;
    try {
      decoded = jwt.verify(reset_token, process.env.JWT_ACCESS_SECRET || "reset_secret");
    } catch (err) {
      return res.status(400).json({ success: false, message: "Mã khôi phục không hợp lệ hoặc đã hết hạn." });
    }

    if (decoded.purpose !== "reset_password") {
      return res.status(400).json({ success: false, message: "Invalid token purpose" });
    }

    const account = await Account.findById(decoded.account_id);
    if (!account) {
      return res.status(404).json({ success: false, message: "Account not found" });
    }

    const salt = await bcrypt.genSalt(10);
    account.password_hash = await bcrypt.hash(new_password, salt);
    await account.save();

    return res.status(200).json({
      success: true,
      message: "Đặt lại mật khẩu thành công",
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

function deprecated410(req, res) {
  return res.status(410).json({
    success: false,
    message: "This endpoint has been removed. Use the new OTP-based flow.",
  });
}

module.exports = {
  register,
  login,
  forgotPassword,
  verifyOtp,
  getMe,
  checkEmail: deprecated410,
  verifyResetOtp: deprecated410,
  resetPassword,
};
