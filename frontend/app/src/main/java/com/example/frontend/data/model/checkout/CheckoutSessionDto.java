package com.example.frontend.data.model.checkout;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CheckoutSessionDto {
    @SerializedName("_id")
    private String id;
    
    public void setId(String id) { this.id = id; }
    public void setSubtotalAmount(Double subtotalAmount) { this.subtotalAmount = subtotalAmount; }
    public void setShippingAmount(Double shippingAmount) { this.shippingAmount = shippingAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public void setPointsAmount(Double pointsAmount) { this.pointsAmount = pointsAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public void setShippingAddress(CheckoutAddressDto shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setItems(List<CheckoutItemDto> items) { this.items = items; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("cart_id")
    private String cartId;

    @SerializedName("session_status")
    private String sessionStatus;

    @SerializedName("coupon_code")
    private String couponCode;

    @SerializedName("subtotal_amount")
    private Double subtotalAmount;

    @SerializedName("shipping_amount")
    private Double shippingAmount;

    @SerializedName("discount_amount")
    private Double discountAmount;

    @SerializedName("points_amount")
    private Double pointsAmount;

    @SerializedName("total_amount")
    private Double totalAmount;

    @SerializedName("shipping_address")
    private CheckoutAddressDto shippingAddress;

    @SerializedName("shipping_method")
    private String shippingMethod;

    @SerializedName("estimated_delivery")
    private String estimatedDelivery;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("items")
    private List<CheckoutItemDto> items;

    public String getId() { return id; }
    public Double getSubtotalAmount() { return subtotalAmount; }
    public Double getShippingAmount() { return shippingAmount; }
    public Double getDiscountAmount() { return discountAmount; }
    public Double getPointsAmount() { return pointsAmount; }
    public Double getTotalAmount() { return totalAmount; }

    public double getSubtotalAmountValue() { return subtotalAmount != null ? subtotalAmount : 0.0; }
    public double getShippingAmountValue() { return shippingAmount != null ? shippingAmount : 0.0; }
    public double getDiscountAmountValue() { return discountAmount != null ? discountAmount : 0.0; }
    public double getPointsAmountValue() { return pointsAmount != null ? pointsAmount : 0.0; }
    public double getTotalAmountValue() { return totalAmount != null ? totalAmount : 0.0; }
    public CheckoutAddressDto getShippingAddress() { return shippingAddress; }
    public String getShippingMethod() { return shippingMethod; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<CheckoutItemDto> getItems() { return items; }
    public String getCouponCode() { return couponCode; }

    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public static class CheckoutItemDto {
        @SerializedName("id")
        private String id;
        @SerializedName("product_id")
        private String productId;
        @SerializedName("variant_id")
        private String variantId;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("variant_name")
        private String variantName;
        @SerializedName("brand_name")
        private String brandName;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("price")
        private double price;
        @SerializedName("image_url")
        private String imageUrl;
        @SerializedName("stock_status")
        private String stockStatus;

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getVariantId() { return variantId; }
        public String getProductName() { return productName; }
        public String getVariantName() { return variantName; }
        public String getBrandName() { return brandName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }
        public String getStockStatus() { return stockStatus; }

        public void setId(String id) { this.id = id; }
        public void setProductId(String productId) { this.productId = productId; }
        public void setVariantId(String variantId) { this.variantId = variantId; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setVariantName(String variantName) { this.variantName = variantName; }
        public void setBrandName(String brandName) { this.brandName = brandName; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPrice(double price) { this.price = price; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
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

        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    }
}
