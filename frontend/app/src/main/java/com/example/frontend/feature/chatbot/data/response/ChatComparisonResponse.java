package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ChatComparisonResponse {
    @SerializedName("products")
    private List<ChatProductResponse> products;

    @SerializedName("differences")
    private Map<String, String> differences;

    @SerializedName("recommendation")
    private String recommendation;

    public List<ChatProductResponse> getProducts() {
        return products;
    }

    public Map<String, String> getDifferences() {
        return differences;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
