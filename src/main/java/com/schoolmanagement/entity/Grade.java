package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grades")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Grade {
    @Id
    @GeneratedValue(generator = "grade-id")
    @GenericGenerator(name = "grade-id", strategy = "com.schoolmanagement.util.GradeIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "G")
    })
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({ "grades", "classroom" })
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnoreProperties({ "grades", "classRooms", "teacher", "school" })
    private Lesson lesson;

    @Column(nullable = false)
    private Integer examNumber; // 1. sınav, 2. sınav gibi

    @Column(nullable = false)
    private Double score; // Not değeri

    @Column(nullable = true)
    private String notes; // Öğretmenin notu/açıklaması
}