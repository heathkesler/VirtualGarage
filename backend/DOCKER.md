# Virtual Garage API - Docker Setup

Complete Docker containerization for the Virtual Garage API with PostgreSQL, Kafka, and all required services.

## 🐳 What's Included

This Docker setup provides:

- **Spring Boot API** - The main Virtual Garage application
- **PostgreSQL 15** - Database with optimized configuration
- **Apache Kafka** - Message streaming platform
- **Apache Zookeeper** - Kafka coordination service
- **Kafka UI** - Web interface for Kafka management (dev mode)
- **PgAdmin** - PostgreSQL administration tool (dev mode)

## 🚀 Quick Start

### Prerequisites

- Docker Desktop or Docker Engine
- Docker Compose v2.0+
- At least 4GB available RAM
- Ports 8080, 5432, 9092, 2181 available

### Start the Application

```bash
# Start all services in development mode
./scripts/start.sh

# Start with a fresh build
./scripts/start.sh --build

# Start in production mode
./scripts/start.sh --env prod --build

# Start in foreground for debugging
./scripts/start.sh --foreground
```

### Stop the Application

```bash
# Stop all services
./scripts/stop.sh

# Stop and remove all data (WARNING: Deletes everything)
./scripts/stop.sh --volumes

# Stop and remove built images
./scripts/stop.sh --images
```

## 📊 Service Endpoints

### API Services
- **Virtual Garage API**: http://localhost:8080/api
- **Swagger Documentation**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health

### Development Tools (dev mode only)
- **Kafka UI**: http://localhost:8090
- **PgAdmin**: http://localhost:8091
  - Email: `admin@virtualgarage.com`
  - Password: `admin123`

### Direct Database Access
- **PostgreSQL**: `localhost:5432`
  - Database: `virtual_garage`
  - Username: `virtual_garage`
  - Password: `garage123`

## 🔧 Docker Compose Configurations

### Development Mode (Default)
```bash
docker-compose up
# Uses docker-compose.yml + docker-compose.override.yml
```

**Features:**
- Lower resource allocation
- Debug logging enabled
- Development tools included (Kafka UI, PgAdmin)
- Hot reload support
- JVM debug port (5005) exposed

### Production Mode
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up
```

**Features:**
- Higher resource allocation
- Optimized logging
- Development tools disabled
- Production JVM settings
- Health checks disabled (for orchestrator management)

## 📋 Container Details

### Virtual Garage API
- **Image**: Custom built from Dockerfile
- **Memory**: 1GB (dev) / 2.5GB (prod)
- **Ports**: 8080, 5005 (debug in dev)
- **Health Check**: API endpoint monitoring
- **Volumes**: Logs, uploads

### PostgreSQL
- **Image**: postgres:15.5-alpine
- **Memory**: 1GB (dev) / 2GB (prod)
- **Port**: 5432
- **Volumes**: Database data, initialization script
- **Features**: Performance tuned, automatic initialization

### Apache Kafka
- **Image**: confluentinc/cp-kafka:7.4.3
- **Memory**: 512MB (dev) / 1GB (prod)
- **Ports**: 9092 (external), 29092 (internal)
- **Features**: Auto topic creation, optimized for single-node

### Zookeeper
- **Image**: confluentinc/cp-zookeeper:7.4.3
- **Memory**: 256MB (dev) / 512MB (prod)
- **Port**: 2181

## 📁 Data Persistence

All data is persisted in Docker volumes:

- **postgres-data**: Database files
- **kafka-data**: Kafka logs and metadata
- **app-logs**: Application logs
- **app-uploads**: Uploaded files

### Backup Locations (Production)
- Database backups: `/var/backups/virtual-garage/postgres`
- Upload backups: `/var/backups/virtual-garage/uploads`

## 🔍 Monitoring and Logs

### View Logs
```bash
# All services
./scripts/logs.sh

# Specific service
./scripts/logs.sh api
./scripts/logs.sh postgres
./scripts/logs.sh kafka

# Follow logs in real-time
./scripts/logs.sh --follow api

# Last 50 lines
./scripts/logs.sh --tail 50 postgres
```

### Health Checks
```bash
# API health
curl http://localhost:8080/api/actuator/health

