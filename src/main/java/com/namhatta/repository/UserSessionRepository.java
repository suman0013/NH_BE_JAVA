package com.namhatta.repository;

import com.namhatta.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    /**
     * Find active session by username and session token
     */
    @Query("SELECT s FROM UserSession s " +
           "JOIN s.user u " +
           "WHERE u.username = :username " +
           "AND s.sessionToken = :sessionToken " +
           "AND s.isActive = :isActive")
    UserSession findByUsernameAndSessionTokenAndIsActive(
        @Param("username") String username,
        @Param("sessionToken") String sessionToken,
        @Param("isActive") Boolean isActive
    );
    
    /**
     * Invalidate all active sessions for a user
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.lastActivityAt = CURRENT_TIMESTAMP " +
           "WHERE s.user.username = :username AND s.isActive = true")
    int invalidateAllUserSessions(@Param("username") String username);
    
    /**
     * Delete expired sessions (older than cutoff time)
     */
    @Modifying
    @Query("DELETE FROM UserSession s " +
           "WHERE s.lastActivityAt < :cutoffTime " +
           "AND s.isActive = false")
    int deleteExpiredSessions(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Count active sessions for a user
     */
    @Query("SELECT COUNT(s) FROM UserSession s " +
           "WHERE s.user.username = :username " +
           "AND s.isActive = true")
    long countActiveSessionsByUsername(@Param("username") String username);
    
    /**
     * Find all active sessions for a user
     */
    @Query("SELECT s FROM UserSession s " +
           "WHERE s.user.username = :username " +
           "AND s.isActive = true " +
           "ORDER BY s.lastActivityAt DESC")
    java.util.List<UserSession> findActiveSessionsByUsername(@Param("username") String username);
}