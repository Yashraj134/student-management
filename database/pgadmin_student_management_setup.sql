-- Student Management System PostgreSQL setup script
-- Run in pgAdmin query tool.

BEGIN;

DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS parent_details CASCADE;
DROP TABLE IF EXISTS personal_info CASCADE;
DROP TABLE IF EXISTS admission_details CASCADE;
DROP TABLE IF EXISTS student_contact CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    first_name VARCHAR(10),
    middle_name VARCHAR(50),
    last_name VARCHAR(50),
    -- ===== ADDED: PROFILE_IMAGE_PATH START =====
    profile_image_path TEXT,
    -- ===== ADDED: PROFILE_IMAGE_PATH END =====
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_contact (
    contact_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    address TEXT,
    mobile_no VARCHAR(15),
    email VARCHAR(100)
);

CREATE TABLE admission_details (
    admission_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    registration_id VARCHAR(50),
    date_of_registration DATE,
    prn VARCHAR(50),
    admitted_academic_year INTEGER,
    current_academic_year INTEGER,
    admission_pattern VARCHAR(20) CHECK (admission_pattern IN ('regular', 'management')),
    fees INT
);

CREATE TABLE personal_info (
    personal_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    birth_place VARCHAR(100),
    nationality VARCHAR(50),
    gender VARCHAR(10) CHECK (gender IN ('male', 'female', 'other')),
    category VARCHAR(50),
    religion VARCHAR(50),
    domicile_state VARCHAR(50),
    blood_group VARCHAR(5) CHECK (
        blood_group IN ('A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-')
    )
);

CREATE TABLE parent_details (
    parent_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    father_first_name VARCHAR(50),
    father_middle_name VARCHAR(50),
    father_last_name VARCHAR(50),
    father_contact_no VARCHAR(15),
    mother_first_name VARCHAR(50),
    mother_middle_name VARCHAR(50),
    mother_last_name VARCHAR(50),
    mother_contact_no VARCHAR(15)
);

CREATE TABLE documents (
    document_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    document_type VARCHAR(50),
    file_name VARCHAR(255),
    file_path TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'STUDENT'))
);

-- Seed students
INSERT INTO students (student_id, first_name, middle_name, last_name) VALUES
(1, 'Aarav', 'Kumar', 'Sharma'),
(2, 'Ananya', 'R', 'Iyer'),
(3, 'Rohan', 'S', 'Patil'),
(4, 'Priya', 'M', 'Nair'),
(5, 'Vikram', 'T', 'Singh');

INSERT INTO student_contact (contact_id, student_id, address, mobile_no, email) VALUES
(1, 1, 'Baner, Pune, Maharashtra', '9876543210', 'aarav.sharma@college.in'),
(2, 2, 'Indiranagar, Bengaluru, Karnataka', '9876543211', 'ananya.iyer@college.in'),
(3, 3, 'Kothrud, Pune, Maharashtra', '9876543212', 'rohan.patil@college.in'),
(4, 4, 'Kowdiar, Thiruvananthapuram, Kerala', '9876543213', 'priya.nair@college.in'),
(5, 5, 'Gomti Nagar, Lucknow, Uttar Pradesh', '9876543214', 'vikram.singh@college.in');

INSERT INTO admission_details (
    admission_id, student_id, registration_id, date_of_registration, prn,
    admitted_academic_year, current_academic_year, admission_pattern, fees
) VALUES
-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
(1, 1, 'REG-MH-2026-0001', '2026-04-01', 'PRN2026MH0001', 2026, 1, 'regular', 85000),
(2, 2, 'REG-KA-2025-0002', '2025-06-20', 'PRN2025KA0002', 2025, 2, 'management', 150000),
(3, 3, 'REG-MH-2024-0003', '2024-07-12', 'PRN2024MH0003', 2024, 3, 'regular', 78000),
(4, 4, 'REG-KL-2026-0004', '2026-03-15', 'PRN2026KL0004', 2026, 1, 'management', 135000),
(5, 5, 'REG-UP-2023-0005', '2023-08-02', 'PRN2023UP0005', 2023, 4, 'regular', 72000);
-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====

