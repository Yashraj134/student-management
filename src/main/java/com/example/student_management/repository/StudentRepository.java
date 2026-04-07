package com.example.student_management.repository;

import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import com.example.student_management.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("""
            select distinct s from Student s
            left join fetch s.studentContact
            left join fetch s.admissionDetails
            left join fetch s.personalInfo
            left join fetch s.parentDetails
            left join fetch s.documents
            where s.studentId = :id
            """)
    Optional<Student> findFullProfileById(@Param("id") Integer id);

    // ===== ADDED: PROFILE_IMAGE_PATH_SUMMARY_QUERIES START =====
    @Query(value = """
            select new com.example.student_management.dto.StudentSummaryResponse(
                s.studentId,
                trim(concat(coalesce(s.firstName, ''), ' ', coalesce(s.middleName, ''), ' ', coalesce(s.lastName, ''))),
                ad.prn,
                sc.email,
                coalesce(s.profileImagePath, case when lower(coalesce(pi.gender, '')) = 'male' then 'defaults/male_icon.jpg' else 'defaults/female_icon.jpg' end)
            )
            from Student s
            left join s.studentContact sc
            left join s.admissionDetails ad
            left join s.personalInfo pi
            where lower(coalesce(s.firstName, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(s.lastName, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(sc.email, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(ad.prn, '')) like lower(concat('%', :keyword, '%'))
            """,
            countQuery = """
                    select count(s)
                    from Student s
                    left join s.studentContact sc
                    left join s.admissionDetails ad
                    where lower(coalesce(s.firstName, '')) like lower(concat('%', :keyword, '%'))
                       or lower(coalesce(s.lastName, '')) like lower(concat('%', :keyword, '%'))
                       or lower(coalesce(sc.email, '')) like lower(concat('%', :keyword, '%'))
                       or lower(coalesce(ad.prn, '')) like lower(concat('%', :keyword, '%'))
                    """)
    Page<StudentSummaryResponse> searchStudents(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
            select new com.example.student_management.dto.StudentSummaryResponse(
                s.studentId,
                trim(concat(coalesce(s.firstName, ''), ' ', coalesce(s.middleName, ''), ' ', coalesce(s.lastName, ''))),
                ad.prn,
                sc.email,
                coalesce(s.profileImagePath, case when lower(coalesce(pi.gender, '')) = 'male' then 'defaults/male_icon.jpg' else 'defaults/female_icon.jpg' end)
            )
            from Student s
            left join s.studentContact sc
            left join s.admissionDetails ad
            left join s.personalInfo pi
            """,
            countQuery = """
                    select count(s)
                    from Student s
                    """)
    Page<StudentSummaryResponse> findAllSummaries(Pageable pageable);

    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS START =====
    @Query("""
            select distinct s from Student s
            left join fetch s.studentContact
            left join fetch s.admissionDetails
            left join fetch s.personalInfo
            left join fetch s.parentDetails
            left join fetch s.documents
            """)
    List<Student> findAllFullProfiles();

    @Query("""
            select new com.example.student_management.dto.StudentSummaryResponse(
                s.studentId,
                trim(concat(coalesce(s.firstName, ''), ' ', coalesce(s.middleName, ''), ' ', coalesce(s.lastName, ''))),
                ad.prn,
                sc.email,
                coalesce(s.profileImagePath, case when lower(coalesce(pi.gender, '')) = 'male' then 'defaults/male_icon.jpg' else 'defaults/female_icon.jpg' end)
            )
            from Student s
            left join s.studentContact sc
            left join s.admissionDetails ad
            left join s.personalInfo pi
            """)
    List<StudentSummaryResponse> findAllSummaryList();
    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS END =====

    @Query(value = """
            select new com.example.student_management.dto.StudentSummaryResponse(
                s.studentId,
                trim(concat(coalesce(s.firstName, ''), ' ', coalesce(s.middleName, ''), ' ', coalesce(s.lastName, ''))),
                ad.prn,
                sc.email,
                coalesce(s.profileImagePath, case when lower(coalesce(pi.gender, '')) = 'male' then 'defaults/male_icon.jpg' else 'defaults/female_icon.jpg' end)
            )
            from Student s
            left join s.studentContact sc
            left join s.admissionDetails ad
            left join s.personalInfo pi
            where (:currentAcademicYear is null or ad.currentAcademicYear = :currentAcademicYear)
              and (:admissionPattern is null or lower(ad.admissionPattern) = lower(:admissionPattern))
            """,
            countQuery = """
                    select count(s)
                    from Student s
                    left join s.admissionDetails ad
                    where (:currentAcademicYear is null or ad.currentAcademicYear = :currentAcademicYear)
                      and (:admissionPattern is null or lower(ad.admissionPattern) = lower(:admissionPattern))
                    """)
    Page<StudentSummaryResponse> filterStudents(
            @Param("currentAcademicYear") Integer currentAcademicYear,
            @Param("admissionPattern") String admissionPattern,
            Pageable pageable);

    @Query("""
            select new com.example.student_management.dto.StudentDashboardStatsResponse(
                count(s),
                sum(case when lower(ad.admissionPattern) = 'regular' then 1 else 0 end),
                sum(case when lower(ad.admissionPattern) = 'management' then 1 else 0 end)
            )
            from Student s
            left join s.admissionDetails ad
            """)
    StudentDashboardStatsResponse fetchDashboardStats();

    @Query("""
            select new com.example.student_management.dto.YearWiseStudentStatsResponse(
                ad.currentAcademicYear,
                count(s)
            )
            from Student s
            join s.admissionDetails ad
            group by ad.currentAcademicYear
            order by ad.currentAcademicYear
            """)
    List<YearWiseStudentStatsResponse> fetchYearWiseStats();

    @Query("""
            select new com.example.student_management.dto.StudentSummaryResponse(
                s.studentId,
                trim(concat(coalesce(s.firstName, ''), ' ', coalesce(s.middleName, ''), ' ', coalesce(s.lastName, ''))),
                ad.prn,
                sc.email,
                coalesce(s.profileImagePath, case when lower(coalesce(pi.gender, '')) = 'male' then 'defaults/male_icon.jpg' else 'defaults/female_icon.jpg' end)
            )
            from Student s
            left join s.studentContact sc
            left join s.admissionDetails ad
            left join s.personalInfo pi
            where s.studentId = :studentId
            """)
    Optional<StudentSummaryResponse> findSummaryByStudentId(@Param("studentId") Integer studentId);
    // ===== ADDED: PROFILE_IMAGE_PATH_SUMMARY_QUERIES END =====
}
