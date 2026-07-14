require("dotenv").config();
const connectDB = require("./config/db");
const mongoose = require("mongoose");
const Customer = require("./models/customer.model");
const customerBeautyProfileController = require("./controllers/customerBeautyProfile.controller");

async function runTest() {
  await connectDB();
  console.log("Connected to DB.");

  // Get any customer to test with
  let customer = await Customer.findOne();
  if (!customer) {
    customer = await Customer.create({
      first_name: "Test",
      last_name: "Customer",
      email: "test_beauty@example.com",
      phone: "0123456789"
    });
  }
  
  console.log(`Using Customer: ${customer._id}`);

  // Create a mock req and res
  const req = {
    params: { customer_id: customer._id.toString() },
    user: { account_type: "admin" }, // Bypass ownership check
    body: {
      skin_type: "SUPER_DRY", // Should pass (was strict before)
      skin_tone: "FAIR", // Legacy field (should be stripped)
      skin_color: "FAIR",
      budget_range: "LOW", // Legacy field
      budget: "LOW",
      lipstick_colors: ["RED"],
      lip_color_preference: ["RED"], // Legacy field
      preferred_brands: ["Loreal", "Olay", "123"], // Invalid ObjectIds (should be filtered)
      makeup_styles: ["NATURAL"],
      makeup_style: ["NATURAL"], // Legacy field
      unknown_field: "BLAHBLAH" // Should be stripped
    }
  };

  const res = {
    status: function(code) {
      this.statusCode = code;
      return this;
    },
    json: function(data) {
      console.log(`Response Status: ${this.statusCode}`);
      console.log(`Response Data:\n`, JSON.stringify(data, null, 2));
    }
  };

  console.log("\n--- Testing updateProfile ---");
  await customerBeautyProfileController.updateProfile(req, res);
  
  // Also try createProfile if update failed due to "Profile not found"
  if (res.statusCode === 404) {
    console.log("\n--- Profile not found, testing createProfile instead ---");
    // reset req.body since it was mutated (stripped) by detectUnknownFields!
    req.body = {
      skin_type: "SUPER_DRY", // Should pass (was strict before)
      skin_tone: "FAIR", // Legacy field (should be stripped)
      skin_color: "FAIR",
      budget_range: "LOW", // Legacy field
      budget: "LOW",
      lipstick_colors: ["RED"],
      lip_color_preference: ["RED"], // Legacy field
      preferred_brands: ["Loreal", "Olay", "123"], // Invalid ObjectIds (should be filtered)
      makeup_styles: ["NATURAL"],
      makeup_style: ["NATURAL"], // Legacy field
      unknown_field: "BLAHBLAH" // Should be stripped
    };
    await customerBeautyProfileController.createProfile(req, res);
  }

  mongoose.connection.close();
}

runTest().catch(err => {
  console.error("Test Error:", err);
  mongoose.connection.close();
});
