package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

public class Brand {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("brandName")
    private String brandName;
    
    @SerializedName("brandCode")
    private String brandCode;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("logoUrl")
    private String logoUrl;
    
    @SerializedName("brandStatus")
    private String brandStatus;
    
    @SerializedName("isActive")
    private boolean isActive;

    // Các thuộc tính bổ sung phục vụ logic UI hiện tại (chưa có trong Backend)
    private boolean isFavorite;
    private int logoRes;
    private String region;

    public Brand() {}

    // Constructor 4 tham số để fix lỗi trong BrandPageFragment
    public Brand(String brandName, int logoRes, boolean isFavorite, String region) {
        this.brandName = brandName;
        this.logoRes = logoRes;
        this.isFavorite = isFavorite;
        this.region = region;
    }

    public String getId() { return id; }
    public String getBrandName() { return brandName; }
    public String getBrandCode() { return brandCode; }
    public String getDescription() { return description; }
    public String getLogoUrl() { return logoUrl; }
    public String getBrandStatus() { return brandStatus; }
    public boolean isActive() { return isActive; }
    
    // Getter/Setter cho logic UI
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public int getLogoRes() { return logoRes; }
    public String getRegion() { return region; }
}
