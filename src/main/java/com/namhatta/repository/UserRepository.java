package com.namhatta.repository;

import com.namhatta.entity.User;
import com.namhatta.entity.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Tag(name = "User Repository", description = "Data access operations for user authentication")
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username for authentication
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by username and active status
     */
    Optional<User> findByUsernameAndIsActive(String username, Boolean isActive);
    
    /**
     * Find all active users by role
     */
    List<User> findByRoleAndIsActive(UserRole role, Boolean isActive);
    
    /**
     * Find all district supervisors
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Check if username exists (for registration validation)
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users with specific role in a district (for district supervisors)
     * TODO: Implement proper district relationship query when districts field is added to User entity
     */
    // List<User> findActiveUsersInDistrict(@Param("role") UserRole role, @Param("districtCode") String districtCode);
    
    /**
     * Count total active users
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * Count users by role
     */
    long countByRoleAndIsActive(UserRole role, Boolean isActive);
    
    /**
     * Find users by username containing search term (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY u.createdAt DESC")
    org.springframework.data.domain.Page<User> findByUsernameContainingIgnoreCase(@Param("search") String search, org.springframework.data.domain.Pageable pageable);
}