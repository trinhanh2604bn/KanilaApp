/**
 * patch-model-collections.js
 * Adds explicit `collection: '...'` option to 28 Mongoose model schema calls.
 * Each patch inserts `{ timestamps: true, collection: 'snake_case_name' }` where
 * only `{ timestamps: true }` existed before, or adds a second options object
 * if the schema has no options at all.
 *
 * Run: node scripts/patch-model-collections.js
 * Safe to re-run — it skips files that already have `collection:` defined.
 */

const fs = require('fs');
const path = require('path');

const MODELS_DIR = path.join(__dirname, '..', 'models');

// Map: filename → canonical collection name
const PATCHES = {
  'inventoryBalance.model.js':    'inventory_balances',
  'inventoryTransaction.model.js':'inventory_transactions',
  'loyaltyAccount.model.js':      'loyalty_accounts',
  'loyaltyPointLedger.model.js':  'loyalty_point_ledger',
  'loyaltyTier.model.js':         'loyalty_tiers',
  'paymentIntent.model.js':       'payment_intents',
  'paymentTransaction.model.js':  'payment_transactions',
  'priceBook.model.js':           'price_books',
  'priceBookEntry.model.js':      'price_book_entries',
  'priceHistory.model.js':        'price_histories',
  'productAttribute.model.js':    'product_attributes',
  'productCategory.model.js':     'product_categories',
  'productMedia.model.js':        'product_media',
  'productOption.model.js':       'product_options',
  'productOptionValue.model.js':  'product_option_values',
  'productVariant.model.js':      'product_variants',
  'promotionRule.model.js':       'promotion_rules',
  'promotionTarget.model.js':     'promotion_targets',
  'couponRedemption.model.js':    'coupon_redemptions',
  'reviewMedia.model.js':         'review_medias',
  'reviewSummary.model.js':       'review_summaries',
  'reviewVote.model.js':          'review_votes',
  'shipmentEvent.model.js':       'shipment_events',
  'shipmentItem.model.js':        'shipment_items',
  'stockReservation.model.js':    'stock_reservations',
  'variantMedia.model.js':        'variant_medias',
  'variantOptionValue.model.js':  'variant_option_values',
  'wishlistItem.model.js':        'wishlist_items',
};

let patched = 0;
let skipped = 0;
let errors = 0;

for (const [file, collectionName] of Object.entries(PATCHES)) {
  const filePath = path.join(MODELS_DIR, file);

  if (!fs.existsSync(filePath)) {
    console.warn(`  ⚠️  Not found: ${file}`);
    errors++;
    continue;
  }

  let content = fs.readFileSync(filePath, 'utf8');

  // Already has collection: option — skip
  if (/collection\s*:/.test(content)) {
    console.log(`  ✅ Already has collection: — skip ${file}`);
    skipped++;
    continue;
  }

  // Strategy 1: Replace `{ timestamps: true }` with `{ timestamps: true, collection: '...' }`
  // This handles the most common pattern.
  const tsPattern = /\{\s*timestamps\s*:\s*true\s*\}/g;
  if (tsPattern.test(content)) {
    content = content.replace(
      /\{\s*timestamps\s*:\s*true\s*\}/g,
      `{ timestamps: true, collection: '${collectionName}' }`
    );
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`  📝 Patched (timestamps) ${file} → ${collectionName}`);
    patched++;
    continue;
  }

  // Strategy 2: No timestamps option — the schema is passed without options object.
  // Find `mongoose.model('ModelName', someSchema)` and add an options object before closing paren.
  // Also handle `new mongoose.Schema({...})` with no second arg.
  const schemaNoOpts = /new mongoose\.Schema\(\s*(\{[\s\S]*?\})\s*\)\s*;/;
  if (schemaNoOpts.test(content)) {
    content = content.replace(
      /new mongoose\.Schema\(\s*(\{[\s\S]*?\})\s*\)\s*;/,
      (match, schemaBody) => `new mongoose.Schema(\n  ${schemaBody.trim()},\n  { collection: '${collectionName}' }\n);`
    );
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`  📝 Patched (no-opts) ${file} → ${collectionName}`);
    patched++;
    continue;
  }

  console.warn(`  ❌ Could not auto-patch ${file} — please add collection: '${collectionName}' manually`);
  errors++;
}

console.log(`\nDone. Patched: ${patched}, Already had collection: ${skipped}, Errors/manual: ${errors}`);
