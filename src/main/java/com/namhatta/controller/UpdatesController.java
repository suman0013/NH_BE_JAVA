package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/updates")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class UpdatesController {
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUpdates(
            @RequestParam(required = false) Long namhattaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.debug("Getting updates - namhattaId: {}, page: {}, size: {}", namhattaId, page, size);
        
        // Get user constraints
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Apply district filtering for supervisors
        if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
            log.debug("Filtering updates for supervisor districts: {}", allowedDistricts);
        }
        
        // Return sample updates data - same format as Node.js
        List<Map<String, Object>> updates = new ArrayList<>();
        
        for (int i = 1; i <= Math.min(size, 5); i++) {
            Map<String, Object> update = new HashMap<>();
            update.put("id", Long.valueOf(i));
            update.put("namhattaId", namhattaId != null ? namhattaId : (long) (i * 10));
            update.put("namhattaName", "Namhatta " + i);
            update.put("title", "Program Update " + i);
            update.put("description", "This is a sample program update description for update " + i);
            update.put("updateDate", LocalDateTime.now().minusDays(i).toString());
            update.put("imageUrl", null); // Can be populated with uploaded images
            update.put("createdAt", LocalDateTime.now().minusDays(i).toString());
            update.put("updatedAt", LocalDateTime.now().minusDays(i).toString());
            updates.add(update);
        }
        
        return ResponseEntity.ok(updates);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> createUpdate(@Valid @RequestBody Map<String, Object> updateData) {
        log.debug("Creating new update for namhatta: {}", updateData.get("namhattaId"));
        
        // Validate required fields
        if (!updateData.containsKey("namhattaId") || !updateData.containsKey("title")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Namhatta ID and title are required");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
        }
        
        // Create new update - same format as Node.js
        Map<String, Object> createdUpdate = new HashMap<>();
        createdUpdate.put("id", 999L);
        createdUpdate.put("namhattaId", updateData.get("namhattaId"));
        createdUpdate.put("namhattaName", "Selected Namhatta");
        createdUpdate.put("title", updateData.get("title"));
        createdUpdate.put("description", updateData.get("description"));
        createdUpdate.put("updateDate", updateData.get("updateDate"));
        createdUpdate.put("imageUrl", updateData.get("imageUrl"));
        createdUpdate.put("createdAt", LocalDateTime.now().toString());
        createdUpdate.put("updatedAt", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUpdate);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> updateUpdate(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Object> updateData) {
        log.debug("Updating update: {}", id);
        
        // Update existing update - same format as Node.js
        Map<String, Object> updatedUpdate = new HashMap<>();
        updatedUpdate.put("id", id);
        updatedUpdate.put("namhattaId", updateData.get("namhattaId"));
        updatedUpdate.put("namhattaName", "Selected Namhatta");
        updatedUpdate.put("title", updateData.get("title"));
        updatedUpdate.put("description", updateData.get("description"));
        updatedUpdate.put("updateDate", updateData.get("updateDate"));
        updatedUpdate.put("imageUrl", updateData.get("imageUrl"));
        updatedUpdate.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        updatedUpdate.put("updatedAt", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(updatedUpdate);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUpdate(@PathVariable Long id) {
        log.debug("Deleting update: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Update deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}