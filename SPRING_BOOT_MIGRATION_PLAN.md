# Spring Boot Migration Plan for Namhatta Management System

## 🤖 AGENT WORKFLOW INSTRUCTIONS

**⚠️ CRITICAL: All agents must follow these rules when working on this migration:**

### Task Status Management
1. **BEFORE starting any task/subtask:** Update the status in this plan from `☐ Not Started` to `☐ In Progress`
2. **AFTER completing any task/subtask:** Update the status from `☐ In Progress` to `☐ Completed`
3. **Never skip status updates** - tracking progress is mandatory for coordination

### Task Execution Order
1. **NEVER pick random tasks** - follow the exact phase and task order listed in this plan
2. **Complete Phase 1 entirely before moving to Phase 2**
3. **Complete all subtasks within a task before marking the task complete**
4. **Dependencies must be respected** - later phases depend on earlier phases

### Status Update Format
```
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed
```

### Work Documentation
- Document any deviations or issues encountered in the task notes
- Update validation criteria as tasks are completed
- Add implementation details for complex configurations

---

## Overview
This document provides a detailed, task-based migration plan to move the Namhatta Management System backend from Node.js/Express to Spring Boot while maintaining 100% functionality and API compatibility. The migration uses the **same PostgreSQL database** with no schema changes required.

## Why Spring Boot Migration?

### Benefits
- **Enterprise-grade framework** with built-in security, validation, and monitoring
- **Better performance** with JVM optimizations and connection pooling
- **Robust ecosystem** with mature libraries and tools
- **Type safety** with Java's strong typing system
- **Production-ready features** like health checks, metrics, and logging
- **Easier scaling** and deployment in enterprise environments

### Migration Approach
- **Zero database changes**: Use existing PostgreSQL schema as-is
- **API compatibility**: Maintain exact same REST endpoints and responses
- **Parallel development**: Build Spring Boot version alongside current Node.js system
- **Gradual migration**: Switch endpoints one by one for safe rollback

## Current Architecture Analysis

### Existing System Overview
- **Runtime**: Node.js 20 with Express.js server
- **Language**: TypeScript with ES modules
- **Database**: PostgreSQL (Neon serverless) with Drizzle ORM
- **Authentication**: JWT tokens in HTTP-only cookies, bcrypt password hashing
- **Session Management**: PostgreSQL sessions with single login enforcement
- **Authorization**: Role-based access (ADMIN, OFFICE, DISTRICT_SUPERVISOR)
- **Data Access**: District-based filtering for supervisors

### Database Schema (12 tables - NO CHANGES NEEDED)
Since we're using the **same database**, no Flyway migrations are needed. The Spring Boot application will connect to your existing PostgreSQL database and use the current schema:

1. **devotees** - Personal information, spiritual status, courses
2. **namhattas** - Spiritual centers with organizational details  
3. **devotional_statuses** - Hierarchical spiritual levels
4. **shraddhakutirs** - Regional spiritual administrative units
5. **leaders** - Hierarchical leadership structure
6. **addresses** - Normalized address data
7. **devotee_addresses** - Junction table for devotee addresses
8. **namhatta_addresses** - Junction table for namhatta addresses
9. **users** - Authentication users with roles
10. **user_districts** - Many-to-many user-district mapping
11. **user_sessions** - Single login enforcement
12. **jwt_blacklist** - Token invalidation

### Current API Endpoints (60+ endpoints to migrate)

**📋 REFERENCE DOCUMENT**: See `COMPLETE_API_SPECIFICATIONS.md` for detailed specifications of all 60+ endpoints with exact request/response formats, authentication requirements, and implementation notes.
All endpoints will maintain exact same behavior and response formats as documented in API_DOCUMENTATION.md:

**Authentication APIs (6 endpoints)**
- `/api/auth/*` - Authentication system (login, logout, verify, user-districts, dev endpoints)

**System APIs (2 endpoints)**
- `/api/health` - Health check endpoint
- `/api/about` - System information endpoint

**Geography APIs (8 endpoints)** 
- `/api/countries`, `/api/states`, `/api/districts`, `/api/sub-districts`, `/api/villages`
- `/api/pincodes`, `/api/pincodes/search`, `/api/address-by-pincode`

**Map Data APIs (5 endpoints)**
- `/api/map/*` - Namhatta counts by geographic levels

**Dashboard APIs (2 endpoints)**
- `/api/dashboard` - Summary statistics
- `/api/status-distribution` - Status distribution data

**Hierarchy APIs (2 endpoints)**
- `/api/hierarchy` - Top-level hierarchy
- `/api/hierarchy/:level` - Leaders by level

**Devotees APIs (8 endpoints)**
- Full CRUD operations with district filtering and status management

**Namhattas APIs (12 endpoints)**
- Full CRUD operations with approval workflow and sub-resources

**Statuses APIs (3 endpoints)**
- `/api/statuses` - CRUD operations for devotional statuses

**Gurudevs APIs (2 endpoints)**
- `/api/gurudevs` - CRUD operations for spiritual teachers

**Shraddhakutirs APIs (2 endpoints)**
- `/api/shraddhakutirs` - Regional spiritual units management

**Updates APIs (2 endpoints)**
- `/api/updates` - Namhatta program updates

**Admin APIs (5 endpoints)**
- `/api/admin/*` - User management and supervisor registration

**Additional APIs (3 endpoints)**
- District supervisors, user profiles, development endpoints

## Complete API Endpoint Specifications

**CRITICAL**: All Spring Boot endpoints must match the exact URLs, request formats, response formats, status codes, and authentication requirements specified in `API_DOCUMENTATION.md`. 

**📋 DETAILED SPECIFICATIONS**: Complete endpoint specifications with exact request/response formats are documented in `COMPLETE_API_SPECIFICATIONS.md`.

### API Categories Summary:
1. **Authentication APIs** - JWT-based auth with HTTP-only cookies
2. **System APIs** - Health checks and system info
3. **Geography APIs** - Location hierarchy data
4. **Map Data APIs** - Namhatta distribution statistics
5. **Dashboard APIs** - Analytics and summaries  
6. **Hierarchy APIs** - Leadership structure
7. **Devotees APIs** - Devotee management with district filtering
8. **Namhattas APIs** - Spiritual center management with approval workflow
9. **Statuses APIs** - Devotional status management
10. **Gurudevs APIs** - Spiritual teacher management
11. **Shraddhakutirs APIs** - Regional unit management
12. **Updates APIs** - Program update tracking
13. **Admin APIs** - User and supervisor management
14. **Additional APIs** - Supporting functionality

### Rate Limiting Requirements:
- **Login API**: 5 attempts per 15 minutes per IP
- **General APIs**: 100 requests per 15 minutes per IP  
- **Modification APIs** (POST/PUT/DELETE): 10 requests per minute per IP

### Authentication Requirements:
- **Method**: JWT tokens in HTTP-only cookies
- **Session Duration**: 1 hour
- **Single Login**: Only one active session per user
- **Token Blacklisting**: Logout invalidates tokens immediately
- **Role-based Access**: ADMIN, OFFICE, DISTRICT_SUPERVISOR with district filtering

### Error Response Format:
All APIs must use consistent error response format:
```json
{
  "error": "Error message",
  "message": "Detailed error message (sometimes)", 
  "details": "Validation errors array (for validation failures)"
}
```

## Detailed Migration Tasks

## Phase 1: Project Setup & Infrastructure

### Task 1.1: Create Replit Spring Boot Project
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Sub-tasks:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create new Replit Java project named "namhatta-springboot"
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up Maven project structure with Spring Boot parent
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure `pom.xml` with all required dependencies (including Lombok & Swagger)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create basic application structure in Replit
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Test basic Spring Boot startup

**Files to create:**
```
pom.xml                           <- Maven configuration
src/main/java/com/namhatta/
├── NamhattaApplication.java     <- Main application class
└── config/
    ├── DatabaseConfig.java      <- Database connection
    ├── SwaggerConfig.java       <- Swagger/OpenAPI configuration
    └── LoggingConfig.java       <- Logging configuration
src/main/resources/
├── application.yml              <- Main configuration
├── application-dev.yml          <- Development configuration
└── logback-spring.xml           <- Logging configuration
```

**Maven Dependencies (pom.xml):**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
</parent>

<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- JWT Authentication -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- Rate Limiting & Caching -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.vladimir-bukhtoyarov</groupId>
        <artifactId>bucket4j-core</artifactId>
        <version>8.7.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.vladimir-bukhtoyarov</groupId>
        <artifactId>bucket4j-redis</artifactId>
        <version>8.7.0</version>
    </dependency>
    
    <!-- Input Sanitization -->
    <dependency>
        <groupId>org.owasp.antisamy</groupId>
        <artifactId>antisamy</artifactId>
        <version>1.7.4</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-text</artifactId>
        <version>1.10.0</version>
    </dependency>
    
    <!-- Documentation & API -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
    <dependency>
        <groupId>io.swagger.core.v3</groupId>
        <artifactId>swagger-annotations</artifactId>
        <version>2.2.8</version>
    </dependency>
    
    <!-- Lombok for reducing boilerplate code -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Swagger Configuration:**

