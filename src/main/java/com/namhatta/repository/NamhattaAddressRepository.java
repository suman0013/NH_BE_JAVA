package com.namhatta.repository;

import com.namhatta.entity.Namhatta;
import com.namhatta.entity.NamhattaAddress;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for NamhattaAddress entity operations
 */
@Repository
@Tag(name = "Namhatta Address Repository", description = "Data access operations for namhatta address management")
public interface NamhattaAddressRepository extends JpaRepository<NamhattaAddress, Long> {
    
    /**
     * Find all addresses for a specific namhatta
     */
    List<NamhattaAddress> findByNamhatta(Namhatta namhatta);
    
    /**
     * Find address by namhatta and type
     */
    Optional<NamhattaAddress> findByNamhattaAndAddressType(Namhatta namhatta, String addressType);
    
    /**
     * Delete all addresses for a namhatta
     */
    void deleteByNamhatta(Namhatta namhatta);
}