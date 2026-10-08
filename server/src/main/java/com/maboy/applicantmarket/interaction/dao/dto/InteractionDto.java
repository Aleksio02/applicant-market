package com.maboy.applicantmarket.interaction.dao.dto;

import com.maboy.applicantmarket.commons.dao.dto.AbstractEntityDto;
import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "interactions")
public class InteractionDto extends AbstractEntityDto {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;

    @Column(name = "employer_id", nullable = false)
    private UUID employerId;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "vacancy_id")
    private UUID vacancyId;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String message;

    @Column(name = "salary_from", nullable = false)
    private Long salaryFrom;

    @Column(name = "salary_to", nullable = false)
    private Long salaryTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionStatus status;

    @Column(name = "contacts_revealed_at")
    private Instant contactsRevealedAt;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "viewed_at")
    private Instant viewedAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (this.sentAt == null) this.sentAt = now;
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}