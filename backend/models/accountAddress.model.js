const mongoose = require("mongoose");
const accountAddressSchema = new mongoose.Schema({
  account_id: { type: mongoose.Schema.Types.ObjectId, ref: "Account", required: true },
  full_name: { type: String, default: "" },
  phone: { type: String, default: "" },
  address_line: { type: String, default: "" },
  is_default: { type: Boolean, default: false },
}, { timestamps: true });
module.exports = mongoose.model("AccountAddress", accountAddressSchema, "account_addresses");
