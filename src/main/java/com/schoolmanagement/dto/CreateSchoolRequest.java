package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSchoolRequest {
    private String name;
    private String code;
    private String address;
    private String principalUsername;
    private String principalPassword;
}