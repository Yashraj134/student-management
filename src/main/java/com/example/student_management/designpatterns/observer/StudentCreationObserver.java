package com.example.student_management.designpatterns.observer;

import com.example.student_management.entity.Student;

public interface StudentCreationObserver {
    void onStudentCreated(Student student);
}

