package com.namhatta.controller;

import com.namhatta.service.GeographicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class GeographicController {
    
    private final GeographicService geographicService;
    
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountries() {
        log.debug("Getting countries list");
        
        try {
            List<String> countries = geographicService.getCountries();
            return ResponseEntity.ok(countries);
            
        } catch (Exception e) {
            log.error("Error retrieving countries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<String>> getStatesByCountry(@RequestParam String country) {
        log.debug("Getting states for country: {}", country);
        
        try {
            List<String> states = geographicService.getStatesByCountry(country);
            return ResponseEntity.ok(states);
            
        } catch (Exception e) {
            log.error("Error retrieving states for country: {}", country, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/districts") 
    public ResponseEntity<List<String>> getDistrictsByState(
            @RequestParam String country,
            @RequestParam String state) {
        log.debug("Getting districts for country: {} and state: {}", country, state);
        
        try {
            List<String> districts = geographicService.getDistrictsByState(country, state);
            return ResponseEntity.ok(districts);
            
        } catch (Exception e) {
            log.error("Error retrieving districts for state: {}", state, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/sub-districts")
    public ResponseEntity<List<String>> getSubDistrictsByDistrict(
            @RequestParam String country,
            @RequestParam String state, 
            @RequestParam String district,
            @RequestParam(required = false) String pincode) {
        log.debug("Getting sub-districts for district: {}", district);
        
        try {
            List<String> subDistricts = geographicService.getSubDistrictsByDistrict(country, state, district, pincode);
            return ResponseEntity.ok(subDistricts);
            
        } catch (Exception e) {
            log.error("Error retrieving sub-districts for district: {}", district, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/villages")
    public ResponseEntity<List<String>> getVillagesBySubDistrict(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            @RequestParam String subDistrict,
            @RequestParam(required = false) String pincode) {
        log.debug("Getting villages for sub-district: {}", subDistrict);
        
        try {
            List<String> villages = geographicService.getVillagesBySubDistrict(country, state, district, subDistrict, pincode);
            return ResponseEntity.ok(villages);
            
        } catch (Exception e) {
            log.error("Error retrieving villages for sub-district: {}", subDistrict, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/pincodes")
    public ResponseEntity<Map<String, Object>> getPincodes(
            @RequestParam String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        log.debug("Getting pincodes for country: {}, search: {}", country, search);
        
        try {
            Map<String, Object> response = geographicService.getPincodes(country, page, limit, search);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving pincodes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve pincodes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/pincodes/search")
    public ResponseEntity<List<String>> searchPincodes(@RequestParam String q) {
        log.debug("Searching pincodes with query: {}", q);
        
        try {
            List<String> matchingPincodes = geographicService.searchPincodes(q);
            return ResponseEntity.ok(matchingPincodes);
            
        } catch (Exception e) {
            log.error("Error searching pincodes with query: {}", q, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/address-by-pincode")
    public ResponseEntity<Map<String, Object>> getAddressByPincode(@RequestParam String pincode) {
        log.debug("Getting address for pincode: {}", pincode);
        
        try {
            Map<String, Object> address = geographicService.getAddressByPincode(pincode);
            return ResponseEntity.ok(address);
            
        } catch (Exception e) {
            log.error("Error retrieving address for pincode: {}", pincode, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve address");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}