`SwaggerConfig.java`:
```java
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
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addServersItem(new Server().url("/").description("Current server"));
    }
}
```

**Main Application Class with Lombok:**

`NamhattaApplication.java`:
```java
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
```

**Validation Criteria:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Spring Boot application starts successfully
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Can connect to existing PostgreSQL database using same connection string
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Maven builds without errors
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Replit workflow runs `mvn spring-boot:run` successfully
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Swagger UI accessible at `/swagger-ui.html`

---

### Task 1.2: Configure Database Connection
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Purpose**: Connect to your existing Neon PostgreSQL database using the same connection string

**Sub-tasks:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create `application.yml` with database configuration
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up environment variables for DATABASE_URL, JWT_SECRET, SESSION_SECRET
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create `DatabaseConfig.java` for connection pooling
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Test database connection
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Verify can read existing data from devotees table

**Configuration Files:**

`application.yml`:
```yaml
spring:
  application:
    name: namhatta-management-system
    
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      
  jpa:
    hibernate:
      ddl-auto: none  # IMPORTANT: Don't modify existing schema
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        
server:
  port: ${PORT:5000}  # Same port as Node.js version
  
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 hours (same as Node.js)
  
session:
  secret: ${SESSION_SECRET}
```

**Validation Criteria:**
- [✓] Application connects to PostgreSQL successfully
- [✓] Can query existing tables (devotees, namhattas, users)
- [✓] Connection pool configured properly
- [✓] Environment variables loaded correctly

---

### Task 1.3: Set Up Replit Configuration  
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Sub-tasks:**
- ❌ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Create `.replit` file for Java/Maven configuration (Cannot edit .replit file in Replit)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure run command for Spring Boot
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up environment variables in Replit Secrets
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Test hot reload functionality
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure port forwarding for port 5000

---

### Task 1.4: File Upload & Storage Configuration
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Purpose**: Configure file upload handling for namhatta update images and implement cloud storage integration

**Sub-tasks:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure Spring Boot Multipart file upload settings
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Implement file upload controller with validation (5MB limit, image types only)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up cloud storage integration (AWS S3, Google Cloud, or similar)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create image upload service with security validation
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure static file serving for uploaded images

**Files to create:**
```
src/main/java/com/namhatta/
├── controller/FileUploadController.java     <- File upload endpoint
├── service/FileStorageService.java          <- File storage logic
├── config/FileStorageConfig.java            <- Upload configuration
└── dto/FileUploadResponse.java              <- Upload response DTO
src/main/resources/
└── application.yml                          <- Multipart configuration
```

**Configuration (application.yml):**
```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 5MB
      max-request-size: 50MB
      location: ${java.io.tmpdir}
      
# Cloud storage configuration
storage:
  type: ${STORAGE_TYPE:local} # local, s3, gcs
  local:
    upload-dir: ${UPLOAD_DIR:./uploads}
  s3:
    bucket: ${AWS_S3_BUCKET:}
    region: ${AWS_REGION:}
    access-key: ${AWS_ACCESS_KEY:}
    secret-key: ${AWS_SECRET_KEY:}
```

**Validation Criteria:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - File upload endpoint accepts multipart/form-data
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Image type validation (jpg, png, gif, webp)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - File size limit enforcement (5MB)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Secure file naming to prevent directory traversal
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Integration with namhatta updates API

---

### Task 1.5: Security & CORS Configuration
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Purpose**: Implement comprehensive security headers, CORS policy, and Content Security Policy

**Sub-tasks:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure Spring Security headers (HSTS, CSP, X-Frame-Options)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up CORS policy (production vs development)
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Implement Content Security Policy directives
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure X-Content-Type-Options and other security headers
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up request size limits and protection against malicious requests

**Files to create:**
```
src/main/java/com/namhatta/config/
├── SecurityConfig.java          <- Spring Security configuration
├── CorsConfig.java             <- CORS policy configuration  
└── WebConfig.java              <- Web MVC configuration
```

**Security Configuration:**
```java
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security filter chain");
        
        return http
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true))
                .contentSecurityPolicy("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com"))
            .csrf(csrf -> csrf.disable()) // Using JWT instead
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/health", "/api/about").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

**Validation Criteria:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - CORS policy blocks unauthorized origins in production
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Security headers present in all responses
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - CSP prevents XSS attacks
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Request size limits prevent DoS attacks

---

### Task 1.6: Environment & Configuration Management
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Purpose**: Set up comprehensive environment-specific configuration management

**Sub-tasks:**
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create environment-specific application.yml files
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up @ConfigurationProperties beans for type-safe configuration
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Configure development vs production settings
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Set up logging configuration per environment
- ✓ **Status**: ☐ Not Started | ☐ In Progress | ✓ Completed - Create configuration validation

**Files to create:**
```
src/main/java/com/namhatta/config/
├── ApplicationProperties.java   <- Type-safe configuration
├── DatabaseProperties.java     <- Database configuration
└── SecurityProperties.java     <- Security configuration
src/main/resources/
├── application.yml             <- Base configuration
├── application-dev.yml         <- Development settings
├── application-prod.yml        <- Production settings
└── application-test.yml        <- Test settings
```

**Configuration Properties:**
```java
@ConfigurationProperties(prefix = "app")
@Data
@Validated
@Slf4j
public class ApplicationProperties {
    
    public ApplicationProperties() {
        log.info("Loading application configuration properties");
    }
    
    @NotNull
    private String name = "Namhatta Management System";
    
    @NotNull
    private String version = "1.0.0";
    
    private final Jwt jwt = new Jwt();
    private final Storage storage = new Storage();
    
    @Data
    public static class Jwt {
        @NotBlank
        private String secret;
        
        @Min(3600000) // Minimum 1 hour
        private long expiration = 3600000;
    }
    
    @Data
    public static class Storage {
        private String type = "local";
        private String uploadDir = "./uploads";
        private long maxFileSize = 5 * 1024 * 1024; // 5MB
    }
}
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Type-safe configuration with validation
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Environment-specific profiles work correctly
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configuration validation fails startup on invalid values

**Replit Configuration Files:**

`.replit`:
```toml
run = "mvn spring-boot:run"
entrypoint = "src/main/java/com/namhatta/NamhattaApplication.java"

[languages.java]
pattern = "**/*.java"

[nix]
channel = "stable-22_11"

[gitHubImport]
requiredFiles = [".replit", "replit.nix", "pom.xml"]

[deployment]
run = ["mvn", "clean", "package", "-DskipTests", "&&", "java", "-jar", "target/*.jar"]
```

**Environment Variables to Set in Replit Secrets:**
- `DATABASE_URL` - Your existing Neon PostgreSQL connection string
- `JWT_SECRET` - Same JWT secret from Node.js version
- `SESSION_SECRET` - Same session secret from Node.js version

**Validation Criteria:**
- [ ] Spring Boot starts via `mvn spring-boot:run`
- [ ] Application accessible on port 5000
- [ ] Environment variables loaded from Replit Secrets
- [ ] Hot reload works when editing Java files

---

### Task 1.7: Scheduled Tasks & Background Jobs
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement cleanup jobs for expired tokens, sessions, and other maintenance tasks

**Sub-tasks:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Enable Spring Boot scheduling (@EnableScheduling)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Create JWT token cleanup job (runs every hour)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Create user session cleanup job (runs every hour)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Add application statistics collection (daily)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configure async task execution

**Files to create:**
```
src/main/java/com/namhatta/
├── service/ScheduledTasksService.java       <- Cleanup jobs
├── config/AsyncConfig.java                  <- Async task configuration
└── config/SchedulingConfig.java             <- Scheduling configuration
```

