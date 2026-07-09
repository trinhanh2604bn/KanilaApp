package com.example.frontend.feature.chatbot.model;

import java.util.List;

public class ChatOrderUiModel {
    private final String orderId;
    private final String orderCode;
    private final String status;
    private final String statusLabel;
    private final String paymentStatus;
    private final String paymentStatusLabel;
    private final long totalAmount;
    private final String createdAt;
    private final String estimatedDelivery;
    private final int itemsCount;
    private final List<ChatOrderTimelineUiModel> timeline;
    private final String nextAction;

    public ChatOrderUiModel(String orderId, String orderCode, String status, String statusLabel, 
                           String paymentStatus, String paymentStatusLabel, long totalAmount, 
                           String createdAt, String estimatedDelivery, int itemsCount, 
                           List<ChatOrderTimelineUiModel> timeline, String nextAction) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.status = status;
        this.statusLabel = statusLabel;
        this.paymentStatus = paymentStatus;
        this.paymentStatusLabel = paymentStatusLabel;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.estimatedDelivery = estimatedDelivery;
        this.itemsCount = itemsCount;
        this.timeline = timeline;
        this.nextAction = nextAction;
    }

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

    public List<ChatOrderTimelineUiModel> getTimeline() {
        return timeline;
    }

    public String getNextAction() {
        return nextAction;
    }
}
