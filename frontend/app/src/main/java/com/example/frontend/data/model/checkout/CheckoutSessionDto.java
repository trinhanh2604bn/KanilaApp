package com.example.frontend.data.model.checkout;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CheckoutSessionDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("cart_id")
    private String cartId;

    @SerializedName("session_status")
    private String sessionStatus;

    @SerializedName("subtotal_amount")
    private double subtotalAmount;

    @SerializedName("shipping_amount")
    private double shippingAmount;

    @SerializedName("discount_amount")
    private double discountAmount;

    @SerializedName("points_amount")
    private double pointsAmount;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("shipping_address")
    private CheckoutAddressDto shippingAddress;

    @SerializedName("shipping_method")
    private String shippingMethod;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("items")
    private List<CheckoutItemDto> items;

    public String getId() { return id; }
    public double getSubtotalAmount() { return subtotalAmount; }
    public double getShippingAmount() { return shippingAmount; }
    public double getDiscountAmount() { return discountAmount; }
    public double getPointsAmount() { return pointsAmount; }
    public double getTotalAmount() { return totalAmount; }
    public CheckoutAddressDto getShippingAddress() { return shippingAddress; }
    public String getShippingMethod() { return shippingMethod; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<CheckoutItemDto> getItems() { return items; }

    public static class CheckoutItemDto {
        @SerializedName("product_name")
        private String productName;
        @SerializedName("variant_name")
        private String variantName;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("price")
        private double price;
        @SerializedName("image_url")
        private String imageUrl;

        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class CheckoutAddressDto {
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("phone")
        private String phone;
        @SerializedName("address_line")
        private String addressLine;

        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getAddressLine() { return addressLine; }
    }
}
