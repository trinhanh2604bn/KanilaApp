/**
 * migrate-beauty-profiles-v2.js
 *
 * Migrates CustomerBeautyProfile documents from legacy schema to canonical v2 schema.
 *
 * USAGE:
 *   node scripts/migrate-beauty-profiles-v2.js --dry-run    ← scan only, no writes
 *   node scripts/migrate-beauty-profiles-v2.js --apply      ← write to database
 *
 * Running without --dry-run or --apply exits immediately with an error.
 * This prevents accidental data modification.
 *
 * FIELD MAPPING TABLE (canonical contract):
 *   Legacy field         → Canonical field       Notes
 *   ─────────────────────────────────────────────────────────────────────
 *   skin_tone            → skin_color            Direct rename
 *   undertone            → skin_undertone        Direct rename
 *   finish_preference    → foundation_finish     Array[0] if multi-value (see conflict policy)
 *   lip_color_preference → lipstick_colors       Wrap scalar in array if needed
 *   makeup_style         → makeup_styles         Wrap scalar in array if needed
 *   budget_range         → budget                Direct rename
 *   shade_preference     → (audited, backed up, no canonical mapping — backed up only)
 *
 * VALUE NORMALIZATION TABLE:
 *   sensitivity_level: low   → LOW
 *   sensitivity_level: medium → MEDIUM
 *   sensitivity_level: high  → HIGH
 *   sensitivity_level: reactive → REACTIVE
 *   skin_type: oily          → OILY_SKIN
 *   skin_type: dry           → DRY_SKIN
 *   skin_type: combination   → COMBINATION_SKIN
 *   skin_type: normal        → NORMAL_SKIN
 *   skin_type: sensitive     → SENSITIVE_SKIN
 *   skin_concerns: acne      → ACNE
 *   skin_concerns: dark_spot → DARK_SPOT
 *   skin_concerns: dark_spots → DARK_SPOT
 *   skin_concerns: dullness  → DULLNESS
 *   skin_concerns: large_pores → LARGE_PORES
 *   skin_concerns: blackheads → BLACKHEADS
 *   skin_concerns: wrinkles  → WRINKLES
 *   skin_concerns: dehydration → DEHYDRATION
 *   skin_concerns: redness   → REDNESS
 *   budget: under_200k       → UNDER_300
 *   budget: 200_500k         → 300_500
 *   budget: 500_1000k        → 500_1000
 *   budget: premium          → OVER_1000
 *   "unknown" string         → null (for nullable single-select fields only)
 *   "no_preference" string   → null (for fragrance_preference only)
 *
 * finish_preference CONFLICT POLICY (documented):
 *   If finish_preference is an array with multiple values, only index [0] is migrated.
 *   All values are preserved in the audit record. No data is silently discarded.
 *
 * shade_preference POLICY:
 *   shade_preference has no canonical target field. The full value is backed up in the
 *   audit collection (beauty_profile_migration_audit) and the field is $unset from the
 *   document. No value is silently lost.
 *
 * BACKUP & ROLLBACK:
 *   Before any document is modified, the original state is written to the
 *   `beauty_profile_migration_audit` collection. Each audit record contains:
 *     - customer_id, original document snapshot, migration_version, migrated_at, status
 *   Rollback can be performed by reading audit records and restoring the original snapshot.
 */

require("dotenv").config();
const mongoose = require("mongoose");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const { ensureCache, isValidCode } = require("../utils/beautyReferenceCodeResolver");

// ─── CLI mode ─────────────────────────────────────────────────────────────────
const args = process.argv.slice(2);
const isDryRun = args.includes("--dry-run");
const isApply  = args.includes("--apply");

if (!isDryRun && !isApply) {
  console.error("ERROR: You must specify --dry-run or --apply.");
  console.error("  node scripts/migrate-beauty-profiles-v2.js --dry-run");
  console.error("  node scripts/migrate-beauty-profiles-v2.js --apply");
  process.exit(1);
}

const MIGRATION_VERSION = "v2.0";

// ─── Audit collection (not a Mongoose model — raw to avoid schema constraints) ─
let auditCollection;

// ─── Value normalization maps ─────────────────────────────────────────────────

/** Normalize lowercase skin_type variants to canonical UPPER_SNAKE_CASE */
const SKIN_TYPE_NORMALIZE = {
  oily: "OILY_SKIN",
  dry: "DRY_SKIN",
  combination: "COMBINATION_SKIN",
  normal: "NORMAL_SKIN",
  sensitive: "SENSITIVE_SKIN",
};

