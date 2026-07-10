package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model representing order summary for list view, matching backend response structure.
 */
public class OrderSummaryDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("fulfillment_status")
    private String fulfillmentStatus;

    @SerializedName("placed_at")
    private String placedAt;

    @SerializedName("grand_total_amount")
    private double grandTotalAmount;

    @SerializedName("subtotal_amount")
    private double subtotalAmount;

    @SerializedName("shipping_fee_amount")
    private double shippingFeeAmount;

    @SerializedName("item_count")
    private int itemCount;

    @SerializedName("total_quantity")
    private int totalQuantity;

    @SerializedName("first_item_name")
    private String firstItemName;

    @SerializedName("first_item_variant")
    private String firstItemVariant;

    @SerializedName("item_previews")
    private List<ItemPreview> itemPreviews;

    @SerializedName("shipment_status")
    private String shipmentStatus;

    @SerializedName("tracking_number")
    private String trackingNumber;

    // Default constructor for GSON
    public OrderSummaryDto() {}

    // Constructor for Mock data
    public OrderSummaryDto(String id, String orderNumber, String orderStatus, double totalAmount, 
                          int totalQuantity, String firstItemName, String firstItemVariant) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.grandTotalAmount = totalAmount;
        this.totalQuantity = totalQuantity;
        this.firstItemName = firstItemName;
        this.firstItemVariant = firstItemVariant;
    }

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public String getPlacedAt() { return placedAt; }
    public double getGrandTotalAmount() { return grandTotalAmount; }
    public double getSubtotalAmount() { return subtotalAmount; }
    public double getShippingFeeAmount() { return shippingFeeAmount; }
    public int getItemCount() { return itemCount; }
    public int getTotalQuantity() { return totalQuantity; }
    public String getFirstItemName() { return firstItemName; }
    public String getFirstItemVariant() { return firstItemVariant; }
    public List<ItemPreview> getItemPreviews() { return itemPreviews; }
    public String getShipmentStatus() { return shipmentStatus; }
    public String getTrackingNumber() { return trackingNumber; }

    public static class ItemPreview {
        @SerializedName("product_name")
        private String productName;
        @SerializedName("variant_name")
        private String variantName;
        @SerializedName("quantity")
        private int quantity;

        public ItemPreview() {}

        public ItemPreview(String productName, String variantName, int quantity) {
            this.productName = productName;
            this.variantName = variantName;
            this.quantity = quantity;
        }

        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public int getQuantity() { return quantity; }
    }
}
