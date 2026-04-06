package com.example.student_management.repository;

import com.example.student_management.entity.ParentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentDetailsRepository extends JpaRepository<ParentDetails, Integer> {
}

