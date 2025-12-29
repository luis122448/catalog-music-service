# Music Platform Microservices Guidelines

This document outlines the mandatory technical standards and best practices for all microservices in the Music Platform project.

## 1. Standard Tech Stack
To ensure compatibility and maintainability across the platform, all microservices must use the following versions:
- **Java**: 21 (LTS)
- **Spring Boot**: 4.0.1
- **Spring Cloud**: 2025.1.0 (Enterprise/Microservices support)
- **Build Tool**: Maven 3.9+

## 2. Common Dependencies
The following libraries are standardized across all services:
- **Persistence**: Spring Data JPA with PostgreSQL.
- **Security**: Spring Security 7+ with JWT.
- **JWT Handling**: `io.jsonwebtoken:jjwt` version `0.13.0`.
- **Documentation**: `springdoc-openapi` version `3.0.0` (Swagger UI).
- **Utilities**: Lombok (latest), Caffeine (for caching).
- **Monitoring**: Spring Boot Actuator.

## 3. Architecture: Layered Pattern
All microservices follow a consistent layered architecture to ensure separation of concerns.

### 3.1. Package Structure
The following package hierarchy is mandatory for all services:
- `pe.bbg.music.[service].config`: Global configurations (Security, JPA, OpenAPI, Data Initializers).
- `pe.bbg.music.[service].controller`: REST controllers handling HTTP entry points.
- `pe.bbg.music.[service].service`: Business logic interfaces and implementations.
- `pe.bbg.music.[service].repository`: Spring Data JPA interfaces for database access.
- `pe.bbg.music.[service].entity`: JPA entities (ending in `Entity`). Enums used as entity fields must be in `pe.bbg.music.[service].entity.enums`.
- `pe.bbg.music.[service].dto`: Data Transfer Objects for API requests and responses. Enums used in DTOs must be in `pe.bbg.music.[service].dto.enums`.
- `pe.bbg.music.[service].exception`: Global exception handlers and custom exception classes.

### 3.2. Layer Responsibilities
- **Controller Layer**: Handles HTTP requests, input validation, and maps DTOs.
- **Service Layer**: Contains business logic and orchestrates calls between repositories and other services.
- **Repository Layer**: Interfaces with the database using Spring Data JPA.
- **Entity Layer**: Represents the database schema.
- **DTO Layer**: Data Transfer Objects for API requests and responses.

## 2. Naming Conventions
To maintain clarity across the codebase, classes must include their role in their name:
- **Entities**: Must end in `Entity` (e.g., `UserEntity`).
- **Enums**: Must end in `Enum` (e.g., `UserRoleEnum`).
- **Repositories**: Must end in `Repository` (e.g., `UserRepository`).
- **Services**: Must end in `Service` (e.g., `AuthService`).
- **Controllers**: Must end in `Controller` (e.g., `AuthController`).
- **DTOs**: Must end in `Request` or `Response` (e.g., `RegisterRequest`, `UserResponse`).

## 3. Audit Fields
All main entities must implement auditing to track record lifecycle. The standard field names are:
- `createdBy`: (String) The user who created the record.
- `createdAt`: (LocalDateTime) The timestamp of creation.
- `updatedBy`: (String) The last user who modified the record.
- `updatedAt`: (LocalDateTime) The timestamp of the last modification.

Use Spring Data JPA `@CreatedBy`, `@CreatedDate`, `@LastModifiedBy`, and `@LastModifiedDate` annotations.

## 4. API Response Standard (`ApiResponse<T>`)
All endpoints must return a consistent JSON structure. This class uses Jackson `@JsonInclude(JsonInclude.Include.NON_NULL)` to omit null fields from the final response.

### 4.1. Response Attributes
- `status`: (`ResponseStatusEnum`) Categorizes the result of the operation.
- `message`: (`String`) User-friendly description of the result (in English).
- `data`: (`T`) The payload of the response (can be an object, a list, or null).
- `logUser`: (`String`) The username or "ANONYMOUS" associated with the action.
- `logMessage`: (`String`) Technical detail or exception trace for debugging (internal use).
- `logDate`: (`LocalDateTime`) ISO-8601 timestamp of when the response was generated.

### 4.2. Status Enum (`ResponseStatusEnum`)
- `SUCCESS`: Operation completed successfully.
- `ERROR`: A failure occurred (validation, security, or internal server error).
- `WARNING`: Operation completed but with caveats or minor issues.
- `INFO`: Neutral informational message.

### 4.3. Standard Factory Methods
Developers should use these static methods instead of the builder or constructors to ensure consistency:

- `ApiResponse.success(T data, String message)`: Standard success response with data.
- `ApiResponse.error(String message, String logMessage, String logUser)`: Error response with technical context.
- `ApiResponse.warning(String message)`: Simple warning response.

### 4.4. Example JSON Structure
```json
{
  "status": "SUCCESS",
  "message": "User details retrieved successfully",
  "data": { "id": "uuid", "username": "admin" },
  "logDate": "2025-12-28T20:00:00"
}
```

## 5. Error Handling
- Use `@RestControllerAdvice` for global exception handling.
- Capture specific exceptions (e.g., `BadCredentialsException`) and map them to appropriate HTTP status codes while maintaining the `ApiResponse` body.
- Always log the technical error in `logMessage` for internal audit.

## 6. Database & Entity Naming
- **Tables**: Use `snake_case` with a `tbl_` prefix (e.g., `tbl_user`).
- **Columns**: Use `snake_case`.
- **Primary Keys**: Mandatory use of `UUID` for all main entities to ensure global uniqueness and prevent ID enumeration attacks.

## 7. Documentation (OpenAPI/Swagger)
- Every microservice must include `springdoc-openapi`.
- Configuration class `OpenApiConfig.java` must define:
    - API Title and Version.
    - Security Scheme (JWT Bearer Auth).
- Use `@Operation` and `@Tag` annotations in Controllers to document endpoint functionality.
- Swagger UI must be accessible at `/swagger-ui/index.html`.

## 8. Security & Token Validation
- **Stateless Authentication**: All services must use JWT (JSON Web Tokens) for authentication and session management.
- **Route Protection**: Token validation and signature verification are **mandatory only for private or administrator routes**.
- **Public Routes**: Endpoints intended for public access (e.g., public catalogs, health checks, login) do not require token validation.
- **Signature Validation**: For protected routes, non-authentication microservices (Resource Servers) must validate the incoming token's signature using the shared secret.
- **Shared Secret (Development)**: For local development, the following secret is used: `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`.
- **Production Environment**: In production, the `jwt.secret` MUST be injected via environment variables.

## 9. Language Policy
- **Code**: All variables, classes, and methods must be in English.
- **Comments**: All code comments must be in English.
- **Messages**: All user-facing and log messages must be in English.
```
