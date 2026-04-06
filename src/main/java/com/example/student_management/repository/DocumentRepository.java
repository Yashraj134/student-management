package com.example.student_management.repository;

import com.example.student_management.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    void deleteByStudent_StudentId(Integer studentId);
}

