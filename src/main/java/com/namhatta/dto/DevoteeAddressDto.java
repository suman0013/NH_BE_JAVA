package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevoteeAddressDto {
    
    private Long id;
    private String addressType; // 'present' or 'permanent'
    private String landmark;
    private AddressDto address;
}