"use strict";
const mongoose = require("mongoose");

/**
 * search_aliases
 *
 * Versioned alias dictionary mapping Vietnamese / abbreviated inputs
 * to canonical makeup terms.
 */
const searchAliasSchema = new mongoose.Schema(
  {
    alias:              { type: String, required: true },
    alias_normalized:   { type: String, required: true },
    canonical_term:     { type: String, required: true },
    canonical_normalized: { type: String, required: true },
    entity_type: {
      type: String,
      enum: ["generic", "brand", "category", "product", "characteristic"],
      default: "generic",
    },
    entity_id:         { type: mongoose.Schema.Types.ObjectId, default: null },
    category_context:  { type: String, default: null }, // e.g. "lip", "face", "eye"
    priority:          { type: Number, default: 0 },
    is_active:         { type: Boolean, default: true },
  },
  { timestamps: true, collection: "search_aliases" }
);

searchAliasSchema.index({ alias_normalized: 1, is_active: 1 });
searchAliasSchema.index({ canonical_normalized: 1 });
searchAliasSchema.index({ entity_type: 1 });
searchAliasSchema.index({ is_active: 1 });

module.exports = mongoose.model("SearchAlias", searchAliasSchema);
