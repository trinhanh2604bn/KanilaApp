package com.example.frontend.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("registration_channel")
    private String registrationChannel; // "email" or "phone"

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("terms_accepted")
    private boolean termsAccepted;

    @SerializedName("marketing_opt_in")
    private boolean marketingOptIn;

    @SerializedName("guest_session_id")
    private String guestSessionId;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public RegisterRequest(String registrationChannel, String fullName, String email, String phone, 
                           String username, String password,
                           boolean termsAccepted, boolean marketingOptIn, String guestSessionId) {
        this.registrationChannel = registrationChannel;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.termsAccepted = termsAccepted;
        this.marketingOptIn = marketingOptIn;
        this.guestSessionId = guestSessionId;
    }
}
