package com.example.student_management.aop;

import com.example.student_management.dto.StudentUpsertRequest;
import com.example.student_management.entity.AdmissionDetails;
import com.example.student_management.entity.StudentContact;
import com.example.student_management.exception.DuplicateResourceException;
import com.example.student_management.repository.AdmissionDetailsRepository;
import com.example.student_management.repository.StudentContactRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {

    private final StudentContactRepository studentContactRepository;
    private final AdmissionDetailsRepository admissionDetailsRepository;

    @Before("@annotation(com.example.student_management.aop.CheckDuplicateStudent)")
    public void validateDuplicates(JoinPoint joinPoint) {
        StudentUpsertRequest request = null;
        Integer studentId = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof StudentUpsertRequest upsertRequest) {
                request = upsertRequest;
            }
            if (arg instanceof Integer idArg) {
                studentId = idArg;
            }
        }

        if (request == null) {
            return;
        }

        String email = request.getStudentContact().getEmail();
        StudentContact existingContact = studentContactRepository.findByEmail(email).orElse(null);
        if (existingContact != null && !existingContact.getStudent().getStudentId().equals(studentId)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        String prn = request.getAdmissionDetails().getPrn();
        AdmissionDetails existingAdmission = admissionDetailsRepository.findByPrn(prn).orElse(null);
        if (existingAdmission != null && !existingAdmission.getStudent().getStudentId().equals(studentId)) {
            throw new DuplicateResourceException("PRN already exists: " + prn);
        }
    }
}

