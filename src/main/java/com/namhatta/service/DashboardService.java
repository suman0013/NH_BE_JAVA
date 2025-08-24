package com.namhatta.service;

import com.namhatta.dto.DashboardStatsDto;
import com.namhatta.dto.StatusDistributionDto;
import com.namhatta.repository.DevoteeRepository;
import com.namhatta.repository.DevotionalStatusRepository;
import com.namhatta.repository.NamhattaRepository;
import com.namhatta.repository.ShraddhakutirRepository;
import com.namhatta.entity.DevotionalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DashboardService - Business logic for dashboard statistics
 * Replaces hardcoded values with real database queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {
    
    private final DevoteeRepository devoteeRepository;
    private final NamhattaRepository namhattaRepository;
    private final ShraddhakutirRepository shraddhakutirRepository;
    private final DevotionalStatusRepository devotionalStatusRepository;
    
    /**
     * Get dashboard statistics with district filtering for supervisors
     */
    public DashboardStatsDto getDashboardStats(List<String> allowedDistricts) {
        log.debug("Getting dashboard statistics for districts: {}", allowedDistricts);
        
        try {
            // Get total counts based on user access level
            if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                // District supervisor - filtered counts
                return DashboardStatsDto.builder()
                    .totalDevotees(getDevoteeCountByDistricts(allowedDistricts))
                    .totalNamhattas(getNamhattaCountByDistricts(allowedDistricts))
                    .totalStatuses(getShraddhakutirCountByDistricts(allowedDistricts))
                    .totalUpdates(getPendingApprovalsCountByDistricts(allowedDistricts))
                    .statusDistribution(getStatusDistributionByDistricts(allowedDistricts))
                    .namhattaDistribution(new HashMap<>())
                    .build();
            } else {
                // Admin/Office - all counts
                return DashboardStatsDto.builder()
                    .totalDevotees(devoteeRepository.count())
                    .totalNamhattas(namhattaRepository.count())
                    .totalStatuses(shraddhakutirRepository.count())
                    .totalUpdates(namhattaRepository.countByIsApproved(false))
                    .statusDistribution(getStatusDistribution())
                    .namhattaDistribution(new HashMap<>())
                    .build();
            }
            
        } catch (Exception e) {
            log.error("Error calculating dashboard statistics", e);
            throw new RuntimeException("Failed to retrieve dashboard statistics", e);
        }
    }
    
    /**
     * Get status distribution for all devotees
     */
    public Map<String, Integer> getStatusDistribution() {
        log.debug("Getting status distribution for all devotees");
        
        List<DevotionalStatus> statuses = devotionalStatusRepository.findAll();
        Map<String, Integer> distribution = new HashMap<>();
        
        for (DevotionalStatus status : statuses) {
            long count = devoteeRepository.countByDevotionalStatus(status);
            distribution.put(status.getName(), (int) count);
        }
        
        return distribution;
    }
    
    /**
     * Get status distribution for devotees in specific districts
     */
    public Map<String, Integer> getStatusDistributionByDistricts(List<String> districts) {
        log.debug("Getting status distribution for districts: {}", districts);
        
        List<DevotionalStatus> statuses = devotionalStatusRepository.findAll();
        Map<String, Integer> distribution = new HashMap<>();
        
        for (DevotionalStatus status : statuses) {
            long count = devoteeRepository.countByDevotionalStatusAndDistricts(status, districts);
            distribution.put(status.getName(), (int) count);
        }
        
        return distribution;
    }
    
    /**
     * Get status distribution as list format (for /status-distribution endpoint)
     */
    public List<StatusDistributionDto> getStatusDistributionList(List<String> allowedDistricts) {
        log.debug("Getting status distribution list for districts: {}", allowedDistricts);
        
        Map<String, Integer> distribution;
        long totalDevotees;
        
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            distribution = getStatusDistributionByDistricts(allowedDistricts);
            totalDevotees = getDevoteeCountByDistricts(allowedDistricts);
        } else {
            distribution = getStatusDistribution();
            totalDevotees = devoteeRepository.count();
        }
        
        return distribution.entrySet().stream()
                .map(entry -> StatusDistributionDto.builder()
                    .status(entry.getKey())
                    .count(entry.getValue())
                    .label(entry.getKey() + " (" + entry.getValue() + ")")
                    .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get devotee count for specific districts
     */
    private long getDevoteeCountByDistricts(List<String> districts) {
        return devoteeRepository.countByDistricts(districts);
    }
    
    /**
     * Get namhatta count for specific districts
     */
    private long getNamhattaCountByDistricts(List<String> districts) {
        return namhattaRepository.countByDistricts(districts);
    }
    
    /**
     * Get shraddhakutir count for specific districts
     */
    private long getShraddhakutirCountByDistricts(List<String> districts) {
        return shraddhakutirRepository.countByDistricts(districts);
    }
    
    /**
     * Get pending approvals count for specific districts
     */
    private long getPendingApprovalsCountByDistricts(List<String> districts) {
        return namhattaRepository.countByIsApprovedAndDistricts(false, districts);
    }
}