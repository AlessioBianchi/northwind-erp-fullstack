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

- **Root package:** `it.zerob.erp`
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
| Database | Oracle Database XE | XEPDB1 @ localhost:1521 |
| JDBC Driver | ojdbc11 | runtime |
| Security | Spring Security 6 | (inherited) |
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
├── .mvn/wrapper/
│   └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/it/zerob/erp/
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
    │   │   │   ├── ReportsController.java
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
    │   │   ├── dto/
    │   │   │   ├── DashboardDTO.java
    │   │   │   └── DashboardDTOBuilder.java
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
    │   │       ├── ReportsService.java
    │   │       ├── ShippersService.java
    │   │       ├── SuppliersService.java
    │   │       └── UsersDetailsService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/it/zerob/erp/
            ├── ErpSpringApplicationTests.java
            └── service/                        # One test class per service
                └── (10 test files)
```

---

## Configuration

File: `src/main/resources/application.properties`

```properties
spring.application.name=ERPSpring
logging.level.web=debug
logging.level.org.springframework.jdbc.core=debug

spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=ZRB_ERP
spring.datasource.password=ZRB_ERP
```

- No connection pool tuning (uses HikariCP defaults).
- No schema migration tool (Flyway/Liquibase absent — schema is pre-existing Northwind-style Oracle DB).
- No profiles or environment-specific overrides.

---

## Database Schema

**Oracle sequences:** `SEQ_NW_CATEGORIES`, `SEQ_NW_CUSTOMERS`, `SEQ_NW_EMPLOYEES`, `SEQ_NW_PRODUCTS`, `SEQ_NW_SUPPLIERS`, `SEQ_NW_SHIPPERS`, `SEQ_NW_ORDERS`

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

No migration scripts in the repo — database objects are assumed to exist before the app starts.

---

## Architecture & Patterns

### Layered structure
3-tier layered architecture: **Controller → Service → DAO (Repository)**

```
controller/   @Controller / @RestController — HTTP only, no logic
service/      Business rules, delete guards, aggregations
dao/          Spring Data JPA interfaces (CrudRepository + @Query)
model/        JPA entities + Builder companions
dto/          DashboardDTO + builder
config/       Security and CORS (AppConfig.java)
```

### Builder pattern
Every entity has a hand-written `*Builder` companion (fluent API). Used in service layer for partial updates — the service loads the existing entity, applies changes via the builder, then saves.

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
- Single `GET /api/dashboard/stats` endpoint returning `DashboardDTO`.
- Aggregates: total/monthly order count and revenue, last 10 orders, orders-by-category map, low-stock products (< 10 units).
- **Status: complete.**

### Reports (PDF) — `controller/`, `service/`
- `GET /reports/orders/{id}` streams a PDF for a given order.
- Generated with OpenPDF; currency formatted in EUR (€).
- **Status: complete.**

### Authentication — `config/`, `service/`, `model/User.java`
- Session-based login/logout via Spring Security form auth.
- `isManager` flag derived from `employee.reportsTo == null`.
- **Status: complete.**

---

## API Endpoints

All resource controllers are under `/api/v1/`. `POST` = create, `PUT /{id}` = update (ID comes from path, not body).

### CategoriesController (`/api/v1/categories`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/categories` | `List<Category>` |
| POST | `/api/v1/categories` | `ResponseEntity<Category>` |
| PUT | `/api/v1/categories/{categoryId}` | `ResponseEntity<Category>` |
| DELETE | `/api/v1/categories/delete/{categoryId}` | `ResponseEntity<Map<String,String>>` |

### CustomersController (`/api/v1/customers`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/customers` | `List<Customer>` |
| GET | `/api/v1/customers/paginated` | `Page<Customer>` |
| POST | `/api/v1/customers` | `ResponseEntity<Customer>` |
| PUT | `/api/v1/customers/{customerId}` | `ResponseEntity<Customer>` |
| DELETE | `/api/v1/customers/delete/{customerId}` | `ResponseEntity<Map<String,String>>` |

### EmployeesController (`/api/v1/employees`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/employees` | `List<Employee>` |
| GET | `/api/v1/employees/paginated` | `Page<Employee>` |
| POST | `/api/v1/employees` | `ResponseEntity<Employee>` |
| PUT | `/api/v1/employees/{employeeId}` | `ResponseEntity<Employee>` |
| DELETE | `/api/v1/employees/delete/{employeeId}` | `ResponseEntity<Map<String,String>>` |

### ProductsController (`/api/v1/products`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/products` | `List<Product>` |
| GET | `/api/v1/products/paginated` | `Page<Product>` |
| POST | `/api/v1/products` | `ResponseEntity<Product>` |
| PUT | `/api/v1/products/{productId}` | `ResponseEntity<Product>` |
| DELETE | `/api/v1/products/delete/{productId}` | `ResponseEntity<Map<String,String>>` |

