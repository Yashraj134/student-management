package com.example.student_management.controller;

import com.example.student_management.designpatterns.facade.StudentManagementFacade;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import com.example.student_management.entity.Student;
import com.example.student_management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentManagementFacade studentManagementFacade;
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentProfileResponse> createStudent(@Valid @RequestBody StudentUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentManagementFacade.createStudent(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<StudentProfileResponse>> bulkCreateStudents(
            @Valid @RequestBody List<StudentUpsertRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentManagementFacade.bulkCreateStudents(requests));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<DocumentResponse> uploadStudentDocument(
            @PathVariable("id") Integer studentId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentManagementFacade.uploadStudentDocument(studentId, documentType, file));
    }

    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API START =====
    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<StudentProfileResponse> uploadStudentProfileImage(
            @PathVariable("id") Integer studentId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentManagementFacade.uploadStudentProfileImage(studentId, file));
    }
    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API END =====

    @GetMapping("/{id}")
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<StudentProfileResponse> getStudent(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentManagementFacade.getStudentById(studentId));
    }

    @GetMapping("/summary/{id}")
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<StudentSummaryResponse> getStudentSummary(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentManagementFacade.getStudentSummaryById(studentId));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<StudentSummaryResponse>> getStudents(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(studentManagementFacade.getAllStudents(pageable));
    }

    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS START =====
    @GetMapping("/all")
    public ResponseEntity<List<StudentProfileResponse>> getAllStudents() {
        return ResponseEntity.ok(studentManagementFacade.getAllStudentsList());
    }

    @GetMapping("/summary/all")
    public ResponseEntity<List<StudentSummaryResponse>> getAllSummary() {
        return ResponseEntity.ok(studentManagementFacade.getAllSummary());
    }
    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS END =====

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<StudentSummaryResponse>> searchStudents(
            @RequestParam("keyword") String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(studentManagementFacade.searchStudents(keyword, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<StudentSummaryResponse>> filterStudents(
            @RequestParam(value = "currentAcademicYear", required = false) Integer currentAcademicYear,
            @RequestParam(value = "admissionPattern", required = false) String admissionPattern,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(studentManagementFacade.filterStudents(currentAcademicYear, admissionPattern, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<StudentDashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(studentManagementFacade.getDashboardStats());
    }

    @GetMapping("/stats/year-wise")
    public ResponseEntity<List<YearWiseStudentStatsResponse>> getYearWiseStats() {
        return ResponseEntity.ok(studentManagementFacade.getYearWiseStats());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> updateStudent(
            @PathVariable("id") Integer studentId,
            @Valid @RequestBody StudentUpsertRequest request) {
        return ResponseEntity.ok(studentManagementFacade.updateStudent(studentId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") Integer studentId) {
        studentManagementFacade.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping({"/{id}/request-id-card", "/{id}/id-card/request"})
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<Student> requestIdCard(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentService.requestIdCard(studentId));
    }

    @GetMapping({"/id-card-requests", "/id-card/pending"})
    public ResponseEntity<List<Student>> getPendingIdCardRequests() {
        return ResponseEntity.ok(studentService.getPendingRequests());
    }

    @PutMapping({"/{id}/approve-id-card", "/{id}/id-card/approve"})
    public ResponseEntity<Student> approveIdCard(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentService.approveIdCard(studentId));
    }

    @PutMapping({"/{id}/reject-id-card", "/{id}/id-card/reject"})
    public ResponseEntity<Student> rejectIdCard(
            @PathVariable("id") Integer studentId,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestBody(required = false) Map<String, String> payload) {
        String resolvedRemark = remark;
        if ((resolvedRemark == null || resolvedRemark.isBlank()) && payload != null) {
            resolvedRemark = payload.get("remark");
        }
        if (resolvedRemark == null || resolvedRemark.isBlank()) {
            throw new IllegalArgumentException("remark is required");
        }
        return ResponseEntity.ok(studentService.rejectIdCard(studentId, resolvedRemark.trim()));
    }

    @GetMapping({"/{id}/id-card-status", "/{id}/id-card/status"})
    @PreAuthorize("@studentAccessEvaluator.canAccessStudent(authentication, #p0)")
    public ResponseEntity<Map<String, String>> getIdCardStatus(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentService.getIdCardStatus(studentId));
    }
}
