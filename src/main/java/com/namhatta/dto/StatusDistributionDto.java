package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Status distribution item DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDistributionDto {
    
    private String status;
    private Integer count;
    private String label;
}