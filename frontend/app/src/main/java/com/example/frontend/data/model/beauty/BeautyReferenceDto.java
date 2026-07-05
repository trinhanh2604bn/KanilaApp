package com.example.frontend.data.model.beauty;

import com.google.gson.annotations.SerializedName;

public class BeautyReferenceDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("reference_type")
    private String referenceType; // e.g., "goal", "skin_type", "concern"

    @SerializedName("reference_group")
    private String referenceGroup;

    @SerializedName("reference_code")
    private String referenceCode;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("description")
    private String description;

    @SerializedName("icon_url")
    private String iconUrl;

    public String getId() { return id; }
    public String getReferenceType() { return referenceType; }
    public String getReferenceGroup() { return referenceGroup; }
    public String getReferenceCode() { return referenceCode; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getIconUrl() { return iconUrl; }
}
