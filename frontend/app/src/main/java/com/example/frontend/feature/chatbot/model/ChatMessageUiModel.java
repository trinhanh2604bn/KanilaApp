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
    private final List<ChatProductUiModel> upsellProducts;
    private final ChatOrderUiModel order;
    private final ChatTicketUiModel ticket;
    private final String replyType;
    private final boolean customerContextUsed;
    private final ChatPreferenceQuestionUiModel preferenceQuestion;
    private final ChatCartSummaryUiModel cartSummary;
    private final ChatCartActionUiModel cartAction;
    private final ComparisonUiModel comparison;
    private final IngredientUiModel ingredientData;
    private final boolean handoffRequired;
    private final String supportPhone;
    private final String supportZalo;
    private final int profileCompletionRate;
    private final List<String> profileMissingFields;

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp) {
        this(id, content, isUser, timestamp, false, new ArrayList<>(), null, null, null, false, null, null, null, null, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping) {
        this(id, content, isUser, timestamp, isTyping, new ArrayList<>(), null, null, null, false, null, null, null, null, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                              ChatTicketUiModel ticket, String replyType) {
        this(id, content, isUser, timestamp, isTyping, products, order, ticket, replyType, false, null, null, null, null, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType, boolean customerContextUsed,
                              ChatPreferenceQuestionUiModel preferenceQuestion) {
        this(id, content, isUser, timestamp, isTyping, products, order, ticket, replyType, customerContextUsed, preferenceQuestion, null, null, null, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType, boolean customerContextUsed,
                             ChatPreferenceQuestionUiModel preferenceQuestion,
                             ChatCartSummaryUiModel cartSummary,
                              ChatCartActionUiModel cartAction) {
        this(id, content, isUser, timestamp, isTyping, products, order, ticket, replyType, customerContextUsed, preferenceQuestion, cartSummary, cartAction, null, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType, boolean customerContextUsed,
                             ChatPreferenceQuestionUiModel preferenceQuestion,
                             ChatCartSummaryUiModel cartSummary,
                             ChatCartActionUiModel cartAction,
                              ComparisonUiModel comparison) {
        this(id, content, isUser, timestamp, isTyping, products, order, ticket, replyType, customerContextUsed, preferenceQuestion, cartSummary, cartAction, comparison, null, null, false, null, null, 0, null);
    }

    public ChatMessageUiModel(String id, String content, boolean isUser, long timestamp, boolean isTyping, 
                             List<ChatProductUiModel> products, ChatOrderUiModel order, 
                             ChatTicketUiModel ticket, String replyType, boolean customerContextUsed,
                             ChatPreferenceQuestionUiModel preferenceQuestion,
                             ChatCartSummaryUiModel cartSummary,
                             ChatCartActionUiModel cartAction,
                              ComparisonUiModel comparison,
                             IngredientUiModel ingredientData,
                             List<ChatProductUiModel> upsellProducts,
                             boolean handoffRequired,
                             String supportPhone,
                             String supportZalo,
                             int profileCompletionRate,
                             List<String> profileMissingFields) {
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
        this.cartSummary = cartSummary;
        this.cartAction = cartAction;
        this.comparison = comparison;
        this.ingredientData = ingredientData;
        this.upsellProducts = upsellProducts != null ? upsellProducts : new ArrayList<>();
        this.handoffRequired = handoffRequired;
        this.supportPhone = supportPhone;
        this.supportZalo = supportZalo;
        this.profileCompletionRate = profileCompletionRate;
        this.profileMissingFields = profileMissingFields != null ? profileMissingFields : new ArrayList<>();
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

    public List<ChatProductUiModel> getUpsellProducts() {
        return upsellProducts;
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

    public ChatCartSummaryUiModel getCartSummary() {
        return cartSummary;
    }

    public ChatCartActionUiModel getCartAction() {
        return cartAction;
    }

    public ComparisonUiModel getComparison() {
        return comparison;
    }

    public IngredientUiModel getIngredientData() {
        return ingredientData;
    }

    public boolean isHandoffRequired() {
        return handoffRequired;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public String getSupportZalo() {
        return supportZalo;
    }

    public int getProfileCompletionRate() {
        return profileCompletionRate;
    }

    public List<String> getProfileMissingFields() {
        return profileMissingFields;
    }

    public static ChatMessageUiModel createTypingIndicator() {
        return new ChatMessageUiModel("typing", "", false, System.currentTimeMillis(), true);
    }
}
