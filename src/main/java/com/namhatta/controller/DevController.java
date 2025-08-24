package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Development-only controller for testing and debugging
 * This controller is only available when the 'development' profile is active
 */
@RestController
@RequestMapping("/api/auth/dev")
@Profile("development")
@RequiredArgsConstructor
@Slf4j
public class DevController {
    
    private final Environment environment;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        log.info("Development auth status check requested");
        
        Map<String, Object> status = new HashMap<>();
        status.put("authEnabled", environment.getProperty("app.auth.enabled", "true"));
        status.put("environment", environment.getProperty("spring.profiles.active", "development"));
        status.put("devMode", "false".equals(environment.getProperty("app.auth.enabled")));
        status.put("bypassAllowed", environment.getProperty("app.auth.bypass-allowed", "true"));
        status.put("corsOrigins", environment.getProperty("app.cors.allowed-origins", "*"));
        status.put("securityHeaders", environment.getProperty("app.security.headers-enabled", "false"));
        status.put("rateLimiting", environment.getProperty("app.rate-limit.enabled", "false"));
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleAuth(@RequestBody Map<String, Boolean> request) {
        log.warn("Development auth toggle requested: {}", request);
        
        // CRITICAL: Same safety checks as Node.js implementation
        String currentProfile = environment.getProperty("spring.profiles.active", "development");
        
        if ("production".equals(currentProfile)) {
            log.error("🚨 SECURITY ERROR: Authentication bypass attempted in production!");
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Operation not allowed in production");
            errorResponse.put("message", "Authentication bypass is not permitted in production environment");
            errorResponse.put("status", 403);
            
            return ResponseEntity.status(403).body(errorResponse);
        }
        
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            enabled = true; // Default to secure state
        }
        
        // Set system property for runtime toggle
        System.setProperty("app.auth.enabled", enabled.toString());
        
        if (!enabled) {
            log.warn("⚠️ WARNING: Authentication bypass is now active in development mode");
            log.warn("⚠️ This should NEVER be used in production!");
        } else {
            log.info("✅ Authentication is now enabled");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("authEnabled", enabled.toString());
        response.put("environment", currentProfile);
        response.put("message", String.format("Authentication %s (restart may be required for full effect)", 
                                            enabled ? "enabled" : "disabled"));
        response.put("warning", enabled ? null : "SECURITY WARNING: Authentication is disabled!");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/env")
    public ResponseEntity<Map<String, Object>> getEnvironmentInfo() {
        log.debug("Development environment info requested");
        
        Map<String, Object> envInfo = new HashMap<>();
        envInfo.put("activeProfile", environment.getProperty("spring.profiles.active"));
        envInfo.put("javaVersion", System.getProperty("java.version"));
        envInfo.put("springBootVersion", environment.getProperty("spring-boot.version"));
        envInfo.put("databaseUrl", maskDatabaseUrl(environment.getProperty("spring.datasource.url")));
        envInfo.put("isDevelopment", "development".equals(environment.getProperty("spring.profiles.active")));
        
        return ResponseEntity.ok(envInfo);
    }
    
    private String maskDatabaseUrl(String url) {
        if (url == null) return "Not configured";
        // Mask sensitive parts of the database URL
        return url.replaceAll("(://[^:]+:)[^@]+(@)", "$1***$2");
    }
}