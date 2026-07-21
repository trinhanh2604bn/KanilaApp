package com.example.frontend.feature.ar.gpu;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Compiles and manages the lipstick composite fragment shader.
 *
 * Pipeline per pixel:
 *  1. Sample camera frame (samplerExternalOES) → cameraColor (sRGB)
 *  2. Sample lip mask (sampler2D) → maskAlpha ∈ [0,1]
 *  3. sRGB → linear RGB (baseLinear)
 *  4. Luminance Y = dot(baseLinear, vec3(0.2126, 0.7152, 0.0722))
 *  5. Adjust shade color by Y (luminance-preserving blend, not flat replace)
 *  6. Apply finish via uFinishType uniforms (TINT/SATIN/MATTE/GLOSS)
 *  7. Mix base + textured shade weighted by maskAlpha × coverage × opacity
 *  8. linear → sRGB output
 *  9. Clamp to [0,1]
 *
 * Shade switching: only glUniform* calls — no shader recompile, no camera restart.
 *
 * Must be used on the GL thread.
 */
public final class LipstickShaderProgram {

    private static final String TAG = "LipstickShaderProgram";

    // ─── Vertex shader ────────────────────────────────────────────────────────

    private static final String VERTEX_SHADER_SRC =
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTexCoord;\n" +
            "varying vec2 vTexCoord;\n" +
            "varying vec2 vMaskCoord;\n" +
            "uniform mat4 uTexMatrix;\n" +
            "void main() {\n" +
            "    gl_Position = aPosition;\n" +
            "    vTexCoord   = (uTexMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;\n" +
            "    // Flip Y for mask: Canvas Y=0 is top, GL V=0 is bottom\n" +
            "    vMaskCoord  = vec2(aTexCoord.x, 1.0 - aTexCoord.y);\n" +
            "}\n";

    // ─── Fragment shader ──────────────────────────────────────────────────────

    private static final String FRAGMENT_SHADER_SRC =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTexCoord;\n" +
            "varying vec2 vMaskCoord;\n" +
            "uniform samplerExternalOES uCameraTexture;\n" +
            "uniform sampler2D          uMaskTexture;\n" +
            // Shade uniforms
            "uniform vec3  uShadeColor;\n" +        // linear RGB
            "uniform float uOpacity;\n" +
            "uniform float uCoverage;\n" +
            "uniform float uTextureRetention;\n" +
            "uniform float uBrightnessBias;\n" +
            "uniform float uSaturation;\n" +
            "uniform float uRoughness;\n" +
            "uniform float uSpecularStrength;\n" +
            "uniform int   uFinishType;\n" +        // 0=TINT 1=SATIN 2=GLOSS 3=MATTE

            // Injected from GlColorSpaceUtils.GLSL_COLOR_HELPERS
            GlColorSpaceUtils.GLSL_COLOR_HELPERS +

