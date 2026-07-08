package com.example.frontend.feature.chatbot.model;

public class ChatMessageUiModel {
    private final String id;
    private final String content;
    private final boolean isUser;
    private final long timestamp;
    private final boolean isTyping;

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp) {
        this(id, content, isUser, timestamp, false);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping) {
        this.id = id;
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.isTyping = isTyping;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public static ChatMessageUiModel createTypingIndicator() {
        return new ChatMessageUiModel("typing", "", false, System.currentTimeMillis(), true);
    }
}
