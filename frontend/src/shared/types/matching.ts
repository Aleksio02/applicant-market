import type { UUID } from './api'

export type CandidateSortBy =
  | 'RANK'
  | 'MATCH_LEVEL'
  | 'FRESHNESS'
  | 'FSP'
  | 'ASSESSMENT_SCORE'

export interface CandidateSearchRequest {
  specializationId: UUID
  gradeId: UUID
  skillIds?: UUID[]
  vacancyId?: UUID
  minMatchLevel?: number
  sortBy?: CandidateSortBy
  page?: number
  size?: number
}

export interface MatchReason {
  code: string
  text: string
  weight: number
}

export interface CandidateCard {
  applicantId: UUID
  displayName?: string | null
  city?: string | null
  experienceYears?: number | null
  primarySkillId?: UUID | null
  primarySkillName?: string | null
  primaryGradeId?: UUID | null
  primaryGradeName?: string | null
  categoryId?: UUID | null
  categoryName?: string | null
  assessmentScore?: number | null
  fspAchievementCount: number
  matchLevel: number
  rankScore: number
  reasons: MatchReason[]
}

export interface CandidateSearchResponse {
  queryId: UUID
  page: number
  size: number
  total: number
  totalPages: number
  items: CandidateCard[]
  suggestion?: string | null
}

export interface CategorySummary {
  categoryId: UUID
  categoryCode: string
  categoryName: string
  specializationId: UUID
  gradeId: UUID
  candidateCount: number
}