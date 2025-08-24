package com.namhatta.controller;

import com.namhatta.dto.CreateDevoteeDto;
import com.namhatta.dto.DevoteeDto;
import com.namhatta.dto.UpdateDevoteeDto;
import com.namhatta.service.DevoteeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DevoteeController implementing all /api/devotees/* endpoints
 * Maintains 100% API compatibility with the Node.js Express implementation
 */
@RestController
@RequestMapping("/api/devotees")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000", "https://*.replit.app", "https://*.replit.dev"}, allowCredentials = "true")
@Validated
@RequiredArgsConstructor
@Slf4j
public class DevoteeController {
    
    private final DevoteeService devoteeService;
    
    /**
     * Get all devotees with filtering, sorting, and pagination
     * Same as Node.js GET /api/devotees
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDevotees(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            HttpServletRequest request) {
        
        log.info("Getting devotees - page: {}, size: {}, status: {}, search: {}, sortBy: {}, sortOrder: {}",
                page, size, status, search, sortBy, sortOrder);
        
        try {
            // Get user districts from JWT token (set by JwtAuthenticationFilter)
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            String userRole = (String) request.getAttribute("userRole");
            
            log.debug("User role: {}, Districts: {}", userRole, allowedDistricts);
            
            // Apply district filtering only for supervisors
            if ("DISTRICT_SUPERVISOR".equals(userRole)) {
                // District filtering will be applied in service layer
                log.debug("Applying district filtering for supervisor");
            } else {
                allowedDistricts = null; // Admin and Office can see all
                log.debug("Admin/Office user - no district filtering");
            }
            
            Page<DevoteeDto> devotees = devoteeService.getFilteredDevotees(
                allowedDistricts, status, search, sortBy, sortOrder, page, size);
            
            // Build response in same format as Node.js API
            Map<String, Object> response = new HashMap<>();
            response.put("data", devotees.getContent());
            response.put("total", devotees.getTotalElements());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", devotees.getTotalPages());
            response.put("hasNext", devotees.hasNext());
            response.put("hasPrevious", devotees.hasPrevious());
            
            log.info("Successfully retrieved {} devotees (total: {})", 
                    devotees.getContent().size(), devotees.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving devotees", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve devotees");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get devotee by ID
     * Same as Node.js GET /api/devotees/:id
     */
    @GetMapping("/{id}")
    public ResponseEntity<DevoteeDto> getDevotee(@PathVariable Long id, HttpServletRequest request) {
        
        log.info("Getting devotee by ID: {}", id);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            DevoteeDto devotee = devoteeService.getDevoteeById(id, allowedDistricts);
            
            if (devotee == null) {
                log.warn("Devotee not found or access denied for ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            log.info("Successfully retrieved devotee: {} ({})", devotee.getLegalName(), id);
            return ResponseEntity.ok(devotee);
            
        } catch (Exception e) {
            log.error("Error retrieving devotee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new devotee
     * Same as Node.js POST /api/devotees
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<DevoteeDto> createDevotee(@Valid @RequestBody CreateDevoteeDto dto) {
        
        log.info("Creating new devotee: {}", dto.getLegalName());
        
        try {
            DevoteeDto createdDevotee = devoteeService.createDevotee(dto);
            
            log.info("Successfully created devotee: {} (ID: {})", 
                    createdDevotee.getLegalName(), createdDevotee.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevotee);
            
        } catch (Exception e) {
            log.error("Error creating devotee: {}", dto.getLegalName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update devotee
     * Same as Node.js PUT /api/devotees/:id
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<DevoteeDto> updateDevotee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDevoteeDto dto,
            HttpServletRequest request) {
        
        log.info("Updating devotee ID: {}", id);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            DevoteeDto updatedDevotee = devoteeService.updateDevotee(id, dto, allowedDistricts);
            
            if (updatedDevotee == null) {
                log.warn("Devotee not found or access denied for update: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            log.info("Successfully updated devotee: {} (ID: {})", 
                    updatedDevotee.getLegalName(), id);
            
            return ResponseEntity.ok(updatedDevotee);
            
        } catch (Exception e) {
            log.error("Error updating devotee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete devotee
     * Same as Node.js DELETE /api/devotees/:id
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDevotee(@PathVariable Long id) {
        
        log.info("Deleting devotee ID: {}", id);
        
        try {
            boolean deleted = devoteeService.deleteDevotee(id);
            
            if (!deleted) {
                log.warn("Devotee not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Devotee deleted successfully");
            response.put("devoteeId", id.toString());
            
            log.info("Successfully deleted devotee ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting devotee with ID: {}", id, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete devotee");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get devotees by namhatta
     * Same as Node.js GET /api/devotees/namhatta/:namhattaId
     */
    @GetMapping("/namhatta/{namhattaId}")
    public ResponseEntity<List<DevoteeDto>> getDevoteesByNamhatta(
            @PathVariable Long namhattaId, 
            HttpServletRequest request) {
        
        log.info("Getting devotees for namhatta ID: {}", namhattaId);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<DevoteeDto> devotees = devoteeService.getDevoteesByNamhatta(namhattaId, allowedDistricts);
            
            log.info("Retrieved {} devotees for namhatta ID: {}", devotees.size(), namhattaId);
            return ResponseEntity.ok(devotees);
            
        } catch (Exception e) {
            log.error("Error retrieving devotees for namhatta ID: {}", namhattaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get allowed districts based on user role
     */
    private List<String> getAllowedDistricts(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            List<String> districtCodes = (List<String>) request.getAttribute("userDistricts");
            // In a real implementation, we would convert district codes to district names
            // For now, return the district codes as-is
            return districtCodes;
        }
        
        return null; // Admin and Office can access all
    }
}