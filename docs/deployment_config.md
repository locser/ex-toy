# Configuration & Deployment cho 10K RPS Giveaway API

## 1. Application Configuration

```yaml
# application-production.yml
server:
  port: 8080
  tomcat:
    threads:
      max: 400           # Tăng thread pool
      min-spare: 50
    connection-timeout: 5000
    max-connections: 4000
    accept-count: 200
  compression:
    enabled: true
    min-response-size: 1024

spring:
  # Redis Configuration
  redis:
    host: redis-cluster
    port: 6379
    timeout: 1000ms
    lettuce:
      pool:
        max-active: 50
        max-wait: 1000ms
        max-idle: 20
        min-idle: 5
    cluster:
      nodes: redis1:6379,redis2:6379,redis3:6379
      
  # Kafka Configuration  
  kafka:
    bootstrap-servers: kafka1:9092,kafka2:9092,kafka3:9092
    producer:
      acks: 1
      retries: 0
      batch-size: 32768
      linger-ms: 5
      buffer-memory: 67108864
      compression-type: snappy
      
  # Database Configuration
  datasource:
    url: jdbc:mysql://mysql-primary:3306/giveaway_db?useSSL=false&rewriteBatchedStatements=true&cachePrepStmts=true
    username: app_user
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20    # Giảm vì chủ yếu Redis
      minimum-idle: 5
      connection-timeout: 3000
      idle-timeout: 300000
      
  # Cache Configuration
  cache:
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=30s
      
# Custom App Settings
app:
  gift:
    async-timeout: 100ms
    max-concurrent-claims: 1000
  rate-limit:
    user-per-minute: 10
    ip-per-minute: 100
  cache:
    campaign-ttl: 30s
    gift-counter-ttl: 5s
    
# JVM Settings
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 2. Docker Compose - Production Ready

```yaml
# docker-compose-production.yml
version: '3.8'

services:
  # Application instances (3 replicas)
  giveaway-api-1:
    image: giveaway-api:latest
    ports:
      - "8081:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_PASSWORD=secure_password
      - REDIS_HOST=redis-cluster
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'
        reservations:
          memory: 1G
          cpus: '1'
    depends_on:
      - redis-cluster
      - kafka-cluster
      - mysql-primary
      
  giveaway-api-2:
    image: giveaway-api:latest
    ports:
      - "8082:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_PASSWORD=secure_password
      - REDIS_HOST=redis-cluster
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'
        reservations:
          memory: 1G
          cpus: '1'
    depends_on:
      - redis-cluster
      - kafka-cluster
      - mysql-primary
      
  giveaway-api-3:
    image: giveaway-api:latest
    ports:
      - "8083:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_PASSWORD=secure_password
      - REDIS_HOST=redis-cluster
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'
        reservations:
          memory: 1G
          cpus: '1'
    depends_on:
      - redis-cluster
      - kafka-cluster
      - mysql-primary

  # Load Balancer
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - giveaway-api-1
      - giveaway-api-2
      - giveaway-api-3
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '1'

  # Redis Cluster (3 nodes)
  redis-1:
    image: redis:7-alpine
    command: redis-server --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    ports:
      - "7001:6379"
    volumes:
      - redis_1_data:/data
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1'

  redis-2:
    image: redis:7-alpine
    command: redis-server --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    ports:
      - "7002:6379"
    volumes:
      - redis_2_data:/data
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1'

  redis-3:
    image: redis:7-alpine
    command: redis-server --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000 --appendonly yes
    ports:
      - "7003:6379"
    volumes:
      - redis_3_data:/data
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1'

  # Kafka Cluster
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    volumes:
      - zookeeper_data:/var/lib/zookeeper/data

  kafka-1:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-1:9092
      KAFKA_NUM_PARTITIONS: 6
      KAFKA_DEFAULT_REPLICATION_FACTOR: 2
      KAFKA_LOG_RETENTION_MS: 86400000  # 1 day
    volumes:
      - kafka_1_data:/var/lib/kafka/data
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'

  kafka-2:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-2:9092
      KAFKA_NUM_PARTITIONS: 6
      KAFKA_DEFAULT_REPLICATION_FACTOR: 2
      KAFKA_LOG_RETENTION_MS: 86400000
    volumes:
      - kafka_2_data:/var/lib/kafka/data
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '2'

  # MySQL Primary
  mysql-primary:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: giveaway_db
      MYSQL_USER: app_user
      MYSQL_PASSWORD: secure_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./mysql/my.cnf:/etc/mysql/conf.d/custom.cnf:ro
      - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    deploy:
      resources:
        limits:
          memory: 4G
          cpus: '2'

  # Monitoring
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana

