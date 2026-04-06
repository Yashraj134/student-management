package com.example.student_management.dto;

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
public class AdmissionDetailsResponse {
    private String registrationId;
    private LocalDate dateOfRegistration;
    private String prn;
    private Integer admittedAcademicYear;
    private Integer currentAcademicYear;
    private String admissionPattern;
    private Integer fees;
}

