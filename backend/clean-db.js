const mongoose = require('mongoose');
require('dotenv').config({ path: '.env' });
mongoose.connect(process.env.MONGO_URI).then(async () => {
  const r = await mongoose.connection.db.collection('customer_beauty_profiles').updateMany(
    {}, 
    { $unset: {
        skin_tone: "", undertone: "", budget_range: "", finish_preference: "", lip_color_preference: "", makeup_style: ""
      }
    }
  );
  console.log(r);
  process.exit(0);
});
