package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;

public class ReviewCommentDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("reviewId")
    private String reviewId;

    @SerializedName("customer_id")
    private CustomerInfo customer;

    @SerializedName("commentContent")
    private String commentContent;

    @SerializedName("commentStatus")
    private String commentStatus;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() { return id; }
    public String getReviewId() { return reviewId; }
    public CustomerInfo getCustomer() { return customer; }
    public String getCommentContent() { return commentContent; }
    public String getCommentStatus() { return commentStatus; }
    public String getCreatedAt() { return createdAt; }

    public static class CustomerInfo {
        @SerializedName("_id")
        private String id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getAvatarUrl() { return avatarUrl; }
    }
}
