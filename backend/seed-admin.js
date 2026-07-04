/**
 * Seed script: Create a demo admin account
 *
 * Usage: npm run seed:admin
 *
 * Idempotent — safe to run multiple times.
 * If admin@kanila.com already exists, skips creation.
 *
 * NOTE: Passwordless system — no password_hash is created.
 * Admins log in via email OTP (POST /api/auth/login → POST /api/auth/verify-otp).
 */
require("dotenv").config();
const mongoose = require("mongoose");
const Account = require("./models/account.model");

const ADMIN_EMAIL = "admin@kanila.com";

async function seedAdmin() {
  try {
    console.log("Connecting to MongoDB...");
    await mongoose.connect(process.env.MONGO_URI);
    console.log("Connected.\n");

    // Check if already exists
    const existing = await Account.findOne({ email: ADMIN_EMAIL });
    if (existing) {
      console.log("Admin account already exists:");
      console.log("  _id:          ", existing._id);
      console.log("  email:        ", existing.email);
      console.log("  account_type: ", existing.account_type);
      console.log("  status:       ", existing.account_status);

      // Fix account_type/status if needed
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
        console.log("\n  → Fixed account_type/status/email_verified_at");
      } else {
        console.log("\n  No changes needed.");
      }
    } else {
      const account = await Account.create({
        email: ADMIN_EMAIL,
        account_type: "admin",
        account_status: "active",
        username: "Kanila Admin",
        email_verified_at: new Date(),
      });

      console.log("Admin account created successfully!");
      console.log("  _id:          ", account._id);
      console.log("  email:        ", account.email);
      console.log("  account_type: ", account.account_type);
      console.log("  status:       ", account.account_status);
    }

    console.log("\n--- Login instructions (passwordless) ---");
    console.log("  Email:     ", ADMIN_EMAIL);
    console.log("  Step 1:    POST /api/auth/login        { email }");
    console.log("  Step 2:    POST /api/auth/verify-otp   { email, otp, purpose: \"login\" }");
    console.log("  Dev OTP:   Set AUTH_DEBUG_OTP in .env for bypass");
    console.log("-----------------------------------------\n");
  } catch (error) {
    console.error("Seed failed:", error.message);
    process.exit(1);
  } finally {
    await mongoose.disconnect();
    console.log("Disconnected from MongoDB.");
  }
}

seedAdmin();
