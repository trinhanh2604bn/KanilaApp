package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("categoryCode")
    private String categoryCode;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("parentCategoryId")
    private String parentCategoryId;
    
    @SerializedName("displayOrder")
    private int displayOrder;
    
    @SerializedName("categoryStatus")
    private String categoryStatus;
    
    @SerializedName("isActive")
    private boolean isActive;

    public String getId() { return id; }
    public String getCategoryName() { return categoryName; }
    public String getCategoryCode() { return categoryCode; }
    public String getDescription() { return description; }
    public String getParentCategoryId() { return parentCategoryId; }
    public int getDisplayOrder() { return displayOrder; }
    public String getCategoryStatus() { return categoryStatus; }
    public boolean isActive() { return isActive; }
}
