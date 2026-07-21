package com.example.frontend.feature.ar.gpu;

import android.view.View;

import androidx.camera.core.Preview;

import com.example.frontend.BuildConfig;
import com.example.frontend.feature.ar.data.ArShade;
import com.example.frontend.feature.ar.domain.LandmarkPoint;
import com.example.frontend.feature.ar.ui.LipColorRenderer;
import com.example.frontend.feature.ar.ui.LipOverlayView;
import com.example.frontend.feature.ar.domain.LipRenderProfile;

import java.util.List;

/**
 * Factory and unified interface for the AR lip renderer.
 *
 * Selects between Canvas (legacy) and GPU (new) renderer based on
 * {@code BuildConfig.AR_LIP_RENDERER}:
 *  - "GPU"    → LipGlSurfaceView (per-pixel compositing in shader)
 *  - "CANVAS" → LipOverlayView (current Canvas overlay)
 *
 * Debug builds can toggle at runtime via {@link #switchRenderer(boolean, LipGlSurfaceView, LipOverlayView)}.
 *
 * Neither ProductVariant, cart, nor analytics logic is changed.
 * Only the visual rendering path changes.
 */
public final class ArRendererFactory {

    public static final String RENDERER_GPU    = "GPU";
    public static final String RENDERER_CANVAS = "CANVAS";

    private ArRendererFactory() {}

    /** Returns true if GPU renderer is configured. */
    public static boolean isGpuEnabled() {
        try {
            return RENDERER_GPU.equals(BuildConfig.AR_LIP_RENDERER);
        } catch (Exception e) {
            // BuildConfig field may not exist in older builds — fallback safe
            return false;
        }
    }

    /**
     * Called when the user selects a shade.
     *
     * GPU path: converts hex → linear RGB, builds LipGpuRenderState from profile, updates atomically.
     * Canvas path: calls existing LipColorRenderer (unchanged).
     *
     * @return the Paint for Canvas path (null for GPU path).
     */
    public static android.graphics.Paint applyShade(
            ArShade shade,
            LipGlSurfaceView gpuView,
            LipOverlayView canvasView,
            LipColorRenderer canvasRenderer,
            LipColorRenderer.FinishType finishType,
            float opacity,
            LipRenderProfile canvasProfile) {

        if (isGpuEnabled() && gpuView != null) {
            // ── GPU path ──
            float[] linearRgb = parseHexToLinearRgb(shade.getShadeHex());
            LipFinishProfile profile = LipFinishProfile.fromString(shade.getFinishType());

            // Override from optional backend fields (all optional, validated 0-1)
            float coverage         = getOptionalFloat(shade.getCoverage(),         profile.coverage);
            float textureRetention = getOptionalFloat(shade.getTextureRetention(),  profile.textureRetention);
            float brightnessBias   = getOptionalFloat(shade.getBrightnessBias(),    profile.brightnessBias);
            float sat              = getOptionalFloat(shade.getSaturation(),         profile.saturation);
            float roughness        = getOptionalFloat(shade.getRoughness(),          profile.roughness);
            float specular         = getOptionalFloat(shade.getSpecularStrength(),   profile.specularStrength);

            LipGpuRenderState state = LipGpuRenderState.fromProfile(linearRgb, opacity, profile)
                    .coverage(coverage)
                    .textureRetention(textureRetention)
                    .brightnessBias(brightnessBias)
                    .saturation(sat)
                    .roughness(roughness)
                    .specularStrength(specular)
                    .build();

            gpuView.setRenderState(state);
            return null; // GPU doesn't use Paint
        } else {
            // ── Canvas path (unchanged from previous implementation) ──
            return canvasRenderer.getLipPaint(shade.getShadeHex(), finishType, opacity, canvasProfile);
        }
    }

    /**
     * Switch between GPU and Canvas renderer at runtime (debug builds only).
     * Shows/hides the respective views.
     */
    public static void switchRenderer(boolean useGpu, LipGlSurfaceView gpuView, LipOverlayView canvasView) {
        if (gpuView != null)   gpuView.setVisibility(useGpu ? View.VISIBLE : View.GONE);
        if (canvasView != null) canvasView.setVisibility(useGpu ? View.GONE  : View.VISIBLE);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Parse #RRGGBB / #AARRGGBB hex string → linear RGB float[3].
     * Pure Java implementation (no android.graphics.Color) for JVM unit test compatibility.
     * Falls back to Kanila brand dark rose on any parse error.
     */
    static float[] parseHexToLinearRgb(String hex) {
        try {
            if (hex == null || hex.isEmpty()) throw new IllegalArgumentException("null hex");
            String h = hex.startsWith("#") ? hex.substring(1) : hex;
            int argb;
            if (h.length() == 6) {
                argb = (int) (0xFF000000L | Long.parseLong(h, 16));
            } else if (h.length() == 8) {
                argb = (int) Long.parseLong(h, 16);
            } else {
                throw new IllegalArgumentException("Unexpected hex length: " + h.length());
            }
            return GlColorSpaceUtils.argbToLinearRgb(argb);
        } catch (Exception e) {
            // Fallback: Kanila brand dark rose (#D13444) in linear RGB
            return GlColorSpaceUtils.argbToLinearRgb(0xFFD13444);
        }
    }

    /** Returns the value if valid (0-1), otherwise the profile default. */
    private static float getOptionalFloat(Float value, float profileDefault) {
        if (value == null || value < 0f || value > 1f) return profileDefault;
        return value;
    }
}
