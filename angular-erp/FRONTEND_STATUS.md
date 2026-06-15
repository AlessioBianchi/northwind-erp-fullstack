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
**Backend base URL:** `http://localhost:8080` (hardcoded in services)

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
│       ├── app.config.ts                # HttpClient, CSRF, router providers
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

**Base URL:** `http://localhost:8080` — hardcoded in each service file.  
**HTTP client config (`app.config.ts`):**
- `withFetch()` — uses browser Fetch API.
- `withXsrfConfiguration({ cookieName: 'XSRF-TOKEN', headerName: 'X-XSRF-TOKEN' })`.
- `withCredentials: true` on all requests (session cookie passed to backend).
- `csrfInterceptor` — functional interceptor that reads the XSRF cookie and injects the header on non-GET requests.

### Endpoint map

| Feature | Method | Endpoint |
|---|---|---|
| Auth | POST | `/api/auth/login` |
| Dashboard | GET | `/api/dashboard/stats` |
| Orders | GET | `/api/orders/paginated` |
| Orders | POST | `/api/orders/save` |
| Orders | DELETE | `/api/orders/delete/{id}` |
| Order Details | GET | `/api/orders/details/{orderId}` |
| Order Details | POST | `/api/orders/details/save` |
| Order Details | DELETE | `/api/orders/details/delete` (body) |
| Products | GET | `/products/all`, `/products/paginated` |
| Products | POST | `/products/save` |
| Products | GET | `/products/delete/{id}` |
| Categories | GET | `/categories/all` |
| Categories | POST | `/categories/save` |
| Categories | GET | `/categories/delete/{id}` |
| Customers | GET | `/customers/all`, `/customers/paginated` |
| Customers | POST | `/customers/save` |
| Customers | GET | `/customers/delete/{id}` |
| Suppliers | GET | `/suppliers/all`, `/suppliers/paginated` |
| Suppliers | POST | `/suppliers/save` |
| Suppliers | GET | `/suppliers/delete/{id}` |
| Shippers | GET | `/shippers/all` |
| Shippers | POST | `/shippers/save` |
| Shippers | GET | `/shippers/delete/{id}` |
| Employees | GET | `/employees/all`, `/employees/paginated` |
| Employees | POST | `/employees/save` |
| Employees | GET | `/employees/delete/{id}` |

> Note: Products/Categories/Customers/Suppliers/Employees endpoints lack the `/api/` prefix. Orders and Dashboard use `/api/`. Verify with backend team if this is intentional.

---

## Auth & Session

| Aspect | Implementation |
|---|---|
| Login | POST with `application/x-www-form-urlencoded` body |
| Session storage | `sessionStorage`: keys `username` and `isManager` |
| Role check | `isManager === 'true'` string comparison |
| Route guards | **None implemented** — UI hides links but routes are unprotected |
| Logout | Clears sessionStorage, navigates to `/login` (no server-side call) |
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

- [ ] **No route guards** — any URL is accessible without login if sessionStorage is manipulated.
- [ ] **No environment files** — backend URL (`http://localhost:8080`) is hardcoded in services. Needs `environment.ts` / `environment.prod.ts`.
- [ ] **No wildcard/404 route** — accessing unknown URLs produces a blank page.
- [ ] **Subscription cleanup** — components do not unsubscribe from Observables on destroy (no `takeUntilDestroyed`, no `unsubscribe`).
- [ ] **No loading spinners** — no visual feedback during HTTP requests.
- [ ] **No global error handling** — HTTP errors are handled per-component (or not at all).
- [ ] **Delete uses GET** on products, customers, suppliers, employees — should be DELETE method.
- [ ] **Logout is client-side only** — server session is not invalidated.
- [ ] **No tests written** — Vitest is configured but no spec files exist yet.

---

## Changelog

> Format for new entries:
> ```
> ### [YYYY-MM-DD] — Short title
> - What was added / changed / fixed
> - Include component/service/file paths for precision
> ```

---

### [2026-06-12] — Initial status document created

- Project scanned and documented from scratch.
- **Angular version:** 21.2.0 (standalone components, no NgModules).
- **Completed sections:** Login, Main Layout, Dashboard, Orders, Products, Customers, Suppliers, Employees.
- All core CRUD modules implemented and functional at a UI level.
- Backend integration via HTTP to Spring Boot on `localhost:8080`.
- CSRF protection in place via functional interceptor.
- No route guards, no environment files, no tests — noted as known gaps.
- Employees component file open in IDE at session start — may be current work in progress.
