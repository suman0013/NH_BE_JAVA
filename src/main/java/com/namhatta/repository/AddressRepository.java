package com.namhatta.repository;

import com.namhatta.entity.Address;
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
@Tag(name = "Address Repository", description = "Data access operations for geographic address data")
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Find address by pincode
     */
    List<Address> findByPincode(String pincode);
    
    /**
     * Find addresses by district
     */
    Page<Address> findByDistrictNameEnglish(String districtName, Pageable pageable);
    
    /**
     * Find addresses by state
     */
    Page<Address> findByStateNameEnglish(String stateName, Pageable pageable);
    
    /**
     * Find addresses by country
     */
    Page<Address> findByCountry(String country, Pageable pageable);
    
    /**
     * Search addresses by pincode pattern
     */
    List<Address> findByPincodeStartingWith(String pincodePrefix);
    
    /**
     * Get all distinct countries
     */
    @Query("SELECT DISTINCT a.country FROM Address a WHERE a.country IS NOT NULL ORDER BY a.country")
    List<String> findDistinctCountries();
    
    /**
     * Get all distinct states for a country
     */
    @Query("SELECT DISTINCT a.stateNameEnglish FROM Address a WHERE a.country = :country AND a.stateNameEnglish IS NOT NULL ORDER BY a.stateNameEnglish")
    List<String> findDistinctStatesByCountry(@Param("country") String country);
    
    /**
     * Get all distinct districts for a state
     */
    @Query("SELECT DISTINCT a.districtNameEnglish FROM Address a WHERE a.stateNameEnglish = :state AND a.districtNameEnglish IS NOT NULL ORDER BY a.districtNameEnglish")
    List<String> findDistinctDistrictsByState(@Param("state") String state);
    
    /**
     * Get all distinct sub-districts for a district
     */
    @Query("SELECT DISTINCT a.subdistrictNameEnglish FROM Address a WHERE a.districtNameEnglish = :district AND a.subdistrictNameEnglish IS NOT NULL ORDER BY a.subdistrictNameEnglish")
    List<String> findDistinctSubdistrictsByDistrict(@Param("district") String district);
    
    /**
     * Get all distinct villages for a sub-district
     */
    @Query("SELECT DISTINCT a.villageNameEnglish FROM Address a WHERE a.subdistrictNameEnglish = :subdistrict AND a.villageNameEnglish IS NOT NULL ORDER BY a.villageNameEnglish")
    List<String> findDistinctVillagesBySubdistrict(@Param("subdistrict") String subdistrict);
    
    /**
     * Find exact address match for deduplication
     */
    Optional<Address> findByCountryAndStateNameEnglishAndDistrictNameEnglishAndSubdistrictNameEnglishAndVillageNameEnglishAndPincode(
        String country, String state, String district, String subdistrict, String village, String pincode);
    
    /**
     * Search addresses by location hierarchy
     */
    @Query("""
        SELECT a FROM Address a 
        WHERE (:country IS NULL OR LOWER(a.country) LIKE LOWER(CONCAT('%', :country, '%')))
        AND (:state IS NULL OR LOWER(a.stateNameEnglish) LIKE LOWER(CONCAT('%', :state, '%')))
        AND (:district IS NULL OR LOWER(a.districtNameEnglish) LIKE LOWER(CONCAT('%', :district, '%')))
        AND (:village IS NULL OR LOWER(a.villageNameEnglish) LIKE LOWER(CONCAT('%', :village, '%')))
        """)
    Page<Address> searchByLocationHierarchy(@Param("country") String country,
                                          @Param("state") String state,
                                          @Param("district") String district,
                                          @Param("village") String village,
                                          Pageable pageable);
}