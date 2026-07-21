package com.example.frontend.feature.ar.domain;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

/**
 * Estimates ambient luminance for the GPU renderer without requiring a Bitmap.
 *
 * In GPU mode, PreviewView is hidden so previewView.getBitmap() is unavailable.
 * This estimator maintains a running luminance estimate that can be fed from two sources:
 *
 *  1. {@link #feedBitmap(android.graphics.Bitmap, List)} — same as before (Canvas mode).
 *  2. {@link #feedLuminanceHint(float)} — from any external source (e.g., sensor, shader readback).
 *
 * The GPU shader already adjusts to camera luminance per-pixel via the textureRetention
 * and luminance-preserving blend in LipstickShaderProgram. This class is only used
 * to adjust the global opacity multiplier across shades.
 *
 * In GPU mode, the shader handles micro-luminance adjustment per-pixel.
 * Global luminance only affects opacity; default (1.0) is fine without estimation.
 */
public class GpuLightingEstimator {

    public static class State {
        /** Normalised luminance 0-1 (0.5 = neutral). */
        public float luminance = 0.5f;
        /**
         * Opacity multiplier: 0.8 in low light, 1.2 in bright, 1.0 neutral.
         * Multiply with base opacity when setting GPU render state.
         */
        public float opacityFactor = 1.0f;
        public boolean lowLight = false;
    }

    private final State state = new State();
    private long lastUpdateMs = 0L;
    private static final long UPDATE_INTERVAL_MS = 1000L;

    /** Feed a direct luminance estimate (0-1). Rate-limited to 1 Hz. */
    public void feedLuminanceHint(float luminance) {
        long now = System.currentTimeMillis();
        if (now - lastUpdateMs < UPDATE_INTERVAL_MS) return;
        lastUpdateMs = now;
        update(luminance);
    }

    /**
     * Feed from a Bitmap (fallback / Canvas mode compat).
     * Samples a small 4×4 grid from the face center area.
     */
    public void feedBitmap(android.graphics.Bitmap bitmap, List<LandmarkPoint> landmarks) {
        if (bitmap == null || landmarks == null || landmarks.isEmpty()) return;
        long now = System.currentTimeMillis();
        if (now - lastUpdateMs < UPDATE_INTERVAL_MS) return;
        lastUpdateMs = now;

        // Sample nose tip (landmark 1) as approximate face center
        float cx = bitmap.getWidth() * 0.5f;
        float cy = bitmap.getHeight() * 0.5f;
        if (landmarks.size() > 1) {
            cx = landmarks.get(1).x * bitmap.getWidth() / Math.max(1, bitmap.getWidth());
            cy = landmarks.get(1).y * bitmap.getHeight() / Math.max(1, bitmap.getHeight());
        }

        long sum = 0;
        int count = 0;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                int px = (int) cx + dx * 10;
                int py = (int) cy + dy * 10;
                if (px >= 0 && px < w && py >= 0 && py < h) {
                    int pixel = bitmap.getPixel(px, py);
                    int r = android.graphics.Color.red(pixel);
                    int g = android.graphics.Color.green(pixel);
                    int b = android.graphics.Color.blue(pixel);
                    sum += (int) (0.299f * r + 0.587f * g + 0.114f * b);
                    count++;
                }
            }
        }
        if (count > 0) {
            update((sum / (float) count) / 255f);
        }
    }

    private void update(float raw) {
        float alpha = 0.2f; // EMA smoothing
        state.luminance = state.luminance * (1f - alpha) + raw * alpha;

        if (state.luminance < 0.3f) {
            state.lowLight = true;
            state.opacityFactor = 0.8f;
        } else if (state.luminance > 0.7f) {
            state.lowLight = false;
            state.opacityFactor = 1.15f;
        } else {
            state.lowLight = false;
            state.opacityFactor = 1.0f;
        }
    }

    public State getState() { return state; }

    /** Returns true — we never need a bitmap in GPU mode (always accept hints). */
    public boolean shouldUpdate() {
        return System.currentTimeMillis() - lastUpdateMs >= UPDATE_INTERVAL_MS;
    }
}
