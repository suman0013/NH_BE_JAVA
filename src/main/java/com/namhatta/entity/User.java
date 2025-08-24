package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"passwordHash"})
@Slf4j
@Schema(description = "User entity for authentication and authorization")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User unique identifier", example = "1")
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Schema(description = "Username for login", example = "admin", required = true)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    @Schema(description = "Hashed password", hidden = true)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Schema(description = "User role", example = "ADMIN", allowableValues = {"ADMIN", "OFFICE", "DISTRICT_SUPERVISOR"})
    private UserRole role;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether user account is active", example = "true")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new user entity: {}", username);
        createdAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded user entity: {} with role: {}", username, role);
    }
}