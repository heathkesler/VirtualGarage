-- Create vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    type VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    engine VARCHAR(100),
    engine_size VARCHAR(50),
    transmission VARCHAR(50),
    drivetrain VARCHAR(50),
    fuel_type VARCHAR(50),
    mileage INTEGER,
    mileage_unit VARCHAR(10) DEFAULT 'miles',
    value DECIMAL(12,2),
    purchase_price DECIMAL(12,2),
    purchase_date TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'Excellent',
    vin_number VARCHAR(17) UNIQUE,
    license_plate VARCHAR(20),
    primary_image_url VARCHAR(500),
    description TEXT,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create vehicle_tags table
CREATE TABLE vehicle_tags (
    vehicle_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Create vehicle_images table
CREATE TABLE vehicle_images (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    original_filename VARCHAR(255),
    content_type VARCHAR(100),
    file_size BIGINT,
    image_width INTEGER,
    image_height INTEGER,
    is_primary BOOLEAN NOT NULL DEFAULT false,
    display_order INTEGER DEFAULT 0,
    caption VARCHAR(255),
    description TEXT,
    alt_text VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Create maintenance_records table
CREATE TABLE maintenance_records (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    maintenance_type VARCHAR(100) NOT NULL,
    description TEXT,
    maintenance_date TIMESTAMP NOT NULL,
    mileage_at_service INTEGER,
    service_provider VARCHAR(200),
    cost DECIMAL(10,2),
    currency VARCHAR(20) DEFAULT 'USD',
    invoice_number VARCHAR(100),
    warranty_until TIMESTAMP,
    next_service_due TIMESTAMP,
    next_service_mileage INTEGER,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_vehicle_year ON vehicles(year);
CREATE INDEX idx_vehicle_make_model ON vehicles(make, model);
CREATE INDEX idx_vehicle_type ON vehicles(type);
CREATE INDEX idx_vehicle_created ON vehicles(created_at);
CREATE INDEX idx_vehicle_is_active ON vehicles(is_active);

CREATE INDEX idx_vehicle_image_vehicle_id ON vehicle_images(vehicle_id);
CREATE INDEX idx_vehicle_image_is_primary ON vehicle_images(is_primary);
CREATE INDEX idx_vehicle_image_created ON vehicle_images(created_at);

CREATE INDEX idx_maintenance_vehicle_id ON maintenance_records(vehicle_id);
CREATE INDEX idx_maintenance_date ON maintenance_records(maintenance_date);
CREATE INDEX idx_maintenance_type ON maintenance_records(maintenance_type);

CREATE INDEX idx_vehicle_tags_vehicle_id ON vehicle_tags(vehicle_id);
CREATE INDEX idx_vehicle_tags_tag ON vehicle_tags(tag);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_maintenance_records_updated_at BEFORE UPDATE ON maintenance_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();