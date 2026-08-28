# Task Manager

A full-stack task manager. You can list tasks, create them, edit them, mark them complete
or incomplete, and delete them.

繁體中文版：[README.zh-TW.md](README.zh-TW.md)

## Tech stack

- Backend: Spring Boot 3.5.3 (Java 17), Spring Data JPA, H2 in-memory database
- Frontend: Vue 3 + TypeScript (`<script setup>`), Vite
- Styling: Tailwind CSS v4, via `@tailwindcss/vite`
- Icons: [lucide-vue-next](https://lucide.dev), SVG icon components
- API docs: springdoc-openapi (OpenAPI 3 and Swagger UI, generated from the Spring controllers)

## Prerequisites

- JDK 17+
- Maven 3.9+ (or add the wrapper and use `./mvnw`)
- Node.js 20+ and npm

## Running locally

### Backend (port 8080)

```bash
mvn spring-boot:run
```

Run this from the repository root. The Maven project lives at the root, and the Vue
frontend is in `frontend/`.

The H2 database runs in memory and resets on every restart, so there is nothing to set up.

### Frontend (port 5173)

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The Vite dev server proxies `/api/*` to
`http://localhost:8080`, so you do not need to configure CORS during development.

## Frontend UI notes

### Icons instead of text labels

The per-task row actions use [lucide-vue-next](https://lucide.dev) icon components rather
than text buttons, which keeps a long task list scannable.

| Action | Component               |
|--------|-------------------------|
| Edit   | `<Pencil :size="16" />` |
| Delete | `<Trash2 :size="16" />` |

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

The icon buttons follow two rules. Every icon-only button carries an `aria-label` and a
`title`: the label is what screen readers announce, the title is the hover tooltip, and
without them the button is unusable with assistive technology. The form's primary actions
keep their text (`新增任務` / `儲存` / `取消`). Icons work for repeated row actions where
the context makes them obvious, but a submit button should say what it does.

### Other UI behavior

- Delete asks for confirmation first. The prompt is a custom `AlertDialog` in confirm mode
  rather than the browser's `window.confirm`, so it matches the page styling; the task is only
  removed after 確定 is pressed.
- The task form uses `novalidate` with its own validation, which shows an inline message
  instead of the browser's native "please fill out this field" bubble.

## Accessing the OpenAPI spec and domain model

With the backend running:

- Swagger UI (interactive): `http://localhost:8080/swagger-ui.html`
- Raw OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console, for inspecting the in-memory database: `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, empty password)

### Domain model

A single `Task` entity (`src/main/java/com/taskmanager/entity/Task.java`):

| Field         | Type    | Notes                       |
|---------------|---------|-----------------------------|
| `id`          | Long    | Auto-generated primary key  |
| `title`       | String  | Required, non-blank         |
| `description` | String  | Optional                    |
| `completed`   | boolean | Defaults to `false`         |
| `createdAt`   | Instant | Set on insert               |
| `updatedAt`   | Instant | Refreshed on every update   |

### REST API

| Method | Path                         | Description                                 |
|--------|------------------------------|---------------------------------------------|
| GET    | `/api/tasks`                 | List all tasks                              |
| GET    | `/api/tasks/{id}`            | Get a single task                           |
| POST   | `/api/tasks`                 | Create a task                               |
| PUT    | `/api/tasks/{id}`            | Replace a task; all fields required         |
| PATCH  | `/api/tasks/{id}`            | Partial update as JSON Merge Patch (RFC 7396). Send only the fields to change; body is `application/merge-patch+json` |
| DELETE | `/api/tasks/{id}`            | Delete a task                               |

Marking a task complete is a merge patch with one field:

```bash
curl -X PATCH http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/merge-patch+json' \
  -d '{"completed": true}'
```

Fields left out of the document keep their current value. `"description": null` clears the
description. Unknown fields, `"completed": null`, and a document that is not a JSON object are
rejected with 400; a body sent as plain `application/json` is rejected with 415.

## Running tests

Backend:

```bash
mvn verify
```

Frontend:

```bash
cd frontend
npm run type-check
npm run build
```

## AI usage disclosure

This project was built with help from Claude Code. The AI did the following:

- Scaffolded the Spring Boot backend and the Vue 3 + TypeScript frontend structure.
- Generated CRUD boilerplate (entity, repository, service, controller, DTOs, API client and
  Vue components) from a plain-language description of the required features.
- Diagnosed and fixed a compatibility problem between springdoc-openapi and Spring Boot.
- Wired up the OpenAPI documentation and verified it end to end.
- Redesigned the frontend UI with Tailwind CSS, added the completion gate on edit and delete,
  and moved the Maven project to the repository root.
- Added unit and integration tests, and fixed the build so the integration tests actually run.

Code written with AI assistance is marked `// [AI assisted <nnn>]`, where `<nnn>` points at
the matching conversation record in [`chat-records/`](chat-records/):

- [`001.chat`](chat-records/001.chat): initial full-stack scaffolding and the CRUD implementation
- [`002.chat`](chat-records/002.chat): Tailwind UI redesign, the edit/delete guard, and the
  restructure that moved the Maven project to the repo root
- [`003.chat`](chat-records/003.chat): light "paper" UI restyle, icon action buttons, custom
  alert dialog and form validation, dev-tools cleanup
- [`004.chat`](chat-records/004.chat): unit and integration tests for the service and error
  handling, plus the maven-failsafe-plugin fix so `mvn verify` actually runs them
