const mongoose = require('mongoose');

async function test() {
  const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
  await mongoose.connect(uri);
  const db = mongoose.connection.db;
  
  // Check how product_id is actually stored in ar configs
  const sampleConfig = await db.collection('product_ar_configs').findOne({});
  console.log('Sample AR config product_id type:', typeof sampleConfig?.product_id);
  console.log('Sample AR config product_id value:', sampleConfig?.product_id);
  console.log('Sample AR config product_name:', sampleConfig?.product_name);
  
  // Check how product _id is stored
  const sampleProduct = await db.collection('products').findOne({ ar_type: 'LIPS', hasAr: true });
  console.log('\nProduct _id type:', typeof sampleProduct?._id);
  console.log('Product _id value:', sampleProduct?._id);
  console.log('Product _id toString:', sampleProduct?._id?.toString());
  
  await mongoose.disconnect();
}
test().catch(console.error);
