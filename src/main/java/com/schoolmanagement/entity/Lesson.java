package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "classRooms" })
public class Lesson {
    @Id
    @GeneratedValue(generator = "lesson-id")
    @GenericGenerator(name = "lesson-id", strategy = "com.schoolmanagement.util.LessonIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "L")
    })
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Integer duration; // Ders süresi (dakika)

    @Column(nullable = false)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToMany(mappedBy = "lessons")
    @Builder.Default
    private Set<ClassRoom> classRooms = new HashSet<>();
}