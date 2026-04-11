# Day 3 Changes

## Modified Files

1. `database/pgadmin_student_management_setup.sql`
   - Added `users` table creation.
   - Added sequence handling for `users_user_id_seq`.

2. `pom.xml`
   - Added Spring Security dependency.
   - Added JWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
   - Added `spring-security-test` for test scope.

3. `src/main/resources/application.properties`
   - Added JWT settings:
     - `app.jwt.secret`
     - `app.jwt.expiration-ms`

4. `README_API_TESTING.md`
   - Added login and JWT testing flow for Postman.

## Newly Created Files

1. `src/main/java/com/example/student_management/config/SecurityConfig.java`
2. `src/main/java/com/example/student_management/controller/AuthController.java`
3. `src/main/java/com/example/student_management/dto/AuthRequest.java`
4. `src/main/java/com/example/student_management/dto/AuthResponse.java`
5. `src/main/java/com/example/student_management/entity/AppUser.java`
6. `src/main/java/com/example/student_management/entity/UserRole.java`
7. `src/main/java/com/example/student_management/repository/AppUserRepository.java`
8. `src/main/java/com/example/student_management/security/AuthDataInitializer.java`
9. `src/main/java/com/example/student_management/security/CustomUserDetailsService.java`
10. `src/main/java/com/example/student_management/security/JwtAuthenticationFilter.java`
11. `src/main/java/com/example/student_management/security/JwtService.java`
12. `src/main/java/com/example/student_management/security/RestAccessDeniedHandler.java`
13. `src/main/java/com/example/student_management/security/RestAuthenticationEntryPoint.java`
14. `src/main/java/com/example/student_management/service/AuthService.java`
15. `day3changes.md`

## pgAdmin Queries to Run (Existing Databases)

Run these once if your DB already existed before Day 3:

```sql
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'STUDENT'))
);

ALTER TABLE students
    ADD COLUMN IF NOT EXISTS profile_image_path TEXT;
```

Optional verification queries:

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name = 'users';

SELECT user_id, username, role
FROM users
ORDER BY user_id;
```

## Notes

- Default users are seeded by `AuthDataInitializer` on app startup:
  - `admin` / `admin123` (role `ADMIN`)
  - `student` / `student123` (role `STUDENT`)
- Role rules:
  - `STUDENT` -> only `GET /students/{id}`
  - `ADMIN` -> all APIs

