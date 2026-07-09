package com.example.frontend.feature.chatbot.model;

public class ChatCartActionUiModel {
    private final String action;
    private final boolean success;
    private final boolean requiresConfirmation;
    private final String reason;
    private final Integer cartCount;

    public ChatCartActionUiModel(String action, boolean success, boolean requiresConfirmation, String reason, Integer cartCount) {
        this.action = action;
        this.success = success;
        this.requiresConfirmation = requiresConfirmation;
        this.reason = reason;
        this.cartCount = cartCount;
    }

    public String getAction() {
        return action;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isRequiresConfirmation() {
        return requiresConfirmation;
    }

    public String getReason() {
        return reason;
    }

    public Integer getCartCount() {
        return cartCount;
    }
}
