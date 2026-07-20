const mongoose = require("mongoose");
const ProductSearchDocument = require("./models/productSearchDocument.model");
const Product = require("./models/product.model");
require("dotenv").config();

async function check() {
  await mongoose.connect(process.env.MONGODB_URI || "mongodb://localhost:27017/kanila");
  
  const searchCount = await ProductSearchDocument.countDocuments();
  const productCount = await Product.countDocuments();
  
  console.log(`ProductSearchDocument count: ${searchCount}`);
  console.log(`Product count: ${productCount}`);
  
  if (searchCount > 0) {
     const sample = await ProductSearchDocument.findOne();
     console.log("Sample search doc terms:", sample.search_terms);
  }
  
  process.exit();
}
check();
