package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("verification_required")
    private boolean verificationRequired;

    @SerializedName("reset_token")
    private String resetToken;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public boolean isVerificationRequired() { return verificationRequired; }
    public String getResetToken() { return resetToken; }
}
