package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devoteeAddresses", "namhattaAddresses"})
@Slf4j
@Schema(description = "Normalized address data for devotees and namhattas")
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Address unique identifier", example = "1")
    private Long id;
    
    @Column
    @Schema(description = "Country name", example = "India")
    private String country;
    
    @Column(name = "state_name_english")
    @Schema(description = "State name in English", example = "West Bengal")
    private String stateNameEnglish;
    
    @Column(name = "district_name_english")
    @Schema(description = "District name in English", example = "Kolkata")
    private String districtNameEnglish;
    
    @Column(name = "subdistrict_name_english")
    @Schema(description = "Sub-district name in English", example = "Gariahat")
    private String subdistrictNameEnglish;
    
    @Column(name = "village_name_english")
    @Schema(description = "Village name in English", example = "Ballygunge")
    private String villageNameEnglish;
    
    @Column(name = "pincode")
    @Schema(description = "Postal code", example = "700019")
    private String pincode;
    
    // Relationships
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Devotee address associations")
    private List<DevoteeAddress> devoteeAddresses = new ArrayList<>();
    
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Namhatta address associations")
    private List<NamhattaAddress> namhattaAddresses = new ArrayList<>();
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded address entity: {}, {}, {} - {}", villageNameEnglish, 
                 subdistrictNameEnglish, districtNameEnglish, pincode);
    }
}