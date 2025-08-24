package com.namhatta.service;

import com.namhatta.entity.Leader;
import com.namhatta.repository.LeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * HierarchyService - Business logic for organizational hierarchy management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HierarchyService {
    
    private final LeaderRepository leaderRepository;
    
    /**
     * Get top level hierarchy with counts
     */
    public List<Map<String, Object>> getTopLevelHierarchy(String userRole, List<String> allowedDistricts) {
        log.debug("Getting top level hierarchy for user role: {}, districts: {}", userRole, allowedDistricts);
        
        try {
            List<Object[]> hierarchyStats = leaderRepository.getHierarchyStatistics();
            boolean isDistrictFiltered = "DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null;
            
            List<Map<String, Object>> hierarchy = new ArrayList<>();
            
            for (Object[] row : hierarchyStats) {
                Integer level = (Integer) row[0];
                Long count = (Long) row[1];
                
                Map<String, Object> levelInfo = new HashMap<>();
                levelInfo.put("id", level);
                levelInfo.put("level", getHierarchyLevelName(level));
                levelInfo.put("name", getHierarchyLevelDisplayName(level));
                
                // Apply district filtering for supervisors
                if (isDistrictFiltered && allowedDistricts != null) {
                    List<Leader> levelLeaders = leaderRepository.findActiveLeadersByDistricts(allowedDistricts)
                        .stream()
                        .filter(leader -> leader.getHierarchyLevel().equals(level))
                        .collect(Collectors.toList());
                    levelInfo.put("count", levelLeaders.size());
                } else {
                    levelInfo.put("count", count.intValue());
                }
                
                hierarchy.add(levelInfo);
            }
            
            log.debug("Retrieved {} hierarchy levels from database", hierarchy.size());
            return hierarchy;
            
        } catch (Exception e) {
            log.error("Error retrieving top level hierarchy", e);
            throw new RuntimeException("Failed to retrieve hierarchy", e);
        }
    }
    
    /**
     * Get leaders by hierarchy level
     */
    public List<Map<String, Object>> getLeadersByLevel(String level, String userRole, List<String> allowedDistricts) {
        log.debug("Getting leaders by level: {} for user role: {}, districts: {}", level, userRole, allowedDistricts);
        
        try {
            Integer hierarchyLevel = getHierarchyLevelNumber(level);
            if (hierarchyLevel == null) {
                log.warn("Unknown hierarchy level requested: {}", level);
                return new ArrayList<>();
            }
            
            List<Leader> leaderEntities;
            boolean isDistrictFiltered = "DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null;
            
            if (isDistrictFiltered && allowedDistricts != null) {
                // Filter leaders by allowed districts
                leaderEntities = leaderRepository.findActiveLeadersByDistricts(allowedDistricts)
                    .stream()
                    .filter(leader -> leader.getHierarchyLevel().equals(hierarchyLevel))
                    .collect(Collectors.toList());
            } else {
                // Get all leaders for this level
                leaderEntities = leaderRepository.findByHierarchyLevelAndIsActiveTrue(hierarchyLevel);
            }
            
            List<Map<String, Object>> leaders = leaderEntities.stream()
                .map(this::convertLeaderToMap)
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} leaders for level: {} from database", leaders.size(), level);
            return leaders;
            
        } catch (Exception e) {
            log.error("Error retrieving leaders for level: {}", level, e);
            throw new RuntimeException("Failed to retrieve leaders", e);
        }
    }
    
    /**
     * Get hierarchy statistics
     */
    public Map<String, Object> getHierarchyStatistics(String userRole, List<String> allowedDistricts) {
        log.debug("Getting hierarchy statistics for user role: {}, districts: {}", userRole, allowedDistricts);
        
        try {
            Map<String, Object> stats = new HashMap<>();
            boolean isDistrictFiltered = "DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null;
            
            if (isDistrictFiltered && allowedDistricts != null) {
                // Filter statistics for district supervisors
                List<Leader> filteredLeaders = leaderRepository.findActiveLeadersByDistricts(allowedDistricts);
                stats.put("totalLeaders", filteredLeaders.size());
                stats.put("districtsManaged", allowedDistricts.size());
            } else {
                // Full statistics for admin/office
                long totalLeaders = leaderRepository.countByHierarchyLevelAndIsActiveTrue(null);
                List<Object[]> hierarchyStats = leaderRepository.getHierarchyStatistics();
                stats.put("totalLeaders", (int) totalLeaders);
                stats.put("totalHierarchyLevels", hierarchyStats.size());
            }
            
            List<Object[]> hierarchyStats = leaderRepository.getHierarchyStatistics();
            stats.put("activeLevels", hierarchyStats.size());
            
            log.debug("Hierarchy statistics calculated successfully from database");
            return stats;
            
        } catch (Exception e) {
            log.error("Error calculating hierarchy statistics", e);
            throw new RuntimeException("Failed to calculate hierarchy statistics", e);
        }
    }
    
    /**
     * Convert Leader entity to Map for API response
     */
    private Map<String, Object> convertLeaderToMap(Leader leader) {
        Map<String, Object> leaderMap = new HashMap<>();
        leaderMap.put("id", leader.getId());
        leaderMap.put("name", leader.getName());
        leaderMap.put("title", leader.getTitle());
        leaderMap.put("level", getHierarchyLevelName(leader.getHierarchyLevel()));
        leaderMap.put("hierarchyLevel", leader.getHierarchyLevel());
        leaderMap.put("region", leader.getRegion());
        leaderMap.put("contactInfo", leader.getContactInfo());
        leaderMap.put("description", leader.getDescription());
        leaderMap.put("isActive", leader.getIsActive());
        return leaderMap;
    }
    
    /**
     * Get hierarchy level name from number
     */
    private String getHierarchyLevelName(Integer level) {
        switch (level) {
            case 1: return "PRESIDENT";
            case 2: return "VICE_PRESIDENT";
            case 3: return "SECRETARY";
            case 4: return "JOINT_SECRETARY";
            case 5: return "REGIONAL_DIRECTOR";
            case 6: return "DISTRICT_SUPERVISOR";
            default: return "LEVEL_" + level;
        }
    }
    
    /**
     * Get hierarchy level display name from number
     */
    private String getHierarchyLevelDisplayName(Integer level) {
        switch (level) {
            case 1: return "President";
            case 2: return "Vice President";
            case 3: return "Secretary";
            case 4: return "Joint Secretary";
            case 5: return "Regional Director";
            case 6: return "District Supervisor";
            default: return "Level " + level;
        }
    }
    
    /**
     * Get hierarchy level number from name
     */
    private Integer getHierarchyLevelNumber(String levelName) {
        switch (levelName.toUpperCase()) {
            case "PRESIDENT": return 1;
            case "VICE_PRESIDENT": return 2;
            case "SECRETARY": return 3;
            case "JOINT_SECRETARY": return 4;
            case "REGIONAL_DIRECTOR": return 5;
            case "DISTRICT_SUPERVISOR": return 6;
            default: return null;
        }
    }
}