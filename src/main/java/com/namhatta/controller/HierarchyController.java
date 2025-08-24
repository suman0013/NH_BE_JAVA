package com.namhatta.controller;

import com.namhatta.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/hierarchy")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class HierarchyController {
    
    private final HierarchyService hierarchyService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTopLevelHierarchy(HttpServletRequest request) {
        log.debug("Getting top level hierarchy");
        
        try {
            // Get user constraints
            String userRole = (String) request.getAttribute("userRole");
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            
            List<Map<String, Object>> hierarchy = hierarchyService.getTopLevelHierarchy(userRole, allowedDistricts);
            return ResponseEntity.ok(hierarchy);
            
        } catch (Exception e) {
            log.error("Error retrieving top level hierarchy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/{level}")
    public ResponseEntity<List<Map<String, Object>>> getLeadersByLevel(
            @PathVariable String level,
            HttpServletRequest request) {
        log.debug("Getting leaders by level: {}", level);
        
        try {
            // Get user constraints
            String userRole = (String) request.getAttribute("userRole");
            List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
            
            List<Map<String, Object>> leaders = hierarchyService.getLeadersByLevel(level, userRole, allowedDistricts);
            return ResponseEntity.ok(leaders);
            
        } catch (Exception e) {
            log.error("Error retrieving leaders for level: {}", level, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}