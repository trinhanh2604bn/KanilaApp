package com.example.frontend.feature.chatbot.model;

public class ChatTicketUiModel {
    private final String ticketId;
    private final String ticketCode;
    private final String status;
    private final String statusLabel;
    private final String category;
    private final String categoryLabel;
    private final String createdAt;
    private final String message;

    public ChatTicketUiModel(String ticketId, String ticketCode, String status, String statusLabel, 
                            String category, String categoryLabel, String createdAt, String message) {
        this.ticketId = ticketId;
        this.ticketCode = ticketCode;
        this.status = status;
        this.statusLabel = statusLabel;
        this.category = category;
        this.categoryLabel = categoryLabel;
        this.createdAt = createdAt;
        this.message = message;
    }

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
