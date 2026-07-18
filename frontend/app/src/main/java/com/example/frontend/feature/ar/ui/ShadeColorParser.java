package com.example.frontend.feature.ar.ui;

import android.graphics.Color;
import android.util.Log;

public class ShadeColorParser {

    private static final String TAG = "ShadeColorParser";
    private static final int DEFAULT_FALLBACK_COLOR = Color.RED;

    /**
     * Parses a hex color string into an integer color safely.
     * Accepts formats like #RRGGBB, #AARRGGBB, lowercase or uppercase.
     * Falls back to the default color if parsing fails.
     *
     * @param hexColor the hex color string to parse
     * @return the parsed integer color, or fallback color if invalid
     */
    public static int parseOrFallback(String hexColor) {
        if (hexColor == null || hexColor.trim().isEmpty()) {
            return DEFAULT_FALLBACK_COLOR;
        }

        String colorToParse = hexColor.trim();
        if (!colorToParse.startsWith("#")) {
            colorToParse = "#" + colorToParse;
        }

        try {
            return Color.parseColor(colorToParse);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid color hex: " + hexColor + ". Falling back to default.");
            return DEFAULT_FALLBACK_COLOR;
        }
    }
}
