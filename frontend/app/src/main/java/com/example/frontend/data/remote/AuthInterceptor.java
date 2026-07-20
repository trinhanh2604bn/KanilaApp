package com.example.frontend.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AuthInterceptor implements Interceptor {
    public static final String ACTION_SESSION_EXPIRED = "com.example.frontend.ACTION_SESSION_EXPIRED";
    
    private final TokenManager tokenManager;
    private final Context context;

    public AuthInterceptor(Context context, TokenManager tokenManager) {
        this.context = context.getApplicationContext();
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

        // Always send guest session if available, it's used for merging carts after login
        String guestSessionId = tokenManager.getGuestSession();
        if (guestSessionId != null && !guestSessionId.trim().isEmpty()) {
            builder.header("X-Guest-Session-Id", guestSessionId);
        }

        Response response = chain.proceed(builder.build());

        if (response.code() == 401) {
            tokenManager.clearToken();
            Intent intent = new Intent(ACTION_SESSION_EXPIRED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        return response;
    }
}
