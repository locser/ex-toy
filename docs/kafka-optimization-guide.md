# Kafka Optimization Guide

## Tổng quan về các tối ưu đã implement

Đã implement 2 tối ưu chính cho Kafka system:

### ## 1. Tối ưu Batch Size động (Dynamic Batch Size Optimization)

**Mục đích**: Tự động điều chỉnh batch size dựa trên performance metrics để tối ưu throughput và latency.

**Components đã thêm**:
- Kafka Consumer: Xử lý message từ Kafka topics
- Enhanced `ToyEventConsumer`: Tích hợp với batch size manager
- `KafkaPerformanceMonitor`: Monitor và báo cáo performance

**Cách hoạt động**:
1. Bắt đầu với initial batch size (default: 50)
2. Monitor processing time và success rate của mỗi batch
3. Tự động tăng batch size nếu processing nhanh và thành công
4. Tự động giảm batch size nếu processing chậm hoặc thất bại
5. Giữ batch size trong khoảng min-max (10-500)

**Configuration**:
```yaml
kafka:
  batch:
    size:
      min: 10           # Minimum batch size
      max: 500          # Maximum batch size  
      initial: 50       # Starting batch size
    optimization:
      target-latency-ms: 1000      # Target processing latency
      throughput-threshold: 100    # Messages per second threshold
      adjustment-factor: 0.2       # Batch size adjustment factor (20%)
```

### ## 2. Kafka Consumer Configuration tối ưu

**Mục đích**: Tối ưu các cấu hình consumer để tăng performance và stability.

**Các tối ưu chính**:

#### A. Fetch Configuration (Tối ưu network round trips)
```yaml
kafka:
  consumer:
    fetch-min-size: 1024        # Minimum 1KB per fetch
    fetch-max-wait: 500         # Maximum 500ms wait time
    max-poll-records: 500       # Maximum 500 records per poll
```

**Note**: 
- `fetch-min-size`: Giảm số lần fetch nhỏ, tăng efficiency
- `fetch-max-wait`: Balance giữa latency và throughput
- `max-poll-records`: Tăng số records per poll để batch processing hiệu quả hơn

#### B. Session Management (Tối ưu consumer group stability)
```yaml
kafka:
  consumer:
    session-timeout: 30000      # 30 seconds session timeout
    heartbeat-interval: 10000   # 10 seconds heartbeat interval
    max-poll-interval: 300000   # 5 minutes max poll interval
```

**Note**:
- `session-timeout`: Tăng từ default 10s lên 30s để giảm false positive rebalancing
- `heartbeat-interval`: 1/3 của session timeout (best practice)
- `max-poll-interval`: Đủ thời gian cho batch processing phức tạp

#### C. Memory and Buffer Optimization
```java
props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 65536);  // 64KB receive buffer
props.put(ConsumerConfig.SEND_BUFFER_CONFIG, 131072);    // 128KB send buffer
```

**Note**: Tăng buffer size để giảm system calls và tăng throughput

#### D. Connection Optimization
```java
props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 540000); // 9 minutes
props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);       // 30s
props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 100);           // 100ms
```

**Note**: Tối ưu connection pooling và retry behavior

#### E. Partition Assignment Strategy
```java
props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, 
          "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
```

**Note**: Sử dụng CooperativeStickyAssignor để giảm rebalancing time và tăng stability

#### F. Manual Acknowledgment
```java
containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
```

**Note**: Manual commit để control tốt hơn việc offset commit, tránh data loss

## Cách sử dụng

### 1. Enable Kafka Optimized Profile

Thêm profile `kafka-optimized` vào application:

```yaml
spring:
  profiles:
    active: kafka,kafka-optimized
```

Hoặc set environment variable:
```bash
SPRING_PROFILES_ACTIVE=kafka,kafka-optimized
```

### 2. Configuration Override

Có thể override các config thông qua environment variables:

```bash
# Dynamic Batch Size
KAFKA_BATCH_SIZE_MIN=20
KAFKA_BATCH_SIZE_MAX=1000
KAFKA_BATCH_SIZE_INITIAL=100

# Consumer Optimization
KAFKA_CONSUMER_CONCURRENCY=5
KAFKA_FETCH_MIN_SIZE=2048
KAFKA_MAX_POLL_RECORDS=1000
```

