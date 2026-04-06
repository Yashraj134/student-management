package com.example.student_management.designpatterns.strategy;

import com.example.student_management.dto.AdmissionDetailsRequest;
import org.springframework.stereotype.Component;

@Component
public class ManagementAdmissionValidationStrategy implements AdmissionValidationStrategy {

    @Override
    public String supportedPattern() {
        return "management";
    }

    @Override
    public void validate(AdmissionDetailsRequest request) {
        if (request.getFees() == null || request.getFees() < 50000) {
            throw new IllegalArgumentException("Fees must be at least 50000 for management admission");
        }
    }
}

