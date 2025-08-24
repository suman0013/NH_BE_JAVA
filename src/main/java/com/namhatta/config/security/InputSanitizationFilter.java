package com.namhatta.config.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class InputSanitizationFilter {
    
    @Bean
    public FilterRegistrationBean<SanitizationFilter> inputSanitizationFilter() {
        FilterRegistrationBean<SanitizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SanitizationFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        registrationBean.setName("InputSanitizationFilter");
        return registrationBean;
    }
    
    private static class SanitizationFilter implements Filter {
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, 
                            FilterChain chain) throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // Only sanitize POST, PUT, PATCH requests (same as Node.js)
            if (Arrays.asList("POST", "PUT", "PATCH").contains(httpRequest.getMethod())) {
                log.trace("Applying input sanitization for: {} {}", 
                         httpRequest.getMethod(), httpRequest.getRequestURI());
                
                SanitizedRequestWrapper sanitizedRequest = new SanitizedRequestWrapper(httpRequest);
                chain.doFilter(sanitizedRequest, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }
    
    /**
     * Request wrapper that sanitizes JSON body content
     * Implements HTML escaping and trimming logic (same as validator.escape in Node.js)
     */
    private static class SanitizedRequestWrapper extends HttpServletRequestWrapper {
        
        private final String sanitizedBody;
        
        public SanitizedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            
            // Read the original body
            String originalBody = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
            
            // Sanitize the body if it contains JSON
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                this.sanitizedBody = sanitizeJsonContent(originalBody);
            } else {
                this.sanitizedBody = originalBody;
            }
        }
        
        @Override
        public ServletInputStream getInputStream() throws IOException {
            ByteArrayInputStream byteArrayInputStream = 
                new ByteArrayInputStream(sanitizedBody.getBytes(StandardCharsets.UTF_8));
            
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }
                
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setReadListener(ReadListener readListener) {
                    // Not needed for this implementation
                }
                
                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
        }
        
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
        
        /**
         * Sanitize JSON content by escaping HTML entities in string values
         * This preserves JSON structure while sanitizing dangerous content
         */
        private String sanitizeJsonContent(String jsonContent) {
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                return jsonContent;
            }
            
            try {
                // Simple regex-based sanitization for JSON string values
                // This matches quoted strings and escapes HTML entities within them
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
                java.util.regex.Matcher matcher = pattern.matcher(jsonContent);
                StringBuffer result = new StringBuffer();
                
                while (matcher.find()) {
                    String content = matcher.group(1); // Content without quotes
                    String escaped = StringEscapeUtils.escapeHtml4(content);
                    String trimmed = escaped.trim();
                    matcher.appendReplacement(result, "\"" + trimmed + "\"");
                }
                matcher.appendTail(result);
                
                return result.toString();
            } catch (Exception e) {
                log.warn("Failed to sanitize JSON content, returning original: {}", e.getMessage());
                return jsonContent;
            }
        }
    }
}