# Database connectivity
docker-compose exec postgres pg_isready -U virtual_garage

# Kafka status
docker-compose exec kafka kafka-topics --bootstrap-server localhost:29092 --list
```

### Container Status
```bash
# View running containers
docker-compose ps

# View resource usage
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
```

## 🧪 Testing

### Automated API Testing
```bash
# Run comprehensive API tests
./scripts/test-api.sh

# Tests include:
# - Health checks
# - CRUD operations
# - Search and filtering
# - Statistics endpoints
# - Error handling
# - Performance tests
```

### Manual Testing
```bash
# Get all vehicles
curl http://localhost:8080/api/vehicles

# Search vehicles
curl "http://localhost:8080/api/vehicles/search?q=BMW"

# Get dashboard statistics
curl http://localhost:8080/api/vehicles/stats/dashboard

# Create a new vehicle
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2024 Toyota Camry",
    "make": "Toyota",
    "model": "Camry",
    "year": 2024,
    "type": "Sedan",
    "value": 35000
  }'
```

## 🚨 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using the port
lsof -i :8080
lsof -i :5432

# Kill process using port
kill -9 $(lsof -ti:8080)
```

#### Out of Memory
```bash
# Increase Docker memory limits in Docker Desktop
# Or reduce container memory limits in docker-compose files
```

#### Database Connection Failed
```bash
# Check PostgreSQL logs
./scripts/logs.sh postgres

# Verify database is ready
docker-compose exec postgres pg_isready -U virtual_garage -d virtual_garage

# Reset database (WARNING: Deletes all data)
./scripts/stop.sh --volumes
./scripts/start.sh --build
```

#### Kafka Issues
```bash
# Check Kafka logs
./scripts/logs.sh kafka

# List topics
docker-compose exec kafka kafka-topics --bootstrap-server kafka:29092 --list

# Reset Kafka data
./scripts/stop.sh --volumes
./scripts/start.sh --build
```

### Log Locations

- **Application Logs**: `docker volume inspect virtual-garage-app-logs`
- **Database Logs**: Via `docker-compose logs postgres`
- **Kafka Logs**: Via `docker-compose logs kafka`

### Performance Tuning

#### For Development
```bash
# Reduce memory usage
export COMPOSE_FILE=docker-compose.yml:docker-compose.override.yml
```

#### For Production
```bash
# Increase memory limits and optimize
export COMPOSE_FILE=docker-compose.yml:docker-compose.prod.yml
```

## 🔄 Development Workflow

### Code Changes
1. Make changes to Java code
2. Rebuild container: `./scripts/start.sh --build`
3. Test changes: `./scripts/test-api.sh`

### Database Changes
1. Add new Flyway migration in `src/main/resources/db/migration/`
2. Restart services: `./scripts/stop.sh && ./scripts/start.sh`
3. Verify migration: Check logs or connect to database

### Configuration Changes
1. Update `application-docker.yml`
2. Restart services: `./scripts/stop.sh && ./scripts/start.sh`

## 🔐 Security Considerations

### Development Mode
- Default passwords are used
- All services exposed on localhost
- Debug ports are open

### Production Mode
- Use environment variables for passwords
- Restrict network access
- Disable development tools
- Enable proper authentication

### Environment Variables
```bash
# Override default passwords
export POSTGRES_PASSWORD=your_secure_password
```

## 📈 Scaling

### Horizontal Scaling
```bash
# Scale API instances
docker-compose up --scale virtual-garage-api=3
```

### Resource Limits
Adjust in docker-compose files:
```yaml
deploy:
  resources:
    limits:
      memory: 2G
      cpus: '1.0'
```

## 🎯 Best Practices

1. **Always use volumes** for data persistence
2. **Monitor resource usage** regularly
3. **Keep containers updated** with security patches
4. **Use specific image tags** instead of 'latest'
5. **Implement proper logging** and monitoring
6. **Regular backups** of volumes
7. **Health checks** for all services

## 📚 Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Confluent Kafka Docker](https://docs.confluent.io/platform/current/installation/docker/development.html)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)

---

**Need help?** Check the [main README](README.md) or run `./scripts/start.sh --help`