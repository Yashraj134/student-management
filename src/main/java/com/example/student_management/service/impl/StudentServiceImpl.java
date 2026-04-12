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
import com.example.student_management.entity.StudentContact;
import com.example.student_management.exception.DuplicateResourceException;
import com.example.student_management.exception.ResourceNotFoundException;
import com.example.student_management.mapper.StudentMapper;
import com.example.student_management.repository.AdmissionDetailsRepository;
import com.example.student_management.repository.StudentRepository;
import com.example.student_management.repository.StudentContactRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for student management operations.
 *
 * <p>This class contains the main business rules for student create/update/read flows,
 * profile image handling, dashboard queries, and ID card request workflow.</p>
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
    private static final String DEFAULT_MALE_IMAGE_PATH = "defaults/male_icon.jpg";
    private static final String DEFAULT_FEMALE_IMAGE_PATH = "defaults/female_icon.jpg";
    private static final String STATUS_NOT_REQUESTED = "NOT_REQUESTED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

    private final StudentRepository studentRepository;
    private final StudentContactRepository studentContactRepository;
    private final AdmissionDetailsRepository admissionDetailsRepository;
    private final StudentMapper studentMapper;
    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final AdmissionValidationContext admissionValidationContext;
    private final AdmissionDetailsFactoryProvider admissionDetailsFactoryProvider;
    private final StudentCreationSubject studentCreationSubject;

    /**
     * Creates a new student after validation, derives academic year, applies defaults,
     * and stores related nested data.
     *
     * @param request complete student input payload
     * @return created student profile response
     */
    @Override
    @Transactional
    public StudentProfileResponse createStudent(StudentUpsertRequest request) {
        validateUniqueEmailAndPrn(request, null);
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

    /**
     * Creates multiple students using the same validations as single create.
     *
     * @param requests list of student create payloads
     * @return list of created student profiles
     */
    @Override
    @Transactional
    public List<StudentProfileResponse> bulkCreateStudents(List<StudentUpsertRequest> requests) {
        return requests.stream().map(this::createStudent).toList();
    }

    // ===== ADDED: PROFILE_IMAGE_UPLOAD_API START =====
    /**
     * Uploads and updates profile image for a student.
     *
     * @param studentId target student id
     * @param file image file to store
     * @return updated student profile response
     */
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

    /**
     * Fetches full student profile by id.
     *
     * @param studentId student id
     * @return full student profile response
     */
    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentById(Integer studentId) {
        Student student = studentRepository.findFullProfileById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return studentMapper.toProfileResponse(student);
    }

    /**
     * Fetches lightweight summary data for one student.
     *
     * @param studentId student id
     * @return student summary response
     */
    @Override
    @Transactional(readOnly = true)
    public StudentSummaryResponse getStudentSummaryById(Integer studentId) {
        return studentRepository.findSummaryByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    /**
     * Returns paginated student summaries.
     *
     * @param pageable paging information
     * @return paged summary response
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> getAllStudents(Pageable pageable) {
        Page<StudentSummaryResponse> page = studentRepository.findAllSummaries(pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS START =====
    /**
     * Returns full profile list for all students.
     *
     * @return list of full student profiles
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> getAllStudentsList() {
        return studentRepository.findAllFullProfiles().stream()
                .map(studentMapper::toProfileResponse)
                .toList();
    }

    /**
     * Returns summary list for all students.
     *
     * @return list of student summaries
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentSummaryResponse> getAllSummary() {
        return studentRepository.findAllSummaryList();
    }
    // ===== ADDED: GET_ALL_STUDENTS_AND_SUMMARY_APIS END =====

    /**
     * Searches students by keyword across supported fields.
     *
     * @param keyword search text
     * @param pageable paging information
     * @return paged summary response
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> searchStudents(String keyword, Pageable pageable) {
        String searchKeyword = keyword == null ? "" : keyword.trim();
        Page<StudentSummaryResponse> page = studentRepository.searchStudents(searchKeyword, pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    /**
     * Filters students by optional academic year and admission pattern.
     *
     * @param currentAcademicYear optional current academic year
     * @param admissionPattern optional admission type
     * @param pageable paging information
     * @return paged summary response
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentSummaryResponse> filterStudents(Integer currentAcademicYear, String admissionPattern, Pageable pageable) {
        String normalizedPattern = admissionPattern == null || admissionPattern.isBlank() ? null : admissionPattern.trim();
        Page<StudentSummaryResponse> page = studentRepository.filterStudents(currentAcademicYear, normalizedPattern, pageable);
        return studentMapper.toPagedSummaryResponse(page);
    }

    /**
     * Loads dashboard card statistics.
     *
     * @return total, regular, and management student counts
     */
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

    /**
     * Loads year-wise student count data for chart usage.
     *
     * @return year-wise stats list
     */
    @Override
    @Transactional(readOnly = true)
    public List<YearWiseStudentStatsResponse> getYearWiseStats() {
        return studentRepository.fetchYearWiseStats();
    }

    /**
     * Raises ID card request when current state allows it.
     * Allowed transitions: NOT_REQUESTED/REJECTED -> PENDING.
     *
     * @param id student id
     * @return updated student entity
     */
    @Override
    @Transactional
    public Student requestIdCard(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String currentStatus = normalizeStatus(student.getIdCardStatus());
        if (STATUS_NOT_REQUESTED.equals(currentStatus) || STATUS_REJECTED.equals(currentStatus)) {
            student.setIdCardStatus(STATUS_PENDING);
            student.setIdCardRemark(null);
            return studentRepository.save(student);
        }

        throw new IllegalArgumentException("ID card request can be raised only when status is NOT_REQUESTED or REJECTED");
    }

    /**
     * Returns students whose ID card request is currently pending.
     *
     * @return list of pending student entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Student> getPendingRequests() {
        return studentRepository.findByIdCardStatus(STATUS_PENDING);
    }

    /**
     * Approves ID card request for a student.
     *
     * @param id student id
     * @return updated student entity
     */
    @Override
    @Transactional
    public Student approveIdCard(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setIdCardStatus(STATUS_APPROVED);
        student.setIdCardRemark(null);
        return studentRepository.save(student);
    }

    /**
     * Rejects ID card request and stores rejection remark.
     *
     * @param id student id
     * @param remark rejection reason
     * @return updated student entity
     */
    @Override
    @Transactional
    public Student rejectIdCard(Integer id, String remark) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setIdCardStatus(STATUS_REJECTED);
        student.setIdCardRemark(remark);
        return studentRepository.save(student);
    }

    /**
     * Returns only the ID card status and remark for one student.
     *
     * @param id student id
     * @return map with keys status and remark
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getIdCardStatus(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        Map<String, String> response = new HashMap<>();
        response.put("status", normalizeStatus(student.getIdCardStatus()));
        response.put("remark", student.getIdCardRemark());
        return response;
    }

    /**
     * Updates existing student data and related nested entities.
     *
     * @param studentId target student id
     * @param request updated student payload
     * @return updated student profile response
     */
    @Override
    @Transactional
    public StudentProfileResponse updateStudent(Integer studentId, StudentUpsertRequest request) {
        Student existingStudent = studentRepository.findFullProfileById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        validateUniqueEmailAndPrn(request, studentId);
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
    /**
     * Applies default profile image when no custom path is provided.
     * Male students get male default, others get female default.
     */
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

    /**
     * Validates unique constraints for student email and PRN.
     *
     * @param request incoming student payload
     * @param studentId existing student id for update, null for create
     */
    private void validateUniqueEmailAndPrn(StudentUpsertRequest request, Integer studentId) {
        String email = request.getStudentContact().getEmail();
        StudentContact existingContact = studentContactRepository.findByEmail(email).orElse(null);
        if (existingContact != null && !existingContact.getStudent().getStudentId().equals(studentId)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        String prn = request.getAdmissionDetails().getPrn();
        AdmissionDetails existingAdmission = admissionDetailsRepository.findByPrn(prn).orElse(null);
        if (existingAdmission != null && !existingAdmission.getStudent().getStudentId().equals(studentId)) {
            throw new DuplicateResourceException("PRN already exists: " + prn);
        }
    }

    // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
    /**
     * Derives current academic year from admitted year and current calendar year.
     * The computed value is clamped between 1 and 4.
     */
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

    /**
     * Deletes a student by id.
     *
     * @param studentId target student id
     */
    @Override
    @Transactional
    public void deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        studentRepository.delete(student);
    }

    /**
     * Normalizes ID card status text to uppercase and returns default status when blank.
     */
    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return STATUS_NOT_REQUESTED;
        }
        return status.trim().toUpperCase();
    }
}
