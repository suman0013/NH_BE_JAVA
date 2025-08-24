package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shraddhakutirs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devotees", "namhattas"})
@Slf4j
@Schema(description = "Regional spiritual administrative units")
public class Shraddhakutir {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Shraddhakutir unique identifier", example = "1")
    private Integer id;
    
    @Column(nullable = false)
    @Schema(description = "Shraddhakutir name", example = "Kolkata Shraddhakutir", required = true)
    private String name;
    
    @Column
    @Schema(description = "Description of the shraddhakutir", example = "Covers West Bengal region")
    private String description;
    
    @Column
    @Schema(description = "Country", example = "India")
    private String country;
    
    @Column
    @Schema(description = "State", example = "West Bengal")
    private String state;
    
    @Column
    @Schema(description = "District", example = "Kolkata")
    private String district;
    
    @Column
    @Schema(description = "Administrative contact person")
    private String contactPerson;
    
    @Column
    @Schema(description = "Contact phone number")
    private String contactPhone;
    
    @Column
    @Schema(description = "Contact email address")
    private String contactEmail;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the shraddhakutir is active", example = "true")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "shraddhakutir", fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Devotees under this shraddhakutir")
    private List<Devotee> devotees = new ArrayList<>();
    
    @OneToMany(mappedBy = "shraddhakutir", fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Namhattas under this shraddhakutir")
    private List<Namhatta> namhattas = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new shraddhakutir entity: {}", name);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        log.debug("Updating shraddhakutir entity: {}", name);
        updatedAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded shraddhakutir entity: {} in {}, {}", name, district, state);
    }
}