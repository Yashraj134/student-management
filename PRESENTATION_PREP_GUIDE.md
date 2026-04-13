# Student Management Backend - Presentation Prep Guide

This document is a complete prep sheet for your backend presentation/viva.
Use it as your speaking script.

---

## 1) 30-Second Project Introduction

"This is a Spring Boot backend for Student Management System using layered architecture. It handles student CRUD, search/filter, dashboard stats, profile image and document upload, ID card workflow, and JWT-based authentication with role-based authorization. Data is stored in PostgreSQL and APIs are tested using Postman."

---

## 2) Tech Stack (Say This Early)

- Java (JDK 17+ compatible for your setup)
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- PostgreSQL
- Maven
- Lombok
- DevTools

---

## 3) High-Level Architecture

### Layered Flow
`Controller -> Facade -> Service -> Repository -> Database`

### Why this matters
- Controller: only HTTP request/response handling
- Facade: one coordination layer for use-cases + audit calls
- Service: business rules and validations
- Repository: DB queries
- Mapper: Entity <-> DTO conversion

This separation makes code easier to test, maintain, and explain.

---

## 4) End-to-End Request Flow (Important)

Use this one example in viva: `POST /students`

1. `StudentController` receives request body.
2. Control goes to `StudentManagementFacadeImpl`.
3. Facade calls `StudentServiceImpl.createStudent(...)`.
4. Service validates unique email/PRN.
5. Service derives `currentAcademicYear`.
6. Strategy validates admission details.
7. Factory creates admission details object based on admission type.
8. Mapper converts DTO to entity.
9. Repository saves to DB.
10. Observer gets notified for post-create action.
11. Mapper converts saved entity to response DTO.
12. Controller returns response to frontend/Postman.

---

## 5) Security Flow (JWT + Roles)

### Login flow
1. `POST /auth/login` goes to `AuthController`.
2. `AuthService.login(...)` authenticates username/password.
3. `JwtService.generateToken(...)` creates JWT.
4. Token is returned in `AuthResponse`.

### Secured API flow
1. Request includes `Authorization: Bearer <token>`.
2. `JwtAuthenticationFilter` runs before controller.
3. Filter extracts username from token.
4. Token validity is checked.
5. Spring Security context is populated.
6. Role checks happen based on `SecurityConfig` rules.
7. For student ownership checks, `StudentAccessEvaluator` is used.

### Role behavior (current design)
- ADMIN: full access
- STUDENT: restricted access (especially own resource access)

### 401 vs 403
- 401: not authenticated (`RestAuthenticationEntryPoint`)
- 403: authenticated but not allowed (`RestAccessDeniedHandler`)

---

## 6) Why DTOs Are Used

DTOs are used to:
- avoid exposing internal entity structure directly
- validate input cleanly
- shape API responses for frontend needs
- keep request and response models separate

Examples:
- request DTO: `StudentUpsertRequest`
- full response DTO: `StudentProfileResponse`
- list response DTO: `StudentSummaryResponse`
- auth DTOs: `AuthRequest`, `AuthResponse`

---

## 7) Mapper Layer (How to Explain)

`StudentMapper` centralizes conversion logic:
- `toStudentEntity(...)` for create
- `updateStudentEntity(...)` for update
- `toProfileResponse(...)` for details API
- `toSummaryResponse(...)` for listing APIs
- pagination conversion methods
- document mapping methods

Why mapper is helpful:
- keeps service logic clean
- avoids repeated conversion code
- supports consistent response structure

---

## 8) Service Layer Responsibilities

### `StudentServiceImpl`
Main business logic:
- create/update/delete student
- get by id, summary, list, search, filter
- dashboard and year-wise stats
- profile image upload
- ID card request workflow (request/pending/approve/reject/status)

### `DocumentServiceImpl`
- upload documents
- map document DTOs
- delete documents by student

### `LocalFileStorageService`
- saves files in local uploads folder
- returns relative file path for DB

### `AuthService`
- validates login credentials
- generates JWT
- returns token + role + studentId

### `AuditServiceImpl`
- logs important actions (for traceability)

---

## 9) Repository + Custom Queries (Very Common Viva Question)

Custom queries are in repository interfaces, mainly `StudentRepository`.

Used for:
- full profile fetch with joins
- search by keyword
- filter by academic year/admission type
- summary projections
- dashboard aggregated counts
- year-wise chart data

Service layer calls repository methods; controller never directly talks to repository.

---

