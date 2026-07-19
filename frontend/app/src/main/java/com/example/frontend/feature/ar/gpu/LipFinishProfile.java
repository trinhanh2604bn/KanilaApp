package com.example.frontend.feature.ar.gpu;

/**
 * Default GPU render parameters for each lipstick finish type.
 *
 * Each finish is a named preset of uniforms sent to the fragment shader.
 * Individual ArShade variants may override any field via optional backend fields.
 *
 * All float values are in [0, 1].
 */
public final class LipFinishProfile {

    public enum FinishType {
        MATTE, SATIN, TINT, GLOSS;

        public static FinishType fromString(String s) {
            if (s == null) return MATTE;
            try { return valueOf(s.toUpperCase().trim()); }
            catch (IllegalArgumentException e) { return MATTE; }
        }

        /** Integer code sent as uniform uFinishType to shader. */
        public int code() {
            switch (this) {
                case TINT:  return 0;
                case SATIN: return 1;
                case GLOSS: return 2;
                case MATTE:
                default:    return 3;
            }
        }
    }

    // ─── Immutable parameters ─────────────────────────────────────────────────

    /** How much the shade color covers the lip (0=none, 1=full). */
    public final float coverage;
    /** Fraction of original lip micro-texture retained (0=none, 1=full). */
    public final float textureRetention;
    /** Saturation multiplier applied to the shade in linear space. */
    public final float saturation;
    /** Specular highlight strength (0=none). */
    public final float specularStrength;
    /** Surface roughness (0=mirror, 1=fully matte). */
    public final float roughness;
    /** Additive luminance bias (positive = brighter). */
    public final float brightnessBias;
    /** Edge feather radius ratio (used on CPU mask side). */
    public final float edgeFeather;
    /** Finish type code for shader. */
    public final FinishType finishType;

    private LipFinishProfile(
            FinishType finishType,
            float coverage,
            float textureRetention,
            float saturation,
            float specularStrength,
            float roughness,
            float brightnessBias,
            float edgeFeather) {
        this.finishType       = finishType;
        this.coverage         = clamp(coverage);
        this.textureRetention = clamp(textureRetention);
        this.saturation       = clamp(saturation);
        this.specularStrength = clamp(specularStrength);
        this.roughness        = clamp(roughness);
        this.brightnessBias   = brightnessBias; // allow small negative
        this.edgeFeather      = clamp(edgeFeather);
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    // ─── Default profiles ─────────────────────────────────────────────────────

    /**
     * TINT: Sheer coverage, high texture retention, very soft edge.
     * Lip's natural color strongly influences final result.
     */
    public static final LipFinishProfile TINT = new LipFinishProfile(
            FinishType.TINT,
            /*coverage*/         0.38f,
            /*textureRetention*/ 0.90f,
            /*saturation*/       0.75f,
            /*specularStrength*/ 0.05f,
            /*roughness*/        0.90f,
            /*brightnessBias*/   0.00f,
            /*edgeFeather*/      0.28f
    );

    /**
     * SATIN: Balanced coverage, preserves natural luminance and highlights.
     * Light specular for a healthy sheen.
     */
    public static final LipFinishProfile SATIN = new LipFinishProfile(
            FinishType.SATIN,
            /*coverage*/         0.68f,
            /*textureRetention*/ 0.60f,
            /*saturation*/       0.95f,
            /*specularStrength*/ 0.28f,
            /*roughness*/        0.50f,
            /*brightnessBias*/   0.01f,
            /*edgeFeather*/      0.14f
    );

    /**
     * MATTE: Higher coverage, stable saturation, no shiny spots.
     * Still retains enough texture to avoid flat-block look.
     */
    public static final LipFinishProfile MATTE = new LipFinishProfile(
            FinishType.MATTE,
            /*coverage*/         0.82f,
            /*textureRetention*/ 0.38f,
            /*saturation*/       0.92f,
            /*specularStrength*/ 0.04f,
            /*roughness*/        0.92f,
            /*brightnessBias*/  -0.01f,
            /*edgeFeather*/      0.10f
    );

    /**
     * GLOSS: Moderate coverage, strong specular derived from camera highlights.
     * Does NOT paint a fixed white streak — uses actual highlight pixels.
     */
    public static final LipFinishProfile GLOSS = new LipFinishProfile(
            FinishType.GLOSS,
            /*coverage*/         0.58f,
            /*textureRetention*/ 0.72f,
            /*saturation*/       0.88f,
            /*specularStrength*/ 0.72f,
            /*roughness*/        0.10f,
            /*brightnessBias*/   0.04f,
            /*edgeFeather*/      0.16f
    );

    // ─── Factory ──────────────────────────────────────────────────────────────

    public static LipFinishProfile fromFinishType(FinishType type) {
        if (type == null) return MATTE;
        switch (type) {
            case TINT:  return TINT;
            case SATIN: return SATIN;
            case GLOSS: return GLOSS;
            case MATTE:
            default:    return MATTE;
        }
    }

    public static LipFinishProfile fromString(String finishTypeName) {
        return fromFinishType(FinishType.fromString(finishTypeName));
    }

    /**
     * Returns a copy with overridden parameters.
     * Any value < 0 means "use profile default".
     */
    public LipFinishProfile withOverrides(
            float coverage, float textureRetention, float saturation,
            float specularStrength, float roughness, float brightnessBias) {
        return new LipFinishProfile(
                this.finishType,
                coverage         >= 0 ? coverage         : this.coverage,
                textureRetention >= 0 ? textureRetention : this.textureRetention,
                saturation       >= 0 ? saturation       : this.saturation,
                specularStrength >= 0 ? specularStrength : this.specularStrength,
                roughness        >= 0 ? roughness        : this.roughness,
                brightnessBias   != Float.MIN_VALUE ? brightnessBias : this.brightnessBias,
                this.edgeFeather
        );
    }
}
