const mongoose = require('mongoose');
async function run() {
  try {
    const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
    await mongoose.connect(uri);
    const db = mongoose.connection.db;
    const arConfigs = await db.collection('product_ar_configs').find({}).limit(2).toArray();
    console.log("AR CONFIGS:");
    console.log(JSON.stringify(arConfigs, null, 2));
    await mongoose.disconnect();
  } catch (err) {
    console.error(err);
  }
}
run();
