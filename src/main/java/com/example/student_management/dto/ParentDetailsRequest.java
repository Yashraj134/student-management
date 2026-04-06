package com.example.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class ParentDetailsRequest {

    @NotBlank
    private String fatherFirstName;

    private String fatherMiddleName;

    @NotBlank
    private String fatherLastName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,15}$", message = "fatherContactNo must contain 10-15 digits")
    private String fatherContactNo;

    @NotBlank
    private String motherFirstName;

    private String motherMiddleName;

    @NotBlank
    private String motherLastName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,15}$", message = "motherContactNo must contain 10-15 digits")
    private String motherContactNo;
}

