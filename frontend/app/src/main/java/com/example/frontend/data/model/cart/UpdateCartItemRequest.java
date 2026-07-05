package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

public class UpdateCartItemRequest {
    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("selected")
    private Boolean selected;

    public UpdateCartItemRequest(Integer quantity, Boolean selected) {
        this.quantity = quantity;
        this.selected = selected;
    }
}
