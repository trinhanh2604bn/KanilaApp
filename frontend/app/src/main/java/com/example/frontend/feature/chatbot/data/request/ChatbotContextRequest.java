package com.example.frontend.feature.chatbot.data.request;

import com.google.gson.annotations.SerializedName;

public class ChatbotContextRequest {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("order_id")
    private String orderId;

    public ChatbotContextRequest(String productId, String orderId) {
        this.productId = productId;
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public String getOrderId() {
        return orderId;
    }
}
