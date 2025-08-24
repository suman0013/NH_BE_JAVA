package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NamhattaAddressDto {
    
    private Long id;
    private String addressType; // "primary", "postal", etc.
    private String landmark;
    private AddressDto address;
}