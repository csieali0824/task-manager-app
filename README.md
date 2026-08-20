# Task Manager

Full-stack task management application: view, create, edit, mark complete/incomplete, and
delete tasks.

## Tech stack

- **Backend**: Spring Boot 3.5.3 (Java 17), Spring Data JPA, H2 in-memory database
- **Frontend**: Vue 3 + TypeScript (`<script setup>`), Vite
- **API docs**: springdoc-openapi (OpenAPI 3 / Swagger UI, generated from the Spring controllers)

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use `./mvnw` if you prefer to add the wrapper)
- Node.js 20+ and npm

## Running locally

### Backend (port 8082)

```bash
cd backend
mvn spring-boot:run
```

The H2 database is in-memory and reset on every restart — no setup required.

### Frontend (port 5173)

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite dev server proxies `/api/*` to
`http://localhost:8082`, so no CORS configuration is needed in development.

## Accessing the OpenAPI spec and domain model

With the backend running:

- **Swagger UI** (interactive, browsable): `http://localhost:8082/swagger-ui.html`
- **Raw OpenAPI JSON**: `http://localhost:8082/v3/api-docs`
- **H2 console** (inspect the in-memory DB): `http://localhost:8082/h2-console`
  (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, empty password)

### Domain model

A single `Task` entity (`backend/src/main/java/com/taskmanager/entity/Task.java`):

| Field         | Type      | Notes                                   |
|---------------|-----------|------------------------------------------|
| `id`          | Long      | Auto-generated primary key               |
| `title`       | String    | Required, non-blank                      |
| `description` | String    | Optional                                 |
| `completed`   | boolean   | Defaults to `false`                      |
| `createdAt`   | Instant   | Set on insert                            |
| `updatedAt`   | Instant   | Refreshed on every update                |

### REST API

| Method | Path                        | Description                     |
|--------|------------------------------|----------------------------------|
| GET    | `/api/tasks`                 | List all tasks                  |
| GET    | `/api/tasks/{id}`             | Get a single task               |
| POST   | `/api/tasks`                  | Create a task                   |
| PUT    | `/api/tasks/{id}`              | Update a task's title/description/completed |
| PATCH  | `/api/tasks/{id}/complete`     | Mark a task as completed        |
| PATCH  | `/api/tasks/{id}/incomplete`   | Mark a task as not completed    |
| DELETE | `/api/tasks/{id}`              | Delete a task                   |

## Running tests

```bash
cd backend
mvn verify
```

```bash
cd frontend
npm run type-check
npm run build
```

## AI usage disclosure

This project was built with AI assistance (Claude Code). AI was used to:

- Scaffold the Spring Boot backend and Vue 3 + TypeScript frontend project structure.
- Generate CRUD boilerplate (entity/repository/service/controller/DTOs, API client and Vue
  components) from a plain-language description of the required features.
- Diagnose and fix a dependency-compatibility issue between springdoc-openapi and Spring Boot.
- Wire up and verify the OpenAPI documentation end-to-end.

Code sections generated with AI assistance are marked `// [AI assisted 001]`. The corresponding
conversation record is in [`chat-records/001.chat`](chat-records/001.chat).
