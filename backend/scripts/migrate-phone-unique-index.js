/**
 * migrate-phone-unique-index.js
 *
 * Safe, idempotent migration script for Account.phone partial unique index.
 *
 * Steps:
 *   1. Connect to MongoDB Atlas.
 *   2. Normalize empty-string phones to null.
 *   3. Find and report duplicate non-empty phones.
 *   4. Without --dry-run: resolve duplicates by keeping the most recent account's
 *      phone and setting the others to null.
 *   5. Create the partial unique index if it doesn't exist.
 *
 * Usage:
 *   node scripts/migrate-phone-unique-index.js            # run migration
 *   node scripts/migrate-phone-unique-index.js --dry-run   # audit only
 *
 * Deprecation notes:
 *   - phone_verified_at has been removed from Account. Phone is a contact field only.
 *   - password_hash has been removed as part of the passwordless email-OTP flow.
 *     No plain password field replaces it.
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

  // ── Step 1: Normalize empty-string phones to null ────────────────────────────
  const emptyPhoneCount = await collection.countDocuments({ phone: "" });
  console.log(`📊 Accounts with phone = \"\" (empty string): ${emptyPhoneCount}`);

  if (emptyPhoneCount > 0) {
    if (DRY_RUN) {
      console.log(`   Would set ${emptyPhoneCount} empty phones to null.`);
    } else {
      const normResult = await collection.updateMany(
        { phone: "" },
        { $set: { phone: null } }
      );
      console.log(`   ✅ Normalized ${normResult.modifiedCount} empty phones to null.`);
    }
  }
  console.log();

  // ── Step 2: Find duplicate non-empty phones ──────────────────────────────────
  const duplicates = await collection.aggregate([
    { $match: { phone: { $exists: true, $ne: null, $ne: "" } } },
    { $group: { _id: "$phone", count: { $sum: 1 }, accounts: { $push: { _id: "$_id", email: "$email", updated_at: "$updated_at" } } } },
    { $match: { count: { $gt: 1 } } },
    { $sort: { count: -1 } },
  ]).toArray();

  if (duplicates.length === 0) {
    console.log("✅ No duplicate phone values found.\n");
  } else {
    console.log(`⚠️  Found ${duplicates.length} phone value(s) with duplicates:\n`);
    for (const dup of duplicates) {
      console.log(`   Phone: ${dup._id}  (${dup.count} accounts)`);
      for (const acc of dup.accounts) {
        console.log(`      - _id: ${acc._id}, email: ${acc.email}, updated_at: ${acc.updated_at || "N/A"}`);
      }
    }
    console.log();

    if (DRY_RUN) {
      console.log("🔍 DRY RUN — no changes will be made.");
      console.log("   Strategy: keep phone on the most recently updated account, set others to null.\n");
    } else {
      console.log("🚀 Resolving duplicates (keeping most recent, nullifying others)...\n");
      for (const dup of duplicates) {
        // Sort by updated_at desc — keep the first (most recent)
        const sorted = dup.accounts.sort((a, b) => {
          const dateA = a.updated_at ? new Date(a.updated_at).getTime() : 0;
          const dateB = b.updated_at ? new Date(b.updated_at).getTime() : 0;
          return dateB - dateA;
        });

        const keeper = sorted[0];
        const toNullify = sorted.slice(1);

        console.log(`   Phone ${dup._id}: keeping on account ${keeper._id} (${keeper.email})`);
        for (const acc of toNullify) {
          await collection.updateOne(
            { _id: acc._id },
            { $set: { phone: null } }
          );
          console.log(`      → Nullified phone on account ${acc._id} (${acc.email})`);
        }
      }
      console.log();
    }
  }

  // ── Step 3: Create partial unique index ──────────────────────────────────────
  if (DRY_RUN) {
    console.log("🔍 DRY RUN — would create partial unique index on phone.");
    await mongoose.disconnect();
    console.log("\n🔌 Disconnected from MongoDB.");
    return;
  }

  console.log("📐 Dropping old phone_1 index if it exists...");
  try {
    await collection.dropIndex("phone_1");
    console.log("✅ Dropped old phone_1 index.");
  } catch (err) {
    console.log("ℹ️  Could not drop phone_1 index (might not exist).");
  }

  console.log("📐 Creating partial unique index on phone...");
  try {
    await collection.createIndex(
      { phone: 1 },
      {
        unique: true,
        partialFilterExpression: { phone: { $exists: true, $gt: "" } },
        name: "phone_1_partial_unique",
      }
    );
    console.log("✅ Partial unique index created successfully.");
  } catch (indexErr) {
    if (indexErr.code === 85 || indexErr.code === 86) {
      // 85 = IndexOptionsConflict, 86 = IndexKeySpecsConflict — index already exists
      console.log("ℹ️  Index already exists (possibly with same or different options). Skipping.");
    } else {
      throw indexErr;
    }
  }

  // ── Step 4: Post-migration verification ──────────────────────────────────────
  const remainingDups = await collection.aggregate([
    { $match: { phone: { $exists: true, $ne: null, $ne: "" } } },
    { $group: { _id: "$phone", count: { $sum: 1 } } },
    { $match: { count: { $gt: 1 } } },
  ]).toArray();

  if (remainingDups.length === 0) {
    console.log("✅ Verification passed — no duplicate phones remain.");
  } else {
    console.warn(`⚠️  ${remainingDups.length} duplicate phone(s) still exist. Re-run this script.`);
  }

  const indexes = await collection.indexes();
  const phoneIndex = indexes.find((idx) => idx.name === "phone_1_partial_unique" || (idx.key?.phone === 1 && idx.unique));
  if (phoneIndex) {
    console.log("✅ Phone unique index verified:", JSON.stringify(phoneIndex, null, 2));
  } else {
    console.warn("⚠️  Phone unique index not found in collection indexes.");
  }

  console.log("\n── Deprecation Notes ──────────────────────────────────────");
  console.log("  • phone_verified_at: REMOVED — phone is a contact field only.");
  console.log("  • password_hash: REMOVED — system uses passwordless email-OTP.");
  console.log("    Run scripts/migrate-remove-password-hash.js to clean up old data.");
  console.log("──────────────────────────────────────────────────────────\n");

  await mongoose.disconnect();
  console.log("🔌 Disconnected from MongoDB.");
}

run().catch((err) => {
  console.error("❌ Migration failed:", err.message || err);
  process.exit(1);
});
