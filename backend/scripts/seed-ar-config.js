const mongoose = require("mongoose");
const path = require("path");
require("dotenv").config({ path: path.join(__dirname, "../.env") });

const ProductArConfig = require("../models/productArConfig.model");

const MONGO_URI = process.env.MONGODB_URI || "mongodb://127.0.0.1:27017/kanila";

const seedArConfigs = async () => {
  try {
    await mongoose.connect(MONGO_URI);
    console.log("Connected to DB...");

    // Clear existing AR configs
    await ProductArConfig.deleteMany();
    console.log("Cleared existing AR Configs");

    // Fetch some products natively to bypass schema mismatches
    const products = await mongoose.connection.db.collection('products').find().limit(3).toArray();

    if (products.length === 0) {
      console.log("No products found to seed AR configs. Please seed products first.");
      process.exit(1);
    }

    // Mock AR configs mapping
    const mockConfigs = [
      {
        makeup_type: "lipstick",
        texture_url: "models/lipstick.png",
        hex_color: "#E53935", // Red
        intensity: 0.8
      },
      {
        makeup_type: "blush",
        texture_url: "models/blush.png",
        hex_color: "#F48FB1", // Pink
        intensity: 0.6
      },
      {
        makeup_type: "eyeshadow",
        texture_url: "models/eyeShadow.png",
        hex_color: "#8E24AA", // Purple
        intensity: 0.7
      }
    ];

    for (let i = 0; i < products.length; i++) {
      const product = products[i];
      const config = mockConfigs[i % mockConfigs.length];

      console.log("Seeding for product id:", product._id);
      await mongoose.connection.db.collection('product_ar_configs').insertOne({
        product_id: product._id,
        makeup_type: config.makeup_type,
        texture_url: config.texture_url,
        hex_color: config.hex_color,
        intensity: config.intensity,
        is_active: true,
        created_at: new Date(),
        updated_at: new Date()
      });
      
      // Also update the Product's hasAr flag
      await mongoose.connection.db.collection('products').updateOne(
        { _id: product._id },
        { $set: { hasAr: true } }
      );
      
      console.log(`Seeded AR Config for product: ${product.name || product.productName} (${config.makeup_type})`);
    }

    console.log("AR Config seeding completed!");
    process.exit(0);
  } catch (error) {
    console.error("Error seeding AR Configs:", error);
    process.exit(1);
  }
};

seedArConfigs();
