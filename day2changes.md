# Day 2 Changes

## Modified Files

1. `src/main/java/com/example/student_management/service/impl/StudentServiceImpl.java`
   - Added derived current academic year logic.
   - Added marker comments:
     - `// ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====`
     - `// ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====`

2. `src/main/java/com/example/student_management/dto/AdmissionDetailsRequest.java`
   - Updated `currentAcademicYear` validation to study year range `1..4`.
   - Added marker comments around this change.

3. `database/pgadmin_student_management_setup.sql`
   - Updated seed values of `current_academic_year` to `1..4`.
   - Added one-time backfill query for existing data.
   - Added marker comments around all Day 2 SQL changes.

## Newly Created Files

1. `day2changes.md`

## pgAdmin Queries to Run (Existing Databases)

Run this query once to convert existing rows from calendar year style to study year style:

```sql
-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
UPDATE admission_details
SET current_academic_year = LEAST(
    4,
    GREATEST(1, EXTRACT(YEAR FROM CURRENT_DATE)::INT - admitted_academic_year + 1)
)
WHERE admitted_academic_year IS NOT NULL;
-- ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====
```

## Notes

- `currentAcademicYear` is now derived by backend as:
  - `current calendar year - admittedAcademicYear + 1`
  - clamped to range `1..4`
- This keeps values aligned to `1st/2nd/3rd/4th year` semantics.

