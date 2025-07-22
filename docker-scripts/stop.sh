#!/bin/bash

# Stop script for x-toy application Docker deployment
set -e

echo "🛑 Stopping x-toy application..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Stop services
print_status "Stopping services..."
docker compose down

if [ $? -eq 0 ]; then
    print_status "Services stopped successfully!"
else
    print_error "Failed to stop services"
    exit 1
fi

# Option to remove volumes
read -p "Do you want to remove volumes (database data will be lost)? [y/N] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Removing volumes..."
    docker compose down -v
    print_status "Volumes removed!"
fi

print_status "Cleanup completed!"