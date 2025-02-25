package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = { "school", "students", "teachers", "lessons" })
@EqualsAndHashCode(exclude = { "school", "students", "teachers", "lessons" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "classrooms")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClassRoom {
    @Id
    @GeneratedValue(generator = "classroom-id")
    @GenericGenerator(name = "classroom-id", strategy = "com.schoolmanagement.util.ClassRoomIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "CL")
    })
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonBackReference
    private School school;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("classroom")
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "classroom_teachers", joinColumns = @JoinColumn(name = "classroom_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    @JsonIgnoreProperties({ "school", "classRooms" })
    @Builder.Default
    private Set<Teacher> teachers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "classroom_lessons", joinColumns = @JoinColumn(name = "classroom_id"), inverseJoinColumns = @JoinColumn(name = "lesson_id"))
    @JsonIgnoreProperties({ "school", "classRooms", "teacher" })
    @Builder.Default
    private Set<Lesson> lessons = new HashSet<>();

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.getClassRooms().add(this);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.getClassRooms().remove(this);
    }
}