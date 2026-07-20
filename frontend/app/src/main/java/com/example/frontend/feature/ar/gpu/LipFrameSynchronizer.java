package com.example.frontend.feature.ar.gpu;

import com.example.frontend.feature.ar.domain.LandmarkPoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Synchronizes lip mask data (produced by ML Kit analysis thread) with
 * camera frames (consumed by GL render thread).
 *
 * ImageAnalysis and Preview run independently; ML Kit results arrive at a
 * different cadence than camera preview frames. This class keeps the most
 * recent valid mask and exposes it safely to the GL thread.
 *
 * Stale-mask policy:
 *  - If the mask is older than {@code maxMaskAgeMs}, return null (GL renders
 *    without lipstick rather than letting it drift off the lips).
 *  - Default threshold: 100 ms. Calibrate on real device after measurement.
 *
 * Thread-safety: all public methods are thread-safe via AtomicReference.
 */
public final class LipFrameSynchronizer {

    private static final long DEFAULT_MAX_MASK_AGE_MS = 100L;

    /** Immutable snapshot of a mask frame. */
    public static final class MaskFrame {
        /** Landmark points (already smoothed + view-space mapped). */
        public final List<LandmarkPoint> landmarks;
        /** Wall-clock time when these landmarks were captured. */
        public final long timestampMs;
        /** True if a face was detected; false = no face (mask should be hidden). */
        public final boolean hasFace;

        public MaskFrame(List<LandmarkPoint> landmarks, long timestampMs, boolean hasFace) {
            this.landmarks   = landmarks;
            this.timestampMs = timestampMs;
            this.hasFace     = hasFace;
        }
    }

    private final AtomicReference<MaskFrame> latestMask = new AtomicReference<>(null);
    private volatile long maxMaskAgeMs = DEFAULT_MAX_MASK_AGE_MS;

    /** Called from analysis/main thread when new landmarks arrive. */
    public void putMask(List<LandmarkPoint> landmarks, long timestampMs, boolean hasFace) {
        latestMask.set(new MaskFrame(landmarks, timestampMs, hasFace));
    }

    /** Called from analysis/main thread when face is lost. */
    public void clearMask() {
        long ts = System.currentTimeMillis();
        latestMask.set(new MaskFrame(null, ts, false));
    }

    /**
     * Called from GL render thread to get the current mask.
     *
     * Returns null if:
     *  - No mask has been received yet.
     *  - Most recent mask is older than {@code maxMaskAgeMs}.
     *
     * Returns a {@link MaskFrame} with {@code hasFace=false} if face was lost
     * but within the age threshold (allows smooth fade-out).
     */
    public MaskFrame getMaskForRender() {
        MaskFrame frame = latestMask.get();
        if (frame == null) return null;
        long age = System.currentTimeMillis() - frame.timestampMs;
        if (age > maxMaskAgeMs) return null; // stale — hide lipstick
        return frame;
    }

    /**
     * Adjust stale-mask threshold.
     * Start with ~100ms; tune down if lip drift is visible, tune up if flicker occurs.
     */
    public void setMaxMaskAgeMs(long maxMaskAgeMs) {
        this.maxMaskAgeMs = Math.max(16L, maxMaskAgeMs); // min 1 frame
    }

    public long getMaxMaskAgeMs() {
        return maxMaskAgeMs;
    }

    /** Returns age of the last mask in ms, or Long.MAX_VALUE if no mask. */
    public long currentMaskAgeMs() {
        MaskFrame frame = latestMask.get();
        if (frame == null) return Long.MAX_VALUE;
        return System.currentTimeMillis() - frame.timestampMs;
    }
}
