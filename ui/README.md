# Virtual Garage - Modern Garage Management Application

A modern, responsive garage management application built with React, TypeScript, and Vite. This application allows users to manage multiple garages, track vehicles, and create detailed build sheets for their automotive projects.

## 🚗 Features

### ✅ Completed Features

- **Modern Authentication System**
  - Beautiful login/register forms with animations
  - Form validation and error handling
  - Secure user sessions with localStorage
  - Password visibility toggles
  - Smooth form transitions

- **Dynamic Dashboard**
  - Overview of all garages and vehicles
  - Real-time statistics (vehicle count, build progress, investment tracking)
  - Interactive cards with hover effects
  - Responsive design for mobile and desktop

- **Modern UI Components**
  - Built with Radix UI primitives
  - Tailwind CSS for styling
  - Framer Motion animations
  - Custom design system with CSS variables
  - Consistent visual hierarchy

- **Mock Data & API Layer**
  - Complete TypeScript type definitions
  - Mock data for development
  - Stock vehicle photos from Unsplash
  - Realistic build components and pricing

- **Testing Suite**
  - Playwright E2E tests
  - Authentication flow testing
  - Dashboard functionality tests
  - Responsive design testing

### 🔨 In Development

- **Garage Management Interface**
  - Create, edit, and delete garages
  - Garage-specific settings and descriptions

- **Vehicle Management System**
  - Add vehicles with stock photos or custom uploads
  - Vehicle details (make, model, year, VIN, mileage)
  - Photo upload functionality

- **Build Sheet System**
  - Component search and selection
  - Detailed parts management
  - Cost tracking and budgeting
  - Build progress monitoring

## 🛠️ Technology Stack

- **Frontend Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS v4
- **UI Components**: Radix UI
- **Animations**: Framer Motion
- **Form Handling**: React Hook Form with Zod validation
- **Testing**: Playwright
- **Icons**: Lucide React

## 📁 Project Structure

```
src/
├── components/
│   ├── auth/              # Authentication components
│   ├── ui/                # Reusable UI components
│   └── Header.tsx         # Main navigation header
├── contexts/
│   └── AuthContext.tsx    # Authentication state management
├── hooks/                 # Custom React hooks
├── lib/
│   └── utils.ts          # Utility functions
├── pages/
│   ├── AuthPage.tsx      # Login/register page
│   └── DashboardPage.tsx # Main dashboard
├── services/
│   └── mockData.ts       # Mock data and API services
└── types/
    └── index.ts          # TypeScript type definitions
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

1. Clone the repository
```bash
git clone <repository-url>
cd VirtualGarage/ui
```

2. Install dependencies
```bash
npm install
```

3. Start the development server
```bash
npm run dev
```

4. Open your browser and navigate to `http://localhost:5173`

### Building for Production

```bash
npm run build
```

### Running Tests

```bash
# Install Playwright browsers (first time only)
npx playwright install

# Run tests (start dev server first)
npm run dev
# In another terminal:
npm test

# Run tests with UI
npm run test:ui

# Debug tests
npm run test:debug
```

## 🎨 Design System

The application uses a modern design system with:

- **Color Palette**: HSL-based CSS variables for light/dark themes
- **Typography**: Inter font with proper hierarchy
- **Spacing**: Consistent spacing scale
- **Animations**: Smooth micro-interactions
- **Components**: Accessible, reusable UI primitives

### Key Design Principles

1. **Mobile-First**: Responsive design optimized for all devices
2. **Accessibility**: WCAG compliant components
3. **Performance**: Optimized animations and lazy loading
4. **Consistency**: Unified visual language throughout

## 🧪 Testing Strategy

### End-to-End Tests

- **Authentication Flow**: Login, register, validation
- **Dashboard Functionality**: Stats display, garage navigation
- **Responsive Design**: Mobile and desktop layouts
- **User Interactions**: Form submissions, navigation

### Test Files

- `tests/auth.spec.ts` - Authentication flow tests
- `tests/dashboard.spec.ts` - Dashboard functionality tests

## 📊 Mock Data

The application includes realistic mock data:

- **Vehicles**: Honda Civic Type R, Toyota Supra, Ford Mustang GT
- **Components**: Performance parts from real manufacturers
- **Builds**: Stage 2 performance builds with realistic costs
- **Stock Photos**: High-quality vehicle images from Unsplash

## 🔒 Authentication

Current implementation uses localStorage for demo purposes:

- **Login**: Any email/password combination works
- **Registration**: Creates mock user with avatar
- **Session**: Persisted in localStorage
- **Logout**: Clears session data

*Note: In production, this would integrate with a proper backend authentication system.*

## 🎯 Next Steps

1. **Garage Management**
   - Create/edit garage forms
   - Garage-specific vehicle organization

2. **Vehicle Management**
   - Advanced vehicle search
   - Photo upload with image optimization
   - Vehicle specifications database

3. **Build System**
   - Component marketplace integration
   - Build templates and presets
   - Cost optimization tools

4. **Backend Integration**
   - REST API development
   - Database schema design
   - File upload handling

## 🤝 Contributing

This is a demonstration project showcasing modern web development practices. The codebase follows:

- **Clean Architecture**: Separation of concerns
- **Type Safety**: Full TypeScript coverage
- **Modern Patterns**: Hooks, context, composition
- **Testing**: Comprehensive E2E coverage

## 📝 License

This project is for demonstration purposes.

## 🎨 Design Credits

- **Icons**: Lucide React
- **Images**: Unsplash (vehicle stock photos)
- **UI Components**: Radix UI primitives
- **Design System**: Custom implementation inspired by shadcn/ui

---

**Built with ❤️ using modern web technologies**