package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateClassRoomRequest;
import com.schoolmanagement.entity.ClassRoom;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.entity.Role;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.service.ClassRoomService;
import com.schoolmanagement.service.SchoolService;
import com.schoolmanagement.service.TeacherService;
import com.schoolmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classRoomService;
    private final SchoolService schoolService;
    private final TeacherService teacherService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ClassRoom> createClassRoom(
            @RequestBody CreateClassRoomRequest request,
            Authentication authentication) {
        String schoolId = schoolService.getSchoolByPrincipalUsername(authentication.getName()).getId();
        return ResponseEntity.ok(classRoomService.createClassRoom(schoolId, request));
    }

    @GetMapping("/my-school")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<List<ClassRoom>> getMySchoolClassRooms(Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();
        return ResponseEntity.ok(classRoomService.getClassRoomsBySchool(schoolId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<ClassRoom> getClassRoomById(@PathVariable String id, Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!classRoomService.isClassRoomBelongsToSchool(id, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classRoomService.getClassRoomById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#id, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Void> deleteClassRoom(@PathVariable String id) {
        classRoomService.deleteClassRoom(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/teachers")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<Set<Teacher>> getClassRoomTeachers(@PathVariable String id, Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!classRoomService.isClassRoomBelongsToSchool(id, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classRoomService.getClassRoomTeachers(id));
    }

    @PutMapping("/{classRoomId}/teachers/{teacherId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classRoomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<ClassRoom> addTeacherToClassRoom(
            @PathVariable String classRoomId,
            @PathVariable String teacherId) {
        return ResponseEntity.ok(classRoomService.addTeacherToClassRoom(classRoomId, teacherId));
    }

    @DeleteMapping("/{classRoomId}/teachers/{teacherId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classRoomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<ClassRoom> removeTeacherFromClassRoom(
            @PathVariable String classRoomId,
            @PathVariable String teacherId) {
        return ResponseEntity.ok(classRoomService.removeTeacherFromClassRoom(classRoomId, teacherId));
    }

    @GetMapping("/{id}/lessons")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<Set<Lesson>> getClassRoomLessons(@PathVariable String id, Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!classRoomService.isClassRoomBelongsToSchool(id, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(classRoomService.getClassRoomLessons(id));
    }

    @PutMapping("/{classRoomId}/lessons/{lessonId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classRoomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<ClassRoom> addLessonToClassRoom(
            @PathVariable String classRoomId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(classRoomService.addLessonToClassRoom(classRoomId, lessonId));
    }

    @DeleteMapping("/{classRoomId}/lessons/{lessonId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classRoomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<ClassRoom> removeLessonFromClassRoom(
            @PathVariable String classRoomId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(classRoomService.removeLessonFromClassRoom(classRoomId, lessonId));
    }
}