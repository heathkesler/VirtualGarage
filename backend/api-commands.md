# Virtual Garage API - cURL Commands Reference

Base URL: `http://localhost:8080/api/vehicles`

## Prerequisites

Install `jq` for JSON formatting:
```bash
brew install jq
```

Make sure the application is running:
```bash
docker compose up -d
```

## Health Check

```bash
# Check application health
curl -s http://localhost:8080/api/actuator/health | jq .
```

## GET Endpoints

### List Vehicles

```bash
# Get all vehicles (default pagination)
curl -s http://localhost:8080/api/vehicles | jq .

# Get vehicles with custom pagination
curl -s "http://localhost:8080/api/vehicles?page=0&size=5&sortBy=name&sortDir=asc" | jq .
```

### Get Single Vehicle

```bash
# Get vehicle by ID
curl -s http://localhost:8080/api/vehicles/1 | jq .

# Get non-existent vehicle (should return 404)
curl -s -w "HTTP Status: %{http_code}\n" http://localhost:8080/api/vehicles/999
```

### Search and Filter

```bash
# Search vehicles by query
curl -s "http://localhost:8080/api/vehicles/search?q=Toyota" | jq .

# Filter by type
curl -s "http://localhost:8080/api/vehicles/filter?type=sedan" | jq .

# Filter by make and year range
curl -s "http://localhost:8080/api/vehicles/filter?make=Toyota&startYear=2020&endYear=2023" | jq .

# Filter by multiple criteria
curl -s "http://localhost:8080/api/vehicles/filter?search=car&type=sedan&make=Honda&startYear=2015&endYear=2025&minValue=15000&maxValue=50000" | jq .

# Get vehicles by type
curl -s http://localhost:8080/api/vehicles/type/sedan | jq .

# Get vehicles by tag
curl -s http://localhost:8080/api/vehicles/tag/sports | jq .
```

### Statistics

```bash
# Get dashboard statistics
curl -s http://localhost:8080/api/vehicles/stats/dashboard | jq .

# Get statistics by type
curl -s http://localhost:8080/api/vehicles/stats/by-type | jq .

# Get statistics by make
curl -s http://localhost:8080/api/vehicles/stats/by-make | jq .
```

## POST Endpoint - Create Vehicle

```bash
# Create a new vehicle
curl -s -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Dream Car",
    "make": "Tesla",
    "model": "Model S",
    "year": 2023,
    "type": "sedan",
    "color": "White",
    "engine": "Electric",
    "engine_size": "N/A",
    "transmission": "Automatic",
    "drivetrain": "AWD",
    "fuel_type": "Electric",
    "mileage": 1500,
    "mileage_unit": "miles",
    "value": 95000,
    "purchase_price": 105000,
    "status": "active",
    "vin_number": "5YJ3E1EA4KF123456",
    "license_plate": "TESLA01",
    "description": "High-performance electric sedan",
    "notes": "Full self-driving capability",
    "tags": ["electric", "luxury", "performance"]
  }' | jq .
```

### Minimal Vehicle Creation

```bash
# Create vehicle with minimum required fields only
curl -s -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Simple Car",
    "make": "Honda",
    "model": "Civic",
    "year": 2022,
    "type": "sedan"
  }' | jq .
```

## PUT Endpoint - Update Vehicle

```bash
# Update existing vehicle (replace {id} with actual vehicle ID)
curl -s -X PUT http://localhost:8080/api/vehicles/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Car Name",
    "make": "Tesla",
    "model": "Model S",
    "year": 2023,
    "type": "sedan",
    "color": "Blue",
    "engine": "Electric",
    "mileage": 2000,
    "value": 93000,
    "status": "active",
    "description": "Updated description",
    "tags": ["electric", "luxury", "updated"]
  }' | jq .
```

## DELETE Endpoint

```bash
# Delete vehicle (soft delete - replace {id} with actual vehicle ID)
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE http://localhost:8080/api/vehicles/{id}
```

## Error Testing

```bash
# Try to create vehicle with missing required fields (should return 400)
curl -s -w "HTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"name": "Incomplete Vehicle"}' | jq .

# Try to update non-existent vehicle (should return 404)
curl -s -w "HTTP Status: %{http_code}\n" -X PUT http://localhost:8080/api/vehicles/99999 \
  -H "Content-Type: application/json" \
  -d '{"name": "Non-existent", "make": "Test", "model": "Test", "year": 2023, "type": "sedan"}' | jq .
```

## API Documentation

```bash
# Get OpenAPI specification
curl -s http://localhost:8080/api/v3/api-docs | jq .

# Access Swagger UI in browser
open http://localhost:8080/api/swagger-ui.html
```

## Testing Workflow Example

```bash
# 1. Check health
curl -s http://localhost:8080/api/actuator/health | jq .status

# 2. List existing vehicles
curl -s http://localhost:8080/api/vehicles | jq '.content | length'

# 3. Create a test vehicle
VEHICLE_ID=$(curl -s -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Vehicle",
    "make": "Toyota",
    "model": "Corolla",
    "year": 2023,
    "type": "sedan"
  }' | jq -r '.id')

echo "Created vehicle with ID: $VEHICLE_ID"

# 4. Get the created vehicle
curl -s http://localhost:8080/api/vehicles/$VEHICLE_ID | jq .

# 5. Update the vehicle
curl -s -X PUT http://localhost:8080/api/vehicles/$VEHICLE_ID \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test Vehicle",
    "make": "Toyota",
    "model": "Corolla",
    "year": 2023,
    "type": "sedan",
    "color": "Blue"
  }' | jq .

# 6. Delete the vehicle
curl -s -X DELETE http://localhost:8080/api/vehicles/$VEHICLE_ID

# 7. Verify deletion (should return 404 or show as inactive)
curl -s -w "HTTP Status: %{http_code}\n" http://localhost:8080/api/vehicles/$VEHICLE_ID
```

## Response Examples

### Successful Response Structure
```json
{
  "id": 1,
  "name": "My Car",
  "make": "Toyota",
  "model": "Camry",
  "year": 2023,
  "type": "sedan",
  "color": "Silver",
  "status": "active",
  "created_at": "2025-09-13T01:00:00Z",
  "updated_at": "2025-09-13T01:00:00Z"
}
```

### Paginated Response Structure
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {...}
  },
  "totalElements": 10,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

### Error Response Structure
```json
{
  "error": "Vehicle not found"
}
```