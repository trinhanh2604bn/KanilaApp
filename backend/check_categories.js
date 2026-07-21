const mongoose = require("mongoose");
require("dotenv").config();
const Category = require("./models/category.model");

async function check() {
  await mongoose.connect(process.env.MONGO_URI);
  const categories = await Category.find().lean();
  console.log("Categories in DB:");
  categories.forEach(c => console.log(c.categoryName));
  process.exit();
}
check();
