package com.namhatta.controller;

import com.namhatta.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
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
            Map<String, Object> stats = dashboardService.getDashboardStats(allowedDistricts);
            
            log.debug("Dashboard statistics retrieved successfully");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error retrieving dashboard statistics", e);
            
            // Return error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve dashboard statistics");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/status-distribution")
    public ResponseEntity<List<Map<String, Object>>> getStatusDistribution(HttpServletRequest request) {
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
            List<Map<String, Object>> distribution = dashboardService.getStatusDistributionList(allowedDistricts);
            
            log.debug("Status distribution retrieved successfully");
            return ResponseEntity.ok(distribution);
            
        } catch (Exception e) {
            log.error("Error retrieving status distribution", e);
            
            // Return error response
            List<Map<String, Object>> errorResponse = new ArrayList<>();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to retrieve status distribution");
            error.put("message", e.getMessage());
            errorResponse.add(error);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}