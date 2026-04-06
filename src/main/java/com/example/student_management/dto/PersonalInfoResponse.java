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
public class PersonalInfoResponse {
    private String birthPlace;
    private String nationality;
    private String gender;
    private String category;
    private String religion;
    private String domicileState;
    private String bloodGroup;
}

