package com.example.frontend.data.model.category;

import com.google.gson.annotations.SerializedName;

public class CategoryDto {

    @SerializedName(value = "_id", alternate = {"id", "categoryId"})
    private String id;

    @SerializedName("categoryCode")
    private String categoryCode;

    @SerializedName(value = "categoryName", alternate = {"name"})
    private String name;

    @SerializedName("parentCategoryId")
    private String parentCategoryId;

    @SerializedName("parentName")
    private String parentName;

    @SerializedName("slug")
    private String slug;

    @SerializedName("displayOrder")
    private int displayOrder;

    public String getId() {
        return id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getName() {
        return name;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public String getParentName() {
        return parentName;
    }

    public String getSlug() {
        return slug;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}