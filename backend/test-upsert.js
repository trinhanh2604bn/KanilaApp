

async function test() {
  try {
    // 1. Get an existing customer ID (we will use the first one from DB)
    const mongoose = require('mongoose');
    require('dotenv').config({ path: '.env' });
    await mongoose.connect(process.env.MONGO_URI);
    const profile = await mongoose.connection.db.collection('customer_beauty_profiles').findOne({});
    if (!profile) return console.log("No profile found");
    const customerId = profile.customer_id.toString();

    // 2. We mock the backend request that the Android app would make
    // Since we don't have the auth token easily, we can just hit the service directly
    // Or we can mock the controller logic exactly.
    const service = require("./services/customerBeautyProfile.service");
    
    // Simulate what the Android App sends (CustomerBeautyProfileDto)
    const requestBody = {
      skin_type: "OILY_SKIN",
      skin_color: "fair", // the new field
      budget: "under_300k",
      // Notice: NO skin_tone, NO budget_range
    };
    
    console.log("Simulating PATCH with:", requestBody);
    
    // Mock the validation
    const { validateCustomerBeautyProfile } = require("./validations/customerBeautyProfile.validation");
    const errors = await validateCustomerBeautyProfile(requestBody);
    if (errors.length > 0) {
      console.log("Validation Failed:", errors);
      process.exit(1);
    }
    
    // Run the service
    const updatedProfile = await service.upsertProfile(customerId, requestBody);
    console.log("Upserted Profile:", updatedProfile);
    
    // Check what is in the DB
    const rawInDb = await mongoose.connection.db.collection('customer_beauty_profiles').findOne({ customer_id: profile.customer_id });
    console.log("RAW IN DB:", rawInDb);
    
    process.exit(0);
  } catch(e) {
    console.error(e);
    process.exit(1);
  }
}
test();
