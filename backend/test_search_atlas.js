const mongoose = require("mongoose");
const SearchService = require("./services/search.service");
require("dotenv").config();

async function test() {
  await mongoose.connect(process.env.MONGO_URI);
  try {
    const res = await SearchService.searchProducts("kem nền", { limit: 10 });
    console.log("Found items:", res.items.length);
    if(res.items.length > 0) {
      console.log("First item:", res.items[0].productName);
    }
  } catch (err) {
    console.error("Error:", err);
  }
  process.exit();
}
test();
