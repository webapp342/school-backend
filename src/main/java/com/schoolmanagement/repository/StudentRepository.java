package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassroomId(String classroomId);

    boolean existsByStudentNumber(String studentNumber);

    Optional<Student> findTopByOrderByStudentNumberDesc();
}