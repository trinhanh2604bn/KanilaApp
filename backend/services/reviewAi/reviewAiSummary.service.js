const crypto = require("crypto");
const ProductReviewAiSummary = require("../../models/productReviewAiSummary.model");
const Review = require("../../models/review.model");
const Product = require("../../models/product.model");
const geminiProvider = require("./geminiReviewAi.provider");

const ALGORITHM_VERSION = "review_summary_ai_v1";
const PROMPT_VERSION = "review_summary_prompt_v1";

class ReviewAiSummaryService {
  async getReviewInsights(productId, options = {}) {
    const segmentType = options.segmentType || "ALL";
    const language = options.language || "vi";
    
    // Attempt to find existing summary
    const summary = await ProductReviewAiSummary.findOne({
      product_id: productId,
      segment_type: segmentType,
      language: language
    }).lean();

    if (summary && ["READY", "STALE"].includes(summary.status)) {
      // Background revalidation check handled by workers usually, but we can trigger state change here if stale.
      // Returning it directly serves stale-while-revalidate.
      return this._formatResponse(summary);
    }
    
    // If we don't have one, or it's PENDING/FAILED, we return what we have (or nullish)
    if (summary) {
      return this._formatResponse(summary);
    }
    
    // If no record exists at all, create a PENDING one and let the worker pick it up
    await ProductReviewAiSummary.create({
      product_id: productId,
      segment_type: segmentType,
      language: language,
      status: "PENDING"
    });
    
    return {
      status: "PENDING",
      short_summary: "Đang tổng hợp đánh giá...",
      positive_themes: [],
      negative_themes: [],
    };
  }

  async processPendingOrStaleSummaries() {
    const now = new Date();
    // Find summaries that are PENDING or STALE, and not currently locked (or lock expired)
    const summaries = await ProductReviewAiSummary.find({
      status: { $in: ["PENDING", "STALE"] },
      $or: [
        { generation_lock_until: null },
        { generation_lock_until: { $lte: now } }
      ],
      $or: [
        { next_retry_at: null },
        { next_retry_at: { $lte: now } }
      ]
    }).limit(5); // process 5 at a time

    for (const summary of summaries) {
      await this._generateForSummary(summary);
    }
  }

  async _generateForSummary(summary) {
    // 1. Lock the record
    const locked = await ProductReviewAiSummary.findOneAndUpdate(
      { 
        _id: summary._id,
        $or: [
          { generation_lock_until: null },
          { generation_lock_until: { $lte: new Date() } }
        ]
      },
      {
        $set: {
          status: "GENERATING",
          generation_started_at: new Date(),
          generation_lock_until: new Date(Date.now() + 5 * 60 * 1000) // 5 min lock
        }
      },
      { new: true }
    );

    if (!locked) return; // someone else got the lock

    try {
      // 2. Fetch reviews
      // TODO: If segment_type != ALL, we need to join with CustomerBeautyProfile. For MVP, just ALL.
      const reviews = await Review.find({
        productId: summary.product_id,
        reviewStatus: "visible"
      }).sort({ createdAt: -1 }).limit(100).lean();

      if (reviews.length < 5) {
        // Not enough reviews
        locked.status = "INSUFFICIENT_REVIEWS";
        locked.generation_lock_until = null;
        await locked.save();
        return;
      }

      const product = await Product.findById(summary.product_id).populate("categoryId").lean();

      // 3. Compute hash
      const reviewsContent = reviews.map(r => String(r._id) + r.rating + r.reviewContent).join("|");
      const hash = crypto.createHash("md5").update(reviewsContent).digest("hex");

      if (locked.status === "STALE" && locked.source_reviews_hash === hash) {
        // Nothing changed actually
        locked.status = "READY";
        locked.generation_lock_until = null;
        await locked.save();
        return;
      }

      // 4. Call Provider
      const aiResult = await geminiProvider.generateSummary(product, reviews, { language: summary.language });

      // 5. Update DB
      locked.short_summary = aiResult.short_summary;
      locked.positive_themes = aiResult.positive_themes || [];
      locked.negative_themes = aiResult.negative_themes || [];
      locked.common_experiences = aiResult.common_experiences || [];
      locked.usage_tips = aiResult.usage_tips || [];
      locked.cautions = aiResult.cautions || [];
      
      locked.source_review_count = await Review.countDocuments({ productId: summary.product_id, reviewStatus: "visible" });
      locked.sampled_review_count = reviews.length;
      locked.source_reviews_hash = hash;
      locked.latest_review_updated_at = reviews[0]?.updatedAt || new Date();
      
      locked.status = "READY";
      locked.generated_at = new Date();
      locked.generation_lock_until = null;
      locked.retry_count = 0;
      locked.next_retry_at = null;
      locked.last_error_code = null;
      locked.prompt_version = PROMPT_VERSION;
      locked.algorithm_version = ALGORITHM_VERSION;
      
      await locked.save();

    } catch (err) {
      console.error("AI Generation Error:", err);
      locked.retry_count += 1;
      locked.last_error_code = err.message;
      locked.generation_lock_until = null;
      
      if (locked.retry_count >= 3) {
        locked.status = "FAILED";
      } else {
        // Exponential backoff
        locked.status = "PENDING";
        locked.next_retry_at = new Date(Date.now() + Math.pow(2, locked.retry_count) * 60 * 1000);
      }
      
      await locked.save();
    }
  }

  _formatResponse(summary) {
    return {
      status: summary.status,
      short_summary: summary.short_summary,
      positive_themes: summary.positive_themes,
      negative_themes: summary.negative_themes,
      common_experiences: summary.common_experiences,
      usage_tips: summary.usage_tips,
      cautions: summary.cautions,
      sampled_review_count: summary.sampled_review_count,
      generated_at: summary.generated_at
    };
  }
}

module.exports = new ReviewAiSummaryService();
