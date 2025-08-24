package com.namhatta.config.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.List;

@Configuration
@Slf4j
public class SecurityHeadersConfig {
    
    @Value("${spring.profiles.active:development}")
    private String activeProfile;
    
    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;
    
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registrationBean.setName("SecurityHeadersFilter");
        return registrationBean;
    }
    
    private class SecurityHeadersFilter implements Filter {
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, 
                            FilterChain chain) throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            log.trace("Applying security headers for: {}", httpRequest.getRequestURI());
            
            // CORS Headers (development vs production)
            if ("development".equals(activeProfile)) {
                httpResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", 
                    "Origin, Content-Type, Accept, Authorization, X-Requested-With");
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpResponse.setHeader("Access-Control-Max-Age", "3600");
            } else {
                // Production CORS - more restrictive
                String origin = httpRequest.getHeader("Origin");
                if (origin != null && isAllowedOrigin(origin)) {
                    httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                }
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                httpResponse.setHeader("Access-Control-Allow-Headers", 
                    "Origin, Content-Type, Accept, Authorization");
                httpResponse.setHeader("Access-Control-Max-Age", "3600");
            }
            
            // Security headers (same as Helmet configuration in Node.js)
            if ("production".equals(activeProfile)) {
                // Content Security Policy (production only)
                httpResponse.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                    "font-src 'self' https://fonts.gstatic.com; " +
                    "img-src 'self' data: https:; " +
                    "connect-src 'self' https://api.replit.com; " +
                    "object-src 'none'; " +
                    "media-src 'self'; " +
                    "frame-src 'none'");
                
                // Strict Transport Security (HTTPS only)
                httpResponse.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
            }
            
            // Common security headers (all environments)
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResponse.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
            
            // Handle preflight requests
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            chain.doFilter(request, response);
        }
        
        private boolean isAllowedOrigin(String origin) {
            for (String allowedOrigin : allowedOrigins) {
                if (allowedOrigin.equals("*") || allowedOrigin.equals(origin)) {
                    return true;
                }
                // Support wildcard subdomains (e.g., *.replit.app)
                if (allowedOrigin.startsWith("*.")) {
                    String domain = allowedOrigin.substring(2);
                    if (origin.endsWith("." + domain) || origin.equals(domain)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}