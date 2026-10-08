import type { UUID } from './api'
import type { WorkFormat } from './employer'

export type VacancyStatus = 'DRAFT' | 'PUBLISHED' | 'CLOSED'

export interface Vacancy {
  id: UUID
  companyId: UUID
  title: string
  description: string
  specializationId: UUID
  gradeId: UUID
  specialization?: { id: UUID; code: string; name: string } | null
  grade?: { id: UUID; code: string; name: string; level: number } | null
  salaryFrom: number
  salaryTo: number
  format: WorkFormat
  location?: string | null
  status: VacancyStatus
  publishedAt?: string | null
  closedAt?: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateVacancyRequest {
  title: string
  description: string
  specializationId: UUID
  gradeId: UUID
  salaryFrom: number
  salaryTo: number
  format: WorkFormat
  location?: string
}

export interface UpdateVacancyRequest {
  title?: string
  description?: string
  specializationId?: UUID
  gradeId?: UUID
  salaryFrom?: number
  salaryTo?: number
  format?: WorkFormat
  location?: string
}

export interface GetVacancyListParams {
  page?: number
  pageSize?: number
  specializationId?: UUID
  gradeId?: UUID
  format?: WorkFormat
  salaryFrom?: number
  location?: string
  status?: VacancyStatus
}

export interface VacancyRequirement {
  id: UUID
  vacancyId: UUID
  skillId: UUID
  skill?: { id: UUID; code: string; name: string; category: string } | null
  level: number
  mandatory: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateVacancyRequirementRequest {
  skillId: UUID
  level: number
  mandatory: boolean
}

export interface UpdateVacancyRequirementRequest {
  level?: number
  mandatory?: boolean
}