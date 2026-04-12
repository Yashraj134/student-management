package com.example.student_management.service;

/**
 * Contract for application audit logging operations.
 */
public interface AuditService {

    /**
     * Logs a named action for a student context.
     *
     * @param action action label to log
     * @param studentId related student id, if available
     */
    void logAction(String action, Integer studentId);
}

