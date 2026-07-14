package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ReviewInsightDto {
    @SerializedName("status")
    private Status status;

    @SerializedName("short_summary")
    private String shortSummary;

    @SerializedName("positive_themes")
    private List<Theme> positiveThemes = new ArrayList<>();

    @SerializedName("negative_themes")
    private List<Theme> negativeThemes = new ArrayList<>();

    @SerializedName("common_experiences")
    private List<String> commonExperiences = new ArrayList<>();

    @SerializedName("usage_tips")
    private List<String> usageTips = new ArrayList<>();

    @SerializedName("cautions")
    private List<String> cautions = new ArrayList<>();

    @SerializedName("sampled_review_count")
    private Integer sampledReviewCount;

    @SerializedName("generated_at")
    private String generatedAt;

    public enum Status {
        @SerializedName("PENDING") PENDING,
        @SerializedName("GENERATING") GENERATING,
        @SerializedName("READY") READY,
        @SerializedName("STALE") STALE,
        @SerializedName("FAILED") FAILED,
        @SerializedName("DISABLED") DISABLED,
        @SerializedName("INSUFFICIENT_REVIEWS") INSUFFICIENT_REVIEWS
    }

    public static class Theme {
        @SerializedName("code")
        private String code;
        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("supporting_review_refs")
        private List<String> supportingReviewRefs = new ArrayList<>();

        // Getters
        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public List<String> getSupportingReviewRefs() { return supportingReviewRefs; }
    }

    // Getters
    public Status getStatus() { return status; }
    public String getShortSummary() { return shortSummary; }
    public List<Theme> getPositiveThemes() { return positiveThemes; }
    public List<Theme> getNegativeThemes() { return negativeThemes; }
    public List<String> getCommonExperiences() { return commonExperiences; }
    public List<String> getUsageTips() { return usageTips; }
    public List<String> getCautions() { return cautions; }
    public Integer getSampledReviewCount() { return sampledReviewCount; }
    public String getGeneratedAt() { return generatedAt; }
}
