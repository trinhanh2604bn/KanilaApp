package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class SkinMatchDto implements Serializable {
    @SerializedName("status")
    private Status status;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("score")
    private Integer score;

    @SerializedName("estimated_score")
    private Integer estimatedScore;

    @SerializedName("estimated")
    private boolean estimated;

    @SerializedName("match_level")
    private MatchLevel matchLevel;

    @SerializedName("confidence_score")
    private Integer confidenceScore;

    @SerializedName("profile_completion_rate")
    private Integer profileCompletionRate;

    @SerializedName("matching_data_completeness")
    private Integer matchingDataCompleteness;

    @SerializedName(value = "match_explanation", alternate = {"explanation", "matchExplanation"})
    private String matchExplanation;

    @SerializedName("reasons")
    private List<Reason> reasons;

    @SerializedName("cautions")
    private List<Caution> cautions;

    @SerializedName("hard_conflicts")
    private List<HardConflict> hardConflicts;

    @SerializedName("matched_attributes")
    private List<String> matchedAttributes;

    @SerializedName("generated_at")
    private String generatedAt;

    @SerializedName("expires_at")
    private String expiresAt;

    public enum Status implements Serializable {
        @SerializedName("READY") READY,
        @SerializedName("PROFILE_REQUIRED") PROFILE_REQUIRED,
        @SerializedName("PROFILE_INCOMPLETE") PROFILE_INCOMPLETE,
        @SerializedName("INSUFFICIENT_PRODUCT_DATA") INSUFFICIENT_PRODUCT_DATA,
        @SerializedName("CAUTION") CAUTION,
        @SerializedName("TEMPORARILY_UNAVAILABLE") TEMPORARILY_UNAVAILABLE
    }

    public enum MatchLevel implements Serializable {
        @SerializedName("EXCELLENT_MATCH") EXCELLENT_MATCH,
        @SerializedName("GOOD_MATCH") GOOD_MATCH,
        @SerializedName("MODERATE_MATCH") MODERATE_MATCH,
        @SerializedName("CAUTION") CAUTION,
        @SerializedName("INSUFFICIENT_DATA") INSUFFICIENT_DATA
    }

    public static class Reason implements Serializable {
        @SerializedName("code")
        private String code;
        @SerializedName("text")
        private String text;
        @SerializedName("contribution")
        private Integer contribution;

        // Getters
        public String getCode() { return code; }
        public String getText() { return text; }
        public Integer getContribution() { return contribution; }
    }

    public static class Caution implements Serializable {
        @SerializedName("code")
        private String code;
        @SerializedName("text")
        private String text;
        @SerializedName("severity")
        private String severity; // INFO, HIGH

        // Getters
        public String getCode() { return code; }
        public String getText() { return text; }
        public String getSeverity() { return severity; }
    }

    public static class HardConflict implements Serializable {
        @SerializedName("code")
        private String code;
        @SerializedName("text")
        private String text;

        // Getters
        public String getCode() { return code; }
        public String getText() { return text; }
    }

    // Getters
    public Status getStatus() { return status; }
    public String getProductId() { return productId; }
    public Integer getScore() { return score; }
    public Integer getEstimatedScore() { return estimatedScore; }
    public boolean isEstimated() { return estimated; }
    public MatchLevel getMatchLevel() { return matchLevel; }
    public Integer getConfidenceScore() { return confidenceScore; }
    public Integer getProfileCompletionRate() { return profileCompletionRate; }
    public Integer getMatchingDataCompleteness() { return matchingDataCompleteness; }
    public String getMatchExplanation() { return matchExplanation; }
    public List<Reason> getReasons() { return reasons; }
    public List<Caution> getCautions() { return cautions; }
    public List<HardConflict> getHardConflicts() { return hardConflicts; }
    public List<String> getMatchedAttributes() { return matchedAttributes; }
    public String getGeneratedAt() { return generatedAt; }
    public String getExpiresAt() { return expiresAt; }
}
