package com.example.frontend.feature.ar.gpu;

/**
 * Color space conversion utilities for the GPU lip renderer.
 *
 * Provides:
 * 1. Java-side sRGB ↔ linear RGB (float channel, 0-1 range) for unit testing.
 * 2. GLSL function strings to be injected into fragment shader source.
 *
 * All math follows IEC 61966-2-1 (sRGB standard).
 */
public final class GlColorSpaceUtils {

    private GlColorSpaceUtils() {}

    // ─── Java-side conversions (for unit tests) ───────────────────────────────

    /**
     * Convert a single channel from sRGB gamma (0-1) to linear (0-1).
     */
    public static float srgbChannelToLinear(float c) {
        if (c < 0f) return 0f;
        if (c > 1f) return 1f;
        return (c <= 0.04045f) ? (c / 12.92f) : (float) Math.pow((c + 0.055f) / 1.055f, 2.4);
    }

    /**
     * Convert a single channel from linear (0-1) to sRGB gamma (0-1).
     */
    public static float linearChannelToSrgb(float c) {
        if (c < 0f) return 0f;
        if (c > 1f) return 1f;
        return (c <= 0.0031308f) ? (c * 12.92f) : (1.055f * (float) Math.pow(c, 1.0 / 2.4) - 0.055f);
    }

    /**
     * Convert a packed ARGB int (Android Color) from sRGB to linear float[3] {r,g,b}.
     */
    public static float[] argbToLinearRgb(int argb) {
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        return new float[]{srgbChannelToLinear(r), srgbChannelToLinear(g), srgbChannelToLinear(b)};
    }

    /**
     * Rec. 709 / sRGB luminance from linear RGB float[3].
     * Y = dot(color, vec3(0.2126, 0.7152, 0.0722))
     */
    public static float luminance(float[] linearRgb) {
        return 0.2126f * linearRgb[0] + 0.7152f * linearRgb[1] + 0.0722f * linearRgb[2];
    }

    // ─── GLSL source fragments ────────────────────────────────────────────────

    /**
     * GLSL helper functions to inject at the top of the fragment shader.
     * These convert between sRGB and linear color spaces per-pixel.
     */
    public static final String GLSL_COLOR_HELPERS =
            "// sRGB gamma ↔ linear conversions (IEC 61966-2-1)\n" +
            "vec3 srgbToLinear(vec3 c) {\n" +
            "    vec3 lo = c / 12.92;\n" +
            "    vec3 hi = pow((c + 0.055) / 1.055, vec3(2.4));\n" +
            "    return mix(lo, hi, step(0.04045, c));\n" +
            "}\n" +
            "vec3 linearToSrgb(vec3 c) {\n" +
            "    c = clamp(c, 0.0, 1.0);\n" +
            "    vec3 lo = c * 12.92;\n" +
            "    vec3 hi = 1.055 * pow(c, vec3(1.0 / 2.4)) - 0.055;\n" +
            "    return mix(lo, hi, step(0.0031308, c));\n" +
            "}\n" +
            "float luminance(vec3 linear) {\n" +
            "    return dot(linear, vec3(0.2126, 0.7152, 0.0722));\n" +
            "}\n";
}
