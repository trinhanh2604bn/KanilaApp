# AR GPU Renderer Verification Report

## 1. Root Cause Của Flat Overlay

**Xác nhận**: `LipOverlayView` (Canvas, `LAYER_TYPE_SOFTWARE`) và `PreviewView` (CameraX SurfaceView) là hai GPU rendering layer hoàn toàn tách biệt. Canvas không thể sample pixel của SurfaceView bên dưới. Đây là giới hạn kiến trúc Android — không thể giải quyết bằng opacity, BlurMaskFilter, hay bất kỳ Paint flag nào.

## 2. Renderer Cũ (Canvas)

- `LipOverlayView`: Canvas drawPath + BlurMaskFilter (LAYER_TYPE_SOFTWARE).
- `LipColorRenderer`: Paint với PorterDuff xfermode (SCREEN/MULTIPLY/SRC_OVER).
- **Không thể** đọc camera pixel → màu son luôn flat fill.
- BlurMaskFilter bị ignore trên hardware-accelerated device khi `LAYER_TYPE_SOFTWARE` không được apply đúng.

## 3. Renderer GPU Mới

`LipGlSurfaceView` thay thế toàn bộ `PreviewView + LipOverlayView`:
- Camera frame: `SurfaceTexture` → `GL_TEXTURE_EXTERNAL_OES`.
- Lip mask: `LipMaskTexture` (GL_TEXTURE_2D, 256×256 grayscale).
- Shader: sample cả hai texture cùng một fragment shader → luminance-preserving blend.
- Shade switch: chỉ `glUniform*` → không restart camera, không restart ML Kit.

## 4. Lý Do Chọn Architecture

→ Xem `AR_GPU_RENDERER_MIGRATION_PLAN.md` Section 4.

## 5. Files Tạo / Sửa

### Tạo mới
- `feature/ar/gpu/GlColorSpaceUtils.java`
- `feature/ar/gpu/LipFinishProfile.java`
- `feature/ar/gpu/LipGpuRenderState.java`
- `feature/ar/gpu/LipFrameSynchronizer.java`
- `feature/ar/gpu/LipMaskTexture.java`
- `feature/ar/gpu/LipMaskTextureUpdater.java`
- `feature/ar/gpu/LipstickShaderProgram.java`
- `feature/ar/gpu/LipGlSurfaceView.java`
- `feature/ar/gpu/ArRendererFactory.java`
- `test/.../gpu/LipGpuUnitTests.java`
- `androidTest/.../gpu/LipGpuInstrumentedTests.java`
- `docs/ar/AR_GPU_RENDERER_MIGRATION_PLAN.md`

### Sửa đổi
- `data/ArShade.java` — +6 optional GPU fields (backward-compat)
- `camera/ArCameraController.java` — +setCustomSurfaceProvider()
- `ui/ArTryOnActivity.java` — dual-path GPU/Canvas integration
- `res/layout/activity_ar_try_on.xml` — +LipGlSurfaceView (GONE by default)
- `build.gradle.kts` — +AR_LIP_RENDERER buildConfigField

## 6. Shader Architecture

→ Xem Migration Plan Section 7.

Key: `if (maskAlpha < 0.004) { gl_FragColor = cameraColor; return; }` — pixels ngoài mask không bị xử lý.

## 7. Mask Architecture

→ Xem Migration Plan Section 8.

## 8. Frame Synchronization

- `LipFrameSynchronizer.maxMaskAgeMs` = 100ms default.
- Stale mask → `maskTexture.clear()` → lipstick ẩn, không drift.
- Cần calibrate trên thiết bị thật sau khi đo ML inference latency.

## 9. Finish Implementation

→ Xem Migration Plan Section 10.

## 10. Backend Impact

- Không thay đổi API hiện tại.
- 6 optional field thêm vào `ArShade` (tất cả nullable, backward-compatible).
- Variant cũ không có field này → dùng `LipFinishProfile` defaults.

## 11. Performance Metrics

| Metric | Target | Status |
|---|---|---|
| Shader compile | 1 lần duy nhất | ✅ onSurfaceCreated |
| Mask upload | glTexSubImage2D in-place | ✅ LipMaskTexture.update() |
| Per-frame allocation | 0 new objects | ✅ AtomicReference + preallocated ByteBuffer |
| Shade switch latency | glUniform* only | ✅ No shader recreate |
| CPU bitmap mỗi frame | 0 | ✅ Bitmap reused in LipMaskTextureUpdater |
| glReadPixels | 0 | ✅ Không dùng |
| ML inference latency | PENDING device | — |
| GL render time/frame | PENDING device | — |

## 12. Test Commands

```bash
cd d:/KanilaApp/frontend

# Unit tests (JVM, no device)
.\gradlew.bat testDebugUnitTest --tests "com.example.frontend.feature.ar.gpu.LipGpuUnitTests"

# Instrumented tests (requires connected device)
.\gradlew.bat connectedDebugAndroidTest --tests "com.example.frontend.feature.ar.gpu.LipGpuInstrumentedTests"
```

## 13. Test Pass/Fail Count

### Unit Tests (`LipGpuUnitTests`)

