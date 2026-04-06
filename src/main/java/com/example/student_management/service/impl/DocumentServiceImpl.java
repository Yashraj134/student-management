package com.example.student_management.service.impl;

import com.example.student_management.dto.DocumentRequest;
import com.example.student_management.entity.Student;
import com.example.student_management.mapper.StudentMapper;
import com.example.student_management.repository.DocumentRepository;
import com.example.student_management.service.DocumentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StudentMapper studentMapper;

    @Override
    public void replaceDocuments(Student student, List<DocumentRequest> requests) {
        student.setDocuments(studentMapper.toDocuments(requests));
    }

    @Override
    public void deleteByStudentId(Integer studentId) {
        documentRepository.deleteByStudent_StudentId(studentId);
    }
}

