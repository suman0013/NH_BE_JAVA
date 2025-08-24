package com.namhatta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNamhattaDto {
    
    private String name;
    private String description;
    private String contactPerson;
    
    @Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]+$", message = "Invalid phone number format")
    private String contactPhone;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    private String establishedDate;
    private String regularProgramDay;
    private String regularProgramTime;
    private Boolean isApproved;
    private Boolean isActive;
    private Long shraddhakutirId;
    private Long districtSupervisorId;
    
    // Address information
    private CreateAddressDto address;
}