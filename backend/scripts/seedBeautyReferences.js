const mongoose = require("mongoose");
require("dotenv").config();
const BeautyReference = require("../models/beautyReference.model");

// Re-using the enum groups specified in requirements
const skin_type = [
  { code: "oily", vi: "Da dầu" },
  { code: "dry", vi: "Da khô" },
  { code: "combination", vi: "Da hỗn hợp" },
  { code: "normal", vi: "Da thường" },
  { code: "sensitive", vi: "Da nhạy cảm" },
  { code: "unknown", vi: "Chưa chắc chắn" },
];

const skin_concern = [
  { code: "acne", vi: "Mụn" },
  { code: "dark_spots", vi: "Thâm mụn" },
  { code: "melasma", vi: "Nám/sạm màu" },
  { code: "dullness", vi: "Da xỉn màu" },
  { code: "large_pores", vi: "Lỗ chân lông to" },
  { code: "blackheads", vi: "Mụn đầu đen" },
  { code: "redness", vi: "Da dễ đỏ" },
  { code: "dehydrated", vi: "Da thiếu nước" },
  { code: "wrinkles", vi: "Nếp nhăn/lão hóa" },
  { code: "uneven_texture", vi: "Bề mặt da không mịn" },
  { code: "damaged_barrier", vi: "Hàng rào da yếu" },
  { code: "sun_damage", vi: "Da chịu nắng nhiều" },
];

const sensitivity_level = [
  { code: "low", vi: "Ít nhạy cảm" },
  { code: "medium", vi: "Dễ kích ứng nhẹ" },
  { code: "high", vi: "Rất nhạy cảm" },
  { code: "reactive", vi: "Dễ đỏ/rát khi đổi sản phẩm" },
  { code: "unknown", vi: "Chưa chắc chắn" },
];

const skin_tone = [
  { code: "fair", vi: "Rất sáng" },
  { code: "light", vi: "Sáng" },
  { code: "medium", vi: "Trung bình" },
  { code: "tan", vi: "Ngăm" },
  { code: "deep", vi: "Tối" },
  { code: "unknown", vi: "Chưa chắc chắn" },
];

const undertone = [
  { code: "cool", vi: "Lạnh" },
  { code: "warm", vi: "Ấm" },
  { code: "neutral", vi: "Trung tính" },
  { code: "olive", vi: "Olive" },
  { code: "unknown", vi: "Chưa chắc chắn" },
];

const shade_preference = [
  { code: "natural", vi: "Tự nhiên" },
  { code: "brightening", vi: "Sáng da" },
  { code: "warm_glow", vi: "Ấm và rạng rỡ" },
  { code: "pinkish", vi: "Hồng hào" },
  { code: "matte", vi: "Lì tự nhiên" },
];

const lip_color_preference = [
  { code: "nude", vi: "Nude" },
  { code: "pink", vi: "Hồng" },
  { code: "coral", vi: "Cam san hô" },
  { code: "red", vi: "Đỏ" },
  { code: "brown", vi: "Nâu" },
  { code: "mlbb", vi: "Môi tự nhiên" },
  { code: "bold", vi: "Nổi bật" },
];

const makeup_style = [
  { code: "natural", vi: "Tự nhiên" },
  { code: "korean", vi: "Hàn Quốc" },
  { code: "glam", vi: "Nổi bật" },
  { code: "office", vi: "Công sở" },
  { code: "party", vi: "Dự tiệc" },
  { code: "daily", vi: "Hằng ngày" },
];

const beauty_goal = [
  { code: "hydration", vi: "Cấp ẩm" },
  { code: "brightening", vi: "Làm sáng da" },
  { code: "acne_care", vi: "Hỗ trợ giảm mụn" },
  { code: "oil_control", vi: "Kiểm soát dầu" },
  { code: "barrier_repair", vi: "Phục hồi hàng rào da" },
  { code: "anti_aging", vi: "Chống lão hóa" },
  { code: "pore_care", vi: "Chăm sóc lỗ chân lông" },
  { code: "soothing", vi: "Làm dịu da" },
  { code: "sun_protection", vi: "Chống nắng" },
  { code: "even_tone", vi: "Làm đều màu da" },
];

