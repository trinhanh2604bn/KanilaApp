package com.example.frontend.feature.chatbot.data.request;

import com.google.gson.annotations.SerializedName;

public class ChatbotMessageRequest {
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("message")
    private String message;

    @SerializedName("source_screen")
    private String sourceScreen;

    @SerializedName("context")
    private ChatbotContextRequest context;

    public ChatbotMessageRequest(String sessionId, String message, String sourceScreen, ChatbotContextRequest context) {
        this.sessionId = sessionId;
        this.message = message;
        this.sourceScreen = sourceScreen;
        this.context = context;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceScreen() {
        return sourceScreen;
    }

    public ChatbotContextRequest getContext() {
        return context;
    }
}
