# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot restaurant management application called "Mi Leña" with a layered architecture following MVC pattern. The application manages food items, customers, and additional services for a restaurant.

## Development Commands

### Build and Run
```bash
# Navigate to the Spring Boot project directory
cd restaurante

# Run the application in development mode
./mvnw spring-boot:run

# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw clean package

# Clean build artifacts
./mvnw clean
```

### Database Management
- H2 console available at: http://localhost:8080/h2-console
- Default credentials: username=`sa`, password=`password`
- JDBC URL: `jdbc:h2:mem:testdb`

## Architecture Overview

### Project Structure
- **Main application**: `com.proyecto.restaurante.RestauranteApplication`
- **Models**: Entity classes with JPA annotations and validation
- **Repositories**: Spring Data JPA repositories with custom query methods
- **Services**: Business logic layer with interface/implementation pattern
- **Controllers**: MVC controllers handling web requests and admin operations
- **Templates**: Thymeleaf templates in `src/main/resources/templates/`
- **Static assets**: CSS, JS, and images in `src/main/resources/static/`

### Core Entities
- **Comida**: Food items with slug-based URLs, pricing, categories, and activation status
- **Cliente**: Customer management with authentication
- **Adicional**: Additional services/items linked to food items

### Key Architectural Patterns
- **Service Layer Pattern**: Interfaces (I*Service) with concrete implementations (*ServiceImpl)
- **Repository Pattern**: Spring Data JPA repositories with custom queries
- **MVC Pattern**: Controllers handle HTTP requests, return Thymeleaf views
- **Validation**: Jakarta Bean Validation annotations on entities
- **Transactional**: Service methods properly annotated for database transactions

### Database Configuration
- **Development**: H2 in-memory database with auto-create schema
- **Production**: MySQL configuration available (commented in application.properties)
- **JPA Strategy**: `create-drop` for development, `validate` recommended for production

### URL Structure
- Public routes: `/`, `/menu`, `/producto/{slug}`
- Admin routes: `/comidas/*`, `/clientes/*`, `/adicionales/*`
- Authentication routes: `/login`, `/register`

### Frontend Technologies
- **Thymeleaf**: Server-side template engine
- **Bootstrap**: CSS framework for responsive design
- **Custom CSS/JS**: Located in static resources

### Configuration Notes
- Server runs on port 8080
- File upload limit: 10MB
- SQL logging enabled for development
- Thymeleaf caching disabled for development

## Important Development Practices

### Entity Management
- All entities use Long IDs with auto-generation
- Slug fields for SEO-friendly URLs (unique, validated)
- Active/inactive status pattern for soft deletion
- Proper JPA relationships with lazy loading

### Service Layer
- Always use service interfaces, not direct repository access
- Transactional boundaries properly defined
- Custom business logic methods (e.g., `generarSlug()`, `getPrecioFormateado()`)
- Validation at both entity and service levels

### Controller Patterns
- Admin controllers separate from public controllers
- Consistent error handling with RedirectAttributes
- Form validation with BindingResult
- Flash messages for user feedback

### Database Migrations
- Schema managed by JPA DDL auto-generation in development
- For production, use `spring.jpa.hibernate.ddl-auto=validate`
- Consider Flyway or Liquibase for production migrations