package com.namhatta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDevoteeDto {
    
    @NotBlank(message = "Legal name is required")
    private String legalName;
    
    private String name;
    
    private String dob;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]+$", message = "Invalid phone number format")
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
    private Long devotionalStatusId;
    private CreateAddressDto presentAddress;
    private CreateAddressDto permanentAddress;
}