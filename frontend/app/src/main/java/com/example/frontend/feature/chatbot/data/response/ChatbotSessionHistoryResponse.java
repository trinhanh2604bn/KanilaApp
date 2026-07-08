package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatbotSessionHistoryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private HistoryData data;

    public boolean isSuccess() {
        return success;
    }

    public HistoryData getData() {
        return data;
    }

    public static class HistoryData {
        @SerializedName("messages")
        private List<ChatbotMessageDto> messages;

        public List<ChatbotMessageDto> getMessages() {
            return messages;
        }
    }

    public static class ChatbotMessageDto {
        @SerializedName("sender_type")
        private String senderType;

        @SerializedName("message_text")
        private String messageText;

        @SerializedName("created_at")
        private String createdAt;

        public String getSenderType() {
            return senderType;
        }

        public String getMessageText() {
            return messageText;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
