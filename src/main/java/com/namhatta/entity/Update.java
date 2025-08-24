package com.namhatta.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Update entity - Represents program updates for namhattas
 */
@Entity
@Table(name = "namhatta_updates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"namhatta"})
@Slf4j
@Schema(description = "Program update information for namhattas")
public class Update {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Update unique identifier", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "namhatta_id", nullable = false)
    @Schema(description = "Associated namhatta")
    private Namhatta namhatta;
    
    @Column(name = "program_type", nullable = false)
    @Schema(description = "Type of program", example = "Weekly Meeting")
    private String programType;
    
    @Column(name = "date", nullable = false)
    @Schema(description = "Date of the program", example = "2024-01-15")
    private String date;
    
    @Column(name = "attendance", nullable = false)
    @Schema(description = "Number of attendees", example = "25")
    private Integer attendance;
    
    @Column(name = "prasad_distribution")
    @Schema(description = "Number of prasad distributed", example = "30")
    private Integer prasadDistribution;
    
    @Column(name = "nagar_kirtan")
    @Builder.Default
    @Schema(description = "Number of nagar kirtan participants", example = "15")
    private Integer nagarKirtan = 0;
    
    @Column(name = "book_distribution")
    @Builder.Default
    @Schema(description = "Number of books distributed", example = "5")
    private Integer bookDistribution = 0;
    
    @Column(name = "chanting")
    @Builder.Default
    @Schema(description = "Number of chanting participants", example = "20")
    private Integer chanting = 0;
    
    @Column(name = "arati")
    @Builder.Default
    @Schema(description = "Number of arati participants", example = "18")
    private Integer arati = 0;
    
    @Column(name = "bhagwat_path")
    @Builder.Default
    @Schema(description = "Number of bhagwat path participants", example = "12")
    private Integer bhagwatPath = 0;
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    @Schema(description = "JSON array of image URLs", example = "[\"https://example.com/image1.jpg\"]")
    private String imageUrls;
    
    @Column(name = "facebook_link")
    @Schema(description = "Facebook post link", example = "https://facebook.com/post/123")
    private String facebookLink;
    
    @Column(name = "youtube_link")
    @Schema(description = "YouTube video link", example = "https://youtube.com/watch?v=xyz")
    private String youtubeLink;
    
    @Column(name = "special_attraction")
    @Schema(description = "Special attractions or events", example = "Guest speaker from Mayapur")
    private String specialAttraction;
    
    @Column(name = "created_at")
    @Builder.Default
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PostLoad
    protected void onLoad() {
        log.trace("Loaded update entity: {} for namhatta {} on {}", id, 
                 namhatta != null ? namhatta.getId() : "null", date);
    }
}