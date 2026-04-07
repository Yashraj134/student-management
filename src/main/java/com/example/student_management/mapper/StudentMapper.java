package com.example.student_management.mapper;

import com.example.student_management.dto.AdmissionDetailsResponse;
import com.example.student_management.dto.DocumentRequest;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.dto.PagedResponse;
import com.example.student_management.dto.ParentDetailsResponse;
import com.example.student_management.dto.PersonalInfoResponse;
import com.example.student_management.dto.StudentContactResponse;
import com.example.student_management.dto.StudentProfileResponse;
import com.example.student_management.dto.StudentSummaryResponse;
import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.entity.*;

import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StudentMapper {

    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
    private static final String DEFAULT_MALE_IMAGE_PATH = "defaults/male_icon.jpg";
    private static final String DEFAULT_FEMALE_IMAGE_PATH = "defaults/female_icon.jpg";
    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

    public Student toStudentEntity(StudentUpsertRequest request) {
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING START =====
                .profileImagePath(request.getProfileImagePath())
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING END =====
                .build();

        student.setStudentContact(StudentContact.builder()
                .address(request.getStudentContact().getAddress())
                .mobileNo(request.getStudentContact().getMobileNo())
                .email(request.getStudentContact().getEmail())
                .build());

        student.setPersonalInfo(PersonalInfo.builder()
                .birthPlace(request.getPersonalInfo().getBirthPlace())
                .nationality(request.getPersonalInfo().getNationality())
                .gender(request.getPersonalInfo().getGender())
                .category(request.getPersonalInfo().getCategory())
                .religion(request.getPersonalInfo().getReligion())
                .domicileState(request.getPersonalInfo().getDomicileState())
                .bloodGroup(request.getPersonalInfo().getBloodGroup())
                .build());

        student.setParentDetails(ParentDetails.builder()
                .fatherFirstName(request.getParentDetails().getFatherFirstName())
                .fatherMiddleName(request.getParentDetails().getFatherMiddleName())
                .fatherLastName(request.getParentDetails().getFatherLastName())
                .fatherContactNo(request.getParentDetails().getFatherContactNo())
                .motherFirstName(request.getParentDetails().getMotherFirstName())
                .motherMiddleName(request.getParentDetails().getMotherMiddleName())
                .motherLastName(request.getParentDetails().getMotherLastName())
                .motherContactNo(request.getParentDetails().getMotherContactNo())
                .build());

        student.setAdmissionDetails(
                AdmissionDetails.builder()
                        .registrationId(request.getAdmissionDetails().getRegistrationId())
                        .dateOfRegistration(request.getAdmissionDetails().getDateOfRegistration())
                        .prn(request.getAdmissionDetails().getPrn())
                        .admittedAcademicYear(request.getAdmissionDetails().getAdmittedAcademicYear())
                        .currentAcademicYear(request.getAdmissionDetails().getCurrentAcademicYear())
                        .admissionPattern(request.getAdmissionDetails().getAdmissionPattern())
                        .fees(request.getAdmissionDetails().getFees())
                        .build()
        );

        return student;
    }

    public void updateStudentEntity(Student student, StudentUpsertRequest request) {
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING START =====
        student.setProfileImagePath(request.getProfileImagePath());
        // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING END =====

        StudentContact contact = student.getStudentContact() == null ? StudentContact.builder().build() : student.getStudentContact();
        contact.setAddress(request.getStudentContact().getAddress());
        contact.setMobileNo(request.getStudentContact().getMobileNo());
        contact.setEmail(request.getStudentContact().getEmail());
        student.setStudentContact(contact);

        PersonalInfo personalInfo = student.getPersonalInfo() == null ? PersonalInfo.builder().build() : student.getPersonalInfo();
        personalInfo.setBirthPlace(request.getPersonalInfo().getBirthPlace());
        personalInfo.setNationality(request.getPersonalInfo().getNationality());
        personalInfo.setGender(request.getPersonalInfo().getGender());
        personalInfo.setCategory(request.getPersonalInfo().getCategory());
        personalInfo.setReligion(request.getPersonalInfo().getReligion());
        personalInfo.setDomicileState(request.getPersonalInfo().getDomicileState());
        personalInfo.setBloodGroup(request.getPersonalInfo().getBloodGroup());
        student.setPersonalInfo(personalInfo);

        ParentDetails parent = student.getParentDetails() == null ? ParentDetails.builder().build() : student.getParentDetails();
        parent.setFatherFirstName(request.getParentDetails().getFatherFirstName());
        parent.setFatherMiddleName(request.getParentDetails().getFatherMiddleName());
        parent.setFatherLastName(request.getParentDetails().getFatherLastName());
        parent.setFatherContactNo(request.getParentDetails().getFatherContactNo());
        parent.setMotherFirstName(request.getParentDetails().getMotherFirstName());
        parent.setMotherMiddleName(request.getParentDetails().getMotherMiddleName());
        parent.setMotherLastName(request.getParentDetails().getMotherLastName());
        parent.setMotherContactNo(request.getParentDetails().getMotherContactNo());
        student.setParentDetails(parent);

        student.setAdmissionDetails(
                AdmissionDetails.builder()
                        .registrationId(request.getAdmissionDetails().getRegistrationId())
                        .dateOfRegistration(request.getAdmissionDetails().getDateOfRegistration())
                        .prn(request.getAdmissionDetails().getPrn())
                        .admittedAcademicYear(request.getAdmissionDetails().getAdmittedAcademicYear())
                        .currentAcademicYear(request.getAdmissionDetails().getCurrentAcademicYear())
                        .admissionPattern(request.getAdmissionDetails().getAdmissionPattern())
                        .fees(request.getAdmissionDetails().getFees())
                        .build()
        );
    }

    public List<Document> toDocuments(List<DocumentRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(request -> Document.builder()
                        .documentType(request.getDocumentType())
                        .fileName(request.getFileName())
                        .filePath(request.getFilePath())
                        .build())
                .toList();
    }

    public StudentProfileResponse toProfileResponse(Student student) {
        return StudentProfileResponse.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING START =====
                .profileImagePath(resolveProfileImagePath(student))
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING END =====
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .studentContact(StudentContactResponse.builder()
                        .address(student.getStudentContact() != null ? student.getStudentContact().getAddress() : null)
                        .mobileNo(student.getStudentContact() != null ? student.getStudentContact().getMobileNo() : null)
                        .email(student.getStudentContact() != null ? student.getStudentContact().getEmail() : null)
                        .build())
                .admissionDetails(AdmissionDetailsResponse.builder()
                        .registrationId(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getRegistrationId() : null)
                        .dateOfRegistration(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getDateOfRegistration() : null)
                        .prn(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getPrn() : null)
                        .admittedAcademicYear(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getAdmittedAcademicYear() : null)
                        .currentAcademicYear(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getCurrentAcademicYear() : null)
                        .admissionPattern(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getAdmissionPattern() : null)
                        .fees(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getFees() : null)
                        .build())
                .personalInfo(PersonalInfoResponse.builder()
                        .birthPlace(student.getPersonalInfo() != null ? student.getPersonalInfo().getBirthPlace() : null)
                        .nationality(student.getPersonalInfo() != null ? student.getPersonalInfo().getNationality() : null)
                        .gender(student.getPersonalInfo() != null ? student.getPersonalInfo().getGender() : null)
                        .category(student.getPersonalInfo() != null ? student.getPersonalInfo().getCategory() : null)
                        .religion(student.getPersonalInfo() != null ? student.getPersonalInfo().getReligion() : null)
                        .domicileState(student.getPersonalInfo() != null ? student.getPersonalInfo().getDomicileState() : null)
                        .bloodGroup(student.getPersonalInfo() != null ? student.getPersonalInfo().getBloodGroup() : null)
                        .build())
                .parentDetails(ParentDetailsResponse.builder()
                        .fatherFirstName(student.getParentDetails() != null ? student.getParentDetails().getFatherFirstName() : null)
                        .fatherMiddleName(student.getParentDetails() != null ? student.getParentDetails().getFatherMiddleName() : null)
                        .fatherLastName(student.getParentDetails() != null ? student.getParentDetails().getFatherLastName() : null)
                        .fatherContactNo(student.getParentDetails() != null ? student.getParentDetails().getFatherContactNo() : null)
                        .motherFirstName(student.getParentDetails() != null ? student.getParentDetails().getMotherFirstName() : null)
                        .motherMiddleName(student.getParentDetails() != null ? student.getParentDetails().getMotherMiddleName() : null)
                        .motherLastName(student.getParentDetails() != null ? student.getParentDetails().getMotherLastName() : null)
                        .motherContactNo(student.getParentDetails() != null ? student.getParentDetails().getMotherContactNo() : null)
                        .build())
                .documents(student.getDocuments() == null ? List.of() : student.getDocuments().stream()
                        .map(document -> DocumentResponse.builder()
                                .documentId(document.getDocumentId())
                                .documentType(document.getDocumentType())
                                .fileName(document.getFileName())
                                .filePath(document.getFilePath())
                                .uploadedAt(document.getUploadedAt())
                                .build())
                        .toList())
                .build();
    }

    public StudentSummaryResponse toSummaryResponse(Student student) {
        String fullName = String.join(" ",
                student.getFirstName() == null ? "" : student.getFirstName(),
                student.getMiddleName() == null ? "" : student.getMiddleName(),
                student.getLastName() == null ? "" : student.getLastName()).trim().replaceAll("\\s+", " ");

        return StudentSummaryResponse.builder()
                .studentId(student.getStudentId())
                .fullName(fullName)
                .prn(student.getAdmissionDetails() != null ? student.getAdmissionDetails().getPrn() : null)
                .email(student.getStudentContact() != null ? student.getStudentContact().getEmail() : null)
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING START =====
                .profileImagePath(resolveProfileImagePath(student))
                // ===== ADDED: PROFILE_IMAGE_PATH_MAPPING END =====
                .build();
    }

    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS START =====
    public String resolveProfileImagePath(Student student) {
        if (student == null) {
            return DEFAULT_FEMALE_IMAGE_PATH;
        }
        if (StringUtils.hasText(student.getProfileImagePath())) {
            return student.getProfileImagePath().trim();
        }

        String gender = student.getPersonalInfo() != null ? student.getPersonalInfo().getGender() : null;
        if (StringUtils.hasText(gender) && "male".equalsIgnoreCase(gender.trim())) {
            return DEFAULT_MALE_IMAGE_PATH;
        }
        return DEFAULT_FEMALE_IMAGE_PATH;
    }
    // ===== ADDED: PROFILE_IMAGE_PATH_DEFAULTS END =====

    public PagedResponse<StudentSummaryResponse> toPagedResponse(Page<Student> page) {
        return PagedResponse.<StudentSummaryResponse>builder()
                .content(page.getContent().stream().map(this::toSummaryResponse).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public PagedResponse<StudentSummaryResponse> toPagedSummaryResponse(Page<StudentSummaryResponse> page) {
        return PagedResponse.<StudentSummaryResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public DocumentResponse toDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}

