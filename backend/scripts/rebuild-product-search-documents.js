#!/usr/bin/env node
"use strict";

/**
 * scripts/rebuild-product-search-documents.js
 *
 * Idempotent script to sync all active makeup products into product_search_documents.
 * Safe to run multiple times. Uses bulkWrite upsert by product_id.
 *
 * Usage:
 *   node scripts/rebuild-product-search-documents.js [--limit=N] [--productId=<id>]
 *
 * Options:
 *   --limit=N       Process at most N products (useful for testing)
 *   --productId=id  Rebuild only one specific product
 *
 * Environment:
 *   MONGO_URI — MongoDB connection string
 */

require("dotenv").config();
const mongoose = require("mongoose");

const Product               = require("../models/product.model");
const ProductVariant        = require("../models/productVariant.model");
const ProductCategory       = require("../models/productCategory.model");
const ProductSearchDocument = require("../models/productSearchDocument.model");
const SearchQueryNormalizer = require("../utils/searchQueryNormalizer");

// Brand and Category models are registered by product.model require
require("../models/brand.model");
require("../models/category.model");
require("../models/productAttribute.model");

const BATCH_SIZE = 50;

// ─── Arg parsing ──────────────────────────────────────────────────────────────
const args = process.argv.slice(2);
const argMap = Object.fromEntries(
  args.map((a) => a.replace(/^--/, "").split("="))
);
const LIMIT_ARG       = argMap.limit ? parseInt(argMap.limit, 10) : null;
const PRODUCT_ID_ARG  = argMap.productId || null;

// ─── Vietnamese fold helper ───────────────────────────────────────────────────
function fold(str) {
  if (!str) return "";
  const normalized = SearchQueryNormalizer.normalize(str);
  return normalized.folded;
}

function foldArr(arr) {
  return (arr || []).filter(Boolean).map(fold);
}

// ─── Map a product + related data → ProductSearchDocument fields ──────────────
async function buildDoc(product, Brand, Category) {
  // Brand
  const brand = product.brandId && product.brandId._id
    ? product.brandId
    : (await Brand.findById(product.brandId).lean().catch(() => null));

  const brandName     = brand?.brandName || brand?.brand_name || "";
  const brandCode     = brand?.brandCode || brand?.brand_code || "";

  // Categories
  const productCats = await ProductCategory.find({ productId: product._id })
    .populate("categoryId")
    .lean()
    .catch(() => []);

  const categories = productCats
    .map((pc) => pc.categoryId)
    .filter(Boolean);

  const categoryIds    = categories.map((c) => c._id);
  const categoryNames  = categories.map((c) => c.categoryName || c.name || "");
  const categoryCodes  = categories.map((c) => c.categoryCode || c.code || "");

  // Also include primary category
  if (product.categoryId && !categoryIds.some((id) => id.equals(product.categoryId))) {
    const primaryCat = await Category.findById(product.categoryId).lean().catch(() => null);
    if (primaryCat) {
      categoryIds.unshift(primaryCat._id);
      categoryNames.unshift(primaryCat.categoryName || primaryCat.name || "");
      categoryCodes.unshift(primaryCat.categoryCode || primaryCat.code || "");
    }
  }

  // Variants
  const variants = await ProductVariant.find({
    productId: product._id,
    variantStatus: "active",
  }).lean().catch(() => []);

  const variantIds    = variants.map((v) => v._id);
  const variantNames  = variants.map((v) => v.variantName || "").filter(Boolean);
  const variantSkus   = variants.map((v) => v.sku || "").filter(Boolean);
  const barcodes      = variants.map((v) => v.barcode || "").filter((b) => b.length > 0);

  // Shades — from product.shades[]
  const shades        = product.shades || [];
  const shadeNames    = shades.map((s) => s.name || "").filter(Boolean);
  const colorHexValues = shades.map((s) => s.hex || "").filter(Boolean);
  const shadeCodes    = shades.map((s) => s.code || s.shadeCode || "").filter(Boolean);

  // Color families derived from shade names (heuristic: first word of shade name)
  const colorFamilies = [...new Set(
    shadeNames
      .map((n) => n.split(/\s+/)[0].toLowerCase())
      .filter((n) => n.length > 2)
  )];

  // Makeup attributes from Product fields
  const finishTypes    = product.finish_type
    ? [product.finish_type.toLowerCase()]
    : [];
  const coverageLevels = product.coverage_type
    ? [product.coverage_type.toLowerCase()]
    : [];

  // Boolean commercial claims
  const waterproof        = product.ingredient_flags?.includes("waterproof") || null;
  const transferProof     = product.ingredient_flags?.includes("transfer_proof") || null;
  const longWear          = product.ingredient_flags?.includes("long_wear") || null;
  const smudgeProof       = product.ingredient_flags?.includes("smudge_proof") || null;
  const oilControl        = product.ingredient_flags?.includes("oil_control") || null;
  const sensitiveFriendly = product.is_sensitive_friendly || null;
  const crueltyfree       = product.ingredient_flags?.includes("cruelty_free") || null;
  const fragranceFree     = product.ingredient_flags?.includes("fragrance_free") || null;

  // Commerce
  const price           = product.price || 0;
  const compareAtPrice  = product.compareAtPrice || null;
  const discountPct     = (compareAtPrice && compareAtPrice > price)
    ? Math.round(((compareAtPrice - price) / compareAtPrice) * 100)
    : null;
  const averageRating   = product.averageRating || 0;
  const salesCount      = product.sales_count || product.bought || 0;
  const stock           = product.stock || 0;
  const inStock         = stock > 0;
  const isBestSeller    = product.is_best_seller || false;
  const isOnSale        = discountPct != null && discountPct > 0;
  const isNew           = product.createdAt
    ? (Date.now() - new Date(product.createdAt).getTime()) < 30 * 24 * 60 * 60 * 1000
    : false;

  // Review count from product.bought (approximate until review_summary is integrated)
  const reviewCount = product.reviewCount || 0;

  // AR
  const arSupported = product.hasAr || false;

  // Skin tones / undertones from tone_match_supported
  const skinTones  = product.tone_match_supported || [];
  const undertones = [];

  return {
    product_id:                product._id,
    product_name:              product.productName || "",
    product_name_normalized:   fold(product.productName),
    product_code:              product.productCode || "",
    slug:                      product.slug || "",
    brand_id:                  brand?._id || null,
    brand_name:                brandName,
    brand_name_normalized:     fold(brandName),
    brand_code:                brandCode,
    category_ids:              categoryIds,
    category_names:            categoryNames,
    category_names_normalized: foldArr(categoryNames),
    category_codes:            categoryCodes,
    variant_ids:               variantIds,
    variant_names:             variantNames,
    variant_names_normalized:  foldArr(variantNames),
    variant_skus:              variantSkus,
    barcodes,
    shade_names:               shadeNames,
    shade_names_normalized:    foldArr(shadeNames),
    shade_codes:               shadeCodes,
    color_families:            colorFamilies,
    color_hex_values:          colorHexValues,
    finish_types:              finishTypes,
    coverage_levels:           coverageLevels,
    formula_types:             [],
    texture_types:             [],
    makeup_styles:             [],
    product_benefits:          product.concerns_targeted || [],
    product_claims:            product.ingredient_flags || [],
    attribute_terms:           [],
    product_tags:              [],
    skin_tones:                skinTones,
    undertones,
    waterproof,
    transfer_proof:            transferProof,
    long_wear:                 longWear,
    smudge_proof:              smudgeProof,
    oil_control:               oilControl,
    sensitive_friendly:        sensitiveFriendly,
    fragrance_free:            fragranceFree,
    cruelty_free:              crueltyfree,
    price,
    compare_at_price:          compareAtPrice,
    discount_percentage:       discountPct,
    average_rating:            averageRating,
    review_count:              reviewCount,
    sales_count:               salesCount,
    stock,
    in_stock:                  inStock,
    is_best_seller:            isBestSeller,
    is_new:                    isNew,
    is_on_sale:                isOnSale,
    ar_supported:              arSupported,
    primary_image_url:         product.imageUrl || "",
    product_status:            product.productStatus || "active",
    is_active:                 product.isActive !== false,
    source_updated_at:         product.updatedAt || new Date(),
    indexed_at:                new Date(),
    schema_version:            2,
  };
}

