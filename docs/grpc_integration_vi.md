# Hướng dẫn tích hợp gRPC

## Tổng quan

Tài liệu này cung cấp thông tin về cách sử dụng và cấu hình tích hợp gRPC trong ứng dụng Toy Exchange. Tích hợp gRPC cho phép giao tiếp hiệu suất cao, độc lập với ngôn ngữ giữa các dịch vụ.

## Tính năng

- Serialization nhị phân hiệu suất cao với Protocol Buffers
- Khả năng streaming hai chiều
- Kiểu dữ liệu mạnh với tạo mã
- Kích hoạt dựa trên profile
- Triển khai client và server

## Cấu hình

### Dependencies

Tích hợp gRPC dựa vào các dependency sau:

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>${protobuf.version}</version>
</dependency>
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

### Cấu hình Plugin

Tạo mã gRPC yêu cầu plugin Maven sau:

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protoc.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
        <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Thuộc tính ứng dụng

Thêm các thuộc tính sau vào file `application.yml` hoặc `application.properties`:

```yaml
grpc:
  server:
    port: 9090
    host: localhost
```

### Kích hoạt gRPC

Tích hợp gRPC bị vô hiệu hóa theo mặc định. Để kích hoạt nó, bạn cần kích hoạt profile `grpc` cho server và `grpc-client` cho client:

```bash
# Khi chạy server
java -jar toy-starter.jar --spring.profiles.active=grpc

# Khi chạy client
java -jar toy-starter.jar --spring.profiles.active=grpc-client

# Hoặc trong application.yml
spring:
  profiles:
    active: grpc,grpc-client
```

## Sử dụng

### Định nghĩa dịch vụ

Dịch vụ được định nghĩa trong các file Protocol Buffer (`.proto`). Ví dụ:

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "locser.infrastructure.grpc";
option java_outer_classname = "ToyServiceProto";

package toy;

service ToyService {
  rpc GetToy (ToyRequest) returns (ToyResponse) {}
  rpc CreateToy (CreateToyRequest) returns (ToyResponse) {}
  // Thêm phương thức...
}

// Định nghĩa message...
```

### Triển khai dịch vụ gRPC

Để triển khai dịch vụ gRPC, mở rộng lớp cơ sở dịch vụ được tạo:

```java
@Service
@Profile("grpc")
public class ToyGrpcService extends ToyServiceGrpc.ToyServiceImplBase {
    private final ToyApplicationService toyApplicationService;

    public ToyGrpcService(ToyApplicationService toyApplicationService) {
        this.toyApplicationService = toyApplicationService;
    }

    @Override
    public void getToy(ToyRequest request, StreamObserver<ToyResponse> responseObserver) {
        // Triển khai...
    }

    // Thêm triển khai phương thức...
}
```

### Sử dụng client gRPC

Để sử dụng client gRPC, tiêm `ToyGrpcClient` vào service của bạn:

```java
@Service
public class YourService {
    private final ToyGrpcClient toyGrpcClient;

    public YourService(ToyGrpcClient toyGrpcClient) {
        this.toyGrpcClient = toyGrpcClient;
    }

    public void doSomething(long toyId) {
        ToyResponse toy = toyGrpcClient.getToy(toyId);
        // Xử lý phản hồi...
    }
}
```

## Các phương pháp hay nhất

1. **Định nghĩa dịch vụ**: Giữ định nghĩa dịch vụ sạch sẽ và tập trung vào một miền duy nhất.
2. **Xử lý lỗi**: Sử dụng xử lý lỗi thích hợp với mã trạng thái và thông báo lỗi.
3. **Versioning**: Phiên bản API của bạn bằng cách bao gồm thông tin phiên bản trong tên gói.
4. **Bảo mật**: Sử dụng TLS cho giao tiếp an toàn trong sản xuất.
5. **Timeouts**: Cấu hình thời gian chờ thích hợp cho các cuộc gọi gRPC.

## Xử lý sự cố

### Vấn đề phổ biến

1. **Connection Refused**: Đảm bảo máy chủ gRPC đang chạy và có thể truy cập tại host và port đã cấu hình.
2. **Service Not Found**: Xác minh rằng dịch vụ được đăng ký đúng cách với máy chủ gRPC.
3. **Lỗi Serialization**: Kiểm tra rằng định nghĩa message khớp giữa client và server.

### Gỡ lỗi

Bật ghi log debug cho gRPC:

```yaml
logging:
  level:
    io.grpc: DEBUG
```

## Biên dịch file Proto

Để biên dịch file proto, chạy lệnh Maven sau:

```bash
mvn clean compile
```

Điều này sẽ tạo ra các lớp Java từ các file proto trong thư mục `target/generated-sources/protobuf`.

## Các bước triển khai

1. **Định nghĩa dịch vụ**: Tạo file proto trong thư mục `src/main/proto`
2. **Biên dịch file proto**: Chạy `mvn clean compile`
3. **Triển khai dịch vụ**: Tạo lớp dịch vụ mở rộng lớp cơ sở được tạo
4. **Đăng ký dịch vụ**: Cập nhật `GrpcConfig` để đăng ký dịch vụ với máy chủ
5. **Triển khai client**: Tạo client để gọi dịch vụ

## Tài liệu tham khảo

- [Tài liệu gRPC](https://grpc.io/docs/)
- [Tài liệu Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview)
- [Repository GitHub gRPC-Java](https://github.com/grpc/grpc-java)
