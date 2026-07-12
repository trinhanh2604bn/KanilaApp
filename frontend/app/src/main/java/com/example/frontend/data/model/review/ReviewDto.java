package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("productId")
    private String productId;

    @SerializedName("customer_id")
    private CustomerInfo customer;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewTitle")
    private String reviewTitle;

    @SerializedName("reviewContent")
    private String reviewContent;
    
    @SerializedName("content")
    private String content;

    @SerializedName("media")
    private List<MediaInfo> media;

    @SerializedName("variantId")
    private VariantInfo variant;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("verifiedPurchaseFlag")
    private boolean verifiedPurchaseFlag;

    @SerializedName("helpfulCount")
    private int helpfulCount;

    @SerializedName("isLikedByMe")
    private boolean isLikedByMe;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }

    public String getReviewContent() { return reviewContent; }
    public void setReviewContent(String reviewContent) { this.reviewContent = reviewContent; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<MediaInfo> getMedia() { return media; }
    public void setMedia(List<MediaInfo> media) { this.media = media; }

    public VariantInfo getVariant() { return variant; }
    public void setVariant(VariantInfo variant) { this.variant = variant; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isVerifiedPurchase() { return verifiedPurchaseFlag; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchaseFlag = verifiedPurchase; }

    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }

    public boolean isLikedByMe() { return isLikedByMe; }
    public void setLikedByMe(boolean likedByMe) { isLikedByMe = likedByMe; }

    public static class CustomerInfo {
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("avatar_url")
        private String avatarUrl;

        public CustomerInfo() {}
        public CustomerInfo(String fullName, String avatarUrl) {
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
        }

        public String getFullName() { return fullName; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    public static class MediaInfo {
        @SerializedName("mediaUrl")
        private String mediaUrl;
        @SerializedName("mediaType")
        private String mediaType;
        public String getMediaUrl() { return mediaUrl; }
        public String getMediaType() { return mediaType; }
    }

    public static class VariantInfo {
        @SerializedName("variantName")
        private String variantName;

        public VariantInfo() {}
        public VariantInfo(String variantName) {
            this.variantName = variantName;
        }

        public String getVariantName() { return variantName; }
    }
}
