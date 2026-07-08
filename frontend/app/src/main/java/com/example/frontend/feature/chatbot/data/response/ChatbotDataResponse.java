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

    @SerializedName("products")
    private List<ChatProductResponse> products;

    @SerializedName("order")
    private ChatOrderResponse order;

    @SerializedName("ticket")
    private ChatTicketResponse ticket;

    @SerializedName("quick_replies")
    private List<String> quickReplies;

    @SerializedName("customer_context_used")
    private Boolean customerContextUsed;

    @SerializedName("preference_question")
    private ChatPreferenceQuestionResponse preferenceQuestion;

    @SerializedName("cart_summary")
    private ChatCartSummaryResponse cartSummary;

    @SerializedName("cart_action")
    private ChatCartActionResponse cartAction;

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

    public List<ChatProductResponse> getProducts() {
        return products;
    }

    public ChatOrderResponse getOrder() {
        return order;
    }

    public ChatTicketResponse getTicket() {
        return ticket;
    }

    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public Boolean getCustomerContextUsed() {
        return customerContextUsed;
    }

    public ChatPreferenceQuestionResponse getPreferenceQuestion() {
        return preferenceQuestion;
    }

    public ChatCartSummaryResponse getCartSummary() {
        return cartSummary;
    }

    public ChatCartActionResponse getCartAction() {
        return cartAction;
    }

    public boolean isHandoffRequired() {
        return handoffRequired;
    }
}
