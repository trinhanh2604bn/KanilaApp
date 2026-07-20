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

    @SerializedName("sensitivity_level")
    private String sensitivityLevel;

    @SerializedName("skin_color")
    private String skinColor;

    @SerializedName("skin_undertone")
    private String skinUndertone;

    @SerializedName("foundation_finish")
    private String foundationFinish;

    @SerializedName("budget")
    private String budget;

    @SerializedName("fragrance_preference")
    private String fragrancePreference;

    @SerializedName("skin_concerns")
    private List<String> skinConcerns;

    @SerializedName("lipstick_colors")
    private List<String> lipstickColors;

    @SerializedName("makeup_styles")
    private List<String> makeupStyles;

    @SerializedName("avoid_ingredients")
    private List<String> avoidIngredients;

    @SerializedName("beauty_goals")
    private List<String> beautyGoals;

    @SerializedName("preferred_ingredients")
    private List<String> preferredIngredients;

    @SerializedName("preferred_brands")
    private List<String> preferredBrands;

    @SerializedName("disliked_brands")
    private List<String> dislikedBrands;

    @SerializedName("preferred_categories")
    private List<String> preferredCategories;

    @SerializedName("texture_preference")
    private List<String> texturePreference;

    @SerializedName("purchase_intent")
    private List<String> purchaseIntent;

    @SerializedName("skin_indicators")
    private SkinIndicatorsWrapper skinIndicators;

    @SerializedName("profile_completion_rate")
    private int profileCompletionRate;

    @SerializedName("profile_hash")
    private String profileHash;

    @SerializedName("source")
    private String source;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("last_updated_at")
    private String lastUpdatedAt;

    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getSkinType() { return skinType; }
    public String getSensitivityLevel() { return sensitivityLevel; }
    public String getSkinColor() { return skinColor; }
    public String getSkinUndertone() { return skinUndertone; }
    public String getFoundationFinish() { return foundationFinish; }
    public String getBudget() { return budget; }
    public String getFragrancePreference() { return fragrancePreference; }
    public List<String> getSkinConcerns() { return skinConcerns; }
    public List<String> getLipstickColors() { return lipstickColors; }
    public List<String> getMakeupStyles() { return makeupStyles; }
    public List<String> getAvoidIngredients() { return avoidIngredients; }
    public List<String> getBeautyGoals() { return beautyGoals; }
    public List<String> getPreferredIngredients() { return preferredIngredients; }
    public List<String> getPreferredBrands() { return preferredBrands; }
    public List<String> getDislikedBrands() { return dislikedBrands; }
    public List<String> getPreferredCategories() { return preferredCategories; }
    public List<String> getTexturePreference() { return texturePreference; }
    public List<String> getPurchaseIntent() { return purchaseIntent; }
    public SkinIndicatorsWrapper getSkinIndicators() { return skinIndicators; }
    public int getProfileCompletionRate() { return profileCompletionRate; }
    public String getProfileHash() { return profileHash; }
    public String getSource() { return source; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getLastUpdatedAt() { return lastUpdatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setSkinType(String skinType) { this.skinType = skinType; }
    public void setSensitivityLevel(String sensitivityLevel) { this.sensitivityLevel = sensitivityLevel; }
    public void setSkinColor(String skinColor) { this.skinColor = skinColor; }
    public void setSkinUndertone(String skinUndertone) { this.skinUndertone = skinUndertone; }
    public void setFoundationFinish(String foundationFinish) { this.foundationFinish = foundationFinish; }
    public void setBudget(String budget) { this.budget = budget; }
    public void setFragrancePreference(String fragrancePreference) { this.fragrancePreference = fragrancePreference; }
    public void setSkinConcerns(List<String> skinConcerns) { this.skinConcerns = skinConcerns; }
    public void setLipstickColors(List<String> lipstickColors) { this.lipstickColors = lipstickColors; }
    public void setMakeupStyles(List<String> makeupStyles) { this.makeupStyles = makeupStyles; }
    public void setAvoidIngredients(List<String> avoidIngredients) { this.avoidIngredients = avoidIngredients; }
    public void setBeautyGoals(List<String> beautyGoals) { this.beautyGoals = beautyGoals; }
    public void setPreferredIngredients(List<String> preferredIngredients) { this.preferredIngredients = preferredIngredients; }
    public void setPreferredBrands(List<String> preferredBrands) { this.preferredBrands = preferredBrands; }
    public void setDislikedBrands(List<String> dislikedBrands) { this.dislikedBrands = dislikedBrands; }
    public void setPreferredCategories(List<String> preferredCategories) { this.preferredCategories = preferredCategories; }
    public void setTexturePreference(List<String> texturePreference) { this.texturePreference = texturePreference; }
    public void setPurchaseIntent(List<String> purchaseIntent) { this.purchaseIntent = purchaseIntent; }
    public void setSkinIndicators(SkinIndicatorsWrapper skinIndicators) { this.skinIndicators = skinIndicators; }
    public void setProfileCompletionRate(int profileCompletionRate) { this.profileCompletionRate = profileCompletionRate; }
    public void setProfileHash(String profileHash) { this.profileHash = profileHash; }
    public void setSource(String source) { this.source = source; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setLastUpdatedAt(String lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public static class SkinIndicatorsWrapper {
        @SerializedName("data")
        private List<SkinIndicatorDto> data;
        @SerializedName("analyzed_at")
        private String analyzedAt;
        @SerializedName("source")
        private String source;
        @SerializedName("confidence_score")
        private Double confidenceScore;

        public List<SkinIndicatorDto> getData() { return data; }
        public void setData(List<SkinIndicatorDto> data) { this.data = data; }
    }

    public static class SkinIndicatorDto {
        @SerializedName("code")
        private String code;
        @SerializedName("score")
        private int score;
        @SerializedName("status")
        private String status;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
