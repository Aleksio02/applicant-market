import type { UUID } from './api'

export type InteractionType = 'INVITATION' | 'APPLICATION'

export type InteractionStatus =
  | 'SENT'
  | 'VIEWED'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'WITHDRAWN'

export interface Interaction {
  id: UUID
  type: InteractionType
  employerId: UUID
  candidateId: UUID
  vacancyId?: UUID | null
  title?: string | null
  message?: string | null
  salaryFrom: number
  salaryTo: number
  status: InteractionStatus
  contactsRevealedAt?: string | null
  sentAt: string
  viewedAt?: string | null
  respondedAt?: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateInvitationRequest {
  candidateId: UUID
  vacancyId?: UUID
  title?: string
  message?: string
  salaryFrom?: number
  salaryTo?: number
}

export interface CreateApplicationRequest {
  message?: string
}

export interface ContactInfo {
  displayName?: string
  email: string
}