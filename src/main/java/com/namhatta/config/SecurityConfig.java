package com.namhatta.config;

import com.namhatta.security.JwtAuthenticationEntryPoint;
import com.namhatta.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security filter chain");
        
        return http
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; img-src 'self' data: https:; connect-src 'self'"))
                .referrerPolicy(referrerPolicy -> referrerPolicy
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .addHeaderWriter((request, response) -> {
                    response.setHeader("X-Content-Type-Options", "nosniff");
                    response.setHeader("X-XSS-Protection", "1; mode=block");
                    response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
                }))
            .csrf(csrf -> csrf.disable()) // Using JWT instead
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (same as Node.js)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health", "/api/about").permitAll()
                .requestMatchers("/api/countries", "/api/states", "/api/districts").permitAll()
                .requestMatchers("/api/sub-districts", "/api/villages", "/api/pincodes/**").permitAll()
                .requestMatchers("/api/address-by-pincode").permitAll()
                
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/files/**").permitAll() // Allow file access
                
                // Protected endpoints with role-based access
                .requestMatchers(HttpMethod.GET, "/api/devotees/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/devotees/**").hasAnyRole("ADMIN", "OFFICE")
                .requestMatchers(HttpMethod.PUT, "/api/devotees/**").hasAnyRole("ADMIN", "OFFICE")
                .requestMatchers(HttpMethod.DELETE, "/api/devotees/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET, "/api/namhattas/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/namhattas/**").hasAnyRole("ADMIN", "OFFICE")
                .requestMatchers(HttpMethod.PUT, "/api/namhattas/**").hasAnyRole("ADMIN", "OFFICE")
                .requestMatchers(HttpMethod.DELETE, "/api/namhattas/**").hasRole("ADMIN")
                
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                .requestMatchers("/api/hierarchy/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                .requestMatchers("/api/status-distribution/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                .requestMatchers("/api/map/**").hasAnyRole("ADMIN", "OFFICE", "DISTRICT_SUPERVISOR")
                
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/district-supervisors/**").hasAnyRole("ADMIN", "OFFICE")
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Configuring BCrypt password encoder with 12 rounds (same as Node.js)");
        return new BCryptPasswordEncoder(12); // Same rounds as Node.js
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        log.info("Configuring authentication manager");
        return authConfig.getAuthenticationManager();
    }
}