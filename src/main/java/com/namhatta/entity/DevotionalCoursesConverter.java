package com.namhatta.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Converter
@Slf4j
public class DevotionalCoursesConverter implements AttributeConverter<List<DevotionalCourse>, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(List<DevotionalCourse> courses) {
        if (courses == null || courses.isEmpty()) {
            return "[]";
        }
        
        try {
            String json = objectMapper.writeValueAsString(courses);
            log.trace("Converting devotional courses to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting devotional courses to JSON", e);
            return "[]";
        }
    }
    
    @Override
    public List<DevotionalCourse> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || "null".equals(dbData)) {
            return new ArrayList<>();
        }
        
        try {
            List<DevotionalCourse> courses = objectMapper.readValue(dbData, new TypeReference<List<DevotionalCourse>>() {});
            log.trace("Converting JSON to devotional courses: {} courses found", courses.size());
            return courses;
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to devotional courses: {}", dbData, e);
            return new ArrayList<>();
        }
    }
}