package com.namhatta.service;

import com.namhatta.entity.DevotionalStatus;
import com.namhatta.repository.DevotionalStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StatusService - Business logic for devotional status management
 * Replaces hardcoded status operations with real database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatusService {
    
    private final DevotionalStatusRepository devotionalStatusRepository;
    
    /**
     * Get all devotional statuses
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDevotionalStatuses() {
        log.debug("Getting all devotional statuses");
        
        try {
            List<DevotionalStatus> statuses = devotionalStatusRepository.findAllByOrderByHierarchyLevelAsc();
            
            List<Map<String, Object>> statusList = statuses.stream()
                .map(this::convertStatusToMap)
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} devotional statuses", statusList.size());
            return statusList;
            
        } catch (Exception e) {
            log.error("Error retrieving devotional statuses", e);
            throw new RuntimeException("Failed to retrieve devotional statuses", e);
        }
    }
    
    /**
     * Create new devotional status
     */
    public Map<String, Object> createDevotionalStatus(String name) {
        log.debug("Creating devotional status: {}", name);
        
        try {
            // Validate input
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Status name is required");
            }
            
            // Check if name already exists
            if (devotionalStatusRepository.existsByName(name.trim())) {
                throw new IllegalArgumentException("Status name already exists");
            }
            
            // Determine hierarchy level (next highest level)
            int nextHierarchyLevel = getNextHierarchyLevel();
            
            // Create new status
            DevotionalStatus status = DevotionalStatus.builder()
                .name(name.trim())
                .hierarchyLevel(nextHierarchyLevel)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            DevotionalStatus savedStatus = devotionalStatusRepository.save(status);
            
            log.info("Successfully created devotional status: {} with hierarchy level: {}", name, nextHierarchyLevel);
            return convertStatusToMap(savedStatus);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating devotional status: {}", name, e);
            throw new RuntimeException("Failed to create devotional status", e);
        }
    }
    
    /**
     * Update devotional status
     */
    public Map<String, Object> updateDevotionalStatus(Long id, String name) {
        log.debug("Updating devotional status: {} with name: {}", id, name);
        
        try {
            // Validate input
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Status name is required");
            }
            
            // Find existing status
            Optional<DevotionalStatus> statusOpt = devotionalStatusRepository.findById(id);
            if (statusOpt.isEmpty()) {
                throw new IllegalArgumentException("Devotional status not found");
            }
            
            DevotionalStatus status = statusOpt.get();
            
            // Check if new name already exists (excluding current status)
            Optional<DevotionalStatus> existingStatus = devotionalStatusRepository.findByName(name.trim());
            if (existingStatus.isPresent() && !existingStatus.get().getId().equals(id)) {
                throw new IllegalArgumentException("Status name already exists");
            }
            
            // Update status
            status.setName(name.trim());
            status.setUpdatedAt(LocalDateTime.now());
            
            DevotionalStatus updatedStatus = devotionalStatusRepository.save(status);
            
            log.info("Successfully updated devotional status: {} to name: {}", id, name);
            return convertStatusToMap(updatedStatus);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status update data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating devotional status: {}", id, e);
            throw new RuntimeException("Failed to update devotional status", e);
        }
    }
    
    /**
     * Delete devotional status
     */
    public void deleteDevotionalStatus(Long id) {
        log.debug("Deleting devotional status: {}", id);
        
        try {
            // Check if status exists
            Optional<DevotionalStatus> statusOpt = devotionalStatusRepository.findById(id);
            if (statusOpt.isEmpty()) {
                throw new IllegalArgumentException("Devotional status not found");
            }
            
            DevotionalStatus status = statusOpt.get();
            
            // Check if status is being used by devotees
            if (!status.getDevotees().isEmpty()) {
                throw new IllegalArgumentException("Cannot delete status that is assigned to devotees");
            }
            
            // Delete status
            devotionalStatusRepository.delete(status);
            
            log.info("Successfully deleted devotional status: {} ({})", status.getName(), id);
            
        } catch (IllegalArgumentException e) {
            log.warn("Cannot delete status: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting devotional status: {}", id, e);
            throw new RuntimeException("Failed to delete devotional status", e);
        }
    }
    
    /**
     * Get status distribution with counts and percentages
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStatusDistribution() {
        log.debug("Getting status distribution");
        
        try {
            List<Object[]> distribution = devotionalStatusRepository.getStatusDistribution();
            long totalDevotees = distribution.stream()
                .mapToLong(row -> ((Long) row[1]))
                .sum();
            
            List<Map<String, Object>> result = distribution.stream()
                .map(row -> {
                    String statusName = (String) row[0];
                    Long count = (Long) row[1];
                    
                    Map<String, Object> statusInfo = new HashMap<>();
                    statusInfo.put("status", statusName);
                    statusInfo.put("count", count.intValue());
                    statusInfo.put("percentage", totalDevotees > 0 ? 
                        Math.round((count * 100.0 / totalDevotees) * 10.0) / 10.0 : 0.0);
                    return statusInfo;
                })
                .collect(Collectors.toList());
            
            log.debug("Status distribution calculated with {} statuses", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("Error calculating status distribution", e);
            throw new RuntimeException("Failed to calculate status distribution", e);
        }
    }
    
    /**
     * Get next hierarchy level for new status
     */
    private int getNextHierarchyLevel() {
        List<DevotionalStatus> allStatuses = devotionalStatusRepository.findAll();
        if (allStatuses.isEmpty()) {
            return 1;
        }
        
        return allStatuses.stream()
            .mapToInt(status -> status.getHierarchyLevel() != null ? status.getHierarchyLevel() : 0)
            .max()
            .orElse(0) + 1;
    }
    
    /**
     * Convert DevotionalStatus entity to Map for API response
     */
    private Map<String, Object> convertStatusToMap(DevotionalStatus status) {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("id", status.getId());
        statusMap.put("name", status.getName());
        statusMap.put("hierarchyLevel", status.getHierarchyLevel());
        statusMap.put("createdAt", status.getCreatedAt() != null ? status.getCreatedAt().toString() : null);
        statusMap.put("updatedAt", status.getUpdatedAt() != null ? status.getUpdatedAt().toString() : null);
        
        return statusMap;
    }
}