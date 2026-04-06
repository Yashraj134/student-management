package com.example.student_management.designpatterns.observer;

import com.example.student_management.entity.Student;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StudentCreationSubject {

    private final List<StudentCreationObserver> observers;

    public StudentCreationSubject(List<StudentCreationObserver> observers) {
        this.observers = observers;
    }

    public void notifyStudentCreated(Student student) {
        observers.forEach(observer -> observer.onStudentCreated(student));
    }
}

