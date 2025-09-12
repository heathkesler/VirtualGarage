-- Virtual Garage Database Schema
-- Initial migration script

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS virtualgarage;

-- Set search path
SET search_path TO virtualgarage;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT false,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for users
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_role ON users(role);
CREATE INDEX idx_user_status ON users(status);

-- Garages table
CREATE TABLE garages (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    user_id VARCHAR(36) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT false,
    share_code VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_garage_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for garages
CREATE INDEX idx_garage_user ON garages(user_id);
CREATE INDEX idx_garage_created ON garages(created_at);
CREATE INDEX idx_garage_share_code ON garages(share_code);

-- Vehicles table
CREATE TABLE vehicles (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    garage_id VARCHAR(36) NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(50),
    vin VARCHAR(17) UNIQUE,
    mileage INTEGER,
    photo VARCHAR(500),
    stock_photo_url VARCHAR(500),
    trim VARCHAR(50),
    engine VARCHAR(50),
    transmission VARCHAR(50),
    fuel_type VARCHAR(20),
    drivetrain VARCHAR(20),
    is_favorite BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_garage FOREIGN KEY (garage_id) REFERENCES garages(id) ON DELETE CASCADE,
    CONSTRAINT chk_vehicle_year CHECK (year >= 1900 AND year <= 2030)
);

-- Create indexes for vehicles
CREATE INDEX idx_vehicle_garage ON vehicles(garage_id);
CREATE INDEX idx_vehicle_make_model ON vehicles(make, model);
CREATE INDEX idx_vehicle_year ON vehicles(year);
CREATE INDEX idx_vehicle_vin ON vehicles(vin);

-- Vehicle photos table
CREATE TABLE vehicle_photos (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    vehicle_id VARCHAR(36) NOT NULL,
    url VARCHAR(500) NOT NULL,
    caption VARCHAR(500),
    is_primary BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_photo_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Create indexes for vehicle_photos
CREATE INDEX idx_vehicle_photo_vehicle ON vehicle_photos(vehicle_id);

-- Builds table
CREATE TABLE builds (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    vehicle_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    target_completion_date TIMESTAMP,
    actual_completion_date TIMESTAMP,
    is_public BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_build_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    CONSTRAINT chk_build_total_cost CHECK (total_cost >= 0)
);

-- Create indexes for builds
CREATE INDEX idx_build_vehicle ON builds(vehicle_id);
CREATE INDEX idx_build_status ON builds(status);
CREATE INDEX idx_build_created ON builds(created_at);

-- Build photos table
CREATE TABLE build_photos (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    build_id VARCHAR(36) NOT NULL,
    url VARCHAR(500) NOT NULL,
    caption VARCHAR(500),
    phase VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_build_photo_build FOREIGN KEY (build_id) REFERENCES builds(id) ON DELETE CASCADE
);

-- Create indexes for build_photos
CREATE INDEX idx_build_photo_build ON build_photos(build_id);

-- Components table
CREATE TABLE components (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    build_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    part_number VARCHAR(100),
    description TEXT,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    url VARCHAR(500),
    image_url VARCHAR(500),
    notes TEXT,
    order_date TIMESTAMP,
    received_date TIMESTAMP,
    installed_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_component_build FOREIGN KEY (build_id) REFERENCES builds(id) ON DELETE CASCADE,
    CONSTRAINT chk_component_price CHECK (price >= 0),
    CONSTRAINT chk_component_quantity CHECK (quantity >= 1)
);

-- Create indexes for components
CREATE INDEX idx_component_build ON components(build_id);
CREATE INDEX idx_component_category ON components(category);
CREATE INDEX idx_component_status ON components(status);
CREATE INDEX idx_component_brand_name ON components(brand, name);

-- Saved parts table
CREATE TABLE saved_parts (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    user_id VARCHAR(36) NOT NULL,
    part_name VARCHAR(200) NOT NULL,
    part_brand VARCHAR(100) NOT NULL,
    part_number VARCHAR(100),
    part_url VARCHAR(500),
    image_url VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_part_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for saved_parts
CREATE INDEX idx_saved_part_user ON saved_parts(user_id);
CREATE INDEX idx_saved_part_created ON saved_parts(created_at);

-- Search history table
CREATE TABLE search_history (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    user_id VARCHAR(36) NOT NULL,
    query VARCHAR(500) NOT NULL,
    vehicle_make VARCHAR(50),
    vehicle_model VARCHAR(50),
    vehicle_year INTEGER,
    category VARCHAR(50),
    results_count INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_search_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for search_history
CREATE INDEX idx_search_user ON search_history(user_id);
CREATE INDEX idx_search_created ON search_history(created_at);

-- Refresh tokens table (for JWT authentication)
CREATE TABLE refresh_tokens (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for refresh_tokens
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_expires ON refresh_tokens(expires_at);

-- Stock vehicles reference table (for initial vehicle selection)
CREATE TABLE stock_vehicles (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4()::VARCHAR,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    trim VARCHAR(50),
    body_style VARCHAR(50),
    engine VARCHAR(100),
    transmission VARCHAR(50),
    drivetrain VARCHAR(20),
    fuel_type VARCHAR(20),
    image_url VARCHAR(500),
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stock_vehicle_year CHECK (year >= 1900 AND year <= 2030)
);

-- Create indexes for stock_vehicles
CREATE INDEX idx_stock_vehicle_make_model_year ON stock_vehicles(make, model, year);
CREATE INDEX idx_stock_vehicle_category ON stock_vehicles(category);

-- Insert some sample stock vehicles
INSERT INTO stock_vehicles (make, model, year, trim, body_style, engine, transmission, drivetrain, fuel_type, image_url, category) VALUES
('Honda', 'Civic', 2023, 'Sport', 'Sedan', '2.0L I4', 'CVT', 'FWD', 'Gasoline', 'https://example.com/honda-civic-2023.jpg', 'Compact'),
('Toyota', 'Camry', 2023, 'XSE', 'Sedan', '2.5L I4', '8-Speed Automatic', 'FWD', 'Gasoline', 'https://example.com/toyota-camry-2023.jpg', 'Midsize'),
('Ford', 'F-150', 2023, 'XLT', 'Truck', '3.5L V6', '10-Speed Automatic', 'RWD', 'Gasoline', 'https://example.com/ford-f150-2023.jpg', 'Truck'),
('BMW', '330i', 2023, 'Base', 'Sedan', '2.0L I4 Turbo', '8-Speed Automatic', 'RWD', 'Gasoline', 'https://example.com/bmw-330i-2023.jpg', 'Luxury'),
('Chevrolet', 'Corvette', 2023, 'Stingray', 'Coupe', '6.2L V8', '8-Speed DCT', 'RWD', 'Gasoline', 'https://example.com/chevrolet-corvette-2023.jpg', 'Sports');

-- Create function to update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_garages_updated_at BEFORE UPDATE ON garages FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_builds_updated_at BEFORE UPDATE ON builds FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_components_updated_at BEFORE UPDATE ON components FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();