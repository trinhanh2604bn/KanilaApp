package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatTicketResponse {
    @SerializedName("ticket_id")
    private String ticketId;

    @SerializedName("ticket_code")
    private String ticketCode;

    @SerializedName("status")
    private String status;

    @SerializedName("status_label")
    private String statusLabel;

    @SerializedName("category")
    private String category;

    @SerializedName("category_label")
    private String categoryLabel;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("message")
    private String message;

    public String getTicketId() {
        return ticketId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }
}
