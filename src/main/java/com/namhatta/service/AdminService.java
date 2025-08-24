package com.namhatta.service;

import com.namhatta.entity.User;
import com.namhatta.entity.UserRole;
import com.namhatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AdminService - Business logic for user administration
 * Replaces hardcoded user management with real database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Get users with pagination and search
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUsers(int page, int size, String search) {
        log.debug("Getting users - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            // Create pageable with sorting by creation date
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            
            Page<User> userPage;
            if (search != null && !search.trim().isEmpty()) {
                userPage = userRepository.findByUsernameContainingIgnoreCase(search.trim(), pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }
            
            // Convert to response format
            List<Map<String, Object>> users = userPage.getContent().stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", users);
            response.put("total", userPage.getTotalElements());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", userPage.getTotalPages());
            
            log.debug("Retrieved {} users successfully", users.size());
            return response;
            
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Create new user
     */
    public Map<String, Object> createUser(Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String roleStr = (String) userData.get("role");
        
        log.debug("Creating new user: {}", username);
        
        try {
            // Validate required fields
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (roleStr == null || roleStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Role is required");
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists");
            }
            
            // Parse role
            UserRole role = UserRole.valueOf(roleStr.toUpperCase());
            
            // Create new user
            User user = User.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            User savedUser = userRepository.save(user);
            
            log.info("Successfully created user: {} with role: {}", username, role);
            return convertUserToMap(savedUser);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating user: {}", username, e);
            throw new RuntimeException("Failed to create user", e);
        }
    }
    
    /**
     * Register district supervisor with district assignments
     */
    public Map<String, Object> registerSupervisor(Map<String, Object> registrationData) {
        String username = (String) registrationData.get("username");
        String password = (String) registrationData.get("password");
        List<String> districts = (List<String>) registrationData.get("districts");
        
        log.debug("Registering new supervisor: {}", username);
        
        try {
            // Validate required fields
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (districts == null || districts.isEmpty()) {
                throw new IllegalArgumentException("Districts are required for supervisor");
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists");
            }
            
            // Create new supervisor
            User supervisor = User.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(UserRole.DISTRICT_SUPERVISOR)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            // TODO: Implement district assignments when UserDistrict entity is created
            // For now, we'll store districts as a comment or separate logic
            
            User savedSupervisor = userRepository.save(supervisor);
            
            Map<String, Object> result = convertUserToMap(savedSupervisor);
            result.put("districts", districts); // Add districts to response
            
            log.info("Successfully registered supervisor: {} for districts: {}", username, districts);
            return result;
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid supervisor registration data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error registering supervisor: {}", username, e);
            throw new RuntimeException("Failed to register supervisor", e);
        }
    }
    
    /**
     * Get district supervisors for a specific district
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDistrictSupervisors(String district) {
        log.debug("Getting district supervisors for district: {}", district);
        
        try {
            List<User> supervisors = userRepository.findByRoleAndIsActive(UserRole.DISTRICT_SUPERVISOR, true);
            
            // TODO: Filter by actual district assignments when UserDistrict relationship is implemented
            // For now, return all supervisors with the district info
            List<Map<String, Object>> result = supervisors.stream()
                .map(supervisor -> {
                    Map<String, Object> supervisorMap = convertUserToMap(supervisor);
                    // Add district info (placeholder until proper relationship is implemented)
                    supervisorMap.put("districts", List.of(Map.of("code", district, "name", district)));
                    return supervisorMap;
                })
                .collect(Collectors.toList());
            
            log.debug("Found {} supervisors for district: {}", result.size(), district);
            return result;
            
        } catch (Exception e) {
            log.error("Error retrieving supervisors for district: {}", district, e);
            throw new RuntimeException("Failed to retrieve district supervisors", e);
        }
    }
    
    /**
     * Get user address defaults (placeholder implementation)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserAddressDefaults(Long userId) {
        log.debug("Getting address defaults for user: {}", userId);
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }
            
            // TODO: Implement proper address defaults based on user's profile/district
            // For now, return default values
            Map<String, Object> defaults = new HashMap<>();
            defaults.put("country", "India");
            defaults.put("state", "West Bengal");
            defaults.put("district", "Kolkata");
            
            log.debug("Address defaults retrieved for user: {}", userId);
            return defaults;
            
        } catch (Exception e) {
            log.error("Error retrieving address defaults for user: {}", userId, e);
            throw new RuntimeException("Failed to retrieve address defaults", e);
        }
    }
    
    /**
     * Convert User entity to Map for API response
     */
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("role", user.getRole().name());
        userMap.put("isActive", user.getIsActive());
        userMap.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        userMap.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        
        // TODO: Add districts when UserDistrict relationship is implemented
        userMap.put("districts", List.of()); // Empty for now
        
        return userMap;
    }
}