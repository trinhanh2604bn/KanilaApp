# Walkthrough - Thêm Số điện thoại cho tài khoản Email

Tôi đã hoàn thành việc cập nhật hệ thống để cho phép người dùng đăng ký bằng Email có thể thêm Số điện thoại vào hồ sơ của mình, đồng thời đảm bảo an toàn cho thông tin đăng ký gốc.

## Các thay đổi đã thực hiện

### Backend

#### [account.controller.js](file:///D:/KanilaApp/backend/controllers/account.controller.js)
- Cập nhật hàm `getProfileHub` để trả về trường `registrationChannel` (kênh đăng ký: email hoặc phone). Thông tin này giúp Frontend biết được trường nào là thông tin gốc không được sửa.
- Đảm bảo logic trong `patchMyProfile` xử lý tốt việc cập nhật Số điện thoại (kiểm tra trùng lặp và định dạng).

### Frontend (Android)

#### [ProfileHubDto.java](file:///D:/KanilaApp/frontend/app/src/main/java/com/example/frontend/data/model/account/ProfileHubDto.java)
- Bổ sung trường `registrationChannel` vào mô hình dữ liệu để nhận thông tin từ API.

#### [page_profile_overview.xml](file:///D:/KanilaApp/frontend/app/src/main/res/layout/page_profile_overview.xml)
- Thêm các icon mũi tên (`ivEmailChevron`, `ivPhoneChevron`) cho dòng Email và Số điện thoại để chỉ dẫn trực quan trường nào có thể chỉnh sửa.

#### [ProfileOverviewFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/account/ProfileOverviewFragment.java)
- **Logic khóa thông tin gốc**:
    - Nếu đăng ký bằng **Email**: Ẩn mũi tên ở dòng Email và hiện thông báo "Email đăng ký không thể thay đổi" khi nhấn vào. Hiện mũi tên và cho phép chỉnh sửa Số điện thoại.
    - Nếu đăng ký bằng **SĐT**: Làm ngược lại (khóa SĐT, cho phép sửa Email).
- **Đồng bộ dữ liệu**: Đảm bảo trường `phone` được gửi lên server khi người dùng nhấn "Lưu thay đổi".

## Kết quả kiểm tra
- Tài khoản Email hiện đã có thể nhấn vào dòng "Số điện thoại" để nhập thông tin.
- Giao diện hiển thị rõ ràng thông qua icon mũi tên giúp người dùng biết mình được phép sửa gì.
- Thông tin đăng ký gốc (Email đối với acc Email, SĐT đối với acc SĐT) được bảo vệ, không cho phép thay đổi tùy tiện.
