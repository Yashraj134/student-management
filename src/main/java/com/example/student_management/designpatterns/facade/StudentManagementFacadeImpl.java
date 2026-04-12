package com.example.student_management.designpatterns.facade;

import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import com.example.student_management.service.AuditService;
import com.example.student_management.service.DocumentService;
import com.example.student_management.service.StudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class StudentManagementFacadeImpl implements StudentManagementFacade {

    private final StudentService studentService;
    private final DocumentService documentService;
    private final AuditService auditService;

    @Override
    public StudentProfileResponse createStudent(StudentUpsertRequest request) {
        StudentProfileResponse response = studentService.createStudent(request);
        auditService.logAction("CREATE_STUDENT", response.getStudentId());
        return response;
    }

    @Override
    public List<StudentProfileResponse> bulkCreateStudents(List<StudentUpsertRequest> requests) {
        List<StudentProfileResponse> responses = studentService.bulkCreateStudents(requests);
        responses.forEach(response -> auditService.logAction("CREATE_STUDENT", response.getStudentId()));
        return responses;
    }

    @Override
    public DocumentResponse uploadStudentDocument(Integer studentId, String documentType, MultipartFile file) {
        DocumentResponse response = documentService.uploadDocument(studentId, documentType, file);
        auditService.logAction("UPLOAD_STUDENT_DOCUMENT", studentId);
        return response;
    }

    @Override
    public StudentProfileResponse uploadStudentProfileImage(Integer studentId, MultipartFile file) {
        StudentProfileResponse response = studentService.uploadStudentProfileImage(studentId, file);
        auditService.logAction("UPLOAD_PROFILE_IMAGE", studentId);
        return response;
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
    public List<StudentProfileResponse> getAllStudentsList() {
        return studentService.getAllStudentsList();
    }

    @Override
    public List<StudentSummaryResponse> getAllSummary() {
        return studentService.getAllSummary();
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
    public StudentProfileResponse updateStudent(Integer studentId, StudentUpsertRequest request) {
        StudentProfileResponse response = studentService.updateStudent(studentId, request);
        auditService.logAction("UPDATE_STUDENT", response.getStudentId());
        return response;
    }

    @Override
    public void deleteStudent(Integer studentId) {
        documentService.deleteByStudentId(studentId);
        studentService.deleteStudent(studentId);
        auditService.logAction("DELETE_STUDENT", studentId);
    }
}
