package com.namhatta.repository;

import com.namhatta.entity.Leader;
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
@Tag(name = "Leader Repository", description = "Data access operations for spiritual leadership hierarchy")
public interface LeaderRepository extends JpaRepository<Leader, Long> {
    
    /**
     * Find leader by name
     */
    Optional<Leader> findByName(String name);
    
    /**
     * Find active leaders
     */
    Page<Leader> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Find leaders by hierarchy level
     */
    List<Leader> findByHierarchyLevelAndIsActive(Integer hierarchyLevel, Boolean isActive);
    
    /**
     * Find top-level leaders (no parent)
     */
    List<Leader> findByParentLeaderIdIsNullAndIsActive(Boolean isActive);
    
    /**
     * Find leaders under a parent leader
     */
    List<Leader> findByParentLeaderIdAndIsActive(Long parentLeaderId, Boolean isActive);
    
    /**
     * Search leaders by name or title
     */
    @Query("""
        SELECT l FROM Leader l 
        WHERE l.isActive = true
        AND (LOWER(l.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY l.hierarchyLevel, l.name
        """)
    Page<Leader> searchByNameOrTitle(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find leaders by region
     */
    List<Leader> findByRegionAndIsActive(String region, Boolean isActive);
    
    /**
     * Get complete hierarchy starting from top level
     */
    @Query("""
        SELECT l FROM Leader l 
        WHERE l.isActive = true
        ORDER BY l.hierarchyLevel, l.parentLeaderId NULLS FIRST, l.name
        """)
    List<Leader> findCompleteHierarchy();
    
    /**
     * Get hierarchy by level
     */
    List<Leader> findByHierarchyLevelOrderByName(Integer hierarchyLevel);
    
    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String name);
    
    /**
     * Count leaders by hierarchy level
     */
    long countByHierarchyLevelAndIsActive(Integer hierarchyLevel, Boolean isActive);
    
    /**
     * Get hierarchy structure for specific leader
     */
    @Query(value = """
        WITH RECURSIVE leader_hierarchy AS (
            SELECT id, name, title, parent_leader_id, hierarchy_level, 0 as depth
            FROM leaders 
            WHERE id = :leaderId AND is_active = true
            
            UNION ALL
            
            SELECT l.id, l.name, l.title, l.parent_leader_id, l.hierarchy_level, lh.depth + 1
            FROM leaders l
            INNER JOIN leader_hierarchy lh ON l.parent_leader_id = lh.id
            WHERE l.is_active = true
        )
        SELECT * FROM leader_hierarchy ORDER BY depth, hierarchy_level
        """, nativeQuery = true)
    List<Object[]> getLeaderHierarchy(@Param("leaderId") Long leaderId);
}