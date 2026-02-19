# J01: springapi — Production-Grade REST API with Spring Boot

**Catalog ID:** J01 | **Size:** M | **Language:** Java 21 / Spring Boot 3.3
**Repo name:** `springapi`
**One-liner:** A production-grade REST API with Spring Boot 3.3 — JPA entities with Flyway migrations, Spring Security with JWT auth, role-based access control, MapStruct DTOs, OpenAPI docs, and comprehensive testing with TestContainers.

---

## Why This Stands Out

- **Java 21 modern features** — records for DTOs, pattern matching, virtual threads enabled
- **Spring Boot 3.3** — latest version with Spring Web, Data JPA, Security, and Validation
- **JWT auth with refresh tokens** — access token + refresh token rotation, proper security flow
- **Role-based access control** — ADMIN and USER roles with method-level security
- **Flyway migrations** — versioned database schema evolution, not Hibernate auto-DDL
- **MapStruct** — compile-time DTO mapping, no reflection-based mapping overhead
- **OpenAPI 3 documentation** — springdoc-openapi auto-generates spec from annotated controllers
- **RFC 7807 Problem Details** — Spring 6's native support for standard error responses
- **TestContainers** — real PostgreSQL in integration tests, no H2 compromises
- **Multi-model depth** — Users, Posts, Comments, Tags with proper JPA relationships

---

## Architecture

