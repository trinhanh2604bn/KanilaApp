package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatbotMessageResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ChatbotDataResponse data;

    @SerializedName("error")
    private ChatbotErrorResponse error;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ChatbotDataResponse getData() {
        return data;
    }

    public ChatbotErrorResponse getError() {
        return error;
    }
}
