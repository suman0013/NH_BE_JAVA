package com.namhatta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties
@Slf4j
public class NamhattaApplication {

    public static void main(String[] args) {
        log.info("Starting Namhatta Management System Spring Boot Application");
        SpringApplication.run(NamhattaApplication.class, args);
        log.info("Namhatta Management System application started successfully");
    }
}