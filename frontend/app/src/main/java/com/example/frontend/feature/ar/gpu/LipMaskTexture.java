package com.example.frontend.feature.ar.gpu;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Manages a single OpenGL grayscale 2D texture used as the lip mask.
 *
 * The mask is an 8-bit single-channel (GL_LUMINANCE) texture:
 *   0   = no lipstick coverage
 *   255 = full lipstick coverage
 *
 * The texture is created once and updated in-place via glTexSubImage2D.
 * No new GL objects are created on each frame update.
 *
 * Must be used on the GL thread (after EGL context is created).
 */
public final class LipMaskTexture {

    private static final String TAG = "LipMaskTexture";

    private final int width;
    private final int height;

    private int textureId = -1;
    private boolean created = false;

    public LipMaskTexture(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Creates the GL texture object. Call once from {@code onSurfaceCreated}.
     */
    public void create() {
        if (created) return;

        int[] ids = new int[1];
        GLES20.glGenTextures(1, ids, 0);
        textureId = ids[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Allocate the texture with zeros (fully transparent mask)
        ByteBuffer zeros = ByteBuffer.allocateDirect(width * height);
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                width, height, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                zeros);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "GL error creating mask texture: " + err);
        } else {
            created = true;
        }
    }

    /**
     * Uploads new mask data to the GPU. Call from GL thread.
     *
     * @param buffer  ByteBuffer (capacity = width × height), position=0, single-channel 8-bit.
     */
    public void update(ByteBuffer buffer) {
        if (!created || textureId < 0) return;
        buffer.position(0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexSubImage2D(
                GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                buffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    /**
     * Uploads a fully transparent (zeroed) mask. Hides lipstick without GL errors.
     */
    public void clear() {
        if (!created || textureId < 0) return;
        ByteBuffer zeros = ByteBuffer.allocateDirect(width * height);
        update(zeros);
    }

    /**
     * Binds this texture to the given texture unit (e.g. GLES20.GL_TEXTURE1).
     */
    public void bind(int textureUnit) {
        GLES20.glActiveTexture(textureUnit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    }

    public int getTextureId() { return textureId; }
    public int getWidth()     { return width; }
    public int getHeight()    { return height; }
    public boolean isCreated() { return created; }

    /**
     * Deletes the GL texture. Call from GL thread on teardown.
     */
    public void delete() {
        if (textureId >= 0) {
            GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
            textureId = -1;
            created   = false;
        }
    }
}
