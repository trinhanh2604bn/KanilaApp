package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("login_channel")
    private String loginChannel; // "email" or "phone"

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("guest_session_id")
    private String guestSessionId;

    @SerializedName("password")
    private String password;

    public LoginRequest(String loginChannel, String identifier, String password, String guestSessionId) {
        this.loginChannel = loginChannel;
        this.identifier = identifier;
        this.password = password;
        this.guestSessionId = guestSessionId;
    }

    public String getLoginChannel() { return loginChannel; }
    public String getIdentifier() { return identifier; }
    public String getGuestSessionId() { return guestSessionId; }
}
