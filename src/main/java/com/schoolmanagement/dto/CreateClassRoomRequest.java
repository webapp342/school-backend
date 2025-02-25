package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRoomRequest {
    private String name;
    private Integer grade;
    private String section;
    private Integer capacity;
}