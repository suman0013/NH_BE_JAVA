package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Health check response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckDto {
    
    private String status;
    private String version;
    private String uptime;
    private String timestamp;
    private Map<String, String> checks;
}