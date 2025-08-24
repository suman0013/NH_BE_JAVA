package com.namhatta.repository;

import com.namhatta.entity.DevotionalStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Tag(name = "Devotional Status Repository", description = "Data access operations for devotional status management")
public interface DevotionalStatusRepository extends JpaRepository<DevotionalStatus, Long> {
    
    /**
     * Find devotional status by name
     */
    Optional<DevotionalStatus> findByName(String name);
    
    /**
     * Find all devotional statuses ordered by hierarchy level
     */
    List<DevotionalStatus> findAllByOrderByHierarchyLevelAsc();
    
    /**
     * Find devotional statuses by hierarchy level
     */
    List<DevotionalStatus> findByHierarchyLevel(Integer hierarchyLevel);
    
    /**
     * Search devotional statuses by name
     */
    Page<DevotionalStatus> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find devotional statuses with hierarchy level greater than specified
     */
    List<DevotionalStatus> findByHierarchyLevelGreaterThanOrderByHierarchyLevelAsc(Integer level);
    
    /**
     * Find devotional statuses with hierarchy level less than specified
     */
    List<DevotionalStatus> findByHierarchyLevelLessThanOrderByHierarchyLevelDesc(Integer level);
    
    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String name);
    
    /**
     * Get status distribution statistics
     */
    @Query("SELECT ds.name, COUNT(d) FROM DevotionalStatus ds LEFT JOIN ds.devotees d GROUP BY ds.id, ds.name ORDER BY ds.hierarchyLevel")
    List<Object[]> getStatusDistribution();
}