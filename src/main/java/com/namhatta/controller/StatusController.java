package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class StatusController {
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getDevotionalStatuses() {
        log.debug("Getting devotional statuses");
        
        // Return sample devotional statuses - same format as Node.js
        List<Map<String, Object>> statuses = new ArrayList<>();
        
        Map<String, Object> regular = new HashMap<>();
        regular.put("id", 1);
        regular.put("name", "Regular Devotee");
        regular.put("createdAt", LocalDateTime.now().toString());
        statuses.add(regular);
        
        Map<String, Object> aspiring = new HashMap<>();
        aspiring.put("id", 2);
        aspiring.put("name", "Aspiring Devotee");
        aspiring.put("createdAt", LocalDateTime.now().toString());
        statuses.add(aspiring);
        
        Map<String, Object> initiated = new HashMap<>();
        initiated.put("id", 3);
        initiated.put("name", "Initiated Devotee");
        initiated.put("createdAt", LocalDateTime.now().toString());
        statuses.add(initiated);
        
        return ResponseEntity.ok(statuses);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> createDevotionalStatus(@Valid @RequestBody Map<String, String> request) {
        log.debug("Creating devotional status: {}", request.get("name"));
        
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Status name is required");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
        }
        
        // Return created status - same format as Node.js
        Map<String, Object> createdStatus = new HashMap<>();
        createdStatus.put("id", 4);
        createdStatus.put("name", name);
        createdStatus.put("createdAt", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<Map<String, Object>> updateDevotionalStatus(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> request) {
        log.debug("Updating devotional status: {}", id);
        
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Status name is required");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
        }
        
        // Return updated status - same format as Node.js
        Map<String, Object> updatedStatus = new HashMap<>();
        updatedStatus.put("id", id);
        updatedStatus.put("name", name);
        updatedStatus.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        updatedStatus.put("updatedAt", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(updatedStatus);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDevotionalStatus(@PathVariable Long id) {
        log.debug("Deleting devotional status: {}", id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Devotional status deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}