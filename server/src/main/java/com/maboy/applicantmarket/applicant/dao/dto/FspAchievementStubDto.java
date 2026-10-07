package com.maboy.applicantmarket.applicant.dao.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "applicant_fsp_achievements_stub")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FspAchievementStubDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "event_name", nullable = false, length = 255)
    private String eventName;

    @Column(name = "event_date")
    private LocalDate eventDate;

    private Integer place;

    @Column(length = 120)
    private String category;

    @Column(nullable = false)
    private Boolean verified;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (verified == null) verified = false;
        if (source == null) source = "STUB";
    }
}