package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateClassRoomRequest;
import com.schoolmanagement.entity.ClassRoom;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.entity.Teacher;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final SchoolService schoolService;
    private final TeacherService teacherService;
    private final LessonService lessonService;

    @Transactional
    public ClassRoom createClassRoom(String schoolId, CreateClassRoomRequest request) {
        School school = schoolService.getSchoolById(schoolId);

        ClassRoom classRoom = ClassRoom.builder()
                .name(request.getName())
                .grade(request.getGrade())
                .section(request.getSection())
                .capacity(request.getCapacity())
                .school(school)
                .build();

        return classRoomRepository.save(classRoom);
    }

    public List<ClassRoom> getClassRoomsBySchool(String schoolId) {
        return classRoomRepository.findBySchoolId(schoolId);
    }

    public ClassRoom getClassRoomById(String id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
    }

    public void deleteClassRoom(String id) {
        classRoomRepository.deleteById(id);
    }

    public boolean isClassRoomBelongsToSchool(String classRoomId, String schoolId) {
        return classRoomRepository.findById(classRoomId)
                .map(classRoom -> classRoom.getSchool().getId().equals(schoolId))
                .orElse(false);
    }

    @Transactional
    public ClassRoom addTeacherToClassRoom(String classRoomId, String teacherId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        Teacher teacher = teacherService.getTeacherById(teacherId);

        if (!teacher.getSchool().getId().equals(classRoom.getSchool().getId())) {
            throw new IllegalArgumentException("Teacher must belong to the same school");
        }

        classRoom.addTeacher(teacher);
        return classRoomRepository.save(classRoom);
    }

    @Transactional
    public ClassRoom removeTeacherFromClassRoom(String classRoomId, String teacherId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        Teacher teacher = teacherService.getTeacherById(teacherId);

        classRoom.removeTeacher(teacher);
        return classRoomRepository.save(classRoom);
    }

    @Transactional
    public ClassRoom addLessonToClassRoom(String classRoomId, String lessonId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        Lesson lesson = lessonService.getLessonById(lessonId);

        if (!lesson.getSchool().getId().equals(classRoom.getSchool().getId())) {
            throw new IllegalArgumentException("Lesson must belong to the same school");
        }

        classRoom.addLesson(lesson);
        return classRoomRepository.save(classRoom);
    }

    @Transactional
    public ClassRoom removeLessonFromClassRoom(String classRoomId, String lessonId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        Lesson lesson = lessonService.getLessonById(lessonId);

        classRoom.removeLesson(lesson);
        return classRoomRepository.save(classRoom);
    }

    public Set<Teacher> getClassRoomTeachers(String classRoomId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        return classRoom.getTeachers();
    }

    public Set<Lesson> getClassRoomLessons(String classRoomId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        return classRoom.getLessons();
    }
}