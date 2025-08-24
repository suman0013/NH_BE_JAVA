package com.namhatta.service;

import com.namhatta.dto.CreateDevoteeDto;
import com.namhatta.dto.DevoteeDto;
import com.namhatta.dto.UpdateDevoteeDto;
import com.namhatta.entity.Devotee;
import com.namhatta.mapper.DevoteeMapper;
import com.namhatta.repository.DevoteeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * DevoteeService implementing all devotee business logic
 * Maintains 100% compatibility with Node.js service behavior
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DevoteeService {
    
    private final DevoteeRepository devoteeRepository;
    private final DevoteeMapper devoteeMapper;
    
    /**
     * Get filtered devotees with pagination and sorting
     * Same behavior as Node.js getFilteredDevotees
     */
    @Transactional(readOnly = true)
    public Page<DevoteeDto> getFilteredDevotees(
            List<String> allowedDistricts,
            String status,
            String search,
            String sortBy,
            String sortOrder,
            int page,
            int size) {
        
        log.debug("Getting filtered devotees - districts: {}, status: {}, search: {}, sortBy: {}, sortOrder: {}, page: {}, size: {}",
                allowedDistricts, status, search, sortBy, sortOrder, page, size);
        
        // Create sort object
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        
        // Create pageable (Spring uses 0-based indexing)
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<Devotee> devotees;
        
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            // District supervisor - apply filtering
            if (search != null && !search.trim().isEmpty()) {
                if (status != null && !status.trim().isEmpty()) {
                    devotees = devoteeRepository.findByDistrictsAndStatusAndSearch(
                            allowedDistricts, status, search.trim(), pageable);
                } else {
                    devotees = devoteeRepository.findByDistrictsAndSearch(
                            allowedDistricts, search.trim(), pageable);
                }
            } else {
                if (status != null && !status.trim().isEmpty()) {
                    devotees = devoteeRepository.findByDistrictsAndStatus(
                            allowedDistricts, status, pageable);
                } else {
                    devotees = devoteeRepository.findByDistricts(allowedDistricts, pageable);
                }
            }
        } else {
            // Admin or Office - no district filtering
            if (search != null && !search.trim().isEmpty()) {
                if (status != null && !status.trim().isEmpty()) {
                    devotees = devoteeRepository.findByStatusAndSearch(status, search.trim(), pageable);
                } else {
                    devotees = devoteeRepository.findBySearch(search.trim(), pageable);
                }
            } else {
                if (status != null && !status.trim().isEmpty()) {
                    devotees = devoteeRepository.findByStatus(status, pageable);
                } else {
                    devotees = devoteeRepository.findAll(pageable);
                }
            }
        }
        
        log.debug("Found {} devotees (total: {})", devotees.getContent().size(), devotees.getTotalElements());
        
        // Convert to DTOs
        return devotees.map(devoteeMapper::toDto);
    }
    
    /**
     * Get devotee by ID with access control
     * Same behavior as Node.js getDevoteeById
     */
    @Transactional(readOnly = true)
    public DevoteeDto getDevoteeById(Long id, List<String> allowedDistricts) {
        
        log.debug("Getting devotee by ID: {}, allowed districts: {}", id, allowedDistricts);
        
        Optional<Devotee> devoteeOpt;
        
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            // District supervisor - check if devotee is in allowed districts
            devoteeOpt = devoteeRepository.findByIdAndDistricts(id, allowedDistricts);
        } else {
            // Admin or Office - no district filtering
            devoteeOpt = devoteeRepository.findById(id);
        }
        
        if (devoteeOpt.isPresent()) {
            DevoteeDto dto = devoteeMapper.toDto(devoteeOpt.get());
            log.debug("Found devotee: {} ({})", dto.getLegalName(), id);
            return dto;
        }
        
        log.debug("Devotee not found or access denied: {}", id);
        return null;
    }
    
    /**
     * Create new devotee
     * Same behavior as Node.js createDevotee
     */
    public DevoteeDto createDevotee(CreateDevoteeDto dto) {
        
        log.info("Creating devotee: {}", dto.getLegalName());
        
        try {
            // Convert DTO to entity
            Devotee devotee = devoteeMapper.toEntity(dto);
            
            // Save devotee
            Devotee savedDevotee = devoteeRepository.save(devotee);
            
            // Convert back to DTO
            DevoteeDto result = devoteeMapper.toDto(savedDevotee);
            
            log.info("Successfully created devotee: {} (ID: {})", result.getLegalName(), result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Error creating devotee: {}", dto.getLegalName(), e);
            throw new RuntimeException("Failed to create devotee: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update devotee
     * Same behavior as Node.js updateDevotee
     */
    public DevoteeDto updateDevotee(Long id, UpdateDevoteeDto dto, List<String> allowedDistricts) {
        
        log.info("Updating devotee ID: {}", id);
        
        try {
            // First get the existing devotee with access control
            Optional<Devotee> existingOpt;
            
            if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                existingOpt = devoteeRepository.findByIdAndDistricts(id, allowedDistricts);
            } else {
                existingOpt = devoteeRepository.findById(id);
            }
            
            if (existingOpt.isEmpty()) {
                log.warn("Devotee not found or access denied for update: {}", id);
                return null;
            }
            
            Devotee existing = existingOpt.get();
            
            // Update the entity with DTO data
            devoteeMapper.updateEntityFromDto(dto, existing);
            
            // Save updated devotee
            Devotee savedDevotee = devoteeRepository.save(existing);
            
            // Convert back to DTO
            DevoteeDto result = devoteeMapper.toDto(savedDevotee);
            
            log.info("Successfully updated devotee: {} (ID: {})", result.getLegalName(), id);
            return result;
            
        } catch (Exception e) {
            log.error("Error updating devotee ID: {}", id, e);
            throw new RuntimeException("Failed to update devotee: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete devotee
     * Same behavior as Node.js deleteDevotee
     */
    public boolean deleteDevotee(Long id) {
        
        log.info("Deleting devotee ID: {}", id);
        
        try {
            Optional<Devotee> devoteeOpt = devoteeRepository.findById(id);
            
            if (devoteeOpt.isEmpty()) {
                log.warn("Devotee not found for deletion: {}", id);
                return false;
            }
            
            devoteeRepository.deleteById(id);
            
            log.info("Successfully deleted devotee ID: {}", id);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting devotee ID: {}", id, e);
            throw new RuntimeException("Failed to delete devotee: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get devotees by namhatta with access control
     * Same behavior as Node.js getDevoteesByNamhatta
     */
    @Transactional(readOnly = true)
    public List<DevoteeDto> getDevoteesByNamhatta(Long namhattaId, List<String> allowedDistricts) {
        
        log.debug("Getting devotees for namhatta ID: {}, allowed districts: {}", namhattaId, allowedDistricts);
        
        List<Devotee> devotees;
        
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            // District supervisor - apply filtering
            devotees = devoteeRepository.findByNamhattaIdAndDistricts(namhattaId, allowedDistricts);
        } else {
            // Admin or Office - no district filtering
            devotees = devoteeRepository.findByNamhattaId(namhattaId);
        }
        
        log.debug("Found {} devotees for namhatta ID: {}", devotees.size(), namhattaId);
        
        // Convert to DTOs
        return devotees.stream()
                .map(devoteeMapper::toDto)
                .toList();
    }
}