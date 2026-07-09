package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatbotDataResponse {
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("conversationId")
    private String conversationId;

    @SerializedName("reply_type")
    private String replyType;

    @SerializedName("intent")
    private String intent;

    @SerializedName("bot_message")
    private String botMessage;

    @SerializedName("message")
    private String message;

    @SerializedName("products")
    private List<ChatProductResponse> products;

    @SerializedName("order")
    private ChatOrderResponse order;

    @SerializedName("ticket")
    private ChatTicketResponse ticket;

    @SerializedName("quick_replies")
    private List<Object> quickReplies;

    @SerializedName("quickReplies")
    private List<Object> quickRepliesList;

    @SerializedName("customer_context_used")
    private Boolean customerContextUsed;

    @SerializedName("preference_question")
    private ChatPreferenceQuestionResponse preferenceQuestion;

    @SerializedName("cart_summary")
    private ChatCartSummaryResponse cartSummary;

    @SerializedName("cart_action")
    private ChatCartActionResponse cartAction;

    @SerializedName("comparison")
    private ChatComparisonResponse comparison;

    @SerializedName("ingredient_context")
    private ChatIngredientResponse ingredientContext;

    @SerializedName("handoff_required")
    private boolean handoffRequired;

    @SerializedName("upsell_products")
    private List<ChatProductResponse> upsellProducts;

    @SerializedName("needs_variant_selection")
    private Boolean needsVariantSelection;

    @SerializedName("filters")
    private Object filters;

    @SerializedName("support_actions")
    private List<Object> supportActions;

    public String getSessionId() {
        return sessionId != null ? sessionId : conversationId;
    }

    public String getReplyType() {
        return replyType != null ? replyType : intent;
    }

    public String getBotMessage() {
        return botMessage != null ? botMessage : message;
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
        List<Object> raw = quickReplies != null ? quickReplies : quickRepliesList;
        return convertQuickReplies(raw);
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

    public ChatComparisonResponse getComparison() {
        return comparison;
    }

    public ChatIngredientResponse getIngredientContext() {
        return ingredientContext;
    }

    public boolean isHandoffRequired() {
        return handoffRequired;
    }

    public List<ChatProductResponse> getUpsellProducts() {
        return upsellProducts;
    }

    public Boolean getNeedsVariantSelection() {
        return needsVariantSelection;
    }

    public Object getFilters() {
        return filters;
    }

    public List<Object> getSupportActions() {
        return supportActions;
    }

    private List<String> convertQuickReplies(List<Object> rawReplies) {
        if (rawReplies == null) return null;
        List<String> result = new java.util.ArrayList<>();
        for (Object item : rawReplies) {
            if (item instanceof String) {
                result.add((String) item);
            } else if (item instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) item;
                Object text = map.get("text");
                if (text instanceof String) {
                    result.add((String) text);
                }
            }
        }
        return result;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setOrder(ChatOrderResponse order) {
        this.order = order;
    }

    public void setTicket(ChatTicketResponse ticket) {
        this.ticket = ticket;
    }

    public void setCustomerContextUsed(Boolean customerContextUsed) {
        this.customerContextUsed = customerContextUsed;
    }

    public void setPreferenceQuestion(ChatPreferenceQuestionResponse preferenceQuestion) {
        this.preferenceQuestion = preferenceQuestion;
    }

    public void setCartSummary(ChatCartSummaryResponse cartSummary) {
        this.cartSummary = cartSummary;
    }

    public void setCartAction(ChatCartActionResponse cartAction) {
        this.cartAction = cartAction;
    }

    public void setComparison(ChatComparisonResponse comparison) {
        this.comparison = comparison;
    }

    public void setIngredientContext(ChatIngredientResponse ingredientContext) {
        this.ingredientContext = ingredientContext;
    }

    public void setHandoffRequired(boolean handoffRequired) {
        this.handoffRequired = handoffRequired;
    }

    public void setUpsellProducts(List<ChatProductResponse> upsellProducts) {
        this.upsellProducts = upsellProducts;
    }

    public void setNeedsVariantSelection(Boolean needsVariantSelection) {
        this.needsVariantSelection = needsVariantSelection;
    }

    public void setFilters(Object filters) {
        this.filters = filters;
    }

    public void setSupportActions(List<Object> supportActions) {
        this.supportActions = supportActions;
    }

    public static ChatbotDataResponse createFromFlat(String botMessage, String replyType, List<ChatProductResponse> products, List<String> quickRepliesStrings) {
        ChatbotDataResponse data = new ChatbotDataResponse();
        data.botMessage = botMessage;
        data.replyType = replyType;
        data.products = products;
        if (quickRepliesStrings != null) {
            data.quickReplies = new java.util.ArrayList<>(quickRepliesStrings);
        }
        return data;
    }
}
