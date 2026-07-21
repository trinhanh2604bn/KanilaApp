const mongoose = require("mongoose");
const SearchService = require("./services/search.service");
const SearchQueryNormalizer = require("./utils/searchQueryNormalizer");
require("dotenv").config();

async function test() {
  await mongoose.connect(process.env.MONGO_URI);
  
  const res = await SearchService.searchProducts("son", { limit: 10 });
  console.log("Found items for 'son':", res.items.length);
  
  process.exit();
}
test();