            "void main() {\n" +
            // 1. Sample camera and mask
            "    vec4 camSrgb  = texture2D(uCameraTexture, vTexCoord);\n" +
            "    float maskAlpha = texture2D(uMaskTexture, vMaskCoord).r;\n" +
            // If outside mask, output camera directly (no lipstick)
            "    if (maskAlpha < 0.004) {\n" +
            "        gl_FragColor = camSrgb;\n" +
            "        return;\n" +
            "    }\n" +
            // 2. Convert camera to linear
            "    vec3 baseLinear = srgbToLinear(camSrgb.rgb);\n" +
            // 3. Luminance of natural lip
            "    float baseLuma = luminance(baseLinear);\n" +
            // 4. Luminance-aware shade: scale shade brightness by lip luma
            //    This preserves dark areas staying dark and highlights bright
            "    float targetLuma = luminance(uShadeColor);\n" +
            "    float lumaRatio  = (targetLuma > 0.001) ? (baseLuma / targetLuma) : 1.0;\n" +
            // textureRetention: blend between shade and luma-adjusted shade
            "    float lumaBlend  = clamp(lumaRatio, 0.5, 2.0);\n" +
            "    vec3  adjustedShade = uShadeColor * mix(1.0, lumaBlend, uTextureRetention);\n" +
            // 5. Saturation adjustment on shade
            "    float shadeLuma = luminance(adjustedShade);\n" +
            "    adjustedShade   = mix(vec3(shadeLuma), adjustedShade, uSaturation);\n" +
            // 6. Finish-specific modifiers
            "    float specContrib = 0.0;\n" +
            "    if (uFinishType == 2) {\n" +
            "        float specSource = clamp((baseLuma - 0.5) * 2.0, 0.0, 1.0);\n" +
            "        specContrib = specSource * uSpecularStrength;\n" +
            "        adjustedShade += vec3(specContrib) * (1.0 - uRoughness);\n" +
            "    } else if (uFinishType == 0) {\n" +
            "        adjustedShade = mix(baseLinear, adjustedShade, uCoverage);\n" +
            "    }\n" +
            // 7. Brightness bias (small correction, not lamp replacement)
            "    adjustedShade = adjustedShade + uBrightnessBias;\n" +
            // 8. Preserve lip micro-contrast in the textured shade
            //    textureRetention=1 keeps all lip micro-texture
            "    vec3 texturedShade = mix(adjustedShade, adjustedShade * (baseLinear / max(vec3(baseLuma), vec3(0.001))), uTextureRetention * 0.5);\n" +
            // 9. Final blend: mix camera with textured shade
            "    float blendWeight = maskAlpha * uCoverage * uOpacity;\n" +
            "    blendWeight = clamp(blendWeight, 0.0, 1.0);\n" +
            "    vec3 result = mix(baseLinear, texturedShade, blendWeight);\n" +
            // 10. Back to sRGB and output
            "    result = linearToSrgb(result);\n" +
            "    result = clamp(result, 0.0, 1.0);\n" +
            "    gl_FragColor = vec4(result, 1.0);\n" +
            "}\n";

    // ─── Full-screen quad geometry ─────────────────────────────────────────────

    private static final float[] QUAD_VERTICES = {
        // X     Y      U     V
        -1.0f, -1.0f,  0.0f, 0.0f,
         1.0f, -1.0f,  1.0f, 0.0f,
        -1.0f,  1.0f,  0.0f, 1.0f,
         1.0f,  1.0f,  1.0f, 1.0f,
    };
    private static final int STRIDE = 4 * 4; // 4 floats × 4 bytes

    // ─── GL handles ───────────────────────────────────────────────────────────

    private int programId = -1;

    // Attribute locations
    private int locPosition;
    private int locTexCoord;

    // Uniform locations (cached — never call glGetUniformLocation in render loop)
    private int locTexMatrix;
    private int locCameraTexture;
    private int locMaskTexture;
    private int locShadeColor;
    private int locOpacity;
    private int locCoverage;
    private int locTextureRetention;
    private int locBrightnessBias;
    private int locSaturation;
    private int locRoughness;
    private int locSpecularStrength;
    private int locFinishType;

    private FloatBuffer vertexBuffer;
    private boolean compiled = false;

    // ─── Compile ──────────────────────────────────────────────────────────────

