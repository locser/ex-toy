#!/bin/bash

# Build script for x-toy application Docker deployment
set -e

echo "🚀 Building x-toy application..."

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

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build the application
print_status "Building Docker image..."
docker build -t x-toy:latest .

if [ $? -eq 0 ]; then
    print_status "Docker image built successfully!"
else
    print_error "Failed to build Docker image"
    exit 1
fi

# Build and start services
print_status "Starting services with docker compose..."
docker compose up -d --build

if [ $? -eq 0 ]; then
    print_status "Services started successfully!"
    print_status "Application will be available at: http://localhost:1122"
    print_status "Health check: http://localhost:1122/actuator/health"
    print_status "API test: http://localhost:1122/api/test"

    echo ""
    print_warning "Waiting for services to be ready..."
    echo "You can check the logs with: docker compose logs -f x-toy-app"
else
    print_error "Failed to start services"
    exit 1
fi