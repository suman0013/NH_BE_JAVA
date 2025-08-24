package com.namhatta.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "spring.datasource.hikari")
@Data
@Validated
@Component
@Slf4j
public class DatabaseProperties {
    
    public DatabaseProperties() {
        log.info("Loading database configuration properties");
    }
    
    @Min(1)
    @Max(50)
    private int maximumPoolSize = 10;
    
    @Min(0)
    @Max(20)
    private int minimumIdle = 5;
    
    @Min(1000)
    @Max(120000)
    private long connectionTimeout = 30000;
    
    @Min(30000)
    @Max(300000)
    private long leakDetectionThreshold = 60000;
    
    @NotBlank
    private String poolName = "NamhattaHikariCP";
}