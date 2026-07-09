package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class CustomerDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("account_id")
    private String accountId;

    @SerializedName("customer_code")
    private String customerCode;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("avatar_url")
    private String avatarUrl;

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getCustomerCode() { return customerCode; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public String getAvatarUrl() { return avatarUrl; }
}
