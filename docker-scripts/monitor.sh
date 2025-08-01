#!/bin/bash

# X-Toy Docker Monitoring Script
# This script provides monitoring capabilities for the x-toy application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

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

# Show real-time logs
show_realtime_logs() {
    print_header "Real-time Logs"
    print_status "Press Ctrl+C to stop monitoring"
    docker-compose logs -f --tail=50
}

# Show resource usage
show_resources() {
    print_header "Resource Usage"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
}

# Show service health
show_health() {
    print_header "Service Health"

    # Check application health
    if curl -s http://localhost:1122/actuator/health > /dev/null 2>&1; then
        print_status "Application: HEALTHY"
    else
        print_error "Application: UNHEALTHY"
    fi

    # Check MySQL
    if docker exec x-toy-mysql mysqladmin ping -h localhost > /dev/null 2>&1; then
        print_status "MySQL: HEALTHY"
    else
        print_error "MySQL: UNHEALTHY"
    fi

    # Check Redis
    if docker exec x-toy-redis redis-cli --raw incr ping > /dev/null 2>&1; then
        print_status "Redis: HEALTHY"
    else
        print_error "Redis: UNHEALTHY"
    fi
}

# Show database stats
show_db_stats() {
    print_header "Database Statistics"

    echo "MySQL Statistics:"
    docker exec x-toy-mysql mysql -u root -ppassword -e "
        SELECT
            table_schema as 'Database',
            ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
        FROM information_schema.tables
        WHERE table_schema = 'java_demo'
        GROUP BY table_schema;
    " 2>/dev/null || print_warning "Could not connect to MySQL"

    echo ""
    echo "Redis Statistics:"
    docker exec x-toy-redis redis-cli --raw info memory 2>/dev/null | grep -E "(used_memory|used_memory_peak)" || print_warning "Could not connect to Redis"
}

# Show application metrics
show_app_metrics() {
    print_header "Application Metrics"

    if curl -s http://localhost:1122/actuator/metrics > /dev/null 2>&1; then
        echo "JVM Metrics:"
        curl -s http://localhost:1122/actuator/metrics/jvm.memory.used | jq '.measurements[0].value' 2>/dev/null || echo "Memory usage: $(curl -s http://localhost:1122/actuator/metrics/jvm.memory.used | grep -o '"value":[0-9]*' | cut -d: -f2) bytes"

        echo ""
        echo "HTTP Metrics:"
        curl -s http://localhost:1122/actuator/metrics/http.server.requests | jq '.measurements[0].value' 2>/dev/null || echo "Total requests: $(curl -s http://localhost:1122/actuator/metrics/http.server.requests | grep -o '"value":[0-9]*' | cut -d: -f2)"
    else
        print_warning "Could not connect to application metrics"
    fi
}

# Show network connections
show_network() {
    print_header "Network Connections"
    docker exec x-toy-app netstat -tuln 2>/dev/null || print_warning "Could not get network info"
}

# Show error logs
show_errors() {
    print_header "Recent Errors"
    docker-compose logs --tail=100 | grep -i error || print_status "No recent errors found"
}

# Show slow queries (if any)
show_slow_queries() {
    print_header "Database Slow Queries"
    docker exec x-toy-mysql mysql -u root -ppassword -e "
        SELECT
            start_time,
            query_time,
            sql_text
        FROM mysql.slow_log
        WHERE start_time > DATE_SUB(NOW(), INTERVAL 1 HOUR)
        ORDER BY start_time DESC
        LIMIT 10;
    " 2>/dev/null || print_warning "No slow query log available"
}

# Interactive monitoring
interactive_monitor() {
    print_header "Interactive Monitoring"
    print_status "Choose monitoring option:"
    echo "1. Real-time logs"
    echo "2. Resource usage"
    echo "3. Service health"
    echo "4. Database stats"
    echo "5. Application metrics"
    echo "6. Network connections"
    echo "7. Error logs"
    echo "8. Slow queries"
    echo "9. All metrics"
    echo "0. Exit"

    read -p "Enter your choice (0-9): " choice

    case $choice in
        1) show_realtime_logs ;;
        2) show_resources ;;
        3) show_health ;;
        4) show_db_stats ;;
        5) show_app_metrics ;;
        6) show_network ;;
        7) show_errors ;;
        8) show_slow_queries ;;
        9)
            show_health
            show_resources
            show_db_stats
            show_app_metrics
            ;;
        0) exit 0 ;;
        *) print_error "Invalid choice" ;;
    esac
}

# Continuous monitoring
continuous_monitor() {
    print_header "Continuous Monitoring"
    print_status "Press Ctrl+C to stop"

    while true; do
        clear
        show_header "X-Toy Monitoring Dashboard"
        echo "$(date)"
        echo ""

        show_health
        echo ""
        show_resources
        echo ""
        show_db_stats

        sleep 10
    done
}

# Show help
show_help() {
    echo "X-Toy Docker Monitoring Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  logs        Show real-time logs"
    echo "  resources   Show resource usage"
    echo "  health      Show service health"
    echo "  db-stats    Show database statistics"
    echo "  metrics     Show application metrics"
    echo "  network     Show network connections"
    echo "  errors      Show recent errors"
    echo "  slow        Show slow queries"
    echo "  interactive Interactive monitoring menu"
    echo "  continuous  Continuous monitoring dashboard"
    echo "  all         Show all metrics"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 logs"
    echo "  $0 continuous"
    echo "  $0 all"
}

# Main script logic
main() {
    check_services

    case "${1:-help}" in
        logs)
            show_realtime_logs
            ;;
        resources)
            show_resources
            ;;
        health)
            show_health
            ;;
        db-stats)
            show_db_stats
            ;;
        metrics)
            show_app_metrics
            ;;
        network)
            show_network
            ;;
        errors)
            show_errors
            ;;
        slow)
            show_slow_queries
            ;;
        interactive)
            interactive_monitor
            ;;
        continuous)
            continuous_monitor
            ;;
        all)
            show_health
            show_resources
            show_db_stats
            show_app_metrics
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