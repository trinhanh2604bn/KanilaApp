package com.example.frontend.feature.chatbot.model;

public class ChatProductUiModel {
    private final String productId;
    private final String variantId;
    private final String slug;
    private final String name;
    private final String brandName;
    private final String categoryName;
    private final String priceText;
    private final String finalPriceText;
    private final String compareAtPriceText;
    private final String imageUrl;
    private final String ratingText;
    private final String reviewCountText;
    private final String stockStatus;
    private String reason;
    private final String suggestedUse;
    private final String action;

    public ChatProductUiModel(String productId, String variantId, String slug, String name, String brandName,
                             String priceText, String compareAtPriceText, String imageUrl, String ratingText,
                             String reviewCountText, String stockStatus, String reason) {
        this(productId, variantId, slug, name, brandName, null, priceText, null, compareAtPriceText, imageUrl, ratingText, reviewCountText, stockStatus, reason, null, null);
    }

    public ChatProductUiModel(String productId, String variantId, String slug, String name, String brandName,
                             String categoryName, String priceText, String finalPriceText, String compareAtPriceText, 
                             String imageUrl, String ratingText, String reviewCountText, String stockStatus, 
                             String reason, String suggestedUse, String action) {
        this.productId = productId;
        this.variantId = variantId;
        this.slug = slug;
        this.name = name;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.priceText = priceText;
        this.finalPriceText = finalPriceText;
        this.compareAtPriceText = compareAtPriceText;
        this.imageUrl = imageUrl;
        this.ratingText = ratingText;
        this.reviewCountText = reviewCountText;
        this.stockStatus = stockStatus;
        this.reason = reason;
        this.suggestedUse = suggestedUse;
        this.action = action;
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

    public String getPriceText() {
        return priceText;
    }

    public String getFinalPriceText() {
        return finalPriceText;
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

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSuggestedUse() {
        return suggestedUse;
    }

    public String getAction() {
        return action;
    }
}
