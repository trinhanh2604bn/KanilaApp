package com.example.frontend.feature.chatbot;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.chatbot.data.ChatbotRepository;
import com.example.frontend.feature.chatbot.data.request.ChatbotContextRequest;
import com.example.frontend.feature.chatbot.data.request.ChatbotMessageRequest;
import com.example.frontend.feature.chatbot.data.response.ChatCartActionResponse;
import com.example.frontend.feature.chatbot.data.response.ChatCartSummaryResponse;
import com.example.frontend.feature.chatbot.data.response.ChatComparisonResponse;
import com.example.frontend.feature.chatbot.data.response.ChatIngredientResponse;
import com.example.frontend.feature.chatbot.data.response.ChatOrderResponse;
import com.example.frontend.feature.chatbot.data.response.ChatOrderTimelineResponse;
import com.example.frontend.feature.chatbot.data.response.ChatProductResponse;
import com.example.frontend.feature.chatbot.data.response.ChatPreferenceQuestionResponse;
import com.example.frontend.feature.chatbot.data.response.ChatTicketResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotDataResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotMessageResponse;


import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.feature.chatbot.data.response.ChatbotSessionHistoryResponse;
import com.example.frontend.feature.chatbot.model.ChatCartActionUiModel;
import com.example.frontend.feature.chatbot.model.ChatCartSummaryUiModel;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderTimelineUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatPreferenceQuestionUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;
import com.example.frontend.feature.chatbot.model.ComparisonUiModel;
import com.example.frontend.feature.chatbot.model.IngredientUiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatbotViewModel extends AndroidViewModel {

    private final ChatbotRepository repository;
    private final com.example.frontend.data.repository.CartRepository cartRepository;
    private final MutableLiveData<ChatbotUiState> uiState = new MutableLiveData<>(ChatbotUiState.empty());
    private final List<ChatMessageUiModel> messageList = new ArrayList<>();
    private final List<String> currentQuickReplies = new ArrayList<>();
    private final MutableLiveData<NetworkResult<ChatbotMessageResponse>> lastResponse = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<ChatbotSessionHistoryResponse>> historyResponse = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<com.example.frontend.data.model.cart.CartDto>> addToCartResult = new MutableLiveData<>();
    private final TokenManager tokenManager;
    private String sessionId;

    public ChatbotViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ChatbotRepository(application);
        this.cartRepository = new com.example.frontend.data.repository.CartRepository(application);
        this.tokenManager = TokenManager.getInstance(application);
        this.sessionId = tokenManager.getChatbotSession();
        
        if (this.sessionId != null) {
            loadHistory();
        }
    }

    public LiveData<NetworkResult<com.example.frontend.data.model.cart.CartDto>> getAddToCartResult() {
        return addToCartResult;
    }

    public void addToCart(String productId, String variantId) {
        AddToCartRequest request = new AddToCartRequest(productId, variantId, 1);
        cartRepository.addToCart(request, addToCartResult);
    }

    private void loadHistory() {
        repository.getHistory(sessionId, historyResponse);
    }

    public LiveData<NetworkResult<ChatbotSessionHistoryResponse>> getHistoryResponse() {
        return historyResponse;
    }

    public void handleHistoryResponse(NetworkResult<ChatbotSessionHistoryResponse> result) {
        if (result == null || result.status != NetworkResult.Status.SUCCESS || result.data == null) return;

        List<ChatbotSessionHistoryResponse.ChatbotMessageDto> history = result.data.getData().getMessages();
        if (history != null) {
            messageList.clear();
            for (ChatbotSessionHistoryResponse.ChatbotMessageDto msg : history) {
                long timestamp = System.currentTimeMillis();
                if (msg.getCreatedAt() != null) {
                    try {
                        // Assuming ISO 8601 format: "yyyy-MM-dd'T'HH:mm:ss.SSSX" or similar
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        java.util.Date date = sdf.parse(msg.getCreatedAt());
                        if (date != null) {
                            timestamp = date.getTime();
                        }
                    } catch (Exception e) {
                        // Fallback to second attempt for simpler format
                        try {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
                            java.util.Date date = sdf.parse(msg.getCreatedAt());
                            if (date != null) {
                                timestamp = date.getTime();
                            }
                        } catch (Exception e2) {
                            // Keep current timestamp
                        }
                    }
                }

                messageList.add(new ChatMessageUiModel(
                        UUID.randomUUID().toString(),
                        msg.getMessageText(),
                        "user".equals(msg.getSenderType()),
                        timestamp
                ));
            }
            updateState(false, null);
        }
        historyResponse.setValue(null);
    }

    public LiveData<ChatbotUiState> getUiState() {
        return uiState;
    }

    public void sendMessage(String content) {
        if (content == null || content.trim().isEmpty()) return;

        // 1. Add user message bubble immediately
        ChatMessageUiModel userMsg = new ChatMessageUiModel(
                UUID.randomUUID().toString(),
                content,
                true,
                System.currentTimeMillis()
        );
        messageList.add(userMsg);
        
        // 2. Show typing indicator
        messageList.add(ChatMessageUiModel.createTypingIndicator());
        updateState(true, null);

        // 3. Call repository
        ChatbotMessageRequest request = new ChatbotMessageRequest(
                sessionId,
                content,
                "chatbot",
                new ChatbotContextRequest(null, null)
        );

        repository.sendMessage(request, lastResponse);
        
        // Observe lastResponse (using a one-time observation or mediator if needed, 
        // but here we can just react to it in the fragment or use transformation)
    }

    public LiveData<NetworkResult<ChatbotMessageResponse>> getLastResponse() {
        return lastResponse;
    }

    public void handleBotResponse(NetworkResult<ChatbotMessageResponse> result) {
        if (result == null || result.status == NetworkResult.Status.LOADING) return;

        // Requirement 1: Log ViewModel state transitions
        android.util.Log.d("CHATBOT_DEBUG", "ViewModel handleBotResponse status: " + result.status);

        // Remove typing indicator
        messageList.removeIf(ChatMessageUiModel::isTyping);

        if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
            ChatbotDataResponse data = result.data.getData();
            if (data != null) {
                // Requirement 1: Detailed logs for data fields
                android.util.Log.d("CHATBOT_DEBUG", "ViewModel Data Received:");
                android.util.Log.d("CHATBOT_DEBUG", " - reply_type: " + data.getReplyType());
                android.util.Log.d("CHATBOT_DEBUG", " - bot_message: " + data.getBotMessage());
                android.util.Log.d("CHATBOT_DEBUG", " - products_count: " + (data.getProducts() != null ? data.getProducts().size() : 0));
                android.util.Log.d("CHATBOT_DEBUG", " - quick_reply_count: " + (data.getQuickReplies() != null ? data.getQuickReplies().size() : 0));

                // Save session id
                this.sessionId = data.getSessionId();
                tokenManager.saveChatbotSession(this.sessionId);

                // Update quick replies
                currentQuickReplies.clear();
                if (data.getQuickReplies() != null && !data.getQuickReplies().isEmpty()) {
                    currentQuickReplies.addAll(data.getQuickReplies());
                }

                // Add bot message
                List<ChatProductUiModel> productUiModels = new ArrayList<>();
                if (data.getProducts() != null) {
                    for (ChatProductResponse p : data.getProducts()) {
                        productUiModels.add(mapToUiModel(p));
                    }
                }

                List<ChatProductUiModel> upsellUiModels = new ArrayList<>();
                if (data.getUpsellProducts() != null) {
                    for (ChatProductResponse p : data.getUpsellProducts()) {
                        upsellUiModels.add(mapToUiModel(p));
                    }
                }

                ChatOrderUiModel orderUiModel = null;
                if (data.getOrder() != null) {
                    orderUiModel = mapToUiModel(data.getOrder());
                }

                ChatTicketUiModel ticketUiModel = null;
                if (data.getTicket() != null) {
                    ticketUiModel = mapToUiModel(data.getTicket());
                }

                ChatPreferenceQuestionUiModel preferenceQuestionUiModel = null;
                if (data.getPreferenceQuestion() != null) {
                    preferenceQuestionUiModel = mapToUiModel(data.getPreferenceQuestion());
                }

                ChatCartSummaryUiModel cartSummaryUiModel = null;
                if (data.getCartSummary() != null) {
                    cartSummaryUiModel = mapToUiModel(data.getCartSummary());
                }

                ChatCartActionUiModel cartActionUiModel = null;
                if (data.getCartAction() != null) {
                    cartActionUiModel = mapToUiModel(data.getCartAction());
                }

                ComparisonUiModel comparisonUiModel = null;
                if (data.getComparison() != null) {
                    comparisonUiModel = mapToUiModel(data.getComparison());
                }

                IngredientUiModel ingredientUiModel = null;
                if (data.getIngredientContext() != null) {
                    ingredientUiModel = mapToUiModel(data.getIngredientContext());
                }

                String botMessage = data.getBotMessage();
                // ONLY use fallback message if it's SPECIFICALLY a product search that returned NOTHING
                if ((botMessage == null || botMessage.isEmpty()) && "product_search".equals(data.getReplyType()) && productUiModels.isEmpty()) {
                    botMessage = getApplication().getString(R.string.chat_no_product_found);
                }

                // Parse product analysis from botMessage if products exist
                if (botMessage != null && !botMessage.isEmpty() && !productUiModels.isEmpty()) {
                    StringBuilder overview = new StringBuilder();
                    java.util.List<StringBuilder> productReasons = new java.util.ArrayList<>();
                    int currentProductIndex = -1;
                    
                    String[] lines = botMessage.split("\n");
                    for (String line : lines) {
                        if (line.trim().matches("^\\d+\\.\\s+.*")) {
                            currentProductIndex++;
                            productReasons.add(new StringBuilder());
                            productReasons.get(currentProductIndex).append(line).append("\n");
                        } else if (currentProductIndex < 0) {
                            overview.append(line).append("\n");
                        } else {
                            if (currentProductIndex < productReasons.size()) {
                                productReasons.get(currentProductIndex).append(line).append("\n");
                            }
                        }
                    }
                    
                    if (!productReasons.isEmpty()) {
                        botMessage = overview.toString().trim();
                        for (int i = 0; i < productReasons.size(); i++) {
                            if (i < productUiModels.size()) {
                                String extractedReason = productReasons.get(i).toString().trim();
                                if (!extractedReason.isEmpty()) {
                                    productUiModels.get(i).setReason(extractedReason);
                                }
                            }
                        }
                    }
                }

                ChatMessageUiModel botMsg = new ChatMessageUiModel(
                        UUID.randomUUID().toString(),
                        botMessage,
                        false,
                        System.currentTimeMillis(),
                        false,
                        productUiModels,
                        orderUiModel,
                        ticketUiModel,
                        data.getReplyType(),
                        data.getCustomerContextUsed() != null && data.getCustomerContextUsed(),
                        preferenceQuestionUiModel,
                        cartSummaryUiModel,
                        cartActionUiModel,
                        comparisonUiModel,
                        ingredientUiModel,
                        upsellUiModels
                );
                messageList.add(botMsg);

                // Handle no products found case if it's a product search intent but no products returned
                if ("product_search".equals(data.getReplyType()) && productUiModels.isEmpty()) {
                    currentQuickReplies.clear();
                    currentQuickReplies.add(getApplication().getString(R.string.chat_suggest_expand_budget));
                    currentQuickReplies.add(getApplication().getString(R.string.chat_suggest_similar_products));
                    currentQuickReplies.add(getApplication().getString(R.string.chat_suggest_best_selling));
                }

                updateState(false, null);
            }
        } else if (result.status == NetworkResult.Status.ERROR) {
            // Add error bubble
            String errorMsg = result.message != null ? result.message : "Mình chưa thể trả lời bạn, hãy thử sau";
            android.util.Log.e("CHATBOT_DEBUG", "ViewModel ERROR state: " + errorMsg);
            addErrorBubble(errorMsg);
        } else if (result.status == NetworkResult.Status.NO_INTERNET) {
            String errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra Wi-Fi/4G và thử lại.";
            android.util.Log.e("CHATBOT_DEBUG", "ViewModel NO_INTERNET state");
            addErrorBubble(errorMsg);
            // Overwrite error state for specific UI handling in fragment
            updateState(false, "no_internet");
        }

        // Reset lastResponse to null so it doesn't trigger again on configuration change
        lastResponse.setValue(null);
    }

    public void confirmAddCombo(String action) {
        // Show typing indicator
        messageList.add(ChatMessageUiModel.createTypingIndicator());
        updateState(true, null);

        // Call repository with the action as message or a specific trigger
        ChatbotMessageRequest request = new ChatbotMessageRequest(
                sessionId,
                action, // Use the action from the response as the message trigger
                "chatbot",
                new ChatbotContextRequest(null, null)
        );

        repository.sendMessage(request, lastResponse);
    }

    public void startNewChat() {
        messageList.clear();
        currentQuickReplies.clear();
        sessionId = null;
        tokenManager.saveChatbotSession(null);
        updateState(false, null);
    }

    private void addErrorBubble(String message) {
        ChatMessageUiModel errorMsg = new ChatMessageUiModel(
                UUID.randomUUID().toString(),
                message,
                false,
                System.currentTimeMillis()
        );
        messageList.add(errorMsg);
        updateState(false, message);
    }

    private ChatProductUiModel mapToUiModel(ChatProductResponse p) {
        // Use full AI analysis (with strengths, bestFor, tip) for the reason dialog
        String reasonText = p.getFullAiAnalysis();
        return new ChatProductUiModel(
                p.getProductId(),
                p.getVariantId(),
                p.getSlug(),
                p.getName(),
                p.getBrandName(),
                p.getCategoryName(),
                p.getPrice() != null ? formatPrice(p.getPrice()) : "Liên hệ",
                p.getFinalPrice() != null ? formatPrice(p.getFinalPrice()) : null,
                p.getCompareAtPrice() != null ? formatPrice(p.getCompareAtPrice()) : null,
                p.getImageUrl(),
                p.getRating() != null ? String.valueOf(p.getRating()) : null,
                p.getReviewCount() != null ? String.valueOf(p.getReviewCount()) : "0",
                p.getStockStatus(),
                reasonText,
                p.getSuggestedUse(),
                p.getAction()
        );
    }

    private ChatOrderUiModel mapToUiModel(ChatOrderResponse o) {
        List<ChatOrderTimelineUiModel> timeline = new ArrayList<>();
        if (o.getTimeline() != null) {
            for (ChatOrderTimelineResponse t : o.getTimeline()) {
                timeline.add(new ChatOrderTimelineUiModel(
                        t.getStatus(),
                        t.getLabel(),
                        t.getTime(),
                        t.getDescription()
                ));
            }
        }
        return new ChatOrderUiModel(
                o.getOrderId(),
                o.getOrderCode(),
                o.getStatus(),
                o.getStatusLabel(),
                o.getPaymentStatus(),
                o.getPaymentStatusLabel(),
                o.getTotalAmount(),
                o.getCreatedAt(),
                o.getEstimatedDelivery(),
                o.getItemsCount(),
                timeline,
                o.getNextAction()
        );
    }

    private ChatTicketUiModel mapToUiModel(ChatTicketResponse t) {
        return new ChatTicketUiModel(
                t.getTicketId(),
                t.getTicketCode(),
                t.getStatus(),
                t.getStatusLabel(),
                t.getCategory(),
                t.getCategoryLabel(),
                t.getCreatedAt(),
                t.getMessage()
        );
    }

    private ChatPreferenceQuestionUiModel mapToUiModel(ChatPreferenceQuestionResponse q) {
        return new ChatPreferenceQuestionUiModel(
                q.getQuestionType(),
                q.getQuestion(),
                q.getOptions()
        );
    }

    private ChatCartSummaryUiModel mapToUiModel(ChatCartSummaryResponse s) {
        return new ChatCartSummaryUiModel(
                s.getItemsCount() != null ? s.getItemsCount() : 0,
                s.getSubtotal() != null ? formatPrice(s.getSubtotal()) : "0đ",
                s.getDiscount() != null ? formatPrice(s.getDiscount()) : "0đ",
                s.getTotal() != null ? formatPrice(s.getTotal()) : "0đ",
                s.getDiscount() != null && s.getDiscount() > 0
        );
    }

    private ChatCartActionUiModel mapToUiModel(ChatCartActionResponse a) {
        return new ChatCartActionUiModel(
                a.getAction(),
                a.getSuccess() != null && a.getSuccess(),
                a.getRequiresConfirmation() != null && a.getRequiresConfirmation(),
                a.getReason(),
                a.getCartCount()
        );
    }

    private ComparisonUiModel mapToUiModel(ChatComparisonResponse c) {
        List<ChatProductUiModel> productUiModels = new ArrayList<>();
        if (c.getProducts() != null) {
            for (ChatProductResponse p : c.getProducts()) {
                productUiModels.add(mapToUiModel(p));
            }
        }
        return new ComparisonUiModel(
                productUiModels,
                c.getDifferences(),
                c.getRecommendation()
        );
    }

    private IngredientUiModel mapToUiModel(ChatIngredientResponse i) {
        return new IngredientUiModel(
                i.getIngredientName(),
                i.getBenefits(),
                i.getSuitableSkinTypes(),
                i.getWarnings(),
                i.getCompatibilityLevel(),
                i.getCompatibilityReason()
        );
    }

    private String formatPrice(long price) {
        return String.format(Locale.US, "%,dđ", price).replace(",", ".");
    }

    private void updateState(boolean isLoading, String error) {
        uiState.setValue(new ChatbotUiState(
                new ArrayList<>(messageList),
                isLoading,
                error,
                messageList.isEmpty(),
                new ArrayList<>(currentQuickReplies)
        ));
    }
}
