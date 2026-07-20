package com.example.frontend.feature.ar.gpu;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Immutable snapshot of all GPU uniform state for the lipstick renderer.
 *
 * Thread-safety strategy:
 * - Main thread writes via {@link Holder#update(LipGpuRenderState)}.
 * - GL thread reads via {@link Holder#get()}.
 * - {@link AtomicReference} ensures atomic swap, no lock.
 *
 * Shade switching = create new state + atomic swap on main thread.
 * GL thread sees the new state on next frame. No camera restart required.
 */
public final class LipGpuRenderState {

    // ─── Shade color in linear RGB [0,1] ──────────────────────────────────────
    public final float shadeR;
    public final float shadeG;
    public final float shadeB;

    // ─── Blend parameters ─────────────────────────────────────────────────────
    /** Overall opacity of the lipstick (0=invisible, 1=full). */
    public final float opacity;
    /** How much the shade covers the lip texture. */
    public final float coverage;
    /** How much original lip micro-texture shows through (0=none, 1=full). */
    public final float textureRetention;
    /** Additive luminance bias in linear space. */
    public final float brightnessBias;
    /** Saturation multiplier (0=grayscale, 1=natural, >1=vivid). */
    public final float saturation;
    /** Surface roughness (0=mirror, 1=matte). */
    public final float roughness;
    /** Specular highlight contribution (0=none). */
    public final float specularStrength;
    /** Finish type code for shader branching (see LipFinishProfile.FinishType.code()). */
    public final int finishTypeCode;

    // ─── Mask age threshold: if mask is older than this, hide lipstick ─────────
    public final long maxMaskAgeMs;

    private LipGpuRenderState(Builder b) {
        this.shadeR           = b.shadeR;
        this.shadeG           = b.shadeG;
        this.shadeB           = b.shadeB;
        this.opacity          = clamp(b.opacity);
        this.coverage         = clamp(b.coverage);
        this.textureRetention = clamp(b.textureRetention);
        this.brightnessBias   = b.brightnessBias;
        this.saturation       = Math.max(0f, b.saturation);
        this.roughness        = clamp(b.roughness);
        this.specularStrength = clamp(b.specularStrength);
        this.finishTypeCode   = b.finishTypeCode;
        this.maxMaskAgeMs     = b.maxMaskAgeMs > 0 ? b.maxMaskAgeMs : 100L;
    }

    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

    /** Returns a hidden-lipstick state (opacity=0). Used while no face detected. */
    public static LipGpuRenderState hidden() {
        return new Builder()
                .shadeLinearRgb(0f, 0f, 0f)
                .opacity(0f)
                .build();
    }

    // ─── Builder ──────────────────────────────────────────────────────────────

    public static Builder fromProfile(float[] linearRgb, float opacity, LipFinishProfile profile) {
        return new Builder()
                .shadeLinearRgb(linearRgb[0], linearRgb[1], linearRgb[2])
                .opacity(opacity)
                .coverage(profile.coverage)
                .textureRetention(profile.textureRetention)
                .brightnessBias(profile.brightnessBias)
                .saturation(profile.saturation)
                .roughness(profile.roughness)
                .specularStrength(profile.specularStrength)
                .finishTypeCode(profile.finishType.code());
    }

    public static final class Builder {
        private float shadeR = 0.5f, shadeG = 0.1f, shadeB = 0.1f;
        private float opacity = 0.7f;
        private float coverage = 0.7f;
        private float textureRetention = 0.5f;
        private float brightnessBias = 0f;
        private float saturation = 1f;
        private float roughness = 0.5f;
        private float specularStrength = 0.2f;
        private int finishTypeCode = LipFinishProfile.FinishType.MATTE.code();
        private long maxMaskAgeMs = 100L;

        public Builder shadeLinearRgb(float r, float g, float b) { shadeR=r; shadeG=g; shadeB=b; return this; }
        public Builder opacity(float v) { opacity = v; return this; }
        public Builder coverage(float v) { coverage = v; return this; }
        public Builder textureRetention(float v) { textureRetention = v; return this; }
        public Builder brightnessBias(float v) { brightnessBias = v; return this; }
        public Builder saturation(float v) { saturation = v; return this; }
        public Builder roughness(float v) { roughness = v; return this; }
        public Builder specularStrength(float v) { specularStrength = v; return this; }
        public Builder finishTypeCode(int v) { finishTypeCode = v; return this; }
        public Builder maxMaskAgeMs(long v) { maxMaskAgeMs = v; return this; }
        public LipGpuRenderState build() { return new LipGpuRenderState(this); }
    }

    // ─── Thread-safe Holder ───────────────────────────────────────────────────

    /** Atomic holder for sharing render state between main thread and GL thread. */
    public static final class Holder {
        private final AtomicReference<LipGpuRenderState> ref =
                new AtomicReference<>(LipGpuRenderState.hidden());

        public void update(LipGpuRenderState state) {
            ref.set(state);
        }

        public LipGpuRenderState get() {
            return ref.get();
        }
    }
}
