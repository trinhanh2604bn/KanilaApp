const mongoose = require("mongoose");

const customerProductMatchSnapshotSchema = new mongoose.Schema(
  {
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      required: true,
      index: true,
    },
    product_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: true,
      index: true,
    },

    profile_hash: {
      type: String,
      required: true,
    },
    product_match_hash: {
      type: String,
      required: true,
    },
    algorithm_version: {
      type: String,
      required: true,
    },

    status: {
      type: String,
      enum: ["READY", "PROFILE_REQUIRED", "PROFILE_INCOMPLETE", "INSUFFICIENT_PRODUCT_DATA", "TEMPORARILY_UNAVAILABLE", "CAUTION"],
      default: "READY",
    },

    score: {
      type: Number,
      default: 0,
    },
    estimated_score: {
      type: Number,
      default: null,
    },
    match_level: {
      type: String,
      enum: ["EXCELLENT_MATCH", "GOOD_MATCH", "MODERATE_MATCH", "CAUTION", "INSUFFICIENT_DATA"],
      default: "INSUFFICIENT_DATA",
    },
    confidence_score: {
      type: Number,
      default: 0,
    },
    profile_completion_rate: {
      type: Number,
      default: 0,
    },
    matching_data_completeness: {
      type: Number,
      default: 0,
    },
    
    match_explanation: {
      type: String,
      default: "",
    },

    reasons: {
      type: [
        {
          code: String,
          text: String,
          contribution: Number,
          _id: false,
        },
      ],
      default: [],
    },

    cautions: {
      type: [
        {
          code: String,
          text: String,
          severity: String,
          _id: false,
        },
      ],
      default: [],
    },

    hard_conflicts: {
      type: [
        {
          code: String,
          text: String,
          _id: false,
        },
      ],
      default: [],
    },

    matched_attributes: {
      type: [String],
      default: [],
    },
    score_breakdown: {
      type: mongoose.Schema.Types.Mixed,
      default: {},
    },

    generated_at: {
      type: Date,
      default: Date.now,
    },
    expires_at: {
      type: Date,
      default: null,
      index: true,
    },
    invalidated_at: {
      type: Date,
      default: null,
    },
  },
  {
    timestamps: true,
    collection: "customer_product_match_snapshots",
  }
);

customerProductMatchSnapshotSchema.index({ customer_id: 1, product_id: 1 }, { unique: true });
customerProductMatchSnapshotSchema.index({ customer_id: 1, invalidated_at: 1 });
customerProductMatchSnapshotSchema.index({ product_id: 1 });

module.exports = mongoose.model("CustomerProductMatchSnapshot", customerProductMatchSnapshotSchema);
