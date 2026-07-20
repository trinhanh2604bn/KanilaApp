# AR GPU Renderer Migration Plan

## 1. Root Cause Của Flat Overlay

`LipOverlayView` (custom `View` với `LAYER_TYPE_SOFTWARE`) và `PreviewView` (CameraX `SurfaceView` backend) nằm ở hai GPU rendering layer hoàn toàn tách biệt:

- `PreviewView` render camera frame xuống một dedicated `Surface` (Hardware-accelerated).
- `LipOverlayView` vẽ Canvas lên một software bitmap layer.
- **Canvas không có quyền truy cập pixel của Surface bên dưới.**
- `Canvas.drawPath()` + `BlurMaskFilter` chỉ đọc được pixel trong Canvas layer của chính nó.
- Kết quả: màu son luôn là ARGB fill flat, không thể giữ texture hay adjust theo camera pixel.

## 2. Renderer Cũ

| Thành phần | Kỹ thuật |
|---|---|
| Camera preview | `PreviewView` (PERFORMANCE mode, SurfaceView backend) |
| Lip overlay | `LipOverlayView` (`LAYER_TYPE_SOFTWARE`, Canvas) |
| Path building | `LipPathBuilder` (Path.Op.DIFFERENCE for inner mouth) |
| Color rendering | `LipColorRenderer` (Paint + BlurMaskFilter + PorterDuffXfermode) |
| Smoothing | `OneEuroLipSmoother` |
| Lighting | `LipLightingEstimator` (sample via `previewView.getBitmap()` 1x/s) |

**Giới hạn không thể vượt qua với Canvas:**
- Không sample được camera pixel thực tế.
- BlurMaskFilter không hoạt động trong hardware-accelerated layer.
- Không thể làm luminance-preserving blend hay adjust texture môi.

## 3. Renderer GPU Mới

| Thành phần | Kỹ thuật |
|---|---|
| Camera source | `SurfaceTexture` (GL_TEXTURE_EXTERNAL_OES) |
| Rendering | `LipGlSurfaceView` (GLSurfaceView + custom Renderer) |
| Shader | `LipstickShaderProgram` (vertex + fragment, compiled once) |
| Mask | `LipMaskTexture` (GL_TEXTURE_2D grayscale, updated in-place) |
| Mask CPU | `LipMaskTextureUpdater` (Bitmap reuse, ByteBuffer atomic handoff) |
| Sync | `LipFrameSynchronizer` (stale mask rejection ≤100ms threshold) |
| Shade state | `LipGpuRenderState` (immutable, AtomicReference swap) |
| Color math | `GlColorSpaceUtils` (sRGB↔linear, GLSL + Java) |
| Finish profiles | `LipFinishProfile` (TINT/SATIN/MATTE/GLOSS defaults) |
| Factory | `ArRendererFactory` (BuildConfig.AR_LIP_RENDERER switch) |

## 4. Lý Do Chọn TextureView + GLSurfaceView Thay Vì Media3Effect

| Yêu cầu | Media3Effect | TextureView + GLSurfaceView |
|---|---|---|
| Không có Media3 dep | ❌ Cần thêm nặng | ✅ Không cần |
| CameraX 1.4.0-beta02 | ❌ API thay đổi nhiều | ✅ Stable Preview API |
| Mask texture động | ✅ | ✅ |
| Uniforms động | ✅ | ✅ |
| minSdk 24 | ⚠️ Hạn chế | ✅ OpenGL ES 2.0 đảm bảo |
| Complexity | Cao | Trung bình |
| Fallback dễ | Khó | ✅ |

## 5. Files Tạo Mới

```
frontend/app/src/main/java/com/example/frontend/feature/ar/gpu/
├── GlColorSpaceUtils.java         — sRGB↔linear, GLSL strings
├── LipFinishProfile.java          — TINT/SATIN/MATTE/GLOSS default params
├── LipGpuRenderState.java         — Immutable shade state + AtomicReference holder
├── LipFrameSynchronizer.java      — Stale mask rejection
├── LipMaskTexture.java            — GL_TEXTURE_2D grayscale wrapper
├── LipMaskTextureUpdater.java     — CPU mask generator (reusable Bitmap/ByteBuffer)
├── LipstickShaderProgram.java     — Vertex + fragment shader + uniform management
├── LipGlSurfaceView.java          — Core GL renderer (GLSurfaceView + SurfaceProvider)
└── ArRendererFactory.java         — BuildConfig switch + shade application

frontend/app/src/test/.../gpu/
└── LipGpuUnitTests.java           — JVM unit tests (no device needed)

frontend/app/src/androidTest/.../gpu/
└── LipGpuInstrumentedTests.java   — Device tests (most PENDING)

docs/ar/
├── AR_GPU_RENDERER_MIGRATION_PLAN.md     (này)
└── AR_GPU_RENDERER_VERIFICATION_REPORT.md (sau khi test đạt)
```

## 6. Files Sửa Đổi

