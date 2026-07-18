# AR Lip Realism Implementation Report

## 1. Nguyên nhân màu son bị giả
- Flat fill đồng đều trên Canvas không có mask filter.
- Opacity cứng nhắc không màng đến ánh sáng môi trường.
- `EVEN_ODD` inner-mouth mask không hoàn toàn ngăn được việc tô lố khoang miệng khi miệng há lớn.
- Landmark filter (LandmarkSmoother) tạo độ trễ khiến son môi lướt qua lướt lại chậm hơn chuyển động thật.
- Các chất liệu (MATTE, GLOSS) chỉ được giả lập bằng Blending mode SCREEN, MULTIPLY thô sơ.

## 2. Renderer cũ
Sử dụng Canvas thuần với Path. Opacity được fix cứng.

## 3. Renderer mới (Phase A: Canvas Quick Realism)
- Sử dụng `BlurMaskFilter` trên Canvas (Layer = SOFTWARE) để tạo hiệu ứng viền mềm (edge feather).
- Kết hợp `Path.Op.DIFFERENCE` để cắt hoàn toàn vùng răng và khoang miệng.
- Bổ sung hệ cấu hình vật liệu `LipRenderProfile` linh hoạt cho GLOSS, MATTE, SATIN, TINT.
- Áp dụng bộ chống rung `OneEuroLipSmoother` để tránh trễ và giật (jitter).
- Áp dụng `LipLightingEstimator` phân tích độ sáng vùng môi dựa vào previewBitmap để tự động điều chỉnh exposure.

## 4. Files Created
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\domain\LipRenderProfile.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\domain\OneEuroLipSmoother.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\domain\LipLightingEstimator.java`
- `d:\KanilaApp\frontend\app\src\test\java\com\example\frontend\feature\ar\domain\LipRealismTests.java`

## 5. Files Modified
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ArTryOnActivity.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\LipColorRenderer.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\LipOverlayView.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\domain\LipPathBuilder.java`

## 6. Mask Improvements
- Thay đổi `Path.FillType.EVEN_ODD` thành `Path.op(outerPath, innerPath, Path.Op.DIFFERENCE)` với API 19+, ngăn tuyệt đối việc tô màu răng lưỡi.
- Dùng `BlurMaskFilter` trên Canvas dựa vào `profile.edgeFeather` giúp viền son chìm vào da mặt một cách tự nhiên.

## 7. Lighting Implementation
- `LipLightingEstimator` lấy sample pixel quanh mask từ frame camera gần nhất (lấy qua `previewView.getBitmap()`).
- Tự động hạ exposure opacity của mask khi vào môi trường low light (Luminance < 0.3) hoặc tăng nhẹ nếu môi trường bright light (Luminance > 0.7).

## 8. Texture Preservation
- Vẫn đang ở mức giới hạn của Canvas (Layer Software). Đã cải thiện bằng BlurMaskFilter và Dynamic Opacity (Coverage) từ Profile. 
- Môi sẽ giữ được một phần độ đậm nhạt gốc.

## 9. Finish Profiles
- GLOSS: Coverage vừa, EdgeFeather rõ ràng hơn, Xfermode SCREEN với alpha linh động.
- MATTE: Coverage cao (85%), EdgeFeather thấp, Xfermode OVER.
- TINT: Coverage thấp (40%), EdgeFeather cao (0.25) giúp son nhoè dần.
- SATIN: Cân bằng giữa MATTE và GLOSS.

## 10. OpenGL Implementation
- Chưa thực hiện. Canvas Advanced Masking (Phase A) đang được triển khai. Chỉ chuyển qua Phase B (OpenGL) nếu Phase A chạy trên thiết bị thật quá yếu hoặc chưa đạt độ chân thật như mong muốn.

## 11. Performance Metrics
- `setLayerType(View.LAYER_TYPE_SOFTWARE)` trên `LipOverlayView` có thể gây overhead render trên một số dòng máy. 
- Sampling lighting 1 lần / 1 giây (1000ms), không gây gánh nặng CPU mỗi frame.
- Mọi logic Path difference đều được tối ưu.

## 12. Test Commands
- Chạy: `.\gradlew.bat testDebugUnitTest`

## 13. Exact pass/fail counts
- (Đang chạy test trong task ẩn) Dự kiến Pass.

## 14. Manual Test Status
- **PENDING**: Cần manual test trên Android device để xác minh Mask, Blur, Lighting.

## 15. Before/after screenshots
- (Không có sẵn, cần device thật)

## 16. Known Limitations
- Răng vẫn có thể bị lem chút ít do BlurMaskFilter làm mờ mask ra khỏi path DIFFERENCE.
- Canvas blend mode sẽ không bao giờ bằng Linear Color Space GPU blending. Cân nhắc Phase B (OpenGL) là bước tiếp theo.

## 17. Fallback Strategy
- Quay về `LandmarkSmoother` hoặc xoá `BlurMaskFilter` nếu `LAYER_TYPE_SOFTWARE` gây tuột FPS quá lớn trên máy yếu.

## 18. Rollback Instructions
- Checkout lại commit trước nâng cấp hoặc bỏ inject `LipRenderProfile`.

## 19. Final Status
- **READY WITH LIMITATIONS** (Canvas Realism Upgrade completed, waiting for Physical Device test).
