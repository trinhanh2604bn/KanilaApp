/**
 * dataAR_mock_real_ids.js
 *
 * Kanila AR mock seed generated from products_export.json.
 *
 * Scope:
 * - Uses the real product ObjectId values from the uploaded product export.
 * - Creates 4 mock shades for every product that can be mapped safely to the
 *   renderer types currently supported by Kanila: LIPS, CHEEKS and EYES.
 * - Disables hasAr for unsupported base/mixed products so Android does not
 *   silently fall back to LIPS.
 * - Upserts into product_ar_configs without resetting the rest of the database.
 *
 * IMPORTANT:
 * - variant_id values are deterministic MOCK strings because the supplied export
 *   contains product IDs but does not contain real product_variant IDs.
 * - Use these configs to test AR shade rendering. For production cart integration,
 *   replace variant_id with IDs from the canonical product_variants collection.
 *
 * Usage:
 *   node dataAR_mock_real_ids.js --self-test
 *   node dataAR_mock_real_ids.js --dry-run
 *   node dataAR_mock_real_ids.js
 *   node dataAR_mock_real_ids.js --reset
 *
 * Environment:
 *   MONGODB_URI or MONGO_URI or MONGODB_URL or DATABASE_URL
 */

'use strict';

try {
  require('dotenv').config();
} catch (error) {
  // dotenv is optional for --self-test and --dry-run.
}

let mongooseInstance = null;

function loadMongoose() {
  if (!mongooseInstance) {
    try {
      mongooseInstance = require('mongoose');
    } catch (error) {
      throw new Error(
        'Missing dependency "mongoose". Run "npm install mongoose dotenv" in the backend root.'
      );
    }
  }
  return mongooseInstance;
}

const SEED_BATCH = 'kanila_ar_mock_v2';
const CONFIG_COLLECTION = 'product_ar_configs';
const PRODUCT_COLLECTION = 'products';
const ALLOWED_AR_TYPES = new Set(['LIPS', 'CHEEKS', 'EYES']);
const HEX_RE = /^#[0-9A-Fa-f]{6}$/;

