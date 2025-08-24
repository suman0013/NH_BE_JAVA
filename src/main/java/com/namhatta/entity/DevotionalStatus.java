package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devotional_statuses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devotees"})
@Slf4j
@Schema(description = "Devotional status levels in spiritual hierarchy")
public class DevotionalStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Status unique identifier", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Status name", example = "Sishya", required = true)
    private String name;
    
    @Column
    @Schema(description = "Status description", example = "Student level in spiritual hierarchy")
    private String description;
    
    @Column(name = "hierarchy_level")
    @Schema(description = "Hierarchical level (higher number = higher status)", example = "1")
    private Integer hierarchyLevel;
    
    @Column(name = "created_at")
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
    
    // Relationship with devotees
    @OneToMany(mappedBy = "devotionalStatus", fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Devotees with this status")
    private List<Devotee> devotees = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new devotional status entity: {}", name);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        log.debug("Updating devotional status entity: {}", name);
        updatedAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded devotional status entity: {} (level: {})", name, hierarchyLevel);
    }
}