### SuppliersController (`/api/v1/suppliers`) — same pattern as Customers
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/suppliers` | `List<Supplier>` |
| GET | `/api/v1/suppliers/paginated` | `Page<Supplier>` |
| POST | `/api/v1/suppliers` | `ResponseEntity<Supplier>` |
| PUT | `/api/v1/suppliers/{supplierId}` | `ResponseEntity<Supplier>` |
| DELETE | `/api/v1/suppliers/delete/{supplierId}` | `ResponseEntity<Map<String,String>>` |

### ShippersController (`/api/v1/shippers`)
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/shippers` | `List<Shipper>` |
| POST | `/api/v1/shippers` | `ResponseEntity<Shipper>` |
| PUT | `/api/v1/shippers/{shipperId}` | `ResponseEntity<Shipper>` |
| DELETE | `/api/v1/shippers/delete/{shipperId}` | `ResponseEntity<Map<String,String>>` |

### OrdersController (`/api/v1/orders`) — `@RestController`
| Method | Path | Notes |
|---|---|---|
| GET | `/api/v1/orders/paginated` | `Page<Order>` |
| POST | `/api/v1/orders` | Employee resolved from `Principal` |
| PUT | `/api/v1/orders/{orderId}` | |
| DELETE | `/api/v1/orders/delete/{orderId}` | |
| GET | `/api/v1/orders/details/{orderId}` | `List<OrderDetail>` |
| POST | `/api/v1/orders/details` | Unit price auto-set from product |
| DELETE | `/api/v1/orders/details/delete` | Body: `OrderDetail` |

### DashboardController — `@RestController`
> **Bug:** controller is mapped to `/api/v1/api/dashboard` (double prefix). Frontend calls `/api/v1/dashboard/stats` — dashboard is broken until fixed.

| Method | Path | Response |
|---|---|---|
| GET | `/api/v1/api/dashboard/stats` | `ResponseEntity<DashboardDTO>` |

### ReportsController (`/reports`)
| Method | Path | Response |
|---|---|---|
| GET | `/reports/orders/{id}` | `application/pdf` byte stream |

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
| `OrdersService` | CRUD + pagination; `save(order, username)` auto-sets employee from auth principal; detail save auto-sets unitPrice from product |
| `DashboardService` | Aggregates: total/monthly orders & revenue, last 10 orders, orders-by-category map, low-stock products |
| `ReportsService` | Generates PDF for a given order using OpenPDF; EUR currency |
| `UsersDetailsService` | Implements `UserDetailsService`; loads `Employee` by username, wraps in `User` |

### DTO

**`DashboardDTO`** (built via `DashboardDTOBuilder`):

| Field | Type |
|---|---|
| totalOrdersCount | Long |
| totalRevenue | Double |
| lastMonthOrdersCount | Long |
| lastMonthRevenue | Double |
| lastTenOrders | List\<Order\> |
| ordersByCategory | Map\<String, Integer\> |
| productStock | List\<Product\> (low-stock items) |

---

## Tests

10 test classes under `src/test/java/it/zerob/erp/service/` — one per service — plus the application context smoke test (`ErpSpringApplicationTests.java`). Tooling: JUnit 5 + Mockito via Spring Boot test starters. No test fixtures or test data files detected.

---

## Known Gaps & TODOs

- [ ] **No global exception handler** — no `@RestControllerAdvice`; errors fall through to Spring Boot default.
- [ ] **Dashboard controller double-prefix bug** — `DashboardController` uses `@RequestMapping("/api/v1/api/dashboard")` instead of `/api/v1/dashboard`. Dashboard endpoint is unreachable from the frontend.
- [ ] **No input validation** — no `@Valid` or `@NotNull` constraints on controllers or entities.
- [ ] **No API documentation** — Swagger / SpringDoc OpenAPI not configured.
- [ ] **No caching** — no `@Cacheable` on read-heavy endpoints.
- [ ] **No Actuator endpoints** — no health check or metrics.
- [ ] **No database migration tool** — schema must be managed manually (no Flyway/Liquibase).
- [ ] **Thymeleaf dead code** — view references remain in some controllers; UI is fully Angular now.
- [ ] **Hardcoded DB credentials** in `application.properties` — acceptable for dev, must not go to production.
- [ ] **Magic number** — low-stock threshold `< 10` is hardcoded in `DashboardService`.
- [ ] **Date pattern typo** in `Employee.java` — `"yyy-MM-dd"` (three y's) should be `"yyyy-MM-dd"`.
- [ ] **No tests written** — test class stubs exist but no actual test logic verified.

---

## Changelog

> Format for new entries:
> ```
> ### [YYYY-MM-DD] — Short title
> - What was added / changed / fixed
> - Include class/file paths for precision
> ```

---

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
