package com.example.student_management.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic DTO that consolidates all fields from every request and response DTO in the
 * student-management domain into one class.
 *
 * <p>This class is NOT used directly by any controller endpoint – the existing, typed DTO
 * classes remain as the public API contract.  Instead it acts as a common data-carrier
 * between {@link DtoRequestMapper} and {@link DtoResponseMapper} implementations, allowing
 * a single {@link GenericDtoMapper} instance to convert between any request and any
 * response type without duplicating field declarations.
 *
 * <p>Field groups:
 * <ul>
 *   <li><b>Auth</b> – from {@link AuthRequest} / {@link AuthResponse}</li>
 *   <li><b>Student core</b> – from {@link StudentUpsertRequest} / {@link StudentProfileResponse}
 *       / {@link StudentSummaryResponse}</li>
 *   <li><b>Contact</b> – from {@link StudentContactRequest} / {@link StudentContactResponse}</li>
 *   <li><b>Admission</b> – from {@link AdmissionDetailsRequest} /
 *       {@link AdmissionDetailsResponse}</li>
 *   <li><b>Personal info</b> – from {@link PersonalInfoRequest} /
 *       {@link PersonalInfoResponse}</li>
 *   <li><b>Parent details</b> – from {@link ParentDetailsRequest} /
 *       {@link ParentDetailsResponse}</li>
 *   <li><b>Document</b> – from {@link DocumentRequest} / {@link DocumentResponse};
 *       a list of documents is stored in {@code documents}</li>
 *   <li><b>Stats</b> – from {@link StudentDashboardStatsResponse} /
 *       {@link YearWiseStudentStatsResponse}</li>
 *   <li><b>Pagination</b> – from {@link PagedResponse}</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericStudentDto {

    // ── Auth ─────────────────────────────────────────────────────────────────
    private String username;
    private String password;
    private String token;
    private String tokenType;
    private String role;

    // ── Student core ─────────────────────────────────────────────────────────
    private Integer studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    /** Computed full name (used in summary responses). */
    private String fullName;
    private String profileImagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Contact ──────────────────────────────────────────────────────────────
    private String address;
    private String mobileNo;
    private String email;

    // ── Admission details ────────────────────────────────────────────────────
    private String registrationId;
    private LocalDate dateOfRegistration;
    private String prn;
    private Integer admittedAcademicYear;
    private Integer currentAcademicYear;
    private String admissionPattern;
    private Integer fees;

    // ── Personal info ────────────────────────────────────────────────────────
    private String birthPlace;
    private String nationality;
    private String gender;
    private String category;
    private String religion;
    private String domicileState;
    private String bloodGroup;

    // ── Parent details ───────────────────────────────────────────────────────
    private String fatherFirstName;
    private String fatherMiddleName;
    private String fatherLastName;
    private String fatherContactNo;
    private String motherFirstName;
    private String motherMiddleName;
    private String motherLastName;
    private String motherContactNo;

    // ── Document (single document fields) ────────────────────────────────────
    private Integer documentId;
    private String documentType;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    /**
     * List of nested document DTOs when this instance represents a student that contains
     * multiple documents.  Named {@code documentList} to distinguish it from the
     * single-document scalar fields (documentId, documentType, fileName, filePath,
     * uploadedAt) declared above.
     */
    private List<GenericStudentDto> documentList;

    // ── Stats ────────────────────────────────────────────────────────────────
    private Long totalStudents;
    private Long regularStudents;
    private Long managementStudents;
    private Integer academicYear;
    private Long studentCount;

    // ── Pagination ───────────────────────────────────────────────────────────
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    /**
     * Nested list used when this DTO wraps a page of student summaries.  Named
     * {@code pagedStudents} to clearly indicate the content type.
     */
    private List<GenericStudentDto> pagedStudents;
}
