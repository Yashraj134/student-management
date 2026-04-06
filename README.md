# Student Management Backend

Spring Boot backend for Admin + Student Info module using MVC, SOLID, AOP, and design patterns.

## Base Package

`com.example.student_management`

## Implemented Modules

- `entity` - JPA models mapped to the provided PostgreSQL schema
- `repository` - JpaRepository contracts with custom methods and JPQL query
- `service` - interfaces + implementations with transactional orchestration
- `controller` - REST APIs for student CRUD and pagination
- `dto` - request/response DTOs with validation
- `mapper` - entity/DTO conversions with builder usage
- `exception` - custom exceptions and global exception handler
- `aop` - logging, performance, duplicate validation, audit aspects
- `designpatterns` - Factory, Strategy, Facade, Observer implementations

## APIs

- `POST /students`
- `GET /students/{id}`
- `GET /students?page=0&size=10`
- `PUT /students/{id}`
- `DELETE /students/{id}`

## Documentation

- SQL setup + seed data: `database/pgadmin_student_management_setup.sql`
- API testing playbook: `README_API_TESTING.md`
- Architecture and package guide: `README_ARCHITECTURE.md`
- Setup/prerequisites guide: `README_SETUP.md`

## Local Run

```powershell
Set-Location "C:\internshipproject\student_management"
.\mvnw.cmd spring-boot:run
```

## Notes

- Keep PostgreSQL running and configure datasource in `src/main/resources/application.properties`.
- Duplicate `email` and `prn` are validated in AOP before save/update.