**Scheduled Service:**
```java
@Service
@Slf4j
public class ScheduledTasksService {
    
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired JWT tokens");
        try {
            int cleaned = jwtService.cleanupExpiredTokens();
            log.info("Cleaned up {} expired JWT tokens", cleaned);
        } catch (Exception e) {
            log.error("Error during JWT token cleanup", e);
        }
    }
    
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupExpiredSessions() {
        log.info("Starting scheduled cleanup of expired user sessions");
        try {
            int cleaned = sessionService.cleanupExpiredSessions();
            log.info("Cleaned up {} expired user sessions", cleaned);
        } catch (Exception e) {
            log.error("Error during session cleanup", e);
        }
    }
}
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Scheduled tasks run automatically
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Cleanup jobs reduce database bloat
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Error handling prevents task failures

---

### Task 1.8: Global Exception Handling & Error Responses
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement consistent error response format across all APIs

**Sub-tasks:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Create @ControllerAdvice for global exception handling
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Implement standard error response DTOs
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Handle validation errors (Bean Validation)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Handle authentication/authorization errors
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Handle database constraint violations

**Files to create:**
```
src/main/java/com/namhatta/
├── exception/GlobalExceptionHandler.java    <- @ControllerAdvice
├── exception/NamhattaException.java         <- Custom exceptions
├── dto/ErrorResponse.java                   <- Standard error format
└── dto/ValidationErrorResponse.java         <- Validation error format
```

**Error Response Format:**
```java
@Data
@Builder
@Schema(description = "Standard error response")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Invalid input data")
    private String error;
    
    @Schema(description = "Detailed error message", example = "Field 'name' cannot be empty")
    private String message;
    
    @Schema(description = "Validation errors", example = "[\"name is required\", \"email is invalid\"]")
    private List<String> details;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Timestamp", example = "2025-01-01T12:00:00Z")
    private LocalDateTime timestamp;
}
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - All APIs return consistent error format
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Validation errors include field-specific details
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Security errors don't leak sensitive information

---

### Task 1.9: Monitoring & Health Checks  
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement comprehensive application monitoring and health check endpoints

**Sub-tasks:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configure Spring Boot Actuator endpoints
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Implement custom health indicators (database, external services)
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Set up application metrics collection
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configure security for management endpoints
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Add custom info endpoint with build information

**Files to create:**
```
src/main/java/com/namhatta/
├── health/DatabaseHealthIndicator.java      <- Custom health checks
├── health/StorageHealthIndicator.java       <- Storage health check
└── config/ActuatorConfig.java               <- Actuator configuration
```

**Health Configuration:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
  health:
    db:
      enabled: true
    diskspace:
      enabled: true
  info:
    env:
      enabled: true
    build:
      enabled: true
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Health endpoint returns database connectivity status
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Metrics endpoint provides application statistics
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Management endpoints are secured
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Custom health indicators detect service issues

---

### Task 1.10: Testing Configuration & Infrastructure
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Set up comprehensive testing infrastructure for integration and unit tests

**Sub-tasks:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configure Testcontainers for integration tests
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Set up test profiles and configurations
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Create test data builders and utilities
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Configure MockMvc for controller testing
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Set up database test fixtures

**Files to create:**
```
src/test/java/com/namhatta/
├── config/TestConfig.java                   <- Test configuration
├── util/TestDataBuilder.java                <- Test data creation
├── integration/BaseIntegrationTest.java     <- Integration test base
└── controller/AbstractControllerTest.java   <- Controller test base
```

**Test Configuration:**
```java
@TestConfiguration
@Profile("test")
@Slf4j
public class TestConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        log.info("Setting up test database with Testcontainers");
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("namhatta_test")
            .withUsername("test")
            .withPassword("test");
        postgres.start();
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        return new HikariDataSource(config);
    }
}
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Integration tests run with real database
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Test data builders create valid entities
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Controller tests validate request/response formats
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Test database isolated from development database

## Phase 2: Database Entities & JPA Mapping

### Task 2.1: Create Core Entity Classes
**Status**: ✓ Completed

**Purpose**: Map existing PostgreSQL tables to JPA entities without changing database schema

**Sub-tasks:**
- [x] Create `User.java` entity for authentication
- [x] Create `Devotee.java` entity with all fields
- [x] Create `Namhatta.java` entity with all fields
- [x] Create `DevotionalStatus.java` entity
- [x] Create `Shraddhakutir.java` entity
- [x] Create `Leader.java` entity
- [x] Test entity mapping with simple queries

**Key Entity: User.java (with Lombok)**
```java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"passwordHash", "districts"})
@Slf4j
@Schema(description = "User entity for authentication and authorization")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User unique identifier", example = "1")
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Schema(description = "Username for login", example = "admin", required = true)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    @Schema(description = "Hashed password", hidden = true)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Schema(description = "User role", example = "ADMIN", allowableValues = {"ADMIN", "OFFICE", "DISTRICT_SUPERVISOR"})
    private UserRole role; // ADMIN, OFFICE, DISTRICT_SUPERVISOR
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether user account is active", example = "true")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    // Many-to-many relationship with districts
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_districts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "district_code")
    )
    @Builder.Default
    @Schema(description = "Districts assigned to this user (for supervisors)")
    private Set<District> districts = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        log.debug("Creating new user entity: {}", username);
        createdAt = LocalDateTime.now();
    }
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded user entity: {} with role: {}", username, role);
    }
}
```

**Key Entity: Devotee.java**
```java
@Entity
@Table(name = "devotees")
public class Devotee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "legal_name", nullable = false)
    private String legalName;
    
    @Column(name = "name") // Initiated/spiritual name
    private String name;
    
    @Column(name = "dob")
    private String dob; // Keep as String to match current format
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    // Family information
    @Column(name = "father_name")
    private String fatherName;
    
    @Column(name = "mother_name")
    private String motherName;
    
    @Column(name = "husband_name")
    private String husbandName;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "blood_group")
    private String bloodGroup;
    
    @Column(name = "marital_status")
    private String maritalStatus;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devotional_status_id")
    private DevotionalStatus devotionalStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namhatta_id")
    private Namhatta namhatta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shraddhakutir_id")
    private Shraddhakutir shraddhakutir;
    
    // Address relationships
    @OneToMany(mappedBy = "devotee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DevoteeAddress> addresses = new ArrayList<>();
    
    // Spiritual information
    @Column(name = "initiated_name")
    private String initiatedName;
    
    @Column(name = "harinam_date")
    private String harinamDate;
    
    @Column(name = "pancharatrik_date")
    private String pancharatrikDate;
    
    // Professional information
    @Column(name = "education")
    private String education;
    
    @Column(name = "occupation")
    private String occupation;
    
    // JSON field for devotional courses
    @Column(name = "devotional_courses", columnDefinition = "jsonb")
    @Convert(converter = DevotionalCoursesConverter.class)
    private List<DevotionalCourse> devotionalCourses = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters, setters, constructors
}
```

**Validation Criteria:**
- [✓] All entities compile without errors
- [✓] Can retrieve existing data from each table
- [✓] Relationships work correctly (devotee -> namhatta, devotee -> status)
- [✓] JSON fields (devotional_courses) map correctly

---

### Task 2.2: Create Address & Junction Tables
**Status**: ✓ Completed

**Sub-tasks:**
- [x] Create `Address.java` entity for normalized addresses
- [x] Create `DevoteeAddress.java` junction entity
- [x] Create `NamhattaAddress.java` junction entity
- [x] Test address relationships
- [x] Verify landmark data handling

**Address Entities:**

`Address.java`:
```java
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "state_name_english")
    private String stateNameEnglish;
    
    @Column(name = "district_name_english") 
    private String districtNameEnglish;
    
    @Column(name = "subdistrict_name_english")
    private String subdistrictNameEnglish;
    
    @Column(name = "village_name_english")
    private String villageNameEnglish;
    
    @Column(name = "pincode")
    private String pincode;
    
    // Relationships
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<DevoteeAddress> devoteeAddresses = new ArrayList<>();
    
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<NamhattaAddress> namhattaAddresses = new ArrayList<>();
    
    // Getters, setters
}
```

`DevoteeAddress.java`:
```java
@Entity
@Table(name = "devotee_addresses")
public class DevoteeAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devotee_id", nullable = false)
    private Devotee devotee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    @Column(name = "address_type") // 'present' or 'permanent'
    private String addressType;
    
    @Column(name = "landmark")
    private String landmark;
    
    // Getters, setters
}
```

**Validation Criteria:**
- [✓] Can retrieve devotee with all addresses
- [✓] Can retrieve namhatta with address and landmark
- [✓] Junction table relationships work correctly
- [✓] Address filtering by district works for supervisors

---

### Task 2.3: Create Repository Interfaces
**Status**: ✓ Completed

**Sub-tasks:**
- [x] Create `UserRepository.java` with authentication queries
- [x] Create `DevoteeRepository.java` with filtering methods
- [x] Create `NamhattaRepository.java` with district filtering
- [x] Create `AddressRepository.java` for geographic queries
- [x] Test all repository methods with existing data

**Key Repository: DevoteeRepository.java**
```java
@Repository
public interface DevoteeRepository extends JpaRepository<Devotee, Long> {
    
    // Find devotees by namhatta
    List<Devotee> findByNamhattaId(Long namhattaId);
    
    // Count devotees by status
    @Query("SELECT COUNT(d) FROM Devotee d WHERE d.devotionalStatus.id = :statusId")
    long countByDevotionalStatusId(@Param("statusId") Long statusId);
    
