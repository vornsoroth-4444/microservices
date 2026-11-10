# IAM Service (Identity and Access Management)

An enterprise-grade Identity and Access Management (IAM) service engineered with **Spring Authorization Server (OAuth 2.1 & OpenID Connect 1.0)**, **Spring Boot 4 / Spring 7**, **Java 25**, and **Clean / Hexagonal Architecture (Domain-Driven Design)**.

---

## Architecture & Module Structure

The project strictly follows Hexagonal Architecture and unidirectional dependency rules. Inner domain modules remain independent of Spring, JPA, Hibernate, and Web technologies.

```
iam-service/
├── iam-domain/
│   ├── iam-domain-core/            # Pure Java domain models, value objects, exceptions (Plain Java, zero dependencies)
│   └── iam-application-service/    # Use cases (1 class = 1 use case), command/query records, and port interfaces
├── iam-persistence/                # JPA entities, Spring Data repositories, and persistence port adapters
├── iam-infrastructure/             # Spring Authorization Server, JDBC services, Token customizer, DCR, Social login
├── iam-rest-api/                   # REST controllers, DTOs, RFC 9457 Problem Details, API security
├── iam-web/                        # JTE (Java Template Engine) pages, web controllers, English & Khmer i18n
└── iam-main/                       # Spring Boot application wiring, Liquibase changelogs, database seeder
```

### Module Dependency Matrix

| Module | Responsibility | Dependencies |
| :--- | :--- | :--- |
| `iam-domain-core` | Pure domain entities (`User`, `Role`, `Permission`, `Identity`, assignments) and business rules. Plain Java only. | *None* |
| `iam-application-service`| Orchestrates use cases (39 distinct classes) and output ports. | `iam-domain-core` |
| `iam-persistence` | Spring Data JPA implementations, explicit join entities, soft delete & audit mapping. | `iam-application-service` |
| `iam-infrastructure` | Spring Authorization Server, JDBC OAuth2 services, OIDC claims injection, DCR (RFC 7591), social federation. | `iam-application-service` |
| `iam-rest-api` | REST controllers, DTO request/response mapping, RFC 9457 `ProblemDetail`, JWT resource server filter chain. | `iam-application-service` |
| `iam-web` | JTE templates, web security, English (`en`) & Khmer (`km`) internationalization. | `iam-application-service` |
| `iam-main` | Spring Boot bootstrap, Liquibase migrations, data seeder (`InitCore`). | *All modules* |

---

## Core Domain & Business Rules

1. **Explicit Join Entities**: Many-to-many relationships are explicitly modeled without `@ManyToMany`:
   - `UserRole` (`user_roles`)
   - `RolePermission` (`role_permissions`)
   - `UserPermission` (`user_permissions`)
2. **Assignments are Revoked, Never Hard Deleted**: Assignments contain `assigned_at`, `assigned_by`, `revoked_at`, `revoked_by`, `reason`, and optional `expires_at`.
3. **Effective Permissions Calculation Engine**:
   $$\text{Effective Permissions} = (\text{Active Role Permissions}) \cup (\text{Direct GRANT}) \setminus (\text{Direct DENY})$$
4. **Protected System Entities**: Built-in system roles (`ADMIN`, `USER`) and permissions cannot be deleted or renamed. Inactive entities cannot be assigned.
5. **Auditing & Concurrency**:
   - Soft delete (`is_deleted`, `deleted_at`, `deleted_by`) on all entities.
   - Optimistic concurrency control via `@Version` (`version`).
   - Audit metadata (`created_at`, `created_by`, `updated_at`, `updated_by`).

---

## Environment Variables & Configuration

Configuration is externalized in `application.yml` and overridable via environment variables:

| Variable | Description | Default |
| :--- | :--- | :--- |
| `DB_HOST` | PostgreSQL hostname | `localhost` |
| `DB_PORT` | PostgreSQL port | `9543` |
| `DB_NAME` | PostgreSQL database name | `db_oauth2` |
| `DB_USERNAME` | Database username | `iteuser` |
| `DB_PASSWORD` | Database password | `qwerqwer` |
| `ISSUER_URI` | OpenID Connect Issuer URL | `http://localhost:9000` |
| `SERVER_PORT` | Application HTTP Port | `9000` |
| `GOOGLE_CLIENT_ID` | OAuth2 Google Client ID | *(optional)* |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Google Client Secret | *(optional)* |
| `GITHUB_CLIENT_ID` | OAuth2 GitHub Client ID | *(optional)* |
| `GITHUB_CLIENT_SECRET` | OAuth2 GitHub Client Secret | *(optional)* |
| `TELEGRAM_BOT_TOKEN` | Telegram Bot Token for Auth widget | *(optional)* |

