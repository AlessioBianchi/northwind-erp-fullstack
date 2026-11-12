# Northwind ERP — Backend Status

> **Purpose of this file:** Living reference document for Claude Code. Read this first in any new conversation — it gives full context on the project without needing to re-scan the codebase. Update the [Changelog](#changelog) section whenever a meaningful change is made.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Configuration](#configuration)
5. [Database Schema](#database-schema)
6. [Architecture & Patterns](#architecture--patterns)
7. [Features & Module Status](#features--module-status)
8. [API Endpoints](#api-endpoints)
9. [Security](#security)
10. [Data Model](#data-model)
11. [Tests](#tests)
12. [Known Gaps & TODOs](#known-gaps--todos)
13. [Changelog](#changelog)

---

## Project Overview

Spring Boot REST backend that exposes the classic Northwind ERP dataset as a JSON API consumed by an Angular frontend running on `localhost:4200`. Personal replica/learning project, not a production system.

- **Root package:** `com.github.alessiobianchi.erp`
- **Main class:** `ErpSpringApplication.java`
- **Base URL pattern:** mix of `/api/**` (REST controllers) and `/**` (MVC controllers — Thymeleaf remnants, largely dead code now that Angular handles the UI)
- **Frontend base URL:** `http://localhost:4200` (CORS allowed origin)

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 4.0.5 |
| Web | Spring Web MVC | (inherited) |
| Persistence | Spring Data JPA + Hibernate | (inherited) |
| Database | PostgreSQL (Docker, `docker-compose.yml`) | 16 @ localhost:5432 |
| JDBC Driver | postgresql | runtime |
| Migrations | Flyway (`spring-boot-starter-flyway` + `flyway-database-postgresql`) | (inherited) |
| Security | Spring Security 6 | (inherited) |
| Validation | Bean Validation (Hibernate Validator via `spring-boot-starter-validation`) | (inherited) |
| Templating | Thymeleaf (+ security extras) | (inherited) — mostly unused |
| PDF Generation | OpenPDF | 1.3.30 |
| PDF Formatting | Apache FOP | 2.9 |
| Build | Maven + spring-boot-maven-plugin | — |
| Dev Tools | spring-boot-devtools | runtime |
| API Docs | springdoc-openapi (`springdoc-openapi-starter-webmvc-ui`) | 3.0.3 (built on Boot 4.0.5; 3.1+ target Boot 4.1) |
| Testing | JUnit 5 + Mockito (via Spring Boot test starters) | (inherited) |
| Integration tests | Testcontainers (`spring-boot-testcontainers`, `testcontainers-postgresql`, `postgres:16`) | 2.0.4 (Boot-managed) |

---

## Project Structure

```
spring-erp/
├── pom.xml
├── docker-compose.yml                          # PostgreSQL 16 container (creds from .env)
├── .env.example                                # Template for the gitignored .env
├── .mvn/wrapper/
│   └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/github/alessiobianchi/erp/
    │   │   ├── ErpSpringApplication.java
    │   │   ├── config/
    │   │   │   ├── AppConfig.java              # Security + CORS configuration
    │   │   │   ├── JpaAuditingConfig.java      # @EnableJpaAuditing
    │   │   │   ├── OpenApiConfig.java          # OpenAPI info, session-cookie scheme, shared error responses
    │   │   │   └── PasswordConfig.java
    │   │   ├── controller/
    │   │   │   ├── CategoriesController.java
    │   │   │   ├── CustomersController.java
    │   │   │   ├── DashboardController.java
    │   │   │   ├── EmployeesController.java
    │   │   │   ├── OrdersController.java
    │   │   │   ├── Paging.java                 # MAX_PAGE_SIZE (100)
    │   │   │   ├── ProductsController.java
    │   │   │   ├── ShippersController.java
    │   │   │   └── SuppliersController.java
    │   │   ├── dao/
    │   │   │   ├── CategoriesDAO.java
    │   │   │   ├── CustomersDAO.java
    │   │   │   ├── EmployeesDAO.java
    │   │   │   ├── OrderDetailsDAO.java
    │   │   │   ├── OrdersDAO.java
    │   │   │   ├── ProductsDAO.java
    │   │   │   ├── ShippersDAO.java
    │   │   │   └── SuppliersDAO.java
    │   │   ├── converter/                      # Static bean ↔ DTO converters
    │   │   │   ├── EmployeeBeanToDtoConverter.java / EmployeeDtoToBeanConverter.java
    │   │   │   ├── OrderBeanToDtoConverter.java / OrderDtoToBeanConverter.java
    │   │   │   └── OrderDetailsBeanToDtoConverter.java / OrderDetailsDtoToBeanConverter.java
    │   │   ├── dto/
    │   │   │   ├── DashboardDTO.java + DashboardDTOBuilder.java
    │   │   │   ├── EmployeeDTO.java + EmployeeDTOBuilder.java
    │   │   │   ├── OrderDTO.java + OrderDTOBuilder.java
    │   │   │   ├── OrderDetailsDTO.java + OrderDetailsDTOBuilder.java
    │   │   │   └── ErrorMessage.java           # Error envelope { status, message, timestamp }
    │   │   ├── exception/
    │   │   │   ├── BadRequestException.java    # → 400
    │   │   │   ├── GlobalExceptionHandler.java # @RestControllerAdvice
    │   │   │   └── ResourceNotFoundException.java # → 404
    │   │   ├── model/
    │   │   │   ├── Auditable.java              # @MappedSuperclass: createdAt / updatedAt
    │   │   │   ├── Category.java + CategoryBuilder.java
    │   │   │   ├── Customer.java + CustomerBuilder.java
    │   │   │   ├── Employee.java + EmployeeBuilder.java
    │   │   │   ├── Order.java + OrderBuilder.java
    │   │   │   ├── OrderDetail.java + OrderDetailBuilder.java
    │   │   │   ├── OrderDetailId.java          # Composite PK class
    │   │   │   ├── Product.java + ProductBuilder.java
    │   │   │   ├── Shipper.java + ShipperBuilder.java
    │   │   │   ├── Supplier.java + SupplierBuilder.java
    │   │   │   └── User.java                   # UserDetails adapter (not an entity)
    │   │   └── service/                        # Interfaces (what controllers depend on)
    │   │       ├── CategoriesService.java
    │   │       ├── CustomersService.java
    │   │       ├── DashboardService.java
    │   │       ├── EmployeesService.java
    │   │       ├── OrdersService.java
    │   │       ├── ProductsService.java
    │   │       ├── ShippersService.java
    │   │       ├── SuppliersService.java
    │   │       ├── UsersDetailsService.java    # Class: implements Spring's UserDetailsService
    │   │       └── impl/                       # @Service implementations
    │   │           └── *ServiceImpl.java       # One per interface above (8)
    │   └── resources/
    │       ├── application.properties
    │       ├── application-prod.properties
    │       └── db/migration/                   # Flyway V1–V5
    └── test/
        └── java/com/github/alessiobianchi/erp/
            ├── ErpSpringApplicationTests.java
            ├── TestcontainersConfiguration.java # @ServiceConnection PostgreSQL container
            ├── controller/                     # MockMvc tests (validation, security)
            ├── integration/                    # @SpringBootTest + real DB: Auth, Orders, Employees
            ├── model/                          # UserTest
            └── service/
                ├── UsersDetailsServiceTest.java
                └── impl/                       # One test class per *ServiceImpl (8)
```

---

## Configuration

File: `src/main/resources/application.properties`

```properties
spring.application.name=ERPSpring

spring.config.import=optional:file:.env[.properties]

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.csrf.enabled=true
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.operations-sorter=method
springdoc.paths-to-match=/api/**
springdoc.default-produces-media-type=application/json
```

- **Secrets:** no credentials in the repo. `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` come from OS environment variables or, locally, from the gitignored `spring-erp/.env` (template `.env.example`). Real env vars override `.env`. No defaults → startup fails if missing.
- `.env` is resolved relative to the working directory, so run the app from `spring-erp/` (`./mvnw`, or IntelliJ with working dir = module).
- `docker-compose.yml` reads the same `.env` (`${DB_USERNAME:?…}` fails fast if unset).
- **`prod` profile** (`application-prod.properties`, `SPRING_PROFILES_ACTIVE=prod`): `show-sql=false`; springdoc api-docs + Swagger UI disabled (→ 404); credentials via env vars only.
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` (spec at `/v3/api-docs`). Log in via **Authentication → Log in** first (form fields), then run any GET once so the `XSRF-TOKEN` cookie is set; the session cookie is then reused and `csrf.enabled` sends `X-XSRF-TOKEN` on writes.
- No connection pool tuning (uses HikariCP defaults).
- Schema owned by Flyway (default config, runs on startup); Hibernate only validates it.

---

## Database Schema

**Migrations** (`src/main/resources/db/migration/`):

| Version | File | Content |
|---|---|---|
| V1 | `V1__northwind_base_schema.sql` | Northwind PostgreSQL dump (tables + data) |
| V2 | `V2__adapt_schema_to_entities.sql` | smallint → integer, `discontinued` → boolean, customers integer PK (old code → `customer_code`), IDENTITY on PKs |
| V3 | `V3__add_employee_credentials.sql` | `employees.username`/`password` + seed logins (`password123`) |
| V4 | `V4__sync_identity_sequences.sql` | `setval` identity sequences to `MAX(id)` |
| V5 | `V5__add_auditing_columns.sql` | `created_at` / `updated_at` `timestamptz NOT NULL DEFAULT now()` on all 8 tables; existing rows = migration time |

**ID generation:** `GENERATED ALWAYS AS IDENTITY` on categories, employees, orders, products, shippers, suppliers; `SERIAL` on customers. Entities use `GenerationType.IDENTITY`.

**Tables and key relationships:**

```
CATEGORIES ←── PRODUCTS ──→ SUPPLIERS
                   ↑
ORDER_DETAILS (composite PK: order_id + product_id)
                   ↑
ORDERS ──→ CUSTOMERS
ORDERS ──→ EMPLOYEES (self-ref: REPORTS_TO)
ORDERS ──→ SHIPPERS
```

Schema changes go in a new `V{n}__*.sql` file — never edit an applied migration (checksum mismatch).

---

## Architecture & Patterns

### Layered structure
3-tier layered architecture: **Controller → Service → DAO (Repository)**

```
controller/   @Controller / @RestController — HTTP only, no logic
service/      Service interfaces; service/impl/ holds the @Service *ServiceImpl classes (business rules, delete guards, aggregations)
dao/          Spring Data JPA interfaces (CrudRepository + @Query)
model/        JPA entities + Builder companions
dto/          API DTOs + builders (Dashboard, Employee, Order, OrderDetails)
converter/    Static bean ↔ DTO converters, called from services
config/       Security and CORS (AppConfig.java), OpenAPI (OpenApiConfig.java)
```

### Service interfaces
Each service is an interface in `service/` with one `@Service` implementation `service/impl/<Name>ServiceImpl`. Controllers and `@MockitoBean`/`@Mock` in tests depend on the interface; service unit tests (`test/.../service/impl/`) `@InjectMocks` the `Impl`. Names stay plural (`ProductsService`) to match the rest of the code. `UsersDetailsService` is the exception: it already implements Spring's `UserDetailsService`.

### Auditing (createdAt / updatedAt)
All 8 entities extend `model/Auditable` (`@MappedSuperclass`, `@EntityListeners(AuditingEntityListener.class)`): `@CreatedDate createdAt` (`updatable = false`) and `@LastModifiedDate updatedAt`, both `Instant` → `timestamptz`, `@JsonProperty(READ_ONLY)` so clients can't set them. Enabled by `config/JpaAuditingConfig` (kept off the main class so `@WebMvcTest` slices still start). Updates rebuild the entity from the request body, so each service `update` loads the row with `findById` and passes `withCreatedAt(existing.getCreatedAt())` to the builder; otherwise the PUT response would show `createdAt: null`. Builders copy `createdAt` and set it through `Auditable`'s package-private setter. `EmployeeDTO`, `OrderDTO`, `OrderDetailsDTO` expose both fields (read-only) via the bean→DTO converters.

### Builder pattern
Every entity has a hand-written `*Builder` companion (fluent API). Used in service layer for partial updates — the service loads the existing entity, applies changes via the builder, then saves.

### DTOs & converters
Employees, Orders, Order Details and the Dashboard's `lastTenOrders` go through DTOs; Categories, Customers, Products, Suppliers and Shippers still expose entities directly. Conversion happens in the **service** layer via static `converter/*Converter.convert(...)` methods (no Spring beans). Controllers only see DTOs for these modules. Order/OrderDetail DTOs are flat: associations become `xxxId` + `xxxName`, and DTO → bean builds id-only association stubs for JPA.

### Data access
Spring Data JPA with derived query methods (`findAllByOrderBy…`) and custom `@Query` for aggregations (revenue sums, category counts). Pagination uses Spring Data `Page` + `Pageable`; serialization mode: `VIA_DTO` (Spring 6.1+).

### API documentation (springdoc)
Every controller has `@Tag`; every endpoint has `@Operation(summary, description)` + `@ApiResponse` per status code. Error responses reference shared components defined in `OpenApiConfig` (`@ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)` → `ErrorMessage` schema). The list GET and the paginated GET share one path; OpenAPI allows one operation per path + method, so springdoc merges them (response `oneOf[PagedModel, array]`). Put `@Operation` on the plain list method only; `OpenApiConfig.optionalParamsOnMergedGets` marks the `pageNumber`/`pageSize` params as optional in the spec. Descriptions are Markdown: use `\n\n` or `- ` lists (a single `\n` renders as a space). They document behavior a client cannot guess (409 causes, password handling, auto-set fields); global conventions live in the `Info` description.

### Authentication adapter
`User.java` is not a JPA entity — it wraps `Employee` and implements Spring Security's `UserDetails`. `UsersDetailsService` bridges JPA and Spring Security by loading an `Employee` by username and wrapping it in a `User`. `User.getAuthorities()` returns `ROLE_USER`, plus `ROLE_MANAGER` when `reportsTo == null`.

---

## Features & Module Status

### Categories — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD: list all, save (create/update), delete.
- Delete blocked if any product references the category.
- **Status: complete.**

### Customers — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD + pagination.
- Delete blocked if customer has orders.
- **Status: complete.**

### Employees — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD + pagination.
- Self-referencing `reportsTo` relationship.
- Carries `username`/`password` fields — doubles as the auth user model.
- Create/update/delete are manager-only (403 otherwise); every employee can change their own password via `PUT /me/password` (current password checked).
- Password rules in `EmployeesService.validatePassword`: min 8 characters, max 72 UTF-8 bytes (BCrypt limit) → 400.
- Delete blocked if employee has orders.
- **Status: complete.**

### Products — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD + pagination.
- DAO queries by category, by supplier, and by low-stock threshold.
- Delete blocked if product appears in order details.
- **Status: complete.**

### Suppliers — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD + pagination.
- Delete blocked if supplier has products.
- **Status: complete.**

### Shippers — `controller/`, `service/`, `dao/`, `model/`
- Full CRUD (no pagination — small lookup table).
- Delete blocked if shipper has orders.
- **Status: complete.**

### Orders & Order Details — `controller/`, `service/`, `dao/`, `model/`
- Orders: paginated list, save (auto-resolves employee from auth principal), delete (blocked if details exist).
- Order Details: list by order, save (auto-sets unit price from product), delete.
- Composite PK on ORDER_DETAILS handled by `OrderDetailId`.
- **Status: complete.**

### Dashboard — `controller/`, `service/`, `dto/`
- Single `GET /api/v1/dashboard` endpoint returning `DashboardDTO`.
- Aggregates: total/monthly order count and revenue, last 10 orders, orders-by-category map, low-stock products (< 10 units).
- **Status: complete.**

### Reports (PDF) — `OrdersController`, `OrdersService`
- `GET /api/v1/orders/{id}/report` streams a PDF for a given order.
- Generated with OpenPDF; currency formatted in EUR (€).
- **Status: complete.**

### Authentication — `config/`, `service/`, `model/User.java`
- Session-based login/logout via Spring Security form auth.
- `isManager` flag derived from `employee.reportsTo == null`; exposed as `ROLE_MANAGER` and read from the authorities in the login success handler.
- **Status: complete.**

---

## API Endpoints

All resource controllers are under `/api/v1/`. Status codes: `GET`/`PUT` → 200, `POST` → 201 (no `Location`: there is no `GET /{id}`), `DELETE` → 204; `PUT`/`DELETE` on a missing id → 404; `DELETE` blocked by referencing rows → 409 (`ConflictException`); validation/bad input → 400. Errors use the `{ status, message, timestamp }` envelope. `POST` = create, `PUT /{id}` = update, `DELETE /{id}` = delete (ID comes from path, not body). Paginated lists are the same collection path selected by `pageNumber` + `pageSize` query params (`@GetMapping(params = ...)`). Limits: `pageNumber` ≥ 0, `pageSize` 1–100 (`Paging.MAX_PAGE_SIZE`), checked with `@Min`/`@Max` on the `@RequestParam`s; a violation → 400 via `HandlerMethodValidationException` (message e.g. `pageSize: must be at most 100`).

### CategoriesController (`/api/v1/categories`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/categories` | `List<Category>` |
| POST | `/api/v1/categories` | `201` + `Category` |
| PUT | `/api/v1/categories/{categoryId}` | `ResponseEntity<Category>` |
| DELETE | `/api/v1/categories/{categoryId}` | `204`; `404` if missing, `409` if referenced |

### CustomersController (`/api/v1/customers`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/customers` | `List<Customer>` |
| GET | `/api/v1/customers?pageNumber=&pageSize=` | `Page<Customer>` |
| POST | `/api/v1/customers` | `201` + `Customer` |
| PUT | `/api/v1/customers/{customerId}` | `ResponseEntity<Customer>` |
| DELETE | `/api/v1/customers/{customerId}` | `204`; `404` if missing, `409` if referenced |

### EmployeesController (`/api/v1/employees`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/employees` | `List<EmployeeDTO>` |
| GET | `/api/v1/employees?pageNumber=&pageSize=` | `Page<EmployeeDTO>` |
| POST | `/api/v1/employees` | **Manager only.** `201` + `EmployeeDTO` (body: `Employee`); `400` if password missing/invalid |
| PUT | `/api/v1/employees/{employeeId}` | **Manager only.** `ResponseEntity<EmployeeDTO>` (body: `Employee`; blank password keeps the stored hash, otherwise reset without the old one) |
| DELETE | `/api/v1/employees/{employeeId}` | **Manager only.** `204`; `404` if missing, `409` if referenced |
| PUT | `/api/v1/employees/me/password` | Any logged-in employee. Body `ChangePasswordDTO { currentPassword, newPassword }`; `204`; `400` if current password wrong or new one invalid |

### ProductsController (`/api/v1/products`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/products` | `List<Product>` |
| GET | `/api/v1/products?pageNumber=&pageSize=` | `Page<Product>` |
| POST | `/api/v1/products` | `201` + `Product` |
| PUT | `/api/v1/products/{productId}` | `ResponseEntity<Product>` |
| DELETE | `/api/v1/products/{productId}` | `204`; `404` if missing, `409` if referenced |

### SuppliersController (`/api/v1/suppliers`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/suppliers` | `List<Supplier>` |
| GET | `/api/v1/suppliers?pageNumber=&pageSize=` | `Page<Supplier>` |
| POST | `/api/v1/suppliers` | `201` + `Supplier` |
| PUT | `/api/v1/suppliers/{supplierId}` | `ResponseEntity<Supplier>` |
| DELETE | `/api/v1/suppliers/{supplierId}` | `204`; `404` if missing, `409` if referenced |

### ShippersController (`/api/v1/shippers`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/shippers` | `List<Shipper>` |
| POST | `/api/v1/shippers` | `201` + `Shipper` |
| PUT | `/api/v1/shippers/{shipperId}` | `ResponseEntity<Shipper>` |
| DELETE | `/api/v1/shippers/{shipperId}` | `204`; `404` if missing, `409` if referenced |

### OrdersController (`/api/v1/orders`) — `@RestController`
| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/orders` | `List<OrderDTO>` |
| GET | `/api/v1/orders?pageNumber=&pageSize=` | `Page<OrderDTO>` |
| POST | `/api/v1/orders` | `201`; body/response `OrderDTO`; employee resolved from `Principal` |
| PUT | `/api/v1/orders/{orderId}` | Body/response `OrderDTO` |
| DELETE | `/api/v1/orders/{orderId}` | `204`; `404` if missing, `409` if it has details |
| GET | `/api/v1/orders/{orderId}/details` | `List<OrderDetailsDTO>` |
| POST | `/api/v1/orders/{orderId}/details` | `201`; body/response `OrderDetailsDTO`; body `orderId` must match path (400); `404` if order or product missing; unit price auto-set from product |
| DELETE | `/api/v1/orders/{orderId}/details/{productId}` | `204`; `404` if missing |
| GET | `/api/v1/orders/{id}/report` | `application/pdf` byte stream; `404` if order missing |

### DashboardController (`/api/v1/dashboard`) — `@RestController`
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/dashboard` | `ResponseEntity<DashboardDTO>` |

### Auth (Spring Security managed)
| Method | Path | Notes |
|---|---|---|
| POST | `/api/auth/login` | JSON response `{message, username, isManager}` |
| POST | `/api/auth/logout` | Invalidates session, deletes JSESSIONID cookie |

---

## Security

**Mechanism:** Session-based (JSESSIONID cookie) — no JWT.

| Aspect | Detail |
|---|---|
| Auth provider | `DaoAuthenticationProvider` + `UsersDetailsService` |
| Password hashing | `BCryptPasswordEncoder` |
| Login | POST `/api/auth/login` → JSON `{message, username, isManager}` |
| Logout | POST `/api/auth/logout` → invalidates session, deletes cookie |
| `isManager` logic | `employee.reportsTo == null` |
| CSRF | `CookieCsrfTokenRepository` (httpOnly=false); custom `CsrfCookieFilter`; bypassed on `/api/auth/**` |
| CORS | Origin `http://localhost:4200`; credentials allowed; `X-XSRF-TOKEN` header permitted |
| Access control | All endpoints require authentication except `/api/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`. `POST`/`PUT`/`DELETE` on `/api/v1/employees/**` require `ROLE_MANAGER` (403), except `PUT /api/v1/employees/me/password` |
| Roles | `ROLE_USER` for everyone, `ROLE_MANAGER` when `reportsTo == null` (`User.getAuthorities()`) |
| Password rules | Min 8 characters, max 72 UTF-8 bytes (`EmployeesService.validatePassword`) |

---

## Data Model

### Entities

All entities also have `createdAt` / `updatedAt` (from `Auditable`).

| Entity | Table | Key Fields / Notes |
|---|---|---|
| `Category` | CATEGORIES | categoryId, categoryName, description, picture |
| `Customer` | CUSTOMERS | customerId, customerCode, companyName, contactName, address fields |
| `Employee` | EMPLOYEES | employeeId, names, dates, address, photo, notes; self-ref `reportsTo`; `username`/`password` for auth |
| `Product` | PRODUCTS | productId, productName, unitPrice, stock fields; FK→Supplier, FK→Category |
| `Supplier` | SUPPLIERS | supplierId, companyName, contactName, address fields |
| `Shipper` | SHIPPERS | shipperId, companyName, phone |
| `Order` | ORDERS | orderId, dates (order/required/shipped), freight, ship* fields; FK→Customer, Employee, Shipper |
| `OrderDetail` | ORDER_DETAILS | composite PK (`OrderDetailId`); FK→Order, Product; unitPrice, quantity, discount |
| `User` | — | Not a DB entity; wraps `Employee` to implement `UserDetails` |

### DAOs (Repositories)

| DAO | Notable Methods |
|---|---|
| `CategoriesDAO` | `findAllByOrderByCategoryIdDesc()` |
| `CustomersDAO` | findAll…Desc(), findAll…Desc(Pageable) |
| `EmployeesDAO` | `findByUsername(String)`, findAll…Desc(), findAll…Desc(Pageable) |
| `ProductsDAO` | findAll…Desc(Pageable), `findAllByCategory()`, `findAllBySupplier()`, `findByUnitsInStockLessThan(threshold)` |
| `SuppliersDAO` | findAll…Desc(), findAll…Desc(Pageable) |
| `ShippersDAO` | findAll…Desc() |
| `OrdersDAO` | findAll…Desc(Pageable), findAllByCustomer/Shipper/Employee(), `countAllOrders()`, `countLastMonthOrders(date)`, `findFirst10…Desc()` |
| `OrderDetailsDAO` | findAllByOrder/Product(), `calculateTotalRevenue()`, `calculateMonthlyRevenue(date)`, `findOrdersCountByCategory()` |

### Services

| Service | Key Business Logic |
|---|---|
| `CategoriesService` | CRUD; delete blocked if products reference the category |
| `CustomersService` | CRUD + pagination; delete blocked if customer has orders |
| `EmployeesService` | CRUD + pagination; delete blocked if employee has orders; password rules on create/update; `changeOwnPassword(username, current, new)` checks the current password |
| `ProductsService` | CRUD + pagination; delete blocked if product appears in order details |
| `SuppliersService` | CRUD + pagination; delete blocked if supplier has products |
| `ShippersService` | CRUD; delete blocked if shipper has orders |
| `OrdersService` | CRUD + pagination; `save(order, username)` auto-sets employee from auth principal; detail create checks path `orderId` matches body (400 otherwise), that the order exists (404) and auto-sets unitPrice from product; PDF report via OpenPDF (EUR) |
| `DashboardService` | Aggregates: total/monthly orders & revenue, last 10 orders, orders-by-category map, low-stock products |
| `UsersDetailsService` | Implements `UserDetailsService`; loads `Employee` by username, wraps in `User` |

### DTO

**`DashboardDTO`** (built via `DashboardDTOBuilder`):

| Field | Type |
|---|---|
| totalOrdersCount | Long |
| totalRevenue | Double |
| lastMonthOrdersCount | Long |
| lastMonthRevenue | Double |
| lastTenOrders | List\<OrderDTO\> |
| ordersByCategory | Map\<String, Integer\> |
| productStock | List\<Product\> (low-stock items) |

**`EmployeeDTO`**: every `Employee` field; `reportsTo` is a nested `EmployeeDTO`. `password` is `@JsonProperty(WRITE_ONLY)` — accepted in requests, never serialized.

**`OrderDTO`**: `Order` scalar fields + `customerId`/`customerName` (company name), `employeeId`/`employeeName` (first + last), `shipperId`/`shipperName`. Names are ignored on input. `customerId` is `@NotNull` (every order must have a customer).

**`OrderDetailsDTO`**: `orderId`, `productId`, `productName`, `unitPrice`, `quantity`, `discount`.

---

## Tests

- `service/impl/` — 8 unit test classes, one per `*ServiceImpl`; `service/UsersDetailsServiceTest`. 9 classes in total. JUnit 5 + `@ExtendWith(MockitoExtension.class)`, DAOs/`PasswordEncoder` mocked, Arrange/Act/Assert. Every public service method is covered, including the not-found (404), conflict (409) and bad-request (400) paths.
- `controller/OrdersControllerValidationTest` — standalone MockMvc: bean validation, status codes.
- `controller/EmployeesControllerSecurityTest` — `@WebMvcTest` + `AppConfig`, `@WithMockUser`: manager-only writes (403), create without `employeeId` (201), `/me/password` (204 / 401 / 400).
- `model/UserTest` — `ROLE_MANAGER` only when `reportsTo == null`.
- `model/EntityJsonBindingTest` — create bodies without an ID deserialize with a `null` ID (6 entities); `Product` without `discontinued` → `false`.
- `integration/` — `@SpringBootTest` + `@AutoConfigureMockMvc` against a throwaway PostgreSQL from Testcontainers (`TestcontainersConfiguration`, `@ServiceConnection`); Flyway V1–V5 + seed data run on it. Real form login (`AbstractIntegrationTest.login` → `MockHttpSession`), real CSRF (`.with(csrf())`), real services and DB. No `@Transactional` rollback on purpose (requests commit like production); each test creates the rows it changes.
  - `AuthIntegrationTest` (9): login ok / manager flag / wrong password / JSON body → 401, no session → 401, logout invalidates session, write without CSRF → 403.
  - `OrdersIntegrationTest` (10): employee set from the logged-in user, missing customer → 400, update keeps `createdAt`, detail unit price from product, missing order → 404, orderId mismatch → 400, delete with details → 409, PDF report, pagination.
  - `EmployeesIntegrationTest` (9): employee writes → 403 (incl. trying to reset the manager's password), manager create without ID → new employee can log in, password rules, blank password keeps the old one, change own password.
  - `PaginationIntegrationTest` (26): all 5 paginated endpoints reject `pageNumber=-1`, `pageSize=0`, `pageSize=101` with 400 and accept 50 and 100.
  - Checked by breaking the code on purpose: removing the manager rule fails 6 tests; removing the order-exists check fails the 404 test (500).
- `ErpSpringApplicationTests` — context smoke test, also on Testcontainers.
- **Tests need Docker running**, not the dev `erp-postgres` container or `.env` (they never touch the dev DB).
- 164 tests total, all passing, ~20 s (2026-11-12).

---

## Known Gaps & TODOs

- [ ] **No caching** — no `@Cacheable` on read-heavy endpoints.
- [ ] **No Actuator endpoints** — no health check or metrics.
- [ ] **Thymeleaf dead code** — view references remain in some controllers; UI is fully Angular now.
- [ ] **CORS origin hardcoded** — `http://localhost:4200` in `AppConfig.corsConfigurationSource()`; needs a property before a real prod deploy.
- [ ] **Magic number** — low-stock threshold `< 10` is hardcoded in `DashboardService`.
- [ ] **Frontend: no "change my password" form** for `PUT /api/v1/employees/me/password`; 403 on employee writes not shown specifically.
- [ ] **Other sessions survive a password change** — would need a `SessionRegistry`.
- [ ] **Date pattern typo** in `Employee.java` — `"yyy-MM-dd"` (three y's) should be `"yyyy-MM-dd"`.

---

## Changelog

> Format for new entries:
> ```
> ### [YYYY-MM-DD] — Short title
> - What was added / changed / fixed
> - Include class/file paths for precision
> ```

---

### [2026-11-12] — Pagination input validated (400 instead of 500)
- Invalid page parameters reached `PageRequest.of(...)`, which throws `IllegalArgumentException` → catch-all → 500; `pageSize` had no maximum (`pageSize=1000000` returned all 830 orders).
- The 5 paginated controller methods (`Customers`, `Employees`, `Orders`, `Products`, `Suppliers`): `@Min(0) pageNumber`, `@Min(1) @Max(MAX_PAGE_SIZE) pageSize` with explicit messages (the validator's default messages follow the JVM locale). New `controller/Paging.MAX_PAGE_SIZE = 100` (frontend uses 50). Swagger: limits in the `@Parameter` descriptions, min/max in the schema, 400 response on the list operation.
- `GlobalExceptionHandler`: `HandlerMethodValidationException` → 400, message `param: error; ...`.
- New `integration/PaginationIntegrationTest` (26). Checked by removing `@Max` from `SuppliersController`: exactly that case fails. 164 passing.
- Verified on the running app: -1 / 0 / 1000000 → 400 with the message, 50 → 200; no "Unhandled exception" log lines.

---

### [2026-11-12] — Integration tests for auth, orders, employees
- `pom.xml`: test deps `spring-boot-testcontainers`, `testcontainers-junit-jupiter`, `testcontainers-postgresql` (Boot-managed 2.0.4).
- New `TestcontainersConfiguration` (`@ServiceConnection` `postgres:16`); `ErpSpringApplicationTests` imports it, so no test needs the dev DB anymore.
- New `integration/AbstractIntegrationTest` (full app, MockMvc, real login helper) + `AuthIntegrationTest` (9), `OrdersIntegrationTest` (10), `EmployeesIntegrationTest` (9).
- 138 tests passing; mutation checks (manager rule, order-exists check) make the expected tests fail.

---

### [2026-11-12] — Create without an ID fixed on the remaining entities
- `Category`, `Customer`, `Product`, `Shipper`, `Supplier` (+ builders): ID `int` → `Integer`. Jackson binds entities through their all-args constructor, so a `POST` body without the ID failed with 400 "Malformed request body" (same cause as the `Employee` / `Order` fixes).
- `Product` constructor: `discontinued` parameter is `Boolean`, `null` → `false` (field stays `boolean`; the column is `NOT NULL`). A body without `discontinued` failed the same way.
- New `model/EntityJsonBindingTest` (7 tests); confirmed it fails with the old `int` type. 110 passing.
- Verified on the running app: `POST` without an ID → 201 for all 5; test rows deleted.

---

### [2026-11-12] — Spring Data auditing (createdAt / updatedAt)
- New `db/migration/V5__add_auditing_columns.sql`: `created_at`, `updated_at` (`timestamptz NOT NULL DEFAULT now()`) on all 8 tables; existing rows get the migration time.
- New `model/Auditable` (`@MappedSuperclass` + `AuditingEntityListener`, `@CreatedDate` / `@LastModifiedDate`, read-only in JSON); all 8 entities extend it. New `config/JpaAuditingConfig` (`@EnableJpaAuditing`).
- All 8 entity builders copy `createdAt` and have `withCreatedAt`. The 7 service `update` methods use `findById` (instead of `existsById`) and keep the stored `createdAt`, so the PUT response doesn't show `null`.
- `EmployeeDTO`, `OrderDTO`, `OrderDetailsDTO` (+ builders, bean→DTO converters): `createdAt` / `updatedAt`, read-only.
- Tests: 12 update tests stub `findById`; success cases assert the saved entity keeps `createdAt`. 103 passing.
- Verified on the running app: backfilled rows; create sets both; update keeps `createdAt` and moves `updatedAt`; values sent in the body are ignored; Swagger shows both as read-only `date-time`.
- Found: create without an ID failed on 5 entities — fixed in the next entry.

---

### [2026-11-12] — Service interfaces (ServiceImpl pattern)
- The 8 domain services (`Categories`, `Customers`, `Dashboard`, `Employees`, `Orders`, `Products`, `Shippers`, `Suppliers`) are now interfaces in `service/`; implementations moved to `service/impl/<Name>ServiceImpl` (`implements`, `@Override` on every public method). `UsersDetailsService` unchanged.
- Controllers, `AppConfig` and the controller tests unchanged (they already depend on `<Name>Service`). Service tests live in `test/.../service/impl/` (same package as the class under test) and `@InjectMocks` the `Impl`.
- No behavior change: 103 tests passing; app starts and endpoints respond.

---

### [2026-11-12] — Swagger: roles in the API description
- `config/OpenApiConfig`: `Info` conventions list now says some operations are managers only and return 403 for other users.

---

### [2026-11-12] — Employee create without `employeeId` fixed
- `model/Employee.employeeId` and `EmployeeBuilder.withEmployeeId`: `int` → `Integer`. Jackson binds `Employee` through its all-args constructor, so a body without `employeeId` (the Angular create form) failed with 400 "Malformed request body" — same root cause as the earlier `Order.orderId` fix.
- `EmployeesControllerSecurityTest`: `create_AsManagerWithoutEmployeeId_ShouldReturn201`. 103 passing.
- Verified on the running app: manager create → 201, new employee can log in, test row deleted.

---

### [2026-11-12] — Employee password security
- `model/User.getAuthorities()`: adds `ROLE_MANAGER` when `reportsTo == null` (new `isManager()`).
- `config/AppConfig`: `POST`/`PUT`/`DELETE` `/api/v1/employees/**` require `ROLE_MANAGER` (`accessDeniedHandler` → 403); `PUT /api/v1/employees/me/password` open to any logged-in user. Login handler reads `isManager` from the authorities; removed the `EmployeesService` dependency. Closes the hole where any employee could reset another's password or set their own `reportsTo` to `null` to become manager.
- `service/EmployeesService`: `validatePassword` (min 8 chars, max 72 UTF-8 bytes → 400) on create and on update when a password is sent; new `changeOwnPassword(username, current, new)` (`passwordEncoder.matches`, 400 if wrong); `getEmployee` helper for find-or-404; `StringUtils.hasText` for blank checks.
- New `dto/ChangePasswordDTO` record; new `EmployeesController.changeOwnPassword` (`PUT /me/password`, 204).
- `OpenApiConfig`: shared `ManagerOnly` 403 response; Employees operations documented as manager-only with the password rules.
- Tests: `EmployeesServiceTest` (+7: rules, changeOwnPassword), new `EmployeesControllerSecurityTest` (9), new `UserTest` (2); existing tests use 8+ char passwords. 102 passing.
- Verified on the running app: employee → 403 on PUT/DELETE of another employee; self-change 400 / 204; old password stops working.

---

### [2026-11-12] — Swagger login fixed
- Swagger UI login always returned 401: `springdoc.show-login-endpoint=true` documents a JSON body, but `formLogin` only reads `application/x-www-form-urlencoded` fields.
- Removed `springdoc.show-login-endpoint`; `OpenApiConfig.authEndpoints()` (`OpenApiCustomizer`) documents `POST /api/auth/login` (form body, `{message, username, isManager}` response, 401) and `POST /api/auth/logout` under an `Authentication` tag, with no security requirement.

---

### [2026-11-12] — Order detail create on a missing order → 404
- `OrdersService.create(int, OrderDetailsDTO)`: checks `ordersDao.existsById(orderId)` after the path/body match → `ResourceNotFoundException` (404). Previously the insert failed on the FK and the catch-all returned 500.
- `OrdersServiceTest`: new `createOrderDetail_WhenOrderNotFound_ShouldThrowNotFound`; existing detail-create tests stub `existsById`.
- `OrdersController`: Swagger description updated (404 for order or product).

---

### [2026-11-12] — OpenAPI descriptions written
- All 8 `@Tag`s, every `@Operation` and the `OpenApiConfig` `Info` description filled in (Info: auth flow, CSRF header, conventions).
- Operation descriptions list the real error cases (e.g. which references cause 409) and non-obvious behavior: employee password rules, order employee from the logged-in user, detail unit price from the product.

---

### [2026-11-12] — Swagger / OpenAPI documentation
- `pom.xml`: added `springdoc-openapi-starter-webmvc-ui` 3.0.3.
- New `config/OpenApiConfig.java` (see the later entry for the auth endpoints): `OpenAPI` bean (title, version, empty description), `JSESSIONID` cookie security scheme, shared `BadRequest`/`NotFound`/`Conflict` responses (`ErrorMessage` schema), `OperationCustomizer` that makes params optional on `@GetMapping(params = …)` methods (merged with the plain list GET).
- `config/AppConfig.java`: `permitAll` on `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`.
- `application.properties`: springdoc settings (UI path, CSRF header, sorters, `/api/**` only, JSON default, login endpoint shown). `application-prod.properties`: api-docs + UI disabled.
- All 8 controllers: `@Tag`, `@Operation(summary, description = "")`, `@ApiResponse` codes matching the real statuses (201/204/400/404/409); pagination params described; `Principal` hidden on `POST /orders`; PDF report documented as `application/pdf` binary.
- `exception/GlobalExceptionHandler`: `NoResourceFoundException` → 404 (unknown URLs, e.g. Swagger in prod, previously hit the catch-all → 500).

---

### [2026-11-06] — DB credentials moved to environment variables
- `application.properties`: datasource url/username/password → `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` (no defaults); `spring.config.import=optional:file:.env[.properties]` loads a local `.env`.
- New `application-prod.properties` (`prod` profile): `spring.jpa.show-sql=false`.
- `docker-compose.yml`: `POSTGRES_USER`/`POSTGRES_PASSWORD` read from `.env`.
- New `spring-erp/.env.example` (placeholders); `.env` added to root `.gitignore`.
- `README.md`: setup step `cp .env.example .env`; credentials table points to `.env`.
- Verified: full suite passes with `.env`; an exported `DB_PASSWORD` overrides `.env`.

---

### [2026-11-06] — Service unit test coverage completed
- `EmployeesServiceTest`: `findByUsername` found / not found; `create` saves the encoded password (captor); `create` with null password → 400.
- `OrdersServiceTest`: plain (non-paginated) `findAllByOrderByOrderIdDesc`; `create` with unknown username → `UsernameNotFoundException`, nothing saved.
- `DashboardServiceTest`: monthly queries use the first day of the current month; empty category rows / last orders produce empty collections.
- Service tests: 67 → 74, all passing. Removed the stale "No tests written" gap.

---

### [2026-11-06] — Flyway schema migrations
- `pom.xml`: added `spring-boot-starter-flyway` + `flyway-database-postgresql`.
- Removed root `northwind_setup.sql`; split into `db/migration/V1__northwind_base_schema.sql`, `V2__adapt_schema_to_entities.sql`, `V3__add_employee_credentials.sql`.
- New `V4__sync_identity_sequences.sql`: V2 added IDENTITY to populated tables, so sequences started at 1 and every create hit a duplicate PK (also broken in the old setup script).
- `README.md`: setup step now says Flyway creates the schema on first startup.
- This file: tech stack/config/schema sections updated from Oracle to the actual PostgreSQL setup.

---

### [2026-11-02] — Order customer is mandatory
- `dto/OrderDTO.customerId`: `@NotNull` → create/update without a customer returns 400 (was saved with `customer_id = NULL`, and the PDF report then failed with an NPE → 500).
- `model/Order.customer`: `@JoinColumn(name="customer_id", nullable = false)`.
- `OrdersService.getOrdersReport`: uses the eagerly loaded `order.getCustomer()`; removed the redundant `CustomersDAO` lookup and the `CustomersDAO` dependency.
- Tests: `OrdersControllerValidationTest` missing `customerId` → 400; dropped the customer-not-found report test.
- Frontend still allows an empty customer select and shows a "Walk-in Customer" fallback — to align.

---

### [2026-11-02] — REST status codes
- New `exception/ConflictException` → 409 in `GlobalExceptionHandler`.
- All 7 resource services: `delete` is now `void`; loads the entity with `findById` (404 `ResourceNotFoundException` if missing), throws `ConflictException` when referenced (previous messages kept). `update` checks `existsById` → 404 (Employees already did).
- `OrdersService`: `delete(orderId, productId)` → 404 via new `OrderDetailsDAO.existsByOrderOrderIdAndProductProductId`; `getOrdersReport` → 404 instead of `NoSuchElementException` (500).
- Controllers: `POST` → `201 Created`; `DELETE` → `204 No Content` (`ResponseEntity<Void>`, no more `{message}` body). Removed unused `Principal` from `OrdersController.update`.
- Tests: service delete tests rewritten (404 / 409 / success) + update-not-found cases; `OrdersControllerValidationTest` covers 201, 204, 404, 409.
- **Frontend impact:** deletes have no body; blocked-delete message now arrives in the HTTP error (`error.error.message`).

---

### [2026-11-02] — RESTful API paths
- All resource controllers: `DELETE /{resource}/delete/{id}` → `DELETE /{resource}/{id}`; `GET /{resource}/paginated` → `GET /{resource}?pageNumber=&pageSize=` (`@GetMapping(params = {"pageNumber", "pageSize"})` next to the plain list `@GetMapping`).
- `OrdersController`: added plain `GET /orders`; details moved under the order — `GET|POST /orders/{orderId}/details`, `DELETE /orders/{orderId}/details/{productId}` (no request body); report moved to `GET /orders/{id}/report`.
- `DashboardController`: `GET /dashboard/stats` → `GET /dashboard`.
- `OrdersService.create(int orderId, OrderDetailsDTO)`: throws `BadRequestException` (400) when body `orderId` ≠ path; `delete(orderId, productId)`; `getOrdersReport` moved here. Removed unused web imports.
- Removed `controller/ReportsController.java`, `service/ReportsService.java`, `service/ReportsServiceTest.java`; report tests moved into `OrdersServiceTest`.
- Tests: `OrdersControllerValidationTest` uses `/orders/{orderId}/details` + mismatch → 400 case; `OrdersServiceTest` mismatch case.

---

### [2026-10-26] — Bean Validation on request bodies

- `pom.xml`: added `spring-boot-starter-validation`.
- `@Valid` on every create/update `@RequestBody` in all 7 resource controllers. Not on `DELETE /orders/details/delete` (now `DELETE /orders/{orderId}/details/{productId}`), which only needs the two ids.
- Constraints mirror the live PostgreSQL columns: `NOT NULL` varchar → `@NotBlank` + `@Size(max)`, nullable varchar → `@Size(max)`.
  - Entities: `Category`, `Customer`, `Supplier`, `Shipper`, `Product` (also `@PositiveOrZero` on price/stock fields), `Employee` (`password` `@Size(max = 72)`, BCrypt's input limit).
  - `OrderDTO`: `@PositiveOrZero` freight, `@Size` on `ship*`. `customerId`/`shipperId` stay optional (nullable columns, "Walk-in Customer" in the UI).
  - `OrderDetailsDTO`: `@NotNull` `orderId`/`productId`, `@Positive` quantity, discount in `[0, 1]`.
  - No entity has an email column, so no `@Email`.
- Hibernate also runs these constraints on insert/update, since a validator is now on the classpath.
- `GlobalExceptionHandler`: `MethodArgumentNotValidException` → 400 with `"field: message; …"` in the existing `{ status, message, timestamp }` envelope; new `BadRequestException` → 400.
- `EmployeesService.create()`: throws `BadRequestException` when the password is null/blank. Done in the service rather than with `@NotBlank` because update allows a blank password (keeps the stored hash).
- Tests: new `controller/OrdersControllerValidationTest` (standalone MockMvc: invalid body → 400 listing the fields, service not called; valid body → 200). `EmployeesServiceTest`: create sends a password, plus a blank-password case.

### [2026-10-26] — DTOs for Employees, Orders, Order Details and Dashboard

- Fixes the BCrypt password hash leaking through `/employees`, `/orders/paginated`, `/orders/details/*` and `/dashboard/stats` (all serialized the `Employee` entity directly or nested in `Order`).
- New `dto/EmployeeDTO`, `OrderDTO`, `OrderDetailsDTO` + builders (same pattern as the entity builders). Renamed the misspelled `OrderDitailsDTO.java` → `OrderDetailsDTO.java`.
- New `converter/` package with static bean ↔ DTO converters for the three types.
- `EmployeesService`: all reads/writes return `EmployeeDTO`; `findByUsername` throws `ResourceNotFoundException`. `update()` now keeps the stored hash when the incoming password is null/blank and encodes it otherwise (the old `Objects.equals` on two BCrypt hashes never matched) and throws `ResourceNotFoundException` if the employee doesn't exist.
- `OrdersService` / `OrdersController`: take and return `OrderDTO` / `OrderDetailsDTO`. `create(OrderDetailsDTO)` puts the loaded `Product` on the saved detail so `productName` is populated in the response.
- `DashboardDTO.lastTenOrders`: `List<Order>` → `List<OrderDTO>`.
- `config/AppConfig.java`: login success handler adapted to `findByUsername` returning `EmployeeDTO`.
- Tests: `EmployeesServiceTest` (+3 update cases: blank password, new password, not found), `OrdersServiceTest` updated for DTOs.

### [2026-10-26] — Global exception handler

- `exception/GlobalExceptionHandler.java`: `@RestControllerAdvice` returning a standard `{ status, message, timestamp }` envelope.
  - `ResourceNotFoundException` → 404; `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, `MissingServletRequestParameterException` → 400; any other `Exception` → 500 with a generic message (real error logged).
- `dto/ErrorMessage.java`: error envelope.
- `exception/ResourceNotFoundException.java`: new unchecked exception.
- `OrdersService.create(OrderDetail)`: throws `ResourceNotFoundException` instead of `RuntimeException` when the product is missing; `OrdersServiceTest` updated.

### [2026-09-21] — Fixed primitive `orderId` breaking order creation

- `Order.orderId` was a primitive `int` used directly as the `@RequestBody` type on `POST /api/v1/orders` (`OrdersController.create`). New orders don't have an `orderId` yet, so Jackson had no value to bind for the primitive constructor parameter, failing order creation from the frontend.
- Same root-cause pattern as the earlier Date/LocalDate bug: a primitive type used somewhere it needs to tolerate "no value yet."
- `Order.java`: `orderId` changed from `int` to `Integer` (nullable), matching how `freight` is already `Double` not `double`.
- `OrderBuilder.java`: `orderId` field and `withOrderId()` param changed to `Integer` to match.

### [2026-09-21] — Fixed Date/LocalDate mismatch breaking dashboard stats

- `DashboardService.getStats()` was building the "start of month" cutoff as a `java.util.Date` (via `Calendar`) and passing it into DAO queries filtering on `Order.orderDate`, which is `LocalDate` — Hibernate threw on the type mismatch, surfaced by the frontend as a generic error banner.
- `DashboardService.getStats()`: replaced `Calendar`/`Date` construction with `LocalDate.now().withDayOfMonth(1)`.
- `OrdersDAO.countLastMonthOrders()`: param type changed from `Date` to `LocalDate`.
- `OrderDetailsDAO.calculateMonthlyRevenue()`: param type changed from `Date` to `LocalDate`.
- `DashboardServiceTest`: mocks updated to match `LocalDate` param type.

### [2026-06-26] — Rename root package to com.github.alessiobianchi.erp

- All Java source files updated: `package` and `import` declarations changed from `com.github.alessiobianchi.erp` to `com.github.alessiobianchi.erp`.
- `pom.xml` groupId changed from `com.github.alessiobianchi` to `com.github.alessiobianchi`.
- Source tree moved: `src/main/java/com/github/alessiobianchi/erp/` → `src/main/java/com/github/alessiobianchi/erp/`
- Test tree moved: `src/test/java/com/github/alessiobianchi/erp/` → `src/test/java/com/github/alessiobianchi/erp/`

### [2026-06-22] — Fixed DashboardController double-prefix path

- `DashboardController`: corrected `@RequestMapping` from `/api/v1/api/dashboard` to `/api/v1/dashboard`.

### [2026-06-22] — Proper HTTP methods + create/update service split

- All `delete` endpoints changed from `@GetMapping` to `@DeleteMapping`.
- `GET /all` dropped — list endpoints now map to the base resource path (`@GetMapping` with no suffix).
- Thymeleaf view methods (`showX()`) removed from all controllers — UI is fully Angular.
- Services: `save(entity)` split into `create(entity)` and `update(Long id, entity)`.
  - `update()` enforces the ID from the path, not the request body.
  - `EmployeesService.update()` fetches the existing hashed password and only re-encodes if the incoming value differs.
  - `EmployeesService.create()` bug fixed — was saving the unencoded `employee` instead of the encoded `employeeToSave`.
- Controllers: POST maps to base path (create), PUT maps to `/{id}` (update); both delegate to the correct service method.
- Tests updated: `save_Should…` renamed to `create_Should…`; `update_Should…` tests added for all services.
- Affected files: all `controller/*.java`, all `service/*.java`, all `test/service/*Test.java`.

### [2026-06-12] — Initial status document created

- Project scanned and documented from scratch at commit `ec3afc7` (Hotfix/session storage is manager value).
- All core Northwind domain modules implemented: Categories, Customers, Employees, Products, Suppliers, Shippers, Orders, OrderDetails.
- Dashboard aggregation endpoint live (`/api/dashboard/stats`).
- PDF report generation for orders live (`/reports/orders/{id}`).
- Session-based Spring Security 6 with CSRF and CORS for Angular frontend.
- Builder pattern used throughout for all entities and the DashboardDTO.
- 10 service-level test stubs present; no actual test logic verified.
