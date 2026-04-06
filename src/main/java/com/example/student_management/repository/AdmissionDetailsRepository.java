package com.example.student_management.repository;

import com.example.student_management.entity.AdmissionDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionDetailsRepository extends JpaRepository<AdmissionDetails, Integer> {
    Optional<AdmissionDetails> findByPrn(String prn);
}

