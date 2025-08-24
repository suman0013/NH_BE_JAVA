package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class AdminController {
    
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        log.debug("Getting users - page: {}, size: {}, search: {}", page, size, search);
        
        // Return sample users data - same format as Node.js
        List<Map<String, Object>> users = new ArrayList<>();
        
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1L);
        admin.put("username", "admin");
        admin.put("role", "ADMIN");
        admin.put("isActive", true);
        admin.put("districts", new ArrayList<>());
        admin.put("createdAt", LocalDateTime.now().minusMonths(6).toString());
        users.add(admin);
        
        Map<String, Object> office = new HashMap<>();
        office.put("id", 2L);
        office.put("username", "office1");
        office.put("role", "OFFICE");
        office.put("isActive", true);
        office.put("districts", new ArrayList<>());
        office.put("createdAt", LocalDateTime.now().minusMonths(3).toString());
        users.add(office);
        
        Map<String, Object> supervisor = new HashMap<>();
        supervisor.put("id", 3L);
        supervisor.put("username", "supervisor1");
        supervisor.put("role", "DISTRICT_SUPERVISOR");
        supervisor.put("isActive", true);
        List<Map<String, String>> districts = Arrays.asList(
            Map.of("code", "WB_KOL", "name", "Kolkata"),
            Map.of("code", "WB_HOO", "name", "Hooghly")
        );
        supervisor.put("districts", districts);
        supervisor.put("createdAt", LocalDateTime.now().minusWeeks(2).toString());
        users.add(supervisor);
        
        // Filter by search term if provided
        if (search != null && !search.trim().isEmpty()) {
            final String searchTerm = search.toLowerCase();
            users = users.stream()
                .filter(user -> ((String) user.get("username")).toLowerCase().contains(searchTerm))
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
        }
        
        // Paginate results
        int start = (page - 1) * size;
        int end = Math.min(start + size, users.size());
        List<Map<String, Object>> paginatedUsers = users.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", paginatedUsers);
        response.put("total", users.size());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", (int) Math.ceil((double) users.size() / size));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody Map<String, Object> userData) {
        log.debug("Creating new user: {}", userData.get("username"));
        
        // Validate required fields
        if (!userData.containsKey("username") || !userData.containsKey("password") || !userData.containsKey("role")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username, password, and role are required");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
        }
        
        // Create new user - same format as Node.js
        Map<String, Object> createdUser = new HashMap<>();
        createdUser.put("id", 999L);
        createdUser.put("username", userData.get("username"));
        createdUser.put("role", userData.get("role"));
        createdUser.put("isActive", true);
        createdUser.put("districts", userData.getOrDefault("districts", new ArrayList<>()));
        createdUser.put("createdAt", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PostMapping("/supervisor-registration")
    public ResponseEntity<Map<String, Object>> registerSupervisor(@Valid @RequestBody Map<String, Object> registrationData) {
        log.debug("Registering new supervisor: {}", registrationData.get("username"));
        
        // Validate required fields for supervisor
        List<String> requiredFields = Arrays.asList("username", "password", "districts");
        for (String field : requiredFields) {
            if (!registrationData.containsKey(field)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required field: " + field);
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", error));
            }
        }
        
        // Create new supervisor - same format as Node.js
        Map<String, Object> supervisor = new HashMap<>();
        supervisor.put("id", 888L);
        supervisor.put("username", registrationData.get("username"));
        supervisor.put("role", "DISTRICT_SUPERVISOR");
        supervisor.put("isActive", true);
        supervisor.put("districts", registrationData.get("districts"));
        supervisor.put("createdAt", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisor);
    }
    
    @GetMapping("/district-supervisors")
    public ResponseEntity<List<Map<String, Object>>> getDistrictSupervisors(@RequestParam String district) {
        log.debug("Getting district supervisors for district: {}", district);
        
        // Return sample supervisors for the district
        List<Map<String, Object>> supervisors = new ArrayList<>();
        
        Map<String, Object> supervisor = new HashMap<>();
        supervisor.put("id", 3L);
        supervisor.put("username", "supervisor1");
        supervisor.put("role", "DISTRICT_SUPERVISOR");
        supervisor.put("isActive", true);
        List<Map<String, String>> districts = Arrays.asList(
            Map.of("code", district, "name", district)
        );
        supervisor.put("districts", districts);
        supervisors.add(supervisor);
        
        return ResponseEntity.ok(supervisors);
    }
    
    @GetMapping("/user-address-defaults/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAddressDefaults(@PathVariable Long userId) {
        log.debug("Getting address defaults for user: {}", userId);
        
        // Return sample address defaults
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("country", "India");
        defaults.put("state", "West Bengal");
        defaults.put("district", "Kolkata");
        
        return ResponseEntity.ok(defaults);
    }
}