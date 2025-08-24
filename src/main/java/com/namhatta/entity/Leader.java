package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "leaders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Schema(description = "Spiritual leadership hierarchy")
public class Leader {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Leader unique identifier", example = "1")
    private Integer id;
    
    @Column(nullable = false)
    @Schema(description = "Leader name", example = "His Divine Grace", required = true)
    private String name;
    
    @Column
    @Schema(description = "Leader title", example = "Acharya")
    private String title;
    
    @Column
    @Schema(description = "Leader description and qualifications")
    private String description;
    
    @Column(name = "hierarchy_level")
    @Schema(description = "Level in spiritual hierarchy", example = "1")
    private Integer hierarchyLevel;
    
    @Column(name = "parent_leader_id")
    @Schema(description = "ID of parent leader in hierarchy")
    private Integer parentLeaderId;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the leader is currently active", example = "true")
    private Boolean isActive = true;
    
    @Column(name = "contact_info")
    @Schema(description = "Contact information")
    private String contactInfo;
    
    @Column(name = "region")
    @Schema(description = "Geographic region of responsibility")
    private String region;
    
    @Column(name = "created_at")
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new leader entity: {}", name);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        log.debug("Updating leader entity: {}", name);
        updatedAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded leader entity: {} - {} (level: {})", name, title, hierarchyLevel);
    }
}