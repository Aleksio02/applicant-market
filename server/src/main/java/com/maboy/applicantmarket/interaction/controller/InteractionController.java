package com.maboy.applicantmarket.interaction.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.interaction.model.Interaction;
import com.maboy.applicantmarket.interaction.model.request.CreateApplicationRequest;
import com.maboy.applicantmarket.interaction.model.request.CreateInvitationRequest;
import com.maboy.applicantmarket.interaction.model.response.ContactInfo;
import com.maboy.applicantmarket.interaction.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    // ---------- Приглашения ----------

    @PostMapping("/invitation")
    public Interaction createInvitation(@Valid @RequestBody CreateInvitationRequest request,
                                        @CurrentUser UUID ownerId) {
        return interactionService.createInvitation(ownerId, request);
    }

    @GetMapping("/invitation/outgoing")
    public List<Interaction> getOutgoingInvitations(@CurrentUser UUID ownerId) {
        return interactionService.getOutgoingInvitations(ownerId);
    }

    @GetMapping("/invitation/incoming")
    public List<Interaction> getIncomingInvitations(@CurrentUser UUID candidateId) {
        return interactionService.getIncomingInvitations(candidateId);
    }

    @PatchMapping("/invitation/{id}/accept")
    public Interaction acceptInvitation(@PathVariable UUID id,
                                        @CurrentUser UUID candidateId) {
        return interactionService.acceptInvitation(candidateId, id);
    }

    @PatchMapping("/invitation/{id}/reject")
    public Interaction rejectInvitation(@PathVariable UUID id,
                                        @CurrentUser UUID candidateId) {
        return interactionService.rejectInvitation(candidateId, id);
    }

    @PatchMapping("/invitation/{id}/withdraw")
    public Interaction withdrawInvitation(@PathVariable UUID id,
                                          @CurrentUser UUID ownerId) {
        return interactionService.withdrawInvitation(ownerId, id);
    }

    // ---------- Отклики ----------

    @PostMapping("/application/vacancy/{vacancyId}")
    public Interaction createApplication(@PathVariable UUID vacancyId,
                                         @RequestBody(required = false) CreateApplicationRequest request,
                                         @CurrentUser UUID candidateId) {
        if (request == null) {
            request = new CreateApplicationRequest();
        }
        return interactionService.createApplication(candidateId, vacancyId, request);
    }

    @GetMapping("/application/mine")
    public List<Interaction> getMyApplications(@CurrentUser UUID candidateId) {
        return interactionService.getMyApplications(candidateId);
    }

    @GetMapping("/application/incoming")
    public List<Interaction> getIncomingApplications(@CurrentUser UUID ownerId) {
        return interactionService.getIncomingApplications(ownerId);
    }

    @PatchMapping("/application/{id}/accept")
    public Interaction acceptApplication(@PathVariable UUID id,
                                         @CurrentUser UUID ownerId) {
        return interactionService.acceptApplication(ownerId, id);
    }

    @PatchMapping("/application/{id}/reject")
    public Interaction rejectApplication(@PathVariable UUID id,
                                         @CurrentUser UUID ownerId) {
        return interactionService.rejectApplication(ownerId, id);
    }

    @PatchMapping("/application/{id}/withdraw")
    public Interaction withdrawApplication(@PathVariable UUID id,
                                           @CurrentUser UUID candidateId) {
        return interactionService.withdrawApplication(candidateId, id);
    }

    // ---------- Общее ----------

    @GetMapping("/{id}")
    public Interaction getById(@PathVariable UUID id,
                               @CurrentUser UUID requesterId) {
        return interactionService.getById(requesterId, id);
    }

    @GetMapping("/{id}/contacts")
    public ContactInfo getContacts(@PathVariable UUID id,
                                   @CurrentUser UUID ownerId) {
        return interactionService.getContacts(ownerId, id);
    }
}