# Contributing to Virtual Garage

Thank you for your interest in contributing to Virtual Garage! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Issues
- Use the GitHub Issues tab to report bugs or request features
- Search existing issues before creating new ones
- Provide detailed information including steps to reproduce
- Include screenshots for UI issues

### Development Setup

#### Prerequisites
- Node.js 18+ and npm
- Java 17+ and Maven
- Git

#### Frontend Setup
```bash
cd ui
npm install
npm run dev
```

#### Backend Setup
```bash
cd backend
mvn spring-boot:run
```

### Making Changes

1. **Fork the Repository**
   - Click "Fork" on GitHub to create your copy

2. **Clone Your Fork**
   ```bash
   git clone https://github.com/YOUR-USERNAME/VirtualGarage.git
   cd VirtualGarage
   ```

3. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Make Your Changes**
   - Follow the coding standards below
   - Write tests for new functionality
   - Update documentation as needed

5. **Test Your Changes**
   ```bash
   # Frontend tests
   cd ui
   npm test
   npm run build
   
   # Backend tests (when available)
   cd backend
   mvn test
   ```

6. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Add your descriptive commit message"
   ```

7. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

8. **Create a Pull Request**
   - Go to GitHub and create a PR from your fork
   - Fill out the PR template completely
   - Link any related issues

## 📋 Coding Standards

### Frontend (React/TypeScript)
- Use TypeScript for all new code
- Follow ESLint and Prettier configurations
- Use functional components with hooks
- Write descriptive variable and function names
- Add JSDoc comments for complex functions
- Use semantic HTML elements

### Backend (Java/Spring Boot)
- Follow Java naming conventions
- Use Spring Boot best practices
- Write comprehensive unit tests
- Document API endpoints with OpenAPI/Swagger
- Follow REST API conventions

### Git Commit Messages
Use the conventional commit format:
```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or modifying tests
- `chore`: Build process or auxiliary tool changes

**Examples:**
```
feat(ui): add vehicle photo upload functionality
fix(backend): resolve authentication token expiration
docs: update API documentation
```

## 🧪 Testing Guidelines

### Frontend Testing
- Write E2E tests for critical user flows
- Test responsive design on multiple screen sizes
- Ensure accessibility compliance
- Test with different browsers

### Backend Testing
- Write unit tests for all service methods
- Include integration tests for API endpoints
- Test error handling scenarios
- Validate input/output data

## 📚 Documentation

### Code Documentation
- Add JSDoc/JavaDoc comments for public methods
- Include examples in documentation
- Update README files when adding features
- Document configuration options

### API Documentation
- Use OpenAPI/Swagger for API documentation
- Include request/response examples
- Document error codes and messages
- Keep documentation up to date

## 🎨 Design Guidelines

### UI/UX Standards
- Follow the existing design system
- Ensure consistent spacing and typography
- Use semantic color schemes
- Implement responsive design
- Consider accessibility in all designs

### Component Structure
- Create reusable components
- Use proper prop types
- Implement error boundaries
- Follow atomic design principles

## 🔍 Code Review Process

### For Contributors
- Ensure your PR is focused on a single feature/fix
- Write clear PR descriptions
- Respond to review feedback promptly
- Keep PRs reasonably sized (< 500 lines when possible)

### For Reviewers
- Be constructive and helpful in feedback
- Focus on code quality, not personal preferences
- Test the changes locally when needed
- Approve only when confident in the changes

## 🚀 Deployment and Release

### Development
- All changes go through pull requests
- CI/CD runs tests automatically
- Staging environment for testing

### Release Process
- Use semantic versioning (MAJOR.MINOR.PATCH)
- Create release notes for each version
- Tag releases in git
- Deploy to production after thorough testing

## 📞 Getting Help

### Communication Channels
- GitHub Issues for bug reports and feature requests
- GitHub Discussions for general questions
- Pull Request comments for code-specific discussions

### Resources
- [Frontend Documentation](./ui/README.md)
- [Backend Documentation](./backend/README.md)
- [API Documentation](./docs/api.md) (coming soon)

## 🏆 Recognition

Contributors will be:
- Listed in the project's contributors section
- Credited in release notes for significant contributions
- Invited to participate in project direction discussions

## 📄 License

By contributing to Virtual Garage, you agree that your contributions will be licensed under the same license as the project (MIT License).

---

Thank you for contributing to Virtual Garage! Together we're building something amazing for the automotive community. 🚗✨