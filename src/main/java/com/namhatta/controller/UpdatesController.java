package com.namhatta.controller;

import com.namhatta.service.UpdatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/updates")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class UpdatesController {
    
    private final UpdatesService updatesService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUpdates(
            @RequestParam(required = false) Long namhattaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.debug("Getting updates - namhattaId: {}, page: {}, size: {}", namhattaId, page, size);
        
        try {
            // Get user constraints
            String userRole = (String) request.getAttribute("userRole");
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            
            // Apply district filtering for supervisors
            if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
                log.debug("Filtering updates for supervisor districts: {}", allowedDistricts);
            }
            
            List<Map<String, Object>> updates = updatesService.getUpdates(namhattaId, page, size, allowedDistricts);
            return ResponseEntity.ok(updates);
            
        } catch (Exception e) {
            log.error("Error retrieving updates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> createUpdate(@Valid @RequestBody Map<String, Object> updateData) {
        log.debug("Creating new update for namhatta: {}", updateData.get("namhattaId"));
        
        try {
            Map<String, Object> createdUpdate = updatesService.createUpdate(updateData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUpdate);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid update data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error creating update", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> updateUpdate(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Object> updateData) {
        log.debug("Updating update: {}", id);
        
        try {
            Map<String, Object> updatedUpdate = updatesService.updateUpdate(id, updateData);
            return ResponseEntity.ok(updatedUpdate);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid update data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error updating update: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUpdate(@PathVariable Long id) {
        log.debug("Deleting update: {}", id);
        
        try {
            updatesService.deleteUpdate(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Update deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting update: {}", id, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}