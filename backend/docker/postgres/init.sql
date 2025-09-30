-- PostgreSQL Database Initialization Script for Virtual Garage
-- This script is executed when the PostgreSQL container starts for the first time

-- Create the application user if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'virtual_garage') THEN
        CREATE USER virtual_garage WITH PASSWORD 'garage123';
    END IF;
END
$$;

-- Create the database if it doesn't exist
SELECT 'CREATE DATABASE virtual_garage OWNER virtual_garage'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'virtual_garage')\gexec

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON DATABASE virtual_garage TO virtual_garage;

-- Connect to the virtual_garage database
\c virtual_garage;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO virtual_garage;
GRANT ALL ON ALL TABLES IN SCHEMA public TO virtual_garage;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO virtual_garage;
GRANT ALL ON ALL FUNCTIONS IN SCHEMA public TO virtual_garage;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO virtual_garage;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO virtual_garage;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO virtual_garage;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log successful initialization
SELECT 'PostgreSQL database initialization completed successfully' AS status;