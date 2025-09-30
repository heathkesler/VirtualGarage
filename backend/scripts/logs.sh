#!/bin/bash

# Virtual Garage API - Docker Logs Script
# This script shows logs from Docker services

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
SERVICE=""
FOLLOW=false
TAIL_LINES=100

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS] [SERVICE]"
    echo ""
    echo "Services:"
    echo "  api, app, virtual-garage-api    # Spring Boot API logs"
    echo "  postgres, db                    # PostgreSQL database logs"
    echo "  kafka                          # Kafka message broker logs"
    echo "  zookeeper                      # Zookeeper logs"
    echo "  kafka-ui                       # Kafka UI logs (dev mode)"
    echo "  pgadmin                        # PgAdmin logs (dev mode)"
    echo ""
    echo "Options:"
    echo "  -f, --follow                   Follow log output (tail -f)"
    echo "  -t, --tail LINES              Number of lines to show [default: 100]"
    echo "  -h, --help                     Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                             # Show logs from all services"
    echo "  $0 api                         # Show API logs only"
    echo "  $0 --follow api                # Follow API logs"
    echo "  $0 --tail 50 postgres          # Show last 50 lines from PostgreSQL"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--follow)
            FOLLOW=true
            shift
            ;;
        -t|--tail)
            TAIL_LINES="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        api|app|virtual-garage-api)
            SERVICE="virtual-garage-api"
            shift
            ;;
        postgres|db)
            SERVICE="postgres"
            shift
            ;;
        kafka)
            SERVICE="kafka"
            shift
            ;;
        zookeeper)
            SERVICE="zookeeper"
            shift
            ;;
        kafka-ui)
            SERVICE="kafka-ui"
            shift
            ;;
        pgadmin)
            SERVICE="pgadmin"
            shift
            ;;
        *)
            if [[ -z "$SERVICE" ]]; then
                SERVICE="$1"
                shift
            else
                echo "Unknown option or service: $1"
                usage
            fi
            ;;
    esac
done

echo -e "${BLUE}📄 Virtual Garage API Logs${NC}"

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running.${NC}"
    exit 1
fi

# Build Docker Compose command
COMPOSE_CMD="docker-compose logs"

if [[ "$FOLLOW" == true ]]; then
    COMPOSE_CMD="$COMPOSE_CMD -f"
fi

COMPOSE_CMD="$COMPOSE_CMD --tail=$TAIL_LINES"

# Add service if specified
if [[ -n "$SERVICE" ]]; then
    # Check if service exists
    if ! docker-compose ps "$SERVICE" >/dev/null 2>&1; then
        echo -e "${RED}❌ Service '$SERVICE' not found or not running.${NC}"
        echo -e "${YELLOW}💡 Available services:${NC}"
        docker-compose ps --services
        exit 1
    fi
    COMPOSE_CMD="$COMPOSE_CMD $SERVICE"
    echo -e "${GREEN}📄 Showing logs for service: $SERVICE${NC}"
else
    echo -e "${GREEN}📄 Showing logs for all services${NC}"
fi

if [[ "$FOLLOW" == true ]]; then
    echo -e "${YELLOW}🔄 Following logs (Press Ctrl+C to stop)...${NC}"
else
    echo -e "${YELLOW}📄 Showing last $TAIL_LINES lines...${NC}"
fi

echo ""

# Execute the command
$COMPOSE_CMD