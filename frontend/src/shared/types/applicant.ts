import type { UUID } from './api'

export type ApplicantStatus = 'DRAFT' | 'ACTIVE' | 'HIDDEN'

export interface ApplicantProfile {
  id: UUID
  firstName: string
  lastName: string
  middleName?: string | null
  phone?: string | null
  city?: string | null
  country?: string | null
  about?: string | null
  experienceYears?: number | null
  fspId?: string | null
  fspLinkedAt?: string | null
  status: ApplicantStatus
}

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  middleName?: string
  phone?: string
  city?: string
  country?: string
  about?: string
  experienceYears?: number
}

export interface ProfileCompleteness {
  percent: number
  missing: string[]
}

export interface ExperienceItem {
  id: UUID
  company: string
  position: string
  startDate: string  // ISO date
  endDate?: string | null
  current: boolean
  description?: string | null
  sortOrder?: number | null
}

export interface AddExperienceRequest {
  company: string
  position: string
  startDate: string
  endDate?: string | null
  current?: boolean
  description?: string
  sortOrder?: number
}

export type UpdateExperienceRequest = Partial<AddExperienceRequest>

export interface EducationItem {
  id: UUID
  institution: string
  degree?: string | null
  field?: string | null
  startYear?: number | null
  endYear?: number | null
}

export interface AddEducationRequest {
  institution: string
  degree?: string
  field?: string
  startYear?: number | null
  endYear?: number | null
}

export type UpdateEducationRequest = Partial<AddEducationRequest>

export interface ApplicantSkill {
  id: UUID
  skillId: UUID
  skillCode?: string
  skillName?: string
  skillCategory?: string
  selfAssessedLevel?: number | null
  verifiedGradeId?: UUID | null
  verifiedAt?: string | null
  primary: boolean
  yearsExperience?: number | null
}

export interface AddSkillRequest {
  skillId: UUID
  selfAssessedLevel?: number
  yearsExperience?: number
}

export interface UpdateSkillRequest {
  selfAssessedLevel?: number
  yearsExperience?: number
}

export interface PrivacySettings {
  visibleInSearch: boolean
  allowInvitations: boolean
  showContactsAfterAccept: boolean
  showFspAchievements: boolean
}

export type UpdatePrivacyRequest = Partial<PrivacySettings>

export interface FspAchievement {
  id: UUID
  eventName: string
  eventDate?: string | null
  place?: number | null
  category?: string | null
  verified: boolean
}

export interface GradeHistoryItem {
  id: UUID
  skillId: UUID
  fromGradeId?: UUID | null
  toGradeId: UUID
  reason: string
  changedAt: string
}