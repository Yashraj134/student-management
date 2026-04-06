package com.example.student_management.designpatterns.factory;

import com.example.student_management.dto.AdmissionDetailsRequest;
import com.example.student_management.entity.AdmissionDetails;

public interface AdmissionDetailsFactory {
    String supportedPattern();
    AdmissionDetails create(AdmissionDetailsRequest request);
}

