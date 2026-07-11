package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {
    @SerializedName("target_type")
    private String targetType; // "email" or "phone"

    @SerializedName("target_value")
    private String targetValue;

    @SerializedName("otp")
    private String otp;

    @SerializedName("purpose")
    private String purpose; // "register", "login", or "reset_password"

    @SerializedName("guest_session_id")
    private String guestSessionId;

    public VerifyOtpRequest(String targetType, String targetValue, String otp, String purpose, String guestSessionId) {
        this.targetType = targetType;
        this.targetValue = targetValue;
        this.otp = otp;
        this.purpose = purpose;
        this.guestSessionId = guestSessionId;
    }
}
