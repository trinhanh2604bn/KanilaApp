/**
 * seed-demo-admin.js
 *
 * Creates or updates a demo admin account for local development.
 *
 * NOTE: Passwordless system — no password is stored.
 * Use AUTH_DEBUG_OTP in .env or configure SMTP for email OTP delivery.
 */
const mongoose = require("mongoose");
const path = require("path");

require("dotenv").config({ path: path.join(__dirname, ".env") });

const Account = require("./models/account.model");

async function seedAdmin() {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log("Connected to MongoDB for admin seeding");

    const email = "admin@gmail.com";

    let account = await Account.findOne({ email });

    if (account) {
      account.account_type = "admin";
      account.account_status = "active";
      if (!account.email_verified_at) {
        account.email_verified_at = new Date();
      }
      await account.save();
      console.log("Admin account updated (passwordless).");
    } else {
      account = await Account.create({
        email,
        account_type: "admin",
        account_status: "active",
        username: "Kanila Admin",
        email_verified_at: new Date(),
      });
      console.log("Admin account created (passwordless).");
    }

    console.log("  Email:", account.email);
    console.log("  Login: POST /api/auth/login → POST /api/auth/verify-otp");
  } catch (error) {
    console.error("Error seeding admin:", error);
  } finally {
    await mongoose.disconnect();
  }
}

seedAdmin();
