package com.example.frontend.data.model.recommendation;

import com.example.frontend.model.Product;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class RecommendedProduct {
    @SerializedName("product")
    private Product product;

    @SerializedName("score")
    private int score;

    @SerializedName("reasons")
    private List<String> reasons;

    @SerializedName("reason_codes")
    private List<String> reasonCodes;

    @SerializedName("badges")
    private List<String> badges;

    @SerializedName("score_breakdown")
    private Map<String, Object> scoreBreakdown;

    public Product getProduct() {
        return product;
    }

    public int getScore() {
        return score;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public List<String> getReasonCodes() {
        return reasonCodes;
    }

    public List<String> getBadges() {
        return badges;
    }

    public Map<String, Object> getScoreBreakdown() {
        return scoreBreakdown;
    }
}
