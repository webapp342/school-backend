package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessonRequest {
    private String name;
    private String code;
    private Integer duration;
    private String teacherId;
    private String department;
}