```
springapi/
├── src/
│   ├── main/
│   │   ├── java/com/devaloi/springapi/
│   │   │   ├── SpringapiApplication.java         # Main class with @SpringBootApplication
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java            # Spring Security filter chain, JWT config
│   │   │   │   ├── OpenApiConfig.java             # springdoc-openapi customization
│   │   │   │   ├── JacksonConfig.java             # ObjectMapper settings (dates, naming)
│   │   │   │   └── WebConfig.java                 # CORS, virtual threads config
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java          # JWT generation, validation, refresh
│   │   │   │   ├── JwtAuthenticationFilter.java   # OncePerRequestFilter for JWT extraction
│   │   │   │   ├── UserDetailsServiceImpl.java    # Load user from DB for Spring Security
│   │   │   │   └── SecurityUser.java              # UserDetails implementation
│   │   │   ├── entity/
│   │   │   │   ├── User.java                      # @Entity: id, email, password, name, role, timestamps
│   │   │   │   ├── Post.java                      # @Entity: id, title, content, author (User), tags, timestamps
│   │   │   │   ├── Comment.java                   # @Entity: id, content, author (User), post (Post), timestamps
│   │   │   │   ├── Tag.java                       # @Entity: id, name (unique), posts (ManyToMany)
│   │   │   │   ├── Role.java                      # Enum: ADMIN, USER
│   │   │   │   └── BaseEntity.java                # @MappedSuperclass: id, createdAt, updatedAt
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java            # JpaRepository + findByEmail
│   │   │   │   ├── PostRepository.java            # JpaRepository + custom queries (search, by author)
│   │   │   │   ├── CommentRepository.java         # JpaRepository + findByPostId
│   │   │   │   └── TagRepository.java             # JpaRepository + findByName
│   │   │   ├── dto/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginRequest.java          # record(String email, String password)
│   │   │   │   │   ├── RegisterRequest.java       # record(String email, String password, String name)
│   │   │   │   │   ├── TokenResponse.java         # record(String accessToken, String refreshToken, long expiresIn)
│   │   │   │   │   └── RefreshRequest.java        # record(String refreshToken)
│   │   │   │   ├── user/
│   │   │   │   │   ├── UserResponse.java          # record(Long id, String email, String name, Role role)
│   │   │   │   │   └── UpdateUserRequest.java     # record(String name)
│   │   │   │   ├── post/
│   │   │   │   │   ├── CreatePostRequest.java     # record(String title, String content, Set<String> tags)
│   │   │   │   │   ├── UpdatePostRequest.java     # record(String title, String content, Set<String> tags)
│   │   │   │   │   └── PostResponse.java          # record(Long id, String title, String content, UserResponse author, ...)
│   │   │   │   └── comment/
│   │   │   │       ├── CreateCommentRequest.java  # record(String content)
│   │   │   │       └── CommentResponse.java       # record(Long id, String content, UserResponse author, ...)
│   │   │   ├── mapper/
│   │   │   │   ├── UserMapper.java                # MapStruct: User ↔ UserResponse
│   │   │   │   ├── PostMapper.java                # MapStruct: Post ↔ PostResponse, CreatePostRequest → Post
│   │   │   │   └── CommentMapper.java             # MapStruct: Comment ↔ CommentResponse
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java               # Register, login, refresh token
│   │   │   │   ├── UserService.java               # Get user, update profile, list users (admin)
│   │   │   │   ├── PostService.java               # CRUD + search + pagination
│   │   │   │   └── CommentService.java            # Create, list by post, delete
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java            # /api/v1/auth/** endpoints
│   │   │   │   ├── UserController.java            # /api/v1/users/** endpoints
│   │   │   │   ├── PostController.java            # /api/v1/posts/** endpoints
│   │   │   │   └── CommentController.java         # /api/v1/posts/{id}/comments/** endpoints
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java    # @RestControllerAdvice with RFC 7807
│   │   │       ├── ResourceNotFoundException.java # 404
│   │   │       ├── DuplicateResourceException.java # 409
│   │   │       └── UnauthorizedException.java     # 401
│   │   └── resources/
│   │       ├── application.yml                    # Default config (datasource, JPA, JWT)
│   │       ├── application-dev.yml                # Dev profile overrides
│   │       ├── application-test.yml               # Test profile overrides
│   │       └── db/migration/
│   │           ├── V1__create_users.sql           # Users table + role enum
│   │           ├── V2__create_posts.sql           # Posts table with author FK
│   │           ├── V3__create_comments.sql        # Comments table with post + author FK
│   │           ├── V4__create_tags.sql            # Tags table + post_tags join table
│   │           └── V5__seed_data.sql              # Development seed data
│   └── test/
│       └── java/com/devaloi/springapi/
│           ├── SpringapiApplicationTests.java     # Context loads test
│           ├── config/
│           │   └── TestContainersConfig.java      # PostgreSQL container setup
│           ├── controller/
│           │   ├── AuthControllerTest.java        # Integration: register, login, refresh
│           │   ├── PostControllerTest.java        # Integration: CRUD, pagination, search
│           │   └── CommentControllerTest.java     # Integration: create, list, delete
│           ├── service/
│           │   ├── AuthServiceTest.java           # Unit: mock repo, test logic
│           │   ├── PostServiceTest.java           # Unit: mock repo, test logic
│           │   └── CommentServiceTest.java        # Unit: mock repo, test logic
│           ├── security/
│           │   └── JwtTokenProviderTest.java      # Unit: generate, validate, expired, malformed
│           └── repository/
│               ├── PostRepositoryTest.java        # @DataJpaTest with TestContainers
│               └── UserRepositoryTest.java        # @DataJpaTest with TestContainers
├── docker-compose.yml                             # PostgreSQL for local development
├── pom.xml                                        # Maven build with profiles
├── .mvn/wrapper/                                  # Maven wrapper
├── mvnw                                           # Maven wrapper script
├── mvnw.cmd
├── .gitignore
├── .editorconfig
├── LICENSE
└── README.md
```

---

## API Reference

### Auth Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/auth/register` | No | Register new user |
| `POST` | `/api/v1/auth/login` | No | Login, receive tokens |
| `POST` | `/api/v1/auth/refresh` | No | Refresh access token |

### User Endpoints

| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| `GET` | `/api/v1/users/me` | Yes | Any | Get current user profile |
| `PUT` | `/api/v1/users/me` | Yes | Any | Update current user profile |
| `GET` | `/api/v1/users` | Yes | ADMIN | List all users (paginated) |
| `GET` | `/api/v1/users/{id}` | Yes | ADMIN | Get user by ID |

### Post Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/v1/posts` | No | List posts (paginated, sortable) |
| `GET` | `/api/v1/posts/{id}` | No | Get post by ID |
| `POST` | `/api/v1/posts` | Yes | Create post |
| `PUT` | `/api/v1/posts/{id}` | Yes | Update post (author only) |
| `DELETE` | `/api/v1/posts/{id}` | Yes | Delete post (author or ADMIN) |
| `GET` | `/api/v1/posts/search?q=` | No | Search posts by title/content |

