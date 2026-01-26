# Contributing to OpenElifba

First off, thank you for considering contributing to OpenElifba! 🎉

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Code Style](#code-style)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## Getting Started

### Prerequisites

- **Java 21** or later
- **Docker** (for PostgreSQL and TestContainers)
- **Git**

### Development Setup

1. **Fork the repository** on GitHub

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/openelifba.git
   cd openelifba
   ```

3. **Start the database**
   ```bash
   docker-compose up -d
   ```

4. **Run migrations and generate jOOQ classes**
   ```bash
   ./gradlew flywayMigrate generateJooq
   ```

5. **Verify the setup**
   ```bash
   ./gradlew build
   ```

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce**
- **Describe the behavior you observed**
- **Explain which behavior you expected**
- **Include relevant logs or screenshots**

### Suggesting Features

Feature suggestions are welcome! Please create an issue with:

- **A clear and descriptive title**
- **Detailed explanation of the feature**
- **Any relevant examples or mockups**
- **Explanation of why this would be useful**

### Contributing Code

1. **Check existing issues** or create a new one to discuss your idea
2. **Fork the repository** and create a branch from `master`
3. **Make your changes** following our code style
4. **Write or update tests** as needed
5. **Run the test suite** to ensure everything passes
6. **Submit a pull request**

## Code Style

We use automated tools to maintain consistent code style:

### ktlint

Kotlin code formatting:

```bash
# Check formatting
./gradlew ktlintCheck

# Auto-fix formatting issues
./gradlew ktlintFormat
```

### Detekt

Static code analysis:

```bash
./gradlew detekt
```

### Style Guidelines

- Use **Kotlin coding conventions**
- Prefer **data classes** for DTOs and value objects
- Use **descriptive names** for variables, functions, and classes
- Keep **functions small and focused**
- Write **meaningful commit messages**
- Add **KDoc comments** for public APIs

### Architecture Guidelines

OpenElifba follows **Hexagonal Architecture**:

- **Domain layer** (`domain/`): Pure business logic, no framework dependencies
- **Application layer** (`application/`): Use cases orchestrating domain logic
- **Infrastructure layer** (`infra/`): Controllers, repositories, external integrations

Key principles:
- Domain entities should not depend on infrastructure
- Use **ports (interfaces)** to define contracts
- Implement ports as **adapters** in the infrastructure layer
- Use cases should only depend on port interfaces

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.wordiam.openelifba.application.GetCategoriesUseCaseTest"

# Run with detailed output
./gradlew test --info
```

### Test Types

- **Unit Tests**: Test individual classes in isolation using MockK
- **Integration Tests**: Test with real database using TestContainers

### Writing Tests

- Follow the **Arrange-Act-Assert** pattern
- Use **descriptive test names** (backtick syntax for readability)
- Test both **happy paths and edge cases**
- Use the `@JooqTest` annotation for repository tests

Example:

```kotlin
@Test
fun `should return categories with statistics when all data is available`() {
    // Arrange
    val category = Category(id = testCategoryId, name = "Test", statistic = null)
    every { categoryFetcher.fetchCategories() } returns listOf(category)
    
    // Act
    val result = useCase.execute(testUserId)
    
    // Assert
    assertEquals(1, result.size)
    assertEquals("Test", result[0].name)
}
```

## Pull Request Process

1. **Update documentation** if you're changing functionality
2. **Add tests** for new features or bug fixes
3. **Run the full test suite** (`./gradlew build`)
4. **Run code quality checks** (`./gradlew ktlintCheck detekt`)
5. **Update the CHANGELOG** if applicable
6. **Request review** from maintainers

### PR Title Format

Use clear, descriptive titles:
- `feat: Add user authentication`
- `fix: Correct interval calculation in Memory`
- `docs: Update README with API examples`
- `test: Add tests for CategoryStatusService`
- `refactor: Extract common repository logic`

### PR Checklist

- [ ] Tests pass locally
- [ ] Code follows project style
- [ ] Documentation updated (if needed)
- [ ] Commit messages are clear and descriptive
- [ ] PR description explains the changes and motivation

## Questions?

Feel free to open an issue if you have questions or need help getting started!

Thank you for contributing! 🙌
