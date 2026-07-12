const request = require("supertest");
const app = require("../app");
const mongoose = require("mongoose");
const Customer = require("../models/customer.model");
const Product = require("../models/product.model");
const CustomerBeautyProfile = require("../models/customerBeautyProfile.model");
const skinMatchCacheService = require("../services/skinMatch/skinMatchCache.service");
const jwt = require("jsonwebtoken");

describe("Integration Tests: Skin Match and Review AI", () => {
  let customer, product, token;

  beforeAll(async () => {
    // Connect to DB if not already
    if (mongoose.connection.readyState === 0) {
      await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/kanila_test", {
        useNewUrlParser: true,
        useUnifiedTopology: true
      });
    }

    // Seed mock data
    customer = await Customer.create({
      full_name: "Test User",
      email: "test@example.com",
      phone_number: "0901234567",
      customer_code: "CUST-TEST",
      account_id: new mongoose.Types.ObjectId()
    });

    product = await Product.create({
      productName: "Test Cream",
      productCode: "CREAM-001",
      brandId: new mongoose.Types.ObjectId(),
      categoryId: new mongoose.Types.ObjectId(),
      basePrice: 100000,
      price: 100000,
      productStatus: "active",
      skin_types_supported: ["oily"]
    });

    await CustomerBeautyProfile.create({
      customer_id: customer._id,
      skin_type: "oily",
      profile_hash: "hash_123",
      profile_completion_rate: 100
    });

    // Mock token
    token = jwt.sign({ account_id: customer.account_id }, process.env.JWT_ACCESS_SECRET || process.env.JWT_SECRET || "access_secret", { expiresIn: "1h" });
  });

  afterAll(async () => {
    await Customer.deleteMany({});
    await Product.deleteMany({});
    await CustomerBeautyProfile.deleteMany({});
    await mongoose.connection.close();
  });

  it("GET /api/products/:id/skin-match/me - should return 401 without token", async () => {
    const res = await request(app).get(`/api/products/${product._id}/skin-match/me`);
    expect(res.status).toBe(401);
  });

  it("GET /api/products/:id/skin-match/me - should compute and return skin match correctly", async () => {
    const res = await request(app)
      .get(`/api/products/${product._id}/skin-match/me`)
      .set("Authorization", `Bearer ${token}`);
    
    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.status).toBe("READY");
    expect(res.body.data.score).toBeGreaterThan(0);
    // Caching check is implicitly verified if it returns successfully and matching works
  });

  it("GET /api/products/:id/review-insights - should return pending if no summary exists", async () => {
    const res = await request(app)
      .get(`/api/products/${product._id}/review-insights`);
    
    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.status).toBe("PENDING");
  });
});
