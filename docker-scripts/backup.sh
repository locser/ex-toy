#!/bin/bash

# X-Toy Docker Backup and Restore Script
# This script helps backup and restore data for the x-toy application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Backup directory
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if services are running
check_services() {
    if ! docker-compose ps | grep -q "Up"; then
        print_error "Services are not running. Start them first with: ./start-services.sh start"
        exit 1
    fi
    print_status "Services are running"
}

# Create backup directory
create_backup_dir() {
    mkdir -p "$BACKUP_DIR"
    print_status "Backup directory created: $BACKUP_DIR"
}

# Backup MySQL database
backup_mysql() {
    print_header "Backing up MySQL database"

    local backup_file="$BACKUP_DIR/mysql_backup_$DATE.sql"

    docker exec x-toy-mysql mysqldump -u root -ppassword \
        --single-transaction \
        --routines \
        --triggers \
        --all-databases > "$backup_file"

    if [ $? -eq 0 ]; then
        print_status "MySQL backup completed: $backup_file"
        print_status "Backup size: $(du -h "$backup_file" | cut -f1)"
    else
        print_error "MySQL backup failed"
        exit 1
    fi
}

# Backup Redis data
backup_redis() {
    print_header "Backing up Redis data"

    local backup_file="$BACKUP_DIR/redis_backup_$DATE.rdb"

    # Trigger Redis save
    docker exec x-toy-redis redis-cli --raw BGSAVE

    # Wait for save to complete
    sleep 5

    # Copy the dump file
    docker cp x-toy-redis:/data/dump.rdb "$backup_file"

    if [ $? -eq 0 ]; then
        print_status "Redis backup completed: $backup_file"
        print_status "Backup size: $(du -h "$backup_file" | cut -f1)"
    else
        print_error "Redis backup failed"
        exit 1
    fi
}

# Backup application logs
backup_logs() {
    print_header "Backing up application logs"

    local backup_file="$BACKUP_DIR/logs_backup_$DATE.tar.gz"

    docker cp x-toy-app:/app/logs ./temp_logs 2>/dev/null || mkdir -p temp_logs

    tar -czf "$backup_file" temp_logs/ 2>/dev/null || print_warning "No logs to backup"

    rm -rf temp_logs

    if [ -f "$backup_file" ]; then
        print_status "Logs backup completed: $backup_file"
        print_status "Backup size: $(du -h "$backup_file" | cut -f1)"
    fi
}

# Full backup
full_backup() {
    print_header "Starting full backup"

    create_backup_dir
    backup_mysql
    backup_redis
    backup_logs

    # Create backup manifest
    local manifest_file="$BACKUP_DIR/backup_manifest_$DATE.txt"
    cat > "$manifest_file" << EOF
X-Toy Backup Manifest
=====================
Date: $(date)
Backup ID: $DATE

Files:
- MySQL: mysql_backup_$DATE.sql
- Redis: redis_backup_$DATE.rdb
- Logs: logs_backup_$DATE.tar.gz

Services:
- Application: x-toy-app
- Database: x-toy-mysql
- Cache: x-toy-redis

Total backup size: $(du -sh "$BACKUP_DIR" | cut -f1)
EOF

    print_status "Full backup completed successfully!"
    print_status "Manifest: $manifest_file"
}

# Restore MySQL database
restore_mysql() {
    local backup_file="$1"

    if [ ! -f "$backup_file" ]; then
        print_error "Backup file not found: $backup_file"
        exit 1
    fi

    print_header "Restoring MySQL database from: $backup_file"

    # Stop application to prevent data corruption
    print_warning "Stopping application..."
    docker-compose stop x-toy-app

    # Restore database
    docker exec -i x-toy-mysql mysql -u root -ppassword < "$backup_file"

    if [ $? -eq 0 ]; then
        print_status "MySQL restore completed successfully"
    else
        print_error "MySQL restore failed"
        exit 1
    fi

    # Restart application
    print_status "Restarting application..."
    docker-compose start x-toy-app
}

# Restore Redis data
restore_redis() {
    local backup_file="$1"

    if [ ! -f "$backup_file" ]; then
        print_error "Backup file not found: $backup_file"
        exit 1
    fi

    print_header "Restoring Redis data from: $backup_file"

    # Stop Redis
    docker-compose stop redis

    # Copy backup file
    docker cp "$backup_file" x-toy-redis:/data/dump.rdb

    # Start Redis
    docker-compose start redis

    print_status "Redis restore completed successfully"
}

