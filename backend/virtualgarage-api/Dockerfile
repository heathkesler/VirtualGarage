# Build stage
FROM openjdk:21-jdk-slim as build

WORKDIR /workspace/app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY virtualgarage-*/pom.xml ./virtualgarage-*/

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY virtualgarage-common/src ./virtualgarage-common/src
COPY virtualgarage-auth/src ./virtualgarage-auth/src
COPY virtualgarage-vehicle/src ./virtualgarage-vehicle/src
COPY virtualgarage-parts/src ./virtualgarage-parts/src
COPY virtualgarage-build/src ./virtualgarage-build/src
COPY virtualgarage-notification/src ./virtualgarage-notification/src
COPY virtualgarage-integration/src ./virtualgarage-integration/src
COPY virtualgarage-api/src ./virtualgarage-api/src

# Build application
RUN ./mvnw clean package -DskipTests -B
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Runtime stage
FROM openjdk:21-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy from build stage
ARG DEPENDENCY=/workspace/app/virtualgarage-api/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Create logs directory
USER root
RUN mkdir -p /app/logs && chown spring:spring /app/logs
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-cp", "app:app/lib/*", \
           "-Djava.security.egd=file:/dev/./urandom", \
           "-Dspring.profiles.active=docker", \
           "com.virtualgarage.VirtualGarageApplication"]