package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ProductMediaDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("mediaType")
    private String mediaType; // "image", "video"

    @SerializedName("mediaUrl")
    private String url;

    @SerializedName("sortOrder")
    private int displayOrder;

    public String getId() { return id; }
    public String getMediaType() { return mediaType; }
    public String getUrl() { return url; }
    public int getDisplayOrder() { return displayOrder; }

    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setUrl(String url) { this.url = url; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
