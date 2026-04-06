package com.example.student_management.service;

import com.example.student_management.dto.DocumentRequest;
import com.example.student_management.entity.Student;
import java.util.List;

public interface DocumentService {
    void replaceDocuments(Student student, List<DocumentRequest> requests);
    void deleteByStudentId(Integer studentId);
}

