import type { UUID } from './api'

export type WorkFormat = 'OFFICE' | 'REMOTE' | 'HYBRID'

export interface Company {
  id: UUID
  ownerId: UUID
  name: string
  description?: string | null
  industry?: string | null
  website?: string | null
  contactPersonName?: string | null
  contactPersonPosition?: string | null
  contactEmail?: string | null
  contactPhone?: string | null
  logoUrl?: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateCompanyRequest {
  name: string
  description?: string
  industry?: string
  website?: string
  contactPersonName?: string
  contactPersonPosition?: string
  contactEmail?: string
  contactPhone?: string
  logoUrl?: string
}

export interface UpdateCompanyRequest {
  name?: string
  description?: string
  industry?: string
  website?: string
  contactPersonName?: string
  contactPersonPosition?: string
  contactEmail?: string
  contactPhone?: string
  logoUrl?: string
}

export interface HiringNeed {
  id: UUID
  companyId: UUID
  title: string
  description?: string | null
  specializationId: UUID
  gradeId: UUID
  specialization?: { id: UUID; code: string; name: string } | null
  grade?: { id: UUID; code: string; name: string; level: number } | null
  salaryFrom?: number | null
  salaryTo?: number | null
  format: WorkFormat
  location?: string | null
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateHiringNeedRequest {
  title: string
  description?: string
  specializationId: UUID
  gradeId: UUID
  salaryFrom: number
  salaryTo: number
  format: WorkFormat
  location?: string
}

export interface UpdateHiringNeedRequest {
  title?: string
  description?: string
  specializationId?: UUID
  gradeId?: UUID
  salaryFrom?: number
  salaryTo?: number
  format?: WorkFormat
  location?: string
}

export interface GetHiringNeedListParams {
  page?: number
  pageSize?: number
  active?: boolean
}