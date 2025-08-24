package com.namhatta.service;

import com.namhatta.entity.User;
import com.namhatta.entity.UserSession;
import com.namhatta.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing user sessions
 * Implements single login enforcement matching Node.js behavior
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    
    private final UserSessionRepository userSessionRepository;
    
    /**
     * Create a new session for the user (enforces single login)
     * Same behavior as Node.js implementation
     */
    public String createSession(User user) {
        log.debug("Creating session for user: {}", user.getUsername());
        
        // Invalidate any existing sessions (single login enforcement)
        invalidateAllUserSessions(user.getUsername());
        
        // Create new session token
        String sessionToken = UUID.randomUUID().toString();
        
        // Create session record
        UserSession session = UserSession.builder()
            .user(user)
            .sessionToken(sessionToken)
            .createdAt(LocalDateTime.now())
            .lastActivityAt(LocalDateTime.now())
            .isActive(true)
            .build();
        
        userSessionRepository.save(session);
        
        log.info("Session created for user: {} with token: {}", 
                user.getUsername(), sessionToken.substring(0, 8) + "...");
        
        return sessionToken;
    }
    
    /**
     * Validate if session is still active and valid
     */
    public boolean isSessionValid(String username, String sessionToken) {
        log.trace("Validating session for user: {} with token: {}", 
                 username, sessionToken != null ? sessionToken.substring(0, 8) + "..." : "null");
        
        if (sessionToken == null || username == null) {
            log.debug("Session validation failed: missing username or token");
            return false;
        }
        
        UserSession session = userSessionRepository.findByUsernameAndSessionTokenAndIsActive(
            username, sessionToken, true);
        
        if (session == null) {
            log.debug("Session not found or inactive for user: {}", username);
            return false;
        }
        
        // Update last activity
        session.setLastActivityAt(LocalDateTime.now());
        userSessionRepository.save(session);
        
        log.trace("Session valid for user: {}", username);
        return true;
    }
    
    /**
     * Invalidate a specific session
     */
    public void invalidateSession(String username, String sessionToken) {
        log.debug("Invalidating session for user: {} with token: {}", 
                 username, sessionToken != null ? sessionToken.substring(0, 8) + "..." : "null");
        
        if (sessionToken == null || username == null) {
            log.warn("Cannot invalidate session: missing username or token");
            return;
        }
        
        UserSession session = userSessionRepository.findByUsernameAndSessionTokenAndIsActive(
            username, sessionToken, true);
        
        if (session != null) {
            session.setIsActive(false);
            session.setLastActivityAt(LocalDateTime.now());
            userSessionRepository.save(session);
            
            log.info("Session invalidated for user: {}", username);
        } else {
            log.debug("Session not found for invalidation: user={}", username);
        }
    }
    
    /**
     * Invalidate all sessions for a user (used during login for single session enforcement)
     */
    public void invalidateAllUserSessions(String username) {
        log.debug("Invalidating all sessions for user: {}", username);
        
        int invalidatedCount = userSessionRepository.invalidateAllUserSessions(username);
        
        if (invalidatedCount > 0) {
            log.info("Invalidated {} existing session(s) for user: {}", invalidatedCount, username);
        } else {
            log.debug("No existing sessions to invalidate for user: {}", username);
        }
    }
    
    /**
     * Clean up expired sessions (can be called by scheduled task)
     */
    public void cleanupExpiredSessions() {
        log.debug("Starting cleanup of expired sessions");
        
        // Consider sessions older than 24 hours as expired
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        
        int cleanedCount = userSessionRepository.deleteExpiredSessions(cutoffTime);
        
        if (cleanedCount > 0) {
            log.info("Cleaned up {} expired session(s)", cleanedCount);
        } else {
            log.debug("No expired sessions found for cleanup");
        }
    }
}