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

@ConfigurationProperties(prefix = "security")
@Data
@Validated
@Component
@Slf4j
public class SecurityProperties {
    
    public SecurityProperties() {
        log.info("Loading security configuration properties");
    }
    
    @Valid
    private final RateLimit rateLimit = new RateLimit();
    
    @Valid
    private final Headers headers = new Headers();
    
    @Data
    public static class RateLimit {
        @Min(1)
        private int loginAttempts = 5;
        
        @Min(60)
        private long loginWindowSeconds = 15 * 60; // 15 minutes
        
        @Min(10)
        private int apiRequests = 100;
        
        @Min(60)
        private long apiWindowSeconds = 15 * 60; // 15 minutes
        
        @Min(1)
        private int modifyRequests = 10;
        
        @Min(60)
        private long modifyWindowSeconds = 60; // 1 minute
    }
    
    @Data
    public static class Headers {
        @NotBlank
        private String contentSecurityPolicy = "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; img-src 'self' data: https:; connect-src 'self'";
        
        @NotNull
        private boolean enableHsts = true;
        
        @Min(3600)
        private long hstsMaxAge = 31536000; // 1 year
        
        @NotNull
        private boolean hstsIncludeSubdomains = true;
        
        @NotBlank
        private String referrerPolicy = "strict-origin-when-cross-origin";
        
        @NotBlank
        private String permissionsPolicy = "camera=(), microphone=(), geolocation=()";
    }
}