package com.namhatta.service;

import com.namhatta.dto.*;
import com.namhatta.entity.*;
import com.namhatta.mapper.NamhattaMapper;
import com.namhatta.repository.*;
import com.namhatta.security.UserDetailsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * NamhattaService - Business logic for namhatta management
 * Maintains 100% compatibility with Node.js implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NamhattaService {
    
    private final NamhattaRepository namhattaRepository;
    private final ShraddhakutirRepository shraddhakutirRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final NamhattaAddressRepository namhattaAddressRepository;
    private final DevoteeRepository devoteeRepository;
    private final NamhattaMapper namhattaMapper;
    
    /**
     * Get all namhattas with filtering, pagination, and district access control
     */
    @Transactional(readOnly = true)
    public PagedResponse<NamhattaDto> getAllNamhattas(int page, int size, String search, 
                                                      String country, String state, String district, 
                                                      String subDistrict, String village, String status,
                                                      String sortBy, String sortOrder, List<String> allowedDistricts) {
        log.debug("Fetching namhattas: page={}, size={}, search={}, status={}", page, size, search, status);
        
        // Create pageable with sorting
        Sort sort = createSort(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<Namhatta> namhattaPage;
        
        // Apply district filtering for supervisors
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            if (search != null && !search.trim().isEmpty()) {
                namhattaPage = namhattaRepository.searchNamhattasInDistricts(search.trim(), allowedDistricts, pageable);
            } else {
                namhattaPage = namhattaRepository.findActiveNamhattasInDistricts(allowedDistricts, pageable);
            }
        } else {
            // Admin/Office users see all namhattas
            if (search != null && !search.trim().isEmpty()) {
                namhattaPage = namhattaRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
            } else if ("PENDING_APPROVAL".equals(status)) {
                namhattaPage = namhattaRepository.findByIsApproved(false, pageable);
            } else {
                namhattaPage = namhattaRepository.findAll(pageable);
            }
        }
        
        List<NamhattaDto> namhattaDtos = namhattaPage.getContent().stream()
                .map(namhattaMapper::toDto)
                .toList();
        
        return PagedResponse.<NamhattaDto>builder()
                .data(namhattaDtos)
                .page(page)
                .size(size)
                .total((int) namhattaPage.getTotalElements())
                .totalPages(namhattaPage.getTotalPages())
                .build();
    }
    
    /**
     * Get pending approval namhattas (Admin/Office only)
     */
    @Transactional(readOnly = true)
    public List<NamhattaDto> getPendingApprovalNamhattas() {
        log.debug("Fetching pending approval namhattas");
        List<Namhatta> pendingNamhattas = namhattaRepository.findPendingApprovalNamhattas();
        return pendingNamhattas.stream()
                .map(namhattaMapper::toDto)
                .toList();
    }
    
    /**
     * Get namhatta by ID with access control
     */
    @Transactional(readOnly = true)
    public NamhattaDto getNamhattaById(Long id, List<String> allowedDistricts) {
        log.debug("Fetching namhatta by ID: {}", id);
        
        Namhatta namhatta = namhattaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found with id: " + id));
        
        // Check district access for supervisors
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            if (!hasDistrictAccess(namhatta, allowedDistricts)) {
                throw new AccessDeniedException("Access denied: Namhatta not in your assigned districts");
            }
        }
        
        return namhattaMapper.toDto(namhatta);
    }
    
    /**
     * Create new namhatta
     */
    public NamhattaDto createNamhatta(CreateNamhattaDto createDto) {
        log.info("Creating new namhatta: {}", createDto.getName());
        
        // Validate and create namhatta entity
        Namhatta namhatta = namhattaMapper.toEntity(createDto);
        
        // Set default approval status (requires admin approval)
        namhatta.setIsApproved(false);
        namhatta.setIsActive(true);
        
        // Save namhatta first
        Namhatta savedNamhatta = namhattaRepository.save(namhatta);
        log.debug("Saved namhatta with ID: {}", savedNamhatta.getId());
        
        // Handle address if provided
        if (createDto.getAddress() != null) {
            saveNamhattaAddress(savedNamhatta, createDto.getAddress());
        }
        
        return namhattaMapper.toDto(savedNamhatta);
    }
    
    /**
     * Update existing namhatta
     */
    public NamhattaDto updateNamhatta(Long id, UpdateNamhattaDto updateDto) {
        log.info("Updating namhatta: {}", id);
        
        Namhatta existingNamhatta = namhattaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found with id: " + id));
        
        // Update fields from DTO
        namhattaMapper.updateEntityFromDto(updateDto, existingNamhatta);
        
        // Save updated namhatta
        Namhatta updatedNamhatta = namhattaRepository.save(existingNamhatta);
        
        // Handle address update if provided
        if (updateDto.getAddress() != null) {
            saveNamhattaAddress(updatedNamhatta, updateDto.getAddress());
        }
        
        return namhattaMapper.toDto(updatedNamhatta);
    }
    
    /**
     * Check if namhatta code exists (for uniqueness validation)
     */
    @Transactional(readOnly = true)
    public boolean checkNamhattaCodeExists(String code) {
        // This method would check for a unique code field if it exists
        // For now, we'll use name as the unique identifier
        return namhattaRepository.findByNameContainingIgnoreCase(code, Pageable.unpaged())
                .getTotalElements() > 0;
    }
    
    /**
     * Approve namhatta (Admin only)
     */
    public void approveNamhatta(Long id) {
        log.info("Approving namhatta: {}", id);
        
        Namhatta namhatta = namhattaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found with id: " + id));
        
        namhatta.setIsApproved(true);
        namhattaRepository.save(namhatta);
        
        log.debug("Namhatta {} approved successfully", id);
    }
    
    /**
     * Reject namhatta (Admin only)
     */
    public void rejectNamhatta(Long id, String reason) {
        log.info("Rejecting namhatta: {} with reason: {}", id, reason);
        
        Namhatta namhatta = namhattaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found with id: " + id));
        
        // For rejection, we might want to mark as inactive or add rejection reason
        namhatta.setIsActive(false);
        namhattaRepository.save(namhatta);
        
        log.debug("Namhatta {} rejected successfully", id);
    }
    
    /**
     * Get devotees by namhatta
     */
    @Transactional(readOnly = true)
    public PagedResponse<DevoteeDto> getDevoteesByNamhatta(Long namhattaId, int page, int size, Long statusId, List<String> allowedDistricts) {
        log.debug("Fetching devotees for namhatta: {}, page: {}, size: {}", namhattaId, page, size);
        
        // Verify namhatta exists and user has access
        Namhatta namhatta = namhattaRepository.findById(namhattaId)
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found with id: " + namhattaId));
        
        // Check district access for supervisors
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            if (!hasDistrictAccess(namhatta, allowedDistricts)) {
                throw new AccessDeniedException("Access denied: Namhatta not in your assigned districts");
            }
        }
        
        // Get devotees for this namhatta using DevoteeRepository
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Devotee> devoteePage;
        
        if (statusId != null) {
            devoteePage = devoteeRepository.findByNamhattaAndDevotionalStatusId(namhatta, statusId, pageable);
        } else {
            devoteePage = devoteeRepository.findByNamhatta(namhatta, pageable);
        }
        
        List<DevoteeDto> devoteeDtos = devoteePage.getContent().stream()
                .map(devotee -> DevoteeDto.builder()
                    .id(devotee.getId())
                    .name(devotee.getName())
                    .gender(devotee.getGender())
                    .dateOfBirth(devotee.getDateOfBirth())
                    .contactNumber(devotee.getContactNumber())
                    .isActive(devotee.getIsActive())
                    .namhattaId(devotee.getNamhatta() != null ? devotee.getNamhatta().getId() : null)
                    .devotionalStatusId(devotee.getDevotionalStatus() != null ? devotee.getDevotionalStatus().getId() : null)
                    .devotionalStatusName(devotee.getDevotionalStatus() != null ? devotee.getDevotionalStatus().getName() : null)
                    .build())
                .toList();
        
        return PagedResponse.<DevoteeDto>builder()
                .data(devoteeDtos)
                .page(page)
                .size(size)
                .total((int) devoteePage.getTotalElements())
                .totalPages(devoteePage.getTotalPages())
                .build();
    }
    
    // Helper methods
    
    private Sort createSort(String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.by("createdAt").descending();
        }
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        return Sort.by(direction, sortBy);
    }
    
    private boolean hasDistrictAccess(Namhatta namhatta, List<String> allowedDistricts) {
        // Check if the namhatta is in any of the allowed districts
        try {
            // Get namhatta addresses and check districts
            List<NamhattaAddress> namhattaAddresses = namhatta.getAddresses();
            if (namhattaAddresses.isEmpty()) {
                return false; // No address means no district access
            }
            
            for (NamhattaAddress namhattaAddress : namhattaAddresses) {
                String district = namhattaAddress.getAddress().getDistrictNameEnglish();
                if (district != null && allowedDistricts.contains(district)) {
                    return true; // Found matching district
                }
            }
            
            return false; // No matching districts found
        } catch (Exception e) {
            log.warn("Error checking district access for namhatta {}: {}", namhatta.getId(), e.getMessage());
            return false; // Deny access on error
        }
    }
    
    private void saveNamhattaAddress(Namhatta namhatta, CreateAddressDto addressDto) {
        // Create and save address
        Address address = Address.builder()
                .country(addressDto.getCountry())
                .stateNameEnglish(addressDto.getStateNameEnglish())
                .districtNameEnglish(addressDto.getDistrictNameEnglish())
                .subdistrictNameEnglish(addressDto.getSubdistrictNameEnglish())
                .villageNameEnglish(addressDto.getVillageNameEnglish())
                .pincode(addressDto.getPincode())
                .build();
        
        Address savedAddress = addressRepository.save(address);
        
        // Create namhatta-address relationship
        NamhattaAddress namhattaAddress = NamhattaAddress.builder()
                .namhatta(namhatta)
                .address(savedAddress)
                .addressType("primary")
                .landmark(addressDto.getLandmark())
                .build();
        
        namhattaAddressRepository.save(namhattaAddress);
    }
}