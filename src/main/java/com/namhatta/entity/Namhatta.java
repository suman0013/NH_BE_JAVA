package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "namhattas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devotees", "shraddhakutir", "districtSupervisor", "addresses"})
@Slf4j
@Schema(description = "Spiritual centers (Namhattas) for community activities")
public class Namhatta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Namhatta unique identifier", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Namhatta name", example = "Sri Krishna Namhatta", required = true)
    private String name;
    
    @Column
    @Schema(description = "Description of the namhatta")
    private String description;
    
    @Column(name = "contact_person")
    @Schema(description = "Primary contact person")
    private String contactPerson;
    
    @Column(name = "contact_phone")
    @Schema(description = "Contact phone number")
    private String contactPhone;
    
    @Column(name = "contact_email")
    @Schema(description = "Contact email address")
    private String contactEmail;
    
    @Column(name = "established_date")
    @Schema(description = "Date when namhatta was established")
    private String establishedDate;
    
    @Column(name = "regular_program_day")
    @Schema(description = "Day of week for regular programs", example = "Sunday")
    private String regularProgramDay;
    
    @Column(name = "regular_program_time")
    @Schema(description = "Time for regular programs", example = "10:00 AM")
    private String regularProgramTime;
    
    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the namhatta is approved", example = "true")
    private Boolean isApproved = false;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the namhatta is active", example = "true")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shraddhakutir_id")
    @Schema(description = "Associated shraddhakutir")
    private Shraddhakutir shraddhakutir;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_supervisor_id")
    @Schema(description = "District supervisor overseeing this namhatta")
    private User districtSupervisor;
    
    @OneToMany(mappedBy = "namhatta", fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Devotees attending this namhatta")
    private List<Devotee> devotees = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new namhatta entity: {}", name);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        log.debug("Updating namhatta entity: {}", name);
        updatedAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded namhatta entity: {} (approved: {}, active: {})", name, isApproved, isActive);
    }
}