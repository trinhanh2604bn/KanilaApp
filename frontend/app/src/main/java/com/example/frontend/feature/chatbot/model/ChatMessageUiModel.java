package com.example.frontend.feature.chatbot.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageUiModel {
    private final String id;
    private final String content;
    private final boolean isUser;
    private final long timestamp;
    private final boolean isTyping;
    private final List<ChatProductUiModel> products;
    private final String replyType;

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp) {
        this(id, content, isUser, timestamp, false, new ArrayList<>(), null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping) {
        this(id, content, isUser, timestamp, isTyping, new ArrayList<>(), null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, List<ChatProductUiModel> products, String replyType) {
        this.id = id;
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.isTyping = isTyping;
        this.products = products != null ? products : new ArrayList<>();
        this.replyType = replyType;
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

    public List<ChatProductUiModel> getProducts() {
        return products;
    }

    public String getReplyType() {
        return replyType;
    }

    public static ChatMessageUiModel createTypingIndicator() {
        return new ChatMessageUiModel("typing", "", false, System.currentTimeMillis(), true);
    }
}
