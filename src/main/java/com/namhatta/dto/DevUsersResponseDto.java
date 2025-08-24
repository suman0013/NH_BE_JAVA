package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Development users response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevUsersResponseDto {
    
    private List<DevUserDto> availableUsers;
    private String note;
    private String environment;
}