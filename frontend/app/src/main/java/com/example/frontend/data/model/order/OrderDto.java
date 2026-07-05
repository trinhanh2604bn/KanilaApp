package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("items")
    private List<OrderItemDto> items;

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderStatus() { return orderStatus; }
    public double getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
    public List<OrderItemDto> getItems() { return items; }

    public static class OrderItemDto {
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
}
