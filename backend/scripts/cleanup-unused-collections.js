/**
 * ⚠️  WARNING: This script DROPS MongoDB collections.
 * ⚠️  By default it runs in DRY-RUN mode and prints what WOULD be dropped.
 * ⚠️  Pass --apply to actually execute the drops.
 * ⚠️  NEVER run --apply in a production environment without a backup.
 * ⚠️  Do NOT drop any collection used by a current Mongoose model.
 * ⚠️  Do NOT drop any collection that has live data unless explicitly confirmed.
 *
 * Usage:
 *   node scripts/cleanup-unused-collections.js            # dry-run
 *   node scripts/cleanup-unused-collections.js --apply    # actually drops
 *   node scripts/cleanup-unused-collections.js --confirm-has-data  # include legacy collections with docs
 */

require('dotenv').config({ path: require('path').join(__dirname, '..', '.env') });
const mongoose = require('mongoose');

const MONGO_URI = process.env.MONGO_URI || process.env.MONGODB_URI;
const DRY_RUN = !process.argv.includes('--apply');
const INCLUDE_HAS_DATA = process.argv.includes('--confirm-has-data');

// ── Classification of collections to drop ─────────────────────────────────────
// TIER 1: Safe empty ghost collections created by old Mongoose inferred pluralization.
// No current model writes to these. The canonical snake_case collections are correct.
const EMPTY_GHOST_COLLECTIONS = [
  'cartitems',               // ghost of CartItem (canonical: cart_items)
  'checkoutaddresses',       // ghost of CheckoutAddress (canonical: checkout_addresses)
  'checkoutsessions',        // ghost of CheckoutSession (canonical: checkout_sessions)
  'checkoutshippingmethods', // ghost of CheckoutShippingMethod (canonical: checkout_shipping_methods)
  'paymentmethods',          // ghost of PaymentMethod (canonical: payment_methods)
  'shippingmethods',         // ghost of ShippingMethod (canonical: shipping_methods)
  'inventorybalances',       // ghost of InventoryBalance (canonical: inventory_balances)
  'inventorytransactions',   // ghost of InventoryTransaction (canonical: inventory_transactions)
  'loyaltyaccounts',         // ghost of LoyaltyAccount (canonical: loyalty_accounts)
  'loyaltypointledgers',     // ghost of LoyaltyPointLedger (canonical: loyalty_point_ledger)
  'loyaltytiers',            // ghost of LoyaltyTier (canonical: loyalty_tiers)
  'paymentintents',          // ghost of PaymentIntent (canonical: payment_intents)
  'paymenttransactions',     // ghost of PaymentTransaction (canonical: payment_transactions)
  'pricebookentries',        // ghost of PriceBookEntry (canonical: price_book_entries)
  'pricehistories',          // ghost of PriceHistory (canonical: price_histories)
  'productattributes',       // ghost of ProductAttribute (canonical: product_attributes)
  'productcategories',       // ghost of ProductCategory (canonical: product_categories)
  'productmedias',           // ghost of ProductMedia (canonical: product_media)
  'productoptions',          // ghost of ProductOption (canonical: product_options)
  'productoptionvalues',     // ghost of ProductOptionValue (canonical: product_option_values)
  'productvariants',         // ghost of ProductVariant (canonical: product_variants)
  'promotionrules',          // ghost of PromotionRule (canonical: promotion_rules)
  'reviewmedias',            // ghost of ReviewMedia (canonical: review_medias)
  'reviewsummaries',         // ghost of ReviewSummary (canonical: review_summaries/review_summary)
  'reviewvotes',             // ghost of ReviewVote (canonical: review_votes)
  'shipmentevents',          // ghost of ShipmentEvent (canonical: shipment_events)
  'shipmentitems',           // ghost of ShipmentItem (canonical: shipment_items)
  'shipments',               // ghost — NOTE: if Shipment model still has NO explicit collection and 0 docs, safe to keep and reseed
  'stockreservations',       // ghost of StockReservation (canonical: stock_reservations)
  'variantmedias',           // ghost of VariantMedia (canonical: variant_medias)
  'variantoptionvalues',     // ghost of VariantOptionValue (canonical: variant_option_values)
  'warehouses',              // ghost — Warehouse model has 0 docs, will be recreated by data.js after model fix
  'wishlists',               // ghost — Wishlist model has 0 docs (canonical: wishlists after explicit collection: 'wishlists')
  'wishlistitems',           // ghost of WishlistItem (canonical: wishlist_items)
  'products',                // ghost — Product model 0 docs (after data.js rerun with correct model, this will be reseeded)
];

// TIER 2: Legacy collections with data from old seed scripts.
// These require --confirm-has-data flag to drop.
// Confirm with owner first. The canonical collection has more/better data.
const LEGACY_WITH_DATA_COLLECTIONS = [
  { name: 'addresses', docs: 10, reason: 'Legacy from seed-data.js; canonical is customer_addresses (46 docs)' },
  { name: 'customers', docs: 15, reason: 'Legacy from seed-data.js; canonical is customer_profiles (23 docs)' },
  { name: 'password_reset_otps', docs: 2, reason: 'Deprecated password reset flow; system is now passwordless' },
];

// TIER 3: Duplicate old-seed camelPlural collections that had some data
// but the canonical snake_case collection is the authoritative source.
// Require --confirm-has-data to drop.
const DUPLICATE_WITH_DATA_COLLECTIONS = [
  { name: 'couponredemptions', docs: 20, reason: 'Old seed data; canonical is coupon_redemptions (once model is fixed with explicit collection)' },
  { name: 'pricebooks', docs: 3, reason: 'Old data.js seed with wrong inferred name; canonical is price_books (5 docs)' },
  { name: 'promotiontargets', docs: 20, reason: 'Old data.js seed with wrong inferred name; canonical is promotion_targets (50 docs)' },
];

