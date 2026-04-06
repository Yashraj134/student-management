package com.example.student_management.service;

public interface AuditService {
    void logAction(String action, Integer studentId);
}

