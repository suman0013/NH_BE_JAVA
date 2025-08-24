package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    
    private Long id;
    private String country;
    private String stateNameEnglish;
    private String districtNameEnglish;
    private String subdistrictNameEnglish;
    private String villageNameEnglish;
    private String pincode;
}