package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ReviewSummaryDto {
    @SerializedName("averageRating")
    private double averageRating;

    @SerializedName("reviewCount")
    private int reviewCount;

    @SerializedName("ratingDistribution")
    private Map<String, Integer> ratingDistribution;

    @SerializedName("aiSummary")
    private String aiSummary;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("reviewMediaPreview")
    private List<ReviewMediaDto> reviewMediaPreview;

    public double getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Map<String, Integer> getRatingDistribution() {
        return ratingDistribution;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<ReviewMediaDto> getReviewMediaPreview() {
        return reviewMediaPreview;
    }
}
