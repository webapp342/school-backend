package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateGradeRequest;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.service.GradeService;
import com.schoolmanagement.service.StudentService;
import com.schoolmanagement.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final SchoolService schoolService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Grade> createGrade(
            @RequestBody CreateGradeRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(gradeService.createGrade(request));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Grade> createOrUpdateGrade(
            @RequestBody CreateGradeRequest request,
            Authentication authentication) {
        try {
            log.info("Received request to create/update grade: {}", request);
            Grade grade = gradeService.createOrUpdateGrade(request);
            log.info("Grade created/updated successfully: {}", grade);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            log.error("Error creating/updating grade", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable String studentId) {
        return ResponseEntity.ok(gradeService.getStudentGrades(studentId));
    }

    @GetMapping("/student/{studentId}/lesson/{lessonId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Grade>> getStudentGradesByLesson(
            @PathVariable String studentId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(gradeService.getStudentGradesByLesson(studentId, lessonId));
    }

    @GetMapping("/student/{studentId}/averages")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Map<String, Double>> getStudentAverages(@PathVariable String studentId) {
        return ResponseEntity.ok(gradeService.calculateAllLessonAverages(studentId));
    }
}