package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ProductVariantDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("variant_name")
    private String variantName;

    @SerializedName("price")
    private Double price;

    @SerializedName("stock_quantity")
    private int stockQuantity;

    @SerializedName("image_url")
    private String imageUrl;

    public String getId() { return id; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }
}
