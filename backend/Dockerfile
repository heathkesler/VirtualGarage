# Virtual Garage API Dockerfile
# Multi-stage build for optimized production image

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds, run tests separately)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create application user (security best practice)
RUN groupadd -r virtualgarage && useradd -r -g virtualgarage virtualgarage

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/virtual-garage-api-*.jar app.jar

# Create directories for logs and uploads
RUN mkdir -p /app/logs /app/uploads && \
    chown -R virtualgarage:virtualgarage /app

# Switch to non-root user
USER virtualgarage

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# JVM tuning for containerized environment
ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar app.jar"]