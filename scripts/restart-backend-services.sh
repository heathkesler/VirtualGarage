#!/bin/bash

# VirtualGarage Backend Services Restart Script
# This script stops all running Docker services and then starts them fresh
# Author: VirtualGarage Development Team
# Version: 1.0

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
COMPOSE_DIR="$REPO_ROOT/backend"
COMPOSE_FILE="$COMPOSE_DIR/docker-compose.yml"
MAX_WAIT_TIME=120

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to check if compose file exists
check_compose_file() {
    if [ ! -f "$COMPOSE_FILE" ]; then
        print_error "Docker Compose file not found: $COMPOSE_FILE"
        exit 1
    fi
    print_success "Docker Compose file found"
}

# Function to wait for services to be healthy
wait_for_services() {
    print_status "Waiting for services to be healthy..."
    local timeout=$MAX_WAIT_TIME
    
    while [ $timeout -gt 0 ]; do
        # Get the status of all services
        local running_services=$(docker compose ps --services --filter "status=running" 2>/dev/null || echo "")
        
        if [ -n "$running_services" ]; then
            local all_healthy=true
            
            # Check each running service for health
            while IFS= read -r service; do
                if [ -n "$service" ]; then
                    local container_name=$(docker compose ps --format "{{.Name}}" "$service" 2>/dev/null | head -n1)
                    if [ -n "$container_name" ]; then
                        local health_status=$(docker inspect --format="{{if .Config.Healthcheck}}{{.State.Health.Status}}{{else}}healthy{{end}}" "$container_name" 2>/dev/null || echo "unknown")
                        if [ "$health_status" != "healthy" ]; then
                            all_healthy=false
                            break
                        fi
                    fi
                fi
            done <<< "$running_services"
            
            if [ "$all_healthy" = true ]; then
                print_success "All services are healthy!"
                return 0
            fi
        fi
        
        echo -n "."
        sleep 5
        timeout=$((timeout - 5))
    done
    
    echo ""
    print_warning "Timeout waiting for all services to be healthy"
    print_status "Continuing anyway..."
    return 0
}

# Function to display service status
show_status() {
    print_status "Current service status:"
    docker compose ps
    echo ""
    
    print_status "Service URLs:"
    echo "🚀 Virtual Garage API: http://localhost:8080"
    echo "📊 API Health Check: http://localhost:8080/api/actuator/health"
    echo "🗄️ PostgreSQL: localhost:5432 (virtual_garage/garage123)"
    echo "📨 Kafka: localhost:9092 (fresh data - no previous topics/messages)"
    echo "🔧 Zookeeper: localhost:2181"
    echo ""
}

# Function to test API connectivity
test_api() {
    print_status "Testing API connectivity..."
    local max_attempts=12
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
            print_success "API is responding!"
            return 0
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_warning "API is not responding yet, but services are starting..."
            print_status "You can check logs with: docker compose logs virtual-garage-api"
            return 0
        fi
        
        echo -n "."
        sleep 10
        attempt=$((attempt + 1))
    done
}

# Main script execution
main() {
    echo "======================================"
    echo "VirtualGarage Backend Services Restart"
    echo "======================================"
    echo ""
    
    # Change to backend directory containing docker-compose.yml
    cd "$COMPOSE_DIR"
    
    # Pre-flight checks
    print_status "Performing pre-flight checks..."
    check_docker
    check_compose_file
    echo ""
    
    # Step 1: Stop all services and remove volumes
    print_status "Step 1: Stopping all Docker services and removing volumes..."
    if docker compose down --remove-orphans -v 2>/dev/null; then
        print_success "All services stopped and volumes removed successfully"
    else
        print_warning "Some services may have already been stopped"
    fi
    echo ""
    
    # Step 2: Clean up Docker resources and Kafka data
    print_status "Step 2: Cleaning up Docker resources and Kafka data..."
    docker system prune -f >/dev/null 2>&1 || true
    
    # Remove Kafka data volume to ensure fresh start
    print_status "Removing Kafka data volume for fresh start..."
    docker volume rm virtual-garage-kafka-data >/dev/null 2>&1 || true
    
    print_success "Cleanup completed"
    echo ""
    
    # Step 3: Pull latest images
    print_status "Step 3: Pulling latest Docker images..."
    if docker compose pull 2>/dev/null; then
        print_success "Latest images pulled"
    else
        print_warning "Some images may not have been pulled (continuing anyway)"
    fi
    echo ""
    
    # Step 4: Build application image
    print_status "Step 4: Building Virtual Garage API image..."
    # Use --no-cache to ensure fresh build and handle Maven issues
    if docker compose build --no-cache virtual-garage-api; then
        print_success "Application image built successfully"
    else
        print_warning "Build failed, trying without cache and with different approach..."
        # Try building with more verbose output for debugging
        if docker compose build --progress=plain virtual-garage-api; then
            print_success "Application image built successfully on retry"
        else
            print_error "Failed to build application image after retry"
            print_status "You can continue without building and start only infrastructure services"
            print_status "Or check the Dockerfile and Maven configuration"
            exit 1
        fi
    fi
    echo ""
    
    # Step 5: Start infrastructure services first
    print_status "Step 5: Starting infrastructure services (Zookeeper, PostgreSQL, Kafka)..."
    if docker compose up -d zookeeper postgres kafka; then
        print_success "Infrastructure services started"
    else
        print_error "Failed to start infrastructure services"
        exit 1
    fi
    echo ""
    
    # Step 6: Wait for infrastructure to be healthy
    print_status "Step 6: Waiting for infrastructure services to be healthy..."
    wait_for_services
    echo ""
    
    # Step 7: Start application service
    print_status "Step 7: Starting Virtual Garage API..."
    if docker compose up -d virtual-garage-api; then
        print_success "Virtual Garage API started"
    else
        print_error "Failed to start Virtual Garage API"
        exit 1
    fi
    echo ""
    
    # Step 8: Wait for application to be ready
    print_status "Step 8: Waiting for API to be ready..."
    test_api
    echo ""
    
    # Step 9: Display final status
    print_status "Step 9: Final service status"
    show_status
    
    print_success "🎉 All Virtual Garage backend services are running!"
    print_status "Use 'docker compose logs -f' to view real-time logs"
    print_status "Use 'docker compose down' to stop all services"
}

# Handle script interruption
cleanup() {
    echo ""
    print_warning "Script interrupted! Services may be in partial state."
    print_status "Run 'docker compose down' to stop all services if needed."
    exit 1
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Run main function
main "$@"