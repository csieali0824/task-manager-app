# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A single-resource task manager: Spring Boot 3.5.3 (Java 17, JPA, H2 in-memory) backend at the
repo root, Vue 3.5 + TypeScript + Tailwind v4 frontend in `frontend/`. One entity (`Task`), one
controller, five components. UI text is Traditional Chinese.

## Commands

Backend (run from repo root; the Maven project lives here, not in a subfolder):

```bash
mvn spring-boot:run                                  # port 8080
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081   # when 8080 is taken
mvn clean verify                                     # unit + integration tests
mvn -Dtest=TaskServiceTest test                      # one unit test class (surefire)
mvn -Dit.test=TaskControllerIT verify                # one integration test class (failsafe uses it.test, not test)
```

Frontend (`cd frontend`):

```bash
npm run dev          # Vite on 5173, proxies /api/* to localhost:8080
npm run build        # runs type-check and vite build in parallel; treat as the CI check
npm run type-check   # vue-tsc --build
```

There is no lint or formatter configured (no eslint, no prettier). Node 22.18+ or 24.12+.

Test class naming is load-bearing: `*Test` runs under surefire in `test`, `*IT` runs under
failsafe in `integration-test`/`verify`. Failsafe is bound explicitly in `pom.xml`; before that
binding existed, `mvn verify` reported BUILD SUCCESS while running zero tests.

## Local environment gotchas

- A long-running IDE instance often holds port 8080 with stale code. Before smoke-testing a
  backend change, check `netstat -ano | grep :8080`; run on 8081 rather than killing it.
- Git Bash on Windows mangles non-ASCII in `curl -d`. Use ASCII payloads in shell smoke tests.
- `docs/` is excluded via `.git/info/exclude`, not `.gitignore`. It holds local HTML notes that
  are published as Artifacts (Vue 3 notes:
  https://claude.ai/code/artifact/4f4f7d8b-dd9b-4e94-906f-6d0b5f5df25f). Never commit it.
- H2 is in-memory and resets on every backend restart while the browser keeps its old list.
  `App.vue`'s `refresh()` reconciles `editingTask` against the reloaded list for this reason.

## Backend architecture

Controller → Service → Repository, with records as DTOs. The parts that need more than one file
to understand:

**Error contract.** `ApiExceptionHandler` turns every handled exception into
`{timestamp, status, error, message}`. It deliberately omits `path`, which Spring Boot's default
error body always includes; integration tests assert `doesNotContain("path")` as proof the
handler ran, and the frontend `handle()` in `api/tasks.ts` reads `message`. Any new exception
type needs a handler there or it surfaces as a 500 with the wrong shape.

**PATCH is JSON Merge Patch (RFC 7396), not action endpoints.** `PATCH /api/tasks/{id}`
consumes only `application/merge-patch+json`; plain `application/json` gets 415. The controller
takes `JsonNode`, not a DTO, because a DTO cannot distinguish "field absent" from "field null".
`TaskService.merge` overlays the patch onto `TaskRequest.from(task)` with `ObjectNode.setAll`,
then deserializes with `FAIL_ON_UNKNOWN_PROPERTIES` and `FAIL_ON_NULL_FOR_PRIMITIVES` so unknown
fields and `"completed": null` are 400s. Validation runs on the merged result via the injected
`Validator` (not `@Valid`), so failures arrive as `ConstraintViolationException`. Do not
reintroduce `/{id}/complete`-style endpoints.

`TaskService` takes `(TaskRepository, ObjectMapper, Validator)`; `TaskServiceTest` constructs it
directly with a real `ObjectMapper` and `Validation.buildDefaultValidatorFactory()`, no Spring
context.

## Frontend architecture

`App.vue` owns all state (`tasks`, `editingTask`, `errorMessage`, `pendingDelete`) and every
API call. The former "unchecked tasks cannot be edited or deleted" rule was removed on
2026-08-29; do not reintroduce it. Children receive props and emit; nothing else holds state. No Pinia, no router.

- `AlertDialog` uses the `message` string as its open flag: empty string means closed. `mode`
  is `'alert' | 'confirm'`; it emits `close` (dismissed without acting) and `confirm`. Delete
  goes through a confirm-mode instance driven by `pendingDelete`. It is wrapped in
  `<Teleport to="body">` so the fixed overlay escapes ancestor `overflow`/`transform`.
- Vue `<Transition>`/`<TransitionGroup>` were removed on purpose (imperceptible 4px/200ms
  animations behind a network round trip). Hover and checkbox feedback are Tailwind
  `transition-colors` utilities. Do not re-add Vue transitions.
- `BaseButton` is a variant map (`Record<Variant, string>`) plus base classes, content via slot.
  It declares no emits, so `@click` and `autofocus` reach the native `<button>` as fallthrough
  attributes. `type` defaults to `'button'` because the native default is `submit`.
- `TaskForm` copies the read-only `editingTask` prop into a local `reactive` form via `watch`
  and emits `submit` with a copy; it never mutates the prop.
- Styling is Tailwind utility classes only; components have no `<style>` blocks. If one is ever
  needed with `@apply`, it must start with `@reference '@/assets/main.css'` (Tailwind v4).
- Icon-only buttons (lucide-vue-next) carry both `aria-label` and `title`. Form primary actions
  keep text labels.

## Documentation conventions

- `README.md` and `README.zh-TW.md` are translations of each other; change both, especially the
  REST API table.
- Prose in READMEs and chat records follows a plain style: no em dashes, no bolded inline list
  headers, no emoji, active voice.
- `chat-records/001–004.chat` are historical AI-usage disclosures. **Do not add new records**
  and do not add new `// [AI assisted nnn]` markers; existing markers stay as they are.

## Git

Commits go directly on `main` (single-author repo). English imperative subject, body explains
why. Do not push unless asked.
