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

    @SerializedName("pros_cons")
    private Map<String, ProsCons> prosCons;

    public static class ProsCons {
        @SerializedName("pros")
        private List<String> pros;
        @SerializedName("cons")
        private List<String> cons;

        public List<String> getPros() { return pros; }
        public List<String> getCons() { return cons; }
    }

    public List<ChatProductResponse> getProducts() {
        return products;
    }

    public Map<String, String> getDifferences() {
        return differences;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public Map<String, ProsCons> getProsCons() {
        return prosCons;
    }
}