    // Complex filtering query for district supervisors
    @Query("SELECT DISTINCT d FROM Devotee d " +
           "LEFT JOIN d.addresses da " +
           "LEFT JOIN da.address a " +
           "WHERE (:allowedDistricts IS NULL OR a.districtNameEnglish IN :allowedDistricts) " +
           "AND (:status IS NULL OR d.devotionalStatus.name = :status) " +
           "AND (:search IS NULL OR " +
           "     LOWER(d.legalName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(d.phone) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Devotee> findFilteredDevotees(
        @Param("allowedDistricts") List<String> allowedDistricts,
        @Param("status") String status,
        @Param("search") String search,
        Pageable pageable
    );
}
```

**Key Repository: NamhattaRepository.java**
```java
@Repository  
public interface NamhattaRepository extends JpaRepository<Namhatta, Long> {
    
    // Check if code exists (for uniqueness validation)
    boolean existsByCode(String code);
    
    // Complex filtering for district supervisors
    @Query("SELECT DISTINCT n FROM Namhatta n " +
           "LEFT JOIN n.addresses na " +
           "LEFT JOIN na.address a " +
           "WHERE (:allowedDistricts IS NULL OR a.districtNameEnglish IN :allowedDistricts) " +
           "AND (:status IS NULL OR n.status = :status) " +
           "AND (:search IS NULL OR " +
           "     LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(n.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Namhatta> findFilteredNamhattas(
        @Param("allowedDistricts") List<String> allowedDistricts,
        @Param("status") String status,
        @Param("search") String search,
        Pageable pageable
    );
    
    // Get namhattas with devotee count
    @Query("SELECT n, COUNT(d) as devoteeCount FROM Namhatta n " +
           "LEFT JOIN n.devotees d " +
           "WHERE n.id = :id " +
           "GROUP BY n")
    Optional<Object[]> findByIdWithDevoteeCount(@Param("id") Long id);
}
```

**Validation Criteria:**
- [✓] All repository methods compile and work
- [✓] Can query existing data successfully
- [✓] Filtering by districts works for supervisors
- [✓] Pagination and sorting work correctly
- [✓] Complex joins return expected results

---

## Phase 3: Security & Authentication System

### Task 3.1: Spring Security Configuration
**Status**: ✓ Completed

**Purpose**: Implement JWT-based authentication with HTTP-only cookies matching current Node.js system

**Sub-tasks:**
- [x] Create `SecurityConfig.java` with authentication rules
- [x] Create `JwtAuthenticationFilter.java` for token validation
- [x] Create `JwtTokenProvider.java` for token creation/validation
- [x] Create `UserDetailsServiceImpl.java` for user loading
- [x] Test authentication flow with existing users

**Security Configuration:**

**Key Entity Examples:**
```java
@Entity
@Table(name = "devotees")
public class Devotee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "legal_name", nullable = false)
    private String legalName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devotional_status_id")
    private DevotionalStatus devotionalStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namhatta_id")
    private Namhatta namhatta;
    
    @OneToMany(mappedBy = "devotee", cascade = CascadeType.ALL)
    private List<DevoteeAddress> addresses;
    
    // ... other fields, getters, setters
}

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // ADMIN, OFFICE, DISTRICT_SUPERVISOR
    
    @ManyToMany
    @JoinTable(name = "user_districts",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "district_code"))
    private Set<District> districts;
    
    // ... other fields
}
```

#### 2.2 Repository Layer
Create Spring Data JPA repositories with custom queries:

```java
@Repository
public interface DevoteeRepository extends JpaRepository<Devotee, Long> {
    
    @Query("SELECT d FROM Devotee d JOIN d.addresses da JOIN da.address a " +
           "WHERE (:district IS NULL OR a.districtNameEnglish IN :allowedDistricts)")
    Page<Devotee> findFilteredDevotees(
        @Param("allowedDistricts") List<String> allowedDistricts,
        Pageable pageable
    );
    
    List<Devotee> findByNamhattaId(Long namhattaId);
    
    @Query("SELECT COUNT(d) FROM Devotee d WHERE d.devotionalStatus.id = :statusId")
    long countByStatusId(Long statusId);
}
```



`SecurityConfig.java`:
```java
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (same as Node.js)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health", "/api/about").permitAll()
                .requestMatchers("/api/countries", "/api/states", "/api/districts").permitAll()
                .requestMatchers("/api/sub-districts", "/api/villages", "/api/pincodes/**").permitAll()
                .requestMatchers("/api/address-by-pincode").permitAll()
                
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
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Same rounds as Node.js
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
```

**JWT Token Provider:**

`JwtTokenProvider.java`:
```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    public String createToken(User user, String sessionToken) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        // Extract district codes for the user
        List<String> districtCodes = user.getDistricts().stream()
            .map(district -> district.getCode())
            .collect(Collectors.toList());
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .claim("sessionToken", sessionToken)
            .claim("districts", districtCodes)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
    
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
    }
}
```

**JWT Authentication Filter:**

`JwtAuthenticationFilter.java`:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private SessionService sessionService;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (token != null && tokenProvider.validateToken(token)) {
            try {
                Claims claims = tokenProvider.getClaimsFromToken(token);
                String username = claims.getSubject();
                String sessionToken = claims.get("sessionToken", String.class);
                
                // Validate session (same as Node.js - single login enforcement)
                if (sessionService.isSessionValid(username, sessionToken)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // Add user info to request for controllers
                        request.setAttribute("currentUser", userDetails);
                        request.setAttribute("userId", claims.get("userId", Long.class));
                        request.setAttribute("userRole", claims.get("role", String.class));
                        request.setAttribute("userDistricts", claims.get("districts", List.class));
                    }
                }
            } catch (Exception e) {
                logger.error("Could not set user authentication in security context", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // Check HTTP-only cookie first (same as Node.js)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

**Validation Criteria:**
- [✓] JWT tokens created with same format as Node.js
- [✓] HTTP-only cookies work for authentication
- [✓] Session validation enforces single login
- [✓] Role-based access control works correctly
- [✓] District-based filtering applied for supervisors

---

### Task 3.2: Rate Limiting & Security Configuration
**Status**: ✓ Completed

**Purpose**: Implement comprehensive rate limiting and security headers matching Node.js system

**Sub-tasks:**
- [x] Create `RateLimitConfig.java` with three rate limiting configurations
- [x] Create `SecurityHeadersConfig.java` for Helmet-equivalent security headers
- [x] Create `InputSanitizationFilter.java` for XSS protection
- [x] Configure CORS policies for development vs production
- [x] Test all security configurations

**Rate Limiting Configuration:**

`RateLimitConfig.java`:
```java
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfig {
    
    @Bean
    @Qualifier("loginRateLimit")
    public RedisRateLimiter loginRateLimit() {
        // 5 attempts per 15 minutes (same as Node.js)
        return new RedisRateLimiter(5, 15 * 60, Duration.ofMinutes(15));
    }
    
    @Bean 
    @Qualifier("apiRateLimit")
    public RedisRateLimiter apiRateLimit() {
        // 100 requests per 15 minutes (same as Node.js)
        return new RedisRateLimiter(100, 15 * 60, Duration.ofMinutes(15));
    }
    
    @Bean
    @Qualifier("modifyRateLimit") 
    public RedisRateLimiter modifyRateLimit() {
        // 10 requests per 1 minute for modifications (same as Node.js)
        return new RedisRateLimiter(10, 60, Duration.ofMinutes(1));
    }
}
```

**Input Sanitization Filter:**

`InputSanitizationFilter.java`:
```java
@Component
@Order(FilterRegistrationBean.HIGHEST_PRECEDENCE + 1)
public class InputSanitizationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Only sanitize POST, PUT, PATCH requests (same as Node.js)
        if (Arrays.asList("POST", "PUT", "PATCH").contains(httpRequest.getMethod())) {
            SanitizedRequestWrapper sanitizedRequest = new SanitizedRequestWrapper(httpRequest);
            chain.doFilter(sanitizedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
    
    private static class SanitizedRequestWrapper extends HttpServletRequestWrapper {
        // Implement HTML escaping and trimming logic (same as validator.escape in Node.js)
    }
}
```

**Security Headers Configuration:**

`SecurityHeadersConfig.java`:
```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    
    private static class SecurityHeadersFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, 
                            FilterChain chain) throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Same security headers as Helmet configuration in Node.js
            if ("production".equals(System.getProperty("spring.profiles.active"))) {
                httpResponse.setHeader("Content-Security-Policy", 
                    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                    "font-src 'self' https://fonts.gstatic.com; " +
                    "img-src 'self' data: https:; connect-src 'self' https://api.replit.com; " +
                    "object-src 'none'; media-src 'self'; frame-src 'none'");
                    
                httpResponse.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
                    
                httpResponse.setHeader("X-Content-Type-Options", "nosniff");
                httpResponse.setHeader("X-Frame-Options", "DENY");
                httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            }
            
            chain.doFilter(request, response);
        }
    }
}
```

**Validation Criteria:**
- [✓] Login rate limiting: 5 attempts per 15 minutes per IP
- [✓] API rate limiting: 100 requests per 15 minutes per IP
- [✓] Modification rate limiting: 10 requests per minute per IP
- [✓] Input sanitization escapes HTML entities and trims whitespace
- [✓] Security headers set correctly in production
- [✓] CORS configuration matches Node.js behavior

---

### Task 3.3: Development & Environment Configuration
**Status**: ✓ Completed

**Purpose**: Implement development endpoints and environment-specific configurations

**Sub-tasks:**
- [x] Create development-only authentication endpoints
- [x] Configure environment-specific properties
- [x] Implement authentication bypass for development (with safety checks)
- [x] Create profile-specific configurations
- [x] Test environment switching

**Development Controller:**

`DevController.java`:
```java
@RestController
@RequestMapping("/api/auth/dev")
@Profile("development")
public class DevController {
    