volumes:
  redis_1_data:
  redis_2_data:
  redis_3_data:
  kafka_1_data:
  kafka_2_data:
  zookeeper_data:
  mysql_data:
  grafana_data:
```

## 3. Nginx Load Balancer Configuration

```nginx
# nginx.conf
events {
    worker_connections 4096;
    use epoll;
    multi_accept on;
}

http {
    upstream giveaway_backend {
        least_conn;
        server giveaway-api-1:8080 max_fails=3 fail_timeout=30s;
        server giveaway-api-2:8080 max_fails=3 fail_timeout=30s;
        server giveaway-api-3:8080 max_fails=3 fail_timeout=30s;
        keepalive 100;
    }
    
    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=100r/s;
    limit_req_zone $http_x_user_id zone=user:10m rate=10r/s;
    
    server {
        listen 80;
        
        # Gzip compression
        gzip on;
        gzip_types application/json;
        
        location /api/v1/giveaway/claim {
            # Rate limiting
            limit_req zone=api burst=200 nodelay;
            limit_req zone=user burst=20 nodelay;
            
            # Proxy settings
            proxy_pass http://giveaway_backend;
            proxy_http_version 1.1;
            proxy_set_header Connection "";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            
            # Timeouts
            proxy_connect_timeout 1s;
            proxy_send_timeout 2s;
            proxy_read_timeout 2s;
            
            # Disable buffering for real-time response
            proxy_buffering off;
        }
        
        location /health {
            proxy_pass http://giveaway_backend;
            access_log off;
        }
    }
}
```

## 4. MySQL Optimization cho High Load

```ini
# mysql/my.cnf
[mysqld]
# Connection handling
max_connections = 500
max_connect_errors = 100000
connect_timeout = 10
wait_timeout = 28800

# Memory settings
innodb_buffer_pool_size = 2G
innodb_buffer_pool_instances = 8
innodb_log_buffer_size = 64M

# Write optimization
innodb_flush_log_at_trx_commit = 2
innodb_doublewrite = 0
sync_binlog = 0
innodb_flush_method = O_DIRECT

# IO settings
innodb_io_capacity = 2000
innodb_io_capacity_max = 4000
innodb_read_io_threads = 8
innodb_write_io_threads = 8

# Query optimization
query_cache_type = 0
query_cache_size = 0

# Logging
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

## 5. Database Schema Optimization

```sql
-- mysql/init.sql
CREATE DATABASE IF NOT EXISTS giveaway_db;
USE giveaway_db;

-- Campaign table (read-heavy, cached)
CREATE TABLE campaigns (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
    total_gifts INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_status_time (status, start_time, end_time)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED;

-- Gift inventory (Redis managed, MySQL backup)
CREATE TABLE gift_inventory (
    campaign_id VARCHAR(50) PRIMARY KEY,
    remaining_count INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
) ENGINE=InnoDB;

-- Participants (write-heavy, partitioned)
CREATE TABLE participants (
    id BIGINT AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    campaign_id VARCHAR(50) NOT NULL,
    gift_code VARCHAR(50),
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    
    PRIMARY KEY (id, claimed_at),
    UNIQUE KEY unique_user_campaign (user_id, campaign_id),
    INDEX idx_campaign_time (campaign_id, claimed_at),
    INDEX idx_user_time (user_id, claimed_at)
) ENGINE=InnoDB 
  ROW_FORMAT=COMPRESSED
  PARTITION BY RANGE (TO_DAYS(claimed_at)) (
    PARTITION p_current VALUES LESS THAN (TO_DAYS(NOW() + INTERVAL 1 DAY)),
    PARTITION p_future VALUES LESS THAN MAXVALUE
  );

-- Initialize sample data
INSERT INTO campaigns (id, name, start_time, end_time, total_gifts) VALUES
('CAMPAIGN_001', 'Summer Giveaway', UNIX_TIMESTAMP(NOW()) * 1000, (UNIX_TIMESTAMP(NOW()) + 86400) * 1000, 10000);

INSERT INTO gift_inventory (campaign_id, remaining_count) VALUES
('CAMPAIGN_001', 10000);
```

