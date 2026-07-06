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
    private Brand brand;
    
    @SerializedName("brandName")
    private String brandName;
    
    @SerializedName("categoryId")
    private Category category;
    
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

    private boolean isFavorite;

    private int imageResource;

    private String badgeText;

    @SerializedName("shades")
    private List<Shade> shades;

    public static class Shade {
        @SerializedName("shadeName")
        private String shadeName;
        @SerializedName("hex")
        private String hex;

        public String getShadeName() { return shadeName; }
        public String getHex() { return hex; }
    }

    public Product() {}

    public Product(String id, String brandName, String productName, String price, String averageRating, String reviewCount, int imageResource, String badgeText, String subcategory) {
        this.id = id;
        this.brandName = brandName;
        this.productName = productName;
        try {
            this.price = Double.parseDouble(price);
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
        this.subcategory = subcategory;
        if ("Best Seller".equalsIgnoreCase(badgeText)) {
            this.isBestSeller = true;
        }
    }

    public String getId() { return id; }
    
    public String getName() { return productName != null ? productName : ""; }
    
    public String getBrand() { 
        if (brandName != null && !brandName.isEmpty()) return brandName;
        if (brand != null && brand.getBrandName() != null) return brand.getBrandName();
        return "";
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

    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getSubcategory() { return subcategory != null ? subcategory : ""; }

    public boolean hasAr() { return hasAr; }

    public void setHasAr(boolean hasAr) { this.hasAr = hasAr; }
    
    public String getBadgeText() { 
        if (badgeText != null && !badgeText.isEmpty()) return badgeText;
        if (isBestSeller) return "Best Seller";
        return "";
    }

    public Brand getBrandObject() { return brand; }
    public Category getCategoryObject() { return category; }
    public String getSlug() { return slug; }
    public double getPriceValue() { return price; }
    public double getAverageRatingValue() { return averageRating; }
    public boolean isBestSeller() { return isBestSeller; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public List<String> getSkinTypesSupported() { return skinTypesSupported; }
    public List<Shade> getShades() { return shades; }
}
