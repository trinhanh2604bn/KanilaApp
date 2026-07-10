package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDto {
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
    
    @SerializedName("items")
    private List<OrderItemDetailDto> items;
    
    @SerializedName("order_addresses")
    private List<OrderAddressDto> addresses;
    
    @SerializedName("order_total")
    private OrderTotalDto total;
    
    @SerializedName("shipment")
    private ShipmentDto shipment;

    public OrderDetailDto() {}

    // Constructor for mocking
    public OrderDetailDto(String id, String orderNumber, String orderStatus, String paymentStatus, String placedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.placedAt = placedAt;
        this.items = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    public void addItem(String name, String variant, int qty, double price) {
        if (items == null) items = new ArrayList<>();
        items.add(new OrderItemDetailDto(name, variant, qty, price));
    }

    public void setShippingAddress(String name, String phone, String line1, String ward, String district) {
        if (addresses == null) addresses = new ArrayList<>();
        addresses.add(new OrderAddressDto("shipping", name, phone, line1, ward, district, "HCM"));
    }

    public void setTotal(double grandTotal) {
        this.total = new OrderTotalDto(grandTotal);
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public String getPlacedAt() { return placedAt; }
    public List<OrderItemDetailDto> getItems() { return items; }
    public List<OrderAddressDto> getAddresses() { return addresses; }
    public OrderTotalDto getTotal() { return total; }
    public ShipmentDto getShipment() { return shipment; }

    public static class OrderItemDetailDto {
        @SerializedName("product_name_snapshot")
        private String productName;
        @SerializedName("variant_name_snapshot")
        private String variantName;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("unit_final_price_amount")
        private double unitPrice;
        @SerializedName("line_total_amount")
        private double lineTotal;

        public OrderItemDetailDto(String productName, String variantName, int quantity, double unitPrice) {
            this.productName = productName;
            this.variantName = variantName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = unitPrice * quantity;
        }

        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return lineTotal; }
    }

    public static class OrderAddressDto {
        @SerializedName("address_type")
        private String addressType;
        @SerializedName("recipient_name")
        private String recipientName;
        @SerializedName("phone")
        private String phone;
        @SerializedName("address_line_1")
        private String addressLine1;
        @SerializedName("ward")
        private String ward;
        @SerializedName("district")
        private String district;
        @SerializedName("city")
        private String city;

        public OrderAddressDto(String type, String name, String phone, String line1, String ward, String district, String city) {
            this.addressType = type;
            this.recipientName = name;
            this.phone = phone;
            this.addressLine1 = line1;
            this.ward = ward;
            this.district = district;
            this.city = city;
        }

        public String getAddressType() { return addressType; }
        public String getRecipientName() { return recipientName; }
        public String getPhone() { return phone; }
        public String getAddressLine1() { return addressLine1; }
        public String getWard() { return ward; }
        public String getDistrict() { return district; }
        public String getCity() { return city; }
    }

    public static class OrderTotalDto {
        @SerializedName("grand_total_amount")
        private double grandTotal;
        @SerializedName("subtotal_amount")
        private double subtotal;
        @SerializedName("shipping_fee_amount")
        private double shippingFee;

        public OrderTotalDto(double grandTotal) {
            this.grandTotal = grandTotal;
            this.subtotal = grandTotal - 30000;
            this.shippingFee = 30000;
        }

        public double getGrandTotal() { return grandTotal; }
        public double getSubtotal() { return subtotal; }
        public double getShippingFee() { return shippingFee; }
    }

    public static class ShipmentDto {
        @SerializedName("tracking_number")
        private String trackingNumber;
        @SerializedName("shipmentStatus")
        private String status;
        @SerializedName("carrierCode")
        private String carrierCode;

        public String getTrackingNumber() { return trackingNumber; }
        public String getStatus() { return status; }
        public String getCarrierCode() { return carrierCode; }
    }
}
