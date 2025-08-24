package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "namhatta_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"namhatta", "address"})
@Slf4j
@Schema(description = "Junction table linking namhattas to their addresses")
public class NamhattaAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Association unique identifier", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namhatta_id", nullable = false)
    @Schema(description = "Associated namhatta")
    private Namhatta namhatta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @Schema(description = "Associated address")
    private Address address;
    
    @Column(name = "address_type")
    @Schema(description = "Type of address", example = "meeting_location")
    private String addressType; // typically 'meeting_location'
    
    @Column(name = "landmark")
    @Schema(description = "Nearby landmark for easy identification", example = "Behind Main Temple")
    private String landmark;
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded namhatta address association: namhatta {} - {} with landmark: {}", 
                 namhatta != null ? namhatta.getId() : "null", addressType, landmark);
    }
}