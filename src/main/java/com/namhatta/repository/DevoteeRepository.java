package com.namhatta.repository;

import com.namhatta.entity.Devotee;
import com.namhatta.entity.DevotionalStatus;
import com.namhatta.entity.Namhatta;
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
@Tag(name = "Devotee Repository", description = "Data access operations for devotee management")
public interface DevoteeRepository extends JpaRepository<Devotee, Long> {
    
    /**
     * Find devotees by name (legal or initiated name) - for search functionality
     */
    @Query("SELECT d FROM Devotee d WHERE LOWER(d.legalName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Devotee> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Find devotees by email
     */
    Optional<Devotee> findByEmail(String email);
    
    /**
     * Find devotees by phone
     */
    Optional<Devotee> findByPhone(String phone);
    
    /**
     * Find devotees by devotional status
     */
    Page<Devotee> findByDevotionalStatus(DevotionalStatus status, Pageable pageable);
    
    /**
     * Find devotees by namhatta
     */
    Page<Devotee> findByNamhatta(Namhatta namhatta, Pageable pageable);
    
    /**
     * Find devotees by shraddhakutir
     */
    Page<Devotee> findByShraddhakutir(Shraddhakutir shraddhakutir, Pageable pageable);
    
    /**
     * Find devotees in specific districts (for district supervisor filtering)
     * TODO: Implement when address relationship is properly defined
     */
    // @Query("""
    //     SELECT d FROM Devotee d 
    //     JOIN d.addresses da 
    //     JOIN da.address a 
    //     WHERE a.districtNameEnglish IN :districts
    //     """)
    // Page<Devotee> findByDistrictsIn(@Param("districts") List<String> districts, Pageable pageable);
    
    /**
     * Search devotees with district filtering
     * TODO: Implement when address relationship is properly defined
     */
    // @Query("""
    //     SELECT d FROM Devotee d 
    //     JOIN d.addresses da 
    //     JOIN da.address a 
    //     WHERE (LOWER(d.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
    //         OR LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    //     AND a.districtNameEnglish IN :districts
    //     """)
    // Page<Devotee> searchByNameInDistricts(@Param("searchTerm") String searchTerm, 
    //                                      @Param("districts") List<String> districts, 
    //                                      Pageable pageable);
    
    /**
     * Count devotees by devotional status
     */
    long countByDevotionalStatus(DevotionalStatus status);
    
    /**
     * Count devotees by namhatta
     */
    long countByNamhatta(Namhatta namhatta);
    
    /**
     * Count devotees in districts
     * TODO: Implement when address relationship is properly defined
     */
    // @Query("""
    //     SELECT COUNT(DISTINCT d) FROM Devotee d 
    //     JOIN d.addresses da 
    //     JOIN da.address a 
    //     WHERE a.districtNameEnglish IN :districts
    //     """)
    // long countDevoteesInDistricts(@Param("districts") List<String> districts);
    
    /**
     * Find devotees with specific initiation status
     */
    @Query("SELECT d FROM Devotee d WHERE d.harinamDate IS NOT NULL")
    List<Devotee> findInitiatedDevotees();
    
    /**
     * Find devotees by gender
     */
    Page<Devotee> findByGender(String gender, Pageable pageable);
}