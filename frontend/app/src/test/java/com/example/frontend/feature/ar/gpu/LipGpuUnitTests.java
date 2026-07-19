package com.example.frontend.feature.ar.gpu;

import static org.junit.Assert.*;

import com.example.frontend.feature.ar.domain.LandmarkPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for GPU renderer pipeline.
 * These run on JVM (no Android device required).
 *
 * Covers:
 *  - sRGB ↔ linear RGB conversion
 *  - Luminance calculation
 *  - Shade luminance adjustment logic (Java simulation)
 *  - LipFinishProfile defaults
 *  - LipGpuRenderState builder + thread-safe holder
 *  - LipFrameSynchronizer stale mask logic
 *  - ArRendererFactory.parseHexToLinearRgb
 */
public class LipGpuUnitTests {

    private static final float DELTA = 0.001f;

    // ─── sRGB ↔ linear RGB ───────────────────────────────────────────────────

    @Test
    public void srgbToLinear_black_is_zero() {
        assertEquals(0f, GlColorSpaceUtils.srgbChannelToLinear(0f), DELTA);
    }

    @Test
    public void srgbToLinear_white_is_one() {
        assertEquals(1f, GlColorSpaceUtils.srgbChannelToLinear(1f), DELTA);
    }

    @Test
    public void srgbToLinear_midGray_correct() {
        // 0.5 sRGB ≈ 0.214 linear (standard value)
        float linear = GlColorSpaceUtils.srgbChannelToLinear(0.5f);
        assertEquals(0.214f, linear, 0.005f);
    }

    @Test
    public void linearToSrgb_black_is_zero() {
        assertEquals(0f, GlColorSpaceUtils.linearChannelToSrgb(0f), DELTA);
    }

    @Test
    public void linearToSrgb_white_is_one() {
        assertEquals(1f, GlColorSpaceUtils.linearChannelToSrgb(1f), DELTA);
    }

    @Test
    public void srgbLinearRoundTrip() {
        // Converting sRGB → linear → sRGB must recover original within tolerance
        for (float c : new float[]{0.0f, 0.1f, 0.25f, 0.5f, 0.75f, 1.0f}) {
            float linear = GlColorSpaceUtils.srgbChannelToLinear(c);
            float back   = GlColorSpaceUtils.linearChannelToSrgb(linear);
            assertEquals("Round-trip failed for c=" + c, c, back, 0.001f);
        }
    }

    @Test
    public void srgbToLinear_clampsNegative() {
        assertEquals(0f, GlColorSpaceUtils.srgbChannelToLinear(-1f), DELTA);
    }

    @Test
    public void srgbToLinear_clampsAboveOne() {
        assertEquals(1f, GlColorSpaceUtils.srgbChannelToLinear(2f), DELTA);
    }

    // ─── Luminance ────────────────────────────────────────────────────────────

    @Test
    public void luminance_black_is_zero() {
        assertEquals(0f, GlColorSpaceUtils.luminance(new float[]{0f, 0f, 0f}), DELTA);
    }

    @Test
    public void luminance_white_is_one() {
        assertEquals(1f, GlColorSpaceUtils.luminance(new float[]{1f, 1f, 1f}), DELTA);
    }

    @Test
    public void luminance_coefficients_sum_to_one() {
        // 0.2126 + 0.7152 + 0.0722 = 1.0
        float sum = 0.2126f + 0.7152f + 0.0722f;
        assertEquals(1.0f, sum, DELTA);
    }

    @Test
    public void luminance_pure_green_highest() {
        float greenLuma = GlColorSpaceUtils.luminance(new float[]{0f, 1f, 0f});
        float redLuma   = GlColorSpaceUtils.luminance(new float[]{1f, 0f, 0f});
        float blueLuma  = GlColorSpaceUtils.luminance(new float[]{0f, 0f, 1f});
        assertTrue("Green should have highest luminance", greenLuma > redLuma);
        assertTrue("Red should have higher luma than blue", redLuma > blueLuma);
    }

    // ─── LipFinishProfile ─────────────────────────────────────────────────────

    @Test
    public void finishProfile_matte_has_high_coverage() {
        assertTrue(LipFinishProfile.MATTE.coverage >= 0.75f);
    }

    @Test
    public void finishProfile_tint_has_low_coverage() {
        assertTrue(LipFinishProfile.TINT.coverage <= 0.45f);
    }

    @Test
    public void finishProfile_gloss_has_high_specular() {
        assertTrue(LipFinishProfile.GLOSS.specularStrength >= 0.5f);
    }

    @Test
    public void finishProfile_matte_has_low_specular() {
        assertTrue(LipFinishProfile.MATTE.specularStrength <= 0.1f);
    }

    @Test
    public void finishProfile_allValues_inRange() {
        LipFinishProfile[] profiles = {
                LipFinishProfile.MATTE, LipFinishProfile.SATIN,
                LipFinishProfile.TINT,  LipFinishProfile.GLOSS
        };
        for (LipFinishProfile p : profiles) {
            assertInRange("coverage",         p.coverage);
            assertInRange("textureRetention", p.textureRetention);
            assertInRange("saturation",       p.saturation);
            assertInRange("specularStrength", p.specularStrength);
            assertInRange("roughness",        p.roughness);
            assertInRange("edgeFeather",      p.edgeFeather);
        }
    }

