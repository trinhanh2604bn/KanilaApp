package com.example.frontend.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "kanila_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_GUEST_SESSION_ID = "guest_session_id";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveToken(String accessToken) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .apply();
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void saveGuestSession(String guestSessionId) {
        prefs.edit().putString(KEY_GUEST_SESSION_ID, guestSessionId).apply();
    }

    public String getGuestSession() {
        return prefs.getString(KEY_GUEST_SESSION_ID, null);
    }
}