# List available backups
list_backups() {
    print_header "Available Backups"

    if [ ! -d "$BACKUP_DIR" ] || [ -z "$(ls -A "$BACKUP_DIR" 2>/dev/null)" ]; then
        print_warning "No backups found"
        return
    fi

    echo "MySQL Backups:"
    ls -lh "$BACKUP_DIR"/mysql_backup_*.sql 2>/dev/null || echo "  No MySQL backups"

    echo ""
    echo "Redis Backups:"
    ls -lh "$BACKUP_DIR"/redis_backup_*.rdb 2>/dev/null || echo "  No Redis backups"

    echo ""
    echo "Log Backups:"
    ls -lh "$BACKUP_DIR"/logs_backup_*.tar.gz 2>/dev/null || echo "  No log backups"

    echo ""
    echo "Manifests:"
    ls -lh "$BACKUP_DIR"/backup_manifest_*.txt 2>/dev/null || echo "  No manifests"
}

# Clean old backups
cleanup_backups() {
    local days=${1:-7}

    print_header "Cleaning backups older than $days days"

    find "$BACKUP_DIR" -name "*.sql" -mtime +$days -delete 2>/dev/null || true
    find "$BACKUP_DIR" -name "*.rdb" -mtime +$days -delete 2>/dev/null || true
    find "$BACKUP_DIR" -name "*.tar.gz" -mtime +$days -delete 2>/dev/null || true
    find "$BACKUP_DIR" -name "*.txt" -mtime +$days -delete 2>/dev/null || true

    print_status "Cleanup completed"
}

# Show backup statistics
show_stats() {
    print_header "Backup Statistics"

    if [ ! -d "$BACKUP_DIR" ]; then
        print_warning "No backup directory found"
        return
    fi

    echo "Backup Directory: $BACKUP_DIR"
    echo "Total size: $(du -sh "$BACKUP_DIR" | cut -f1)"
    echo ""

    echo "File counts:"
    echo "  MySQL backups: $(find "$BACKUP_DIR" -name "*.sql" | wc -l)"
    echo "  Redis backups: $(find "$BACKUP_DIR" -name "*.rdb" | wc -l)"
    echo "  Log backups: $(find "$BACKUP_DIR" -name "*.tar.gz" | wc -l)"
    echo "  Manifests: $(find "$BACKUP_DIR" -name "*.txt" | wc -l)"

    echo ""
    echo "Recent backups:"
    ls -lt "$BACKUP_DIR" | head -10
}

# Show help
show_help() {
    echo "X-Toy Docker Backup and Restore Script"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  backup              Create full backup (MySQL + Redis + Logs)"
    echo "  backup-mysql        Backup MySQL database only"
    echo "  backup-redis        Backup Redis data only"
    echo "  backup-logs         Backup application logs only"
    echo "  restore-mysql FILE  Restore MySQL database from backup file"
    echo "  restore-redis FILE  Restore Redis data from backup file"
    echo "  list                List available backups"
    echo "  cleanup [DAYS]      Clean backups older than DAYS (default: 7)"
    echo "  stats               Show backup statistics"
    echo "  help                Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 backup"
    echo "  $0 restore-mysql backups/mysql_backup_20241201_143022.sql"
    echo "  $0 cleanup 30"
    echo "  $0 list"
}

# Main script logic
main() {
    case "${1:-help}" in
        backup)
            check_services
            full_backup
            ;;
        backup-mysql)
            check_services
            create_backup_dir
            backup_mysql
            ;;
        backup-redis)
            check_services
            create_backup_dir
            backup_redis
            ;;
        backup-logs)
            check_services
            create_backup_dir
            backup_logs
            ;;
        restore-mysql)
            if [ -z "$2" ]; then
                print_error "Please specify backup file"
                exit 1
            fi
            check_services
            restore_mysql "$2"
            ;;
        restore-redis)
            if [ -z "$2" ]; then
                print_error "Please specify backup file"
                exit 1
            fi
            check_services
            restore_redis "$2"
            ;;
        list)
            list_backups
            ;;
        cleanup)
            cleanup_backups "$2"
            ;;
        stats)
            show_stats
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"