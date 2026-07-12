package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("customer_id")
    private CustomerInfo customer;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewContent")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("verifiedPurchaseFlag")
    private boolean verifiedPurchase;

    @SerializedName("helpfulCount")
    private int helpfulCount;

    @SerializedName("variantId")
    private VariantInfo variant;

    @SerializedName("media")
    private List<MyReviewDto.ReviewMediaDto> media;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    
    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public VariantInfo getVariant() { return variant; }
    public void setVariant(VariantInfo variant) { this.variant = variant; }
    
    public List<MyReviewDto.ReviewMediaDto> getMedia() { return media; }
    public void setMedia(List<MyReviewDto.ReviewMediaDto> media) { this.media = media; }

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
