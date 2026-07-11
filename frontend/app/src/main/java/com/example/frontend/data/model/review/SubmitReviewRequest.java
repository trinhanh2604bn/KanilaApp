package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubmitReviewRequest {
    @SerializedName("orderItemId")
    private String orderItemId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewTitle")
    private String reviewTitle;

    @SerializedName("reviewContent")
    private String reviewContent;

    @SerializedName("reviewTags")
    private List<String> reviewTags;

    @SerializedName("skinTypes")
    private List<String> skinTypes;

    @SerializedName("mediaUrls")
    private List<String> mediaUrls;

    public SubmitReviewRequest(String orderItemId, int rating, String reviewTitle, String reviewContent, List<String> reviewTags, List<String> skinTypes, List<String> mediaUrls) {
        this.orderItemId = orderItemId;
        this.rating = rating;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.reviewTags = reviewTags;
        this.skinTypes = skinTypes;
        this.mediaUrls = mediaUrls;
    }

    // Getters
    public String getOrderItemId() { return orderItemId; }
    public int getRating() { return rating; }
    public String getReviewTitle() { return reviewTitle; }
    public String getReviewContent() { return reviewContent; }
    public List<String> getReviewTags() { return reviewTags; }
    public List<String> getSkinTypes() { return skinTypes; }
    public List<String> getMediaUrls() { return mediaUrls; }
}
