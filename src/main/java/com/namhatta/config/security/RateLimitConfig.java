package com.namhatta.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitConfig {
    
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * Login rate limiting: 5 attempts per 15 minutes per IP (same as Node.js)
     */
    public Bucket getLoginRateLimitBucket(String clientId) {
        return buckets.computeIfAbsent("login:" + clientId, this::createLoginBucket);
    }
    
    /**
     * API rate limiting: 100 requests per 15 minutes per IP (same as Node.js)
     */
    public Bucket getApiRateLimitBucket(String clientId) {
        return buckets.computeIfAbsent("api:" + clientId, this::createApiBucket);
    }
    
    /**
     * Modification rate limiting: 10 requests per minute per IP (same as Node.js)
     */
    public Bucket getModifyRateLimitBucket(String clientId) {
        return buckets.computeIfAbsent("modify:" + clientId, this::createModifyBucket);
    }
    
    private Bucket createLoginBucket(String key) {
        // 5 attempts per 15 minutes (same as Node.js)
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(15)));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
    
    private Bucket createApiBucket(String key) {
        // 100 requests per 15 minutes (same as Node.js)
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(15)));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
    
    private Bucket createModifyBucket(String key) {
        // 10 requests per minute for modifications (same as Node.js)
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
    
    /**
     * Get client identifier (IP address + user agent hash for better tracking)
     */
    public String getClientId(String ipAddress, String userAgent) {
        if (userAgent != null) {
            return ipAddress + ":" + Math.abs(userAgent.hashCode());
        }
        return ipAddress;
    }
}