## 6. Performance Testing Script

```bash
#!/bin/bash
# load_test.sh

# Test with Apache Bench
ab -n 100000 -c 100 -H "Content-Type: application/json" -p claim_request.json http://localhost/api/v1/giveaway/claim

# Test with wrk (more advanced)
wrk -t20 -c400 -d60s --script=claim_test.lua http://localhost/api/v1/giveaway/claim

# JMeter test plan
jmeter -n -t giveaway_test_plan.jmx -l results.jtl
```

## 7. Monitoring & Alerting

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'giveaway-api'
    static_configs:
      - targets: ['giveaway-api-1:8080', 'giveaway-api-2:8080', 'giveaway-api-3:8080']
    metrics_path: '/actuator/prometheus'
    
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-1:6379', 'redis-2:6379', 'redis-3:6379']
      
  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-primary:3306']

rule_files:
  - "alert_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093
```

## 8. Key Performance Optimizations Summary

### Architecture Level:
- **3 App instances** behind Nginx load balancer
- **Redis Cluster** cho caching & atomic operations
- **Kafka Cluster** cho async message processing
- **Connection pooling** tối ưu ở mọi layer

### Application Level:
- **Multi-level caching**: Caffeine (L1) + Redis (L2)
- **Async processing** với CompletableFuture
- **Lua scripts** cho Redis atomic operations
- **Rate limiting** với sliding window
- **Fast timeout** (100ms) cho real-time response

### Database Level:
- **Minimal DB operations** (chỉ backup data)
- **Optimized MySQL config** cho write-heavy
- **Table partitioning** theo thời gian
- **Compressed row format**

### Network Level:
- **Keep-alive connections**
- **Gzip compression**
- **Connection pooling**
- **Load balancing** với health checks

## 9. Deployment Steps

```bash
# 1. Build application
./mvnw clean package -DskipTests

# 2. Build Docker image
docker build -t giveaway-api:latest .

# 3. Start infrastructure
docker-compose -f docker-compose-production.yml up -d redis-1 redis-2 redis-3 zookeeper kafka-1 kafka-2 mysql-primary

# 4. Initialize Redis cluster
docker exec -it redis-1 redis-cli --cluster create \
  redis-1:6379 redis-2:6379 redis-3:6379 \
  --cluster-replicas 0

# 5. Create Kafka topics
docker exec kafka-1 kafka-topics --create --topic giveaway-participants --partitions 6 --replication-factor 2 --bootstrap-server kafka-1:9092

# 6. Start application services
docker-compose -f docker-compose-production.yml up -d giveaway-api-1 giveaway-api-2 giveaway-api-3

# 7. Start load balancer
docker-compose -f docker-compose-production.yml up -d nginx

# 8. Verify deployment
curl -X POST http://localhost/api/v1/giveaway/claim \
  -H "Content-Type: application/json" \
  -d '{"userId":"test_user_1","campaignId":"CAMPAIGN_001"}'
```

## 10. Expected Performance Metrics

### Target Metrics:
- **Throughput**: 10,000+ RPS
- **Latency**: P99 < 100ms
- **Error Rate**: < 0.1%
- **CPU Usage**: < 70% per instance
- **Memory Usage**: < 80% per instance

### Bottleneck Analysis:
1. **Network I/O**: Handled by Nginx + Keep-alive
2. **CPU**: Distributed across 3 instances
3. **Memory**: Caffeine cache + Redis cluster
4. **Database**: Minimal usage, mostly Redis
5. **Kafka**: Async, non-blocking

## 11. Troubleshooting Guide

### Common Issues:
- **High Latency**: Check Redis cluster performance
- **Rate Limiting**: Tune Nginx + Application limits
- **Memory Issues**: Adjust Caffeine cache sizes
- **Database Locks**: Check MySQL slow query log
- **Kafka Lag**: Monitor consumer lag metrics

### Scaling Options:
- **Horizontal**: Add more app instances
- **Redis**: Add more cluster nodes
- **Database**: Add read replicas
- **Kafka**: Increase partitions