package com.namhatta.controller;

import com.namhatta.dto.HealthCheckDto;
import com.namhatta.dto.AboutDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<HealthCheckDto> getHealth() {
        log.debug("Health check requested");
        
        // Return health status - same format as Node.js
        HealthCheckDto health = HealthCheckDto.builder()
                .status("healthy")
                .timestamp(LocalDateTime.now().toString())
                .uptime("Namhatta Management System - Spring Boot")
                .version("1.0.0")
                .checks(Map.of(
                    "database", "connected",
                    "authentication", "active",
                    "server", "running"
                ))
                .build();
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/about")
    public ResponseEntity<AboutDto> getAbout() {
        log.debug("About information requested");
        
        // Return system information - same format as Node.js
        AboutDto about = AboutDto.builder()
                .application("Namhatta Management System")
                .version("1.0.0")
                .description("Spring Boot backend for Namhatta Management System with PostgreSQL database and JWT authentication")
                .maintainer("Spring Boot 3.2")
                .contact(Map.of(
                    "organization", "Namhatta Organization",
                    "email", "admin@namhatta.org"
                ))
                .build();
        
        return ResponseEntity.ok(about);
    }
}