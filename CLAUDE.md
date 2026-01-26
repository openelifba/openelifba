# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Elifba is a Spring Boot application for vocabulary learning and spaced repetition, built with Kotlin. The application follows Domain-Driven Design (DDD) principles with a clear separation between domain logic, application use cases, and infrastructure.

## Technology Stack

- **Language**: Kotlin 1.9.23 (JVM 21)
- **Framework**: Spring Boot 3.2.5
- **Database**: PostgreSQL 17
- **Query Builder**: jOOQ 3.18.14 (generated from database schema)
- **Migrations**: Flyway
- **Testing**: JUnit 5, TestContainers, MockK, Database Rider
- **Code Quality**: Detekt, ktlint
- **Build Tool**: Gradle 8.x with Kotlin DSL

## Architecture

The codebase follows **Hexagonal Architecture** (Ports & Adapters) with these layers:

### Domain Layer (`domain/`)
Contains pure business logic with no framework dependencies:
- **Entities**: `Category`, `Exercise`, `Memory` - core domain models
- **Value Objects**: `CategoryId`, `UserId`, `CategoryStatus`
- **Domain Services**: `CategoryStatusService` - business logic that doesn't belong to a single entity
- **Ports**: Interfaces defining contracts (`CategoryFetcher`, `ExerciseFetcher`, `MemoryStatsFetcher`, etc.)

### Application Layer (`application/`)
Use cases that orchestrate domain logic:
- `GetCategoriesUseCase` - enriches categories with statistics
- `GetDueExercisesUseCase` - retrieves exercises due for review
- `UpdateMemoryUseCase` - updates user's memory for exercises

Use cases are annotated with `@Component` and depend only on port interfaces, not concrete implementations.

### Infrastructure Layer (`infra/`)
Adapters implementing domain ports:
- **Controllers** (`infra/controller/`): REST endpoints exposing use cases
- **Repositories** (`infra/repository/`): jOOQ-based implementations of fetcher/upserter ports
- **DTOs** (`infra/controller/dto/`): Data transfer objects for API layer
- **Config** (`infra/config/`): Spring configuration, domain bean registration

## Common Development Commands

### Database Setup
```bash
# Start PostgreSQL via Docker
docker-compose up -d

# Check database container status
docker ps

# Run Flyway migrations manually
./gradlew flywayMigrate

# Generate jOOQ classes after schema changes
./gradlew generateJooq
```

### Build & Run
```bash
# Build project (includes flywayMigrate, generateJooq)
./gradlew build

# Run application
./gradlew bootRun

# Build without tests
./gradlew build -x test
```

### Testing
```bash
# Run all tests (requires Docker for TestContainers)
./gradlew test

# Run specific test class
./gradlew test --tests "com.wordiam.openelifba.application.GetCategoriesUseCaseTest"

# Run tests with detailed output
./gradlew test --info
```

### Code Quality
```bash
# Run ktlint checks
./gradlew ktlintCheck

# Auto-fix ktlint issues
./gradlew ktlintFormat

# Run detekt analysis
./gradlew detekt

# Run all quality checks
./gradlew ktlintCheck detekt
```

## jOOQ Code Generation

jOOQ classes are generated from the PostgreSQL schema into:
- **Build directory**: `build/generated/source/jooq/main/com/wordiam/openelifba/jooq/generated/`
- **Package**: `com.wordiam.openelifba.jooq.generated`

The generation process:
1. `flywayMigrate` runs first to create/update the schema
2. `generateJooq` generates type-safe query classes from the schema
3. Generated classes include: Records, POJOs, Tables with fluent setters

**Important**: After creating new Flyway migrations, run `./gradlew generateJooq` to update jOOQ classes before writing repository code.

## Database Migrations

Flyway migrations are in `src/main/resources/db/migration/`:
- Naming: `V{version}__{description}.sql` (e.g., `V001__create_category_table.sql`)
- Migrations run automatically on application startup
- Current schema includes: `category`, `exercise`, `memory` tables

Database credentials (dev environment):
- URL: `jdbc:postgresql://localhost:5432/openelifba`
- User/Password: `openelifba/openelifba`

Override via environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`

## Testing Strategy

### Integration Tests
Use the `@JooqTest` annotation for repository tests:
```kotlin
@JooqTest
class JooqCategoryRepositoryIntegrationTest { ... }
```

This annotation combines:
- `@SpringBootTest` - full Spring context
- `@Testcontainers` - PostgreSQL container
- `@ActiveProfiles("test")` - test profile
- `@WithDataSet` - Database Rider for test data
- `@TestInstance(Lifecycle.PER_CLASS)` - reuse test instance

Test datasets are in `src/test/resources/datasets/` (YAML format for Database Rider).

### Unit Tests
Use MockK for mocking dependencies:
```kotlin
@Test
fun `should return categories with statistics`() {
    every { categoryFetcher.fetchCategories() } returns listOf(...)
    // ...
}
```

## Domain Patterns

### Ports (Interfaces)
Domain defines contracts as interfaces:
- **Fetchers**: Read operations (e.g., `CategoryFetcher.fetchCategories()`)
- **Upserters**: Write operations (e.g., `ExerciseMemoryUpserter.upsert()`)
- **Finders**: Optional reads (e.g., `ExerciseMemoryFinder.find()` returns nullable)

Infrastructure provides implementations (e.g., `JooqCategoryRepository` implements `CategoryFetcher`).

### Value Objects
Use inline value classes for type safety:
```kotlin
@JvmInline
value class CategoryId(val value: UUID)
```

### Domain Services
Business logic spanning multiple entities goes in domain services registered as Spring beans via `DomainBeanConfiguration`.

## Key Implementation Notes

1. **Controllers are thin**: Delegate to use cases, handle only HTTP concerns
2. **Use cases orchestrate**: Coordinate multiple domain services and ports
3. **Repositories use jOOQ DSL**: Type-safe queries, avoid raw SQL
4. **DTOs at boundaries**: Convert domain models to DTOs in controllers
5. **Immutability**: Prefer data classes and `copy()` for updates
6. **Null safety**: Use Kotlin's nullable types; jOOQ queries may return null

## Configuration

Application config is in `src/main/resources/application.yml`:
- Datasource connection
- jOOQ SQL dialect (POSTGRES)
- Flyway settings
- Actuator endpoints (health, info)

Test configuration uses TestContainers for PostgreSQL, configured in `TestcontainersConfiguration`.
