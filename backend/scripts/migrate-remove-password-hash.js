/**
 * migrate-remove-password-hash.js
 *
 * Safe, idempotent migration script.
 *
 * Removes `password_hash` and `phone_verified_at` from all documents in the
 * `accounts` MongoDB collection.
 *
 * Background:
 *   Kanila has migrated to passwordless email-OTP authentication.
 *   The `password_hash` field is no longer part of the Account schema.
 *   The `phone_verified_at` field has been removed — phone is now a contact
 *   field only, not an authentication verification factor.
 *
 * This script uses MongoDB's `$unset` operator, which is safe to run even
 * when the fields have already been removed (the operation is a no-op per doc).
 *
 * Usage:
 *   node scripts/migrate-remove-password-hash.js
 *
 * Options:
 *   --dry-run    Print affected counts without modifying any documents.
 */
require("dotenv").config({ path: require("path").join(__dirname, "..", ".env") });
const mongoose = require("mongoose");

const DRY_RUN = process.argv.includes("--dry-run");

async function run() {
  const uri = process.env.MONGO_URI;
  if (!uri) {
    console.error("❌ MONGO_URI is not set in .env");
    process.exit(1);
  }

  console.log("🔗 Connecting to MongoDB...");
  await mongoose.connect(uri);
  console.log("✅ Connected.\n");

  const collection = mongoose.connection.collection("accounts");

  // Count documents that still have the old fields
  const withPasswordHash = await collection.countDocuments({ password_hash: { $exists: true } });
  const withPhoneVerified = await collection.countDocuments({ phone_verified_at: { $exists: true } });

  console.log("📊 Pre-migration audit:");
  console.log(`   Documents with password_hash  : ${withPasswordHash}`);
  console.log(`   Documents with phone_verified_at: ${withPhoneVerified}`);
  console.log();

  if (withPasswordHash === 0 && withPhoneVerified === 0) {
    console.log("✅ No documents require migration. Fields already removed.");
    await mongoose.disconnect();
    return;
  }

  if (DRY_RUN) {
    console.log("🔍 DRY RUN — no changes will be made.");
    console.log(`   Would unset password_hash from ${withPasswordHash} document(s).`);
    console.log(`   Would unset phone_verified_at from ${withPhoneVerified} document(s).`);
    await mongoose.disconnect();
    return;
  }

  console.log("🚀 Running migration...\n");

  // Unset both fields in a single pass using updateMany
  const result = await collection.updateMany(
    { $or: [{ password_hash: { $exists: true } }, { phone_verified_at: { $exists: true } }] },
    { $unset: { password_hash: "", phone_verified_at: "" } }
  );

  console.log(`✅ Migration complete.`);
  console.log(`   Matched  : ${result.matchedCount} document(s)`);
  console.log(`   Modified : ${result.modifiedCount} document(s)`);
  console.log();

  // Post-migration verification
  const remaining = await collection.countDocuments({
    $or: [{ password_hash: { $exists: true } }, { phone_verified_at: { $exists: true } }],
  });

  if (remaining === 0) {
    console.log("✅ Verification passed — no documents retain password_hash or phone_verified_at.");
  } else {
    console.warn(`⚠️  ${remaining} document(s) still have the legacy fields. Re-run this script.`);
  }

  await mongoose.disconnect();
  console.log("\n🔌 Disconnected from MongoDB.");
}

run().catch((err) => {
  console.error("❌ Migration failed:", err.message || err);
  process.exit(1);
});
