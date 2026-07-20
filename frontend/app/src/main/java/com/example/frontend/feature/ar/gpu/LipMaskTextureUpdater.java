package com.example.frontend.feature.ar.gpu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import com.example.frontend.feature.ar.domain.LandmarkPoint;
import com.example.frontend.feature.ar.domain.LipGeometry;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Generates the grayscale lip mask from ML Kit landmarks and queues it for
 * upload to {@link LipMaskTexture} on the GL thread.
 *
 * Runs entirely on the analysis/main thread — never on the GL thread.
 *
 * Performance rules:
 *  - Bitmap, ByteBuffer, Canvas, and Paint are created once and reused.
 *  - Path objects are reset (not re-allocated) per frame.
 *  - The finished ByteBuffer is posted atomically for the GL thread to pick up.
 *
 * Mask strategy:
 *  - Outer lip region: full coverage.
 *  - Inner mouth (teeth/tongue): zero coverage via Path.Op.DIFFERENCE.
 *  - Edge feather: paint mask with radial gradient NORMAL outer ring → 0 at boundary.
 *  - Lip corners: reduced coverage via scaled gradient.
 *  - Coverage gradient: consistent with LipFinishProfile.edgeFeather.
 */
public final class LipMaskTextureUpdater {

    /** Mask texture dimensions. Intentionally small to keep upload fast. */
    public static final int MASK_WIDTH  = 256;
    public static final int MASK_HEIGHT = 256;

    // ─── Reusable CPU objects (not thread-shared, only analysis thread) ───────
    private final Bitmap  maskBitmap;
    private final Canvas  maskCanvas;
    private final Paint   fillPaint;
    private final Paint   clearPaint;
    private final Path    outerPath = new Path();
    private final Path    innerPath = new Path();
    private final Path    lipPath   = new Path();

    // ─── Atomic handoff to GL thread ──────────────────────────────────────────
    private final AtomicReference<ByteBuffer> pendingBuffer = new AtomicReference<>(null);
    private final AtomicBoolean hasNewData = new AtomicBoolean(false);

    /** Preallocated buffer — reused every frame. */
    private final ByteBuffer pixelBuffer;

    /** Configurable edge feather radius as fraction of lip bounding box. */
    private float edgeFeatherRatio = 0.12f;

    public LipMaskTextureUpdater() {
        maskBitmap  = Bitmap.createBitmap(MASK_WIDTH, MASK_HEIGHT, Bitmap.Config.ARGB_8888);
        maskCanvas  = new Canvas(maskBitmap);

        fillPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);

        clearPaint  = new Paint();
        clearPaint.setColor(Color.TRANSPARENT);
        clearPaint.setXfermode(new android.graphics.PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        pixelBuffer = ByteBuffer.allocateDirect(MASK_WIDTH * MASK_HEIGHT);
    }

    /**
     * Called from analysis/main thread with new smoothed, view-space landmarks
     * and the view dimensions for coordinate normalization.
     *
     * @param landmarks  smoothed, view-coordinate landmark points
     * @param viewWidth  PreviewView width in pixels
     * @param viewHeight PreviewView height in pixels
     * @param feather    edge feather ratio from active finish profile
     */
    public void update(List<LandmarkPoint> landmarks, int viewWidth, int viewHeight, float feather) {
        if (!LipGeometry.isValidFaceMesh(landmarks) || viewWidth <= 0 || viewHeight <= 0) {
            // Post a clear mask
            clearMask();
            return;
        }

        this.edgeFeatherRatio = Math.max(0.05f, feather);

        // 1. Clear the bitmap
        maskCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // 2. Build outer lip path in view-space, then scale into mask-space
        buildPath(outerPath, landmarks, LipGeometry.OUTER_UPPER_LIP, true);
        buildPath(outerPath, landmarks, LipGeometry.OUTER_LOWER_LIP, false);
        outerPath.close();

        buildPath(innerPath, landmarks, LipGeometry.INNER_UPPER_LIP, true);
        buildPath(innerPath, landmarks, LipGeometry.INNER_LOWER_LIP, false);
        innerPath.close();

        // 3. Difference outer - inner (excludes teeth/tongue)
        lipPath.reset();
        lipPath.op(outerPath, innerPath, Path.Op.DIFFERENCE);

        // 4. Scale path from view-space → mask-space
        android.graphics.Matrix scaleMatrix = new android.graphics.Matrix();
        scaleMatrix.setScale((float) MASK_WIDTH / viewWidth, (float) MASK_HEIGHT / viewHeight);
        lipPath.transform(scaleMatrix);

        // 5. Draw filled lip region (white)
        maskCanvas.drawPath(lipPath, fillPaint);

        // 6. Feather edges: erode boundary with transparent gradient ring
        applyEdgeFeather(lipPath);

        // 7. Convert ARGB_8888 alpha channel → single-channel grayscale ByteBuffer
        extractAlphaToBuffer();

        // 8. Post buffer for GL thread
        pendingBuffer.set(pixelBuffer);
        hasNewData.set(true);
    }

