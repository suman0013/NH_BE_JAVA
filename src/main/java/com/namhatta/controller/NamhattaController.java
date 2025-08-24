package com.namhatta.controller;

import com.namhatta.dto.*;
import com.namhatta.security.UserDetailsServiceImpl;
import com.namhatta.service.NamhattaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * NamhattaController - REST API endpoints for namhatta management
 * Maintains 100% compatibility with Node.js API endpoints
 */
@RestController
@RequestMapping("/api/namhattas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Namhatta Management", description = "REST API for managing namhattas (spiritual centers)")
@SecurityRequirement(name = "JWT")
public class NamhattaController {
    
    private final NamhattaService namhattaService;
    
    /**
     * Get all namhattas with filtering, pagination, and search
     * Equivalent to: GET /api/namhattas
     */
    @GetMapping
    @Operation(summary = "Get all namhattas", 
               description = "Retrieve paginated list of namhattas with optional filtering and search. " +
                           "District supervisors see only namhattas in their assigned districts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Namhattas retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PagedResponse<NamhattaDto>> getAllNamhattas(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Search term for namhatta name")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by country")
            @RequestParam(required = false) String country,
            
            @Parameter(description = "Filter by state")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Filter by district")
            @RequestParam(required = false) String district,
            
            @Parameter(description = "Filter by sub-district")
            @RequestParam(required = false) String subDistrict,
            
            @Parameter(description = "Filter by village")
            @RequestParam(required = false) String village,
            
            @Parameter(description = "Filter by status", example = "PENDING_APPROVAL")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(required = false) String sortBy,
            
