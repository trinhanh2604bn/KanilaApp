package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ProductAttributeDto {
    @SerializedName("_id")
    private String id;
    @SerializedName("attributeName")
    private String attributeName;
    @SerializedName("attributeValue")
    private String attributeValue;
    @SerializedName("displayOrder")
    private int displayOrder;

    public String getId() { return id; }
    public String getAttributeName() { return attributeName; }
    public String getAttributeValue() { return attributeValue; }
}
