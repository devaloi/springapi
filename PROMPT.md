# Build springapi — Production-Grade REST API with Spring Boot

You are building a **portfolio project** for a Senior AI Engineer's public GitHub. It must be impressive, clean, and production-grade. Read these docs before writing any code:

1. **`J01-spring-boot-api.md`** — Complete project spec: architecture, phases, API reference, commit plan. This is your primary blueprint. Follow it phase by phase.
2. **`github-portfolio.md`** — Portfolio goals and Definition of Done (Level 1 + Level 2). Understand the quality bar.
3. **`github-portfolio-checklist.md`** — Pre-publish checklist. Every item must pass before you're done.

---

## Instructions

### Read first, build second
Read all three docs completely before writing a single line of code. Understand the Spring Boot layered architecture, the JWT security flow, the JPA entity relationships, and the quality expectations.

### Follow the phases in order
The project spec has 6 phases. Do them in order:
1. **Project Scaffold + Database** — Maven project, Docker Compose, Flyway migrations, JPA entities
2. **Security + Auth** — JWT provider, Spring Security config, auth endpoints
3. **CRUD Operations** — repositories, MapStruct mappers, services, controllers
4. **Exception Handling + Validation** — global handler with RFC 7807, Jakarta Validation
5. **OpenAPI Documentation** — springdoc-openapi, Swagger UI
6. **Testing + Polish** — TestContainers integration tests, Mockito unit tests, README

### Commit frequently
Follow the commit plan in the spec. Use **conventional commits** (`feat:`, `test:`, `refactor:`, `docs:`, `chore:`). Each commit should be a logical unit.

### Quality non-negotiables
- **Java 21 features.** Use records for all DTOs. Use pattern matching where it simplifies code. Enable virtual threads in application.yml (`spring.threads.virtual.enabled=true`).
- **Flyway migrations only.** No Hibernate auto-DDL (`spring.jpa.hibernate.ddl-auto=validate`). All schema changes via versioned SQL files.
- **MapStruct for mapping.** No manual entity-to-DTO conversion. No ModelMapper or reflection-based mapping.
- **Spring Security properly configured.** Stateless sessions, CSRF disabled (API-only), method-level security with @PreAuthorize where appropriate.
- **TestContainers for integration tests.** Real PostgreSQL, not H2. This is the whole point of the Java portfolio piece.
- **RFC 7807 Problem Details.** Use Spring 6's built-in ProblemDetail class. No custom error response formats.
- **Pagination via Spring Data Pageable.** Don't hand-roll pagination. Use Page<T>, Pageable, Sort.
- **Tests at every layer.** Controller tests (MockMvc), service tests (Mockito), repository tests (@DataJpaTest), security tests.
- **Clean Maven build.** `mvn clean verify` must pass with zero warnings. No compiler warnings.
- **No Lombok.** Use Java 21 records instead. This is a deliberate choice to show modern Java.

### What NOT to do
- Don't use Lombok. Java 21 records replace most Lombok use cases. Manual getters/setters for entities are fine.
- Don't use H2 for integration tests. TestContainers with PostgreSQL is required.
- Don't use `spring.jpa.hibernate.ddl-auto=update`. Use Flyway.
- Don't skip the refresh token. The dual-token auth flow is a key differentiator.
- Don't leave default Spring Boot error responses. Every error must go through the global handler.
- Don't use field injection (`@Autowired` on fields). Use constructor injection exclusively.

---

## GitHub Username

The GitHub username is **devaloi**. Java group ID is `com.devaloi`. Artifact ID is `springapi`. Package: `com.devaloi.springapi`. All internal imports must use this package path.

## Start

Read the three docs. Then begin Phase 1 from `J01-spring-boot-api.md`.
