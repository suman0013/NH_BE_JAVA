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
public class NamhattaDto {
    
    private Long id;
    private String name;
    private String description;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String establishedDate;
    private String regularProgramDay;
    private String regularProgramTime;
    private Boolean isApproved;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationship fields
    private Long shraddhakutirId;
    private String shraddhakutirName;
    private Long districtSupervisorId;
    private String districtSupervisorName;
    private List<NamhattaAddressDto> addresses;
    
    // Statistical fields
    private Long devoteeCount;
    private String status; // Computed field for UI (PENDING_APPROVAL, APPROVED, etc.)
}