# Docker Deployment Guide - x-toy Application

Hướng dẫn triển khai ứng dụng x-toy trên Docker local.

## 📋 Yêu cầu hệ thống

- Docker 20.10+
- Docker Compose 2.0+
- RAM tối thiểu: 4GB
- Disk space: 2GB

## 🚀 Triển khai nhanh

### 1. Build và khởi chạy

```bash
# Sử dụng script build
./docker-scripts/build.sh

# Hoặc chạy thủ công
docker-compose up -d --build
```

### 2. Kiểm tra trạng thái

```bash
# Kiểm tra tất cả services
docker-compose ps

# Kiểm tra health
curl http://localhost:1122/actuator/health
```

### 3. Test API

```bash
# Test API cơ bản
curl http://localhost:1122/api/test

# Test API đơn giản
curl http://localhost:1122/api/test-simple
```

## 📊 Services

| Service   | Port | Description             | Health Check       |
| --------- | ---- | ----------------------- | ------------------ |
| x-toy-app | 1122 | Spring Boot Application | `/actuator/health` |
| MySQL     | 3306 | Database                | `mysqladmin ping`  |
| Redis     | 6379 | Cache                   | `redis-cli ping`   |

## 🛠 Quản lý

### Xem logs

```bash
# Xem logs application
./docker-scripts/logs.sh app

# Follow logs real-time
./docker-scripts/logs.sh -f

# Xem logs MySQL
./docker-scripts/logs.sh mysql

# Xem tất cả logs
./docker-scripts/logs.sh all -f
```

### Dừng services

```bash
# Dừng services (giữ lại data)
./docker-scripts/stop.sh

# Dừng và xóa volumes (mất data)
docker-compose down -v
```

### Restart services

```bash
# Restart application only
docker-compose restart x-toy-app

# Restart tất cả
docker-compose restart
```

## 🔧 Cấu hình

### Environment Variables

Các biến môi trường chính trong `docker-compose.yml`:

```yaml
# Database
DB_URL: jdbc:mysql://mysql:3306/java_demo
DB_USERNAME: root
DB_PASSWORD: password

# Redis
REDIS_HOST: redis
REDIS_PASSWORD: GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5

# JVM
JAVA_OPTS: -server -Xms512m -Xmx1024m -XX:+UseG1GC
```

### Volumes

- `mysql_data`: Dữ liệu MySQL
- `redis_data`: Dữ liệu Redis
- `app_logs`: Log files ứng dụng

## 🐛 Troubleshooting

### 1. Application không start

```bash
# Kiểm tra logs
./docker-scripts/logs.sh app

# Kiểm tra database connection
docker-compose exec mysql mysql -u root -p -e "SHOW DATABASES;"
```

### 2. Database connection error

```bash
# Kiểm tra MySQL status
docker-compose exec mysql mysqladmin ping -u root -p

# Reset database
docker-compose down mysql
docker volume rm x-toy_mysql_data
docker-compose up -d mysql
```

### 3. Redis connection error

```bash
# Test Redis connection
docker-compose exec redis redis-cli -a GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5 ping

# Reset Redis
docker-compose restart redis
```

### 4. Port conflicts

Nếu port bị conflict, sửa trong `docker-compose.yml`:

```yaml
services:
  x-toy-app:
    ports:
      - "8080:1122" # Thay đổi port external
```

## 📝 API Endpoints

### Health Check

```bash
curl http://localhost:1122/actuator/health
```

### Test Endpoints

```bash
# Basic test
curl http://localhost:1122/api/test

# Simple test
curl http://localhost:1122/api/test-simple
```

### Business APIs

```bash
# Get campaigns
curl http://localhost:1122/api/v1/campaigns

# Get toys
curl http://localhost:1122/api/v1/toys
```

## 🔄 Development Workflow

### 1. Code changes

```bash
# Rebuild chỉ application
docker-compose build x-toy-app
docker-compose up -d x-toy-app

# Hoặc rebuild toàn bộ
./docker-scripts/build.sh
```

### 2. Database changes

```bash
# Reset database schema
docker-compose down mysql
docker volume rm x-toy_mysql_data
docker-compose up -d
```

### 3. Debugging

```bash
# Vào container application
docker-compose exec x-toy-app bash

# Vào MySQL
docker-compose exec mysql mysql -u root -p

# Vào Redis
docker-compose exec redis redis-cli -a GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5
```

## 📊 Monitoring

### Health Checks

```bash
# Application health
curl http://localhost:1122/actuator/health

# Detailed health info
curl http://localhost:1122/actuator/health | jq .
```

### Metrics

```bash
# JVM metrics
curl http://localhost:1122/actuator/metrics

# Database metrics
curl http://localhost:1122/actuator/metrics/hikaricp.connections
```

### Resource Usage

```bash
# Container stats
docker stats

# Container resource usage
docker-compose top
```

## 🔒 Security Notes

1. **Production**: Thay đổi tất cả passwords mặc định
2. **Network**: Sử dụng custom bridge network
3. **User**: Application chạy với non-root user
4. **Secrets**: Sử dụng Docker secrets trong production

