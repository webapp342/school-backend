package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = { "principal", "classRooms" })
@EqualsAndHashCode(exclude = { "principal", "classRooms" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(generator = "school-id")
    @GenericGenerator(name = "school-id", strategy = "com.schoolmanagement.util.SchoolIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "S")
    })
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String address;

    @OneToOne
    @JoinColumn(name = "principal_id", nullable = false)
    @JsonBackReference
    private User principal;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ClassRoom> classRooms = new ArrayList<>();
}