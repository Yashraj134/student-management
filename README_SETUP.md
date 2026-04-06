# Setup Guide (Clone and Run on Another Laptop)

This guide helps you clone and run the project on another machine with **JDK 17** and **PostgreSQL**.

## 1) Prerequisites

- **OS**: Windows/macOS/Linux
- **JDK**: 17 (required by `pom.xml` -> `java.version=17`)
- **PostgreSQL**: 13+ (recommended 14/15/16)
- **Git**: latest
- **Maven**: optional (project includes Maven Wrapper)
  - Preferred: use `mvnw` / `mvnw.cmd`

## 2) Project Versions (from `pom.xml`)

- **GroupId**: `com.example`
- **ArtifactId**: `student_management`
- **Spring Boot parent**: `4.0.5`
- **Java version**: `17`

## 3) Dependencies Used

### Main dependencies

- `org.springframework.boot:spring-boot-starter-aop:3.5.13`
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.springframework.boot:spring-boot-starter-validation`
- `org.springframework.boot:spring-boot-starter-webmvc`
- `org.springframework.boot:spring-boot-devtools` (runtime)
- `org.postgresql:postgresql` (runtime)
- `org.projectlombok:lombok` (optional)

### Test dependencies

- `org.springframework.boot:spring-boot-starter-data-jpa-test`
- `org.springframework.boot:spring-boot-starter-validation-test`
- `org.springframework.boot:spring-boot-starter-webmvc-test`

### Build plugins

- `org.springframework.boot:spring-boot-maven-plugin`
- `org.apache.maven.plugins:maven-compiler-plugin`

## 4) Clone the Repository

```powershell
git clone <your-repo-url>
Set-Location "student_management"
```

## 5) Database Setup (PostgreSQL)

Create DB and load schema + sample data using pgAdmin or `psql`.

### Option A: pgAdmin

1. Create database: `student_management`
2. Open query tool
3. Run script: `database/pgadmin_student_management_setup.sql`

### Option B: psql (command line)

```powershell
psql -U postgres -h localhost -p 5432 -c "CREATE DATABASE student_management;"
psql -U postgres -h localhost -p 5432 -d student_management -f "database/pgadmin_student_management_setup.sql"
```

## 6) Environment Variables (Recommended)

`application.properties` supports env override:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/student_management`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default from project file)

Set explicitly before run:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/student_management"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

## 7) Build and Run

### Windows

```powershell
Set-Location "C:\path\to\student_management"
.\mvnw.cmd clean spring-boot:run
```

### macOS/Linux

```bash
cd /path/to/student_management
chmod +x mvnw
./mvnw clean spring-boot:run
```

## 8) Application URL / Port

Current app port from `src/main/resources/application.properties`:

- `server.port=8081`

So base URL is:

- `http://localhost:8081`

## 9) Quick Health Check

Try:

- `GET http://localhost:8081/students?page=0&size=10`
- `GET http://localhost:8081/students/stats`

## 10) Useful Commands

Check versions:

```powershell
java -version
.\mvnw.cmd -v
psql --version
```

Run tests:

```powershell
.\mvnw.cmd test
```

## 11) Troubleshooting

- **Datasource error / driver class issue**
  - Verify PostgreSQL is running and env vars are correct.
- **401 Unauthorized in Postman**
  - Confirm you are hitting this app instance and correct port (`8081`).
- **Port already in use**
  - Free the port or change `server.port`.
- **Connection refused**
  - Check DB host/port and firewall settings.
- **Wrong credentials**
  - Re-check `DB_USERNAME` / `DB_PASSWORD`.

## 12) Related Docs

- API testing: `README_API_TESTING.md`
- Architecture: `README_ARCHITECTURE.md`
- SQL setup: `database/pgadmin_student_management_setup.sql`

