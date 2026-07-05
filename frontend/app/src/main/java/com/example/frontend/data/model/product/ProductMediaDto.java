package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ProductMediaDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("media_type")
    private String mediaType; // "image", "video"

    @SerializedName("url")
    private String url;

    @SerializedName("display_order")
    private int displayOrder;

    public String getId() { return id; }
    public String getMediaType() { return mediaType; }
    public String getUrl() { return url; }
    public int getDisplayOrder() { return displayOrder; }
}
