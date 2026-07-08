package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatbotDataResponse {
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("reply_type")
    private String replyType;

    @SerializedName("bot_message")
    private String botMessage;

    @SerializedName("quick_replies")
    private List<String> quickReplies;

    @SerializedName("handoff_required")
    private boolean handoffRequired;

    public String getSessionId() {
        return sessionId;
    }

    public String getReplyType() {
        return replyType;
    }

    public String getBotMessage() {
        return botMessage;
    }

    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public boolean isHandoffRequired() {
        return handoffRequired;
    }
}
