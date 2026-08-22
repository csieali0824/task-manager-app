# Task Manager

Full-stack task management application: view, create, edit, mark complete/incomplete, and
delete tasks.

## Tech stack

- **Backend**: Spring Boot 3.5.3 (Java 17), Spring Data JPA, H2 in-memory database
- **Frontend**: Vue 3 + TypeScript (`<script setup>`), Vite
- **Styling**: Tailwind CSS v4 (via `@tailwindcss/vite`)
- **Icons**: [lucide-vue-next](https://lucide.dev) — SVG icon components
- **API docs**: springdoc-openapi (OpenAPI 3 / Swagger UI, generated from the Spring controllers)

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use `./mvnw` if you prefer to add the wrapper)
- Node.js 20+ and npm

## Running locally

### Backend (port 8080)

```bash
mvn spring-boot:run
```

(Run from the repository root — the Maven project lives at the root, with the
Vue frontend in `frontend/`.)

The H2 database is in-memory and reset on every restart — no setup required.

### Frontend (port 5173)

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite dev server proxies `/api/*` to
`http://localhost:8080`, so no CORS configuration is needed in development.

## Frontend UI notes

### Icons instead of text labels

The per-task row actions use [lucide-vue-next](https://lucide.dev) icon components instead
of text buttons, so a long task list stays scannable:

| Action | Icon | Component |
|---------|------|------------------------------|
| Edit    | ✏️   | `<Pencil :size="16" />`      |
| Delete  | 🗑️   | `<Trash2 :size="16" />`      |

Icons are imported per component, so only the ones actually used end up in the bundle:

```vue
<script setup lang="ts">
import { Pencil, Trash2 } from 'lucide-vue-next'
</script>

<template>
  <button type="button" aria-label="編輯" title="編輯" @click="emit('edit', task)">
    <Pencil :size="16" />
  </button>
</template>
```

Two rules the icon buttons follow:

- **Every icon-only button carries an `aria-label` and a `title`** — the label is what screen
  readers announce, the title is the hover tooltip. Without them an icon button is unusable
  with assistive technology.
- **The form's primary actions keep their text** (`新增任務` / `儲存` / `取消`). Icons work for
  repeated row actions where context makes them obvious; a submit button should say what it
  does.

### Other UI behavior

- Edit and delete are gated on completion: acting on an unchecked task opens a custom
  `AlertDialog` instead of the browser's `window.alert`, so it matches the page styling.
- The task form uses `novalidate` with its own validation, replacing the browser's native
  "please fill out this field" bubble with an inline message that fits the design.

## Accessing the OpenAPI spec and domain model

With the backend running:

- **Swagger UI** (interactive, browsable): `http://localhost:8080/swagger-ui.html`
- **Raw OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **H2 console** (inspect the in-memory DB): `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, empty password)

### Domain model

A single `Task` entity (`src/main/java/com/taskmanager/entity/Task.java`):

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
- Redesign the frontend UI with Tailwind CSS, add completion-gated edit/delete behavior, and
  restructure the repository so the Maven project lives at the root.
- Add unit and integration tests, and fix the build so the integration tests actually run.

Code sections generated with AI assistance are marked `// [AI assisted <nnn>]`, where `<nnn>`
refers to the corresponding conversation record in [`chat-records/`](chat-records/):

- [`001.chat`](chat-records/001.chat) — initial full-stack scaffolding and CRUD implementation
- [`002.chat`](chat-records/002.chat) — Tailwind UI redesign, edit/delete guard, project
  restructure (Maven project moved to repo root)
- [`003.chat`](chat-records/003.chat) — light "paper" UI restyle, icon action buttons,
  custom alert dialog and form validation, dev-tools cleanup
- [`004.chat`](chat-records/004.chat) — unit and integration tests for the service and error
  handling, plus a maven-failsafe-plugin fix so `mvn verify` actually runs them
