package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTopLevelHierarchy(HttpServletRequest request) {
        log.debug("Getting top level hierarchy");
        
        // Get user constraints
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Return top level hierarchy - same format as Node.js
        List<Map<String, Object>> hierarchy = new ArrayList<>();
        
        Map<String, Object> president = new HashMap<>();
        president.put("id", 1);
        president.put("name", "President");
        president.put("level", "PRESIDENT");
        president.put("count", 1);
        hierarchy.add(president);
        
        Map<String, Object> vicePresident = new HashMap<>();
        vicePresident.put("id", 2);
        vicePresident.put("name", "Vice President");
        vicePresident.put("level", "VICE_PRESIDENT");
        vicePresident.put("count", 2);
        hierarchy.add(vicePresident);
        
        Map<String, Object> secretary = new HashMap<>();
        secretary.put("id", 3);
        secretary.put("name", "Secretary");
        secretary.put("level", "SECRETARY");
        secretary.put("count", 3);
        hierarchy.add(secretary);
        
        Map<String, Object> jointSecretary = new HashMap<>();
        jointSecretary.put("id", 4);
        jointSecretary.put("name", "Joint Secretary");
        jointSecretary.put("level", "JOINT_SECRETARY");
        jointSecretary.put("count", 5);
        hierarchy.add(jointSecretary);
        
        return ResponseEntity.ok(hierarchy);
    }
    
    @GetMapping("/{level}")
    public ResponseEntity<List<Map<String, Object>>> getLeadersByLevel(
            @PathVariable String level,
            HttpServletRequest request) {
        log.debug("Getting leaders by level: {}", level);
        
        // Get user constraints
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Return leaders for the specified level - same format as Node.js
        List<Map<String, Object>> leaders = new ArrayList<>();
        
        switch (level.toUpperCase()) {
            case "PRESIDENT":
                Map<String, Object> president = new HashMap<>();
                president.put("id", 1);
                president.put("name", "Sri Radhika Raman Das");
                president.put("level", "PRESIDENT");
                president.put("district", "Kolkata");
                president.put("phone", "+91-9876543210");
                president.put("email", "president@namhatta.org");
                leaders.add(president);
                break;
                
            case "VICE_PRESIDENT":
                Map<String, Object> vp1 = new HashMap<>();
                vp1.put("id", 2);
                vp1.put("name", "Sri Krishna Das");
                vp1.put("level", "VICE_PRESIDENT");
                vp1.put("district", "Hooghly");
                vp1.put("phone", "+91-9876543211");
                vp1.put("email", "vp1@namhatta.org");
                leaders.add(vp1);
                
                Map<String, Object> vp2 = new HashMap<>();
                vp2.put("id", 3);
                vp2.put("name", "Sri Gauranga Das");
                vp2.put("level", "VICE_PRESIDENT");
                vp2.put("district", "Howrah");
                vp2.put("phone", "+91-9876543212");
                vp2.put("email", "vp2@namhatta.org");
                leaders.add(vp2);
                break;
                
            case "SECRETARY":
                for (int i = 1; i <= 3; i++) {
                    Map<String, Object> secretary = new HashMap<>();
                    secretary.put("id", 3 + i);
                    secretary.put("name", "Sri Secretary " + i);
                    secretary.put("level", "SECRETARY");
                    secretary.put("district", "District " + i);
                    secretary.put("phone", "+91-98765432" + (10 + i));
                    secretary.put("email", "secretary" + i + "@namhatta.org");
                    leaders.add(secretary);
                }
                break;
                
            default:
                // Return empty list for unknown levels
                break;
        }
        
        return ResponseEntity.ok(leaders);
    }
}