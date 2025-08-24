package com.namhatta.repository;

import com.namhatta.entity.Update;
import com.namhatta.entity.Namhatta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Long> {
    
    /**
     * Find updates by namhatta
     */
    Page<Update> findByNamhatta(Namhatta namhatta, Pageable pageable);
    
    /**
     * Find updates by namhatta ID
     */
    Page<Update> findByNamhattaId(Long namhattaId, Pageable pageable);
    
    /**
     * Find updates by namhattas in specific districts (for district supervisor filtering)
     */
    @Query("""
        SELECT u FROM Update u 
        JOIN u.namhatta n
        JOIN NamhattaAddress na ON na.namhatta = n
        JOIN na.address a 
        WHERE a.districtNameEnglish IN :districts
        ORDER BY u.createdAt DESC
        """)
    Page<Update> findUpdatesByDistricts(@Param("districts") List<String> districts, Pageable pageable);
    
    /**
     * Find all updates ordered by creation date
     */
    Page<Update> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Count updates by namhatta
     */
    long countByNamhatta(Namhatta namhatta);
    
    /**
     * Find recent updates for dashboard
     */
    @Query("""
        SELECT u FROM Update u 
        WHERE u.createdAt >= :since
        ORDER BY u.createdAt DESC
        """)
    List<Update> findRecentUpdates(@Param("since") java.time.LocalDateTime since, Pageable pageable);
}