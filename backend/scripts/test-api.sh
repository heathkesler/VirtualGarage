#!/bin/bash

# Virtual Garage API - Testing Script
# This script tests the API endpoints and validates the setup

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

API_BASE_URL="http://localhost:8080/api"
FAILED_TESTS=0
TOTAL_TESTS=0

# Function to run a test
run_test() {
    local test_name="$1"
    local url="$2"
    local expected_status="${3:-200}"
    local method="${4:-GET}"
    local data="${5:-}"
    
    echo -e "${BLUE}🧪 Testing: $test_name${NC}"
    ((TOTAL_TESTS++))
    
    # Build curl command
    local curl_cmd="curl -s -w '%{http_code}' -X $method"
    
    if [[ -n "$data" ]]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json' -d '$data'"
    fi
    
    curl_cmd="$curl_cmd '$url'"
    
    # Execute curl and capture response
    local response
    response=$(eval $curl_cmd 2>/dev/null)
    local status_code="${response: -3}"
    local body="${response%???}"
    
    # Check status code
    if [[ "$status_code" == "$expected_status" ]]; then
        echo -e "${GREEN}  ✅ PASS - Status: $status_code${NC}"
        if [[ -n "$body" && "$body" != "null" ]]; then
            echo -e "${YELLOW}     Response preview: ${body:0:100}...${NC}"
        fi
    else
        echo -e "${RED}  ❌ FAIL - Expected: $expected_status, Got: $status_code${NC}"
        if [[ -n "$body" ]]; then
            echo -e "${RED}     Response: $body${NC}"
        fi
        ((FAILED_TESTS++))
    fi
    echo ""
}

# Function to wait for API to be ready
wait_for_api() {
    echo -e "${YELLOW}⏳ Waiting for API to be ready...${NC}"
    local max_attempts=30
    local attempt=1
    
    while [[ $attempt -le $max_attempts ]]; do
        if curl -sf "$API_BASE_URL/actuator/health" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ API is ready!${NC}"
            echo ""
            return 0
        fi
        
        echo -e "${YELLOW}  Attempt $attempt/$max_attempts - API not ready yet...${NC}"
        sleep 5
        ((attempt++))
    done
    
    echo -e "${RED}❌ API failed to start within $(($max_attempts * 5)) seconds${NC}"
    exit 1
}

echo -e "${BLUE}🚀 Virtual Garage API Testing Suite${NC}"
echo ""

# Wait for API to be ready
wait_for_api

# Health Check Tests
echo -e "${BLUE}🏥 Health Check Tests${NC}"
run_test "Health Check" "$API_BASE_URL/actuator/health"
run_test "API Info" "$API_BASE_URL/actuator/info"

# Vehicle API Tests
echo -e "${BLUE}🚗 Vehicle API Tests${NC}"
run_test "Get All Vehicles" "$API_BASE_URL/vehicles"
run_test "Get All Vehicles (Paginated)" "$API_BASE_URL/vehicles?page=0&size=10"
run_test "Search Vehicles" "$API_BASE_URL/vehicles/search?q=BMW"
run_test "Filter Vehicles by Type" "$API_BASE_URL/vehicles/type/Sport%20Sedan"
run_test "Filter Vehicles by Tag" "$API_BASE_URL/vehicles/tag/Performance"

# Statistics Tests
echo -e "${BLUE}📊 Statistics API Tests${NC}"
run_test "Dashboard Stats" "$API_BASE_URL/vehicles/stats/dashboard"
run_test "Stats by Type" "$API_BASE_URL/vehicles/stats/by-type"
run_test "Stats by Make" "$API_BASE_URL/vehicles/stats/by-make"

# Individual Vehicle Tests (using seeded data)
echo -e "${BLUE}🔍 Individual Vehicle Tests${NC}"
run_test "Get Vehicle by ID (BMW)" "$API_BASE_URL/vehicles/1"
run_test "Get Vehicle by ID (Mustang)" "$API_BASE_URL/vehicles/2"

# Error Handling Tests
echo -e "${BLUE}❌ Error Handling Tests${NC}"
run_test "Non-existent Vehicle" "$API_BASE_URL/vehicles/999" "404"
run_test "Invalid Vehicle ID" "$API_BASE_URL/vehicles/invalid" "400"

# OpenAPI/Swagger Tests
echo -e "${BLUE}📖 API Documentation Tests${NC}"
run_test "OpenAPI JSON" "$API_BASE_URL/v3/api-docs"
run_test "Swagger UI" "$API_BASE_URL/swagger-ui.html" "200"

# Create Vehicle Test (POST)
echo -e "${BLUE}➕ Create Vehicle Test${NC}"
create_vehicle_json='{
    "name": "Test Vehicle",
    "make": "Test Make",
    "model": "Test Model",
    "year": 2024,
    "type": "Test Type",
    "status": "Excellent",
    "value": 50000,
    "tags": ["Test", "API"]
}'
run_test "Create New Vehicle" "$API_BASE_URL/vehicles" "201" "POST" "$create_vehicle_json"

# Database Connection Test (indirect via API)
echo -e "${BLUE}🗄️  Database Connection Test${NC}"
run_test "Database Connectivity (via stats)" "$API_BASE_URL/vehicles/stats/dashboard"

# Performance Test (simple load test)
echo -e "${BLUE}⚡ Basic Performance Test${NC}"
echo -e "${YELLOW}🔄 Running 10 concurrent requests...${NC}"
for i in {1..10}; do
    curl -sf "$API_BASE_URL/vehicles" >/dev/null 2>&1 &
done
wait
echo -e "${GREEN}✅ Concurrent requests completed${NC}"
echo ""

# Test Summary
echo -e "${BLUE}📋 Test Summary${NC}"
echo -e "Total Tests: $TOTAL_TESTS"
echo -e "Passed: $((TOTAL_TESTS - FAILED_TESTS))"
echo -e "Failed: $FAILED_TESTS"

if [[ $FAILED_TESTS -eq 0 ]]; then
    echo -e "${GREEN}🎉 All tests passed! API is working correctly.${NC}"
    
    echo ""
    echo -e "${BLUE}🌐 API Endpoints Summary:${NC}"
    echo -e "  • API Base:        $API_BASE_URL"
    echo -e "  • Health Check:    $API_BASE_URL/actuator/health"
    echo -e "  • All Vehicles:    $API_BASE_URL/vehicles"
    echo -e "  • Search:          $API_BASE_URL/vehicles/search?q=BMW"
    echo -e "  • Dashboard Stats: $API_BASE_URL/vehicles/stats/dashboard"
    echo -e "  • Swagger UI:      $API_BASE_URL/swagger-ui.html"
    
    echo ""
    echo -e "${YELLOW}📊 Sample Data Available:${NC}"
    curl -sf "$API_BASE_URL/vehicles" | python3 -m json.tool | head -20 2>/dev/null || echo "  Raw data available via API calls"
    
    exit 0
else
    echo -e "${RED}❌ $FAILED_TESTS test(s) failed. Please check the API setup.${NC}"
    
    echo ""
    echo -e "${YELLOW}🔧 Troubleshooting:${NC}"
    echo -e "  • Check container status: docker-compose ps"
    echo -e "  • View logs: ./scripts/logs.sh"
    echo -e "  • Restart services: ./scripts/stop.sh && ./scripts/start.sh"
    
    exit 1
fi