const AR_CONFIGS = [
  {
    "product_id": "000000000000000000000fb6",
    "product_name": "Maybelline Lash Sensational Sky High Mascara",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fb6_01",
        "variant_name": "Black",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.92,
        "price": 249000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fb6_02",
        "variant_name": "Brown Black",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 249000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/27201C/FFFFFF?text=Brown%20Black",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fb6_03",
        "variant_name": "Deep Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 249000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3B251D/FFFFFF?text=Deep%20Brown",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fb6_04",
        "variant_name": "Burgundy Brown",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.86,
        "price": 249000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/4A2028/FFFFFF?text=Burgundy%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fb7",
    "product_name": "Kiss Me Heroine Make Long & Curl Mascara Advanced Film",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fb7_01",
        "variant_name": "Black",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.92,
        "price": 329000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fb7_02",
        "variant_name": "Brown Black",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 329000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/27201C/FFFFFF?text=Brown%20Black",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fb7_03",
        "variant_name": "Deep Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 329000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3B251D/FFFFFF?text=Deep%20Brown",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fb7_04",
        "variant_name": "Burgundy Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.86,
        "price": 329000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/4A2028/FFFFFF?text=Burgundy%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fb8",
    "product_name": "Lancôme Lash Idôle Mascara",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fb8_01",
        "variant_name": "Black",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.92,
        "price": 880000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fb8_02",
        "variant_name": "Brown Black",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 880000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/27201C/FFFFFF?text=Brown%20Black",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fb8_03",
        "variant_name": "Deep Brown",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 880000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3B251D/FFFFFF?text=Deep%20Brown",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fb8_04",
        "variant_name": "Burgundy Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.86,
        "price": 880000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/4A2028/FFFFFF?text=Burgundy%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fb9",
    "product_name": "NYX Epic Ink Waterproof Liquid Eyeliner",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fb9_01",
        "variant_name": "Black",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.94,
        "price": 269000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fb9_02",
        "variant_name": "Brown",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 269000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/5A341F/FFFFFF?text=Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fb9_03",
        "variant_name": "Midnight Navy",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 269000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/1E2E4A/FFFFFF?text=Midnight%20Navy",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fb9_04",
        "variant_name": "Smoky Charcoal",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.87,
        "price": 269000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3E4146/FFFFFF?text=Smoky%20Charcoal",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fba",
    "product_name": "Stila Stay All Day Waterproof Liquid Eye Liner",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fba_01",
        "variant_name": "Black",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.94,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fba_02",
        "variant_name": "Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/5A341F/FFFFFF?text=Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fba_03",
        "variant_name": "Midnight Navy",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/1E2E4A/FFFFFF?text=Midnight%20Navy",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fba_04",
        "variant_name": "Smoky Charcoal",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.87,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3E4146/FFFFFF?text=Smoky%20Charcoal",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fbb",
    "product_name": "Maybelline Hyper Easy Liquid Pen Eyeliner",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fbb_01",
        "variant_name": "Black",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.94,
        "price": 189000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/141414/FFFFFF?text=Black",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fbb_02",
        "variant_name": "Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.9,
        "price": 189000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/5A341F/FFFFFF?text=Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fbb_03",
        "variant_name": "Midnight Navy",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.88,
        "price": 189000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/1E2E4A/FFFFFF?text=Midnight%20Navy",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fbb_04",
        "variant_name": "Smoky Charcoal",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.87,
        "price": 189000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3E4146/FFFFFF?text=Smoky%20Charcoal",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fbc",
    "product_name": "Urban Decay Naked3 Eyeshadow Palette",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fbc_01",
        "variant_name": "Warm Nude",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 1350000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B37758/FFFFFF?text=Warm%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fbc_02",
        "variant_name": "Rose Brown",
        "shade_hex": "#FF6B6B",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 1350000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9F5F62/FFFFFF?text=Rose%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fbc_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#FF8C94",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 1350000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fbc_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#E84A5F",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 1350000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fbd",
    "product_name": "ColourPop Super Shock Shadow",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fbd_01",
        "variant_name": "Warm Nude",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 190000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B37758/FFFFFF?text=Warm%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fbd_02",
        "variant_name": "Rose Brown",
        "shade_hex": "#FF4500",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 190000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9F5F62/FFFFFF?text=Rose%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fbd_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#FF6B6B",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 190000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fbd_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#FF8C94",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 190000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fbe",
    "product_name": "3CE Multi Eye Color Palette Overtake",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fbe_01",
        "variant_name": "Warm Nude",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B37758/FFFFFF?text=Warm%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fbe_02",
        "variant_name": "Rose Brown",
        "shade_hex": "#FF7F50",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9F5F62/FFFFFF?text=Rose%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fbe_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#FF4500",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fbe_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#FF6B6B",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fbf",
    "product_name": "Anastasia Beverly Hills Brow Wiz",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fbf_01",
        "variant_name": "Soft Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.74,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7B4D32/FFFFFF?text=Soft%20Brown",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fbf_02",
        "variant_name": "Dark Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.78,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fbf_03",
        "variant_name": "Ash Brown",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.8,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/5C4A40/FFFFFF?text=Ash%20Brown",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fbf_04",
        "variant_name": "Dark Brown",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.84,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc0",
    "product_name": "Benefit Precisely, My Brow Pencil",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc0_01",
        "variant_name": "Soft Brown",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.74,
        "price": 720000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7B4D32/FFFFFF?text=Soft%20Brown",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc0_02",
        "variant_name": "Dark Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.78,
        "price": 720000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc0_03",
        "variant_name": "Ash Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.8,
        "price": 720000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/5C4A40/FFFFFF?text=Ash%20Brown",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc0_04",
        "variant_name": "Dark Brown",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.84,
        "price": 720000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc1",
    "product_name": "Ardell Demi Wispies False Lashes",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc1_01",
        "variant_name": "Natural",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.92,
        "price": 160000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F0D5C0/FFFFFF?text=Natural",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc1_02",
        "variant_name": "Dramatic",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.95,
        "price": 160000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/1B1B1B/FFFFFF?text=Dramatic",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc1_03",
        "variant_name": "Brown Black",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.91,
        "price": 160000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/2B221E/FFFFFF?text=Brown%20Black",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc1_04",
        "variant_name": "Dark Brown",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.89,
        "price": 160000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc2",
    "product_name": "Huda Beauty Classic False Lashes Samantha #7",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc2_01",
        "variant_name": "Natural",
        "shade_hex": "#FF7F50",
        "finish_type": "MATTE",
        "opacity": 0.92,
        "price": 480000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F0D5C0/FFFFFF?text=Natural",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc2_02",
        "variant_name": "Dramatic",
        "shade_hex": "#FF4500",
        "finish_type": "MATTE",
        "opacity": 0.95,
        "price": 480000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/1B1B1B/FFFFFF?text=Dramatic",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc2_03",
        "variant_name": "Brown Black",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.91,
        "price": 480000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/2B221E/FFFFFF?text=Brown%20Black",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc2_04",
        "variant_name": "Dark Brown",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.89,
        "price": 480000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/3A2419/FFFFFF?text=Dark%20Brown",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc3",
    "product_name": "MAC Matte Lipstick Ruby Woo",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc3_01",
        "variant_name": "Rose Nude",
        "shade_hex": "#BC6B72",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BC6B72/FFFFFF?text=Rose%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc3_02",
        "variant_name": "Classic Red",
        "shade_hex": "#B20F2A",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B20F2A/FFFFFF?text=Classic%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc3_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc3_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc4",
    "product_name": "Charlotte Tilbury Matte Revolution Pillow Talk",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc4_01",
        "variant_name": "Rose Nude",
        "shade_hex": "#BC6B72",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 950000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BC6B72/FFFFFF?text=Rose%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc4_02",
        "variant_name": "Classic Red",
        "shade_hex": "#B20F2A",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 950000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B20F2A/FFFFFF?text=Classic%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc4_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 950000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc4_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 950000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc5",
    "product_name": "Dior Rouge Dior Lipstick 999",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc5_01",
        "variant_name": "Rose Nude",
        "shade_hex": "#BC6B72",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 1100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BC6B72/FFFFFF?text=Rose%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc5_02",
        "variant_name": "Classic Red",
        "shade_hex": "#B20F2A",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 1100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B20F2A/FFFFFF?text=Classic%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc5_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 1100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc5_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 1100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc6",
    "product_name": "YSL Rouge Pur Couture Lipstick",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc6_01",
        "variant_name": "Rose Nude",
        "shade_hex": "#BC6B72",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 1050000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BC6B72/FFFFFF?text=Rose%20Nude",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc6_02",
        "variant_name": "Classic Red",
        "shade_hex": "#B20F2A",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 1050000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B20F2A/FFFFFF?text=Classic%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc6_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 1050000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc6_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 1050000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc7",
    "product_name": "Fenty Beauty Gloss Bomb Universal Lip Luminizer",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc7_01",
        "variant_name": "Clear Pink",
        "shade_hex": "#F7CDD4",
        "finish_type": "GLOSSY",
        "opacity": 0.42,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F7CDD4/FFFFFF?text=Clear%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc7_02",
        "variant_name": "Rose Shimmer",
        "shade_hex": "#D98B9A",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98B9A/FFFFFF?text=Rose%20Shimmer",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc7_03",
        "variant_name": "Peach Nude",
        "shade_hex": "#E8A08D",
        "finish_type": "GLOSSY",
        "opacity": 0.44,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/E8A08D/FFFFFF?text=Peach%20Nude",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc7_04",
        "variant_name": "Berry Shine",
        "shade_hex": "#A84B6A",
        "finish_type": "GLOSSY",
        "opacity": 0.48,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A84B6A/FFFFFF?text=Berry%20Shine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc8",
    "product_name": "NYX Butter Gloss",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc8_01",
        "variant_name": "Clear Pink",
        "shade_hex": "#F7CDD4",
        "finish_type": "GLOSSY",
        "opacity": 0.42,
        "price": 180000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F7CDD4/FFFFFF?text=Clear%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc8_02",
        "variant_name": "Rose Shimmer",
        "shade_hex": "#D98B9A",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 180000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98B9A/FFFFFF?text=Rose%20Shimmer",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc8_03",
        "variant_name": "Peach Nude",
        "shade_hex": "#E8A08D",
        "finish_type": "GLOSSY",
        "opacity": 0.44,
        "price": 180000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/E8A08D/FFFFFF?text=Peach%20Nude",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc8_04",
        "variant_name": "Berry Shine",
        "shade_hex": "#A84B6A",
        "finish_type": "GLOSSY",
        "opacity": 0.48,
        "price": 180000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A84B6A/FFFFFF?text=Berry%20Shine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fc9",
    "product_name": "Dior Addict Lip Maximizer",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fc9_01",
        "variant_name": "Clear Pink",
        "shade_hex": "#F7CDD4",
        "finish_type": "GLOSSY",
        "opacity": 0.42,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F7CDD4/FFFFFF?text=Clear%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fc9_02",
        "variant_name": "Rose Shimmer",
        "shade_hex": "#D98B9A",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98B9A/FFFFFF?text=Rose%20Shimmer",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fc9_03",
        "variant_name": "Peach Nude",
        "shade_hex": "#E8A08D",
        "finish_type": "GLOSSY",
        "opacity": 0.44,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/E8A08D/FFFFFF?text=Peach%20Nude",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fc9_04",
        "variant_name": "Berry Shine",
        "shade_hex": "#A84B6A",
        "finish_type": "GLOSSY",
        "opacity": 0.48,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A84B6A/FFFFFF?text=Berry%20Shine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fca",
    "product_name": "Laneige Lip Sleeping Mask Berry",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fca_01",
        "variant_name": "Berry",
        "shade_hex": "#A83B62",
        "finish_type": "TINT",
        "opacity": 0.36,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A83B62/FFFFFF?text=Berry",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fca_02",
        "variant_name": "Vanilla",
        "shade_hex": "#F1DFC8",
        "finish_type": "TINT",
        "opacity": 0.28,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F1DFC8/FFFFFF?text=Vanilla",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fca_03",
        "variant_name": "Soft Coral",
        "shade_hex": "#D98276",
        "finish_type": "TINT",
        "opacity": 0.34,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98276/FFFFFF?text=Soft%20Coral",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fca_04",
        "variant_name": "Rose Balm",
        "shade_hex": "#C77986",
        "finish_type": "TINT",
        "opacity": 0.35,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C77986/FFFFFF?text=Rose%20Balm",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fcb",
    "product_name": "rhode Peptide Lip Treatment",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fcb_01",
        "variant_name": "Berry",
        "shade_hex": "#A83B62",
        "finish_type": "TINT",
        "opacity": 0.36,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A83B62/FFFFFF?text=Berry",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fcb_02",
        "variant_name": "Vanilla",
        "shade_hex": "#F1DFC8",
        "finish_type": "TINT",
        "opacity": 0.28,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F1DFC8/FFFFFF?text=Vanilla",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fcb_03",
        "variant_name": "Soft Coral",
        "shade_hex": "#D98276",
        "finish_type": "TINT",
        "opacity": 0.34,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98276/FFFFFF?text=Soft%20Coral",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fcb_04",
        "variant_name": "Rose Balm",
        "shade_hex": "#C77986",
        "finish_type": "TINT",
        "opacity": 0.35,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C77986/FFFFFF?text=Rose%20Balm",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fcc",
    "product_name": "Kiehl’s Butterstick Lip Treatment",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fcc_01",
        "variant_name": "Berry",
        "shade_hex": "#A83B62",
        "finish_type": "TINT",
        "opacity": 0.36,
        "price": 580000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A83B62/FFFFFF?text=Berry",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fcc_02",
        "variant_name": "Vanilla",
        "shade_hex": "#F1DFC8",
        "finish_type": "TINT",
        "opacity": 0.28,
        "price": 580000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F1DFC8/FFFFFF?text=Vanilla",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fcc_03",
        "variant_name": "Soft Coral",
        "shade_hex": "#D98276",
        "finish_type": "TINT",
        "opacity": 0.34,
        "price": 580000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98276/FFFFFF?text=Soft%20Coral",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fcc_04",
        "variant_name": "Rose Balm",
        "shade_hex": "#C77986",
        "finish_type": "TINT",
        "opacity": 0.35,
        "price": 580000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C77986/FFFFFF?text=Rose%20Balm",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fcd",
    "product_name": "MAC Lip Pencil Spice",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fcd_01",
        "variant_name": "Nude Pink",
        "shade_hex": "#C98686",
        "finish_type": "MATTE",
        "opacity": 0.74,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C98686/FFFFFF?text=Nude%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fcd_02",
        "variant_name": "Warm Brown",
        "shade_hex": "#9C5F42",
        "finish_type": "MATTE",
        "opacity": 0.76,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9C5F42/FFFFFF?text=Warm%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fcd_03",
        "variant_name": "Deep Rose",
        "shade_hex": "#A14F60",
        "finish_type": "MATTE",
        "opacity": 0.75,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A14F60/FFFFFF?text=Deep%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fcd_04",
        "variant_name": "Brick Nude",
        "shade_hex": "#8C493B",
        "finish_type": "MATTE",
        "opacity": 0.77,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8C493B/FFFFFF?text=Brick%20Nude",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fce",
    "product_name": "Charlotte Tilbury Lip Cheat Pillow Talk",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fce_01",
        "variant_name": "Nude Pink",
        "shade_hex": "#C98686",
        "finish_type": "MATTE",
        "opacity": 0.74,
        "price": 780000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C98686/FFFFFF?text=Nude%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fce_02",
        "variant_name": "Warm Brown",
        "shade_hex": "#9C5F42",
        "finish_type": "MATTE",
        "opacity": 0.76,
        "price": 780000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9C5F42/FFFFFF?text=Warm%20Brown",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fce_03",
        "variant_name": "Deep Rose",
        "shade_hex": "#A14F60",
        "finish_type": "MATTE",
        "opacity": 0.75,
        "price": 780000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A14F60/FFFFFF?text=Deep%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fce_04",
        "variant_name": "Brick Nude",
        "shade_hex": "#8C493B",
        "finish_type": "MATTE",
        "opacity": 0.77,
        "price": 780000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8C493B/FFFFFF?text=Brick%20Nude",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fcf",
    "product_name": "rom&nd Juicy Lasting Tint",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fcf_01",
        "variant_name": "Coral",
        "shade_hex": "#EB6D61",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EB6D61/FFFFFF?text=Coral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fcf_02",
        "variant_name": "Berry Red",
        "shade_hex": "#B51F45",
        "finish_type": "TINT",
        "opacity": 0.54,
        "price": 250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B51F45/FFFFFF?text=Berry%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fcf_03",
        "variant_name": "Pink MLBB",
        "shade_hex": "#C45E72",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C45E72/FFFFFF?text=Pink%20MLBB",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fcf_04",
        "variant_name": "Plum Red",
        "shade_hex": "#8E3554",
        "finish_type": "TINT",
        "opacity": 0.56,
        "price": 250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8E3554/FFFFFF?text=Plum%20Red",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd0",
    "product_name": "Peripera Ink Mood Glowy Tint",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd0_01",
        "variant_name": "Coral",
        "shade_hex": "#EB6D61",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 210000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EB6D61/FFFFFF?text=Coral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd0_02",
        "variant_name": "Berry Red",
        "shade_hex": "#B51F45",
        "finish_type": "TINT",
        "opacity": 0.54,
        "price": 210000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B51F45/FFFFFF?text=Berry%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd0_03",
        "variant_name": "Pink MLBB",
        "shade_hex": "#C45E72",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 210000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C45E72/FFFFFF?text=Pink%20MLBB",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd0_04",
        "variant_name": "Plum Red",
        "shade_hex": "#8E3554",
        "finish_type": "TINT",
        "opacity": 0.56,
        "price": 210000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8E3554/FFFFFF?text=Plum%20Red",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd1",
    "product_name": "Etude Dear Darling Water Gel Tint",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd1_01",
        "variant_name": "Coral",
        "shade_hex": "#EB6D61",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 150000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EB6D61/FFFFFF?text=Coral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd1_02",
        "variant_name": "Berry Red",
        "shade_hex": "#B51F45",
        "finish_type": "TINT",
        "opacity": 0.54,
        "price": 150000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B51F45/FFFFFF?text=Berry%20Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd1_03",
        "variant_name": "Pink MLBB",
        "shade_hex": "#C45E72",
        "finish_type": "TINT",
        "opacity": 0.5,
        "price": 150000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C45E72/FFFFFF?text=Pink%20MLBB",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd1_04",
        "variant_name": "Plum Red",
        "shade_hex": "#8E3554",
        "finish_type": "TINT",
        "opacity": 0.56,
        "price": 150000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8E3554/FFFFFF?text=Plum%20Red",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd2",
    "product_name": "Rare Beauty Soft Pinch Liquid Blush Happy",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd2_01",
        "variant_name": "Soft Pink",
        "shade_hex": "#EDA0AD",
        "finish_type": "SATIN",
        "opacity": 0.38,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EDA0AD/FFFFFF?text=Soft%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd2_02",
        "variant_name": "Peach Coral",
        "shade_hex": "#F08A70",
        "finish_type": "SATIN",
        "opacity": 0.4,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F08A70/FFFFFF?text=Peach%20Coral",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd2_03",
        "variant_name": "Dusty Rose",
        "shade_hex": "#C7838D",
        "finish_type": "MATTE",
        "opacity": 0.37,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C7838D/FFFFFF?text=Dusty%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd2_04",
        "variant_name": "Mauve Berry",
        "shade_hex": "#A9687B",
        "finish_type": "SATIN",
        "opacity": 0.39,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A9687B/FFFFFF?text=Mauve%20Berry",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd3",
    "product_name": "NARS Blush Orgasm",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd3_01",
        "variant_name": "Soft Pink",
        "shade_hex": "#EDA0AD",
        "finish_type": "SATIN",
        "opacity": 0.38,
        "price": 890000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EDA0AD/FFFFFF?text=Soft%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd3_02",
        "variant_name": "Peach Coral",
        "shade_hex": "#F08A70",
        "finish_type": "SATIN",
        "opacity": 0.4,
        "price": 890000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F08A70/FFFFFF?text=Peach%20Coral",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd3_03",
        "variant_name": "Dusty Rose",
        "shade_hex": "#C7838D",
        "finish_type": "MATTE",
        "opacity": 0.37,
        "price": 890000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C7838D/FFFFFF?text=Dusty%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd3_04",
        "variant_name": "Mauve Berry",
        "shade_hex": "#A9687B",
        "finish_type": "SATIN",
        "opacity": 0.39,
        "price": 890000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A9687B/FFFFFF?text=Mauve%20Berry",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd4",
    "product_name": "Canmake Cream Cheek",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd4_01",
        "variant_name": "Soft Pink",
        "shade_hex": "#EDA0AD",
        "finish_type": "SATIN",
        "opacity": 0.38,
        "price": 220000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EDA0AD/FFFFFF?text=Soft%20Pink",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd4_02",
        "variant_name": "Peach Coral",
        "shade_hex": "#F08A70",
        "finish_type": "SATIN",
        "opacity": 0.4,
        "price": 220000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F08A70/FFFFFF?text=Peach%20Coral",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd4_03",
        "variant_name": "Dusty Rose",
        "shade_hex": "#C7838D",
        "finish_type": "MATTE",
        "opacity": 0.37,
        "price": 220000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C7838D/FFFFFF?text=Dusty%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd4_04",
        "variant_name": "Mauve Berry",
        "shade_hex": "#A9687B",
        "finish_type": "SATIN",
        "opacity": 0.39,
        "price": 220000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A9687B/FFFFFF?text=Mauve%20Berry",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd5",
    "product_name": "Benefit Hoola Matte Bronzer",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd5_01",
        "variant_name": "Light Bronze",
        "shade_hex": "#BD8152",
        "finish_type": "MATTE",
        "opacity": 0.32,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BD8152/FFFFFF?text=Light%20Bronze",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd5_02",
        "variant_name": "Warm Tan",
        "shade_hex": "#9D6639",
        "finish_type": "MATTE",
        "opacity": 0.34,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9D6639/FFFFFF?text=Warm%20Tan",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd5_03",
        "variant_name": "Neutral Bronze",
        "shade_hex": "#A87957",
        "finish_type": "MATTE",
        "opacity": 0.33,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A87957/FFFFFF?text=Neutral%20Bronze",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd5_04",
        "variant_name": "Deep Bronze",
        "shade_hex": "#74462D",
        "finish_type": "MATTE",
        "opacity": 0.36,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/74462D/FFFFFF?text=Deep%20Bronze",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd6",
    "product_name": "Fenty Beauty Sun Stalk’r Instant Warmth Bronzer",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd6_01",
        "variant_name": "Light Bronze",
        "shade_hex": "#BD8152",
        "finish_type": "MATTE",
        "opacity": 0.32,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/BD8152/FFFFFF?text=Light%20Bronze",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd6_02",
        "variant_name": "Warm Tan",
        "shade_hex": "#9D6639",
        "finish_type": "MATTE",
        "opacity": 0.34,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9D6639/FFFFFF?text=Warm%20Tan",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd6_03",
        "variant_name": "Neutral Bronze",
        "shade_hex": "#A87957",
        "finish_type": "MATTE",
        "opacity": 0.33,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A87957/FFFFFF?text=Neutral%20Bronze",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd6_04",
        "variant_name": "Deep Bronze",
        "shade_hex": "#74462D",
        "finish_type": "MATTE",
        "opacity": 0.36,
        "price": 850000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/74462D/FFFFFF?text=Deep%20Bronze",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd7",
    "product_name": "Dior Backstage Glow Face Palette",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd7_01",
        "variant_name": "Champagne",
        "shade_hex": "#EAD1A3",
        "finish_type": "GLOSSY",
        "opacity": 0.28,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EAD1A3/FFFFFF?text=Champagne",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd7_02",
        "variant_name": "Rose Gold",
        "shade_hex": "#D89A8F",
        "finish_type": "GLOSSY",
        "opacity": 0.3,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D89A8F/FFFFFF?text=Rose%20Gold",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd7_03",
        "variant_name": "Pearl",
        "shade_hex": "#F4E6CF",
        "finish_type": "SATIN",
        "opacity": 0.24,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F4E6CF/FFFFFF?text=Pearl",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd7_04",
        "variant_name": "Warm Gold",
        "shade_hex": "#D7B06A",
        "finish_type": "GLOSSY",
        "opacity": 0.31,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D7B06A/FFFFFF?text=Warm%20Gold",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd8",
    "product_name": "Rare Beauty Positive Light Liquid Luminizer",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd8_01",
        "variant_name": "Champagne",
        "shade_hex": "#EAD1A3",
        "finish_type": "GLOSSY",
        "opacity": 0.28,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EAD1A3/FFFFFF?text=Champagne",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd8_02",
        "variant_name": "Rose Gold",
        "shade_hex": "#D89A8F",
        "finish_type": "GLOSSY",
        "opacity": 0.3,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D89A8F/FFFFFF?text=Rose%20Gold",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd8_03",
        "variant_name": "Pearl",
        "shade_hex": "#F4E6CF",
        "finish_type": "SATIN",
        "opacity": 0.24,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F4E6CF/FFFFFF?text=Pearl",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd8_04",
        "variant_name": "Warm Gold",
        "shade_hex": "#D7B06A",
        "finish_type": "GLOSSY",
        "opacity": 0.31,
        "price": 650000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D7B06A/FFFFFF?text=Warm%20Gold",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fd9",
    "product_name": "Hourglass Ambient Lighting Powder",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fd9_01",
        "variant_name": "Champagne",
        "shade_hex": "#EAD1A3",
        "finish_type": "GLOSSY",
        "opacity": 0.28,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/EAD1A3/FFFFFF?text=Champagne",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fd9_02",
        "variant_name": "Rose Gold",
        "shade_hex": "#D89A8F",
        "finish_type": "GLOSSY",
        "opacity": 0.3,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D89A8F/FFFFFF?text=Rose%20Gold",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fd9_03",
        "variant_name": "Pearl",
        "shade_hex": "#F4E6CF",
        "finish_type": "SATIN",
        "opacity": 0.24,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F4E6CF/FFFFFF?text=Pearl",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fd9_04",
        "variant_name": "Warm Gold",
        "shade_hex": "#D7B06A",
        "finish_type": "GLOSSY",
        "opacity": 0.31,
        "price": 1250000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D7B06A/FFFFFF?text=Warm%20Gold",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fda",
    "product_name": "Fenty Beauty Match Stix Matte Contour Skinstick",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fda_01",
        "variant_name": "Light Medium",
        "shade_hex": "#C78E68",
        "finish_type": "MATTE",
        "opacity": 0.3,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C78E68/FFFFFF?text=Light%20Medium",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fda_02",
        "variant_name": "Medium Deep",
        "shade_hex": "#8A5537",
        "finish_type": "MATTE",
        "opacity": 0.34,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8A5537/FFFFFF?text=Medium%20Deep",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fda_03",
        "variant_name": "Neutral Taupe",
        "shade_hex": "#9C7B68",
        "finish_type": "MATTE",
        "opacity": 0.31,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9C7B68/FFFFFF?text=Neutral%20Taupe",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fda_04",
        "variant_name": "Deep Cocoa",
        "shade_hex": "#69412F",
        "finish_type": "MATTE",
        "opacity": 0.36,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/69412F/FFFFFF?text=Deep%20Cocoa",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fdb",
    "product_name": "Charlotte Tilbury Hollywood Contour Wand",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fdb_01",
        "variant_name": "Light Medium",
        "shade_hex": "#C78E68",
        "finish_type": "MATTE",
        "opacity": 0.3,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C78E68/FFFFFF?text=Light%20Medium",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fdb_02",
        "variant_name": "Medium Deep",
        "shade_hex": "#8A5537",
        "finish_type": "MATTE",
        "opacity": 0.34,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8A5537/FFFFFF?text=Medium%20Deep",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fdb_03",
        "variant_name": "Neutral Taupe",
        "shade_hex": "#9C7B68",
        "finish_type": "MATTE",
        "opacity": 0.31,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/9C7B68/FFFFFF?text=Neutral%20Taupe",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fdb_04",
        "variant_name": "Deep Cocoa",
        "shade_hex": "#69412F",
        "finish_type": "MATTE",
        "opacity": 0.36,
        "price": 980000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/69412F/FFFFFF?text=Deep%20Cocoa",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fdc",
    "product_name": "Huda Beauty Empowered Eyeshadow Palette",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fdc_01",
        "variant_name": "Warm Neutral",
        "shade_hex": "#E84A5F",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 1750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B87755/FFFFFF?text=Warm%20Neutral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fdc_02",
        "variant_name": "Rose Glam",
        "shade_hex": "#FF7F50",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 1750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A85D71/FFFFFF?text=Rose%20Glam",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fdc_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#FF4500",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 1750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fdc_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#FF6B6B",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 1750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fdd",
    "product_name": "Pat McGrath Labs Mothership Eyeshadow Palette",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fdd_01",
        "variant_name": "Warm Neutral",
        "shade_hex": "#FF8C94",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 3200000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B87755/FFFFFF?text=Warm%20Neutral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fdd_02",
        "variant_name": "Rose Glam",
        "shade_hex": "#E84A5F",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 3200000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A85D71/FFFFFF?text=Rose%20Glam",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fdd_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#FF7F50",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 3200000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fdd_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#FF4500",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 3200000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fde",
    "product_name": "Morphe 35O Supernatural Glow Artistry Palette",
    "status": "active",
    "ar_type": "EYES",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fde_01",
        "variant_name": "Warm Neutral",
        "shade_hex": "#FF6B6B",
        "finish_type": "MATTE",
        "opacity": 0.52,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B87755/FFFFFF?text=Warm%20Neutral",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fde_02",
        "variant_name": "Rose Glam",
        "shade_hex": "#FF8C94",
        "finish_type": "SATIN",
        "opacity": 0.54,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A85D71/FFFFFF?text=Rose%20Glam",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fde_03",
        "variant_name": "Champagne Gold",
        "shade_hex": "#E84A5F",
        "finish_type": "GLOSSY",
        "opacity": 0.46,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D6B17D/FFFFFF?text=Champagne%20Gold",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fde_04",
        "variant_name": "Smoky Plum",
        "shade_hex": "#FF7F50",
        "finish_type": "SATIN",
        "opacity": 0.58,
        "price": 750000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/6F4A5D/FFFFFF?text=Smoky%20Plum",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fdf",
    "product_name": "Hourglass Ambient Lighting Edit Unlocked Palette",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fdf_01",
        "variant_name": "Light Medium",
        "shade_hex": "#C78E68",
        "finish_type": "GLOSSY",
        "opacity": 0.28,
        "price": 2100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C78E68/FFFFFF?text=Light%20Medium",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fdf_02",
        "variant_name": "Medium Deep",
        "shade_hex": "#8A5537",
        "finish_type": "GLOSSY",
        "opacity": 0.3,
        "price": 2100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8A5537/FFFFFF?text=Medium%20Deep",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fdf_03",
        "variant_name": "Pearl",
        "shade_hex": "#F4E6CF",
        "finish_type": "SATIN",
        "opacity": 0.24,
        "price": 2100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/F4E6CF/FFFFFF?text=Pearl",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fdf_04",
        "variant_name": "Warm Gold",
        "shade_hex": "#D7B06A",
        "finish_type": "GLOSSY",
        "opacity": 0.31,
        "price": 2100000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D7B06A/FFFFFF?text=Warm%20Gold",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fe0",
    "product_name": "Make Up For Ever HD Skin Face Essentials Palette",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fe0_01",
        "variant_name": "Light Medium",
        "shade_hex": "#C78E68",
        "finish_type": "SATIN",
        "opacity": 0.34,
        "price": 1800000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C78E68/FFFFFF?text=Light%20Medium",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fe0_02",
        "variant_name": "Medium Deep",
        "shade_hex": "#8A5537",
        "finish_type": "SATIN",
        "opacity": 0.36,
        "price": 1800000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8A5537/FFFFFF?text=Medium%20Deep",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fe0_03",
        "variant_name": "Champagne Glow",
        "shade_hex": "#E0C28E",
        "finish_type": "GLOSSY",
        "opacity": 0.27,
        "price": 1800000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/E0C28E/FFFFFF?text=Champagne%20Glow",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fe0_04",
        "variant_name": "Soft Bronze",
        "shade_hex": "#A76E4F",
        "finish_type": "MATTE",
        "opacity": 0.33,
        "price": 1800000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A76E4F/FFFFFF?text=Soft%20Bronze",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fe5",
    "product_name": "MAC Mini Matte Lipstick Velvet Teddy",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fe5_01",
        "variant_name": "Nude Rose",
        "shade_hex": "#C98274",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 390000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C98274/FFFFFF?text=Nude%20Rose",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fe5_02",
        "variant_name": "Red",
        "shade_hex": "#8F4F42",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 390000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8F4F42/FFFFFF?text=Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fe5_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 390000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fe5_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 390000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fe6",
    "product_name": "Dior Rouge Dior Mini Lipstick 999",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fe6_01",
        "variant_name": "Nude Rose",
        "shade_hex": "#C98274",
        "finish_type": "MATTE",
        "opacity": 0.68,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C98274/FFFFFF?text=Nude%20Rose",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fe6_02",
        "variant_name": "Red",
        "shade_hex": "#8F4F42",
        "finish_type": "MATTE",
        "opacity": 0.7,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/8F4F42/FFFFFF?text=Red",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fe6_03",
        "variant_name": "Terracotta",
        "shade_hex": "#C75D45",
        "finish_type": "MATTE",
        "opacity": 0.66,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C75D45/FFFFFF?text=Terracotta",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fe6_04",
        "variant_name": "Plum Wine",
        "shade_hex": "#7D3048",
        "finish_type": "SATIN",
        "opacity": 0.64,
        "price": 520000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/7D3048/FFFFFF?text=Plum%20Wine",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fe7",
    "product_name": "Rare Beauty Mini Soft Pinch Liquid Blush Trio",
    "status": "active",
    "ar_type": "CHEEKS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fe7_01",
        "variant_name": "Discovery",
        "shade_hex": "#D8A48F",
        "finish_type": "SATIN",
        "opacity": 0.38,
        "price": 790000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D8A48F/FFFFFF?text=Discovery",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fe7_02",
        "variant_name": "Travel",
        "shade_hex": "#B98972",
        "finish_type": "SATIN",
        "opacity": 0.4,
        "price": 790000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B98972/FFFFFF?text=Travel",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fe7_03",
        "variant_name": "Dusty Rose",
        "shade_hex": "#C7838D",
        "finish_type": "MATTE",
        "opacity": 0.37,
        "price": 790000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C7838D/FFFFFF?text=Dusty%20Rose",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fe7_04",
        "variant_name": "Mauve Berry",
        "shade_hex": "#A9687B",
        "finish_type": "SATIN",
        "opacity": 0.39,
        "price": 790000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/A9687B/FFFFFF?text=Mauve%20Berry",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  },
  {
    "product_id": "000000000000000000000fe8",
    "product_name": "Laneige Mini Lip Sleeping Mask Set",
    "status": "active",
    "ar_type": "LIPS",
    "renderer_version": "v2",
    "variants": [
      {
        "variant_id": "ar_000fe8_01",
        "variant_name": "Discovery",
        "shade_hex": "#D8A48F",
        "finish_type": "TINT",
        "opacity": 0.36,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D8A48F/FFFFFF?text=Discovery",
        "enabled": true,
        "display_order": 1
      },
      {
        "variant_id": "ar_000fe8_02",
        "variant_name": "Travel",
        "shade_hex": "#B98972",
        "finish_type": "TINT",
        "opacity": 0.28,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/B98972/FFFFFF?text=Travel",
        "enabled": true,
        "display_order": 2
      },
      {
        "variant_id": "ar_000fe8_03",
        "variant_name": "Soft Coral",
        "shade_hex": "#D98276",
        "finish_type": "TINT",
        "opacity": 0.34,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/D98276/FFFFFF?text=Soft%20Coral",
        "enabled": true,
        "display_order": 3
      },
      {
        "variant_id": "ar_000fe8_04",
        "variant_name": "Rose Balm",
        "shade_hex": "#C77986",
        "finish_type": "TINT",
        "opacity": 0.35,
        "price": 620000,
        "currency_code": "VND",
        "in_stock": true,
        "thumbnail_url": "https://placehold.co/100x100/C77986/FFFFFF?text=Rose%20Balm",
        "enabled": true,
        "display_order": 4
      }
    ],
    "mock_data": true,
    "seed_batch": "kanila_ar_mock_v2",
    "createdAt": "2026-07-19T00:00:00.000Z",
    "updatedAt": "2026-07-19T00:00:00.000Z"
  }
];

const UNSUPPORTED_PRODUCTS = [
  {
    "product_id": "000000000000000000000fa1",
    "product_name": "Maybelline Fit Me Matte + Poreless Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa2",
    "product_name": "L’Oréal Paris Infallible 24H Fresh Wear Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fab",
    "product_name": "e.l.f. Power Grip Primer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fac",
    "product_name": "Benefit The POREfessional Face Primer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fad",
    "product_name": "Milk Makeup Hydro Grip Primer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fae",
    "product_name": "Laura Mercier Translucent Loose Setting Powder",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000faf",
    "product_name": "Charlotte Tilbury Airbrush Flawless Finish Powder",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb0",
    "product_name": "Urban Decay All Nighter Setting Spray",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb1",
    "product_name": "Charlotte Tilbury Airbrush Flawless Setting Spray",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb2",
    "product_name": "IT Cosmetics CC+ Cream SPF 50+",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb3",
    "product_name": "Erborian CC Red Correct",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb4",
    "product_name": "Rare Beauty Positive Light Tinted Moisturizer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa3",
    "product_name": "Fenty Beauty Pro Filt’r Soft Matte Longwear Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fb5",
    "product_name": "Saie Slip Tint Dewy Tinted Moisturizer SPF 35",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa4",
    "product_name": "NARS Light Reflecting Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa5",
    "product_name": "Estée Lauder Double Wear Stay-in-Place Makeup",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa6",
    "product_name": "Armani Beauty Luminous Silk Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa7",
    "product_name": "Dior Forever Skin Glow Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fe1",
    "product_name": "Sephora Favorites Makeup Must-Haves Kit",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fe2",
    "product_name": "Charlotte Tilbury Pillow Talk Makeup Kit",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fe3",
    "product_name": "NARS Mini Light Reflecting Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fe4",
    "product_name": "Fenty Beauty Mini Pro Filt’r Foundation",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa8",
    "product_name": "NARS Radiant Creamy Concealer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fe9",
    "product_name": "Benefit Mini Bestseller Trial Kit",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000fa9",
    "product_name": "Maybelline Instant Age Rewind Eraser Concealer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  },
  {
    "product_id": "000000000000000000000faa",
    "product_name": "Tarte Shape Tape Full Coverage Concealer",
    "reason": "Current Kanila renderer only supports LIPS, CHEEKS and EYES; this product requires a FACE/base or mixed-product renderer."
  }
];

function getMongoUri() {
  return (
    process.env.MONGODB_URI ||
    process.env.MONGO_URI ||
    process.env.MONGODB_URL ||
    process.env.DATABASE_URL ||
    ''
  );
}

function isValidObjectIdString(value) {
  return typeof value === 'string' && /^[0-9a-fA-F]{24}$/.test(value);
}

function toObjectId(value, fieldName = 'ObjectId') {
  if (!isValidObjectIdString(value)) {
    throw new Error(`Invalid ${fieldName}: ${value}`);
  }
  const mongoose = loadMongoose();
  return new mongoose.Types.ObjectId(value);
}

function materializeConfig(config) {
  return {
    ...config,
    product_id: toObjectId(config.product_id, 'product_id'),
    createdAt: new Date(config.createdAt),
    updatedAt: new Date(),
  };
}

function selfTest() {
  const errors = [];
  const productIds = new Set();
  const variantIds = new Set();

  for (const config of AR_CONFIGS) {
    if (!isValidObjectIdString(config.product_id)) {
      errors.push(`Invalid product_id: ${config.product_id}`);
    }
    if (productIds.has(config.product_id)) {
      errors.push(`Duplicate product_id: ${config.product_id}`);
    }
    productIds.add(config.product_id);

    if (!ALLOWED_AR_TYPES.has(config.ar_type)) {
      errors.push(`Invalid ar_type ${config.ar_type} for ${config.product_name}`);
    }
    if (config.status !== 'active') {
      errors.push(`Config is not active: ${config.product_name}`);
    }
    if (!Array.isArray(config.variants) || config.variants.length < 3 || config.variants.length > 4) {
      errors.push(`Expected 3-4 variants for ${config.product_name}`);
      continue;
    }

    for (const variant of config.variants) {
      if (!variant.variant_id || variantIds.has(variant.variant_id)) {
        errors.push(`Missing or duplicate variant_id: ${variant.variant_id}`);
      }
      variantIds.add(variant.variant_id);

      if (!HEX_RE.test(variant.shade_hex)) {
        errors.push(`Invalid shade_hex ${variant.shade_hex} in ${config.product_name}`);
      }
      if (
        typeof variant.opacity !== 'number' ||
        variant.opacity < 0 ||
        variant.opacity > 1
      ) {
        errors.push(`Invalid opacity for ${variant.variant_id}`);
      }
      if (!['MATTE', 'GLOSSY', 'SATIN', 'TINT'].includes(variant.finish_type)) {
        errors.push(`Invalid finish_type ${variant.finish_type}`);
      }
      if (variant.price !== Number(variant.price) || variant.price < 0) {
        errors.push(`Invalid price for ${variant.variant_id}`);
      }
    }
  }

  const unsupportedIds = new Set(UNSUPPORTED_PRODUCTS.map((item) => item.product_id));
  for (const id of productIds) {
    if (unsupportedIds.has(id)) {
      errors.push(`Product exists in both supported and unsupported lists: ${id}`);
    }
  }

  if (errors.length) {
    console.error('dataAR mock self-test: FAIL');
    for (const error of errors) console.error(`- ${error}`);
    process.exitCode = 1;
    return false;
  }

  const counts = AR_CONFIGS.reduce(
    (acc, config) => {
      acc[config.ar_type] = (acc[config.ar_type] || 0) + 1;
      acc.variants += config.variants.length;
      return acc;
    },
    { LIPS: 0, CHEEKS: 0, EYES: 0, variants: 0 }
  );

  console.log('dataAR mock self-test: PASS');
  console.log(
    `Configs=${AR_CONFIGS.length}, Variants=${counts.variants}, ` +
      `LIPS=${counts.LIPS}, CHEEKS=${counts.CHEEKS}, EYES=${counts.EYES}, ` +
      `Unsupported=${UNSUPPORTED_PRODUCTS.length}`
  );
  return true;
}

async function resetSeed(db) {
  const configs = db.collection(CONFIG_COLLECTION);
  const products = db.collection(PRODUCT_COLLECTION);

  const deleted = await configs.deleteMany({ seed_batch: SEED_BATCH });
  const resetProducts = await products.updateMany(
    { ar_seed_batch: SEED_BATCH },
    {
      $unset: {
        ar_type: '',
        ar_renderer_version: '',
        ar_seed_batch: '',
        ar_mock_variant_count: '',
        ar_mock_updated_at: '',
        ar_unsupported_reason: '',
      },
      $set: { hasAr: false },
    }
  );

  console.log(
    `Reset complete: deletedConfigs=${deleted.deletedCount}, ` +
      `updatedProducts=${resetProducts.modifiedCount}`
  );
}

async function seed(db) {
  const configsCollection = db.collection(CONFIG_COLLECTION);
  const productsCollection = db.collection(PRODUCT_COLLECTION);

  let upserted = 0;
  let modified = 0;

  for (const rawConfig of AR_CONFIGS) {
    const config = materializeConfig(rawConfig);
    delete config.createdAt;
    const result = await configsCollection.updateOne(
      { product_id: config.product_id },
      {
        $set: {
          ...config,
          updatedAt: new Date(),
        },
        $setOnInsert: {
          createdAt: new Date(rawConfig.createdAt || new Date()),
        },
      },
      { upsert: true }
    );

    upserted += result.upsertedCount || 0;
    modified += result.modifiedCount || 0;
  }

  const supportedOperations = AR_CONFIGS.map((config) => ({
    updateOne: {
      filter: { _id: toObjectId(config.product_id, 'supported product_id') },
      update: {
        $set: {
          hasAr: true,
          ar_type: config.ar_type,
          ar_renderer_version: config.renderer_version,
          ar_seed_batch: SEED_BATCH,
          ar_mock_variant_count: config.variants.length,
          ar_mock_updated_at: new Date(),
        },
        $unset: { ar_unsupported_reason: '' },
      },
    },
  }));

  const unsupportedOperations = UNSUPPORTED_PRODUCTS.map((item) => ({
    updateOne: {
      filter: { _id: toObjectId(item.product_id, 'unsupported product_id') },
      update: {
        $set: {
          hasAr: false,
          ar_seed_batch: SEED_BATCH,
          ar_unsupported_reason: item.reason,
          ar_mock_updated_at: new Date(),
        },
        $unset: {
          ar_type: '',
          ar_renderer_version: '',
          ar_mock_variant_count: '',
        },
      },
    },
  }));

  const operations = [...supportedOperations, ...unsupportedOperations];
  const productResult = operations.length
    ? await productsCollection.bulkWrite(operations, { ordered: false })
    : null;

  console.log('Kanila AR mock seed completed.');
  console.log(`Config upserts: inserted=${upserted}, modified=${modified}`);
  console.log(
    `Product updates: matched=${productResult ? productResult.matchedCount : 0}, ` +
      `modified=${productResult ? productResult.modifiedCount : 0}`
  );
  console.log(
    `Supported configs=${AR_CONFIGS.length}, unsupported products=${UNSUPPORTED_PRODUCTS.length}`
  );
}

async function main() {
  if (!selfTest()) return;

  const args = new Set(process.argv.slice(2));
  if (args.has('--self-test')) return;

  if (args.has('--dry-run')) {
    console.log(JSON.stringify({
      seed_batch: SEED_BATCH,
      supported_config_count: AR_CONFIGS.length,
      unsupported_product_count: UNSUPPORTED_PRODUCTS.length,
      sample_config: AR_CONFIGS[0],
    }, null, 2));
    return;
  }

  const uri = getMongoUri();
  if (!uri) {
    throw new Error(
      'Missing MongoDB URI. Set MONGODB_URI, MONGO_URI, MONGODB_URL or DATABASE_URL.'
    );
  }

  const options = process.env.MONGODB_DB_NAME
    ? { dbName: process.env.MONGODB_DB_NAME }
    : undefined;

  const mongoose = loadMongoose();
  await mongoose.connect(uri, options);
  console.log(`Connected to MongoDB database: ${mongoose.connection.name}`);

  try {
    if (args.has('--reset')) {
      await resetSeed(mongoose.connection.db);
      return;
    }
    await seed(mongoose.connection.db);
  } finally {
    await mongoose.disconnect();
  }
}

if (require.main === module) {
  main().catch((error) => {
    console.error('dataAR mock seed failed:', error);
    process.exitCode = 1;
  });
}

module.exports = {
  AR_CONFIGS,
  UNSUPPORTED_PRODUCTS,
  selfTest,
};