### Comment Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/v1/posts/{postId}/comments` | No | List comments on post |
| `POST` | `/api/v1/posts/{postId}/comments` | Yes | Add comment to post |
| `DELETE` | `/api/v1/posts/{postId}/comments/{id}` | Yes | Delete comment (author or ADMIN) |

### Pagination Response Format

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 47,
  "totalPages": 3,
  "last": false,
  "first": true,
  "sort": {
    "field": "createdAt",
    "direction": "DESC"
  }
}
```

### Error Response Format (RFC 7807)

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Post with id '42' not found",
  "instance": "/api/v1/posts/42"
}
```

---

## Tech Stack

| Component | Choice |
|-----------|--------|
| Language | Java 21 (records, pattern matching, virtual threads) |
| Framework | Spring Boot 3.3 |
| Web | Spring Web MVC |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA (Hibernate 6) |
| Migrations | Flyway |
| Security | Spring Security 6 + JWT (jjwt library) |
| Mapping | MapStruct 1.6 |
| Validation | Jakarta Validation (Bean Validation 3.0) |
| OpenAPI | springdoc-openapi 2.x |
| Testing | JUnit 5, Mockito, TestContainers, Spring MockMvc |
| Build | Maven 3.9 with profiles (dev, test, prod) |
| Containerization | Docker Compose (PostgreSQL only) |

---

## Phased Build Plan

### Phase 1: Project Scaffold + Database

**1.1 — Spring Boot project initialization**
- Spring Initializr or manual pom.xml: Spring Web, Data JPA, Security, Validation, Flyway, PostgreSQL
- Add dependencies: jjwt, mapstruct, springdoc-openapi, testcontainers
- Maven profiles: dev (H2 fallback), test, prod
- Directory structure per architecture above
- application.yml with datasource, JPA, JWT config placeholders

**1.2 — Docker Compose for PostgreSQL**
- PostgreSQL 16 container
- Persistent volume for data
- Environment variables for credentials
- Health check

**1.3 — Flyway migrations**
- V1: users table (id BIGSERIAL, email, password_hash, name, role, created_at, updated_at)
- V2: posts table (id BIGSERIAL, title, content, author_id FK, created_at, updated_at)
- V3: comments table (id BIGSERIAL, content, author_id FK, post_id FK, created_at, updated_at)
- V4: tags table + post_tags join table (ManyToMany)
- V5: seed data (2 users, 5 posts, 10 comments, 5 tags)
- Verify migrations run on startup

**1.4 — JPA entities**
- BaseEntity: @MappedSuperclass with id, createdAt, updatedAt, @PrePersist/@PreUpdate
- User, Post, Comment, Tag entities with JPA annotations
- Proper relationships: Post.author (ManyToOne), Post.comments (OneToMany), Post.tags (ManyToMany)
- Role enum
- Tests: entity creation, relationship mapping

### Phase 2: Security + Auth

**2.1 — JWT token provider**
- Generate access token (15 min) and refresh token (7 days)
- Validate token, extract claims
- Use jjwt library with HS512
- Configurable secret and expiry via application.yml
- Tests: generate, validate, expired, malformed, wrong secret

