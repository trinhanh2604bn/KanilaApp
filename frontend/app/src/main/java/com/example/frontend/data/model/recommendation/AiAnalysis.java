package com.example.frontend.data.model.recommendation;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AiAnalysis {
    @SerializedName("health_score")
    private Integer healthScore;

    @SerializedName("analysis_text")
    private String analysisText;

    @SerializedName("ideal_ingredients")
    private List<String> idealIngredients;

    public Integer getHealthScore() {
        return healthScore;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public List<String> getIdealIngredients() {
        return idealIngredients;
    }
}
