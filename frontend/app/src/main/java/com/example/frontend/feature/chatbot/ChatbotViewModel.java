package com.example.frontend.feature.chatbot;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.feature.chatbot.data.ChatbotRepository;
import com.example.frontend.feature.chatbot.data.request.ChatbotContextRequest;
import com.example.frontend.feature.chatbot.data.request.ChatbotMessageRequest;
import com.example.frontend.feature.chatbot.data.response.ChatCartActionResponse;
import com.example.frontend.feature.chatbot.data.response.ChatCartSummaryResponse;
import com.example.frontend.feature.chatbot.data.response.ChatOrderResponse;
import com.example.frontend.feature.chatbot.data.response.ChatOrderTimelineResponse;
import com.example.frontend.feature.chatbot.data.response.ChatProductResponse;
import com.example.frontend.feature.chatbot.data.response.ChatPreferenceQuestionResponse;
import com.example.frontend.feature.chatbot.data.response.ChatTicketResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotDataResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotMessageResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotSessionHistoryResponse;
import com.example.frontend.feature.chatbot.model.ChatCartActionUiModel;
import com.example.frontend.feature.chatbot.model.ChatCartSummaryUiModel;
import com.example.frontend.feature.chatbot.model.ChatMessageUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderTimelineUiModel;
import com.example.frontend.feature.chatbot.model.ChatOrderUiModel;
import com.example.frontend.feature.chatbot.model.ChatPreferenceQuestionUiModel;
import com.example.frontend.feature.chatbot.model.ChatProductUiModel;
import com.example.frontend.feature.chatbot.model.ChatTicketUiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatbotViewModel extends AndroidViewModel {

    private final ChatbotRepository repository;
    private final MutableLiveData<ChatbotUiState> uiState = new MutableLiveData<>(ChatbotUiState.empty());
    private final List<ChatMessageUiModel> messageList = new ArrayList<>();
    private final List<String> currentQuickReplies = new ArrayList<>();
    private final MutableLiveData<NetworkResult<ChatbotMessageResponse>> lastResponse = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<ChatbotSessionHistoryResponse>> historyResponse = new MutableLiveData<>();
    private final TokenManager tokenManager;
    private String sessionId;

    public ChatbotViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ChatbotRepository(application);
        this.tokenManager = TokenManager.getInstance(application);
        this.sessionId = tokenManager.getChatbotSession();
        
        if (this.sessionId != null) {
            loadHistory();
        }
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

        // Remove typing indicator
        messageList.removeIf(ChatMessageUiModel::isTyping);

        if (result.status == NetworkResult.Status.SUCCESS && result.data != null) {
            ChatbotDataResponse data = result.data.getData();
            if (data != null) {
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

                ChatMessageUiModel botMsg = new ChatMessageUiModel(
                        UUID.randomUUID().toString(),
                        data.getBotMessage(),
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
                        cartActionUiModel
                );
                messageList.add(botMsg);
                updateState(false, null);
            }
        } else if (result.status == NetworkResult.Status.ERROR) {
            // Add error bubble
            String errorMsg = result.message != null ? result.message : "Mình chưa thể trả lời lúc này. Bạn thử lại giúp mình nhé.";
            addErrorBubble(errorMsg);
        } else if (result.status == NetworkResult.Status.NO_INTERNET) {
            addErrorBubble("Không có kết nối mạng. Vui lòng kiểm tra Wi-Fi/4G và thử lại.");
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
        return new ChatProductUiModel(
                p.getProductId(),
                p.getVariantId(),
                p.getSlug(),
                p.getName(),
                p.getBrandName(),
                p.getPrice() != null ? formatPrice(p.getPrice()) : "Liên hệ",
                p.getCompareAtPrice() != null ? formatPrice(p.getCompareAtPrice()) : null,
                p.getImageUrl(),
                p.getRating() != null ? String.valueOf(p.getRating()) : null,
                p.getReviewCount() != null ? String.valueOf(p.getReviewCount()) : "0",
                p.getStockStatus(),
                p.getReason()
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
