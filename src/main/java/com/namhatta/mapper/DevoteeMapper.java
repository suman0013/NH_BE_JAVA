package com.namhatta.mapper;

import com.namhatta.dto.*;
import com.namhatta.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DevoteeMapper for converting between Devotee entities and DTOs
 * Maintains 100% compatibility with Node.js data transformation logic
 */
@Component
public class DevoteeMapper {
    
    /**
     * Convert Devotee entity to DevoteeDto
     */
    public DevoteeDto toDto(Devotee devotee) {
        if (devotee == null) {
            return null;
        }
        
        return DevoteeDto.builder()
                .id(devotee.getId())
                .legalName(devotee.getLegalName())
                .name(devotee.getName())
                .dob(devotee.getDob())
                .email(devotee.getEmail())
                .phone(devotee.getPhone())
                .gender(devotee.getGender())
                .alternatePhone(null) // Not available in entity
                .relationshipStatus(devotee.getMaritalStatus())
                .spouseName(devotee.getHusbandName())
                .fatherName(devotee.getFatherName())
                .motherName(devotee.getMotherName())
                .initiatedName(devotee.getInitiatedName())
                .harinamDate(devotee.getHarinamDate())
                .pancharatrikDate(devotee.getPancharatrikDate())
                .education(devotee.getEducation())
                .occupation(devotee.getOccupation())
                .devotionalCourses(mapDevotionalCourses(devotee.getDevotionalCourses()))
                .remarks(null) // Not available in entity
                .namhattaId(devotee.getNamhatta() != null ? devotee.getNamhatta().getId() : null)
                .namhattaName(devotee.getNamhatta() != null ? devotee.getNamhatta().getName() : null)
                .devotionalStatusId(devotee.getDevotionalStatus() != null ? devotee.getDevotionalStatus().getId() : null)
                .devotionalStatusName(devotee.getDevotionalStatus() != null ? devotee.getDevotionalStatus().getName() : null)
                .addresses(mapAddressesToDto(devotee.getAddresses()))
                .createdAt(devotee.getCreatedAt())
                .updatedAt(devotee.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert CreateDevoteeDto to Devotee entity
     */
    public Devotee toEntity(CreateDevoteeDto dto) {
        if (dto == null) {
            return null;
        }
        
        Devotee devotee = new Devotee();
        devotee.setLegalName(dto.getLegalName());
        devotee.setName(dto.getName());
        devotee.setDob(dto.getDob());
        devotee.setEmail(dto.getEmail());
        devotee.setPhone(dto.getPhone());
        devotee.setGender(dto.getGender());
        // Map DTO fields to entity fields
        devotee.setMaritalStatus(dto.getRelationshipStatus());
        devotee.setHusbandName(dto.getSpouseName());
        devotee.setFatherName(dto.getFatherName());
        devotee.setMotherName(dto.getMotherName());
        devotee.setInitiatedName(dto.getInitiatedName());
        devotee.setHarinamDate(dto.getHarinamDate());
        devotee.setPancharatrikDate(dto.getPancharatrikDate());
        devotee.setEducation(dto.getEducation());
        devotee.setOccupation(dto.getOccupation());
        devotee.setDevotionalCourses(mapDevotionalCoursesFromDto(dto.getDevotionalCourses()));
        
        // Set namhatta and devotional status (these will be resolved by service layer)
        if (dto.getNamhattaId() != null) {
            Namhatta namhatta = new Namhatta();
            namhatta.setId(dto.getNamhattaId());
            devotee.setNamhatta(namhatta);
        }
        
        if (dto.getDevotionalStatusId() != null) {
            DevotionalStatus status = new DevotionalStatus();
            status.setId(dto.getDevotionalStatusId());
            devotee.setDevotionalStatus(status);
        }
        
        // Handle addresses
        if (dto.getPresentAddress() != null || dto.getPermanentAddress() != null) {
            List<DevoteeAddress> addresses = new ArrayList<>();
            
            if (dto.getPresentAddress() != null) {
                DevoteeAddress presentAddr = mapCreateAddressToEntity(dto.getPresentAddress(), "present");
                presentAddr.setDevotee(devotee);
                addresses.add(presentAddr);
            }
            
            if (dto.getPermanentAddress() != null) {
                DevoteeAddress permanentAddr = mapCreateAddressToEntity(dto.getPermanentAddress(), "permanent");
                permanentAddr.setDevotee(devotee);
                addresses.add(permanentAddr);
            }
            
            // Set addresses to entity - relationship is properly defined
            devotee.setAddresses(addresses);
        }
        
        return devotee;
    }
    
    /**
     * Update existing Devotee entity with UpdateDevoteeDto data
     */
    public void updateEntityFromDto(UpdateDevoteeDto dto, Devotee devotee) {
        if (dto == null || devotee == null) {
            return;
        }
        
        // Update only non-null fields from DTO
        if (dto.getLegalName() != null) {
            devotee.setLegalName(dto.getLegalName());
        }
        if (dto.getName() != null) {
            devotee.setName(dto.getName());
        }
        if (dto.getDob() != null) {
            devotee.setDob(dto.getDob());
        }
        if (dto.getEmail() != null) {
            devotee.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            devotee.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            devotee.setGender(dto.getGender());
        }
        if (dto.getRelationshipStatus() != null) {
            devotee.setMaritalStatus(dto.getRelationshipStatus());
        }
        if (dto.getSpouseName() != null) {
            devotee.setHusbandName(dto.getSpouseName());
        }
        if (dto.getFatherName() != null) {
            devotee.setFatherName(dto.getFatherName());
        }
        if (dto.getMotherName() != null) {
            devotee.setMotherName(dto.getMotherName());
        }
        if (dto.getInitiatedName() != null) {
            devotee.setInitiatedName(dto.getInitiatedName());
        }
        if (dto.getHarinamDate() != null) {
            devotee.setHarinamDate(dto.getHarinamDate());
        }
        if (dto.getPancharatrikDate() != null) {
            devotee.setPancharatrikDate(dto.getPancharatrikDate());
        }
        if (dto.getEducation() != null) {
            devotee.setEducation(dto.getEducation());
        }
        if (dto.getOccupation() != null) {
            devotee.setOccupation(dto.getOccupation());
        }
        if (dto.getDevotionalCourses() != null) {
            devotee.setDevotionalCourses(mapDevotionalCoursesFromDto(dto.getDevotionalCourses()));
        }
        
        // Update namhatta and devotional status
        if (dto.getNamhattaId() != null) {
            Namhatta namhatta = new Namhatta();
            namhatta.setId(dto.getNamhattaId());
            devotee.setNamhatta(namhatta);
        }
        
        if (dto.getDevotionalStatusId() != null) {
            DevotionalStatus status = new DevotionalStatus();
            status.setId(dto.getDevotionalStatusId());
            devotee.setDevotionalStatus(status);
        }
        
        // Handle address updates (this would need more complex logic in a real implementation)
        // For now, we'll keep the existing addresses
    }
    
    /**
     * Map list of DevotionalCourse strings to DevotionalCourseDto list
     */
    private List<DevotionalCourseDto> mapDevotionalCourses(List<DevotionalCourse> devotionalCourses) {
        if (devotionalCourses == null || devotionalCourses.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Convert DevotionalCourse entities to DTOs
        return devotionalCourses.stream()
                .map(course -> DevotionalCourseDto.builder()
                        .courseName(course.getCourseName())
                        .status(course.getStatus())
                        .completionDate(course.getCompletionDate())
                        .remarks(course.getNotes()) // Map notes to remarks
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Map DevotionalCourseDto list to string for database storage
     */
    private List<DevotionalCourse> mapDevotionalCoursesFromDto(List<DevotionalCourseDto> courses) {
        if (courses == null || courses.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Convert DTOs to DevotionalCourse entities
        return courses.stream()
                .map(dto -> DevotionalCourse.builder()
                        .courseName(dto.getCourseName())
                        .status(dto.getStatus())
                        .completionDate(dto.getCompletionDate())
                        .notes(dto.getRemarks()) // Map remarks to notes
                        .grade(null) // Grade not available in DTO
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Map list of DevoteeAddress entities to DevoteeAddressDto list
     */
    private List<DevoteeAddressDto> mapAddressesToDto(List<DevoteeAddress> addresses) {
        if (addresses == null) {
            return new ArrayList<>();
        }
        
        return addresses.stream()
                .map(this::mapAddressToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Map DevoteeAddress entity to DevoteeAddressDto
     */
    private DevoteeAddressDto mapAddressToDto(DevoteeAddress devoteeAddress) {
        if (devoteeAddress == null) {
            return null;
        }
        
        AddressDto addressDto = null;
        if (devoteeAddress.getAddress() != null) {
            Address addr = devoteeAddress.getAddress();
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
        
        return DevoteeAddressDto.builder()
                .id(devoteeAddress.getId())
                .addressType(devoteeAddress.getAddressType())
                .landmark(devoteeAddress.getLandmark())
                .address(addressDto)
                .build();
    }
    
    /**
     * Map CreateAddressDto to DevoteeAddress entity
     */
    private DevoteeAddress mapCreateAddressToEntity(CreateAddressDto dto, String addressType) {
        if (dto == null) {
            return null;
        }
        
        // Create Address entity
        Address address = new Address();
        address.setCountry(dto.getCountry());
        address.setStateNameEnglish(dto.getStateNameEnglish());
        address.setDistrictNameEnglish(dto.getDistrictNameEnglish());
        address.setSubdistrictNameEnglish(dto.getSubdistrictNameEnglish());
        address.setVillageNameEnglish(dto.getVillageNameEnglish());
        address.setPincode(dto.getPincode());
        
        // Create DevoteeAddress entity
        DevoteeAddress devoteeAddress = new DevoteeAddress();
        devoteeAddress.setAddress(address);
        devoteeAddress.setAddressType(addressType);
        devoteeAddress.setLandmark(dto.getLandmark());
        
        return devoteeAddress;
    }
}