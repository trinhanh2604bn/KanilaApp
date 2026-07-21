package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatbotErrorResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("details")
    private String details;

    @SerializedName("error_type")
    private String errorType;

    @SerializedName("recovery_actions")
    private java.util.List<String> recoveryActions;

    public String getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public String getErrorType() {
        return errorType;
    }

    public java.util.List<String> getRecoveryActions() {
        return recoveryActions;
    }
}
