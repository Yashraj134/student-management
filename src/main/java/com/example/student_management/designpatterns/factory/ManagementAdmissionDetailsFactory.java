package com.example.student_management.designpatterns.factory;

import com.example.student_management.dto.AdmissionDetailsRequest;
import com.example.student_management.entity.AdmissionDetails;
import org.springframework.stereotype.Component;

@Component
public class ManagementAdmissionDetailsFactory implements AdmissionDetailsFactory {

    @Override
    public String supportedPattern() {
        return "management";
    }

    @Override
    public AdmissionDetails create(AdmissionDetailsRequest request) {
        return AdmissionDetails.builder()
                .registrationId(request.getRegistrationId())
                .dateOfRegistration(request.getDateOfRegistration())
                .prn(request.getPrn())
                .admittedAcademicYear(request.getAdmittedAcademicYear())
                .currentAcademicYear(request.getCurrentAcademicYear())
                .admissionPattern("management")
                .fees(request.getFees())
                .build();
    }
}

