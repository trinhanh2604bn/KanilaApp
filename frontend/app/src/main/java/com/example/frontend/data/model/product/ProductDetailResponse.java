package com.example.frontend.data.model.product;

import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import com.example.frontend.model.Product;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductDetailResponse {
    @SerializedName("product")
    private Product product;

    @SerializedName("brand")
    private Brand brand;

    @SerializedName("categories")
    private List<Category> categories;

    @SerializedName("media")
    private List<ProductMediaDto> media;

    @SerializedName("variants")
    private List<ProductVariantDto> variants;

    @SerializedName("variantMedia")
    private List<VariantMediaDto> variantMedia;

    @SerializedName("attributes")
    private List<ProductAttributeDto> attributes;

    @SerializedName("inventory")
    private InventoryDto inventory;

    @SerializedName("price")
    private PriceDto price;

    @SerializedName("reviewSummary")
    private ReviewSummaryDto reviewSummary;

    @SerializedName("isWishlisted")
    private boolean isWishlisted;

    @SerializedName("skinMatch")
    private SkinMatchDto skinMatch;

    @SerializedName("relatedProducts")
    private RelatedProductsData relatedProducts;

    // Getters
    public Product getProduct() { return product; }
    public Brand getBrand() { return brand; }
    public List<Category> getCategories() { return categories; }
    public List<ProductMediaDto> getMedia() { return media; }
    public List<ProductVariantDto> getVariants() { return variants; }
    public List<VariantMediaDto> getVariantMedia() { return variantMedia; }
    public List<ProductAttributeDto> getAttributes() { return attributes; }
    public InventoryDto getInventory() { return inventory; }
    public PriceDto getPrice() { return price; }
    public ReviewSummaryDto getReviewSummary() { return reviewSummary; }
    public boolean isWishlisted() { return isWishlisted; }
    public SkinMatchDto getSkinMatch() { return skinMatch; }
    public RelatedProductsData getRelatedProducts() { return relatedProducts; }

    public static class InventoryDto {
        @SerializedName("totalAvailable")
        private int totalAvailable;
        @SerializedName("status")
        private String status;
        public int getTotalAvailable() { return totalAvailable; }
        public String getStatus() { return status; }
    }

    public static class PriceDto {
        @SerializedName("currentPrice")
        private double currentPrice;
        @SerializedName("compareAtPrice")
        private Double compareAtPrice;
        @SerializedName("currency")
        private String currency;
        public double getCurrentPrice() { return currentPrice; }
        public Double getCompareAtPrice() { return compareAtPrice; }
    }

    public static class ReviewSummaryDto {
        @SerializedName("averageRating")
        private double averageRating;
        @SerializedName("reviewCount")
        private int reviewCount;
        public double getAverageRating() { return averageRating; }
        public int getReviewCount() { return reviewCount; }
    }

    public static class SkinMatchDto {
        @SerializedName("score")
        private int score;
        @SerializedName("level")
        private String level;
        @SerializedName("profileChips")
        private List<String> profileChips;
        public int getScore() { return score; }
        public String getLevel() { return level; }
        public List<String> getProfileChips() { return profileChips; }
    }

    public static class RelatedProductsData {
        @SerializedName("items")
        private List<Product> items;
        public List<Product> getItems() { return items; }
    }
}
