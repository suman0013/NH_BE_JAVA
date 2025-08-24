package com.namhatta.controller;

import com.namhatta.dto.LoginRequest;
import com.namhatta.dto.LoginResponse;
import com.namhatta.dto.UserDto;
import com.namhatta.service.AuthService;
import com.namhatta.service.SessionService;
import com.namhatta.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authentication controller implementing all /api/auth/* endpoints
 * Maintains 100% API compatibility with the Node.js Express implementation
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000", "https://*.replit.app", "https://*.replit.dev"}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final SessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Environment environment;
    
    /**
     * Login endpoint - same as Node.js /api/auth/login
     * Sets HTTP-only cookie with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        log.info("Login attempt for user: {}", request.getUsername());
        
        try {
            LoginResponse loginResponse = authService.authenticate(request);
            
            // Set HTTP-only cookie (same as Node.js implementation)
            Cookie cookie = new Cookie("auth_token", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure("production".equals(environment.getProperty("spring.profiles.active")));
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 24 hours (same as Node.js)
            response.addCookie(cookie);
            
            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(loginResponse);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {} - {}", request.getUsername(), e.getMessage());
            
            LoginResponse errorResponse = LoginResponse.builder()
                .token(null)
                .user(null)
                .build();
            
            // Return 401 with error message (same as Node.js)
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error during login for user: {}", request.getUsername(), e);
            
            LoginResponse errorResponse = LoginResponse.builder()
                .token(null)
                .user(null)
                .build();
            
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Logout endpoint - same as Node.js /api/auth/logout
     * Clears HTTP-only cookie and invalidates session
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("Logout request received");
        
        try {
            // Get token from request (cookie or header)
            String token = getTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Extract user info from token
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);
                String username = claims.getSubject();
                String sessionToken = claims.get("sessionToken", String.class);
                
                log.info("Processing logout for user: {}", username);
                
                // Logout user (invalidate session)
                authService.logout(username, sessionToken);
            }
            
            // Clear HTTP-only cookie (same as Node.js)
            Cookie cookie = new Cookie("auth_token", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // Delete cookie
            response.addCookie(cookie);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", "Logged out successfully");
            
            log.info("Logout completed successfully");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error during logout process", e);
            
            // Still clear the cookie even if logout process fails
            Cookie cookie = new Cookie("auth_token", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Token verification endpoint - same as Node.js /api/auth/verify
     * Validates JWT token and session, returns user info
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        
        log.debug("Token verification request received");
        
        try {
            String token = getTokenFromRequest(request);
            
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                log.debug("Invalid or missing token");
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String username = claims.getSubject();
            String sessionToken = claims.get("sessionToken", String.class);
            
            log.debug("Validating session for user: {}", username);
            
            // Validate session (enforces single login like Node.js)
            if (!sessionService.isSessionValid(username, sessionToken)) {
                log.debug("Session invalid for user: {}", username);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            // Return user info (same format as Node.js)
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            user.put("id", claims.get("userId", Long.class));
            user.put("username", username);
            user.put("role", claims.get("role", String.class));
            user.put("districts", claims.get("districts", List.class));
            
            result.put("user", user);
            
            log.debug("Token verification successful for user: {}", username);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error during token verification", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Session expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * Development endpoint to list users - same as Node.js /api/auth/dev/users
     * Only available in development environment
     */
    @GetMapping("/dev/users")
    public ResponseEntity<Map<String, Object>> getDevUsers() {
        String currentEnvironment = environment.getProperty("spring.profiles.active", "development");
        
        if (!"development".equals(currentEnvironment)) {
            log.warn("Development endpoint accessed in non-development environment: {}", currentEnvironment);
            return ResponseEntity.notFound().build();
        }
        
        log.info("Development users endpoint accessed");
        
        try {
            // Create response with available test users (same as Node.js)
            Map<String, Object> users = new HashMap<>();
            users.put("available_users", List.of(
                Map.of(
                    "username", "admin",
                    "role", "ADMIN",
                    "description", "System administrator with full access"
                ),
                Map.of(
                    "username", "office1", 
                    "role", "OFFICE",
                    "description", "Office user with create/edit permissions"
                ),
                Map.of(
                    "username", "supervisor1",
                    "role", "DISTRICT_SUPERVISOR", 
                    "description", "District supervisor with district-specific access"
                )
            ));
            users.put("note", "Use password 'password123' for all test users");
            users.put("environment", currentEnvironment);
            
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            log.error("Error retrieving development users", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to retrieve users");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("Authentication service health check");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("service", "authentication");
        health.put("environment", environment.getProperty("spring.profiles.active", "development"));
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Extract JWT token from request (cookie or Authorization header)
     * Same logic as JwtAuthenticationFilter
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Check HTTP-only cookie first (primary method, same as Node.js)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header for API clients
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}