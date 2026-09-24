# Northwind ERP — Frontend Status

> **Purpose of this file:** Living reference document for Claude Code. Read this first in any new conversation — it gives full context on the project without needing to re-scan the codebase. Update the [Changelog](#changelog) section whenever a meaningful change is made.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Architecture & Patterns](#architecture--patterns)
5. [Features & Module Status](#features--module-status)
6. [API Integration](#api-integration)
7. [Auth & Session](#auth--session)
8. [Styling](#styling)
9. [Known Gaps & TODOs](#known-gaps--todos)
10. [Changelog](#changelog)

---

## Project Overview

Angular 21 frontend for a Northwind ERP replica. Connected to a Spring Boot backend running on `localhost:8080`. The app provides full CRUD management for the classic Northwind database entities: Orders, Products, Categories, Customers, Suppliers, Shippers, and Employees.

**Entry point:** `src/main.ts`  
**Root component:** `src/app/app.ts`  
**Routing:** `src/app/app.routes.ts`  
**App config:** `src/app/app.config.ts`  
**Backend base URL:** `http://localhost:8080` (from `src/environments/environment.ts`, injected via `API_BASE_URL` token)

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Framework | Angular | 21.2.0 |
| Language | TypeScript | ~5.9.2 |
| UI Framework | Bootstrap | 5.3.8 |
| Icons | Bootstrap Icons | 1.13.1 |
| Charts | Chart.js | 4.5.1 |
| Reactive | RxJS | ~7.8.0 |
| Testing | Vitest + jsdom | 4.0.8 |
| Formatter | Prettier | 3.5.3 |
| Build | Angular CLI / @angular/build | 21.2.11 |

**Angular style:** Standalone components (no NgModules). Dependency injection via `inject()` function (not constructor params).

---

## Project Structure

```
angular-erp/
├── src/
│   ├── main.ts                          # App bootstrap
│   ├── index.html
│   ├── styles.css                       # Global CSS (empty — Bootstrap loaded via angular.json)
│   └── app/
│       ├── app.ts                       # Root component (RouterOutlet only)
│       ├── app.routes.ts                # All route definitions
│       ├── app.config.ts                # HttpClient, CSRF, router, API_BASE_URL providers
│       ├── api-base-url.token.ts        # InjectionToken<string> for the backend base URL
│       ├── page-response.model.ts       # Generic PageResponse<T> pagination model
│       │
│       ├── login/
│       │   ├── login.component.ts/html/css
│       │
│       ├── layout/
│       │   ├── main-layout/             # App shell: sidebar + top navbar + <router-outlet>
│       │   ├── dashboard/               # KPI cards + Chart.js pie + last orders + low stock
│       │   ├── orders/                  # Orders CRUD + order-details line items
│       │   ├── products/                # Products CRUD + category management tab
│       │   ├── customers/               # Customers CRUD
│       │   ├── suppliers/               # Suppliers CRUD + shippers management tab
│       │   └── employees/              # Employees CRUD (manager-only)
│       │
│       └── service/
│           ├── auth.service.ts
│           ├── dashboard.service.ts
│           ├── orders.service.ts
│           ├── products.service.ts
│           ├── customers.service.ts
│           ├── suppliers.service.ts
│           ├── employees.service.ts
│           ├── categories.service.ts
│           ├── shippers.service.ts
│           └── csrf.interceptor.ts      # Functional interceptor — adds X-XSRF-TOKEN header
├── src/environments/
│   ├── environment.ts                   # Dev config — apiBaseUrl: http://localhost:8080
│   └── environment.prod.ts              # Prod config — apiBaseUrl: '' (same-origin), swapped in via angular.json fileReplacements
├── angular.json
├── package.json
├── tsconfig.json
└── FRONTEND_STATUS.md                   # This file
```

**Model files** live next to their component (e.g., `orders/order.model.ts`). No barrel/index exports.

---

## Architecture & Patterns

### Component model
- All components are **standalone** (importMap in the component decorator, no NgModules).
- Forms use **template-driven forms** (`FormsModule`, `ngModel`) — no reactive forms.
- State is **local to each component** (no NgRx, no signals-based store). No centralized state management.

### Data flow
1. Service injects `HttpClient`, exposes methods returning `Observable<T>`.
2. Component calls service in `ngOnInit`, subscribes, stores result in component fields.
3. Template binds to component fields.
4. No `async` pipe used — components manage subscriptions manually.

### Pagination
All list views use a shared pagination pattern:
- `currentPage`, `pageSize` (usually 50), `totalPages`, `totalElements` on the component.
- Backend returns `PageResponse<T>` (content array + pagination metadata).
- Previous/Next navigation calls the service again with updated page number.

### Workspace state
List+detail components (orders, products, customers, suppliers, employees) use a `currentWorkspaceState` string:
- `'empty'` — nothing selected, right panel hidden or blank
- `'edit'` — existing record selected
- `'new'` — creating a new record

---

## Features & Module Status

### Login — `src/app/login/`
- Form: username + password with `ngModel`.
- Calls `POST /api/auth/login` (form-urlencoded body).
- On success: saves `username` and `isManager` to `sessionStorage`, navigates to `/layout/dashboard`.
- On failure: shows Bootstrap alert.
- Handles logout message (query param or sessionStorage flag).
- **Status: complete.**

### Main Layout — `src/app/layout/main-layout/`
- Bootstrap sidebar (collapsible) + top navbar.
- Nav links: Dashboard, Orders, Products, Customers, Suppliers+Shippers, Employees.
- Employees link hidden if `sessionStorage.isManager !== 'true'`.
- Logout clears sessionStorage, navigates to `/login`.
- **Status: complete.**

### Dashboard — `src/app/layout/dashboard/`
- 4 KPI stat cards: current-month orders, current-month revenue, total orders, total revenue (EUR).
- Chart.js **pie chart**: Orders by category.
- Table: last 10 orders (customer, date, shipper).
- Table: low-stock products (unitsInStock < 10, highlighted red).
- Data from `GET /api/dashboard/stats` via `DashboardService`.
- Chart initialized in `ngAfterViewInit`.
- **Status: complete.**

### Orders — `src/app/layout/orders/`
- Left panel: paginated list (Order ID, customer company, country). New + Delete buttons.
- Right panel tab 1 — Order form:
  - Customer dropdown, order/required/shipped dates, freight, shipper dropdown.
  - Ship-to address fields.
- Right panel tab 2 — Order Details:
  - Line items table (product, qty, unit price, discount %).
  - Add detail via Bootstrap modal (product selector, qty, price, discount).
  - Delete line item.
- **API:** paginated list, save (create/update), delete, details list, save detail, delete detail.
- **Status: complete.**

### Products — `src/app/layout/products/`
- Left panel: paginated list with search (name, category, supplier). Discontinued badge.
- Right panel tab 1 — Product form:
  - Name, category dropdown, supplier dropdown, quantity per unit, unit price (EUR), stock, on-order, reorder level, discontinued checkbox.
- Right panel tab 2 — Category form:
  - Category dropdown/selector, name, description, save/delete.
- **API:** products (paginated + all + save + delete), categories (all + save + delete).
- **Status: complete.**

### Customers — `src/app/layout/customers/`
- Left panel: paginated list with search (customer code or company name).
- Right panel: full contact + address form (code, company, contact name/title, address, phone, fax).
- New, Save, Cancel, Delete actions.
- **API:** paginated list, all, save, delete.
- **Status: complete.**

### Suppliers — `src/app/layout/suppliers/`
- Left panel: paginated supplier list with search (company name, contact).
- Right panel tab 1 — Supplier form: company, contact title/name, address, phone, fax, homepage URL.
- Right panel tab 2 — Shippers: dropdown to select shipper, company name, phone. Save/delete.
- **API:** suppliers (paginated + all + save + delete), shippers (all + save + delete).
- **Status: complete.**

### Employees — `src/app/layout/employees/`
- **Manager-only section** (hidden in nav if not manager).
- Left panel: paginated list with search (firstname, lastname, title). Shows "reports to" manager.
- Right panel: employee form — lastname, firstname, title, reports-to dropdown, birthdate, hire date, address, phone, extension, username, password.
- **API:** employees (paginated + all + save + delete).
- **Status: complete** (UI built; opened in IDE — may be in progress or under review).

---

## API Integration

**Base URL:** `http://localhost:8080` (dev) — sourced from `src/environments/environment.ts` and injected into every service via the `API_BASE_URL` token (`src/app/api-base-url.token.ts`), provided in `app.config.ts`. Production build swaps in `environment.prod.ts` via `fileReplacements` in `angular.json`.  
**HTTP client config (`app.config.ts`):**
- `withFetch()` — uses browser Fetch API.
- `withXsrfConfiguration({ cookieName: 'XSRF-TOKEN', headerName: 'X-XSRF-TOKEN' })`.
- `withCredentials: true` on all requests (session cookie passed to backend).
- `csrfInterceptor` — functional interceptor that reads the XSRF cookie and injects the header on non-GET requests.
- `errorInterceptor` — functional interceptor that catches 4xx/5xx responses (except `/api/auth/login`, which has its own inline error handling), pushes the message to `ErrorNotificationService`, and re-throws so components can still react if needed. Displayed via `ErrorBannerComponent`, mounted at the root of `app.html`.

### Endpoint map

All endpoints are under `/api/v1/`. `POST` = create (no ID in body), `PUT /{id}` = update.

| Feature | Method | Endpoint |
|---|---|---|
| Auth | POST | `/api/auth/login` |
| Dashboard | GET | `/api/v1/dashboard/stats` |
| Orders | GET | `/api/v1/orders/paginated` |
| Orders | POST | `/api/v1/orders` |
| Orders | PUT | `/api/v1/orders/{orderId}` |
| Orders | DELETE | `/api/v1/orders/delete/{orderId}` |
| Order Details | GET | `/api/v1/orders/details/{orderId}` |
| Order Details | POST | `/api/v1/orders/details` |
| Order Details | DELETE | `/api/v1/orders/details/delete` (body) |
| Products | GET | `/api/v1/products`, `/api/v1/products/paginated` |
| Products | POST | `/api/v1/products` |
| Products | PUT | `/api/v1/products/{productId}` |
| Products | DELETE | `/api/v1/products/delete/{productId}` |
| Categories | GET | `/api/v1/categories` |
| Categories | POST | `/api/v1/categories` |
| Categories | PUT | `/api/v1/categories/{categoryId}` |
| Categories | DELETE | `/api/v1/categories/delete/{categoryId}` |
| Customers | GET | `/api/v1/customers`, `/api/v1/customers/paginated` |
| Customers | POST | `/api/v1/customers` |
| Customers | PUT | `/api/v1/customers/{customerId}` |
| Customers | DELETE | `/api/v1/customers/delete/{customerId}` |
| Suppliers | GET | `/api/v1/suppliers`, `/api/v1/suppliers/paginated` |
| Suppliers | POST | `/api/v1/suppliers` |
| Suppliers | PUT | `/api/v1/suppliers/{supplierId}` |
| Suppliers | DELETE | `/api/v1/suppliers/delete/{supplierId}` |
| Shippers | GET | `/api/v1/shippers` |
| Shippers | POST | `/api/v1/shippers` |
| Shippers | PUT | `/api/v1/shippers/{shipperId}` |
| Shippers | DELETE | `/api/v1/shippers/delete/{shipperId}` |
| Employees | GET | `/api/v1/employees`, `/api/v1/employees/paginated` |
| Employees | POST | `/api/v1/employees` |
| Employees | PUT | `/api/v1/employees/{employeeId}` |
| Employees | DELETE | `/api/v1/employees/delete/{employeeId}` |

---

## Auth & Session

| Aspect | Implementation |
|---|---|
| Login | POST with `application/x-www-form-urlencoded` body |
| Session storage | `sessionStorage`: keys `username` and `isManager` |
| Role check | `isManager === 'true'` string comparison |
| Route guards | `authGuard` (`src/app/auth.guard.ts`) — `CanActivateFn` on the `layout` route, redirects to `/login` if no session. `managerGuard` (`src/app/manager.guard.ts`) — `CanActivateFn` on the `employees` child route, redirects non-managers to `/404` |
| Logout | Calls `POST /api/auth/logout`, then `AuthService.logout()` clears sessionStorage and the in-memory session cache, and navigates to `/login` |
| CSRF | HTTP-only cookie `XSRF-TOKEN` → `X-XSRF-TOKEN` header via interceptor |
| withCredentials | `true` on all requests — session cookie sent automatically |

---

## Styling

- **Bootstrap 5.3.8** loaded globally via `angular.json` (not imported in CSS).
- **Bootstrap Icons 1.13.1** loaded globally.
- **Global styles.css** is empty — all styling via Bootstrap utility classes.
- **Component CSS files** exist but are mostly empty or minimal.
- **Responsive:** Bootstrap grid (`col-md-*`, `col-lg-*`), flex utilities.
- **UI patterns used:**
  - Cards for panels, KPI boxes
  - Tables with `table-hover`, `table-striped`, sticky headers
  - Bootstrap modals for sub-forms (e.g., add order detail)
  - Badges for status (discontinued, low stock)
  - Alerts for feedback messages
  - Sidebar with collapse toggle

---

## Known Gaps & TODOs

- [ ] `app.spec.ts`'s `should render title` test fails (pre-existing, unrelated to services/guard testing work — stale title assertion).

---

## Changelog

> Format for new entries:
> ```
> ### [YYYY-MM-DD] — Short title
> - What was added / changed / fixed
> - Include component/service/file paths for precision
> ```

---

### [2026-09-24] — Reset add-order-detail modal form after save

- `orders.component.ts`: extracted the `modalDetailForm` default shape into a private `createEmptyDetailForm()` method (used both for the initial field value and for resetting). `submitDetailForm()` now resets `modalDetailForm` to that default on a successful save, so reopening the "add line item" modal no longer shows the previous entry's stale product/quantity/unitPrice/discount values.

### [2026-09-24] — Fix order line-item selection toggle

- `orders.component.ts`: `selectDetail(detail)` compared `selectedDetail === null` instead of `selectedDetail === detail`, so clicking a different line item while one was already selected cleared the selection instead of switching to the newly clicked row. Changed to `this.selectedDetail = this.selectedDetail === detail ? null : detail;` — clicking a row selects it, re-clicking the selected row deselects it, and clicking a different row switches to it directly.

### [2026-09-24] — Clear AuthService in-memory state on logout

- `auth.service.ts`: added `logout()`, which clears `sessionStorage` and resets the in-memory `usernameLogged`/`isManager` fields. Previously `getUsernameLogged()`/`isUserManager()` only re-read `sessionStorage` when the cached in-memory field was falsy, so after a `sessionStorage.clear()` alone, a still-truthy in-memory value kept satisfying `authGuard` — e.g. hitting the browser Back button into a guarded route right after logout let the user back in.
- `main-layout.component.ts`: `onLogout()` now calls `this.authService.logout()` instead of `sessionStorage.clear()` directly, on both the `complete` and `error` branches.
- `auth.service.spec.ts`: added a test asserting `logout()` clears both storage and the in-memory cache, verified through the public getters (so a future regression that clears storage but forgets the in-memory fields would fail it).

### [2026-09-24] — Manager-only guard on the Employees route

- Added `src/app/manager.guard.ts`: `managerGuard` (`CanActivateFn`) checks `authService.isUserManager()` and redirects to `/404` if false.
- `app.routes.ts`: applied `canActivate: [managerGuard]` to the `employees` child route (alongside the existing `authGuard` on the parent `layout` route). Added a dedicated `{ path: '404', component: NotFoundComponent }` route so the guard can navigate to a clean URL rather than relying on the wildcard catch-all.
- Added `src/app/manager.guard.spec.ts`: 2 tests — allows activation for managers, redirects to `/404` and blocks activation for non-managers.
- Closes the previously known gap where the Employees module (full CRUD on other employees' usernames/passwords) was gated only by hiding the sidebar link — any authenticated non-manager could reach it by URL.
- Returns 404 rather than redirecting to the dashboard, so an unauthorized user isn't shown a "you can't do this" signal that confirms the route exists.

### [2026-09-24] — Unit tests for AuthService, authGuard, ProductsService

- Added `src/app/service/auth.service.spec.ts`: 3 tests — `login()` stores `username`/`isManager` in `sessionStorage` on success, `getUsernameLogged()` falls back to `sessionStorage`, `isUserManager()` falls back to `sessionStorage`.
- Added `src/app/auth.guard.spec.ts`: 2 tests — allows activation when a user is logged in, redirects to `/login` and blocks activation when not.
- Added `src/app/service/products.service.spec.ts`: 2 tests — `getAllProducts()` GETs the products endpoint, `updateProduct()` PUTs to the product-specific endpoint. Used as the representative case for the other CRUD services, which share the same `inject(HttpClient)` / `inject(API_BASE_URL)` shape.
- All use `provideHttpClient()` + `provideHttpClientTesting()` with `API_BASE_URL` stubbed via `TestBed`.
- Closes the previously known gap where no spec files existed.

### [2026-09-24] — Fix logout success message

- `main-layout.component.ts`: `onLogout()` now navigates to `/login` with `queryParams: { loggedOut: 'true' }` (both on the `complete` and `error` branches), so the login route knows a logout just happened.
- `login.component.ts`: constructor now injects `ActivatedRoute` and sets `hasLoggedOut` from `route.snapshot.queryParamMap.get('loggedOut') === 'true'`. The `@if (hasLoggedOut)` alert in `login.component.html` already existed but the flag was never being set — it's now wired up.

### [2026-09-24] — Wildcard route + 404 page

- Added `src/app/not-found/not-found.component.ts` + `.html`: standalone `NotFoundComponent`, simple Bootstrap 404 message with a link back to `/login`.
- `app.routes.ts`: replaced the commented-out `{ path: '**', redirectTo: 'login' }` with an active `{ path: '**', component: NotFoundComponent }` as the last route.
- Closes the previously known gap where unknown URLs produced a blank page.

### [2026-09-21] — takeUntilDestroyed() on all subscriptions

- Added `private destroyRef = inject(DestroyRef);` and piped `takeUntilDestroyed(this.destroyRef)` into every `.subscribe()` call across the app (32 sites): `CustomersComponent`, `ProductsComponent`, `SuppliersComponent`, `OrdersComponent`, `EmployeesComponent`, `DashboardComponent`, `MainLayoutComponent`, `LoginComponent`.
- Where a call already piped through `finalize()` (the loading-spinner work), `takeUntilDestroyed` was added as an additional pipe operator rather than a separate `.pipe()`.
- Closes the previously known gap where components never unsubscribed from in-flight HTTP calls on destroy.

### [2026-09-21] — Loading spinners on CRUD list panels

- Added `src/app/loading-spinner/loading-spinner.component.ts` + `.html`: small standalone Bootstrap spinner, reused across modules.
- Added `isLoading` boolean to `CustomersComponent`, `ProductsComponent`, `SuppliersComponent`, `OrdersComponent`, `EmployeesComponent` — set around each module's paginated list fetch via `finalize()`, covering initial load, pagination, and the reload triggered after save/delete.
- Each module's list `<tbody>` now shows `<app-loading-spinner>` in place of the rows while `isLoading` is true.
- Closes the previously known gap where there was no visual feedback during HTTP requests.

### [2026-09-21] — Global HTTP error interceptor

- Added `src/app/service/error-notification.service.ts`: signal-based `ErrorNotificationService` holding the current error message.
- Added `src/app/service/error.interceptor.ts`: `errorInterceptor` catches 4xx/5xx on every request except `/api/auth/login` (which has its own inline error handling), pushes the message into `ErrorNotificationService`, then re-throws so components can still handle specific errors if needed.
- Added `src/app/error-banner/error-banner.component.ts` + `.html`: Bootstrap dismissible alert bound to `ErrorNotificationService.message`, mounted in `app.html` so it's visible across every route.
- `app.config.ts`: registered `errorInterceptor` alongside `csrfInterceptor` via `withInterceptors`.
- Closes the previously known gap where HTTP errors were handled per-component or silently swallowed.
- Removed the 4 now-redundant error-path `alert()` calls (`products.component.ts`, `suppliers.component.ts`, `orders.component.ts`) that would have doubled up with the new banner on the same failure. Success-path `alert()`s were left as-is — no replacement notification exists for those yet.

### [2026-09-21] — Implement AuthGuard on layout routes

- Added `src/app/auth.guard.ts`: `authGuard` (`CanActivateFn`) checks `AuthService.getUsernameLogged()` and redirects to `/login` if no session is present.
- `app.routes.ts`: applied `canActivate: [authGuard]` to the `layout` route so all child routes (dashboard, orders, products, customers, suppliers, employees) are protected in one place.
- Closes the previously known gap where any `/layout/*` URL was reachable without logging in.

### [2026-07-25] — Refactor frontend models to match PostgreSQL schema

- `employee.model.ts`: corrected field name casing to match Jackson serialization from the backend — `lastname`→`lastName`, `firstname`→`firstName`, `birthdate`→`birthDate`, `hiredate`→`hireDate`; removed unused `photo` field (not present in backend entity).
- `employees.component.ts`: updated `filteredEmployees` getter and `initializeNewEmployeeForm()` to use new camelCase field names.
- `employees.component.html`: updated all template bindings (`e.lastName`, `e.firstName`, `e.reportsTo.firstName`/`lastName`, `mgr.lastName`/`mgr.firstName`) and all `[(ngModel)]`/`name` attributes to match.
- `product.model.ts`: `discontinued` changed from `'Y' | 'N'` to `boolean`, matching the backend entity (`boolean discontinued`) after the DB column was changed from `integer` to `boolean` in the PostgreSQL migration.
- `products.component.ts`: default value for `discontinued` in `initializeNewProductForm()` changed from `'N'` to `false`.
- `products.component.html`: discontinued badge check `=== 'Y'` → truthy `p.discontinued`; checkbox binding updated accordingly.

### [2026-07-25] — Centralised API_BASE_URL via environment files

- Added `src/environments/environment.ts` (`apiBaseUrl: 'http://localhost:8080'`) and `environment.prod.ts` (`apiBaseUrl: ''`, same-origin).
- Added `API_BASE_URL` injection token: `src/app/api-base-url.token.ts`, provided in `src/app/app.config.ts` from `environment.apiBaseUrl`.
- `angular.json`: added `fileReplacements` to the `production` build configuration to swap in `environment.prod.ts`.
- Replaced hardcoded `http://localhost:8080` in every service (`auth`, `dashboard`, `orders`, `products`, `customers`, `suppliers`, `employees`, `categories`, `shippers`) and in `main-layout.component.ts` (logout call) with `inject(API_BASE_URL)`.

### [2026-06-22] — Proper HTTP methods + POST/PUT split

- All `DELETE` calls changed from `GET` to `DELETE` across every service.
- `/all` suffix dropped from `GET` list endpoints — now call the base resource path.
- `saveX()` methods split into `createX()` (POST to base) and `updateX()` (PUT to `/{id}`).
- Components now branch on `id ? update() : create()` instead of a single `save()` call.
- `orders.service.ts`: `createOrderDetail()` fixed to call `/details` (was `/details/save`).
- Affected files: all `src/app/service/*.service.ts`, all `src/app/layout/*/\*.component.ts`.

### [2026-06-12] — Initial status document created

- Project scanned and documented from scratch.
- **Angular version:** 21.2.0 (standalone components, no NgModules).
- **Completed sections:** Login, Main Layout, Dashboard, Orders, Products, Customers, Suppliers, Employees.
- All core CRUD modules implemented and functional at a UI level.
- Backend integration via HTTP to Spring Boot on `localhost:8080`.
- CSRF protection in place via functional interceptor.
- No route guards, no environment files, no tests — noted as known gaps.
- Employees component file open in IDE at session start — may be current work in progress.
