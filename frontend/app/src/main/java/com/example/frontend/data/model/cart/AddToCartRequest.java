package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("variant_id")
    private String variantId;

    @SerializedName("quantity")
    private int quantity;

    public AddToCartRequest(String productId, String variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }
}