const avoid_ingredient = [
  { code: "fragrance", vi: "Hương liệu" },
  { code: "alcohol_denat", vi: "Cồn khô" },
  { code: "essential_oil", vi: "Tinh dầu" },
  { code: "paraben", vi: "Paraben" },
  { code: "mineral_oil", vi: "Dầu khoáng" },
  { code: "silicone", vi: "Silicone" },
  { code: "sulfate", vi: "Sulfate" },
  { code: "lanolin", vi: "Lanolin" },
  { code: "retinoid", vi: "Retinoid" },
  { code: "aha_bha_high", vi: "Acid nồng độ cao" },
];

const preferred_ingredient = [
  { code: "niacinamide", vi: "Niacinamide" },
  { code: "hyaluronic_acid", vi: "Hyaluronic Acid" },
  { code: "ceramide", vi: "Ceramide" },
  { code: "centella", vi: "Rau má / Centella" },
  { code: "panthenol", vi: "Panthenol" },
  { code: "vitamin_c", vi: "Vitamin C" },
  { code: "bha", vi: "BHA" },
  { code: "aha", vi: "AHA" },
  { code: "retinol", vi: "Retinol" },
  { code: "peptide", vi: "Peptide" },
  { code: "zinc_pca", vi: "Zinc PCA" },
  { code: "tranexamic_acid", vi: "Tranexamic Acid" },
];

const budget_range = [
  { code: "under_200k", vi: "Dưới 200.000đ" },
  { code: "200_500k", vi: "200.000đ - 500.000đ" },
  { code: "500_1000k", vi: "500.000đ - 1.000.000đ" },
  { code: "premium", vi: "Trên 1.000.000đ" },
];

const texture_preference = [
  { code: "gel", vi: "Gel" },
  { code: "cream", vi: "Kem" },
  { code: "lotion", vi: "Lotion" },
  { code: "serum", vi: "Serum" },
  { code: "oil", vi: "Dầu" },
  { code: "balm", vi: "Balm" },
];

const finish_preference = [
  { code: "matte", vi: "Lì" },
  { code: "dewy", vi: "Căng bóng" },
  { code: "natural", vi: "Tự nhiên" },
  { code: "glowy", vi: "Rạng rỡ" },
];

const fragrance_preference = [
  { code: "fragrance_free", vi: "Không hương liệu" },
  { code: "light_fragrance", vi: "Hương nhẹ" },
  { code: "no_preference", vi: "Không yêu cầu" },
];

const purchase_intent = [
  { code: "daily_use", vi: "Dùng hằng ngày" },
  { code: "treatment", vi: "Cải thiện vấn đề da" },
  { code: "gift", vi: "Làm quà tặng" },
  { code: "try_new", vi: "Thử sản phẩm mới" },
  { code: "repurchase", vi: "Mua lại" },
];

const referencesData = {
  skin_type,
  skin_concern,
  sensitivity_level,
  skin_tone,
  undertone,
  shade_preference,
  lip_color_preference,
  makeup_style,
  beauty_goal,
  avoid_ingredient,
  preferred_ingredient,
  budget_range,
  texture_preference,
  finish_preference,
  fragrance_preference,
  purchase_intent
};

const connectDB = async () => {
  try {
    const mongoURI = process.env.MONGODB_URI || "mongodb://127.0.0.1:27017/kanila";
    await mongoose.connect(mongoURI);
    console.log("MongoDB connected successfully");
  } catch (error) {
    console.error("MongoDB connection failed:", error.message);
    process.exit(1);
  }
};

const seedData = async () => {
  await connectDB();

  let totalInserted = 0;
  try {
    console.log("Seeding beauty references...");
    for (const [group, items] of Object.entries(referencesData)) {
      let order = 1;
      for (const item of items) {
        await BeautyReference.updateOne(
          { reference_group: group, reference_code: item.code },
          {
            $set: {
              reference_group: group,
              reference_code: item.code,
              display_name_vi: item.vi,
              sort_order: order,
            }
          },
          { upsert: true }
        );
        order++;
        totalInserted++;
      }
    }

    console.log(`Successfully seeded ${totalInserted} beauty references.`);
  } catch (error) {
    console.error("Error seeding beauty reference data:", error);
  } finally {
    process.exit(0);
  }
};

seedData();
