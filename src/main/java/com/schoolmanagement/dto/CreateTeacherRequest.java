package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeacherRequest {
    private String firstName;
    private String lastName;
    private String teacherNumber;
    private String department;
    private String username;
    private String password;
}