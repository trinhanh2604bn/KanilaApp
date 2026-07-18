const mongoose = require('mongoose');
const ProductVariant = require('../models/productVariant.model');
require('dotenv').config();

const DRY_RUN = process.argv.includes('--dry-run');
const PRODUCT_ID = "6699e17b6a4891b26f59ba01"; // Placeholder, can be passed as arg

const FIXTURES = [
  { sku: "DEMO-AR-01", variantName: "Màu 1", shade_hex: "#FF99CC", finish_type: "MATTE", opacity: 0.62, costAmount: 239000 },
  { sku: "DEMO-AR-02", variantName: "Màu 2", shade_hex: "#FFF0F5", finish_type: "SATIN", opacity: 0.60, costAmount: 249000 },
  { sku: "DEMO-AR-03", variantName: "Màu 3", shade_hex: "#CD9B9B", finish_type: "MATTE", opacity: 0.58, costAmount: 259000 },
  { sku: "DEMO-AR-04", variantName: "Màu 4", shade_hex: "#CD5C5C", finish_type: "TINT", opacity: 0.50, costAmount: 239000 },
  { sku: "DEMO-AR-05", variantName: "Màu 5", shade_hex: "#EE6363", finish_type: "SATIN", opacity: 0.54, costAmount: 249000 }
];

async function seed() {
    if (DRY_RUN) console.log("--- DRY RUN MODE ---");
    
    try {
        await mongoose.connect(process.env.MONGODB_URI);
        console.log("Connected to MongoDB.");

        for (const fixture of FIXTURES) {
            const existing = await ProductVariant.findOne({ sku: fixture.sku });
            
            if (existing) {
                console.log(`[SKIP] Variant ${fixture.sku} already exists.`);
                continue;
            }

            const newVariant = new ProductVariant({
                productId: PRODUCT_ID,
                sku: fixture.sku,
                variantName: fixture.variantName,
                variantStatus: "active",
                costAmount: fixture.costAmount,
                ar_config: {
                    enabled: true,
                    model_type: "lip_v1",
                    shade_hex: fixture.shade_hex,
                    finish_type: fixture.finish_type,
                    opacity: fixture.opacity
                }
            });

            if (!DRY_RUN) {
                await newVariant.save();
                console.log(`[CREATED] Variant ${fixture.sku} (${fixture.variantName})`);
            } else {
                console.log(`[DRY-RUN] Would create variant ${fixture.sku} (${fixture.variantName})`);
            }
        }
    } catch (e) {
        console.error("Seed error:", e);
    } finally {
        await mongoose.disconnect();
        console.log("Disconnected.");
    }
}

seed();