    @Autowired
    private Environment environment;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("authEnabled", environment.getProperty("app.auth.enabled", "true"));
        status.put("environment", environment.getProperty("spring.profiles.active", "development"));
        status.put("devMode", "false".equals(environment.getProperty("app.auth.enabled")));
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleAuth(@RequestBody Map<String, Boolean> request) {
        // Implementation to toggle authentication for development testing
        // Same functionality as Node.js dev endpoints
        
        Boolean enabled = request.get("enabled");
        System.setProperty("app.auth.enabled", enabled.toString());
        
        Map<String, Object> response = new HashMap<>();
        response.put("authEnabled", enabled.toString());
        response.put("message", String.format("Authentication %s (restart required for full effect)", 
                                            enabled ? "enabled" : "disabled"));
        
        return ResponseEntity.ok(response);
    }
}
```

**Environment-Specific Properties:**

`application-development.yml`:
```yaml
app:
  auth:
    enabled: true
    bypass-allowed: true
  cors:
    allowed-origins: "*"
  security:
    headers-enabled: false
    
logging:
  level:
    com.namhatta: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

`application-production.yml`:
```yaml
app:
  auth:
    enabled: true
    bypass-allowed: false
  cors:
    allowed-origins: ${ALLOWED_ORIGINS:https://*.replit.app}
  security:
    headers-enabled: true
    
logging:
  level:
    com.namhatta: INFO
    org.springframework.security: WARN
```

**Authentication Bypass Configuration:**

`AuthBypassFilter.java`:
```java
@Component
@ConditionalOnProperty(name = "app.auth.enabled", havingValue = "false")
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "development")
public class AuthBypassFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        // CRITICAL: Same safety checks as Node.js implementation
        String environment = System.getProperty("spring.profiles.active", "development");
        String authEnabled = System.getProperty("app.auth.enabled", "true");
        
        if ("false".equals(authEnabled) && "production".equals(environment)) {
            log.error("🚨 SECURITY ERROR: Authentication bypass attempted in production!");
            ((HttpServletResponse) response).setStatus(500);
            return;
        }
        
        if ("false".equals(authEnabled) && "development".equals(environment)) {
            log.warn("⚠️ WARNING: Authentication bypass is active in development mode");
            
            // Set mock user (same as Node.js)
            MockUser mockUser = new MockUser(1L, "dev-user", "ADMIN", Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(mockUser, null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        }
        
        chain.doFilter(request, response);
    }
}
```

**Validation Criteria:**
- [✓] Development endpoints only available in development profile
- [✓] Authentication bypass works with safety checks (never in production)
- [✓] Environment-specific CORS and security configurations
- [✓] Profile-based logging configurations
- [✓] Mock user setup matches Node.js dev behavior

---

### Task 3.4: User Authentication Service
**Status**: ✓ Completed

**Sub-tasks:**
- [x] Create `AuthService.java` for login/logout logic
- [x] Create `SessionService.java` for session management  
- [x] Create `UserDetailsServiceImpl.java` for Spring Security
- [x] Implement password validation with BCrypt
- [x] Test with existing users (admin, office1, supervisor1)

**Authentication Service:**

`AuthService.java`:
```java
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization services")
public class AuthService {
    
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Operation(summary = "Authenticate user", description = "Authenticate user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "423", description = "Account disabled")
    })
    public LoginResponse authenticate(LoginRequest request) {
        log.info("Starting authentication for user: {}", request.getUsername());
        
        try {
            // Find user (same logic as Node.js)
            log.debug("Looking up user in database: {}", request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new BadCredentialsException("Invalid credentials");
                });
            
            log.debug("User found, checking if account is active: {}", user.getUsername());
            // Check if user is active
            if (!user.getIsActive()) {
                log.warn("Attempt to login with disabled account: {}", user.getUsername());
                throw new BadCredentialsException("User account is disabled");
            }
            
            // Validate password
            log.debug("Validating password for user: {}", user.getUsername());
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                log.warn("Invalid password attempt for user: {}", user.getUsername());
                throw new BadCredentialsException("Invalid credentials");
            }
            
            // Create session (enforces single login)
            log.debug("Creating session for user: {}", user.getUsername());
            String sessionToken = sessionService.createSession(user);
            
            // Generate JWT token
            log.debug("Generating JWT token for user: {}", user.getUsername());
            String jwtToken = tokenProvider.createToken(user, sessionToken);
            
            // Prepare response
            log.debug("Preparing authentication response for user: {}", user.getUsername());
            UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .districts(user.getDistricts().stream()
                    .map(d -> {
                        log.trace("Adding district to user response: {}", d.getDistrictNameEnglish());
                        return new DistrictDto(d.getCode(), d.getDistrictNameEnglish());
                    })
                    .collect(Collectors.toList()))
                .build();
            
            LoginResponse response = LoginResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
                
            log.info("Authentication successful for user: {}", user.getUsername());
            return response;
            
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getUsername(), e);
            throw new RuntimeException("Authentication process failed", e);
        }
    }
    
    @Operation(summary = "Logout user", description = "Logout user and invalidate session")
    public void logout(String username, String sessionToken) {
        log.info("Starting logout process for user: {}", username);
        
        try {
            log.debug("Invalidating session for user: {}", username);
            // Invalidate session
            sessionService.invalidateSession(username, sessionToken);
            
            log.debug("Adding token to blacklist for user: {}", username);
            // Add token to blacklist (same as Node.js)
            // Implementation similar to current JWT blacklist table
            tokenProvider.blacklistToken(sessionToken);
            
            log.info("Logout successful for user: {}", username);
        } catch (Exception e) {
            log.error("Error during logout for user: {}", username, e);
            throw new RuntimeException("Logout process failed", e);
        }
    }
}
```

**Validation Criteria:**
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Can authenticate with existing users
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Password validation works with BCrypt
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Session management enforces single login
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - Logout invalidates sessions and tokens
- ☐ **Status**: ☐ Not Started | ☐ In Progress | ☐ Completed - District information included for supervisors

---

## Phase 4: Service Layer Implementation

### Task 4.1: Core Business Services
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Create `DevoteeService.java` with all CRUD operations
- [ ] Create `NamhattaService.java` with filtering logic
- [ ] Create `GeographicService.java` for location data
- [ ] Create `DashboardService.java` for statistics
- [ ] Implement district-based filtering for supervisors

**Devotee Service:**

