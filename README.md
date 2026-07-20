# ERP Replica
This project is a full stack web application that replicates an ERP system: manage login based on roles, handle orders, products life cycle and users creation/access. It's built using Spring Boot and Angular. The database is PostgreSQL, running in Docker, loaded with the Northwind schema.
 
# Tech Stack

| Area | Layer | Technology | Version |
|---|---|---|---|
| Backend | Language | Java | 21 |
| Backend | Framework | Spring Boot | 4.0.5 |
| Backend | Web | Spring Web MVC | — |
| Backend | Persistence | Spring Data JPA + Hibernate | — |
| Backend | Security | Spring Security | — |
| Backend | Testing | JUnit 5 + Mockito | — |
| Frontend | Framework | Angular | 21.2.0 |
| Frontend | Language | TypeScript | ~5.9.2 |
| Frontend | UI Framework | Bootstrap | 5.3.8 |
| Frontend | Icons | Bootstrap Icons | 1.13.1 |
| Frontend | Charts | Chart.js | 4.5.1 |
| Frontend | Reactive | RxJS | ~7.8.0 |
| Frontend | Testing | Vitest + jsdom | 4.0.8 |
| Frontend | Formatter | Prettier | 3.5.3 |
| Frontend | Build | Angular CLI / @angular/build | 21.2.11 |
| Infrastructure | Database | PostgreSQL | 16 |
| Infrastructure | Containerization | Docker + Docker Compose | — |

# Architecture overview

The Angular frontend communicates with the Spring Boot backend via REST API, using `/api/auth/**` for authentication and `/api/v1/**` for all other resources. On login, the backend verifies the user's credentials and role against the database through Spring Security. The database is PostgreSQL 16, running in a Docker container and accessed by the backend via JDBC at `localhost:5432`. Data is currently returned directly as JPA entities; DTOs will be introduced where needed in a future iteration.

# Features

## Dashboard

The Dashboard is a simple recap of current situation: total revenue, current month revenue, last 10 orders, products with low stock (less than 10).

## Orders / Products / Suppliers / Customers

A regular user can see all these tabs and can manage the life cycle of orders, products/categories, suppliers/shippers and customers. For all of these elements the operations allowed are create, modify and delete.

## Employees

This section is only accessible to managers. Here the manager logged in can create, modify and delete an employee.

# Getting Started

## Prerequisites

Make sure you have the following installed before running the project:

- **Java 21** — verify with `java --version`
- **Node.js + npm** — verify with `node -v` and `npm -v`
- **Angular CLI** — verify with `ng version`, or install with `npm install -g @angular/cli`
- **Docker Desktop** — [download here](https://docs.docker.com/desktop/)
- **A SQL editor** (optional but recommended) — [DBeaver](https://dbeaver.io/download/)

## Start the database

From the project root, start the PostgreSQL container:

```bash
docker compose up -d
```

Then connect to the database with DBeaver (or any SQL editor) using these credentials:

| Field | Value |
|---|---|
| Host | localhost |
| Port | 5432 |
| Database | northwind |
| Username | erp_user |
| Password | erp_password |

Run the setup script to create the schema and load the data: `northwind_setup.sql`.

## Run the backend

Open the project in IntelliJ and click the Run button, or from the terminal:

```bash
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`.

## Run the frontend

From the `angular-erp` folder:

```bash
npm install
npm start
```

The frontend will be available at `http://localhost:4200`.

## Log in

Open `http://localhost:4200` in your browser and log in with one of the test accounts:

| Username | Password | Role |
|---|---|---|
| test.manager | password123 | Manager |
| test.employee | password123 | Employee |

# Project Status

All core CRUD modules are implemented and functional. There are backend and frontend improvements in progress, which do not affect the core functionality.

## Backend future updates

- Add DTOs for safer data transfer between frontend and backend
- Add custom exception handlers
- Refactor API endpoints to follow REST conventions
- Add integration tests
- Add Swagger/OpenAPI documentation
- Apply ServiceImpl pattern
- Move sensitive config to environment variables

## Frontend future updates

- Implement AuthGuard
- Add unit tests
- Improve UI and UX with loading spinners and custom windows alert
- Add wildcard route and 404 component
