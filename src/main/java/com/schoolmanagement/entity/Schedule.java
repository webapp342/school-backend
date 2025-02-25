package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Schedule {
    @Id
    @GeneratedValue(generator = "schedule-id")
    @GenericGenerator(name = "schedule-id", strategy = "com.schoolmanagement.util.ScheduleIdGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "prefix", value = "SCH")
    })
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    @JsonIgnoreProperties({ "schedules", "students", "teachers", "lessons", "school" })
    private ClassRoom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnoreProperties({ "schedules", "classRooms", "teacher", "school" })
    private Lesson lesson;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer lessonOrder; // Kaçıncı ders (1, 2, 3, ...)
}