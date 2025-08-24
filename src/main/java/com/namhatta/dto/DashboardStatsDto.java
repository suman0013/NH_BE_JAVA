package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Dashboard statistics response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    
    private Long totalDevotees;
    private Long totalNamhattas;
    private Long totalStatuses;
    private Long totalUpdates;
    private Map<String, Integer> statusDistribution;
    private Map<String, Integer> namhattaDistribution;
}