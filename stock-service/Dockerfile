# Multi-stage build for stock-service
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy only pom and .mvn wrapper first (better layer caching)
COPY stock-service/pom.xml .
COPY stock-service/.mvn ./.mvn
COPY stock-service/mvnw .

# Download dependencies (separate layer for cache efficiency)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -DskipTests

# Copy source code
COPY stock-service/src ./src

# Build the application
RUN ./mvnw -DskipTests clean package

# Runtime stage — minimal base image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN useradd -m -u 1001 -s /sbin/nologin appuser && mkdir -p /data && chown -R appuser:appuser /data

# Copy the JAR from builder stage
COPY --from=builder --chown=appuser:appuser /app/target/*.jar app.jar

# Expose port
EXPOSE 4001

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:4001/actuator/health || exit 1

# Run the application with docker profile and memory optimizations
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75", "-Dspring.profiles.active=docker", "-jar", "/app/app.jar"]