`DevoteeService.java`:
```java
@Service
@Transactional
public class DevoteeService {
    
    @Autowired
    private DevoteeRepository devoteeRepository;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private NamhattaRepository namhattaRepository;
    
    @Autowired
    private DevotionalStatusRepository statusRepository;
    
    public Page<DevoteeDto> getFilteredDevotees(
            List<String> allowedDistricts,
            String status,
            String search,
            String sortBy,
            String sortOrder,
            int page,
            int size) {
        
        // Create pageable with sorting (same logic as Node.js)
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        String sortField = "name".equals(sortBy) ? "legalName" : 
                          "createdAt".equals(sortBy) ? "createdAt" : "legalName";
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortField));
        
        // Apply district-based filtering for supervisors
        Page<Devotee> devotees = devoteeRepository.findFilteredDevotees(
            allowedDistricts, status, search, pageable);
        
        // Convert to DTOs with address information
        return devotees.map(this::convertToDto);
    }
    
    public List<DevoteeDto> getDevoteesByNamhatta(Long namhattaId, List<String> allowedDistricts) {
        List<Devotee> devotees = devoteeRepository.findByNamhattaId(namhattaId);
        
        // Filter by allowed districts for supervisors
        if (allowedDistricts != null && !allowedDistricts.isEmpty()) {
            devotees = devotees.stream()
                .filter(d -> hasAccessToDevotee(d, allowedDistricts))
                .collect(Collectors.toList());
        }
        
        return devotees.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public DevoteeDto createDevotee(CreateDevoteeDto dto) {
        // Validate namhatta exists and user has access
        if (dto.getNamhattaId() != null) {
            Namhatta namhatta = namhattaRepository.findById(dto.getNamhattaId())
                .orElseThrow(() -> new EntityNotFoundException("Namhatta not found"));
        }
        
        // Create devotee entity
        Devotee devotee = new Devotee();
        devotee.setLegalName(dto.getLegalName());
        devotee.setName(dto.getName());
        devotee.setDob(dto.getDob());
        devotee.setEmail(dto.getEmail());
        devotee.setPhone(dto.getPhone());
        // ... set all other fields
        
        // Handle devotional status
        if (dto.getDevotionalStatusId() != null) {
            DevotionalStatus status = statusRepository.findById(dto.getDevotionalStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Devotional status not found"));
            devotee.setDevotionalStatus(status);
        }
        
        // Save devotee first
        devotee = devoteeRepository.save(devotee);
        
        // Handle addresses (present and permanent)
        if (dto.getPresentAddress() != null) {
            addressService.createDevoteeAddress(devotee, dto.getPresentAddress(), "present");
        }
        
        if (dto.getPermanentAddress() != null) {
            addressService.createDevoteeAddress(devotee, dto.getPermanentAddress(), "permanent");
        }
        
        return convertToDto(devotee);
    }
    
    private DevoteeDto convertToDto(Devotee devotee) {
        // Convert entity to DTO with all address information
        // Same structure as current Node.js API response
        return DevoteeDto.builder()
            .id(devotee.getId())
            .legalName(devotee.getLegalName())
            .name(devotee.getName())
            .dob(devotee.getDob())
            .email(devotee.getEmail())
            .phone(devotee.getPhone())
            // ... all other fields
            .addresses(devotee.getAddresses().stream()
                .map(this::convertAddressToDto)
                .collect(Collectors.toList()))
            .build();
    }
}
```

**Geographic Service:**

`GeographicService.java`:
```java
@Service
public class GeographicService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    public List<String> getCountries() {
        return addressRepository.findDistinctCountries();
    }
    
    public List<String> getStatesByCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return addressRepository.findDistinctStatesByCountry(country);
    }
    
    public List<String> getDistrictsByState(String country, String state) {
        if (state == null || state.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return addressRepository.findDistinctDistrictsByState(country, state);
    }
    
    public List<String> getSubDistrictsByDistrict(String country, String state, String district, String pincode) {
        // Same logic as Node.js - filter by pincode if provided
        if (pincode != null && !pincode.trim().isEmpty()) {
            return addressRepository.findDistinctSubDistrictsByPincode(pincode);
        }
        
        if (district == null || district.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return addressRepository.findDistinctSubDistrictsByDistrict(country, state, district);
    }
    
    public List<String> getVillagesBySubDistrict(String country, String state, String district, 
                                                String subDistrict, String pincode) {
        // Same logic as Node.js - filter by both subDistrict AND pincode when both provided
        if (subDistrict != null && !subDistrict.trim().isEmpty() && 
            pincode != null && !pincode.trim().isEmpty()) {
            return addressRepository.findDistinctVillagesBySubDistrictAndPincode(subDistrict, pincode);
        }
        
        if (subDistrict == null || subDistrict.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return addressRepository.findDistinctVillagesBySubDistrict(country, state, district, subDistrict);
    }
    
    public PincodeSearchResponse searchPincodes(String country, String searchTerm, int page, int limit) {
        // Same logic as Node.js storage-db.ts
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        Page<String> pincodes;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            pincodes = addressRepository.findDistinctPincodesByCountryAndSearch(country, searchTerm, pageable);
        } else {
            pincodes = addressRepository.findDistinctPincodesByCountry(country, pageable);
        }
        
        return PincodeSearchResponse.builder()
            .pincodes(pincodes.getContent())
            .total((int) pincodes.getTotalElements())
            .hasMore(pincodes.hasNext())
            .build();
    }
    
    public AddressInfoDto getAddressByPincode(String pincode) {
        // Same logic as Node.js - get first address with this pincode
        Optional<Address> address = addressRepository.findFirstByPincode(pincode);
        
        if (address.isPresent()) {
            Address addr = address.get();
            return AddressInfoDto.builder()
                .country(addr.getCountry())
                .state(addr.getStateNameEnglish())
                .district(addr.getDistrictNameEnglish())
                .build();
        }
        
        return null;
    }
}
```

**Validation Criteria:**
- [ ] All geographic endpoints return same data as Node.js
- [ ] Pincode search works with pagination
- [ ] Address lookup by pincode works correctly
- [ ] Sub-district/village filtering by pincode works
- [ ] Empty parameters handled gracefully

---

## Phase 5: REST Controllers & API Layer

### Task 5.1: Authentication Controller
**Status**: ☐ Not Started | ☐ In Progress | ✓ Completed

**Purpose**: Convert all `/api/auth/*` endpoints from Express to Spring Boot

**Sub-tasks:**
- [✓] Create `AuthController.java` with login/logout endpoints
- [✓] Implement HTTP-only cookie handling
- [✓] Create user verification endpoint
- [✓] Add development endpoints for user testing
- [✓] Test authentication flow end-to-end

`AuthController.java`:
```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(credentials = true)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        try {
            LoginResponse loginResponse = authService.authenticate(request);
            
            // Set HTTP-only cookie (same as Node.js)
            Cookie cookie = new Cookie("auth_token", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Set to true in production
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 24 hours
            response.addCookie(cookie);
            
            return ResponseEntity.ok(loginResponse);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse.builder()
                    .error("Invalid credentials")
                    .build());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        try {
            // Get user info from JWT token
            String token = getTokenFromRequest(request);
            if (token != null) {
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);
                String username = claims.getSubject();
                String sessionToken = claims.get("sessionToken", String.class);
                
                // Logout user (invalidate session)
                authService.logout(username, sessionToken);
            }
            
            // Clear HTTP-only cookie
            Cookie cookie = new Cookie("auth_token", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", "Logged out successfully");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        
        try {
            String token = getTokenFromRequest(request);
            
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String username = claims.getSubject();
            String sessionToken = claims.get("sessionToken", String.class);
            
            // Validate session
            if (!sessionService.isSessionValid(username, sessionToken)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            // Return user info (same format as Node.js)
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            user.put("id", claims.get("userId", Long.class));
            user.put("username", username);
            user.put("role", claims.get("role", String.class));
            user.put("districts", claims.get("districts", List.class));
            
            result.put("user", user);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Session expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    // Development endpoint to check users (same as Node.js)
    @GetMapping("/dev/users")
    public ResponseEntity<Map<String, Object>> getDevUsers() {
        if (!"development".equals(environment)) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> users = authService.getDevUsers();
        return ResponseEntity.ok(users);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // Same logic as JWT filter
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
```

**Validation Criteria:**
- [ ] Login works with existing users (admin, office1, supervisor1)
- [ ] HTTP-only cookies set correctly
- [ ] Logout clears cookies and invalidates sessions
- [ ] Token verification returns correct user info
- [ ] Development endpoints work in dev mode only

---

### Task 5.2: Devotee Controller
**Status**: ☐ Not Started | ✓ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Create `DevoteeController.java` with all CRUD endpoints
- [ ] Implement filtering, sorting, and pagination
- [ ] Add district-based access control
- [ ] Create devotee-specific endpoints for namhattas
- [ ] Test all endpoints with existing data

`DevoteeController.java`:
```java
@RestController
@RequestMapping("/api/devotees")
@Validated
public class DevoteeController {
    
    @Autowired
    private DevoteeService devoteeService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDevotees(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            HttpServletRequest request) {
        
        // Get user districts from JWT token
        List<String> allowedDistricts = (List<String>) request.getAttribute("userDistricts");
        String userRole = (String) request.getAttribute("userRole");
        
        // Apply district filtering only for supervisors
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            // Convert district codes to district names (same logic as Node.js)
            allowedDistricts = convertDistrictCodesToNames(allowedDistricts);
        } else {
            allowedDistricts = null; // Admin and Office can see all
        }
        
        Page<DevoteeDto> devotees = devoteeService.getFilteredDevotees(
            allowedDistricts, status, search, sortBy, sortOrder, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", devotees.getContent());
        response.put("total", devotees.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", devotees.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DevoteeDto> getDevotee(@PathVariable Long id, HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        DevoteeDto devotee = devoteeService.getDevoteeById(id, allowedDistricts);
        
        if (devotee == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(devotee);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<DevoteeDto> createDevotee(@Valid @RequestBody CreateDevoteeDto dto) {
        
        DevoteeDto createdDevotee = devoteeService.createDevotee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevotee);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<DevoteeDto> updateDevotee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDevoteeDto dto,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        DevoteeDto updatedDevotee = devoteeService.updateDevotee(id, dto, allowedDistricts);
        
        return ResponseEntity.ok(updatedDevotee);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDevotee(@PathVariable Long id) {
        
        devoteeService.deleteDevotee(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Devotee deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    private List<String> getAllowedDistricts(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            List<String> districtCodes = (List<String>) request.getAttribute("userDistricts");
            return convertDistrictCodesToNames(districtCodes);
        }
        
        return null; // Admin and Office can access all
    }
}
```

