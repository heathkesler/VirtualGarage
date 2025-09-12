-- Initialize Virtual Garage Database

-- Create additional extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Grant privileges to existing user
GRANT ALL PRIVILEGES ON DATABASE virtualgarage TO virtualgarage;

-- Set connection settings
ALTER DATABASE virtualgarage SET timezone TO 'UTC';