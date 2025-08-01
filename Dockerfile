# Multi-stage build for x-toy Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy only pom files first for better layer caching
COPY pom.xml .
COPY common/pom.xml common/
COPY toy-domain/pom.xml toy-domain/
COPY toy-application/pom.xml toy-application/
COPY toy-infrastructure/pom.xml toy-infrastructure/
COPY toy-controller/pom.xml toy-controller/
COPY toy-starter/pom.xml toy-starter/

# Configure Maven for faster builds
ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository -Xmx2048m -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Download dependencies with parallel downloads
RUN mvn dependency:go-offline -B -T 1C

# Copy source code in optimal order (least changing files first)
COPY common/src common/src
COPY toy-domain/src toy-domain/src
COPY toy-application/src toy-application/src
COPY toy-infrastructure/src toy-infrastructure/src
COPY toy-controller/src toy-controller/src
COPY toy-starter/src toy-starter/src

# Build with optimizations
RUN mvn clean package -DskipTests -B -T 1C -Dmaven.compile.fork=true

# Runtime stage with optimized base image
FROM eclipse-temurin:21-jre-alpine

# Install curl for healthcheck (alpine version)
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1000 appuser && adduser -D -s /bin/sh -u 1000 -G appuser appuser

# Set working directory
WORKDIR /app

# Copy the built application
COPY --from=build --chown=appuser:appuser /app/toy-starter/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 1122

# Optimized health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:1122/actuator/health || exit 1

# Optimized JVM options for faster startup and better container performance
ENV JAVA_OPTS="-server \
    -Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -XX:+UseContainerSupport \
    -XX:MaxGCPauseMillis=200 \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/ \
    -XX:+TieredCompilation \
    -XX:TieredStopAtLevel=1 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.jmx.enabled=false \
    -Dspring.main.lazy-initialization=true"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]