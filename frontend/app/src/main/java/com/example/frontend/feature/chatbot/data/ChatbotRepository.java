package com.example.frontend.feature.chatbot.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.chatbot.data.request.ChatbotMessageRequest;
import com.example.frontend.feature.chatbot.data.response.ChatbotMessageResponse;
import com.example.frontend.feature.chatbot.data.response.ChatbotSessionHistoryResponse;
import com.google.gson.Gson;
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
                // Requirement 1: Add detailed logs at every step
                String rawJson = "";
                try {
                    rawJson = new Gson().toJson(response.body());
                } catch (Exception e) {
                    rawJson = "Error parsing raw response: " + e.getMessage();
                }
                Log.d("CHATBOT_DEBUG", "RAW_RESPONSE: " + rawJson);

                if (response.isSuccessful() && response.body() != null) {
                    ChatbotMessageResponse chatbotResponse = response.body();
                    
                    Log.d("CHATBOT_DEBUG", "Parsed response: " + (chatbotResponse != null));
                    Log.d("CHATBOT_DEBUG", "isSuccess: " + (chatbotResponse != null && chatbotResponse.isSuccess()));
                    
                    if (chatbotResponse != null && chatbotResponse.getData() != null) {
                        Log.d("CHATBOT_DEBUG", "getData: " + (chatbotResponse.getData() != null));
                        Log.d("CHATBOT_DEBUG", "botMessage: " + chatbotResponse.getData().getBotMessage());
                        Log.d("CHATBOT_DEBUG", "replyType: " + chatbotResponse.getData().getReplyType());
                        Log.d("CHATBOT_DEBUG", "products count: " + (chatbotResponse.getData().getProducts() != null ? chatbotResponse.getData().getProducts().size() : 0));
                    }

                    if (chatbotResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(chatbotResponse));
                    } else {
                        // Logic for actual errors returned by server
                        String errorMessage = chatbotResponse.getMessage();
                        if (chatbotResponse.getError() != null) {
                             String code = chatbotResponse.getError().getCode();
                             if ("CHATBOT_CONFIG_ERROR".equals(code)) {
                                 errorMessage = "Kanila AI đang tạm thời chưa sẵn sàng. Bạn thử lại sau nhé.";
                             } else if ("CHATBOT_TIMEOUT".equals(code)) {
                                 errorMessage = "Kanila AI phản hồi hơi lâu. Bạn thử gửi lại giúp mình nhé.";
                             } else if (chatbotResponse.getError().getDetails() != null) {
                                 errorMessage = chatbotResponse.getError().getDetails();
                             }
                        }
                        
                        // Requirement 3: Only trigger fallback when absolutely necessary
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Mình chưa thể trả lời bạn, hãy thử sau";
                        }
                        result.setValue(NetworkResult.error(errorMessage));
                    }
                } else {
                    // This is for non-2xx HTTP responses or null body
                    Log.e("CHATBOT_DEBUG", "HTTP Error or Null Body: " + response.code());
                    result.setValue(NetworkResult.error("Mình chưa thể trả lời bạn, hãy thử sau"));
                }
            }

            @Override
            public void onFailure(Call<ChatbotMessageResponse> call, Throwable t) {
                Log.e("CHATBOT_DEBUG", "Retrofit onFailure: " + t.getMessage());
                // Check if it's a network error
                if (t instanceof java.io.IOException) {
                    result.setValue(NetworkResult.noInternet());
                } else {
                    result.setValue(NetworkResult.error("Mình chưa thể trả lời bạn, hãy thử sau"));
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
