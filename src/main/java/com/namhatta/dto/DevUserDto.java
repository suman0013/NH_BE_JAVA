package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Development user information DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevUserDto {
    
    private String username;
    private String role;
    private String description;
}