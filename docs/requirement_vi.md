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

#### Chiến Dịch Trao Đổi (Exchange Campaign)

- **C-001:** Là quản trị viên, tôi muốn tạo một chiến dịch trao đổi mới với các cài đặt cụ thể.
- **C-002:** Là quản trị viên, tôi muốn xem, chỉnh sửa và xóa các chiến dịch hiện có.
- **C-003:** Là bất kỳ người dùng nào, tôi muốn xem các chiến dịch đang hoạt động và đã kết thúc.
- **C-004:** Là bất kỳ người dùng nào, tôi muốn xem các cài đặt và quy tắc cụ thể cho mỗi chiến dịch.

#### Chiến Dịch Phát Đồ Chơi (Toy Giveaway Campaign) - MỚI

- **TC-001:** Là quản trị viên, tôi muốn tạo một chiến dịch phát đồ chơi với số lượng toy giới hạn.
- **TC-002:** Là quản trị viên, tôi muốn thêm các toy hiện có vào chiến dịch phát đồ chơi.
- **TC-003:** Là quản trị viên, tôi muốn xem thống kê chiến dịch (số người tham gia, số toy còn lại).
- **TC-004:** Là người dùng đã đăng nhập, tôi muốn xem danh sách chiến dịch phát toy đang diễn ra.
- **TC-005:** Là người dùng đã đăng nhập, tôi muốn nhấn nút "Nhận toy" để tham gia chiến dịch.
- **TC-006:** Là người dùng đã đăng nhập, tôi muốn xem lịch sử các chiến dịch đã tham gia.
- **TC-007:** Là người dùng đã đăng nhập, tôi muốn nhận thông báo khi chiến dịch mới được tạo.
- **TC-008:** Là hệ thống, tôi cần đảm bảo không có race condition khi nhiều user cùng nhận toy.
- **TC-009:** Là hệ thống, tôi cần tự động đóng chiến dịch khi hết toy hoặc hết thời gian.

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

#### Yêu Cầu Đặc Biệt Cho Chiến Dịch Phát Đồ Chơi

- **Concurrency**: Xử lý tối thiểu 100 request đồng thời cho việc nhận toy
- **Consistency**: Đảm bảo không có duplicate toy allocation
- **Performance**: API nhận toy phải phản hồi < 200ms
- **Availability**: Uptime 99.9% trong thời gian chiến dịch diễn ra

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

## Kế Hoạch Triển Khai Chiến Dịch Phát Quà

### Level 1: Basic Implementation (1-100 users)

#### Mục tiêu

- Triển khai chức năng cơ bản cho chiến dịch phát quà
- Hỗ trợ tối đa 100 người dùng đồng thời
- Đảm bảo tính nhất quán cơ bản

#### Thành phần cần triển khai

**1. Domain Layer**

- `GiftCampaign` entity với các trường cơ bản
- `GiftParticipation` entity để lưu lịch sử tham gia
- `CampaignType` enum (EXCHANGE, GIFT)
- `GiftCampaignStatus` enum
- Domain services cho business logic

**2. Application Layer**

- `GiftCampaignApplicationService`
- DTOs cho request/response
- Validation cơ bản

**3. Infrastructure Layer**

- Repository implementations
- Database migrations

**4. Controller Layer**

- REST APIs cho CRUD operations
- API tham gia chiến dịch

#### Ước tính thời gian: 2-3 tuần

### Level 2: Intermediate Implementation (100-1000 users)

#### Mục tiêu

- Tối ưu hóa performance với caching
- Xử lý concurrency tốt hơn
- Thêm monitoring và logging

#### Cải tiến

**1. Performance Optimization**

- Redis caching cho campaign data
- Database indexing optimization
- Connection pooling tuning

**2. Concurrency Control**

- Optimistic locking với version field
- Retry mechanism cho failed requests
- Rate limiting

**3. Monitoring & Observability**

- Metrics cho campaign participation
- Logging cho audit trail
- Health checks

**4. API Enhancements**

- Pagination cho danh sách campaigns
- Advanced filtering và sorting
- Bulk operations

#### Ước tính thời gian: 3-4 tuần

### Level 3: Advanced Implementation (1000+ users)

#### Mục tiêu

- Xử lý high-traffic scenarios
- Distributed system considerations
- Advanced features

#### Cải tiến

**1. Distributed Systems**

- Redis distributed locking
- Database sharding strategies
- Load balancing considerations

**2. Event-Driven Architecture**

- RabbitMQ cho async processing
- Event sourcing cho audit
- CQRS pattern implementation

**3. Advanced Features**

- Real-time notifications với WebSocket
- Advanced analytics và reporting
- A/B testing framework

**4. Scalability**

- Horizontal scaling strategies
- Microservices decomposition
- CDN cho static assets

#### Ước tính thời gian: 4-6 tuần

### Roadmap Tổng Thể

```
Tuần 1-3:   Level 1 - Basic Implementation
Tuần 4-7:   Level 2 - Performance & Monitoring
Tuần 8-13:  Level 3 - Advanced Features
Tuần 14:    Testing & Documentation
Tuần 15:    Deployment & Go-live
```

### Metrics Đánh Giá

**Level 1:**

- API response time < 500ms
- Support 100 concurrent users
- 0% data inconsistency

**Level 2:**

- API response time < 300ms
- Support 1000 concurrent users
- 99.5% uptime

**Level 3:**

- API response time < 200ms
- Support 10000+ concurrent users
- 99.9% uptime
- Real-time notifications < 1s delay
