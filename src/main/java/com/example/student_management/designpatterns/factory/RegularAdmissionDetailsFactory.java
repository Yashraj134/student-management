package com.example.student_management.designpatterns.factory;

import com.example.student_management.dto.AdmissionDetailsRequest;
import com.example.student_management.entity.AdmissionDetails;
import org.springframework.stereotype.Component;

@Component
public class RegularAdmissionDetailsFactory implements AdmissionDetailsFactory {

    @Override
    public String supportedPattern() {
        return "regular";
    }

    @Override
    public AdmissionDetails create(AdmissionDetailsRequest request) {
        return AdmissionDetails.builder()
                .registrationId(request.getRegistrationId())
                .dateOfRegistration(request.getDateOfRegistration())
                .prn(request.getPrn())
                .admittedAcademicYear(request.getAdmittedAcademicYear())
                .currentAcademicYear(request.getCurrentAcademicYear())
                .admissionPattern("regular")
                .fees(request.getFees())
                .build();
    }
}

