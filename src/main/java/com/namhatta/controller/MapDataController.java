package com.namhatta.controller;

import com.namhatta.service.MapDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    
    private final MapDataService mapDataService;
    
    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountriesWithNamhattaCount(HttpServletRequest request) {
        log.debug("Getting countries with namhatta count");
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<Map<String, Object>> countries = mapDataService.getCountriesWithNamhattaCount(allowedDistricts);
            return ResponseEntity.ok(countries);
            
        } catch (Exception e) {
            log.error("Error retrieving countries with namhatta count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<Map<String, Object>>> getStatesWithNamhattaCount(
            @RequestParam String country,
            HttpServletRequest request) {
        log.debug("Getting states with namhatta count for country: {}", country);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<Map<String, Object>> states = mapDataService.getStatesWithNamhattaCount(country, allowedDistricts);
            return ResponseEntity.ok(states);
            
        } catch (Exception e) {
            log.error("Error retrieving states with namhatta count for country: {}", country, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            HttpServletRequest request) {
        log.debug("Getting districts with namhatta count for state: {}", state);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<Map<String, Object>> districts = mapDataService.getDistrictsWithNamhattaCount(country, state, allowedDistricts);
            return ResponseEntity.ok(districts);
            
        } catch (Exception e) {
            log.error("Error retrieving districts with namhatta count for state: {}", state, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/subdistricts")
    public ResponseEntity<List<Map<String, Object>>> getSubDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            HttpServletRequest request) {
        log.debug("Getting sub-districts with namhatta count for district: {}", district);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<Map<String, Object>> subdistricts = mapDataService.getSubDistrictsWithNamhattaCount(country, state, district, allowedDistricts);
            return ResponseEntity.ok(subdistricts);
            
        } catch (Exception e) {
            log.error("Error retrieving sub-districts for district: {}", district, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/namhattas")
    public ResponseEntity<List<Map<String, Object>>> getNamhattasBySubDistrict(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            @RequestParam String subdistrict,
            HttpServletRequest request) {
        log.debug("Getting namhattas for sub-district: {}", subdistrict);
        
        try {
            List<String> allowedDistricts = getAllowedDistricts(request);
            List<Map<String, Object>> namhattas = mapDataService.getNamhattasBySubDistrict(country, state, district, subdistrict, allowedDistricts);
            return ResponseEntity.ok(namhattas);
            
        } catch (Exception e) {
            log.error("Error retrieving namhattas for sub-district: {}", subdistrict, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
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