# Toy Exchange Application

Ứng dụng trao đổi đồ chơi giúp người dùng có thể trao đổi đồ chơi với nhau trong các sự kiện/chiến dịch.

## Cấu trúc dự án

Dự án được tổ chức theo kiến trúc sạch (Clean Architecture) với các module sau:

```
x-toy/
├── toy-domain/         # Lớp miền (Domain Layer)
├── toy-application/    # Lớp ứng dụng (Application Layer)
├── toy-infrastructure/ # Lớp hạ tầng (Infrastructure Layer)
├── toy-controller/     # Lớp điều khiển (Controller Layer)
└── toy-starter/        # Module khởi động ứng dụng
```

## Kiến trúc ứng dụng

Ứng dụng tuân theo nguyên tắc của Clean Architecture với luồng phụ thuộc từ ngoài vào trong:

```
Controller (Adapter) -> Application Service -> Domain Service -> Domain Model
                                                  ^
                                                  |
Repository Implementation (Adapter) --------------+
```

### Các lớp chính

#### 1. Domain Layer (Lớp miền - trung tâm)
- **Domain Model**: Các entity, value object, domain events
- **Domain Service**: Chứa logic nghiệp vụ cốt lõi
- **Repository Interface**: Định nghĩa các cổng (port) để truy cập dữ liệu

#### 2. Application Layer (Lớp ứng dụng)
- **Application Service**: Điều phối các use case, gọi domain service
- **DTOs**: Đối tượng truyền dữ liệu giữa các lớp
- **Mappers**: Chuyển đổi giữa DTO và Domain Model

#### 3. Infrastructure Layer (Lớp hạ tầng)
- **Repository Implementations**: Triển khai repository interface
- **JPA Mappers**: Tương tác với cơ sở dữ liệu

#### 4. Controller Layer (Lớp điều khiển)
- **Controllers**: Xử lý HTTP request/response
- **Response Models**: Định dạng phản hồi API

## Luồng xử lý request

1. **Controller** nhận HTTP request và chuyển đổi thành DTO
2. **Application Service** điều phối use case và gọi Domain Service
3. **Domain Service** thực hiện logic nghiệp vụ cốt lõi
4. **Repository** lưu trữ và truy xuất dữ liệu
5. **Controller** trả về HTTP response

## Business Logic

Business logic được phân bố trong các lớp sau:

### 1. Domain Service

```
toy-domain/src/main/java/locser/toy/domain/service/impl/EventDomainServiceImpl.java
```

Đây là nơi chứa logic nghiệp vụ cốt lõi:
- Xác thực dữ liệu theo quy tắc nghiệp vụ
- Thực hiện các thao tác nghiệp vụ
- Đảm bảo tính toàn vẹn của dữ liệu

Ví dụ:
```java
// Xác thực ngày bắt đầu và kết thúc của sự kiện
private void validateEventDates(Event event) {
    if (event.getStartDate() != null && event.getEndDate() != null) {
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
    }
}
```

### 2. Application Service

```
toy-application/src/main/java/locser/toy/application/service/EventApplicationService.java
```

Điều phối các use case:
- Chuyển đổi giữa DTO và Domain Model
- Gọi Domain Service để xử lý logic nghiệp vụ
- Quản lý transaction

### 3. Domain Model

```
toy-domain/src/main/java/locser/toy/domain/model/entity/Event.java
```

Chứa các phương thức lifecycle và ràng buộc:
- `@PrePersist`: Logic trước khi lưu
- `@PreUpdate`: Logic trước khi cập nhật

## API Endpoints

### Event API

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | /api/v1/admin/campaigns | Tạo mới sự kiện |
| GET | /api/v1/campaigns | Lấy danh sách sự kiện |
| GET | /api/v1/campaigns/{id} | Lấy chi tiết sự kiện |
| PUT | /api/v1/admin/campaigns/{id} | Cập nhật sự kiện |
| DELETE | /api/v1/admin/campaigns/{id} | Xóa sự kiện |

## Cách tiếp cận code

### 1. Tìm hiểu Domain Model

Bắt đầu với các entity trong package `toy-domain/src/main/java/locser/toy/domain/model/entity/`:
- `Event.java`: Sự kiện/Chiến dịch
- `User.java`: Người dùng
- `Toy.java`: Đồ chơi
- ...

### 2. Tìm hiểu Business Logic

Kiểm tra logic nghiệp vụ trong các file:
- Domain Service: `toy-domain/src/main/java/locser/toy/domain/service/impl/`
- Application Service: `toy-application/src/main/java/locser/toy/application/service/`

### 3. Tìm hiểu API

Kiểm tra các controller trong package `toy-controller/src/main/java/locser/toy/controller/` hoặc `toy-starter/src/main/java/locser/`:
- `EventController.java`: API quản lý sự kiện

## Ví dụ sử dụng API

### Tạo mới sự kiện

```http
POST /api/v1/admin/campaigns
Content-Type: application/json

{
  "name": "Sự kiện trao đổi đồ chơi mùa hè",
  "description": "Trao đổi đồ chơi cho trẻ em trong dịp hè",
  "startDate": "2025-06-01T00:00:00",
  "endDate": "2025-06-30T23:59:59",
  "theme": "Mùa hè",
  "rules": "{\"maxToys\": 5, \"minAge\": 3, \"maxAge\": 12}"
}
```

### Lấy danh sách sự kiện

```http
GET /api/v1/campaigns
GET /api/v1/campaigns?status=UPCOMING
```

### Lấy chi tiết sự kiện

```http
GET /api/v1/campaigns/1
```

### Cập nhật sự kiện

```http
PUT /api/v1/admin/campaigns/1
Content-Type: application/json

{
  "name": "Sự kiện trao đổi đồ chơi mùa hè 2025",
  "status": "ACTIVE"
}
```

### Xóa sự kiện

```http
DELETE /api/v1/admin/campaigns/1
```

## Lợi ích của kiến trúc này

1. **Tách biệt mối quan tâm**: Mỗi lớp có trách nhiệm riêng biệt
2. **Độc lập với framework**: Domain layer không phụ thuộc vào Spring hoặc bất kỳ framework nào
3. **Dễ kiểm thử**: Có thể kiểm thử từng lớp một cách độc lập
4. **Linh hoạt**: Dễ dàng thay đổi infrastructure mà không ảnh hưởng đến logic nghiệp vụ
5. **Rõ ràng**: Luồng xử lý dữ liệu rõ ràng và dễ hiểu

## Hướng dẫn chạy ứng dụng

1. Clone repository
2. Cấu hình cơ sở dữ liệu trong `toy-starter/src/main/resources/application.yml`
3. Chạy lệnh: `./mvnw spring-boot:run -pl toy-starter`
4. Truy cập API tại: `http://localhost:8080/api/v1/campaigns`
