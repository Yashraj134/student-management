package com.example.student_management.service.impl;

import com.example.student_management.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Basic audit service implementation that writes audit entries to application logs.
 */
@Slf4j
@Service
public class AuditServiceImpl implements AuditService {

    /**
     * Writes a structured audit message with action and student id.
     *
     * @param action action label to audit
     * @param studentId related student id
     */
    @Override
    public void logAction(String action, Integer studentId) {
        log.info("Audit action={} studentId={}", action, studentId);
    }
}

