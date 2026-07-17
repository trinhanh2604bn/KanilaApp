"use strict";
const mongoose = require("mongoose");

const ALLOWED_EVENT_TYPES = [
  "SEARCH_SUBMITTED",
  "SUGGESTION_VIEWED",
  "SUGGESTION_CLICKED",
  "PRODUCT_CLICKED",
  "SHADE_CLICKED",
  "FILTER_APPLIED",
  "SORT_CHANGED",
  "NO_RESULT",
  "VOICE_SEARCH",
  "BARCODE_SEARCH",
  "IMAGE_SEARCH",
  "AR_TRY_ON_OPENED_FROM_SEARCH",
  "ADD_TO_CART_FROM_SEARCH",
  "WISHLIST_FROM_SEARCH",
];

const searchEventSchema = new mongoose.Schema(
  {
    query_id:         { type: String, default: null },
    customer_id:      { type: mongoose.Schema.Types.ObjectId, ref: "Customer", default: null },
    guest_session_id: { type: String, default: null },
    event_type:       { type: String, enum: ALLOWED_EVENT_TYPES, required: true },
    query:            { type: String, default: "" },
    normalized_query: { type: String, default: "" },
    product_id:       { type: mongoose.Schema.Types.ObjectId, ref: "Product", default: null },
    variant_id:       { type: mongoose.Schema.Types.ObjectId, ref: "ProductVariant", default: null },
    shade_code:       { type: String, default: null },
    result_position:  { type: Number, default: null },
    result_count:     { type: Number, default: 0 },
    filters:          { type: mongoose.Schema.Types.Mixed, default: {} },
    sort:             { type: String, default: null },
    latency_ms:       { type: Number, default: 0 },
    suggestion_type:  { type: String, default: null }, // product|brand|category|shade|query
  },
  { timestamps: true, collection: "search_events" }
);

searchEventSchema.index({ event_type: 1, createdAt: -1 });
searchEventSchema.index({ normalized_query: 1, createdAt: -1 });
searchEventSchema.index({ query_id: 1 });
searchEventSchema.index({ customer_id: 1, createdAt: -1 });
searchEventSchema.index({ product_id: 1 });

module.exports = mongoose.model("SearchEvent", searchEventSchema);
module.exports.ALLOWED_EVENT_TYPES = ALLOWED_EVENT_TYPES;
