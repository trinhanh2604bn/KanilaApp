package com.example.frontend.feature.ar.gpu;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;

import com.example.frontend.feature.ar.domain.LandmarkPoint;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GLSurfaceView that:
 *  1. Receives camera frames via CameraX Preview.SurfaceProvider.
 *  2. Renders them as a GL_TEXTURE_EXTERNAL_OES texture.
 *  3. Composites lip mask + shade color in the fragment shader.
 *  4. Outputs the combined frame to screen.
 *
 * This replaces the separate PreviewView + LipOverlayView stack.
 * The camera pixel is sampled directly in the shader — no CPU round-trip.
 *
 * Threading model:
 *  - Main/UI thread: setShade(), updateLandmarks(), onPause/Resume.
 *  - GL thread (internal): onSurfaceCreated, onDrawFrame — all GL calls.
 *  - Analysis thread: LipMaskTextureUpdater.update() → atomic buffer.
 */
public class LipGlSurfaceView extends GLSurfaceView
        implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "LipGlSurfaceView";

    // ─── GL objects (GL thread only) ──────────────────────────────────────────
    private int cameraTextureId = -1;
    private SurfaceTexture cameraSurfaceTexture;
    private final float[] texMatrix = new float[16];

    private LipstickShaderProgram shader;
    private LipMaskTexture maskTexture;

    // ─── CPU pipeline (analysis thread → GL thread via atomic) ───────────────
    private LipMaskTextureUpdater maskUpdater;
    private LipFrameSynchronizer  frameSynchronizer;

    // ─── Shared state (main thread writes, GL thread reads atomically) ────────
    private final LipGpuRenderState.Holder stateHolder = new LipGpuRenderState.Holder();

    // ─── CameraX SurfaceProvider support ─────────────────────────────────────
    /** Pending SurfaceRequest from CameraX — resolved once GL context is ready. */
    private final AtomicReference<SurfaceRequest> pendingSurfaceRequest = new AtomicReference<>(null);
    private volatile boolean surfaceReady = false;
    private volatile int surfaceWidth  = 0;
    private volatile int surfaceHeight = 0;

    // ─── View dimensions (set on GL thread, read by mask updater) ────────────
    private volatile int viewWidth  = 1;
    private volatile int viewHeight = 1;

    public LipGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public LipGlSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY); // Only render when frame is available

        maskUpdater       = new LipMaskTextureUpdater();
        frameSynchronizer = new LipFrameSynchronizer();
    }

    // ─── Public API (main/analysis thread) ────────────────────────────────────

    /** Update shader uniforms for a new shade. Thread-safe, no camera restart. */
    public void setRenderState(LipGpuRenderState state) {
        stateHolder.update(state);
        requestRender();
    }

    /** Feed new smoothed landmarks. Called from analysis/main thread. */
    public void updateLandmarks(List<LandmarkPoint> landmarks, long timestampMs) {
        if (landmarks == null || landmarks.isEmpty()) {
            frameSynchronizer.clearMask();
            maskUpdater.clearMask();
        } else {
            frameSynchronizer.putMask(landmarks, timestampMs, true);
            LipGpuRenderState state = stateHolder.get();
            float feather = 0.12f; // safe default; ideally from active profile
            maskUpdater.update(landmarks, viewWidth, viewHeight, feather);
        }
        requestRender();
    }

    /**
     * Returns a CameraX {@link Preview.SurfaceProvider} that routes camera
     * frames into this GL view's SurfaceTexture.
     */
    public Preview.SurfaceProvider getSurfaceProvider() {
        return surfaceRequest -> {
            pendingSurfaceRequest.set(surfaceRequest);
            if (surfaceReady && cameraSurfaceTexture != null) {
                resolveSurfaceRequest(surfaceRequest);
            }
        };
    }

    // ─── GLSurfaceView.Renderer ───────────────────────────────────────────────

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Create camera OES texture
        int[] ids = new int[1];
        GLES20.glGenTextures(1, ids, 0);
        cameraTextureId = ids[0];

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        cameraSurfaceTexture = new SurfaceTexture(cameraTextureId);
        cameraSurfaceTexture.setOnFrameAvailableListener(this);

        // Create mask texture
        maskTexture = new LipMaskTexture(LipMaskTextureUpdater.MASK_WIDTH, LipMaskTextureUpdater.MASK_HEIGHT);
        maskTexture.create();

        // Compile shader
        shader = new LipstickShaderProgram();
        boolean ok = shader.compile();
        if (!ok) {
            Log.e(TAG, "Shader compile failed — GPU renderer unavailable");
        }

        // Mark surface ready and resolve any pending CameraX request
        surfaceReady = true;
        SurfaceRequest request = pendingSurfaceRequest.get();
        if (request != null) {
            resolveSurfaceRequest(request);
        }

        Log.d(TAG, "GL surface created, shader ok=" + ok);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.surfaceWidth  = width;
        this.surfaceHeight = height;
        this.viewWidth     = width;
        this.viewHeight    = height;
        Log.d(TAG, "GL surface changed: " + width + "x" + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (cameraSurfaceTexture == null || !shader.isCompiled()) return;

        // Update camera frame
        cameraSurfaceTexture.updateTexImage();
        cameraSurfaceTexture.getTransformMatrix(texMatrix);

        // Upload pending mask if available
        ByteBuffer pendingMask = maskUpdater.consumePendingBuffer();
        if (pendingMask != null && maskTexture.isCreated()) {
            LipFrameSynchronizer.MaskFrame frame = frameSynchronizer.getMaskForRender();
            if (frame == null || !frame.hasFace) {
                maskTexture.clear();
            } else {
                maskTexture.update(pendingMask);
            }
        }

        // If stale mask, clear
        if (frameSynchronizer.getMaskForRender() == null) {
            maskTexture.clear();
        }

        // Draw
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        LipGpuRenderState state = stateHolder.get();
        shader.draw(cameraTextureId, texMatrix, maskTexture, state);
    }

    // ─── SurfaceTexture.OnFrameAvailableListener ──────────────────────────────

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender(); // triggers onDrawFrame on GL thread
    }

    // ─── CameraX SurfaceRequest resolution ───────────────────────────────────

    private void resolveSurfaceRequest(SurfaceRequest request) {
        if (cameraSurfaceTexture == null) return;
        Size resolution = request.getResolution();
        cameraSurfaceTexture.setDefaultBufferSize(resolution.getWidth(), resolution.getHeight());
        Surface surface = new Surface(cameraSurfaceTexture);
        request.provideSurface(surface, getContext().getMainExecutor(), result -> {
            // Surface is no longer needed — CameraX will call this when done
            surface.release();
        });
        Log.d(TAG, "Camera surface provided: " + resolution.getWidth() + "x" + resolution.getHeight());
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    public void release() {
        queueEvent(() -> {
            if (shader != null) shader.delete();
            if (maskTexture != null) maskTexture.delete();
            if (cameraSurfaceTexture != null) {
                cameraSurfaceTexture.release();
                cameraSurfaceTexture = null;
            }
            surfaceReady = false;
        });
    }
}
