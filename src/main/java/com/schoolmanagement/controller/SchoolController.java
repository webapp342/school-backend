package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateSchoolRequest;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<School> createSchool(@RequestBody CreateSchoolRequest request) {
        return ResponseEntity.ok(schoolService.createSchool(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<School>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or @schoolService.getSchoolById(#id).principal.username == authentication.name")
    public ResponseEntity<School> getSchoolById(@PathVariable String id) {
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or @schoolService.getSchoolByCode(#code).principal.username == authentication.name")
    public ResponseEntity<School> getSchoolByCode(@PathVariable String code) {
        return ResponseEntity.ok(schoolService.getSchoolByCode(code));
    }

    @GetMapping("/my-school")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'TEACHER')")
    public ResponseEntity<School> getMySchool(Authentication authentication) {
        return ResponseEntity.ok(schoolService.getSchoolByUserUsername(authentication.getName()));
    }
}