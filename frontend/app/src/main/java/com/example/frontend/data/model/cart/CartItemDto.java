package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

public class CartItemDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("variant_id")
    private String variantId;

    @SerializedName("sku_snapshot")
    private String skuSnapshot;

    @SerializedName("product_name_snapshot")
    private String productNameSnapshot;

    @SerializedName("variant_name_snapshot")
    private String variantNameSnapshot;

    @SerializedName("brand_name_snapshot")
    private String brandNameSnapshot;

    @SerializedName("image_url_snapshot")
    private String imageUrlSnapshot;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unit_price_amount")
    private double unitPriceAmount;

    @SerializedName("final_unit_price_amount")
    private double finalUnitPriceAmount;

    @SerializedName("line_total_amount")
    private double lineTotalAmount;

    @SerializedName("selected")
    private boolean selected;

    @SerializedName("stock_status")
    private String stockStatus;

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getVariantId() { return variantId; }
    public String getSkuSnapshot() { return skuSnapshot; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public String getVariantNameSnapshot() { return variantNameSnapshot; }
    public String getBrandNameSnapshot() { return brandNameSnapshot; }
    public String getImageUrlSnapshot() { return imageUrlSnapshot; }
    public int getQuantity() { return quantity; }
    public double getUnitPriceAmount() { return unitPriceAmount; }
    public double getFinalUnitPriceAmount() { return finalUnitPriceAmount; }
    public double getLineTotalAmount() { return lineTotalAmount; }
    public boolean isSelected() { return selected; }
    public String getStockStatus() { return stockStatus; }
}
