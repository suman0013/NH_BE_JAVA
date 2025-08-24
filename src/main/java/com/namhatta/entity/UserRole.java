package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role enumeration")
public enum UserRole {
    @Schema(description = "System administrator with full access")
    ADMIN,
    
    @Schema(description = "Office user with limited access")
    OFFICE,
    
    @Schema(description = "District supervisor with district-specific access")
    DISTRICT_SUPERVISOR
}