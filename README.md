# springapi

Production-grade Spring Boot REST API for task management with JPA, Spring Security, and OpenAPI documentation.

## Tech Stack

| Component | Choice |
|-----------|--------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.6 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Security | Spring Security (HTTP Basic) |
| Validation | Jakarta Bean Validation |
| Documentation | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, MockMvc, Mockito, AssertJ |
| Build | Maven |

## Prerequisites

- Java 21+
- Maven 3.9+

## Getting Started

```bash
# Clone
git clone https://github.com/devaloi/springapi.git
cd springapi

# Build
mvn clean package

# Run
mvn spring-boot:run
```

The API starts at `http://localhost:8080`.

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/tasks` | No | List tasks (paginated, filterable) |
| `GET` | `/api/tasks/{id}` | No | Get task by ID |
| `POST` | `/api/tasks` | Yes | Create a new task |
| `PUT` | `/api/tasks/{id}` | Yes | Update a task |
| `DELETE` | `/api/tasks/{id}` | Yes | Delete a task |

### Query Parameters (GET /api/tasks)

| Parameter | Type | Description |
|-----------|------|-------------|
| `status` | `TODO`, `IN_PROGRESS`, `DONE` | Filter by status |
| `priority` | `LOW`, `MEDIUM`, `HIGH` | Filter by priority |
| `search` | string | Search in title (case-insensitive) |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 20) |
| `sort` | string | Sort field and direction (e.g., `createdAt,desc`) |

### Examples

```bash
# List all tasks
curl http://localhost:8080/api/tasks

# Filter by status
curl "http://localhost:8080/api/tasks?status=TODO"

# Search by title
curl "http://localhost:8080/api/tasks?search=deploy"

# Create a task (requires auth)
curl -X POST http://localhost:8080/api/tasks \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"title": "Deploy to production", "priority": "HIGH"}'

# Update a task
curl -X PUT http://localhost:8080/api/tasks/1 \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"status": "DONE"}'

# Delete a task
curl -X DELETE http://localhost:8080/api/tasks/1 -u admin:admin
```

## Authentication

The API uses HTTP Basic authentication. Write operations (POST, PUT, DELETE) require credentials. Read operations (GET) are public.

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin` | ADMIN |
| `user` | `user` | USER |

## OpenAPI / Swagger UI

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs (JSON):** http://localhost:8080/api-docs

## Project Structure

```
src/main/java/com/devaloi/springapi/
├── SpringapiApplication.java       # Application entry point
├── config/
│   ├── OpenApiConfig.java          # OpenAPI/Swagger configuration
│   └── SecurityConfig.java         # Spring Security configuration
├── controller/
│   └── TaskController.java         # REST endpoints
├── dto/
│   ├── CreateTaskRequest.java      # Create request with validation
│   ├── UpdateTaskRequest.java      # Partial update request
│   └── TaskResponse.java           # Response DTO
├── entity/
│   ├── Task.java                   # JPA entity
│   ├── TaskPriority.java           # Priority enum
│   └── TaskStatus.java             # Status enum
├── exception/
│   ├── ErrorResponse.java          # Structured error response
│   └── GlobalExceptionHandler.java # @ControllerAdvice error handler
├── repository/
│   └── TaskRepository.java         # JPA repository with custom queries
└── service/
    ├── TaskService.java            # Service interface
    └── TaskServiceImpl.java        # Service implementation
```

## Running Tests

```bash
# Unit + integration tests
mvn test

# Full verification (compile, test, integration-test)
mvn verify
```

## H2 Console

Available at http://localhost:8080/h2-console during development.

| Setting | Value |
|---------|-------|
| JDBC URL | `jdbc:h2:mem:springapi` |
| Username | `sa` |
| Password | *(empty)* |

## License

[MIT](LICENSE)
