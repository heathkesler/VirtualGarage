# Virtual Garage API Backend

## Architecture Overview

The Virtual Garage backend is built using a modern, microservices-inspired architecture with Java and Spring Boot, designed to provide robust APIs for vehicle management, AI-powered parts search, and user interaction.

## Technology Stack

### Core Framework
- **Spring Boot 3.2+** - Main application framework
- **Spring WebMVC** - REST API development
- **Spring Data JPA** - Database abstraction layer
- **Spring Security** - Authentication & authorization
- **Spring Cache** - Caching abstraction

### Integration & Messaging
- **Apache Camel 4.x** - Enterprise integration patterns
- **Apache Kafka** - Event streaming and async messaging
- **Spring Integration** - Message-driven architecture

### Database & Storage
- **PostgreSQL** - Primary database
- **Redis** - Caching and session storage
- **Amazon S3** - File storage (vehicle photos, part images)

### AI & External Services
- **OpenAI GPT API** - AI-powered parts recommendations
- **Elasticsearch** - Advanced search capabilities
- **Apache HttpClient** - External API integrations

### Development & Operations
- **Maven** - Build management
- **Docker** - Containerization
- **Testcontainers** - Integration testing
- **Micrometer** - Metrics and observability
- **OpenAPI 3** - API documentation

## API Modules

### 1. Authentication Service (`auth-service`)
- User registration and login
- JWT token management  
- Password reset functionality
- OAuth2 integration support

### 2. User Management Service (`user-service`)
- User profile management
- Account settings
- Avatar upload and management

### 3. Garage Management Service (`garage-service`)
- Virtual garage CRUD operations
- Garage sharing and collaboration
- Dashboard analytics

### 4. Vehicle Service (`vehicle-service`)
- Vehicle CRUD operations
- Vehicle specification management
- VIN decoding integration
- Vehicle photo management

### 5. Parts Service (`parts-service`)
- Parts catalog management
- AI-powered parts search
- Parts recommendation engine
- External supplier integrations

### 6. Build Service (`build-service`)
- Vehicle build management
- Component tracking
- Cost calculation
- Build progress monitoring

### 7. Notification Service (`notification-service`)
- Event-driven notifications
- Email and push notifications
- Notification preferences

### 8. Integration Service (`integration-service`)
- External API integrations
- Data synchronization
- Third-party service orchestration

## API Endpoints Overview

### Authentication APIs
```
POST   /api/v1/auth/register      - User registration
POST   /api/v1/auth/login         - User login
POST   /api/v1/auth/logout        - User logout
POST   /api/v1/auth/refresh       - Token refresh
POST   /api/v1/auth/reset-password - Password reset
```

### User Management APIs
```
GET    /api/v1/users/me           - Get current user profile
PUT    /api/v1/users/me           - Update user profile
POST   /api/v1/users/avatar       - Upload user avatar
DELETE /api/v1/users/avatar       - Delete user avatar
```

### Garage Management APIs
```
GET    /api/v1/garages            - List user's garages
POST   /api/v1/garages            - Create new garage
GET    /api/v1/garages/{id}       - Get garage details
PUT    /api/v1/garages/{id}       - Update garage
DELETE /api/v1/garages/{id}       - Delete garage
GET    /api/v1/garages/{id}/dashboard - Get garage dashboard data
```

### Vehicle Management APIs
```
GET    /api/v1/vehicles           - List vehicles (with filtering)
POST   /api/v1/vehicles           - Create new vehicle
GET    /api/v1/vehicles/{id}      - Get vehicle details
PUT    /api/v1/vehicles/{id}      - Update vehicle
DELETE /api/v1/vehicles/{id}      - Delete vehicle
POST   /api/v1/vehicles/{id}/photos - Upload vehicle photos
GET    /api/v1/vehicles/stock     - Get stock vehicle data
POST   /api/v1/vehicles/decode-vin - Decode VIN number
```

