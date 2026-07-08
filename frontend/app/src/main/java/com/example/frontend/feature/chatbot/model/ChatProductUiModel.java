package com.example.frontend.feature.chatbot.model;

public class ChatProductUiModel {
    private final String productId;
    private final String variantId;
    private final String slug;
    private final String name;
    private final String brandName;
    private final String priceText;
    private final String compareAtPriceText;
    private final String imageUrl;
    private final String ratingText;
    private final String reviewCountText;
    private final String stockStatus;
    private final String reason;

    public ChatProductUiModel(String productId, String variantId, String slug, String name, String brandName,
                             String priceText, String compareAtPriceText, String imageUrl, String ratingText,
                             String reviewCountText, String stockStatus, String reason) {
        this.productId = productId;
        this.variantId = variantId;
        this.slug = slug;
        this.name = name;
        this.brandName = brandName;
        this.priceText = priceText;
        this.compareAtPriceText = compareAtPriceText;
        this.imageUrl = imageUrl;
        this.ratingText = ratingText;
        this.reviewCountText = reviewCountText;
        this.stockStatus = stockStatus;
        this.reason = reason;
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

    public String getPriceText() {
        return priceText;
    }

    public String getCompareAtPriceText() {
        return compareAtPriceText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRatingText() {
        return ratingText;
    }

    public String getReviewCountText() {
        return reviewCountText;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public String getReason() {
        return reason;
    }
}
