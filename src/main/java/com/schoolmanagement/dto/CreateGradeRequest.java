package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeRequest {
    private String studentId;
    private String lessonId;
    private Integer examNumber;
    private Double score;
    private String notes;
}