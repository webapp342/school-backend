package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByClassroomId(String classroomId);

    List<Schedule> findByLessonId(String lessonId);

    List<Schedule> findByClassroomIdAndLessonId(String classroomId, String lessonId);
}