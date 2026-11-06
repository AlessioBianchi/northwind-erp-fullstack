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
| Testing | JUnit 5 + Mockito (via Spring Boot test starters) | (inherited) |

---

## Project Structure

```
spring-erp/
├── pom.xml
├── docker-compose.yml                          # PostgreSQL 16 container
├── .mvn/wrapper/
│   └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/github/alessiobianchi/erp/
    │   │   ├── ErpSpringApplication.java
    │   │   ├── config/
    │   │   │   └── AppConfig.java              # Security + CORS configuration
    │   │   ├── controller/
    │   │   │   ├── CategoriesController.java
    │   │   │   ├── CustomersController.java
    │   │   │   ├── DashboardController.java
    │   │   │   ├── EmployeesController.java
    │   │   │   ├── OrdersController.java
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
    │   │   └── service/
    │   │       ├── CategoriesService.java
    │   │       ├── CustomersService.java
    │   │       ├── DashboardService.java
    │   │       ├── EmployeesService.java
    │   │       ├── OrdersService.java
    │   │       ├── ProductsService.java
    │   │       ├── ShippersService.java
    │   │       ├── SuppliersService.java
    │   │       └── UsersDetailsService.java
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/                   # Flyway V1–V4
    └── test/
        └── java/com/github/alessiobianchi/erp/
            ├── ErpSpringApplicationTests.java
            └── service/                        # One test class per service
                └── (10 test files)
```

---

## Configuration

File: `src/main/resources/application.properties`

```properties
spring.application.name=ERPSpring

spring.datasource.url=jdbc:postgresql://localhost:5432/northwind
spring.datasource.username=erp_user
spring.datasource.password=erp_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

- No connection pool tuning (uses HikariCP defaults).
- Schema owned by Flyway (default config, runs on startup); Hibernate only validates it.
- No profiles or environment-specific overrides.

---

## Database Schema

**Migrations** (`src/main/resources/db/migration/`):

| Version | File | Content |
|---|---|---|
| V1 | `V1__northwind_base_schema.sql` | Northwind PostgreSQL dump (tables + data) |
| V2 | `V2__adapt_schema_to_entities.sql` | smallint → integer, `discontinued` → boolean, customers integer PK (old code → `customer_code`), IDENTITY on PKs |
| V3 | `V3__add_employee_credentials.sql` | `employees.username`/`password` + seed logins (`password123`) |
| V4 | `V4__sync_identity_sequences.sql` | `setval` identity sequences to `MAX(id)` |

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
service/      Business rules, delete guards, aggregations
dao/          Spring Data JPA interfaces (CrudRepository + @Query)
model/        JPA entities + Builder companions
dto/          API DTOs + builders (Dashboard, Employee, Order, OrderDetails)
converter/    Static bean ↔ DTO converters, called from services
config/       Security and CORS (AppConfig.java)
```

### Builder pattern
Every entity has a hand-written `*Builder` companion (fluent API). Used in service layer for partial updates — the service loads the existing entity, applies changes via the builder, then saves.

### DTOs & converters
Employees, Orders, Order Details and the Dashboard's `lastTenOrders` go through DTOs; Categories, Customers, Products, Suppliers and Shippers still expose entities directly. Conversion happens in the **service** layer via static `converter/*Converter.convert(...)` methods (no Spring beans). Controllers only see DTOs for these modules. Order/OrderDetail DTOs are flat: associations become `xxxId` + `xxxName`, and DTO → bean builds id-only association stubs for JPA.

### Data access
Spring Data JPA with derived query methods (`findAllByOrderBy…`) and custom `@Query` for aggregations (revenue sums, category counts). Pagination uses Spring Data `Page` + `Pageable`; serialization mode: `VIA_DTO` (Spring 6.1+).

### Authentication adapter
`User.java` is not a JPA entity — it wraps `Employee` and implements Spring Security's `UserDetails`. `UsersDetailsService` bridges JPA and Spring Security by loading an `Employee` by username and wrapping it in a `User`.

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
- `isManager` flag derived from `employee.reportsTo == null`.
- **Status: complete.**

---

## API Endpoints

All resource controllers are under `/api/v1/`. Status codes: `GET`/`PUT` → 200, `POST` → 201 (no `Location`: there is no `GET /{id}`), `DELETE` → 204; `PUT`/`DELETE` on a missing id → 404; `DELETE` blocked by referencing rows → 409 (`ConflictException`); validation/bad input → 400. Errors use the `{ status, message, timestamp }` envelope. `POST` = create, `PUT /{id}` = update, `DELETE /{id}` = delete (ID comes from path, not body). Paginated lists are the same collection path selected by `pageNumber` + `pageSize` query params (`@GetMapping(params = ...)`).

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
| POST | `/api/v1/employees` | `201` + `EmployeeDTO` (body: `Employee`) |
| PUT | `/api/v1/employees/{employeeId}` | `ResponseEntity<EmployeeDTO>` (body: `Employee`; blank password keeps the stored hash) |
| DELETE | `/api/v1/employees/{employeeId}` | `204`; `404` if missing, `409` if referenced |

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
| POST | `/api/v1/orders/{orderId}/details` | `201`; body/response `OrderDetailsDTO`; body `orderId` must match path (400); unit price auto-set from product |
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
| Access control | All endpoints require authentication; no role-based restrictions yet |

---

## Data Model

### Entities

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
| `EmployeesService` | CRUD + pagination; delete blocked if employee has orders |
| `ProductsService` | CRUD + pagination; delete blocked if product appears in order details |
| `SuppliersService` | CRUD + pagination; delete blocked if supplier has products |
| `ShippersService` | CRUD; delete blocked if shipper has orders |
| `OrdersService` | CRUD + pagination; `save(order, username)` auto-sets employee from auth principal; detail create checks path `orderId` matches body (400 otherwise) and auto-sets unitPrice from product; PDF report via OpenPDF (EUR) |
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

- `service/` — 9 unit test classes, one per service (74 tests). JUnit 5 + `@ExtendWith(MockitoExtension.class)`, DAOs/`PasswordEncoder` mocked, Arrange/Act/Assert. Every public service method is covered, including the not-found (404), conflict (409) and bad-request (400) paths.
- `controller/OrdersControllerValidationTest` — standalone MockMvc: bean validation, status codes.
- `ErpSpringApplicationTests` — context smoke test; **needs the Postgres container running** (Flyway connects on startup).

---

## Known Gaps & TODOs

- [ ] **No API documentation** — Swagger / SpringDoc OpenAPI not configured.
- [ ] **No caching** — no `@Cacheable` on read-heavy endpoints.
- [ ] **No Actuator endpoints** — no health check or metrics.
- [ ] **Thymeleaf dead code** — view references remain in some controllers; UI is fully Angular now.
- [ ] **Hardcoded DB credentials** in `application.properties` — acceptable for dev, must not go to production.
- [ ] **Magic number** — low-stock threshold `< 10` is hardcoded in `DashboardService`.
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
