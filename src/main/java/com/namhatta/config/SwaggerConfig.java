package com.namhatta.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Namhatta Management System API",
        version = "1.0.0",
        description = "OpenAPI spec for Namhatta web and mobile-compatible system",
        contact = @Contact(name = "System Administrator", email = "admin@namhatta.com")
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@Slf4j
public class SwaggerConfig {
    
    public SwaggerConfig() {
        log.info("Initializing Swagger configuration for API documentation");
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
        log.debug("Creating custom OpenAPI configuration");
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addServersItem(new Server().url("/").description("Current server"));
    }
}