async function run() {
  console.log("[rebuild] Connecting to MongoDB…");
  await mongoose.connect(process.env.MONGO_URI, {
    serverSelectionTimeoutMS: 30000,
  });

  const Brand    = require("../models/brand.model");
  const Category = require("../models/category.model");

  const query = { productStatus: "active", isActive: true };
  if (PRODUCT_ID_ARG) {
    query._id = new mongoose.Types.ObjectId(PRODUCT_ID_ARG);
  }

  const totalProducts = await Product.countDocuments(query);
  const limit = LIMIT_ARG ? Math.min(LIMIT_ARG, totalProducts) : totalProducts;
  console.log(`[rebuild] Processing ${limit} of ${totalProducts} active products…`);

  let processed = 0;
  let inserted  = 0;
  let updated   = 0;
  let errors    = 0;

  for (let offset = 0; offset < limit; offset += BATCH_SIZE) {
    const batchLimit = Math.min(BATCH_SIZE, limit - offset);
    const products = await Product.find(query)
      .populate("brandId")
      .skip(offset)
      .limit(batchLimit)
      .lean();

    const ops = [];
    for (const product of products) {
      try {
        const doc = await buildDoc(product, Brand, Category);
        ops.push({
          updateOne: {
            filter: { product_id: doc.product_id },
            update:  { $set: doc },
            upsert:  true,
          },
        });
        processed++;
      } catch (e) {
        errors++;
        console.error(`[rebuild] Error mapping product ${product._id}:`, e.message);
      }
    }

    if (ops.length > 0) {
      try {
        const result = await ProductSearchDocument.bulkWrite(ops, { ordered: false });
        inserted += result.upsertedCount || 0;
        updated  += result.modifiedCount || 0;
      } catch (e) {
        console.error("[rebuild] bulkWrite error:", e.message);
      }
    }

    console.log(`[rebuild] Progress: ${Math.min(offset + BATCH_SIZE, limit)}/${limit}`);
  }

  // Mark inactive documents for products no longer active
  if (!PRODUCT_ID_ARG) {
    const activeProductIds = await Product.distinct("_id", query);
    const deactivated = await ProductSearchDocument.updateMany(
      { product_id: { $nin: activeProductIds }, is_active: true },
      { $set: { is_active: false, indexed_at: new Date() } }
    );
    console.log(`[rebuild] Deactivated ${deactivated.modifiedCount} stale search documents.`);
  }

  console.log(`\n[rebuild] ✅ Done.`);
  console.log(`  Processed: ${processed}`);
  console.log(`  Inserted:  ${inserted}`);
  console.log(`  Updated:   ${updated}`);
  console.log(`  Errors:    ${errors}`);

  await mongoose.disconnect();
}

run().catch((e) => {
  console.error("[rebuild] Fatal error:", e);
  process.exit(1);
});
