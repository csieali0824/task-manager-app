# Task Manager

一個全端的任務管理程式，可以列出任務、新增、編輯、標記完成或未完成，以及刪除任務。

English version: [README.md](README.md)

## 技術組成

- 後端：Spring Boot 3.5.3（Java 17）、Spring Data JPA、H2 in-memory 資料庫
- 前端：Vue 3 + TypeScript（`<script setup>`）、Vite
- 樣式：Tailwind CSS v4，透過 `@tailwindcss/vite`
- Icon：[lucide-vue-next](https://lucide.dev) 的 SVG icon 元件
- API 文件：springdoc-openapi（OpenAPI 3 與 Swagger UI，由 Spring controller 自動產生）

## 環境需求

- JDK 17 以上
- Maven 3.9 以上（或自己補上 wrapper，改用 `./mvnw`）
- Node.js 20 以上，含 npm

## 在本機執行

### 後端（port 8080）

```bash
mvn spring-boot:run
```

請在 repository 根目錄執行。Maven 專案放在根目錄，Vue 前端放在 `frontend/`。

H2 資料庫跑在記憶體裡，每次重啟都會清空，所以不需要任何前置設定。

### 前端（port 5173）

```bash
cd frontend
npm install
npm run dev
```

打開 `http://localhost:5173`。Vite dev server 會把 `/api/*` proxy 到
`http://localhost:8080`，所以開發時不必處理 CORS 設定。

## 前端 UI 說明

### 用 icon 取代文字標籤

每一列任務的操作按鈕用 [lucide-vue-next](https://lucide.dev) 的 icon 元件，而不是文字
按鈕，任務一多的時候清單比較好掃。

| 操作 | 元件                    |
|------|-------------------------|
| 編輯 | `<Pencil :size="16" />` |
| 刪除 | `<Trash2 :size="16" />` |

Icon 是各元件各自 import，所以只有真的用到的會被打包進去：

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

Icon 按鈕遵守兩條規則。第一，只有 icon 沒有文字的按鈕一定要帶 `aria-label` 和 `title`：
`aria-label` 是螢幕閱讀器會唸出來的內容，`title` 是滑鼠停留時的提示，少了這兩個，輔助
科技就沒辦法操作這顆按鈕。第二，表單的主要按鈕保留文字（`新增任務` / `儲存` / `取消`）。
Icon 適合每列重複出現、情境本身就看得懂的操作，但送出按鈕應該直接寫清楚它會做什麼。

### 其他 UI 行為

- 刪除前會先確認。確認視窗是自製的 `AlertDialog`（confirm 模式），而不是瀏覽器原生的
  `window.confirm`，樣式因此和頁面一致；按下「確定」之後才會真的刪除。
- 任務表單用 `novalidate` 搭配自製驗證，顯示行內訊息，取代瀏覽器原生的「請填寫這個欄位」
  泡泡提示。

## 查看 OpenAPI spec 與 domain model

後端啟動後：

- Swagger UI（可互動）：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON 原始檔：`http://localhost:8080/v3/api-docs`
- H2 console，用來查看記憶體資料庫：`http://localhost:8080/h2-console`
  （JDBC URL `jdbc:h2:mem:taskdb`，帳號 `sa`，密碼空白）

### Domain model

只有一個 `Task` entity（`src/main/java/com/taskmanager/entity/Task.java`）：

| 欄位          | 型別    | 說明                 |
|---------------|---------|----------------------|
| `id`          | Long    | 自動產生的主鍵       |
| `title`       | String  | 必填，不可空白       |
| `description` | String  | 選填                 |
| `completed`   | boolean | 預設 `false`         |
| `createdAt`   | Instant | 新增時寫入           |
| `updatedAt`   | Instant | 每次更新都會刷新     |

### REST API

| Method | 路徑                         | 說明                                     |
|--------|------------------------------|------------------------------------------|
| GET    | `/api/tasks`                 | 列出所有任務                             |
| GET    | `/api/tasks/{id}`            | 取得單一任務                             |
| POST   | `/api/tasks`                 | 新增任務                                 |
| PUT    | `/api/tasks/{id}`            | 整筆替換任務，所有欄位都要帶             |
| PATCH  | `/api/tasks/{id}`            | 部分更新，格式為 JSON Merge Patch（RFC 7396）。只送要改的欄位，Content-Type 是 `application/merge-patch+json` |
| DELETE | `/api/tasks/{id}`            | 刪除任務                                 |

標記完成就是只帶一個欄位的 merge patch：

```bash
curl -X PATCH http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/merge-patch+json' \
  -d '{"completed": true}'
```

沒寫進文件的欄位維持原值。`"description": null` 會把描述清空。未知欄位、`"completed": null`，
以及不是 JSON 物件的文件都會回 400；用一般的 `application/json` 送過來會回 415。

## 執行測試

後端：

```bash
mvn verify
```

前端：

```bash
cd frontend
npm run type-check
npm run build
```

## AI 使用揭露

這個專案是借助 Claude Code 完成的。AI 做的事情如下：

- 產生 Spring Boot 後端與 Vue 3 + TypeScript 前端的專案骨架。
- 依需求功能的口語描述產生 CRUD 樣板程式碼（entity、repository、service、controller、
  DTO、API client 與 Vue 元件）。
- 診斷並修掉 springdoc-openapi 與 Spring Boot 之間的相容問題。
- 接好 OpenAPI 文件，並做端對端驗證。
- 用 Tailwind CSS 重做前端 UI、加上編輯與刪除的完成狀態限制，並把 Maven 專案搬到
  repository 根目錄。
- 補上 unit test 與 integration test，並修掉建置設定，讓 integration test 真的會被執行。

借助 AI 寫出來的程式碼會標上 `// [AI assisted <nnn>]`，`<nnn>` 對應
[`chat-records/`](chat-records/) 裡的對話紀錄：

- [`001.chat`](chat-records/001.chat)：最初的全端骨架與 CRUD 實作
- [`002.chat`](chat-records/002.chat)：Tailwind UI 重做、編輯與刪除的限制、把 Maven 專案
  搬到 repo 根目錄的結構調整
- [`003.chat`](chat-records/003.chat)：改成淺色紙感風格、icon 操作按鈕、自製提醒視窗與
  表單驗證、移除開發者工具
- [`004.chat`](chat-records/004.chat)：service 與錯誤處理的 unit test 和 integration
  test，以及讓 `mvn verify` 真的會跑測試的 maven-failsafe-plugin 修正
