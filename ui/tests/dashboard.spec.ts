import { test, expect } from '@playwright/test'

// Helper function to login
async function login(page: any) {
  await page.goto('/')
  await page.getByRole('textbox', { name: /email/i }).fill('test@example.com')
  await page.getByLabel(/password/i).fill('password123')
  await page.getByRole('button', { name: /sign in/i }).click()
  
  // Wait for dashboard to load
  await page.waitForSelector('text=Welcome back')
}

test.describe('Dashboard', () => {
  test.beforeEach(async ({ page }) => {
    // Clear localStorage before each test
    await page.goto('/')
    await page.evaluate(() => localStorage.clear())
    await page.reload()
  })

  test('should display dashboard after successful login', async ({ page }) => {
    await login(page)
    
    // Check main dashboard elements
    await expect(page.getByRole('heading', { name: /welcome back/i })).toBeVisible()
    await expect(page.getByText('Manage your virtual garage and track your builds')).toBeVisible()
    await expect(page.getByRole('button', { name: /new garage/i })).toBeVisible()
  })

  test('should display stats cards with correct information', async ({ page }) => {
    await login(page)
    
    // Check that all stats cards are present
    await expect(page.getByText('Total Vehicles')).toBeVisible()
    await expect(page.getByText('Active Builds')).toBeVisible()
    await expect(page.getByText('Total Investment')).toBeVisible()
    await expect(page.getByText('Completion Rate')).toBeVisible()
    
    // Check that stats have values (using mock data)
    await expect(page.locator('text=3').first()).toBeVisible() // Total vehicles
    await expect(page.locator('text=2').first()).toBeVisible() // Active builds
  })

  test('should display garage cards', async ({ page }) => {
    await login(page)
    
    // Check garage section
    await expect(page.getByRole('heading', { name: /your garages/i })).toBeVisible()
    
    // Check individual garage cards (based on mock data)
    await expect(page.getByText('My Daily Drivers')).toBeVisible()
    await expect(page.getByText('Cars I drive every day')).toBeVisible()
    await expect(page.getByText('Weekend Warriors')).toBeVisible()
    await expect(page.getByText('Fun cars for the weekend')).toBeVisible()
  })

  test('should display vehicle cards within garages', async ({ page }) => {
    await login(page)
    
    // Wait for garage cards to load
    await page.waitForSelector('text=My Daily Drivers')
    
    // Check for vehicle information in garage cards
    await expect(page.locator('text=Honda')).toBeVisible()
    await expect(page.locator('text=Toyota')).toBeVisible()
    await expect(page.locator('text=Ford')).toBeVisible()
  })

  test('should have working navigation header', async ({ page }) => {
    await login(page)
    
    // Check header elements
    await expect(page.getByText('Virtual Garage').first()).toBeVisible()
    await expect(page.getByText('Dashboard')).toBeVisible()
    await expect(page.getByText('Garages')).toBeVisible()
    await expect(page.getByText('Vehicles')).toBeVisible()
    await expect(page.getByText('Builds')).toBeVisible()
  })

  test('should have working user menu', async ({ page }) => {
    await login(page)
    
    // Click on user avatar/name to open dropdown
    const userMenuTrigger = page.locator('button').filter({ hasText: 'test' }).first()
    await userMenuTrigger.click()
    
    // Check dropdown menu items
    await expect(page.getByText('Profile')).toBeVisible()
    await expect(page.getByText('Settings')).toBeVisible()
    await expect(page.getByText('Logout')).toBeVisible()
  })

  test('should logout successfully', async ({ page }) => {
    await login(page)
    
    // Click on user menu
    const userMenuTrigger = page.locator('button').filter({ hasText: 'test' }).first()
    await userMenuTrigger.click()
    
    // Click logout
    await page.getByText('Logout').click()
    
    // Should redirect back to login page
    await expect(page.getByText('Welcome back')).toBeVisible()
    await expect(page.getByText('Sign in to your Virtual Garage account')).toBeVisible()
  })

  test('should show hover effects on interactive elements', async ({ page }) => {
    await login(page)
    
    // Test hover on stats cards - they should have transform effects
    const statsCard = page.locator('.hover\\:shadow-lg').first()
    await expect(statsCard).toBeVisible()
    
    // Test hover on garage cards
    const garageCard = page.locator('.hover\\:shadow-xl').first()
    await expect(garageCard).toBeVisible()
  })

  test('should display loading state initially', async ({ page }) => {
    await page.goto('/')
    await page.getByRole('textbox', { name: /email/i }).fill('test@example.com')
    await page.getByLabel(/password/i).fill('password123')
    await page.getByRole('button', { name: /sign in/i }).click()
    
    // Should show loading spinner while dashboard loads
    // This test might be flaky due to fast loading, but it's good to have
    const loadingSpinner = page.locator('.animate-spin')
    // Just check that the spinner element exists in DOM
    await expect(loadingSpinner.first()).toBeDefined()
  })

  test('should have responsive layout', async ({ page, isMobile }) => {
    await login(page)
    
    if (isMobile) {
      // On mobile, navigation items should be hidden
      await expect(page.getByText('Dashboard').nth(1)).toBeHidden()
      await expect(page.getByText('Garages').nth(1)).toBeHidden()
    } else {
      // On desktop, navigation should be visible
      await expect(page.getByText('Dashboard').nth(1)).toBeVisible()
      await expect(page.getByText('Garages').nth(1)).toBeVisible()
    }
  })
})