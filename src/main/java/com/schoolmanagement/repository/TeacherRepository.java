package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.TeacherStatus;
import com.schoolmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    List<Teacher> findBySchoolId(String schoolId);

    List<Teacher> findBySchoolIdAndDepartmentAndStatus(String schoolId, String department, TeacherStatus status);

    boolean existsByIdAndSchoolId(String id, String schoolId);

    List<Teacher> findByDepartmentAndStatus(String department, TeacherStatus status);

    // Find a teacher by user ID
    Optional<Teacher> findByUserId(Long userId);

    // Find a teacher by teacher number
    Optional<Teacher> findByTeacherNumber(String teacherNumber);

    // Find a teacher by user entity
    Optional<Teacher> findByUser(User user);
}