// ── Safety check: never drop these regardless of flags ─────────────────────────
const PROTECTED_COLLECTIONS = new Set([
  'accounts', 'account_auth_providers', 'account_roles',
  'customer_addresses', 'admin_profiles', 'audit_logs', 'beauty_references',
  'brands', 'cart_items', 'carts', 'categories', 'checkout_addresses',
  'checkout_sessions', 'checkout_shipping_methods', 'coupons',
  'coupon_redemptions', 'customer_beauty_profiles', 'customer_consents',
  'customer_coupons', 'customer_preferences', 'customer_profiles',
  'customer_recommendation_snapshots', 'email_otps', 'guest_sessions',
  'inventory_balances', 'inventory_transactions',
  'loyalty_accounts', 'loyalty_point_ledger', 'loyalty_tiers',
  'orders', 'order_addresses', 'order_items', 'order_status_history',
  'order_totals', 'payment_intents', 'payment_methods', 'payment_transactions',
  'permissions', 'price_book_entries', 'price_books', 'price_histories',
  'product_attributes', 'product_beauty_profiles', 'product_categories',
  'product_media', 'product_options', 'product_option_values', 'product_variants',
  'products', // keep in PROTECTED after seeding
  'promotions', 'promotion_rules', 'promotion_targets',
  'recommendation_logs', 'refunds', 'return_items', 'returns',
  'review_medias', 'review_summary', 'review_votes', 'reviews',
  'role_permissions', 'roles', 'shipment_events', 'shipment_items', 'shipments',
  'shipping_methods', 'stock_reservations', 'variant_medias', 'variant_option_values',
  'warehouses', 'wishlist_items', 'wishlists',
]);

async function dropCollection(db, name, docs, reason) {
  if (PROTECTED_COLLECTIONS.has(name)) {
    console.log(`  🛡️  PROTECTED — skip ${name} (${docs} docs)`);
    return;
  }

  const label = DRY_RUN ? '[DRY-RUN]' : '[DROP]';
  console.log(`  ${label} ${name} (${docs} docs) — ${reason}`);

  if (!DRY_RUN) {
    try {
      await db.dropCollection(name);
      console.log(`    ✅ Dropped ${name}`);
    } catch (err) {
      console.warn(`    ⚠️  Could not drop ${name}: ${err.message}`);
    }
  }
}

async function main() {
  if (!MONGO_URI) {
    console.error('❌ Missing MONGO_URI in .env');
    process.exit(1);
  }

  console.log('═══════════════════════════════════════════');
  console.log('  KANILA — Cleanup Unused Collections');
  if (DRY_RUN) {
    console.log('  MODE: DRY-RUN (pass --apply to execute)');
  } else {
    console.log('  ⚠️  MODE: APPLY — Collections will be DROPPED');
  }
  console.log('═══════════════════════════════════════════\n');

  await mongoose.connect(MONGO_URI, { serverSelectionTimeoutMS: 15000 });
  const db = mongoose.connection.db;
  const existing = new Set((await db.listCollections().toArray()).map(c => c.name));

  // Count docs in each candidate
  const countOf = async (name) => {
    if (!existing.has(name)) return 0;
    return db.collection(name).countDocuments();
  };

  console.log('── TIER 1: Empty ghost collections ──');
  for (const name of EMPTY_GHOST_COLLECTIONS) {
    if (!existing.has(name)) continue;
    const docs = await countOf(name);
    if (docs > 0) {
      console.log(`  ⚠️  SKIP ${name} — has ${docs} docs (add --confirm-has-data to include)`);
      continue;
    }
    await dropCollection(db, name, docs, 'Empty ghost from old Mongoose inferred pluralization');
  }

  if (INCLUDE_HAS_DATA) {
    console.log('\n── TIER 2: Legacy collections with data (--confirm-has-data active) ──');
    for (const { name, docs, reason } of LEGACY_WITH_DATA_COLLECTIONS) {
      if (!existing.has(name)) continue;
      const actualDocs = await countOf(name);
      await dropCollection(db, name, actualDocs, reason);
    }

    console.log('\n── TIER 3: Old-seed duplicate collections with data ──');
    for (const { name, docs, reason } of DUPLICATE_WITH_DATA_COLLECTIONS) {
      if (!existing.has(name)) continue;
      const actualDocs = await countOf(name);
      await dropCollection(db, name, actualDocs, reason);
    }
  } else {
    console.log('\nℹ️  Tier 2 & 3 (legacy collections with data) skipped.');
    console.log('   Add --confirm-has-data after --apply to include them.');
    console.log('   Collections with data that would be affected:');
    for (const { name, docs, reason } of [...LEGACY_WITH_DATA_COLLECTIONS, ...DUPLICATE_WITH_DATA_COLLECTIONS]) {
      if (existing.has(name)) {
        const actualDocs = await countOf(name);
        console.log(`     ${name} (${actualDocs} docs) — ${reason}`);
      }
    }
  }

  console.log('\n═══════════════════════════════════════════');
  if (DRY_RUN) {
    console.log('  Dry-run complete. No collections were dropped.');
    console.log('  To apply: node scripts/cleanup-unused-collections.js --apply');
    console.log('  With data: node scripts/cleanup-unused-collections.js --apply --confirm-has-data');
  } else {
    console.log('  Cleanup complete.');
  }
  console.log('═══════════════════════════════════════════\n');

  await mongoose.disconnect();
}

main().catch(err => {
  console.error('Cleanup failed:', err.message);
  process.exit(1);
});
