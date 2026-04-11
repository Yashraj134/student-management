# API Testing Guide (Postman)

This file contains all API endpoints for `com.example.student_management` backend.

## Base URL

`http://localhost:8081`

## Common Headers

- `Content-Type: application/json`
- `Accept: application/json`

## Authentication (JWT) - Test First

Most student APIs are protected by JWT. Generate token first, then send it in protected API calls.

### Login API

- **Method**: `POST`
- **URL**: `/auth/login`

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Alternative seeded user:

```json
{
  "username": "student",
  "password": "student123"
}
```

Expected login response:

```json
{
  "token": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

### How to Use Token in Postman

For protected APIs, set Authorization header as:

`Authorization: Bearer <JWT_TOKEN>`

Postman recommended way:

1. Open request -> **Authorization** tab
2. Type = `Bearer Token`
3. Paste token value only (without `Bearer` prefix)

Role behavior:

- `ADMIN` -> can access all APIs
- `STUDENT` -> only `GET /students/{id}`

## 1) Create Student

- **Method**: `POST`
- **URL**: `/students`

```json
{
  "firstName": "Aarav",
  "middleName": "Kumar",
  "lastName": "Sharma",
  "profileImagePath": "uploads/student-1/profile-image/aarav.jpg",
  "studentContact": {
    "address": "Baner, Pune, Maharashtra",
    "mobileNo": "9876543210",
    "email": "aarav.sharma@college.in"
  },
  "admissionDetails": {
    "registrationId": "REG-MH-2026-1001",
    "dateOfRegistration": "2026-04-07",
    "prn": "PRN2026MH1001",
    "admittedAcademicYear": 2026,
    "currentAcademicYear": 2026,
    "admissionPattern": "regular",
    "fees": 90000
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
  "documents": [
    {
      "documentType": "Aadhaar",
      "fileName": "aadhaar_aarav.pdf",
      "filePath": "/docs/aadhaar/aadhaar_aarav.pdf"
    }
  ]
}
```

If `profileImagePath` is omitted or blank, backend auto-sets:
- `defaults/male_icon.jpg` when gender is `male`
- `defaults/female_icon.jpg` for all other gender values

## 2) Bulk Create Students

- **Method**: `POST`
- **URL**: `/students/bulk`
- **Body**: array of the same object used in create API.

## 3) Get Student Full Profile

- **Method**: `GET`
- **URL**: `/students/{id}`
- **Example**: `/students/1`

## 4) Get Student Summary

- **Method**: `GET`
- **URL**: `/students/summary/{id}`
- **Example**: `/students/summary/1`

## 5) Get Students (Paginated)

- **Method**: `GET`
- **URL**: `/students?page=0&size=10`

## 6) Search Students

Searches by `firstName`, `lastName`, `email`, `prn`.

- **Method**: `GET`
- **URL**: `/students/search?keyword=patil&page=0&size=10`

## 7) Filter Students

Filters by optional parameters `currentAcademicYear` and `admissionPattern`.

- **Method**: `GET`
- **URL**: `/students/filter?currentAcademicYear=2026&admissionPattern=regular&page=0&size=10`
- **Only year**: `/students/filter?currentAcademicYear=2026&page=0&size=10`
- **Only pattern**: `/students/filter?admissionPattern=management&page=0&size=10`

## 8) Dashboard Stats

Returns total, regular count, management count.

- **Method**: `GET`
- **URL**: `/students/stats`

## 9) Year-wise Stats

Returns data for charts.

- **Method**: `GET`
- **URL**: `/students/stats/year-wise`

## 10) Update Student

- **Method**: `PUT`
- **URL**: `/students/{id}`
- **Example**: `/students/1`
- **Body**: same shape as create API.

## 11) Delete Student

- **Method**: `DELETE`
- **URL**: `/students/{id}`
- **Expected**: `204 No Content`

## 12) Upload Certificate / Document (Multipart)

Use this API to upload a file from local system into `uploads/` and save metadata in `documents` table.

- **Method**: `POST`
- **URL**: `/students/{id}/documents`
- **Example**: `/students/1/documents`
- **Body type (Postman)**: `form-data`
  - `documentType` -> Text (example: `Aadhaar`)
  - `file` -> File (choose local file such as PDF/JPG)

Expected response includes `documentId`, `documentType`, `fileName`, `filePath`, `uploadedAt`.

## 13) Upload Student Profile Image (Multipart)

Use this API when user wants to override default icon (`male_icon.jpg` / `female_icon.jpg`) with an actual photo.

- **Method**: `POST`
- **URL**: `/students/{id}/profile-image`
- **Example**: `/students/1/profile-image`
- **Body type (Postman)**: `form-data`
  - `file` -> File (choose local JPG/PNG from Downloads)

Success response returns updated `StudentProfileResponse` with `profileImagePath`.

## Frontend Image URL Rule

Backend now serves local uploads using `/files/**` mapping.

- If API returns `profileImagePath = defaults/male_icon.jpg`, frontend image URL is:
  - `http://localhost:8081/files/defaults/male_icon.jpg`
- If API returns `profileImagePath = student-1/profile-image/abc.jpg`, frontend image URL is:
  - `http://localhost:8081/files/student-1/profile-image/abc.jpg`

## Error Responses You Should Test

- `400 Bad Request` -> invalid payload/validation
- `401 Unauthorized` -> token missing/invalid/expired
- `403 Forbidden` -> valid token but role does not have access
- `404 Not Found` -> unknown student id
- `409 Conflict` -> duplicate email or duplicate PRN

## Suggested Execution Order

1. Run SQL setup in `database/pgadmin_student_management_setup.sql`
2. Test `POST /auth/login` and copy JWT token
3. Test `GET /students/{id}` with `Authorization: Bearer <token>`
4. Test `GET /students?page=0&size=10`
5. Test `GET /students/search?...`
6. Test `GET /students/filter?...`
7. Test `GET /students/stats`
8. Test `GET /students/stats/year-wise`
9. Test `GET /students/summary/{id}`
10. Test `PUT /students/{id}`
11. Test `DELETE /students/{id}`
12. Test `POST /students` and `POST /students/bulk`

