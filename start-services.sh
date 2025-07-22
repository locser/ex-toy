#!/bin/bash

# Start x-toy services manually (alternative to docker-compose)
set -e

echo "🚀 Starting x-toy services..."

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Create network
print_status "Creating network..."
docker network create x-toy-network 2>/dev/null || print_warning "Network already exists"

# Start MySQL
print_status "Starting MySQL..."
docker run -d --name x-toy-mysql \
  --network x-toy-network \
  -p 3306:3306 \
  -e MYSQL_DATABASE=java_demo \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=password \
  -v mysql_data:/var/lib/mysql \
  -v ./docker/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql:ro \
  --restart unless-stopped \
  mysql:8.0 2>/dev/null || print_warning "MySQL already running"

# Start Redis
print_status "Starting Redis..."
docker run -d --name x-toy-redis \
  --network x-toy-network \
  -p 6379:6379 \
  -v redis_data:/data \
  --restart unless-stopped \
  redis:7.2-alpine \
  redis-server --requirepass GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5 2>/dev/null || print_warning "Redis already running"

# Wait for services
print_status "Waiting for services to be ready..."
sleep 10

# Build and start app
print_status "Building and starting x-toy app..."
docker build -t x-toy-app:latest .

docker build -t x-toy-app . && docker run -d --name x-toy-app -p 1122:1122 -e "DB_URL=jdbc:mysql://host.docker.internal:3307/java_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" -e "DB_USERNAME=root" -e "DB_PASSWORD=password" -e "REDIS_HOST=host.docker.internal" -e "REDIS_PASSWORD=GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5" -e "JAVA_OPTS=-server -Xms512m -Xmx1536m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxGCPauseMillis=200 -XX:+ExitOnOutOfMemoryError -Dspring.profiles.active=docker" -e "SPRING_PROFILES_ACTIVE=docker" --memory=2g x-toy-app

print_status "Services started successfully!"
print_status "Application available at: http://localhost:1122"
print_status "Health check: http://localhost:1122/actuator/health"

echo ""
print_status "Service status:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"