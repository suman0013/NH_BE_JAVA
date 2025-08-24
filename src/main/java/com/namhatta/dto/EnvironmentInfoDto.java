package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Environment information response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentInfoDto {
    
    private String profile;
    private String javaVersion;
    private String springVersion;
    private String timestamp;
}