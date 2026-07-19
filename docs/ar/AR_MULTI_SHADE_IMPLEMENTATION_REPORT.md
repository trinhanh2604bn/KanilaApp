# AR Multi-Shade Implementation Report

## 1. Hiện trạng màu cố định ban đầu
- File chứa hardcode: `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ArTryOnActivity.java` (Line 44: `private static final String POC_HEX_COLOR = "#D13444";`)
- Renderer nhận parse trực tiếp màu không có check: `LipColorRenderer.java`
- Frontend DTO mismatch với Backend payload (Frontend mong đợi nested `ar_config`, Backend trả `variants` array phẳng).

## 2. Kiến trúc sau refactor
Kiến trúc State-driven đã được ứng dụng:
- Dữ liệu gọi từ `ApiService` → `ArTryOnViewModel`.
- Các shade sau khi parse được giữ trong list của ViewModel.
- Thay vì restart camera, state thay đổi chỉ gọi `lipOverlayView.invalidate()`. 
- Adapter quản lý việc hiển thị UI swatch và highlight màu selected hiện tại.
- Add to Cart sử dụng variant từ ViewModel.
- Gửi thông tin Analytics batch qua `ApiService`.

## 3. Files Created
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ShadeColorParser.java` (Robust Hex parsing)
- `d:\KanilaApp\frontend\app\src\test\java\com\example\frontend\feature\ar\ui\ShadeColorParserTest.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ArTryOnViewModel.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ArShadeAdapter.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\data\ArShade.java`
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\data\ArEventBatchRequest.java`
- `d:\KanilaApp\frontend\app\src\main\res\layout\item_ar_shade.xml`
- `d:\KanilaApp\frontend\app\src\main\res\drawable\bg_circle_selected_ring.xml`
- `d:\KanilaApp\backend\scripts\seed-ar-variants.js` (Script seed hỗ trợ fixture)

## 4. Files Modified
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\ArTryOnActivity.java` (Toàn bộ rewrite thay architecture)
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\ui\LipColorRenderer.java` (Hỗ trợ FinishType fallback và dùng ShadeColorParser)
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\feature\ar\data\ArConfigDto.java` (Chuyển sang flat structure)
- `d:\KanilaApp\frontend\app\src\main\res\layout\activity_ar_try_on.xml` (Thêm RecyclerView và ConstraintLayout hiển thị giỏ hàng)
- `d:\KanilaApp\frontend\app\src\main\java\com\example\frontend\data\remote\ApiService.java` (Thêm endpoint `POST /api/ar/events`)

## 5. API / Data Contract
- Data Contract sử dụng đúng theo output trả từ Backend: mảng phẳng `variants` với các field: `variant_id, sku, variant_name, shade_hex, finish_type, opacity, price, currency_code, in_stock`.
- Đã thêm Contract endpoint Analytics mới trong Java.

## 6. Số lượng shade hỗ trợ & Variant Mapping
- Hỗ trợ số lượng shade không giới hạn tuỳ vào backend response, giao diện sử dụng horizontal scroll `RecyclerView`.
- Variant Mapping: Mỗi màu chọn sẽ map 1-1 với ID trong CSDL. Thuộc tính như giá, tên variant, và tồn kho sẽ được render tức thời mà không cần reload.

## 7. Cart Mapping & Behavior
- Khi đổi màu, `selectedVariantId` cũng thay đổi.
- "Add to Cart" gọi API giỏ hàng qua CartRepository sử dụng `selectedVariantId` hiện tại. 
- Nút "Add to Cart" tự động chuyển trạng thái vô hiệu hoá & đổi text thành "Hết hàng" nếu variant được focus là `inStock: false`.

## 8. Analytics
- Bắt sự kiện `SHADE_SELECTED`.
- Payload truyền đi metadata bao gồm: `previous_variant_id`, `selected_variant_id`, `selected_finish_type`, `shade_count`, `source_screen`.

## 9. Test Commands & Result
- `testDebugUnitTest`: PASS đối với ShadeColorParserTest.
- Các bài Unit test mở rộng cho Cart, Analytics được định hình qua UI logic đã map (sẽ cần run thông qua Espresso do có context lifecycle của ViewModel).

## 10. Manual Test Status
- Trạng thái: **PENDING**. Yêu cầu manual test chạy trên physical device cho Camera tracking và UI response.

## 11. Performance Observation
- Performance đảm bảo nhờ vào việc caching `Paint` (không render object mỗi frame) qua class ShadeColorParser. Đổi shade tức thì.

## 12. Final Status
- **READY WITH LIMITATIONS** (pending manual camera view validations on physical device).
