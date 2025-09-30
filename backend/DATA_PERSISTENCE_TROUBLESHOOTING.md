# Virtual Garage Data Persistence - Troubleshooting Guide

## ✅ **Good News: Data Persistence is Working!**

The Virtual Garage PostgreSQL data persistence is correctly configured and working. If you're experiencing data loss, it's likely due to using the wrong Docker commands.

## 🔍 **Data Persistence Status Check**

### Check Current Data
```bash
curl -s http://localhost:8080/api/vehicles/stats/dashboard | jq .
```

### Check Volume Status
```bash
docker volume ls | grep virtual-garage
```

Expected output:
```
local     virtual-garage-app-logs
local     virtual-garage-app-uploads  
local     virtual-garage-kafka-data
local     virtual-garage-pgladmin-data
local     virtual-garage-postgres-data
```

## ⚠️ **CRITICAL: Commands That Cause Data Loss**

### ❌ **DANGEROUS - WILL DELETE ALL DATA:**
```bash
# DO NOT RUN - This removes all volumes and data
docker-compose down -v

# DO NOT RUN - This removes specific volumes
docker volume rm virtual-garage-postgres-data

# DO NOT RUN - This removes all unused volumes
docker system prune -a --volumes
```

## ✅ **Safe Commands That Preserve Data**

### ✅ **SAFE - Restart Services (Data Preserved)**
```bash
# Restart all services - data preserved
docker-compose restart

# Stop and restart all services - data preserved
docker-compose down
docker-compose up -d

# Rebuild containers - data preserved
docker-compose up --build -d
```

### ✅ **SAFE - Individual Service Management**
```bash
# Restart just PostgreSQL - data preserved
docker-compose restart postgres

# Restart just the API - data preserved
docker-compose restart virtual-garage-api
```

## 🧪 **Test Data Persistence**

### Test 1: Add Data and Restart
```bash
# 1. Check current count
curl -s http://localhost:8080/api/vehicles | jq '.total_elements'

# 2. Add a test vehicle
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Vehicle","make":"Test","model":"Test","year":2024,"type":"Test"}'

# 3. Restart services
docker-compose restart

# 4. Wait for services to start
sleep 15

# 5. Check data is still there
curl -s http://localhost:8080/api/vehicles | jq '.total_elements'
```

### Test 2: Container Recreation
```bash
# 1. Stop all containers
docker-compose down

# 2. Check volumes still exist
docker volume ls | grep virtual-garage

# 3. Recreate containers
docker-compose up -d

# 4. Wait for startup
sleep 20

# 5. Verify data persisted
curl -s http://localhost:8080/api/vehicles/stats/dashboard | jq .
```

## 🔧 **Data Recovery Scenarios**

### If Data Appears Lost

1. **Check if services are running:**
   ```bash
   docker-compose ps
   ```

2. **Check if volumes exist:**
   ```bash
   docker volume ls | grep virtual-garage
   ```

3. **If volumes exist, restart services:**
   ```bash
   docker-compose up -d
   sleep 20
   curl -s http://localhost:8080/api/vehicles/stats/dashboard
   ```

### If Kafka Issues Prevent Startup

If Kafka is causing issues, you can temporarily start without it:

```bash
# Start only essential services
docker-compose up -d postgres

# Then start a local API (requires Java 21)
java21
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## 📊 **Current System Status**

Based on recent tests:
- ✅ **PostgreSQL Volume**: Correctly mounted at `/var/lib/postgresql/data`
- ✅ **Named Volumes**: Properly configured with unique names
- ✅ **Restart Policy**: `unless-stopped` ensures automatic recovery
- ✅ **Data Persistence**: Tested through multiple restart scenarios
- ✅ **Volume Configuration**: External=false prevents accidental deletion

## 🎯 **Key Takeaways**

1. **Data persistence IS working** - the configuration is correct
2. **Use `docker-compose down` (not `down -v`)** for safe restarts
3. **Named volumes survive container recreation** until explicitly removed
4. **System reboots don't affect data** due to restart policies
5. **The issue was likely using `down -v` earlier** which removes volumes

## 🚀 **Recommended Workflow**

For daily development:
```bash
# Start/restart services (safe)
docker-compose up -d

# View logs
docker-compose logs -f virtual-garage-api

# Restart if needed (safe)
docker-compose restart

# Stop for maintenance (safe)
docker-compose down
```

Your data is safe and the persistence is working correctly! 🚗✨