**2.2 — Spring Security configuration**
- SecurityFilterChain: stateless session, CSRF disabled, endpoint security rules
- Public endpoints: auth/**, posts GET, health
- Authenticated endpoints: posts POST/PUT/DELETE, comments POST/DELETE, users/me
- Admin endpoints: users list, users/{id}
- JwtAuthenticationFilter: extract token from Authorization header, validate, set auth context
- Tests: security filter chain allows/denies correct endpoints

**2.3 — Auth service + controller**
- Register: validate input, check email uniqueness, hash password (BCrypt), create user, return tokens
- Login: validate credentials, return tokens
- Refresh: validate refresh token, generate new token pair
- DTOs as Java records with validation annotations
- Tests: register flow, login, refresh, duplicate email, wrong password

### Phase 3: CRUD Operations

**3.1 — Repository layer**
- UserRepository: findByEmail, existsByEmail
- PostRepository: custom JPQL for search, findByAuthorId, with Pageable support
- CommentRepository: findByPostId with Pageable
- TagRepository: findByName, findByNameIn
- Tests: @DataJpaTest with TestContainers

**3.2 — MapStruct mappers**
- UserMapper: User → UserResponse
- PostMapper: Post → PostResponse (includes author, tags, comment count)
- PostMapper: CreatePostRequest → Post (handles tag resolution)
- CommentMapper: Comment → CommentResponse
- Compile-time generation, no runtime reflection

**3.3 — Service layer**
- PostService: create (resolve tags), getById, list (paginated/sorted), update (author check), delete (author or admin), search
- CommentService: create, listByPost (paginated), delete (author or admin)
- UserService: getCurrentUser, updateProfile, listAll (admin), getById (admin)
- Tests: Mockito unit tests for each service

**3.4 — Controller layer**
- PostController: @RestController with @RequestMapping("/api/v1/posts")
- CommentController: nested under /api/v1/posts/{postId}/comments
- UserController: /api/v1/users
- Pagination via Pageable parameter, sorting via Sort parameter
- OpenAPI annotations: @Operation, @ApiResponse, @Tag
- Tests: MockMvc integration tests

### Phase 4: Exception Handling + Validation

**4.1 — Global exception handler**
- @RestControllerAdvice with @ExceptionHandler methods
- ResourceNotFoundException → 404 ProblemDetail
- DuplicateResourceException → 409 ProblemDetail
- MethodArgumentNotValidException → 400 ProblemDetail with field errors
- AccessDeniedException → 403 ProblemDetail
- Generic Exception → 500 ProblemDetail (sanitized, no stack trace)
- Tests: each exception type returns correct ProblemDetail

**4.2 — Request validation**
- Jakarta Validation annotations on request DTOs: @NotBlank, @Email, @Size, @NotNull
- Custom validators where needed
- Validation errors formatted as problem details with field-level error details
- Tests: validation passes for valid input, correct errors for each invalid field

### Phase 5: OpenAPI Documentation

**5.1 — springdoc-openapi configuration**
- Auto-scan controllers for OpenAPI spec
- Custom OpenAPI info: title, description, version, contact
- Security scheme definition (Bearer JWT)
- Group APIs by tag (Auth, Users, Posts, Comments)
- Swagger UI at /swagger-ui.html
- OpenAPI JSON at /v3/api-docs

### Phase 6: Testing + Polish

**6.1 — TestContainers setup**
- @TestConfiguration with PostgreSQL container
- Shared container across test classes for speed
- Test application-test.yml pointing to container
- Verify migrations + seed data work in test container

**6.2 — Integration test suite**
- Full auth flow: register → login → access protected endpoint → refresh → access again
- Full post CRUD with pagination and search
- Full comment lifecycle
- Role-based access: USER can't access admin endpoints, ADMIN can delete any post
- Validation error scenarios
- Concurrent access scenarios

**6.3 — README and documentation**
- Badges (CI, Java, Spring Boot, license)
- Prerequisites: Java 21, Maven, Docker
- Quick start: docker compose up, mvn spring-boot:run, curl examples
- Full API reference table
- Authentication flow documentation
- Database schema diagram (text-based)
- OpenAPI/Swagger access instructions
- Configuration reference (environment variables)

---

## Commit Plan

1. `chore: scaffold Spring Boot project with Maven config`
2. `feat: add Docker Compose for PostgreSQL`
3. `feat: add Flyway migrations for users, posts, comments, tags`
4. `feat: add JPA entities with relationships`
5. `feat: add JWT token provider with generation and validation`
6. `feat: add Spring Security config with JWT filter`
7. `feat: add auth service and controller with register/login/refresh`
8. `feat: add repositories with custom queries`
9. `feat: add MapStruct mappers for DTOs`
10. `feat: add post service and controller with CRUD and pagination`
11. `feat: add comment service and controller`
12. `feat: add user service and controller with role-based access`
13. `feat: add global exception handler with RFC 7807`
14. `feat: add request validation with Jakarta Validation`
15. `feat: add springdoc-openapi configuration`
16. `test: add TestContainers integration tests`
17. `test: add unit tests with Mockito for services`
18. `docs: add README with API reference and setup guide`
