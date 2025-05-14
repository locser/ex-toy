# Yêu Cầu Dự Án - Nền Tảng Trao Đổi Đồ Chơi

Tài liệu này phác thảo các yêu cầu chức năng và phi chức năng cho nền tảng.

## Yêu Cầu Chức Năng (User Stories)

### Quản Lý Người Dùng

- **U-001:** Là người dùng mới, tôi muốn đăng ký tài khoản bằng email và mật khẩu để có thể đăng đồ chơi và tạo yêu cầu trao đổi.
- **U-002:** Là người dùng đã đăng ký, tôi muốn đăng nhập vào tài khoản để truy cập các tính năng của nền tảng.
- **U-003:** Là người dùng đã đăng nhập, tôi muốn xem và chỉnh sửa thông tin hồ sơ của mình (ví dụ: tên, địa điểm).
- **U-004:** Là người dùng đã đăng nhập, tôi muốn xem danh sách đồ chơi của mình (cả trong và ngoài chiến dịch).
- **U-005:** Là người dùng đã đăng nhập, tôi muốn xem lịch sử trao đổi của mình (đã tạo, đã nhận, đã hoàn thành).

### Quản Lý Đồ Chơi

#### Quản Lý Cơ Bản

- **T-001:** Là người dùng đã đăng nhập, tôi muốn đăng một đồ chơi mới với các thông tin:
  - Thông tin cơ bản: tiêu đề, mô tả, danh mục, tình trạng
  - Hình ảnh: nhiều ảnh, chọn ảnh chính, sắp xếp thứ tự
  - Metadata: độ tuổi phù hợp, thương hiệu, giá gốc
  - Thông tin vận chuyển: cân nặng, kích thước
  - Sở thích trao đổi: danh mục mong muốn, tình trạng mong muốn, mô tả chi tiết
- **T-002:** Là người dùng đã đăng nhập, tôi muốn tải lên nhiều đồ chơi cùng lúc (batch upload).
- **T-003:** Là người dùng đã đăng nhập, tôi muốn chỉnh sửa thông tin đồ chơi của mình.
- **T-004:** Là người dùng đã đăng nhập, tôi muốn xóa đồ chơi của mình (soft delete).
- **T-005:** Là người dùng đã đăng nhập, tôi muốn khôi phục đồ chơi đã xóa.

#### Quản Lý Trạng Thái

- **T-006:** Là người dùng đã đăng nhập, tôi muốn thay đổi trạng thái đồ chơi:
  - `AVAILABLE`: Có sẵn để trao đổi
  - `PENDING_EXCHANGE`: Đang trong quá trình trao đổi
  - `EXCHANGED`: Đã được trao đổi
  - `HIDDEN`: Tạm ẩn
  - `DELETED`: Đã xóa
  - `DRAFT`: Bản nháp
  - `UNDER_REVIEW`: Đang chờ duyệt

#### Tích Hợp Chiến Dịch

- **T-007:** Là người dùng đã đăng nhập, tôi muốn thêm đồ chơi vào một chiến dịch đang hoạt động.
- **T-008:** Là người dùng đã đăng nhập, tôi muốn xóa đồ chơi khỏi một chiến dịch.
- **T-009:** Là người dùng đã đăng nhập, tôi muốn xem danh sách đồ chơi của mình trong từng chiến dịch.

### Quản Lý Chiến Dịch

- **C-001:** Là quản trị viên, tôi muốn tạo một chiến dịch trao đổi mới với các cài đặt cụ thể.
- **C-002:** Là quản trị viên, tôi muốn xem, chỉnh sửa và xóa các chiến dịch hiện có.
- **C-003:** Là bất kỳ người dùng nào, tôi muốn xem các chiến dịch đang hoạt động và đã kết thúc.
- **C-004:** Là bất kỳ người dùng nào, tôi muốn xem các cài đặt và quy tắc cụ thể cho mỗi chiến dịch.

### Quy Trình Trao Đổi

#### Quản Lý Trạng Thái Trao Đổi

- **E-001:** Là người dùng đã đăng nhập, tôi muốn khởi tạo yêu cầu trao đổi.
- **E-002:** Là chủ sở hữu đồ chơi, tôi muốn nhận thông báo khi có yêu cầu trao đổi mới.
- **E-003:** Là chủ sở hữu đồ chơi, tôi muốn chấp nhận hoặc từ chối yêu cầu trao đổi.
- **E-004:** Là người dùng tham gia trao đổi, tôi muốn giao tiếp với người dùng khác.
- **E-005:** Là người dùng tham gia trao đổi, tôi muốn xác nhận hoàn thành trao đổi.

### Tính Năng Bổ Sung (Mới)

#### Hệ Thống Tag và Phân Loại

- **TAG-001:** Là người dùng, tôi muốn thêm tag cho đồ chơi để dễ tìm kiếm.
- **TAG-002:** Là người dùng, tôi muốn tìm kiếm đồ chơi theo tag.
- **TAG-003:** Là quản trị viên, tôi muốn quản lý danh sách tag phổ biến.

#### Đánh Giá và Bình Luận

- **R-001:** Là người dùng, tôi muốn đánh giá người dùng khác sau khi trao đổi.
- **R-002:** Là người dùng, tôi muốn bình luận về đồ chơi.
- **R-003:** Là người dùng, tôi muốn xem đánh giá và bình luận của người khác.

#### Báo Cáo và Kiểm Duyệt

- **REP-001:** Là người dùng, tôi muốn báo cáo đồ chơi không phù hợp.
- **REP-002:** Là quản trị viên, tôi muốn xem và xử lý các báo cáo.
- **REP-003:** Là quản trị viên, tôi muốn kiểm duyệt đồ chơi trước khi cho phép đăng.

## Yêu Cầu Phi Chức Năng

### Hiệu Suất

- Thời gian phản hồi API < 500ms cho 95% request
- Hỗ trợ tối thiểu 1000 người dùng đồng thời
- Xử lý upload ảnh nhanh chóng với CDN

### Bảo Mật

- Sử dụng Spring Security cho xác thực và phân quyền
- Mã hóa mật khẩu với BCrypt
- Bảo vệ API với JWT
- Validate tất cả đầu vào

### Khả Năng Mở Rộng

- Thiết kế microservices
- Sử dụng cache (Redis) cho dữ liệu thường xuyên truy cập
- Hỗ trợ horizontal scaling

### Giao Diện

- RESTful API tuân thủ chuẩn
- API documentation với Swagger
- Xử lý lỗi và response nhất quán

### Công Nghệ

- Java 17+
- Spring Boot 2.7+
- Spring Data JPA
- Spring Security
- PostgreSQL
- Redis (cache)
- RabbitMQ (messaging)
- Docker
- Kubernetes (optional)