    /** Clears the mask (posts zeroed buffer). */
    public void clearMask() {
        pixelBuffer.clear();
        for (int i = 0; i < MASK_WIDTH * MASK_HEIGHT; i++) pixelBuffer.put(i, (byte) 0);
        pendingBuffer.set(pixelBuffer);
        hasNewData.set(true);
    }

    /**
     * Called from GL thread to check and consume pending mask data.
     * Returns the ByteBuffer to upload, or null if nothing new.
     */
    public ByteBuffer consumePendingBuffer() {
        if (!hasNewData.compareAndSet(true, false)) return null;
        return pendingBuffer.get();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void buildPath(Path path, List<LandmarkPoint> pts, int[] indices, boolean isFirst) {
        if (isFirst) path.reset();
        for (int i = 0; i < indices.length; i++) {
            LandmarkPoint p = pts.get(indices[i]);
            if (i == 0 && isFirst) path.moveTo(p.x, p.y);
            else path.lineTo(p.x, p.y);
        }
    }

    private void applyEdgeFeather(Path scaledLipPath) {
        // Get bounding box of the lip in mask space
        android.graphics.RectF bounds = new android.graphics.RectF();
        scaledLipPath.computeBounds(bounds, true);

        if (bounds.isEmpty()) return;

        float cx = bounds.centerX();
        float cy = bounds.centerY();
        float outerR = Math.max(bounds.width(), bounds.height()) * 0.5f;
        float innerR = outerR * (1f - edgeFeatherRatio);

        // Draw a clear ring from innerR → outerR to feather the edge
        Paint featherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        featherPaint.setShader(new RadialGradient(
                cx, cy, outerR,
                new int[]{Color.TRANSPARENT, Color.TRANSPARENT, 0x88000000},
                new float[]{0f, innerR / outerR, 1f},
                Shader.TileMode.CLAMP
        ));
        featherPaint.setXfermode(new android.graphics.PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Clip to lip path, then apply feather
        maskCanvas.save();
        maskCanvas.clipPath(scaledLipPath);
        // Draw a radial gradient from center (keep) to edge (fade out)
        android.graphics.Matrix gradMatrix = new android.graphics.Matrix();
        Paint gradPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradPaint.setShader(new RadialGradient(
                cx, cy, outerR,
                new int[]{0xFFFFFFFF, 0xFFFFFFFF, 0x00FFFFFF},
                new float[]{0f, innerR / outerR, 1f},
                Shader.TileMode.CLAMP
        ));
        gradPaint.setXfermode(new android.graphics.PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        maskCanvas.drawRect(0, 0, MASK_WIDTH, MASK_HEIGHT, gradPaint);
        maskCanvas.restore();
    }

    private void extractAlphaToBuffer() {
        // Extract ARGB_8888 pixel array → use alpha channel as grayscale mask
        int[] pixels = new int[MASK_WIDTH * MASK_HEIGHT];
        maskBitmap.getPixels(pixels, 0, MASK_WIDTH, 0, 0, MASK_WIDTH, MASK_HEIGHT);

        pixelBuffer.clear();
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            pixelBuffer.put(i, (byte) alpha);
        }
        pixelBuffer.position(0);
    }
}
