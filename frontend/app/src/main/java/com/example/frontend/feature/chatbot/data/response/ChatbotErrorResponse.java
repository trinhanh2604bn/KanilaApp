package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatbotErrorResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("details")
    private String details;

    public String getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }
}
