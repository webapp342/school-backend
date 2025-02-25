package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateGradeRequest;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentService studentService;
    private final LessonService lessonService;

    @Transactional
    public Grade createGrade(CreateGradeRequest request) {
        Student student = studentService.getStudentById(request.getStudentId());
        Lesson lesson = lessonService.getLessonById(request.getLessonId());

        Grade grade = Grade.builder()
                .student(student)
                .lesson(lesson)
                .examNumber(request.getExamNumber())
                .score(request.getScore())
                .notes(request.getNotes())
                .build();

        return gradeRepository.save(grade);
    }

    @Transactional
    public Grade createOrUpdateGrade(CreateGradeRequest request) {
        Student student = studentService.getStudentById(request.getStudentId());
        Lesson lesson = lessonService.getLessonById(request.getLessonId());

        // Aynı öğrenci, ders ve sınav numarası için not var mı kontrol et
        Optional<Grade> existingGrade = gradeRepository.findByStudentIdAndLessonIdAndExamNumber(
                request.getStudentId(),
                request.getLessonId(),
                request.getExamNumber());

        Grade grade;
        if (existingGrade.isPresent()) {
            // Mevcut notu güncelle
            grade = existingGrade.get();
            grade.setScore(request.getScore());
            grade.setNotes(request.getNotes());
        } else {
            // Yeni not oluştur
            grade = Grade.builder()
                    .student(student)
                    .lesson(lesson)
                    .examNumber(request.getExamNumber())
                    .score(request.getScore())
                    .notes(request.getNotes())
                    .build();
        }

        return gradeRepository.save(grade);
    }

    public List<Grade> getStudentGrades(String studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public Map<String, List<Grade>> getStudentGradesByLesson(String studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getLesson().getId()));
    }

    public List<Grade> getStudentGradesByLesson(String studentId, String lessonId) {
        return gradeRepository.findByStudentIdAndLessonId(studentId, lessonId);
    }

    public double calculateLessonAverage(String studentId, String lessonId) {
        List<Grade> grades = getStudentGradesByLesson(studentId, lessonId);
        if (grades.isEmpty()) {
            return 0.0;
        }
        return grades.stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);
    }

    public Map<String, Double> calculateAllLessonAverages(String studentId) {
        Map<String, List<Grade>> gradesByLesson = getStudentGradesByLesson(studentId);
        return gradesByLesson.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(Grade::getScore)
                                .average()
                                .orElse(0.0)));
    }
}