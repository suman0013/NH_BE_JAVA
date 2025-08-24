package com.namhatta.repository;

import com.namhatta.entity.Shraddhakutir;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Tag(name = "Shraddhakutir Repository", description = "Data access operations for shraddhakutir management")
public interface ShraddhakutirRepository extends JpaRepository<Shraddhakutir, Long> {
    
    /**
     * Find shraddhakutir by name
     */
    Optional<Shraddhakutir> findByName(String name);
    
    /**
     * Find active shraddhakutirs
     */
    Page<Shraddhakutir> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Search shraddhakutirs by name
     */
    Page<Shraddhakutir> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find shraddhakutirs by country
     */
    List<Shraddhakutir> findByCountryAndIsActive(String country, Boolean isActive);
    
    /**
     * Find shraddhakutirs by state
     */
    List<Shraddhakutir> findByStateAndIsActive(String state, Boolean isActive);
    
    /**
     * Find shraddhakutirs by district
     */
    List<Shraddhakutir> findByDistrictAndIsActive(String district, Boolean isActive);
    
    /**
     * Search shraddhakutirs by location
     */
    @Query("""
        SELECT s FROM Shraddhakutir s 
        WHERE s.isActive = true
        AND (:country IS NULL OR LOWER(s.country) LIKE LOWER(CONCAT('%', :country, '%')))
        AND (:state IS NULL OR LOWER(s.state) LIKE LOWER(CONCAT('%', :state, '%')))
        AND (:district IS NULL OR LOWER(s.district) LIKE LOWER(CONCAT('%', :district, '%')))
        """)
    List<Shraddhakutir> findByLocationCriteria(@Param("country") String country,
                                              @Param("state") String state,
                                              @Param("district") String district);
    
    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String name);
    
    /**
     * Count active shraddhakutirs
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * Get shraddhakutir statistics
     */
    @Query("""
        SELECT s.name, 
               (SELECT COUNT(d) FROM s.devotees d) as devoteeCount,
               (SELECT COUNT(n) FROM s.namhattas n WHERE n.isActive = true) as namhattaCount
        FROM Shraddhakutir s 
        WHERE s.isActive = true
        ORDER BY s.name
        """)
    List<Object[]> getShraddhakutirStatistics();
    
    /**
     * Count shraddhakutirs by districts
     */
    @Query("SELECT COUNT(s) FROM Shraddhakutir s WHERE s.district IN :districts AND s.isActive = true")
    long countByDistricts(@Param("districts") List<String> districts);
}