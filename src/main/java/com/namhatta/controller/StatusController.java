package com.namhatta.controller;

import com.namhatta.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class StatusController {
    
    private final StatusService statusService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getDevotionalStatuses() {
        log.debug("Getting devotional statuses");
        
        try {
            List<Map<String, Object>> statuses = statusService.getDevotionalStatuses();
            return ResponseEntity.ok(statuses);
            
        } catch (Exception e) {
            log.error("Error retrieving devotional statuses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> createDevotionalStatus(@Valid @RequestBody Map<String, String> request) {
        log.debug("Creating devotional status: {}", request.get("name"));
        
        try {
            String name = request.get("name");
            Map<String, Object> createdStatus = statusService.createDevotionalStatus(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error creating devotional status", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create devotional status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> updateDevotionalStatus(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> request) {
        log.debug("Updating devotional status: {}", id);
        
        try {
            String name = request.get("name");
            Map<String, Object> updatedStatus = statusService.updateDevotionalStatus(id, name);
            return ResponseEntity.ok(updatedStatus);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status update data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error updating devotional status: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update devotional status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDevotionalStatus(@PathVariable Long id) {
        log.debug("Deleting devotional status: {}", id);
        
        try {
            statusService.deleteDevotionalStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Devotional status deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Cannot delete status: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error deleting devotional status: {}", id, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete devotional status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}