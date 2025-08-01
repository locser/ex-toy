#!/bin/bash

# X-Toy Docker Services Management Script
# This script helps manage the x-toy application and its dependencies

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_status "Docker is running"
}

# Build the application
build_app() {
    print_header "Building x-toy application"
    docker-compose build --no-cache
    print_status "Application built successfully"
}

# Start all services
start_services() {
    print_header "Starting x-toy services"

    # Check if services are already running
    # grafana: admin/admin
    if docker-compose ps | grep -q "Up"; then
        print_warning "Some services are already running. Stopping them first..."
        docker-compose down
    fi

    # Start services in background
    docker-compose up -d

    print_status "Services started. Waiting for them to be ready..."

    # Wait for services to be healthy
    wait_for_services

    print_status "All services are ready!"
    show_service_status
}

# Stop all services
stop_services() {
    print_header "Stopping x-toy services"
    docker-compose down
    print_status "Services stopped"
}

# Restart all services
restart_services() {
    print_header "Restarting x-toy services"
    docker-compose restart
    print_status "Services restarted"
}

# only start x-toy-app
start_app() {
    print_header "Starting x-toy-app"
    docker-compose up -d x-toy-app
    print_status "x-toy-app started"
}

# Show service status
show_service_status() {
    print_header "Service Status"
    docker-compose ps

    echo ""
    print_status "Service URLs:"
    echo "  Application: http://localhost:1122"
    echo "  Health Check: http://localhost:1122/actuator/health"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana: http://localhost:3000"
    echo "  MySQL: localhost:3307"
    echo "  Redis: localhost:6379"
    echo "  Elasticsearch: localhost:9200"
    echo "  Kibana: localhost:5601"
    echo "  Logstash: localhost:5044"
    echo "  MySQL Exporter: localhost:9104"
    echo "  Redis Exporter: localhost:9121"
    echo "  Node Exporter: localhost:9100"
}

# Wait for services to be healthy
wait_for_services() {
    local max_attempts=30
    local attempt=1

    print_status "Waiting for services to be ready..."

    while [ $attempt -le $max_attempts ]; do
        if docker-compose ps | grep -q "healthy"; then
            print_status "All services are healthy!"
            return 0
        fi

        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done

    print_warning "Some services may not be fully ready. Check with 'docker-compose ps'"
}

# Show logs
show_logs() {
    local service=${1:-"x-toy-app"}
    print_header "Showing logs for $service"
    docker-compose logs -f $service
}

# Clean up everything
cleanup() {
    print_header "Cleaning up all containers and volumes"
    docker-compose down -v
    docker system prune -f
    print_status "Cleanup completed"
}

# Show help
show_help() {
    echo "X-Toy Docker Services Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Start all services"
    echo "  startapp    Start x-toy-app"
    echo "  stop        Stop all services"
    echo "  restart     Restart all services"
    echo "  build       Build the application"
    echo "  status      Show service status"
    echo "  logs [SERVICE]  Show logs (default: x-toy-app)"
    echo "  cleanup     Remove all containers and volumes"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 logs mysql"
    echo "  $0 cleanup"
}

# Main script logic
main() {
    check_docker

    case "${1:-help}" in
        start)
            start_services
            ;;

        startapp)
            start_app
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        build)
            build_app
            ;;
        status)
            show_service_status
            ;;
        logs)
            show_logs "$2"
            ;;
        cleanup)
            cleanup
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