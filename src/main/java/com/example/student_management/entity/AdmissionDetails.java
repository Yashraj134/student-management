package com.example.student_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admission_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admission_id")
    private Integer admissionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    @JsonIgnore
    private Student student;

    @Column(name = "registration_id", length = 50)
    private String registrationId;

    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;

    @Column(name = "prn", length = 50)
    private String prn;

    @Column(name = "admitted_academic_year")
    private Integer admittedAcademicYear;

    @Column(name = "current_academic_year")
    private Integer currentAcademicYear;

    @Column(name = "admission_pattern", length = 20)
    private String admissionPattern;

    @Column(name = "fees")
    private Integer fees;
}