**Validation Criteria:**
- [ ] All CRUD operations work correctly
- [ ] District filtering works for supervisors
- [ ] Pagination and sorting match Node.js behavior
- [ ] Role-based access control enforced
- [ ] Response format matches current API exactly

---

### Task 5.3: Namhatta Controller
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Create `NamhattaController.java` with all endpoints
- [ ] Implement namhatta-specific devotee endpoints
- [ ] Add address update functionality
- [ ] Create update management endpoints
- [ ] Test with existing namhatta data

**Validation Criteria:**
- [ ] All namhatta endpoints work correctly
- [ ] Address updates work properly
- [ ] Devotee assignment/management works
- [ ] District filtering applied for supervisors
- [ ] Response format matches Node.js API

---

### Task 5.4: Geographic & Dashboard Controllers
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Create `GeographicController.java` for location endpoints
- [ ] Create `DashboardController.java` for statistics
- [ ] Create `StatusController.java` for devotional statuses
- [ ] Create `HierarchyController.java` for leadership

---

### Task 5.5: Map Data & Statistics Controllers
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement map data APIs for namhatta distribution statistics by geographic levels

**Sub-tasks:**
- [ ] Create `MapDataController.java` for namhatta count statistics
- [ ] Implement geographic hierarchy statistics (country, state, district, sub-district)
- [ ] Add district-based filtering for supervisor access
- [ ] Test all map data endpoints

**Map Data Controller:**

`MapDataController.java`:
```java
@RestController
@RequestMapping("/api/map")
@Validated
public class MapDataController {
    
    @Autowired
    private MapDataService mapDataService;
    
    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountriesWithNamhattaCount(
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<Map<String, Object>> countries = mapDataService.getCountriesWithNamhattaCount(allowedDistricts);
        
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<Map<String, Object>>> getStatesWithNamhattaCount(
            @RequestParam String country,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<Map<String, Object>> states = mapDataService.getStatesWithNamhattaCount(country, allowedDistricts);
        
        return ResponseEntity.ok(states);
    }
    
    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<Map<String, Object>> districts = mapDataService.getDistrictsWithNamhattaCount(
                country, state, allowedDistricts);
        
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/subdistricts")
    public ResponseEntity<List<Map<String, Object>>> getSubDistrictsWithNamhattaCount(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<Map<String, Object>> subdistricts = mapDataService.getSubDistrictsWithNamhattaCount(
                country, state, district, allowedDistricts);
        
        return ResponseEntity.ok(subdistricts);
    }
    
    @GetMapping("/namhattas")
    public ResponseEntity<List<Map<String, Object>>> getNamhattasBySubDistrict(
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String district,
            @RequestParam String subdistrict,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<Map<String, Object>> namhattas = mapDataService.getNamhattasBySubDistrict(
                country, state, district, subdistrict, allowedDistricts);
        
        return ResponseEntity.ok(namhattas);
    }
    
    private List<String> getAllowedDistricts(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            List<String> districtCodes = (List<String>) request.getAttribute("userDistricts");
            return convertDistrictCodesToNames(districtCodes);
        }
        
        return null; // Admin and Office can access all
    }
}
```

**Validation Criteria:**
- [ ] All map data endpoints return correct namhatta counts
- [ ] District filtering works for supervisors
- [ ] Response format matches Node.js API exactly
- [ ] Geographic hierarchy navigation works properly
- [ ] Performance is optimized for large datasets

---

### Task 5.6: Updates & Admin Controllers
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement namhatta updates and admin user management APIs

**Sub-tasks:**
- [ ] Create `UpdatesController.java` for namhatta program updates
- [ ] Create `AdminController.java` for user management
- [ ] Implement supervisor registration endpoints
- [ ] Add user profile management endpoints
- [ ] Test all admin and update endpoints

**Updates Controller:**

`UpdatesController.java`:
```java
@RestController
@RequestMapping("/api/updates")
@Validated
public class UpdatesController {
    
    @Autowired
    private UpdatesService updatesService;
    
    @GetMapping
    public ResponseEntity<List<UpdateDto>> getUpdates(
            @RequestParam(required = false) Long namhattaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        List<String> allowedDistricts = getAllowedDistricts(request);
        List<UpdateDto> updates = updatesService.getUpdates(namhattaId, page, size, allowedDistricts);
        
        return ResponseEntity.ok(updates);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICE')")
    public ResponseEntity<UpdateDto> createUpdate(@Valid @RequestBody CreateUpdateDto dto) {
        
        UpdateDto createdUpdate = updatesService.createUpdate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUpdate);
    }
    
    private List<String> getAllowedDistricts(HttpServletRequest request) {
        String userRole = (String) request.getAttribute("userRole");
        
        if ("DISTRICT_SUPERVISOR".equals(userRole)) {
            List<String> districtCodes = (List<String>) request.getAttribute("userDistricts");
            return convertDistrictCodesToNames(districtCodes);
        }
        
        return null; // Admin and Office can access all
    }
}
```

**Admin Controller:**

`AdminController.java`:
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Page<UserDto> users = userService.getUsers(page, size, search);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto dto) {
        
        UserDto createdUser = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PostMapping("/supervisor-registration")
    public ResponseEntity<UserDto> registerSupervisor(@Valid @RequestBody SupervisorRegistrationDto dto) {
        
        UserDto supervisor = userService.registerSupervisor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisor);
    }
    
    @GetMapping("/district-supervisors")
    public ResponseEntity<List<UserDto>> getDistrictSupervisors(@RequestParam String district) {
        
        List<UserDto> supervisors = userService.getDistrictSupervisors(district);
        return ResponseEntity.ok(supervisors);
    }
    
    @GetMapping("/user-address-defaults/{userId}")
    public ResponseEntity<AddressDefaultsDto> getUserAddressDefaults(@PathVariable Long userId) {
        
        AddressDefaultsDto defaults = userService.getUserAddressDefaults(userId);
        return ResponseEntity.ok(defaults);
    }
}
```

**Validation Criteria:**
- [ ] Updates CRUD operations work correctly
- [ ] User management endpoints functional
- [ ] Supervisor registration workflow works
- [ ] District-based filtering for updates
- [ ] Role-based access control enforced

---

## Phase 6: Error Handling & Infrastructure

### Task 6.1: Global Exception Handling
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement comprehensive error handling matching Node.js error responses

**Sub-tasks:**
- [ ] Create `GlobalExceptionHandler.java` for centralized error handling
- [ ] Implement custom exceptions for domain-specific errors
- [ ] Configure validation error responses
- [ ] Add logging for all exceptions
- [ ] Test error scenarios

**Global Exception Handler:**

`GlobalExceptionHandler.java`:
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .error("Validation failed")
            .message(ex.getMessage())
            .details(ex.getDetails())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .error("Resource not found")
            .message(ex.getMessage())
            .build();
            
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .error("Access denied")
            .message("You don't have permission to access this resource")
            .build();
            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .error("Invalid credentials")
            .build();
            
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .error("Internal server error")
            .message("An unexpected error occurred")
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

**Error Response DTO:**

`ErrorResponse.java`:
```java
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private List<String> details;
    private String timestamp;
    
    @JsonIgnore
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder().timestamp(Instant.now().toString());
    }
}
```

**Validation Criteria:**
- [ ] All exception types handled appropriately
- [ ] Error response format matches Node.js exactly
- [ ] Validation errors include detailed field information
- [ ] Security exceptions don't leak sensitive information
- [ ] All errors are properly logged

---

### Task 6.2: Monitoring & Health Checks
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Purpose**: Implement application monitoring and health checks

**Sub-tasks:**
- [ ] Configure Spring Boot Actuator
- [ ] Create custom health indicators
- [ ] Set up application metrics
- [ ] Configure logging levels
- [ ] Test monitoring endpoints

**Health Check Configuration:**

`HealthCheckConfig.java`:
```java
@Configuration
public class HealthCheckConfig {
    
