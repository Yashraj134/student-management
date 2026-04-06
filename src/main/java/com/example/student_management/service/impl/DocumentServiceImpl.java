package com.example.student_management.service.impl;

import com.example.student_management.dto.DocumentRequest;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.entity.Document;
import com.example.student_management.entity.Student;
import com.example.student_management.mapper.StudentMapper;
import com.example.student_management.repository.DocumentRepository;
import com.example.student_management.repository.StudentRepository;
import com.example.student_management.service.DocumentService;
import com.example.student_management.service.FileStorageService;
import com.example.student_management.exception.ResourceNotFoundException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;
    private final StudentMapper studentMapper;

    @Override
    public void replaceDocuments(Student student, List<DocumentRequest> requests) {
        student.setDocuments(studentMapper.toDocuments(requests));
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(Integer studentId, String documentType, MultipartFile file) {
        if (!StringUtils.hasText(documentType)) {
            throw new IllegalArgumentException("documentType is required");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required and must not be empty");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        String storedPath;
        try {
            storedPath = fileStorageService.store(studentId, documentType, file);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }

        String cleanFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());
        Document savedDocument = documentRepository.save(Document.builder()
                .student(student)
                .documentType(documentType.trim())
                .fileName(cleanFileName)
                .filePath(storedPath)
                .build());

        return studentMapper.toDocumentResponse(savedDocument);
    }

    @Override
    public void deleteByStudentId(Integer studentId) {
        documentRepository.deleteByStudent_StudentId(studentId);
    }
}

