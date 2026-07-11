package com.example.frontend.data.model.beauty;

import com.google.gson.annotations.SerializedName;

public class BeautyReferenceDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("reference_group")
    private String referenceGroup;

    @SerializedName("reference_code")
    private String referenceCode;

    @SerializedName("display_name_vi")
    private String displayNameVi;

    @SerializedName("display_name_en")
    private String displayNameEn;

    @SerializedName("description")
    private String description;

    @SerializedName("icon_url")
    private String iconUrl;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReferenceGroup() { return referenceGroup; }
    public void setReferenceGroup(String referenceGroup) { this.referenceGroup = referenceGroup; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }

    public String getDisplayNameVi() { return displayNameVi; }
    public void setDisplayNameVi(String displayNameVi) { this.displayNameVi = displayNameVi; }

    public String getDisplayNameEn() { return displayNameEn; }
    public void setDisplayNameEn(String displayNameEn) { this.displayNameEn = displayNameEn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}
