package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(generator = "department-id")
    @GenericGenerator(name = "department-id", strategy = "com.schoolmanagement.util.DepartmentIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "D")
    })
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
}