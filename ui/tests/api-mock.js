/**
 * Mock API responses for Playwright tests.
 * Use these to create consistent, predictable test scenarios.
 */

export const mockPartsSearchResponse = {
  vehicleId: 1,
  vehicleName: '2020 Ford Mustang',
  query: 'brake pads',
  llmProvider: 'claude',
  summary: 'Found 3 suggestions for brake pads on your 2020 Ford Mustang',
  suggestions: [
    {
      name: 'Brembo Performance Brake Pads',
      partNumber: 'P23001',
      description: 'High-performance ceramic brake pads designed for the 2018-2023 Ford Mustang. Excellent stopping power with minimal dust.',
      brand: 'Brembo',
      priceRange: '$120-150',
      installationDifficulty: 'Medium',
      notes: 'Recommended for street/track use. Break-in period of 200 miles required.',
      whereToBuy: ['RockAuto', 'Amazon', 'Tire Rack'],
    },
    {
      name: 'OEM Ford Motorcraft Brake Pads',
      partNumber: 'BR-123-A',
      description: 'Original equipment replacement brake pads. Direct fit for all Mustang trims.',
      brand: 'Motorcraft',
      priceRange: '$80-100',
      installationDifficulty: 'Medium',
      notes: 'OEM quality, guaranteed fit. Best for daily driving.',
      whereToBuy: ['Ford Dealer', 'AutoZone', 'Advance Auto'],
    },
    {
      name: 'PowerStop Z26 Street Warrior',
      partNumber: 'Z26-1792',
      description: 'Carbon-fiber ceramic pads for muscle cars. Upgraded stopping power for high-HP applications.',
      brand: 'PowerStop',
      priceRange: '$95-130',
      installationDifficulty: 'Medium',
      notes: 'Popular choice for modified Mustangs. Low noise, low dust.',
      whereToBuy: ['Amazon', 'RockAuto', "O'Reilly Auto Parts"],
    },
  ],
};

export const mockVehicle = {
  id: 1,
  name: 'My Mustang',
  make: 'Ford',
  model: 'Mustang',
  year: 2020,
  type: 'Sports Car',
  color: 'Grabber Blue',
  engine: '5.0L V8',
  engineSize: '5.0L',
  transmission: 'Manual',
  drivetrain: 'RWD',
  fuelType: 'Gasoline',
  mileage: 25000,
  value: 45000,
  status: 'Excellent',
  primaryImageUrl: 'https://example.com/mustang.jpg',
};

/**
 * Setup route mocking for Playwright tests.
 * 
 * Usage in test:
 * ```
 * import { setupMocks } from './api-mock';
 * 
 * test.beforeEach(async ({ page }) => {
 *   await setupMocks(page);
 * });
 * ```
 */
export async function setupMocks(page) {
  // Mock parts search API
  await page.route('**/api/parts/search', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(mockPartsSearchResponse),
    });
  });

  // Mock vehicles API
  await page.route('**/api/vehicles', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        content: [mockVehicle],
        totalElements: 1,
        totalPages: 1,
        number: 0,
      }),
    });
  });

  // Mock single vehicle API
  await page.route('**/api/vehicles/*', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(mockVehicle),
    });
  });

  // Mock dashboard stats
  await page.route('**/api/vehicles/stats/dashboard', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        total_vehicles: 1,
        total_value: 45000,
        average_value: 45000,
        new_this_month: 0,
      }),
    });
  });
}
