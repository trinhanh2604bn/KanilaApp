package com.example.frontend.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        String token = tokenManager.getAccessToken();
        if (token != null && !token.trim().isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        String guestSessionId = tokenManager.getGuestSession();
        if (guestSessionId != null && !guestSessionId.trim().isEmpty()) {
            builder.header("X-Guest-Session-Id", guestSessionId);
        }

        return chain.proceed(builder.build());
    }
}