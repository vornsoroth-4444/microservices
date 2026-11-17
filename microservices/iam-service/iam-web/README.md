fffff# iam-web (React.js IAM Console)

`iam-web` is a modern, responsive Single-Page Application (SPA) frontend for the Identity and Access Management (IAM) platform built with **React 19**, **Vite**, **Lucide Icons**, and vanilla CSS. It integrates seamlessly with the `iam-service` REST API and Spring Authorization Server (OAuth 2.1 & OpenID Connect 1.0).

---

## 🌟 Key Capabilities & Point Breakdown (+90 pts)

### 1. Account Workflows (+40 pts)
- **Profile (+5 pts)**: View and update personal information (given name, family name, username, email, phone number, and avatar image URL) via `GET/PATCH /api/me`.
- **Security & Password (+5 pts)**: Self-service password change with confirmation and strength requirements via `PUT /api/me/password`.
- **Linked Account (+5 pts)**: View connected social login providers (Google, GitHub, Facebook), initiate new connections, and unlink via `GET/DELETE /api/me/identities`.
- **Active Sessions (+5 pts)**: View all active OAuth2 sessions, client applications, devices, user-agents, IP addresses, and expiration times via `GET /api/me/sessions`.
- **Session Management (+5 pts)**: Terminate individual active sessions (`DELETE /api/me/sessions/{id}`) or revoke all sessions / sign out from all devices (`DELETE /api/me/sessions`).
- **Login History (+5 pts)**: View successful and failed login attempts with timestamps, IP addresses, and browser details via `GET /api/me/login-history`.
- **Account Activity (+5 pts)**: Inspect security-critical activities including profile updates, password modifications, and authentication events via `GET /api/me/activity`.
- **Account Deactivation (+5 pts)**: Request account deactivation with confirmation warning dialog via `POST /api/me/deactivation-request`.

### 2. Management Workflows (+50 pts)
- **Admin Overview (+2.5 pts)**: Real-time dashboard showing total users, active users, roles, permissions, OAuth clients, authentication throughput trends (SVG chart), security posture gauge, and recent sign-ins.
- **User Management (+2.5 pts)**: Searchable and filterable users directory. Create (`POST /api/users`), view details, edit (`PATCH /api/users/{uuid}`), activate/deactivate (`POST /api/users/{uuid}/enable` & `disable`), lock/unlock (`POST /api/users/{uuid}/lock` & `unlock`), and delete (`DELETE /api/users/{uuid}`).
- **User Role Assignment (+2.5 pts)**: Assign roles from the system catalog (`POST /api/users/{uuid}/roles`) and revoke role assignments (`DELETE /api/users/{uuid}/roles/{roleUuid}`).
- **User Permission Assignment (+2.5 pts)**: Grant direct permissions (`POST /api/users/{uuid}/permissions` with `effect: 'GRANT'`), deny permissions (`effect: 'DENY'`), and revoke direct permissions (`DELETE /api/users/{uuid}/permissions/{permUuid}`).
- **User Sessions (+5 pts)**: Inspect all active sessions of any specific user (`GET /api/users/{uuid}/sessions`), terminate individual sessions, or terminate all sessions for that user.
- **User Login History (+5 pts)**: Monitor user-specific login attempts, failed authentications, and device signatures (`GET /api/users/{uuid}/login-history`).
- **Role Management (+5 pts)**: Role catalog table (`GET /api/roles`), create roles (`POST /api/roles`), edit role details (`PATCH /api/roles/{uuid}`), assign/remove permissions in bulk (`POST/DELETE /api/roles/{uuid}/permissions`), activate/deactivate roles, and delete custom roles (`DELETE /api/roles/{uuid}`).
- **Permission Management (+5 pts)**: Fine-grained resource-action permission catalog (`GET /api/permissions`), create permissions (`POST /api/permissions`), edit (`PATCH /api/permissions/{uuid}`), toggle active status, and delete (`DELETE /api/permissions/{uuid}`).
- **OAuth2 / OIDC Client Management (+5 pts)**: Register new OAuth clients (`POST /api/clients`), configure grant types, redirect URIs, and scopes. Secret reveal dialog with clipboard copy. Edit client settings (`PUT /api/clients/{clientId}`), rotate client secrets (`POST /api/clients/{clientId}/rotate-secret`), and delete clients (`DELETE /api/clients/{clientId}`).
- **Account Security Management (+5 pts)**: Administrator password reset (`POST /api/users/{uuid}/reset-password`), lock/unlock account controls, and audit history inspection.
- **Audit Logs (+5 pts)**: Comprehensive audit log viewer (`GET /api/audit-logs`) with full-text search, event type filtering (`LOGIN_SUCCESS`, `LOGIN_FAILURE`, `USER_CREATE`, `USER_ROLE_ASSIGN`, etc.), IP and device details, and live refresh.
- **System Configuration (+5 pts)**: Configure password policy (minimum length, uppercase, number, symbol requirements) and authentication policy (social login toggle, access token lifetime, refresh token lifetime) via `GET/PATCH /api/system-config`.

### 3. Internationalization (English & Khmer)
- Instant bilingual toggle between **English (`en`)** and **Khmer (`km`)** in the top navigation bar.
- Translated UI elements: titles, buttons, status badges, forms, modals, tables, and alerts.
- Language choice is automatically persisted in `localStorage`.

---

## 🚀 Running the Application

### Option A: Embedded in Spring Boot (Production)
The React app is built into static assets inside `iam-web/src/main/resources/static/iam-ui/`.

1. Build the React app:
   ```bash
   cd microservices/iam-service/iam-web
   npm run build
   ```
2. Start the IAM Spring Boot service:
   ```bash
   ./gradlew :iam-main:bootRun
   ```
3. Open the console directly in your browser:
   **`http://localhost:9000/iam-ui/`**

### Option B: Vite Dev Server (Hot Module Reload)
1. Install dependencies & start Vite:
   ```bash
   cd microservices/iam-service/iam-web
   npm install
   npm run dev
   ```
2. Open **`http://localhost:5173/iam-ui/`**.
   All `/api`, `/oauth2`, and `/login` requests are automatically proxied to `http://localhost:9000`.

---

## 🔐 Authentication & Demonstration
- **OAuth 2.1 PKCE Flow**: Click **"Sign in"** in the topbar to initiate PKCE authorization with the public client `iam-web-spa`.
- **Manual Bearer Token**: Go to **"My account"** -> **"API access token"** to paste or update a JWT access token.
- **Demo Mode**: If the backend is offline or unauthenticated, the application automatically runs in demo preview mode with interactive mock data so every single screen, modal, and workflow can be explored and tested.