---

## Default Seeded Credentials

When `iam-service` boots for the first time, `InitCore` seeds standard permissions, roles, users, and OAuth2 clients:

### 1. Default OAuth2 Client
- **Client ID**: `iam-client`
- **Client Secret**: `iam-secret`
- **Grant Types**: `authorization_code`, `refresh_token`, `client_credentials`
- **Scopes**: `openid`, `profile`, `email`, `phone`, `address`, `client.create`, `user:read`, `user:create`, `user:update`, `role:read`, `permission:read`
- **Redirect URIs**: `http://localhost:8080/login/oauth2/code/iam-client`, `https://oauth.pstmn.io/v1/callback`

### 2. Default Seeded Users
| Username | Password | Roles | Permissions |
| :--- | :--- | :--- | :--- |
| `admin` | `AdminPassword123!` | `ADMIN`, `USER` | Full privileges on `user:*`, `role:*`, `permission:*` |
| `user` | `UserPassword123!` | `USER` | `user:read` |

---

## OAuth 2.1 & OIDC Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/.well-known/openid-configuration` | `GET` | OpenID Connect 1.0 Discovery document |
| `/oauth2/jwks` | `GET` | JSON Web Key Set (RSA 2048 public keys) |
| `/oauth2/authorize` | `GET` | Authorization endpoint (supports PKCE) |
| `/oauth2/token` | `POST` | Token endpoint (Authorization Code, Client Credentials, Refresh Token) |
| `/oauth2/consent` | `GET/POST` | User scope authorization and consent screen |
| `/connect/register` | `POST` | RFC 7591 Dynamic Client Registration (requires `client.create` scope) |
| `/connect/register` | `GET` | RFC 7592 Client Configuration query |

---

## Web Pages & Internationalization (JTE)

The user-facing web pages are rendered server-side using **JTE (Java Template Engine)** and feature bilingual localization in **English (`en`)** and **Khmer (`km`)** without hardcoded strings:

- **Sign In**: `/login` (`login.jte`) — Sign in, social login buttons, language switcher
- **Sign Up**: `/register` (`register.jte`) — Sign-up form with validation errors
- **OAuth2 Consent**: `/oauth2/consent` (`consent.jte`) — Client name, requested scopes, Allow / Cancel
- **Dashboard / Profile**: `/` (`index.jte`) — Profile: claims, roles and permissions
- **Edit Profile**: `/profile/edit` (`profile-edit.jte`) — Edit profile
- **Change Password**: `/profile/password` (`change-password.jte`) — Change password
- **Linked Social Accounts**: `/profile/accounts` (`linked-accounts.jte`) — Link and unlink social accounts
- **Connected Apps**: `/profile/apps` (`connected-apps.jte`) — Authorized apps; revoke consent

> Switch language at any time via URL parameter: `?lang=en` or `?lang=km` (stored in cookie).

---

## REST API Overview (`/api`)

All REST endpoints are secured via JWT bearer tokens and enforced with permission authorities (`user:read`, `user:create`, `role:read`, etc.). API errors strictly return **RFC 9457 Problem Details** (`application/problem+json`).

- `POST /api/auth/register` — Public user registration
- `GET/PATCH /api/me` — Current user profile
- `PUT /api/me/password` — Change password
- `GET/DELETE /api/me/identities` — Manage linked social accounts
- `GET/POST/PATCH/DELETE /api/users` — User CRUD, enable/disable, lock/unlock, password reset
- `GET/POST/DELETE /api/users/{uuid}/roles` — Assign and revoke roles
- `GET/POST/DELETE /api/users/{uuid}/permissions` — Grant and revoke direct permissions (GRANT/DENY)
- `GET/POST/PATCH/DELETE /api/roles` — Role CRUD, activation, and permission assignments
- `GET/POST/PATCH/DELETE /api/permissions` — Permission catalog management

---

## Testing & Verification

Run the full automated test suite (including domain unit tests and Spring Boot integration tests):

```bash
# From repository root
./gradlew test

# Or specifically for iam-main integration tests
./gradlew :iam-main:test
```

### Postman Collection
Import `iam-service.postman_collection.json` into Postman to test all endpoints. The collection automatically handles OAuth2 token acquisition, token chaining, and UUID parameterization across requests.

---

## Running the Application Locally

```bash
# 1. Start PostgreSQL (e.g. docker run or existing container)
docker start ite-postgres

# 2. Boot the IAM service
./gradlew :iam-main:bootRun
```

The service will start on `http://localhost:9000`.
