package com.example.student_management.dto;

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
public class StudentSummaryResponse {
    private Integer studentId;
    private String fullName;
    private String prn;
    private String email;

    // ===== ADDED: PROFILE_IMAGE_PATH START =====
    private String profileImagePath;
    // ===== ADDED: PROFILE_IMAGE_PATH END =====
}

