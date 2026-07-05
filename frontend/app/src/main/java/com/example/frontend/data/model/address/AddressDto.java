package com.example.frontend.data.model.address;

import com.google.gson.annotations.SerializedName;

public class AddressDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address_line")
    private String addressLine;

    @SerializedName("is_default")
    private boolean isDefault;

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public boolean isDefault() { return isDefault; }
}
