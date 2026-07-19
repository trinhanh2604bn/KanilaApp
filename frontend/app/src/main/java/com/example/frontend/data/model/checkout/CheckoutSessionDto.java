package com.example.frontend.data.model.checkout;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CheckoutSessionDto implements Serializable {
    @SerializedName(value = "_id", alternate = {"sessionId"})
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

    @SerializedName(value = "customer_id", alternate = {"customerId"})
    private String customerId;

    @SerializedName(value = "cart_id", alternate = {"cartId"})
    private String cartId;

    @SerializedName(value = "session_status", alternate = {"checkoutStatus"})
    private String sessionStatus;

    @SerializedName(value = "coupon_code", alternate = {"appliedCouponCode"})
    private String couponCode;

    @SerializedName(value = "subtotal_amount", alternate = {"subtotal"})
    private Double subtotalAmount;

    @SerializedName(value = "shipping_amount", alternate = {"shippingFee"})
    private Double shippingAmount;

    @SerializedName(value = "discount_amount", alternate = {"discount"})
    private Double discountAmount;

    @SerializedName("points_amount")
    private Double pointsAmount;

    @SerializedName(value = "total_amount", alternate = {"total"})
    private Double totalAmount;

    @SerializedName(value = "shipping_address", alternate = {"shippingAddress"})
    private CheckoutAddressDto shippingAddress;

    @SerializedName("shipping_method")
    private String shippingMethod;

    @SerializedName("estimated_delivery")
    private String estimatedDelivery;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName(value = "items", alternate = {"selectedItems"})
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

    public static class CheckoutItemDto implements Serializable {
        @SerializedName("_id")
        private String id;

        @SerializedName(value = "product_id", alternate = {"productId"})
        private String productId;

        @SerializedName(value = "variant_id", alternate = {"variantId"})
        private String variantId;

        @SerializedName(value = "product_name", alternate = {"productName", "product_name_snapshot"})
        private String productName;

        @SerializedName(value = "variant_name", alternate = {"variantLabel", "variant_name_snapshot"})
        private String variantName;

        @SerializedName(value = "brand_name", alternate = {"brandName", "brand_name_snapshot"})
        private String brandName;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName(value = "price", alternate = {"finalUnitPrice", "final_unit_price_amount", "unitPrice"})
        private double price;

        @SerializedName(value = "image_url", alternate = {"imageUrl", "image_url_snapshot"})
        private String imageUrl;

        @SerializedName(value = "stock_status", alternate = {"stockStatus"})
        private String stockStatus;

        @SerializedName("selected")
        private boolean selected = true;

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
        public boolean isSelected() { return selected; }

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
        public void setSelected(boolean selected) { this.selected = selected; }
    }

    public static class CheckoutAddressDto implements Serializable {
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
