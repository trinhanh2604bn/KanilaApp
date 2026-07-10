package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;

public class MockOrderResponse {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("total_amount")
    private double totalAmount;

    public String getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
