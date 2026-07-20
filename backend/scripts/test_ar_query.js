const mongoose = require('mongoose');

async function test() {
  const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
  await mongoose.connect(uri);
  const db = mongoose.connection.db;
  
  const productId = '000000000000000000000fc3'; // MAC Matte Lipstick
  
  const oidQuery = { product_id: new mongoose.Types.ObjectId(productId) };
  const config = await db.collection('product_ar_configs').findOne(oidQuery);
  
  if (config) {
    console.log('SUCCESS - AR Config found!');
    console.log('Product name:', config.product_name);
    console.log('ar_type:', config.ar_type);
    console.log('Variants count:', config.variants && config.variants.length);
    if (config.variants && config.variants.length > 0) {
      console.log('\nFirst variant:');
      console.log(JSON.stringify(config.variants[0], null, 2));
    }
  } else {
    console.log('FAIL - AR Config NOT found for product:', productId);
  }
  
  // Also list all products with hasAr=true and their ar_type
  const arProducts = await db.collection('products').find({ hasAr: true, ar_type: { $exists: true } }, {
    projection: { productName: 1, ar_type: 1 }
  }).toArray();
  console.log('\nProducts with AR (' + arProducts.length + '):');
  arProducts.forEach(p => console.log(' -', p._id, '|', p.ar_type, '|', p.productName));
  
  await mongoose.disconnect();
}
test().catch(console.error);
