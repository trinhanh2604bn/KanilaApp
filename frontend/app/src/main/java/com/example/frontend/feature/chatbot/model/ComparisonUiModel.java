package com.example.frontend.feature.chatbot.model;

import java.util.List;
import java.util.Map;

public class ComparisonUiModel {
    private final List<ChatProductUiModel> products;
    private final Map<String, String> differences;
    private final String recommendation;
    private final Map<String, ProsConsUi> prosCons;

    public static class ProsConsUi {
        private final List<String> pros;
        private final List<String> cons;
        public ProsConsUi(List<String> pros, List<String> cons) {
            this.pros = pros;
            this.cons = cons;
        }
        public List<String> getPros() { return pros; }
        public List<String> getCons() { return cons; }
    }

    public ComparisonUiModel(List<ChatProductUiModel> products, Map<String, String> differences, String recommendation, Map<String, ProsConsUi> prosCons) {
        this.products = products;
        this.differences = differences;
        this.recommendation = recommendation;
        this.prosCons = prosCons;
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

    public Map<String, ProsConsUi> getProsCons() {
        return prosCons;
    }
}
