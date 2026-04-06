package com.example.student_management.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Integer documentId;
    private String documentType;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
}

