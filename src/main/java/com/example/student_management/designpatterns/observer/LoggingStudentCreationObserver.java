package com.example.student_management.designpatterns.observer;

import com.example.student_management.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingStudentCreationObserver implements StudentCreationObserver {

    @Override
    public void onStudentCreated(Student student) {
        log.info("Observer notification -> student created: id={}, name={} {}", student.getStudentId(), student.getFirstName(), student.getLastName());
    }
}

