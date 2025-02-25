package com.schoolmanagement.service;

import com.schoolmanagement.dto.CreateScheduleRequest;
import com.schoolmanagement.dto.ScheduleResponse;
import com.schoolmanagement.entity.ClassRoom;
import com.schoolmanagement.entity.Lesson;
import com.schoolmanagement.entity.Schedule;
import com.schoolmanagement.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomService classRoomService;
    private final LessonService lessonService;

    @Transactional
    public Schedule createSchedule(String classroomId, CreateScheduleRequest request) {
        ClassRoom classroom = classRoomService.getClassRoomById(classroomId);
        Lesson lesson = lessonService.getLessonById(request.getLessonId());

        // Çakışma kontrolü
        List<Schedule> existingSchedules = scheduleRepository.findByClassroomId(classroomId);
        boolean hasConflict = existingSchedules.stream()
                .anyMatch(s -> s.getDayOfWeek() == request.getDayOfWeek() &&
                        (s.getLessonOrder().equals(request.getLessonOrder()) ||
                                (s.getStartTime().isBefore(request.getEndTime())
                                        && s.getEndTime().isAfter(request.getStartTime()))));

        if (hasConflict) {
            throw new RuntimeException("Schedule conflict detected");
        }

        Schedule schedule = Schedule.builder()
                .classroom(classroom)
                .lesson(lesson)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .lessonOrder(request.getLessonOrder())
                .build();

        return scheduleRepository.save(schedule);
    }

    public List<ScheduleResponse> getSchedulesByClassroom(String classroomId) {
        List<Schedule> schedules = scheduleRepository.findByClassroomId(classroomId);
        return schedules.stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    public void deleteSchedule(String id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .lessonId(schedule.getLesson().getId())
                .lessonName(schedule.getLesson().getName())
                .lessonCode(schedule.getLesson().getCode())
                .teacherName(
                        schedule.getLesson().getTeacher() != null ? schedule.getLesson().getTeacher().getFirstName()
                                + " " + schedule.getLesson().getTeacher().getLastName() : null)
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .lessonOrder(schedule.getLessonOrder())
                .build();
    }
}