package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Product {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("productName")
    private String name;
    
    @SerializedName("productCode")
    private String productCode;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("brandId")
    private Object brandId;
    
    @SerializedName("brandName")
    private String brandName;
    
    @SerializedName("categoryId")
    private Object categoryId;
    
    @SerializedName("price")
    private double priceValue;
    
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
    private double averageRatingValue;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("productStatus")
    private String productStatus;

    @SerializedName("ingredientText")
    private String ingredientText;

    @SerializedName("usageInstruction")
    private String usageInstruction;
    
    @SerializedName("skin_types_supported")
    private List<String> skinTypesSupported;
    
    @SerializedName("concerns_targeted")
    private List<String> concernsTargeted;
    
    @SerializedName("is_best_seller")
    private boolean bestSeller;
    
    @SerializedName("sales_count")
    private int salesCount;

    @SerializedName("reviewCount")
    private String reviewCount;

    @SerializedName("subcategory")
    private String subcategory;

    @SerializedName("hasAr")
    private boolean hasAr;

    @SerializedName(value = "arType", alternate = {"ar_type"})
    private String arType;

    private double score;

    private boolean isFavorite;

    private int imageResource;

    private String badgeText;

    @SerializedName("shades")
    private List<Shade> shades;

    public static class Shade {
        @SerializedName(value = "name", alternate = {"shadeName", "shade_name"})
        private String shadeName;

        @SerializedName("hex")
        private String hex;

        public String getShadeName() {
            return shadeName != null ? shadeName : "";
        }

        public String getHex() {
            return hex;
        }
    }

    public Product() {}

    public Product(String id, String brandName, String productName, String price, String averageRating, String reviewCount, int imageResource, String badgeText, String subcategory) {
        this.id = id;
        this.brandName = brandName;
        this.name = productName;
        try {
            this.priceValue = Double.parseDouble(price);
        } catch (Exception e) {
            this.priceValue = 0;
        }
        try {
            this.averageRatingValue = Double.parseDouble(averageRating);
        } catch (Exception e) {
            this.averageRatingValue = 0;
        }
        this.reviewCount = reviewCount;
        this.imageResource = imageResource;
        this.badgeText = badgeText;
        this.subcategory = subcategory;
        if ("Best Seller".equalsIgnoreCase(badgeText)) {
            this.bestSeller = true;
        }
    }

    public String getId() { return id; }
    
    public String getName() { return name != null ? name : ""; }
    
    public String getBrand() { 
        if (brandName != null && !brandName.isEmpty()) return brandName;
        if (brandId instanceof com.google.gson.internal.LinkedTreeMap) {
            Object nameObj = ((com.google.gson.internal.LinkedTreeMap<?, ?>) brandId).get("brandName");
            if (nameObj != null) return nameObj.toString();
        }
        if (brandId != null) return brandId.toString();
        return "";
    }
    
    public String getPrice() { 
        if (priceValue == 0) return "Liên hệ";
        return String.format(java.util.Locale.US, "%,.0fđ", priceValue).replace(",", ".");
    }
    
    public String getRating() { return String.valueOf(averageRatingValue); }
    
    public String getReviewCount() { 
        if (reviewCount != null) return reviewCount;
        return String.valueOf(bought); 
    }
    
    public String getImageUrl() { return imageUrl; }
    
    public int getImageResource() { return imageResource; }

    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getSubcategory() { return subcategory != null ? subcategory : ""; }

    public String getShortDescription() { return shortDescription; }

    public String getLongDescription() { return longDescription; }

    public int getBought() { return bought; }

    public int getStock() { return stock; }

    public Double getCompareAtPrice() { return compareAtPrice; }

    public boolean hasAr() { return hasAr; }

    public void setHasAr(boolean hasAr) { this.hasAr = hasAr; }

    public String getArType() { return arType; }
    
    public void setArType(String arType) { this.arType = arType; }

    public double getScore() { return score; }

    public void setScore(double score) { this.score = score; }
    
    public String getBadgeText() { 
        if (badgeText != null && !badgeText.isEmpty()) return badgeText;
        if (bestSeller) return "Best Seller";
        return "";
    }

    public Object getBrandId() { return brandId; }
    public Object getCategoryId() { return categoryId; }
    public String getSlug() { return slug; }
    public double getPriceValue() { return priceValue; }
    public double getAverageRatingValue() { return averageRatingValue; }
    public boolean isBestSeller() { return bestSeller; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public List<String> getSkinTypesSupported() { return skinTypesSupported; }
    public List<Shade> getShades() { return shades; }
    public String getIngredientText() { return ingredientText; }
    public String getUsageInstruction() { return usageInstruction; }
}
