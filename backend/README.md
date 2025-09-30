# Virtual Garage API

A comprehensive backend API for the Virtual Garage application, built with Spring Boot, Apache Camel, Kafka, and PostgreSQL.

## 🚀 Features

- **RESTful API** for vehicle management
- **PostgreSQL database** with Flyway migrations
- **Apache Camel** for event processing and integration
- **Kafka** for event-driven architecture
- **Comprehensive search and filtering** capabilities
- **OpenAPI/Swagger documentation**
- **Docker support** for easy deployment
- **Health monitoring** with Spring Actuator

## 🛠 Technology Stack

- **Spring Boot 3.2.0** - Application framework
- **PostgreSQL** - Primary database
- **Apache Camel 4.2.0** - Integration framework
- **Apache Kafka** - Event streaming platform
- **Flyway** - Database migration tool
- **OpenAPI 3** - API documentation
- **Maven** - Build tool

## 📋 Prerequisites

Before running the application, ensure you have:

- Java 17 or later
- PostgreSQL 12+ running on localhost:5432
- Apache Kafka running on localhost:9092
- Maven 3.6+ (or use the included wrapper)

## 🗄 Database Setup

1. **Install PostgreSQL** and start the service
2. **Create database and user**:
   ```sql
   CREATE DATABASE virtual_garage;
   CREATE USER virtual_garage WITH PASSWORD 'garage123';
   GRANT ALL PRIVILEGES ON DATABASE virtual_garage TO virtual_garage;
   ```

3. **Database migrations** will run automatically on startup via Flyway

## 📊 Kafka Setup

1. **Start Kafka** (assuming Kafka is installed):
   ```bash
   # Start Zookeeper
   zookeeper-server-start.sh config/zookeeper.properties
   
   # Start Kafka Server
   kafka-server-start.sh config/server.properties
   ```

2. **Create topics** (optional - they will be created automatically):
   ```bash
   kafka-topics.sh --create --topic vehicle-events --bootstrap-server localhost:9092
   kafka-topics.sh --create --topic image-events --bootstrap-server localhost:9092
   kafka-topics.sh --create --topic user-activity --bootstrap-server localhost:9092
   ```

## 🚀 Running the Application

### Development Mode

```bash
# Clone and navigate to the backend directory
cd /path/to/VirtualGarage/backend

# Run with Maven
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode

```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar -Dspring.profiles.active=prod target/virtual-garage-api-1.0.0.jar
```

## 🔌 API Endpoints

### Vehicle Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/vehicles` | Get all vehicles with pagination |
| GET | `/api/vehicles/{id}` | Get vehicle by ID |
| POST | `/api/vehicles` | Create new vehicle |
| PUT | `/api/vehicles/{id}` | Update vehicle |
| DELETE | `/api/vehicles/{id}` | Soft delete vehicle |
| GET | `/api/vehicles/search?q={term}` | Search vehicles |
| GET | `/api/vehicles/filter?type={type}&make={make}` | Filter vehicles |
| GET | `/api/vehicles/type/{type}` | Get vehicles by type |
| GET | `/api/vehicles/tag/{tag}` | Get vehicles by tag |

### Statistics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/vehicles/stats/dashboard` | Get dashboard statistics |
| GET | `/api/vehicles/stats/by-type` | Get stats by vehicle type |
| GET | `/api/vehicles/stats/by-make` | Get stats by vehicle make |

### Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/actuator/health` | Health check |
| GET | `/api/actuator/metrics` | Application metrics |
| GET | `/api/swagger-ui.html` | API documentation |

## 📊 Sample API Usage

### Create a Vehicle
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2024 BMW M4 Competition",
    "make": "BMW",
    "model": "M4 Competition",
    "year": 2024,
    "type": "Sports Car",
    "color": "Alpine White",
    "engine": "Twin-Turbo 3.0L I6",
    "value": 95000,
    "status": "Excellent",
    "tags": ["Performance", "Luxury"]
  }'
