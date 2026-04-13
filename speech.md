# Backend Presentation Speech (Schema -> Folder Structure -> 4 API Flows)

Use this as your ready-to-speak script.
You can read this directly in presentation.

---

## 1) Opening (20-30 seconds)

Good morning everyone.
Today I am presenting my **Student Management Backend** built using **Spring Boot, PostgreSQL, JPA, and Spring Security with JWT**.
I will explain in this order:
1. Schema design
2. Folder structure and significance
3. Control flow of 4 important APIs

---

## 2) Schema Design (Start Here)

I designed this project around a central `students` table and related detail tables.
This keeps student data modular and easy to maintain.

### Core tables

1. **`students`**
- Primary key: `student_id`
- Basic name fields
- `profile_image_path`
- timestamps
- This is the parent entity.

2. **`student_contact`**
- Contact details like address, mobile, email
- Linked using `student_id` foreign key

3. **`admission_details`**
- registration id, prn, admission pattern, fees
- academic year fields
- linked to student

4. **`personal_info`**
- birthplace, gender, category, blood group
- linked to student

5. **`parent_details`**
- father and mother information
- linked to student

6. **`documents`**
- document type, file name, file path, uploaded timestamp
- linked to student

7. **`users`**
- username, password, role (`ADMIN` or `STUDENT`)
- optional `student_id` mapping for student-specific authorization

### Relationship design summary

- `students` is the root record.
- Other tables depend on `students` through foreign keys.
- This design avoids putting everything in one big table.
- It also supports clean CRUD, security mapping, and file/document features.

### Why this schema is good

- Normalized and clean
- Easy to add new features
- Separate auth table (`users`) from student data
- Supports role-based access and student ownership checks

---

## 3) Folder Structure + Significance

Now I will explain backend package structure and why each folder exists.

### Root: `src/main/java/com/example/student_management`

1. **`controller`**
- Entry point of APIs
- Handles HTTP methods and paths
- Example: `StudentController`, `AuthController`

2. **`designpatterns`**
- Contains educational and modular design implementations
- `factory`: creates admission objects based on pattern
- `strategy`: validates admission rules dynamically
- `observer`: triggers post-create events

3. **`dto`**
- Request and response models for API contract
- Keeps entity separate from external API model
- Example: `StudentUpsertRequest`, `StudentProfileResponse`, `AuthResponse`

4. **`entity`**
- JPA entities mapped to DB tables
- Example: `Student`, `AdmissionDetails`, `AppUser`, `Document`

5. **`exception`**
- Custom exceptions and global exception handler
- Gives uniform error responses (status, message, details)

6. **`facade`**
- Coordinates use-cases between controller and service
- Keeps controller thin and readable
- Example: `StudentManagementFacadeImpl`

7. **`mapper`**
- Converts DTO <-> Entity
- Keeps conversion logic centralized and avoids duplication

8. **`repository`**
- DB access layer using JPA repositories
- Includes custom `@Query` methods for search/filter/stats

9. **`security`**
- JWT generation/validation
- filter, custom user details service
- access evaluator and handlers

10. **`service` and `service/impl`**
- Business logic and use-case implementation
- Interfaces in `service`, logic in `service/impl`

11. **`config`**
- Project-level configuration files
- Security config and static resource mapping

### Resource folder: `src/main/resources`

- `application.properties`: database, port, upload paths, and app settings

### Database scripts folder: `database`

- Contains SQL setup scripts for pgAdmin initialization

### Upload folder

- Files are stored locally (documents/profile images)
- DB stores relative path, not binary file blob

---

## 4) Control Flow of 4 APIs

I will explain 4 API flows from request to response.

---

### API 1: Create Student
**Endpoint:** `POST /students`

#### Control flow
1. Request enters `StudentController.createStudent(...)`
2. Controller calls `StudentManagementFacadeImpl.createStudent(...)`
3. Facade calls `StudentServiceImpl.createStudent(...)`
4. Service does:
   - `validateUniqueEmailAndPrn(...)`
   - derives current academic year
   - validates admission by strategy
   - creates admission object by factory
   - maps DTO to entity using mapper
   - applies default profile image path if missing
5. `StudentRepository.save(...)` stores record
6. Observer notification is triggered
7. Mapper converts entity to `StudentProfileResponse`
8. Response returned to controller and then client

#### Why important
This API demonstrates full architecture: validation, patterns, mapper, repository, and clean response.

---

### API 2: Get Student by ID
**Endpoint:** `GET /students/{id}`

#### Control flow
1. Request enters `StudentController.getStudentById(...)`
2. Facade method is called
3. Service `getStudentById(id)` runs
4. Repository custom query `findFullProfileById(...)` fetches joined data
5. If not found -> `ResourceNotFoundException`
6. Mapper builds profile response DTO
7. Response returned

#### Why custom query here
To fetch full profile with related tables in one optimized query.

---

### API 3: Login
**Endpoint:** `POST /auth/login`

#### Control flow
1. Request enters `AuthController.login(...)`
2. Calls `AuthService.login(...)`
3. `AuthenticationManager` verifies username/password
4. `CustomUserDetailsService` loads user details
5. `JwtService.generateToken(...)` creates token
6. `AppUserRepository.findByUsername(...)` fetches role + studentId
7. Returns `AuthResponse` with token, role, username, studentId

#### After login
Client sends `Authorization: Bearer <token>` in secured APIs.

---

### API 4: Request ID Card
**Endpoint:** `POST /students/{id}/request-id-card`

#### Control flow
1. Request first goes through Security Filter Chain
2. JWT token validated by `JwtAuthenticationFilter`
3. Role/ownership checked as per `SecurityConfig` and access evaluator
4. Request enters `StudentController.requestIdCard(...)`
5. Calls `studentService.requestIdCard(id)`
6. Service loads student from repository
7. Allowed transition check:
   - `NOT_REQUESTED` or `REJECTED` -> set `PENDING`
   - otherwise throws error
8. Repository saves updated status
9. Updated student/status returned

#### Why important
Shows role-based workflow and status-transition business rules.

---

## 5) Security Summary (Short lines)

- Login gives JWT token
- Token checked on every secured API
- 401 -> invalid/no token
- 403 -> token valid but no permission
- ADMIN has broad access, STUDENT has restricted access

---

## 6) One-Minute Closing Script

In this backend, I focused on clean architecture and real business workflows.
The schema is normalized around student as root entity with separate tables for contact, admission, personal info, parent details, documents, and users.
Code is organized layer by layer so each folder has one clear responsibility.
For API flow, I demonstrated create student, get student by id, login, and request ID card.
The project uses JWT security, custom queries, DTO mapping, and design patterns where useful.
This makes the backend easier to scale, test, and integrate with frontend.

---

## 7) Quick Backup Answers (if asked suddenly)

- **Why facade?** To keep controller thin and centralize use-case orchestration.
- **Why DTO?** To control API contract and avoid direct entity exposure.
- **Why custom query?** For optimized joins, projection, and aggregate queries.
- **Why transactional?** To ensure complete rollback on failure.
- **Where files are stored?** In local uploads directory, path stored in DB.
- **How auth works?** Login creates JWT, filter validates token per request.