INSERT INTO personal_info (
    personal_id, student_id, birth_place, nationality, gender, category,
    religion, domicile_state, blood_group
) VALUES
(1, 1, 'Nagpur', 'Indian', 'male', 'OBC', 'Hindu', 'Maharashtra', 'B+'),
(2, 2, 'Chennai', 'Indian', 'female', 'OPEN', 'Hindu', 'Karnataka', 'A+'),
(3, 3, 'Kolhapur', 'Indian', 'male', 'SC', 'Hindu', 'Maharashtra', 'O+'),
(4, 4, 'Kochi', 'Indian', 'female', 'OBC', 'Hindu', 'Kerala', 'AB+'),
(5, 5, 'Varanasi', 'Indian', 'male', 'OPEN', 'Sikh', 'Uttar Pradesh', 'A-');

-- ===== ADDED: PROFILE_IMAGE_PATH START =====
UPDATE students s
SET profile_image_path = CASE
    WHEN lower(coalesce(pi.gender, '')) = 'male' THEN 'defaults/male_icon.jpg'
    ELSE 'defaults/female_icon.jpg'
END
FROM personal_info pi
WHERE pi.student_id = s.student_id;
-- ===== ADDED: PROFILE_IMAGE_PATH END =====

INSERT INTO parent_details (
    parent_id, student_id, father_first_name, father_middle_name, father_last_name, father_contact_no,
    mother_first_name, mother_middle_name, mother_last_name, mother_contact_no
) VALUES
(1, 1, 'Rajesh', 'P', 'Sharma', '9822011111', 'Sunita', 'R', 'Sharma', '9890011111'),
(2, 2, 'Raghavan', 'K', 'Iyer', '9822022222', 'Lakshmi', 'R', 'Iyer', '9890022222'),
(3, 3, 'Suresh', 'M', 'Patil', '9822033333', 'Meena', 'S', 'Patil', '9890033333'),
(4, 4, 'Mohan', 'P', 'Nair', '9822044444', 'Deepa', 'M', 'Nair', '9890044444'),
(5, 5, 'Harpreet', 'S', 'Singh', '9822055555', 'Gurpreet', 'K', 'Singh', '9890055555');

INSERT INTO documents (document_id, student_id, document_type, file_name, file_path) VALUES
(1, 1, 'Aadhaar', 'aadhaar_aarav.pdf', '/docs/aadhaar/aadhaar_aarav.pdf'),
(2, 1, '10th Marksheet', '10th_aarav.pdf', '/docs/marksheets/10th_aarav.pdf'),
(3, 2, 'Aadhaar', 'aadhaar_ananya.pdf', '/docs/aadhaar/aadhaar_ananya.pdf'),
(4, 3, 'PAN', 'pan_rohan.pdf', '/docs/pan/pan_rohan.pdf'),
(5, 4, '12th Marksheet', '12th_priya.pdf', '/docs/marksheets/12th_priya.pdf'),
(6, 5, 'Aadhaar', 'aadhaar_vikram.pdf', '/docs/aadhaar/aadhaar_vikram.pdf');

-- Default users are inserted by AuthDataInitializer on app startup.

SELECT setval('students_student_id_seq', (SELECT MAX(student_id) FROM students));
SELECT setval('student_contact_contact_id_seq', (SELECT MAX(contact_id) FROM student_contact));
SELECT setval('admission_details_admission_id_seq', (SELECT MAX(admission_id) FROM admission_details));
SELECT setval('personal_info_personal_id_seq', (SELECT MAX(personal_id) FROM personal_info));
SELECT setval('parent_details_parent_id_seq', (SELECT MAX(parent_id) FROM parent_details));
SELECT setval('documents_document_id_seq', (SELECT MAX(document_id) FROM documents));
SELECT setval('users_user_id_seq', COALESCE((SELECT MAX(user_id) FROM users), 1), true);

COMMIT;

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

-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
UPDATE admission_details
SET current_academic_year = LEAST(
    4,
    GREATEST(1, EXTRACT(YEAR FROM CURRENT_DATE)::INT - admitted_academic_year + 1)
)
WHERE admitted_academic_year IS NOT NULL;
-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====

