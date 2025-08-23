package com.namhatta.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;
    
    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;
    
    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;
    
    public DatabaseConfig() {
        log.info("Initializing database configuration for PostgreSQL connection");
    }
    
    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Creating HikariCP connection pool for database: {}", 
                 databaseUrl.replaceAll("password=[^&\\s]+", "password=***"));
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("NamhattaHikariCP");
        
        log.debug("HikariCP configuration - MaxPoolSize: {}, MinIdle: {}, ConnectionTimeout: {}ms", 
                  maximumPoolSize, minimumIdle, connectionTimeout);
        
        return new HikariDataSource(config);
    }
}