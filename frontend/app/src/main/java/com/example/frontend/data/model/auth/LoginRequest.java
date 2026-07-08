package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("login_channel")
    private String loginChannel; // "email" or "phone"

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("password")
    private String password;

    @SerializedName("guest_session_id")
    private String guestSessionId;

    public LoginRequest(String loginChannel, String identifier, String guestSessionId) {
        this.loginChannel = loginChannel;
        this.identifier = identifier;
        this.guestSessionId = guestSessionId;
    }

    public LoginRequest(String loginChannel, String identifier, String password, String guestSessionId) {
        this.loginChannel = loginChannel;
        this.identifier = identifier;
        this.password = password;
        this.guestSessionId = guestSessionId;
    }

    public String getLoginChannel() { return loginChannel; }
    public String getIdentifier() { return identifier; }
    public String getPassword() { return password; }
    public String getGuestSessionId() { return guestSessionId; }
}
