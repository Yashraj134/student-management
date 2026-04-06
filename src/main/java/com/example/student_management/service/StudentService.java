package com.example.student_management.service;

import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    StudentProfileResponse createStudent(StudentUpsertRequest request);

    List<StudentProfileResponse> bulkCreateStudents(List<StudentUpsertRequest> requests);

    StudentProfileResponse getStudentById(Integer studentId);

    StudentSummaryResponse getStudentSummaryById(Integer studentId);

    PagedResponse<StudentSummaryResponse> getAllStudents(Pageable pageable);

    PagedResponse<StudentSummaryResponse> searchStudents(String keyword, Pageable pageable);

    PagedResponse<StudentSummaryResponse> filterStudents(
            Integer currentAcademicYear,
            String admissionPattern,
            Pageable pageable);

    StudentDashboardStatsResponse getDashboardStats();

    List<YearWiseStudentStatsResponse> getYearWiseStats();

    StudentProfileResponse updateStudent(Integer studentId, StudentUpsertRequest request);

    void deleteStudent(Integer studentId);
}
