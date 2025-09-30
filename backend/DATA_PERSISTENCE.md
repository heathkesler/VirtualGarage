# Virtual Garage Data Persistence Guide

## Overview

The Virtual Garage application is configured with robust data persistence to ensure that all your vehicle data, images, and application state survive container restarts, updates, and recreations.

## 📊 Persistent Data Components

### 1. PostgreSQL Database
- **Volume**: `virtual-garage-postgres-data`
- **Mount Point**: `/var/lib/postgresql/data`
- **Purpose**: Stores all vehicle data, maintenance records, and application state
- **Persistence**: ✅ **Data survives all container operations**

### 2. Kafka Message Queue
- **Volume**: `virtual-garage-kafka-data`  
- **Mount Point**: `/var/lib/kafka/data`
- **Purpose**: Event queue data and message persistence
- **Persistence**: ✅ **Messages and topics survive restarts**

### 3. Application Logs
- **Volume**: `virtual-garage-app-logs`
- **Mount Point**: `/app/logs`
- **Purpose**: Application logging and debugging information
- **Persistence**: ✅ **Log history maintained across restarts**

### 4. File Uploads
- **Volume**: `virtual-garage-app-uploads`
- **Mount Point**: `/app/uploads`
- **Purpose**: Vehicle images and document uploads
- **Persistence**: ✅ **All uploaded files preserved**

### 5. pgAdmin Configuration
- **Volume**: `virtual-garage-pgadmin-data`
- **Mount Point**: `/var/lib/pgladmin`
- **Purpose**: Database administration tool settings
- **Persistence**: ✅ **Admin settings and connections preserved**

## 🔒 Data Safety Features

### Named Volumes
All data is stored in Docker named volumes with unique names:
- `virtual-garage-postgres-data`
- `virtual-garage-kafka-data`
- `virtual-garage-app-logs`
- `virtual-garage-app-uploads`
- `virtual-garage-pgladmin-data`

### Restart Policies
All services are configured with `restart: unless-stopped` to ensure:
- Automatic recovery from failures
- Persistence across system reboots
- Continuous availability

### Data Directory Configuration
PostgreSQL uses `PGDATA: /var/lib/postgresql/data/pgdata` for organized data storage within the volume.

## 🧪 Testing Data Persistence

### Test Scenario 1: Container Restart
```bash
# Add some data to your garage
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Vehicle","make":"Toyota","model":"Camry","year":2023}'

# Restart containers
docker-compose restart

# Verify data is still there
curl http://localhost:8080/api/vehicles
```

### Test Scenario 2: Container Recreation
```bash
# Stop and remove containers (but keep volumes)
docker-compose down

# Recreate containers
docker-compose up -d

# Data should still be present
curl http://localhost:8080/api/vehicles
```

### Test Scenario 3: Volume Inspection
```bash
# List all volumes
docker volume ls | grep virtual-garage

# Inspect PostgreSQL data volume
docker volume inspect virtual-garage-postgres-data

# Check volume contents
docker run --rm -v virtual-garage-postgres-data:/data alpine ls -la /data
```

## 🗂️ Volume Management Commands

### List Volumes
```bash
docker volume ls | grep virtual-garage
```

### Inspect Volume Details
```bash
docker volume inspect virtual-garage-postgres-data
```

### Backup Database Volume
```bash
# Create backup
docker run --rm -v virtual-garage-postgres-data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz -C /data .

# Restore backup (DANGER: This will overwrite existing data)
docker run --rm -v virtual-garage-postgres-data:/data -v $(pwd):/backup alpine sh -c "cd /data && tar xzf /backup/postgres-backup.tar.gz"
```

### Database Backup (Application Level)
```bash
# Backup database using pg_dump
docker exec virtual-garage-postgres pg_dump -U virtual_garage virtual_garage > backup.sql

# Restore database
docker exec -i virtual-garage-postgres psql -U virtual_garage virtual_garage < backup.sql
```

## 🔧 Volume Cleanup (USE WITH CAUTION)

### Remove All Volumes (DANGER: Data Loss!)
```bash
# This will DELETE ALL DATA permanently
docker-compose down -v
```

### Remove Specific Volume
```bash
# Stop containers first
docker-compose down

# Remove specific volume (DANGER: Data Loss!)
docker volume rm virtual-garage-postgres-data
```

## 📈 Monitoring Data Usage

### Check Volume Disk Usage
```bash
# Check Docker system usage
docker system df

# Detailed volume information
docker system df -v
```

### PostgreSQL Database Size
```bash
# Connect to database and check size
docker exec -it virtual-garage-postgres psql -U virtual_garage -d virtual_garage -c "
SELECT 
    pg_database.datname,
    pg_size_pretty(pg_database_size(pg_database.datname)) AS size
FROM pg_database;
"
```

## ⚠️ Important Notes

1. **Volume Persistence**: Named volumes persist until explicitly removed with `docker volume rm` or `docker-compose down -v`

2. **System Reboots**: Data survives system reboots due to `restart: unless-stopped` policy

3. **Docker Updates**: Data survives Docker engine updates and restarts

4. **Container Updates**: Data survives application container rebuilds and updates

5. **Backup Strategy**: Regular backups are recommended for production use

## 🚨 Data Loss Scenarios (How to Avoid)

### ❌ Actions That WILL Cause Data Loss:
- `docker-compose down -v` (removes volumes)
- `docker volume rm virtual-garage-*` (removes specific volumes)
- `docker system prune -a --volumes` (removes all unused volumes)

### ✅ Actions That WILL NOT Cause Data Loss:
- `docker-compose down` (stops containers, keeps volumes)
- `docker-compose restart` (restarts containers)
- `docker-compose up -d --force-recreate` (recreates containers, keeps volumes)
- System reboot
- Docker engine restart

## 🔄 Migration and Recovery

If you need to migrate data to a new system:

1. **Backup volumes** using the commands above
2. **Copy backup files** to new system  
3. **Create new volumes** with same names
4. **Restore data** from backup files
5. **Start services** with `docker-compose up -d`

Your Virtual Garage data will be fully preserved! 🚗✨