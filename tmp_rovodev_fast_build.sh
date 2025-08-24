#!/bin/bash

echo "🚀 Fast Docker build process..."

# Step 1: Always rebuild JAR from source
JAR_FILE="toy-starter/target/toy-starter-0.0.1-SNAPSHOT.jar"

echo "📦 Rebuilding Java source with Maven..."
mvn clean package -DskipTests -B

# Check if build succeeded
if [ $? -ne 0 ]; then
    echo "❌ Maven build failed!"
    exit 1
fi

# Check if JAR was created
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found after build: $JAR_FILE"
    exit 1
fi

echo "✅ JAR built successfully!"

# Step 2: Build Docker image using the fast Dockerfile
echo "🐳 Building Docker image..."
docker build -f Dockerfile.fast -t x-toy-app .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully!"
    echo "🎉 You can now run: docker-compose up -d or ./start-services.sh startapp"
    echo "💡 Image 'x-toy-app' is ready to use with docker-compose.yml"
else
    echo "❌ Docker build failed!"
    exit 1
fi