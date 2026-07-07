package com.example.frontend.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    private static Toast currentToast;
    private static String lastMessage;
    private static long lastShownAt = 0;
    private static final long DEBOUNCE_INTERVAL = 1500; // 1.5 seconds

    public static void show(Context context, String message) {
        showShort(context, message);
    }

    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    public static void showShort(Context context, int resId) {
        if (context == null) return;
        show(context, context.getString(resId), Toast.LENGTH_SHORT);
    }

    private static void show(Context context, String message, int duration) {
        if (context == null || message == null || message.trim().isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        
        // Debounce duplicate messages within 1500ms
        if (message.equals(lastMessage) && (currentTime - lastShownAt < DEBOUNCE_INTERVAL)) {
            return;
        }

        if (currentToast != null) {
            currentToast.cancel();
        }

        lastMessage = message;
        lastShownAt = currentTime;

        currentToast = Toast.makeText(context.getApplicationContext(), message, duration);
        currentToast.show();
    }
}
