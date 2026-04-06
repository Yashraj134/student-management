package com.example.student_management.designpatterns.strategy;

import com.example.student_management.dto.AdmissionDetailsRequest;

public interface AdmissionValidationStrategy {
    String supportedPattern();
    void validate(AdmissionDetailsRequest request);
}

