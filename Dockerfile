# Multi-stage build for x-toy Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom files for dependency resolution
COPY pom.xml .
COPY common/pom.xml common/
COPY toy-domain/pom.xml toy-domain/
COPY toy-application/pom.xml toy-application/
COPY toy-infrastructure/pom.xml toy-infrastructure/
COPY toy-controller/pom.xml toy-controller/
COPY toy-starter/pom.xml toy-starter/

# Download dependencies (this layer will be cached if pom files don't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY common/src common/src
COPY toy-domain/src toy-domain/src
COPY toy-application/src toy-application/src
COPY toy-infrastructure/src toy-infrastructure/src
COPY toy-controller/src toy-controller/src
COPY toy-starter/src toy-starter/src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Install necessary tools
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built application
COPY --from=build /app/toy-starter/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 1122

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:1122/actuator/health || exit 1

# Set JVM options for containerized environment - optimized for memory
ENV JAVA_OPTS="-server -Xms256m -Xmx768m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxGCPauseMillis=200 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/ -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]