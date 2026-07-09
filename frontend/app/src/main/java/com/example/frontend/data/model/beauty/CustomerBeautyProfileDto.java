package com.example.frontend.data.model.beauty;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CustomerBeautyProfileDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("skin_type")
    private String skinType;

    @SerializedName("skin_concerns")
    private List<String> skinConcerns;

    @SerializedName("sensitivity_level")
    private String sensitivityLevel;

    @SerializedName("skin_color")
    private String skinColor;

    @SerializedName("skin_undertone")
    private String skinUndertone;

    @SerializedName("foundation_finish")
    private String foundationFinish;

    @SerializedName("lipstick_colors")
    private List<String> lipstickColors;

    @SerializedName("makeup_styles")
    private List<String> makeupStyles;

    @SerializedName("budget")
    private String budget;

    @SerializedName("avoid_ingredients")
    private List<String> avoidIngredients;

    @SerializedName("beauty_goals")
    private List<String> beautyGoals;

    @SerializedName("skin_indicators")
    private List<SkinIndicatorDto> skinIndicators;

    @SerializedName("profile_completion")
    private int profileCompletion;

    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getSkinType() { return skinType; }
    public List<String> getSkinConcerns() { return skinConcerns; }
    public String getSensitivityLevel() { return sensitivityLevel; }
    public String getSkinColor() { return skinColor; }
    public String getSkinUndertone() { return skinUndertone; }
    public String getFoundationFinish() { return foundationFinish; }
    public List<String> getLipstickColors() { return lipstickColors; }
    public List<String> getMakeupStyles() { return makeupStyles; }
    public String getBudget() { return budget; }
    public List<String> getAvoidIngredients() { return avoidIngredients; }
    public List<String> getBeautyGoals() { return beautyGoals; }
    public List<SkinIndicatorDto> getSkinIndicators() { return skinIndicators; }
    public int getProfileCompletion() { return profileCompletion; }

    // Setters
    public void setSkinType(String skinType) { this.skinType = skinType; }
    public void setSkinConcerns(List<String> skinConcerns) { this.skinConcerns = skinConcerns; }
    public void setSensitivityLevel(String sensitivityLevel) { this.sensitivityLevel = sensitivityLevel; }
    public void setSkinColor(String skinColor) { this.skinColor = skinColor; }
    public void setSkinUndertone(String skinUndertone) { this.skinUndertone = skinUndertone; }
    public void setFoundationFinish(String foundationFinish) { this.foundationFinish = foundationFinish; }
    public void setLipstickColors(List<String> lipstickColors) { this.lipstickColors = lipstickColors; }
    public void setMakeupStyles(List<String> makeupStyles) { this.makeupStyles = makeupStyles; }
    public void setBudget(String budget) { this.budget = budget; }
    public void setAvoidIngredients(List<String> avoidIngredients) { this.avoidIngredients = avoidIngredients; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }

    public static class SkinIndicatorDto {
        @SerializedName("code")
        private String code;
        @SerializedName("score")
        private int score;
        @SerializedName("status")
        private String status;

        public String getCode() { return code; }
        public int getScore() { return score; }
        public String getStatus() { return status; }
    }
}
