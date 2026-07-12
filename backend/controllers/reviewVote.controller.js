const ReviewVote = require("../models/reviewVote.model");
const Review = require("../models/review.model");
const Customer = require("../models/customer.model");
const validateObjectId = require("../utils/validateObjectId");
const { pickCustomerId } = require("../utils/pickCustomerRef");

const CUST = "customer_code full_name";

const getAllReviewVotes = async (req, res) => {
  try {
    const votes = await ReviewVote.find().populate("reviewId", "reviewTitle").populate("customer_id", CUST).sort({ createdAt: -1 });
    res.status(200).json({ success: true, message: "Get all review votes successfully", count: votes.length, data: votes });
  } catch (error) { res.status(500).json({ success: false, message: error.message }); }
};

const getReviewVoteById = async (req, res) => {
  try {
    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });
    const vote = await ReviewVote.findById(id).populate("reviewId", "reviewTitle").populate("customer_id", CUST);
    if (!vote) return res.status(404).json({ success: false, message: "Review vote not found" });
    res.status(200).json({ success: true, message: "Get review vote successfully", data: vote });
  } catch (error) { res.status(500).json({ success: false, message: error.message }); }
};

const getVotesByReviewId = async (req, res) => {
  try {
    const { reviewId } = req.params;
    if (!validateObjectId(reviewId)) return res.status(400).json({ success: false, message: "Invalid review ID" });
    const votes = await ReviewVote.find({ reviewId }).populate("customer_id", CUST).sort({ createdAt: -1 });
    res.status(200).json({ success: true, message: "Get votes by review successfully", count: votes.length, data: votes });
  } catch (error) { res.status(500).json({ success: false, message: error.message }); }
};

const createReviewVote = async (req, res) => {
  try {
    const { reviewId, voteType } = req.body;
    const customer_id = pickCustomerId(req.body);
    if (!reviewId || !customer_id || !voteType) return res.status(400).json({ success: false, message: "reviewId, customer_id, and voteType are required" });

    const payload = { ...req.body, customer_id };
    delete payload.customerId;
    const vote = await ReviewVote.create(payload);

    // Update helpfulCount on the review
    if (voteType === "helpful") {
      await Review.findByIdAndUpdate(reviewId, { $inc: { helpfulCount: 1 } });
    }

    res.status(201).json({ success: true, message: "Review vote created successfully", data: vote });
  } catch (error) { res.status(500).json({ success: false, message: error.message }); }
};

const deleteReviewVote = async (req, res) => {
  try {
    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });
    const vote = await ReviewVote.findByIdAndDelete(id);
    if (!vote) return res.status(404).json({ success: false, message: "Review vote not found" });

    // Decrement helpfulCount if was helpful
    if (vote.voteType === "helpful") {
      await Review.findByIdAndUpdate(vote.reviewId, { $inc: { helpfulCount: -1 } });
    }

    res.status(200).json({ success: true, message: "Review vote deleted successfully", data: vote });
  } catch (error) { res.status(500).json({ success: false, message: error.message }); }
};

// POST /api/reviews/:reviewId/vote
// Toggle like behavior: if exists delete and decrement helpfulCount, if not create and increment.
const voteOnReview = async (req, res) => {
  try {
    const { reviewId } = req.params;
    const voteType = req.body.voteType || "helpful"; // Default to helpful if not provided, treated as "like"

    if (!validateObjectId(reviewId)) return res.status(400).json({ success: false, message: "Invalid reviewId" });

    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId) return res.status(401).json({ success: false, message: "Unauthorized" });

    const customer = await Customer.findOne({ account_id: accountId });
    if (!customer) return res.status(404).json({ success: false, message: "Customer not found" });

    const review = await Review.findById(reviewId);
    if (!review) return res.status(404).json({ success: false, message: "Review not found" });

    const existingVote = await ReviewVote.findOne({ reviewId, customer_id: customer._id });

    if (existingVote) {
      // Toggle off: delete vote and decrement helpfulCount
      await ReviewVote.findByIdAndDelete(existingVote._id);

      const newHelpfulCount = Math.max(0, (review.helpfulCount || 0) - 1);
      await Review.findByIdAndUpdate(reviewId, { helpfulCount: newHelpfulCount });

      return res.status(200).json({
        success: true,
        message: "Vote removed",
        data: { reviewId, liked: false, helpfulCount: newHelpfulCount }
      });
    } else {
      // Toggle on: create vote and increment helpfulCount
      await ReviewVote.create({ reviewId, customer_id: customer._id, voteType: "helpful" });

      const newHelpfulCount = (review.helpfulCount || 0) + 1;
      await Review.findByIdAndUpdate(reviewId, { helpfulCount: newHelpfulCount });

      return res.status(201).json({
        success: true,
        message: "Vote recorded",
        data: { reviewId, liked: true, helpfulCount: newHelpfulCount }
      });
    }
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = {
  getAllReviewVotes,
  getReviewVoteById,
  getVotesByReviewId,
  createReviewVote,
  deleteReviewVote,
  voteOnReview,
};
