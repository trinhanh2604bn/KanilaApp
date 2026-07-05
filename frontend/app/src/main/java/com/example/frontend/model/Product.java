package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Product {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("productCode")
    private String productCode;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("brandId")
    private String brandId;
    
    @SerializedName("brandName") // Often populated or added in controllers
    private String brandName;
    
    @SerializedName("categoryId")
    private String categoryId;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("compareAtPrice")
    private Double compareAtPrice;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("shortDescription")
    private String shortDescription;
    
    @SerializedName("longDescription")
    private String longDescription;
    
    @SerializedName("stock")
    private int stock;
    
    @SerializedName("bought")
    private int bought;
    
    @SerializedName("averageRating")
    private double averageRating;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("productStatus")
    private String productStatus;
    
    @SerializedName("skin_types_supported")
    private List<String> skinTypesSupported;
    
    @SerializedName("concerns_targeted")
    private List<String> concernsTargeted;
    
    @SerializedName("is_best_seller")
    private boolean isBestSeller;
    
    @SerializedName("sales_count")
    private int salesCount;

    @SerializedName("reviewCount")
    private String reviewCount;

    @SerializedName("subcategory")
    private String subcategory;

    @SerializedName("hasAr")
    private boolean hasAr;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    @SerializedName("shades")
    private List<Shade> shades;

    // UI-only fields
    private int imageResource;
    private String badgeText;

    public static class Shade {
        @SerializedName("shadeName")
        private String shadeName;
        @SerializedName("hex")
        private String hex;

        public String getShadeName() { return shadeName; }
        public String getHex() { return hex; }
    }

    public Product() {}

    // Constructor to support old sample data creation with subcategory
    public Product(String id, String brandName, String productName, String priceStr, String averageRating, String reviewCount, int imageResource, String badgeText, String subcategory) {
        this(id, brandName, productName, priceStr, averageRating, reviewCount, imageResource, badgeText);
        this.subcategory = subcategory;
    }

    // Constructor to support old sample data creation
    public Product(String id, String brandName, String productName, String priceStr, String averageRating, String reviewCount, int imageResource, String badgeText) {
        this.id = id;
        this.brandName = brandName;
        this.productName = productName;
        try {
            this.price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            this.price = 0;
        }
        try {
            this.averageRating = Double.parseDouble(averageRating);
        } catch (Exception e) {
            this.averageRating = 0;
        }
        this.reviewCount = reviewCount;
        this.imageResource = imageResource;
        this.badgeText = badgeText;
    }

    // Getters for compatibility with existing UI code
    public String getId() { return id; }
    
    public String getName() { return productName != null ? productName : ""; }
    
    public String getBrand() { 
        return brandName != null ? brandName : (brandId != null ? brandId : ""); 
    }
    
    public String getPrice() { 
        if (price == 0) return "Liên hệ";
        return String.format(java.util.Locale.US, "%,.0fđ", price).replace(",", ".");
    }
    
    public String getRating() { return String.valueOf(averageRating); }
    
    public String getReviewCount() { 
        if (reviewCount != null) return reviewCount;
        return String.valueOf(bought); 
    }
    
    public String getImageUrl() { return imageUrl; }
    
    public int getImageResource() { return imageResource; }

    public String getSubcategory() { return subcategory != null ? subcategory : ""; }

    public boolean hasAr() { return hasAr; }

    public void setHasAr(boolean hasAr) { this.hasAr = hasAr; }

    public boolean isFavorite() { return isFavorite; }

    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public String getBadgeText() { 
        if (badgeText != null && !badgeText.isEmpty()) return badgeText;
        if (isBestSeller) return "Best Seller";
        return "";
    }

    // Full schema getters
    public String getProductCode() { return productCode; }
    public String getSlug() { return slug; }
    public double getPriceValue() { return price; }
    public double getAverageRatingValue() { return averageRating; }
    public boolean isBestSeller() { return isBestSeller; }
    public List<String> getSkinTypesSupported() { return skinTypesSupported; }
    public List<Shade> getShades() { return shades; }
}
