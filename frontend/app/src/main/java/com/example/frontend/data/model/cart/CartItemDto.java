package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItemDto implements Serializable {
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

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public String getSkuSnapshot() {
        return skuSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public String getVariantNameSnapshot() {
        return variantNameSnapshot;
    }

    public String getBrandNameSnapshot() {
        return brandNameSnapshot;
    }

    public String getImageUrlSnapshot() {
        return imageUrlSnapshot;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public double getFinalUnitPriceAmount() {
        return finalUnitPriceAmount;
    }

    public double getLineTotalAmount() {
        return lineTotalAmount;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setBrandNameSnapshot(String brandNameSnapshot) {
        this.brandNameSnapshot = brandNameSnapshot;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public void setVariantNameSnapshot(String variantNameSnapshot) {
        this.variantNameSnapshot = variantNameSnapshot;
    }

    public void setFinalUnitPriceAmount(double finalUnitPriceAmount) {
        this.finalUnitPriceAmount = finalUnitPriceAmount;
        this.lineTotalAmount = this.finalUnitPriceAmount * this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
        this.lineTotalAmount = this.finalUnitPriceAmount * this.quantity;
    }

    public static CartItemDto createMock(String id, String name, String variant, double price,
                                         int quantity, boolean selected, String imageUrl) {
        CartItemDto item = new CartItemDto();
        item.id = id;
        item.productNameSnapshot = name;
        item.variantNameSnapshot = variant;
        item.finalUnitPriceAmount = price;
        item.unitPriceAmount = price;
        item.quantity = Math.max(1, quantity);
        item.selected = selected;
        item.imageUrlSnapshot = imageUrl;
        item.lineTotalAmount = price * item.quantity;
        item.stockStatus = "in_stock";
        return item;
    }
}