/** Normalize lowercase sensitivity_level variants to canonical uppercase */
const SENSITIVITY_NORMALIZE = {
  low: "LOW",
  medium: "MEDIUM",
  high: "HIGH",
  reactive: "REACTIVE",
};

/** Normalize legacy or lowercase skin_concern codes to canonical */
const CONCERN_NORMALIZE = {
  acne: "ACNE",
  dark_spot: "DARK_SPOT",
  dark_spots: "DARK_SPOT",
  dullness: "DULLNESS",
  large_pores: "LARGE_PORES",
  blackheads: "BLACKHEADS",
  wrinkles: "WRINKLES",
  dehydration: "DEHYDRATION",
  redness: "REDNESS",
};

/** Normalize legacy budget codes to canonical seeded codes */
const BUDGET_NORMALIZE = {
  under_200k:  "UNDER_300",
  "200_500k":  "300_500",
  "500_1000k": "500_1000",
  premium:     "OVER_1000",
  under_300000: "UNDER_300",
  "300_500000": "300_500",
  "500_1000000": "500_1000",
};

function normalizeValue(map, val) {
  if (!val) return val;
  const lower = String(val).toLowerCase().replace(/\s+/g, "_");
  return map[lower] || map[val] || val;
}

function normalizeArray(map, arr) {
  if (!Array.isArray(arr)) return arr;
  return [...new Set(arr.map((v) => normalizeValue(map, v)).filter(Boolean))];
}

/** Returns true if a field value counts as "unknown"/empty and should be nulled */
function isUnknownSentinel(val) {
  if (val == null) return true;
  if (typeof val === "string") {
    const lower = val.trim().toLowerCase();
    return lower === "unknown" || lower === "" || lower === "no_preference";
  }
  return false;
}

// ─── Profile analysis ─────────────────────────────────────────────────────────

/**
 * Returns a full description of what would be migrated for a single profile.
 */
async function analyzeProfile(raw) {
  const report = {
    _id: String(raw._id),
    customer_id: String(raw.customer_id),
    legacyFieldsFound: [],
    valueNormalizations: [],
    unknownValues: [],
    shadePreferenceBackup: null,
    invalidCodes: [],
    fieldMappings: [],
    conflictNotes: [],
    requiresMigration: false,
  };

  // ── Legacy field detection ──
  if (raw.skin_tone !== undefined)          report.legacyFieldsFound.push("skin_tone");
  if (raw.undertone !== undefined)          report.legacyFieldsFound.push("undertone");
  if (raw.finish_preference !== undefined)  report.legacyFieldsFound.push("finish_preference");
  if (raw.lip_color_preference !== undefined) report.legacyFieldsFound.push("lip_color_preference");
  if (raw.makeup_style !== undefined)       report.legacyFieldsFound.push("makeup_style");
  if (raw.budget_range !== undefined)       report.legacyFieldsFound.push("budget_range");
  if (raw.shade_preference !== undefined) {
    report.legacyFieldsFound.push("shade_preference");
    report.shadePreferenceBackup = raw.shade_preference;
    report.conflictNotes.push("shade_preference has no canonical mapping — will be backed up and unset");
  }

  // ── Lowercase value detection ──
  if (raw.skin_type && raw.skin_type !== raw.skin_type.toUpperCase() && SKIN_TYPE_NORMALIZE[raw.skin_type.toLowerCase()]) {
    report.valueNormalizations.push({ field: "skin_type", from: raw.skin_type, to: SKIN_TYPE_NORMALIZE[raw.skin_type.toLowerCase()] });
  }
  if (raw.sensitivity_level && SENSITIVITY_NORMALIZE[raw.sensitivity_level.toLowerCase()]) {
    report.valueNormalizations.push({ field: "sensitivity_level", from: raw.sensitivity_level, to: SENSITIVITY_NORMALIZE[raw.sensitivity_level.toLowerCase()] });
  }
  if (Array.isArray(raw.skin_concerns)) {
    raw.skin_concerns.forEach((v) => {
      const normalized = CONCERN_NORMALIZE[String(v).toLowerCase()];
      if (normalized && normalized !== v) {
        report.valueNormalizations.push({ field: "skin_concerns[]", from: v, to: normalized });
      }
    });
  }
  if (raw.budget && BUDGET_NORMALIZE[String(raw.budget).toLowerCase()]) {
    const to = BUDGET_NORMALIZE[raw.budget.toLowerCase()];
    if (to !== raw.budget) {
      report.valueNormalizations.push({ field: "budget", from: raw.budget, to });
    }
  }

  // ── Unknown sentinel detection ──
  const NULLABLE_SINGLES = ["skin_type", "skin_color", "skin_undertone", "sensitivity_level",
    "foundation_finish", "budget", "fragrance_preference"];
  for (const f of NULLABLE_SINGLES) {
    if (isUnknownSentinel(raw[f]) && raw[f] !== null && raw[f] !== undefined) {
      report.unknownValues.push({ field: f, value: raw[f] });
    }
  }

  // ── finish_preference conflict policy ──
  if (Array.isArray(raw.finish_preference) && raw.finish_preference.length > 1) {
    report.conflictNotes.push(
      `finish_preference has ${raw.finish_preference.length} values [${raw.finish_preference.join(", ")}] — only index[0]="${raw.finish_preference[0]}" will be migrated to foundation_finish`
    );
  }

  // ── Code validity checks ──
  const codeCheckFields = [
    { field: "skin_type", group: "skin_type" },
    { field: "sensitivity_level", group: "sensitivity_level" },
    { field: "skin_color", group: "skin_color" },
    { field: "skin_undertone", group: "skin_undertone" },
    { field: "foundation_finish", group: "foundation_finish" },
    { field: "budget", group: "budget" },
  ];
  for (const { field, group } of codeCheckFields) {
    const val = raw[field];
    if (val && !isUnknownSentinel(val)) {
      const normalizedVal = normalizeValue(SKIN_TYPE_NORMALIZE, val) || val;
      const valid = await isValidCode(group, normalizedVal);
      if (!valid) {
        report.invalidCodes.push({ field, value: val, group });
      }
    }
  }

  if (
    report.legacyFieldsFound.length > 0 ||
    report.valueNormalizations.length > 0 ||
    report.unknownValues.length > 0 ||
    report.invalidCodes.length > 0
  ) {
    report.requiresMigration = true;
  }

  return report;
}

