const mongoose = require("mongoose");
const path = require("path");
require("dotenv").config({ path: path.join(__dirname, "../.env") });
const Product = require("../models/product.model");
const ProductVariant = require("../models/productVariant.model");

const isDryRun = process.argv.includes("--dry-run");

async function seedArConfig() {
  try {
    console.log(`Starting AR Config Seed ${isDryRun ? "[DRY RUN]" : ""}`);
    
    // Connect to database
    if (mongoose.connection.readyState === 0) {
      const uri = process.env.MONGODB_URI || "mongodb://localhost:27017/kanila";
      await mongoose.connect(uri);
    }
    
    // Only target makeup lip products
    // Assuming category or some name indicates it's a lipstick
    const lipstickProducts = await Product.find({ 
      productName: { $regex: /lipstick|son môi/i } 
    });

    console.log(`Found ${lipstickProducts.length} potential lipstick products`);

    if (lipstickProducts.length === 0) {
      console.log("No lipstick products found. Creating a dummy one for integration.");
      
      const newProduct = new Product({
        productName: "Son Môi AR Test",
        productCode: "AR-LIP-TEST",
        description: "Test AR Lipstick",
        status: "active"
      });
      
      if (!isDryRun) {
        await newProduct.save();
      }
      lipstickProducts.push(newProduct);
    }

    let updatedCount = 0;

    for (const product of lipstickProducts) {
      const variants = await ProductVariant.find({ productId: product._id });
      
      if (variants.length === 0) {
        console.log(`Creating dummy variants for product: ${product.productName}`);
        const dummyVariants = [
          { sku: `AR-LIP-RED-${product._id.toString().substring(0,4)}`, name: "Đỏ Thuần", hex: "#D13444", finish: "MATTE" },
          { sku: `AR-LIP-PINK-${product._id.toString().substring(0,4)}`, name: "Hồng Đất", hex: "#C76878", finish: "SATIN" },
          { sku: `AR-LIP-ORANGE-${product._id.toString().substring(0,4)}`, name: "Cam Cháy", hex: "#BF5A38", finish: "MATTE" }
        ];

        for (const dv of dummyVariants) {
          if (!isDryRun) {
            await ProductVariant.create({
              productId: product._id,
              sku: dv.sku,
              variantName: dv.name,
              variantStatus: "active",
              costAmount: 299000,
              ar_config: {
                enabled: true,
                type: "LIP",
                shade_hex: dv.hex,
                finish_type: dv.finish,
                opacity: 0.7,
                renderer_version: "lip_v1",
                calibration_status: "VERIFIED"
              }
            });
          }
          updatedCount++;
        }
      } else {
        for (const variant of variants) {
          if (!variant.ar_config || !variant.ar_config.enabled) {
            // Assign some mock color based on variant name or random
            const hex = variant.variantName.toLowerCase().includes("hồng") ? "#C76878" : 
                        variant.variantName.toLowerCase().includes("cam") ? "#BF5A38" : "#D13444";
                        
            variant.ar_config = {
              enabled: true,
              type: "LIP",
              shade_hex: hex,
              finish_type: "MATTE",
              opacity: 0.7,
              renderer_version: "lip_v1",
              calibration_status: "VERIFIED"
            };
            
            if (!isDryRun) {
              await variant.save();
            }
            updatedCount++;
          }
        }
      }
    }
    console.log(`AR Config Seed Completed. Updated/Created ${updatedCount} variants.`);

    // Also update hasAr flag on the products
    for (const product of lipstickProducts) {
      if (!product.hasAr && !isDryRun) {
        product.hasAr = true;
        await product.save();
      }
    }
    console.log("Updated hasAr flag on products.");
    
  } catch (error) {
    console.error("Error during seed:", error);
  } finally {
    if (mongoose.connection.readyState !== 0) {
      await mongoose.disconnect();
    }
    process.exit(0);
  }
}

seedArConfig();
