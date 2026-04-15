package com.example.student_management.dto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic mapper class that pairs a {@link DtoRequestMapper} with a
 * {@link DtoResponseMapper} and provides static, pre-built mapper instances for every
 * request/response DTO in the student-management domain.
 *
 * <h2>Design goals</h2>
 * <ul>
 *   <li>All existing DTO classes remain <em>unchanged</em> – the public API contract
 *       (JSON shapes accepted / returned by the controllers) is preserved.</li>
 *   <li>All mapping logic is centralised here rather than scattered across individual DTO
 *       classes, eliminating code duplication as the number of DTOs grows.</li>
 *   <li>Each concrete mapper is a single-method lambda that implements either
 *       {@link DtoRequestMapper} or {@link DtoResponseMapper} – both of which are
 *       {@code @FunctionalInterface}s.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Convert a request DTO → GenericStudentDto
 * GenericStudentDto generic =
 *     GenericDtoMapper.STUDENT_UPSERT_REQUEST_MAPPER.fromRequest(request);
 *
 * // Convert GenericStudentDto → a typed response DTO
 * StudentProfileResponse response =
 *     GenericDtoMapper.STUDENT_PROFILE_RESPONSE_MAPPER.toResponse(generic);
 *
 * // Or compose them via the generic class:
 * GenericDtoMapper<StudentUpsertRequest, StudentProfileResponse> mapper =
 *     GenericDtoMapper.forStudentProfile();
 * StudentProfileResponse response = mapper.convert(request);
 * }</pre>
 *
 * @param <REQ> the request DTO type
 * @param <RES> the response DTO type
 */
public class GenericDtoMapper<REQ, RES> {

    private final DtoRequestMapper<REQ> requestMapper;
    private final DtoResponseMapper<RES> responseMapper;

