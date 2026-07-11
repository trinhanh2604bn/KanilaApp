package com.example.frontend.feature.beauty;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Request body for PATCH /api/customers/{customer_id}/beauty-profile
 * Only contains user-editable fields.
 */
public class UpdateBeautyProfileRequest {

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

    // Setters
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
}
