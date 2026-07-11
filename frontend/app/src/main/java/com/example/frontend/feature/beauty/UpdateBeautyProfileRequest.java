package com.example.frontend.feature.beauty;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Request body for PATCH /api/customers/{customer_id}/beauty-profile
 * Standardized mapping as per Beauty Profile API Guide.
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
    public void setSkinType(String val) { this.skinType = val; }
    public void setSensitivityLevel(String val) { this.sensitivityLevel = val; }
    public void setSkinColor(String val) { this.skinColor = val; }
    public void setSkinUndertone(String val) { this.skinUndertone = val; }
    public void setFoundationFinish(String val) { this.foundationFinish = val; }
    public void setBudget(String val) { this.budget = val; }
    public void setFragrancePreference(String val) { this.fragrancePreference = val; }
    
    public void setSkinConcerns(List<String> val) { this.skinConcerns = val; }
    public void setLipstickColors(List<String> val) { this.lipstickColors = val; }
    public void setMakeupStyles(List<String> val) { this.makeupStyles = val; }
    public void setAvoidIngredients(List<String> val) { this.avoidIngredients = val; }
    public void setBeautyGoals(List<String> val) { this.beautyGoals = val; }
    public void setPreferredIngredients(List<String> val) { this.preferredIngredients = val; }
    public void setPreferredBrands(List<String> val) { this.preferredBrands = val; }
    public void setDislikedBrands(List<String> val) { this.dislikedBrands = val; }
    public void setPreferredCategories(List<String> val) { this.preferredCategories = val; }
    public void setTexturePreference(List<String> val) { this.texturePreference = val; }
    public void setPurchaseIntent(List<String> val) { this.purchaseIntent = val; }

    // Getters
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
}
