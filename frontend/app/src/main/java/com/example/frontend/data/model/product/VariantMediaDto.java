package com.example.frontend.data.model.product;

import com.google.gson.annotations.SerializedName;

public class VariantMediaDto {
    @SerializedName("_id")
    private String id;
    @SerializedName("variantId")
    private String variantId;
    @SerializedName("mediaType")
    private String mediaType;
    @SerializedName("mediaUrl")
    private String mediaUrl;
    @SerializedName("sortOrder")
    private int sortOrder;
    @SerializedName("isPrimary")
    private boolean isPrimary;

    public String getId() { return id; }
    public String getVariantId() { return variantId; }
    public String getMediaType() { return mediaType; }
    public String getMediaUrl() { return mediaUrl; }
}