/**
 * Build the $set and $unset update objects for a single profile.
 */
function buildUpdate(raw, report) {
  const $set = {};
  const $unset = {};

  // ── Field mappings ──
  if (raw.skin_tone !== undefined && raw.skin_color === undefined) {
    $set.skin_color = raw.skin_tone;
    $unset.skin_tone = "";
    report.fieldMappings.push("skin_tone → skin_color");
  }
  if (raw.undertone !== undefined && raw.skin_undertone === undefined) {
    $set.skin_undertone = raw.undertone;
    $unset.undertone = "";
    report.fieldMappings.push("undertone → skin_undertone");
  }
  if (raw.finish_preference !== undefined && raw.foundation_finish === undefined) {
    $set.foundation_finish = Array.isArray(raw.finish_preference)
      ? raw.finish_preference[0] || null
      : raw.finish_preference || null;
    $unset.finish_preference = "";
    report.fieldMappings.push(`finish_preference → foundation_finish (value: ${$set.foundation_finish})`);
  }
  if (raw.lip_color_preference !== undefined && raw.lipstick_colors === undefined) {
    $set.lipstick_colors = Array.isArray(raw.lip_color_preference)
      ? raw.lip_color_preference
      : [raw.lip_color_preference].filter(Boolean);
    $unset.lip_color_preference = "";
    report.fieldMappings.push("lip_color_preference → lipstick_colors");
  }
  if (raw.makeup_style !== undefined && raw.makeup_styles === undefined) {
    $set.makeup_styles = Array.isArray(raw.makeup_style)
      ? raw.makeup_style
      : [raw.makeup_style].filter(Boolean);
    $unset.makeup_style = "";
    report.fieldMappings.push("makeup_style → makeup_styles");
  }
  if (raw.budget_range !== undefined && raw.budget === undefined) {
    $set.budget = normalizeValue(BUDGET_NORMALIZE, raw.budget_range) || raw.budget_range;
    $unset.budget_range = "";
    report.fieldMappings.push(`budget_range → budget (value: ${$set.budget})`);
  }
  // shade_preference: back up only (no canonical mapping)
  if (raw.shade_preference !== undefined) {
    $unset.shade_preference = "";
    report.fieldMappings.push("shade_preference → (backed up to audit, unset)");
  }

  // ── Value normalizations ──
  if (raw.skin_type && SKIN_TYPE_NORMALIZE[String(raw.skin_type).toLowerCase()]) {
    $set.skin_type = SKIN_TYPE_NORMALIZE[String(raw.skin_type).toLowerCase()];
  }
  if (raw.sensitivity_level && SENSITIVITY_NORMALIZE[String(raw.sensitivity_level).toLowerCase()]) {
    $set.sensitivity_level = SENSITIVITY_NORMALIZE[String(raw.sensitivity_level).toLowerCase()];
  }
  if (Array.isArray(raw.skin_concerns)) {
    $set.skin_concerns = normalizeArray(CONCERN_NORMALIZE, raw.skin_concerns);
  }
  if (raw.budget && BUDGET_NORMALIZE[String(raw.budget).toLowerCase()]) {
    $set.budget = BUDGET_NORMALIZE[String(raw.budget).toLowerCase()];
  }

  // ── Unknown sentinel → null ──
  const NULLABLE_SINGLES = ["skin_type", "skin_color", "skin_undertone", "sensitivity_level",
    "foundation_finish", "budget", "fragrance_preference"];
  for (const f of NULLABLE_SINGLES) {
    const currentVal = $set[f] !== undefined ? $set[f] : raw[f];
    if (isUnknownSentinel(currentVal) && currentVal !== null) {
      $set[f] = null;
    }
  }

  $set.source = "migration";
  $set.migrated_at = new Date();

  return { $set, $unset };
}

