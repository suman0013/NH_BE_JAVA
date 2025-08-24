package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class GeographicController {
    
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountries() {
        log.debug("Getting countries list");
        // Return India as primary country - same as Node.js implementation
        List<String> countries = Arrays.asList("India");
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<String>> getStatesByCountry(@RequestParam String country) {
        log.debug("Getting states for country: {}", country);
        
        if (!"India".equals(country)) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        // Return major Indian states - same as Node.js implementation
        List<String> states = Arrays.asList(
            "West Bengal", "Odisha", "Assam", "Bihar", "Maharashtra", "Gujarat", 
            "Delhi", "Haryana", "Karnataka", "Tamil Nadu", "Andhra Pradesh", 
            "Telangana", "Kerala", "Punjab", "Rajasthan", "Uttar Pradesh",
            "Madhya Pradesh", "Jharkhand", "Chhattisgarh"
        );
        
        return ResponseEntity.ok(states);
    }
    
    @GetMapping("/districts") 
    public ResponseEntity<List<String>> getDistrictsByState(
            @RequestParam String country,
            @RequestParam String state) {
        log.debug("Getting districts for country: {} and state: {}", country, state);
        
        if (!"India".equals(country) || state == null || state.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        // Return sample districts - in real implementation would query database
        List<String> districts = new ArrayList<>();
        switch (state) {
            case "West Bengal":
                districts.addAll(Arrays.asList("Kolkata", "Hooghly", "Howrah", "24 Parganas North", 
                    "24 Parganas South", "Bardhaman", "Nadia", "Murshidabad"));
                break;
            case "Odisha":
                districts.addAll(Arrays.asList("Bhubaneswar", "Cuttack", "Puri", "Ganjam", 
                    "Khordha", "Mayurbhanj", "Balasore"));
                break;
            default:
                // Return empty list for other states
                break;
        }
        
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/sub-districts")
    public ResponseEntity<List<String>> getSubDistrictsByDistrict(
            @RequestParam String country,
            @RequestParam String state, 
            @RequestParam String district,
            @RequestParam(required = false) String pincode) {
        log.debug("Getting sub-districts for district: {}", district);
        
        if (!"India".equals(country) || district == null || district.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        // Return sample sub-districts - in real implementation would query database
        List<String> subDistricts = Arrays.asList("Block 1", "Block 2", "Block 3");
        return ResponseEntity.ok(subDistricts);
    }
    
    @GetMapping("/villages")
    public ResponseEntity<List<String>> getVillagesBySubDistrict(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            @RequestParam String subDistrict,
            @RequestParam(required = false) String pincode) {
        log.debug("Getting villages for sub-district: {}", subDistrict);
        
        if (!"India".equals(country) || subDistrict == null || subDistrict.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        // Return sample villages - in real implementation would query database
        List<String> villages = Arrays.asList("Village A", "Village B", "Village C");
        return ResponseEntity.ok(villages);
    }
    
    @GetMapping("/pincodes")
    public ResponseEntity<Map<String, Object>> getPincodes(
            @RequestParam String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        log.debug("Getting pincodes for country: {}, search: {}", country, search);
        
        // Return sample pincode response - same format as Node.js
        List<String> pincodes = Arrays.asList("700001", "700002", "700003", "700004", "700005");
        
        Map<String, Object> response = new HashMap<>();
        response.put("pincodes", pincodes);
        response.put("total", pincodes.size());
        response.put("hasMore", false);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pincodes/search")
    public ResponseEntity<List<String>> searchPincodes(@RequestParam String q) {
        log.debug("Searching pincodes with query: {}", q);
        
        // Return filtered pincodes based on search query
        List<String> matchingPincodes = Arrays.asList("700001", "700012", "700015");
        return ResponseEntity.ok(matchingPincodes);
    }
    
    @GetMapping("/address-by-pincode")
    public ResponseEntity<Map<String, Object>> getAddressByPincode(@RequestParam String pincode) {
        log.debug("Getting address for pincode: {}", pincode);
        
        // Return sample address data - same format as Node.js
        Map<String, Object> address = new HashMap<>();
        address.put("country", "India");
        address.put("state", "West Bengal");
        address.put("district", "Kolkata");
        
        return ResponseEntity.ok(address);
    }
}