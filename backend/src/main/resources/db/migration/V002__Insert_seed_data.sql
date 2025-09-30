-- Insert sample vehicles (matching the UI data)
INSERT INTO vehicles (name, make, model, year, type, color, engine, transmission, fuel_type, mileage, value, status, primary_image_url, description) VALUES
(
    '2024 BMW M3 Competition', 
    'BMW', 
    'M3 Competition', 
    2024, 
    'Sport Sedan',
    'Alpine White',
    'Twin-Turbo 3.0L I6',
    'Automatic',
    'Gasoline',
    1250,
    89500.00,
    'Excellent',
    'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop&crop=center',
    'High-performance luxury sedan with M TwinPower Turbo engine. Perfect balance of luxury and sport.'
),
(
    '1969 Ford Mustang Boss 429', 
    'Ford', 
    'Mustang Boss 429', 
    1969, 
    'Classic Muscle',
    'Grabber Orange',
    '429 Super Cobra Jet V8',
    'Manual',
    'Gasoline',
    45000,
    165000.00,
    'Pristine',
    'https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center',
    'Legendary Boss 429 with numbers-matching 429 Super Cobra Jet engine. One of the most sought-after muscle cars.'
),
(
    '2023 Porsche 911 GT3', 
    'Porsche', 
    '911 GT3', 
    2023, 
    'Sports Car',
    'Racing Yellow',
    'Naturally Aspirated 4.0L Flat-6',
    'PDK',
    'Gasoline',
    3200,
    195000.00,
    'Excellent',
    'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop&crop=center',
    'Track-focused 911 with naturally aspirated flat-six engine. Racing DNA in a road-legal package.'
),
(
    '2024 Tesla Model S Plaid', 
    'Tesla', 
    'Model S Plaid', 
    2024, 
    'Electric Sedan',
    'Pearl White',
    'Tri-Motor Electric',
    'Single-Speed',
    'Electric',
    8500,
    105000.00,
    'Like New',
    'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop&crop=center',
    'Fastest production sedan with 1,020 horsepower. Cutting-edge technology and performance.'
),
(
    '2022 McLaren 720S', 
    'McLaren', 
    '720S', 
    2022, 
    'Supercar',
    'McLaren Orange',
    'Twin-Turbo 4.0L V8',
    'Automatic',
    'Gasoline',
    2100,
    285000.00,
    'Pristine',
    'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop&crop=center',
    'British engineering excellence with 710 horsepower. Carbon fiber monocoque chassis.'
),
(
    '1965 Shelby Cobra 427', 
    'Shelby', 
    'Cobra 427', 
    1965, 
    'Classic Roadster',
    'Guardsman Blue',
    '427 Side-Oiler V8',
    'Manual',
    'Gasoline',
    12000,
    750000.00,
    'Concours',
    'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop&crop=center',
    'Legendary American sports car with massive 427 big-block V8. Racing heritage and timeless design.'
);

-- Insert vehicle tags
INSERT INTO vehicle_tags (vehicle_id, tag) VALUES
(1, 'Performance'),
(1, 'Daily Driver'),
(2, 'Classic'),
(2, 'Collector'),
(3, 'Track'),
(3, 'Performance'),
(4, 'Electric'),
(4, 'Tech'),
(5, 'Exotic'),
(5, 'Performance'),
(6, 'Classic'),
(6, 'Rare'),
(6, 'Investment');

-- Insert sample vehicle images
INSERT INTO vehicle_images (vehicle_id, image_url, is_primary, display_order, caption, alt_text) VALUES
(1, 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop&crop=center', true, 1, 'Front view of BMW M3 Competition', 'BMW M3 Competition front exterior'),
(2, 'https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center', true, 1, 'Classic Ford Mustang Boss 429', 'Ford Mustang Boss 429 side profile'),
(3, 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop&crop=center', true, 1, 'Porsche 911 GT3 Racing Yellow', 'Porsche 911 GT3 front three-quarter view'),
(4, 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop&crop=center', true, 1, 'Tesla Model S Plaid in Pearl White', 'Tesla Model S Plaid exterior'),
(5, 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop&crop=center', true, 1, 'McLaren 720S in Signature Orange', 'McLaren 720S side view'),
(6, 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop&crop=center', true, 1, 'Shelby Cobra 427 Classic Blue', 'Shelby Cobra 427 front view');

-- Insert sample maintenance records
INSERT INTO maintenance_records (vehicle_id, maintenance_type, description, maintenance_date, mileage_at_service, service_provider, cost, notes) VALUES
(1, 'Oil Change', 'Regular maintenance oil change with BMW approved oil', '2024-01-15 10:00:00', 500, 'BMW Service Center', 150.00, 'Used BMW approved 0W-30 oil'),
(1, 'Brake Inspection', 'Annual brake system inspection', '2024-03-20 14:30:00', 800, 'BMW Service Center', 75.00, 'All components in excellent condition'),
(2, 'Engine Tune-up', 'Complete engine tune-up for classic muscle car', '2024-02-10 09:00:00', 44800, 'Classic Car Specialists', 850.00, 'Replaced spark plugs, points, and condenser'),
(3, 'Track Day Preparation', 'Pre-track inspection and fluid changes', '2024-04-05 08:00:00', 3000, 'Porsche Track Support', 300.00, 'All systems checked and approved for track use'),
(4, 'Software Update', 'Over-the-air software update and inspection', '2024-03-01 12:00:00', 8000, 'Tesla Service', 0.00, 'Updated to latest firmware version'),
(6, 'Annual Inspection', 'Complete annual inspection for classic vehicle', '2024-01-30 11:00:00', 11950, 'Cobra Restoration Shop', 500.00, 'All original components verified and documented');

-- Update primary image URLs in vehicles table to match the inserted images
UPDATE vehicles SET primary_image_url = (
    SELECT image_url FROM vehicle_images 
    WHERE vehicle_images.vehicle_id = vehicles.id 
    AND vehicle_images.is_primary = true 
    LIMIT 1
);