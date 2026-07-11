package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyReviewDto {
    @SerializedName("reviewId")
    private String reviewId;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("orderItemId")
    private String orderItemId;

    @SerializedName("product")
    private ProductInfo product;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewContent")
    private String reviewContent;

    @SerializedName("reviewTags")
    private List<String> reviewTags;

    @SerializedName("skinTypes")
    private List<String> skinTypes;

    @SerializedName("media")
    private List<ReviewMediaDto> media;

    @SerializedName("createdAt")
    private String createdAt;

    public String getReviewId() { return reviewId; }
    public String getOrderId() { return orderId; }
    public String getOrderItemId() { return orderItemId; }
    public ProductInfo getProduct() { return product; }
    public int getRating() { return rating; }
    public String getReviewContent() { return reviewContent; }
    public List<String> getReviewTags() { return reviewTags; }
    public List<String> getSkinTypes() { return skinTypes; }
    public List<ReviewMediaDto> getMedia() { return media; }
    public String getCreatedAt() { return createdAt; }

    public static class ProductInfo {
        @SerializedName("productId")
        private String productId;

        @SerializedName("productName")
        private String productName;

        @SerializedName("variantName")
        private String variantName;

        @SerializedName("imageUrl")
        private String imageUrl;

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class ReviewMediaDto {
        @SerializedName("mediaType")
        private String mediaType;

        @SerializedName("mediaUrl")
        private String mediaUrl;

        public String getMediaType() { return mediaType; }
        public String getMediaUrl() { return mediaUrl; }
    }
}
