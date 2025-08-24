package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Geographic data response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeographicResponseDto {
    
    private String type;
    private String name;
    private String code;
    private Integer count;
    private Double latitude;
    private Double longitude;
}