package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class AccountDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("username")
    private String username;

    @SerializedName("account_status")
    private String accountStatus;

    public String getId() { return id; }
    public String getAccountType() { return accountType; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getUsername() { return username; }
    public String getAccountStatus() { return accountStatus; }
}
