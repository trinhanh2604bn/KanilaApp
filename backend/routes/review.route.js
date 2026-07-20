const express = require("express");
const router = express.Router();
const authMiddleware = require("../middlewares/auth.middleware");
const optionalAuthMiddleware = require("../middlewares/optionalAuth.middleware");
const upload = require("../middlewares/upload.middleware");
const {
  getAllReviews,
  getReviewById,
  getReviewsByProductId,
  createReview,
  updateReview,
  deleteReview,
  getReviewWriteEligibility,
  submitReviewFromOrderItem,
  submitReviewDirect,
  getMyReviews,
  getMyReviewDetail,
  patchMyReview,
  deleteMyReview,
  getReviewableItems,
} = require("../controllers/review.controller");
const { voteOnReview } = require("../controllers/reviewVote.controller");
const { addReviewComment, getReviewComments } = require("../controllers/reviewComment.controller");
router.get("/", getAllReviews);
router.get("/product/:productId", optionalAuthMiddleware, getReviewsByProductId);
router.post("/", createReview);
router.put("/:id", updateReview);
router.delete("/:id", deleteReview);

// Helpful vote for a review (auth required)
router.post("/:reviewId/vote", authMiddleware, voteOnReview);

// Authenticated review flows
router.get("/me", authMiddleware, getMyReviews);
router.get("/me/:id", authMiddleware, getMyReviewDetail);
router.get("/write-eligibility/:orderItemId", authMiddleware, getReviewWriteEligibility);
router.get("/reviewable-items/:productId", authMiddleware, getReviewableItems);
router.post("/submit", authMiddleware, upload.array("medias", 10), submitReviewFromOrderItem);
router.post("/submit-direct", authMiddleware, upload.array("medias", 10), submitReviewDirect);
router.patch("/me/:id", authMiddleware, patchMyReview);
router.delete("/me/:id", authMiddleware, deleteMyReview);

// Review comments
router.get("/:reviewId/comments", getReviewComments);
router.post("/:reviewId/comments", authMiddleware, addReviewComment);

// Public single review read
router.get("/:id", getReviewById);
module.exports = router;
