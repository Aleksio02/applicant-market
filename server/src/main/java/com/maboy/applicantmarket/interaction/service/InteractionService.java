package com.maboy.applicantmarket.interaction.service;

import com.maboy.applicantmarket.interaction.model.Interaction;
import com.maboy.applicantmarket.interaction.model.request.CreateApplicationRequest;
import com.maboy.applicantmarket.interaction.model.request.CreateInvitationRequest;
import com.maboy.applicantmarket.interaction.model.response.ContactInfo;

import java.util.List;
import java.util.UUID;

public interface InteractionService {

    // Приглашения
    Interaction createInvitation(UUID ownerId, CreateInvitationRequest request);
    List<Interaction> getOutgoingInvitations(UUID ownerId);
    List<Interaction> getIncomingInvitations(UUID candidateId);
    Interaction acceptInvitation(UUID candidateId, UUID interactionId);
    Interaction rejectInvitation(UUID candidateId, UUID interactionId);
    Interaction withdrawInvitation(UUID ownerId, UUID interactionId);

    // Отклики
    Interaction createApplication(UUID candidateId, UUID vacancyId, CreateApplicationRequest request);
    List<Interaction> getMyApplications(UUID candidateId);
    List<Interaction> getIncomingApplications(UUID ownerId);
    Interaction acceptApplication(UUID ownerId, UUID interactionId);
    Interaction rejectApplication(UUID ownerId, UUID interactionId);
    Interaction withdrawApplication(UUID candidateId, UUID interactionId);

    // Общее
    Interaction getById(UUID requesterId, UUID interactionId);
    ContactInfo getContacts(UUID ownerId, UUID interactionId);
}