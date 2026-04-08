package com.example.student_management.service.impl;

import com.example.student_management.designpatterns.factory.AdmissionDetailsFactory;
import com.example.student_management.designpatterns.factory.AdmissionDetailsFactoryProvider;
import com.example.student_management.designpatterns.observer.StudentCreationSubject;
import com.example.student_management.designpatterns.strategy.AdmissionValidationContext;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.StudentDashboardStatsResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.dto.YearWiseStudentStatsResponse;
import com.example.student_management.entity.AdmissionDetails;
import com.example.student_management.entity.Student;
import com.example.student_management.exception.ResourceNotFoundException;
import com.example.student_management.mapper.StudentMapper;
import com.example.student_management.repository.StudentRepository;
import com.example.student_management.service.DocumentService;
import com.example.student_management.service.FileStorageService;
import com.example.student_management.service.StudentService;
import java.io.IOException;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
    private static final String DEFAULT_MALE_IMAGE_PATH = "defaults/male_icon.jpg";
    private static final String DEFAULT_FEMALE_IMAGE_PATH = "defaults/female_icon.jpg";
    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final AdmissionValidationContext admissionValidationContext;
    private final AdmissionDetailsFactoryProvider admissionDetailsFactoryProvider;
    private final StudentCreationSubject studentCreationSubject;

    @Override
    @Transactional
    public StudentProfileResponse createStudent(StudentUpsertRequest request) {
        // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
        deriveAndSetCurrentAcademicYear(request);
        // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====
        admissionValidationContext.validate(request.getAdmissionDetails());
        AdmissionDetailsFactory factory = admissionDetailsFactoryProvider.getFactory(request.getAdmissionDetails().getAdmissionPattern());

        Student student = studentMapper.toStudentEntity(request);
        AdmissionDetails admissionDetails = factory.create(request.getAdmissionDetails());
        student.setAdmissionDetails(admissionDetails);
        documentService.replaceDocuments(student, request.getDocuments());
        // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
        applyDefaultProfileImagePath(student);
        // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

        Student savedStudent = studentRepository.save(student);
        studentCreationSubject.notifyStudentCreated(savedStudent);
        return studentMapper.toProfileResponse(savedStudent);
    }

    @Override
    @Transactional
    public List<StudentProfileResponse> bulkCreateStudents(List<StudentUpsertRequest> requests) {
        return requests.stream().map(this::createStudent).toList();
    }

    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API START =====
    @Override
    @Transactional
    public StudentProfileResponse uploadStudentProfileImage(Integer studentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required and must not be empty");
        }

        Student student = studentRepository.findFullProfileById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        String storedPath;
        try {
            storedPath = fileStorageService.store(studentId, "profile-image", file);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store profile image", ex);
        }

        student.setProfileImagePath(storedPath);
        Student updated = studentRepository.save(student);
        return studentMapper.toProfileResponse(updated);
    }
    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API END =====

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentById(Integer studentId) {
        Student student = studentRepository.findFullProfileById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return studentMapper.toProfileResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSummaryResponse getStudentSummaryById(Integer studentId) {
        return studentRepository.findSummaryByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> getAllStudents(Pageable pageable) {
        Page<StudentSummaryResponse> page = studentRepository.findAllSummaries(pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS START =====
    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getAllStudentsList() {
        return studentRepository.findAllFullProfiles().stream()
                .map(studentMapper::toProfileResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSummaryResponse> getAllSummary() {
        return studentRepository.findAllSummaryList();
    }
    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS END =====

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> searchStudents(String keyword, Pageable pageable) {
        String searchKeyword = keyword == null ? "" : keyword.trim();
        Page<StudentSummaryResponse> page = studentRepository.searchStudents(searchKeyword, pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> filterStudents(Integer currentAcademicYear, String admissionPattern, Pageable pageable) {
        String normalizedPattern = admissionPattern == null || admissionPattern.isBlank() ? null : admissionPattern.trim();
        Page<StudentSummaryResponse> page = studentRepository.filterStudents(currentAcademicYear, normalizedPattern, pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardStatsResponse getDashboardStats() {
        StudentDashboardStatsResponse stats = studentRepository.fetchDashboardStats();
        if (stats == null) {
            return StudentDashboardStatsResponse.builder()
                    .totalStudents(0L)
                    .regularStudents(0L)
                    .managementStudents(0L)
                    .build();
        }
        return StudentDashboardStatsResponse.builder()
                .totalStudents(stats.getTotalStudents() == null ? 0L : stats.getTotalStudents())
                .regularStudents(stats.getRegularStudents() == null ? 0L : stats.getRegularStudents())
                .managementStudents(stats.getManagementStudents() == null ? 0L : stats.getManagementStudents())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YearWiseStudentStatsResponse> getYearWiseStats() {
        return studentRepository.fetchYearWiseStats();
    }

    @Override
    @Transactional
    public StudentProfileResponse updateStudent(Integer studentId, StudentUpsertRequest request) {
        Student existingStudent = studentRepository.findFullProfileById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
        deriveAndSetCurrentAcademicYear(request);
        // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====
        admissionValidationContext.validate(request.getAdmissionDetails());
        AdmissionDetailsFactory factory = admissionDetailsFactoryProvider.getFactory(request.getAdmissionDetails().getAdmissionPattern());

        studentMapper.updateStudentEntity(existingStudent, request);
        existingStudent.setAdmissionDetails(factory.create(request.getAdmissionDetails()));
        documentService.replaceDocuments(existingStudent, request.getDocuments());
        // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
        applyDefaultProfileImagePath(existingStudent);
        // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

        Student updated = studentRepository.save(existingStudent);
        return studentMapper.toProfileResponse(updated);
    }

    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
    private void applyDefaultProfileImagePath(Student student) {
        if (StringUtils.hasText(student.getProfileImagePath())) {
            student.setProfileImagePath(student.getProfileImagePath().trim());
            return;
        }

        String gender = student.getPersonalInfo() != null ? student.getPersonalInfo().getGender() : null;
        if (StringUtils.hasText(gender) && "male".equalsIgnoreCase(gender.trim())) {
            student.setProfileImagePath(DEFAULT_MALE_IMAGE_PATH);
            return;
        }
        student.setProfileImagePath(DEFAULT_FEMALE_IMAGE_PATH);
    }
    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

    // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
    private void deriveAndSetCurrentAcademicYear(StudentUpsertRequest request) {
        if (request == null || request.getAdmissionDetails() == null || request.getAdmissionDetails().getAdmittedAcademicYear() == null) {
            throw new IllegalArgumentException("admittedAcademicYear is required");
        }

        int currentCalendarYear = Year.now().getValue();
        int admittedAcademicYear = request.getAdmissionDetails().getAdmittedAcademicYear();
        int derivedYear = currentCalendarYear - admittedAcademicYear + 1;

        if (derivedYear < 1) {
            derivedYear = 1;
        } else if (derivedYear > 4) {
            derivedYear = 4;
        }

        request.getAdmissionDetails().setCurrentAcademicYear(derivedYear);
    }
    // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====

    @Override
    @Transactional
    public void deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        studentRepository.delete(student);
    }
}
