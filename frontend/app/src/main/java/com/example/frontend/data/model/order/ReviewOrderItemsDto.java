package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewOrderItemsDto {
    @SerializedName("orderId")
    private String orderId;

    @SerializedName("orderCode")
    private String orderCode;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("deliveredAt")
    private String deliveredAt;

    @SerializedName("items")
    private List<ReviewItemDto> items;

    public String getOrderId() { return orderId; }
    public String getOrderCode() { return orderCode; }
    public String getOrderStatus() { return orderStatus; }
    public String getDeliveredAt() { return deliveredAt; }
    public List<ReviewItemDto> getItems() { return items; }

    public static class ReviewItemDto {
        @SerializedName("orderItemId")
        private String orderItemId;

        @SerializedName("productId")
        private String productId;

        @SerializedName("variantId")
        private String variantId;

        @SerializedName("productName")
        private String productName;

        @SerializedName("variantName")
        private String variantName;

        @SerializedName(value = "image_url_snapshot", alternate = {"imageUrl", "image_url"})
        private String imageUrl;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("unitPrice")
        private double unitPrice;

        @SerializedName("reviewStatus")
        private String reviewStatus;

        @SerializedName("reviewId")
        private String reviewId;

        public String getOrderItemId() { return orderItemId; }
        public String getProductId() { return productId; }
        public String getVariantId() { return variantId; }
        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public String getImageUrl() { return imageUrl; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public String getReviewStatus() { return reviewStatus; }
        public String getReviewId() { return reviewId; }
    }
}
