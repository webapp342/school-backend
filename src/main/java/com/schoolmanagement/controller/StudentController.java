package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateStudentRequest;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.service.StudentService;
import com.schoolmanagement.service.SchoolService;
import com.schoolmanagement.service.ClassRoomService;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SchoolService schoolService;
    private final ClassRoomService classRoomService;
    private final TeacherService teacherService;

    @GetMapping("/next-number")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<String> getNextStudentNumber() {
        return ResponseEntity.ok(studentService.getNextStudentNumber());
    }

    @PostMapping("/classroom/{classroomId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classroomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Student> createStudent(
            @PathVariable String classroomId,
            @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(classroomId, request));
    }

    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classroomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<List<Student>> getStudentsByClassroom(@PathVariable String classroomId) {
        return ResponseEntity.ok(studentService.getStudentsByClassroom(classroomId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') and @studentService.isStudentInClassroom(#id, #classroomId) and @classRoomService.isClassRoomBelongsToSchool(#classroomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable String id,
            @RequestParam String classroomId) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') and @studentService.isStudentBelongsToSchool(#id, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
}