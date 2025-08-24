package com.namhatta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class MapDataController {
    
    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountriesWithNamhattaCount(HttpServletRequest request) {
        log.debug("Getting countries with namhatta count");
        
        // Get user constraints for district filtering
        String userRole = (String) request.getAttribute("userRole");
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        
        // Apply district filtering for supervisors
        if ("DISTRICT_SUPERVISOR".equals(userRole) && allowedDistricts != null) {
            log.debug("Filtering map data for supervisor districts: {}", allowedDistricts);
        }
        
        // Return sample country data with namhatta counts
        List<Map<String, Object>> countries = new ArrayList<>();
        
        Map<String, Object> india = new HashMap<>();
        india.put("country", "India");
        india.put("namhattaCount", 85);
        countries.add(india);
        
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<Map<String, Object>>> getStatesWithNamhattaCount(
            @RequestParam String country,
            HttpServletRequest request) {
        log.debug("Getting states with namhatta count for country: {}", country);
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        
        // Return sample state data with namhatta counts
        List<Map<String, Object>> states = new ArrayList<>();
        
        if ("India".equals(country)) {
            Map<String, Object> wb = new HashMap<>();
            wb.put("state", "West Bengal");
            wb.put("namhattaCount", 45);
            states.add(wb);
            
            Map<String, Object> odisha = new HashMap<>();
            odisha.put("state", "Odisha");
            odisha.put("namhattaCount", 25);
            states.add(odisha);
            
            Map<String, Object> assam = new HashMap<>();
            assam.put("state", "Assam");
            assam.put("namhattaCount", 15);
            states.add(assam);
        }
        
        return ResponseEntity.ok(states);
    }
    
    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            HttpServletRequest request) {
        log.debug("Getting districts with namhatta count for state: {}", state);
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        
        // Return sample district data with namhatta counts
        List<Map<String, Object>> districts = new ArrayList<>();
        
        if ("West Bengal".equals(state)) {
            Map<String, Object> kolkata = new HashMap<>();
            kolkata.put("district", "Kolkata");
            kolkata.put("namhattaCount", 12);
            districts.add(kolkata);
            
            Map<String, Object> hooghly = new HashMap<>();
            hooghly.put("district", "Hooghly");
            hooghly.put("namhattaCount", 18);
            districts.add(hooghly);
            
            Map<String, Object> howrah = new HashMap<>();
            howrah.put("district", "Howrah");
            howrah.put("namhattaCount", 15);
            districts.add(howrah);
        }
        
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/subdistricts")
    public ResponseEntity<List<Map<String, Object>>> getSubDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            HttpServletRequest request) {
        log.debug("Getting sub-districts with namhatta count for district: {}", district);
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        
        // Return sample sub-district data with namhatta counts
        List<Map<String, Object>> subdistricts = new ArrayList<>();
        
        if ("Kolkata".equals(district)) {
            for (int i = 1; i <= 4; i++) {
                Map<String, Object> subdistrict = new HashMap<>();
                subdistrict.put("subdistrict", "Block " + i);
                subdistrict.put("namhattaCount", 3);
                subdistricts.add(subdistrict);
            }
        }
        
        return ResponseEntity.ok(subdistricts);
    }
    
    @GetMapping("/namhattas")
    public ResponseEntity<List<Map<String, Object>>> getNamhattasBySubDistrict(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            @RequestParam String subdistrict,
            HttpServletRequest request) {
        log.debug("Getting namhattas for sub-district: {}", subdistrict);
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        
        // Return sample namhatta data
        List<Map<String, Object>> namhattas = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> namhatta = new HashMap<>();
            namhatta.put("id", i);
            namhatta.put("name", "Namhatta " + i);
            namhatta.put("code", "NH" + String.format("%03d", i));
            namhatta.put("devoteeCount", 15 + i * 5);
            namhattas.add(namhatta);
        }
        
        return ResponseEntity.ok(namhattas);
    }
    
    private List<String> getAllowedDistricts(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            List<String> districtCodes = (List<String>) request.getAttribute("userDistricts");
            // In real implementation, convert district codes to names
            return districtCodes;
        }
        
        return null; // Admin and Office can access all
    }
}