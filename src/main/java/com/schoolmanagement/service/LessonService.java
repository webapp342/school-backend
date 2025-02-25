package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateLessonRequest;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.LessonRepository;
import com.schoolmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherService teacherService;
    private final SchoolService schoolService;

    public String getNextLessonCode() {
        try {
            log.info("Fetching all lessons to generate next lesson code");
            List<Lesson> lessons = lessonRepository.findAll();
            log.info("Found {} lessons", lessons.size());

            if (lessons == null || lessons.isEmpty()) {
                log.info("No lessons found, returning initial code L100");
                return "L100"; // İlk ders için başlangıç kodu
            }

            int maxNumber = 0;
            Pattern pattern = Pattern.compile("L(\\d+)");

            for (Lesson lesson : lessons) {
                if (lesson.getCode() != null) {
                    Matcher matcher = pattern.matcher(lesson.getCode());
                    if (matcher.matches()) {
                        int number = Integer.parseInt(matcher.group(1));
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    }
                }
            }

            String nextCode = "L" + (maxNumber + 1);
            log.info("Generated next lesson code: {}", nextCode);
            return nextCode;
        } catch (Exception e) {
            log.error("Error generating next lesson code", e);
            // Hata durumunda varsayılan bir kod döndür
            return "L100";
        }
    }

    @Transactional
    public Lesson createLesson(String schoolId, CreateLessonRequest request) {
        if (lessonRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Lesson code already exists");
        }

        School school = schoolService.getSchoolById(schoolId);
        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherService.getTeacherById(request.getTeacherId());
            if (!teacher.getSchool().getId().equals(schoolId)) {
                throw new RuntimeException("Teacher does not belong to this school");
            }
        }

        Lesson lesson = Lesson.builder()
                .name(request.getName())
                .code(request.getCode())
                .duration(request.getDuration())
                .department(request.getDepartment())
                .teacher(teacher)
                .school(school)
                .build();

        return lessonRepository.save(lesson);
    }

    public List<Lesson> getLessonsBySchool(String schoolId) {
        return lessonRepository.findBySchoolId(schoolId);
    }

    public Lesson getLessonById(String id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public List<Lesson> getLessonsByTeacher(String teacherId) {
        return lessonRepository.findByTeacherId(teacherId);
    }

    public void deleteLesson(String id) {
        lessonRepository.deleteById(id);
    }

    public boolean isLessonBelongsToSchool(String lessonId, String schoolId) {
        return lessonRepository.findById(lessonId)
                .map(lesson -> lesson.getSchool().getId().equals(schoolId))
                .orElse(false);
    }

    public Lesson assignTeacher(String lessonId, String teacherId) {
        Lesson lesson = getLessonById(lessonId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        if (!teacher.getDepartment().equals(lesson.getDepartment())) {
            throw new IllegalArgumentException("Teacher must be from the same department as the lesson");
        }

        lesson.setTeacher(teacher);
        return lessonRepository.save(lesson);
    }

    public Lesson removeTeacher(String lessonId) {
        Lesson lesson = getLessonById(lessonId);
        lesson.setTeacher(null);
        return lessonRepository.save(lesson);
    }
}