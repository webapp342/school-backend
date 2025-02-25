package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateTeacherRequest;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.service.TeacherService;
import com.schoolmanagement.service.SchoolService;
import com.schoolmanagement.service.DepartmentService;
import com.schoolmanagement.service.LessonService;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    private final TeacherService teacherService;
    private final SchoolService schoolService;
    private final DepartmentService departmentService;
    private final LessonService lessonService;
    private final TeacherRepository teacherRepository;
    private final ClassRoomRepository classRoomRepository;

    @GetMapping("/next-number")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Map<String, String>> getNextTeacherNumber() {
        try {
            log.info("Getting next teacher number");
            String nextNumber = teacherService.getNextTeacherNumber();
            log.info("Next teacher number: {}", nextNumber);
            Map<String, String> response = new HashMap<>();
            response.put("number", nextNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting next teacher number", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Teacher> createTeacher(
            @RequestBody CreateTeacherRequest request,
            Authentication authentication) {
        try {
            log.info("Creating teacher with request: {}", request);
            log.info("Authentication: {}", authentication.getName());

            String schoolId;
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"))) {
                schoolId = schoolService.getSchoolByPrincipalUsername(authentication.getName()).getId();
            } else {
                // Öğretmen için okul ID'sini bul
                Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
                schoolId = teacher.getSchool().getId();
            }
            log.info("School ID: {}", schoolId);

            Teacher createdTeacher = teacherService.createTeacher(schoolId, request);
            log.info("Teacher created successfully: {}", createdTeacher);

            return ResponseEntity.ok(createdTeacher);
        } catch (Exception e) {
            log.error("Error creating teacher", e);
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Teacher>> getAllTeachers(Authentication authentication) {
        String schoolId;
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"))) {
            schoolId = schoolService.getSchoolByPrincipalUsername(authentication.getName()).getId();
        } else {
            // Öğretmen için okul ID'sini bul
            Teacher teacher = teacherService.getTeacherByUsername(authentication.getName());
            schoolId = teacher.getSchool().getId();
        }
        return ResponseEntity.ok(teacherService.getTeachersBySchool(schoolId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable String id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Teacher> updateTeacherStatus(
            @PathVariable String id,
            @RequestBody TeacherStatus status) {
        return ResponseEntity.ok(teacherService.updateTeacherStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/department/{departmentId}/active")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<List<Teacher>> getActiveTeachersByDepartment(
            @PathVariable String departmentId,
            Authentication authentication) {
        return ResponseEntity.ok(teacherService.getActiveTeachersByDepartment(departmentId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Teacher> getMyTeacherInfo(Authentication authentication) {
        try {
            log.info("Getting teacher info for authenticated user: {}", authentication.getName());
            log.info("User authorities: {}", authentication.getAuthorities());

            // Get the username from the authentication object
            String username = authentication.getName();
            log.info("Username from authentication: {}", username);

            // Find the teacher by username
            Teacher teacher = null;
            try {
                teacher = teacherService.getTeacherByUsername(username);
                if (teacher == null) {
                    log.error("Teacher not found for username: {}", username);
                    return ResponseEntity.status(404).body(null);
                }

                log.info("Found teacher: ID={}, Name={} {}, TeacherNumber={}, Department={}",
                        teacher.getId(), teacher.getFirstName(), teacher.getLastName(),
                        teacher.getTeacherNumber(), teacher.getDepartment());

                // Öğretmenin sınıflarını kontrol et
                Set<ClassRoom> classrooms = teacherService.getTeacherClassrooms(teacher.getId());
                log.info("Teacher has access to {} classrooms", classrooms.size());

                return ResponseEntity.ok(teacher);
            } catch (Exception e) {
                log.error("Error retrieving teacher by username: {}", username, e);
                return ResponseEntity.status(500).body(null);
            }
        } catch (Exception e) {
            log.error("Unexpected error in getMyTeacherInfo", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/my-classrooms")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Set<ClassRoom>> getMyClassrooms(Authentication authentication) {
        try {
            log.info("Getting classrooms for authenticated user: {}", authentication.getName());

            // Get the username from the authentication object
            String username = authentication.getName();
            log.info("Username from authentication: {}", username);

            // Kullanıcının rolünü kontrol et
            boolean isPrincipal = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"));

            if (isPrincipal) {
                // Müdür için tüm sınıfları getir
                log.info("User is a principal, getting all classrooms");
                String schoolId = schoolService.getSchoolByPrincipalUsername(username).getId();
                List<ClassRoom> allClassrooms = classRoomRepository.findBySchoolId(schoolId);
                log.info("Found {} classrooms for principal", allClassrooms.size());

                // List'i Set'e dönüştür
                Set<ClassRoom> classroomSet = new HashSet<>(allClassrooms);
                return ResponseEntity.ok(classroomSet);
            } else {
                // Öğretmen için sınıfları getir
                log.info("User is a teacher, getting teacher's classrooms");
                Teacher teacher = teacherService.getTeacherByUsername(username);
                if (teacher == null) {
                    log.error("Teacher not found for username: {}", username);
                    return ResponseEntity.status(404).body(null);
                }

                log.info("Found teacher: ID={}, Name={} {}",
                        teacher.getId(), teacher.getFirstName(), teacher.getLastName());

                // Get teacher's classrooms
                Set<ClassRoom> classrooms = teacherService.getTeacherClassrooms(teacher.getId());
                log.info("Found {} classrooms for teacher ID: {}", classrooms.size(), teacher.getId());

                // Log classroom details
                classrooms.forEach(classroom -> log.info("Classroom: ID={}, Name={}, Grade={}, Section={}",
                        classroom.getId(), classroom.getName(), classroom.getGrade(), classroom.getSection()));

                return ResponseEntity.ok(classrooms);
            }
        } catch (Exception e) {
            log.error("Unexpected error in getMyClassrooms", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/my-lessons")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Lesson>> getMyLessons(Authentication authentication) {
        try {
            log.info("Getting lessons for authenticated user: {}", authentication.getName());
            String username = authentication.getName();

            // Kullanıcının rolünü kontrol et
            boolean isPrincipal = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"));

            if (isPrincipal) {
                // Müdür için tüm dersleri getir
                log.info("User is a principal, getting all lessons");
                String schoolId = schoolService.getSchoolByPrincipalUsername(username).getId();
                List<Lesson> allLessons = lessonService.getLessonsBySchool(schoolId);
                log.info("Found {} lessons for principal", allLessons.size());
                return ResponseEntity.ok(allLessons);
            } else {
                // Öğretmen için dersleri getir
                log.info("User is a teacher, getting teacher's lessons");
                Teacher teacher = teacherService.getTeacherByUsername(username);
                if (teacher == null) {
                    log.error("Teacher not found for username: {}", username);
                    return ResponseEntity.notFound().build();
                }

                log.info("Found teacher: ID={}, Name={} {}",
                        teacher.getId(), teacher.getFirstName(), teacher.getLastName());

                // Öğretmenin derslerini getir
                List<Lesson> lessons = lessonService.getLessonsByTeacher(teacher.getId());
                log.info("Found {} lessons for teacher ID: {}", lessons.size(), teacher.getId());
                return ResponseEntity.ok(lessons);
            }
        } catch (Exception e) {
            log.error("Unexpected error in getMyLessons", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/my-students")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Student>> getMyStudents(Authentication authentication) {
        try {
            log.info("Getting students for authenticated user: {}", authentication.getName());
            String username = authentication.getName();

            // Kullanıcının rolünü kontrol et
            boolean isPrincipal = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"));

            if (isPrincipal) {
                // Müdür için tüm öğrencileri getir
                log.info("User is a principal, getting all students");
                String schoolId = schoolService.getSchoolByPrincipalUsername(username).getId();

                // Okulun tüm sınıflarını bul
                List<ClassRoom> classrooms = classRoomRepository.findBySchoolId(schoolId);
                log.info("Found {} classrooms for school ID: {}", classrooms.size(), schoolId);

                // Tüm sınıfların öğrencilerini topla
                List<Student> allStudents = new ArrayList<>();
                for (ClassRoom classroom : classrooms) {
                    allStudents.addAll(classroom.getStudents());
                }

                log.info("Found {} students for principal", allStudents.size());
                return ResponseEntity.ok(allStudents);
            } else {
                // Öğretmen için öğrencileri getir
                log.info("User is a teacher, getting teacher's students");
                Teacher teacher = teacherService.getTeacherByUsername(username);
                if (teacher == null) {
                    log.error("Teacher not found for username: {}", username);
                    return ResponseEntity.notFound().build();
                }

                log.info("Found teacher: ID={}, Name={} {}",
                        teacher.getId(), teacher.getFirstName(), teacher.getLastName());

                // Öğretmenin öğrencilerini getir (öğretmenin sınıflarındaki tüm öğrenciler)
                List<Student> students = teacherService.getTeacherStudents(teacher.getId());
                log.info("Found {} students for teacher ID: {}", students.size(), teacher.getId());
                return ResponseEntity.ok(students);
            }
        } catch (Exception e) {
            log.error("Unexpected error in getMyStudents", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}