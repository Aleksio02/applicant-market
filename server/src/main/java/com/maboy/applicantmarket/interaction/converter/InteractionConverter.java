package com.maboy.applicantmarket.interaction.converter;

import com.maboy.applicantmarket.interaction.dao.dto.InteractionDto;
import com.maboy.applicantmarket.interaction.model.Interaction;

public class InteractionConverter {
    public void fromDto(InteractionDto source, Interaction destination) {
        destination.setId(source.getId());
        destination.setType(source.getType());
        destination.setEmployerId(source.getEmployerId());
        destination.setCandidateId(source.getCandidateId());
        destination.setVacancyId(source.getVacancyId());
        destination.setTitle(source.getTitle());
        destination.setMessage(source.getMessage());
        destination.setSalaryFrom(source.getSalaryFrom());
        destination.setSalaryTo(source.getSalaryTo());
        destination.setStatus(source.getStatus());
        destination.setContactsRevealedAt(source.getContactsRevealedAt());
        destination.setSentAt(source.getSentAt());
        destination.setViewedAt(source.getViewedAt());
        destination.setRespondedAt(source.getRespondedAt());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}