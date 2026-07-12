const mongoose = require('mongoose');
require('dotenv').config({ path: '.env' });

async function fixFoundationFinish() {
  await mongoose.connect(process.env.MONGO_URI);
  const db = mongoose.connection.db;
  
  // Find all profiles where foundation_finish is an array
  const profiles = await db.collection('customer_beauty_profiles').find({ 
    foundation_finish: { $type: "array" } 
  }).toArray();
  
  console.log(`Found ${profiles.length} profiles with array foundation_finish`);
  
  let updated = 0;
  for (const profile of profiles) {
    let newStr = "unknown";
    if (profile.foundation_finish && profile.foundation_finish.length > 0) {
      newStr = profile.foundation_finish[0]; // Take the first preference
    }
    await db.collection('customer_beauty_profiles').updateOne(
      { _id: profile._id },
      { $set: { foundation_finish: String(newStr) } }
    );
    updated++;
  }
  
  console.log(`Fixed ${updated} profiles.`);
  process.exit(0);
}

fixFoundationFinish().catch(err => {
  console.error(err);
  process.exit(1);
});
