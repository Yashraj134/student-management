package com.example.student_management.service.impl;

import com.example.student_management.service.FileStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadRoot;

    public LocalFileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(Integer studentId, String documentType, MultipartFile file) throws IOException {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());
        String safeType = documentType.trim().toLowerCase().replaceAll("[^a-z0-9_-]", "_");
        String storedName = UUID.randomUUID() + "_" + originalName;

        Path studentFolder = uploadRoot.resolve("student-" + studentId).resolve(safeType).normalize();
        Files.createDirectories(studentFolder);

        Path targetPath = studentFolder.resolve(storedName).normalize();
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uploadRoot.relativize(targetPath).toString().replace('\\', '/');
    }
}

