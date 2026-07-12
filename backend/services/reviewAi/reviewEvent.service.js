const ProductReviewAiSummary = require("../../models/productReviewAiSummary.model");

class ReviewEventService {
  async markProductReviewAiSummaryStale(productId) {
    try {
      await ProductReviewAiSummary.updateMany(
        { 
          product_id: productId,
          status: "READY"
        },
        { 
          $set: { 
            status: "STALE",
            stale_at: new Date()
          } 
        }
      );
    } catch (err) {
      console.error(`Failed to mark AI summary stale for product ${productId}:`, err);
    }
  }
}

module.exports = new ReviewEventService();
