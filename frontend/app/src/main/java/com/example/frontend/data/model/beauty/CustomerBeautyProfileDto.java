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

    @SerializedName("beauty_goals")
    private List<String> beautyGoals;

    @SerializedName("skin_indicators")
    private List<SkinIndicatorDto> skinIndicators;

    @SerializedName("profile_completion")
    private int profileCompletion;

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getSkinType() { return skinType; }
    public List<String> getSkinConcerns() { return skinConcerns; }
    public List<String> getBeautyGoals() { return beautyGoals; }
    public List<SkinIndicatorDto> getSkinIndicators() { return skinIndicators; }
    public int getProfileCompletion() { return profileCompletion; }

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