    @Bean
    public HealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource);
    }
    
    @Bean
    public HealthIndicator authHealthIndicator() {
        return () -> {
            try {
                // Check if JWT secret is configured
                String jwtSecret = environment.getProperty("jwt.secret");
                if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                    return Health.down().withDetail("reason", "JWT secret not configured").build();
                }
                
                return Health.up().withDetail("status", "Authentication system healthy").build();
            } catch (Exception e) {
                return Health.down(e).build();
            }
        };
    }
}
```

**Validation Criteria:**
- [ ] `/api/health` endpoint matches Node.js response
- [ ] Database health check works
- [ ] Custom health indicators report correctly
- [ ] Metrics are collected properly
- [ ] Logging configuration is appropriate
- [ ] Test all public and protected endpoints

**Validation Criteria:**
- [ ] All geographic endpoints return correct data
- [ ] Dashboard statistics match Node.js calculations
- [ ] Status distribution works correctly
- [ ] Leadership hierarchy displays properly
- [ ] Public endpoints accessible without authentication

---

## Phase 6: Testing & Validation

### Task 6.1: Unit Testing
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Write tests for all service classes
- [ ] Write tests for repository methods
- [ ] Write tests for authentication logic
- [ ] Write tests for geographic services
- [ ] Achieve 80%+ code coverage

### Task 6.2: Integration Testing
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Test all REST endpoints with TestRestTemplate
- [ ] Test authentication flows end-to-end
- [ ] Test district-based filtering for supervisors
- [ ] Test address CRUD operations
- [ ] Compare API responses with Node.js version

### Task 6.3: Performance Testing
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Load test with existing data volume
- [ ] Compare response times with Node.js
- [ ] Test database connection pooling
- [ ] Optimize slow queries
- [ ] Validate memory usage

---

## Phase 7: Deployment & Migration

### Task 7.1: Production Configuration
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Create production application.yml
- [ ] Configure environment variables
- [ ] Set up logging configuration
- [ ] Configure SSL and security headers
- [ ] Test with production database

### Task 7.2: Replit Deployment Setup
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Configure Replit deployment settings
- [ ] Set up environment variables in Replit Secrets
- [ ] Test deployment process
- [ ] Configure auto-scaling if needed
- [ ] Set up monitoring and health checks

### Task 7.3: Migration Execution
**Status**: ☐ Not Started | ☐ In Progress | ☐ Completed

**Sub-tasks:**
- [ ] Run final comparison tests
- [ ] Switch frontend API calls to Spring Boot
- [ ] Monitor system performance
- [ ] Verify all functionality works
- [ ] Keep Node.js version as backup

---

## Timeline Summary

### Week 1: Foundation (Tasks 1.1 - 2.3)
- **Days 1-2**: Project setup, database connection, entity mapping
- **Days 3-4**: Repository layer, basic queries, relationship testing
- **Days 5-7**: Security configuration, JWT implementation, authentication

### Week 2: Core Implementation (Tasks 3.1 - 4.1)
- **Days 8-9**: Complete authentication system, session management
- **Days 10-11**: Business services, district filtering, CRUD operations
- **Days 12-14**: REST controllers, endpoint implementation, API testing

### Week 3: Completion & Migration (Tasks 5.1 - 7.3)
- **Days 15-16**: Geographic services, dashboard, remaining endpoints
- **Days 17-18**: Comprehensive testing, performance optimization
- **Days 19-21**: Production deployment, migration execution, monitoring

## Success Criteria Checklist

### Functional Requirements
- [ ] All 25+ API endpoints work identically to Node.js version
- [ ] JWT authentication with HTTP-only cookies functional
- [ ] Role-based access control (ADMIN, OFFICE, DISTRICT_SUPERVISOR) working
- [ ] District-based data filtering for supervisors implemented
- [ ] Geographic data hierarchy and filtering operational
- [ ] Pagination, sorting, search functionality preserved
- [ ] Address CRUD operations with landmark handling working
- [ ] Session management with single login enforcement active

### Technical Requirements
- [ ] Spring Boot application starts successfully on port 5000
- [ ] Connects to existing PostgreSQL database without schema changes
- [ ] Maven builds and runs in Replit environment
- [ ] Response times within 10% of Node.js version
- [ ] Memory usage optimized
- [ ] Error handling matches Node.js behavior
- [ ] Logging configuration appropriate for debugging

### Security Requirements
- [ ] Password validation with BCrypt working
- [ ] JWT token validation identical to Node.js
- [ ] Session invalidation and token blacklisting functional
- [ ] CORS configuration appropriate for frontend
- [ ] SQL injection protection via JPA parameterized queries
- [ ] Input validation on all endpoints

This comprehensive migration plan provides granular, checklistable tasks that can be tracked as "Not Started", "In Progress", or "Completed", making the migration process manageable and transparent.
```

### Phase 6: Configuration & Properties (Day 7)

#### 6.1 Application Configuration
```yaml
# application.yml
spring:
  application:
    name: namhatta-management-system
  
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        
  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours

session:
  secret: ${SESSION_SECRET}
  
server:
  port: ${PORT:5000}
  
logging:
  level:
    com.namhatta: DEBUG
    org.springframework.security: DEBUG
```

### Phase 7: Testing & Validation (Day 8-9)

#### 7.1 Unit Tests
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class DevoteeServiceTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("namhatta_test")
            .withUsername("test")
            .withPassword("test");
    
    @Test
    void shouldCreateDevoteeWithAddress() {
        // Test devotee creation
    }
    
    @Test
    void shouldFilterDevoteesByDistrict() {
        // Test district-based filtering
    }
}
```

#### 7.2 Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerIntegrationTest {
    
    @Test
    void shouldAuthenticateUserAndSetCookie() {
        // Test complete authentication flow
    }
}
```

### Phase 8: Deployment Configuration (Day 9-10)

#### 8.1 Docker Configuration
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/namhatta-management-system-1.0.jar app.jar

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 8.2 Replit Configuration
```bash
# .replit
run = "mvn spring-boot:run"
entrypoint = "src/main/java/com/namhatta/NamhattaApplication.java"

[languages.java]
pattern = "**/*.java"

[nix]
channel = "stable-22_11"

[deployment]
run = ["mvn", "clean", "package", "-DskipTests", "&&", "java", "-jar", "target/namhatta-management-system-1.0.jar"]
```

## Migration Execution Plan

### Week 1: Core Migration
- **Day 1-2**: Project setup, dependencies, basic configuration
- **Day 3-4**: Entity mapping, repository layer
- **Day 5-6**: Security configuration, JWT implementation
- **Day 7**: Service layer for core entities (Devotee, Namhatta)

### Week 2: Feature Completion
- **Day 8-9**: Complete all service implementations
- **Day 10-11**: REST controller implementation
- **Day 12-13**: Authentication system, role-based access
- **Day 14**: Testing, validation, bug fixes

### Week 3: Advanced Features & Deployment
- **Day 15-16**: Geographic services, dashboard APIs
- **Day 17-18**: Complete testing suite
- **Day 19-20**: Performance optimization, caching
- **Day 21**: Production deployment configuration

## Key Migration Considerations

### 1. Database Compatibility
- Keep existing PostgreSQL schema unchanged
- Use Flyway for any necessary schema updates
- Maintain all foreign key relationships

### 2. API Compatibility
- Maintain exact same REST endpoints
- Keep identical request/response formats
- Preserve authentication flow with HTTP-only cookies

### 3. Authentication Migration
- Port JWT implementation exactly
- Maintain session-based single login enforcement
- Keep role-based access control logic

### 4. Data Access Patterns
- Implement same filtering logic for district supervisors
- Maintain pagination, sorting, search functionality
- Keep geographic data hierarchy intact

### 5. Performance Considerations
- Implement JPA query optimization
- Add appropriate database indexes
- Consider caching for geographic data

## Risk Mitigation

### 1. Parallel Development
- Keep Node.js version running during migration
- Use feature flags for gradual rollout
- Maintain database schema compatibility

### 2. Testing Strategy
- Comprehensive unit test coverage
- Integration tests for all endpoints
- Load testing with existing data volume

### 3. Rollback Plan
- Database backup before migration
- Keep Node.js deployment ready
- Environment-based routing for gradual migration

### 4. Data Validation
- Compare API responses between versions
- Validate authentication flows
- Test all role-based access scenarios

## Success Criteria

### Functional Requirements
- ✅ All 25+ API endpoints working identically
- ✅ Authentication system with HTTP-only cookies
- ✅ Role-based access control (ADMIN, OFFICE, DISTRICT_SUPERVISOR)
- ✅ District-based data filtering for supervisors
- ✅ Geographic data hierarchy intact
- ✅ Pagination, sorting, search functionality

### Performance Requirements
- Response times within 10% of current system
- Support for current user load (concurrent sessions)
- Database query optimization maintained

### Security Requirements
- JWT token validation identical
- Password hashing compatibility
- Session management with single login enforcement
- Token blacklisting for secure logout

## Tools & Libraries Required

### Core Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

This comprehensive plan provides a structured approach to migrating your Namhatta Management System to Spring Boot while maintaining all existing functionality and ensuring a smooth transition.