const mongoose = require("mongoose");

const reviewCommentSchema = new mongoose.Schema(
  {
    reviewId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Review",
      required: true,
      index: true,
    },
    customer_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Customer",
      required: true,
      index: true,
    },
    commentContent: {
      type: String,
      required: true,
      trim: true,
      maxlength: 1000,
    },
    commentStatus: {
      type: String,
      enum: ["visible", "hidden"],
      default: "visible",
    },
  },
  { timestamps: true }
);

reviewCommentSchema.index({ reviewId: 1, createdAt: -1 });

module.exports = mongoose.model("ReviewComment", reviewCommentSchema);
