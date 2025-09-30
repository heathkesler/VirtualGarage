/**
 * Transform vehicle data from backend API format to UI format
 */
export function transformVehicleFromAPI(apiVehicle) {
  if (!apiVehicle) return null;

  return {
    id: apiVehicle.id,
    name: apiVehicle.name || `${apiVehicle.year} ${apiVehicle.make} ${apiVehicle.model}`,
    make: apiVehicle.make,
    model: apiVehicle.model,
    type: apiVehicle.type,
    year: apiVehicle.year,
    image: apiVehicle.primary_image_url || apiVehicle.primaryImageUrl || 'https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center',
    mileage: apiVehicle.mileage ? `${apiVehicle.mileage.toLocaleString()} ${apiVehicle.mileage_unit || apiVehicle.mileageUnit || 'miles'}` : '0 miles',
    value: apiVehicle.value ? `$${apiVehicle.value.toLocaleString()}` : '$0',
    status: apiVehicle.status || 'Unknown',
    tags: apiVehicle.tags || [],
    color: apiVehicle.color,
    engine: apiVehicle.engine,
    engineSize: apiVehicle.engine_size || apiVehicle.engineSize,
    transmission: apiVehicle.transmission,
    drivetrain: apiVehicle.drivetrain,
    fuelType: apiVehicle.fuel_type || apiVehicle.fuelType,
    description: apiVehicle.description,
    notes: apiVehicle.notes,
    vinNumber: apiVehicle.vin_number || apiVehicle.vinNumber,
    licensePlate: apiVehicle.license_plate || apiVehicle.licensePlate,
    purchasePrice: apiVehicle.purchase_price || apiVehicle.purchasePrice,
    purchaseDate: apiVehicle.purchase_date || apiVehicle.purchaseDate,
    createdAt: apiVehicle.created_at || apiVehicle.createdAt,
    updatedAt: apiVehicle.updated_at || apiVehicle.updatedAt,
  };
}

/**
 * Transform vehicle data from UI format to backend API format
 */
export function transformVehicleToAPI(uiVehicle) {
  if (!uiVehicle) return null;

  // Convert value string to number (remove $ and commas)
  const valueNumber = typeof uiVehicle.value === 'string' 
    ? parseFloat(uiVehicle.value.replace(/[$,]/g, '')) 
    : uiVehicle.value;

  // Convert mileage string to number (extract number part)
  const mileageNumber = typeof uiVehicle.mileage === 'string'
    ? parseInt(uiVehicle.mileage.replace(/[^0-9]/g, ''))
    : uiVehicle.mileage;

  return {
    id: uiVehicle.id,
    name: uiVehicle.name,
    make: uiVehicle.make,
    model: uiVehicle.model,
    type: uiVehicle.type,
    year: uiVehicle.year,
    color: uiVehicle.color,
    engine: uiVehicle.engine,
    engine_size: uiVehicle.engineSize,
    transmission: uiVehicle.transmission,
    drivetrain: uiVehicle.drivetrain,
    fuel_type: uiVehicle.fuelType,
    mileage: mileageNumber,
    mileage_unit: 'miles',
    value: valueNumber,
    status: uiVehicle.status,
    description: uiVehicle.description,
    notes: uiVehicle.notes,
    tags: uiVehicle.tags || [],
    primary_image_url: uiVehicle.image,
    vin_number: uiVehicle.vinNumber,
    license_plate: uiVehicle.licensePlate,
    purchase_price: uiVehicle.purchasePrice,
    purchase_date: uiVehicle.purchaseDate,
  };
}

/**
 * Transform dashboard stats from API format to UI format
 */
export function transformDashboardStatsFromAPI(apiStats) {
  if (!apiStats) return null;

  return {
    totalVehicles: apiStats.total_vehicles || apiStats.totalVehicles || 0,
    totalValue: apiStats.total_value || apiStats.totalValue || 0,
    avgValue: function() {
      return this.totalVehicles > 0 ? Math.round(this.totalValue / this.totalVehicles) : 0;
    }
  };
}

/**
 * Transform paginated response from API format
 */
export function transformPagedResponse(apiResponse) {
  if (!apiResponse) return { content: [], totalElements: 0, totalPages: 0 };

  return {
    content: (apiResponse.content || []).map(transformVehicleFromAPI),
    totalElements: apiResponse.totalElements || apiResponse.total_elements || 0,
    totalPages: apiResponse.totalPages || apiResponse.total_pages || 0,
    number: apiResponse.number || 0,
    size: apiResponse.size || 20,
    first: apiResponse.first !== false,
    last: apiResponse.last !== false,
    empty: apiResponse.empty !== false,
  };
}