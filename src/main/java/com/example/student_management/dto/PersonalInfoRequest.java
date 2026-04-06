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
public class PersonalInfoRequest {

    @NotBlank
    private String birthPlace;

    @NotBlank
    private String nationality;

    @NotBlank
    @Pattern(regexp = "^(male|female|other)$", message = "gender must be male, female or other")
    private String gender;

    @NotBlank
    private String category;

    @NotBlank
    private String religion;

    @NotBlank
    private String domicileState;

    @NotBlank
    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|O\\+|O-|AB\\+|AB-)$", message = "invalid blood group")
    private String bloodGroup;
}
