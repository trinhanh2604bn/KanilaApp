const SearchService = require("./services/search.service");
const mongoose = require("mongoose");
require("dotenv").config();

async function test() {
  await mongoose.connect(process.env.MONGODB_URI || "mongodb://localhost:27017/kanila");
  try {
    const res = await SearchService.searchProducts("kem nền", { limit: 10 });
    console.log("Success:", res);
  } catch (err) {
    console.error("Error:", err);
  }
  process.exit();
}
test();
