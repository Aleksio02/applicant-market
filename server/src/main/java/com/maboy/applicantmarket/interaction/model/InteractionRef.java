package com.maboy.applicantmarket.interaction.api.model;

import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionRef {
    private UUID id;
    private InteractionType type;
    private InteractionStatus status;
}