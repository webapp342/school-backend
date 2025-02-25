package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, String> {
    List<Lesson> findByTeacherId(String teacherId);

    List<Lesson> findBySchoolId(String schoolId);

    boolean existsByCode(String code);
}