package com.example.student_management.designpatterns.strategy;

import com.example.student_management.dto.AdmissionDetailsRequest;
import org.springframework.stereotype.Component;

@Component
public class RegularAdmissionValidationStrategy implements AdmissionValidationStrategy {

    @Override
    public String supportedPattern() {
        return "regular";
    }

    @Override
    public void validate(AdmissionDetailsRequest request) {
        if (request.getFees() == null || request.getFees() <= 0) {
            throw new IllegalArgumentException("Fees must be greater than 0 for regular admission");
        }
    }
}

