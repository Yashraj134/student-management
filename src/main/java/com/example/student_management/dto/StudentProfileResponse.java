package com.example.student_management.dto;

import java.time.LocalDateTime;
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
public class StudentProfileResponse {
    private Integer studentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StudentContactResponse studentContact;
    private AdmissionDetailsResponse admissionDetails;
    private PersonalInfoResponse personalInfo;
    private ParentDetailsResponse parentDetails;
    private List<DocumentResponse> documents;
}

