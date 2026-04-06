package com.example.student_management.repository;

import com.example.student_management.entity.StudentContact;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentContactRepository extends JpaRepository<StudentContact, Integer> {
    Optional<StudentContact> findByEmail(String email);
}

