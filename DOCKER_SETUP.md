# Docker Setup Guide

## Overview

This project contains 3 microservices orchestrated with Docker Compose:
- **vendor-a-service** (Port 9000) - Product API for Vendor A
- **stock-service** (Port 4001) - Main stock synchronization service  
- **stock-sync-scheduler** (Port 8080) - Scheduled sync orchestrator

## Prerequisites

- Docker (version 20.10+)
- Docker Compose (version 1.29+)
- At least 2GB free disk space
- Ports 9000, 4001, 8080 available

## Quick Start

### Build and Run All Services

```bash
cd C:\Users\naisa\Downloads\stock
docker-compose up -d
```

### View Service Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f vendor-a-service
docker-compose logs -f stock-service
docker-compose logs -f stock-sync-scheduler
```

### Check Service Health

```bash
# Vendor A Service
curl http://localhost:9000/actuator/health

# Stock Service
curl http://localhost:4001/actuator/health

# Stock Sync Scheduler
curl http://localhost:8080/actuator/health
```

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Data

```bash
docker-compose down -v
```

---

## Service Details

### 1. Vendor A Service (Port 9000)

**Purpose:** Exposes product API for Vendor A

**Endpoints:**
- `GET /vendor-a/products` - List all products
- `GET /swagger-ui/index.html` - API documentation

**Database:** H2 (file-based at `/data/vendorastockdb`)

**Environment Variables:**
- `SERVER_PORT=9000`
- `SPRING_H2_CONSOLE_ENABLED=true`

**Health Check:** `http://localhost:9000/actuator/health`

---

### 2. Stock Service (Port 4001)

**Purpose:** Main synchronization service that aggregates product data

**Endpoints:**
- `GET /api/products` - List all products
- `POST /api/products/sync` - Sync products (accepts vendor type)
- `GET /swagger-ui/index.html` - API documentation
- `GET /actuator/health` - Health status

**Database:** H2 (file-based at `/data/stockdb`)

**Dependencies:** Connects to vendor-a-service via `http://vendor-a-service:9000`

**Environment Variables:**
- `SERVER_PORT=4001`
- `SPRING_PROFILES_ACTIVE=docker`
- `API_VENDOR_A_SERVICE_BASE_URL=http://vendor-a-service:9000`

**Health Check:** `http://localhost:4001/actuator/health`

---

### 3. Stock Sync Scheduler (Port 8080)

**Purpose:** Scheduled tasks for automatic stock synchronization

**Cron Schedules:**
- Vendor A: Every 10 minutes (00, 10, 20, 30, 40, 50)
- Vendor B: Every 10 minutes (05, 15, 25, 35, 45, 55)

**Timezone:** UTC

**Dependencies:** Connects to stock-service via `http://stock-service:4001`

**Environment Variables:**
- `SPRING_PROFILES_ACTIVE=docker`
- `API_STOCK_SYNC_SERVICE_BASE_URL=http://stock-service:4001`
- `STOCK_SYNC_VENDORA_CRON=0 0/10 * * * *`

**Health Check:** `http://localhost:8080/actuator/health`

---

## Testing the Setup

### 1. Verify All Services Are Running

```bash
docker ps
```

Expected output: 3 containers running (`vendor-a-service`, `stock-service`, `stock-sync-scheduler`)

### 2. Test Vendor A Service

```bash
# Get all products from Vendor A
curl http://localhost:9000/vendor-a/products

# H2 Console (browser)
# http://localhost:9000/h2-console
# JDBC URL: jdbc:h2:file:/data/vendorastockdb
# Username: sa
# Password: password
```

### 3. Test Stock Service

```bash
# Get all products
curl http://localhost:4001/api/products

# Sync products for Vendor A
curl -X POST http://localhost:4001/api/products/sync \
  -H "Content-Type: text/plain" \
  -d "VENDOR_A_API"

# Sync products for Vendor B
curl -X POST http://localhost:4001/api/products/sync \
  -H "Content-Type: text/plain" \
  -d "VENDOR_B_CSV"

# H2 Console (browser)
# http://localhost:4001/h2-console
# JDBC URL: jdbc:h2:file:/data/stockdb
# Username: sa
# Password: password
```

### 4. Test Stock Sync Scheduler

```bash
# Check health
curl http://localhost:8080/actuator/health
```

---

## Docker Network

All services communicate over the `stock-network` bridge network:
- `vendor-a-service:9000`
- `stock-service:4001`
- `stock-sync-scheduler:8080`

**Note:** Service names are DNS resolvable within the Docker network (e.g., `http://vendor-a-service:9000`)

---

## Volumes and Data Persistence

### Volumes Created:
- `vendor-a-data` - Persists Vendor A H2 database
- `stock-data` - Persists Stock Service H2 database

### Clean Up Data:
```bash
docker-compose down -v
```

---

## Troubleshooting

### Services Won't Start

1. **Check logs:**
   ```bash
   docker-compose logs stock-service
   ```

2. **Verify ports are not in use:**
   ```bash
   # Windows PowerShell
   netstat -ano | findstr :9000
   netstat -ano | findstr :4001
   netstat -ano | findstr :8080
   ```

3. **Rebuild images:**
   ```bash
   docker-compose up --build --force-recreate
   ```

### Connection Between Services Failing

1. **Verify network:**
   ```bash
   docker network ls
   docker network inspect stock_stock-network
   ```

2. **Test connectivity:**
   ```bash
   docker exec stock-service curl http://vendor-a-service:9000/actuator/health
   ```

3. **Check DNS resolution:**
   ```bash
   docker exec stock-service getent hosts vendor-a-service
   ```

### Database Issues

1. **Check database files:**
   ```bash
   docker exec vendor-a-service ls -la /data/
   docker exec stock-service ls -la /data/
   ```

2. **Reset databases:**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Out of Memory

Increase Docker memory allocation:
- Docker Desktop Settings → Resources → Memory (set to 4GB+)

---

## Performance Tuning

### Increase JVM Heap Size

Edit `docker-compose.yml` and add to services:
```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m
```

### Enable Caching

```bash
docker-compose up --build
```

---

## Monitoring

### View Real-time Logs

```bash
docker-compose logs -f --tail=100
```

### Check Service Status

```bash
docker-compose ps
```

### Monitor Resource Usage

```bash
docker stats
```

## Additional Commands

### Rebuild Specific Service

```bash
docker-compose up -d --build vendor-a-service
```

### Run Commands in Container

```bash
docker exec stock-service curl http://localhost:4001/actuator/health
```

### View Container Processes

```bash
docker top vendor-a-service
```

### Inspect Container

```bash
docker inspect vendor-a-service
```

### View Resource Usage

```bash
docker stats vendor-a-service stock-service stock-sync-scheduler
```

---

## Common Issues & Solutions

### Issue: Port Already in Use
**Solution:** Change port in docker-compose.yml or kill process using the port

### Issue: Service Fails to Start
**Solution:** 
1. Check logs: `docker-compose logs service-name`
2. Rebuild: `docker-compose up --build`

### Issue: Cannot Connect Between Services
**Solution:** Verify all services are on the same network and use service names for DNS

### Issue: Database Not Persisting
**Solution:** Ensure volumes are configured in docker-compose.yml and not using `down -v`

---

## References

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [H2 Database Console](http://h2database.com/html/quickstart.html)

---

**Last Updated:** July 10, 2026

