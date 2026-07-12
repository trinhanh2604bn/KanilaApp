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

    @SerializedName("media")
    private List<MediaInfo> media;

    @SerializedName("variantId")
    private VariantInfo variant;

    @SerializedName("verifiedPurchaseFlag")
    private boolean verifiedPurchaseFlag;

    @SerializedName("helpfulCount")
    private int helpfulCount;

    @SerializedName("isLikedByMe")
    private boolean isLikedByMe;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public CustomerInfo getCustomer() { return customer; }
    public int getRating() { return rating; }
    public String getReviewTitle() { return reviewTitle; }
    public String getReviewContent() { return reviewContent; }
    public List<MediaInfo> getMedia() { return media; }
    public VariantInfo getVariant() { return variant; }
    public boolean isVerifiedPurchase() { return verifiedPurchaseFlag; }
    public int getHelpfulCount() { return helpfulCount; }
    public boolean isLikedByMe() { return isLikedByMe; }
    public void setLikedByMe(boolean likedByMe) { isLikedByMe = likedByMe; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    public String getCreatedAt() { return createdAt; }

    public static class CustomerInfo {
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("avatar_url")
        private String avatarUrl;
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
        public String getVariantName() { return variantName; }
    }
}
