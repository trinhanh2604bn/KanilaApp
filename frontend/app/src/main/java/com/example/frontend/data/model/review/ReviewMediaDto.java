package com.example.frontend.data.model.review;

import com.google.gson.annotations.SerializedName;

public class ReviewMediaDto {

    @SerializedName("_id")
    private String id;

    @SerializedName("reviewId")
    private String reviewId;

    @SerializedName("mediaType")
    private String mediaType;

    @SerializedName("mediaUrl")
    private String mediaUrl;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    @SerializedName("sortOrder")
    private Integer sortOrder;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public ReviewMediaDto() {}

    public ReviewMediaDto(String mediaUrl, String mediaType) {
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }

    public String getId() {
        return id;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean isImage() {
        return "image".equalsIgnoreCase(mediaType);
    }

    public boolean isVideo() {
        return "video".equalsIgnoreCase(mediaType);
    }
}
