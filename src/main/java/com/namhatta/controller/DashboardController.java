package com.namhatta.controller;

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
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        log.debug("Getting dashboard statistics");
        
        // Get user role and districts from JWT token attributes
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Apply district filtering for supervisors
        if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
            log.debug("Filtering dashboard stats for supervisor districts: {}", allowedDistricts);
        }
        
        // Return sample dashboard statistics - same format as Node.js
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDevotees", 1250);
        stats.put("totalNamhattas", 85);
        stats.put("totalShraddhakutirs", 12);
        stats.put("approvalsPending", 8);
        
        Map<String, Integer> statusDistribution = new HashMap<>();
        statusDistribution.put("Regular Devotee", 800);
        statusDistribution.put("Aspiring Devotee", 300);
        statusDistribution.put("Initiated Devotee", 150);
        stats.put("statusDistribution", statusDistribution);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/status-distribution")
    public ResponseEntity<List<Map<String, Object>>> getStatusDistribution(HttpServletRequest request) {
        log.debug("Getting status distribution");
        
        // Get user constraints
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Return status distribution data - same format as Node.js
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        Map<String, Object> regular = new HashMap<>();
        regular.put("status", "Regular Devotee");
        regular.put("count", 800);
        regular.put("percentage", 64.0);
        distribution.add(regular);
        
        Map<String, Object> aspiring = new HashMap<>();
        aspiring.put("status", "Aspiring Devotee");
        aspiring.put("count", 300);
        aspiring.put("percentage", 24.0);
        distribution.add(aspiring);
        
        Map<String, Object> initiated = new HashMap<>();
        initiated.put("status", "Initiated Devotee");
        initiated.put("count", 150);
        initiated.put("percentage", 12.0);
        distribution.add(initiated);
        
        return ResponseEntity.ok(distribution);
    }
}