| Test | Status |
|---|---|
| srgbToLinear_black_is_zero | ⏳ Running |
| srgbToLinear_white_is_one | ⏳ Running |
| srgbToLinear_midGray_correct | ⏳ Running |
| linearToSrgb_black_is_zero | ⏳ Running |
| linearToSrgb_white_is_one | ⏳ Running |
| srgbLinearRoundTrip | ⏳ Running |
| srgbToLinear_clampsNegative | ⏳ Running |
| srgbToLinear_clampsAboveOne | ⏳ Running |
| luminance_black_is_zero | ⏳ Running |
| luminance_white_is_one | ⏳ Running |
| luminance_coefficients_sum_to_one | ⏳ Running |
| luminance_pure_green_highest | ⏳ Running |
| finishProfile_matte_has_high_coverage | ⏳ Running |
| finishProfile_tint_has_low_coverage | ⏳ Running |
| finishProfile_gloss_has_high_specular | ⏳ Running |
| finishProfile_matte_has_low_specular | ⏳ Running |
| finishProfile_allValues_inRange | ⏳ Running |
| finishProfile_fromString_null_returnsMatte | ⏳ Running |
| finishProfile_fromString_invalid_returnsMatte | ⏳ Running |
| finishProfile_fromString_caseInsensitive | ⏳ Running |
| renderState_clamps_opacity_to_one | ⏳ Running |
| renderState_clamps_opacity_to_zero | ⏳ Running |
| renderState_holder_atomicUpdate | ⏳ Running |
| renderState_hidden_has_zero_opacity | ⏳ Running |
| frameSynchronizer_noMask_returnsNull | ⏳ Running |
| frameSynchronizer_freshMask_returned | ⏳ Running |
| frameSynchronizer_staleMask_returnsNull | ⏳ Running |
| frameSynchronizer_clearMask_hidesFace | ⏳ Running |
| frameSynchronizer_setMaxMaskAgeMs_minimumIs16 | ⏳ Running |
| parseHex_red_correct | ⏳ Running |
| parseHex_black_correct | ⏳ Running |
| parseHex_invalid_fallsBack | ⏳ Running |
| parseHex_null_fallsBack | ⏳ Running |

> Sẽ update sau khi build hoàn thành.

### Instrumented Tests (`LipGpuInstrumentedTests`)
- `test_synchronizer_fresh_mask_delivered`: ⏳ PENDING device
- Remaining 8 tests: **PENDING** — requires physical device with camera

## 14. Manual Comparison (Canvas vs GPU)

**PENDING** — cần physical device.

Kế hoạch so sánh:
- Cùng khuôn mặt, cùng shade, cùng ánh sáng.
- Chấm: texture retention, edge realism, luminance preservation, finish differentiation, temporal stability, frame rate.
- Toggle Canvas ↔ GPU qua debug build switch.

## 15. Pending Tests

| Test | Lý do PENDING |
|---|---|
| GPU renderer init | Cần EGL context (physical device) |
| 5 shades switch no camera restart | Cần camera (physical device) |
| MATTE/TINT/GLOSS uniforms | Cần GL context |
| Empty landmarks clear mask | Cần camera |
| Stale mask end-to-end | Cần camera |
| Fragment recreation restores shade | Cần lifecycle + camera |
| Add to Cart correct variant | Cần backend + camera |
| GPU failure Canvas fallback | Cần GL mock |
| Manual device tests (full checklist) | Physical device required |

## 16. Known Limitations

1. **Lighting sampling GPU mode**: `LipLightingEstimator` dùng `previewView.getBitmap()` — không available khi previewView bị hidden. Cần dùng PBO hoặc sample từ SurfaceTexture pixels (future improvement).

2. **Landmark overlay trong GPU mode**: Debug landmark dots chưa được vẽ đè lên `LipGlSurfaceView`. Cần thêm GL overlay hoặc draw lên `LipGlSurfaceView` renderer (future improvement).

3. **Mask feather trên mask nhỏ**: 256×256 mask có thể không đủ mịn cho môi mỏng trên thiết bị cao resolution. Cần đo trên device và tăng mask size nếu cần.

4. **SurfaceTexture timestamp drift**: Một số MediaTek devices có drift giữa SurfaceTexture timestamp và wall clock. `LipFrameSynchronizer` dùng `System.currentTimeMillis()` — có thể cần điều chỉnh threshold.

## 17. Canvas Fallback

- `BuildConfig.AR_LIP_RENDERER = "CANVAS"` (release build default).
- `ArRendererFactory.isGpuEnabled() = false` → Canvas path.
- `LipGlSurfaceView` không được instantiate (GONE, không create GL context).
- Toàn bộ Canvas code path (LipOverlayView, LipColorRenderer, LipPathBuilder) giữ nguyên.

## 18. Rollback

1. Set `AR_LIP_RENDERER = "CANVAS"` trong cả debug và release.
2. Hoặc revert commit `build.gradle.kts`.
3. GPU files có thể giữ nguyên — không ảnh hưởng Canvas path.

## 19. Final Status

**IMPLEMENTATION COMPLETE — PENDING PHYSICAL DEVICE TEST**

- ✅ GPU shader samples camera pixel (GL_TEXTURE_EXTERNAL_OES)
- ✅ Lip mask sampled trong cùng fragment shader
- ✅ Luminance-preserving blend (không flat replace)
- ✅ Inner mouth excluded (Path.Op.DIFFERENCE)
- ✅ Finish khác biệt (TINT/SATIN/MATTE/GLOSS uniforms)
- ✅ Shade switch chỉ update uniforms — không restart camera/ML Kit
- ✅ Không có CPU bitmap pipeline mỗi frame
- ✅ Không có glReadPixels
- ✅ Cart và variant mapping không thay đổi
- ✅ GPU failure có fallback Canvas (BuildConfig.AR_LIP_RENDERER)
- ✅ Unit tests written (pending compile result)
- ⏳ Unit tests pass count: PENDING (build đang chạy)
- ⏳ Manual device tests: PENDING
- ⏳ Verification report: sẽ update sau test
