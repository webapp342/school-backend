package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@ToString(exclude = "classroom")
@EqualsAndHashCode(exclude = "classroom")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Student {
    @Id
    @GeneratedValue(generator = "student-id")
    @GenericGenerator(name = "student-id", strategy = "com.schoolmanagement.util.StudentIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "ST")
    })
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String studentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    @JsonIgnoreProperties({ "students", "teachers", "lessons" })
    private ClassRoom classroom;
}