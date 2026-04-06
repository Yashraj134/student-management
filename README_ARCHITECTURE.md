# Project Architecture and Flow

## Base Package

`com.example.student_management`

## High-level Architecture

The project follows layered MVC with clean separation of concerns:

`Controller -> Facade -> Service -> Repository -> Database`

Supporting layers:

- `dto` for API contracts
- `mapper` for DTO <-> Entity conversion
- `exception` for centralized error handling
- `aop` for cross-cutting concerns (logging, validation, audit, performance)
- `designpatterns` for practical OO patterns (factory, strategy, observer, facade)

## Package Significance

### `controller`
- Exposes REST endpoints
- Handles request params/body validation
- Delegates business execution to facade
- Keeps controller methods thin

### `designpatterns.facade`
- Provides use-case oriented APIs for controller
- Orchestrates multiple services where needed
- Single entry point for student module operations

### `service`
- Contains business contracts (`StudentService`, `DocumentService`, `AuditService`)
- Encapsulates transactional business logic
- Applies SOLID via interfaces and DI

### `service.impl`
- Concrete business implementations
- Performs create/update/delete orchestration
- Uses strategy/factory for admission handling

### `repository`
- Extends Spring Data `JpaRepository`
- Contains JPQL queries and pagination logic
- Responsible for persistence interaction only

### `entity`
- JPA models mapped to PostgreSQL tables
- Defines relationships (`@OneToOne`, `@OneToMany`, etc.)
- Represents domain persistence state

### `dto`
- Input DTOs (`*Request`) and output DTOs (`*Response`)
- Prevents exposing entities directly to API
- Defines stable external API contract

### `mapper`
- Converts request DTOs to entities
- Converts entities/query projections to response DTOs
- Keeps conversion logic centralized and reusable

### `exception`
- Custom domain exceptions
- `@ControllerAdvice` to return consistent error responses

### `aop`
- Logging method entry/exit
- Performance timing around service execution
- Duplicate validation before save/update
- Audit hooks for action tracing

### `designpatterns.factory`
- Creates admission entities depending on admission pattern
- Helps open/closed design for future admission types

### `designpatterns.strategy`
- Applies admission-specific validation behavior
- Eliminates if/else heavy validation code

### `designpatterns.observer`
- Notifies subscribers on student creation events
- Useful for future notifications/integration hooks

## Request Flow (Example: Create Student)

1. `StudentController` accepts `POST /students` with `StudentUpsertRequest`
2. `StudentManagementFacadeImpl` orchestrates the use case
3. AOP duplicate validation runs before save
4. `StudentServiceImpl` validates strategy and creates entities
5. `StudentRepository` saves parent + related entities via cascade
6. Mapper returns `StudentProfileResponse`
7. Audit/observer hooks log and notify post-create

## Why This Structure Matters

- **Maintainability**: Changes stay localized per layer.
- **Testability**: Service and repository can be tested independently.
- **Scalability**: New modules can follow same pattern.
- **Readability**: Clear role per package and class.
- **Extensibility**: Design patterns reduce coupling and support future features.

## Current Module Coverage

- Student CRUD
- Search and filter with pagination
- Dashboard stats and year-wise stats
- Student summary view
- Bulk create support
- Centralized exception and AOP support

