package com.example.frontend.feature.chatbot.model;

import java.util.List;

public class IngredientUiModel {
    private final String ingredientName;
    private final List<String> benefits;
    private final List<String> suitableSkinTypes;
    private final List<String> warnings;
    private final String compatibilityLevel;
    private final String compatibilityReason;

    public IngredientUiModel(String ingredientName, List<String> benefits, List<String> suitableSkinTypes, 
                           List<String> warnings, String compatibilityLevel, String compatibilityReason) {
        this.ingredientName = ingredientName;
        this.benefits = benefits;
        this.suitableSkinTypes = suitableSkinTypes;
        this.warnings = warnings;
        this.compatibilityLevel = compatibilityLevel;
        this.compatibilityReason = compatibilityReason;
    }

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
