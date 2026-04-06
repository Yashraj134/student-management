package com.example.student_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "first_name", length = 10)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private StudentContact studentContact;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AdmissionDetails admissionDetails;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PersonalInfo personalInfo;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ParentDetails parentDetails;

    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    public void setStudentContact(StudentContact studentContact) {
        if (studentContact != null) {
            studentContact.setStudent(this);
        }
        this.studentContact = studentContact;
    }

    public void setAdmissionDetails(AdmissionDetails admissionDetails) {
        if (admissionDetails != null) {
            admissionDetails.setStudent(this);
        }
        this.admissionDetails = admissionDetails;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        if (personalInfo != null) {
            personalInfo.setStudent(this);
        }
        this.personalInfo = personalInfo;
    }

    public void setParentDetails(ParentDetails parentDetails) {
        if (parentDetails != null) {
            parentDetails.setStudent(this);
        }
        this.parentDetails = parentDetails;
    }

    public void setDocuments(List<Document> documents) {
        this.documents.clear();
        if (documents != null) {
            documents.forEach(document -> document.setStudent(this));
            this.documents.addAll(documents);
        }
    }
}

