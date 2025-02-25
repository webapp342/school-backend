package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateTeacherRequest;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.DepartmentRepository;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.repository.UserRepository;
import com.schoolmanagement.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SchoolService schoolService;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClassRoomRepository classRoomRepository;

    public String getNextTeacherNumber() {
        try {
            log.info("Fetching all teachers to generate next teacher number");
            List<Teacher> teachers = teacherRepository.findAll();
            log.info("Found {} teachers", teachers.size());

            if (teachers == null || teachers.isEmpty()) {
                log.info("No teachers found, returning initial number T1000");
                return "T1000"; // İlk öğretmen için başlangıç numarası
            }

            int maxNumber = 0;
            Pattern pattern = Pattern.compile("T(\\d+)");

            for (Teacher teacher : teachers) {
                if (teacher.getTeacherNumber() != null) {
                    Matcher matcher = pattern.matcher(teacher.getTeacherNumber());
                    if (matcher.matches()) {
                        int number = Integer.parseInt(matcher.group(1));
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    }
                }
            }

            String nextNumber = "T" + (maxNumber + 1);
            log.info("Generated next teacher number: {}", nextNumber);
            return nextNumber;
        } catch (Exception e) {
            log.error("Error generating next teacher number", e);
            // Hata durumunda varsayılan bir numara döndür
            return "T1000";
        }
    }

    @Transactional
    public Teacher createTeacher(String schoolId, CreateTeacherRequest request) {
        try {
            log.info("Creating teacher for school ID: {}", schoolId);
            log.info("Teacher request: {}", request);

            School school = schoolService.getSchoolById(schoolId);
            log.info("Found school: {}", school.getName());

            Department department = departmentRepository.findById(request.getDepartment())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + request.getDepartment()));
            log.info("Found department: {}", department.getName());

            // Kullanıcı adı kontrolü
            if (userRepository.existsByUsername(request.getUsername())) {
                log.error("Username already exists: {}", request.getUsername());
                throw new RuntimeException("Username already exists");
            }

            // Kullanıcı oluştur
            log.info("Creating user with username: {}", request.getUsername());
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .code(request.getTeacherNumber())
                    .role(Role.TEACHER)
                    .build();

            User savedUser = userRepository.save(user);
            log.info("User created with ID: {}", savedUser.getId());

            Teacher teacher = Teacher.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .teacherNumber(request.getTeacherNumber())
                    .department(department.getCode())
                    .status(TeacherStatus.ACTIVE)
                    .school(school)
                    .user(savedUser)
                    .build();

            Teacher savedTeacher = teacherRepository.save(teacher);
            log.info("Teacher saved with ID: {}", savedTeacher.getId());

            // User-Teacher ilişkisini güncelle
            savedUser.setTeacher(savedTeacher);
            userRepository.save(savedUser);
            log.info("User-Teacher relationship updated");

            return savedTeacher;
        } catch (Exception e) {
            log.error("Error creating teacher", e);
            throw e;
        }
    }

    public List<Teacher> getTeachersBySchool(String schoolId) {
        return teacherRepository.findBySchoolId(schoolId);
    }

    public Teacher getTeacherById(String id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    @Transactional
    public Teacher updateTeacherStatus(String id, TeacherStatus status) {
        Teacher teacher = getTeacherById(id);
        teacher.setStatus(status);
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(String id) {
        Teacher teacher = getTeacherById(id);
        if (teacher.getUser() != null) {
            userRepository.delete(teacher.getUser());
        }
        teacherRepository.deleteById(id);
    }

    public boolean isTeacherBelongsToSchool(String teacherId, String schoolId) {
        return teacherRepository.existsByIdAndSchoolId(teacherId, schoolId);
    }

    public List<Teacher> getActiveTeachersByDepartment(String departmentCode) {
        return teacherRepository.findByDepartmentAndStatus(departmentCode, TeacherStatus.ACTIVE);
    }

    public Teacher getTeacherByUsername(String username) {
        try {
            log.info("Finding teacher by username: {}", username);

            if (username == null || username.isEmpty()) {
                log.error("Username is null or empty");
                return null;
            }

            // Find the user by username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found with username: {}", username);
                        return new RuntimeException("User not found with username: " + username);
                    });

            log.info("Found user with ID: {}, role: {}, code: {}", user.getId(), user.getRole(), user.getCode());

            // Check if the user has the TEACHER role
            if (user.getRole() != Role.TEACHER) {
                log.error("User found but does not have TEACHER role. Actual role: {}", user.getRole());
                throw new RuntimeException("User does not have TEACHER role");
            }

            // Değişiklik burada: user.getId() yerine user.getCode() kullanarak öğretmeni
            // buluyoruz
            // Öğretmenin teacher_number değeri, user tablosundaki code değeri ile eşleşiyor
            Teacher teacher = teacherRepository.findByTeacherNumber(user.getCode())
                    .orElseThrow(() -> {
                        log.error("No teacher record found for teacher number: {}", user.getCode());
                        return new RuntimeException("No teacher record found for this user");
                    });

            log.info("Found teacher with ID: {}, name: {} {}, teacher_number: {}",
                    teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getTeacherNumber());

            return teacher;
        } catch (Exception e) {
            log.error("Error finding teacher by username: {}", username, e);
            throw e;
        }
    }

    public Set<ClassRoom> getTeacherClassrooms(String teacherId) {
        try {
            log.info("Getting classrooms for teacher ID: {}", teacherId);

            // Öğretmenin var olup olmadığını kontrol et
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> {
                        log.error("Teacher not found with ID: {}", teacherId);
                        return new RuntimeException("Teacher not found with ID: " + teacherId);
                    });

            log.info("Found teacher: {} {}, teacher_number: {}",
                    teacher.getFirstName(), teacher.getLastName(), teacher.getTeacherNumber());

            // Öğretmenin sınıflarını bul
            Set<ClassRoom> classrooms = classRoomRepository.findClassroomsByTeacherId(teacherId);

            log.info("Found {} classrooms for teacher ID: {}", classrooms.size(), teacherId);

            // Sınıf ID'lerini loglayalım
            classrooms.forEach(classroom -> log.info("Teacher's classroom: ID={}, Name={}, Grade={}, Section={}",
                    classroom.getId(), classroom.getName(), classroom.getGrade(), classroom.getSection()));

            return classrooms;
        } catch (Exception e) {
            log.error("Error getting classrooms for teacher ID: {}", teacherId, e);
            return new HashSet<>();
        }
    }

    public List<Student> getTeacherStudents(String teacherId) {
        Set<ClassRoom> classrooms = getTeacherClassrooms(teacherId);
        List<Student> students = new ArrayList<>();

        for (ClassRoom classroom : classrooms) {
            students.addAll(classroom.getStudents());
        }

        return students;
    }

    public boolean canTeacherAccessClassroom(String classroomId, String username) {
        try {
            log.info("Checking if teacher with username '{}' can access classroom '{}'", username, classroomId);

            // Sınıf ID'si kontrolü
            if (classroomId == null || classroomId.isEmpty()) {
                log.error("Classroom ID is null or empty");
                return false;
            }

            // Kullanıcı adı kontrolü
            if (username == null || username.isEmpty()) {
                log.error("Username is null or empty");
                return false;
            }

            // Öğretmeni bul
            Teacher teacher = getTeacherByUsername(username);
            if (teacher == null) {
                log.error("Teacher not found for username: {}", username);
                return false;
            }

            log.info("Found teacher: ID={}, Name={} {}, TeacherNumber={}",
                    teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getTeacherNumber());

            // Öğretmenin sınıflarını bul
            Set<ClassRoom> classrooms = getTeacherClassrooms(teacher.getId());
            log.info("Teacher has access to {} classrooms", classrooms.size());

            // Öğretmenin belirtilen sınıfa erişimi var mı kontrol et
            boolean hasAccess = classrooms.stream()
                    .anyMatch(classroom -> classroom.getId().equals(classroomId));

            log.info("Teacher {} access to classroom {}: {}",
                    hasAccess ? "HAS" : "DOES NOT HAVE", classroomId, hasAccess);

            return hasAccess;
        } catch (Exception e) {
            log.error("Error checking if teacher can access classroom", e);
            return false;
        }
    }
}