# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Virtual Garage is a full-stack automotive management application with a React TypeScript frontend and Java Spring Boot backend using Apache Camel and Kafka for event-driven architecture. The project follows Warp 2.0 Java development standards emphasizing Apache Foundation components.

## 🚨 CRITICAL: Java 21 Requirement

**MANDATORY FOR ALL DEVELOPMENT**: This project requires Java 21 and will not function correctly with other Java versions.

### Before Any Development Work:
```bash
# Run this command EVERY TIME before working on the project
java21

# Verify the correct version is active
java -version  # Must show: java version "21.0.1"
```

### Why Java 21 is Required:
- Spring Boot 3.2.0 optimizations specific to Java 21
- Performance improvements in Virtual Threads and memory management
- Docker containers built specifically for Java 21 runtime
- Apache Camel 4.2.0 leverages Java 21 features

**⚠️ Warning**: Using other Java versions will cause build failures, runtime errors, and unpredictable behavior.

## Architecture

### Full-Stack Structure
- **Frontend (`/ui`)**: React 19 + TypeScript + Vite + Tailwind CSS
- **Backend (`/backend`)**: Spring Boot 3.2.0 + Apache Camel 4.2.0 + Kafka + PostgreSQL
- **Event System**: Apache Kafka for vehicle, image, and user activity events
- **Integration**: Apache Camel routes for event processing and file handling

### Event-Driven Design
The backend uses Apache Camel routes to process three main event types:
- **Vehicle Events**: CREATED, UPDATED, DELETED, VIEWED
- **Image Events**: UPLOADED, DELETED, SET_PRIMARY  
- **User Activity**: SEARCH, FILTER, LOGIN

### Key Components
- **Camel Routes**: Event processors in `/backend/src/main/java/com/virtualgarage/camel/`
- **REST APIs**: Vehicle management with comprehensive CRUD operations
- **Database**: PostgreSQL with Flyway migrations
- **Frontend Services**: API client in `/ui/src/services/api.js`

## Development Commands

### Backend Development
```bash
# STEP 1: ALWAYS set Java 21 first
java21

# STEP 2: Navigate to backend
cd backend

# Run with Maven (development)
mvn spring-boot:run

# Run with specific profile  
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build and package
mvn clean package

# Run with JAR (production)
java -jar -Dspring.profiles.active=prod target/virtual-garage-api-1.0.0.jar

# Run tests
mvn test

# Run integration tests
mvn verify

# Test with coverage
mvn test jacoco:report
```

### Frontend Development
```bash
cd ui

# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

### Docker Development
```bash
# STEP 1: ALWAYS set Java 21 first
java21

# STEP 2: Navigate to backend
cd backend

# Start all services (PostgreSQL, Kafka, Zookeeper, API)
docker compose up -d

# With debug services (Kafka UI, pgAdmin)
docker compose --profile debug up -d

# Stop all services
docker compose down

# View logs
docker compose logs -f virtual-garage-api
```

### Testing the API
```bash
cd backend

# Run comprehensive API test suite
./test-api.sh

# Quick health check
curl -s http://localhost:8080/api/actuator/health | jq .

# Test specific endpoints
curl -s http://localhost:8080/api/vehicles | jq .
curl -s http://localhost:8080/api/vehicles/stats/dashboard | jq .
```

## Apache Camel Testing Guidelines

**IMPORTANT**: All Apache Camel tests must use `adviceWith()` following the project's testing standards:

```java
@Test
public void testVehicleEventRoute() throws Exception {
    context.getRouteDefinition("vehicle-events-processor")
           .adviceWith(context, new AdviceWithRouteBuilder() {
               @Override
               public void configure() throws Exception {
                   replaceFromWith("direct:start");
                   mockEndpointsAndSkip("kafka:*");
               }
           });
    
    // Test implementation
}
```

## Database Setup

### Prerequisites
- PostgreSQL 12+ running on localhost:5432
- Apache Kafka running on localhost:9092

### Database Configuration
```sql
CREATE DATABASE virtual_garage;
CREATE USER virtual_garage WITH PASSWORD 'garage123';
GRANT ALL PRIVILEGES ON DATABASE virtual_garage TO virtual_garage;
```

### Flyway Migrations
Database migrations run automatically on startup. For manual migration:
```bash
mvn flyway:migrate
```

## API Documentation

### Key Endpoints
- **Vehicles**: `/api/vehicles` - Full CRUD with pagination, search, filtering
- **Statistics**: `/api/vehicles/stats/dashboard` - Dashboard metrics
- **Health**: `/api/actuator/health` - Application health status
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`

