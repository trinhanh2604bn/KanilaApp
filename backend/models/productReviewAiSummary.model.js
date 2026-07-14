const mongoose = require("mongoose");

const productReviewAiSummarySchema = new mongoose.Schema(
  {
    product_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: true,
      index: true,
    },

    segment_type: {
      type: String,
      default: "ALL", // ALL, SKIN_TYPE, SKIN_TONE
    },
    segment_value: {
      type: String,
      default: null,
    },
    language: {
      type: String,
      default: "vi",
    },

    status: {
      type: String,
      enum: ["PENDING", "GENERATING", "READY", "STALE", "FAILED", "DISABLED", "INSUFFICIENT_REVIEWS"],
      default: "PENDING",
    },

    short_summary: {
      type: String,
      default: "",
    },

    positive_themes: {
      type: [
        {
          code: String,
          title: String,
          description: String,
          supporting_review_refs: [String],
          _id: false,
        },
      ],
      default: [],
    },

    negative_themes: {
      type: [
        {
          code: String,
          title: String,
          description: String,
          supporting_review_refs: [String],
          _id: false,
        },
      ],
      default: [],
    },

    common_experiences: {
      type: [String],
      default: [],
    },
    usage_tips: {
      type: [String],
      default: [],
    },
    cautions: {
      type: [String],
      default: [],
    },

    source_review_count: {
      type: Number,
      default: 0,
    },
    sampled_review_count: {
      type: Number,
      default: 0,
    },
    verified_review_count: {
      type: Number,
      default: 0,
    },

    source_reviews_hash: {
      type: String,
      default: null,
    },
    latest_review_updated_at: {
      type: Date,
      default: null,
    },

    provider: {
      type: String,
      default: "gemini",
    },
    model_name: {
      type: String,
      default: "",
    },
    prompt_version: {
      type: String,
      default: "review_summary_prompt_v1",
    },
    algorithm_version: {
      type: String,
      default: "review_summary_ai_v1",
    },

    generation_started_at: {
      type: Date,
      default: null,
    },
    generation_lock_until: {
      type: Date,
      default: null,
      index: true,
    },
    generated_at: {
      type: Date,
      default: null,
    },
    stale_at: {
      type: Date,
      default: null,
    },

    retry_count: {
      type: Number,
      default: 0,
    },
    next_retry_at: {
      type: Date,
      default: null,
      index: true,
    },
    last_error_code: {
      type: String,
      default: null,
    },
  },
  {
    timestamps: true,
    collection: "product_review_ai_summaries",
  }
);

productReviewAiSummarySchema.index(
  { product_id: 1, segment_type: 1, segment_value: 1, language: 1 },
  { unique: true }
);

productReviewAiSummarySchema.index({ product_id: 1, status: 1 });

module.exports = mongoose.model("ProductReviewAiSummary", productReviewAiSummarySchema);