    public GenericDtoMapper(DtoRequestMapper<REQ> requestMapper,
                            DtoResponseMapper<RES> responseMapper) {
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    /**
     * Converts a typed request DTO into a {@link GenericStudentDto}.
     *
     * @param request the incoming request DTO
     * @return populated {@link GenericStudentDto}
     */
    public GenericStudentDto extract(REQ request) {
        return requestMapper.fromRequest(request);
    }

    /**
     * Converts a {@link GenericStudentDto} into the typed response DTO.
     *
     * @param dto the populated generic DTO
     * @return the typed response DTO
     */
    public RES map(GenericStudentDto dto) {
        return responseMapper.toResponse(dto);
    }

    /**
     * Convenience method: converts a request DTO directly to the response DTO by
     * internally routing through {@link GenericStudentDto}.
     *
     * @param request the incoming request DTO
     * @return the typed response DTO
     */
    public RES convert(REQ request) {
        return responseMapper.toResponse(requestMapper.fromRequest(request));
    }

    // =========================================================================
    //  Static utility conversion methods
    // =========================================================================

    /**
     * Converts any request DTO {@code T} into a {@link GenericStudentDto} using the
     * supplied mapper.
     */
    public static <T> GenericStudentDto fromRequest(T request, DtoRequestMapper<T> mapper) {
        return mapper.fromRequest(request);
    }

    /**
     * Converts a {@link GenericStudentDto} into any response DTO {@code T} using the
     * supplied mapper.
     */
    public static <T> T toResponse(GenericStudentDto dto, DtoResponseMapper<T> mapper) {
        return mapper.toResponse(dto);
    }

    // =========================================================================
    //  Static factory methods – one per logical DTO pair
    // =========================================================================

    /** Returns a pre-configured mapper for StudentUpsertRequest → StudentProfileResponse. */
    public static GenericDtoMapper<StudentUpsertRequest, StudentProfileResponse> forStudentProfile() {
        return new GenericDtoMapper<>(
                STUDENT_UPSERT_REQUEST_MAPPER,
                STUDENT_PROFILE_RESPONSE_MAPPER);
    }

    /** Returns a pre-configured mapper for StudentUpsertRequest → StudentSummaryResponse. */
    public static GenericDtoMapper<StudentUpsertRequest, StudentSummaryResponse> forStudentSummary() {
        return new GenericDtoMapper<>(
                STUDENT_UPSERT_REQUEST_MAPPER,
                STUDENT_SUMMARY_RESPONSE_MAPPER);
    }

    /** Returns a pre-configured mapper for AuthRequest → AuthResponse. */
    public static GenericDtoMapper<AuthRequest, AuthResponse> forAuth() {
        return new GenericDtoMapper<>(AUTH_REQUEST_MAPPER, AUTH_RESPONSE_MAPPER);
    }

    /** Returns a pre-configured mapper for DocumentRequest → DocumentResponse. */
    public static GenericDtoMapper<DocumentRequest, DocumentResponse> forDocument() {
        return new GenericDtoMapper<>(DOCUMENT_REQUEST_MAPPER, DOCUMENT_RESPONSE_MAPPER);
    }

    // =========================================================================
    //  Pre-built DtoRequestMapper implementations
    //  Note: simple/leaf mappers are declared first to avoid forward references.
    // =========================================================================

    /** Maps an {@link AuthRequest} into a {@link GenericStudentDto}. */
    public static final DtoRequestMapper<AuthRequest> AUTH_REQUEST_MAPPER =
            request -> GenericStudentDto.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();

    /**
     * Maps a single {@link DocumentRequest} into a {@link GenericStudentDto}.
     * Declared before {@link #STUDENT_UPSERT_REQUEST_MAPPER} so it can be
     * referenced inside that mapper's lambda without a forward-reference error.
     */
    public static final DtoRequestMapper<DocumentRequest> DOCUMENT_REQUEST_MAPPER =
            request -> GenericStudentDto.builder()
                    .documentType(request.getDocumentType())
                    .fileName(request.getFileName())
                    .filePath(request.getFilePath())
                    .build();

    /**
     * Maps a {@link StudentUpsertRequest} (including all nested sub-DTOs) into a flat
     * {@link GenericStudentDto}.  Document entries are mapped via
     * {@link #DOCUMENT_REQUEST_MAPPER} to avoid duplicating the builder logic.
     */
    public static final DtoRequestMapper<StudentUpsertRequest> STUDENT_UPSERT_REQUEST_MAPPER =
            request -> GenericStudentDto.builder()
                    // Student core
                    .firstName(request.getFirstName())
                    .middleName(request.getMiddleName())
                    .lastName(request.getLastName())
                    .profileImagePath(request.getProfileImagePath())
                    // Contact
                    .address(request.getStudentContact() != null
                            ? request.getStudentContact().getAddress() : null)
                    .mobileNo(request.getStudentContact() != null
                            ? request.getStudentContact().getMobileNo() : null)
                    .email(request.getStudentContact() != null
                            ? request.getStudentContact().getEmail() : null)
                    // Admission details
                    .registrationId(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getRegistrationId() : null)
                    .dateOfRegistration(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getDateOfRegistration() : null)
                    .prn(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getPrn() : null)
                    .admittedAcademicYear(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getAdmittedAcademicYear() : null)
                    .currentAcademicYear(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getCurrentAcademicYear() : null)
                    .admissionPattern(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getAdmissionPattern() : null)
                    .fees(request.getAdmissionDetails() != null
                            ? request.getAdmissionDetails().getFees() : null)
                    // Personal info
                    .birthPlace(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getBirthPlace() : null)
                    .nationality(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getNationality() : null)
                    .gender(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getGender() : null)
                    .category(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getCategory() : null)
                    .religion(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getReligion() : null)
                    .domicileState(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getDomicileState() : null)
                    .bloodGroup(request.getPersonalInfo() != null
                            ? request.getPersonalInfo().getBloodGroup() : null)
                    // Parent details
                    .fatherFirstName(request.getParentDetails() != null
                            ? request.getParentDetails().getFatherFirstName() : null)
                    .fatherMiddleName(request.getParentDetails() != null
                            ? request.getParentDetails().getFatherMiddleName() : null)
                    .fatherLastName(request.getParentDetails() != null
                            ? request.getParentDetails().getFatherLastName() : null)
                    .fatherContactNo(request.getParentDetails() != null
                            ? request.getParentDetails().getFatherContactNo() : null)
                    .motherFirstName(request.getParentDetails() != null
                            ? request.getParentDetails().getMotherFirstName() : null)
                    .motherMiddleName(request.getParentDetails() != null
                            ? request.getParentDetails().getMotherMiddleName() : null)
                    .motherLastName(request.getParentDetails() != null
                            ? request.getParentDetails().getMotherLastName() : null)
                    .motherContactNo(request.getParentDetails() != null
                            ? request.getParentDetails().getMotherContactNo() : null)
                    // Documents – reuse DOCUMENT_REQUEST_MAPPER to avoid duplicating builder logic
                    .documentList(request.getDocuments() == null ? null
                            : request.getDocuments().stream()
                                    .map(DOCUMENT_REQUEST_MAPPER::fromRequest)
                                    .collect(Collectors.toList()))
                    .build();

    // =========================================================================
    //  Pre-built DtoResponseMapper implementations
    //  Note: simple/leaf mappers are declared first to avoid forward references.
    // =========================================================================

    /** Maps a {@link GenericStudentDto} to an {@link AuthResponse}. */
    public static final DtoResponseMapper<AuthResponse> AUTH_RESPONSE_MAPPER =
            dto -> AuthResponse.builder()
                    .token(dto.getToken())
                    .tokenType(dto.getTokenType())
                    .username(dto.getUsername())
                    .role(dto.getRole())
                    .studentId(dto.getStudentId())
                    .build();

    /**
     * Maps a {@link GenericStudentDto} to a {@link DocumentResponse}.
     * Declared before {@link #STUDENT_PROFILE_RESPONSE_MAPPER} so it can be reused
     * inside that mapper's lambda without a forward-reference error.
     */
    public static final DtoResponseMapper<DocumentResponse> DOCUMENT_RESPONSE_MAPPER =
            dto -> DocumentResponse.builder()
                    .documentId(dto.getDocumentId())
                    .documentType(dto.getDocumentType())
                    .fileName(dto.getFileName())
                    .filePath(dto.getFilePath())
                    .uploadedAt(dto.getUploadedAt())
                    .build();

    /**
     * Maps a {@link GenericStudentDto} to a fully-nested {@link StudentProfileResponse}.
     * Re-creates all nested sub-DTOs (contact, admission, personal info, parent details,
     * documents) from the flat generic DTO fields.  Document entries are mapped via
     * {@link #DOCUMENT_RESPONSE_MAPPER} to avoid duplicating the builder logic.
     */
    public static final DtoResponseMapper<StudentProfileResponse> STUDENT_PROFILE_RESPONSE_MAPPER =
            dto -> StudentProfileResponse.builder()
                    .studentId(dto.getStudentId())
                    .firstName(dto.getFirstName())
                    .middleName(dto.getMiddleName())
                    .lastName(dto.getLastName())
                    .profileImagePath(dto.getProfileImagePath())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .studentContact(StudentContactResponse.builder()
                            .address(dto.getAddress())
                            .mobileNo(dto.getMobileNo())
                            .email(dto.getEmail())
                            .build())
                    .admissionDetails(AdmissionDetailsResponse.builder()
                            .registrationId(dto.getRegistrationId())
                            .dateOfRegistration(dto.getDateOfRegistration())
                            .prn(dto.getPrn())
                            .admittedAcademicYear(dto.getAdmittedAcademicYear())
                            .currentAcademicYear(dto.getCurrentAcademicYear())
                            .admissionPattern(dto.getAdmissionPattern())
                            .fees(dto.getFees())
                            .build())
                    .personalInfo(PersonalInfoResponse.builder()
                            .birthPlace(dto.getBirthPlace())
                            .nationality(dto.getNationality())
                            .gender(dto.getGender())
                            .category(dto.getCategory())
                            .religion(dto.getReligion())
                            .domicileState(dto.getDomicileState())
                            .bloodGroup(dto.getBloodGroup())
                            .build())
                    .parentDetails(ParentDetailsResponse.builder()
                            .fatherFirstName(dto.getFatherFirstName())
                            .fatherMiddleName(dto.getFatherMiddleName())
                            .fatherLastName(dto.getFatherLastName())
                            .fatherContactNo(dto.getFatherContactNo())
                            .motherFirstName(dto.getMotherFirstName())
                            .motherMiddleName(dto.getMotherMiddleName())
                            .motherLastName(dto.getMotherLastName())
                            .motherContactNo(dto.getMotherContactNo())
                            .build())
                    // Documents – reuse DOCUMENT_RESPONSE_MAPPER to avoid duplicating builder logic
                    .documents(dto.getDocumentList() == null ? List.of()
                            : dto.getDocumentList().stream()
                                    .map(DOCUMENT_RESPONSE_MAPPER::toResponse)
                                    .collect(Collectors.toList()))
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link StudentSummaryResponse}. */
    public static final DtoResponseMapper<StudentSummaryResponse> STUDENT_SUMMARY_RESPONSE_MAPPER =
            dto -> StudentSummaryResponse.builder()
                    .studentId(dto.getStudentId())
                    .fullName(dto.getFullName())
                    .prn(dto.getPrn())
                    .email(dto.getEmail())
                    .profileImagePath(dto.getProfileImagePath())
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link StudentDashboardStatsResponse}. */
    public static final DtoResponseMapper<StudentDashboardStatsResponse> DASHBOARD_STATS_RESPONSE_MAPPER =
            dto -> StudentDashboardStatsResponse.builder()
                    .totalStudents(dto.getTotalStudents())
                    .regularStudents(dto.getRegularStudents())
                    .managementStudents(dto.getManagementStudents())
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link YearWiseStudentStatsResponse}. */
    public static final DtoResponseMapper<YearWiseStudentStatsResponse> YEAR_WISE_STATS_RESPONSE_MAPPER =
            dto -> YearWiseStudentStatsResponse.builder()
                    .academicYear(dto.getAcademicYear())
                    .studentCount(dto.getStudentCount())
                    .build();

    /** Maps a {@link GenericStudentDto} to an {@link AdmissionDetailsResponse}. */
    public static final DtoResponseMapper<AdmissionDetailsResponse> ADMISSION_DETAILS_RESPONSE_MAPPER =
            dto -> AdmissionDetailsResponse.builder()
                    .registrationId(dto.getRegistrationId())
                    .dateOfRegistration(dto.getDateOfRegistration())
                    .prn(dto.getPrn())
                    .admittedAcademicYear(dto.getAdmittedAcademicYear())
                    .currentAcademicYear(dto.getCurrentAcademicYear())
                    .admissionPattern(dto.getAdmissionPattern())
                    .fees(dto.getFees())
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link PersonalInfoResponse}. */
    public static final DtoResponseMapper<PersonalInfoResponse> PERSONAL_INFO_RESPONSE_MAPPER =
            dto -> PersonalInfoResponse.builder()
                    .birthPlace(dto.getBirthPlace())
                    .nationality(dto.getNationality())
                    .gender(dto.getGender())
                    .category(dto.getCategory())
                    .religion(dto.getReligion())
                    .domicileState(dto.getDomicileState())
                    .bloodGroup(dto.getBloodGroup())
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link ParentDetailsResponse}. */
    public static final DtoResponseMapper<ParentDetailsResponse> PARENT_DETAILS_RESPONSE_MAPPER =
            dto -> ParentDetailsResponse.builder()
                    .fatherFirstName(dto.getFatherFirstName())
                    .fatherMiddleName(dto.getFatherMiddleName())
                    .fatherLastName(dto.getFatherLastName())
                    .fatherContactNo(dto.getFatherContactNo())
                    .motherFirstName(dto.getMotherFirstName())
                    .motherMiddleName(dto.getMotherMiddleName())
                    .motherLastName(dto.getMotherLastName())
                    .motherContactNo(dto.getMotherContactNo())
                    .build();

    /** Maps a {@link GenericStudentDto} to a {@link StudentContactResponse}. */
    public static final DtoResponseMapper<StudentContactResponse> STUDENT_CONTACT_RESPONSE_MAPPER =
            dto -> StudentContactResponse.builder()
                    .address(dto.getAddress())
                    .mobileNo(dto.getMobileNo())
                    .email(dto.getEmail())
                    .build();
}
