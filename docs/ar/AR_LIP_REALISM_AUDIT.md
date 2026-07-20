# AR Lip Realism Audit

## I. Phân Tích Hiện Trạng

1. **Flat fill (Màu đặc phẳng)**: 
   - **Tình trạng**: Đang vẽ một lớp ARGB đều. `LipColorRenderer` trả về một `Paint` với opacity cố định. Lớp này vẽ đè hoàn toàn (hoặc trộn bằng xfermode) lên `Canvas` nhưng không lấy được texture của môi dưới.
   - **File**: `LipColorRenderer.java`, `LipOverlayView.java`
   - **Ảnh hưởng**: Lớn. Toàn bộ nếp nhăn và kết cấu môi tự nhiên biến mất.
   - **Hướng xử lý**: Tách vùng xử lý mask và tích hợp một shader hoặc dùng BlurMaskFilter kết hợp custom bitmap (nếu Canvas) hoặc tốt nhất là chuyển qua OpenGL. Trước mắt (Phase 1) có thể dùng variable coverage và feather.

2. **Hard edge (Viền cứng)**:
   - **Tình trạng**: `LipOverlayView` dùng `Path` vẽ trực tiếp bằng `Canvas.drawPath` không có `MaskFilter` nên viền sắc nét, trông như dán sticker.
   - **File**: `LipOverlayView.java`
   - **Ảnh hưởng**: Lớn. Gây cảm giác giả tạo.
   - **Hướng xử lý**: Dùng `BlurMaskFilter` trên `Paint` để làm mềm viền (edge feathering), hoặc chuyển qua render qua Bitmap rồi xử lý mask mềm.

3. **Inner mouth (Khoang miệng bị tô)**:
   - **Tình trạng**: `LipPathBuilder` đã có `Path.FillType.EVEN_ODD`, nhưng có thể do hướng của inner/outer path không chuẩn hoặc EVEN_ODD không xử lý triệt để trong mọi tình huống (nếu môi bị vặn).
   - **File**: `LipPathBuilder.java`
   - **Ảnh hưởng**: Lớn khi người dùng há miệng hoặc cười.
   - **Hướng xử lý**: Đổi sang `Path.Op.DIFFERENCE` giữa path ngoài và path trong để loại trừ răng/lưỡi một cách rõ ràng và chắc chắn hơn, hoặc chỉnh sửa inner path feather cẩn thận hơn.

4. **Lighting (Thiếu tương tác ánh sáng)**:
   - **Tình trạng**: Opacity và color giữ nguyên không màng ánh sáng camera. Môi trường tối vẫn thấy môi sáng loá (của màu gốc).
   - **File**: Chưa có cơ chế.
   - **Ảnh hưởng**: Gây chói ở môi trường tối, xỉn ở môi trường sáng.
   - **Hướng xử lý**: Bổ sung `LipLightingEstimator` để ước lượng exposure/luminance từ frame của camera, từ đó điều chỉnh brightness bias, opacity của shade.

5. **Temporal stability (Khung hình rung lắc)**:
   - **Tình trạng**: Có class `LandmarkSmoother` (hiện dang dùng 0.5f alpha EMA). Tuy nhiên, môi mềm và chuyển động linh hoạt nên EMA tuyến tính có thể gây rung khi đứng yên và trễ (lag) khi cử động nhanh.
   - **File**: `ArTryOnActivity.java` (gọi Smoother)
   - **Ảnh hưởng**: Vừa.
   - **Hướng xử lý**: Chuyển sang 1-Euro Filter hoặc adaptive EMA (smooth mạnh khi ít di chuyển, ít smooth khi di chuyển nhanh).

6. **Finish (Các loại chất liệu chưa đa dạng)**:
   - **Tình trạng**: `LipColorRenderer` dùng `PorterDuff.Mode.SCREEN` cho GLOSS, `MULTIPLY` cho TINT, `SRC_OVER` cho MATTE/SATIN. Cách làm này quá thô sơ, GLOSS màn hình trắng bệt, TINT lại quá tối.
   - **File**: `LipColorRenderer.java`
   - **Ảnh hưởng**: Lớn. Không thể hiện được khác biệt chất son.
   - **Hướng xử lý**: Định nghĩa `LipRenderProfile` với các tham số coverage, edge_feather, texture_retention, roughness, specular. 

7. **Color space**:
   - **Tình trạng**: Đang blend thẳng bằng canvas (sRGB).
   - **Ảnh hưởng**: Vừa/Nhỏ.
   - **Hướng xử lý**: Nếu dùng OpenGL sẽ xử lý tuyến tính hoá trước khi mix. Trong Canvas thì phải chịu giới hạn này hoặc xử lý qua pixel manipulation (CPU) nhưng sẽ chậm.

## II. Kế Hoạch Thay Đổi (Phần Giữ, Phần Refactor)

- **Phần Giữ Nguyên**: 
  - `ArCameraController`, `MlKitFaceMeshProvider` (Face tracker)
  - `ArTryOnViewModel`, `ArShadeAdapter`, Logic Add to Cart, Shade UI.
- **Phần Refactor / Thêm Mới**:
  - Viết lại `LipOverlayView` (hoặc thay bằng GLSurfaceView nếu cần OpenGL, nhưng trước tiên thử Canvas Advanced).
  - Viết lại `LipColorRenderer` thành `LipRealismRenderer` / `LipRenderProfile`.
  - Cải tiến `LipPathBuilder` để dùng `Op.DIFFERENCE` hoặc blur mask.
  - Sửa `LandmarkSmoother` thành `OneEuroLipSmoother`.
  - Thêm `LipLightingEstimator`.

(End of Audit)
