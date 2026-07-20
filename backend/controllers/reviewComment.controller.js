const ReviewComment = require("../models/reviewComment.model");
const Review = require("../models/review.model");
const Customer = require("../models/customer.model");
const validateObjectId = require("../utils/validateObjectId");

const CUST = "customer_code full_name avatar_url";

const getCustomerFromAuth = async (req) => {
  const accountId = req.user?.account_id || req.user?.accountId;
  if (!accountId) return null;
  return Customer.findOne({ account_id: accountId });
};

const addReviewComment = async (req, res) => {
  try {
    const { reviewId } = req.params;
    const { commentContent } = req.body;

    if (!validateObjectId(reviewId)) return res.status(400).json({ success: false, message: "Invalid reviewId" });
    if (!commentContent || String(commentContent).trim().length === 0) {
      return res.status(400).json({ success: false, message: "Comment content is required" });
    }

    const customer = await getCustomerFromAuth(req);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized" });

    const review = await Review.findById(reviewId);
    if (!review) return res.status(404).json({ success: false, message: "Review not found" });
    if (review.reviewStatus !== "visible") {
      return res.status(400).json({ success: false, message: "Cannot comment on a hidden review" });
    }

    const comment = await ReviewComment.create({
      reviewId,
      customer_id: customer._id,
      commentContent: String(commentContent).trim(),
      commentStatus: "visible"
    });

    const populatedComment = await ReviewComment.findById(comment._id).populate("customer_id", CUST);

    res.status(201).json({
      success: true,
      message: "Comment added successfully",
      data: populatedComment
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

const getReviewComments = async (req, res) => {
  try {
    const { reviewId } = req.params;
    if (!validateObjectId(reviewId)) return res.status(400).json({ success: false, message: "Invalid reviewId" });

    const comments = await ReviewComment.find({ reviewId, commentStatus: "visible" })
      .populate("customer_id", CUST)
      .sort({ createdAt: 1 }); // Ascending order for conversation flow

    res.status(200).json({
      success: true,
      message: "Comments retrieved successfully",
      count: comments.length,
      data: comments
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = {
  addReviewComment,
  getReviewComments
};
