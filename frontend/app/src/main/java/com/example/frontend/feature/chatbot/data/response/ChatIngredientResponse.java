package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatIngredientResponse {
    @SerializedName("ingredient_name")
    private String ingredientName;

    @SerializedName("benefits")
    private List<String> benefits;

    @SerializedName("suitable_skin_types")
    private List<String> suitableSkinTypes;

    @SerializedName("warnings")
    private List<String> warnings;

    @SerializedName("compatibility_level")
    private String compatibilityLevel;

    @SerializedName("compatibility_reason")
    private String compatibilityReason;

    public String getIngredientName() {
        return ingredientName;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public List<String> getSuitableSkinTypes() {
        return suitableSkinTypes;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getCompatibilityLevel() {
        return compatibilityLevel;
    }

    public String getCompatibilityReason() {
        return compatibilityReason;
    }
}
