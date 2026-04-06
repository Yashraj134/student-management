package com.example.student_management.service.impl;

import com.example.student_management.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditServiceImpl implements AuditService {

    @Override
    public void logAction(String action, Integer studentId) {
        log.info("Audit action={} studentId={}", action, studentId);
    }
}

