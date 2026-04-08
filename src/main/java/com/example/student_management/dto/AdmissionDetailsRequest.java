package com.example.student_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
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
public class AdmissionDetailsRequest {

    @NotBlank
    private String registrationId;

    @NotNull
    private LocalDate dateOfRegistration;

    @NotBlank
    private String prn;

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer admittedAcademicYear;

    // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION START =====
    // Derived in service as: (current calendar year - admittedAcademicYear + 1), clamped to 1..4.
    @Min(1)
    @Max(4)
    private Integer currentAcademicYear;
    // ===== DAY2_CURRENT_ACADEMIC_YEAR_DERIVATION END =====

    @NotBlank
    @Pattern(regexp = "^(regular|management)$", message = "admissionPattern must be regular or management")
    private String admissionPattern;

    @NotNull
    @Min(0)
    private Integer fees;
}

