package com.example.student_management.controller;

import com.example.student_management.designpatterns.facade.StudentManagementFacade;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentManagementFacade studentManagementFacade;

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
    public ResponseEntity<DocumentResponse> uploadStudentDocument(
            @PathVariable("id") Integer studentId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentManagementFacade.uploadStudentDocument(studentId, documentType, file));
    }

    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API START =====
    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentProfileResponse> uploadStudentProfileImage(
            @PathVariable("id") Integer studentId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentManagementFacade.uploadStudentProfileImage(studentId, file));
    }
    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API END =====

    @GetMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> getStudent(@PathVariable("id") Integer studentId) {
        return ResponseEntity.ok(studentManagementFacade.getStudentById(studentId));
    }

    @GetMapping("/summary/{id}")
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
}
