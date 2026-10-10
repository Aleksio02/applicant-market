import { api } from './client'
import type {
  ContactInfo,
  CreateApplicationRequest,
  CreateInvitationRequest,
  Interaction,
} from '@/shared/types/interaction'

export const interactionApi = {
  // ===== Invitations =====
  createInvitation: (payload: CreateInvitationRequest) =>
    api
      .post<Interaction>('/interaction/invitation', payload)
      .then((r) => r.data),

  listOutgoingInvitations: () =>
    api
      .get<Interaction[]>('/interaction/invitation/outgoing')
      .then((r) => r.data),

  listIncomingInvitations: () =>
    api
      .get<Interaction[]>('/interaction/invitation/incoming')
      .then((r) => r.data),

  acceptInvitation: (id: string) =>
    api
      .patch<Interaction>(`/interaction/invitation/${id}/accept`)
      .then((r) => r.data),

  rejectInvitation: (id: string) =>
    api
      .patch<Interaction>(`/interaction/invitation/${id}/reject`)
      .then((r) => r.data),

  withdrawInvitation: (id: string) =>
    api
      .patch<Interaction>(`/interaction/invitation/${id}/withdraw`)
      .then((r) => r.data),

  // ===== Applications =====
  createApplication: (vacancyId: string, payload: CreateApplicationRequest) =>
    api
      .post<Interaction>(
        `/interaction/application/vacancy/${vacancyId}`,
        payload
      )
      .then((r) => r.data),

  listMyApplications: () =>
    api
      .get<Interaction[]>('/interaction/application/mine')
      .then((r) => r.data),

  listIncomingApplications: () =>
    api
      .get<Interaction[]>('/interaction/application/incoming')
      .then((r) => r.data),

  acceptApplication: (id: string) =>
    api
      .patch<Interaction>(`/interaction/application/${id}/accept`)
      .then((r) => r.data),

  rejectApplication: (id: string) =>
    api
      .patch<Interaction>(`/interaction/application/${id}/reject`)
      .then((r) => r.data),

  withdrawApplication: (id: string) =>
    api
      .patch<Interaction>(`/interaction/application/${id}/withdraw`)
      .then((r) => r.data),

  // ===== Common =====
  getById: (id: string) =>
    api.get<Interaction>(`/interaction/${id}`).then((r) => r.data),

  getContacts: (id: string) =>
    api.get<ContactInfo>(`/interaction/${id}/contacts`).then((r) => r.data),
}