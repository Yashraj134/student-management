# Login DB Setup Guide (PostgreSQL + Spring Boot)

This guide explains what DB changes are required for login, how to set username/password, and whether SQL files run automatically when the app starts.

## 1) Does `.sql` run automatically on project start?

Short answer: **your `database/pgadmin_student_management_setup.sql` does NOT run automatically**.

Why:
- Your project uses `spring.jpa.hibernate.ddl-auto=update` in `src/main/resources/application.properties`.
- There is no configured Spring SQL init file (`schema.sql` / `data.sql`) for automatic execution.
- So table updates may happen from entities, but your full pgAdmin setup script is manual.

## 2) What is already auto-created by backend on startup?

`AuthDataInitializer` auto-creates default users only if missing:
- `admin / admin123` (role `ADMIN`, no student mapping)
- `student / student123` (role `STUDENT`, mapped to `student_id=1`)
- `student4 / student123` (role `STUDENT`, mapped to `student_id=4`)

File reference: `src/main/java/com/example/student_management/security/AuthDataInitializer.java`

## 3) Required pgAdmin queries for login functionality

Run these in pgAdmin once (safe for existing DB).

```sql
-- users table required for login/auth
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'STUDENT')),
    student_id INT REFERENCES students(student_id) ON DELETE SET NULL
);
```

## 4) Add or update user credentials from pgAdmin

### Option A (recommended): let app create defaults
- Start backend.
- `AuthDataInitializer` inserts defaults if they are missing.

### Option B: create users manually from SQL
Use BCrypt-compatible hash in DB using `pgcrypto`:

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Example: student mapped to student_id=5
INSERT INTO users (username, password, role, student_id)
VALUES (
    'student5',
    crypt('student5@123', gen_salt('bf')),
    'STUDENT',
    5
)
ON CONFLICT (username) DO UPDATE
SET
    password = EXCLUDED.password,
    role = EXCLUDED.role,
    student_id = EXCLUDED.student_id;
```

Admin example:

```sql
INSERT INTO users (username, password, role, student_id)
VALUES (
    'admin2',
    crypt('admin2@123', gen_salt('bf')),
    'ADMIN',
    NULL
)
ON CONFLICT (username) DO UPDATE
SET
    password = EXCLUDED.password,
    role = EXCLUDED.role,
    student_id = EXCLUDED.student_id;
```

## 5) Verify users in DB

```sql
SELECT user_id, username, role, student_id
FROM users
ORDER BY user_id;
```

## 6) Login API to test

Endpoint:
- `POST http://localhost:8081/auth/login`

Body:

```json
{
  "username": "student5",
  "password": "student5@123"
}
```

If credentials are correct, API returns JWT token and role details.

