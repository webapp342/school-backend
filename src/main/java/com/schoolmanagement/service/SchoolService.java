package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateSchoolRequest;
import com.schoolmanagement.entity.Role;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.SchoolRepository;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public School createSchool(CreateSchoolRequest request) {
        // Check if school code already exists
        if (schoolRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("School code already exists");
        }

        // Create principal user with code based on school code
        User principal = new User();
        principal.setUsername(request.getPrincipalUsername());
        principal.setPassword(passwordEncoder.encode(request.getPrincipalPassword()));
        principal.setCode("PRINCIPAL_" + request.getCode());
        principal.setRole(Role.PRINCIPAL);
        userRepository.save(principal);

        // Create school
        School school = new School();
        school.setName(request.getName());
        school.setCode(request.getCode());
        school.setAddress(request.getAddress());
        school.setPrincipal(principal);

        return schoolRepository.save(school);
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School getSchoolById(String id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    public School getSchoolByCode(String code) {
        return schoolRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    public School getSchoolByPrincipalUsername(String username) {
        User principal = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        return schoolRepository.findByPrincipal(principal)
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    public School getSchoolByUserUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.PRINCIPAL) {
            return schoolRepository.findByPrincipal(user)
                    .orElseThrow(() -> new RuntimeException("School not found"));
        } else if (user.getRole() == Role.TEACHER) {
            return teacherRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"))
                    .getSchool();
        }

        throw new RuntimeException("Invalid user role");
    }
}