package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    List<Grade> findByStudentId(String studentId);

    List<Grade> findByStudentIdAndLessonId(String studentId, String lessonId);

    Optional<Grade> findByStudentIdAndLessonIdAndExamNumber(String studentId, String lessonId, Integer examNumber);
}