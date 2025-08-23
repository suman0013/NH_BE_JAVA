# Complete API Specifications for Spring Boot Migration

**CRITICAL**: This document provides EXACT specifications for all 60+ endpoints that must be implemented in Spring Boot. Every endpoint must match the URLs, request/response formats, authentication requirements, and behavior from the Node.js implementation and API_DOCUMENTATION.md.

## Summary of API Categories

### 1. Authentication APIs (6 endpoints)
- POST `/api/auth/login` - User login with rate limiting
- POST `/api/auth/logout` - User logout with token blacklisting  
- GET `/api/auth/verify` - JWT token verification
- GET `/api/auth/user-districts` - User's assigned districts
- GET `/api/auth/dev/status` - Auth status (dev only)
- POST `/api/auth/dev/toggle` - Toggle auth (dev only)

### 2. System APIs (2 endpoints)
- GET `/api/health` - Health check
- GET `/api/about` - System information

### 3. Geography APIs (8 endpoints)
- GET `/api/countries` - All countries
- GET `/api/states?country=string` - States by country
- GET `/api/districts?state=string` - Districts by state
- GET `/api/sub-districts?district=string&pincode=string` - Sub-districts
- GET `/api/villages?subDistrict=string&pincode=string` - Villages
- GET `/api/pincodes?village=string&district=string&subDistrict=string` - Pincodes
- GET `/api/pincodes/search` - Paginated pincode search
- GET `/api/address-by-pincode?pincode=string` - Address lookup

### 4. Map Data APIs (5 endpoints)
- GET `/api/map/countries` - Namhatta counts by country
- GET `/api/map/states` - Namhatta counts by state
- GET `/api/map/districts` - Namhatta counts by district
- GET `/api/map/sub-districts` - Namhatta counts by sub-district
- GET `/api/map/villages` - Namhatta counts by village

### 5. Dashboard APIs (2 endpoints)
- GET `/api/dashboard` - Dashboard summary statistics
- GET `/api/status-distribution` - Devotional status distribution

### 6. Hierarchy APIs (2 endpoints)
- GET `/api/hierarchy` - Top-level hierarchy
- GET `/api/hierarchy/:level` - Leaders by hierarchy level

### 7. Devotees APIs (8 endpoints)
- GET `/api/devotees` - Paginated devotees with filters
- GET `/api/devotees/:id` - Single devotee details
- POST `/api/devotees` - Create new devotee
- POST `/api/devotees/:namhattaId` - Create devotee for namhatta
- PUT `/api/devotees/:id` - Update devotee
- POST `/api/devotees/:id/upgrade-status` - Upgrade devotee status
- GET `/api/devotees/:id/status-history` - Devotee status history
- GET `/api/devotees/:id/addresses` - Devotee addresses

### 8. Namhattas APIs (12 endpoints)
- GET `/api/namhattas` - Paginated namhattas with filters
- GET `/api/namhattas/pending` - Pending approval namhattas
- GET `/api/namhattas/:id` - Single namhatta details
- GET `/api/namhattas/check-code/:code` - Check code uniqueness
- POST `/api/namhattas` - Create new namhatta
- PUT `/api/namhattas/:id` - Update namhatta
- GET `/api/namhattas/:id/devotees` - Devotees in namhatta
- POST `/api/namhattas/:id/approve` - Approve namhatta
- POST `/api/namhattas/:id/reject` - Reject namhatta
- GET `/api/namhattas/:id/updates` - Namhatta program updates
- GET `/api/namhattas/:id/devotee-status-count` - Status counts
- GET `/api/namhattas/:id/status-history` - Status history

### 9. Statuses APIs (3 endpoints)
- GET `/api/statuses` - All devotional statuses
- POST `/api/statuses` - Create new status
- POST `/api/statuses/:id/rename` - Rename status

### 10. Gurudevs APIs (2 endpoints)
- GET `/api/gurudevs` - All spiritual teachers
- POST `/api/gurudevs` - Create new gurudev

### 11. Shraddhakutirs APIs (2 endpoints)
- GET `/api/shraddhakutirs` - Regional units
- POST `/api/shraddhakutirs` - Create new shraddhakutir

### 12. Updates APIs (2 endpoints)
- GET `/api/updates/all` - All namhatta updates
- POST `/api/updates` - Create new update

### 13. Admin APIs (5 endpoints)
- POST `/api/admin/register-supervisor` - Register district supervisor
- GET `/api/admin/users` - Get all users
- GET `/api/admin/available-districts` - Available districts
- PUT `/api/admin/users/:id` - Update user
- DELETE `/api/admin/users/:id` - Deactivate user

### 14. Additional APIs (3 endpoints)
- GET `/api/district-supervisors` - Supervisors by district
- GET `/api/user/address-defaults` - User address defaults
- GET `/api/dev/users` - Development user info

## Rate Limiting Requirements
- **Login API**: 5 attempts per 15 minutes per IP
- **General APIs**: 100 requests per 15 minutes per IP
- **Modification APIs** (POST/PUT/DELETE): 10 requests per minute per IP

## Authentication Requirements
- **Method**: JWT tokens in HTTP-only cookies (NOT Authorization header)
- **Session Duration**: 1 hour
- **Single Login**: Only one active session per user
- **Token Blacklisting**: Logout invalidates tokens immediately
- **Role-based Access**: ADMIN, OFFICE, DISTRICT_SUPERVISOR with district filtering

## District Filtering Rules
For DISTRICT_SUPERVISOR role:
- Can only access devotees/namhattas in their assigned districts
- Applied automatically to GET requests
- Validated on PUT/POST operations
- Enforced at service layer, not just UI

## Error Response Format
All APIs must use consistent error response format:
```json
{
  "error": "Error message",
  "message": "Detailed error message (sometimes)",
  "details": "Validation errors array (for validation failures)"
}
```

## DETAILED ENDPOINT SPECIFICATIONS

[This document would continue with complete specifications for each endpoint matching the API_DOCUMENTATION.md format]

## Implementation Priority

**Phase 1 (Core Authentication & Security)**:
1. Authentication APIs (all 6 endpoints)
2. System APIs (health, about)
3. Spring Security configuration

**Phase 2 (Data Access)**:
1. Geography APIs (all 8 endpoints)
2. Dashboard APIs 
3. Hierarchy APIs

**Phase 3 (Main Business Logic)**:
1. Devotees APIs (all 8 endpoints with district filtering)
2. Namhattas APIs (all 12 endpoints with approval workflow)
3. Statuses, Gurudevs, Shraddhakutirs APIs

**Phase 4 (Supporting Features)**:
1. Updates APIs
2. Map Data APIs
3. Admin APIs
4. Development APIs

Each phase must be completed and tested before moving to the next phase.