package com.namhatta.service;

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
    public Map<String, Object> getDashboardStats(List<String> allowedDistricts) {
        log.debug("Getting dashboard statistics for districts: {}", allowedDistricts);
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total counts based on user access level
            if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                // District supervisor - filtered counts
                stats.put("totalDevotees", getDevoteeCountByDistricts(allowedDistricts));
                stats.put("totalNamhattas", getNamhattaCountByDistricts(allowedDistricts));
                stats.put("totalShraddhakutirs", getShraddhakutirCountByDistricts(allowedDistricts));
                stats.put("approvalsPending", getPendingApprovalsCountByDistricts(allowedDistricts));
                stats.put("statusDistribution", getStatusDistributionByDistricts(allowedDistricts));
            } else {
                // Admin/Office - all counts
                stats.put("totalDevotees", devoteeRepository.count());
                stats.put("totalNamhattas", namhattaRepository.count());
                stats.put("totalShraddhakutirs", shraddhakutirRepository.count());
                stats.put("approvalsPending", namhattaRepository.countByIsApproved(false));
                stats.put("statusDistribution", getStatusDistribution());
            }
            
            log.debug("Dashboard stats calculated successfully");
            return stats;
            
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
    public List<Map<String, Object>> getStatusDistributionList(List<String> allowedDistricts) {
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
                .map(entry -> {
                    Map<String, Object> statusInfo = new HashMap<>();
                    statusInfo.put("status", entry.getKey());
                    statusInfo.put("count", entry.getValue());
                    statusInfo.put("percentage", totalDevotees > 0 ? 
                        Math.round((entry.getValue() * 100.0 / totalDevotees) * 10.0) / 10.0 : 0.0);
                    return statusInfo;
                })
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