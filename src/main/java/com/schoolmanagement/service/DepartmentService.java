package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateDepartmentRequest;
import com.schoolmanagement.entity.Department;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final SchoolService schoolService;

    public String getNextDepartmentCode() {
        try {
            log.info("Fetching all departments to generate next code");
            List<Department> departments = departmentRepository.findAll();
            log.info("Found {} departments", departments.size());

            if (departments == null || departments.isEmpty()) {
                log.info("No departments found, returning initial code D1");
                return "D1"; // İlk departman için başlangıç kodu
            }

            int maxNumber = 0;
            Pattern pattern = Pattern.compile("D(\\d+)");

            for (Department department : departments) {
                if (department.getCode() != null) {
                    Matcher matcher = pattern.matcher(department.getCode());
                    if (matcher.matches()) {
                        int number = Integer.parseInt(matcher.group(1));
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    }
                }
            }

            String nextCode = "D" + (maxNumber + 1);
            log.info("Generated next department code: {}", nextCode);
            return nextCode;
        } catch (Exception e) {
            log.error("Error generating next department code", e);
            // Hata durumunda varsayılan bir kod döndür
            return "D1";
        }
    }

    @Transactional
    public Department createDepartment(String schoolId, CreateDepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists");
        }

        School school = schoolService.getSchoolById(schoolId);

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .school(school)
                .build();

        return departmentRepository.save(department);
    }

    public List<Department> getDepartmentsBySchool(String schoolId) {
        return departmentRepository.findBySchoolId(schoolId);
    }

    public Department getDepartmentById(String id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public void deleteDepartment(String id) {
        departmentRepository.deleteById(id);
    }

    public boolean isDepartmentBelongsToSchool(String departmentId, String schoolId) {
        return departmentRepository.findById(departmentId)
                .map(department -> department.getSchool().getId().equals(schoolId))
                .orElse(false);
    }
}