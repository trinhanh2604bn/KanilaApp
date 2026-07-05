package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("account")
    private AccountInfo account;

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public AccountInfo getAccount() { return account; }

    public static class AccountInfo {
        @SerializedName("_id")
        private String id;
        @SerializedName("email")
        private String email;
        @SerializedName("fullName")
        private String fullName;

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
    }
}
