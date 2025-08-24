package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Devotional course information for devotees")
public class DevotionalCourse {
    
    @Schema(description = "Course name", example = "Bhagavad Gita Level 1")
    private String courseName;
    
    @Schema(description = "Course completion status", example = "completed")
    private String status; // completed, in-progress, not-started
    
    @Schema(description = "Completion date", example = "2024-12-01")
    private String completionDate;
    
    @Schema(description = "Course score or grade", example = "A")
    private String grade;
    
    @Schema(description = "Additional notes about the course")
    private String notes;
}