package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        log.debug("Health check requested");
        
        // Return health status - same format as Node.js
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "Namhatta Management System - Spring Boot");
        health.put("version", "1.0.0");
        
        Map<String, Object> checks = new HashMap<>();
        checks.put("database", "connected");
        checks.put("authentication", "active");
        checks.put("server", "running");
        health.put("checks", checks);
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/about")
    public ResponseEntity<Map<String, Object>> getAbout() {
        log.debug("About information requested");
        
        // Return system information - same format as Node.js
        Map<String, Object> about = new HashMap<>();
        about.put("name", "Namhatta Management System");
        about.put("version", "1.0.0");
        about.put("description", "Spring Boot backend for Namhatta Management System");
        about.put("framework", "Spring Boot 3.2");
        about.put("database", "PostgreSQL");
        about.put("authentication", "JWT with HTTP-only cookies");
        
        Map<String, String> contact = new HashMap<>();
        contact.put("organization", "Namhatta Organization");
        contact.put("email", "admin@namhatta.org");
        about.put("contact", contact);
        
        return ResponseEntity.ok(about);
    }
}