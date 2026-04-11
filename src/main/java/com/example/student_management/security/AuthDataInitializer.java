package com.example.student_management.security;

import com.example.student_management.entity.AppUser;
import com.example.student_management.entity.UserRole;
import com.example.student_management.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthDataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "admin123", UserRole.ADMIN);
        createUserIfMissing("student", "student123", UserRole.STUDENT);
    }

    private void createUserIfMissing(String username, String rawPassword, UserRole role) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            return;
        }

        appUserRepository.save(AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build());
    }
}

