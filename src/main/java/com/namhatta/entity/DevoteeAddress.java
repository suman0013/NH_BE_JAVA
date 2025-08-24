package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "devotee_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"devotee", "address"})
@Slf4j
@Schema(description = "Junction table linking devotees to their addresses")
public class DevoteeAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Association unique identifier", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devotee_id", nullable = false)
    @Schema(description = "Associated devotee")
    private Devotee devotee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @Schema(description = "Associated address")
    private Address address;
    
    @Column(name = "address_type")
    @Schema(description = "Type of address", example = "present", allowableValues = {"present", "permanent"})
    private String addressType; // 'present' or 'permanent'
    
    @Column(name = "landmark")
    @Schema(description = "Nearby landmark for easy identification", example = "Near City Center Mall")
    private String landmark;
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded devotee address association: devotee {} - {} address with landmark: {}", 
                 devotee != null ? devotee.getId() : "null", addressType, landmark);
    }
}