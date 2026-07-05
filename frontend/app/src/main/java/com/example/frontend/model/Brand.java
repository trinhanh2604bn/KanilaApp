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

    public String getId() { return id; }
    public String getBrandName() { return brandName; }
    public String getBrandCode() { return brandCode; }
    public String getDescription() { return description; }
    public String getLogoUrl() { return logoUrl; }
    public String getBrandStatus() { return brandStatus; }
    public boolean isActive() { return isActive; }
}
