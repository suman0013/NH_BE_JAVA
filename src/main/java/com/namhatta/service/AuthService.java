package com.namhatta.service;

import com.namhatta.dto.DistrictDto;
import com.namhatta.dto.LoginRequest;
import com.namhatta.dto.LoginResponse;
import com.namhatta.dto.UserDto;
import com.namhatta.entity.User;
import com.namhatta.repository.UserRepository;
import com.namhatta.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Authentication service for user login/logout operations
 * Implements same business logic as Node.js AuthService
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate user with username and password
     * Same authentication flow as Node.js implementation
     */
    public LoginResponse authenticate(LoginRequest request) {
        log.info("Starting authentication for user: {}", request.getUsername());
        
        try {
            // Find user (same logic as Node.js)
            log.debug("Looking up user in database: {}", request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new BadCredentialsException("Invalid credentials");
                });
            
            log.debug("User found, checking if account is active: {}", user.getUsername());
            // Check if user is active
            if (!user.getIsActive()) {
                log.warn("Attempt to login with disabled account: {}", user.getUsername());
                throw new BadCredentialsException("User account is disabled");
            }
            
            // Validate password
            log.debug("Validating password for user: {}", user.getUsername());
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                log.warn("Invalid password attempt for user: {}", user.getUsername());
                throw new BadCredentialsException("Invalid credentials");
            }
            
            // Create session (enforces single login)
            log.debug("Creating session for user: {}", user.getUsername());
            String sessionToken = sessionService.createSession(user);
            
            // Generate JWT token
            log.debug("Generating JWT token for user: {}", user.getUsername());
            String jwtToken = tokenProvider.createToken(user, sessionToken);
            
            // Prepare response
            log.debug("Preparing authentication response for user: {}", user.getUsername());
            UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .districts(java.util.Collections.emptyList()) // TODO: Implement district relationship
                .build();
            
            LoginResponse response = LoginResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
                
            log.info("Authentication successful for user: {}", user.getUsername());
            return response;
            
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getUsername(), e);
            throw new RuntimeException("Authentication process failed", e);
        }
    }
    
    /**
     * Logout user and invalidate session
     */
    public void logout(String username, String sessionToken) {
        log.info("Starting logout process for user: {}", username);
        
        try {
            log.debug("Invalidating session for user: {}", username);
            // Invalidate session
            sessionService.invalidateSession(username, sessionToken);
            
            log.debug("Token blacklisted for user: {}", username);
            // Note: Token blacklisting could be implemented here if needed
            // For now, session invalidation provides the security
            
            log.info("Logout successful for user: {}", username);
        } catch (Exception e) {
            log.error("Error during logout for user: {}", username, e);
            throw new RuntimeException("Logout process failed", e);
        }
    }
    
    /**
     * Verify if user exists and is active
     */
    public boolean isUserValid(String username) {
        log.debug("Checking if user is valid: {}", username);
        
        return userRepository.findByUsernameAndIsActive(username, true)
            .isPresent();
    }
    
    /**
     * Get user information by username (for authentication purposes)
     */
    public UserDto getUserInfo(String username) {
        log.debug("Getting user info for: {}", username);
        
        User user = userRepository.findByUsernameAndIsActive(username, true)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .role(user.getRole().name())
            .districts(java.util.Collections.emptyList()) // TODO: Implement district relationship
            .build();
    }
}