#!/bin/bash

# MCP PostgreSQL Server Startup Script for Virtual Garage
# This script starts the Model Context Protocol server for PostgreSQL

# Database connection details from docker-compose.yml
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="virtual_garage"
DB_USER="virtual_garage"
DB_PASSWORD="garage123"

# Construct the PostgreSQL connection string
CONNECTION_STRING="postgresql://${DB_USER}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/${DB_NAME}"

echo "🚀 Starting MCP PostgreSQL Server for Virtual Garage..."
echo "📊 Database: ${DB_NAME} on ${DB_HOST}:${DB_PORT}"
echo "👤 User: ${DB_USER}"
echo "🔗 Connection String: postgresql://${DB_USER}:***@${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo ""

# Check if PostgreSQL is accessible
echo "🔍 Testing database connection..."
if PGPASSWORD=${DB_PASSWORD} psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -d ${DB_NAME} -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Database connection successful!"
else
    echo "❌ Database connection failed! Please ensure PostgreSQL container is running:"
    echo "   docker-compose up -d postgres"
    exit 1
fi

echo ""
echo "🎯 Starting MCP Server..."
echo "💡 The server will listen for MCP protocol messages on stdin/stdout"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start the MCP server
exec mcp-server-postgres "${CONNECTION_STRING}"