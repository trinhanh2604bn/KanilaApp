const mongoose = require("mongoose");

const accountAddressSchema = new mongoose.Schema({
  customer_id: { type: mongoose.Schema.Types.ObjectId, ref: "Customer", required: true },
  address_label: { type: String, default: "" },
  recipient_name: { type: String, default: "" },
  phone: { type: String, default: "" },
  address_line_1: { type: String, default: "" },
  address_line_2: { type: String, default: "" },
  ward: { type: String, default: "" },
  district: { type: String, default: "" },
  city: { type: String, default: "" },
  country_code: { type: String, default: "VN" },
  postal_code: { type: String, default: "" },
  address_type: { type: String, default: "home" }, // home, office, etc.
  address_note: { type: String, default: "" },
  is_default: { type: Boolean, default: false },
  is_default_shipping: { type: Boolean, default: false },
  is_default_billing: { type: Boolean, default: false },
}, { timestamps: true });

module.exports = mongoose.model("AccountAddress", accountAddressSchema, "customer_addresses");