### Testing Workflow
```bash
# Create vehicle
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Car","make":"Toyota","model":"Camry","year":2023,"type":"sedan"}'

# Search vehicles  
curl "http://localhost:8080/api/vehicles/search?q=Toyota"

# Get dashboard stats
curl http://localhost:8080/api/vehicles/stats/dashboard
```

## Configuration Profiles

- **default**: Basic configuration
- **dev**: Development with debug logging
- **test**: Test configuration with H2 database  
- **prod**: Production optimized
- **docker**: Docker container configuration

## Event Processing Architecture

### Kafka Topics
- `vehicle-events`: Vehicle lifecycle events
- `image-events`: Image management events  
- `user-activity`: User behavior tracking

### Camel Route Structure
```
VehicleEventRoute.java:
├── vehicle-events-processor (main route)
├── handle-vehicle-created
├── handle-vehicle-updated  
├── handle-vehicle-deleted
├── handle-vehicle-viewed
├── image-events-processor
├── user-activity-processor
└── file-processor (image uploads)
```

## Development Environment

### CRITICAL: Java Version Requirement
**MANDATORY**: This project MUST use Java 21 for all development and runtime operations.

```bash
# ALWAYS run this command before working on the project
java21

# Verify correct version (should show Java 21.0.1)
java -version
```

### Required Dependencies
- **Java 21** (MANDATORY - use `java21` command)
- Node.js 18+
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka (via Docker)

### Docker Services
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`
- API: `localhost:8080`
- Kafka UI: `localhost:8090` (debug profile)
- pgAdmin: `localhost:8091` (debug profile)

### CORS Configuration
Frontend CORS configured for:
- `http://localhost:3000` (React dev server)
- `http://localhost:5173` (Vite dev server)

## Mandatory Framework Standards

Following Warp 2.0 Java development rule, this project uses:

### Primary Stack
- **Spring Boot 3.2.0**: Application framework
- **Apache Camel 4.2.0**: Enterprise Integration Patterns
- **Apache Kafka**: Event streaming platform
- **Apache Commons**: Utilities (Lang3, IO)

### Data Processing
- **PostgreSQL**: Primary database
- **Jackson**: JSON processing
- **Flyway**: Database migrations
- **HikariCP**: Connection pooling

### Monitoring
- **Spring Actuator**: Health checks and metrics
- **OpenAPI 3**: API documentation
- **Prometheus**: Metrics export (available)

## Frontend Architecture  

### Component Structure
```
/ui/src/
├── components/        # Reusable UI components
├── pages/            # Main application pages
├── services/         # API client and utilities  
├── data/             # Mock data and constants
└── utils/            # Utility functions
```

### Key Technologies
- **React 19**: Component framework
- **TypeScript**: Type safety
- **Vite**: Build tool and dev server
- **Tailwind CSS**: Utility-first styling
- **Lucide React**: Icon library
- **React Router**: Client-side routing

## Project Status

### Completed (Phase 1)
- ✅ React frontend with authentication UI
- ✅ Spring Boot API with comprehensive endpoints
- ✅ Apache Camel event processing routes
- ✅ PostgreSQL database with migrations
- ✅ Kafka integration for events
- ✅ Docker development environment
- ✅ API documentation and testing

### In Progress (Phase 2)  
- 🚧 Frontend-backend integration
- 🚧 Authentication endpoints
- 🚧 Image upload functionality

### Planned (Phase 3)
- 📋 JWT authentication
- 📋 Build sheet management
- 📋 Photo management system
- 📋 Advanced analytics