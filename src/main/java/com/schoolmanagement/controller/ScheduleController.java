package com.schoolmanagement.controller;

import com.schoolmanagement.dto.CreateScheduleRequest;
import com.schoolmanagement.dto.ScheduleResponse;
import com.schoolmanagement.entity.Schedule;
import com.schoolmanagement.service.ScheduleService;
import com.schoolmanagement.service.SchoolService;
import com.schoolmanagement.service.ClassRoomService;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final SchoolService schoolService;
    private final ClassRoomService classRoomService;
    private final TeacherService teacherService;

    @PostMapping("/classroom/{classroomId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classroomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<Schedule> createSchedule(
            @PathVariable String classroomId,
            @RequestBody CreateScheduleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(scheduleService.createSchedule(classroomId, request));
    }

    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("hasRole('PRINCIPAL') and @classRoomService.isClassRoomBelongsToSchool(#classroomId, @schoolService.getSchoolByPrincipalUsername(authentication.name).id)")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByClassroom(@PathVariable String classroomId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClassroom(classroomId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}