#!/bin/bash

# Logs script for x-toy application Docker deployment

echo "📋 x-toy Application Logs"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Show usage if no argument provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 [service|option]"
    echo ""
    echo "Services:"
    echo "  app      - Show application logs"
    echo "  mysql    - Show MySQL logs"
    echo "  redis    - Show Redis logs"
    echo "  all      - Show all services logs"
    echo ""
    echo "Options:"
    echo "  -f       - Follow logs (default: app)"
    echo "  -tail N  - Show last N lines (default: 100)"
    echo ""
    echo "Examples:"
    echo "  $0 app           # Show app logs (last 100 lines)"
    echo "  $0 -f            # Follow app logs"
    echo "  $0 mysql -tail 50  # Show last 50 lines of MySQL logs"
    echo "  $0 all -f        # Follow all services logs"
    exit 1
fi

# Default values
SERVICE="app"
FOLLOW=""
TAIL="--tail=100"

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        app|mysql|redis|all)
            SERVICE="$1"
            shift
            ;;
        -f|--follow)
            FOLLOW="-f"
            TAIL="--tail=50"  # Reduce initial lines when following
            shift
            ;;
        -tail)
            TAIL="--tail=$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Map service names to container names
case $SERVICE in
    app)
        CONTAINER="x-toy-app"
        ;;
    mysql)
        CONTAINER="x-toy-mysql"
        ;;
    redis)
        CONTAINER="x-toy-redis"
        ;;
    all)
        CONTAINER=""
        ;;
esac

# Show logs
if [ "$SERVICE" = "all" ]; then
    print_status "Showing logs for all services..."
    docker compose logs $FOLLOW $TAIL
else
    print_status "Showing logs for $SERVICE ($CONTAINER)..."
    docker compose logs $FOLLOW $TAIL $SERVICE
fi