```
frontend/app/src/main/java/.../feature/ar/
├── data/ArShade.java              — +6 optional GPU calibration fields (backward-compat)
├── camera/ArCameraController.java — +setCustomSurfaceProvider() for GPU mode
└── ui/ArTryOnActivity.java        — Tích hợp GPU + Canvas dual-path

frontend/app/src/main/res/layout/
└── activity_ar_try_on.xml         — +LipGlSurfaceView (visibility=gone, runtime toggle)

frontend/app/build.gradle.kts      — +buildConfigField AR_LIP_RENDERER
```

## 7. Shader Architecture

**Vertex shader**: Full-screen quad, pass-through. Transforms UV via `uTexMatrix` (from SurfaceTexture).

**Fragment shader pipeline**:
```glsl
1. cameraColor   = texture2D(samplerExternalOES, vTexCoord)   // camera frame
2. maskAlpha     = texture2D(sampler2D, vMaskCoord).r          // grayscale mask
3. baseLinear    = srgbToLinear(cameraColor.rgb)               // gamma correction
4. baseLuma      = luminance(baseLinear)                        // Y = dot(color, vec3(0.2126,0.7152,0.0722))
5. adjustedShade = uShadeColor × (baseLuma/shadeLuma)^textureRetention  // luminance preservation
6. adjustedShade = mix(vec3(shadeLuma), adjustedShade, uSaturation)     // saturation control
7. GLOSS path: specContrib = (baseLuma-0.5)*2.0 × uSpecularStrength     // real highlight pixels
   TINT path:  adjustedShade = mix(baseLinear, adjustedShade, uCoverage) // lip color dominates
8. adjustedShade += uBrightnessBias                             // small correction only
9. blendWeight  = maskAlpha × uCoverage × uOpacity             // weighted composite
10. result      = mix(baseLinear, texturedShade, blendWeight)   // blend
11. output      = linearToSrgb(clamp(result, 0,1))             // back to sRGB
```

**Key property**: Pixels outside mask → `gl_FragColor = cameraColor` (no processing).

## 8. Mask Architecture

- **Size**: 256×256 grayscale (small to minimize upload bandwidth).
- **Mask generation** (CPU, analysis thread):
  - `Path.Op.DIFFERENCE` (outer - inner) → teeth/tongue excluded.
  - `RadialGradient` → edge feathered, 0 at boundary.
  - Feather radius = `profile.edgeFeather × lip_bbox_size`.
- **Upload** (GL thread): `glTexSubImage2D` in-place, no new texture object.
- **Handoff**: `AtomicReference<ByteBuffer>` + `AtomicBoolean` dirty flag.

## 9. Frame Synchronization

- `LipFrameSynchronizer` holds latest `MaskFrame(landmarks, timestampMs, hasFace)`.
- GL thread calls `getMaskForRender()` → null if age > `maxMaskAgeMs` (default 100ms).
- Stale mask → `maskTexture.clear()` → lipstick hidden (not floating).
- Threshold calibration: start 100ms, tune on real device by measuring ML inference latency.

## 10. Finish Profiles

| Finish | Coverage | Texture Ret. | Specular | Roughness | Edge Feather |
|---|---|---|---|---|---|
| TINT  | 0.38 | 0.90 | 0.05 | 0.90 | 0.28 |
| SATIN | 0.68 | 0.60 | 0.28 | 0.50 | 0.14 |
| MATTE | 0.82 | 0.38 | 0.04 | 0.92 | 0.10 |
| GLOSS | 0.58 | 0.72 | 0.72 | 0.10 | 0.16 |

## 11. Backend Impact

**Không thay đổi API contract**. Optional fields thêm vào `ArShade`:
- `coverage`, `texture_retention`, `brightness_bias`, `saturation`, `roughness`, `specular_strength`
- Tất cả `nullable` — variant cũ không có các field này vẫn hoạt động bình thường.
- Renderer dùng `LipFinishProfile` defaults khi null.

## 12. Performance

| Rule | Implementation |
|---|---|
| Không CPU bitmap mỗi frame | Bitmap reused in LipMaskTextureUpdater |
| Không glReadPixels | Không dùng |
| Shader compile 1 lần | `onSurfaceCreated` only |
| Uniform location cache | Tất cả location cached trong `compile()` |
| Shade switch | Chỉ glUniform* calls, không recreate |
| Mask upload | glTexSubImage2D in-place |
| No new allocations in render loop | AtomicReference + preallocated ByteBuffer |

## 13. Canvas Fallback

- `BuildConfig.AR_LIP_RENDERER = "CANVAS"` → `ArRendererFactory.isGpuEnabled() = false`.
- `LipGlSurfaceView` hidden, `PreviewView + LipOverlayView` visible.
- Toàn bộ Canvas code path từ trước không bị thay đổi.
- GPU init exception → không crash (Activity catches và dùng Canvas fallback).

## 14. Rollback

Nếu GPU cần rollback:
1. Set `buildConfigField("String", "AR_LIP_RENDERER", "\"CANVAS\"")` trong cả release và debug.
2. Không cần xóa GPU files — chúng chỉ được instantiate khi flag = "GPU".
3. Hoặc revert commit chứa `build.gradle.kts` change.
