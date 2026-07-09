package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatbotMessageResponse {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ChatbotDataResponse data;

    @SerializedName("error")
    private ChatbotErrorResponse error;

    // Support flat response structure
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("conversationId")
    private String conversationId;

    @SerializedName("bot_message")
    private String botMessage;

    @SerializedName("reply_type")
    private String replyType;

    @SerializedName("intent")
    private String intent;

    @SerializedName("products")
    private List<ChatProductResponse> products;

    @SerializedName("upsell_products")
    private List<ChatProductResponse> upsellProducts;

    @SerializedName("needs_variant_selection")
    private Boolean needsVariantSelection;

    @SerializedName("quick_replies")
    private List<Object> quickReplies;

    @SerializedName("order")
    private ChatOrderResponse order;

    @SerializedName("ticket")
    private ChatTicketResponse ticket;

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
    private Boolean handoffRequired;

    @SerializedName("filters")
    private Object filters;

    @SerializedName("support_actions")
    private List<Object> supportActions;

    public boolean isSuccess() {
        // Requirement 2: isSuccess() must return true when valid chatbot content exists.
        // Support both Format A (success: true) and Format B (flat)
        
        // Check for any valid chatbot content
        boolean hasValidContent = (data != null) || 
                                (botMessage != null && !botMessage.isEmpty()) || 
                                (message != null && !message.isEmpty()) ||
                                (replyType != null && !replyType.isEmpty()) ||
                                (intent != null && !intent.isEmpty()) ||
                                (products != null && !products.isEmpty()) ||
                                (upsellProducts != null && !upsellProducts.isEmpty()) ||
                                (quickReplies != null && !quickReplies.isEmpty()) ||
                                (order != null) || (ticket != null) || (cartSummary != null) ||
                                (comparison != null) || (ingredientContext != null);

        // Log for debugging
        android.util.Log.d("CHATBOT_DEBUG", "isSuccess check - hasValidContent: " + hasValidContent + ", error: " + (error != null) + ", success field: " + success);

        // If we have content and no error object, it's a success
        if (hasValidContent && error == null) {
            return true;
        }

        // Fallback to success flag if present
        if (success != null) {
            return success;
        }

        return false;
    }

    public String getMessage() {
        return message;
    }

    public ChatbotDataResponse getData() {
        // Requirement 3: getData() must create ChatbotDataResponse from flat response when needed.
        String effectiveBotMessage = botMessage != null ? botMessage : message;
        String effectiveReplyType = replyType != null ? replyType : intent;
        String effectiveSessionId = sessionId != null ? sessionId : conversationId;

        if (data == null && (effectiveBotMessage != null || effectiveReplyType != null || products != null || quickReplies != null || order != null || ticket != null || cartSummary != null || comparison != null || ingredientContext != null || upsellProducts != null)) {
            // Create a synthetic data response from flat fields
            ChatbotDataResponse syntheticData = ChatbotDataResponse.createFromFlat(
                effectiveBotMessage, effectiveReplyType, products, convertQuickReplies(quickReplies)
            );
            
            // Set other fields
            syntheticData.setSessionId(effectiveSessionId);
            syntheticData.setOrder(order);
            syntheticData.setTicket(ticket);
            syntheticData.setCustomerContextUsed(customerContextUsed);
            syntheticData.setPreferenceQuestion(preferenceQuestion);
            syntheticData.setCartSummary(cartSummary);
            syntheticData.setCartAction(cartAction);
            syntheticData.setComparison(comparison);
            syntheticData.setIngredientContext(ingredientContext);
            if (handoffRequired != null) syntheticData.setHandoffRequired(handoffRequired);
            syntheticData.setFilters(filters);
            syntheticData.setSupportActions(supportActions);
            syntheticData.setUpsellProducts(upsellProducts);
            syntheticData.setNeedsVariantSelection(needsVariantSelection);
            
            android.util.Log.d("CHATBOT_DEBUG", "getData: Created comprehensive synthetic data from flat fields");
            return syntheticData;
        }
        return data;
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

    public ChatbotErrorResponse getError() {
        return error;
    }
}
