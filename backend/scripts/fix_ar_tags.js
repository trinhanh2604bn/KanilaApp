const mongoose = require('mongoose');
async function fixArTags() {
  const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
  await mongoose.connect(uri);
  const db = mongoose.connection.db;
  
  // Set all to false first
  await db.collection('products').updateMany({}, { $set: { hasAr: false } });
  
  // Find all products that have an AR config
  const configs = await db.collection('product_ar_configs').find({}).toArray();
  const productIds = configs.map(c => {
    try {
      return new mongoose.Types.ObjectId(c.product_id);
    } catch(e) {
      return c.product_id;
    }
  });
  
  // Set hasAr to true for those products
  const res = await db.collection('products').updateMany(
    { _id: { $in: productIds } }, 
    { $set: { hasAr: true } }
  );
  
  console.log(`Updated ${res.modifiedCount} products to have AR tag.`);
  await mongoose.disconnect();
}
fixArTags().catch(console.error);
