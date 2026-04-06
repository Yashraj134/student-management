package com.example.student_management.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(Integer studentId, String documentType, MultipartFile file) throws IOException;
}

