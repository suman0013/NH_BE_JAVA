package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * About information response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutDto {
    
    private String application;
    private String version;
    private String description;
    private String maintainer;
    private Map<String, String> contact;
}