            @Parameter(description = "Sort order", example = "asc")
            @RequestParam(required = false) String sortOrder) {
        
        log.debug("GET /api/namhattas - page: {}, size: {}, search: {}", page, size, search);
        
        // Get user context for district filtering
        List<String> allowedDistricts = getUserAllowedDistricts();
        
        PagedResponse<NamhattaDto> response = namhattaService.getAllNamhattas(
                page, size, search, country, state, district, subDistrict, village, 
                status, sortBy, sortOrder, allowedDistricts);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get pending approval namhattas (Admin/Office only)
     * Equivalent to: GET /api/namhattas/pending
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    @Operation(summary = "Get pending approval namhattas", 
               description = "Retrieve namhattas awaiting admin approval. Admin and Office users only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending namhattas retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin or Office role required")
    })
    public ResponseEntity<List<NamhattaDto>> getPendingApprovalNamhattas() {
        log.debug("GET /api/namhattas/pending");
        
        List<NamhattaDto> pendingNamhattas = namhattaService.getPendingApprovalNamhattas();
        return ResponseEntity.ok(pendingNamhattas);
    }
    
    /**
     * Check if namhatta code exists (Admin/Office only)
     * Equivalent to: GET /api/namhattas/check-code/:code
     */
    @GetMapping("/check-code/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    @Operation(summary = "Check namhatta code uniqueness", 
               description = "Check if a namhatta code already exists in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code check completed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Boolean>> checkNamhattaCodeExists(
            @Parameter(description = "Namhatta code to check", required = true)
            @PathVariable String code) {
        
        log.debug("GET /api/namhattas/check-code/{}", code);
        
        boolean exists = namhattaService.checkNamhattaCodeExists(code);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    /**
     * Get namhatta by ID
     * Equivalent to: GET /api/namhattas/:id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get namhatta by ID", 
               description = "Retrieve detailed information about a specific namhatta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Namhatta found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = NamhattaDto.class))),
        @ApiResponse(responseCode = "404", description = "Namhatta not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<NamhattaDto> getNamhattaById(
            @Parameter(description = "Namhatta ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.debug("GET /api/namhattas/{}", id);
        
        List<String> allowedDistricts = getUserAllowedDistricts();
        NamhattaDto namhatta = namhattaService.getNamhattaById(id, allowedDistricts);
        
        return ResponseEntity.ok(namhatta);
    }
    
    /**
     * Create new namhatta (Admin/Office only)
     * Equivalent to: POST /api/namhattas
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    @Operation(summary = "Create new namhatta", 
               description = "Create a new namhatta. Requires admin approval before activation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Namhatta created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = NamhattaDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid namhatta data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "409", description = "Namhatta code already exists")
    })
    public ResponseEntity<NamhattaDto> createNamhatta(
            @Parameter(description = "Namhatta creation data", required = true)
            @Valid @RequestBody CreateNamhattaDto createNamhattaDto) {
        
        log.info("POST /api/namhattas - Creating namhatta: {}", createNamhattaDto.getName());
        
        NamhattaDto createdNamhatta = namhattaService.createNamhatta(createNamhattaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNamhatta);
    }
    
    /**
     * Update existing namhatta (Admin/Office only)
     * Equivalent to: PUT /api/namhattas/:id
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    @Operation(summary = "Update namhatta", 
               description = "Update an existing namhatta's information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Namhatta updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = NamhattaDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid namhatta data"),
        @ApiResponse(responseCode = "404", description = "Namhatta not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<NamhattaDto> updateNamhatta(
            @Parameter(description = "Namhatta ID", required = true, example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "Namhatta update data", required = true)
            @Valid @RequestBody UpdateNamhattaDto updateNamhattaDto) {
        
        log.info("PUT /api/namhattas/{} - Updating namhatta", id);
        
        NamhattaDto updatedNamhatta = namhattaService.updateNamhatta(id, updateNamhattaDto);
        return ResponseEntity.ok(updatedNamhatta);
    }
    
    /**
     * Approve namhatta (Admin only)
     * Equivalent to: POST /api/namhattas/:id/approve
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve namhatta", 
               description = "Approve a pending namhatta for activation. Admin only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Namhatta approved successfully"),
        @ApiResponse(responseCode = "404", description = "Namhatta not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Map<String, String>> approveNamhatta(
            @Parameter(description = "Namhatta ID", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("POST /api/namhattas/{}/approve", id);
        
        namhattaService.approveNamhatta(id);
        return ResponseEntity.ok(Map.of("message", "Namhatta approved successfully"));
    }
    
    /**
     * Reject namhatta (Admin only)
     * Equivalent to: POST /api/namhattas/:id/reject
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject namhatta", 
               description = "Reject a pending namhatta with reason. Admin only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Namhatta rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Namhatta not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Map<String, String>> rejectNamhatta(
            @Parameter(description = "Namhatta ID", required = true, example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "Rejection reason")
            @RequestBody Map<String, String> requestBody) {
        
        log.info("POST /api/namhattas/{}/reject", id);
        
        String reason = requestBody.get("reason");
        namhattaService.rejectNamhatta(id, reason);
        return ResponseEntity.ok(Map.of("message", "Namhatta rejected successfully"));
    }
    
    /**
     * Get devotees by namhatta
     * Equivalent to: GET /api/namhattas/:id/devotees
     */
    @GetMapping("/{id}/devotees")
    @Operation(summary = "Get devotees by namhatta", 
               description = "Retrieve paginated list of devotees associated with a specific namhatta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devotees retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Namhatta not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PagedResponse<DevoteeDto>> getDevoteesByNamhatta(
            @Parameter(description = "Namhatta ID", required = true, example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Filter by devotional status ID")
            @RequestParam(required = false) Long statusId) {
        
        log.debug("GET /api/namhattas/{}/devotees - page: {}, size: {}", id, page, size);
        
        List<String> allowedDistricts = getUserAllowedDistricts();
        PagedResponse<DevoteeDto> response = namhattaService.getDevoteesByNamhatta(
                id, page, size, statusId, allowedDistricts);
        
        return ResponseEntity.ok(response);
    }
    
    // Helper methods
    
    /**
     * Get allowed districts for current user (for district supervisors)
     */
    private List<String> getUserAllowedDistricts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal userPrincipal) {
            // For district supervisors, return their assigned districts
            if ("DISTRICT_SUPERVISOR".equals(userPrincipal.getRole().name())) {
                List<String> districts = userPrincipal.getDistricts();
                if (districts != null && !districts.isEmpty()) {
                    return districts;
                }
                return null; // No districts assigned
            }
        }
        
        // Admin and Office users have no district restrictions
        return null;
    }
}