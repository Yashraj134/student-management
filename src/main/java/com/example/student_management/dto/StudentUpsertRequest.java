package com.example.student_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpsertRequest {

    @NotBlank
    @Size(max = 10)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    // ===== ADDED: PROFILE_IMAGE_PATH START =====
    private String profileImagePath;
    // ===== ADDED: PROFILE_IMAGE_PATH END =====

    @Valid
    @NotNull
    private StudentContactRequest studentContact;

    @Valid
    @NotNull
    private AdmissionDetailsRequest admissionDetails;

    @Valid
    @NotNull
    private PersonalInfoRequest personalInfo;

    @Valid
    @NotNull
    private ParentDetailsRequest parentDetails;

    @Valid
    @Builder.Default
    private List<DocumentRequest> documents = new ArrayList<>();
}

