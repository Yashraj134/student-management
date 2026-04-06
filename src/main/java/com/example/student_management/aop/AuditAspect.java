package com.example.student_management.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @AfterReturning("@annotation(auditAction)")
    public void logAudit(JoinPoint joinPoint, AuditAction auditAction) {
        log.info("Audit aspect -> action={} method={}", auditAction.value(), joinPoint.getSignature().toShortString());
    }
}

