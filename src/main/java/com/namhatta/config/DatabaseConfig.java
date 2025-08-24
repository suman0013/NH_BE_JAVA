package com.namhatta.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class DatabaseConfig {
    
    
    public DatabaseConfig() {
        log.info("Initializing database configuration for PostgreSQL connection");
    }
    
    // Using Spring Boot auto-configuration for DataSource
    // Configuration is handled via application.yml
}