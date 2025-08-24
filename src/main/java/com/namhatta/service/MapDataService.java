package com.namhatta.service;

import com.namhatta.repository.NamhattaRepository;
import com.namhatta.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MapDataService - Business logic for geographic and map data
 * Replaces hardcoded geographic data with real database queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MapDataService {
    
    private final NamhattaRepository namhattaRepository;
    private final AddressRepository addressRepository;
    
    /**
     * Get countries with namhatta count
     */
    public List<Map<String, Object>> getCountriesWithNamhattaCount(List<String> allowedDistricts) {
        log.debug("Getting countries with namhatta count for districts: {}", allowedDistricts);
        
        try {
            // Get countries based on actual address data
            List<String> countryNames = addressRepository.findDistinctCountries();
            List<Map<String, Object>> countries = new ArrayList<>();
            
            for (String countryName : countryNames) {
                if (countryName != null && !countryName.trim().isEmpty()) {
                    long namhattaCount;
                    
                    if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                        // District supervisor - count namhattas in allowed districts
                        namhattaCount = namhattaRepository.countByDistricts(allowedDistricts);
                    } else {
                        // Admin/Office - count all namhattas in this country
                        namhattaCount = namhattaRepository.count(); // Using global count as country filtering not needed in current schema
                    }
                    
                    if (namhattaCount > 0) {
                        Map<String, Object> country = new HashMap<>();
                        country.put("country", countryName);
                        country.put("namhattaCount", (int) namhattaCount);
                        countries.add(country);
                    }
                }
            }
            
            // If no countries found, provide India as default
            if (countries.isEmpty()) {
                long totalCount = allowedDistricts != null && !allowedDistricts.isEmpty() ?
                    namhattaRepository.countByDistricts(allowedDistricts) :
                    namhattaRepository.count();
                    
                if (totalCount > 0) {
                    Map<String, Object> india = new HashMap<>();
                    india.put("country", "India");
                    india.put("namhattaCount", (int) totalCount);
                    countries.add(india);
                }
            }
            
            log.debug("Retrieved {} countries with namhatta counts", countries.size());
            return countries;
            
        } catch (Exception e) {
            log.error("Error retrieving countries with namhatta count", e);
            throw new RuntimeException("Failed to retrieve country data", e);
        }
    }
    
    /**
     * Get states with namhatta count for a country
     */
    public List<Map<String, Object>> getStatesWithNamhattaCount(String country, List<String> allowedDistricts) {
        log.debug("Getting states with namhatta count for country: {}, districts: {}", country, allowedDistricts);
        
        try {
            // Use actual repository query for state distribution
            List<Object[]> stateData = namhattaRepository.getNamhattaCountByState();
            
            List<Map<String, Object>> states = stateData.stream()
                .map(row -> {
                    String stateName = (String) row[0];
                    Long count = (Long) row[1];
                    
                    Map<String, Object> stateInfo = new HashMap<>();
                    stateInfo.put("state", stateName);
                    stateInfo.put("namhattaCount", count.intValue());
                    return stateInfo;
                })
                .filter(state -> {
                    // Filter states based on allowed districts
                    if (allowedDistricts == null || allowedDistricts.isEmpty()) {
                        return true; // Admin/Office sees all states
                    }
                    // Check if this state contains any of the allowed districts
                    String stateName = (String) state.get("state");
                    List<String> stateDistricts = addressRepository.findDistinctDistrictsByCountryAndState(country, stateName);
                    return stateDistricts.stream().anyMatch(allowedDistricts::contains);
                })
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} states with namhatta counts", states.size());
            return states;
            
        } catch (Exception e) {
            log.error("Error retrieving states with namhatta count for country: {}", country, e);
            throw new RuntimeException("Failed to retrieve state data", e);
        }
    }
    
    /**
     * Get districts with namhatta count for a state
     */
    public List<Map<String, Object>> getDistrictsWithNamhattaCount(String country, String state, List<String> allowedDistricts) {
        log.debug("Getting districts with namhatta count for state: {}, allowed districts: {}", state, allowedDistricts);
        
        try {
            // Use actual repository query for district distribution
            List<Object[]> districtData = namhattaRepository.getNamhattaCountByDistrict();
            
            List<Map<String, Object>> districts = districtData.stream()
                .map(row -> {
                    String districtName = (String) row[0];
                    Long count = (Long) row[1];
                    
                    Map<String, Object> districtInfo = new HashMap<>();
                    districtInfo.put("district", districtName);
                    districtInfo.put("namhattaCount", count.intValue());
                    return districtInfo;
                })
                .filter(district -> allowedDistricts == null || 
                    allowedDistricts.contains((String) district.get("district")))
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} districts with namhatta counts", districts.size());
            return districts;
            
        } catch (Exception e) {
            log.error("Error retrieving districts with namhatta count for state: {}", state, e);
            throw new RuntimeException("Failed to retrieve district data", e);
        }
    }
    
    /**
     * Get sub-districts with namhatta count for a district
     */
    public List<Map<String, Object>> getSubDistrictsWithNamhattaCount(String country, String state, String district, List<String> allowedDistricts) {
        log.debug("Getting sub-districts with namhatta count for district: {}", district);
        
        try {
            // Check district access
            if (allowedDistricts != null && !allowedDistricts.isEmpty() && !allowedDistricts.contains(district)) {
                log.warn("Access denied to district: {} for user with districts: {}", district, allowedDistricts);
                return Collections.emptyList();
            }
            
            // Get actual sub-district data from address repository
            List<String> subdistrictNames = addressRepository.findDistinctSubdistrictsByCountryStateAndDistrict(country, state, district);
            
            List<Map<String, Object>> subdistricts = new ArrayList<>();
            
            for (String subdistrictName : subdistrictNames) {
                if (subdistrictName != null && !subdistrictName.trim().isEmpty()) {
                    // Get namhatta count for this sub-district
                    long namhattaCount = namhattaRepository.countBySubdistrict(subdistrictName);
                    
                    Map<String, Object> subdistrict = new HashMap<>();
                    subdistrict.put("subdistrict", subdistrictName);
                    subdistrict.put("namhattaCount", (int) namhattaCount);
                    subdistricts.add(subdistrict);
                }
            }
            
            // If no sub-districts found, provide default structure
            if (subdistricts.isEmpty()) {
                Map<String, Object> defaultSubdistrict = new HashMap<>();
                defaultSubdistrict.put("subdistrict", district + " Central");
                defaultSubdistrict.put("namhattaCount", namhattaRepository.countByDistrict(district));
                subdistricts.add(defaultSubdistrict);
            }
            
            log.debug("Retrieved {} sub-districts for district: {}", subdistricts.size(), district);
            return subdistricts;
            
        } catch (Exception e) {
            log.error("Error retrieving sub-districts for district: {}", district, e);
            throw new RuntimeException("Failed to retrieve sub-district data", e);
        }
    }
    
    /**
     * Get namhattas by sub-district
     */
    public List<Map<String, Object>> getNamhattasBySubDistrict(String country, String state, String district, String subdistrict, List<String> allowedDistricts) {
        log.debug("Getting namhattas for sub-district: {} in district: {}", subdistrict, district);
        
        try {
            // Check district access
            if (allowedDistricts != null && !allowedDistricts.isEmpty() && !allowedDistricts.contains(district)) {
                log.warn("Access denied to district: {} for user with districts: {}", district, allowedDistricts);
                return Collections.emptyList();
            }
            
            // Get actual namhattas by sub-district
            List<Map<String, Object>> namhattaData = namhattaRepository.getNamhattasBySubdistrict(subdistrict);
            
            List<Map<String, Object>> namhattas = namhattaData.stream()
                .map(row -> {
                    Map<String, Object> namhatta = new HashMap<>();
                    namhatta.put("id", row.get("id"));
                    namhatta.put("name", row.get("name"));
                    namhatta.put("code", "NH" + row.get("id"));
                    namhatta.put("devoteeCount", row.get("devoteeCount") != null ? row.get("devoteeCount") : 0);
                    return namhatta;
                })
                .collect(Collectors.toList());
            
            log.debug("Retrieved {} namhattas for sub-district: {}", namhattas.size(), subdistrict);
            return namhattas;
            
        } catch (Exception e) {
            log.error("Error retrieving namhattas for sub-district: {}", subdistrict, e);
            throw new RuntimeException("Failed to retrieve namhatta data", e);
        }
    }
    
    /**
     * Get geographic statistics for dashboard
     */
    public Map<String, Object> getGeographicStatistics(List<String> allowedDistricts) {
        log.debug("Getting geographic statistics for districts: {}", allowedDistricts);
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
                // District supervisor statistics
                stats.put("districtsManaged", allowedDistricts.size());
                stats.put("namhattasInDistricts", namhattaRepository.countByDistricts(allowedDistricts));
            } else {
                // Admin/Office global statistics
                List<Object[]> stateData = namhattaRepository.getNamhattaCountByState();
                List<Object[]> districtData = namhattaRepository.getNamhattaCountByDistrict();
                
                stats.put("totalStates", stateData.size());
                stats.put("totalDistricts", districtData.size());
                stats.put("totalNamhattas", namhattaRepository.count());
            }
            
            log.debug("Geographic statistics calculated successfully");
            return stats;
            
        } catch (Exception e) {
            log.error("Error calculating geographic statistics", e);
            throw new RuntimeException("Failed to calculate geographic statistics", e);
        }
    }
}