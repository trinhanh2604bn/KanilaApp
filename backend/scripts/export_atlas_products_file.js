const mongoose = require('mongoose');
const fs = require('fs');

async function run() {
  try {
    const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
    await mongoose.connect(uri);
    const db = mongoose.connection.db;
    
    // Extract only necessary fields to keep the file clean: _id, productName, hasAr, shades
    const products = await db.collection('products').find({}, {
        projection: { productName: 1, hasAr: 1, shades: 1, price: 1, category: 1 }
    }).toArray();
    
    fs.writeFileSync('d:\\KanilaApp\\products_export.json', JSON.stringify(products, null, 2));
    console.log("Successfully exported " + products.length + " products to d:\\KanilaApp\\products_export.json");
    
    await mongoose.disconnect();
  } catch (err) {
    console.error(err);
  }
}
run();
