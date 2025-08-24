package com.namhatta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNamhattaDto {
    
    @NotBlank(message = "Namhatta name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Contact person is required")
    private String contactPerson;
    
    @Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]+$", message = "Invalid phone number format")
    private String contactPhone;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    private String establishedDate;
    
    private String regularProgramDay;
    
    private String regularProgramTime;
    
    private Long shraddhakutirId;
    
    private Long districtSupervisorId;
    
    // Address information
    private CreateAddressDto address;
}