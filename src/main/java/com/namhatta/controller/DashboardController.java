package com.namhatta.controller;

import com.namhatta.dto.DashboardStatsDto;
import com.namhatta.dto.ErrorResponse;
import com.namhatta.dto.StatusDistributionDto;
import com.namhatta.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(HttpServletRequest request) {
        log.debug("Getting dashboard statistics");
        
        try {
            // Get user role and districts from JWT token attributes
            String userRole = (String) request.getAttribute("userRole");
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            
            // Apply district filtering for supervisors
            if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
                log.debug("Filtering dashboard stats for supervisor districts: {}", allowedDistricts);
            }
            
            // Get real dashboard statistics from service
            DashboardStatsDto stats = dashboardService.getDashboardStats(allowedDistricts);
            
            log.debug("Dashboard statistics retrieved successfully");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error retrieving dashboard statistics", e);
            
            // Return error response
            return ResponseEntity.status(500).body(
                ErrorResponse.builder()
                    .error("Failed to retrieve dashboard statistics")
                    .message(e.getMessage())
                    .build()
            );
        }
    }
    
    @GetMapping("/status-distribution")
    public ResponseEntity<?> getStatusDistribution(HttpServletRequest request) {
        log.debug("Getting status distribution");
        
        try {
            // Get user constraints
            String userRole = (String) request.getAttribute("userRole");
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            
            // Apply district filtering for supervisors
            if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
                log.debug("Filtering status distribution for supervisor districts: {}", allowedDistricts);
            }
            
            // Get real status distribution data from service
            List<StatusDistributionDto> distribution = dashboardService.getStatusDistributionList(allowedDistricts);
            
            log.debug("Status distribution retrieved successfully");
            return ResponseEntity.ok(distribution);
            
        } catch (Exception e) {
            log.error("Error retrieving status distribution", e);
            
            // Return error response
            return ResponseEntity.status(500).body(
                ErrorResponse.builder()
                    .error("Failed to retrieve status distribution")
                    .message(e.getMessage())
                    .build()
            );
        }
    }
}