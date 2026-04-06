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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parent_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Integer parentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    @JsonIgnore
    private Student student;

    @Column(name = "father_first_name", length = 50)
    private String fatherFirstName;

    @Column(name = "father_middle_name", length = 50)
    private String fatherMiddleName;

    @Column(name = "father_last_name", length = 50)
    private String fatherLastName;

    @Column(name = "father_contact_no", length = 15)
    private String fatherContactNo;

    @Column(name = "mother_first_name", length = 50)
    private String motherFirstName;

    @Column(name = "mother_middle_name", length = 50)
    private String motherMiddleName;

    @Column(name = "mother_last_name", length = 50)
    private String motherLastName;

    @Column(name = "mother_contact_no", length = 15)
    private String motherContactNo;
}

