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

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    private int logoRes;
    private boolean isFavorite;
    private String region;

    public Brand() {}

    public String getId() { return id; }
    public String getBrandName() { return brandName; }
    public String getBrandCode() { return brandCode; }
    public String getDescription() { return description; }
    public String getLogoUrl() { return logoUrl; }
    public String getBrandStatus() { return brandStatus; }
    public boolean isActive() { return isActive; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public int getLogoRes() { return logoRes; }
    public void setLogoRes(int logoRes) { this.logoRes = logoRes; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
