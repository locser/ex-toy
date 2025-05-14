# Hướng dẫn tích hợp Kafka

## Tổng quan

Tài liệu này cung cấp thông tin về cách sử dụng và cấu hình tích hợp Kafka trong ứng dụng Toy Exchange. Tích hợp Kafka được thiết kế để xử lý giao tiếp hướng sự kiện giữa các phần khác nhau của ứng dụng và có thể với các hệ thống bên ngoài.

## Tính năng

- Xuất bản sự kiện liên quan đến đồ chơi (tạo mới, cập nhật, thay đổi trạng thái, v.v.)
- Tiêu thụ và xử lý sự kiện
- Cấu hình topic và nhóm người tiêu dùng
- Kích hoạt dựa trên profile

## Cấu hình

### Dependencies

Tích hợp Kafka dựa vào các dependency sau:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### Thuộc tính ứng dụng

Thêm các thuộc tính sau vào file `application.yml` hoặc `application.properties`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: toy-exchange
      auto-offset-reset: earliest
    topics:
      toy-events: toy-events
      exchange-events: exchange-events
```

### Kích hoạt Kafka

Tích hợp Kafka bị vô hiệu hóa theo mặc định. Để kích hoạt nó, bạn cần kích hoạt profile `kafka`:

```bash
# Khi chạy ứng dụng
java -jar toy-starter.jar --spring.profiles.active=kafka

# Hoặc trong application.yml
spring:
  profiles:
    active: kafka
```

## Sử dụng

### Xuất bản sự kiện

Để xuất bản sự kiện đến Kafka, tiêm `ToyEventProducer` vào service của bạn:

```java
@Service
public class YourService {
    private final ToyEventProducer toyEventProducer;

    public YourService(ToyEventProducer toyEventProducer) {
        this.toyEventProducer = toyEventProducer;
    }

    public void createToy(Toy toy) {
        // Logic nghiệp vụ...
        
        // Xuất bản sự kiện
        toyEventProducer.publishToyCreatedEvent(
            toy.getId(),
            toy.getUserId(),
            toy.getName()
        );
    }
}
```

### Tiêu thụ sự kiện

Sự kiện được tự động tiêu thụ bởi `ToyEventConsumer` khi profile Kafka được kích hoạt. Consumer lắng nghe các topic đã cấu hình và xử lý sự kiện dựa trên loại của chúng.

Để thêm logic xử lý sự kiện tùy chỉnh, sửa đổi lớp `ToyEventConsumer` hoặc tạo một lớp consumer mới với chú thích `@KafkaListener`:

```java
@Component
@Profile("kafka")
public class CustomEventConsumer {
    @KafkaListener(topics = "${spring.kafka.topics.custom-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCustomEvent(CustomEvent event) {
        // Xử lý sự kiện
    }
}
```

## Loại sự kiện

Các loại sự kiện sau được hỗ trợ:

- `CREATED`: Một đồ chơi đã được tạo
- `UPDATED`: Một đồ chơi đã được cập nhật
- `DELETED`: Một đồ chơi đã bị xóa
- `STATUS_CHANGED`: Trạng thái của đồ chơi đã thay đổi
- `ADDED_TO_CAMPAIGN`: Một đồ chơi đã được thêm vào chiến dịch
- `REMOVED_FROM_CAMPAIGN`: Một đồ chơi đã bị xóa khỏi chiến dịch

## Các phương pháp hay nhất

1. **Xử lý lỗi**: Luôn xử lý ngoại lệ trong consumer sự kiện để ngăn chặn lỗi xử lý tin nhắn.
2. **Idempotency**: Thiết kế trình xử lý sự kiện để idempotent, vì tin nhắn có thể được gửi nhiều lần.
3. **Giám sát**: Thiết lập giám sát cho các topic và consumer Kafka để theo dõi xử lý tin nhắn.
4. **Kiểm thử**: Kiểm thử producer và consumer sự kiện với Kafka nhúng cho kiểm thử tích hợp.

## Xử lý sự cố

### Vấn đề phổ biến

1. **Connection Refused**: Đảm bảo Kafka đang chạy và có thể truy cập tại các bootstrap server đã cấu hình.
2. **Lỗi Serialization**: Kiểm tra rằng các lớp sự kiện có thể được serialized đúng cách.
3. **Consumer không xử lý**: Xác minh rằng ID nhóm consumer là chính xác và consumer đã đăng ký với topic đúng.

### Gỡ lỗi

Bật ghi log debug cho Kafka:

```yaml
logging:
  level:
    org.apache.kafka: DEBUG
    org.springframework.kafka: DEBUG
```

## Thiết lập Kafka cục bộ

1. Tải Kafka từ [trang web Apache Kafka](https://kafka.apache.org/downloads)
2. Giải nén file đã tải
3. Khởi động ZooKeeper:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
4. Khởi động máy chủ Kafka:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```
5. Tạo các topic cần thiết:
   ```bash
   bin/kafka-topics.sh --create --topic toy-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   bin/kafka-topics.sh --create --topic exchange-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```

## Tài liệu tham khảo

- [Tài liệu Spring for Apache Kafka](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Tài liệu Apache Kafka](https://kafka.apache.org/documentation/)
