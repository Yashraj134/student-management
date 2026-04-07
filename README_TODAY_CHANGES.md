# Today Changes - Backend + Image Feature

This document captures all changes done today: modified files, newly created files, PostgreSQL queries, new APIs, and test payloads.

## 1) Files Modified Today

- `README_API_TESTING.md`
- `database/pgadmin_student_management_setup.sql`
- `src/main/java/com/example/student_management/controller/StudentController.java`
- `src/main/java/com/example/student_management/designpatterns/facade/StudentManagementFacade.java`
- `src/main/java/com/example/student_management/designpatterns/facade/StudentManagementFacadeImpl.java`
- `src/main/java/com/example/student_management/dto/StudentProfileResponse.java`
- `src/main/java/com/example/student_management/dto/StudentSummaryResponse.java`
- `src/main/java/com/example/student_management/dto/StudentUpsertRequest.java`
- `src/main/java/com/example/student_management/entity/Student.java`
- `src/main/java/com/example/student_management/mapper/StudentMapper.java`
- `src/main/java/com/example/student_management/repository/StudentRepository.java`
- `src/main/java/com/example/student_management/service/StudentService.java`
- `src/main/java/com/example/student_management/service/impl/StudentServiceImpl.java`

## 2) New Files Created Today

- `src/main/java/com/example/student_management/config/StaticResourceConfig.java`
- `uploads/defaults/male_icon.jpg`
- `uploads/defaults/female_icon.jpg`

## 3) New APIs Added Today

Base URL: `http://localhost:8081`

1. `GET /students/all`
   - Returns full profile list of all students.

2. `GET /students/summary/all`
   - Returns summary list of all students.

3. `POST /students/{id}/profile-image` (multipart/form-data)
   - Upload custom profile image for a student.
   - Form key: `file` (type File).

4. Static file access mapping (not controller API, but accessible route)
   - `GET /files/{relativePath}`
   - Example: `/files/defaults/male_icon.jpg`

## 4) PostgreSQL Queries to Run in pgAdmin

Run these if your existing DB was created before profile-image changes.

```sql
ALTER TABLE students
ADD COLUMN IF NOT EXISTS profile_image_path TEXT;

UPDATE students s
SET profile_image_path = CASE
    WHEN lower(coalesce(pi.gender, '')) = 'male' THEN 'defaults/male_icon.jpg'
    ELSE 'defaults/female_icon.jpg'
END
FROM personal_info pi
WHERE pi.student_id = s.student_id
  AND (s.profile_image_path IS NULL OR trim(s.profile_image_path) = '');
```

## 5) Test Data / Payloads

### A) Create Student (raw JSON)

Endpoint: `POST /students`

```json
{
  "firstName": "Aarav",
  "middleName": "Kumar",
  "lastName": "Sharma",
  "profileImagePath": "",
  "studentContact": {
    "address": "Baner, Pune, Maharashtra",
    "mobileNo": "9876543210",
    "email": "aarav.sharma.test@college.in"
  },
  "admissionDetails": {
    "registrationId": "REG-MH-2026-2001",
    "dateOfRegistration": "2026-04-07",
    "prn": "PRN2026MH2001",
    "admittedAcademicYear": 2026,
    "currentAcademicYear": 2026,
    "admissionPattern": "regular",
    "fees": 95000
  },
  "personalInfo": {
    "birthPlace": "Pune",
    "nationality": "Indian",
    "gender": "male",
    "category": "OBC",
    "religion": "Hindu",
    "domicileState": "Maharashtra",
    "bloodGroup": "B+"
  },
  "parentDetails": {
    "fatherFirstName": "Rajesh",
    "fatherMiddleName": "P",
    "fatherLastName": "Sharma",
    "fatherContactNo": "9822011111",
    "motherFirstName": "Sunita",
    "motherMiddleName": "R",
    "motherLastName": "Sharma",
    "motherContactNo": "9890011111"
  },
  "documents": []
}
```

Notes:
- If `profileImagePath` is blank/null, backend sets default by gender.
- `male` -> `defaults/male_icon.jpg`
- `female`/`other` -> `defaults/female_icon.jpg`

### B) Update Student with explicit image path (raw JSON)

Endpoint: `PUT /students/{id}`

```json
{
  "firstName": "Aarav",
  "middleName": "Kumar",
  "lastName": "Sharma",
  "profileImagePath": "student-1/profile-image/custom_avatar.jpg",
  "studentContact": {
    "address": "Baner, Pune, Maharashtra",
    "mobileNo": "9876543210",
    "email": "aarav.sharma.test@college.in"
  },
  "admissionDetails": {
    "registrationId": "REG-MH-2026-2001",
    "dateOfRegistration": "2026-04-07",
    "prn": "PRN2026MH2001",
    "admittedAcademicYear": 2026,
    "currentAcademicYear": 2026,
    "admissionPattern": "regular",
    "fees": 95000
  },
  "personalInfo": {
    "birthPlace": "Pune",
    "nationality": "Indian",
    "gender": "male",
    "category": "OBC",
    "religion": "Hindu",
    "domicileState": "Maharashtra",
    "bloodGroup": "B+"
  },
  "parentDetails": {
    "fatherFirstName": "Rajesh",
    "fatherMiddleName": "P",
    "fatherLastName": "Sharma",
    "fatherContactNo": "9822011111",
    "motherFirstName": "Sunita",
    "motherMiddleName": "R",
    "motherLastName": "Sharma",
    "motherContactNo": "9890011111"
  },
  "documents": []
}
```

### C) Upload custom image from local machine (Downloads)

Endpoint: `POST /students/{id}/profile-image`

Body type: `form-data`
- key: `file` (File)
- choose local image from `Downloads`

No raw JSON body for this endpoint (multipart only).

## 6) Quick API Verification List

- `GET /students/all`
- `GET /students/summary/all`
- `POST /students/{id}/profile-image` (multipart)
- `GET /students/{id}` (check `profileImagePath`)
- `GET /files/{profileImagePath}` (image should open)

## 7) Frontend Image URL Rule

Frontend should build image URL as:

`http://localhost:8081/files/{profileImagePath}`

Examples:
- `http://localhost:8081/files/defaults/male_icon.jpg`
- `http://localhost:8081/files/student-1/profile-image/abc123_photo.jpg`

