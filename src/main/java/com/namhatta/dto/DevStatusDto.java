package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Development status response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevStatusDto {
    
    private String status;
    private String message;
    private String environment;
    private String timestamp;
}