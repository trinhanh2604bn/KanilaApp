package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatProductResponse {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("variant_id")
    private String variantId;

    @SerializedName("slug")
    private String slug;

    @SerializedName("name")
    private String name;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("price")
    private Long price;

    @SerializedName("final_price")
    private Long finalPrice;

    @SerializedName("compare_at_price")
    private Long compareAtPrice;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("rating")
    private Double rating;

    @SerializedName("review_count")
    private Integer reviewCount;

    @SerializedName("stock_status")
    private String stockStatus;

    @SerializedName("reason")
    private String reason;

    @SerializedName("matched_reason")
    private String matchedReason;

    @SerializedName("suggested_use")
    private String suggestedUse;

    @SerializedName("action")
    private String action;

    @SerializedName("recommendation")
    private Recommendation recommendation;

    // Nested recommendation object from backend
    public static class Recommendation {
        @SerializedName("whyRecommended")
        private String whyRecommended;

        @SerializedName("strengths")
        private String strengths;

        @SerializedName("bestFor")
        private String bestFor;

        @SerializedName("tip")
        private String tip;

        @SerializedName("reason")
        private String reason;

        @SerializedName("usage")
        private String usage;

        public String getWhyRecommended() { return whyRecommended; }
        public String getStrengths() { return strengths; }
        public String getBestFor() { return bestFor; }
        public String getTip() { return tip; }
        public String getReason() { return reason; }
        public String getUsage() { return usage; }
    }

    public String getProductId() {
        return productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getPrice() {
        return price;
    }

    public Long getFinalPrice() {
        return finalPrice;
    }

    public Long getCompareAtPrice() {
        return compareAtPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    /**
     * Returns the best available reason text:
     * Priority: flat reason (may be AI-enriched) > recommendation.whyRecommended > matchedReason
     */
    public String getReason() {
        // Flat reason is updated by backend with AI analysis when available
        if (reason != null && !reason.isEmpty()) return reason;
        // Fallback to nested recommendation whyRecommended
        if (recommendation != null && recommendation.whyRecommended != null && !recommendation.whyRecommended.isEmpty()) {
            return recommendation.whyRecommended;
        }
        return matchedReason;
    }

    /**
     * Returns the full AI analysis text with structured sections.
     * Used for the "Lý do gợi ý" popup dialog.
     */
    public String getFullAiAnalysis() {
        if (recommendation == null) return getReason();

        StringBuilder sb = new StringBuilder();
        if (recommendation.whyRecommended != null && !recommendation.whyRecommended.isEmpty()) {
            sb.append(recommendation.whyRecommended);
        }
        if (recommendation.strengths != null && !recommendation.strengths.isEmpty()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("💪 ").append(recommendation.strengths);
        }
        if (recommendation.bestFor != null && !recommendation.bestFor.isEmpty()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("🎯 ").append(recommendation.bestFor);
        }
        if (recommendation.tip != null && !recommendation.tip.isEmpty()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("💡 ").append(recommendation.tip);
        }
        return sb.length() > 0 ? sb.toString() : getReason();
    }

    public String getSuggestedUse() {
        // Priority: flat suggestedUse > recommendation.usage
        if (suggestedUse != null && !suggestedUse.isEmpty()) return suggestedUse;
        if (recommendation != null && recommendation.usage != null && !recommendation.usage.isEmpty()) {
            return recommendation.usage;
        }
        return suggestedUse;
    }

    public String getAction() {
        return action;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }
}
