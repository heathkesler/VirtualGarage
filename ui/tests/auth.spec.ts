import { test, expect } from '@playwright/test'

test.describe('Authentication', () => {
  test.beforeEach(async ({ page }) => {
    // Clear localStorage before each test
    await page.goto('/')
    await page.evaluate(() => localStorage.clear())
    await page.reload()
  })

  test('should show login form by default', async ({ page }) => {
    await page.goto('/')
    
    // Check that we're on the login page
    await expect(page.getByText('Welcome back')).toBeVisible()
    await expect(page.getByText('Sign in to your Virtual Garage account')).toBeVisible()
    await expect(page.getByRole('textbox', { name: /email/i })).toBeVisible()
    await expect(page.getByLabel(/password/i)).toBeVisible()
    await expect(page.getByRole('button', { name: /sign in/i })).toBeVisible()
  })

  test('should switch to register form when clicking sign up', async ({ page }) => {
    await page.goto('/')
    
    // Click the sign up link
    await page.getByRole('button', { name: /sign up/i }).click()
    
    // Check that we're now on the register form
    await expect(page.getByText('Create account')).toBeVisible()
    await expect(page.getByText('Join Virtual Garage and start building your dream cars')).toBeVisible()
    await expect(page.getByRole('textbox', { name: /full name/i })).toBeVisible()
    await expect(page.getByRole('textbox', { name: /email/i })).toBeVisible()
    await expect(page.locator('input[type=\"password\"]').first()).toBeVisible()
    await expect(page.getByRole('button', { name: /create account/i })).toBeVisible()
  })

  test('should switch back to login form', async ({ page }) => {
    await page.goto('/')
    
    // Go to register form
    await page.getByRole('button', { name: /sign up/i }).click()
    
    // Switch back to login
    await page.getByRole('button', { name: /sign in/i }).click()
    
    // Should be back to login form
    await expect(page.getByText('Welcome back')).toBeVisible()
  })

  test('should show password visibility toggle', async ({ page }) => {
    await page.goto('/')
    
    const passwordInput = page.getByLabel(/password/i)
    const toggleButton = page.getByRole('button').filter({ has: page.locator('[data-lucide=\"eye\"], [data-lucide=\"eye-off\"]') }).first()
    
    // Password should be hidden by default
    await expect(passwordInput).toHaveAttribute('type', 'password')
    
    // Click toggle to show password
    await toggleButton.click()
    await expect(passwordInput).toHaveAttribute('type', 'text')
    
    // Click toggle again to hide password
    await toggleButton.click()
    await expect(passwordInput).toHaveAttribute('type', 'password')
  })

  test('should successfully login with valid credentials', async ({ page }) => {
    await page.goto('/')
    
    // Fill in login form
    await page.getByRole('textbox', { name: /email/i }).fill('test@example.com')
    await page.getByLabel(/password/i).fill('password123')
    
    // Submit form
    await page.getByRole('button', { name: /sign in/i }).click()
    
    // Should redirect to dashboard
    await expect(page.getByText(/welcome back/i)).toBeVisible()
    await expect(page.getByText('Virtual Garage')).toBeVisible()
    await expect(page.getByText('Total Vehicles')).toBeVisible()
  })

  test('should successfully register new user', async ({ page }) => {
    await page.goto('/')
    
    // Go to register form
    await page.getByRole('button', { name: /sign up/i }).click()
    
    // Fill in register form
    await page.getByRole('textbox', { name: /full name/i }).fill('Test User')
    await page.getByRole('textbox', { name: /email/i }).fill('newuser@example.com')
    await page.locator('input[type=\"password\"]').first().fill('password123')
    await page.locator('input[type=\"password\"]').nth(1).fill('password123')
    
    // Submit form
    await page.getByRole('button', { name: /create account/i }).click()
    
    // Should redirect to dashboard
    await expect(page.getByText(/welcome back/i)).toBeVisible()
    await expect(page.getByText('Virtual Garage')).toBeVisible()
  })

  test('should show error for password mismatch during registration', async ({ page }) => {
    await page.goto('/')
    
    // Go to register form
    await page.getByRole('button', { name: /sign up/i }).click()
    
    // Fill in register form with mismatched passwords
    await page.getByRole('textbox', { name: /full name/i }).fill('Test User')
    await page.getByRole('textbox', { name: /email/i }).fill('newuser@example.com')
    await page.locator('input[type=\"password\"]').first().fill('password123')
    await page.locator('input[type=\"password\"]').nth(1).fill('different')
    
    // Submit form
    await page.getByRole('button', { name: /create account/i }).click()
    
    // Should show error message
    await expect(page.getByText('Passwords do not match')).toBeVisible()
  })

  test('should show loading state during login', async ({ page }) => {
    await page.goto('/')
    
    // Fill in login form
    await page.getByRole('textbox', { name: /email/i }).fill('test@example.com')
    await page.getByLabel(/password/i).fill('password123')
    
    // Submit form and check for loading state
    await page.getByRole('button', { name: /sign in/i }).click()
    
    // The button should be disabled during loading
    const signInButton = page.getByRole('button', { name: /sign in/i })
    await expect(signInButton).toBeDisabled()
  })
})