    /**
     * Compile shaders and cache all attribute/uniform locations.
     * Call once from {@code onSurfaceCreated}.
     */
    public boolean compile() {
        if (compiled) return true;

        int vs = compileShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_SRC);
        int fs = compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SRC);
        if (vs == 0 || fs == 0) return false;

        programId = GLES20.glCreateProgram();
        GLES20.glAttachShader(programId, vs);
        GLES20.glAttachShader(programId, fs);
        GLES20.glLinkProgram(programId);

        int[] status = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e(TAG, "Program link error: " + GLES20.glGetProgramInfoLog(programId));
            GLES20.glDeleteProgram(programId);
            programId = -1;
            return false;
        }

        // Cache attribute locations
        locPosition   = GLES20.glGetAttribLocation(programId, "aPosition");
        locTexCoord   = GLES20.glGetAttribLocation(programId, "aTexCoord");

        // Cache uniform locations
        locTexMatrix        = GLES20.glGetUniformLocation(programId, "uTexMatrix");
        locCameraTexture    = GLES20.glGetUniformLocation(programId, "uCameraTexture");
        locMaskTexture      = GLES20.glGetUniformLocation(programId, "uMaskTexture");
        locShadeColor       = GLES20.glGetUniformLocation(programId, "uShadeColor");
        locOpacity          = GLES20.glGetUniformLocation(programId, "uOpacity");
        locCoverage         = GLES20.glGetUniformLocation(programId, "uCoverage");
        locTextureRetention = GLES20.glGetUniformLocation(programId, "uTextureRetention");
        locBrightnessBias   = GLES20.glGetUniformLocation(programId, "uBrightnessBias");
        locSaturation       = GLES20.glGetUniformLocation(programId, "uSaturation");
        locRoughness        = GLES20.glGetUniformLocation(programId, "uRoughness");
        locSpecularStrength = GLES20.glGetUniformLocation(programId, "uSpecularStrength");
        locFinishType       = GLES20.glGetUniformLocation(programId, "uFinishType");

        // Create vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(QUAD_VERTICES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(QUAD_VERTICES);
        vertexBuffer.position(0);

        // Cleanup shader objects (linked into program)
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);

        compiled = true;
        Log.d(TAG, "Shader compiled successfully");
        return true;
    }

    private int compileShader(int type, String src) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, src);
        GLES20.glCompileShader(shader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e(TAG, "Shader compile error (" + type + "): " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    // ─── Draw ─────────────────────────────────────────────────────────────────

    /**
     * Draw a full-screen quad compositing camera + mask.
     *
     * @param cameraTextureId   GL_TEXTURE_EXTERNAL_OES texture from SurfaceTexture
     * @param texMatrix         4x4 transform matrix from SurfaceTexture.getTransformMatrix()
     * @param maskTexture       grayscale 2D mask texture
     * @param state             current shade uniforms
     */
    public void draw(int cameraTextureId, float[] texMatrix,
                     LipMaskTexture maskTexture, LipGpuRenderState state) {
        if (!compiled || programId < 0) return;

        GLES20.glUseProgram(programId);

        // Camera texture (unit 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId);
        GLES20.glUniform1i(locCameraTexture, 0);

        // Mask texture (unit 1)
        maskTexture.bind(GLES20.GL_TEXTURE1);
        GLES20.glUniform1i(locMaskTexture, 1);

        // Texture transform matrix
        GLES20.glUniformMatrix4fv(locTexMatrix, 1, false, texMatrix, 0);

        // Shade uniforms — only these change when user switches shade
        GLES20.glUniform3f(locShadeColor,       state.shadeR, state.shadeG, state.shadeB);
        GLES20.glUniform1f(locOpacity,          state.opacity);
        GLES20.glUniform1f(locCoverage,         state.coverage);
        GLES20.glUniform1f(locTextureRetention, state.textureRetention);
        GLES20.glUniform1f(locBrightnessBias,   state.brightnessBias);
        GLES20.glUniform1f(locSaturation,       state.saturation);
        GLES20.glUniform1f(locRoughness,        state.roughness);
        GLES20.glUniform1f(locSpecularStrength, state.specularStrength);
        GLES20.glUniform1i(locFinishType,       state.finishTypeCode);

        // Vertex attributes
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(locPosition, 2, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
        GLES20.glEnableVertexAttribArray(locPosition);

        vertexBuffer.position(2);
        GLES20.glVertexAttribPointer(locTexCoord, 2, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);
        GLES20.glEnableVertexAttribArray(locTexCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(locPosition);
        GLES20.glDisableVertexAttribArray(locTexCoord);
    }

    public boolean isCompiled() { return compiled; }

    public void delete() {
        if (programId >= 0) {
            GLES20.glDeleteProgram(programId);
            programId = -1;
            compiled  = false;
        }
    }
}
