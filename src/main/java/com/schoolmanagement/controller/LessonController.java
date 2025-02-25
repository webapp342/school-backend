package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateLessonRequest;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.service.LessonService;
import com.schoolmanagement.service.SchoolService;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Slf4j
public class LessonController {

    private final LessonService lessonService;
    private final SchoolService schoolService;
    private final TeacherService teacherService;

    @GetMapping("/next-code")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Map<String, String>> getNextLessonCode() {
        try {
            log.info("Getting next lesson code");
            String nextCode = lessonService.getNextLessonCode();
            log.info("Next lesson code: {}", nextCode);
            Map<String, String> response = new HashMap<>();
            response.put("code", nextCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting next lesson code", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Lesson> createLesson(
            @RequestBody CreateLessonRequest request,
            Authentication authentication) {
        String schoolId = schoolService.getSchoolByPrincipalUsername(authentication.getName()).getId();
        return ResponseEntity.ok(lessonService.createLesson(schoolId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<List<Lesson>> getAllLessons(Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();
        return ResponseEntity.ok(lessonService.getLessonsBySchool(schoolId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<Lesson> getLessonById(@PathVariable String id, Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!lessonService.isLessonBelongsToSchool(id, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<List<Lesson>> getLessonsByTeacher(@PathVariable String teacherId,
            Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!teacherService.isTeacherBelongsToSchool(teacherId, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(lessonService.getLessonsByTeacher(teacherId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') and @lessonService.isLessonBelongsToSchool(#id, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{lessonId}/teacher/{teacherId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @lessonService.isLessonBelongsToSchool(#lessonId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id) and @teacherService.isTeacherBelongsToSchool(#teacherId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Lesson> assignTeacher(
            @PathVariable String lessonId,
            @PathVariable String teacherId) {
        return ResponseEntity.ok(lessonService.assignTeacher(lessonId, teacherId));
    }

    @DeleteMapping("/{id}/teacher")
    @PreAuthorize("hasRole('PRINCIPAL') and @lessonService.isLessonBelongsToSchool(#id, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Lesson> removeTeacher(@PathVariable String id) {
        return ResponseEntity.ok(lessonService.removeTeacher(id));
    }
}