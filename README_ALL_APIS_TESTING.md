# Student Management - Complete API Testing Guide

Base URL: `http://localhost:8081`

## 1) Authentication API

### POST `/auth/login`
- Auth required: No
- Body (raw JSON):

```json
{
  "username": "admin",
  "password": "admin123"
}
```

- Example success response:

```json
{
  "token": "<jwt-token>",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN",
  "studentId": null
}
```

Use returned token in all protected APIs:
`Authorization: Bearer <jwt-token>`

---

## 2) Student APIs (`/students`)

## GET APIs

### GET `/students/{id}`
### GET `/students/summary/{id}`
### GET `/students?page=0&size=10`
### GET `/students/all`
### GET `/students/summary/all`
### GET `/students/search?keyword=rohan&page=0&size=10`
### GET `/students/filter?currentAcademicYear=2&admissionPattern=regular&page=0&size=10`
### GET `/students/stats`
### GET `/students/stats/year-wise`

---

## POST APIs

### POST `/students`
- Body (raw JSON):

```json
{
  "firstName": "Aarav",
  "middleName": "Kumar",
  "lastName": "Sharma",
  "profileImagePath": "defaults/male_icon.jpg",
  "studentContact": {
    "address": "Baner, Pune, Maharashtra",
    "mobileNo": "9876543210",
    "email": "aarav.sharma.new@college.in"
  },
  "admissionDetails": {
    "registrationId": "REG-MH-2026-0101",
    "dateOfRegistration": "2026-04-10",
    "prn": "PRN2026MH0101",
    "admittedAcademicYear": 2026,
    "currentAcademicYear": 1,
    "admissionPattern": "regular",
    "fees": 85000
  },
  "personalInfo": {
    "birthPlace": "Nagpur",
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
      "filePath": "docs/aadhaar/aadhaar_aarav.pdf"
    }
  ]
}
```

### POST `/students/bulk`
- Body (raw JSON array):

```json
[
  {
    "firstName": "Riya",
    "middleName": "M",
    "lastName": "Patil",
    "profileImagePath": "defaults/female_icon.jpg",
    "studentContact": {
      "address": "Kothrud, Pune",
      "mobileNo": "9876500001",
      "email": "riya.patil@college.in"
    },
    "admissionDetails": {
      "registrationId": "REG-MH-2026-0201",
      "dateOfRegistration": "2026-04-12",
      "prn": "PRN2026MH0201",
      "admittedAcademicYear": 2026,
      "currentAcademicYear": 1,
      "admissionPattern": "regular",
      "fees": 80000
    },
    "personalInfo": {
      "birthPlace": "Pune",
      "nationality": "Indian",
      "gender": "female",
      "category": "OPEN",
      "religion": "Hindu",
      "domicileState": "Maharashtra",
      "bloodGroup": "A+"
    },
    "parentDetails": {
      "fatherFirstName": "Mahesh",
      "fatherMiddleName": "K",
      "fatherLastName": "Patil",
      "fatherContactNo": "9822000001",
      "motherFirstName": "Seema",
      "motherMiddleName": "K",
      "motherLastName": "Patil",
      "motherContactNo": "9890000001"
    },
    "documents": []
  }
]
```

### POST `/students/{id}/documents`
- Content-Type: `multipart/form-data`
- form-data keys:
  - `documentType` = `Aadhaar`
  - `file` = (choose file)

### POST `/students/{id}/profile-image`
- Content-Type: `multipart/form-data`
- form-data keys:
  - `file` = (choose image)

### POST `/students/{id}/request-id-card`
Alias: `/students/{id}/id-card/request`
- Body (raw JSON):

```json
{}
```

---

## PUT APIs

### PUT `/students/{id}`
- Body (raw JSON):

```json
{
  "firstName": "Aarav",
  "middleName": "K",
  "lastName": "Sharma",
  "profileImagePath": "defaults/male_icon.jpg",
  "studentContact": {
    "address": "Wakad, Pune, Maharashtra",
    "mobileNo": "9876543219",
    "email": "aarav.sharma.updated@college.in"
  },
  "admissionDetails": {
    "registrationId": "REG-MH-2026-0101",
    "dateOfRegistration": "2026-04-10",
    "prn": "PRN2026MH0101",
    "admittedAcademicYear": 2026,
    "currentAcademicYear": 1,
    "admissionPattern": "regular",
    "fees": 90000
  },
  "personalInfo": {
    "birthPlace": "Nagpur",
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

### PUT `/students/{id}/approve-id-card`
Alias: `/students/{id}/id-card/approve`
- Body (raw JSON):

```json
{}
```

### PUT `/students/{id}/reject-id-card?remark=Documents%20not%20clear`
Alias: `/students/{id}/id-card/reject`
- Option A (recommended): query param `remark`
- Option B body (raw JSON):

```json
{
  "remark": "Documents not clear"
}
```

---

## DELETE APIs

### DELETE `/students/{id}`

---

## ID Card Status APIs

### GET `/students/id-card-requests`
Alias: `/students/id-card/pending`

### GET `/students/{id}/id-card-status`
Alias: `/students/{id}/id-card/status`

---

## Suggested Test Order in Postman

1. `POST /auth/login` (get token)
2. `POST /students`
3. `GET /students/{id}`
4. `PUT /students/{id}`
5. `POST /students/{id}/request-id-card`
6. `GET /students/{id}/id-card-status`
7. `PUT /students/{id}/approve-id-card` or `reject-id-card`
8. `POST /students/{id}/documents` and `POST /students/{id}/profile-image`
9. `GET /students/all`, `GET /students/summary/all`, `GET /students/stats`