## 10) Design Patterns Used (Simple Explanation)

### 1) Factory Pattern
Used to create admission details based on admission pattern.
- classes in factory package choose correct creation logic.

### 2) Strategy Pattern
Used for admission validation rules.
- different validation strategy for regular vs management.

### 3) Observer Pattern
Used after student creation.
- `StudentCreationSubject` notifies listeners.

Presentation line:
"Patterns are used only where needed to keep code modular and future-ready."

---

## 11) ID Card Workflow (Business Flow)

Status values:
- `NOT_REQUESTED`
- `PENDING`
- `APPROVED`
- `REJECTED`

Flow:
1. Student requests -> `PENDING`
2. Admin approves -> `APPROVED`
3. Admin rejects with remark -> `REJECTED`
4. Student checks own status via status API

---

## 12) File Upload and Storage Flow

### Profile image
- upload API stores file on server
- DB stores relative path
- frontend builds URL via `/files/...`

### Documents
- multipart upload API stores files under uploads directory
- DB stores file metadata (`documentType`, `fileName`, `filePath`)
- frontend can open/download using stored path mapping

If URL gives `No static resource ...`, usually path in DB does not match actual stored file path or static resource mapping path.

---

## 13) Core API Groups to Mention

1. Auth APIs
- `POST /auth/login`

2. Student CRUD
- `POST /students`
- `GET /students/{id}`
- `PUT /students/{id}`
- `DELETE /students/{id}`

3. List/Search/Filter
- `GET /students`
- `GET /students/all`
- `GET /students/summary/all`
- `GET /students/search`
- `GET /students/filter`

4. Dashboard
- `GET /students/stats`
- `GET /students/stats/year-wise`

5. Uploads
- `POST /students/{id}/profile-image`
- `POST /students/{id}/documents`

6. ID Card
- `POST /students/{id}/request-id-card`
- `GET /students/id-card-requests`
- `PUT /students/{id}/approve-id-card`
- `PUT /students/{id}/reject-id-card`
- `GET /students/{id}/id-card-status`

---

## 14) Database Prep Before Running (Important)

Before run on new machine:
1. Install PostgreSQL and create database.
2. Update DB connection in `application.properties`.
3. Run required SQL setup scripts (users/table changes if not auto-created).
4. Ensure uploads directory exists or is creatable by app.

Note:
- SQL files do not run automatically unless explicitly configured using Spring SQL init properties.
- If not configured, run scripts manually in pgAdmin.

---

## 15) Demo Order for Presentation (Safe Sequence)

1. Start app and show startup success.
2. Login API in Postman, get JWT token.
3. Use token in Authorization header.
4. Create a student.
5. Get student by id.
6. Update student.
7. Upload profile image and one document.
8. Show search/filter/list/stats APIs.
9. Trigger ID card request and approve/reject flow.
10. Show one security check (forbidden endpoint by wrong role).

---

## 16) Common Viva Questions + Short Answers

### Q1: Why facade layer if service already exists?
A: Facade coordinates multi-service use-cases and keeps controller very thin.

### Q2: Why DTOs?
A: API contract control, validation, and security from entity overexposure.

### Q3: Why `@Transactional`?
A: Ensures atomic DB updates and rollback on failure.

### Q4: Why custom queries?
A: For optimized projections, joins, and aggregate stats directly from DB.

### Q5: Why JWT?
A: Stateless authentication; each request carries token and role context.

### Q6: Difference between 401 and 403?
A: 401 = not logged in/invalid token, 403 = logged in but no permission.

### Q7: Where files are stored?
A: Local filesystem in configured upload directory; DB stores relative paths.

---

## 17) Backup Lines If You Get Stuck

- "Controller is thin; business logic is in service."
- "Repository custom queries are used for performance and projection."
- "Mapper keeps DTO/entity conversion centralized."
- "Security flow is: token extract -> validate -> set authentication -> role/ownership check."
- "All critical write operations are transactional."

---

## 18) Final 60-Second Closing Script

"In this backend, I implemented a clean layered architecture with secure JWT auth and role-based access. I handled full student lifecycle, document/profile upload, and ID card workflow with status transitions. I used DTOs and mapper for clean contracts, custom repository queries for efficient data retrieval, and design patterns like factory, strategy, and observer where they add clarity. The project is modular, scalable for next features, and ready to integrate with Angular frontend."

---

Good luck for your presentation. Read sections 4, 5, 8, 10, and 15 once before demo start.
