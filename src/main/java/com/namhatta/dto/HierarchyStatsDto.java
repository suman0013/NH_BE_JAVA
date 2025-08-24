package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hierarchy statistics response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HierarchyStatsDto {
    
    private Long totalLevels;
    private Long totalLeaders;
    private Long totalRelationships;
}