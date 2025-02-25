package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateDepartmentRequest;
import com.schoolmanagement.entity.Department;
import com.schoolmanagement.service.DepartmentService;
import com.schoolmanagement.service.SchoolService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;
    private final SchoolService schoolService;

    @GetMapping("/next-code")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Map<String, String>> getNextDepartmentCode() {
        try {
            log.info("Getting next department code");
            String nextCode = departmentService.getNextDepartmentCode();
            log.info("Next department code: {}", nextCode);
            Map<String, String> response = new HashMap<>();
            response.put("code", nextCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting next department code", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Department> createDepartment(
            @RequestBody CreateDepartmentRequest request,
            Authentication authentication) {

        String schoolId = schoolService.getSchoolByPrincipalUsername(authentication.getName()).getId();

        return ResponseEntity.ok(departmentService.createDepartment(schoolId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<List<Department>> getDepartments(Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();
        return ResponseEntity.ok(departmentService.getDepartmentsBySchool(schoolId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String id, Authentication authentication) {
        String schoolId = schoolService.getSchoolByUserUsername(authentication.getName()).getId();

        if (!departmentService.isDepartmentBelongsToSchool(id, schoolId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') and @departmentService.isDepartmentBelongsToSchool(#id, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }
}