    @Test
    public void finishProfile_fromString_null_returnsMatte() {
        assertSame(LipFinishProfile.MATTE, LipFinishProfile.fromString(null));
    }

    @Test
    public void finishProfile_fromString_invalid_returnsMatte() {
        assertSame(LipFinishProfile.MATTE, LipFinishProfile.fromString("UNKNOWN_FINISH"));
    }

    @Test
    public void finishProfile_fromString_caseInsensitive() {
        assertSame(LipFinishProfile.GLOSS, LipFinishProfile.fromString("gloss"));
        assertSame(LipFinishProfile.TINT,  LipFinishProfile.fromString("tint"));
    }

    // ─── LipGpuRenderState ────────────────────────────────────────────────────

    @Test
    public void renderState_clamps_opacity_to_one() {
        LipGpuRenderState state = new LipGpuRenderState.Builder()
                .opacity(5.0f)
                .build();
        assertEquals(1.0f, state.opacity, DELTA);
    }

    @Test
    public void renderState_clamps_opacity_to_zero() {
        LipGpuRenderState state = new LipGpuRenderState.Builder()
                .opacity(-1.0f)
                .build();
        assertEquals(0.0f, state.opacity, DELTA);
    }

    @Test
    public void renderState_holder_atomicUpdate() {
        LipGpuRenderState.Holder holder = new LipGpuRenderState.Holder();
        // Initial state is hidden (opacity=0)
        assertEquals(0f, holder.get().opacity, DELTA);

        LipGpuRenderState newState = new LipGpuRenderState.Builder()
                .opacity(0.8f)
                .build();
        holder.update(newState);
        assertEquals(0.8f, holder.get().opacity, DELTA);
    }

    @Test
    public void renderState_hidden_has_zero_opacity() {
        assertEquals(0f, LipGpuRenderState.hidden().opacity, DELTA);
    }

    // ─── LipFrameSynchronizer ────────────────────────────────────────────────

    @Test
    public void frameSynchronizer_noMask_returnsNull() {
        LipFrameSynchronizer sync = new LipFrameSynchronizer();
        assertNull(sync.getMaskForRender());
    }

    @Test
    public void frameSynchronizer_freshMask_returned() {
        LipFrameSynchronizer sync = new LipFrameSynchronizer();
        sync.setMaxMaskAgeMs(500L);
        sync.putMask(new ArrayList<>(), System.currentTimeMillis(), true);
        assertNotNull(sync.getMaskForRender());
    }

    @Test
    public void frameSynchronizer_staleMask_returnsNull() throws InterruptedException {
        LipFrameSynchronizer sync = new LipFrameSynchronizer();
        sync.setMaxMaskAgeMs(50L); // 50ms threshold
        sync.putMask(new ArrayList<>(), System.currentTimeMillis(), true);
        Thread.sleep(80); // wait until stale
        assertNull(sync.getMaskForRender());
    }

    @Test
    public void frameSynchronizer_clearMask_hidesFace() {
        LipFrameSynchronizer sync = new LipFrameSynchronizer();
        sync.setMaxMaskAgeMs(500L);
        sync.putMask(new ArrayList<>(), System.currentTimeMillis(), true);
        sync.clearMask();
        LipFrameSynchronizer.MaskFrame frame = sync.getMaskForRender();
        // Frame may be non-null but hasFace=false
        if (frame != null) assertFalse(frame.hasFace);
    }

    @Test
    public void frameSynchronizer_setMaxMaskAgeMs_minimumIs16() {
        LipFrameSynchronizer sync = new LipFrameSynchronizer();
        sync.setMaxMaskAgeMs(0L);
        assertTrue(sync.getMaxMaskAgeMs() >= 16L);
    }

    // ─── ArRendererFactory hex parsing ───────────────────────────────────────

    @Test
    public void parseHex_red_correct() {
        float[] linear = ArRendererFactory.parseHexToLinearRgb("#FF0000");
        // Pure red sRGB → linear red ≈ 1.0
        assertEquals(1.0f, linear[0], 0.01f);
        assertEquals(0.0f, linear[1], DELTA);
        assertEquals(0.0f, linear[2], DELTA);
    }

    @Test
    public void parseHex_black_correct() {
        float[] linear = ArRendererFactory.parseHexToLinearRgb("#000000");
        assertEquals(0f, linear[0], DELTA);
        assertEquals(0f, linear[1], DELTA);
        assertEquals(0f, linear[2], DELTA);
    }

    @Test
    public void parseHex_invalid_fallsBack() {
        // Should not throw, should return some non-null value
        float[] linear = ArRendererFactory.parseHexToLinearRgb("not_a_color");
        assertNotNull(linear);
        assertEquals(3, linear.length);
    }

    @Test
    public void parseHex_null_fallsBack() {
        float[] linear = ArRendererFactory.parseHexToLinearRgb(null);
        assertNotNull(linear);
        assertEquals(3, linear.length);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void assertInRange(String name, float value) {
        assertTrue(name + " out of [0,1]: " + value, value >= 0f && value <= 1f);
    }
}
