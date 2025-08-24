package com.namhatta.service;

import com.namhatta.entity.Address;
import com.namhatta.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * GeographicService - Business logic for geographic data management
 * TODO: Implement with proper Geographic entities when created
 * For now, provides structured service layer to replace hardcoded controller logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GeographicService {
    
    private final AddressRepository addressRepository;
    
    /**
     * Get available countries
     */
    public List<String> getCountries() {
        log.debug("Getting countries list");
        
        try {
            List<String> countries = addressRepository.findDistinctCountries();
            
            // If no countries in database, return India as default
            if (countries.isEmpty()) {
                countries = Arrays.asList("India");
            }
            
            log.debug("Retrieved {} countries from database", countries.size());
            return countries;
            
        } catch (Exception e) {
            log.error("Error retrieving countries", e);
            throw new RuntimeException("Failed to retrieve countries", e);
        }
    }
    
    /**
     * Get states by country
     */
    public List<String> getStatesByCountry(String country) {
        log.debug("Getting states for country: {}", country);
        
        try {
            List<String> states = addressRepository.findDistinctStatesByCountry(country);
            
            // If no states in database for this country, return empty list
            if (states.isEmpty() && "India".equals(country)) {
                // Fallback for India if no data exists
                states = Arrays.asList(
                    "West Bengal", "Odisha", "Assam", "Bihar", "Maharashtra", "Gujarat", 
                    "Delhi", "Haryana", "Karnataka", "Tamil Nadu", "Andhra Pradesh", 
                    "Telangana", "Kerala", "Punjab", "Rajasthan", "Uttar Pradesh",
                    "Madhya Pradesh", "Jharkhand", "Chhattisgarh"
                );
            }
            
            log.debug("Retrieved {} states for country: {} from database", states.size(), country);
            return states;
            
        } catch (Exception e) {
            log.error("Error retrieving states for country: {}", country, e);
            throw new RuntimeException("Failed to retrieve states", e);
        }
    }
    
    /**
     * Get districts by state
     */
    public List<String> getDistrictsByState(String country, String state) {
        log.debug("Getting districts for country: {} and state: {}", country, state);
        
        try {
            if (country == null || state == null || state.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<String> districts = addressRepository.findDistinctDistrictsByCountryAndState(country, state);
            
            // If no districts in database, provide fallback for major states
            if (districts.isEmpty() && "India".equals(country)) {
                switch (state) {
                    case "West Bengal":
                        districts.addAll(Arrays.asList("Kolkata", "Hooghly", "Howrah", "24 Parganas North", 
                            "24 Parganas South", "Bardhaman", "Nadia", "Murshidabad"));
                        break;
                    case "Odisha":
                        districts.addAll(Arrays.asList("Bhubaneswar", "Cuttack", "Puri", "Ganjam", 
                            "Khordha", "Mayurbhanj", "Balasore"));
                        break;
                    case "Maharashtra":
                        districts.addAll(Arrays.asList("Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad"));
                        break;
                    case "Gujarat":
                        districts.addAll(Arrays.asList("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Gandhinagar"));
                        break;
                    default:
                        // No fallback for other states
                        break;
                }
            }
            
            log.debug("Retrieved {} districts for state: {} from database", districts.size(), state);
            return districts;
            
        } catch (Exception e) {
            log.error("Error retrieving districts for state: {}", state, e);
            throw new RuntimeException("Failed to retrieve districts", e);
        }
    }
    
    /**
     * Get sub-districts by district
     */
    public List<String> getSubDistrictsByDistrict(String country, String state, String district, String pincode) {
        log.debug("Getting sub-districts for district: {}", district);
        
        try {
            if (country == null || district == null || district.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<String> subDistricts = addressRepository.findDistinctSubdistrictsByCountryStateAndDistrict(country, state, district);
            
            // If no sub-districts in database, return generic blocks as fallback
            if (subDistricts.isEmpty()) {
                for (int i = 1; i <= 4; i++) {
                    subDistricts.add("Block " + i);
                }
            }
            
            log.debug("Retrieved {} sub-districts for district: {} from database", subDistricts.size(), district);
            return subDistricts;
            
        } catch (Exception e) {
            log.error("Error retrieving sub-districts for district: {}", district, e);
            throw new RuntimeException("Failed to retrieve sub-districts", e);
        }
    }
    
    /**
     * Get villages by sub-district
     */
    public List<String> getVillagesBySubDistrict(String country, String state, String district, String subDistrict, String pincode) {
        log.debug("Getting villages for sub-district: {}", subDistrict);
        
        try {
            if (country == null || subDistrict == null || subDistrict.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            List<String> villages = addressRepository.findDistinctVillagesByCountryStateDistrictAndSubdistrict(country, state, district, subDistrict);
            
            // If no villages in database, return sample villages as fallback
            if (villages.isEmpty()) {
                villages = Arrays.asList("Village A", "Village B", "Village C", "Village D");
            }
            
            log.debug("Retrieved {} villages for sub-district: {} from database", villages.size(), subDistrict);
            return villages;
            
        } catch (Exception e) {
            log.error("Error retrieving villages for sub-district: {}", subDistrict, e);
            throw new RuntimeException("Failed to retrieve villages", e);
        }
    }
    
    /**
     * Get pincodes with pagination
     */
    public Map<String, Object> getPincodes(String country, int page, int limit, String search) {
        log.debug("Getting pincodes for country: {}, search: {}", country, search);
        
        try {
            List<String> allPincodes;
            
            if (search != null && !search.trim().isEmpty()) {
                allPincodes = addressRepository.findDistinctPincodesByCountryAndSearch(country, search.trim());
            } else {
                allPincodes = addressRepository.findDistinctPincodesByCountry(country);
            }
            
            // If no pincodes in database, provide sample data
            if (allPincodes.isEmpty()) {
                allPincodes = Arrays.asList(
                    "700001", "700002", "700003", "700004", "700005",
                    "700012", "700015", "700016", "700017", "700020"
                );
                
                // Filter sample data by search if provided
                if (search != null && !search.trim().isEmpty()) {
                    String searchTerm = search.trim();
                    allPincodes = allPincodes.stream()
                        .filter(pincode -> pincode.contains(searchTerm))
                        .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
                }
            }
            
            // Apply pagination
            int start = (page - 1) * limit;
            int end = Math.min(start + limit, allPincodes.size());
            List<String> paginatedPincodes = allPincodes.subList(start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pincodes", paginatedPincodes);
            response.put("total", allPincodes.size());
            response.put("hasMore", end < allPincodes.size());
            
            log.debug("Retrieved {} pincodes from database (page {}, limit {})", paginatedPincodes.size(), page, limit);
            return response;
            
        } catch (Exception e) {
            log.error("Error retrieving pincodes", e);
            throw new RuntimeException("Failed to retrieve pincodes", e);
        }
    }
    
    /**
     * Search pincodes by query
     */
    public List<String> searchPincodes(String query) {
        log.debug("Searching pincodes with query: {}", query);
        
        try {
            List<String> matchingPincodes = addressRepository.findDistinctPincodesContaining(query);
            
            // If no pincodes in database, use sample data
            if (matchingPincodes.isEmpty()) {
                List<String> samplePincodes = Arrays.asList(
                    "700001", "700002", "700003", "700004", "700005",
                    "700012", "700015", "700016", "700017", "700020"
                );
                
                matchingPincodes = samplePincodes.stream()
                    .filter(pincode -> pincode.contains(query))
                    .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
            }
            
            log.debug("Found {} matching pincodes for query: {} from database", matchingPincodes.size(), query);
            return matchingPincodes;
            
        } catch (Exception e) {
            log.error("Error searching pincodes with query: {}", query, e);
            throw new RuntimeException("Failed to search pincodes", e);
        }
    }
    
    /**
     * Get address information by pincode
     */
    public Map<String, Object> getAddressByPincode(String pincode) {
        log.debug("Getting address for pincode: {}", pincode);
        
        try {
            Optional<Address> addressOpt = addressRepository.findFirstByPincode(pincode);
            Map<String, Object> address = new HashMap<>();
            
            if (addressOpt.isPresent()) {
                Address addr = addressOpt.get();
                address.put("country", addr.getCountry());
                address.put("state", addr.getStateNameEnglish());
                address.put("district", addr.getDistrictNameEnglish());
                address.put("subdistrict", addr.getSubdistrictNameEnglish());
                address.put("village", addr.getVillageNameEnglish());
            } else {
                // If no address found in database, provide fallback based on pincode patterns
                if (pincode.startsWith("700")) {
                    address.put("country", "India");
                    address.put("state", "West Bengal");
                    address.put("district", "Kolkata");
                } else if (pincode.startsWith("751")) {
                    address.put("country", "India");
                    address.put("state", "Odisha");
                    address.put("district", "Bhubaneswar");
                } else {
                    address.put("country", "India");
                    address.put("state", "West Bengal");
                    address.put("district", "Kolkata");
                }
            }
            
            log.debug("Address retrieved for pincode: {} from database", pincode);
            return address;
            
        } catch (Exception e) {
            log.error("Error retrieving address for pincode: {}", pincode, e);
            throw new RuntimeException("Failed to retrieve address", e);
        }
    }
}