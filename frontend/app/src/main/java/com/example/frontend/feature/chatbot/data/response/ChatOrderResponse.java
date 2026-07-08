package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatOrderResponse {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("status")
    private String status;

    @SerializedName("status_label")
    private String statusLabel;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("payment_status_label")
    private String paymentStatusLabel;

    @SerializedName("total_amount")
    private long totalAmount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("estimated_delivery")
    private String estimatedDelivery;

    @SerializedName("items_count")
    private int itemsCount;

    @SerializedName("timeline")
    private List<ChatOrderTimelineResponse> timeline;

    @SerializedName("next_action")
    private String nextAction;

    public String getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentStatusLabel() {
        return paymentStatusLabel;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public List<ChatOrderTimelineResponse> getTimeline() {
        return timeline;
    }

    public String getNextAction() {
        return nextAction;
    }
}
