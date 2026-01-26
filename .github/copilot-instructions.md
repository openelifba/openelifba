# Copilot Instructions for Elifba Project

## Project Overview
This is a Spring Boot application called "Elifba" built with Kotlin, using jOOQ for database access and PostgreSQL as the database.

## Technology Stack
- **Language**: Kotlin (target JVM 21)
- **Framework**: Spring Boot 3.2.5
- **Database**: PostgreSQL
- **ORM/Query Builder**: jOOQ 3.18.14
- **Migration**: Flyway
- **Testing**: JUnit 5, TestContainers, MockK
- **Build Tool**: Gradle with Kotlin DSL

## Project Structure
- `src/main/kotlin/com/wordiam/elifba/` - Main application code
- `src/main/resources/db/migration/` - Flyway migration scripts
- `src/test/kotlin/` - Test code
- `build/generated/source/jooq/main/` - Generated jOOQ classes

## Code Conventions
- Use Kotlin coding conventions
- Prefer data classes for DTOs
- Use Spring Boot annotations (@Service, @Repository, @RestController)
- Follow RESTful API design principles
- Use camelCase for properties and functions
- Use PascalCase for class names

## Database Guidelines
- Use Flyway for all schema changes
- Name migration files as `V{version}__{description}.sql`
- Use jOOQ generated classes for database access
- Prefer type-safe queries over raw SQL

## Testing Guidelines
- Use TestContainers for integration tests
- Use MockK for mocking in unit tests
- Test classes should end with `Test`
- Integration test classes should end with `IntegrationTest`
- Use `@SpringBootTest` for integration tests
- Use `@TestMethodOrder(OrderAnnotation::class)` when test order matters

## Common Patterns
- Controllers should be thin and delegate to services
- Services contain business logic
- Repositories handle data access using jOOQ
- Use sealed classes for representing different states
- Prefer extension functions for utility methods
- Use nullable types appropriately

## Dependencies Notes
- jOOQ code generation happens before compilation
- TestContainers requires Docker to be running for tests
- Database credentials are configured for development environment
- Flyway migrations run automatically on application startup

## Specific Instructions
- When writing database queries, use jOOQ DSL instead of raw SQL
- Always handle nullable return types from jOOQ queries
- Use Spring's dependency injection instead of manual object creation
- Write comprehensive tests for service layer methods
- Use appropriate HTTP status