const mongoose = require('mongoose');
async function run() {
  try {
    await mongoose.connect('mongodb://127.0.0.1:27017/kanilabeauty');
    const db = mongoose.connection.db;
    const count = await db.collection('products').countDocuments();
    console.log('Total products:', count);
    
    // Export up to 50 products to a JSON file in the artifacts directory
    const products = await db.collection('products').find({}).limit(50).toArray();
    const fs = require('fs');
    fs.writeFileSync('C:\\Users\\Trinhthingocanh\\.gemini\\antigravity\\brain\\e5f28f5e-2abc-476f-b0a9-723c70405e59\\scratch\\products_export.json', JSON.stringify(products, null, 2));
    console.log('Exported products to scratch directory');
    
    await mongoose.disconnect();
  } catch (err) {
    console.error(err);
  }
}
run();
