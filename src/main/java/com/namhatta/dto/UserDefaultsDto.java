package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User address defaults response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDefaultsDto {
    
    private String country;
    private String state;
    private String district;
    private String subDistrict;
    private String village;
    private String pincode;
}