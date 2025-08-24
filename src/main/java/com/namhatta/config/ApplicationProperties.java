package com.namhatta.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "app")
@Data
@Validated
@Component
@Slf4j
public class ApplicationProperties {
    
    public ApplicationProperties() {
        log.info("Loading application configuration properties");
    }
    
    @NotNull
    private String name = "Namhatta Management System";
    
    @NotNull
    private String version = "1.0.0";
    
    @Valid
    private final Jwt jwt = new Jwt();
    
    @Valid
    private final Storage storage = new Storage();
    
    @Valid
    private final Cors cors = new Cors();
    
    @Data
    public static class Jwt {
        @NotBlank
        private String secret;
        
        @Min(3600000) // Minimum 1 hour
        private long expiration = 3600000;
    }
    
    @Data
    public static class Storage {
        private String type = "local";
        private String uploadDir = "./uploads";
        private long maxFileSize = 5 * 1024 * 1024; // 5MB
    }
    
    @Data
    public static class Cors {
        private String allowedOrigins = "http://localhost:3000,http://localhost:5000,https://*.replit.app,https://*.replit.dev";
        private boolean allowCredentials = true;
        private long maxAge = 3600L;
    }
}