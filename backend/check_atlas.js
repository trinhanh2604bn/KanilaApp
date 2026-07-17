const mongoose = require("mongoose");
const ProductSearchDocument = require("./models/productSearchDocument.model");
require("dotenv").config();

async function check() {
  await mongoose.connect(process.env.MONGO_URI);
  
  const docs = await ProductSearchDocument.find().limit(5).lean();
  console.log("Sample product_name_normalized:");
  docs.forEach(d => console.log(` - ${d.product_name_normalized}`));
  
  const kemNen = await ProductSearchDocument.countDocuments({ product_name_normalized: /kem nen/i });
  console.log("Docs containing 'kem nen':", kemNen);
  
  process.exit();
}
check();
