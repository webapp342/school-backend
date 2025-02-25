package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@ToString(exclude = { "school", "user" })
@EqualsAndHashCode(exclude = { "school", "user" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Teacher {
    @Id
    @GeneratedValue(generator = "teacher-id")
    @GenericGenerator(name = "teacher-id", strategy = "com.schoolmanagement.util.TeacherIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "T")
    })
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String teacherNumber;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeacherStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({ "teachers", "classRooms", "students" })
    private School school;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("teacher")
    private User user;
}