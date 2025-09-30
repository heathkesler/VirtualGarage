# MCP PostgreSQL Server for Virtual Garage

This document explains how to set up and use the Model Context Protocol (MCP) server for the Virtual Garage PostgreSQL database.

## Overview

The MCP PostgreSQL server enables AI assistants (like Claude) to interact directly with your PostgreSQL database through the Model Context Protocol. This allows for:

- **Schema exploration**: Browse database structure, tables, columns, and relationships
- **Query execution**: Run SELECT, INSERT, UPDATE, DELETE operations
- **Data analysis**: Perform complex analytical queries
- **Database management**: Create tables, indexes, and manage database objects

## Prerequisites

- ✅ Docker and Docker Compose (for PostgreSQL)
- ✅ Node.js 18+ and npm
- ✅ PostgreSQL client tools (psql)
- ✅ MCP PostgreSQL server package

## Installation

The MCP PostgreSQL server has been installed globally:

```bash
npm install -g @modelcontextprotocol/server-postgres
```

## Database Configuration

Your PostgreSQL database is running in Docker with these settings:

- **Host**: localhost
- **Port**: 5432
- **Database**: virtual_garage
- **Username**: virtual_garage
- **Password**: garage123
- **Connection String**: `postgresql://virtual_garage:garage123@localhost:5432/virtual_garage`

## Database Schema

Current tables in the database:

```sql
-- Core tables
- vehicles              -- Vehicle information
- vehicle_images        -- Vehicle photos and media
- vehicle_tags          -- Vehicle categorization
- maintenance_records   -- Service and maintenance history
- flyway_schema_history -- Database migration history
```

## Starting the MCP Server

### Option 1: Using the Startup Script (Recommended)

```bash
cd /Users/heathkesler/code/VirtualGarage/backend
./start-mcp-server.sh
```

This script will:
1. ✅ Test the database connection
2. 🚀 Start the MCP server with proper configuration
3. 📊 Display connection information
4. 🛑 Allow graceful shutdown with Ctrl+C

### Option 2: Direct Command

```bash
mcp-server-postgres "postgresql://virtual_garage:garage123@localhost:5432/virtual_garage"
```

### Option 3: Using Configuration File

The server can be configured using `mcp-server-config.json`:

```bash
# Start with environment variables
export MCP_CONFIG_FILE="./mcp-server-config.json"
mcp-server-postgres
```

## Testing the Server

### 1. Database Connection Test

```bash
# Test direct database access
PGPASSWORD=garage123 psql -h localhost -p 5432 -U virtual_garage -d virtual_garage -c "\\dt"
```

### 2. MCP Server Process Check

```bash
# Check if the server is running
ps aux | grep mcp-server-postgres
```

### 3. Connection String Validation

```bash
# Test connection string format
node -e "console.log(new URL('postgresql://virtual_garage:garage123@localhost:5432/virtual_garage'))"
```

## Server Capabilities

When the MCP server is running, it provides these tools:

### Database Schema Tools
- `list_tables` - List all tables in the database
- `describe_table` - Get detailed table schema information
- `list_schemas` - List database schemas

### Query Execution Tools
- `query` - Execute SELECT queries
- `execute` - Execute INSERT/UPDATE/DELETE operations

### Data Analysis Tools
- `analyze_table` - Get table statistics
- `explain_query` - Get query execution plan

## Usage Examples

Once connected through MCP, you can:

### Explore the Database
```sql
-- List all tables
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Describe vehicles table
\\d vehicles

-- Count records in each table
SELECT 
  schemaname,
  tablename,
  n_tup_ins as inserts,
  n_tup_upd as updates,
  n_tup_del as deletes
FROM pg_stat_user_tables;
```

### Query Vehicle Data
```sql
-- Get all vehicles with their maintenance records
SELECT 
  v.id,
  v.make,
  v.model,
  v.year,
  COUNT(mr.id) as maintenance_count
FROM vehicles v
LEFT JOIN maintenance_records mr ON v.id = mr.vehicle_id
GROUP BY v.id, v.make, v.model, v.year
ORDER BY v.year DESC;
```

### Analyze Data Patterns
```sql
-- Vehicle distribution by make
SELECT make, COUNT(*) as count
FROM vehicles
GROUP BY make
ORDER BY count DESC;

-- Maintenance frequency analysis
SELECT 
  DATE_TRUNC('month', performed_date) as month,
  COUNT(*) as maintenance_count
FROM maintenance_records
WHERE performed_date >= NOW() - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', performed_date)
ORDER BY month;
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   ```bash
   # Ensure PostgreSQL container is running
   docker-compose ps postgres
   docker-compose up -d postgres
   ```

2. **Authentication Failed**
   ```bash
   # Verify credentials in docker-compose.yml
   cat docker-compose.yml | grep -A 10 "POSTGRES_"
   ```

3. **MCP Server Not Found**
   ```bash
   # Reinstall MCP server
   npm install -g @modelcontextprotocol/server-postgres
   ```

4. **Port Already in Use**
   ```bash
   # Check what's using PostgreSQL port
   lsof -i :5432
   ```

### Debug Mode

Start the server with debug logging:

```bash
DEBUG=mcp:* mcp-server-postgres "postgresql://virtual_garage:garage123@localhost:5432/virtual_garage"
```

## Security Considerations

- 🔒 The database password is visible in the connection string
- 🛡️ Only run this server in development environments
- 🔐 For production, use environment variables for credentials
- 🚧 The server has full read/write access to the database

## Environment Variables

You can use environment variables for sensitive data:

```bash
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=virtual_garage
export POSTGRES_USER=virtual_garage
export POSTGRES_PASSWORD=garage123

# Then use:
mcp-server-postgres "postgresql://$POSTGRES_USER:$POSTGRES_PASSWORD@$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
```

## Files Created

- ✅ `start-mcp-server.sh` - Startup script with connection testing
- ✅ `mcp-server-config.json` - Configuration file
- ✅ `MCP-POSTGRES-README.md` - This documentation

## Next Steps

1. Start the MCP server using the startup script
2. Connect your AI assistant to the MCP server
3. Begin exploring and querying your Virtual Garage database
4. Use the MCP capabilities for data analysis and management

---

**Happy querying! 🚗💾**