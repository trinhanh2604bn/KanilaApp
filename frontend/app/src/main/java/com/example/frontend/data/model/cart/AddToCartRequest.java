package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("productId")
    private String productId;

    @SerializedName("variantId")
    private String variantId;

    @SerializedName("quantity")
    private int quantity;

    public AddToCartRequest(String productId, String variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public int getQuantity() {
        return quantity;
    }
}
