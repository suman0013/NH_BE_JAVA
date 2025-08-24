package com.namhatta.controller;

import com.namhatta.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        log.debug("Getting users - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Map<String, Object> response = adminService.getUsers(page, size, search);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve users");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody Map<String, Object> userData) {
        log.debug("Creating new user: {}", userData.get("username"));
        
        try {
            Map<String, Object> createdUser = adminService.createUser(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error creating user", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/supervisor-registration")
    public ResponseEntity<Map<String, Object>> registerSupervisor(@Valid @RequestBody Map<String, Object> registrationData) {
        log.debug("Registering new supervisor: {}", registrationData.get("username"));
        
        try {
            Map<String, Object> supervisor = adminService.registerSupervisor(registrationData);
            return ResponseEntity.status(HttpStatus.CREATED).body(supervisor);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid supervisor registration data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error registering supervisor", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to register supervisor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/district-supervisors")
    public ResponseEntity<List<Map<String, Object>>> getDistrictSupervisors(@RequestParam String district) {
        log.debug("Getting district supervisors for district: {}", district);
        
        try {
            List<Map<String, Object>> supervisors = adminService.getDistrictSupervisors(district);
            return ResponseEntity.ok(supervisors);
            
        } catch (Exception e) {
            log.error("Error retrieving district supervisors for district: {}", district, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/user-address-defaults/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAddressDefaults(@PathVariable Long userId) {
        log.debug("Getting address defaults for user: {}", userId);
        
        try {
            Map<String, Object> defaults = adminService.getUserAddressDefaults(userId);
            return ResponseEntity.ok(defaults);
            
        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Error retrieving address defaults for user: {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve address defaults");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}