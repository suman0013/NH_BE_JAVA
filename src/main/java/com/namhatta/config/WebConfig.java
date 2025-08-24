package com.namhatta.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size:50MB}")
    private String maxRequestSize;

    @PostConstruct
    public void init() {
        log.info("Web configuration initialized");
        log.info("Maximum file size: {}", maxFileSize);
        log.info("Maximum request size: {}", maxRequestSize);
        log.info("Request size limits configured for DoS protection");
    }
}