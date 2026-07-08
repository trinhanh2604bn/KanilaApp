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
    private final ChatOrderUiModel order;
    private final ChatTicketUiModel ticket;
    private final String replyType;
    private final boolean customerContextUsed;
    private final ChatPreferenceQuestionUiModel preferenceQuestion;

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp) {
        this(id, content, isUser, timestamp, false, new ArrayList<>(), null, null, null, false, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping) {
        this(id, content, isUser, timestamp, isTyping, new ArrayList<>(), null, null, null, false, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType) {
        this(id, content, isUser, timestamp, isTyping, products, order, ticket, replyType, false, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType, boolean customerContextUsed,
                             ChatPreferenceQuestionUiModel preferenceQuestion) {
        this.id = id;
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.isTyping = isTyping;
        this.products = products != null ? products : new ArrayList<>();
        this.order = order;
        this.ticket = ticket;
        this.replyType = replyType;
        this.customerContextUsed = customerContextUsed;
        this.preferenceQuestion = preferenceQuestion;
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

    public ChatOrderUiModel getOrder() {
        return order;
    }

    public ChatTicketUiModel getTicket() {
        return ticket;
    }

    public String getReplyType() {
        return replyType;
    }

    public boolean isCustomerContextUsed() {
        return customerContextUsed;
    }

    public ChatPreferenceQuestionUiModel getPreferenceQuestion() {
        return preferenceQuestion;
    }

    public static ChatMessageUiModel createTypingIndicator() {
        return new ChatMessageUiModel("typing", "", false, System.currentTimeMillis(), true);
    }
}
