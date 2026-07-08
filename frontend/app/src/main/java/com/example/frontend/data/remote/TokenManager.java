package com.example.frontend.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "kanila_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_GUEST_SESSION_ID = "guest_session_id";
    private static final String KEY_CHATBOT_SESSION_ID = "chatbot_session_id";

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
        String accessToken = getAccessToken();
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    public void saveGuestSession(String guestSessionId) {
        if (guestSessionId == null || guestSessionId.trim().isEmpty()) return;

        prefs.edit()
                .putString(KEY_GUEST_SESSION_ID, guestSessionId)
                .apply();
    }

    public String getGuestSession() {
        return prefs.getString(KEY_GUEST_SESSION_ID, null);
    }

    public boolean hasGuestSession() {
        String guestSessionId = getGuestSession();
        return guestSessionId != null && !guestSessionId.trim().isEmpty();
    }

    public void clearGuestSession() {
        prefs.edit()
                .remove(KEY_GUEST_SESSION_ID)
                .apply();
    }

    public void clearAll() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_GUEST_SESSION_ID)
                .remove(KEY_CHATBOT_SESSION_ID)
                .apply();
    }

    public void saveChatbotSession(String sessionId) {
        prefs.edit().putString(KEY_CHATBOT_SESSION_ID, sessionId).apply();
    }

    public String getChatbotSession() {
        return prefs.getString(KEY_CHATBOT_SESSION_ID, null);
    }

    public void clearChatbotSession() {
        prefs.edit().remove(KEY_CHATBOT_SESSION_ID).apply();
    }
}