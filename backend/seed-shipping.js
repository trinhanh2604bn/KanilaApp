require("dotenv").config();
const mongoose = require("mongoose");
const ShippingMethod = require("./models/shippingMethod.model");

const MONGO_URI = process.env.MONGO_URI;

const shippingMethodsData = [
  {
    shipping_method_code: "STANDARD",
    shipping_method_name: "Tiêu chuẩn",
    carrier_code: "VNPOST",
    service_level: "Standard",
    description: "Giao hàng tiêu chuẩn, phù hợp mọi đơn hàng",
    shipping_fee: 15000,
    estimated_delivery: "2 - 3 ngày",
    is_active: true,
  },
  {
    shipping_method_code: "FAST",
    shipping_method_name: "Nhanh",
    carrier_code: "GHTK",
    service_level: "Fast",
    description: "Giao nhanh, tiết kiệm thời gian",
    shipping_fee: 25000,
    estimated_delivery: "1 - 2 ngày",
    is_active: true,
  },
  {
    shipping_method_code: "EXPRESS",
    shipping_method_name: "Hỏa tốc",
    carrier_code: "AHAMOVE",
    service_level: "Express",
    description: "Giao siêu tốc trong ngày (áp dụng nội thành)",
    shipping_fee: 45000,
    estimated_delivery: "Trong ngày",
    is_active: true,
  },
  {
    shipping_method_code: "STORE",
    shipping_method_name: "Nhận tại cửa hàng",
    carrier_code: "KANILA",
    service_level: "Pickup",
    description: "Nhận hàng tại cửa hàng Kanila gần bạn",
    shipping_fee: 0,
    estimated_delivery: "2 - 3 ngày",
    is_active: true,
  },
];

async function seedShipping() {
  try {
    await mongoose.connect(MONGO_URI);
    console.log("✅ Connected to MongoDB Atlas");

    await ShippingMethod.deleteMany({});
    console.log("  🗑️  Cleared shipping_methods");

    const inserted = await ShippingMethod.insertMany(shippingMethodsData);
    console.log(`✅ Inserted ${inserted.length} shipping methods`);

  } catch (err) {
    console.error("❌ Seed error:", err.message);
  } finally {
    await mongoose.disconnect();
    console.log("Disconnected from MongoDB.");
    process.exit(0);
  }
}

seedShipping();
