# X-Toy Docker Setup Guide

## Overview

This guide explains how to run the X-Toy application using Docker with all its dependencies including MySQL, Redis, Prometheus, and Grafana.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose installed
- At least 4GB of available RAM
- At least 10GB of available disk space

## Quick Start

### 1. Start All Services

```bash
# Make scripts executable
chmod +x start-services.sh
chmod +x docker-scripts/*.sh

# Start all services
./start-services.sh start
```

### 2. Verify Services

```bash
# Check service status
./start-services.sh status

# View logs
./start-services.sh logs
```

### 3. Access Services

- **Application**: http://localhost:1122
- **Health Check**: http://localhost:1122/actuator/health
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

## Service Management

### Using start-services.sh

```bash
# Start all services
./start-services.sh start

# Stop all services
./start-services.sh stop

# Restart all services
./start-services.sh restart

# Build application
./start-services.sh build

# Show service status
./start-services.sh status

# View logs (default: x-toy-app)
./start-services.sh logs

# View specific service logs
./start-services.sh logs mysql
./start-services.sh logs redis

# Clean up everything
./start-services.sh cleanup

# Show help
./start-services.sh help
```

### Using docker-compose directly

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f x-toy-app
docker-compose logs -f mysql
docker-compose logs -f redis

# Restart specific service
docker-compose restart x-toy-app
```

## Monitoring

### Using monitor.sh

```bash
# Make script executable
chmod +x docker-scripts/monitor.sh

# Show real-time logs
./docker-scripts/monitor.sh logs

# Show resource usage
./docker-scripts/monitor.sh resources

# Show service health
./docker-scripts/monitor.sh health

# Show database statistics
./docker-scripts/monitor.sh db-stats

# Show application metrics
./docker-scripts/monitor.sh metrics

# Show all metrics
./docker-scripts/monitor.sh all

# Interactive monitoring menu
./docker-scripts/monitor.sh interactive

# Continuous monitoring dashboard
./docker-scripts/monitor.sh continuous
```

### Using Prometheus & Grafana

1. **Prometheus** (http://localhost:9090)

   - View metrics from the application
   - Check targets status
   - Create basic queries

2. **Grafana** (http://localhost:3000)
   - Default credentials: admin/admin
   - Add Prometheus as data source: http://prometheus:9090
   - Create dashboards for monitoring

## Backup and Restore

### Using backup.sh

```bash
# Make script executable
chmod +x docker-scripts/backup.sh

# Create full backup (MySQL + Redis + Logs)
./docker-scripts/backup.sh backup

# Backup only MySQL
./docker-scripts/backup.sh backup-mysql

# Backup only Redis
./docker-scripts/backup.sh backup-redis

# Backup only logs
./docker-scripts/backup.sh backup-logs

# List available backups
./docker-scripts/backup.sh list

# Restore MySQL from backup
./docker-scripts/backup.sh restore-mysql backups/mysql_backup_20241201_143022.sql

# Restore Redis from backup
./docker-scripts/backup.sh restore-redis backups/redis_backup_20241201_143022.rdb

# Clean old backups (older than 7 days)
./docker-scripts/backup.sh cleanup

# Clean old backups (older than 30 days)
./docker-scripts/backup.sh cleanup 30

# Show backup statistics
./docker-scripts/backup.sh stats
```

## Configuration

### Environment Variables

The application uses the following environment variables:

```bash
# Database
DB_URL=jdbc:mysql://mysql:3306/java_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=password

# Redis
REDIS_HOST=redis
REDIS_PASSWORD=GNwHez7OT53ftK5Ui3IOOlg1jUMwKT5

# Application
JAVA_OPTS=-server -Xms512m -Xmx1536m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxGCPauseMillis=200 -XX:+ExitOnOutOfMemoryError
SPRING_PROFILES_ACTIVE=docker
```

### Ports

- **1122**: Application
- **3306**: MySQL
- **6379**: Redis
- **9090**: Prometheus
- **3000**: Grafana

### Volumes

- `mysql_data`: MySQL data persistence
- `redis_data`: Redis data persistence
- `app_logs`: Application logs

## Troubleshooting

### Common Issues

1. **Port conflicts**

   ```bash
   # Check what's using the ports
   lsof -i :1122
   lsof -i :3306
   lsof -i :6379
   ```

2. **Insufficient memory**

   ```bash
   # Check Docker memory allocation
   docker stats
   ```

3. **Database connection issues**

   ```bash
   # Check MySQL container
   docker exec x-toy-mysql mysqladmin ping -h localhost

   # Check application logs
   ./start-services.sh logs x-toy-app
   ```

4. **Redis connection issues**
   ```bash
   # Check Redis container
   docker exec x-toy-redis redis-cli ping
   ```

### Debug Commands

```bash
# Check container status
docker-compose ps

# Check container logs
docker-compose logs

# Check resource usage
docker stats

# Access MySQL
docker exec -it x-toy-mysql mysql -u root -ppassword

# Access Redis
docker exec -it x-toy-redis redis-cli

# Access application container
docker exec -it x-toy-app /bin/bash
```

### Performance Tuning

1. **Increase Docker memory allocation** (Docker Desktop settings)
2. **Adjust JVM settings** in docker-compose.yml:

   ```yaml
   environment:
     JAVA_OPTS: -server -Xms1g -Xmx2g -XX:+UseG1GC
   ```

3. **Optimize MySQL settings**:
   ```sql
   SET GLOBAL innodb_buffer_pool_size = 256 * 1024 * 1024;
   SET GLOBAL max_connections = 200;
   ```

## Development

### Building from source

```bash
# Build application
./start-services.sh build

# Rebuild without cache
docker-compose build --no-cache
```

### Adding new services

1. Add service to `docker-compose.yml`
2. Update health checks
3. Add to monitoring scripts if needed

### Custom configurations

1. Modify `application-docker.yml` for application settings
2. Modify `docker-compose.yml` for service configurations
3. Modify `prometheus.yml` for monitoring settings

## Security Considerations

1. **Change default passwords** in production
2. **Use secrets management** for sensitive data
3. **Enable SSL/TLS** for external access
4. **Restrict network access** using Docker networks
5. **Regular security updates** for base images

## Production Deployment

1. **Use production-grade images**
2. **Implement proper logging**
3. **Set up monitoring and alerting**
4. **Configure backups**
5. **Use orchestration tools** (Kubernetes, Docker Swarm)

## Support

For issues and questions:

1. Check the troubleshooting section
2. Review application logs
3. Check service health status
4. Verify Docker and system resources
