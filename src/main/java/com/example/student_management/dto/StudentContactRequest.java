package com.example.student_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class StudentContactRequest {

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,15}$", message = "mobileNo must contain 10-15 digits")
    private String mobileNo;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}

