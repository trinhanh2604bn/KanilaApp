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

    @SerializedName("price")
    private Long price;

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

    public Long getPrice() {
        return price;
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

    public String getReason() {
        return reason;
    }
}
