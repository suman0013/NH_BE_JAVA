package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Map statistics response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapStatsDto {
    
    private Long totalCountries;
    private Long totalStates;
    private Long totalDistricts;
    private Long totalNamhattas;
}