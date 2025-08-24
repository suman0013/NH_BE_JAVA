package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevoteeDto {
    
    private Long id;
    private String legalName;
    private String name;
    private String dob;
    private String email;
    private String phone;
    private String gender;
    private String alternatePhone;
    private String relationshipStatus;
    private String spouseName;
    private String fatherName;
    private String motherName;
    private String initiatedName;
    private String harinamDate;
    private String pancharatrikDate;
    private String education;
    private String occupation;
    private List<DevotionalCourseDto> devotionalCourses;
    private String remarks;
    private Long namhattaId;
    private String namhattaName;
    private Long devotionalStatusId;
    private String devotionalStatusName;
    private List<DevoteeAddressDto> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}