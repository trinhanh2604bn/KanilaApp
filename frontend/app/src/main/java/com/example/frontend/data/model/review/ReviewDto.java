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
    public CustomerInfo getCustomer() { return customer; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public int getHelpfulCount() { return helpfulCount; }
    public VariantInfo getVariant() { return variant; }
    public List<MyReviewDto.ReviewMediaDto> getMedia() { return media; }

    public static class CustomerInfo {
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("avatar_url")
        private String avatarUrl;

        public String getFullName() { return fullName; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    public static class VariantInfo {
        @SerializedName("variantName")
        private String variantName;
        public String getVariantName() { return variantName; }
    }
}