## 📁 File Structure

```
x-toy/
├── docker-compose.yml          # Main Docker Compose file
├── Dockerfile                  # Multi-stage build file
├── .dockerignore              # Docker ignore patterns
├── docker/
│   └── mysql/
│       └── init.sql           # MySQL initialization
├── docker-scripts/
│   ├── build.sh              # Build script
│   ├── stop.sh               # Stop script
│   └── logs.sh               # Logs script
└── toy-starter/src/main/resources/
    └── application-docker.yml  # Docker-specific config
```

## 🎯 Next Steps

1. **CI/CD**: Tích hợp với GitHub Actions/Jenkins
2. **Monitoring**: Thêm Prometheus + Grafana
3. **Logging**: Centralized logging với ELK stack
4. **Scaling**: Multi-instance deployment
5. **Production**: Kubernetes deployment

# Xem logs

docker logs x-toy-app -f

# Kiểm tra status

docker ps

# Stop all services

docker stop x-toy-app x-toy-mysql x-toy-redis

# Start lại

docker start x-toy-mysql x-toy-redis x-toy-app

# Check tại sao container x-toy-app lại tự động stop.

## Xác nhận việc container tự khởi động lại

```
  docker inspect --format='{{.RestartCount}}' x-toy-app
```

## Nếu kết quả > 0 → container đã bị restart nhiều lần.

## Xem lại 100 dòng gần nhất trong container

```
  docker logs --tail 100 x-toy-app
```

## 🔍 Kiểm tra health status:

```
  docker inspect --format='{{json .State.Health}}' x-toy-app
```

## Check Exit Code Meaning

docker inspect x-toy-app --format='Exit Code: {{.State.ExitCode}}'

```
 -> Exit Code: 137

 What Exit Code 137 Really Means:
 137 = 128 + 9 → killed by SIGKILL (kill -9)

 Most common reason in Docker: OOM (Out of Memory)

 The Linux kernel kills the container process when memory runs out to protect the host system
```

| Exit Code | Meaning                             |
| --------- | ----------------------------------- |
| `0`       | Exited normally (no error)          |
| `1`       | General error (exception, crash)    |
| `137`     | Killed (likely OOM — out of memory) |
| `143`     | Graceful stop (SIGTERM)             |
| `139`     | Segmentation fault                  |

## 🔍 Giải thích dòng này trong Dockerfile:

` ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"`

| Tham số                    | Ý nghĩa                                        |
| -------------------------- | ---------------------------------------------- |
| `-Xms512m`                 | JVM sẽ **khởi động với 512MB** bộ nhớ heap     |
| `-Xmx1024m`                | JVM có thể dùng tối đa **1024MB** heap         |
| `-XX:+UseContainerSupport` | JVM **nhận diện hạn chế tài nguyên container** |
| `-XX:+UseG1GC`             | Garbage Collector: tốt với app lớn, ổn định    |

## Khi container đang chạy

`docker stats x-toy-app`

`CONTAINER ID   NAME        CPU %     MEM USAGE / LIMIT     MEM %     NET I/O         BLOCK I/O       PIDS
a48468cbd050   x-toy-app   0.21%     657.7MiB / 1.914GiB   33.56%    26.9kB / 19kB   135MB / 135kB   36 `

## Docker Memory Management Summary

### **Problem**: Exit code 137 (container killed by Docker)

### **Root Cause**:

- Container exceeded Docker's memory limit (not physical RAM shortage)
- JVM heap + non-heap + system memory > Docker limit

### **Solution Applied**:

```dockerfile
# OLD CONFIG
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# NEW CONFIG
ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:+UseContainerSupport"
```

### **Memory Breakdown**:

```
JVM Heap:           768MB (was 1024MB)
+ Non-heap memory:  ~300MB
+ System memory:    ~200MB
= Total usage:      ~1.3GB (was ~1.5-1.8GB)
```

### **Result**:

- Docker stats: `657.7MiB / 1.914GiB (33.56%)`
- Container stable, no more exit code 137
- Trade-off: Slightly lower performance for better stability

### **Key Insight**:

Docker limit ≠ JVM allocation. Container total memory usage must stay under Docker's hard limit to avoid being killed.

## Fix Docker Memory with Colima - Summary

### **Problem**: Docker memory limit too low (2GB causing OOM exit code 137)

### **Solution**:

```bash
# 1. Stop Colima
colima stop

# 2. Start with more memory
colima start --memory 4 --cpu 4

# 3. Verify
colima list
docker system info | grep Memory
```

### **Result**:

- **Before**: 2GiB → **After**: 4GiB (3.814GiB available)
- CPU: 2 cores → 4 cores
- No more OOM kills (exit code 137)

### **Key Commands**:

- `colima list` - Check current resources
- `colima stop` - Stop VM
- `colima start --memory X --cpu Y` - Start with custom resources
- `docker system info` - Verify Docker sees new limits

### **Note**:

Colima creates a VM, so memory must be allocated at VM level, not just Docker container level.
