package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatCartActionResponse {
    @SerializedName("action")
    private String action;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("requires_confirmation")
    private Boolean requiresConfirmation;

    @SerializedName("reason")
    private String reason;

    @SerializedName("cart_count")
    private Integer cartCount;

    public String getAction() {
        return action;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getRequiresConfirmation() {
        return requiresConfirmation;
    }

    public String getReason() {
        return reason;
    }

    public Integer getCartCount() {
        return cartCount;
    }
}
