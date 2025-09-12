# 🚗 Virtual Garage

A modern, full-stack garage management application for automotive enthusiasts to manage their vehicles, track builds, and organize parts and modifications.

## 🏗️ Project Structure

```
VirtualGarage/
├── ui/                     # React TypeScript Frontend
├── backend/               # Java Spring Boot Backend
├── docs/                  # Documentation
└── README.md             # This file
```

## ✨ Features

### 🎯 Current Features (UI)
- **🔐 Authentication System**: Modern login/register with session management
- **📊 Interactive Dashboard**: Real-time statistics and garage overview
- **🏠 Garage Management**: Multiple garage organization
- **🚗 Vehicle Tracking**: Vehicle information and build management
- **📱 Responsive Design**: Works perfectly on desktop and mobile
- **✨ Modern UI**: Smooth animations and professional design

### 🚧 Planned Features
- **🔧 Build Sheet Management**: Detailed parts lists and modification tracking
- **💰 Cost Tracking**: Budget management and expense tracking  
- **📷 Photo Management**: Vehicle and parts photo uploads
- **🔍 Parts Search**: Integration with parts databases
- **📈 Analytics**: Build progress and cost analytics
- **🌐 API Integration**: RESTful backend services

## 🛠️ Technology Stack

### Frontend (`/ui`)
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS v3
- **UI Components**: Radix UI primitives
- **Animations**: Framer Motion
- **Form Management**: React Hook Form + Zod
- **Testing**: Playwright E2E tests
- **Icons**: Lucide React

### Backend (`/backend`)
- **Framework**: Java Spring Boot
- **Database**: PostgreSQL (planned)
- **Authentication**: Spring Security + JWT
- **API**: RESTful services
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit + TestContainers

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Java 17+ and Maven (for backend)
- Git

### Frontend Setup
```bash
cd ui
npm install
npm run build
python3 -m http.server 8000 --directory dist
# Open http://localhost:8000
```

### Backend Setup (Coming Soon)
```bash
cd backend
mvn spring-boot:run
# API will be available at http://localhost:8080
```

## 📱 Application Preview

### Login Screen
- Professional authentication interface
- Email/password validation
- "Remember me" functionality

### Dashboard
- **Statistics Overview**: Vehicle count, active builds, total investment
- **Garage Cards**: Interactive garage management
- **Recent Activity**: Latest updates and modifications

### Garage Management
- **Multiple Garages**: Organize vehicles by category
- **Vehicle Details**: Make, model, year, VIN, mileage tracking
- **Build Sheets**: Track modifications and upgrades

## 🎨 Design System

The application uses a modern design system featuring:
- **Color Palette**: Carefully crafted light/dark themes
- **Typography**: Consistent font hierarchy with Inter font family
- **Components**: Reusable, accessible UI primitives
- **Animations**: Subtle micro-interactions for enhanced UX
- **Icons**: Consistent iconography with Lucide React

## 🧪 Testing

### Frontend Testing
- **E2E Tests**: Comprehensive user flow testing with Playwright
- **Component Testing**: Unit tests for critical components
- **Visual Testing**: Screenshot comparison testing

### Backend Testing (Planned)
- **Unit Tests**: JUnit for service layer testing
- **Integration Tests**: TestContainers for database testing
- **API Tests**: Contract testing with REST Assured

## 📦 Deployment

### Development
- Frontend: Python HTTP server for development
- Backend: Spring Boot embedded server

### Production (Planned)
- Frontend: Static site deployment (Vercel/Netlify)
- Backend: Container deployment (Docker + Kubernetes)
- Database: PostgreSQL (AWS RDS/Google Cloud SQL)

## 🔧 Development

### Project Standards
- **Code Style**: ESLint + Prettier for frontend, Checkstyle for backend
- **Git Workflow**: Feature branches with pull requests
- **Documentation**: Comprehensive README files in each module
- **Testing**: Required tests for new features

### Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📚 Documentation

- **[Frontend Documentation](./ui/README.md)**: React app setup and development
- **[Backend Documentation](./backend/README.md)**: Spring Boot API documentation
- **[API Documentation](./docs/api.md)**: RESTful API endpoints (coming soon)
- **[Deployment Guide](./docs/deployment.md)**: Production deployment instructions (coming soon)

## 🎯 Roadmap

### Phase 1: Foundation (✅ Current)
- [x] Project structure setup
- [x] Frontend authentication system
- [x] Interactive dashboard
- [x] Basic garage management UI
- [x] Responsive design implementation

### Phase 2: Backend Integration (🚧 In Progress)
- [ ] Spring Boot REST API
- [ ] Database schema design
- [ ] Authentication endpoints
- [ ] CRUD operations for garages/vehicles

### Phase 3: Advanced Features (📋 Planned)
- [ ] Build sheet management
- [ ] Photo upload system
- [ ] Parts search integration
- [ ] Cost tracking and analytics
- [ ] Mobile app (React Native)

### Phase 4: Production (🎯 Future)
- [ ] Performance optimization
- [ ] Security hardening
- [ ] Monitoring and logging
- [ ] CI/CD pipeline
- [ ] Production deployment

## 🤝 Contributing

We welcome contributions! Please see our contributing guidelines and code of conduct.

### Development Setup
1. Clone the repository
2. Set up frontend: `cd ui && npm install`
3. Set up backend: `cd backend && mvn install`
4. Run tests: `npm test` (frontend), `mvn test` (backend)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Frontend**: Modern React development with TypeScript
- **Backend**: Enterprise Java development with Spring Boot
- **Design**: Modern UI/UX with accessibility focus
- **Testing**: Comprehensive E2E and unit testing

## 🙏 Acknowledgments

- **Design Inspiration**: Modern automotive management applications
- **UI Components**: Radix UI for accessible primitives
- **Icons**: Lucide React for consistent iconography
- **Testing**: Playwright for reliable E2E testing

---

**Built with ❤️ for automotive enthusiasts**

*Virtual Garage - Organize your builds, track your progress, achieve your automotive dreams.*