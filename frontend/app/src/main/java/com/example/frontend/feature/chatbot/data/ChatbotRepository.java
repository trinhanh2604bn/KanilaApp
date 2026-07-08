package com.example.frontend.feature.chatbot.data;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.chatbot.data.request.ChatbotMessageRequest;
import com.example.frontend.feature.chatbot.data.response.ChatbotMessageResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotSessionHistoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotRepository {
    private final ApiService apiService;

    public ChatbotRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void sendMessage(ChatbotMessageRequest request, MutableLiveData<NetworkResult<ChatbotMessageResponse>> result) {
        result.setValue(NetworkResult.loading());
        apiService.sendChatbotMessage(request).enqueue(new Callback<ChatbotMessageResponse>() {
            @Override
            public void onResponse(Call<ChatbotMessageResponse> call, Response<ChatbotMessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatbotMessageResponse chatbotResponse = response.body();
                    if (chatbotResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(chatbotResponse));
                    } else {
                        String errorMessage = chatbotResponse.getMessage();
                        if (chatbotResponse.getError() != null) {
                             // Map technical codes to friendly messages as per requirement
                             String code = chatbotResponse.getError().getCode();
                             if ("CHATBOT_CONFIG_ERROR".equals(code)) {
                                 errorMessage = "Kanila AI đang tạm thời chưa sẵn sàng. Bạn thử lại sau nhé.";
                             } else if ("CHATBOT_TIMEOUT".equals(code)) {
                                 errorMessage = "Kanila AI phản hồi hơi lâu. Bạn thử gửi lại giúp mình nhé.";
                             } else if (chatbotResponse.getError().getDetails() != null) {
                                 errorMessage = chatbotResponse.getError().getDetails();
                             }
                        }
                        if (errorMessage == null) errorMessage = "Mình chưa thể trả lời lúc này. Bạn thử lại giúp mình nhé.";
                        result.setValue(NetworkResult.error(errorMessage));
                    }
                } else {
                    result.setValue(NetworkResult.error("Mình chưa thể trả lời lúc này. Bạn thử lại giúp mình nhé."));
                }
            }

            @Override
            public void onFailure(Call<ChatbotMessageResponse> call, Throwable t) {
                // Check if it's a network error
                if (t instanceof java.io.IOException) {
                    result.setValue(NetworkResult.noInternet());
                } else {
                    result.setValue(NetworkResult.error("Mình chưa thể trả lời lúc này. Bạn thử lại giúp mình nhé."));
                }
            }
        });
    }

    public void getHistory(String sessionId, MutableLiveData<NetworkResult<ChatbotSessionHistoryResponse>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getChatbotHistory(sessionId).enqueue(new Callback<ChatbotSessionHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatbotSessionHistoryResponse> call, Response<ChatbotSessionHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(NetworkResult.success(response.body()));
                } else {
                    result.setValue(NetworkResult.error("Không thể tải lịch sử trò chuyện."));
                }
            }

            @Override
            public void onFailure(Call<ChatbotSessionHistoryResponse> call, Throwable t) {
                result.setValue(NetworkResult.error("Lỗi kết nối khi tải lịch sử."));
            }
        });
    }
}
