package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;

public class ReviewVoteResponse {
    @SerializedName("reviewId")
    private String reviewId;

    @SerializedName("liked")
    private boolean liked;

    @SerializedName("helpfulCount")
    private int helpfulCount;

    public String getReviewId() { return reviewId; }
    public boolean isLiked() { return liked; }
    public int getHelpfulCount() { return helpfulCount; }
}
