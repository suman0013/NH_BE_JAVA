package com.namhatta.mapper;

import com.namhatta.dto.*;
import com.namhatta.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NamhattaMapper for converting between Namhatta entities and DTOs
 * Maintains 100% compatibility with Node.js data transformation logic
 */
@Component
public class NamhattaMapper {
    
    /**
     * Convert Namhatta entity to NamhattaDto
     */
    public NamhattaDto toDto(Namhatta namhatta) {
        if (namhatta == null) {
            return null;
        }
        
        return NamhattaDto.builder()
                .id(namhatta.getId())
                .name(namhatta.getName())
                .description(namhatta.getDescription())
                .contactPerson(namhatta.getContactPerson())
                .contactPhone(namhatta.getContactPhone())
                .contactEmail(namhatta.getContactEmail())
                .establishedDate(namhatta.getEstablishedDate())
                .regularProgramDay(namhatta.getRegularProgramDay())
                .regularProgramTime(namhatta.getRegularProgramTime())
                .isApproved(namhatta.getIsApproved())
                .isActive(namhatta.getIsActive())
                .shraddhakutirId(namhatta.getShraddhakutir() != null ? Long.valueOf(namhatta.getShraddhakutir().getId()) : null)
                .shraddhakutirName(namhatta.getShraddhakutir() != null ? namhatta.getShraddhakutir().getName() : null)
                .districtSupervisorId(namhatta.getDistrictSupervisor() != null ? namhatta.getDistrictSupervisor().getId() : null)
                .districtSupervisorName(namhatta.getDistrictSupervisor() != null ? namhatta.getDistrictSupervisor().getUsername() : null)
                .addresses(new ArrayList<>()) // TODO: Map addresses when relationship is available
                .devoteeCount(Long.valueOf(namhatta.getDevotees().size()))
                .status(determineStatus(namhatta))
                .createdAt(namhatta.getCreatedAt())
                .updatedAt(namhatta.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert CreateNamhattaDto to Namhatta entity
     */
    public Namhatta toEntity(CreateNamhattaDto dto) {
        if (dto == null) {
            return null;
        }
        
        Namhatta namhatta = new Namhatta();
        namhatta.setName(dto.getName());
        namhatta.setDescription(dto.getDescription());
        namhatta.setContactPerson(dto.getContactPerson());
        namhatta.setContactPhone(dto.getContactPhone());
        namhatta.setContactEmail(dto.getContactEmail());
        namhatta.setEstablishedDate(dto.getEstablishedDate());
        namhatta.setRegularProgramDay(dto.getRegularProgramDay());
        namhatta.setRegularProgramTime(dto.getRegularProgramTime());
        
        // Set relationships (these will be resolved by service layer)
        if (dto.getShraddhakutirId() != null) {
            Shraddhakutir shraddhakutir = new Shraddhakutir();
            shraddhakutir.setId(dto.getShraddhakutirId().intValue());
            namhatta.setShraddhakutir(shraddhakutir);
        }
        
        if (dto.getDistrictSupervisorId() != null) {
            User supervisor = new User();
            supervisor.setId(dto.getDistrictSupervisorId());
            namhatta.setDistrictSupervisor(supervisor);
        }
        
        return namhatta;
    }
    
    /**
     * Update existing Namhatta entity with UpdateNamhattaDto data
     */
    public void updateEntityFromDto(UpdateNamhattaDto dto, Namhatta namhatta) {
        if (dto == null || namhatta == null) {
            return;
        }
        
        // Update only non-null fields from DTO
        if (dto.getName() != null) {
            namhatta.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            namhatta.setDescription(dto.getDescription());
        }
        if (dto.getContactPerson() != null) {
            namhatta.setContactPerson(dto.getContactPerson());
        }
        if (dto.getContactPhone() != null) {
            namhatta.setContactPhone(dto.getContactPhone());
        }
        if (dto.getContactEmail() != null) {
            namhatta.setContactEmail(dto.getContactEmail());
        }
        if (dto.getEstablishedDate() != null) {
            namhatta.setEstablishedDate(dto.getEstablishedDate());
        }
        if (dto.getRegularProgramDay() != null) {
            namhatta.setRegularProgramDay(dto.getRegularProgramDay());
        }
        if (dto.getRegularProgramTime() != null) {
            namhatta.setRegularProgramTime(dto.getRegularProgramTime());
        }
        if (dto.getIsApproved() != null) {
            namhatta.setIsApproved(dto.getIsApproved());
        }
        if (dto.getIsActive() != null) {
            namhatta.setIsActive(dto.getIsActive());
        }
        
        // Update relationships
        if (dto.getShraddhakutirId() != null) {
            Shraddhakutir shraddhakutir = new Shraddhakutir();
            shraddhakutir.setId(dto.getShraddhakutirId().intValue());
            namhatta.setShraddhakutir(shraddhakutir);
        }
        
        if (dto.getDistrictSupervisorId() != null) {
            User supervisor = new User();
            supervisor.setId(dto.getDistrictSupervisorId());
            namhatta.setDistrictSupervisor(supervisor);
        }
    }
    
    /**
     * Determine status string for UI display
     */
    private String determineStatus(Namhatta namhatta) {
        if (!namhatta.getIsActive()) {
            return "INACTIVE";
        }
        if (!namhatta.getIsApproved()) {
            return "PENDING_APPROVAL";
        }
        return "APPROVED";
    }
    
    /**
     * Map list of NamhattaAddress entities to NamhattaAddressDto list
     */
    private List<NamhattaAddressDto> mapAddressesToDto(List<NamhattaAddress> addresses) {
        if (addresses == null) {
            return new ArrayList<>();
        }
        
        return addresses.stream()
                .map(this::mapAddressToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Map NamhattaAddress entity to NamhattaAddressDto
     */
    private NamhattaAddressDto mapAddressToDto(NamhattaAddress namhattaAddress) {
        if (namhattaAddress == null) {
            return null;
        }
        
        AddressDto addressDto = null;
        if (namhattaAddress.getAddress() != null) {
            Address addr = namhattaAddress.getAddress();
            addressDto = AddressDto.builder()
                    .id(addr.getId())
                    .country(addr.getCountry())
                    .stateNameEnglish(addr.getStateNameEnglish())
                    .districtNameEnglish(addr.getDistrictNameEnglish())
                    .subdistrictNameEnglish(addr.getSubdistrictNameEnglish())
                    .villageNameEnglish(addr.getVillageNameEnglish())
                    .pincode(addr.getPincode())
                    .build();
        }
        
        return NamhattaAddressDto.builder()
                .id(namhattaAddress.getId())
                .addressType(namhattaAddress.getAddressType())
                .landmark(namhattaAddress.getLandmark())
                .address(addressDto)
                .build();
    }
}