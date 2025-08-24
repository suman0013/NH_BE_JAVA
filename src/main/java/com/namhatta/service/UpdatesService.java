package com.namhatta.service;

import com.namhatta.entity.Update;
import com.namhatta.entity.Namhatta;
import com.namhatta.repository.UpdateRepository;
import com.namhatta.repository.NamhattaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UpdatesService - Business logic for program updates management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdatesService {
    
    private final UpdateRepository updateRepository;
    private final NamhattaRepository namhattaRepository;
    
    /**
     * Get updates with pagination and filtering
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUpdates(Long namhattaId, int page, int size, List<String> allowedDistricts) {
        log.debug("Getting updates - namhattaId: {}, page: {}, size: {}, districts: {}", 
                namhattaId, page, size, allowedDistricts);
        
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Update> updatesPage;
            
            if (namhattaId != null) {
                // Get updates for specific namhatta
                updatesPage = updateRepository.findByNamhattaId(namhattaId, pageable);
            } else if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                // District supervisor - filter by allowed districts
                updatesPage = updateRepository.findUpdatesByDistricts(allowedDistricts, pageable);
            } else {
                // Admin/Office - get all updates
                updatesPage = updateRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
            
            List<Map<String, Object>> updates = updatesPage.getContent().stream()
                .map(this::convertUpdateToMap)
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} updates from database", updates.size());
            return updates;
            
        } catch (Exception e) {
            log.error("Error retrieving updates", e);
            throw new RuntimeException("Failed to retrieve updates", e);
        }
    }
    
    /**
     * Create new program update
     */
    public Map<String, Object> createUpdate(Map<String, Object> updateData) {
        log.debug("Creating new update for namhatta: {}", updateData.get("namhattaId"));
        
        try {
            // Validate required fields
            if (!updateData.containsKey("namhattaId") || updateData.get("namhattaId") == null) {
                throw new IllegalArgumentException("Namhatta ID is required");
            }
            if (!updateData.containsKey("programType") || updateData.get("programType") == null) {
                throw new IllegalArgumentException("Program type is required");
            }
            if (!updateData.containsKey("date") || updateData.get("date") == null) {
                throw new IllegalArgumentException("Date is required");
            }
            if (!updateData.containsKey("attendance") || updateData.get("attendance") == null) {
                throw new IllegalArgumentException("Attendance is required");
            }
            
            // Validate namhatta exists
            Long namhattaId = Long.valueOf(updateData.get("namhattaId").toString());
            Namhatta namhatta = namhattaRepository.findById(namhattaId)
                .orElseThrow(() -> new IllegalArgumentException("Namhatta not found with ID: " + namhattaId));
            
            // Create Update entity
            Update update = Update.builder()
                .namhatta(namhatta)
                .programType(updateData.get("programType").toString())
                .date(updateData.get("date").toString())
                .attendance(Integer.valueOf(updateData.get("attendance").toString()))
                .prasadDistribution(updateData.containsKey("prasadDistribution") ? 
                    Integer.valueOf(updateData.get("prasadDistribution").toString()) : null)
                .nagarKirtan(updateData.containsKey("nagarKirtan") ? 
                    Integer.valueOf(updateData.get("nagarKirtan").toString()) : 0)
                .bookDistribution(updateData.containsKey("bookDistribution") ? 
                    Integer.valueOf(updateData.get("bookDistribution").toString()) : 0)
                .chanting(updateData.containsKey("chanting") ? 
                    Integer.valueOf(updateData.get("chanting").toString()) : 0)
                .arati(updateData.containsKey("arati") ? 
                    Integer.valueOf(updateData.get("arati").toString()) : 0)
                .bhagwatPath(updateData.containsKey("bhagwatPath") ? 
                    Integer.valueOf(updateData.get("bhagwatPath").toString()) : 0)
                .imageUrls(updateData.containsKey("imageUrls") ? 
                    updateData.get("imageUrls").toString() : null)
                .facebookLink(updateData.containsKey("facebookLink") ? 
                    updateData.get("facebookLink").toString() : null)
                .youtubeLink(updateData.containsKey("youtubeLink") ? 
                    updateData.get("youtubeLink").toString() : null)
                .specialAttraction(updateData.containsKey("specialAttraction") ? 
                    updateData.get("specialAttraction").toString() : null)
                .build();
            
            Update savedUpdate = updateRepository.save(update);
            
            log.info("Update created successfully: {} for namhatta {}", savedUpdate.getId(), namhattaId);
            return convertUpdateToMap(savedUpdate);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid update data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating update", e);
            throw new RuntimeException("Failed to create update", e);
        }
    }
    
    /**
     * Update existing program update
     */
    public Map<String, Object> updateUpdate(Long id, Map<String, Object> updateData) {
        log.debug("Updating update: {}", id);
        
        try {
            // Find existing update
            Update existingUpdate = updateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Update not found with ID: " + id));
            
            // Update fields
            if (updateData.containsKey("programType") && updateData.get("programType") != null) {
                existingUpdate.setProgramType(updateData.get("programType").toString());
            }
            if (updateData.containsKey("date") && updateData.get("date") != null) {
                existingUpdate.setDate(updateData.get("date").toString());
            }
            if (updateData.containsKey("attendance") && updateData.get("attendance") != null) {
                existingUpdate.setAttendance(Integer.valueOf(updateData.get("attendance").toString()));
            }
            if (updateData.containsKey("prasadDistribution")) {
                existingUpdate.setPrasadDistribution(updateData.get("prasadDistribution") != null ? 
                    Integer.valueOf(updateData.get("prasadDistribution").toString()) : null);
            }
            if (updateData.containsKey("nagarKirtan")) {
                existingUpdate.setNagarKirtan(updateData.get("nagarKirtan") != null ? 
                    Integer.valueOf(updateData.get("nagarKirtan").toString()) : 0);
            }
            if (updateData.containsKey("bookDistribution")) {
                existingUpdate.setBookDistribution(updateData.get("bookDistribution") != null ? 
                    Integer.valueOf(updateData.get("bookDistribution").toString()) : 0);
            }
            if (updateData.containsKey("chanting")) {
                existingUpdate.setChanting(updateData.get("chanting") != null ? 
                    Integer.valueOf(updateData.get("chanting").toString()) : 0);
            }
            if (updateData.containsKey("arati")) {
                existingUpdate.setArati(updateData.get("arati") != null ? 
                    Integer.valueOf(updateData.get("arati").toString()) : 0);
            }
            if (updateData.containsKey("bhagwatPath")) {
                existingUpdate.setBhagwatPath(updateData.get("bhagwatPath") != null ? 
                    Integer.valueOf(updateData.get("bhagwatPath").toString()) : 0);
            }
            if (updateData.containsKey("imageUrls")) {
                existingUpdate.setImageUrls(updateData.get("imageUrls") != null ? 
                    updateData.get("imageUrls").toString() : null);
            }
            if (updateData.containsKey("facebookLink")) {
                existingUpdate.setFacebookLink(updateData.get("facebookLink") != null ? 
                    updateData.get("facebookLink").toString() : null);
            }
            if (updateData.containsKey("youtubeLink")) {
                existingUpdate.setYoutubeLink(updateData.get("youtubeLink") != null ? 
                    updateData.get("youtubeLink").toString() : null);
            }
            if (updateData.containsKey("specialAttraction")) {
                existingUpdate.setSpecialAttraction(updateData.get("specialAttraction") != null ? 
                    updateData.get("specialAttraction").toString() : null);
            }
            
            Update savedUpdate = updateRepository.save(existingUpdate);
            
            log.info("Update modified successfully: {}", id);
            return convertUpdateToMap(savedUpdate);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid update data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating update: {}", id, e);
            throw new RuntimeException("Failed to update update", e);
        }
    }
    
    /**
     * Delete program update
     */
    public void deleteUpdate(Long id) {
        log.debug("Deleting update: {}", id);
        
        try {
            if (!updateRepository.existsById(id)) {
                throw new IllegalArgumentException("Update not found with ID: " + id);
            }
            
            updateRepository.deleteById(id);
            
            log.info("Update deleted successfully: {}", id);
            
        } catch (Exception e) {
            log.error("Error deleting update: {}", id, e);
            throw new RuntimeException("Failed to delete update", e);
        }
    }
    
    /**
     * Convert Update entity to Map for API response
     */
    private Map<String, Object> convertUpdateToMap(Update update) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", update.getId());
        updateMap.put("namhattaId", update.getNamhatta().getId());
        updateMap.put("namhattaName", update.getNamhatta().getName());
        updateMap.put("programType", update.getProgramType());
        updateMap.put("date", update.getDate());
        updateMap.put("attendance", update.getAttendance());
        updateMap.put("prasadDistribution", update.getPrasadDistribution());
        updateMap.put("nagarKirtan", update.getNagarKirtan());
        updateMap.put("bookDistribution", update.getBookDistribution());
        updateMap.put("chanting", update.getChanting());
        updateMap.put("arati", update.getArati());
        updateMap.put("bhagwatPath", update.getBhagwatPath());
        updateMap.put("imageUrls", update.getImageUrls());
        updateMap.put("facebookLink", update.getFacebookLink());
        updateMap.put("youtubeLink", update.getYoutubeLink());
        updateMap.put("specialAttraction", update.getSpecialAttraction());
        updateMap.put("createdAt", update.getCreatedAt().toString());
        return updateMap;
    }
}