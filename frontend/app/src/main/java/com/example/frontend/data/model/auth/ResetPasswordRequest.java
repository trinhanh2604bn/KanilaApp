package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("reset_token")
    private String resetToken;
    
    @SerializedName("new_password")
    private String newPassword;
    
    @SerializedName("confirm_password")
    private String confirmPassword;

    public ResetPasswordRequest(String resetToken, String newPassword, String confirmPassword) {
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
