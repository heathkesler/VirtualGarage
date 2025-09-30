#!/bin/bash

# Virtual Garage API - Docker Startup Script
# This script builds and starts all Docker services

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
ENVIRONMENT="dev"
BUILD=false
DETACH=true

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENVIRONMENT    Environment (dev, prod) [default: dev]"
    echo "  -b, --build             Force rebuild of containers"
    echo "  -f, --foreground        Run in foreground (don't detach)"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                      # Start in development mode"
    echo "  $0 --env prod --build   # Build and start in production mode"
    echo "  $0 --foreground         # Start in foreground for debugging"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -b|--build)
            BUILD=true
            shift
            ;;
        -f|--foreground)
            DETACH=false
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option $1"
            usage
            ;;
    esac
done

# Validate environment
if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
    echo -e "${RED}Error: Environment must be 'dev' or 'prod'${NC}"
    exit 1
fi

echo -e "${BLUE}🚀 Starting Virtual Garage API in ${ENVIRONMENT} mode...${NC}"

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Set Docker Compose command based on environment
if [[ "$ENVIRONMENT" == "prod" ]]; then
    COMPOSE_CMD="docker-compose -f docker-compose.yml -f docker-compose.prod.yml"
else
    COMPOSE_CMD="docker-compose"
fi

# Stop any running containers
echo -e "${YELLOW}🛑 Stopping any existing containers...${NC}"
$COMPOSE_CMD down --remove-orphans >/dev/null 2>&1 || true

# Build containers if requested
if [[ "$BUILD" == true ]]; then
    echo -e "${BLUE}🔨 Building containers...${NC}"
    $COMPOSE_CMD build --no-cache
fi

# Create necessary directories
echo -e "${BLUE}📁 Creating necessary directories...${NC}"
mkdir -p docker/postgres

# Start services
echo -e "${GREEN}🚀 Starting services...${NC}"

if [[ "$DETACH" == true ]]; then
    $COMPOSE_CMD up -d
else
    $COMPOSE_CMD up
fi

# Wait for services to be ready
if [[ "$DETACH" == true ]]; then
    echo -e "${YELLOW}⏳ Waiting for services to start...${NC}"
    
    # Wait for database
    echo -e "  🗄️  Waiting for PostgreSQL..."
    timeout 60 bash -c 'until docker-compose exec -T postgres pg_isready -U virtual_garage -d virtual_garage; do sleep 2; done' >/dev/null 2>&1
    
    # Wait for Kafka
    echo -e "  📨 Waiting for Kafka..."
    timeout 60 bash -c 'until docker-compose exec -T kafka kafka-topics --bootstrap-server kafka:29092 --list >/dev/null 2>&1; do sleep 2; done'
    
    # Wait for API
    echo -e "  🌐 Waiting for API to be ready..."
    timeout 120 bash -c 'until curl -sf http://localhost:8080/api/actuator/health >/dev/null 2>&1; do sleep 5; done'
    
    echo ""
    echo -e "${GREEN}✅ All services are running!${NC}"
    echo ""
    echo -e "${BLUE}🌐 Service URLs:${NC}"
    echo -e "  • API:        http://localhost:8080/api"
    echo -e "  • Swagger UI: http://localhost:8080/api/swagger-ui.html"
    echo -e "  • Health:     http://localhost:8080/api/actuator/health"
    
    if [[ "$ENVIRONMENT" == "dev" ]]; then
        echo -e "  • Kafka UI:   http://localhost:8090"
        echo -e "  • PgAdmin:    http://localhost:8091"
        echo -e "    - Email:    admin@virtualgarage.com"
        echo -e "    - Password: admin123"
    fi
    
    echo ""
    echo -e "${YELLOW}📋 Useful Commands:${NC}"
    echo -e "  • View logs:    docker-compose logs -f"
    echo -e "  • Stop all:     ./scripts/stop.sh"
    echo -e "  • View status:  docker-compose ps"
    echo -e "  • Shell into container: docker-compose exec virtual-garage-api bash"
fi

echo -e "${GREEN}🎉 Virtual Garage API started successfully!${NC}"