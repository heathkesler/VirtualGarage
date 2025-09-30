#!/bin/bash

# Virtual Garage API - Docker Stop Script
# This script stops all Docker services

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
REMOVE_VOLUMES=false
REMOVE_IMAGES=false

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -v, --volumes           Remove volumes (WARNING: This will delete all data)"
    echo "  -i, --images            Remove built images"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                      # Stop services only"
    echo "  $0 --volumes            # Stop and remove volumes (deletes data)"
    echo "  $0 --images             # Stop and remove built images"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -v|--volumes)
            REMOVE_VOLUMES=true
            shift
            ;;
        -i|--images)
            REMOVE_IMAGES=true
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

echo -e "${BLUE}🛑 Stopping Virtual Garage API services...${NC}"

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running.${NC}"
    exit 1
fi

# Stop and remove containers
echo -e "${YELLOW}🔄 Stopping containers...${NC}"
docker-compose down --remove-orphans

# Remove volumes if requested
if [[ "$REMOVE_VOLUMES" == true ]]; then
    echo -e "${RED}⚠️  WARNING: Removing volumes (this will delete all data)...${NC}"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose down -v
        docker volume prune -f
        echo -e "${YELLOW}📦 Volumes removed.${NC}"
    else
        echo -e "${GREEN}❌ Volume removal cancelled.${NC}"
    fi
fi

# Remove images if requested
if [[ "$REMOVE_IMAGES" == true ]]; then
    echo -e "${YELLOW}🖼️  Removing built images...${NC}"
    docker-compose down --rmi local
    echo -e "${YELLOW}📦 Built images removed.${NC}"
fi

# Clean up orphaned containers and networks
echo -e "${YELLOW}🧹 Cleaning up...${NC}"
docker container prune -f >/dev/null 2>&1 || true
docker network prune -f >/dev/null 2>&1 || true

# Show remaining containers
RUNNING_CONTAINERS=$(docker ps -q --filter "name=virtual-garage" 2>/dev/null | wc -l)
if [[ $RUNNING_CONTAINERS -gt 0 ]]; then
    echo -e "${RED}⚠️  Warning: Some Virtual Garage containers are still running:${NC}"
    docker ps --filter "name=virtual-garage"
else
    echo -e "${GREEN}✅ All Virtual Garage services stopped successfully!${NC}"
fi

# Show disk usage
echo ""
echo -e "${BLUE}💾 Docker disk usage:${NC}"
docker system df

echo ""
echo -e "${YELLOW}📋 Useful Commands:${NC}"
echo -e "  • Start again:     ./scripts/start.sh"
echo -e "  • View all containers: docker ps -a"
echo -e "  • Clean up system: docker system prune -f"