const mongoose = require("mongoose");
const ProductSearchDocument = require("./models/productSearchDocument.model");
require("dotenv").config();

async function check() {
  await mongoose.connect(process.env.MONGODB_URI || "mongodb://localhost:27017/kanila");
  
  const sample = await ProductSearchDocument.findOne({ category_names_normalized: /kem nen/i });
  console.log("Sample kem nen doc:", sample ? sample.product_name : "None");
  
  const anySample = await ProductSearchDocument.findOne();
  console.log("Any Sample name:", anySample.product_name_normalized);
  console.log("Any Sample category:", anySample.category_names_normalized);
  
  process.exit();
}
check();