// ─── Main ─────────────────────────────────────────────────────────────────────

async function migrate() {
  await mongoose.connect(process.env.MONGODB_URI || process.env.MONGO_URI);
  console.log(`\n📦 Connected to MongoDB — database: "${mongoose.connection.name}"`);
  console.log(`📋 Collection: customer_beauty_profiles`);
  console.log(`🔧 Mode: ${isDryRun ? "DRY-RUN (no writes)" : "APPLY (will write to database)"}`);
  console.log(`🏷  Migration version: ${MIGRATION_VERSION}\n`);

  // Raw access to audit collection (avoids schema overhead)
  auditCollection = mongoose.connection.db.collection("beauty_profile_migration_audit");

  await ensureCache();
  console.log("✅ BeautyReference cache loaded.\n");

  const profiles = await CustomerBeautyProfile.find({}).lean();
  console.log(`📊 Total profiles scanned: ${profiles.length}`);

  const stats = {
    total: profiles.length,
    alreadyCanonical: 0,
    requiresMigration: 0,
    successfullyMigrated: 0,
    errors: 0,
    shadePreferenceCount: 0,
    invalidCodeCount: 0,
    legacyFieldCounts: {},
    snapshotsToInvalidate: 0,
  };

  const errorDetails = [];

  for (const raw of profiles) {
    try {
      const report = await analyzeProfile(raw);

      // Count legacy fields
      for (const f of report.legacyFieldsFound) {
        stats.legacyFieldCounts[f] = (stats.legacyFieldCounts[f] || 0) + 1;
      }
      if (report.shadePreferenceBackup != null) stats.shadePreferenceCount++;
      stats.invalidCodeCount += report.invalidCodes.length;

      if (!report.requiresMigration) {
        stats.alreadyCanonical++;
        continue;
      }

      stats.requiresMigration++;

      if (isDryRun) {
        // Print what would happen
        console.log(`\n[DRY-RUN] Profile ${raw._id} (customer: ${raw.customer_id})`);
        if (report.legacyFieldsFound.length)  console.log(`  Legacy fields: ${report.legacyFieldsFound.join(", ")}`);
        if (report.valueNormalizations.length) {
          report.valueNormalizations.forEach((n) =>
            console.log(`  Normalize: ${n.field}: "${n.from}" → "${n.to}"`));
        }
        if (report.unknownValues.length) {
          report.unknownValues.forEach((u) =>
            console.log(`  Unknown sentinel: ${u.field}="${u.value}" → null`));
        }
        if (report.invalidCodes.length) {
          report.invalidCodes.forEach((ic) =>
            console.log(`  ⚠ Invalid code: ${ic.field}="${ic.value}" (group: ${ic.group})`));
        }
        if (report.conflictNotes.length) {
          report.conflictNotes.forEach((n) => console.log(`  ℹ  ${n}`));
        }
        if (report.shadePreferenceBackup) {
          console.log(`  🔒 shade_preference will be backed up: ${JSON.stringify(report.shadePreferenceBackup)}`);
        }
        stats.successfullyMigrated++;
        continue;
      }

      // ── APPLY mode ──

      // 1. Write backup to audit collection BEFORE any modification
      await auditCollection.updateOne(
        { customer_id: raw.customer_id, migration_version: MIGRATION_VERSION },
        {
          $setOnInsert: {
            customer_id: raw.customer_id,
            original_snapshot: raw,
            migration_version: MIGRATION_VERSION,
            migrated_at: new Date(),
          },
        },
        { upsert: true }
      );

      // 2. Build update operations
      const { $set, $unset } = buildUpdate(raw, report);

      // 3. Apply via Mongoose Document (triggers pre("validate") hook → hash + completion)
      const doc = await CustomerBeautyProfile.findById(raw._id);
      if (!doc) {
        throw new Error(`Document ${raw._id} not found during apply step`);
      }

      for (const [k, v] of Object.entries($set)) {
        doc[k] = v;
      }

      // Save triggers pre("validate"): BeautyReference validation + hash recalculation
      await doc.save();

      // 4. $unset legacy fields (schema doesn't contain them, so we use raw collection)
      if (Object.keys($unset).length > 0) {
        await CustomerBeautyProfile.collection.updateOne(
          { _id: raw._id },
          { $unset }
        );
      }

      // 5. Update audit record with final status
      await auditCollection.updateOne(
        { customer_id: raw.customer_id, migration_version: MIGRATION_VERSION },
        {
          $set: {
            status: "migrated",
            field_mappings: report.fieldMappings,
            conflict_notes: report.conflictNotes,
            shade_preference_backup: report.shadePreferenceBackup,
          },
        }
      );

      stats.successfullyMigrated++;
    } catch (err) {
      stats.errors++;
      errorDetails.push({ _id: String(raw._id), error: err.message });
      console.error(`❌ Error processing profile ${raw._id}: ${err.message}`);

      if (isApply && auditCollection) {
        try {
          await auditCollection.updateOne(
            { customer_id: raw.customer_id, migration_version: MIGRATION_VERSION },
            { $set: { status: "error", error_message: err.message } }
          );
        } catch (_) {}
      }
    }
  }

  // ── Final Summary ──
  console.log("\n═══════════════════════════════════════════════════════════");
  console.log(`✅ Migration ${isDryRun ? "DRY-RUN" : "APPLY"} complete — ${MIGRATION_VERSION}`);
  console.log(`   Database:            ${mongoose.connection.name}`);
  console.log(`   Collection:          customer_beauty_profiles`);
  console.log(`   Total scanned:       ${stats.total}`);
  console.log(`   Already canonical:   ${stats.alreadyCanonical}`);
  console.log(`   Require migration:   ${stats.requiresMigration}`);
  console.log(`   Successfully handled:${stats.successfullyMigrated}`);
  console.log(`   Errors:              ${stats.errors}`);
  console.log(`   Invalid codes found: ${stats.invalidCodeCount}`);
  console.log(`   shade_preference:    ${stats.shadePreferenceCount} (backed up)`);

  if (Object.keys(stats.legacyFieldCounts).length) {
    console.log("\n   Legacy field breakdown:");
    for (const [f, c] of Object.entries(stats.legacyFieldCounts)) {
      console.log(`     ${f.padEnd(25)} ${c} documents`);
    }
  }

  if (errorDetails.length) {
    console.log("\n   Failed documents:");
    errorDetails.forEach((e) => console.log(`     ${e._id}: ${e.error}`));
  }

  if (isDryRun) {
    console.log("\n⚠  DRY-RUN — no changes were written to the database.");
    console.log("   Re-run with --apply to execute migration.");
  } else {
    console.log("\n🔒 Audit records written to: beauty_profile_migration_audit");
    console.log("   Rollback: restore from audit.original_snapshot for each customer_id.");
  }
  console.log("═══════════════════════════════════════════════════════════\n");

  await mongoose.disconnect();
  process.exit(stats.errors > 0 ? 1 : 0);
}

migrate().catch((err) => {
  console.error("Fatal migration error:", err.message);
  process.exit(1);
});
