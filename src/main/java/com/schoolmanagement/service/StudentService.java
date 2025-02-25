package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateStudentRequest;
import com.schoolmanagement.entity.ClassRoom;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRoomService classRoomService;

    public String getNextStudentNumber() {
        Optional<Student> lastStudent = studentRepository.findTopByOrderByStudentNumberDesc();
        if (lastStudent.isEmpty()) {
            return "1000"; // Başlangıç numarası
        }

        int lastNumber = Integer.parseInt(lastStudent.get().getStudentNumber());
        return String.valueOf(lastNumber + 1);
    }

    @Transactional
    public Student createStudent(String classroomId, CreateStudentRequest request) {
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new RuntimeException("Student number already exists");
        }

        ClassRoom classroom = classRoomService.getClassRoomById(classroomId);

        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .studentNumber(request.getStudentNumber())
                .classroom(classroom)
                .build();

        return studentRepository.save(student);
    }

    public List<Student> getStudentsByClassroom(String classroomId) {
        return studentRepository.findByClassroomId(classroomId);
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    public boolean isStudentInClassroom(String studentId, String classroomId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getClassroom().getId().equals(classroomId))
                .orElse(false);
    }

    public Student getStudentById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public boolean isStudentBelongsToSchool(String studentId, String schoolId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getClassroom().getSchool().getId().equals(schoolId))
                .orElse(false);
    }
}