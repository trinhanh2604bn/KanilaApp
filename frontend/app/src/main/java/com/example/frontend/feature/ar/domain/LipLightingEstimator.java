package com.example.frontend.feature.ar.domain;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.List;

public class LipLightingEstimator {

    public static class LipLightingState {
        public float luminance = 0.5f;
        public float exposureFactor = 1.0f;
        public float saturationFactor = 1.0f;
        public float warmthFactor = 1.0f;
        public boolean lowLight = false;
    }

    private LipLightingState currentState = new LipLightingState();
    private long lastEstimateTime = 0;
    private static final long ESTIMATE_INTERVAL_MS = 1000; // estimate once per second

    public boolean shouldEstimate() {
        return System.currentTimeMillis() - lastEstimateTime >= ESTIMATE_INTERVAL_MS;
    }

    public LipLightingState estimate(Bitmap bitmap, List<LandmarkPoint> lips) {
        if (bitmap == null || lips == null || lips.isEmpty()) {
            return currentState;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEstimateTime < ESTIMATE_INTERVAL_MS) {
            return currentState;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Sample center of lips
        int sumLuminance = 0;
        int count = 0;

        for (LandmarkPoint p : lips) {
            int x = (int) p.x;
            int y = (int) p.y;
            
            if (x >= 0 && x < width && y >= 0 && y < height) {
                int pixel = bitmap.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                
                // Simple perceived luminance
                int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                sumLuminance += lum;
                count++;
            }
        }

        if (count > 0) {
            float avgLuminance = (sumLuminance / (float) count) / 255f;
            
            // Smooth transitions
            float alpha = 0.2f;
            currentState.luminance = currentState.luminance * (1 - alpha) + avgLuminance * alpha;
            
            if (currentState.luminance < 0.3f) {
                currentState.lowLight = true;
                currentState.exposureFactor = 0.8f;
                currentState.saturationFactor = 0.8f;
            } else if (currentState.luminance > 0.7f) {
                currentState.lowLight = false;
                currentState.exposureFactor = 1.2f;
                currentState.saturationFactor = 1.0f;
            } else {
                currentState.lowLight = false;
                currentState.exposureFactor = 1.0f;
                currentState.saturationFactor = 1.0f;
            }
        }

        lastEstimateTime = currentTime;
        return currentState;
    }
    
    public LipLightingState getCurrentState() {
        return currentState;
    }
}
