package com.example.student_management.service;

import com.example.student_management.dto.DocumentRequest;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.entity.Student;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    void replaceDocuments(Student student, List<DocumentRequest> requests);
    DocumentResponse uploadDocument(Integer studentId, String documentType, MultipartFile file);
    void deleteByStudentId(Integer studentId);
}
