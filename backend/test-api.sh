#!/bin/bash

# Virtual Garage API Test Script
# Tests all available endpoints with curl commands

BASE_URL="http://localhost:8080/api"
VEHICLES_URL="${BASE_URL}/vehicles"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print section headers
print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Function to print test descriptions
print_test() {
    echo -e "\n${YELLOW}➤ $1${NC}"
}

# Function to execute curl command with pretty output
execute_curl() {
    echo -e "${GREEN}Command:${NC} $1"
    echo -e "${GREEN}Response:${NC}"
    eval $1
    echo -e "\n"
}

print_header "VIRTUAL GARAGE API TESTING SCRIPT"
echo "Testing endpoints at: $BASE_URL"
echo "Make sure the application is running with: docker compose up -d"

# Health Check
print_header "HEALTH CHECK"
print_test "Check application health"
execute_curl "curl -s -X GET '${BASE_URL}/actuator/health' | jq ."

# GET Endpoints
print_header "GET ENDPOINTS - RETRIEVE DATA"

print_test "Get all vehicles (default pagination)"
execute_curl "curl -s -X GET '${VEHICLES_URL}' | jq ."

print_test "Get all vehicles with custom pagination"
execute_curl "curl -s -X GET '${VEHICLES_URL}?page=0&size=5&sortBy=name&sortDir=asc' | jq ."

print_test "Get vehicle by ID (ID: 1)"
execute_curl "curl -s -X GET '${VEHICLES_URL}/1' | jq ."

print_test "Get non-existent vehicle (ID: 999)"
execute_curl "curl -s -w 'HTTP Status: %{http_code}\n' -X GET '${VEHICLES_URL}/999' | jq ."

print_test "Search vehicles by query"
execute_curl "curl -s -X GET '${VEHICLES_URL}/search?q=Toyota' | jq ."

print_test "Filter vehicles by type"
execute_curl "curl -s -X GET '${VEHICLES_URL}/filter?type=sedan' | jq ."

print_test "Filter vehicles by make and year range"
execute_curl "curl -s -X GET '${VEHICLES_URL}/filter?make=Toyota&startYear=2020&endYear=2023' | jq ."

print_test "Get vehicles by type (sedan)"
execute_curl "curl -s -X GET '${VEHICLES_URL}/type/sedan' | jq ."

print_test "Get vehicles by tag (sports)"
execute_curl "curl -s -X GET '${VEHICLES_URL}/tag/sports' | jq ."

# Statistics Endpoints
print_header "STATISTICS ENDPOINTS"

print_test "Get dashboard statistics"
execute_curl "curl -s -X GET '${VEHICLES_URL}/stats/dashboard' | jq ."

print_test "Get vehicle statistics by type"
execute_curl "curl -s -X GET '${VEHICLES_URL}/stats/by-type' | jq ."

print_test "Get vehicle statistics by make"
execute_curl "curl -s -X GET '${VEHICLES_URL}/stats/by-make' | jq ."

# POST Endpoint - Create Vehicle
print_header "POST ENDPOINT - CREATE VEHICLE"

print_test "Create a new vehicle"
VEHICLE_JSON='{
  "name": "Test Sports Car",
  "make": "Ferrari",
  "model": "488 GTB",
  "year": 2022,
  "type": "sports",
  "color": "Red",
  "engine": "V8 Twin Turbo",
  "engine_size": "3.9L",
  "transmission": "Automatic",
  "drivetrain": "RWD",
  "fuel_type": "Gasoline",
  "mileage": 5000,
  "mileage_unit": "miles",
  "value": 250000,
  "purchase_price": 280000,
  "status": "active",
  "vin_number": "TEST123456789",
  "license_plate": "FERRARI1",
  "description": "Beautiful red Ferrari in excellent condition",
  "notes": "Test vehicle created via API",
  "tags": ["sports", "luxury", "performance"]
}'

execute_curl "curl -s -X POST '${VEHICLES_URL}' \\
  -H 'Content-Type: application/json' \\
  -d '$VEHICLE_JSON' | jq ."

# Store the created vehicle ID for subsequent tests
print_test "Get the newly created vehicle ID"
CREATED_ID=$(curl -s -X GET "${VEHICLES_URL}/search?q=Test%20Sports%20Car" | jq -r '.content[0].id // empty')

