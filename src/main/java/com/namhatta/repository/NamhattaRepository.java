package com.namhatta.repository;

import com.namhatta.entity.Namhatta;
import com.namhatta.entity.Shraddhakutir;
import com.namhatta.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Tag(name = "Namhatta Repository", description = "Data access operations for namhatta management")
public interface NamhattaRepository extends JpaRepository<Namhatta, Long> {
    
    /**
     * Find namhattas by name - for search functionality
     */
    Page<Namhatta> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find approved and active namhattas
     */
    Page<Namhatta> findByIsApprovedAndIsActive(Boolean isApproved, Boolean isActive, Pageable pageable);
    
    /**
     * Find namhattas by approval status
     */
    Page<Namhatta> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    /**
     * Find namhattas by district supervisor
     */
    Page<Namhatta> findByDistrictSupervisor(User districtSupervisor, Pageable pageable);
    
    /**
     * Find namhattas by shraddhakutir
     */
    Page<Namhatta> findByShraddhakutir(Shraddhakutir shraddhakutir, Pageable pageable);
    
    /**
     * Find namhattas in specific districts (for district supervisor filtering)
     */
    @Query("""
        SELECT n FROM Namhatta n 
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE a.districtNameEnglish IN :districts
        AND n.isActive = true
        """)
    Page<Namhatta> findActiveNamhattasInDistricts(@Param("districts") List<String> districts, Pageable pageable);
    
    /**
     * Search namhattas with district filtering
     */
    @Query("""
        SELECT n FROM Namhatta n 
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE LOWER(n.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        AND a.districtNameEnglish IN :districts
        AND n.isActive = true
        """)
    Page<Namhatta> searchNamhattasInDistricts(@Param("searchTerm") String searchTerm, 
                                             @Param("districts") List<String> districts, 
                                             Pageable pageable);
    
    /**
     * Find pending approval namhattas for admins
     */
    @Query("SELECT n FROM Namhatta n WHERE n.isApproved = false AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Namhatta> findPendingApprovalNamhattas();
    
    /**
     * Count namhattas by approval and active status
     */
    long countByIsApprovedAndIsActive(Boolean isApproved, Boolean isActive);
    
    /**
     * Count namhattas supervised by a district supervisor
     */
    long countByDistrictSupervisorAndIsActive(User districtSupervisor, Boolean isActive);
    
    /**
     * Count namhattas in districts
     */
    @Query("""
        SELECT COUNT(DISTINCT n) FROM Namhatta n 
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE a.districtNameEnglish IN :districts
        AND n.isActive = true
        """)
    long countActiveNamhattasInDistricts(@Param("districts") List<String> districts);
    
    /**
     * Get namhatta distribution by district for maps
     */
    @Query("""
        SELECT a.districtNameEnglish, COUNT(DISTINCT n) 
        FROM Namhatta n 
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE n.isApproved = true AND n.isActive = true
        GROUP BY a.districtNameEnglish
        """)
    List<Object[]> getNamhattaCountByDistrict();
    
    /**
     * Get namhatta distribution by state for maps
     */
    @Query("""
        SELECT a.stateNameEnglish, COUNT(DISTINCT n) 
        FROM Namhatta n 
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE n.isApproved = true AND n.isActive = true
        GROUP BY a.stateNameEnglish
        """)
    List<Object[]> getNamhattaCountByState();
}