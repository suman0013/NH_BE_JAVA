package com.namhatta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressDto {
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private String stateNameEnglish;
    private String districtNameEnglish;
    private String subdistrictNameEnglish;
    private String villageNameEnglish;
    private String pincode;
    private String landmark;
}