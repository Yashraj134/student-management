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
public class ParentDetailsResponse {
    private String fatherFirstName;
    private String fatherMiddleName;
    private String fatherLastName;
    private String fatherContactNo;
    private String motherFirstName;
    private String motherMiddleName;
    private String motherLastName;
    private String motherContactNo;
}

