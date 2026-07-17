const mongoose = require("mongoose");
const SearchAlias = require("./models/searchAlias.model");
require("dotenv").config();

async function check() {
  await mongoose.connect(process.env.MONGO_URI);
  
  const aliases = await SearchAlias.find({ alias_normalized: /kem nen/i }).lean();
  console.log("Aliases matching kem nen:", aliases);
  
  const someAliases = await SearchAlias.find().limit(5).lean();
  console.log("Some aliases:", someAliases.map(a => a.alias_normalized));
  
  process.exit();
}
check();
