package com.example.frontend.data.model.cart;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CartDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("owner_type")
    private String ownerType;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("guest_session_id")
    private String guestSessionId;

    @SerializedName("cart_status")
    private String cartStatus;

    @SerializedName("currency_code")
    private String currencyCode;

    @SerializedName("item_count")
    private int itemCount;

    @SerializedName("subtotal_amount")
    private double subtotalAmount;

    @SerializedName("discount_amount")
    private double discountAmount;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("items")
    private List<CartItemDto> items;

    public String getId() {
        return id;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getGuestSessionId() {
        return guestSessionId;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public static CartDto createEmptyGuestCart() {
        CartDto cart = new CartDto();
        cart.ownerType = "guest";
        cart.cartStatus = "active";
        cart.currencyCode = "VND";
        cart.itemCount = 0;
        cart.subtotalAmount = 0;
        cart.discountAmount = 0;
        cart.totalAmount = 0;
        cart.items = new ArrayList<>();
        return cart;
    }

    public static CartDto createMockCart(List<CartItemDto> items, double subtotal, double discount, double total) {
        CartDto cart = new CartDto();
        cart.id = "mock_cart_id";
        cart.ownerType = "guest";
        cart.cartStatus = "active";
        cart.currencyCode = "VND";
        cart.items = items;
        cart.itemCount = items != null ? items.size() : 0;
        cart.subtotalAmount = subtotal;
        cart.discountAmount = discount;
        cart.totalAmount = total;
        return cart;
    }
}
