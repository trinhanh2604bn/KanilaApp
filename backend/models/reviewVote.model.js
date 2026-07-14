const mongoose = require("mongoose");

const reviewVoteSchema = new mongoose.Schema(
  {
    reviewId: { type: mongoose.Schema.Types.ObjectId, ref: "Review", required: true },
    customer_id: { type: mongoose.Schema.Types.ObjectId, ref: "Customer", required: true },
    voteType: { type: String, enum: ["helpful", "not_helpful"], required: true },
  },
  { timestamps: true, collection: "review_votes" }
);

reviewVoteSchema.index({ reviewId: 1, customer_id: 1, voteType: 1 }, { unique: true });

module.exports = mongoose.model("ReviewVote", reviewVoteSchema);
