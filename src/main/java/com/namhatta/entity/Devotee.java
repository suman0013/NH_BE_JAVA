package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devotees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devotionalStatus", "namhatta", "shraddhakutir"})
@Slf4j
@Schema(description = "Devotee information and spiritual progress")
public class Devotee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Devotee unique identifier", example = "1")
    private Long id;
    
    @Column(name = "legal_name", nullable = false)
    @Schema(description = "Legal name as per official documents", example = "John Doe", required = true)
    private String legalName;
    
    @Column(name = "name")
    @Schema(description = "Initiated/spiritual name", example = "Krishna Das")
    private String name;
    
    @Column(name = "dob")
    @Schema(description = "Date of birth", example = "1990-01-15")
    private String dob;
    
    @Column(name = "email")
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    
    @Column(name = "phone")
    @Schema(description = "Phone number", example = "+91-9876543210")
    private String phone;
    
    // Family information
    @Column(name = "father_name")
    @Schema(description = "Father's name")
    private String fatherName;
    
    @Column(name = "mother_name")
    @Schema(description = "Mother's name")
    private String motherName;
    
    @Column(name = "husband_name")
    @Schema(description = "Husband's name (if applicable)")
    private String husbandName;
    
    @Column(name = "gender")
    @Schema(description = "Gender", example = "Male")
    private String gender;
    
    @Column(name = "blood_group")
    @Schema(description = "Blood group", example = "O+")
    private String bloodGroup;
    
    @Column(name = "marital_status")
    @Schema(description = "Marital status", example = "Single")
    private String maritalStatus;
    
    // Spiritual information
    @Column(name = "initiated_name")
    @Schema(description = "Initiated spiritual name")
    private String initiatedName;
    
    @Column(name = "harinam_date")
    @Schema(description = "Harinam initiation date")
    private String harinamDate;
    
    @Column(name = "pancharatrik_date")
    @Schema(description = "Pancharatrik initiation date")
    private String pancharatrikDate;
    
    // Professional information
    @Column(name = "education")
    @Schema(description = "Educational qualification")
    private String education;
    
    @Column(name = "occupation")
    @Schema(description = "Current occupation")
    private String occupation;
    
    // JSON field for devotional courses
    @Column(name = "devotional_courses", columnDefinition = "jsonb")
    @Convert(converter = DevotionalCoursesConverter.class)
    @Builder.Default
    @Schema(description = "List of devotional courses taken")
    private List<DevotionalCourse> devotionalCourses = new ArrayList<>();
    
    @Column(name = "created_at")
    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devotional_status_id")
    @Schema(description = "Current devotional status")
    private DevotionalStatus devotionalStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namhatta_id")
    @Schema(description = "Associated namhatta")
    private Namhatta namhatta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shraddhakutir_id")
    @Schema(description = "Associated shraddhakutir")
    private Shraddhakutir shraddhakutir;
    
    // Address relationships
    @OneToMany(mappedBy = "devotee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Associated addresses")
    private List<DevoteeAddress> addresses = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new devotee entity: {}", legalName);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        log.debug("Updating devotee entity: {}", legalName);
        updatedAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded devotee entity: {} ({})", legalName, name);
    }
}