### 3. Monitoring

#### Health Check
```bash
curl http://localhost:8080/actuator/health/kafkaPerformanceMonitor
```

#### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

#### Logs
Batch metrics sẽ được log định kỳ:
```
INFO  - === Kafka Performance Metrics ===
INFO  - Current Batch Size: 75
INFO  - Total Processed Messages: 1500
INFO  - Average Latency: 45.67ms per message
INFO  - Current Throughput: 125.34 messages/second
INFO  - Consecutive Successful Batches: 12
INFO  - Consecutive Slow Batches: 0
INFO  - ================================
INFO  - Performance Analysis: EXCELLENT LATENCY (45.67ms) - HIGH THROUGHPUT (125.34 msg/s) - BATCH SIZE WELL OPTIMIZED (12 consecutive successful batches) - PERFORMANCE OPTIMAL
```

## Performance Tuning Tips

### 1. Batch Size Tuning
- **High latency**: Giảm `max-batch-size` hoặc `target-latency-ms`
- **Low throughput**: Tăng `max-batch-size` hoặc `consumer-concurrency`
- **Memory issues**: Giảm `max-batch-size` và `max-poll-records`

### 2. Consumer Tuning
- **Frequent rebalancing**: Tăng `session-timeout` và `max-poll-interval`
- **Network issues**: Tăng `fetch-max-wait` và buffer sizes
- **High CPU**: Giảm `consumer-concurrency` hoặc `max-poll-records`

### 3. Topic Configuration
Đảm bảo topic có đủ partitions cho parallel processing:
```bash
# Tạo topic với multiple partitions
kafka-topics.sh --create --topic toy-events --partitions 6 --replication-factor 3
```

### 4. JVM Tuning
```bash
# Tăng heap size cho high throughput
-Xmx2g -Xms2g

# G1GC cho low latency
-XX:+UseG1GC -XX:MaxGCPauseMillis=100
```

## Troubleshooting

### Common Issues

1. **OutOfMemoryError**
   - Giảm `max-poll-records` và `max-batch-size`
   - Tăng JVM heap size

2. **Consumer lag tăng cao**
   - Tăng `consumer-concurrency`
   - Optimize business logic processing
   - Tăng `max-poll-records`

3. **Frequent rebalancing**
   - Tăng `session-timeout` và `max-poll-interval`
   - Check network stability
   - Optimize processing time

4. **High latency**
   - Giảm `target-latency-ms`
   - Giảm `fetch-max-wait`
   - Tăng `consumer-concurrency`

### Debug Commands

```bash
# Check consumer group status
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group toy-exchange-optimized --describe

# Monitor topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic toy-events --from-beginning

# Check topic configuration
kafka-topics.sh --bootstrap-server localhost:9092 --topic toy-events --describe
```

## Migration Guide

### Từ cấu hình cũ sang cấu hình mới:

1. **Backup current configuration**
2. **Update dependencies** (nếu cần)
3. **Add new configuration files**
4. **Test in staging environment**
5. **Monitor performance metrics**
6. **Gradual rollout to production**

### Rollback Plan

Nếu có issues, có thể rollback bằng cách:
1. Remove `kafka-optimized` profile
2. Restart application với cấu hình cũ
3. Monitor consumer lag recovery

## Best Practices

1. **Always monitor** batch metrics và performance
2. **Test thoroughly** trước khi deploy production
3. **Gradual tuning** - thay đổi từng config một
4. **Document changes** và performance impact
5. **Set up alerts** cho performance degradation
6. **Regular review** của configuration dựa trên usage patterns

## Environment-Specific Recommendations

### Development
```yaml
kafka:
  batch:
    size:
      min: 5
      max: 50
      initial: 10
  consumer:
    concurrency: 1
```

### Staging
```yaml
kafka:
  batch:
    size:
      min: 10
      max: 200
      initial: 25
  consumer:
    concurrency: 2
```

### Production
```yaml
kafka:
  batch:
    size:
      min: 20
      max: 500
      initial: 50
  consumer:
    concurrency: 3-5
```