```

### Search Vehicles
```bash
curl "http://localhost:8080/api/vehicles/search?q=BMW&page=0&size=10"
```

### Get Dashboard Stats
```bash
curl http://localhost:8080/api/vehicles/stats/dashboard
```

## 🎯 Event-Driven Architecture

The application uses Kafka for event streaming:

### Vehicle Events
- `VEHICLE_CREATED` - When a new vehicle is added
- `VEHICLE_UPDATED` - When a vehicle is modified
- `VEHICLE_DELETED` - When a vehicle is deleted
- `VEHICLE_VIEWED` - When a vehicle is viewed

### Image Events
- `IMAGE_UPLOADED` - When a vehicle image is uploaded
- `IMAGE_DELETED` - When an image is deleted
- `IMAGE_SET_PRIMARY` - When an image is set as primary

### User Activity Events
- `USER_LOGIN` - User authentication events
- `USER_SEARCH` - Search activity tracking
- `USER_FILTER` - Filter usage tracking

## 🔄 Apache Camel Routes

The application includes several Camel routes:

- **Vehicle Event Processor** - Processes vehicle lifecycle events
- **Image Event Processor** - Handles image-related events
- **User Activity Processor** - Tracks user behavior
- **File Processor** - Processes uploaded files
- **Health Check** - Monitors route health

## 📝 Configuration

### Application Profiles

- **default** - Basic configuration
- **dev** - Development settings with debug logging
- **test** - Test configuration with H2 database
- **prod** - Production settings with optimized logging

### Key Configuration Properties

```yaml
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/virtual_garage
spring.datasource.username=virtual_garage
spring.datasource.password=garage123

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# File Upload
virtual-garage.file.upload-dir=/tmp/virtual-garage/uploads
virtual-garage.file.max-size=10MB

# CORS
virtual-garage.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

## 🐳 Docker Support

Build and run with Docker:

```bash
# Build Docker image
docker build -t virtual-garage-api .

# Run with Docker Compose (includes PostgreSQL and Kafka)
docker-compose up -d
```

## 📊 Monitoring and Health Checks

- **Health Endpoint**: `GET /api/actuator/health`
- **Metrics**: `GET /api/actuator/metrics`
- **Application Info**: `GET /api/actuator/info`
- **Prometheus Metrics**: `GET /api/actuator/prometheus`

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with coverage
mvn test jacoco:report
```

## 📚 API Documentation

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

## 🔧 Development

### Code Structure

```
src/main/java/com/virtualgarage/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Data repositories
├── service/        # Business logic
├── camel/          # Apache Camel routes
└── exception/      # Exception handlers

src/main/resources/
├── db/migration/   # Flyway migration scripts
└── application.yml # Application configuration
```

### Building

```bash
# Clean and compile
mvn clean compile

# Package without tests
mvn package -DskipTests

# Full build with tests
mvn clean package
```

## 🤝 Integration with Frontend

The API is designed to work seamlessly with the React frontend:

- **CORS configured** for localhost:3000 and localhost:5173
- **JSON responses** with snake_case field naming
- **Pagination support** for large datasets
- **Comprehensive error handling** with meaningful messages

## 📈 Performance Considerations

- **Database indexing** on frequently queried fields
- **JPA lazy loading** for related entities
- **Connection pooling** with HikariCP
- **Pagination** for large result sets
- **Caching** with Spring Cache (Redis ready)

## 🔐 Security Features

- **Input validation** with Bean Validation
- **CORS configuration** for frontend integration
- **SQL injection prevention** with JPA/Hibernate
- **Soft deletes** for data preservation

## 📋 Roadmap

- [ ] JWT authentication and authorization
- [ ] Redis caching layer
- [ ] File upload functionality
- [ ] Image processing and thumbnails
- [ ] Advanced analytics and reporting
- [ ] Kubernetes deployment manifests
- [ ] GraphQL API support

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure PostgreSQL is running on port 5432
   - Check username/password in configuration
   - Verify database exists

2. **Kafka Connection Issues**
   - Start Zookeeper before Kafka
   - Ensure Kafka is running on port 9092
   - Check topic creation

3. **Port Already in Use**
   - Change server.port in application.yml
   - Kill processes using port 8080

### Logs

Check application logs for detailed error information:
```bash
# View logs
tail -f logs/application.log

# Or with specific level
mvn spring-boot:run -Dlogging.level.com.virtualgarage=DEBUG
```

## 📧 Support

For questions or issues, please refer to the project documentation or create an issue in the repository.

---

**Virtual Garage API** - Built with ❤️ for automotive enthusiasts