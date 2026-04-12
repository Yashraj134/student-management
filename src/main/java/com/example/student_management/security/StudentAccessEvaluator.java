package com.example.student_management.security;

import com.example.student_management.entity.AppUser;
import com.example.student_management.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("studentAccessEvaluator")
@RequiredArgsConstructor
public class StudentAccessEvaluator {

    private final AppUserRepository appUserRepository;

    public boolean canAccessStudent(Authentication authentication, Integer studentId) {
        if (authentication == null || !authentication.isAuthenticated() || studentId == null) {
            return false;
        }

        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        if (!hasRole(authentication, "ROLE_STUDENT")) {
            return false;
        }

        AppUser user = appUserRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null || user.getStudentId() == null) {
            return false;
        }

        return user.getStudentId().equals(studentId);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}

