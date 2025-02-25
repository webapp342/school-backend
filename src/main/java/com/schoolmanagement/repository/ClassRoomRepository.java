package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ClassRoom;
import com.schoolmanagement.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, String> {
    List<ClassRoom> findBySchool(School school);

    List<ClassRoom> findBySchoolId(String schoolId);

    @Query("SELECT DISTINCT c FROM ClassRoom c JOIN c.teachers t WHERE t.id = :teacherId")
    Set<ClassRoom> findClassroomsByTeacherId(@Param("teacherId") String teacherId);
}