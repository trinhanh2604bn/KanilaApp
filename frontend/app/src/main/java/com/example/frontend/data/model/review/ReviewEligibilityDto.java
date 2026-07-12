package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;

public class ReviewEligibilityDto {
    @SerializedName("eligible")
    private boolean eligible;

    @SerializedName("verifiedPurchaseFlag")
    private boolean verifiedPurchaseFlag;

    @SerializedName("existingReview")
    private ExistingReview existingReview;

    @SerializedName("preview")
    private ReviewPreview preview;

    public boolean isEligible() { return eligible; }
    public boolean isVerifiedPurchaseFlag() { return verifiedPurchaseFlag; }
    public ExistingReview getExistingReview() { return existingReview; }
    public ReviewPreview getPreview() { return preview; }

    public static class ExistingReview {
        @SerializedName("id")
        private String id;
        @SerializedName("reviewStatus")
        private String reviewStatus;
        @SerializedName("rating")
        private int rating;

        public String getId() { return id; }
        public String getReviewStatus() { return reviewStatus; }
        public int getRating() { return rating; }
    }

    public static class ReviewPreview {
        @SerializedName("orderItemId")
        private String orderItemId;
        @SerializedName("productId")
        private String productId;
        @SerializedName("productName")
        private String productName;
        @SerializedName("productImageUrl")
        private String productImageUrl;
        @SerializedName("variantId")
        private String variantId;
        @SerializedName("variantLabel")
        private String variantLabel;
        @SerializedName("sku")
        private String sku;
        @SerializedName("orderNumber")
        private String orderNumber;
        @SerializedName("orderPlacedAt")
        private String orderPlacedAt;

        public String getOrderItemId() { return orderItemId; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductImageUrl() { return productImageUrl; }
        public String getVariantId() { return variantId; }
        public String getVariantLabel() { return variantLabel; }
        public String getSku() { return sku; }
        public String getOrderNumber() { return orderNumber; }
        public String getOrderPlacedAt() { return orderPlacedAt; }
    }
}
