/**
 * fix-model-collection-names.js
 * ─────────────────────────────────────────────────────────────────────────────
 * Audit / verification script — lists all 28 models that were updated with an
 * explicit `collection:` option and confirms Mongoose resolves each model to
 * the expected snake_case collection name.
 *
 * Run:  node scripts/fix-model-collection-names.js
 *
 * No writes are performed by this script; it is read-only verification.
 * ─────────────────────────────────────────────────────────────────────────────
 */

"use strict";

const path = require("path");
require("dotenv").config({ path: path.join(__dirname, "../.env") });

// The 28 models that were updated and their expected canonical collection names
const EXPECTED = [
  ["InventoryBalance",    "inventory_balances"],
  ["InventoryTransaction","inventory_transactions"],
  ["LoyaltyAccount",      "loyalty_accounts"],
  ["LoyaltyPointLedger",  "loyalty_point_ledger"],
  ["LoyaltyTier",         "loyalty_tiers"],
  ["PaymentIntent",       "payment_intents"],
  ["PaymentTransaction",  "payment_transactions"],
  ["PriceBook",           "price_books"],
  ["PriceBookEntry",      "price_book_entries"],
  ["PriceHistory",        "price_histories"],
  ["ProductAttribute",    "product_attributes"],
  ["ProductCategory",     "product_categories"],
  ["ProductMedia",        "product_media"],
  ["ProductOption",       "product_options"],
  ["ProductOptionValue",  "product_option_values"],
  ["ProductVariant",      "product_variants"],
  ["PromotionRule",       "promotion_rules"],
  ["PromotionTarget",     "promotion_targets"],
  ["CouponRedemption",    "coupon_redemptions"],
  ["ReviewMedia",         "review_medias"],
  ["ReviewSummary",       "review_summary"],
  ["ReviewVote",          "review_votes"],
  ["ShipmentEvent",       "shipment_events"],
  ["ShipmentItem",        "shipment_items"],
  ["StockReservation",    "stock_reservations"],
  ["VariantMedia",        "variant_medias"],
  ["VariantOptionValue",  "variant_option_values"],
  ["WishlistItem",        "wishlist_items"],
];

// Require all models so Mongoose registers them
const modelsDir = path.join(__dirname, "../models");
const modelFiles = [
  "inventoryBalance.model.js",
  "inventoryTransaction.model.js",
  "loyaltyAccount.model.js",
  "loyaltyPointLedger.model.js",
  "loyaltyTier.model.js",
  "paymentIntent.model.js",
  "paymentTransaction.model.js",
  "priceBook.model.js",
  "priceBookEntry.model.js",
  "priceHistory.model.js",
  "productAttribute.model.js",
  "productCategory.model.js",
  "productMedia.model.js",
  "productOption.model.js",
  "productOptionValue.model.js",
  "productVariant.model.js",
  "promotionRule.model.js",
  "promotionTarget.model.js",
  "couponRedemption.model.js",
  "reviewMedia.model.js",
  "reviewSummary.model.js",
  "reviewVote.model.js",
  "shipmentEvent.model.js",
  "shipmentItem.model.js",
  "stockReservation.model.js",
  "variantMedia.model.js",
  "variantOptionValue.model.js",
  "wishlistItem.model.js",
];

modelFiles.forEach((f) => require(path.join(modelsDir, f)));

const mongoose = require("mongoose");

console.log("\n========== Model Collection Name Verification ==========\n");
console.log(
  `${"Model".padEnd(26)} ${"Expected".padEnd(30)} ${"Actual".padEnd(30)} ${"Status"}`
);
console.log("─".repeat(95));

let allPassed = true;

for (const [modelName, expectedCollection] of EXPECTED) {
  let actualCollection = "(model not found)";
  let status = "❌ FAIL";

  try {
    const Model = mongoose.model(modelName);
    actualCollection = Model.collection.collectionName;
    if (actualCollection === expectedCollection) {
      status = "✅ PASS";
    } else {
      allPassed = false;
    }
  } catch {
    allPassed = false;
  }

  console.log(
    `${modelName.padEnd(26)} ${expectedCollection.padEnd(30)} ${actualCollection.padEnd(30)} ${status}`
  );
}

console.log("\n" + "─".repeat(95));
console.log(
  allPassed
    ? "\n✅  All 28 models resolve to the correct collection names.\n"
    : "\n❌  One or more models do NOT resolve to the expected collection name. Review the table above.\n"
);

process.exit(allPassed ? 0 : 1);
