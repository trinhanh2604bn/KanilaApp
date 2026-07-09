package com.example.frontend.feature.chatbot.model;

import java.util.List;
import java.util.Map;

public class ComparisonUiModel {
    private final List<ChatProductUiModel> products;
    private final Map<String, String> differences;
    private final String recommendation;

    public ComparisonUiModel(List<ChatProductUiModel> products, Map<String, String> differences, String recommendation) {
        this.products = products;
        this.differences = differences;
        this.recommendation = recommendation;
    }

    public List<ChatProductUiModel> getProducts() {
        return products;
    }

    public Map<String, String> getDifferences() {
        return differences;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