if [ -n "$CREATED_ID" ] && [ "$CREATED_ID" != "null" ]; then
    echo -e "${GREEN}Created vehicle ID: $CREATED_ID${NC}"
    
    # PUT Endpoint - Update Vehicle
    print_header "PUT ENDPOINT - UPDATE VEHICLE"
    
    print_test "Update the created vehicle"
    UPDATED_VEHICLE_JSON='{
      "name": "Updated Test Sports Car",
      "make": "Ferrari",
      "model": "488 GTB",
      "year": 2022,
      "type": "sports",
      "color": "Blue",
      "engine": "V8 Twin Turbo",
      "engine_size": "3.9L",
      "transmission": "Automatic",
      "drivetrain": "RWD",
      "fuel_type": "Gasoline",
      "mileage": 5500,
      "mileage_unit": "miles",
      "value": 240000,
      "purchase_price": 280000,
      "status": "active",
      "vin_number": "TEST123456789",
      "license_plate": "FERRARI1",
      "description": "Updated: Beautiful blue Ferrari in excellent condition",
      "notes": "Test vehicle updated via API",
      "tags": ["sports", "luxury", "performance", "updated"]
    }'
    
    execute_curl "curl -s -X PUT '${VEHICLES_URL}/${CREATED_ID}' \\
      -H 'Content-Type: application/json' \\
      -d '$UPDATED_VEHICLE_JSON' | jq ."
    
    print_test "Get the updated vehicle to verify changes"
    execute_curl "curl -s -X GET '${VEHICLES_URL}/${CREATED_ID}' | jq ."
    
    # DELETE Endpoint - Delete Vehicle
    print_header "DELETE ENDPOINT - DELETE VEHICLE"
    
    print_test "Delete the test vehicle (soft delete)"
    execute_curl "curl -s -w 'HTTP Status: %{http_code}\n' -X DELETE '${VEHICLES_URL}/${CREATED_ID}'"
    
    print_test "Try to get the deleted vehicle (should return 404 or show inactive)"
    execute_curl "curl -s -w 'HTTP Status: %{http_code}\n' -X GET '${VEHICLES_URL}/${CREATED_ID}' | jq ."
    
else
    echo -e "${RED}Could not find created vehicle ID, skipping update and delete tests${NC}"
fi

# Error Testing
print_header "ERROR HANDLING TESTS"

print_test "Try to create vehicle with missing required fields"
INVALID_VEHICLE_JSON='{
  "name": "Invalid Vehicle"
}'

execute_curl "curl -s -w 'HTTP Status: %{http_code}\n' -X POST '${VEHICLES_URL}' \\
  -H 'Content-Type: application/json' \\
  -d '$INVALID_VEHICLE_JSON' | jq ."

print_test "Try to update non-existent vehicle"
execute_curl "curl -s -w 'HTTP Status: %{http_code}\n' -X PUT '${VEHICLES_URL}/99999' \\
  -H 'Content-Type: application/json' \\
  -d '$VEHICLE_JSON' | jq ."

# OpenAPI Documentation
print_header "API DOCUMENTATION"

print_test "Get OpenAPI documentation"
execute_curl "curl -s -X GET '${BASE_URL}/v3/api-docs' | jq . | head -30"

print_test "Access Swagger UI (open in browser)"
echo -e "${GREEN}Swagger UI available at:${NC} http://localhost:8080/api/swagger-ui.html"

print_header "API TESTING COMPLETE"
echo -e "${GREEN}All tests completed!${NC}"
echo ""
echo -e "${YELLOW}Quick Reference Commands:${NC}"
echo -e "• List all vehicles:     ${GREEN}curl -s '${VEHICLES_URL}' | jq .${NC}"
echo -e "• Get vehicle by ID:     ${GREEN}curl -s '${VEHICLES_URL}/1' | jq .${NC}"  
echo -e "• Search vehicles:       ${GREEN}curl -s '${VEHICLES_URL}/search?q=Toyota' | jq .${NC}"
echo -e "• Get dashboard stats:   ${GREEN}curl -s '${VEHICLES_URL}/stats/dashboard' | jq .${NC}"
echo -e "• Health check:          ${GREEN}curl -s '${BASE_URL}/actuator/health' | jq .${NC}"
echo ""
echo -e "${YELLOW}Note:${NC} Make sure 'jq' is installed for JSON formatting: ${GREEN}brew install jq${NC}"