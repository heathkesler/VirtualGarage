# Virtual Garage API Endpoints & Testing Guide

## 🚀 Application Status

✅ **Application is running successfully!**
- **Base URL**: http://localhost:8080/api/v1
- **Infrastructure**: Docker containers for PostgreSQL, Redis, Kafka, Elasticsearch, and MinIO
- **Database**: Hibernate created all tables automatically
- **Security**: JWT-based authentication with Spring Security

## 📋 Available Services Status

| Service | Port | Status | URL |
|---------|------|--------|-----|
| Virtual Garage API | 8080 | ✅ Running | http://localhost:8080 |
| PostgreSQL | 5432 | ✅ Running | localhost:5432/virtualgarage |
| Redis | 6379 | ✅ Running | localhost:6379 |
| Kafka | 9092 | ✅ Running | localhost:9092 |
| Elasticsearch | 9200 | ✅ Running | http://localhost:9200 |
| MinIO | 9000/9001 | ✅ Running | http://localhost:9000 |

## 🔐 Authentication Endpoints

### 1. Health Check (Public)
```bash
curl -X GET http://localhost:8080/api/v1/auth/health
```
**Response:**
```json
{
  "service": "Authentication Service",
  "timestamp": "2025-09-12T17:56:46.789907Z",
  "status": "UP"
}
```

### 2. User Registration
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "name": "John Doe",
    "password": "securepassword123"
  }'
```
**Expected Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900000,
  "user": {
    "id": "uuid-here",
    "email": "john.doe@example.com",
    "name": "John Doe",
    "avatar": null,
    "role": "USER",
    "createdAt": "2025-09-12T17:56:46.789Z",
    "lastLogin": null
  }
}
```

### 3. User Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securepassword123"
  }'
```

### 4. Token Refresh
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

### 5. User Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 🚗 Vehicle & API Endpoints (Requires Authentication)

### Get API Health (Protected)
```bash
curl -X GET http://localhost:8080/api/v1/api/health \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Get Stock Vehicles
```bash
curl -X GET http://localhost:8080/api/v1/api/vehicles/stock?page=0&size=10 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Search Stock Vehicles
```bash
# Search by make
curl -X GET "http://localhost:8080/api/v1/api/vehicles/stock/search?make=Honda&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"

# Search by year
curl -X GET "http://localhost:8080/api/v1/api/vehicles/stock/search?year=2023&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"

# Search by make and model
curl -X GET "http://localhost:8080/api/v1/api/vehicles/stock/search?make=Honda&model=Civic&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 📊 Complete Test Workflow

### Step 1: Register a New User
```bash
export API_BASE="http://localhost:8080/api/v1"

# Register user
REGISTER_RESPONSE=$(curl -s -X POST $API_BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@virtualgarage.com",
    "name": "Test User",
    "password": "testpassword123"
  }')

echo "Registration Response:"
echo $REGISTER_RESPONSE | jq .
```

### Step 2: Extract Access Token
```bash
# Extract access token from response
ACCESS_TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.accessToken')
echo "Access Token: $ACCESS_TOKEN"
```

### Step 3: Test Protected Endpoints
```bash
# Test API health endpoint
curl -X GET $API_BASE/api/health \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# Get stock vehicles
curl -X GET $API_BASE/api/vehicles/stock \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# Search stock vehicles
curl -X GET "$API_BASE/api/vehicles/stock/search?make=Honda" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Step 4: Login with Existing User
```bash
LOGIN_RESPONSE=$(curl -s -X POST $API_BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@virtualgarage.com",
    "password": "testpassword123"
  }')

echo "Login Response:"
echo $LOGIN_RESPONSE | jq .

# Extract new access token
NEW_ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')
```

### Step 5: Logout
```bash
curl -X POST $API_BASE/auth/logout \
  -H "Authorization: Bearer $NEW_ACCESS_TOKEN"
```

## 🔍 Additional Testing Commands

### Check Application Logs
```bash
tail -f app.log
```

### Check Docker Services
```bash
docker-compose ps
```

### Verify Database Connection
```bash
docker exec virtualgarage-postgres psql -U virtualgarage -d virtualgarage -c "\dt"
```

### Check Redis
```bash
docker exec virtualgarage-redis redis-cli ping
```

### Check Kafka Topics
```bash
docker exec virtualgarage-kafka kafka-topics.sh --bootstrap-server localhost:9092 --list
```

## 🚀 Future Endpoints (Planned Implementation)

### User Management
- `PUT /api/v1/users/me` - Update user profile
- `POST /api/v1/users/avatar` - Upload user avatar

### Garage Management
- `GET /api/v1/garages` - List user's garages
- `POST /api/v1/garages` - Create new garage
- `GET /api/v1/garages/{id}` - Get garage details
- `PUT /api/v1/garages/{id}` - Update garage
- `DELETE /api/v1/garages/{id}` - Delete garage

### Vehicle Management
- `GET /api/v1/vehicles` - List user's vehicles
- `POST /api/v1/vehicles` - Create new vehicle
- `GET /api/v1/vehicles/{id}` - Get vehicle details
- `PUT /api/v1/vehicles/{id}` - Update vehicle
- `DELETE /api/v1/vehicles/{id}` - Delete vehicle

### AI-Powered Parts Search
- `GET /api/v1/parts/search` - AI-powered parts search
- `POST /api/v1/parts/recommend` - Get AI recommendations
- `POST /api/v1/parts/save` - Save part to favorites

### Build Management
- `GET /api/v1/builds` - List vehicle builds
- `POST /api/v1/builds` - Create new build
- `GET /api/v1/builds/{id}` - Get build details
- `PUT /api/v1/builds/{id}` - Update build
- `POST /api/v1/builds/{id}/components` - Add component to build

## ⚠️ Important Notes

1. **JWT Token Expiration**: Access tokens expire in 15 minutes (900,000ms)
2. **Refresh Token**: Refresh tokens expire in 7 days
3. **CORS**: Enabled for localhost development
4. **Rate Limiting**: Currently disabled but available in configuration
5. **Database**: Using PostgreSQL with auto-generated UUID primary keys
6. **Security**: All endpoints except `/auth/**` require authentication

## 🔧 Troubleshooting

If you encounter issues:

1. **Application not starting**: Check Java version (requires Java 21)
2. **Database connection errors**: Ensure Docker services are running
3. **403 Forbidden**: Check if you're using the correct Authorization header
4. **404 Not Found**: Verify the endpoint URL includes the correct base path

## 🎯 Development Notes

- Application runs with Spring profile: `dev`
- Database tables are auto-created by Hibernate
- Flyway is temporarily disabled due to PostgreSQL 16.3 compatibility
- Kafka integration is prepared but temporarily disabled
- Full Swagger documentation will be available at `/swagger-ui.html` when enabled