### Parts Search & Management APIs
```
GET    /api/v1/parts/search       - AI-powered parts search
POST   /api/v1/parts/recommend    - Get AI recommendations
GET    /api/v1/parts/categories   - List part categories
GET    /api/v1/parts/{id}         - Get part details
POST   /api/v1/parts/save         - Save part to favorites
DELETE /api/v1/parts/save/{id}    - Remove saved part
```

### Build Management APIs
```
GET    /api/v1/builds             - List vehicle builds
POST   /api/v1/builds             - Create new build
GET    /api/v1/builds/{id}        - Get build details
PUT    /api/v1/builds/{id}        - Update build
DELETE /api/v1/builds/{id}        - Delete build
POST   /api/v1/builds/{id}/components - Add component to build
PUT    /api/v1/builds/{id}/components/{cid} - Update component
DELETE /api/v1/builds/{id}/components/{cid} - Remove component
GET    /api/v1/builds/{id}/cost   - Calculate build cost
```

## Event-Driven Architecture

### Kafka Topics
- `user.events` - User registration, login, profile updates
- `vehicle.events` - Vehicle creation, updates, deletions
- `build.events` - Build modifications, component changes
- `search.events` - Search queries, AI recommendations
- `notification.events` - Notification triggers and delivery

### Event Producers
- Authentication events
- Vehicle lifecycle events
- Build modification events
- Search and recommendation events

### Event Consumers
- Notification service
- Analytics service
- Audit logging
- Cache invalidation

## Security Implementation

### JWT Token Strategy
- Access tokens (15 minutes expiration)
- Refresh tokens (7 days expiration)
- Secure HTTP-only cookies for web clients
- Token blacklisting for logout

### Authorization Levels
- `USER` - Standard user operations
- `PREMIUM` - Advanced features access
- `ADMIN` - Administrative operations

### API Security
- Rate limiting per user/IP
- Request validation and sanitization
- CORS configuration
- SQL injection prevention
- XSS protection headers

## AI Integration Architecture

### Parts Recommendation Engine
```java
@Component
public class AIPartsRecommendationService {
    
    public List<PartRecommendation> getRecommendations(
        VehicleSpecs specs, 
        String searchQuery,
        BuildContext context
    ) {
        // OpenAI GPT integration
        // Vector similarity search
        // Filtering and ranking
    }
}
```

### Integration Flow (Apache Camel)
```java
@Component
public class PartsSearchRoute extends RouteBuilder {
    
    @Override
    public void configure() {
        from("direct:search-parts")
            .to("direct:validate-request")
            .to("direct:ai-processing")
            .to("direct:external-catalog-search")
            .to("direct:aggregate-results")
            .to("kafka:search.events");
    }
}
```

## Database Design

### Key Entities
- `users` - User accounts and profiles
- `garages` - Virtual garage containers
- `vehicles` - Vehicle information and specs
- `builds` - Vehicle build projects
- `components` - Individual parts and components
- `searches` - Search history and preferences
- `notifications` - User notifications

### Relationships
- User 1:N Garages
- Garage 1:N Vehicles  
- Vehicle 1:N Builds
- Build 1:N Components
- User 1:N Saved Parts

## Caching Strategy

### Redis Cache Layers
- **L1**: Frequently accessed user data (15 min TTL)
- **L2**: Vehicle specifications and stock data (1 hour TTL)
- **L3**: Parts catalog and search results (30 min TTL)
- **L4**: AI recommendations cache (24 hour TTL)

## Monitoring & Observability

### Metrics Collection
- API response times and throughput
- Database query performance
- Cache hit/miss ratios
- AI service response times
- Kafka message lag

### Health Checks
- Database connectivity
- Redis availability
- Kafka broker health
- External service status
- AI service availability

## Deployment Architecture

### Docker Containers
- `virtualgarage-api` - Main API application
- `virtualgarage-worker` - Background job processor
- `postgresql` - Database
- `redis` - Cache and sessions
- `kafka` - Message broker
- `elasticsearch` - Search engine

### Environment Configuration
- `application.yml` - Base configuration
- `application-dev.yml` - Development overrides
- `application-prod.yml` - Production settings
- Environment variables for secrets

This architecture provides a scalable, maintainable foundation for the Virtual Garage application with modern Java technologies and best practices.