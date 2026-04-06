package com.example.student_management.designpatterns.facade;

import com.example.student_management.aop.AuditAction;
import com.example.student_management.aop.CheckDuplicateStudent;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import com.example.student_management.service.AuditService;
import com.example.student_management.service.DocumentService;
import com.example.student_management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentManagementFacadeImpl implements StudentManagementFacade {

    private final StudentService studentService;
    private final DocumentService documentService;
    private final AuditService auditService;

    @Override
    @CheckDuplicateStudent
    @AuditAction("CREATE_STUDENT")
    public StudentProfileResponse createStudent(StudentUpsertRequest request) {
        StudentProfileResponse response = studentService.createStudent(request);
        auditService.logAction("CREATE_STUDENT", response.getStudentId());
        return response;
    }

    @Override
    @AuditAction("BULK_CREATE_STUDENT")
    public List<StudentProfileResponse> bulkCreateStudents(List<StudentUpsertRequest> requests) {
        List<StudentProfileResponse> responses = studentService.bulkCreateStudents(requests);
        responses.forEach(response -> auditService.logAction("CREATE_STUDENT", response.getStudentId()));
        return responses;
    }

    @Override
    public StudentProfileResponse getStudentById(Integer studentId) {
        return studentService.getStudentById(studentId);
    }

    @Override
    public StudentSummaryResponse getStudentSummaryById(Integer studentId) {
        return studentService.getStudentSummaryById(studentId);
    }

    @Override
    public PagedResponse<StudentSummaryResponse> getAllStudents(Pageable pageable) {
        return studentService.getAllStudents(pageable);
    }

    @Override
    public PagedResponse<StudentSummaryResponse> searchStudents(String keyword, Pageable pageable) {
        return studentService.searchStudents(keyword, pageable);
    }

    @Override
    public PagedResponse<StudentSummaryResponse> filterStudents(Integer currentAcademicYear, String admissionPattern, Pageable pageable) {
        return studentService.filterStudents(currentAcademicYear, admissionPattern, pageable);
    }

    @Override
    public StudentDashboardStatsResponse getDashboardStats() {
        return studentService.getDashboardStats();
    }

    @Override
    public List<YearWiseStudentStatsResponse> getYearWiseStats() {
        return studentService.getYearWiseStats();
    }

    @Override
    @CheckDuplicateStudent
    @AuditAction("UPDATE_STUDENT")
    public StudentProfileResponse updateStudent(Integer studentId, StudentUpsertRequest request) {
        StudentProfileResponse response = studentService.updateStudent(studentId, request);
        auditService.logAction("UPDATE_STUDENT", response.getStudentId());
        return response;
    }

    @Override
    @AuditAction("DELETE_STUDENT")
    public void deleteStudent(Integer studentId) {
        documentService.deleteByStudentId(studentId);
        studentService.deleteStudent(studentId);
        auditService.logAction("DELETE_STUDENT", studentId);
    }
}
